package app.zornslemma.mypricelog.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import app.zornslemma.mypricelog.common.formatDoubleForEditing
import app.zornslemma.mypricelog.common.parseStringAsDoubleOrNull
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.domain.CurrencyFormat
import app.zornslemma.mypricelog.domain.MeasurementUnit
import app.zornslemma.mypricelog.domain.Quantity
import app.zornslemma.mypricelog.domain.baseUnit
import app.zornslemma.mypricelog.domain.createCurrencyFormat
import java.time.Instant
import java.util.Locale
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "price",
    foreignKeys =
        [
            ForeignKey(
                entity = DataSet::class,
                parentColumns = ["id"],
                childColumns = ["data_set_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = Item::class,
                parentColumns = ["id"],
                childColumns = ["item_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = Source::class,
                parentColumns = ["id"],
                childColumns = ["source_id"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
    indices =
        [
            Index(value = ["data_set_id"], unique = false), // just because this is a foreign key
            // We don't include data_set_id here because although some queries specify it along with
            // item_id, it's just belt-and-braces - item_id already implies a data_set_id if all is
            // well.
            Index(value = ["item_id"], unique = false),
            Index(value = ["source_id"], unique = false),
            // We put item_id first in this index as it's likely to be more selective than source_id
            // and ENHANCE: it may allow us to remove the index on item_id by itself later on. This
            // index is not just for efficiency; it will also prevent data corruption if a bug
            // causes us to try to insert more than one price for an (item, source) pair.
            Index(value = ["item_id", "source_id"], unique = true),
        ],
)
data class PriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    @ColumnInfo(name = "item_id") val itemId: Long,
    @ColumnInfo(name = "source_id") val sourceId: Long,

    // The item is sold for "price" per "count"*"quantity_in_base_unit", e.g. £1.42 for 1x500g.
    //
    // We use floating point for the price - it saves worrying about storing in pence or the
    // currency's equivalent and then getting in a mess if somehow the conventional number of
    // decimal places changes. For the kinds of prices we are representing and the limited amount of
    // calculation we are doing on them, there should in practice be no problems at all, as long as
    // we round to the relevant number of decimal places on display.
    //
    // "quantity_in_base_unit" is stored in the metric base unit associated with the item_id's
    // quantity_type. This avoids having to do bulk database updates if the user wants to change
    // unit conventions - this could happen even within a measurement system if shops switch to
    // marking pack sizes in ounces instead of lbs, for example. We use floating point because it
    // allows us to round-trip non-metric measures perfectly (provided we round them for display),
    // and it doesn't seem to have any real downside in practice.
    val price: Double,
    val count: Long,
    @ColumnInfo(name = "quantity_in_base_unit") val quantityInBaseUnit: Double,

    // Although quantity is stored in the base unit, we also record the actual unit the user entered
    // the price in. This allows us to show it back to them in the most natural form when they are
    // e.g. comparing the database price with the current shelf price. We do have a default unit
    // stored on the item, but tracking it per actual price allows us to handle situations where
    // supermarket A sells milk in pint multiples while supermarket B sells it in litre multiples.
    @ColumnInfo(name = "user_unit") val userUnit: MeasurementUnit,
    @ColumnInfo(name = "confirmed_at") val confirmedAt: Instant,
    val notes: String,

    // modifiedAt is borderline redundant here, but it feels generally neater to have it here as
    // well as on PriceHistory and probably simplifies things.
    @ColumnInfo(name = "modified_at") val modifiedAt: Instant,
)

// ENHANCE: PriceWithItem is arguably redundant now - given we have an original_unit on each price,
// that effectively tells us the quantity type implicitly and we don't need to join to item to get
// it. However, I suspect it still has some value because it allows us to do a bit of extra
// validation which may catch bugs. My inclination is to keep it for now, since the code already
// exists, and perhaps refactor to remove this at some point in the future.
data class PriceWithItemEntity(
    @Embedded val priceEntity: PriceEntity,
    @ColumnInfo(name = "default_unit") val itemDefaultUnit: MeasurementUnit,
)

fun PriceWithItemEntity.toDomain(): Price {
    // I have checks like this in various places but this is probably a pretty solid place for one.
    // On the way from database->domain, this is where we have a "solid" itemDefaultUnit value
    // (because it came from a database join) and that gives us an independent cross-check that
    // priceEntity.userUnit is of the right QuantityType.
    myCheck(priceEntity.userUnit.quantityType == itemDefaultUnit.quantityType) {
        "Expected consistent units on PriceWithItemEntity but we have userUnit " +
            "${priceEntity.userUnit} and itemDefaultUnit $itemDefaultUnit"
    }
    return Price(
        id = priceEntity.id,
        dataSetId = priceEntity.dataSetId,
        itemId = priceEntity.itemId,
        sourceId = priceEntity.sourceId,
        price = priceEntity.price,
        count = priceEntity.count,
        quantity =
            Quantity(priceEntity.quantityInBaseUnit, priceEntity.userUnit.quantityType.baseUnit())
                .to(priceEntity.userUnit),
        confirmedAt = priceEntity.confirmedAt,
        notes = priceEntity.notes,
        modifiedAt = priceEntity.modifiedAt,
        itemDefaultUnit = itemDefaultUnit,
    )
}

@Parcelize
data class Price(
    val id: Long = 0,
    val dataSetId: Long,
    val itemId: Long,
    val sourceId: Long,
    val count: Long,
    val price: Double,
    val quantity: Quantity,
    val confirmedAt: Instant,
    val notes: String,
    val modifiedAt: Instant,
    // itemDefaultUnit is a copy of the defaultUnit from the Item when we originally read the
    // PriceWithItemEntity in from the database. It is intended to allow a best effort (protecting
    // against buggy code, not malicious code) validation that when we write back to the database,
    // quantity hasn't somehow mutated into a different QuantityType.
    val itemDefaultUnit: MeasurementUnit,
) : Parcelable

fun PriceEntity.toPriceHistory(): PriceHistory {
    return PriceHistory(
        priceId = id,
        dataSetId = dataSetId,
        itemId = itemId,
        sourceId = sourceId,
        price = price,
        count = count,
        quantityInBaseUnit = quantityInBaseUnit,
        userUnit = userUnit,
        confirmedAt = confirmedAt,
        notes = notes,
        modifiedAt = modifiedAt,
    )
}

fun Price.toEntity(): PriceEntity {
    // This check is just a more explicit version of that implicitly done inside the
    // quantity.asValue() call below.
    myCheck(quantity.unit.quantityType == itemDefaultUnit.quantityType) {
        "Expected consistent quantity type when converting Price to PriceEntity but found " +
            "measure $quantity with itemDefaultUnit $itemDefaultUnit"
    }
    return PriceEntity(
        id = id,
        dataSetId = dataSetId,
        itemId = itemId,
        sourceId = sourceId,
        price = price,
        count = count,
        quantityInBaseUnit = quantity.asValue(itemDefaultUnit.quantityType.baseUnit()),
        userUnit = quantity.unit,
        confirmedAt = confirmedAt,
        notes = notes,
        modifiedAt = modifiedAt,
    )
}

fun Price.toEditable(locale: Locale, currencyFormat: CurrencyFormat): EditablePrice =
    EditablePrice(
        id = id,
        dataSetId = dataSetId,
        itemId = itemId,
        sourceId = sourceId,
        count = count.toString(),
        price =
            formatDoubleForEditing(
                price,
                minDecimals = currencyFormat.decimalPlaces,
                maxDecimals = currencyFormat.decimalPlaces,
                locale,
            ),
        // Rounding is particularly important here - for non-metric measures, which are stored in
        // doubles in metric base units in the database, if we didn't round we could end up with
        // some visible noise in the least significant decimal places.
        measureValue =
            formatDoubleForEditing(
                quantity.value,
                minDecimals = 0,
                maxDecimals = quantity.unit.maxDecimals,
                locale,
            ),
        measurementUnit = quantity.unit,
        confirmedAt = confirmedAt,
        toConfirm = false,
        notes = notes,
        itemDefaultUnit = itemDefaultUnit,
    )

@Parcelize
data class EditablePrice(
    val id: Long,
    val dataSetId: Long,
    val itemId: Long,
    val sourceId: Long,
    val price: String,
    val count: String,
    val measureValue: String,
    val measurementUnit: MeasurementUnit,
    val confirmedAt: Instant,
    val toConfirm: Boolean,
    val notes: String,
    val itemDefaultUnit: MeasurementUnit,
) : Parcelable {

    companion object {
        fun forNew(
            dataSetId: Long,
            itemId: Long,
            sourceId: Long,
            itemDefaultUnit: MeasurementUnit,
        ) =
            EditablePrice(
                id = 0,
                dataSetId = dataSetId,
                itemId = itemId,
                sourceId = sourceId,
                price = "",
                count = "",
                measureValue = "",
                measurementUnit = itemDefaultUnit,
                confirmedAt = Instant.now(),
                toConfirm = true,
                notes = "",
                itemDefaultUnit = itemDefaultUnit,
            )
    }
}

fun EditablePrice.toDomain(locale: Locale): Price? {
    val priceDouble = parseStringAsDoubleOrNull(locale, price)
    // If we are adding a first price for a non-multipack item, count may be an empty string and we
    // interpret that as 1. It's up to the validation rules whether to allow an empty string to make
    // it this far or not.
    val countLong =
        if (count.trim().isEmpty()) 1L else parseStringAsDoubleOrNull(locale, count)?.toLong()
    val measureValueDouble = parseStringAsDoubleOrNull(locale, measureValue)
    return if (priceDouble == null || countLong == null || measureValueDouble == null) {
        null
    } else {
        val now = Instant.now()
        Price(
            id = id,
            dataSetId = dataSetId,
            itemId = itemId,
            sourceId = sourceId,
            price = priceDouble,
            count = countLong,
            quantity = Quantity(measureValueDouble, measurementUnit),
            confirmedAt = if (toConfirm) now else confirmedAt,
            notes = notes,
            modifiedAt = now,
            itemDefaultUnit = itemDefaultUnit,
        )
    }
}

// This must be kept in sync with any changes to PriceEntity.
@Entity(
    tableName = "price_history",
    foreignKeys =
        [
            // We don't declare a foreign key relationship of our price_id to price.id. It is
            // possible to delete a price but retain the history, which wouldn't work with such a
            // foreign key relationship. Arguably we don't need price_id at all on this table, but
            // having it will allow us to observe when it changes for the same (data_set_id,
            // source_id, item_id) combination and infer a price deletion at that point in the
            // history.
            ForeignKey(
                entity = DataSet::class,
                parentColumns = ["id"],
                childColumns = ["data_set_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = Item::class,
                parentColumns = ["id"],
                childColumns = ["item_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = Source::class,
                parentColumns = ["id"],
                childColumns = ["source_id"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
    indices =
        [
            Index(value = ["data_set_id"], unique = false), // because this is a foreign key
            Index(value = ["source_id"], unique = false), // because this is a foreign key
            Index(value = ["item_id"], unique = false), // because this is a foreign key

            // We don't include data_set_id in this index as it's technically redundant - item_id
            // and source_id both imply a data_set_id and it should be the same. We put item_id
            // first because it feels more likely we might want to select history using just item_id
            // than just source_id in the future. I also suspect it helps that item_id is far more
            // selective than source_id - there will typically be many more items than sources and
            // our queries will be using equality conditions. The index above on just item_id is
            // probably redundant because of this, but in practice it's not a big deal to have it.
            Index(value = ["item_id", "source_id"], unique = false),
        ],
)
// I like the table name price_history but am less keen on having this class called PriceHistory,
// which sounds like it represents the total history of a price and not a single historical version
// of the price. However, I like the table name and don't like the idea of breaking the obvious
// mapping between the table name and this class, so let's stick with it..
data class PriceHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "price_id") val priceId: Long,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    @ColumnInfo(name = "item_id") val itemId: Long,
    @ColumnInfo(name = "source_id") val sourceId: Long,
    val price: Double,
    val count: Long,
    @ColumnInfo(name = "quantity_in_base_unit") val quantityInBaseUnit: Double,
    @ColumnInfo(name = "user_unit") val userUnit: MeasurementUnit,
    @ColumnInfo(name = "confirmed_at") val confirmedAt: Instant,
    val notes: String,
    @ColumnInfo(name = "modified_at") val modifiedAt: Instant,
)

fun PriceHistory.toPrice(): Price {
    return Price(
        id = priceId,
        dataSetId = dataSetId,
        itemId = itemId,
        sourceId = sourceId,
        price = price,
        count = count,
        quantity = Quantity(quantityInBaseUnit, userUnit.quantityType.baseUnit()).to(userUnit),
        confirmedAt = confirmedAt,
        notes = notes,
        modifiedAt = modifiedAt,
        // itemDefaultUnit "ought" to be taken from the Item table for itemId. It's not really
        // convenient to have to do that (it would mean introducing an extra layer into the
        // history-related data classes, as we do with PriceWithItemEntity, so we can do the
        // join) and I don't think it would buy us that much in terms of catching errors, so we
        // just fake up a plausible value here. Note that this won't get written back to the
        // database if a historical price gets converted back into a current price, as it is not
        // present on the database's price table in the first place. It's used entirely for
        // in-memory consistency checks.
        itemDefaultUnit = userUnit.quantityType.baseUnit(),
    )
}

fun PriceHistory.toEditable(priceId: Long, locale: Locale, dataSet: DataSet): EditablePrice {
    return toPrice().copy(id = priceId).toEditable(locale, dataSet.createCurrencyFormat(locale))
}
