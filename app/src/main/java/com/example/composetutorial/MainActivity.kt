@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.composetutorial

import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.time.Duration
//import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.parcelize.Parcelize
import androidx.compose.foundation.layout.padding
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Button
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.rememberNavController
import android.os.Bundle
import android.os.Parcelable
import android.os.StrictMode
import android.text.format.DateUtils
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.Upsert
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Currency
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.math.log10
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

// Enum class to represent whether something is sold by "count of items" ($4 for 6 bananas),
// weight or volume. This is fundamental as we make no effort to convert between them using some
// sort of density estimate or whatever. Actual units (kg, oz, etc) of the same quantity type can
// be varied much more freely.
// TODO: Just possibly rename this "MeasureType"? ChatGPT suggestion, maybe has a point, "QuantityType" is definitely not a terrible name though.
enum class QuantityType(val value: Int) {
    ITEM(1),
    WEIGHT(2), // technically mass but everyone says "price per weight"
    VOLUME(3);

    companion object {
        fun fromValue(value: Int): QuantityType? {
            return entries.find { it.value == value }
        }
    }
}

enum class UnitFamily {
    METRIC,
    IMPERIAL, // as used in UK
    US_CUSTOMARY, // as used in US
    ITEM, // TODO: not sure if we need this
}

// TODO: CHECK ALL THE MULTIPLIERS HERE - THIS IS CHATGPT CODE, AND WE MAY ALSO NEED TO ADDRESS IMPERIAL VS US OR WHATEVER TERMINOLOGY IS
// TODO: IDS SHOULD PROBABLY BE TIDIED UP IF WE KEEP EG G100
// TODO: IF WE KEEP G100 AND ML100, WE MAY NEED A FLAG TO INDICATE THESE ARE SECOND-CLASS CITIZENS AND ONLY ELIGIBLE FOR UNIT PRICE DENOMINATOR NOT GENERATE UNIT SELECTION
enum class MeasureUnit(
    val id: Long,
    val unitFamilies: Set<UnitFamily>,
    val quantityType: QuantityType,
    val symbol: String,
    val toBase: Double,
    val displayOnly: Boolean
) {
    // Weight
    G(101, setOf(UnitFamily.METRIC), QuantityType.WEIGHT, "g", 1.0, false),
    G100(
        1001,
        setOf(UnitFamily.METRIC),
        QuantityType.WEIGHT,
        "100 g",
        100.0,
        true
    ), // TODO: experimental
    KG(102, setOf(UnitFamily.METRIC), QuantityType.WEIGHT, "kg", 1000.0, false),
    OZ(
        103,
        setOf(UnitFamily.IMPERIAL, UnitFamily.US_CUSTOMARY),
        QuantityType.WEIGHT,
        "oz",
        28.3495,
        false
    ),
    LB(
        104,
        setOf(UnitFamily.IMPERIAL, UnitFamily.US_CUSTOMARY),
        QuantityType.WEIGHT,
        "lb",
        453.592,
        false
    ),

    // Volume
    ML(201, setOf(UnitFamily.METRIC), QuantityType.VOLUME, "ml", 1.0, false),
    ML100(
        2001,
        setOf(UnitFamily.METRIC),
        QuantityType.VOLUME,
        "100 ml",
        100.0,
        true
    ), // TODO: experimental
    L(202, setOf(UnitFamily.METRIC), QuantityType.VOLUME, "l", 1000.0, false),

    // TODO: As a massive hack to help me notice problems during debugging, I have replaced the space in "fl oz" with a U or I to
    // let me see which type is in use. I don't seriously expect subtle bugs here (if we do mess up our unit family handling, we
    // will probably end up with duplicated values in dropdowns which will be fairly obvious), but might as well keep an eye on it.
    // I don't want to add a suffix " (US)" or whatever just for debugging as it will mean the unit sizes aren't realistic in
    // layouts.
    US_CUSTOMARY_FLOZ(
        203,
        setOf(UnitFamily.US_CUSTOMARY),
        QuantityType.VOLUME,
        "flUoz",
        29.5735,
        false
    ),
    US_CUSTOMARY_PINT(
        2033,
        setOf(UnitFamily.US_CUSTOMARY),
        QuantityType.VOLUME,
        "pt",
        473.176473,
        false
    ),
    US_CUSTOMARY_GAL(
        204,
        setOf(UnitFamily.US_CUSTOMARY),
        QuantityType.VOLUME,
        "gal",
        3785.41,
        false
    ),
    IMPERIAL_FLOZ(
        2041,
        setOf(UnitFamily.IMPERIAL),
        QuantityType.VOLUME,
        "flIoz",
        28.4130625,
        false
    ),
    IMPERIAL_PINT(205, setOf(UnitFamily.IMPERIAL), QuantityType.VOLUME, "pt", 568.26125, false),
    IMPERIAL_GAL(206, setOf(UnitFamily.IMPERIAL), QuantityType.VOLUME, "gal", 4546.09, false),

    // Countable items
    // TODO: Should symbol be empty string or something else here? feeling my way. I suspect "" looks best, it may lead to strings like "for 20 " with a trailing space but that's probably not a big deal in practice. (We could also just make a point of trimming strings generated using symbol.) We sort of might want "1" for the unit price denominator stuff though.
    EACH(
        301,
        setOf(UnitFamily.ITEM),
        QuantityType.ITEM,
        "",
        1.0,
        false
    ), // TODO: RENAME "EACH" TO "ITEM"?
    EACH10(302, setOf(UnitFamily.ITEM), QuantityType.ITEM, "10", 10.0, true),
    EACH100(303, setOf(UnitFamily.ITEM), QuantityType.ITEM, "100", 100.0, true);

    companion object {
        fun fromValue(measureUnitId: Long): MeasureUnit? {
            return MeasureUnit.entries.find { it.id == measureUnitId }
        }
    }
}

fun getRelevantUnitFamilies(dataSet: DataSet): Set<UnitFamily> {
    val relevantUnitFamilies = setOfNotNull(
        if (dataSet.allowMetric) UnitFamily.METRIC else null,
        if (dataSet.allowImperial) UnitFamily.IMPERIAL else null,
        if (dataSet.allowUSCustomary) UnitFamily.US_CUSTOMARY else null,
        UnitFamily.ITEM,
    )
    devCheck(relevantUnitFamilies.isNotEmpty()) { "Data set ID ${dataSet.id} has no unit families enabled" }
    devCheck(!(dataSet.allowImperial && dataSet.allowUSCustomary)) { "Data set ID ${dataSet.id} has both imperial and US Customary unit families enabled" }
    return relevantUnitFamilies
}

// TODO: Should this live in the "companion object" on MeasureUnit??
// TODO: Not just here, it may be better to have single high-level unit families metric/US/imperial and use those in combination with quantitytype. This would at least be a purely internal change so I can see how/if it cleans up the code without needing to redo the database.
// TODO: The results from this will probably be shown to the user so order matters. We should maybe
// sort them and/or rely on MeasureUnit.entities having some order. We may want some way for the
// caller to indicate that if there are multiple unit families in the results, they prefer a
// particularly family (e.g. the one the user last used to enter a price) at the top. Within a unit
// family, we should probably order by smallest to largest (which we can do by relying on
// MeasureUnit.entities being in that order, or by sorting on base - probably nicer just to go with
// the baked-in order for now
fun getRelevantMeasureUnits(
    dataSet: DataSet,
    quantityType: QuantityType,
    includeDisplayOnly: Boolean
): List<MeasureUnit> {
    val relevantUnitFamilies = getRelevantUnitFamilies(dataSet)
    val relevantMeasureUnits =
        MeasureUnit.entries.filter { it.quantityType == quantityType && it.unitFamilies.any { it in relevantUnitFamilies } && (!it.displayOnly || includeDisplayOnly) }
    devCheck(relevantMeasureUnits.isNotEmpty()) { "Expected at least one relevant measure unit for QuantityType ${quantityType.name} in the context of data set ID ${dataSet.id} but found none" }
    return relevantMeasureUnits
}

// TODO: Note that this regards measureUnit as its own sibling
// TODO: This is *probably* only used internally to generate some units which we pick among automatically and we don't care about the order of the results.
fun getSiblingMeasureUnits(
    dataSet: DataSet,
    measureUnit: MeasureUnit,
    includeDisplayOnly: Boolean
): List<MeasureUnit> {
    val unitFamily = measureUnit.unitFamilies.intersect(getRelevantUnitFamilies(dataSet))
    devCheck(unitFamily.size == 1) { "Expected MeasureUnit ID ${measureUnit.id} to be a member of exactly one unit family in the context of data set ID ${dataSet.id} but got ${unitFamily.size}" }
    val siblingMeasureUnits =
        MeasureUnit.entries.filter { it.quantityType == measureUnit.quantityType && unitFamily.single() in it.unitFamilies }
    devCheck(siblingMeasureUnits.isNotEmpty()) { "Expected at least one sibling measure unit for MeasureUnit ${measureUnit.id} in the context of data set ID ${dataSet.id} but found none" }
    // TODO: We could verify that measureUnit is a member of the returned list, but it feels a bad
    // idea to do a linear search just for a check.
    return siblingMeasureUnits
}

// TODO: ChatGPT magic, is this really the best way?
fun formatDoubleLocaleAware(
    value: Double,
    maxDecimals: Int,
    locale: Locale = Locale.getDefault()
): String {
    val nf = NumberFormat.getNumberInstance(locale)
    nf.maximumFractionDigits = maxDecimals
    nf.minimumFractionDigits = 0 // Avoid trailing zeros
    nf.isGroupingUsed = false    // Optional: disable thousands separator
    return nf.format(value)
}

data class MeasuredValue(val value: Double, val unit: MeasureUnit) {
    val quantityType: QuantityType get() = unit.quantityType

    fun to(unit: MeasureUnit): MeasuredValue {
        devRequire(this.quantityType == unit.quantityType) {
            "Cannot convert between different quantity types: trying to convert $this (${this.quantityType}) to ${unit.quantityType}"
        }
        val baseValue = this.value * this.unit.toBase
        return MeasuredValue(baseValue / unit.toBase, unit)
    }

    operator fun plus(other: MeasuredValue): MeasuredValue {
        devRequire(this.quantityType == other.quantityType) {
            "Cannot add values of different quantity types (this: $this, other: $other)"
        }
        val otherInThis = other.to(this.unit)
        return MeasuredValue(this.value + otherInThis.value, this.unit)
    }

    fun asValue(unit: MeasureUnit): Double = this.to(unit).value

    // precision is a maximum number of decimal places; we will not pad with trailing zeroes.
    fun toDisplayString(precision: Int): String =
        "${formatDoubleLocaleAware(value, precision)} ${unit.symbol}"
}

@Database(
    entities = [DataSet::class, Item::class, Source::class, Price::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
// TODO: Should not be called *Inventory*Database
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun dataSetDao(): DataSetDao
    abstract fun productDao(): ItemDao
    abstract fun sourceDao(): SourceDao
    abstract fun priceDao(): PriceDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, InventoryDatabase::class.java, "main.db")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val db = InventoryDatabase.getDatabase(context)
                                // TODO: I may want to add multiple demo data sets - if so, given them all names of the form "Demo (foo)", probably. I may at the very least want to do an imperial unit demo set, so new potential users don't assume the app is metric only. This might be overkill but it may not hurt. We could just use imperial with the metric-ish data set (i.e. just configure the display units to be the user's current regional ones by default when we set the database up), and that might well be reasonable - it would give "odd" pack sizes (e.g. nominally imperial demo data selling 2 litre cartons of milk which the shops call a 3.52 pint pack) but for demo purposes it is probably fine.
                                // TODO: We should have some cases in the demo data set where there is no price for a store+product combination
                                db.withTransaction {
                                    // TODO: It's probably smart to default the demo data to the local currency, since that will look most natural to our new user, but do rethink this afterwards. (It's also just possible, remember, that they will start editing the demo dataset for their own use, rather than starting again with a fresh dataset.)
                                    // TODO: Just experimentally, make sure to set the demo data up with a non-local currency and see that the app works!
                                    // TODO: We should probably pick one of IMPERIAL or US_CUSTOMARY here based on the current locale (and make sure any non-metric units in the data below are changed accordingly)
                                    // TODO: We should have some demo products which are (fake) "branded" products, so get the idea across that this is another way to do things if you are brand-sensitive on a particular item
                                    val dataSetId = db.dataSetDao().insert(
                                        DataSet(
                                            name = "Demo",
                                            currencyCode = Currency.getInstance(Locale.getDefault()).currencyCode,
                                            allowMetric = true,
                                            allowImperial = true,
                                            allowUSCustomary = false
                                        )
                                    )
                                    val dataSetId2 = db.dataSetDao().insert(DataSet(name = "Demo 2", currencyCode = "AUD", allowMetric = true, allowImperial = false, allowUSCustomary = true)) // TODO TEMP HACK
                                    val dataSetId3 = db.dataSetDao().insert(DataSet(name = "Demo 3", currencyCode = "AUD", allowMetric = true, allowImperial = false, allowUSCustomary = true)) // TODO TEMP HACK
                                    val item21 = db.productDao().insert(Item(dataSetId = dataSetId2, name = "Demo 2 Item", quantityType = QuantityType.WEIGHT))
                                    val itemIdGroundCoffee = db.productDao().insert(
                                        Item(
                                            dataSetId = dataSetId,
                                            name = "Coffee (ground)",
                                            quantityType = QuantityType.WEIGHT
                                        )
                                    )
                                    val itemIdWholeMilk = db.productDao().insert(
                                        Item(
                                            dataSetId = dataSetId,
                                            name = "Milk (whole)",
                                            quantityType = QuantityType.VOLUME
                                        )
                                    )
                                    val itemIdTeabags = db.productDao().insert(
                                        Item(
                                            dataSetId = dataSetId,
                                            name = "Teabags",
                                            quantityType = QuantityType.ITEM
                                        )
                                    )
                                    // TODO: Do some web searches and confirm these are not real supermarket names
                                    val sourceIdValueMart = db.sourceDao()
                                        .insert(Source(dataSetId = dataSetId, name = "ValueMart"))
                                    val sourceIdSuperiorStore = db.sourceDao().insert(
                                        Source(
                                            dataSetId = dataSetId,
                                            name = "SuperiorStore"
                                        )
                                    )
                                    val now = Instant.now()
                                    db.priceDao().insert(
                                        Price(
                                            dataSetId = dataSetId,
                                            itemId = itemIdGroundCoffee,
                                            sourceId = sourceIdValueMart,
                                            price = 2.03,
                                            measure = 500.0,
                                            originalUnit = MeasureUnit.G,
                                            confirmed = now.minus(2, ChronoUnit.MINUTES),
                                            details = "Large pack own brand"
                                        )
                                    )
                                    db.priceDao().insert(
                                        Price(
                                            dataSetId = dataSetId,
                                            itemId = itemIdGroundCoffee,
                                            sourceId = sourceIdSuperiorStore,
                                            price = 1.50,
                                            measure = 227.0,
                                            originalUnit = MeasureUnit.G,
                                            confirmed = now.minus(4, ChronoUnit.DAYS),
                                            details = "Own brand"
                                        )
                                    )
                                    db.priceDao().insert(
                                        Price(
                                            dataSetId = dataSetId,
                                            itemId = itemIdWholeMilk,
                                            sourceId = sourceIdValueMart,
                                            price = 1.99,
                                            measure = 4 * 568.0,
                                            originalUnit = MeasureUnit.IMPERIAL_PINT,
                                            confirmed = now,
                                            details = ""
                                        )
                                    )
                                    db.priceDao().insert(
                                        Price(
                                            dataSetId = dataSetId,
                                            itemId = itemIdWholeMilk,
                                            sourceId = sourceIdSuperiorStore,
                                            price = 2.86,
                                            measure = 2000.0,
                                            originalUnit = MeasureUnit.L,
                                            confirmed = now.minus(63, ChronoUnit.DAYS),
                                            details = ""
                                        )
                                    )
                                    db.priceDao().insert(
                                        Price(
                                            dataSetId = dataSetId,
                                            itemId = itemIdTeabags,
                                            sourceId = sourceIdValueMart,
                                            price = 0.76,
                                            measure = 40.0,
                                            originalUnit = MeasureUnit.EACH,
                                            confirmed = now.minus(7, ChronoUnit.DAYS),
                                            details = "Soft pack own brand"
                                        )
                                    )
                                    db.priceDao().insert(
                                        Price(
                                            dataSetId = dataSetId,
                                            itemId = itemIdTeabags,
                                            sourceId = sourceIdSuperiorStore,
                                            price = 0.60,
                                            measure = 20.0,
                                            originalUnit = MeasureUnit.EACH,
                                            confirmed = now.minus(4, ChronoUnit.HOURS),
                                            details = ""
                                        )
                                    )
                                    /*
                                    db.productDao().insert(Product(name = "Demo Product"))
                                    db.itemDao().insert(Item(name = "Demo Item"))
                                    // ...insert into other DAOs as needed
                                    */
                                }
                            }
                        }
                    })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

