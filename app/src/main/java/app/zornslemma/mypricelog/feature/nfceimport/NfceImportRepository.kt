package app.zornslemma.mypricelog.feature.nfceimport

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.PrimaryKey
import androidx.room.withTransaction
import app.zornslemma.mypricelog.data.AppDatabase
import app.zornslemma.mypricelog.data.Item
import app.zornslemma.mypricelog.data.LoyaltyType
import app.zornslemma.mypricelog.data.PriceEntity
import app.zornslemma.mypricelog.data.Source
import app.zornslemma.mypricelog.data.toPriceHistory
import app.zornslemma.mypricelog.domain.MeasurementUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first

@Entity(tableName = "nfce_import")
data class NfceImport(
    @PrimaryKey val key: String,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    @ColumnInfo(name = "imported_at") val importedAt: Instant,
    @ColumnInfo(name = "source_url") val sourceUrl: String,
)

@Dao
interface NfceImportDao {
    @Query("SELECT * FROM nfce_import WHERE key = :key AND data_set_id = :dataSetId LIMIT 1")
    suspend fun getByKeyAndDataSet(key: String, dataSetId: Long): NfceImport?

    @Insert(onConflict = OnConflictStrategy.ABORT) suspend fun insert(entity: NfceImport)
}

class NfceImportRepository(private val db: AppDatabase) {
    fun importKey(receipt: Receipt): String = receipt.accessKey ?: NfceParsing.hashUrl(receipt.sourceUrl)

    suspend fun importReceipt(dataSetId: Long, receipt: Receipt): Result<NfceImportSummary> {
        return runCatching {
            val key = importKey(receipt)
            db.withTransaction {
                if (db.nfceImportDao().getByKeyAndDataSet(key, dataSetId) != null) {
                    error("Already imported")
                }

                val now = Instant.now()
                val source = upsertSource(dataSetId, receipt)

                var importedCount = 0
                var createdItems = 0
                var updatedPrices = 0
                var skippedCount = 0

                receipt.items.forEach { receiptItem ->
                    val normalizedDescription = NfceParsing.normalizeDescription(receiptItem.description)
                    if (normalizedDescription.isBlank()) {
                        skippedCount++
                        return@forEach
                    }

                    val mapped = mapToPrice(receiptItem, dataSetId, source.id, receipt, now)
                    if (mapped == null) {
                        skippedCount++
                        return@forEach
                    }

                    val (item, created) = findOrCreateItem(dataSetId, receiptItem)
                    if (created) createdItems++

                    db.priceDao().getCurrentPriceEntity(dataSetId, item.id, source.id)?.let { current ->
                        db.priceHistoryDao().insert(current.toPriceHistory())
                    }

                    db.priceDao().upsert(mapped.copy(itemId = item.id))
                    importedCount++
                    updatedPrices++
                }

                db.nfceImportDao().insert(
                    NfceImport(key = key, dataSetId = dataSetId, importedAt = now, sourceUrl = receipt.sourceUrl)
                )

                val dateText =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .withZone(ZoneId.systemDefault())
                        .format(receipt.datetime ?: now)

                NfceImportSummary(
                    importedCount = importedCount,
                    createdItems = createdItems,
                    updatedPrices = updatedPrices,
                    skippedCount = skippedCount,
                    storeName = source.name,
                    datetimeText = dateText,
                )
            }
        }
    }

    private suspend fun upsertSource(dataSetId: Long, receipt: Receipt): Source {
        val sourceName = NfceParsing.normalizeDescription(receipt.storeName ?: "NFC-e SP")
        val sourceDao = db.sourceDao()
        val existing =
            sourceDao.getAllSources(dataSetId).first().firstOrNull {
                it.name.equals(sourceName, ignoreCase = true)
            }
        val notes = buildString {
            receipt.cnpj?.let { append("CNPJ:$it") }
        }
        val source =
            (existing
                    ?: Source(
                        dataSetId = dataSetId,
                        name = sourceName,
                        loyaltyType = LoyaltyType.NONE,
                        loyaltyMultiplier = 1.0,
                        notes = notes,
                    ))
                .copy(notes = notes)

        val id = sourceDao.upsert(source)
        return if (source.id == 0L) source.copy(id = id) else source
    }

    private suspend fun findOrCreateItem(dataSetId: Long, receiptItem: ReceiptItem): Pair<Item, Boolean> {
        val itemDao = db.productDao()
        val normalizedName = NfceParsing.normalizeDescription(receiptItem.description)
        val existing =
            itemDao.getAllItems(dataSetId).first().firstOrNull {
                it.name.equals(normalizedName, ignoreCase = true)
            }
        if (existing != null) return existing to false

        val unitText = NfceParsing.normalizeUnitText(receiptItem.unitText)
        val defaultUnit =
            when (unitText) {
                "KG" -> MeasurementUnit.KG
                "L" -> MeasurementUnit.L
                else -> NfceParsing.detectPackSize(normalizedName)?.second ?: MeasurementUnit.EACH
            }

        val entity =
            Item(
                dataSetId = dataSetId,
                name = normalizedName,
                defaultUnit = defaultUnit,
                allowMultipack = NfceParsing.detectMultipack(normalizedName) > 1,
                notes = receiptItem.productCode?.let { "CODE:$it" } ?: "",
            )
        val id = itemDao.upsert(entity)
        return entity.copy(id = id) to true
    }

    private fun mapToPrice(
        receiptItem: ReceiptItem,
        dataSetId: Long,
        sourceId: Long,
        receipt: Receipt,
        now: Instant,
    ): PriceEntity? {
        val unitPrice = receiptItem.unitPrice?.toDouble()?.takeIf { it >= 0.0 } ?: return null
        val normalizedUnit = NfceParsing.normalizeUnitText(receiptItem.unitText)

        val userUnit: MeasurementUnit
        val quantityInBaseUnit: Double
        val count: Long

        when (normalizedUnit) {
            "KG" -> {
                userUnit = MeasurementUnit.KG
                quantityInBaseUnit = 1000.0
                count = 1L
            }
            "L" -> {
                userUnit = MeasurementUnit.L
                quantityInBaseUnit = 1000.0
                count = 1L
            }
            else -> {
                val pack = NfceParsing.detectPackSize(receiptItem.description)
                if (pack != null) {
                    userUnit = pack.second
                    quantityInBaseUnit = pack.first
                } else {
                    userUnit = MeasurementUnit.EACH
                    quantityInBaseUnit = 1.0
                }
                count = NfceParsing.detectMultipack(receiptItem.description)
            }
        }

        return PriceEntity(
            dataSetId = dataSetId,
            itemId = 0,
            sourceId = sourceId,
            price = unitPrice,
            count = count,
            quantityInBaseUnit = quantityInBaseUnit,
            userUnit = userUnit,
            confirmedAt = receipt.datetime ?: now,
            notes =
                "NFCE:${receipt.accessKey ?: NfceParsing.hashUrl(receipt.sourceUrl)} PURCHASE_QTY:${receiptItem.qty ?: 0.0}",
            modifiedAt = now,
        )
    }
}
