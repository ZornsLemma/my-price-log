@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.composetutorial

import java.time.Duration
//import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.parcelize.Parcelize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
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
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
import kotlinx.coroutines.flow.flowOf
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
enum class MeasureUnit(val id : Long, val unitFamilies: Set<UnitFamily>, val quantityType: QuantityType, val symbol: String, val toBase: Double, val displayOnly: Boolean) {
    // Weight
    G( 101, setOf(UnitFamily.METRIC), QuantityType.WEIGHT,  "g", 1.0, false),
    G100( 1001, setOf(UnitFamily.METRIC), QuantityType.WEIGHT, "100 g", 100.0, true), // TODO: experimental
    KG(102, setOf(UnitFamily.METRIC), QuantityType.WEIGHT, "kg", 1000.0, false),
    OZ(103, setOf(UnitFamily.IMPERIAL, UnitFamily.US_CUSTOMARY), QuantityType.WEIGHT, "oz", 28.3495, false),
    LB(104, setOf(UnitFamily.IMPERIAL, UnitFamily.US_CUSTOMARY), QuantityType.WEIGHT, "lb", 453.592, false),

    // TODO: The (US) etc suffixes on here are a nuisance when displaying as they look ugly. We probably want some kind of concept of
    // eliding these for display purposes. My fully fleshed out mental design is that for each data set, the user gets to
    // opt in to either an arbitrary set of weight units or a group of unit families or something, then there is no practical
    // ambiguity and we can (maybe optionally) trim off the bracketed suffix in most contexts. But this may be overkill to start
    // with, and it may be best just to redesign to allow space for the (US) type suffix, or to have a global setting which
    // picks metric/imperial/US customary for everything. But we do probably (even I, now) want "metric+imperial" to be an option.
    // Maybe the simple but relatively clean option would be a "imperial volume vs US customary volume" toggle at the data set
    // level.
    // Volume
    ML(  201, setOf(UnitFamily.METRIC), QuantityType.VOLUME, "ml",   1.0, false),
    ML100 (2001, setOf(UnitFamily.METRIC), QuantityType.VOLUME, "100 ml", 100.0, true), // TODO: experimental
    L(   202, setOf(UnitFamily.METRIC), QuantityType.VOLUME, "l",    1000.0, false),
    // TODO: As a massive hack to help me notice problems during debugging, I have replaced the space in "fl oz" with a U or I to
    // let me see which type is in use. I don't seriously expect subtle bugs here (if we do mess up our unit family handling, we
    // will probably end up with duplicated values in dropdowns which will be fairly obvious), but might as well keep an eye on it.
    // I don't want to add a suffix " (US)" or whatever just for debugging as it will mean the unit sizes aren't realistic in
    // layouts.
    US_CUSTOMARY_FLOZ(203, setOf(UnitFamily.US_CUSTOMARY), QuantityType.VOLUME, "flUoz", 29.5735, false),
    US_CUSTOMARY_PINT(2033, setOf(UnitFamily.US_CUSTOMARY), QuantityType.VOLUME, "pt", 473.176473, false),
    US_CUSTOMARY_GAL( 204, setOf(UnitFamily.US_CUSTOMARY), QuantityType.VOLUME, "gal",  3785.41, false),
    IMPERIAL_FLOZ(2041, setOf(UnitFamily.IMPERIAL), QuantityType.VOLUME, "flIoz", 28.4130625, false),
    IMPERIAL_PINT( 205, setOf(UnitFamily.IMPERIAL), QuantityType.VOLUME, "pt", 568.26125, false),
    IMPERIAL_GAL(206, setOf(UnitFamily.IMPERIAL), QuantityType.VOLUME, "gal", 4546.09, false),

    // Countable items
    // TODO: Should symbol be empty string or something else here? feeling my way. I suspect "" looks best, it may lead to strings like "for 20 " with a trailing space but that's probably not a big deal in practice. (We could also just make a point of trimming strings generated using symbol.) We sort of might want "1" for the unit price denominator stuff though.
    EACH(301, setOf(UnitFamily.ITEM), QuantityType.ITEM, "", 1.0, false), // TODO: RENAME "EACH" TO "ITEM"?
    EACH10(302, setOf(UnitFamily.ITEM), QuantityType.ITEM, "10", 10.0, true),
    EACH100(303, setOf(UnitFamily.ITEM), QuantityType.ITEM, "100", 100.0, true);

    companion object {
        fun fromValue(measureUnitId: Long): MeasureUnit? {
            return MeasureUnit.entries.find { it.id == measureUnitId }
        }
    }
}

// TODO: Should this live in the "companion object" on MeasureUnit??
// TODO: Not just here, it may be better to have single high-level unit families metric/US/imperial and use those in combination with quantitytype. This would at least be a purely internal change so I can see how/if it cleans up the code without needing to redo the database.
fun getRelevantMeasureUnits(dataSet: DataSet, quantityType: QuantityType, includeDisplayOnly : Boolean): List<MeasureUnit> {
    val relevantUnitFamilies = setOfNotNull(
        if (dataSet.allowMetric) UnitFamily.METRIC else null,
        if (dataSet.allowImperial) UnitFamily.IMPERIAL else null,
        if (dataSet.allowUSCustomary) UnitFamily.US_CUSTOMARY else null,
        UnitFamily.ITEM,
    )
    devCheck(relevantUnitFamilies.isNotEmpty()) { "Data set ID ${dataSet.id} has no unit families enabled" }
    devCheck(!(dataSet.allowImperial && dataSet.allowUSCustomary)) { "Data set ID ${dataSet.id} has both imperial and US Customary unit families enabled" }
    return MeasureUnit.entries.filter { it.quantityType == quantityType && it.unitFamilies.any { it in relevantUnitFamilies } && (!it.displayOnly || includeDisplayOnly) }
}

// TODO: ChatGPT magic, is this really the best way?
fun formatDoubleLocaleAware(value: Double, maxDecimals: Int, locale: Locale = Locale.getDefault()): String {
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
    fun toDisplayString(precision: Int): String = "${formatDoubleLocaleAware(value, precision)} ${unit.symbol}"
}