interface PriceTrackerRepository {
    fun getAllDataSets(): Flow<List<DataSet>>
    fun getDataSet(dataSetId: Long): Flow<List<DataSet>>
    fun getAllItems(dataSetId: Long): Flow<List<Item>>
    fun getAllSources(dataSetId: Long): Flow<List<Source>>
    fun getPriceForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<Price>>

    fun getNicePricesForItem(dataSetId: Long, itemId: Long): Flow<List<NicePrice>>

    fun getNicePriceForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<NicePrice>>

    suspend fun updateOrInsertPrice(price: Price)
}

class PriceTrackerRepositoryImpl(
    private val dataSetDao: DataSetDao,
    private val itemDao: ItemDao,
    private val sourceDao: SourceDao,
    private val priceDao: PriceDao
) : PriceTrackerRepository {
    /* TODO
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()

    override fun getItemStream(id: Int): Flow<Item?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Item) = itemDao.insert(item)

    override suspend fun deleteItem(item: Item) = itemDao.delete(item)

    override suspend fun updateItem(item: Item) = itemDao.update(item)
    */

    override fun getAllDataSets(): Flow<List<DataSet>> = dataSetDao.getAllDataSets()

    override fun getDataSet(dataSetId: Long): Flow<List<DataSet>> = dataSetDao.getDataSet(dataSetId)

    override fun getAllItems(dataSetId: Long): Flow<List<Item>> = itemDao.getAllItems(dataSetId)

    override fun getAllSources(dataSetId: Long): Flow<List<Source>> =
        sourceDao.getAllSources(dataSetId)

    override fun getPriceForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<Price>> = priceDao.getPriceForProductAndStore(dataSetId, productId, storeId)

    override fun getNicePricesForItem(dataSetId: Long, itemId: Long): Flow<List<NicePrice>> =
        priceDao.getPriceWithItemForItem(dataSetId= dataSetId, itemId = itemId)
            .map { list -> list.map { it.toDomain() } }



    // TODO: Some ChatGPT magic here, though I am mostly understanding
    // TODO: Use of this function *may* be a red flag now, since I suspect our caller will have data for all stores via getNicePricesForItem
    override fun getNicePriceForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<NicePrice>> =
        priceDao.getPriceWithItemForProductAndStore(dataSetId, productId, storeId)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun updateOrInsertPrice(price: Price) = priceDao.upsert(price)
}

// TODO: WTF?
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Other Initializers
        // Initializer for ItemEntryViewModel
        initializer {
            // TODO: Extra special AI voodoo which wasn't in the codelab but caused startup crashes without it
            val app =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
            // TODO ItemEntryViewModel(MyApplication().itemsRepository)
            PriceTrackerViewModel(app.priceTrackerRepository)
        }
        //...
    }
}

// TODO: WTF? AI hints, complete voodoo
class MyApplication : Application() {
    /* TODO
    val itemsRepository: OfflineItemsRepository by lazy {
        val db = InventoryDatabase.getDatabase(this)
        OfflineItemsRepository(db.itemDao())
    }
    */
    val priceTrackerRepository: PriceTrackerRepositoryImpl by lazy {
        val db = InventoryDatabase.getDatabase(this)
        PriceTrackerRepositoryImpl(db.dataSetDao(), db.productDao(), db.sourceDao(), db.priceDao())
    }

    // TODO: ChatGPT magic, needs checking. May also be worth investigating ACRA/Cockroach/SimpleCrashReport open source libraries.
    // I am fairly sure this specific code is utterly useless anyway. Oddly enough it appears to *hide* a failing check() and
    // commenting it out again restores the crash, but when this code is in place I *don't* seem to get the GlobalExceptionHandler
    // logcat entry. So it seems doubly useless, in that it *hides* crashes somehow!? Really confused.
    /*
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GlobalExceptionHandler", "Uncaught exception", throwable)

            val exceptionMessage = throwable.message ?: "No details"
            val stackTrace = throwable.stackTrace.joinToString("\n").take(500) // limit length

            Handler(Looper.getMainLooper()).post {
                val message = "The app crashed:\n$exceptionMessage\n\n" +
                        "Stack trace:\n$stackTrace\n\n" +
                        "Please take a screenshot or copy this info."

                // You need a way to get current activity or fallback context
                /* TODO: I need to define CurrentActivityHolder myself!? Seems complex and I'll just hack this for now and check those libraries out later
                val activity = CurrentActivityHolder.currentActivity
                if (activity != null) {
                    AlertDialog.Builder(activity)
                        .setTitle("Unexpected error")
                        .setMessage(message)
                        .setPositiveButton("OK") { _, _ ->
                            android.os.Process.killProcess(android.os.Process.myPid())
                            exitProcess(1)
                        }
                        .setCancelable(false)
                        .show()
                } else { */
                    // fallback: just kill app
                    android.os.Process.killProcess(android.os.Process.myPid())
                    exitProcess(1)
                /* }*/
            }
        }
    }
    */
    /*
    // TODO: Simpler ChatGPT magic
    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("UncaughtException", "Uncaught exception in thread ${thread.name}", throwable)
        }
    }
    */

}

// TODO: The database inspector used to show the quantity_type as a string but it seems to have
// stopped working. Not sure why and not the end of the world, but would be nice if that would work
// again.
class Converters {
    // TODO: Grok magic, although it seems logical enough - but read up
    @TypeConverter
    fun fromQuantityType(quantityType: QuantityType?): Int? {
        return quantityType?.value
    }

    @TypeConverter
    fun toQuantityType(value: Int?): QuantityType? {
        return value?.let { QuantityType.fromValue(it) }
    }

    // ChatGPT magic
    @TypeConverter
    fun fromInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun toInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }
}

// TODO: General naming note for databases - both Perplexity and ChatGPT agreed that "_id" suffix on
// column names implies a foreign key - so even if (just as an example - but I need to consider this
// on all tables) we might *later* have a unit table but for now our units are just represented by
// hard-coded in application IDs, columns which store a unit should be called "unit" not "unit_id".
// I am not 100% sure I agree but I do need to at least consider naming for consistency at some
// point, and I wanted to note this opinion.

// TODO: I need to make sure I have the right indexes on all these tables, not sure what if any might get auto-created (and I may want to inhibit some auto-creation if there is any)

@Entity(tableName = "data_set")
// TODO: UI term should probably be "Collection" ("category" sounds a bit like categorising products and we don't want to confuse things)
data class DataSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "currency_code") val currencyCode: String,
    // TODO: For now, I think I will ask the system to format currencies using the currency_code. Later on we may want to add
    // a flag "use system formatting" and some parameters (currency prefix/suffix/decimal places) which the user can specify to
    // override the system formatting. I think it may be that e.g. the system formatting of USD when in a GBP locale may be a bit
    // annoying ("US$ 123.00" instead of "$123.00" perhaps - not tested though) so this extension is not necessarily ridiculous,
    // but let's keep it simple for now. Having the option to use system formatting is good, and that will probably always be the
    // default.
    @ColumnInfo(name = "allow_metric") val allowMetric: Boolean,
    @ColumnInfo(name = "allow_imperial") val allowImperial: Boolean,
    @ColumnInfo(name = "allow_us_customary") val allowUSCustomary: Boolean
)

