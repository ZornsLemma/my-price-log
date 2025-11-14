package com.example.composetutorial.domain

import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.Item
import com.example.composetutorial.models.Price
import com.example.composetutorial.models.PriceHistory
import com.example.composetutorial.models.Source
import kotlinx.coroutines.flow.Flow

// ENHANCE: Although this interface seems a bit pointless at the moment, it is here to help with
// mocking the database if/when I add some test code.
interface Repository {
    fun getAllDataSets(): Flow<List<DataSet>>
    fun getAllItems(dataSetId: Long): Flow<List<Item>>
    fun getAllSources(dataSetId: Long): Flow<List<Source>>

    fun getPricesForItem(dataSetId: Long, itemId: Long): Flow<List<Price>>

    fun getPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long): Flow<List<PriceHistory>>
    fun countPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long): Flow<Long>

    fun countPricesForItem(itemId: Long): Flow<Long>
    fun countPricesForSource(sourceId: Long): Flow<Long>

    suspend fun updateOrInsertDataSet(dataSet: DataSet): Long
    suspend fun updateOrInsertItem(item: Item): Long
    suspend fun updateOrInsertSource(source: Source): Long
    suspend fun updateOrInsertPrice(price: Price): Long
    suspend fun revertPrice(priceBeforeRevert: Price, priceAfterRevert: Price)

    // It feels slightly odd to use Int return values (number of deleted rows) here when we use
    // Long for IDs, but that's just how it is and in practice it doesn't matter, of course.
    suspend fun deleteDataSetById(dataSetId: Long): Int
    suspend fun deleteItemById(itemId: Long): Int
    suspend fun deleteSourceById(sourceId: Long): Int
    suspend fun deletePriceById(priceId: Long): Int
}