@Database(entities = [DataSet::class, Item::class, Source::class, Price::class], version = 1, exportSchema = false)
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
                        override fun onCreate(db: SupportSQLiteDatabase)
                        {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val db = InventoryDatabase.getDatabase(context)
                                // TODO: I may want to add multiple demo data sets - if so, given them all names of the form "Demo (foo)", probably. I may at the very least want to do an imperial unit demo set, so new potential users don't assume the app is metric only. This might be overkill but it may not hurt. We could just use imperial with the metric-ish data set (i.e. just configure the display units to be the user's current regional ones by default when we set the database up), and that might well be reasonable - it would give "odd" pack sizes (e.g. nominally imperial demo data selling 2 litre cartons of milk which the shops call a 3.52 pint pack) but for demo purposes it is probably fine.
                                // TODO: We should have some cases in the demo data set where there is no price for a store+product combination
                                db.withTransaction {
                                    // TODO: It's probably smart to default the demo data to the local currency, since that will look most natural to our new user, but do rethink this afterwards. (It's also just possible, remember, that they will start editing the demo dataset for their own use, rather than starting again with a fresh dataset.)
                                    // TODO: Just experimentally, make sure to set the demo data up with a non-local currency and see that the app works!
                                    // TODO: We should probably pick one of IMPERIAL or US_CUSTOMARY here based on the current locale (and make sure any non-metric units in the data below are changed accordingly)
                                    val dataSetId = db.dataSetDao().insert(DataSet(name ="Demo", currencyCode = Currency.getInstance(Locale.getDefault()).currencyCode, allowMetric = true, allowImperial = true, allowUSCustomary = false))
                                    val itemIdGroundCoffee = db.productDao().insert(Item(dataSetId = dataSetId, name = "Coffee (ground)", quantityType= QuantityType.WEIGHT))
                                    val itemIdWholeMilk = db.productDao().insert(Item(dataSetId = dataSetId, name = "Milk (whole)", quantityType = QuantityType.VOLUME))
                                    val itemIdTeabags = db.productDao().insert(Item(dataSetId = dataSetId, name = "Teabags", quantityType = QuantityType.ITEM))
                                    // TODO: Do some web searches and confirm these are not real supermarket names
                                    val sourceIdValueMart = db.sourceDao().insert(Source(dataSetId = dataSetId, name = "ValueMart"))
                                    val sourceIdSuperiorStore = db.sourceDao().insert(Source(dataSetId = dataSetId, name = "SuperiorStore"))
                                    val now = Instant.now()
                                    db.priceDao().insert(Price(dataSetId = dataSetId, itemId = itemIdGroundCoffee, sourceId = sourceIdValueMart, price=2.03, measure=500.0, originalUnit=MeasureUnit.G, confirmed = now.minus(2, ChronoUnit.MINUTES), details = "Large pack own brand"))
                                    db.priceDao().insert(Price(dataSetId = dataSetId, itemId = itemIdGroundCoffee, sourceId = sourceIdSuperiorStore, price=1.50, measure=227.0, originalUnit=MeasureUnit.G, confirmed = now.minus(4, ChronoUnit.DAYS), details = "Own brand"))
                                    db.priceDao().insert(Price(dataSetId = dataSetId, itemId = itemIdWholeMilk, sourceId = sourceIdValueMart, price=1.99, measure=4*568.0, originalUnit=MeasureUnit.IMPERIAL_PINT, confirmed = now, details = ""))
                                    db.priceDao().insert(Price(dataSetId = dataSetId, itemId = itemIdWholeMilk, sourceId = sourceIdSuperiorStore, price=2.86, measure=2000.0, originalUnit=MeasureUnit.L, confirmed = now.minus(63, ChronoUnit.DAYS), details = ""))
                                    db.priceDao().insert(Price(dataSetId = dataSetId, itemId = itemIdTeabags, sourceId = sourceIdValueMart, price=0.76, measure=40.0, originalUnit=MeasureUnit.EACH, confirmed = now.minus(7, ChronoUnit.DAYS), details = "Soft pack own brand"))
                                    db.priceDao().insert(Price(dataSetId = dataSetId, itemId = itemIdTeabags, sourceId = sourceIdSuperiorStore, price=0.60, measure=20.0, originalUnit=MeasureUnit.EACH, confirmed = now.minus(4, ChronoUnit.HOURS), details = ""))
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
    fun getPriceForProductAndStore(dataSetId: Long, productId: Long, storeId: Long): Flow<List<Price>>
    fun getNicePriceForProductAndStore(dataSetId: Long, productId: Long, storeId: Long): Flow<List<NicePrice>>
    suspend fun updateOrInsertPrice(price: Price)
}

/* TODO!?
/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val itemsRepository: ItemsRepository
}
*/

/* TODO!?
/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
    }
}
*/

class PriceTrackerRepositoryImpl(
    private val dataSetDao: DataSetDao,
    private val itemDao: ItemDao,
    private val sourceDao: SourceDao,
    private val priceDao: PriceDao) : PriceTrackerRepository {
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

    override fun getAllSources(dataSetId: Long): Flow<List<Source>> = sourceDao.getAllSources(dataSetId)

    override fun getPriceForProductAndStore(dataSetId: Long, productId: Long, storeId: Long): Flow<List<Price>> = priceDao.getPriceForProductAndStore(dataSetId, productId, storeId)

    // TODO: Some ChatGPT magic here, though I am mostly understanding
    override fun getNicePriceForProductAndStore(dataSetId: Long, productId: Long, storeId: Long): Flow<List<NicePrice>> =
        priceDao.getPriceWithItemForProductAndStore(dataSetId, productId, storeId).map { list -> list.map { it.toDomain() } }

    override suspend fun updateOrInsertPrice(price: Price) = priceDao.upsert(price)
}

/* TODO
class ItemEntryViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    /*
    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }
*/
}
*/