@Entity(
    tableName = "item", foreignKeys = [
        ForeignKey(
            entity = DataSet::class,
            parentColumns = ["id"],
            childColumns = ["data_set_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    val name: String,
    @ColumnInfo(name = "quantity_type") val quantityType: QuantityType, // TODO: quantity_type*_id* in db?? or is that only for fks?
    // TODO: quantity_type - an enum which says "by item"/"by mass"/"by volume" - the GUI probably *should* allow editing this (not sure though), but wern that editing it will mess up old data (so maybe just don't allow it?)
    // TODO: default_unit - g/kg/oz/floz/litre/etc - this must be consistent with quantity_type (and we may want to let it imply quantity_type rather than storing it explicitly)
)
// TODO: Will temporarily make a note here - I may simply (especially in v1) refuse to allow changes
// of quantity_type in the product edit screen. There is no trivial way to convert. If the user gets
// it wrong and cares, they will notice pretty quickly so having to delete and recreate the product
// shouldn't a huge loss. If the user doesn't notice and doesn't care (and things will mostly "just
// work"), e.g. if the quantity type should be weight but they choose volume and we end up with
// default unit ml but they just type in pack sizes in grammes, things will work as long as they are
// consistent and don't try to do any conversions (which are not a major feature of the current
// design). Fixing this up properly would require a fairly sophisticated and unintuitive GUI where
// we ask the user "what they thought they were entering" so we can apply a suitable correction
// factor (imagine they entered oz weights but the system recorded them as ml, we need to convert
// the "fake" ml values via an oz->gramme conversion to fix up the prices in the database, as
// weights are stored as grammes in there). Not saying a fix it up option won't ever appear if there
// is any demand for it, but even if it exists we probably don't want to over-encourage its use.

@Entity(
    tableName = "source", foreignKeys = [
        ForeignKey(
            entity = DataSet::class,
            parentColumns = ["id"],
            childColumns = ["data_set_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Source(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    val name: String
)

// TODO: Should Price have a price_id on it? If it does, it will need to be nullable (I think) so we can use it in-memory when adding a brand new price, before the db layer assigns an id
// TODO: This needs lots more fields, including the history tracking stuff, but let's start simple
@Entity(
    tableName = "price", foreignKeys = [
        ForeignKey(
            entity = DataSet::class,
            parentColumns = ["id"],
            childColumns = ["data_set_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Source::class,
            parentColumns = ["id"],
            childColumns = ["source_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
// TODO: A record here needs to store:
// - the "pack price" (need to think of generic word for "pack")
// - the "pack measure" (ditto)
// When designing this, think of:
// - £1.20 for 6 bananas
// - £3.00 for 250g
// - £2.00 for 500ml
// We want the "banana" case to be first class - if the shelf says £1.20 for 6 bananas, we don't
// want to force the user to convert this to a unit price themselves. This will probably just fall
// out naturally, but be careful to support it.
//
// The measure will be in a hard-coded "base" unit suitable to the unit type
@Parcelize
// TODO: This should probably be renamed PriceEntity (conventional I believe) or something if my experiment works (I want to make it obvious if this is used, as it normally shouldn't be)
data class Price(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    @ColumnInfo(name = "item_id") val itemId: Long,
    @ColumnInfo(name = "source_id") val sourceId: Long,

    // The item is sold for "price" per "measure", e.g. £1.42 for 500g.
    //
    // We use floating point for the price - it saves worrying about storing in pence or the
    // currency's equivalent and then getting in a mess if somehow the conventional number of
    // decimal places changes. For the kinds of prices we are representing and the limited amount of
    // calculation we are doing on them, there should in practice be no problems at all, as long as
    // we round to the relevant number of decimal places on display.
    //
    // "measure" will always be stored in the metric base unit associated with the item_id's
    // quantity_type. This avoids having to do bulk database updates if the user wants to change
    // unit conventions - this could happen even within a measurement system if shops switch to
    // marking pack sizes in ounces instead of lbs, for example. We use floating point for "measure"
    // because it allows us to round-trip non-metric measures perfectly (provided we round them for
    // display), and it doesn't seem to have any real downside in practice.
    val price: Double,
    val measure: Double, // TODO: would "amount" be a much simpler yet still generic name?? hmm, maybe not - "amount" cost also be a monetary amount - but maybe "quantity" would work? I am cooling on "measure" somewhat right now
    // Although measure is stored in the base unit, we also record the actual unit the user entered
    // the price in. This allows us to show it back to them in the most natural form when they are
    // e.g. comparing the database price with the current shelf price. We do have a default unit
    // stored on the item, but tracking it per actual price allows us to handle situations where
    // supermarket A sells milk in pint multiples (even if the pack still has litres shown, the user
    // may think of this in pints) while supermarket B sells it in litre multiples.
    @ColumnInfo(name = "original_unit") val originalUnit: MeasureUnit,

    // TODO: we need a "confirmed date" - even once we add the historical valid_{from,to} columns,
    // we still need this, because a record can be edited in all sorts of ways (especially a tweak
    // to the notes field, which might just be moting a question to address next time we are at the
    // store) without that indicating a confirmation (although that raises the perhaps thorny point
    // of how we decide when an edit counts as a confirmation - perhaps if the price or pack size
    // changed, we treat that as a confirmation, otherwise we don't - and the user can always click
    // confirm explicitly on the main screen if they want to)
    val confirmed: Instant,

    val details: String // Additional price details TODO: rename "notes"?
) : Parcelable

// TODO: PriceWithItem is arguably redundant now - given we have an original_unit on each price,
// that effectively tells us the quantity type implicitly and we don't need to join to item to get
// it. However, I suspect it still has some value because it allows us to do a bit of extra
// validation which may catch bugs. Probably worth thinking about this again later.
data class PriceWithItem(
    // TODO: should be PriceWithItemEntity eventually
    @Embedded val price: Price,
    @ColumnInfo(name = "quantity_type") val quantityType: QuantityType,
)

//@Parcelize // TODO: probably want this, but check later
data class NicePrice(
    // TODO: probably rename just "Price" once we rename the existing "Price"
    val id: Long = 0,
    val dataSetId: Long,
    val itemId: Long,
    val sourceId: Long,
    val price: Double,
    val measure: MeasuredValue,
    val originalUnit: MeasureUnit,
    val confirmed: Instant,
    val details: String, // Additional price details TODO: rename "notes"?
    // originalQuantityType is a record of the originalQuantityType on measure. It is intended to
    // allow a best effort (protecting against buggy code, not malicious code) validation that when
    // we write back to the database, measure hasn't somehow mutated into a different QuantityType.
    // TODO: NEED TO MAKE SURE I ACTUALLY USE THIS WHEN DOING INSERT/UPDATE
    val originalQuantityType: QuantityType,
) // TODO : Parcelable

// TODO: I suspect we should actually be using the item's "default unit" not its quantityType here - although maybe not, it is perhaps better to keep this in the "internal" unit and convert to the display unit for display, to avoid "oh, it happened to work for me in metric with grams but now I'm in imperial it's displaying badly" concerns
fun Price.toDomain(measureUnit: MeasureUnit): NicePrice =
    NicePrice(
        id = id,
        dataSetId = dataSetId,
        itemId = itemId,
        sourceId = sourceId,
        price = price,
        measure = MeasuredValue(measure, measureUnit),
        originalUnit = originalUnit,
        confirmed = confirmed,
        details = details,
        originalQuantityType = measureUnit.quantityType,
    )

// TODO: Whiff of ChatGPT magic
fun PriceWithItem.toDomain(): NicePrice {
    val baseUnit = when (quantityType) {
        QuantityType.WEIGHT -> MeasureUnit.G
        QuantityType.VOLUME -> MeasureUnit.ML
        QuantityType.ITEM -> MeasureUnit.EACH
    }
    return price.toDomain(baseUnit)
}

@Dao
interface DataSetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dataSet: DataSet): Long

    // TODO: Is this sort case-insensitive? If not I may need to sort myself after, and thus don't need this order by here
    @Query("SELECT * FROM data_set ORDER BY name ASC")
    fun getAllDataSets(): Flow<List<DataSet>>

    @Query("SELECT * FROM data_set WHERE id = :dataSetId")
    fun getDataSet(dataSetId: Long): Flow<List<DataSet>>
}

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    // TODO: Is this sort case-insensitive? If not I may need to sort myself after, and thus don't need this order by here
    @Query("SELECT * FROM item WHERE data_set_id = :dataSetId ORDER BY name ASC")
    fun getAllItems(dataSetId: Long): Flow<List<Item>>
}

@Dao
interface SourceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dataSet: Source): Long

    // TODO: Is this sort case-insensitive? If not I may need to sort myself after, and thus don't need this order by here
    @Query("SELECT * FROM source WHERE data_set_id = :dataSetId ORDER BY name ASC")
    fun getAllSources(dataSetId: Long): Flow<List<Source>>
}

@Dao
interface PriceDao {
    // TODO: Not sure we want insert() or maybe we do but we also want upsert
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(price: Price): Long

    @Upsert()
    suspend fun upsert(price: Price)

    @Query("SELECT * FROM price WHERE data_set_id = :dataSetId AND item_id = :productId AND source_id = :storeId")
    fun getPriceForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<Price>>

    @Query("SELECT price.*, item.quantity_type FROM price JOIN item ON price.item_id = item.id WHERE price.data_set_id = :dataSetId AND price.item_id = :itemId")
    fun getPriceWithItemForItem(
        dataSetId: Long,
        itemId: Long,
    ): Flow<List<PriceWithItem>>

    @Query("SELECT price.*, item.quantity_type FROM price JOIN item ON price.item_id = item.id WHERE price.data_set_id = :dataSetId AND price.item_id = :productId AND price.source_id = :storeId")
    fun getPriceWithItemForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<PriceWithItem>>
}

// ChatGPT magic
class SingleEventState<T>(initialState: T) {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<T> = _state

    private val _events = MutableSharedFlow<T>()
    val events: SharedFlow<T> = _events

    suspend fun update(value: T) {
        _state.value = value
        _events.emit(value)
    }
}

// TODO: Should things in here be "val dataSets: Flow<List<DataSet>> = repository.getAllDataSets()" rather than "getAllDataSets()" functions?
class PriceTrackerViewModel(private val priceTrackerRepository: PriceTrackerRepository) :
    ViewModel() {

    fun getAllDataSets() = priceTrackerRepository.getAllDataSets()

    fun getDataSet(dataSetId: Long) = priceTrackerRepository.getDataSet(dataSetId)

    // val products: Flow<List<Product>> = repository.getAllProducts()
    // val items: Flow<List<Item>> = priceTrackerRepository.getAllItems()
    /*
    fun getAllItems(dataSetId: Long) = priceTrackerRepository.getAllItems(dataSetId)
    */
    fun getAllItems(dataSetId: Long): Flow<List<Item>> {
        return priceTrackerRepository.getAllItems(dataSetId).onEach { items ->
            Log.d("MyApp", "Emitted items: $items")
        }
    }

    fun getItemMap(dataSetId: Long): Flow<Map<Long, Item>> =
        getAllItems(dataSetId).map { list ->
            list.associateBy { it.id }
        }

    fun getAllSources(dataSetId: Long): Flow<List<Source>> =
        priceTrackerRepository.getAllSources(dataSetId)

    fun getSourceMap(dataSetId: Long): Flow<Map<Long, Source>> =
        getAllSources(dataSetId).map { list ->
            list.associateBy { it.id }
        }

    // TODO: DELETE val categories: Flow<List<DataSet>> = priceTrackerRepository.getAllDataSets()

    init {
        Log.d("MyApp", "PriceTrackerViewModel created: $this")
    }
    /* TODO!?
    init {
        _products.postValue(repository.getAllProducts())
    }

    // Example of updating products
    fun updateProducts(newProducts: List<Product>) {
        _products.value = newProducts
    }

    val stores: List<Store> get() = repository.getAllStores()
        */


    /* TODO
    // Prices for selected product (external to ViewModel)
    fun getPricesForProduct(productId: Long): List<Price> {
        return repository.getPricesForProduct(productId)
    }
    */

    // Price details for selected product and store (external to ViewModel)
    //@Composable // TODO!?
    fun getPriceForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<Price>> {
        val priceForProductAndStore =
            priceTrackerRepository.getPriceForProductAndStore(dataSetId, productId, storeId)
        return priceForProductAndStore
    }

    fun getNicePricesForItem(
        dataSetId: Long,
        itemId: Long): Flow<List<NicePrice>> = priceTrackerRepository.getNicePricesForItem(dataSetId = dataSetId, itemId = itemId)

    fun getNicePriceForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<NicePrice>> {
        val priceForProductAndStore =
            priceTrackerRepository.getNicePriceForProductAndStore(dataSetId, productId, storeId)
        return priceForProductAndStore
    }


    // TODO: Is there really no standard abstraction which will wrap all this hellish savestatus crap up?

    enum class SaveStatus { Idle, Saving, Success, Error }

    private val _saveStatus = SingleEventState(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.state
    val saveEvents: SharedFlow<SaveStatus> = _saveStatus.events


    // TODO: I don't think this will insert correctly yet, as Price has no price_id primary key to
    // allow us to indicate to this function when it is an insert rather than an update, but let's
    // worry about that later.
    // TODO: Use upsert in name?
    fun updateOrInsertPrice(price: Price) {
        viewModelScope.launch {
            _saveStatus.update(SaveStatus.Saving)
            try {
                //delay(3700); // TODO TEMP FOR DEBUGGING
                priceTrackerRepository.updateOrInsertPrice(price)
                _saveStatus.update(SaveStatus.Success)
            } catch (e: Exception) {
                _saveStatus.update(SaveStatus.Error) // TODO: how can we preserve e and show it to user in UI?
            }
        }
    }
}

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

val screenBorder = 8.dp
val fullScreenDialogBorder = 24.dp // MD3 specification

// MD3 says 12.dp but MyExposedDropdownMenuBox's dropdown item text doesn't line up with the parent
// TextField text with that. TODO: We could override it for that specific case and use 12.dp for
// other menus?
val menuLeftPadding = 16.dp

// Seems best to make the right padding symmetrical.
val menuRightPadding = menuLeftPadding


// Start Grok chunk
// TODO: This may *not* want to take the viewmodel eventually once refactoring done?
// TODO: RENAME THIS IF IT SURVIVES REFACTORING
@Composable
fun MainScreen(
    dataSet: DataSet?, dataSetList: List<DataSet>?, onSelectedDataSetIdChange: (Long) -> Unit,
    item: Item?, itemList: List<Item>?, onSelectedItemIdChange: (Long) -> Unit
) {
    val selectedDataSetId = dataSet?.id // TODO SEMI TEMP HACK WHILE REFACTORING
    val selectedProductId = item?.id // TODO DITTO

    // TODO: Note that because category and product use a TextField, they have the (I think) nice
    // property that the label expands into a sort of big hint when they are empty. We should
    // probably take advantage of this where having them empty makes sense - and it probably does
    // everywhere, even if it's rare, because the user *could* go and delete every single item in
    // the database in theory. TODO: We should make sure we have the same behaviour for Source,
    // because that actually *should* allow the user to easily set it to empty/none.
    var showItemSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Category Selector
        // TODO: I am starting to think this is the best drop down menu implementation (needs renaming to avoid confusion). We probably don't *want* the primary colour underline highlight here, given that e.g. "buttons" get highlighted by an overall colour change as this does rather than an "underline" - TextFields obviously *do* get this underline for whatever reason known only to MD3 specs, but our TextField is not a "real" TextField so this "darken whole thing" approach is probably consistent
        // TODO: We *may* want to disable the on click ripple whatsit for this, based on how the "official" experimental ExposedDropdownMenuBox behaves - although having thoughts about it and chatted with Grok and ChatGPT, maybe this is *good* and it is a weird quirk of (my impl) of the experimental "official" one that is weird
        MyExposedDropdownMenuBox(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            selectedId = selectedDataSetId,
            onValueChange = { onSelectedDataSetIdChange(it) },
            label = { Text("Category") },
            items = dataSetList ?: emptyList(),
            getId = { it.id },
            getLabel = { it.name },
        )
        // TODO: If we have no data sets, we should (analogous to how the source dropdown works) show a supportingText about selecting one *and hide the rest of the UI*. Nothing makes sense without a dataset, there is no way to pick a product or source. This probably means we need support from our parent (or this needs moving up into the parent) to do that.

        // Item selector
        TextField(
            value = item?.name ?: "",
            onValueChange = { /* No-op, read-only */ },
            label = { Text("Product") },
            enabled = false, // TODO: this is necessary to make "clickable" work, it looks wrong but this is all an experimental hack anyway
            modifier = Modifier
                .fillMaxWidth()
                .clickable { Log.d("MyApp", "SPS"); showItemSheet = true },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Products",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },/* colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ) */
            colors = myTextFieldColors()
        )

        // Item Modal Bottom Sheet
        if (showItemSheet) {
            ModalBottomSheet(onDismissRequest = { showItemSheet = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search Products") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search, contentDescription = "Search"
                            )
                        })
                    LazyColumn {
                        val itemListNonNull = itemList ?: emptyList()
                        items(itemListNonNull.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }) { listItem ->
                            ListItem(
                                headlineContent = { Text(listItem.name) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectedItemIdChange(listItem.id)
                                        showItemSheet = false
                                    })
                        }
                    }
                }
            }
        }
    }
}

// Sometimes we have to make a TextField "enabled = false" for it to be clickable, so we need
// to override the colours to make it look like it is enabled.
@Composable
fun myTextFieldColors() = TextFieldDefaults.colors(
    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    disabledLabelColor = MaterialTheme.colorScheme.primary, // TODO: experimental, "should be" MaterialTheme.colorScheme.onSurfaceVariant, - the idea is that in practice we use these colors for our dropdown selectors and they are "interactive" and maybe deserve highlighting - I have mixed feelings about how good this looks, but will go with primary for the moment
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledIndicatorColor = /* indicatorColor */ MaterialTheme.colorScheme.onSurfaceVariant,
// focusedIndicatorColor = MaterialTheme.colorScheme.primary, // TODO NOT WORKING
)

// TODO: THis needs support for selecting "None" - maybe we just make the user pass it in the input with a null ID, actually?
@Composable
fun <T, ID : Comparable<ID>> MyExposedDropdownMenuBox(
    selectedId: ID?,
    onValueChange: (ID) -> Unit, // TODO: rename onItemSelected? is there a "standard" for e.g. the crappy MD3 experimental dropdown?
    label: @Composable () -> Unit, // TODO: rename to distinguish from getLabel type use?
    supportingText: @Composable (() -> Unit)? = null,
    items: List<T>,
    getId: (T) -> ID,
    getLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    var textFieldWidth by remember { mutableStateOf(0) }

    Column(modifier = modifier) {
        ItemWithDropdown(
            dropdownModifier = Modifier.width(with(LocalDensity.current) { textFieldWidth.toDp() }),
            selectedId = selectedId,
            onValueChange = onValueChange,
            items = items,
            getId = getId,
            getLabel = getLabel,
        ) {
            // TODO: Hacky and probably unnecessary now I am a bit less learning/prototyping
            val itemMap =
                items.associateBy { getId(it) } // TODO: inefficient? should we make caller supply use with this so viewmodel can be caching it
            val PULLEDOUT: String = if (selectedId == null) "" else {
                val item = itemMap[selectedId]
                if (item != null) getLabel(item) else "Invalid ID $selectedId"
            }
            TextField(
                value = PULLEDOUT,
                onValueChange = { /* No-op, handled by dropdown */ },
                label = label,
                // TODO: DELETE - WE DO THIS OURSELVES BELOW supportingText = supportingText,
                readOnly = true,
                enabled = false, // TODO HACK
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        /* TODO modifier = Modifier.rotate(if (expanded) 180f else 0f) */
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    },
                colors = myTextFieldColors() // TODO: not sure this is right, need to think about MD3 etc
            )
        }
        // If we let TextField display supportingText itself, it gets included in the bounding box
        // and the dropdown appears below the supportingText, whereas we want it to drop down over
        // it, "from" the main TextField text box. So we jump through far too many hoops to display
        // it ourselves here.
        Box(modifier = Modifier.padding(start = menuLeftPadding, top = 4.dp)) {
            ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                    supportingText?.invoke()
                }
            }
        }

    }

    /* TODO OLD CODE REF
// TODO: rememberSaveable()? a dark mode toddle will lose expanded otherwise
var expanded by remember { mutableStateOf(false) }/*
val focusedColor   = MaterialTheme.colorScheme.error
val unfocusedColor = MaterialTheme.colorScheme.tertiary //     onSurfaceVariant
var indicatorColor by remember { mutableStateOf(unfocusedColor) } // TODO: ALL THIS STUFF ISN'T WORKING, I SUSPECT THE *FOCUS* ISN'T HITTING THE CONTROL AS IT'S DISABLED, BUT *SOMETHING* IS HITTING IT AND TOGGLING ITS COLOUR BUT IT ISN'T THIS, NOT SURE
*/
var textFieldWidth by remember { mutableStateOf(0) }
val itemMap =
    items.associateBy { getId(it) } // TODO: inefficient? should we make caller supply use with this so viewmodel can be caching it?
