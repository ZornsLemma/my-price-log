package app.zornslemma.mypricelog.data

import android.content.Context
import android.util.Log
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Upsert
import androidx.room.withTransaction
import app.zornslemma.mypricelog.debug.DebugFlags
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.debug.myRequire
import app.zornslemma.mypricelog.domain.Repository
import java.util.concurrent.Executors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val TAG = "RepositoryImpl"

const val DB_NAME = "main.db"
const val DB_VERSION = 1

@Database(
    entities =
        [DataSet::class, Item::class, Source::class, PriceEntity::class, PriceHistory::class],
    version = DB_VERSION,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataSetDao(): DataSetDao

    abstract fun productDao(): ItemDao

    abstract fun sourceDao(): SourceDao

    abstract fun priceDao(): PriceDao

    abstract fun priceHistoryDao(): PriceHistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return INSTANCE
                ?: synchronized(this) {
                    Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                        .apply {
                            @Suppress("KotlinConstantConditions")
                            if (DebugFlags.LOG_SQL) {
                                setQueryCallback(
                                    { sqlQuery, bindArgs ->
                                        Log.d("Database", "Query: $sqlQuery | Arguments: $bindArgs")
                                    },
                                    Executors.newSingleThreadExecutor(),
                                )
                            }
                        }
                        .build()
                        .also { INSTANCE = it }
                }
        }

        fun clearInstance() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}

// We cannot rely on the database to order our results by name as it isn't locale-sensitive, so we
// have to sort the results in memory later. We could therefore omit ORDER BY clauses completely,
// (ENHANCE: and doing this later on would give a small performance/efficiency improvement) but
// instead we use a deliberately wrong ORDER BY DESC to make it obvious if we are failing to apply
// sorting to the results before showing them.

@Dao
interface DataSetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(dataSet: DataSet): Long

    @Upsert suspend fun upsert(dataSet: DataSet): Long

    @Query("SELECT * FROM data_set ORDER BY name DESC") fun getAllDataSets(): Flow<List<DataSet>>

    @Query("DELETE FROM data_set WHERE id = :dataSetId")
    suspend fun deleteById(dataSetId: Long): Int
}

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(item: Item): Long

    @Upsert suspend fun upsert(item: Item): Long

    @Query("SELECT * FROM item WHERE data_set_id = :dataSetId ORDER BY name DESC")
    fun getAllItems(dataSetId: Long): Flow<List<Item>>

    @Query("SELECT COUNT(*) FROM item WHERE data_set_id = :dataSetId")
    fun countItemsForDataSet(dataSetId: Long): Flow<Long>

    @Query("DELETE FROM item WHERE id = :itemId") suspend fun deleteById(itemId: Long): Int
}

@Dao
interface SourceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(dataSet: Source): Long

    @Upsert suspend fun upsert(source: Source): Long

    @Query("SELECT * FROM source WHERE data_set_id = :dataSetId ORDER BY name DESC")
    fun getAllSources(dataSetId: Long): Flow<List<Source>>

    @Query("SELECT COUNT(*) FROM source WHERE data_set_id = :dataSetId")
    fun countSourcesForDataSet(dataSetId: Long): Flow<Long>

    @Query("DELETE FROM source WHERE id = :sourceId") suspend fun deleteById(sourceId: Long): Int
}

@Dao
interface PriceDao {
    @Upsert suspend fun upsert(price: PriceEntity): Long

    @Query(
        "SELECT price.*, item.default_unit FROM price JOIN item ON price.item_id = item.id " +
            "WHERE price.data_set_id = :dataSetId AND price.item_id = :itemId"
    )
    fun getPriceWithItemEntityForItem(
        dataSetId: Long,
        itemId: Long,
    ): Flow<List<PriceWithItemEntity>>

    @Query("SELECT COUNT(*) FROM price WHERE item_id = :itemId")
    fun countPricesForItem(itemId: Long): Flow<Long>