// TODO: WTF?
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Other Initializers
        // Initializer for ItemEntryViewModel
        initializer {
            // TODO: Extra special AI voodoo which wasn't in the codelab but caused startup crashes without it
            val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
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

// TODO: This is boilerplate *in memory* viewmodel stuff which I got from Grok. The idea is that I
// can try to start using viewmodels and passing data back and forth between eg my home screen and
// my notional full screen dialog and have it flow round and update rather than being hardcoded (to
// prove communication is working) without getting into the additional worries of having an actual
// database, which I will retrofit later. I have no idea if the code is actually correct, although
// it seems simple enough that I don't think it hides too many nasty surprises.

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
// colun names implies a foreign key - so even if (just as an example - but I need to consider this
// on all tables) we might *later* have a unit table but for now our units are just represented by
// hard-coded in application IDs, columns which store a unit should be called "unit" not "unit_id".
// I am not 100% sure I agree but I do need to at least consider naming for consistency at some
// point, and I wanted to note this opinion.

// TODO: I need to make sure I have the right indexes on all these tables, not sure what if any might get auto-created (and I may want to inhibit some auto-creation if there is any)

data class UnitX(
    val id: Long,
    val name: String
) // TODO: very hacky, not sure how will represent this

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
    @ColumnInfo(name = "data_set_id") val dataSetId : Long,
    val name: String,
    @ColumnInfo(name = "quantity_type") val quantityType : QuantityType, // TODO: quantity_type*_id* in db?? or is that only for fks?
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
    @ColumnInfo(name = "data_set_id") val dataSetId : Long,
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
    @ColumnInfo(name = "data_set_id") val dataSetId : Long,
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
data class PriceWithItem( // TODO: should be PriceWithItemEntity eventually
    @Embedded val price: Price,
    @ColumnInfo(name = "quantity_type") val quantityType : QuantityType,
)

//@Parcelize // TODO: probably want this, but check later
data class NicePrice( // TODO: probably rename just "Price" once we rename the existing "Price"
    val id: Long = 0,
    val dataSetId : Long,
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

    @Query("SELECT price.*, item.quantity_type FROM price JOIN item ON price.item_id = item.id WHERE price.data_set_id = :dataSetId AND price.item_id = :productId AND price.source_id = :storeId")
    fun getPriceWithItemForProductAndStore(
        dataSetId: Long,
        productId: Long,
        storeId: Long
    ): Flow<List<PriceWithItem>>
}

    // TODO: This is part way through being converted to use Flow
class PriceTrackerRepositoryOldTODO {
    /*
    // TODO: listOf may be more correct than mutableListOf everywhere, not just here, but I really don't understand this.
    private val categories = MutableStateFlow<List<Category>>(
        mutableListOf(
            Category(1, "Demo"),
            Category(2, "Groceries (home)"),
            Category(3, "Groceries (Manchester)")
        )
    )
    */
    /*
    private val products = MutableStateFlow<List<Item>>(
        mutableListOf(
            Item(1, "Milk"), Item(2, "Bread")
        )
    )
    private val stores = MutableStateFlow<List<Source>>(
        mutableListOf(
            Source(1, "Walmart"), Source(2, "Target")
        )
    )
    */
    /*
    private val prices = MutableStateFlow<List<Price>>(listOf(
        Price(1, 1, 3.99, "Organic milk at Walmart"),
        Price(1, 2, 4.29, "Organic milk at Target"),
        Price(2, 1, 2.49, "Whole wheat bread at Walmart"),
        Price(2, 2, 2.79, "Whole wheat bread at Target"))
    )
    */

    /*
    fun getAllCategories(): Flow<List<Category>> = categories

    fun getAllProducts(): Flow<List<Item>> = products

    fun addProduct(item: Item) {
        products.value = products.value + item
    }

    fun getAllStores(): Flow<List<Source>> = stores
    */

    /* TODO
    fun getPricesForProduct(productId: Long): List<Price> =
        prices.filter { it.productId == productId }
        */


    /*
    // We expect the returned list to have 0 or 1 items
    fun getPriceForProductAndStore(productId: Long, storeId: Long): Flow<List<Price>> {
        // TODO: I assume we use find() here to avoid duplicating data, but in a db-backed version
        // this would be an actual SELECT. I suspect this code might *work* without writing a
        // SELECT and we'd end up doing an in memory join after pulling in the entire tables.
        /* TODO: Old broken code
        val result = _prices.find { it.productId == productId && it.storeId == storeId }
        if (result == null) {
            return flowOf(listOf())
        } else {
            return flowOf(listOf(result))
        }
        */
        Log.d("MyApp", "repository getPriceForProductAndStore")
        return prices.map { list ->
            list.filter { it.productId == productId && it.storeId == storeId }
        }
            .onEach { filteredList ->
                Log.d("MyApp", "Flow emitted for $productId/$storeId: $filteredList")
            }
    }
    */

    /*
    fun updateOrInsertPrice(price: Price) {
        // TODO: perplexity.ai code
        prices.update { currentPrices ->
            val index = currentPrices.indexOfFirst {
                it.productId == price.productId && it.storeId == price.storeId
            }
            if (index >= 0) {
                // Update existing
                currentPrices.toMutableList().apply {
                    set(index, price)
                }
            } else {
                // Insert new
                currentPrices + price
            }
        }
        Log.d("MyApp", "after updateorinsert: ${prices.value}")

    }
    */
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

class PriceTrackerViewModel(private val priceTrackerRepository: PriceTrackerRepository) : ViewModel() {
    // TODO DELETE private val repository = PriceTrackerRepositoryOldTODO()

    fun getDataSet(dataSetId: Long) = priceTrackerRepository.getDataSet(dataSetId)

    // val products: Flow<List<Product>> = repository.getAllProducts()
    // val items: Flow<List<Item>> = priceTrackerRepository.getAllItems()
    fun getAllItems(dataSetId: Long) = priceTrackerRepository.getAllItems(dataSetId)

    fun getItemMap(dataSetId: Long): Flow<Map<Long, Item>> =
        getAllItems(dataSetId).map { list ->
            list.associateBy { it.id }
    }

    fun getAllSources(dataSetId: Long): Flow<List<Source>> = priceTrackerRepository.getAllSources(dataSetId)

    fun getSourceMap(dataSetId: Long): Flow<Map<Long, Source>> =
        getAllSources(dataSetId).map { list ->
            list.associateBy { it.id }
        }

    val categories: Flow<List<DataSet>> = priceTrackerRepository.getAllDataSets()

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
    fun getPriceForProductAndStore(dataSetId: Long, productId: Long, storeId: Long): Flow<List<Price>> {
        val priceForProductAndStore =
            priceTrackerRepository.getPriceForProductAndStore(dataSetId, productId, storeId)
        return priceForProductAndStore
    }

    fun getNicePriceForProductAndStore(dataSetId: Long, productId: Long, storeId: Long): Flow<List<NicePrice>> {
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

/* TODO
class StoreEditorViewModel : ViewModel() {
    private val repository = PriceTrackerRepository()

    val stores: List<Store> get() = repository.getAllStores()

    fun addStore(name: String) {
        // For prototyping, add to repository directly
        // Later, call repository.insertStore()
    }

    fun deleteStore(storeId: Long) {
        // For prototyping, remove from repository directly
        // Later, call repository.deleteStore()
    }
}
*/

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

val screenBorder = 8.dp
val fullScreenDialogBorder = 24.dp // MD3 specification


// Start Grok chunk
@Composable
fun MainScreen(vm: PriceTrackerViewModel, selectedDataSetId: Long?, onSelectedDataSetIdChange: (Long) -> Unit,
               selectedProductId: Long?, onSelectedProductIdChange: (Long) -> Unit) {
    /* TODO TEMP TEST CODE FOR MEASUREDVALUE
    val foo = MeasuredValue(5.0, MeasureUnit.KG)
    val bar = MeasuredValue(2.3, MeasureUnit.ML)
    val quux = bar.to(MeasureUnit.FLOZ)
    Log.d("MyApp", quux.toString())
    var baz = foo + bar
    Log.d("MyApp", baz.toString())
    */
    // TODO: Note that because category and product use a TextField, they have the (I think) nice
    // property that the label expands into a sort of big hint when they are empty. We should
    // probably take advantage of this where having them empty makes sense - and it probably does
    // everywhere, even if it's rare, because the user *could* go and delete every single item in
    // the database in theory. TODO: We should make sure we have the same behaviour for Source,
    // because that actually *should* allow the user to easily set it to empty/none.
    var showProductSheet by remember { mutableStateOf(false) }
    //val categories = listOf("Demo", "Groceries (home)", "Groceries (Manchester)")
    //val products = listOf("Beans", "Milk", "Bread", "Chicken" /* ... */)
    var searchQuery by remember { mutableStateOf("") }

    // TODO: I suspect in general (not just here) I should be passing viewmodel *into* these functions rather than getting it from "global", to allow for dependency injection. but in practice it wouldn't be hard to rework this after and i am not sure this ui stuff is testable - I really don't know how it works.
    //var vm: PriceTrackerViewModel = viewModel()
    val categories by vm.categories.collectAsStateWithLifecycle(initialValue = emptyList())
    val products by if (selectedDataSetId != null ) {
        vm.getAllItems(selectedDataSetId!!).collectAsStateWithLifecycle(initialValue = emptyList())
    } else {
        flowOf(emptyList<Item>()).collectAsStateWithLifecycle(initialValue = emptyList())
    }
    val productMap by if (selectedDataSetId != null) {
        vm.getItemMap(selectedDataSetId!!).collectAsStateWithLifecycle(initialValue = emptyMap())
    } else {
        flowOf(emptyMap<Long, Item>()).collectAsStateWithLifecycle(initialValue = emptyMap())
    }

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
            items = categories,
            getId = { it.id },
            getLabel = { it.name },
        )

        // Product Selector
        TextField(
            value = productMap[selectedProductId]?.name ?: "Invalid product ID $selectedProductId",
            onValueChange = { /* No-op, read-only */ },
            label = { Text("Product") },
            enabled = false, // TODO: this is necessary to make "clickable" work, it looks wrong but this is all an experimental hack anyway
            modifier = Modifier
                .fillMaxWidth()
                .clickable { Log.d("MyApp", "SPS"); showProductSheet = true },
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

        // Product Modal Bottom Sheet
        if (showProductSheet) {
            ModalBottomSheet(onDismissRequest = { showProductSheet = false }) {
                //Log.d("MyApp", "FFS:" + items(products).joinToString(","))
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
                        items(products.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }) { product ->
                            ListItem(
                                headlineContent = { Text(product.name) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectedProductIdChange(product.id)
                                        showProductSheet = false
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
        val numberFormat =  NumberFormat.getNumberInstance()
        numberFormat.isGroupingUsed = true // TODO: reasonable? probably mostly irrelevant in most currencies for our type of data
        return "TODO" + numberFormat.format(amount)
    }
}

/*
// TODO: FEELING MY WAY HERE, V EXPERIMENTAL
fun unitPriceDenominatorCandidates(
    amount: Double, measure: MeasuredValue, defaultUnit: MeasureUnit, extraUnit: MeasureUnit?) : List<TODO> {
    // TODO!
}
*/

// TODO: EXPERIMENTAL
// TODO: HOW WILL WE HANDLE "/100G" ETC? WILL WE MAKE THESE FIRST CLASS MEASUREUNITS BUT FLAG THEM AS "MULTIPLES" SO WE OMIT THEM FROM MANY CASES, OR WILL WE MAKE IT A LIST<PAIR<MULT,MEASUREUNIT>>?
// TODO: RENAME THIS? "friendlyUnitPrice"???
data class UnitPrice(val numerator: Double, val denominator: MeasureUnit)
fun getFriendlyUnitPrice(amount: Double, measure: MeasuredValue, candidateDenominators: List<MeasureUnit>) : UnitPrice {
    devCheck(candidateDenominators.isNotEmpty()) { "Expected at least one candidate denominator" }
    // TODO: We should sanity check to avoid division by zero, log10(0) etc
    var bestScore: Double? = null
    var bestUnitPrice: UnitPrice? = null
    for (candidateDenominator in candidateDenominators) {
        val measureWithCandidate = measure.asValue(candidateDenominator)
        val candidateUnitPrice = UnitPrice(amount / measureWithCandidate, candidateDenominator)
        // We compute a score (lower is better) for candidateUnitPrice which measures how far away
        // it is in "decimal place" terms from having a numerator of 1. In other words, we are trying
        // to get as close to a single digit before the decimal point as we can.
        val log10Of1 = 0.0
        val candidateScore = abs(log10(candidateUnitPrice.numerator) - log10Of1) // lower is better
        if (bestScore == null || candidateScore < bestScore) {
            bestScore = candidateScore
            bestUnitPrice = candidateUnitPrice
        }
    }
    return bestUnitPrice!!
}

fun formatUnitPrice(unitPrice: UnitPrice, dataSet: DataSet) : String {
    return "${formatPrice(unitPrice.numerator, dataSet)}/${unitPrice.denominator.symbol}"
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
fun ItemSourceInfo(vm: PriceTrackerViewModel, navController: NavHostController, dataSet: DataSet, selectedProductId: Long?) {
    Log.d("MyApp", "TODO0")
    // TODO: Do we want any kind of "heading" or not? We may want some simple dividers, but those would be provided by the surrounding composables. Gut feeling is we don't want a heading, but think about it.
    var expanded by remember { mutableStateOf(false) }
    var currentUnit by remember { mutableStateOf("100g") }

    //var vm: PriceTrackerViewModel = viewModel()
    val selectedDataSetId = dataSet.id // TODO: maybe a temp hack?
    Log.d("MyApp", "TODO1")
    val sources by vm.getAllSources(selectedDataSetId).collectAsStateWithLifecycle(initialValue = emptyList())

    // fontSize/iconSize are used here so that the drop down icon scales correctly when the user
    // changes the system font size. (Even if we didn't do this, we'd still want to use a fixed
    // size() Modifier (16.dp works quite nicely at the default settings on my current emulator) to
    // improve the appearance, but it's nicer to take font size into account.)
    val fontSize = MaterialTheme.typography.bodyLarge.fontSize
    val iconSize = with(LocalDensity.current) { fontSize.toDp() }
    var selectedSourceId: Long? by rememberSaveable { mutableStateOf(null) }
    Log.d("MyApp", "TODO2")
    //val sources = listOf("None", "Tesco", "Asda", "Sainsbury's Local", "Iceland")
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
            Log.d("MyApp", "TODO3")
            // TODO: We need to allow this to be set to empty/None by the user - how best to do that? And if it is empty, we need to collapse all the stuff below it and replace it with a brief instructional string roughly "Select a store to see and edit product details" - check the ChatGPT discussion I saved for some wording
            MyExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                selectedId = selectedSourceId,
                onValueChange = { selectedSourceId = it },
                label = { Text("Source") },
                supportingText = if (selectedSourceId != null) null else {
                    { Text("Select a source to view or change the price there") }
                },
                items = sources,
                getId = { it.id },
                getLabel = { it.name },
            )
            Log.d("MyApp", "TODO4")
            if (selectedSourceId != null) {
                // TODO: DEFAULTING TO PRODUCT ID 1 IS A MASSIVE HACK BUT I DON'T WANT TO GET SIDETRACKED THINKING ABOUT NULL CASE RIGHT NOW
                val priceList by vm.getNicePriceForProductAndStore(
                    dataSetId = selectedDataSetId,
                    productId = if (selectedProductId == null) 1 else selectedProductId,
                    storeId = selectedSourceId!!
                ).collectAsStateWithLifecycle(initialValue = emptyList())
                Log.d("MyApp", "Recomposed with priceList: $priceList")
                devCheck(priceList.size <= 1) { "Expected 0 or 1 prices for a product and store, but got ${priceList.size}" }

                if (priceList.isEmpty()) {
                    // TODO: Very quick hack
                    Text("TODO: No price, do something useful here")
                } else {


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
                            Text("${formatPrice(priceList[0].price, dataSet)} for ${priceList[0].measure.to(priceList[0].originalUnit).toDisplayString(2)}" /*, color = MaterialTheme.colorScheme.onSurface*/)
                        }

                        LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Last checked") {
                            RelativeTimeText(priceList[0].confirmed)
                            // TODO: would it be helpful to color code this and/or show an icon ("!"?) if this is "old"? maybe even with an ascening amber/red "severity" (and correspondingly different icons?)
                        }
                        LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Unit price") {
                            Row() {
                                // TODO: FWIW a quick discussion with ChatGPT suggests it is
                                // reasonable for i18n to have some kind of format substitition to
                                // generate a unit price string analogous to the one I'm using here.
                                // So having a single "Unit price" field is probably reasonable, and
                                // it does feel like the clearest way to express it.
                                /* TODO: Old code
                                Text("£2.30/")
                                Box {
                                    Row(
                                        modifier = Modifier.clickable { expanded = true },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Text(
                                            text = currentUnit,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.alignBy(LastBaseline)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Select unit",
                                            modifier = Modifier.size(iconSize /* 16.dp */)
                                        )
                                    }
                                    // TODO: I probably actually don't want this dropdown. It just *might* make sense to allow
                                    // the unit to be temporarily changed here (some slightly contrived situation where we're
                                    // looking at a new product on shelf and want to see if it's potentially cheaper but it
                                    // uses a different unit price as shown on shelf, for example - but we're already not
                                    // doing that well, if anything we want a "check new product" option which lets us enter
                                    // its pack size and shelf price and compute unit price ourself, there may not be a unit
                                    // price on shelf or it may not be correct if there's an offer), but it's far from clear,
                                    // and if anything it might make more sense to have a screen-wide "temporarily use X as
                                    // the unit price unit" setting which also affects the card with the cross-store prices
                                    // on. I won't rip this out of the UI yet, but I suspect in a finished first version of
                                    // the app this code will be gone, at least from specifically here.
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }) {
                                        var availableUnits = listOf("100g", "kg", "oz")
                                        availableUnits.forEach { selectionOption ->
                                            DropdownMenuItem(
                                                text = { Text(selectionOption) },
                                                onClick = {
                                                    currentUnit = selectionOption
                                                    expanded = false
                                                })
                                        }
                                    }
                                }
                                */
                                // TODO: I am rather thinking given how close we are (we already ahve it half implemented), if the product's default unit and the originalUnit on this price are different, we should default to using something in the originalUnit family here but also offer a dropdown with the friendly choice from the default unit family.
                                // TODO: We may want to use remember() here to avoid redoing the unit price formatting all the time - although it's possible the outer composable "key-caching" will take care of this, I can't think straight about it right now
                                // TODO: It is wrong to use pricelist[0].originalUnit for unitRelatives - well, not necessarily wrong, especially if we maybe offer a selection via a dropdown - but the "primary" unit family should probably be the default unit on the item - but we don't have that yet
                                // TODO:Rename "up" to unitPrice, after renaming unitPrice() function to free the name up? Ditto "ur"?
                                Log.d("MyApp", "FOO1")
                                val ur = getRelevantMeasureUnits(dataSet, priceList[0].originalUnit.quantityType, includeDisplayOnly = true)
                                Log.d("MyApp", "FOO2")
                                val up = getFriendlyUnitPrice(priceList[0].price, priceList[0].measure, ur)
                                Log.d("MyApp", "FOO3")
                                Text(formatUnitPrice(up, dataSet))
                                Log.d("MyApp", "FOO4")
                                // Text("TODO") // Text(formatUnitPrice(priceList[0].price, priceList[0].measure, TODOHINT?))
                            }
                        }
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
                                onClick = { navController.navigate("fullScreenDialog/$selectedDataSetId/$selectedProductId/$selectedSourceId/${UUID.randomUUID()}") },
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