val PULLEDOUT: String = if (selectedId == null) "" else {
    val item = itemMap[selectedId]
    if (item != null) getLabel(item) else "Invalid ID $selectedId"
}
Box(modifier = modifier) {
    TextField(
        value = PULLEDOUT,
        onValueChange = { /* No-op, handled by dropdown */ },
        label = label,
        supportingText = supportingText,
        readOnly = true,
        enabled = false, // TODO HACK
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                /* TODO modifier = Modifier.rotate(if (expanded) 180f else 0f) */
            )
        },
        modifier = Modifier
            .clickable { Log.d("MyApp", "CATCLICK"); expanded = true }
            //.onFocusChanged { Log.d("MyApp", if (it.isFocused) "Foc" else "Unfoc"); indicatorColor = if (it.isFocused) focusedColor else unfocusedColor  }
            .fillMaxWidth()
            .onGloballyPositioned { coordinates -> textFieldWidth = coordinates.size.width },/* colors = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface
    ) */
        colors = myTextFieldColors()
    )
    DropdownMenu(
        modifier = Modifier
            .width(with(LocalDensity.current) { textFieldWidth.toDp() })
            .background(MaterialTheme.colorScheme.surfaceContainer), // TODO: REDUNDANT?
        expanded = expanded, onDismissRequest = { expanded = false }) {
        items.forEach { item ->
            // TODO: THE TEXT IN THIS DROPDOWN DOESN'T LEFT-ALIGN WITH THE PARENT TEXTFIELD
            DropdownMenuItem(text = {
                Text(
                    getLabel(item),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }, contentPadding = PaddingValues(start = 16.dp), onClick = {
                onValueChange(getId(item))
                expanded = false
            })
        }
    }
}
*/
}

// End Grok chunk