    @Query("SELECT COUNT(*) FROM price WHERE source_id = :sourceId")
    fun countPricesForSource(sourceId: Long): Flow<Long>

    @Query("DELETE FROM price WHERE id = :priceId") suspend fun deleteById(priceId: Long): Int
}

@Dao
interface PriceHistoryDao {
    @Insert suspend fun insert(priceHistory: PriceHistory): Long

    // Note that we get price history without using the price_id. This means that if a price is
    // deleted and subsequently a new price is added (which will allocate a new price_id), both
    // segments of the price history will be retrieved.
    @Query(
        "SELECT * FROM price_history WHERE data_set_id = :dataSetId AND item_id = :itemId AND source_id = :sourceId ORDER BY modified_at DESC"
    )
    fun getPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long): Flow<List<PriceHistory>>

    @Query(
        "SELECT COUNT(*) FROM price_history WHERE data_set_id = :dataSetId AND item_id = :itemId AND source_id = :sourceId"
    )
    fun countPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long): Flow<Long>

    @Query("DELETE FROM price_history WHERE id = :priceHistoryId")
    suspend fun deleteById(priceHistoryId: Long): Int
}

class RepositoryImpl(
    private val db: AppDatabase,
    private val dataSetDao: DataSetDao,
    private val itemDao: ItemDao,
    private val sourceDao: SourceDao,
    private val priceDao: PriceDao,
    private val priceHistoryDao: PriceHistoryDao,
) : Repository {
    override fun getAllDataSets(): Flow<List<DataSet>> = dataSetDao.getAllDataSets()

    override fun getAllItems(dataSetId: Long): Flow<List<Item>> = itemDao.getAllItems(dataSetId)

    override fun getAllSources(dataSetId: Long): Flow<List<Source>> =
        sourceDao.getAllSources(dataSetId)

    override fun getPricesForItem(dataSetId: Long, itemId: Long): Flow<List<Price>> =
        priceDao.getPriceWithItemEntityForItem(dataSetId = dataSetId, itemId = itemId).map { list ->
            list.map { it.toDomain() }
        }

    override fun getPriceHistory(
        dataSetId: Long,
        itemId: Long,
        sourceId: Long,
    ): Flow<List<PriceHistory>> = priceHistoryDao.getPriceHistory(dataSetId, itemId, sourceId)

    override fun countPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long) =
        priceHistoryDao.countPriceHistory(dataSetId, itemId, sourceId)

    override fun countItemsForDataSet(dataSetId: Long): Flow<Long> =
        itemDao.countItemsForDataSet(dataSetId)

    override fun countSourcesForDataSet(dataSetId: Long): Flow<Long> =
        sourceDao.countSourcesForDataSet(dataSetId)

    override fun countPricesForItem(itemId: Long): Flow<Long> = priceDao.countPricesForItem(itemId)

    override fun countPricesForSource(sourceId: Long): Flow<Long> =
        priceDao.countPricesForSource(sourceId)

    override suspend fun updateOrInsertDataSet(dataSet: DataSet): Long = dataSetDao.upsert(dataSet)

    override suspend fun updateOrInsertItem(item: Item): Long = itemDao.upsert(item)

    override suspend fun updateOrInsertSource(source: Source): Long = sourceDao.upsert(source)

    override suspend fun deleteDataSetById(dataSetId: Long): Int = dataSetDao.deleteById(dataSetId)

    override suspend fun deleteItemById(itemId: Long): Int = itemDao.deleteById(itemId)

    override suspend fun deleteSourceById(sourceId: Long): Int = sourceDao.deleteById(sourceId)

    override suspend fun deletePriceById(priceId: Long): Int = priceDao.deleteById(priceId)

    override suspend fun updateOrInsertPrice(price: Price): Long {
        var priceId: Long = 0
        db.withTransaction {
            val priceEntity = price.toEntity()
            priceId = priceDao.upsert(priceEntity)
            val priceEntityWithId =
                if (priceEntity.id != 0L) priceEntity else priceEntity.copy(id = priceId)
            priceHistoryDao.insert(priceEntityWithId.toPriceHistory())
        }
        return priceId
    }

    override suspend fun revertPrice(priceBeforeRevert: Price, priceAfterRevert: Price) {
        // We don't include details in all the messages below, so let's log the inputs here once,
        // which in conjunction with the database should be enough to investigate problems.
        Log.d(TAG, "priceBeforeRevert: $priceBeforeRevert")
        Log.d(TAG, "priceAfterRevert: $priceAfterRevert")

        // Check priceBeforeRevert and priceAfterRevert relate to the same price. It might be
        // arguably OK for "id" not to match between priceBeforeRevert and priceAfterRevert, but in
        // practice it ought to so let's include that in the check.
        myRequire(
            priceBeforeRevert.id == priceAfterRevert.id &&
                priceBeforeRevert.dataSetId == priceAfterRevert.dataSetId &&
                priceBeforeRevert.itemId == priceAfterRevert.itemId &&
                priceBeforeRevert.sourceId == priceAfterRevert.sourceId
        ) {
            "Inconsistent IDs between priceBeforeRevert and priceAfterRevert"
        }

        // ENHANCE: This could be streamlined if we did less checking, but for now at least we are
        // as paranoid as we can be to avoid corrupting anything. Our caller has expressed the
        // change in terms of Price objects, but as we need to fix up the history as well and we
        // don't want to complicate things by updating history entries (we know we should just be
        // deleting the last price_history) we check that what the caller is asking for is
        // equivalent.
        db.withTransaction {
            // Check that priceBeforeRevert matches the current price in the database.
            // ENHANCE: This retrieves more data than necessary and could be optimised with a new
            // Repository function, but it's not likely to be performance critical and may not be
            // done at all later on.
            val currentPrice =
                getPricesForItem(
                        dataSetId = priceBeforeRevert.dataSetId,
                        itemId = priceBeforeRevert.itemId,
                    )
                    .first()
                    .firstOrNull { it.id == priceBeforeRevert.id }
            myCheck(currentPrice != null) { "Can't find database price for priceBeforeRevert" }
            myCheck(currentPrice == priceBeforeRevert) {
                "Database price doesn't match priceBeforeRevert"
            }

            // We will just delete the most recent price_history entry as part of the reversion,
            // leaving the second-to-last as the new latest entry, so pick out the most recent two
            // entries for inspection.
            val priceHistoryList =
                priceHistoryDao
                    .getPriceHistory(
                        dataSetId = priceBeforeRevert.dataSetId,
                        itemId = priceBeforeRevert.itemId,
                        sourceId = priceBeforeRevert.sourceId,
                    )
                    .first()
            myCheck(priceHistoryList.size >= 2) {
                "Expected at least two price history entries when reverting a price update"
            }
            val priceHistoryToDelete = priceHistoryList[0]
            val priceHistoryToRevertTo = priceHistoryList[1]

            // Check that priceBeforeRevert is the same as priceHistoryToDelete after converting
            // the former from a PriceEntity to a PriceHistory and fixing up the ID.
            myCheck(
                priceBeforeRevert.toEntity().toPriceHistory().copy(id = priceHistoryToDelete.id) ==
                    priceHistoryToDelete
            ) {
                "Expected priceBeforeRevert and priceHistoryToDelete to match"
            }
            // Similarly, check priceAfterRevert matches priceHistoryToRevertTo.
            myCheck(
                priceAfterRevert.toEntity().toPriceHistory().copy(id = priceHistoryToRevertTo.id) ==
                    priceHistoryToRevertTo
            ) {
                "Expected priceAfterRevert and priceHistoryToRevertTo to match"
            }

            // We can now go ahead and modify the price and price_history tables to actually revert.
            priceDao.upsert(priceAfterRevert.toEntity())
            priceHistoryDao.deleteById(priceHistoryToDelete.id)
        }
    }
}