// TODO: This probably needs to track state/events via parents
// TODO: A final version of this might want an internal (database, not linear) ID for each item and it might expose that ID as well as/instead of the associated String to the caller, but the ID is of course invisible to the UI
// TODO: OK - this just may fix my problems and/or simply be "right" - should I be using ExposedDropdownMenu(Box) - this may practically *be* a standard combo box? (see e.g.https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-exposed-dropdown-menu-box.html - TBH documentation on this feels oddly sparse) - FWIW https://m3.material.io/components/menus/guidelines under "Filtering" looks like precisely what I want
// TODO: Next problem to solve - if you have cursor in the combobox text and click the icon to make
// the dropdown appear, the cursor disappears (fine). If you click the icon again, the cursor
// reappears (also fine). But if instead of clicking the icon, you click "off" it (let's say in th
// middle of the screen, to be clear) the dropdown disappears (good) but the cursor remains in the
// label (bad). I have been unable to get ChatGPT or Grok to solve this. Note that as per comment
// below, you cannot trivially distinguish these two cases because we do *not* see a click on the
// dropdown icon when we close it. For what it's worth, both ChatGPT and Grok suggest that the
// dropdown is a Popup which overlays the entire screen and effectively blocks the
// clearFocusOnTapOutside code from executing in this case. (Not that it would necessarily do the
// right thing anyway, because it would clear the focus in all cases, which we don't want.)
// (Aside: since I *actually* want to be showing a filtered list of options below the combobox as
// soon as it contains any text at all, it is possible that this precise problem won't exist in
// a final version of this code - the situation where the cursor is in the combobox but the dropdown
// is not shown probably isn't possible. I suspect it will be instructive to try to address this
// anyway to build confidence with forcing Compose to actually do things which are sensible despite
// its natural inclinations. Still, if solving this particular problem eludes me, it might be as
// well to push on with the filtering always-show-dropdown version I actually want and see how
// that goes. I do actually wonder if the dropdown arrow is necessary in this final version,
// although maybe - I'm not sure right now - having it there is helpful as offering one explicit
// way to get rid of the dropdown and go with whatever text is currently in the TextField. In reality
// it's probably not necessary to do that, because you can click anywhere on the screen - modulo
// worries about that activating a button - to get the same behaviour, but it is probably nice to
// have the hint that you can click the dropdown arrow to close the dropdown list, as well as have
// it as a known safe place you can click without other side effects.)
@Composable
fun ComboBox(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    content: List<String>, // TODO: poor name for var?
    modifier: Modifier = Modifier
) {
    // TODO: I suspect we may not want to pass all value changes in the text box through to the onValueChange provided by our parent. We are encouraging the user to type partial substrings which are meaningless to the parent, and it also doesn't really care anyway until we have "finished" and have a possibly-valid (but we may not) string, either because the user typed it or because they clicked it in the list. For now I am not even trying to call the parent and just ignoring the value they supply.
    var isExpanded by remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Column() {
        Row(modifier = modifier) {
            TextField(
                label = { Text(label) },
                value = text,
                onValueChange = { newText -> text = newText },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            // Superficially we want "isExpanded = !isExpanded" here, but that's not
                            // how it works. Clicking on the icon in an attempt to close the
                            // dropdown actually just triggers onDismissRequest on the dropdown and
                            // this code never gets called, presumably because it is a click not on
                            // the dropdown. Of course the toggle code is not wrong, but the
                            // implication that it's called symmetrically is confusing when trying
                            // to get focus behaviour correct, so let's be explicit.
                            isExpanded = true
                        })
                })
        }
        // TODO: I may need to use LazyColumn inside DropdownMenu to get laziness, given my list could have approx 100 items. Not worrying about that just now.
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            for (item in content) {
                DropdownMenuItem(text = { Text(item) }, onClick = {
                    text = item
                    isExpanded = false
                    focusManager.clearFocus()
                })
            }
        }
    }
}