// LabeledItem() attempts to mimic the label style of a TextField but for "read-only" content. It
// works best with a simple Text() child, but other things are possible.
@Composable
fun LabeledItem(
    label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // Passing LocalTextStyle and LocalContentColor *tries* to influence these aspects of all
        // the content. Some components won't respect this, but many will, and if some don't it
        // does at least introduce a visual inconsistency which I might notice and fix, rather
        // than the content being consistent internally but the wrong size/colour.
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge,
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFullScreenDialog(
    onDismiss: () -> Unit
) {
    // Use a ModalBottomSheet or a Dialog for full-screen effect
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("This is a full-screen dialog", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

// TODO: ChatGPT magic, though I do mostly understand it
@Composable
fun RelativeTimeText(instant: Instant) { // TODO: rename parameter? maybe it's OK
    var now by remember { mutableStateOf(Instant.now()) }
    var ageInSeconds = Duration.between(instant, now).seconds
    val secondsPerDay = 24 * 60 * 60

    if (ageInSeconds < secondsPerDay) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(if (ageInSeconds < 60) 1_000 else 60_000) // TODO: OK? fine tune?
                now = Instant.now()
            }
        }
    }
    // getRelativeTimeSpanString() returns "0 min. ago" in English for ages under 60 seconds, and
    // presumably similar in other languages, so even at the cost of adding another string we'll
    // need to translate, this feels nicer. TODO: Do think about this more, maybe "0 min. ago" is
    // better?
    val relativeTime = if (ageInSeconds < 60) "now" else DateUtils.getRelativeTimeSpanString(
        instant.toEpochMilli(),
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
    Text(relativeTime)
}

// TODO: It may be normal, but it may be that ChatGPT is giving me a weird implementation here. My
// emulated phone think it's in the US, and if I force the default database to use EUR currency, it
// formats prices as "1,50 €". That *is* probably correct within some/all of the EUR-using locales
// (based on speaking with ChatGPT about currency conventions), *but* I am not clear it is the
// "right" way to format a EUR price when we're in the US - at the very least the comma instead of
// decimal point feels wrong, and I suspect the € sign should appear at the front. It may be that
// there is no system-defined way of getting this format, but if there is we should use it. The
// basic approach of finding a local which defaults to the currency feels wrong - we want *our*
// locale to format the other currency. I suspect the code is a bit silly.
fun formatPrice(amount: Double, dataSet: DataSet): String {
    // TODO: ChatGPT magic, hacked up
    val currency = Currency.getInstance(dataSet.currencyCode)

    try {
        // Find a locale that uses this currency
        // TODO: Is this immensely inefficient? Should we be building up some kind of cache of locales for currencies we are interested in at the whole-app level?
        val locale = Locale.getAvailableLocales().find {
            // Not all locales have a currency defined, so we need to catch the exceptions from those and ignore them.
            try {
                Currency.getInstance(it) == currency
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        val numberFormat = NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = currency
        }
        return numberFormat.format(amount)
    }
    // TODO: Are we catching too broadly here? This Java-ish stuff seems to just be able throw anything at any time
    catch (e: Exception) {
        // TODO: Eventually we might want to see if there's any useful data in a currency prefix/suffix/decimal places set of fields in dataSet, but we don't have those yet.
        val numberFormat = NumberFormat.getNumberInstance()
        numberFormat.isGroupingUsed =
            true // TODO: reasonable? probably mostly irrelevant in most currencies for our type of data
        return "TODO" + numberFormat.format(amount)
    }
}

// TODO: EXPERIMENTAL
// TODO: HOW WILL WE HANDLE "/100G" ETC? WILL WE MAKE THESE FIRST CLASS MEASUREUNITS BUT FLAG THEM AS "MULTIPLES" SO WE OMIT THEM FROM MANY CASES, OR WILL WE MAKE IT A LIST<PAIR<MULT,MEASUREUNIT>>?
// TODO: RENAME THIS? "friendlyUnitPrice"???
data class UnitPrice(val numerator: Double, val denominator: MeasureUnit)

fun getUnitPrice(amount: Double, measure: MeasuredValue, denominator: MeasureUnit): UnitPrice =
    UnitPrice(amount / measure.asValue(denominator), denominator)

fun getFriendlyUnitPrice(
    amount: Double,
    measure: MeasuredValue,
    candidateDenominators: List<MeasureUnit>
): UnitPrice {
    devCheck(candidateDenominators.isNotEmpty()) { "Expected at least one candidate denominator" }
    // TODO: We should sanity check to avoid division by zero, log10(0) etc
    var bestScore: Double? = null
    var bestUnitPrice: UnitPrice? = null
    for (candidateDenominator in candidateDenominators) {
        val candidateUnitPrice = getUnitPrice(amount, measure, candidateDenominator)
        // We compute a score (lower is better) for candidateUnitPrice which measures how far away
        // it is in "decimal place" terms from having a numerator of 1. In other words, we are trying
        // to get as close to a single digit before the decimal point as we can.
        // TODO: I'm not sure this score is right - e.g. looking at ground coffee at SuperiorStore,
        // it chooses $0.66/100g but $6.61/kg is probably better. This code could maybe try to
        // down-weight "display only" units, but I'm not sure - anyway, that isn't the issue here.
        // I think we sort of don't want a 0 before the decimal point if we can help it, but our
        // score doesn't take this into account.
        val log10Of1 = 0.0
        val candidateScore = abs(log10(candidateUnitPrice.numerator) - log10Of1) // lower is better
        if (bestScore == null || candidateScore < bestScore) {
            bestScore = candidateScore
            bestUnitPrice = candidateUnitPrice
        }
    }
    return bestUnitPrice!!
}

fun formatUnitPrice(unitPrice: UnitPrice, dataSet: DataSet): String {
    return "${formatPrice(unitPrice.numerator, dataSet)}/${unitPrice.denominator.symbol}"
}

// TODO: Could this be merged with MyExposedDropdownMenuBox by pulling the always-visible part out
// into a child composable? But let's just do it standalone first.
// TODO: Note that selectedId is not used. I would like to use this to focus the previously
// selected item when expanding the dropdown using a D-pad, instead of defaulting to the first
// item. However, this appears to be ninja-grade level development and I tried tweaking multiple
// AI-suggested solutions and got nothing but crashes.
@Composable
fun <T, ID : Comparable<ID>> ItemWithDropdown(
    // TODO: RENAME?
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier, // TODO: OK!?
    selectedId: ID?,
    onValueChange: (ID) -> Unit, // TODO: follow naming convention of MyExposedDropdownMenUBox
    items: List<T>,
    getId: (T) -> ID,
    getLabel: (T) -> String,
    getDividerBetween: ((T, T) -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    // TODO: rememberSaveable? A simple dark mode toggle could lose this otherwise.
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.clickable { expanded = true }) {
        content()

        var previousItem: T? = null
        DropdownMenu(
            modifier = dropdownModifier,
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                // We could make the first argument of getDividerBetween take null and call it every
                // time, but I'm fairly sure it makes no sense to have a divider at the very top
                // of the menu anyway.
                if (previousItem != null && getDividerBetween?.invoke(
                        previousItem!!,
                        item
                    ) == true
                ) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
                previousItem = item

                MyDropdownMenuItem(
                    text = {
                        Text(getLabel(item))
                    },
                    onClick = {
                        onValueChange(getId(item))
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun <T, ID : Comparable<ID>> LabeledItemWithDropdown(
    selectedId: ID?,
    label: String,
    text: String,
    onValueChange: (ID) -> Unit, // TODO: follow naming convention of MyExposedDropdownMenUBox
    items: List<T>,
    getId: (T) -> ID,
    getLabel: (T) -> String,
    getDividerBetween: ((T, T) -> Boolean)? = null,
) {
    // fontSize/iconSize are used here so that the drop down icon scales correctly when the user
    // changes the system font size. (Even if we didn't do this, we'd still want to use a fixed
    // size() Modifier (16.dp works quite nicely at the default settings on my current emulator) to
    // improve the appearance, but it's nicer to take font size into account.)
    val fontSize = MaterialTheme.typography.bodyLarge.fontSize
    val iconSize = with(LocalDensity.current) { fontSize.toDp() }

    ItemWithDropdown(
        selectedId = selectedId,
        onValueChange = onValueChange,
        items = items,
        getId = getId,
        getLabel = getLabel,
        getDividerBetween = getDividerBetween,
    ) {
        LabeledItem(label = label) {
            Row() {
                // TODO: FWIW a quick discussion with ChatGPT suggests it is
                // reasonable for i18n to have some kind of format substitition to
                // generate a unit price string analogous to the one I'm using here.
                // So having a single "Unit price" field is probably reasonable, and
                // it does feel like the clearest way to express it.
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // TODO: I am starting to wonder if this down arrow should be vertically centred wrt the textfield as a whole (including its label-above) not just the text, despite working very hard to get it to be lined up with just the "text content" before - probably arguments both ways, but think about it
                        Text(text)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select unit", // TODO: needs to be passed in by caller
                            modifier = Modifier.size(iconSize /* 16.dp */)
                        )
                    }
                }
            }
        }
    }
}

// This composable provides the at-a-glance status of an item at a particular source. It won't always be visible because we may not have a current source, but when we do this should provide "most" of what a user wants to know:
// - is the item well-priced?
// - do we have an up-to-date price for this item?
// - make it easy for the user to confirm our current price or update it
// - (borderline?) do we have up-to-date prices for other sources? if not it's hard to know if this is well-priced or not no matter how up to the date the price at this source is.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
// TODO: Arguably we should have selected{DataSet,Product}Id not allow nulls here - our parent should just not be composing us if these are not set
// TODO: This should probably work with no selected item and it should show itself but with the variant supporting text "choose a product and store to..."
fun ItemSourceInfo(
    navController: NavHostController,
    dataSet: DataSet,
    item: Item?,
    source: Source?,
    sourceList: List<Source>?,
    onSelectedSourceIdChange: (Long) -> Unit,
    itemPriceList: List<NicePrice>?,
) {
    // TODO: Do we want any kind of "heading" or not? We may want some simple dividers, but those would be provided by the surrounding composables. Gut feeling is we don't want a heading, but think about it.

    // TODO: It might just be the emulator, but right now when the settings screen slides out if source was non-null when we entered settings, there is a visible and
    // rather ugly artefact as the card below this one animates down "as if" this card is expanding, although I don't see any visual
    // effect of this card itself expanding. Need to investigate.
    // TODO: Will we have a free-form text field on item-at-source? For eg things like noting the specific product to help find it again.
    // TODO: Will we have a "special offer"/"short term price" flag and maybe associated data? Gut feeling is no, how to handle expiry/deletion gets complex from UI and internal perspective, it's not as if the offer duration is usually clearly stated, free text note probably can be used for this among other things
    // TODO: Should we show free-form text or special offer information here?
    // TODO: Just possibly the card inside layout should be some kind of grid control rather than row+column?
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // TODO: animateContentSize() is experimental. If I keep it, I may also want it on the lower card, which can change size when product changes (just not yet, in this mockup).
        // The odd padding here is because we want 8.dp at the left and right and 12.dp at the top and bottom to try to keep the square-ish
        // corners of the TextField away from the round-ish corners at the top of the card. Because the bottom of the card has two buttons
        // and these have "touchable but background colour" space around them to meet the minimum touch size (and we don't want to make them visually
        // larger), if we use 12.dp at the bottom we actually get a bit more because of that extra space "around" the buttons. So we manually
        // adjust the bottom padding to visually compensate for this while allowing the buttons to have their natural touch region.
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 8.dp)
        ) {
            // TODO: We need to allow this to be set to empty/None by the user - how best to do that? And if it is empty, we need to collapse all the stuff below it and replace it with a brief instructional string roughly "Select a store to see and edit product details" - check the ChatGPT discussion I saved for some wording
            Log.d("MyApp", "ISI dataset ${dataSet}")
            Log.d("MyApp", "ISI item ${item}")
            Log.d("MyApp", "ISI source ${item}")
            val haveItemAndSource = item != null && source != null
            MyExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                selectedId = source?.id,
                onValueChange = onSelectedSourceIdChange,
                label = { Text("Source") },
                supportingText = if (haveItemAndSource) null else {
                    { Text("Select a product and source to view or change the price there") } // TODO: poor wording
                },
                items = sourceList ?: emptyList(),
                getId = { it.id },
                getLabel = { it.name },
            )
            if (haveItemAndSource) {
                /*
                val priceList by vm.getNicePriceForProductAndStore(
                    dataSetId = dataSet.id,
                    productId = item!!.id,
                    storeId = source!!.id
                ).collectAsStateWithLifecycle(initialValue = emptyList())
                */
                val priceList = itemPriceList?.filter { it.sourceId == source!!.id }

                if (priceList.isNullOrEmpty()) {
                    // TODO: Very quick hack - remember if it matters (not decided what we ought to do) we can distinguish "still loading" or "loaded but not data" via nullness of itemPriceList
                    Text("TODO: No price, do something useful here - this may be transient during db load or it may reflect reality")
                } else {
                    devCheck(priceList.size <= 1) { "Expected 0 or 1 prices for a product and store, but got ${priceList.size}" }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Price as sold"
                        ) { // TODO: quite like this, but maybe "Shelf price"?
                            // TODO: hard coding 2 dp is hacky
                            // TODO: There might be an argument for designing the UI to separate the
                            // price and quantity here, then we side-step the internationalisation
                            // issues of "for", which is *probably* tractable but might be a
                            // problem. If I really prefer the UI with a single text string
                            // containing "for", don't let this put me off sticking with it.
                            Text(
                                "${
                                    formatPrice(
                                        priceList[0].price,
                                        dataSet
                                    )
                                } for ${
                                    priceList[0].measure.to(priceList[0].originalUnit)
                                        .toDisplayString(2)
                                }" /*, color = MaterialTheme.colorScheme.onSurface*/
                            )
                        }

                        LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Last checked") {
                            RelativeTimeText(priceList[0].confirmed)
                            // TODO: would it be helpful to color code this and/or show an icon ("!"?) if this is "old"? maybe even with an ascening amber/red "severity" (and correspondingly different icons?)
                        }

                        // TODO: remember/derivedStateOf?
                        val relevantUnitFamilies = getRelevantUnitFamilies(dataSet)

                        // TODO: It might be more elegant if the parent could pass us a Product and we use the quantity type off that, but except for minor recomposition efficiency concerns, this doesn't matter - the quantity type is absolutely fixed wherever it comes from (units can change, not the quantity type)
                        // TODO: I suspect relevantUnitList can and should be using remember but let's not worry about efficiency right now
                        val relevantUnitList = getRelevantMeasureUnits(
                            dataSet,
                            priceList[0].originalUnit.quantityType,
                            includeDisplayOnly = true
                        )
                        // var items = MeasureUnit.entries.filter { true }
                        var todoSelected by rememberSaveable(dataSet, priceList) {
                            val ur = getSiblingMeasureUnits(
                                dataSet,
                                priceList[0].originalUnit,
                                includeDisplayOnly = true
                            )
                            // TODO: Note that we don't actually use the numerator of up - this might be fine, but it maybe suggests we could simplify the return type. OTOH, we've got to *calculate* the numerators anyway, so maybe we might as well pass it back in case it's handy in some other case?
                            val up = getFriendlyUnitPrice(
                                priceList[0].price,
                                priceList[0].measure,
                                ur
                            )
                            mutableStateOf(up.denominator)
                        }
                        // TODO: If the user selects "g" for a product sold in relative bulk, the standard decimal places on the currency is a bit misleading. This isn't a bug as such, but can/should we try to increase the decimal places on the currency in this case? Does the stanmdard formatting stuff we are using have any concept of "not a shelf price so smaller fractions make sense than usual"? Maybe at the very least we should always round prices *up* when showing with official dp - although we are not doing the conversion ourselves, maybe the standard function has an option to do this?
                        val unitPriceString = formatUnitPrice(
                            getUnitPrice(
                                priceList[0].price,
                                priceList[0].measure,
                                todoSelected
                            ), dataSet
                        )
                        LabeledItemWithDropdown(/* modifier = Modifier.weight(1f), */ label = "Unit price",
                            text = unitPriceString,
                            //  TODO: Mixed feelings about the "/" prefix in this menu.
                            items = relevantUnitList,
                            getId = { it },
                            getLabel = { "/${it.symbol}" },
                            getDividerBetween = { previousItem, item ->
                                var previousItemUnitFamily =
                                    previousItem.unitFamilies.intersect(relevantUnitFamilies)
                                var itemUnitFamily =
                                    item.unitFamilies.intersect(relevantUnitFamilies)
                                previousItemUnitFamily != itemUnitFamily
                            },
                            selectedId = todoSelected,
                            onValueChange = { todoSelected = it })

                    }
                    // TODO: Notes row should probably just be omitted if there are no notes - this is read-only view
                    // TODO: I suspect there's going to be inconsistent padding vertically with or without this, because "other" Rows around it will have 8dp on all sides the way they are currently specified, and if this is missing we'll get 2x8dp gap. But I can tweak this once the layout otherwise settles down (e.g. specify explicit top padding on top Row and bottom padding on bottom Row and do the rest consistently, or something)
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        LabeledItem("Notes") {
                            Text(priceList[0].details)
                        }
                        //Text("TODO DUMMY TO FILL SPACE", modifier=Modifier.padding(vertical=130.dp))
                    }
                    // TODO: Vertical spacing probably poor with or without notes field - needs tweaking/better "plan" for how to specify it - that said, the vertical space above this final row probably should be "a bit" larger than the space between the two "reporting our status" rows - the following row is a "summary plus action" row and does want some visual distinction
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row() {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Checked",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            ) // TODO: probably "primary"=good, default text color=neutral, "error"=bad
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Good price")
                        }
                        // TODO: *If* we consider "Confirm" to be the primary action (potentially users
                        // click it every time they buy this item), it should probably get the bottom
                        // right position, i.e. we should swap its position with "Edit".
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            // TODO: Confirm button sets last updated to "today" and turns itself into "Undo confirm" (or something) on being clicked, we should ideally make this as obvious as possible to the user, maybe some kind of animation
                            FilledTonalButton(onClick = {}, shape = MaterialTheme.shapes.small) {
                                Text("Confirm")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalButton(
                                // TODO: I accidentally wrote a double "}}" at the end of"${source.id}" and it triggered a strict mode
                                // violation. Turning strict mode penaltyDeath off showed a simple NumberFormatException, I fixed the
                                // problem and strict mode penaltyDeath no longer crashes. This is a bit worrying - I do not understand
                                // why this could ever cause a strict mode failure. But there doesn't seem to be much I can do about
                                // it right now. The NumberFormatException was not AFAICS present in logcat on the run where
                                // penaltyDeath killed it.
                                onClick = { navController.navigate("fullScreenDialog/${dataSet.id}/${item.id}/${source.id}/${UUID.randomUUID()}") },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text("Edit") // TODO: "Update"? (we do have a history-ish element, maybe)
                            }
                        }
                    }
                }
            }
            Log.d("MyApp", "TODO5")

        }
    }
}

