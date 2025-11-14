package com.example.composetutorial.models

import android.util.Log
import androidx.room.withTransaction
import com.example.composetutorial.AppDatabase
import com.example.composetutorial.DataSetDao
import com.example.composetutorial.ItemDao
import com.example.composetutorial.PriceDao
import com.example.composetutorial.PriceHistoryDao
import com.example.composetutorial.SourceDao
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.myCheck
import com.example.composetutorial.myRequire
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// TODO: I should probably rename the "models" subpackage as "data" now it contains things like
// this. I haven't done it yet to reduce git noise when I might change my mind again later.

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
        priceDao.getPriceWithItemEntityForItem(dataSetId = dataSetId, itemId = itemId)
            .map { list -> list.map { it.toDomain() } }

    override fun getPriceHistory(
        dataSetId: Long,
        itemId: Long,
        sourceId: Long
    ): Flow<List<PriceHistory>> =
        priceHistoryDao.getPriceHistory(dataSetId, itemId, sourceId)

    override fun countPriceHistory(
        dataSetId: Long,
        itemId: Long,
        sourceId: Long
    ) = priceHistoryDao.countPriceHistory(dataSetId, itemId, sourceId)

    override fun countPricesForItem(itemId: Long): Flow<Long> =
        priceDao.countPricesForItem(itemId)

    override fun countPricesForSource(sourceId: Long): Flow<Long> =
        priceDao.countPricesForSource(sourceId)

    override suspend fun updateOrInsertDataSet(dataSet: DataSet): Long =
        dataSetDao.upsert(dataSet)

    override suspend fun updateOrInsertItem(item: Item): Long =
        itemDao.upsert(item)

    override suspend fun updateOrInsertSource(source: Source): Long =
        sourceDao.upsert(source)

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
            priceHistoryDao.insert(priceEntityWithId.toHistory())
        }
        return priceId
    }

    override suspend fun revertPrice(priceBeforeRevert: Price, priceAfterRevert: Price) {
        // We don't include details in all the messages below, so let's log the inputs here once,
        // which in conjunction with the database should be enough to investigate problems.
        Log.d("MyApp", "priceBeforeRevert: $priceBeforeRevert")
        Log.d("MyApp", "priceAfterRevert: $priceAfterRevert")

        // Check priceBeforeRevert and priceAfterRevert relate to the same price. It might be
        // arguably OK for "id" not to match between priceBeforeRevert and priceAfterRevert, but in
        // practice it ought to so let's include that in the check.
        myRequire(priceBeforeRevert.id == priceAfterRevert.id && priceBeforeRevert.dataSetId == priceAfterRevert.dataSetId && priceBeforeRevert.itemId == priceAfterRevert.itemId && priceBeforeRevert.sourceId == priceAfterRevert.sourceId) { "Inconsistent IDs between priceBeforeRevert and priceAfterRevert" }

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
            val currentPrice = getPricesForItem(
                dataSetId = priceBeforeRevert.dataSetId,
                itemId = priceBeforeRevert.itemId
            ).first().firstOrNull { it.id == priceBeforeRevert.id }
            myCheck(currentPrice != null) { "Can't find database price for priceBeforeRevert" }
            myCheck(currentPrice == priceBeforeRevert) { "Database price doesn't match priceBeforeRevert" }

            // We will just delete the most recent price_history entry as part of the reversion,
            // leaving the second-to-last as the new latest entry, so pick out the most recent two
            // entries for inspection.
            val priceHistoryList = priceHistoryDao.getPriceHistory(
                dataSetId = priceBeforeRevert.dataSetId,
                itemId = priceBeforeRevert.itemId,
                sourceId = priceBeforeRevert.sourceId
            ).first()
            myCheck(priceHistoryList.size >= 2) { "Expected at least two price history entries when reverting a price update" }
            val priceHistoryToDelete = priceHistoryList[0]
            val priceHistoryToRevertTo = priceHistoryList[1]

            // Check that priceBeforeRevert is the same as priceHistoryToDelete after converting
            // the former from a PriceEntity to a PriceHistory and fixing up the ID.
            myCheck(
                priceBeforeRevert.toEntity().toHistory()
                    .copy(id = priceHistoryToDelete.id) == priceHistoryToDelete
            ) { "Expected priceBeforeRevert and priceHistoryToDelete to match" }
            // Similarly, check priceAfterRevert matches priceHistoryToRevertTo.
            myCheck(
                priceAfterRevert.toEntity().toHistory()
                    .copy(id = priceHistoryToRevertTo.id)
                        == priceHistoryToRevertTo
            ) { "Expected priceAfterRevert and priceHistoryToRevertTo to match" }

            // We can now go ahead and modify the price and price_history tables to actually revert.
            priceDao.upsert(priceAfterRevert.toEntity())
            priceHistoryDao.deleteById(priceHistoryToDelete.id)
        }
    }
}