// TODO: ChatGPT code. Not tried to understand and I think it definitely has some flaws (which are
// not necessarily its fault) but want to play with this a bit anyway and see if it's viable.
// TODO: So, what's wrong with this?
// - on clicking, text box gets the cursor very briefly then loses it - it *may* be that there's
//   something inherent in "having the drop down on screen" which stops the text field having the
//   cursor.
// - after selecting an item from the drop down, the cursor remains active and the drop down disappears
// - the drop down is not aways present when the cursor is active. This is kind of the goal which
//   ties the above together, although it's valuable to note specific ways it can occur if we're
//   trying to fix it.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBoxSample(modifier: Modifier = Modifier) {
    val options = listOf("Apple", "Banana", "Cherry")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Apple") }

    ExposedDropdownMenuBox(
        modifier = modifier, expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            // keyboardOptions = null,
            value = selectedOptionText,
            onValueChange = { selectedOptionText = it },
            label = { Text("Select a fruit") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                // TODO: This onKeyEvent is necessary to allow you to press Enter to open the
                // drop-down. I haven't tested it yet but Grok tells me this *won't* handle
                // a D-pad click and we may need to also recognize Key.DpadCenter to do that.
                // With this tweak, I suspect this implementation of a "no keyboard entry"
                // combo box is the nicest one going - since we are mostly using TextField
                // without active coercion and we have been able to leave it enabled, color
                // changes and keyboard navigation mostly seem to "just work".
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                        expanded = true
                        true // Consume the event
                    } else {
                        false // Let other events pass through
                    }
                }
            //.fillMaxWidth()
        )

        // TODO: The text in this dropdown doesn't left align with the text in the TextField
        ExposedDropdownMenu(
            // TODO: The font in this dropdown appears unnecessarily small - prob fixed
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer), // TODO: perhaps redundant?
            expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(text = {
                    Text(
                        selectionOption,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }, onClick = {
                    selectedOptionText = selectionOption
                    expanded = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComboBox() {
    ComboBox(
        label = "Label3",
        value = "Value3",
        onValueChange = {},
        content = listOf("foo", "bar", "baz")
    )
}

@Preview
@Composable
fun TripleComboBox() {
    Row() {
        ComboBox(
            label = "Category",
            value = "Foo",
            onValueChange = {},
            content = listOf(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        ComboBox(
            label = "Item",
            value = "Bar",
            onValueChange = {},
            content = listOf(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        ComboBox(
            label = "Source",
            value = "Baz",
            onValueChange = {},
            content = listOf(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun TwoRowTripleComboBox() {
    Column() {
        Row() {
            ComboBox(
                label = "Category",
                value = "Foo",
                onValueChange = {},
                content = listOf(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            ComboBox(
                label = "Source",
                value = "Baz",
                onValueChange = {},
                content = listOf(),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row() {
            ComboBox(
                label = "Item",
                value = "Bar",
                onValueChange = {},
                content = listOf(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ChatGPT wrote this for me after much wrangling. I 70% understand what's going on but there is
// definitely some voodoo here. Compose apparently has no real concept of clearing the focus on
// a TextField when we tap elsewhere on the screen. Instead, we need to arrange for this to happen
// ourselves. We try to wrap "elsewhere" in a Box and attach this modifier to it (although I think
// we could apply this modifier to multiple composables if we couldn't cover "elsewhere" in a single
// Box). Because it uses pointerInput() it gets to see pointer-related events *even if* one of the
// children (which get to see events first - they propagate child->parent) has consumed it, which is
// what we want. (It generally drives me nuts that tapping somewhere just to "close" some
// interaction also triggers new interaction if you tap something like a button, but this is
// apparently standard behaviour so we don't try to change it.)
//
// This still doesn't really work properly. If you drag to scroll the screen, that is picked up here
// and focus is cleared, which is apparently wrong. In practice I suspect this is fine for this app.
//
// The "proper" way to do this is apparently to explicitly call clearFocus() in *every* component on
// the screen which consumes clicks and which the user might choose to tap on when the TextField has
// focus. I think we would also need either this modifier or (maybe, and if so then it's probably
// cleaner, since pointerInput feels more wizard-level than onClick) an onClick handler on this
// "elsewhere" component to catch taps on non-interactive components like labels and actual empty
// space, but I am really not sure.
//
// I am thoroughly disgusted with this. I can't find any helpful discussion on the web generally and
// that such an apparently standard UI interaction requires sprinkling clearFocus() calls around
// every interactive component or convoluted hacks like this which bring their own corner cases
// (e.g. scrolling) feels wrong. But as far as I am able to tell, there is no official, clean
// solution to this and no one ever discusses it outside a couple of niche StackExchange questions
// which don't address the problem generally. I haven't even been able to find anyone saying that
// I shouldn't even want this style of interaction (clearing the focus on clicking elsewhere,
// even if that somewhere is itself an active element like a button) and to stop fighting the
// framework.
@Composable
fun Modifier.clearFocusOnTapOutside() = composed {
    val focusManager: FocusManager = LocalFocusManager.current
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.changes.any { it.pressed && !it.previousPressed }) {
                    focusManager.clearFocus()
                }
            }
        }
    }
}


// TODO: TO INVESTIGATE (CHATGPT):
//Option A: Stay Traditional (zero fancy insets)
//✅ Don’t call enableEdgeToEdge()
//✅ Force system to do padding:
//
//WindowCompat.setDecorFitsSystemWindows(window, true)
//❌ Don’t manually add systemBarsPadding() or safeDrawingPadding()
//✅ Let Scaffold and standard layouts work as expected
//✅ Looks modern, but stays “out of the way”


// Utility to get Activity window
@Composable
private fun Context.getActivityWindow(): Window? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context.window
        context = context.baseContext
    }
    return null
}

// TODO: ChatGPT magic plus my hackery to fix bugs
// TODO: Even if this seems OK and I decide to keep/use it, giving it a thorough manual code review alongside https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/ would likely be a good idea.
// TODO: I got Grok to do a code review on this. Frankly it seemed to miss the point somewhat but it made some comments that *might* be relevant, so probably ought to go over its feedback again once I have studied this code and tried to simplify (if possible) the state handling, which may well be over-complex as a result of ChatGPT and/or my incompetence and/or an interaction between the two. (grok-full-screen-dialog-code-review-iffy...)
// TODO: It might of course be worth asking ChatGPT to do a code review, especially if it approaches the code "fresh" (tell it Grok wrote it) it might find problems
// TODO: Perplexity.ai says "9. Focus Management
//
//    Focus Trap:
//    Platform dialogs trap focus within themselves; your implementation does not.
//    Landmine: If your dialog contains text fields or focusable elements, users could tab out of the dialog into the underlying screen. Consider using FocusRequester to ensure initial focus, and possibly a custom focus trap if accessibility is a concern."
// I am not sure if this is a problem, using the cursor keys to move round doesn't seem to obviously be moving off the dialog (but it is very primitive), but pressing the tab key does seem to "lose" focus for a while, which suggests it might be floating behind. May be worth looking into this and/or asking ChatGPT for advice/tweaks on this.
// TODO: o4-mini says: Definitive Bugs / Behavioral Surprises
//– pointerInput( Unit ) {} does not actually consume all touches.
//• You’ve seen that it blocks clicks “often,” but on a lot of devices / Compose versions touches will actually fall through.
//• If you want to fully block taps behind your “dialog,” you need something like:
//
//kotlin
//
//.pointerInput(Unit) {
//  awaitPointerEventScope {
//    while (true) {
//      // we just loop forever, consuming every event
//      awaitPointerEvent()
//    }
//  }
//}
// • Alternatively, a zero‐visual Modifier.clickable(...) with indication = null + interactionSource = remember { MutableInteractionSource() } will also reliably eat taps.
//
//– zIndex might not be high enough in more complex layouts.
//
// • As soon as your dialog appears, you should move focus into it.
//
//• You can call bringIntoViewRequester on a known focusable, or use a FocusRequester on your first input or button inside.
// • You have .semantics { dialog() } which is great. You should also move accessibility focus into your dialog container when it opens, which you can do with LaunchedEffect(visible) { focusManager.moveFocus(FocusDirection.In) }.
//
//– WindowInsets stacking
//
//• Chaining .windowInsetsPadding(WindowInsets.systemBars) and .windowInsetsPadding(WindowInsets.ime) will add navBar/inset space twice in overlapping areas (e.g. bottom).
//• A better pattern is:
//
//kotlin     val combined = WindowInsets.systemBars.union(WindowInsets.ime)     Modifier.windowInsetsPadding(combined)
//
//    Nice-to-Haves / Hardening
//    – BackHandler placement
//    • You register the BackHandler only when isComposed. That’s correct, but if you inverted your AnimatedVisibility logic you could move it inside your AnimatedVisibility block so you don’t have to manage isComposed at all.

// This is a mash-up of some ChatGPT-written code with
// https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/ and some of my own
// tweaking and experience from earlier implementation efforts, plus trying to investigate and
// address points raised by code reviews from all the AIs I could get my hands on. It attempts to be
// an MD3-compliant implementation of a full-screen dialog box, including animation (sliding in
// vertically to distinguish it from a full-fledged screen which slides in horizontally). Note that
// it does *not* use the standard Dialog class - I was ultimately advised not to use this and to try
// to take care of things myself. If I remember correctly, using Dialog caused weird colour changes
// on the status bar and although I could have hacked around that further, I decided to take
// ChatGPT's advice and sample code and get rid of Dialog. It feels like MD3-compliant full-screen
// dialogs on Android are a black art. I would have preferred not to take so much AI advice but I
// really struggled to find any concrete, recent human-written advice - the amount of blogs turning
// up in web searches which are probably just AI slop doesn't help, of course. I am concerned there
// will be a lingering landmine here ("this crashes if you have an Android 10 device with a hardware
// keyboard attached and use D-pad navigation during the exit animation") but I can only hope most
// cases are covered. The AI code reviews certainly raised some things (like focus trapping in a
// dialog) that I might have missed otherwise.
@Composable
fun AnimatedFullScreenDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enterDurationMillis: Int = 300,
    exitDurationMillis: Int = 250, // was 300,
    content: @Composable () -> Unit
) {
    // The parent holds the source of truth for whether the dialog is visible or not across things
    // like rotations. We just need to be aware that we may be visible on our first composition in
    // that case, so we must initialise visibleState using visible and not hard-code it to false.
    // (We don't want the dialog to animate in if it is visible on first composition.)
    var visibleState = remember { MutableTransitionState(visible) }
    visibleState.targetState = visible

    // The actual content needs to be present in the compose tree during enter and exit animations
    // as well as (obviously) when it is supposed to be visible. The only time we don't need it is
    // if we are idling in the non-visible state.
    if (visibleState.currentState || visibleState.targetState) {
        Dialog(
            onDismissRequest = onDismiss, properties = DialogProperties(
                usePlatformDefaultWidth = false, // Makes dialog full-width
                decorFitsSystemWindows = false, // TODO: to make imepadding work!?
                /* TODO!? Probably not needed now
                dismissOnBackPress = true, // Handles back button
                dismissOnClickOutside = true // Allows dismissal by clicking outside
                */
            )
        ) {
// Get the view to manage insets
            val view = LocalView.current
            LaunchedEffect(view) {
                // Get the Activity and its Window
                val activity = view.context as? android.app.Activity
                activity?.window?.let { window ->
                    // Ensure content is drawn behind system bars
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                        // Consume system insets to prevent automatic adjustments
                        WindowInsetsCompat.Builder(insets).setInsets(
                                WindowInsetsCompat.Type.systemBars(),
                                Insets.NONE
                            ) // Use androidx.core.graphics.Insets
                            .build()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(WindowInsets.ime)
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                // Get the window to control system bars
                val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
                val activityWindow = LocalView.current.context.getActivityWindow()

                // Without the code in this SideEffect block, the Dialog appears to effectively invert
                // the status bar colours. This is readable but looks ugly for our full screen dialog.
                SideEffect {
                    dialogWindow?.let { window ->
                        // Disable the dialog scrim; we don't want this for our full screen dialog,
                        // especially since we are sliding it in from the bottom and having the screen
                        // go dim first looks ugly.
                        window.setDimAmount(0f)

                        // Absolute Grok (I think?) voodoo which avoids the status bar (at the top of
                        // the screen) going white-on-white as a result of the previous line disabling
                        // the scrim.
                        activityWindow?.let { actWindow ->
                            // Copy activity window flags for consistent behavior
                            window.setFlags(
                                actWindow.attributes.flags, actWindow.attributes.flags
                            )
                            // Use WindowCompat for system bar transparency and cutout support
                            WindowCompat.setDecorFitsSystemWindows(window, false)
                            // Match system bar appearance (light/dark icons) to activity
                            val controller =
                                WindowCompat.getInsetsController(window, window.decorView)
                            val activityController =
                                WindowCompat.getInsetsController(actWindow, actWindow.decorView)
                            controller.isAppearanceLightStatusBars =
                                activityController.isAppearanceLightStatusBars
                            controller.isAppearanceLightNavigationBars =
                                activityController.isAppearanceLightNavigationBars
                        }
                    }
                }

                // Animate visibility for dialog content sliding vertically
                AnimatedVisibility(
                    visibleState, enter = slideInVertically(
                    animationSpec = tween(
                    durationMillis = enterDurationMillis, easing = LinearOutSlowInEasing
                ), initialOffsetY = { fullHeight -> fullHeight } // Slide from bottom
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = enterDurationMillis, easing = LinearOutSlowInEasing
                    )
                ), exit = slideOutVertically(
                        animationSpec = tween(
                    durationMillis = exitDurationMillis, easing = FastOutLinearInEasing
                ), targetOffsetY = { fullHeight -> fullHeight } // Exit to bottom
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = exitDurationMillis, easing = FastOutLinearInEasing
                    )
                ),

                    modifier = modifier.fillMaxSize()/* TODO!? These were probably added when we were trying to emulate Dialog and we may well not need them now we *are* using Dialog, but I'll keep them around just in case for a bit
                        // Handle system bars & keyboard insets
                        // .consumeWindowInsets(WindowInsets.systemBars) - probably don't need this, but it will not prevent insets from propagating, whether that is a bad thing or not is utterly beyond me of course - but hey, full screen dialogs are advanced ninja-level magic
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .windowInsetsPadding(WindowInsets.ime)
                        */) {
                    // The actual dialog content container (can be Scaffold, etc)
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(vm: PriceTrackerViewModel, navController: NavHostController) {

    var menuExpanded by remember { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    // TODONOW: I THINK IT MIGHT BE THESE NEXT TWO LINES AND THEIR MASSIVE HACK WHICH CAUSE PROBLEMS WHEN WE ARE RUN AND HAVE TO CREATE THE DB AS WE GO
    var selectedDataSetId: Long by remember { mutableStateOf(1) } // TODO: massive hack defaulting to hardcoded, need to cope with null in some way probably

    var selectedProductId: Long by rememberSaveable { mutableStateOf(1) } // TODO: massive hack defaulting to hardcoded, we need a genuine ID from somewhere and/or support for null


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
            TopAppBar(title = { Text("My App Name Here") }, actions = {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    // TODO: FONTS AND PROB COLORS ON THIS LIST ARE PROB WRONG
                    DropdownMenuItem(text = { Text("Edit product list") }, onClick = {
                        menuExpanded = false
                        // Handle navigation or action
                    })
                    DropdownMenuItem(text = { Text("Edit categories") }, onClick = {
                        menuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Settings") }, onClick = {
                        menuExpanded = false
                        navController.navigate("settings")
                    })
                }
            },
            modifier= Modifier.background(MaterialTheme.colorScheme.surface /* TODO? */)
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
                    vm = vm,
                    selectedDataSetId = selectedDataSetId,
                    onSelectedDataSetIdChange = { selectedDataSetId = it },
                    selectedProductId = selectedProductId,
                    onSelectedProductIdChange = { selectedProductId = it }) // TODO: rename this

                /*
            // TODO TEMP HACK FOR KEYBOARD/SCROLLING EXPERIMENTS
            Spacer(modifier = Modifier.height(300.dp)) // TODO TEMP HACK
            var packSize by remember { mutableStateOf("123") }
            TextField(
                label = { Text("Pack size") },
                value = packSize,
                onValueChange = { packSize = it },
                // TODO: keyboardOptions here hints to on-screen keyboard, we probably also ought to prohibit non-numbers or (regional) decimal separator and *maybe* prohibit multiple decimal separators (but maybe this should just be an error report not prohibited, what's normal?)
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    })
                    */

                androidx.compose.foundation.layout.Spacer(
                    modifier = androidx.compose.ui.Modifier.height(
                        8.dp
                    )
                )

            Log.d("Myapp", "BeforeItemSourceInfo0")
            val dataSetListNullable by vm.getDataSet(selectedDataSetId).collectAsStateWithLifecycle(initialValue = null)
            Log.d("Myapp", "BeforeItemSourceInfo0a")
            // TODO: HACK - we should probably be pulling this kind of mandatory non-null data out in one place, showing "Loading
            // TODO: Note that we must check size > 0 here to cope with the case where we don't have any data sets (e.g. on
            // first install when the database is being populated by a txn which may not have finished yet). In general there is
            // an awful lot of hackery in this area (because I was learning as I wrote the code) and we need to be better about
            // this sort of thing.
            if (dataSetListNullable != null && dataSetListNullable!!.size > 0) {
                Log.d("Myapp", "BeforeItemSourceInfo0b")
                val dataSetList = dataSetListNullable!!
                Log.d("Myapp", "BeforeItemSourceInfo0c")
                devCheck(dataSetList.size == 1) { "Expected one data set with ID $selectedDataSetId but got ${dataSetList.size}" }

                Log.d("Myapp", "BeforeItemSourceInfo0d")

                Log.d("Myapp", "BeforeItemSourceInfo")
                    ItemSourceInfo( // TODO: COMMENTING OUT THIS FIXES THE CRASH ON FIRS RUN AFTER DELETE AND REINSTALL
                        vm = vm,
                        navController = navController,
                        dataSet = dataSetList[0],
                        // selectedDataSetId = selectedDataSetId,
                        selectedProductId = selectedProductId
                    )
                Log.d("Myapp", "AfterItemSourceInfo")

                }
            Log.d("Myapp", "AfterItemSourceInfo1")

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

    /* TODO
    // MD3 spec sort of says that on Android we should be using an "expand" transition to
    // show this dialog, but after much discussion with an AI (because I can't find any
    // other source of advice), it might be better to slide in vertically from the bottom
    // and then out the same way. We do this vertically not horizontally because it is a
    // full screen dialog not a screen. TODO: I half wonder if I should try the expand.
    // TODO: No idea what proper MD3 animations should be here, just trying to get this to work at all for now
    // TODO: As the dialog will probably contain its own scaffold and topappbar, this animatedvisibility component should probably be outside our scaffold
    // TODO: If this two-bool approach works, maybe switch to a three-state enum type thing
    // TODO: https://github.com/JetBrains/compose-multiplatform/issues/4431
    // https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/ is linked to from issue 4431, suggesting it's "reputable"
    AnimatedFullScreenDialog(visible = showEditDialog, onDismiss = { showEditDialog = false }) {
                        OuterFullScreenDialog()
                    } */
}

// https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/
@ReadOnlyComposable
@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window


@Composable
// TODO: https://m3.material.io/components/dialogs/specs says (near bottom) top/left/right padding on a full screen dialog should be 24.dp - I am probably not doing that, should I? Should I use similar padding on "non-dialog full screens" to match??
fun OuterFullScreenDialog(vm: PriceTrackerViewModel, navController: NavHostController, dataSetId: Long, productId: Long, storeId: Long) {
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
    val nullableDataSetList: List<DataSet>? by vm.getDataSet(dataSetId).collectAsStateWithLifecycle(initialValue = null)

    if (nullablePriceList == null || nullableDataSetList == null || !(productId in productMap)) {
        // This will almost certainly never be seen - we will likely get the query results back and
        // be recomposed before the first frame.
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
        var showSaveProgressIndicator by rememberSaveable { mutableStateOf( false ) }
        var showConfirmDialog by rememberSaveable { mutableStateOf(false) }
        var showErrorDialog by rememberSaveable { mutableStateOf(false) }
        var showSavingSnackbar by rememberSaveable { mutableStateOf( false) }
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
                val units: List<MeasureUnit> = getRelevantMeasureUnits(dataSet, product.quantityType, includeDisplayOnly=false)
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
                .penaltyDeath() // TODO .penaltyLog()  // logs violations; you can also add .penaltyDeath() to crash on violation
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
            }/* TODO
            // 1) Set up NavController
            val navController = rememberNavController()
            // 2) Observe the current entry; this is a State<NavBackStackEntry?>.
            //    Any time you navigate, Compose will recompose here.
            // TODO: This back stack stuff works *BUT* it is probably wrong, because in order to get
            // decent animation appearance I probably need TopAppBar to be part of each individual Screen composable,
            // *not* a single shared TopAppBar which is context-sensitive.
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStackEntry?.destination?.route
            val showBackButton = currentDestination != "todorenameme"
            val title = when (currentDestination) {
                "todorenameme" -> "My App Name Here"
                "settings" -> "Settings"
                else -> "MISSING TITLE"
            }


            var menuExpanded by remember { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current
            //val navBackStackEntry by navController.currentBackStackEntryAsState()
            //val canNavigateBack = navBackStackEntry?.previousBackStackEntry != null
            Box(Modifier.safeDrawingPadding()) {
                ComposeTutorialTheme(/* darkTheme = isDarkTheme */) {
                    Scaffold(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                        // TODO: Probably need to apply MD colors to topBar, just poss also font size but it may default to something sensible
                        topBar = {
                            TopAppBar(title = { Text(title) }, navigationIcon = {
                                // TODO: This is absolute 4o-mini voodoo, but it does seem to work,
                                // unlike just about every other solution I found on the web or which
                                // an AI gave me. Note that we also probably actually need to change the title according to the activity. I do wonder if I'm doing something massively wrong. One random code sample I saw on the web actually changed the TopAppBar each time.
                                if (showBackButton) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            }, actions = {
                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                                }

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }) {
                                    // TODO: FONTS AND PROB COLORS ON THIS LIST ARE PROB WRONG
                                    DropdownMenuItem(
                                        text = { Text("Edit product list") },
                                        onClick = {
                                            menuExpanded = false
                                            // Handle navigation or action
                                        })
                                    DropdownMenuItem(
                                        text = { Text("Edit categories") },
                                        onClick = {
                                            menuExpanded = false
                                        })
                                    DropdownMenuItem(text = { Text("Settings") }, onClick = {
                                        menuExpanded = false
                                        navController.navigate("settings")
                                    })
                                }

                            })
                        }) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "todorenameme",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("todorenameme") { TodoRenameMe(navController) }
                            composable(
                                "settings",
                                enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) },
                                exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) }) { SettingsScreen(navController) }
                        }
                    }
                }
            }*/
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
            /*
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            } */
        ) {
            HomeScreen(vm, navController)
        }
        val tweenDurationMillisEnter = 700; // TODO: should probably be 300 in final version
        val tweenDurationMillisExit = 700; // TODO: should probably be 250 in final version
        composable(
            "settings", enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,

                    animationSpec = tween(
                        durationMillis = tweenDurationMillisEnter,
                        easing = LinearOutSlowInEasing
                    ),
                )

            },/* TODO This is probably not used as this is a "leaf" screen
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(2000)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(2000)
                )
            },
            */
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
        // TODO: This needs the correct vertical transitions, but let's not fuss with that for now
        composable("fullScreenDialog/{dataSetId}/{productId}/{storeId}/{randomUUID}") { backStackEntry ->
            val dataSetId = backStackEntry.arguments?.getString("dataSetId")?.toLong() ?: 0
            val productId = backStackEntry.arguments?.getString("productId")?.toLong() ?: 0
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLong() ?: 0
            // TODO: DELETE - NOT NEEDED val randomUUID = backStackEntry.arguments?.getString("randomUUID")
            OuterFullScreenDialog(vm, navController, dataSetId, productId, storeId)
        }
    }
}

// TODO: ChatGPT-inspired (and maybe do my own searches too) libraries that may solve the ComboBox issue:
// https://github.com/Breens-Mbaka/Searchable-Dropdown-Menu-Jetpack-Compose
// https://composablehorizons.github.io/ComposeTheme/
// https://github.com/szeweq/desktopose combo-box (last commit three years ago though, but maybe it's perfect...)

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
inline fun devRequire(condition: Boolean, lazyMessage: () -> String) = devCheck(condition, lazyMessage)