// TODO: o4-mini code, review if keep
// TODO: I should probably display an arrow of some sort next to the column which controls the sort
// order, and I should probably make it clickable to reverse the order - the main thing being to
// visually indicate that the data is sorted, I don't think in practice changing the order is of
// much interest and I certainly don't see the need to allow sorting on other columns.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DataTable(
    header: List<String>,
    rows: List<List<String>>,
    columnWeights: List<Float> = List(header.size) { 1f }
) {
    // TODO: Is there an argument that the column headings should actually use the same appearance
    // as the labels like "Unit price" - they are arguably playing the same kind of informative
    // role and don't necessarily deserve the bold same-size (ish) text treatment they are currently
    // getting. Maybe this would look weird though - perhaps there is a strong expectation that
    // "tables" do have bold header rows which are "at least as big" as the data rows. Not sure,
    // need to experiment. It *might* be reasonable to use primary for the header background,
    // although I am not sure - and certainly if we did, we would not want to mix that with the
    // small text label style used by LabelledText(). To some extent, I need to consider the
    // appearance of the whole screen in deciding this.
    // TODO: The header should remain fixed even when the list scrolls. - this is now done, but the header now loses contrast when a dark zebra row is adjacent
    // TODO: Should the header and the last item of the list have rounded corners? I am not sure. Probably best square corners TBH.

    Column() {
        // optional header
        Surface(color = MaterialTheme.colorScheme.surfaceContainerHighest) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    header.forEachIndexed { index, title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(columnWeights[index])
                                .padding(end = 8.dp)
                        )
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp, color = MaterialTheme.colorScheme.outline /* Variant */
                )
            }
        }


        // data rows
        rows.forEachIndexed { rowIndex, rowData ->
            // TODO: Zebra-striping is experimental, not sure how I feel about it. Even if we do keep it, note that the header row has a somewhat inconsistent colour - it is darker than the "surface" rows (which is probably good) but lighter than the surfaceVariant rows, which is probably bad (this comment is assuming a light mode display)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp, horizontal = 0.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh /* if (rowIndex % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant */)
            ) {
                // TODO: This inner Row is only here for the zebra-striping - if we get rid of it, we can do without it (and move the padding to the parent Row)
                Column {
                    if (rowIndex > 0) {
                        HorizontalDivider(
                            thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                        rowData.forEachIndexed { index, cell ->
                            Text(
                                text = cell,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(columnWeights[index])
                                    .padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


// Utility to get Activity window
// TODO: Is this used?
@Composable
private fun Context.getActivityWindow(): Window? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context.window
        context = context.baseContext
    }
    return null
}


// A simple wrapper around DropdownMenuItem applying MD3 formatting.
// TODO: This isn't fully general as I don't want to add stuff that isn't going to get tested; I can
// always expand it later.
@Composable
fun MyDropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
): Unit {
    DropdownMenuItem(
        text = {
            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                // Default colour seems to be correct so don't fiddle with it.
                text()
            }
        },
        contentPadding = PaddingValues(start = menuLeftPadding, end = menuRightPadding),
        onClick = onClick,
    )
}

/* TODO: TEMP COPY OF PERPLEXITY FRAGMENT:

// In your ViewModel or Composable
val context = LocalContext.current
val sharedPref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
val initialProductId = sharedPref.getLong("last_selected_product_id", -1L)
var currentProductId by rememberSaveable { mutableStateOf(initialProductId) }

// When user selects a new product:
fun onProductSelected(newId: Long) {
    currentProductId = newId
    sharedPref.edit().putLong("last_selected_product_id", newId).apply()
}

*/
// TODO: Perplexity magic
// TODO: The amount of having to explicitly specify coroutinescopes and contexts in order to be able to run these functions is utterly batshit insane.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val SELECTED_DATA_SET_ID_KEY = longPreferencesKey("selected_data_set_id")
val SELECTED_ITEM_ID_KEY = longPreferencesKey( "selected_item_id")
val SELECTED_SOURCE_ID_KEY = longPreferencesKey( "selected_source_id")

fun <T> getPreference(context: Context, key: Preferences.Key<T>): Flow<T?> =
    context.dataStore.data.map { prefs -> prefs[key] }

suspend fun <T> savePreference(context: Context, key: Preferences.Key<T>, value: T?) {
    context.dataStore.edit { prefs ->
        if (value != null) prefs[key] = value else prefs.remove(key)
    }
}

// TODO: Perplexity magic (if it works, that is)
@Composable
fun <T, K> collectAsStateWithResetOnKeyChange(
    key: K,
    flowProvider: (K) -> Flow<T>,
    initialValue: T
): State<T> {
    var state by remember { mutableStateOf(initialValue) }

    // This effect will run every time 'key' changes
    LaunchedEffect(key) {
        state = initialValue // Reset immediately on key change
        flowProvider(key).collect { value ->
            state = value
        }
    }

    return remember { derivedStateOf { state } }
}

data class ParameterizedResult<T, P>(
    val data: T,
    val parameter: P
)

// TODO: THIS VERY NEARLY SEEMS TO KIND OF WORK, BUT IT MAY -OR IT MAY BE SOMETHING UNRELATED - CAUSE "ENDLESS" EMISSIONS WHEN THERE IS DATA. THIS MAY BE EASILY FIXABLE IF SO . I HAVE TO BREAK OFF FOR NOW.
fun <T, P> parameterizedFlow(
    parameter: P,
    flowProvider: (P) -> Flow<T>,
    emitInitial: Boolean = true,
    initialValue: T? = null
): Flow<ParameterizedResult<T, P>> = flow {
    if (emitInitial && initialValue != null) {
        emit(ParameterizedResult(initialValue, parameter))
    }
    flowProvider(parameter).collect { data ->
        emit(ParameterizedResult(data, parameter))
    }
}

// TODO: Would it actually work just as well for us to read these lists with intial_value emptyList() without going via null?
@Composable
fun HomeScreen(vm: PriceTrackerViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // TODO MAGIC
    val dataSetId by getPreference(context, SELECTED_DATA_SET_ID_KEY).collectAsStateWithLifecycle(initialValue = null)
    val itemId by getPreference(context, SELECTED_ITEM_ID_KEY).collectAsStateWithLifecycle(initialValue = null)
    val sourceId by getPreference(context, SELECTED_SOURCE_ID_KEY).collectAsStateWithLifecycle(initialValue = null)
    Log.d("MyApp", "HomeScreen dataSetId $dataSetId, itemId $itemId, sourceId $sourceId")
    // TODO: This code is a bit inconsistent about fooId vs foo.Id - it *probably* doesn't matter in practice, but we should be clear and consistent.

    // TODO: I seem to be repeatedly told that the "elegant" way to do this is with a sealed class,
    // but every time I try it appears to turn into a nightmare of complexity and nonsense AI
    // advice. It may be worth coming back to this later with more experience under my belt, I don't
    // think it should change the fundamental code structure.
    val dataSetListRaw: List<DataSet>? by vm.getAllDataSets()
        .collectAsStateWithLifecycle(initialValue = null)
    /*
    val dataSetListLoaded = dataSetListRaw != null
    val dataSetList: List<DataSet> = if (dataSetListLoaded) dataSetListRaw!! else emptyList<DataSet>()
    */
    val dataSet = dataSetListRaw?.find { it.id == dataSetId }
    Log.d("MyApp", "dataSet ${dataSet}")

    /*
    // If we have a known-invalid dataSetId, revert to nothing being selected.
    if (dataSetListLoaded && dataSetList.none { it.id == dataSetId } ) {
        setDataSetId(null)
    }
    */
    val TODOdataSetId = dataSetId ?: 3
    /*
key(TODOdataSetId) {
val itemsFlow3 = remember(TODOdataSetId) {
    Log.d("MyApp", "remember ${TODOdataSetId}"); vm.getAllItems(TODOdataSetId)
}
    Log.d("MyApp", "TODOdataSetId ${TODOdataSetId}")
    Log.d("MyApp", "${sourceId}")
    itemsFlow3 = emptyList()
}
*/

    val TODOdataSetId2 = dataSetId ?: 3
    var items6 by remember { mutableStateOf<List<Item>>(emptyList()) }
    LaunchedEffect(TODOdataSetId2) {
        items6 = emptyList()
        vm.getAllItems(TODOdataSetId2).collect { newItems ->
            items6 = newItems
        }

    }
    val itemsFlowX = parameterizedFlow(
        parameter = TODOdataSetId2,
        flowProvider = { id -> vm.getAllItems(id) },
        emitInitial = true,
        initialValue = emptyList()
    )
    val resultX by itemsFlowX.collectAsStateWithLifecycle(initialValue = ParameterizedResult(emptyList(), TODOdataSetId2))

    key(TODOdataSetId) {
        Log.d("MyApp", "TODOdataSetId ${TODOdataSetId}")
        Log.d("MyApp", "${sourceId}")
        val itemsFlow = remember(TODOdataSetId) {
            Log.d("MyApp", "remember ${TODOdataSetId}"); vm.getAllItems(TODOdataSetId)
        }
    val TODODEBUGITEMLISTRAW2 by itemsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Log.d("MyApp", "TODODEBUGITEMLISTRAW2 ${TODODEBUGITEMLISTRAW2}")
    }
    val TODODEBUGITEMLISTRAW: List<Item> by vm.getAllItems(dataSetId ?: 3).collectAsStateWithLifecycle(initialValue = emptyList())
    val TODODEBUGITEMLISTRAW88  by collectAsStateWithResetOnKeyChange(
        key = TODOdataSetId2,
        flowProvider = { id -> vm.getAllItems(TODOdataSetId2) },
        initialValue = emptyList()
    )
    val itemListRaw: List<Item>? by if (dataSetId != null) { vm.getAllItems(dataSetId!!).collectAsStateWithLifecycle(initialValue = null) } else { mutableStateOf(null) }
    val item = itemListRaw?.find { it.id == itemId }
    Log.d("MyApp", "TODODEBUGITEMLISTRAW ${TODODEBUGITEMLISTRAW}")
    Log.d("MyApp", "TODODEBUGITEMLISTRAW88 ${TODODEBUGITEMLISTRAW88}")
    Log.d("MyApp", "TODOPARAMLIST ${resultX.parameter} ${resultX.data}")
    Log.d("MyApp", "items6 ${items6}")
    Log.d("MyApp", "item ${item}")
    Log.d("MyApp", "itemListRaw ${itemListRaw}")

    val sourceListRaw: List<Source>? by if (dataSetId != null) { vm.getAllSources(dataSetId!!).collectAsStateWithLifecycle(initialValue = null) } else { mutableStateOf( null ) }
    val source = sourceListRaw?.find { it.id == sourceId }

    val itemPriceListRaw: List<NicePrice>? by if (dataSetId != null && item?.id != null) {
        vm.getNicePricesForItem(dataSetId = dataSetId!!, itemId = item.id)
            .collectAsStateWithLifecycle(initialValue = null)
    } else {
        mutableStateOf(null)
    }

    // TODO: WIP NOTES AS I REFACTOR
    // What do we *need* for this screen?
    // - list of data set/source/item - these populate our drop downs
    // - specific price record when we have all three of the above selected (subset of next item anyway)
    // - all prices across sources when we have data set and product
    //
    // As a usability thing, we probably want to come back to the screen even after hours/days with
    // *the same stuff* selected automatically. This probably gets stored in a shared preference or
    // similar, *not* the database. Is this good/bad/neutral with our initial composition? As long
    // as we don't actually crash if our assumptions are violated, we can probably reasonably assume
    // that since only we change the database, any such saved values *are* still present in tbe db.

    HomeScreenScaffold(
        vm, navController, dataSet, dataSetListRaw, onSelectedDataSetIdChange = {
            coroutineScope.launch { // TODO: can I use viewmodel's scope?! should I? would it help?
                // TODO: If this *has* changed (the user hasn't reselected the same ID) there might be value in setting the product/source to null - not sure
                savePreference(context, SELECTED_DATA_SET_ID_KEY, it)
            }
        },
        item, itemListRaw, onSelectedItemIdChange = {
            coroutineScope.launch {
                savePreference(context, SELECTED_ITEM_ID_KEY, it)
            }
        },
        source,
        sourceListRaw,
        onSelectedSourceIdChange = {
            coroutineScope.launch {
                savePreference(context, SELECTED_SOURCE_ID_KEY, it)
            }
        },
        itemPriceListRaw
    )

}

@Composable
fun HomeScreenScaffold(
    vm: PriceTrackerViewModel,
    navController: NavHostController,
    dataSet: DataSet?,
    dataSetList: List<DataSet>?,
    onSelectedDataSetIdChange: (Long) -> Unit,
    item: Item?,
    itemList: List<Item>?,
    onSelectedItemIdChange: (Long) -> Unit,
    source: Source?,
    sourceListRaw: List<Source>?,
    onSelectedSourceIdChange: (Long) -> Unit,
    itemPriceListRaw: List<NicePrice>?
) {
    var menuExpanded by remember { mutableStateOf(false) }




    // TODONOW: I THINK IT MIGHT BE THESE NEXT TWO LINES AND THEIR MASSIVE HACK WHICH CAUSE PROBLEMS WHEN WE ARE RUN AND HAVE TO CREATE THE DB AS WE GO
    // var selectedDataSetId: Long by remember { mutableStateOf(1) } // TODO: massive hack defaulting to hardcoded, need to cope with null in some way probably

    //var selectedProductId: Long by rememberSaveable { mutableStateOf(1) } // TODO: massive hack defaulting to hardcoded, we need a genuine ID from somewhere and/or support for null


    // TODO: I added this Surface by analogy with the one in SettingsScreen, but it appears to have
    // no real effect - even if I set its color to Red or primary, nothing shows.
    // TODO: Actually it may or may not be this, but on the O6 at least there does seem to be a weird
    // extra background shade with a bit of the white background down the edges where the border is.
    // No - it is there, but even if I remove this surface it is still there. I will have to experiment further. Part of the issue may be that it's the top-level Nav thing which is responsible.
    // Surface(modifier = Modifier.fillMaxSize()/*, color=MaterialTheme.colorScheme.surface */) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red /* TODO DEBUG HACK */),
        topBar = {
            TopAppBar(
                title = { Text("My App Name Here") }, actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        // TODO: FONTS AND PROB COLORS ON THIS LIST ARE PROB WRONG
                        MyDropdownMenuItem(text = { Text("Edit product list") }, onClick = {
                            menuExpanded = false
                            // Handle navigation or action
                        })
                        MyDropdownMenuItem(text = { Text("Edit categories") }, onClick = {
                            menuExpanded = false
                        })
                        MyDropdownMenuItem(text = { Text("Settings") }, onClick = {
                            menuExpanded = false
                            navController.navigate("settings")
                        })
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface /* TODO? */)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                // .background(MaterialTheme.colorScheme.secondary) // TODO debug hack
                .background(MaterialTheme.colorScheme.background) // TODO?
                .fillMaxSize()
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(innerPadding)
                .padding(screenBorder)

        ) {

            MainScreen(
                dataSet = dataSet,
                dataSetList = dataSetList,
                onSelectedDataSetIdChange = onSelectedDataSetIdChange,
                item = item,
                itemList = itemList,
                onSelectedItemIdChange = onSelectedItemIdChange) // TODO: rename this

            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.height(
                    8.dp
                )
            )

            val dataSetListNullable = dataSetList // TODO TEMP INTERMEDIATE?
            if (dataSet != null) {
                Log.d("MyApp", "HSS dataSet ${dataSet}")
                Log.d("MyApp", "HSS item ${item}")
                ItemSourceInfo(
                    navController = navController,
                    dataSet = dataSet,
                    item = item,
                    source = source,
                    sourceList = sourceListRaw,
                    onSelectedSourceIdChange = onSelectedSourceIdChange,
                    itemPriceList = itemPriceListRaw,
                )
            }

            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.height(
                    8.dp
                )
            )

            // TODO: This mock data shows some questions:
            // - should we include Notes? If we do, should we maybe show the first line only (we can let the user put newlines in the text box, and that gives them some control over what shows in this list, albeit imperfectly). Or we could "..."-truncate the text to fit a single line here in the display. Or we could omit this - but I suppose if the note is "special offer price", that *is* helpful to see for "other" stores (the "selected" store's notes are shown in the other card always)?
            // - if we do include notes, since you can see the current source's notes in full in the other card, should we omit them from the table to save space? or apply special "ellipsis truncation" rules just to the current source' data even if we don't do this for others, or something like that? or is this going to cause confusion?
            // - should we duplicate the unit in the unit price column? we could make the header "Price/100g" or whatever. This might also help avoid column width problems as the data varies.
            // - we will probably want some kind of trailing icon and/or colourisation on the price (or the whole row?) to indicate at the very least "this price is very old" and/or "we have had to apply inflation-ish adjustments to this price"
            // - instead of showing the *user's* notes, we could replace the "Notes" column here with usually-empty stuff which perhaps comments on any icons we put on the price (e.g. "last updated 90 days ago" or "old price"), but this may not fit very nicely in the space available either
            // - should we show a rank on the table rows? probably not necessary really. it is likely to be fixed sort on unit price and it's not that long.
            // - it's not out of the question (but we wouldn't want to insist) the user can provide an icon for each *source* (there aren't that many), and we could use icons for the sources. That said, if they are in addition to the text names they do take up more space, and if they are instead of the text names that may not be super readable *even if* every source does have an icon, especially if these are "square" icons not arbitrary "company name as a logo bitmap" shaped things. Probably simplest to forget this and got with pure text.
            // That said, we are probably looking at 5-10 items in the list "realistic max" (scrolling will always be there as a fallback) but
            // I originally did think the list items might be "two rows high" so we don't have to stick to a precise "table" layout - it
            // can be a list of "double height info cards" if we prefer that. This doesn't necessarily change the decisions to be made here,
            // but things are slightly different if we go with this approach.
            // TODO: The "£/100g" (or whatever, when it's dynamically constructed) should have a contextDescription for screen readers which is "Price per 100g", so it gets read out properly. I think "Price per" is OK (better than "Pounds per", actually), because the rows themselves contain the currency symbol.
            val header = kotlin.collections.listOf("Source", "£/100g", "Notes")
            // TODO: With the £/100g header, it is arguably redundant/incorrect to include the £ on the data values, but I think it's a reasonable compromise for readability and use by non-technical users.
            val data = kotlin.collections.listOf(
                kotlin.collections.listOf(
                    "Tesco", "£2.13", "Tesco Finest is actually cheapest"
                ),
                kotlin.collections.listOf("Sainsbury's Local", "£2.94", ""),
                kotlin.collections.listOf("Asda", "£2.08", "KTC brand"),
                kotlin.collections.listOf("Iceland", "£2.38", ""),
                // …
            )

            // TODO: Price column should be right-aligned, of course
            androidx.compose.material3.Card(
                modifier = androidx.compose.ui.Modifier
                    //.weight(1f, fill=false) // only component with weight, so fills all remaining space
                    .fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer)
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier.padding(
                        horizontal = 8.dp, vertical = 12.dp
                    )
                ) {
                    DataTable(
                        header = header, rows = data,
                        // TODO: Manually tweaking these weights is annoying and risks not working for some user's set of sources. Being clever may help, but it's awkward given the somewhat free form source and the very free form notes.
                        columnWeights = kotlin.collections.listOf(1.6f, 1f, 2.2f)
                    )
                }
            }
        }
    }
}


@Composable
// TODO: https://m3.material.io/components/dialogs/specs says (near bottom) top/left/right padding on a full screen dialog should be 24.dp - I am probably not doing that, should I? Should I use similar padding on "non-dialog full screens" to match??
fun OuterFullScreenDialog(
    vm: PriceTrackerViewModel,
    navController: NavHostController,
    dataSetId: Long,
    productId: Long,
    storeId: Long
) {
    //var vm: PriceTrackerViewModel = viewModel()
    // TODO: Should we just have the caller pass the product name through so we don't have to do this lookup? the viewmodel should have the data cached, but we still have to through the collectstatewithlifecycle overhead?
    // TODO: Are we needlessly getting *all* items here when we could just get the one we are interested in?
    val productMap by vm.getItemMap(dataSetId)
        .collectAsStateWithLifecycle(initialValue = emptyMap())
    val productName = productMap[productId]?.name ?: "Invalid product ID $productId"
    val storeMap by vm.getSourceMap(dataSetId)
        .collectAsStateWithLifecycle(initialValue = emptyMap())
    val storeName = storeMap[storeId]?.name ?: "Invalid store ID $storeId"
    val nullablePriceList: List<Price>? by vm.getPriceForProductAndStore(
        dataSetId = dataSetId,
        productId = productId,
        storeId = storeId
    ).collectAsStateWithLifecycle(initialValue = null)
    val nullableDataSetList: List<DataSet>? by vm.getDataSet(dataSetId)
        .collectAsStateWithLifecycle(initialValue = null)

    if (nullablePriceList == null || nullableDataSetList == null || !(productId in productMap)) {
        // This will almost certainly never be seen - we will likely get the query results back and
        // be recomposed before the first frame.
        // TODO: AT LEAST ON EMULATOR, THIS IS SHOWING UP IN A VERY UGLY WAY DURING SLIDE UP OF "EDIT" SCREEN
        Text("Loading...")
    } else {
        val priceList = nullablePriceList!!
        devCheck(priceList.size <= 1) { "Expected 0 or 1 prices for a product and store, but got ${priceList.size}" }

        devCheck(nullableDataSetList!!.size == 1) { "Expected 1 data set with ID ${dataSetId}, but got ${nullableDataSetList!!.size}" }
        val dataSet = nullableDataSetList!![0]
        // TODO: Create empty price like this feels crap, and it's also not right that the price defaults to 0.0 - it needs to be nullable, and possibly the price should be a string not a double at least in this context, not sure about db
        // TODO: price probably needs rememberSaveable
        //var price by rememberSaveable { mutableStateOf ( if (priceList.isEmpty()) Price(productId = productId, storeId = storeId, price = 0.0, details = "") else priceList[0])}

        val product = productMap[productId]!!

        // Initialize price with a default value
        var price by rememberSaveable {
            mutableStateOf(
                if (priceList.isEmpty()) {
                    // TODO: We may need to allow nulls in some way to accommodate this case properly - I don't yet have any UI for adding a first price
                    Price(
                        dataSetId = dataSetId,
                        itemId = productId,
                        sourceId = storeId,
                        price = 0.0,
                        measure = 0.0,
                        originalUnit = MeasureUnit.ML, // TODO MASSIVE HACK
                        confirmed = Instant.now(), // TODO MASSIVE HACK
                        details = ""
                    )
                } else {
                    priceList[0]
                }
            )
        }
        var originalPrice by rememberSaveable { mutableStateOf(price) }

        // TODO: Can I get rid of saveInitiated and instead set the state inside the viewmodel to "idle" when we are not saving? The frequency with which we check it suggests it might be more painful to get rid of it. but if we track this, the distinction between idle and saving is mostly meainingless (the state never gets set back to idle) and we should maybe merge those states into a vague "meh" state.
        var saveInitiated by rememberSaveable { mutableStateOf(false) }
        var showSaveProgressIndicator by rememberSaveable { mutableStateOf(false) }
        var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
        var showErrorDialog by rememberSaveable { mutableStateOf(false) }
        var showSavingSnackbar by rememberSaveable { mutableStateOf(false) }
        var scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        // TODO: ChatGPT magic. This idea here is that a) currentBackStackEntry reflects the actual
        // back stack, not merely "we have popped but it hasn't come into effect yet" b) this will force
        // isNavigating to be initialised to false when we are re-entered "fresh" but not if e.g. a rotation occurs.
        var isNavigating by remember(navController.currentBackStackEntry) {
            mutableStateOf(false)
        }

        fun popBackStack() {
            // We need isNavigating to de-bounce the close button so we don't do a double pop if
            // the user double taps the close button quickly. (We may not need this for other ways
            // of going back, but it shouldn't hurt and is probably safer.)
            if (!isNavigating) {
                isNavigating = true;
                navController.popBackStack()
            }
        }

        fun onCloseRequest() {
            if (price != originalPrice) {
                showConfirmDialog = true
            } else {
                popBackStack()
            }
        }

        BackHandler {
            if (!saveInitiated) {
                onCloseRequest()
            } else {
                // I've discussed this with LLMs and it's not clear if we should do this or not, but
                // I'll go with it for now.
                showSavingSnackbar = true;
            }
        }

        LaunchedEffect(saveInitiated) {
            if (saveInitiated) {
                // We expect the save to complete quickly so we don't want the visual distraction
                // of a progress indicator appearing straight away. Let the progress indicator kick
                // in after a short delay if we're still here waiting for the save to complete.
                delay(150L)
                showSaveProgressIndicator = true
            }
            // TODO: I don't think we need to set it back to false in else, but maybe revise all
            // this later.
        }

        val saveStatus by vm.saveStatus.collectAsStateWithLifecycle()
        // ChatGPT magic more or less
        LaunchedEffect(Unit) {
            vm.saveEvents.collect { event ->
                when (event) {
                    PriceTrackerViewModel.SaveStatus.Success -> {
                        popBackStack()
                    }

                    PriceTrackerViewModel.SaveStatus.Error -> {
                        saveInitiated = false;
                        showErrorDialog = true;
                    }

                    else -> {}
                }
            }
        }


        // TODO: Grok suggests wrapping a Box with:
        //Modifier.semantics {
        //    role = Role.Dialog // Marks this as a dialog for TalkBack
        //    contentDescription = "Full-screen dialog for [task, e.g., entering details]" // Optional: describe purpose
        //    liveRegion = LiveRegionMode.Polite // Announce when dialog opens
        //} *around* the Scaffold. I am not entirely sure about flagging this as a dialog anyway - I sort of get the MD3 "full screen dialog" concept, but it feels very technical and not something a user (accessibility-using or not) is likely to be actively aware of. I suppose there is some argument that it clues the user in to expect (as there is) a close icon and a "confirm" type icon in the top bar.
        // I suspect I shouldn't provide a contentDescription unless/until I do this for other screens, and at the moment I am trying not to be actively accessibility-hostile but not go out of my way to add stuff that may not be helpful. If the app is released it will be open source and I'm happy to take advice/patches if someone actually is using this.
        // I would rather attach the modifier to the Scaffold if I can, but I don't know if that will work correctly. Maybe it
        // doesn't work with a Box either, I haven't tested that. (Perplexity.ai says this semantics modifier won't truly flag it
        // as a dialog, but the link it gives doesn't actually say that. It doesn't have a better option, short of actually
        // using Dialog, which I know to my cost is utterly impractical or I'd already be using it. Perplexity does say I can
        // attach the modifier to the Scaffold no problem. Perplexity also suggests the liveRegion thing is not necessary or appropriate here - it (I haven't tried to read up on this myself) is sort of related to visual things like scrims, and for a full screen dialog it's not appropriate.
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(enabled = !saveInitiated, onClick = { onCloseRequest() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    title = { Text("TODO: Dialog Title") }, // TODO: Do not use "Edit price", you can also eg edit pack size and probably a free text notes field etc
                    actions = {
                        // TODO: When/where should "data is not valid, we cannot save" check happen? We should probably be putting little warnings on the dialog components as the user edits, but we also need to check this before actually saving if they click save without resolving all the issues.
                        TextButton(enabled = !saveInitiated, onClick = {
                            saveInitiated = true; vm.updateOrInsertPrice(price)
                        }) {
                            if (showSaveProgressIndicator) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                )
                            } else {
                                Text("Save") // TODO: arbitrary, not thought about wording
                            }
                        }
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->

            // TODO: We could probably just pass innerPadding through to FullScreenDialog, that may or may not be clearer
            Column(
                modifier = Modifier
                    // TODO: MD3 spec also has surfaceContainer background for "on-scroll", I am struggling to find any non-LLM explanations here, but *maybe* *if we have scrolled away from the top* we should change the background to the surfaceContainer
                    .background(MaterialTheme.colorScheme.surface) // because this is a full-screen dialog
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = fullScreenDialogBorder) // TODO: looks ugly but I haven't actually designed the dialog properly yet, so let's try to follow recommendation for now
                    .verticalScroll(rememberScrollState())
            ) {
                // TODO: I think the use of "remember" here is far too weak, but this is basically old hacky code and converting to the viewmodel approach will automatically fix this
                var packSize by remember { mutableStateOf("123") }
                var selectedUnitId by remember { mutableStateOf(price.originalUnit.id) }
                var packPrice by remember { mutableStateOf("2.98") }
                //var notes by remember { mutableStateOf("My cool notes") }
                // TODO: Product and Store should maybe be in a row. Just hacking up a rough
                // dialog here for testing of my dialog box code (esp focus stuff) for now.
                LabeledItem(label = "Product") {
                    Text(productName)
                }
                // Spacer(modifier = Modifier.height(300.dp)) // TODO TEMP HACK
                LabeledItem(label = "Store") {
                    Text(storeName)
                }
                Spacer(modifier = Modifier.height(8.dp))
                // TODO: WE PROBABLY WANT SOME remember+derivedStateOf HERE BUT LET'S DO IT WITHOUT FIRST
                val units: List<MeasureUnit> = getRelevantMeasureUnits(
                    dataSet,
                    product.quantityType,
                    includeDisplayOnly = false
                )
                Row {
                    // TODO: Don't really like this way of showing pack size and unit etc, but
                    // this is just a quick hack to get some "realistic-ish" content on the
                    // dialog for testing
                    // TODO: Using weight to size the components is also sucky, since we really
                    // just want "a reasonable fixed size" for the unit with
                    // the product taking whatever's left, but this will do for now.
                    // TODO: This TextField will *not* show a cursor or let the value be changed
                    // - I don't know if this is because my dialog code is breaking it, or I've
                    // done something wrong here. OK, if I copy this code to HomeScreen() it
                    // works, so it is probably dialog related. Yay!
                    // TODO: Should I use OutlinedTextFields here? If so, for the drop down too.
                    // TODO: Note that "Pack size" is blue only when the field is selected, but
                    // "Unit" is always blue. This may be a localised colour tweak or it may be
                    // a systemic glitch e.g. with my dropdown menu. FWIW the background of the
                    // two fields appears to change differenly as I navigate around with cursor
                    // keys too, although this *might* be normal - but worth trying to
                    // investigate.
                    // TODO: When the onscreen keyboard is up for "pack size", clicking on unit
                    // opens the dropdown and hides the keyboard but the dropdown gets
                    // positioned "to avoid" the keyboard - this might be normal/OK, but
                    // check/read/think
                    TextField(
                        label = { Text("Pack size") },
                        value = packSize,
                        onValueChange = { packSize = it },
                        // TODO: keyboardOptions here hints to on-screen keyboard, we probably also ought to prohibit non-numbers or (regional) decimal separator and *maybe* prohibit multiple decimal separators (but maybe this should just be an error report not prohibited, what's normal?)
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // TODO: We *may* want to disable the on click ripple whatsit for this, based on how the "official" experimental ExposedDropdownMenuBox behaves - although having thoughts about it and chatted with Grok and ChatGPT, maybe this is *good* and it is a weird quirk of (my impl) of the experimental "official" one that is weird
                    MyExposedDropdownMenuBox(
                        selectedId = selectedUnitId,
                        onValueChange = { selectedUnitId = it },
                        label = { Text("Unit") },
                        items = units,
                        modifier = Modifier.weight(0.5f),
                        getId = { it.id },
                        getLabel = { it.symbol },
                    )

                }
                Spacer(modifier = Modifier.height(8.dp))
                // TODO: Should the pack price be MaxWidth or something more "restrained" given it's short (5-ish digits absolute max)
                TextField(
                    label = { Text("Pack price") },
                    prefix = { Text("£") },
                    value = packPrice,
                    onValueChange = { packPrice = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(8.dp))
                // TODO: Can/should I do something to scroll the screen when focus enters this and the caret is half-hidden?
                TextField(
                    label = { Text("Notes") },
                    value = price.details,
                    onValueChange = { price = price.copy(details = it) },
                )
                //}
            }

            if (showConfirmDialog) {
                // I copied the wording of this dialog directly from a screenshot in the M3 documentaion.
                AlertDialog(
                    title = { Text("Discard unsaved changes?") },
                    text = { Text("You have changes that won't be saved if you close.") },
                    onDismissRequest = { showConfirmDialog = false },
                    dismissButton = {
                        TextButton(onClick = {
                            showConfirmDialog = false
                        }) { Text("Keep editing") }
                    },
                    confirmButton = {
                        TextButton(onClick = { popBackStack() }) {
                            Text(
                                "Discard"
                            )
                        }
                    },
                )
            }

            if (showErrorDialog) {
                // We use an AlertDialog not a snackbar here. This is a local database save which is
                // failing so it is very unlikely to be transient. We also don't want the user
                // missing the snackbar, thinking the app is buggy ("I already saved, why didn't the
                // dialog close?") and then tapping the close icon without realising their changes
                // have not been saved. (If transient failure was a possibility - e.g. we needed to
                // perform network activity - there might be value in showing a snackbar, maybe with
                // a fallback to an AlertDialog if things keep failing.)
                AlertDialog(
                    title = { Text("Unable to save changes") },
                    text = { Text("An error occurred while saving the changes.") },
                    onDismissRequest = { showErrorDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showErrorDialog = false;
                        }) { Text("OK") }
                    }
                )
            }

            if (showSavingSnackbar) {
                scope.launch {
                    snackbarHostState.showSnackbar("Saving, please wait...")
                    showSavingSnackbar = false
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Settings") }, navigationIcon = {

                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary) // TODO: debug hack
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = screenBorder)

            // TODO: copied from Home, maybe want this but put it in when we do .verticalScroll(androidx.compose.foundation.rememberScrollState())
        ) {
            Text("TODO SETTINGS")
        }
    }
    //}
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Target SDK >=35 directly enables edge-to-edge (see e.g. https://stackoverflow.com/questions/79018063/trying-to-understand-edge-to-edge-in-android). We don't particularly want this, but we can work with it so we don't try to fight it.
        // We call it here to be explicit. TODO: I am far from clear but you can pass some arguments to enableEdgeToEdge(), which may have some relevant effect on older and/or newer platforms. For now I will keep it simple but if there are nightmarish inconsistencies on older versions of Android this might be part of the puzzle.
        enableEdgeToEdge()

        // TODO: ChatGPT suggestion. Correct? Do I need to make this debug-build-only somehow?
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                // TODO: This should be reverted to penaltyDeath()
                .penaltyLog() // .penaltyDeath() // TODO .penaltyLog()  // logs violations; you can also add .penaltyDeath() to crash on violation
                .build()
        )

        // TODO: Experiment with adding a Settings activity and make the dark/light/follow system available and grey out (with some text saying why) follow system on Android < 10
        val isDarkTheme = true /* TODO when (userThemePref) {
            ThemePreference.DARK -> true
            ThemePreference.LIGHT -> false
            ThemePreference.SYSTEM -> isSystemInDarkTheme()
        } */
        setContent {
            val darkTheme = isSystemInDarkTheme()

            ComposeTutorialTheme(darkTheme = darkTheme) {
                val window = (this as ComponentActivity).window

                /*
                // This allows us to control status bar icon color
                SideEffect {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                    insetsController.isAppearanceLightStatusBars = !darkTheme // false in dark mode = light icons
                    insetsController.isAppearanceLightNavigationBars = !darkTheme
                }
                */

                // TODO: Grok told me I could/should shove a DisposableEffect() in here to futz around with isAppearanceLightStatusBars. I don't particularly trust it, but let's make a note in csae this is part of fixing any problems we might see on older Android versions later.
                // TODO: OK, I have added this Surface here because I wondered if I "should" as well as/instead of the Surfaces wrapping
                // the individual screens. Honestly don't know any more. There might be some slightly odd colours on the O6 but maybe
                // they are just its theme. I will have to play around with this and maybe it will become clearer as I write more code
                // etc. fillMaxHeight() is perhaps a bit unusual here but I was experimenting and thought I'd leave it in for now.
                Surface(
                    modifier = Modifier
                        .fillMaxSize()/* .safeDrawingPadding() */.imePadding(),
                    color = Color.Green /* MaterialTheme.colorScheme.background */
                ) {
                    Box(modifier = Modifier.safeDrawingPadding()) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

// TODO: ChatGPT magic
// TODO: Random Grok suggestion to maybe play with later: Use LinearOutSlowInEasing for enter transitions (starts fast, slows down) and FastOutLinearInEasing for exit transitions (starts slow, speeds up) to make the slide feel natural.
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    var vm: PriceTrackerViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        // TODO!? modifier = Modifier.padding(innerPadding)
    ) {
        // TODO: The animation here is complete voodoo. This is a tweaked version of https://stackoverflow.com/questions/65643015/animating-between-composables-in-navigation-with-compose
        // and does actually seem to more-or-less behave (and consistently too). I didn't want to force 700ms, this feels a smidge fast at the (I think) default 300 but I think it is OK.
        // No, no, it isn't consistent. Sometimes the back animation is much faster than others. Not a clue. Not a f* clue.

        composable(
            "home",
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
        ) {
            HomeScreen(vm, navController)
        }
        val tweenDurationMillisEnter = 700; // TODO: should probably be 300 in final version
        val tweenDurationMillisExit = 700; // TODO: should probably be 250 in final version
        // TODO: If possible (probably is) we should factor out the "full screen" and "full screen dialog"
        // transitions into helper functions/variables to avoid duplication.
        composable(
            "settings", enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,

                    animationSpec = tween(
                        durationMillis = tweenDurationMillisEnter,
                        easing = LinearOutSlowInEasing
                    ),
                )

            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(
                        durationMillis = tweenDurationMillisExit,
                        easing = FastOutLinearInEasing
                    )
                )
            }) {
            SettingsScreen(navController)
        }
        composable(
            "fullScreenDialog/{dataSetId}/{productId}/{storeId}/{randomUUID}", enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,

                    animationSpec = tween(
                        durationMillis = tweenDurationMillisEnter,
                        easing = LinearOutSlowInEasing
                    ),
                ) /* TODO DELETE? + fadeIn(
                    animationSpec = tween(
                        durationMillis = tweenDurationMillisEnter, easing = LinearOutSlowInEasing
                    )
                ) */

            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(
                        durationMillis = tweenDurationMillisExit,
                        easing = FastOutLinearInEasing
                    )
                )
            }
        ) { backStackEntry ->
            val dataSetId = backStackEntry.arguments?.getString("dataSetId")?.toLong() ?: 0
            val productId = backStackEntry.arguments?.getString("productId")?.toLong() ?: 0
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLong() ?: 0
            // TODO: DELETE - NOT NEEDED val randomUUID = backStackEntry.arguments?.getString("randomUUID")
            OuterFullScreenDialog(vm, navController, dataSetId, productId, storeId)
        }
    }
}

// TODO: ~/pc-sync/ai-chat-misc-to-move/grok-combo-box-and-alternate-ui.txt is a potentially
// valuable discussion, touching on some implementation ideas, design ideas (small tweaks and
// alternatives) etc and would probably be worth a re-read later.


// TODO: It may be that on a real device more than 4.dp around the screen border looks nice. I should probably introduce a named constant for "screen border" and use that everywhere, it would probably help readability and it *is* a clearly defined concept I can identify, not some vague "it looks nicer with 8dp here" layout thing.

// TODO: I should probably lock the app to portrait mode

// TODO: There is no colour in the app at all when running on P7! Material You active without me realising it? I suspect so - look at Theme.kt, which appears to support dynamic colours. This isn't a problem as such, but should make a note about it, and I may want to offer a setting which allows newer Android versions to choose the app's native theme.

// TODO: There is an ugly animation glitch where the "T" of "TODO" on SettingsScreen hangs around far too long when transitioning between home and settings. This wasn't visible before I added the previously-lost border at the left and right of the main activity. It feels like this might be a clue to some problem with the animations but I am far from sure. In practice this will probably not be an issue if we add the same border to settings, but I am not sure that's a "fix" even if it does happen.

// TODO: In final version make sure e.g. going from home to settings to back doesn't lose category/product/source - I think it does now, but since it's all hacky that is fine in short term


// General note type comments to put somewhere appropriate in long term:
//

// TODO: It is just possible that we should be using somethnig like animateContentSize on the column containing the "cards" on the home screen, so that if (most likely because the Notes field appears/disappears/occupies more or less lines because text is longer or shorter) the card showing price-at-store changes size, the card show price-across-stores below it "animates" discreetly into its new position instead of jumping. With the current test data stuff, the upper card tends to be fixed size so this isn't too noticeable yet.

// TODO: Terminology:
// - "shop" may be better than "store", it is more UK-ish anyway but even in the US we can have a "barber shop" but not a "barber store", so it's slightly more generic
// - "item" is fairly nicely generic and almost works as well as "product" in a supermarket context anyway
// - we could offer (probably not in v1) a "pedant mode" toggle which switches shop->"source" and (if we go that way) "product"->"item", then users can toggle it if they want but they won't be misled on first opening the app
// - not too sure what to do in source code, I have already mixed this up quite a lot but will need to standarise on item+source or product+shop for variable names etc at some point


// Full-screen dialog implementation notes:
//
// I must have written this out in comments or git commit messages or questions to LLMs multiple
// times but for the record (and writing a few days after I finally "solved" it, so my memory might
// be imperfect) as of right now the best way to implement this seems to be to fake it, having the
// full-screen dialog actually be a full screen composable accessed through the regular app
// navigation structure. The level of actual trickery to make this work is relatively small - really
// just that the enter/exit transition needs to be a dialog-like vertical slide, not a sibling-like
// horizontal slide. The full-screen dialog ought to have a dialog-style top bar with a close button
// and a "confirm" button and the back button/gesture needs overriding to behave like the close
// button, but those would probably be necessary however it's implemented.
//
// The other suggestions I received and tried very very hard to implement were:
//
// Dialog: This is the obvious way to do it. The documentation does not that it's not intended for
// full-screen dialogs. The killer problem for me here was that since I needed keyboard input in my
// dialog, I had to allow for the on-screen keyboard sliding in and this really seemed to interact
// badly, even though it was near trivial to get it to work in normal full-screen composables.
//
// Popup: This does (I think) "guarantee" that the stuff on the popup is "on top", although it
// still requires finicky hacks to trap focus and avoid touch input sometimes going to the screen underneath. The killer problem for me was that a
// simple editable TextField didn't work on it, even using a hardware keyboard in the emulator I
// never got to the point of trying it with an on-screen keyboard.
//
// Box with high Z-order: This visually ensures our fake dialog's stuff is "on top", but (as with
// Popup) in ways I don't fully understand, you need to stop touch input sometimes going to the
// screen underneath and without the separate context (?) creataed by Popup, the touch input hacks
// become less reliable. I never actually saw a problem caused by touch input going to the lower
// screen, but that's not to say it could never happen. (The other miscellaneous Dialog-emulating
// hacks required by Popup are also required here.)
//
// Using an actual full-screen activity which is navigated to and has a full-fledged non-dialog status
// avoids nearly all of this. Because it *is* a full-fledged screen, there's no "hidden" stuff which
// could somehow steal touch input or whatever, focus navigation of the contents "just works", the
// on-screen keyboard "just works" (once you make the appropriate tweaks to AndroidManifest.xml
// required to make this work anywhere).
//
// TODO: Review/revise this comment later

// TODO: Move this?
//
// Note to self: Rotations are the canonical example of activities being destroyed and re-created
// fairly casually, but I need to remember they are not the *only* way this happens. In particular,
// a light/dark theme toggle (which might happen at an arbitrary point because battery saver kicks
// in, for example) also does this. So even if the app eventually disables rotations for layout
// reasons, don't assume this gets rid of the need to handle being destroyed and re-created.

// TODO: Do I have to do anything special to accommodate e.g. use of "," as a decimal separator on
// input and/or output, or will the relevant libraries just take care of this for me?

// TODO: I just may need to enable Java desugaring to support older Android versions - this is probably just a one-off config.

// TODO: ChatGPT magic. I sort of get this. For some bizarre reason beyond my comprehension, check() sometimes kills the app but without leaving a clear logcat trace, which makes it very hard to figure out what went wrong.
inline fun devCheck(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        val msg = lazyMessage()
        Log.e("DevCheck", "FAILED CHECK: $msg", Throwable())
        throw IllegalStateException(msg) // same as check()
    }
}

// TODO: Technically this should throw IllegalArgumentException but I don't care. Using the two names allows me to preserve the distinction in the code FWIW but without duplicating the body of devCheck.
inline fun devRequire(condition: Boolean, lazyMessage: () -> String) =
    devCheck(condition, lazyMessage)

/* TODO TEMP TEST CODE FOR MEASUREDVALUE
val foo = MeasuredValue(5.0, MeasureUnit.KG)
val bar = MeasuredValue(2.3, MeasureUnit.ML)
val quux = bar.to(MeasureUnit.FLOZ)
Log.d("MyApp", quux.toString())
var baz = foo + bar
Log.d("MyApp", baz.toString())
*/