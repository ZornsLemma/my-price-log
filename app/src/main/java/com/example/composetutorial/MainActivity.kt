@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.composetutorial // TODO: change this!

import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.Alignment
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.combine
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.time.Duration
//import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import android.app.Application
import android.content.Context
import android.icu.text.Collator
import androidx.activity.compose.BackHandler
import androidx.navigation.compose.rememberNavController
import android.os.Bundle
import android.os.LocaleList
import android.os.Parcelable
import android.os.StrictMode
import android.text.format.DateUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.unit.dp
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
//import androidx.compose.ui.semantics.SemanticsProperties.Role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.createSavedStateHandle
import androidx.navigation.NavBackStackEntry
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withTimeoutOrNull
import java.text.DecimalFormatSymbols
import java.util.concurrent.Executors
import kotlin.math.ceil
import kotlin.math.pow

// Enum class to represent whether something is sold by "count of items" ($4 for 6 bananas),
// weight or volume. This is fundamental as we make no effort to convert between them using some
// sort of density estimate or whatever. Actual units (kg, oz, etc) of the same quantity type can
// be varied much more freely.
// TODO: Just possibly rename this "MeasureType"? ChatGPT suggestion, maybe has a point,
// "QuantityType" is definitely not a terrible name though.
enum class QuantityType(val value: Int) {
    ITEM(1),
    WEIGHT(2), // technically mass but everyone says "price per weight"
    VOLUME(3);

    /* TODO: DELETE?
    companion object {
        fun fromValue(value: Int): QuantityType? {
            return entries.find { it.value == value }
        }
    }
    */
}

// TODO: Could/should we get rid of the ITEM unit family and just make MeasureUnit.ITEM a member of
// each of the three other families? This might well be a bad idea in terms of allowing the user to
// have both metric and one of the other families enabled, but at least have a quick think.
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
    val maxDecimals: Int,
    val toBase: Double,
    val displayOnly: Boolean
) {
    // Weight
    G(101, setOf(UnitFamily.METRIC), QuantityType.WEIGHT, "g", 0, 1.0, false),
    G100(
        1001,
        setOf(UnitFamily.METRIC),
        QuantityType.WEIGHT,
        "100 g",
        2,
        100.0,
        true
    ), // TODO: experimental
    KG(102, setOf(UnitFamily.METRIC), QuantityType.WEIGHT, "kg", 3, 1000.0, false),
    OZ(
        103,
        setOf(UnitFamily.IMPERIAL, UnitFamily.US_CUSTOMARY),
        QuantityType.WEIGHT,
        "oz",
        3, // allow for eighths
        28.3495,
        false
    ),
    LB(
        104,
        setOf(UnitFamily.IMPERIAL, UnitFamily.US_CUSTOMARY),
        QuantityType.WEIGHT,
        "lb",
        3, // allow for eighths
        453.592,
        false
    ),

    // Volume
    ML(201, setOf(UnitFamily.METRIC), QuantityType.VOLUME, "ml", 0, 1.0, false),
    ML100(
        2001,
        setOf(UnitFamily.METRIC),
        QuantityType.VOLUME,
        "100 ml",
        2,
        100.0,
        true
    ), // TODO: experimental
    L(202, setOf(UnitFamily.METRIC), QuantityType.VOLUME, "l", 3, 1000.0, false),

    // TODO: Arguably imperial should come first here to match order in UnitFamily
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
        3, // allow for eighths
        29.5735,
        false
    ),
    US_CUSTOMARY_PINT(
        2033,
        setOf(UnitFamily.US_CUSTOMARY),
        QuantityType.VOLUME,
        "pt",
        3, // allow for eighths
        473.176473,
        false
    ),
    US_CUSTOMARY_GAL(
        204,
        setOf(UnitFamily.US_CUSTOMARY),
        QuantityType.VOLUME,
        "gal",
        3, // allow for eighths
        3785.41,
        false
    ),
    IMPERIAL_FLOZ(
        2041,
        setOf(UnitFamily.IMPERIAL),
        QuantityType.VOLUME,
        "flIoz",
        3, // allow for eighths
        28.4130625,
        false
    ),
    IMPERIAL_PINT(
        205,
        setOf(UnitFamily.IMPERIAL),
        QuantityType.VOLUME,
        "pt",
        3, // allow for eighths
        568.26125,
        false
    ),
    IMPERIAL_GAL(
        206,
        setOf(UnitFamily.IMPERIAL),
        QuantityType.VOLUME,
        "gal",
        3, // allow for eighths
        4546.09,
        false
    ),

    // Countable items
    // TODO: Arguably these should come first to match order in QuantityType
    // TODO: Should symbol be empty string or something else here? feeling my way. I suspect "" looks best, it may lead to strings like "for 20 " with a trailing space but that's probably not a big deal in practice. (We could also just make a point of trimming strings generated using symbol.) We sort of might want "1" for the unit price denominator stuff though.
    EACH(
        301,
        setOf(UnitFamily.ITEM),
        QuantityType.ITEM,
        "",
        0,
        1.0,
        false
    ), // TODO: RENAME "EACH" TO "ITEM"?
    EACH10(302, setOf(UnitFamily.ITEM), QuantityType.ITEM, "10", 1, 10.0, true),
    EACH100(303, setOf(UnitFamily.ITEM), QuantityType.ITEM, "100", 2, 100.0, true);

    companion object {
        private val measureUnitById = entries.associateBy { it.id }

        // TODO: Rename "fromId"?
        fun fromValue(measureUnitId: Long): MeasureUnit? = measureUnitById[measureUnitId]
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
    devCheck(!(dataSet.allowImperial && dataSet.allowUSCustomary)) { "Data set ID ${dataSet.id} has both imperial and US customary unit families enabled" }
    return relevantUnitFamilies
}

// TODO: The results from this will probably be shown to the user so order matters. We should maybe
// sort them and/or rely on MeasureUnit.entities having some order. We may want some way for the
// caller to indicate that if there are multiple unit families in the results, they prefer a
// particularly family (e.g. the one the user last used to enter a price) at the top. There may be
// an argument that consistent ordering of families if desirable rather than it varying too much,
// although some users might prefer e.g. metric first and others imperial/US customary first. Within
// a unit family, we should probably order by smallest to largest (which we can do by relying on
// MeasureUnit.entities being in that order, or by sorting on base - probably nicer just to go with
// the baked-in order for now.
fun getRelevantMeasureUnits(
    dataSet: DataSet,
    quantityType: QuantityType,
    includeDisplayOnly: Boolean
): List<MeasureUnit> {
    val relevantUnitFamilies = getRelevantUnitFamilies(dataSet)
    val relevantMeasureUnits = MeasureUnit.entries.filter { measureUnit ->
        measureUnit.quantityType == quantityType &&
                measureUnit.unitFamilies.any { it in relevantUnitFamilies } &&
                (!measureUnit.displayOnly || includeDisplayOnly)
    }
    devCheck(relevantMeasureUnits.isNotEmpty()) {
        "Expected at least one relevant measure unit for QuantityType ${quantityType.name} in " +
                "the context of data set ID ${dataSet.id} but found none"
    }
    return relevantMeasureUnits
}

// Note that this regards measureUnit as its own sibling.
// TODO: This is *probably* only used internally to generate some units which we pick among automatically and we don't care about the order of the results.
fun getSiblingMeasureUnits(
    dataSet: DataSet,
    measureUnit: MeasureUnit,
    includeDisplayOnly: Boolean
): List<MeasureUnit> {
    val unitFamily = measureUnit.unitFamilies.intersect(getRelevantUnitFamilies(dataSet))
    devCheck(unitFamily.size == 1) { "Expected MeasureUnit ID ${measureUnit.id} to be a member of exactly one unit family in the context of data set ID ${dataSet.id} but got ${unitFamily.size}" }
    val siblingMeasureUnits = MeasureUnit.entries.filter {
        it.quantityType == measureUnit.quantityType &&
                unitFamily.single() in it.unitFamilies &&
                (!it.displayOnly || includeDisplayOnly)
    }
    devCheck(siblingMeasureUnits.isNotEmpty()) {
        "Expected at least one sibling measure unit for MeasureUnit ${measureUnit.id} in the " +
                "context of data set ID ${dataSet.id} but found none"
    }
    // TODO: We could verify that measureUnit is a member of the returned list, but it feels a bad
    // idea to do a linear search just for a check.
    return siblingMeasureUnits
}

// The arguments are mandatory here so we don't fail to think about what's correct when we call
// this. For miscellaneous debug output we can just use string interpolation of course.
fun formatDouble(
    value: Double,
    minDecimals: Int,
    maxDecimals: Int,
    useLocaleGrouping: Boolean,
    locale: Locale // = Locale.getDefault() // TODO: GET RID OF DEFAULT HERE?
): String {
    val numberFormat = NumberFormat.getNumberInstance(locale)
    numberFormat.minimumFractionDigits = minDecimals
    numberFormat.maximumFractionDigits = maxDecimals
    if (!useLocaleGrouping) {
        numberFormat.isGroupingUsed = false
    }
    return numberFormat.format(value)
}

@Parcelize // TODO: can we get rid of this later?
// TODO: Should we make "value" memeber private? Direct use could "encourage" buggy code.
data class MeasuredValue(val value: Double, val unit: MeasureUnit) : Parcelable {
    // TODO: We could make quantityType public and slightly simplify some of our callers, but it's
    // *probably* clearer to make them go through unit to get to it.
    private val quantityType: QuantityType get() = unit.quantityType

    fun to(unit: MeasureUnit): MeasuredValue {
        devRequire(this.quantityType == unit.quantityType) {
            "Cannot convert between different quantity types: trying to convert $this to $unit"
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

    // Based on my own experience and a possibly-trustworthy discussion with ChatGPT for an
    // international angle, I suspect that in practice we don't want grouping separators in our
    // measures even when they're for display only - "2272 ml" feels better than "2,272 ml", at
    // least to me.
    fun toDisplayString(locale: Locale): String =
        "${
            formatDouble(
                value,
                minDecimals = 0,
                maxDecimals = unit.maxDecimals,
                useLocaleGrouping = false,
                locale
            )
        } ${unit.symbol}"
}

@Database(
    entities = [DataSet::class, Item::class, Source::class, PriceEntity::class],
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
                    // TODO: Disable query logging in final version of course
                    .setQueryCallback({ sqlQuery, bindArgs ->
                        Log.d("MyApp", "SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, Executors.newSingleThreadExecutor())
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch { populateDemoData(context) }
                        }
                    })
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
/* TODO: I had a chat with ChatGPT and I can probably arrange to start each table's ID counter at a different value with something like:

    val roomCallback = object : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Seed the auto-increment for Foo table to start at 999 (next will be 1000)
        db.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('Foo', 999)")

        // For Bar table, start at 1999 (next will be 2000)
        db.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('Bar', 1999)")
    }
}

This adds "free" debuggability by making it more obvious if an ID is misused or is reported in an error with no context on which table it's from. Gut feeling is I should allocate say 1000 IDs to each of the basic static data things and start prices (which will be way the biggest table) at say 10000.

*/

suspend fun populateDemoData(context: Context) {
    val db = InventoryDatabase.getDatabase(context)
    // TODO: I may want to add multiple demo data sets - if so, given them all names of the form "Demo (foo)", probably. I may at the very least want to do an imperial unit demo set, so new potential users don't assume the app is metric only. This might be overkill but it may not hurt. We could just use imperial with the metric-ish data set (i.e. just configure the display units to be the user's current regional ones by default when we set the database up), and that might well be reasonable - it would give "odd" pack sizes (e.g. nominally imperial demo data selling 2 litre cartons of milk which the shops call a 3.52 pint pack) but for demo purposes it is probably fine.
    // TODO: We should have some cases in the demo data set where there is no price for a store+product combination
    db.withTransaction {
        // TODO: It's probably smart to default the demo data to the local currency, since that will look most natural to our new user, but do rethink this afterwards. (It's also just possible, remember, that they will start editing the demo dataset for their own use, rather than starting again with a fresh dataset.)
        // TODO: Just experimentally, make sure to set the demo data up with a non-local currency and see that the app works!
        // TODO: We should probably pick one of IMPERIAL or US_CUSTOMARY here based on the current locale (and make sure any non-metric units in the data below are changed accordingly)
        // TODO: We should have some demo products which are (fake) "branded" products, so get the idea across that this is another way to do things if you are brand-sensitive on a particular item
        // TODO: I should probably have a demo set using a currency like JPY which doesn't have 2dp - or perhaps better, have something I can turn on for debug builds which will do that, but don't pollute the user initial database with it
        // TODO: We should maybe - perhaps not worth worrying about - avoid using the demo data designed for 2dp currencies with e.g. JPY, if only by forcing the currency to be something else even if that's the system default, or perhaps applying a multiplier of 10^(2-currencydps) to all the prices just so they are "readable"
        val dataSetId = db.dataSetDao().insert(
            DataSet(
                name = "Groceries (demo)",
                currencyCode = "EUR", // TODO TEMP HACK Currency.getInstance(Locale.getDefault()).currencyCode,
                allowMetric = true,
                allowImperial = true,
                allowUSCustomary = false,
                notes = "A sample collection of unrealistic grocery prices for imaginary stores. This is intended to give you something to play with when you first install the app.",
            )
        )
        val dataSetId2 = db.dataSetDao().insert(
            DataSet(
                name = "Demo 2",
                currencyCode = "AUD",
                allowMetric = true,
                allowImperial = false,
                allowUSCustomary = true,
                notes = "",
            )
        ) // TODO TEMP HACK
        db.dataSetDao().insert(
            DataSet(
                name = "Demo 3",
                currencyCode = "AUD",
                allowMetric = true,
                allowImperial = false,
                allowUSCustomary = true,
                notes = "",
            )
        ) // TODO TEMP HACK
        db.productDao().insert(
            Item(
                dataSetId = dataSetId2,
                name = "Demo 2 Item",
                defaultUnit = MeasureUnit.G,
                notes = "",
            )
        )
        val itemIdGroundCoffee = db.productDao().insert(
            Item(
                dataSetId = dataSetId,
                name = "Coffee (ground)",
                defaultUnit = MeasureUnit.G,
                        notes = ""
            )
        )
        val itemIdWholeMilk = db.productDao().insert(
            Item(
                dataSetId = dataSetId,
                name = "Milk (whole)",
                defaultUnit = MeasureUnit.L,
                notes = "",
                )
        )
        val itemIdTeabags = db.productDao().insert(
            Item(
                dataSetId = dataSetId,
                name = "Teabags",
                defaultUnit = MeasureUnit.EACH,
                        notes = "",

                )
        )
        // TODO: Do some web searches and confirm these are not real supermarket names
        val sourceIdValueMart = db.sourceDao()
            .insert(
                Source(
                    dataSetId = dataSetId,
                    name = "ValueMart",
                    loyaltyDiscountType = LoyaltyDiscountType.NONE,
                    loyaltyMultiplier = 1.0,
                    notes = ""
                )
            )
        val sourceIdSuperiorStore = db.sourceDao().insert(
            Source(
                dataSetId = dataSetId,
                name = "SuperiorStore",
                loyaltyDiscountType = LoyaltyDiscountType.NONE,
                loyaltyMultiplier = 1.0,
                notes = ""
            )
        )
        // Newco deliberately has no prices to start with.
        db.sourceDao().insert(
            Source(
                dataSetId = dataSetId,
                name = "Newco",
                loyaltyDiscountType = LoyaltyDiscountType.NONE,
                loyaltyMultiplier = 1.0,
                notes = "Only just opened but I hope their prices will be good."
            )
        )
        val now = Instant.now()
        db.priceDao().insert(
            PriceEntity(
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
            PriceEntity(
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
            PriceEntity(
                dataSetId = dataSetId,
                itemId = itemIdWholeMilk,
                sourceId = sourceIdValueMart,
                price = 1.99,
                measure = MeasuredValue(
                    4.0,
                    MeasureUnit.IMPERIAL_PINT
                ).asValue(MeasureUnit.ML),
                originalUnit = MeasureUnit.IMPERIAL_PINT,
                confirmed = now,
                details = ""
            )
        )
        db.priceDao().insert(
            PriceEntity(
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
            PriceEntity(
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
            PriceEntity(
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
        // Set some defaults for the first run so the user isn't left with a screen with no data
        // wondering what to do. We leave the source blank because that's a state you can
        // deliberately invoke yourself via the "None" option on the store dropdown. TODO: It might
        // be better to set it to one of the stores with data for first impression purposes though -
        // we want to show off our price data to make it clearer what the app is all about.
        context.dataStore.edit { prefs ->
            prefs[SELECTED_DATA_SET_ID_KEY] = dataSetId
            prefs[SELECTED_ITEM_ID_KEY] = itemIdGroundCoffee
        }

    }
}

// TODO: This interface is here to help with mocking the database during testing. I may want to do
// this, so let's keep it around for now.
interface PriceTrackerRepository {
    fun getAllDataSets(): Flow<List<DataSet>>
    fun getAllItems(dataSetId: Long): Flow<List<Item>>
    fun getAllSources(dataSetId: Long): Flow<List<Source>>

    fun getPricesForItem(dataSetId: Long, itemId: Long): Flow<List<Price>>

    fun countPricesForItem(itemId: Long): Flow<Long>
    fun countPricesForSource(sourceId: Long): Flow<Long>

    suspend fun updateOrInsertDataSet(dataSet: DataSet)
    suspend fun updateOrInsertItem(item: Item)
    suspend fun updateOrInsertSource(source: Source)
    suspend fun updateOrInsertPrice(price: Price)

    // TODO: Should these really return Long just to be super paranoid/vaguely consistent with use of Long for IDs (if IDs "don't fit" in 32 bits, neither do deletion counts)
    suspend fun deleteDataSetById(dataSetId: Long): Int
    suspend fun deleteItemById(itemId: Long): Int
    suspend fun deleteSourceById(sourceId: Long): Int
}

class PriceTrackerRepositoryImpl(
    private val dataSetDao: DataSetDao,
    private val itemDao: ItemDao,
    private val sourceDao: SourceDao,
    private val priceDao: PriceDao
) : PriceTrackerRepository {
    override fun getAllDataSets(): Flow<List<DataSet>> = dataSetDao.getAllDataSets()

    override fun getAllItems(dataSetId: Long): Flow<List<Item>> = itemDao.getAllItems(dataSetId)

    override fun getAllSources(dataSetId: Long): Flow<List<Source>> =
        sourceDao.getAllSources(dataSetId)

    override fun getPricesForItem(dataSetId: Long, itemId: Long): Flow<List<Price>> =
        priceDao.getPriceWithItemEntityForItem(dataSetId = dataSetId, itemId = itemId)
            .map { list -> list.map { it.toDomain() } }

    override fun countPricesForItem(itemId: Long): Flow<Long> =
        priceDao.countPricesForItem(itemId)

    override fun countPricesForSource(sourceId: Long): Flow<Long> =
        priceDao.countPricesForSource(sourceId)

    override suspend fun updateOrInsertDataSet(dataSet: DataSet) {
        dataSetDao.upsert(dataSet)
    }

    override suspend fun updateOrInsertItem(item: Item) {
        itemDao.upsert(item)
    }

    override suspend fun updateOrInsertSource(source: Source) {
        // throw IOException("Simulated database failure") // TODO TEMP
        sourceDao.upsert(source)
    }

    override suspend fun deleteDataSetById(dataSetId: Long): Int = dataSetDao.deleteById(dataSetId)

    override suspend fun deleteItemById(itemId: Long): Int = itemDao.deleteById(itemId)

    override suspend fun deleteSourceById(sourceId: Long): Int = sourceDao.deleteById(sourceId)

    // TODO: Tempish note (maybe make permanent) - I discussed with ChatGPT and it seemed to make
    // sense - the repository should take "validated domain level" entities (where we aren't just
    // reusing the database entities throughout all levels for simplicity - which we aren't with
    // Price). So this should take a *Price* and convert it to a PriceEntity for writing, and there
    // shouldn't be any user-error-catching validation here - this might go wrong, but it would be
    // down to hardware failures or bugs in my code. The viewmodel-ish layer code is responsible
    // for turning an EditablePrice (a special variant domain level thing with nullness etc) into
    // a Price and *that* is where final validation occurs.
    override suspend fun updateOrInsertPrice(price: Price) {
        priceDao.upsert(price.toEntity())
    }
}

// AppViewModelProvider.Factory allows us to control the arguments passed to our ViewModel
// constructors when viewModel() is called.
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
            HomeViewModel(app.priceTrackerRepository, app)
        }
        /* TODO DELETE LATER, KEEPING AROUND FOR REF FOR OTHER VIEWS IF NEC FOR NOW
        initializer {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
            val savedStateHandle = createSavedStateHandle()
            EditPriceViewModel(app.priceTrackerRepository, savedStateHandle)
        }
        */
    }
}

// TODO: Lots of AI voodoo here, probably worth reading up later on how MyApplication should behave
// and what it maybe ought to be doing.
class MyApplication : Application() {
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

class Converters {
    /* TODO: DELETE? I don't believe we have any QuantityType fields in the database any more. Maybe
       wait until we're more sure there won't be any in the future before deleting these.
    @TypeConverter
    fun fromQuantityType(quantityType: QuantityType?): Int? {
        return quantityType?.value
    }

    @TypeConverter
    fun toQuantityType(value: Int?): QuantityType? {
        return value?.let { QuantityType.fromValue(it) }
    }
    */

    @TypeConverter
    fun fromMeasureUnit(measureUnit: MeasureUnit?): Long? {
        return measureUnit?.id
    }

    @TypeConverter
    fun toMeasureUnit(value: Long?): MeasureUnit? {
        return value?.let { MeasureUnit.fromValue(it) }
    }

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? {
        return instant?.toEpochMilli()
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun fromLoyaltyDiscountType(loyaltyDiscountType: LoyaltyDiscountType?): Long? {
        return loyaltyDiscountType?.id
    }

    @TypeConverter
    fun toLoyaltyDiscountType(value: Long?): LoyaltyDiscountType? {
        return value?.let { LoyaltyDiscountType.fromValue(it) }
    }
}

// TODO: General naming note for databases - both Perplexity and ChatGPT agreed that "_id" suffix on
// column names implies a foreign key - so even if (just as an example - but I need to consider this
// on all tables) we might *later* have a unit table but for now our units are just represented by
// hard-coded in application IDs, columns which store a unit should be called "unit" not "unit_id".
// I am not 100% sure I agree but I do need to at least consider naming for consistency at some
// point, and I wanted to note this opinion.

// TODO: I need to make sure I have the right indexes on all these tables, not sure what if any
// might get auto-created (and I may want to inhibit some auto-creation if there is any)

@Entity(tableName = "data_set")
@Parcelize
data class DataSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "currency_code") val currencyCode: String,
    // TODO: For now, I think I will ask the system to format currencies using the currency_code.
    // Later on we may want to give DataSet a flag "use system formatting" and some parameters
    // (currency prefix/suffix/decimal places) which the user can specify to override the system
    // formatting. I think it may be that e.g. the system formatting of USD when in a GBP locale may
    // be a bit annoying ("US$ 123.00" instead of "$123.00" perhaps - not tested though) so this
    // extension is not necessarily ridiculous, but let's keep it simple for now. Having the option
    // to use system formatting is good, and that will probably always be the default.
    @ColumnInfo(name = "allow_metric") val allowMetric: Boolean,
    @ColumnInfo(name = "allow_imperial") val allowImperial: Boolean,
    @ColumnInfo(name = "allow_us_customary") val allowUSCustomary: Boolean,
    val notes: String,
) : Parcelable

@Parcelize
data class EditableDataSet(
    val id: Long,
    val name: String,
    val currencyCode: String,
    val allowMetric: Boolean,
    val allowImperial: Boolean,
    val allowUSCustomary: Boolean,
    val notes: String,
) : Parcelable {
    fun toDomain(): DataSet? {
        val trimmedName = name.trim()
        // It could get confusing if an empty name leaked into the database (it would be
        // semi-invisible in the UI) so we'll check that here, even though we could generate a
        // Source with such a name and this is not really validation code - we expect to have been
        // called on a pre-validated EditableSource.
        if (trimmedName.isEmpty()) {
            return null
        }
        // TODO: Is this a reasonable place to do trimming? Gut feeling is that yes it is, since
        // validation doesn't care about this, it's just a bit of "tidying". But not sure.
        return DataSet(
            id = id,
            name = trimmedName,
            currencyCode = currencyCode,
            allowMetric = allowMetric,
            allowImperial = allowImperial,
            allowUSCustomary = allowUSCustomary,
            notes = notes
        )
    }

    companion object {
        fun fromDataSet(dataSet: DataSet?): EditableDataSet {
            if (dataSet == null) {
                // TODO: The currencyCode should default to current locale
                // TODO: The default "allowX" values should probably be configured in settings - will just hard-code something I like for now
                return EditableDataSet(
                    0,
                    "",
                    "",
                    allowMetric = true,
                    allowImperial = true,
                    allowUSCustomary = false,
                    notes = ""
                )
            } else {
                return EditableDataSet(
                    id = dataSet.id,
                    name = dataSet.name,
                    currencyCode = dataSet.currencyCode,
                    allowMetric = dataSet.allowMetric,
                    allowImperial = dataSet.allowImperial,
                    allowUSCustomary = dataSet.allowUSCustomary,
                    notes = dataSet.notes
                )
            }
        }
    }
}

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
@Parcelize
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    val name: String,
    // default_unit implicitly specifies the item's QuantityType. It also serves as the default unit
    // to use when the user is entering the first price for an (item, source) combination.
    @ColumnInfo(name = "default_unit") val defaultUnit: MeasureUnit,
    // TODO: GUI should probably restrict and/or warn before changing default_unit between
    // MeasureUnits - maybe if you have no prices yet you can do it. (It's completely fine to change
    // within a MeasureUnit.)
    val notes: String,
) : Parcelable
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

// Note that we have the suprisingly horrific code around defaultUnitIdByQuantityTypeOrdinal instead
// of a simple "val defaultUnit: MeasureUnit" because I thought it would be user-friendly to keep
// the selected unit for each quantity type while the user is editing, and then it turns into a
// nightmare of un-parcelizable types and working with ordinals and IDs rather than enum class
// objects themselves. It probably isn't that bad in hindsight, but the code is way more complex
// than feels necessary.
@Parcelize
data class EditableItem private constructor(
    val id: Long,
    val dataSetId: Long, // TODO: although the non-editable Item has a dataSetId and that is probably a strong argument for keeping this is, I half wonder if we should just shove our full DataSet object in here. OTOH it will slightly add to the serialisation burden and we do serialise this every time anything changes.
    val name: String,
    val quantityType: QuantityType,
    val defaultUnitIdByQuantityTypeOrdinal: List<Long>,
    val notes: String,
) : Parcelable {
    /* TODO DELETE
    // Note this is indexed by QuantityType.ordinal, not QuantityType.value.
    val defaultUnitArray = QuantityType.entries.map { quantityType ->
        getRelevantMeasureUnits(dataSet, quantityType, includeDisplayOnly = false).first()
    }
    */

    val defaultUnit: MeasureUnit get() = MeasureUnit.fromValue(defaultUnitIdByQuantityTypeOrdinal[quantityType.ordinal])!!

    fun toDomain(): Item? { // TODO: not just here - would "toItem" pair better with fromItem?!
        val trimmedName = name.trim()
        // It could get confusing if an empty name leaked into the database (it would be
        // semi-invisible in the UI) so we'll check that here, even though we could generate an
        // Item with such a name and this is not really validation code - we expect to have been
        // called on a pre-validated EditableItem.
        if (trimmedName.isEmpty()) {
            return null
        }
        // TODO: Is this a reasonable place to do trimming? Gut feeling is that yes it is, since
        // validation doesn't care about this, it's just a bit of "tidying". But not sure.

        // This is a devCheck not a "return null" check because it indicates an internal error.
        devCheck(quantityType == defaultUnit.quantityType) {
            "Expected consistent quantity types on EditableItem but have $quantityType and $defaultUnit" }
        return Item(
            id = id,
            dataSetId = dataSetId,
            name = trimmedName,
            defaultUnit = defaultUnit,
            notes = notes
        )
    }
    // TODO: I have had some intermittent crashes when on the "Edit product" screen and I put it in
    // background, adb kill it and then return to it via the overview menu. The error in logcat is
    // fairly consistently "java.lang.IllegalArgumentException: No enum constant
    // com.example.composetutorial.MeasureUnit.ĭ????" with almost nothing helpful in the gigantic
    // stack backtrace. This does not seem very easy to reproduce, but has cropped up once or twice.
    // I really don't know what's going on. About all I can do is leave this note here to remind
    // me in case I spot something later or if this does go wrong again or to spend some more time
    // trying to reproduce this later.

    companion object {
        fun fromItem(item: Item?, dataSet: DataSet): EditableItem {
            val defaultUnitIdByQuantityTypeOrdinal = QuantityType.entries.map { quantityType ->
                getRelevantMeasureUnits(dataSet, quantityType, includeDisplayOnly = false).first().id
            }.toMutableList()
            if (item == null) {
                // It's probably reasonable to default to sold by weight, and it's nice not to have
                // the possibility of a null state.
                val quantityType = QuantityType.WEIGHT
                // TODO DELETE getRelevantMeasureUnits(dataSet, quantityType, includeDisplayOnly = false).first()
                return EditableItem(0, dataSet.id, "", QuantityType.WEIGHT, defaultUnitIdByQuantityTypeOrdinal,"")
            } else {
                devCheck(dataSet.id == item.dataSetId) {
                    "Expected identical dataSetIds but have dataSet.id ${dataSet.id} and item.dataSetid ${item.dataSetId}"
                }
                defaultUnitIdByQuantityTypeOrdinal[item.defaultUnit.quantityType.ordinal] = item.defaultUnit.id
                return EditableItem(
                    item.id,
                    dataSet.id,
                    item.name,
                    item.defaultUnit.quantityType,
                    defaultUnitIdByQuantityTypeOrdinal,
                    item.notes
                )
            }
        }
    }
}

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
@Parcelize
data class Source(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "data_set_id") val dataSetId: Long,
    val name: String,
    @ColumnInfo(name = "loyalty_discount_type") val loyaltyDiscountType: LoyaltyDiscountType, // TODO: JUST GET RID OF "DISCOUNT" FROM NAMES HERE INCL THE ENUM CLASS?
    @ColumnInfo(name = "loyalty_multiplier") val loyaltyMultiplier: Double,
    val notes: String,
) : Parcelable

enum class LoyaltyDiscountType(val id: Long) {
    NONE(1),
    BONUS(2),
    DISCOUNT(3);

    companion object {
        private val loyaltyDiscountTypeById = LoyaltyDiscountType.entries.associateBy { it.id }

        fun fromValue(loyaltyDiscountTypeId: Long): LoyaltyDiscountType? =
            loyaltyDiscountTypeById[loyaltyDiscountTypeId]
    }
}

// TODO: IN THE DB AND IN "SOURCE", THE LOYALTYDISCOUNTTYPE WILL BE STORED BUT THE PERCENTAGE WILL BE STORED AS A LOYALTY_MULTIPLER WHICH IS JUST APPLIED VIA A MULT AND DOESN'T NEED TO BE TREATED DIFFERENTLY DEPEND ING ON LOYALTHY TYPE
@Parcelize
data class EditableSource(
    val id: Long,
    val dataSetId: Long,
    val name: String,
    val loyaltyDiscountType: LoyaltyDiscountType,
    // TODO: In general I am inconsistent about loyaltyPercentage vs loyaltyDiscountPercentage naming etc - note that the percentage is *not* in general a "discount" percentage, it may be a bonus percentage
    val loyaltyPercentage: String, // TODO: NEED TO ADD THIS TO NON-EDITABLE TOO! WE ALSO NEED TO STORE THE NONE/BONUS/DISCOUNT FLAG HERE
    val notes: String,
) : Parcelable {
    fun toDomain(locale: Locale): Source? {
        val trimmedName = name.trim()
        // It could get confusing if an empty name leaked into the database (it would be
        // semi-invisible in the UI) so we'll check that here, even though we could generate a
        // Source with such a name and this is not really validation code - we expect to have been
        // called on a pre-validated EditableSource.
        if (trimmedName.isEmpty()) {
            return null
        }
        // TODO: Is this a reasonable place to do trimming? Gut feeling is that yes it is, since
        // validation doesn't care about this, it's just a bit of "tidying". But not sure.
        val loyaltyPercentage = parseStringAsDoubleOrNull(locale, loyaltyPercentage)
        val loyaltyMultiplier = when (loyaltyDiscountType) {
            LoyaltyDiscountType.NONE -> 1.0
            LoyaltyDiscountType.BONUS -> if (loyaltyPercentage != null) 100.0 / (100.0 + loyaltyPercentage) else null // TODO: double check this calculation later - I think I am confusing myself and there may not be a difference between bonus and discount, but I am really not sure any more - hmm, *maybe* this is right, and maybe the insight is that a discount is a discount, but with cashback I don't actually get my 5% or whatever *on the cashback* (it's not literal cash back so I can't spend it again for another 5%) - still very unsure though
            LoyaltyDiscountType.DISCOUNT -> if (loyaltyPercentage != null) 1.0 - (loyaltyPercentage / 100.0) else null
        }
        if (loyaltyMultiplier == null) {
            return null
        }
        return Source(
            id = id,
            dataSetId = dataSetId,
            name = trimmedName,
            loyaltyDiscountType = loyaltyDiscountType,
            loyaltyMultiplier = loyaltyMultiplier,
            notes = notes
        )
    }

    // 5% bonus is not the same as 5% discount/cashback. Suppose we want to buy something costing £100.
    // - If there is a 5% discount, the price is £95 and we hand over £95.
    // - If we get 5% cashback,  we hand over £100 and get £5 back, so £95 net.
    // - If we get 5% bonus in some "store account", we need to deposit £95.24 and the 5% bonus makes that up to the £100 we need.
    // - If we get 5% bonus as points on our spending, we "theoretically" spend £95.24, get 5% bonus as points and that makes up the £100 we need. (In reality you can't do this, but I think in the long term it works out as if you can.) I think an alternate way of looking at this is that you spend £100, get £5 worth of points but those points are not quite as good as cash because you don't get 5% back when you spend those points, so we value the points at £4.76 instead of £5.
    // TODO: Revisit this later as I have found myself flip-flopping
    companion object {
        fun fromSource(source: Source?, dataSetId: Long, locale: Locale): EditableSource {
            if (source == null) {
                return EditableSource(0, dataSetId, "", LoyaltyDiscountType.NONE, "", "")
            } else {
                devCheck(dataSetId == source.dataSetId) {
                    "Expected identical dataSetIds but have dataSetId $dataSetId and source.dataSetid ${source.dataSetId}"
                }
                val loyaltyPercentage = when (source.loyaltyDiscountType) {
                    LoyaltyDiscountType.NONE -> {
                        ""
                    }

                    LoyaltyDiscountType.BONUS -> {
                        formatDoubleForEditing(
                            100.0 / source.loyaltyMultiplier - 100.0,
                            minDecimals = 0,
                            maxDecimals = 2,
                            locale
                        )
                    } // TODO CHECK AGAIN LATER
                    LoyaltyDiscountType.DISCOUNT -> {
                        formatDoubleForEditing(
                            100.0 * (1 - source.loyaltyMultiplier),
                            minDecimals = 0,
                            maxDecimals = 2,
                            locale
                        )
                    }
                }
                return EditableSource(
                    source.id,
                    dataSetId,
                    source.name,
                    source.loyaltyDiscountType,
                    loyaltyPercentage,
                    source.notes
                )
            }
        }
    }
}

// TODO: This needs history tracking stuff adding, either on this table or via a separate table.
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
@Parcelize // TODO: probably won't need this once the edit dialog is written to use new style viewmodel data stuff
data class PriceEntity(
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
    val price: Double, // TODO: It might be better to rename this column to avoid "price.price" type stuff
    // TODO: would "amount" be a much simpler yet still generic name instead of "measure"?? hmm,
    // maybe not - "amount" could also be a monetary amount - but maybe "quantity" would work? I am
    // cooling on "measure" somewhat right now
    val measure: Double,
    // Although measure is stored in the base unit, we also record the actual unit the user entered
    // the price in. This allows us to show it back to them in the most natural form when they are
    // e.g. comparing the database price with the current shelf price. We do have a default unit
    // stored on the item, but tracking it per actual price allows us to handle situations where
    // supermarket A sells milk in pint multiples (even if the pack still has litres shown as well,
    // the user may think of this in pints) while supermarket B sells it in litre multiples.
    // TODO: Rename this as "user_unit" or something?
    @ColumnInfo(name = "original_unit") val originalUnit: MeasureUnit,

    val confirmed: Instant,

    val details: String // Additional price details TODO: rename "notes"?
) : Parcelable

// TODO: PriceWithItem is arguably redundant now - given we have an original_unit on each price,
// that effectively tells us the quantity type implicitly and we don't need to join to item to get
// it. However, I suspect it still has some value because it allows us to do a bit of extra
// validation which may catch bugs. Probably worth thinking about this again later.
data class PriceWithItemEntity(
    // TODO: should be PriceWithItemEntity eventually
    @Embedded val priceEntity: PriceEntity,
    @ColumnInfo(name = "default_unit") val itemDefaultUnit: MeasureUnit,
)

// Price is a domain-level class which is nice for us to work with, once we've got away from the
// database layer.
@Parcelize // TODO: Can we get rid of this later!?
data class Price(
    val id: Long = 0,
    val dataSetId: Long,
    val itemId: Long,
    val sourceId: Long,
    val price: Double,
    val measure: MeasuredValue,
    val confirmed: Instant,
    val details: String, // Additional price details TODO: rename "notes"?
    // itemDefaultUnit is a copy of the defaultUnit from the Item when we originally read the
    // PriceWithItemEntity in from the database. It is intended to allow a best effort (protecting
    // against buggy code, not malicious code) validation that when we write back to the database,
    // measure hasn't somehow mutated into a different QuantityType. TODO: NEED TO MAKE SURE I
    // ACTUALLY USE THIS WHEN DOING INSERT/UPDATE - I THINK Price.toEntity() IS NOW DOING THIS,
    // SEE COMMENT BELOW - BUT THINK ABOUT THIS FRESH
    val itemDefaultUnit: MeasureUnit
) : Parcelable {

    fun toEntity(): PriceEntity {
        // TODO: Is this a reasonable place to be doing this check?
        // TODO: I think this check is technically redundant because using
        // itemDefaultUnit.quantityType to determine the base unit will cause an internal check
        // error if measure's own unit is a different type - but this is maybe a bit more explicit.
        devCheck(measure.unit.quantityType == itemDefaultUnit.quantityType) {
            "Expected consistent quantity type when converting Price to PriceEntity but found " +
                    "measure $measure with itemDefaultUnit $itemDefaultUnit"
        }
        return PriceEntity(
            id = id,
            dataSetId = dataSetId,
            itemId = itemId,
            sourceId = sourceId,
            price = price,
            measure = measure.asValue(baseUnitForQuantityType(itemDefaultUnit.quantityType)),
            originalUnit = measure.unit,
            confirmed = confirmed,
            details = details
        )
    }

    /* TODO: DELETE?
    companion object {
        fun createEmpty(): Price {
            return Price(
                dataSetId = 0, // TODO MASSIVE HACK
                itemId = 0, // TODO MASSIVE HACK
                sourceId = 0, // TODO MASSIVE HACK
                price = 0.0,
                measure = MeasuredValue(0.0, MeasureUnit.ML), // TODO MASSIVE HACK
                confirmed = Instant.now(), // TODO MASSIVE HACK
                details = "",
                itemDefaultUnit = MeasureUnit.G // TODO MASSIVE HACK
            )
        }
    }
    */
}

fun baseUnitForQuantityType(quantityType: QuantityType) = when (quantityType) {
    QuantityType.WEIGHT -> MeasureUnit.G
    QuantityType.VOLUME -> MeasureUnit.ML
    QuantityType.ITEM -> MeasureUnit.EACH
}

// TODO: Whiff of ChatGPT magic
// TODO: I suspect we should actually be using the item's "default unit" not its quantityType here -
// although maybe not, it is perhaps better to keep this in the "internal" unit and convert to the
// display unit for display, to avoid "oh, it happened to work for me in metric with grams but now
// I'm in imperial it's displaying badly" concerns
fun PriceWithItemEntity.toDomain(): Price {
    // I have checks like this in various places but this is probably a pretty solid place for one.
    // On the way from database->domain, this is where we have a "solid" itemDefaultUnit value
    // (because it came from a database join) and that gives us an independent cross-check that
    // priceEntity.originalUnit is of the right QuantityType. (TODO: We should also be doing a check
    // before we write to the database, to stop bad data getting in, but at that point we don't have
    // such absolutely confidence in our itemDefaultUnit.)
    devCheck(priceEntity.originalUnit.quantityType == itemDefaultUnit.quantityType) {
        "Expected consistent units on PriceWithItemEntity but we have originalUnit " +
                "${priceEntity.originalUnit} and itemDefaultUnit $itemDefaultUnit"
    }
    return Price(
        id = priceEntity.id,
        dataSetId = priceEntity.dataSetId,
        itemId = priceEntity.itemId,
        sourceId = priceEntity.sourceId,
        price = priceEntity.price,
        measure = MeasuredValue(
            priceEntity.measure,
            baseUnitForQuantityType(priceEntity.originalUnit.quantityType)
        ).to(priceEntity.originalUnit),
        confirmed = priceEntity.confirmed,
        details = priceEntity.details,
        itemDefaultUnit = itemDefaultUnit,
    )
}

@Dao
interface DataSetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dataSet: DataSet): Long

    @Upsert
    suspend fun upsert(dataSet: DataSet)

    // TODO: Is this sort case-insensitive? If not I may need to sort myself after, and thus don't
    // need this order by here
    @Query("SELECT * FROM data_set ORDER BY name ASC")
    fun getAllDataSets(): Flow<List<DataSet>>

    @Query("DELETE FROM data_set WHERE id = :dataSetId")
    suspend fun deleteById(dataSetId: Long): Int
}

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item): Long

    @Upsert
    suspend fun upsert(item: Item)

    /* TODO DELETE?
    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
    */

    // TODO: Is this sort case-insensitive? If not I may need to sort myself after, and thus don't need this order by here
    @Query("SELECT * FROM item WHERE data_set_id = :dataSetId ORDER BY name ASC")
    fun getAllItems(dataSetId: Long): Flow<List<Item>>

    @Query("DELETE FROM item WHERE id = :itemId")
    suspend fun deleteById(itemId: Long): Int
}

@Dao
interface SourceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dataSet: Source): Long

    @Upsert
    suspend fun upsert(source: Source)

    // TODO: Is this sort case-insensitive? If not I may need to sort myself after, and thus don't need this order by here
    @Query("SELECT * FROM source WHERE data_set_id = :dataSetId ORDER BY name ASC")
    fun getAllSources(dataSetId: Long): Flow<List<Source>>

    @Query("DELETE FROM source WHERE id = :sourceId")
    suspend fun deleteById(sourceId: Long): Int
}

@Dao
interface PriceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(price: PriceEntity): Long

    @Upsert
    suspend fun upsert(price: PriceEntity)

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
}

// TODO: ChatGPT semi-magic
// Represents a UI state that should be both:
// - Observable via [state] for UI rendering
// - Emitted via [events] for triggering side-effects
class SyncedStateEvent<T>(initialState: T) {
    private val _state = MutableStateFlow(initialState)
    private val _events = MutableSharedFlow<T>(extraBufferCapacity = 1)

    val state: StateFlow<T> = _state
    val events: SharedFlow<T> = _events

    @Composable
    fun collectAsStateWithLifecycle(): State<T> = _state.collectAsStateWithLifecycle()

    suspend fun update(value: T) {
        _state.value = value
        _events.emit(value)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val priceTrackerRepository: PriceTrackerRepository,
    application: Application
) : ViewModel() {
    init {
        Log.d("MyApp", "HomeScreenViewModel created: $this")
    }

    private val app = application

    // Every time getPreference() is called it returns a *new* StateFlow, which is probably not what
    // we want. So we call it once per preference, cache the result in the ViewModel and then use
    // that everywhere.
    // TODO: I need to be careful not to forget this and call it directly.
    private val selectedDataSetFlow = getPreference(SELECTED_DATA_SET_ID_KEY)
    private val selectedItemIdFlow = getPreference(SELECTED_ITEM_ID_KEY)
    private val selectedSourceIdFlow = getPreference(SELECTED_SOURCE_ID_KEY)
    private fun <T> getPreference(key: Preferences.Key<T>): StateFlow<T?> {
        return app.dataStore.data
            .map { prefs -> prefs[key] }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    }

    fun <T> savePreference(key: Preferences.Key<T>, value: T?) {
        viewModelScope.launch {
            app.dataStore.edit { prefs ->
                if (value != null) prefs[key] = value else prefs.remove(key)
            }
        }
    }

    // TODO: Rename UIContent->HomeScreenUIContent and/or scope it to this ViewModel?
    private val _uiState = MutableStateFlow(
        Pair(
            false,
            HomeScreenUIContent.createEmpty()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        // This forces the delegate to initialize safely on the main thread TODO: VOODOO
        @Suppress("UNUSED_VARIABLE") val unused = app.dataStore

        // TODO: On the very first run when the database is created, we appear to end up with the
        // app running with no data sets - it has to be killed and restarted to see them. Not
        // investigated what's going on - superficially this looks like it ought to cause events
        // to be generated, but maybe the fact the database is *actually empty* without the dataset
        // table on the first run is an edge case. We need to fix this, otherwise users will get a
        // very poor first impression. OK, this certainly doesn't *always* happen and I do wonder
        // if I just got impatient and it was running a bit slow. I will just have to keep an eye
        // on it. It's by no means conclusive but I discussed this with ChatGPT and Perplexity and
        // both seemed to feel that what I am doing should not be at risk of this happening (barring
        // bugs of course).
        val dataSetFlow = priceTrackerRepository.getAllDataSets()

        val dataSetOnlyDatabaseFlow = selectedDataSetFlow.flatMapLatest { dataSetId ->
            // dataSetId can be null here (e.g. during startup when we haven't yet got the
            // preference yet, and maybe also if the user deletes all the data in the database) so
            // we need to deal with it. I think it would be wrong to use filterNotNull(), because we
            // do want to emit something - in particular, during startup, if datasetId is null and
            // *stays* null (e.g. empty database and SELECTED_DATA_SET_ID_KEY has been set to null
            // as a result), any flow that combine()s this one would never see combine() emit. This
            // just might work out OK, but it feels dangerous. I think empty lists are perfect valid
            // results to emit in the null case.
            Log.d("MyFlow", "dataSetOnlyDatabaseFlow dataSetId $dataSetId")
            // We are combining freshly-created DAO flows, so we cannot see "stale" data here, so
            // the dataSetId we are tagging the results with will be correct. (In practice non-empty
            // lists of results for these queries are self-tagging, but we need to handle empty
            // lists correctly too.)
            combine(
                flowOf(dataSetId),
                if (dataSetId != null) priceTrackerRepository.getAllItems(dataSetId) else flowOf(
                    emptyList()
                ),
                if (dataSetId != null) priceTrackerRepository.getAllSources(dataSetId) else flowOf(
                    emptyList()
                ),
                ::Triple
            )
        }

        val dataSetIdAndItemIdFlow = combine(
            selectedDataSetFlow,
            selectedItemIdFlow,
            ::Pair
        )

        val dataSetIdAndItemIdDatabaseFlow =
            dataSetIdAndItemIdFlow.flatMapLatest { (dataSetId, itemId) ->
                Log.d(
                    "MyFlow",
                    "dataSetIdAndItemIdDatabaseFlow dataSetId $dataSetId, itemId $itemId"
                )
                val priceFlow = if (dataSetId != null && itemId != null)
                    priceTrackerRepository.getPricesForItem(dataSetId = dataSetId, itemId = itemId)
                else
                    flowOf(emptyList())
                // We are creating a flow based on a freshly created DAO flow, so we cannot see
                // "stale" data here and thus the IDs we are tagging the results with will be
                // correct.
                priceFlow.flatMapLatest { priceList ->
                    flowOf(
                        Pair(
                            Pair(dataSetId, itemId),
                            priceList
                        )
                    )
                }
            }

        val combinedDatabaseFlow =
            combine(dataSetFlow, dataSetOnlyDatabaseFlow, dataSetIdAndItemIdDatabaseFlow, ::Triple)

        val allUserInputFlow = combine(
            selectedDataSetFlow,
            selectedItemIdFlow,
            selectedSourceIdFlow,
            ::Triple
        )

        val todoRenameMeFlow = combine(
            selectedSourceIdFlow,
            combinedDatabaseFlow
        ) { _, it -> it }

        // completeUIStateFlow delivers complete, consistent results which reflect the user's
        // selection. However, it doesn't make any guarantees as to how long it takes to emit after
        // allUserInputFlow emits.
        val completeUIStateFlow =
            todoRenameMeFlow.flatMapLatest { (dataSetList, taggedItemListAndSourceList, taggedPriceList) ->
                // We can take the current UI values here because ultimately that's all we care
                // about; if the current flow value we're processing is older, we want to discard it
                // anyway and because the flows are dependent on these parameters, they will emit
                // new values once they finish querying. It feels somewhat ridiculous to have to
                // discard stale values like this but as far as I can tell you either do something
                // like this, accept a mixture of stale values or re-run all your queries every
                // single time even if most of them haven't had a parameter change. Maybe I am doing
                // something silly.
                val dataSetId = selectedDataSetFlow.value
                val itemId = selectedItemIdFlow.value
                val sourceId = selectedSourceIdFlow.value

                if (taggedItemListAndSourceList.first != dataSetId) {
                    Log.d(
                        "MyFlow",
                        "completeUIStateFlow discarding dataSetId ${taggedItemListAndSourceList.first}, want $dataSetId"
                    )
                    emptyFlow()
                } else if (taggedPriceList.first != Pair(dataSetId, itemId)) {
                    Log.d(
                        "MyFlow",
                        "completeUIStateFlow discarding (dataSetId, itemId) ${taggedPriceList.first}, want ${
                            Pair(
                                dataSetId,
                                itemId
                            )
                        }"
                    )
                    emptyFlow()
                } else {
                    val itemList = taggedItemListAndSourceList.second
                    val sourceList = taggedItemListAndSourceList.third
                    val priceList = taggedPriceList.second

                    val dataSet = dataSetList.find { it.id == dataSetId }
                    val item = itemList.find { it.id == itemId }
                    val source = sourceList.find { it.id == sourceId }

                    Log.d(
                        "MyFlow",
                        "completeUIStateFlow dataSetId ${selectedDataSetFlow.value} ${dataSet?.id} (list size ${dataSetList.size}), itemId ${item?.id} (list size ${itemList.size}), sourceId ${source?.id} (list size ${sourceList.size})"
                    )

                    // TODO: I suspect in practice this analysis is lightweight enough we are fine doing it in this coroutine on the main thread, but just possibly we should shift (probably the whole database flow, but maybe just this work) onto a coroutine on a worker thread?
                    val priceAnalysis = analysePrices(dataSet, priceList, sourceList)
                    /* TODO: Temp note for reference - will want something like this in UI when picking out a specific supermarket:
                    val sortedSupermarkets: List<Pair<Supermarket, PriceData>> = ...
val selectedPriceData = remember(selectedSupermarket, sortedSupermarkets) {
    sortedSupermarkets.firstOrNull { it.first == selectedSupermarket }
}
*/

                    Log.d("MyFlow", "derived analysedPriceList")

                    // delay(5000) // TODO HACK
                    flowOf(
                        HomeScreenUIContent(
                            dataSet,
                            dataSetList,
                            item,
                            itemList,
                            source,
                            sourceList,
                            priceList,
                            priceAnalysis
                        )
                    )
                }
            }

        viewModelScope.launch(Dispatchers.Default) {
            // Add the "loading" flag to the UI state flow, rather than allowing arbitrarily long
            // delays before the user sees any kind of response. Note that because we use
            // collectLatest(), if the user changes the inputs the timeout starts again, which is
            // what we want.
            val todo1 = allUserInputFlow.flatMapLatest { _ ->
                val newUIContent = withTimeoutOrNull(spinnerDelayMillis) {
                    completeUIStateFlow.first()
                }
                if (newUIContent == null) {
                    // We timed out. Make a new state available which is the current (old) state but
                    // flagged as loading.
                    flowOf(Pair(true /* loading */, _uiState.value.second))
                } else {
                    // We didn't time out.
                    flowOf(Pair(false /* loading */, newUIContent))
                }
            }

            val todo2 = completeUIStateFlow.map { Pair(false /* loading */, it) }

            // TODO: Is there a risk with merge().collectLatest() here that a "loading" state will
            // somehow come *after* the corresponding *loaded* state? If so we'd end up stuck with
            // the scrim up forever. I am not sure there *is* a risk, but one possible fix *might*
            // be to have the "allUserInput-only" flow (the one with the timeout) *redo the
            // collection* in the "we timed out" branch after it emits the "loading=true" state -
            // there should not be any reordering *within* flows from the merge, right? And if we
            // put a distinctUntilChanged() after the merge that will catch any cases where we get a
            // duplicate emission because the database flow also emits the same thing at
            // approximately the same time
            val todo3 = merge(todo1, todo2)

            todo3.collectLatest { todoRename ->
                Log.d("MyFoo", "newUIState")
                _uiState.value = todoRename
            }
        }
    }
}

/* TODO?
enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}
*/

// Since all our data is local, we generally expect to be able to respond promptly to user requests.
// Things like the dropdown they touched closing or the button they touched animating provide
// feedback that their touch has been noticed. We don't immediately show a spinner because AIUI
// "short" delays are mostly perceived as instantaneous, and if we showed a spinner (especially a
// full screen one with scrim) immediately only to remove it after 50ms, that would be jarring.
// Instead we leave the UI unaltered for spinnerDelay ms; if we complete our operations within that
// time, the user never sees a spinner. If things take longer than that, we need to do something as
// the user is not going to perceive the operation as instantaneous anyway, so we show a spinner
// until it completes. We don't update the UI until the operation completes - we acknowledge a
// user's change to a dropdown by the dropdown disappearing, but we don't want to show the new value
// in that dropdown immediately while the rest of the screen still contains data related to the old
// value. The spinner (which in this case is likely to be on a full-screen scrim) shows that the
// on-screen data is outdated, but we retain consistency. Even if the data retrieval is quicker than
// spinnerDelay ms, we don't want a janky double-update where the dropdown's content changes
// instantly then the associated data changes a few ms later.
const val spinnerDelayMillis = 1000L // TODO SHOULD BE 200L

// This value is a trade-off between showing the user validation failures ASAP and not annoying them
// by showing transient validation failures while they are in the middle of actively editing. This
// feels reasonable-ish and we can always tweak it later.
// TODO: a whole second feels insanely slow
const val defaultValidationMessageDelayMillis = 200L

// TODO: If this is too long, the user can break something different, click Save again and have to
// wait until the first animation finishes. Let's start with 1000 and see how it goes.
const val errorHighlightBoxVisibleTimeMillis = 1000L

// TODO: https://m3.material.io/foundations/layout/applying-layout/compact says 16dp left and right
// margins - maybe change this? Then again there are placed where I've used edge-to-edge for lists
// so I just don't know. For that matter, are there 16.dp margins supposed to be added to left and
// right of the top app bar as well? Or just the body content below it?
val screenBorder = 8.dp

// TODO: MD3 specs say there should be a 24.dp horizontal border, but this seems quite ugly. The
// left hand edge of the dialog's body controls don't line up with the close icon and the right hand
// edges don't line up with the right hand edge of the "Save" text button. Some of the screenshots
// in the documentation seem to show some but not all of these misalignments. It just feels
// half-baked and inconsistent so I'm going to go with this.
val fullScreenDialogBorder = 16.dp // TODO: rename ...HorizontalBorder?

val fullScreenDialogVerticalBorder = 8.dp


// MD3 says 12.dp but MyExposedDropdownMenuBox's dropdown item text doesn't line up with the parent
// TextField text with that. TODO: We could override it for that specific case and use 12.dp for
// other menus?
val menuLeftPadding = 16.dp

val defaultErrorHighlightOffset = 6.dp

/* TODO DELETE
// MD3 standard values
val oneLineListItemHeight = 56.dp
val listItemHorizontalPadding = 16.dp
*/

// Seems best to make the right padding symmetrical.
val menuRightPadding = menuLeftPadding

// TODO: These arbitrary lengths are UI-only and are just intended to stop the user typing insane
// amounts of text into TextFields and breaking layouts. They may well want to be tweaked later.
const val maxDataSetNameLength =
    32 // TODO: just possibly shorter than others due to use of nav drawer to show these?!
const val maxItemNameLength = 32
const val maxSourceNameLength = 32
const val maxNotesLength = 200 // TODO TEMP FOR TESTING, SHOULD BE 1024

// TODO: RENAME THIS IF IT SURVIVES REFACTORING
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    item: Item?, itemList: List<Item>, onSelectedItemIdChange: (Long) -> Unit
) {
    var showItemSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // TODO: If we have no data sets, we should (analogous to how the source dropdown works)
        // show a message about selecting/creating one *and hide the rest of the UI*. Nothing makes
        // sense without a dataset, there is no way to pick a product or source. This probably means
        // we need support from our parent (or this needs moving up into the parent) to do that.

        // Item selector
        TextField(
            value = item?.name ?: "",
            onValueChange = { /* No-op, read-only */ },
            label = { Text("Product") },
            enabled = false, // TODO: this is necessary to make "clickable" work, bit hacky
            modifier = Modifier
                .fillMaxWidth()
                .clickable { Log.d("MyApp", "SPS"); showItemSheet = true },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search products",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            // TODO: There might be an argument that this should "sometimes" get the focused
            // colours, but since clicking on it immediately opens a modal bottom sheet, I think
            // it's probably reasonable to hard-code false here.
            colors = myTextFieldColors(false)
        )

        // Item Modal Bottom Sheet
        // TODO: This is mostly untouched AI code and it probably needs a review. I am also wondering
        // if I should just make this a full-screen dialog, now I more-or-less know how to do one
        // and since it would give more space for the product list to be scrolled in etc. But it
        // might be best to just leave this as-is for now and fiddle around with this after hitting
        // MVP.
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
                        label = { Text("Search products") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search, contentDescription = "Search"
                            )
                        })
                    LazyColumn {
                        items(itemList.filter {
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
fun myTextFieldColors(isFocused: Boolean) = TextFieldDefaults.colors(
    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    disabledLabelColor = if (isFocused)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    // We can't make the indicator thicker when mock-focused, but we can at least change the colour.
    disabledIndicatorColor = if (isFocused)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant,
)


// TODO: We *may* want to disable the on click ripple whatsit for this, based on how the
// "official" experimental ExposedDropdownMenuBox behaves - although having thoughts about
// it and chatted with Grok and ChatGPT, maybe this is *good* and it is a weird quirk of (my
// impl) of the experimental "official" one that is weird
// TODO: If a TextField has focus and then you click on a MyExposedDropdownMenuBox, the
// TextField does *not* lose focus so it retains its primary colour label/underline, which
// isn't ideal.
// TODO: We *may* want to disable the on click ripple whatsit for this, based on
// how the "official" experimental ExposedDropdownMenuBox behaves - although
// having thoughts about it and chatted with Grok and ChatGPT, maybe this is
// *good* and it is a weird quirk of (my impl) of the experimental "official"
// one that is weird
@Composable
fun <T, ID : Comparable<ID>> MyExposedDropdownMenuBox(
    modifier: Modifier = Modifier,
    selectedId: ID?,
    onValueChange: (ID) -> Unit, // TODO: rename onItemSelected? is there a "standard" for e.g. the crappy MD3 experimental dropdown?
    enabled: Boolean = true,
    label: @Composable () -> Unit, // TODO: rename to distinguish from getLabel type use?
    supportingText: @Composable (() -> Unit)? = null,
    items: List<T>,
    getId: (T) -> ID,
    getLabel: (T) -> String,
    getDividerBetween: ((T, T) -> Boolean)? = null,
) {
    var textFieldWidth by remember { mutableIntStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ItemWithDropdown(
            dropdownModifier = Modifier.width(with(LocalDensity.current) { textFieldWidth.toDp() }),
            selectedId = selectedId,
            onValueChange = onValueChange,
            enabled = enabled,
            onExpand = { isExpanded = it },
            items = items,
            getId = getId,
            getLabel = getLabel,
            getDividerBetween = getDividerBetween,
        ) {
            val itemMap = items.associateBy { getId(it) }
            val todoPulledOut: String = if (selectedId == null) "" else {
                val item = itemMap[selectedId]
                if (item != null) getLabel(item) else "Invalid ID $selectedId"
            }
            TextField(
                value = todoPulledOut,
                onValueChange = { /* No-op, handled by dropdown */ },
                label = label,
                readOnly = true,
                enabled = false, // TODO: this is necessary to make "clickable" work, bit hacky
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
                    }, /* TODO DELETE
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    }, */
                // TODO: It isn't ideal to use isExpanded as a substitute for focus here, but it
                // doesn't look too bad in practice. As probably noted elsewhere, because we have to
                // have the TextField disabled in order to make it clickable, it doesn't seem to
                // actually get focus (even when it gets that "it's focus but it's not focus" D-pad
                // navigation focus) as far as onFocusChanged is concerned.
                colors = if (enabled) myTextFieldColors(isExpanded) else TextFieldDefaults.colors()
            )
        }
        // If we let TextField display supportingText itself, it gets included in the bounding box
        // and the dropdown appears below the supportingText, whereas we want it to drop down over
        // the supportingText, "hanging off" the main TextField text box. So we jump through far too
        // many hoops to display it ourselves here.
        if (supportingText != null) {
            Box(modifier = Modifier.padding(start = menuLeftPadding, top = 4.dp)) {
                ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                    CompositionLocalProvider(
                        LocalContentColor provides
                                MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        supportingText.invoke()
                    }
                }
            }
        }

    }
}

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

// TODO: ChatGPT magic, though I do mostly understand it
// TODO: Does this "do the right thing" with the user's current timezone? If I'm in Australia, an
// Instant of UTC 23:59 2nd March isn't "yesterday" just because it's now 00:01 3rd March. It probably
// does, but it would be good to check.
@Composable
fun RelativeTimeText(instant: Instant) { // TODO: rename parameter? maybe it's OK
    var now by remember { mutableStateOf(Instant.now()) }
    val ageInSeconds = Duration.between(instant, now).seconds
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
    // TODO: Not 100% sure about coloring this with no further indication to show it's "stale" and
    // try to encourage action, maybe we need a supportingText or a different layout or both. I'll
    // leave the code in for now anyway.
    Text(relativeTime, color = if (ageInSeconds < inflationThresholdDays * secondsPerDay) Color.Unspecified else MaterialTheme.colorScheme.error)
}

fun formatPrice(amount: Double, dataSet: DataSet, locale: Locale): String {
    // At least on Android this doesn't throw for invalid three-letter currency codes but it will
    // throw if given currency code "AAAA", so it seems safest to catch exceptions and have a
    // fallback, even if it's not great.
    try {
        val numberFormat = NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance(dataSet.currencyCode)
        }
        return numberFormat.format(amount)
    } catch (e: Exception) {
        // Generate a generic-ish "USD 1234" value as a fallback, without trying to use any
        // localisation settings.
        // TODO: Eventually we might want to see if there's any useful data in a currency
        // prefix/suffix/decimal places set of fields in dataSet, but we don't have those yet. But
        // even if we did, we'd probably already be using those in preference to
        // getCurrencyInstance(), so they wouldn't help us at this point.
        val numberFormat = NumberFormat.getNumberInstance()
        // TODO: The "x" instead of a space in the next line is temporary, just to make it more
        // obvious if this code is coming into play while I am developing/testing.
        return "${dataSet.currencyCode}x${numberFormat.format(amount)}"
    }
}

// TODO: EXPERIMENTAL
// TODO: HOW WILL WE HANDLE "/100G" ETC? WILL WE MAKE THESE FIRST CLASS MEASUREUNITS BUT FLAG THEM AS "MULTIPLES" SO WE OMIT THEM FROM MANY CASES, OR WILL WE MAKE IT A LIST<PAIR<MULT,MEASUREUNIT>>?
// TODO: A UnitPrice *isn't* a MeasuredValue in some sense (the value is price *per* unit, not X units), but in practice it might work nicely to represent it as one, at least internally. Not sure.
data class UnitPrice(val numerator: Double, val denominator: MeasureUnit) : Comparable<UnitPrice> {
    override fun compareTo(other: UnitPrice): Int {
        // TODO: It feels like using MeasuredValue here is slightly technically incorrect, but it
        // does do what we want and it is probably OK. Maybe it's not even technically incorrect,
        // think about it fresh.
        val thisAsMeasuredValue = MeasuredValue(this.numerator, this.denominator)
        val otherAsMeasuredValue = MeasuredValue(other.numerator, other.denominator)
        val baseUnit = baseUnitForQuantityType(thisAsMeasuredValue.unit.quantityType)
        return thisAsMeasuredValue.asValue(baseUnit).compareTo(otherAsMeasuredValue.asValue(baseUnit))
    }
}

fun getUnitPrice(amount: Double, measure: MeasuredValue, denominator: MeasureUnit): UnitPrice =
    UnitPrice(amount / measure.asValue(denominator), denominator)

// TODO: Note that we don't currently use the numerator of the returned UnitPrice - this might be
// fine, but it suggests we could simplify the return type to just MeasureUnit. OTOH, we've got to
// *calculate* the numerator anyway, so maybe we might as well pass it back in case it's handy in
// some other case?
// TODO: This probably needs to be currency-dp aware - imagine for example we're working with JPY,
// it's not about decimal places per se but about getting the "shortest" number which doesn't
// gratuitously push non-zero digits into the non-displayed part after rounding.
fun getFriendlyUnitPrice(
    amount: Double,
    measure: MeasuredValue,
    candidateDenominators: List<MeasureUnit>
): UnitPrice {
    devCheck(candidateDenominators.isNotEmpty()) { "Expected at least one candidate denominator" }
    devCheck(measure.value > 0.0) { "Expected positive measure; got $measure" }
    var bestScore: Double? = null
    var bestUnitPrice: UnitPrice? = null
    for (candidateDenominator in candidateDenominators) {
        val candidateUnitPrice = getUnitPrice(amount, measure, candidateDenominator)
        // We compute a score (lower is better) for candidateUnitPrice which measures how far away
        // it is in "decimal place" terms from having a numerator of 1. In other words, we are trying
        // to get as close to a single digit before the decimal point as we can.
        // TODO: I'm not sure this score is right - e.g. looking at ground coffee at SuperiorStore,
        // it chooses $0.66/100g but $6.61/kg is probably better. This code could maybe try to
        // down-weight "display only" units, but I'm not sure - anyway, that isn't the issue here. I
        // think we sort of don't want a 0 before the decimal point if we can help it, but our score
        // doesn't take this into account. Off the top of my head, maybe something where we fairly
        // heavily penalise for "more decimal places than our currency display format" and lightly
        // penalise for every extra digit more significant than the units digit?! Actually what
        // might work is using log10 with integer truncation to calculate the "index" of the most
        // significant digit (0 for 1s place, 2 for 10s place, -1 for 0.1s place, etc), then scoring
        // (low is good) by the index but with negative ones multipled by 2 to discourage them -
        // given we have limited dp (because of currency display settings), we want to make full use
        // of the space we have.
        val log10Of1 = 0.0
        val candidateScore = abs(log10(candidateUnitPrice.numerator) - log10Of1) // lower is better
        if (bestScore == null || candidateScore < bestScore) {
            bestScore = candidateScore
            bestUnitPrice = candidateUnitPrice
        }
    }
    return bestUnitPrice!!
}

// TODO: I suspect there is an open issue with whether the denominator should use the full name or
// symbol for the unit, probably with some extra wrinkles around "per individual item".
fun formatUnitPrice(unitPrice: UnitPrice, dataSet: DataSet, locale: Locale): String {
    return "${formatPrice(unitPrice.numerator, dataSet, locale)}/${unitPrice.denominator.symbol}"
}

// TODO: Note that selectedId is not used. I would like to use this to focus the previously
// selected item when expanding the dropdown using a D-pad, instead of defaulting to the first
// item. However, this appears to be ninja-grade level development and I tried tweaking multiple
// AI-suggested solutions and got nothing but crashes.
// TODO: RENAME?
@Composable
fun <T, ID : Comparable<ID>> ItemWithDropdown(
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier, // TODO: OK!?
    @Suppress("UNUSED_PARAMETER") selectedId: ID?, // see above
    onValueChange: (ID) -> Unit, // TODO: follow naming convention of MyExposedDropdownMenUBox
    enabled: Boolean = true,
    onExpand: (Boolean) -> Unit = {},
    items: List<T>,
    getId: (T) -> ID,
    getLabel: (T) -> String,
    getDividerBetween: ((T, T) -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    // TODO: rememberSaveable? A simple dark mode toggle could lose this otherwise. But maybe that
    // is "expected", and users probably also expect the dropdown to close on rotation.
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.clickable {
        if (enabled) {
            expanded = true
            @Suppress("KotlinConstantConditions") onExpand(expanded)
        }
    }) {
        content()

        var previousItem: T? = null
        DropdownMenu(
            modifier = dropdownModifier,
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                @Suppress("KotlinConstantConditions") onExpand(expanded)
            }) {
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
                        @Suppress("KotlinConstantConditions") onExpand(expanded)
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
            Row {
                // TODO: FWIW a quick discussion with ChatGPT suggests it is reasonable for i18n to
                // have some kind of format substitition to generate a unit price string analogous
                // to the one I'm using here. So having a single "Unit price" field is probably
                // reasonable, and it does feel like the clearest way to express it.
                // TODONOW: There's a small bug here, if we edit the price so a different unit price
                // would be more appropriate we do *not* change it when we navigate back. Obviously
                // this isn't super likely with realistic price data, but it could happen. There is
                // a subtlety here, as the user may have changed the unit price unit themselves and
                // *maybe* we should respect that if so.
                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // TODO: I am starting to wonder if this down arrow should be vertically
                        // centred wrt the textfield as a whole (including its label-above) not just
                        // the text, despite working very hard to get it to be lined up with just
                        // the "text content" before - probably arguments both ways, but think about
                        // it
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
// TODO: This is quite a long function and might benefit from subcomposables being factored out.
// TODO: Just possibly this should show the "adjusted (effective) unit price" *as well as* the "raw"
// (don't call it that) price we already show (and must, since it's what the user is seeing on the
// shelf in front of them if the data is correct). It isn't so much that the user necessarily
// directly cares, *but* that this might resolve the visual discrepancy between the (currently only
// shown) "raw" unit price here and the adjusted price in the by-source list below ItemSourceInfo
@Composable
fun ItemSourceInfo(
    dataSet: DataSet,
    item: Item?,
    source: Source?,
    sourceList: List<Source>,
    onSelectedSourceIdChange: (Long?) -> Unit,
    itemPriceList: List<Price>,
    onEditPriceClick: () -> Unit,
) {
    // TODO: Will we have a "special offer"/"short term price" flag and maybe associated data? Gut
    // feeling is no, how to handle expiry/deletion gets complex from UI and internal perspective,
    // it's not as if the offer duration is usually clearly stated, free text note probably can be
    // used for this among other things
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // TODO: animateContentSize() is experimental. If I keep it, I may also want it on the lower
        // card, which can change size when product changes (just not yet, in this mockup). The odd
        // padding here is because we want 8.dp at the left and right and 12.dp at the top and
        // bottom to try to keep the square-ish corners of the TextField away from the round-ish
        // corners at the top of the card. Because the bottom of the card has two buttons and these
        // have "touchable but background colour" space around them to meet the minimum touch size
        // (and we don't want to make them visually larger), if we use 12.dp at the bottom we
        // actually get a bit more because of that extra space "around" the buttons. So we manually
        // adjust the bottom padding to visually compensate for this while allowing the buttons to
        // have their natural touch region.
        // TODO: When the card expands, the button(s) on the "bottom" row of the card jump down
        // instead of animating smoothly "following" the bottom of the card - probably because this
        // layout is sort of "top to bottom". I suspect this can be worked around by using a box and
        // having most of the content inside a column with .align(Alignment.TopStart) and then
        // follow that by the button row with .align(Alignment.BottomCenter) or something along
        // these lines. The trouble with the code as currently structured is that the buttons are
        // generated in conditional code and getting the right layout of composables isn't trivial.
        // It is probably worth tweaking this for visual polish - it might make things clearer
        // anyway, e.g. if we factor out some sub-composables - but I'm not going to get involved
        // with it right now. We may need to attach .animateContentSize() to the Card instead of the
        // Column.
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 8.dp)
        ) {
            Log.d("MyApp", "ISI dataset $dataSet")
            Log.d("MyApp", "ISI item $item")
            Log.d("MyApp", "ISI source $item")
            val haveItemAndSource = item != null && source != null
            // If sourceList is empty this will generate a single-item menu with just "None" in,
            // but that is probably better than the "skeleton" menu we get with no items in.
            val items = listOf(Pair(-1L, "None")) + sourceList.map { Pair(it.id, it.name) }
            // TODO: Did wonder if MyExposedDropdownMenuBox should allow null IDs to avoid the need
            // for the "-1" hack here, but I really didn't want to have to make every user of it
            // be null-tolerant when it *won't* hand you a null itself unless you gave it one in the
            // input item list, so this is perhaps best but I'm not too sure. I did try wrapping
            // the null inside a simple Nullable<T> so it could "pass through" MyExposedDropdownMenuBox
            // without altering the API and I think the idea is sound but I started to run into
            // incomprehensible "out"/covariance stuff and it just felt too much just to fix this
            // where -1L is an easy hack.
            MyExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                // Note that if source is null, we pass that null through to selectedId so the
                // dropdown starts off with nothing selected and the "Store" label expands to form a
                // large "prompt". We could turn null into -1L and have "None" shown, but it's
                // probably nicer this way.
                selectedId = source?.id, /* ?: -1L */
                onValueChange = { onSelectedSourceIdChange(if (it == -1L) null else it) },
                label = { Text("Store") },
                supportingText = if (haveItemAndSource) null else {
                    { Text("Select a product and store to view or change the price there") } // TODO: poor wording? *normally* product will not be null, so maybe we should have variant wording, or maybe the message should just not mention product
                },
                items = items,
                getId = { it.first },
                getLabel = { it.second },
            )
            if (haveItemAndSource) {
                val price = itemPriceList.singleOrNull { it.sourceId == source!!.id }

                if (price == null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // TODO: Should this be in the supportingText on the store dropdown? My gut
                        // feeling is not, as this is "card content" about the store+product
                        // together, not a note specifically on the "Store" dropdown. But think
                        // about it.
                        Text("There is no price recorded for this product at this store yet.")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            FilledTonalButton(
                                onClick = onEditPriceClick,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text("Add") // TODO: "Edit"? "Add price"?
                            }
                        }
                    }
                } else {
                    // TODO: This row can get a bit congested on small phones when the text in some
                    // of the LabeledItems gets a bit long. It does kind of work and some further
                    // tweaking (e.g. making sure we force some space between the three horizontal
                    // elements) might fix the corner cases better than any alternatives, but do
                    // have a think to see if some alternate design would look and/or work better.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Price as sold"
                        ) { // TODO: quite like this, but maybe "Shelf price"? This might also help distinguish this from the "effective/adjusted price". *Just possibly* some sort of similar wording tweak on "Unit price" in ItemSourceInfo might help.
                            // TODO: There might be an argument for designing the UI to separate the
                            // price and quantity here, then we side-step the internationalisation
                            // issues of "for", which is *probably* tractable but might be a
                            // problem. If I really prefer the UI with a single text string
                            // containing "for", don't let this put me off sticking with it.
                            Text(
                                "${
                                    formatPrice(
                                        price.price,
                                        dataSet,
                                        LocalConfiguration.current.locales[0]
                                    )
                                } for ${
                                    price.measure.toDisplayString(LocalConfiguration.current.locales[0])
                                }" /*, color = MaterialTheme.colorScheme.onSurface*/
                            )
                        }

                        // TODO: Label this "Confirmed" to match the button? Or "Last confirmed", but bit long?
                        LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Confirmed" /* "Last checked" */) {
                            RelativeTimeText(price.confirmed)
                            // TODO: would it be helpful to color code this and/or show an icon
                            // ("!"?) if this is "old"? maybe even with an ascending amber/red
                            // "severity" (and correspondingly different icons?)
                        }

                        val relevantUnitFamilies =
                            remember(dataSet) { getRelevantUnitFamilies(dataSet) }

                        val relevantUnitList =
                            remember(dataSet, price.measure.unit.quantityType) {
                                getRelevantMeasureUnits(
                                    dataSet,
                                    price.measure.unit.quantityType,
                                    includeDisplayOnly = true
                                )
                            }
                        // TODO: "candidateDenominators" is also derived inside the UIContent "flow" and we could easily make it available directly here. It probably doesn't save much but we could.
                        var selectedUnitPriceUnit by rememberSaveable(dataSet, price) {
                            val candidateDenominators = getSiblingMeasureUnits(
                                dataSet,
                                price.measure.unit,
                                includeDisplayOnly = true
                            )
                            val friendlyUnitPrice = getFriendlyUnitPrice(
                                price.price,
                                price.measure,
                                candidateDenominators
                            )
                            mutableStateOf(friendlyUnitPrice.denominator)
                        }
                        // TODO: If the user selects "g" for a product sold in relative bulk, the
                        // standard decimal places on the currency is a bit misleading. This isn't a
                        // bug as such, but can/should we try to increase the decimal places on the
                        // currency in this case? Does the standard formatting stuff we are using
                        // have any concept of "not a shelf price so smaller fractions make sense
                        // than usual"? Maybe at the very least we should always round prices *up*
                        // when showing with official dp - although we are not doing the conversion
                        // ourselves, maybe the standard function has an option to do this? We could
                        // perhaps even omit units which would give a "display zero" price from the
                        // dropdown, though that might be more confusing than helpful.
                        val unitPriceString = formatUnitPrice(
                            getUnitPrice(
                                price.price,
                                price.measure,
                                selectedUnitPriceUnit,
                            ), dataSet,
                            LocalConfiguration.current.locales[0]
                        )
                        LabeledItemWithDropdown(/* modifier = Modifier.weight(1f), */ label = "Unit price",
                            text = unitPriceString,
                            //  TODO: Mixed feelings about the "/" prefix in this menu.
                            items = relevantUnitList,
                            getId = { it },
                            getLabel = { "/${it.symbol}" },
                            // Show dividers between unit families
                            getDividerBetween = { previousItem, item ->
                                val previousItemUnitFamily =
                                    previousItem.unitFamilies.intersect(relevantUnitFamilies)
                                val itemUnitFamily =
                                    item.unitFamilies.intersect(relevantUnitFamilies)
                                previousItemUnitFamily != itemUnitFamily
                            },
                            selectedId = selectedUnitPriceUnit,
                            onValueChange = { selectedUnitPriceUnit = it })

                    }

                    if (price.details.isNotEmpty()) {
                        Row(modifier = Modifier.padding(bottom = 8.dp)) {
                            LabeledItem("Notes") {
                                Text(price.details)
                            }
                        }
                    }

                    // TODO: This judgement needs to come from the price analysis data, of course
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Checked",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            ) // TODO: probably "primary"=good, default text color=neutral, "error"=bad
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Good price")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            FilledTonalButton(
                                onClick = onEditPriceClick,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text("Edit") // TODO: "Update"? (we do have a history-ish element, maybe)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // The "Confirm" button is the primary button - we expect it to be the
                            // button users click on most on this card (most of the time prices
                            // won't have changed on subsequent visits) - so it gets the position on
                            // the right.
                            // TODONOW: Confirm button sets last updated to "today" and turns itself into "Undo confirm" (or something) on being clicked, we should ideally make this as obvious as possible to the user, maybe some kind of animation
                            FilledTonalButton(onClick = {}, shape = MaterialTheme.shapes.small) {
                                Text("Confirm")
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

    Column {
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


/* TODO: Delete if not needed
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
*/

// A simple wrapper around DropdownMenuItem applying MD3 formatting.
// TODO: This isn't fully general as I don't want to add stuff that isn't going to get tested; I can
// always expand it later.
@Composable
fun MyDropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    DropdownMenuItem(
        text = {
            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                // Default colour seems to be correct so don't fiddle with it.
                text()
            }
        },
        contentPadding = PaddingValues(start = menuLeftPadding, end = menuRightPadding),
        enabled = enabled,
        onClick = onClick,
    )
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val SELECTED_DATA_SET_ID_KEY = longPreferencesKey("selected_data_set_id")
val SELECTED_ITEM_ID_KEY = longPreferencesKey("selected_item_id")
val SELECTED_SOURCE_ID_KEY = longPreferencesKey("selected_source_id")

data class HomeScreenUIContent(
    val dataSet: DataSet?,
    val dataSetList: List<DataSet>,
    val item: Item?,
    val itemList: List<Item>,
    val source: Source?,
    val sourceList: List<Source>,
    val priceList: List<Price>,
    val priceAnalysis: PriceAnalysis,
) {
    companion object {
        fun createEmpty(): HomeScreenUIContent {
            return HomeScreenUIContent(
                dataSet = null,
                dataSetList = emptyList(),
                item = null,
                itemList = emptyList(),
                source = null,
                sourceList = emptyList(),
                priceList = emptyList(),
                priceAnalysis = PriceAnalysis(emptyList(), null),
            )
        }
    }
}

// A version of Price we can use while editing - it holds the same basic information but with mostly
// string representations for editability.
@Parcelize
data class EditablePrice(
    val id: Long,
    val dataSetId: Long,
    val itemId: Long,
    val sourceId: Long,
    val price: String,
    val measureValue: String,
    val measureUnit: MeasureUnit,
    val confirmed: Instant, // TODO: rename this confirmedAt (everywhere)?
    val toConfirm: Boolean,
    val details: String,
    val itemDefaultUnit: MeasureUnit,

    ) : Parcelable {

    // Constructor for adding the first price for a (source, item) combination - we have the
    // "parent" fields, but everything else starts off blank/default.
    constructor(
        dataSetId: Long,
        itemId: Long,
        sourceId: Long,
        itemDefaultUnit: MeasureUnit
    ) : this(
        id = 0,
        dataSetId = dataSetId,
        itemId = itemId,
        sourceId = sourceId,
        price = "",
        measureValue = "",
        measureUnit = itemDefaultUnit,
        confirmed = Instant.now(),
        toConfirm = true,
        details = "",
        itemDefaultUnit = itemDefaultUnit
    )

    // Constructor for editing an existing Price.
    constructor(price: Price, locale: Locale, currencyFormat: CurrencyFormat) : this(
        id = price.id,
        dataSetId = price.dataSetId,
        itemId = price.itemId,
        sourceId = price.sourceId,
        price = formatDoubleForEditing(
            price.price,
            minDecimals = currencyFormat.decimalPlaces,
            maxDecimals = currencyFormat.decimalPlaces,
            locale
        ),
        // Rounding is particularly important here - for non-metric measures, which are stored in
        // doubles in metric base units in the database, if we didn't round we could end up with
        // some visible noise in the least significant decimal places.
        measureValue =
            formatDoubleForEditing(
                price.measure.value,
                minDecimals = 0,
                maxDecimals = price.measure.unit.maxDecimals,
                locale
            ),
        measureUnit = price.measure.unit,
        confirmed = price.confirmed,
        toConfirm = false,
        details = price.details,
        itemDefaultUnit = price.itemDefaultUnit
    )

    // TODO: Tempish note - EditablePrice is a sort of "variant domain" class just for editing - we
    // need to convert it to the "primary" domain class Price here. This name might be confusing
    // all the same, as we are approaching domain from the opposite side to a toDomain() on an
    // entity class
    fun toDomain(locale: Locale): Price? {
        val priceDouble = parseStringAsDoubleOrNull(locale, price)
        val measureValueDouble = parseStringAsDoubleOrNull(locale, measureValue)
        return if (priceDouble == null || measureValueDouble == null) {
            null
        } else {
            Price(
                id = id,
                dataSetId = dataSetId,
                itemId = itemId,
                sourceId = sourceId,
                price = priceDouble,
                measure = MeasuredValue(measureValueDouble, measureUnit),
                confirmed = if (toConfirm) Instant.now() else confirmed,
                details = details,
                itemDefaultUnit = itemDefaultUnit,
            )
        }
    }
}

fun textOrNull(
    string: String?,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
): @Composable (() -> Unit)? {
    if (string == null) {
        return string
    } else {
        return { Text(string, modifier = modifier, color = color) }
    }
}

// TODO: Apparently Android will cheerfully kill my app, upgrade it *and then restart it with the
// saved state from the old app*. And of course this has to be handled, even though it utterly
// destroys whatever remaining app logic or coherence there is after it's been through the async and
// random revival shredder, but it must be handled. Because. So if - and it feels increasingly
// unlikely I am not going to rage quit - I ever release this app, if I need to update the content
// of any of these savedstatehandle things, I need to find some magic way (can't go to the database -
// async!) to not just version stuff but magically cope with state that simply has potentially
// critical data missing. The immediate upshot of this is that in v1 I probably need to be at least
// serialising a version number so I can *detect* when Android has shot me in the foot (not the back
// of the head - that would kill me and the issue would not arise - it deliberately wants me
// crippled but alive). And maybe I can just bomb out with "Crashing because dealing with this rare
// case is too fucking horrific, please restart" message at worst.
data class EditPriceScreenUIContent(
    val editablePrice: MutableState<EditablePrice>,
    val originalPrice: EditablePrice,
    // TODO: Move the following three to the start of this data class? Entirely cosmetic of course.
    val dataSet: DataSet,
    val item: Item,
    val source: Source,
    val frozenLocale: Locale,
) {
    fun saveState(handle: SavedStateHandle) {
        saveEditablePriceState(handle)
        handle[ORIGINAL_PRICE_KEY] = originalPrice
        handle[DATA_SET_KEY] = dataSet
        handle[ITEM_KEY] = item
        handle[SOURCE_KEY] = source
        handle[LOCALE_TAG] = frozenLocale.toLanguageTag()
    }

    // This is a separate function to minimise the amount of work done after every user edit.
    fun saveEditablePriceState(handle: SavedStateHandle) {
        handle[EDITABLE_PRICE_KEY] = editablePrice.value
    }

    companion object {
        private const val EDITABLE_PRICE_KEY = "editablePrice"
        private const val ORIGINAL_PRICE_KEY = "originalPrice"
        private const val DATA_SET_KEY = "dataSet"
        private const val ITEM_KEY = "item"
        private const val SOURCE_KEY = "source"
        private const val LOCALE_TAG = "localeTag"

        fun fromSavedState(handle: SavedStateHandle): EditPriceScreenUIContent? {
            val savedEditablePrice: EditablePrice? = handle[EDITABLE_PRICE_KEY]
            Log.d("MyApp", "fromSavedState savedEditablePrice $savedEditablePrice")
            val savedOriginalPrice: EditablePrice? = handle[ORIGINAL_PRICE_KEY]
            val savedDataSet: DataSet? = handle[DATA_SET_KEY]
            val savedItem: Item? = handle[ITEM_KEY]
            val savedSource: Source? = handle[SOURCE_KEY]
            val savedLocaleTag: String? = handle[LOCALE_TAG]
            if (savedEditablePrice != null && savedOriginalPrice != null && savedDataSet != null && savedItem != null && savedSource != null && savedLocaleTag != null) {
                Log.d("MyApp", "reconstructed EditPriceScreenUIContent")
                return EditPriceScreenUIContent(
                    mutableStateOf(savedEditablePrice),
                    savedOriginalPrice,
                    savedDataSet,
                    savedItem,
                    savedSource,
                    Locale.forLanguageTag(savedLocaleTag)
                )
            } else {
                Log.d("MyApp", "couldn't reconstruct EditPriceScreenUIContent")
                return null
            }
        }
    }
}

// TODO: I wonder if these EditFooScreenUIContent classes are similar enough we can use generics to save duplicating code.
data class EditItemScreenUIContent(
    val editableItem: MutableState<EditableItem>,
    val originalItem: EditableItem,
    val dataSet: DataSet,
    // TODO: delete if not needed val frozenLocale: Locale,
) {
    fun saveState(savedStateHandle: SavedStateHandle) {
        saveEditableItemState(savedStateHandle)
        savedStateHandle[ORIGINAL_ITEM_KEY] = originalItem
        savedStateHandle[DATA_SET_KEY] = dataSet
        // TODO: delete if not needed savedStateHandle[LOCALE_TAG] = frozenLocale.toLanguageTag()
    }

    // This is a separate function to minimise the amount of work done after every user edit.
    fun saveEditableItemState(savedStateHandle: SavedStateHandle) {
        savedStateHandle[EDITABLE_ITEM_KEY] = editableItem.value
    }

    companion object {
        private const val EDITABLE_ITEM_KEY = "editableItem"
        private const val ORIGINAL_ITEM_KEY = "originalItem"
        private const val DATA_SET_KEY = "dataSet"
        // TODO: delete if not needed private const val LOCALE_TAG = "localeTag"

        fun fromSavedState(savedStateHandle: SavedStateHandle): EditItemScreenUIContent? {
            val savedEditableItem: EditableItem? = savedStateHandle[EDITABLE_ITEM_KEY]
            val savedOriginalItem: EditableItem? = savedStateHandle[ORIGINAL_ITEM_KEY]
            val savedDataSet: DataSet? = savedStateHandle[DATA_SET_KEY]
            // TODO: delete val savedLocaleTag: String? = savedStateHandle[LOCALE_TAG]
            if (savedEditableItem != null && savedOriginalItem != null && savedDataSet != null /* TODO && savedLocaleTag != null */) {
                return EditItemScreenUIContent(
                    mutableStateOf(savedEditableItem),
                    savedOriginalItem,
                    savedDataSet
                    // TODO delete Locale.forLanguageTag(savedLocaleTag)
                )
            } else {
                return null
            }
        }
    }
}

data class EditSourceScreenUIContent(
    val editableSource: MutableState<EditableSource>,
    val originalSource: EditableSource,
    val frozenLocale: Locale,
) {
    fun saveState(savedStateHandle: SavedStateHandle) {
        saveEditableSourceState(savedStateHandle)
        savedStateHandle[ORIGINAL_SOURCE_KEY] = originalSource
        savedStateHandle[LOCALE_TAG] = frozenLocale.toLanguageTag()
    }

    // This is a separate function to minimise the amount of work done after every user edit.
    fun saveEditableSourceState(savedStateHandle: SavedStateHandle) {
        savedStateHandle[EDITABLE_SOURCE_KEY] = editableSource.value
    }

    companion object {
        private const val EDITABLE_SOURCE_KEY = "editableSource"
        private const val ORIGINAL_SOURCE_KEY = "originalSource"
        private const val LOCALE_TAG = "localeTag"

        fun fromSavedState(savedStateHandle: SavedStateHandle): EditSourceScreenUIContent? {
            val savedEditableSource: EditableSource? = savedStateHandle[EDITABLE_SOURCE_KEY]
            val savedOriginalSource: EditableSource? = savedStateHandle[ORIGINAL_SOURCE_KEY]
            val savedLocaleTag: String? = savedStateHandle[LOCALE_TAG]
            if (savedEditableSource != null && savedOriginalSource != null && savedLocaleTag != null) {
                return EditSourceScreenUIContent(
                    mutableStateOf(savedEditableSource),
                    savedOriginalSource,
                    Locale.forLanguageTag(savedLocaleTag)
                )
            } else {
                return null
            }
        }
    }
}

data class EditDataSetScreenUIContent(
    val editableDataSet: MutableState<EditableDataSet>,
    val originalDataSet: EditableDataSet,
) {
    fun saveState(savedStateHandle: SavedStateHandle) {
        saveEditableDataSetState(savedStateHandle)
        savedStateHandle[ORIGINAL_DATA_SET_KEY] = originalDataSet
    }

    // This is a separate function to minimise the amount of work done after every user edit.
    fun saveEditableDataSetState(savedStateHandle: SavedStateHandle) {
        savedStateHandle[EDITABLE_DATA_SET_KEY] = editableDataSet.value
    }

    companion object {
        private const val EDITABLE_DATA_SET_KEY = "editableDataSet"
        private const val ORIGINAL_DATA_SET_KEY = "originalDataSet"

        fun fromSavedState(savedStateHandle: SavedStateHandle): EditDataSetScreenUIContent? {
            val savedEditableDataSet: EditableDataSet? = savedStateHandle[EDITABLE_DATA_SET_KEY]
            val savedOriginalDataSet: EditableDataSet? = savedStateHandle[ORIGINAL_DATA_SET_KEY]
            if (savedEditableDataSet != null && savedOriginalDataSet != null) {
                return EditDataSetScreenUIContent(
                    mutableStateOf(savedEditableDataSet),
                    savedOriginalDataSet
                )
            } else {
                return null
            }
        }
    }
}

// TODO: Now I've increased the inter-field vertical spacing from 8.dp to 16.dp in the various edit
// screens, this one might look a little cramped by comparison. Come back to this later.
@Composable
fun HomeScreen(
    vm: HomeViewModel,
    navController: NavHostController,
    onEditPriceClick: (HomeScreenUIContent) -> Unit,
    onEditDataSetsClick: (HomeScreenUIContent) -> Unit,
    onEditProductsClick: (HomeScreenUIContent) -> Unit,
    onEditSourcesClick: (HomeScreenUIContent) -> Unit
) {
    // In order to minimise jank, we want the previous UI state to be available during the *very
    // first composition* when this screen is re-entered (e.g. after navigating back from another
    // screen).
    //
    // If the first composition is based on null data, even if we manage to recompose with
    // up-to-date data before the first frame, there can still be visual jank: animated components
    // may animate themselves from their initial "null" size to a "non-null" layout. If the very
    // first composition sees non-null data, there's no animation - which is what we want.
    //
    // This is particularly important when returning from a screen that was overlaid on top of this
    // one (via Navigation's backstack), where the user expects this screen to "still be there" —
    // not to visibly reinitialise.
    //
    // This is addressed by having the ViewModel hold the UI state in a hot flow, so when we
    // return to this composable after having navigated elsewhere, the correct state is available
    // for the very first frame.
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val (loading, uiContent) = uiState

    // TODO: HomeScreenScaffold could take uiContent instead of splitting it out here - that
    // wouldn't be unreasonable, *it* would split stuff out, but it would save boilerplate here. We
    // could almost inline HomeScreenScaffold given how trivial the above code now is, and perhaps
    // we should.
    HomeScreenScaffold(
        navController,
        loading,
        uiContent.dataSet,
        uiContent.dataSetList,
        onSelectedDataSetIdChange = {
            vm.savePreference(SELECTED_DATA_SET_ID_KEY, it)
        },
        uiContent.item,
        uiContent.itemList,
        onSelectedItemIdChange = {
            vm.savePreference(SELECTED_ITEM_ID_KEY, it)
        },
        uiContent.source,
        uiContent.sourceList,
        onSelectedSourceIdChange = {
            vm.savePreference(SELECTED_SOURCE_ID_KEY, it)
        },
        uiContent.priceList,
        uiContent.priceAnalysis,
        onEditPriceClick = { onEditPriceClick(uiContent) } /* TODO DELETE (uiContent)
            vm.setEditPriceScreenStateFromHomeScreenState(uiContent)
            // TODO: I don't know if this random UUID is necessary or helpful or harmful any more,
            // need to experiment/think about this once I finish re-implementing the price edit
            // screen.
            navController.navigate("fullScreenDialog/${UUID.randomUUID()}")
        } */,
        onEditDataSetsClick = { onEditDataSetsClick(uiContent) },
        onEditItemsClick = { onEditProductsClick(uiContent) },
        onEditSourcesClick = { onEditSourcesClick(uiContent) }
    )
}

// TODO: Should this have a (fairly rapid) fade in and/or fade out? I am not sure. It's not a
// massive deal given how little I expect it to actually be visible, but I might use it in other
// situations and it might be a nice little bit of polish. If we do fade, remember it probably needs
// to be quick, since it won't even start to fade in until ~150ms has elapsed, and the query could
// return any millisecond now and the scrim disappear before it even got to full opacity. It might
// be that since the scrim is translucent, it looks OK to just pop in. We could also *force* the
// scrim to last for at least the (short, 80ms?) fade in time, but that feels ridiculous -
// especially since it is then only just visible at full "intensity" for one frame maybe before
// disappearing, and we're adding extra slowdown to the app (albeit it might *feel* smoother), and
// we have complex logic to deal with this already unlikely case. I suspect for this specific app
// this is overkill.
@Composable
fun ScrimWithSpinner(visible: Boolean, delayMillis: Long? = null) {
    if (visible) {
        var showScrim by remember { mutableStateOf(false) }

        if (delayMillis != null) {
            LaunchedEffect(Unit) {
                delay(delayMillis)
                showScrim = true
            }
        } else {
            showScrim = true
        }

        if (showScrim) {
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = {
                    Log.d("MyApp", "ODR")
                    // We are trying to emulate the user pressing the back button here.
                    // navController.popBackStack() empirically doesn't work, I think because it's for
                    // our internal back stack. The idea is that if the activity wasn't blocked by the
                    // spinner, the user could go back to some other activity (outside our app,
                    // probably), and we should still allow that while the spinner is up.
                    // TODO: Do we need to debounce this? Set a flag to say we've called
                    // onBackPressed() and don't call again if that flag is set? It probably
                    // wouldn't hurt even if it may not actually be necessary.
                    dispatcher?.onBackPressed()
                },
                properties = PopupProperties(
                    focusable = true, // prevent touches from going through
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)), // TODO: What would MD3 say for color and opacity?
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: Are the defaults here OK?
                    CircularProgressIndicator()
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// TODO: Function might be misnamed if we introduce navigation drawer, but I probably want to refactor a lot of the composables anyway in order to get away from gigantic massively independent functions.
fun HomeScreenScaffold(
    navController: NavHostController,
    loading: Boolean,
    dataSet: DataSet?,
    dataSetList: List<DataSet>,
    onSelectedDataSetIdChange: (Long) -> Unit,
    item: Item?,
    itemList: List<Item>,
    onSelectedItemIdChange: (Long) -> Unit,
    source: Source?,
    sourceList: List<Source>,
    onSelectedSourceIdChange: (Long?) -> Unit,
    priceList: List<Price>,
    priceAnalysis: PriceAnalysis,
    onEditPriceClick: () -> Unit,
    onEditDataSetsClick: () -> Unit,
    onEditItemsClick: () -> Unit,
    onEditSourcesClick: () -> Unit,
) {
    // TODO: Navigation drawer is being deprecated in favour of expanded navigation rail in Material
    // 3 Expressive from May 2025. However, it appears to be a rotten fit for my requirements here -
    // it wants (in its non-expanded form) to be permanently on screen, and I don't have the space,
    // and it seems to be intended for "a few" designer-selected things, not user-defined
    // categories. It also seems to want to live at the bottom of the screen on a portrait
    // smartphone layout. So I am going to stick with the navigation drawer for now.
    // TODO: rememberDrawerState seems to persist across rotations, which feels a bit odd to me -
    // given how we seem to be expected to treat e.g. dropdowns, I'd have expected the drawer to
    // close. Should we try to force it to close on a rotation or just accept this default
    // behaviour?
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var menuExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // TODO: I have tried to get the dimensions right as per M3 specs here, but I'm not that
    // confident. Although I think I have followed the font size/style advice, I am not sure it
    // doesn't look weird - it would maybe be good to e.g. compare with a modern-ish version of
    // GMail and see what that looks like. Playing with Material Files, I do wonder if the desired
    // effect is just that the background of the drawer does go "behind" the top and bottom system
    // bars but they continue to draw on top - in which case I probably can achieve this, if I
    // get rid of my window insets or whatever at the very top level of my NavHost and move it into
    // individual screens, so this screen can have full screen for the drawer and apply the insets
    // to everything else.
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // TODO: Hard-coding this to 2/3 of the screen width feels a bit of a hack, but I really
            // don't like the default behaviour of it taking the full screen width. If nothing else,
            // that makes how to dismiss it feel less discoverable.
            // TODO: Probably irrelevant on a phone, but we should maybe cap the width at 360.dp, which is MD3 specified width.
            ModalDrawerSheet(
                modifier = Modifier
                    .wrapContentWidth()
                    .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 2f / 3f)
            ) {
                // TODO: Probably need to set font style/colour for this "heading"
                Column {
                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .padding(start = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Collections",
                            //color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleSmall,
                            //modifier = Modifier.padding(start = 16.dp).height(56.dp) // TODO!?
                        ) // TODO: 16dp right/necessary?
                    }
                    LazyColumn {
                        items(dataSetList) { item ->
                            val selected = dataSet?.id == item.id
                            // TODO: Should these have some kind of generic bullet-style icon? The half
                            // cut off "labels" gmail screenshot in m3 docs hints at this. But it might
                            // also look a bit weird to have these.
                            NavigationDrawerItem(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .height(56.dp),
                                label = {
                                    Text(
                                        item.name,
                                        // color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    coroutineScope.launch { onSelectedDataSetIdChange(item.id); drawerState.close() }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) {

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Red /* TODO DEBUG HACK */),
            topBar = {
                TopAppBar(
                    title = { Text(dataSet?.name ?: "") }, // TODO: better null handling?
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open drawer"
                            ) // TODO: tweak description?
                        }
                    },
                    actions = {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            ) // TODO: tweak description?
                        }

                        DropdownMenu(
                            expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                            // TODO: There is maybe an argument that "Manage" might be better than
                            // "Edit" because it carries a stronger suggestion of adding/removing
                            // products (or whatever) rather than just tweaking their details. But not
                            // sure. Edit is shorter! And while edit is a *tiny* bit tech jargon it is
                            // widely accepted in phone apps, while "manage" feels vaguely corporate.
                            MyDropdownMenuItem(text = { Text("Edit collections") }, onClick = {
                                menuExpanded = false
                                onEditDataSetsClick()
                            })
                            MyDropdownMenuItem(
                                text = { Text("Edit products") },
                                enabled = dataSet != null,
                                onClick = {
                                    menuExpanded = false
                                    onEditItemsClick()
                                })
                            MyDropdownMenuItem(
                                text = { Text("Edit stores") },
                                enabled = dataSet != null,
                                onClick = {
                                    menuExpanded = false
                                    onEditSourcesClick()
                                })
                            MyDropdownMenuItem(text = { Text("Settings") }, onClick = {
                                menuExpanded = false
                                // TODO: This should probably be done via a callback function provided
                                // to HomeScreen and passed through to us, and this function should
                                // probably *not* have the navController directly available. We might
                                // pass a *route* back but the function passed to us would actually
                                // invoke navController.navigate().
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
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(screenBorder) // TODO: MAYBE THIS SHOULD ONLY BE HORIZONTAL - MY YELLOW DEBUG BACKGROUND IS MAYBE GIVING ME A MISLEADING IDEA AND WE MAYBE SHOULDN'T HAVE VERTICAL SPACE BETWEEN TOP BAR AND TOP OF CONTENT

            ) {

                MainScreen(
                    item = item,
                    itemList = itemList,
                    onSelectedItemIdChange = onSelectedItemIdChange
                ) // TODO: rename this

                Spacer(
                    modifier = Modifier
                        .height(
                            8.dp
                        )
                        .fillMaxWidth()
                        .background(color = Color.Red) // TODO DEBUG HACK
                )

                if (dataSet != null) {
                    Log.d("MyApp", "HSS dataSet $dataSet")
                    Log.d("MyApp", "HSS item $item")
                    ItemSourceInfo(
                        dataSet = dataSet,
                        item = item,
                        source = source,
                        sourceList = sourceList,
                        onSelectedSourceIdChange = onSelectedSourceIdChange,
                        itemPriceList = priceList,
                        onEditPriceClick = onEditPriceClick
                    )

                    Spacer(
                        modifier = Modifier.height(
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
                    // TODO: We may want the denominator to be user-selectable in this list header, if so it should probably offer all the user's selected units of the right type, as the unit price dropdown on ItemStoreInfo does.
                    val locale = LocalConfiguration.current.locales[0]
                    val currencyFormat = remember(dataSet, locale) {
                        getCurrencyFormat(dataSet, locale)
                    }

                    // TODO: I think this is OK but check later: we use "prefix or suffix" here because
                    // although the prefix or suffix nature of a currency symbol in a locale matters in
                    // some other places, here it is appearing in isolation *without* a price next to
                    // it.
                    // TODO: Arguably we could/should use remember or something like that to store the header currency/unit string and avoid rederiving it all the time, albeit it isn't that involved and we are already doing that with the currencycode, but still, we could move this into that remember block and not expose the currency code outside it or something
                    // TODO: I'm hacking this together out of old prototype code but as this evolves we need to be "neater" about how we cope with generating the denominator part of the unit price header on the list when the list is empty - or just not showing the list at all in that case (which might make more sense, and maybe we already *do*, I'm not sure right now)
                    val header = listOf(
                        "Store",
                        "${currencyFormat?.prefix ?: currencyFormat?.suffix ?: ""}/${priceAnalysis.augmentedPriceList.firstOrNull()?.unitPrice?.denominator?.symbol ?: "TODO"}",
                        "Notes"
                    )
                    // TODO: With the £/100g header, it is arguably redundant/incorrect to include the £ on the data values, but I think it's a reasonable compromise for readability and use by non-technical users.
                    // TODO: We might be rebuilding this list every recomposition, I really don't have a clue, as I already noted we should really
                    // not be building this list at all but working directrly with augmentedPriceList or something
                    val data = priceAnalysis.augmentedPriceList.map { augmentedPrice ->
                        listOf(
                            augmentedPrice.sourceName,
                            formatPrice(augmentedPrice.unitPrice.numerator, dataSet, locale),
                            "TODO" // TODO: We probably don't want to show "notes", but we may want to show some icons or just possibly any judgement on the price or something in a third column
                        )
                    }
                    /* TODO DELETE
                    listOf(
                        "Tesco", "£2.13", "Tesco Finest is actually cheapest"
                    ),
                    listOf("Sainsbury's Local", "£2.94", ""),
                    listOf("Asda", "£2.08", "KTC brand"),
                    listOf("Iceland", "£2.38", ""),
                    // …
                )
                    */

                    // TODO: Price column should be right-aligned, of course
                    // TODO: This is based on an early hack and we should probably not be generating miscellaneous lists but instead just working directly with our AugmentedPrice objects or something
                    Card(
                        modifier = Modifier
                            //.weight(1f, fill=false) // only component with weight, so fills all remaining space
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Box(
                            modifier = Modifier.padding(
                                horizontal = 8.dp, vertical = 12.dp
                            )
                        ) {
                            // TODO: This list may not need dividers between items if it is now simpler and doesn't show "Notes", which could have pushed us into multi-line items fairly easily, whereas now only very long store names or very big fonts on very small screens are likely to do it.
                            DataTable(
                                header = header, rows = data,
                                // TODO: Manually tweaking these weights is annoying and risks not working for some user's set of sources. Being clever may help, but it's awkward given the somewhat free form source and the very free form notes.
                                columnWeights = listOf(1.7f, 1f, 1f)
                            )
                        }
                    }
                }
            }
        }
    }

    // In an ideal world the scrim with spinner would cover only the lower two cards and leave the
    // rest of the home screen functional. I experimented with doing this and although I think I
    // could have made it work, it felt incredibly brittle and likely to go wrong depending on
    // Android version and things like edge-to-edge and the SDK implementing that differently on
    // different Android versions etc. Given how rarely we expect the spinner to appear at all (and
    // therefore also how little testing it would get), it seemed best to go with this relatively
    // simple full screen spinner.
    //
    // Note that we do not pass a delayMillis parameter here; the delay before the scrim appears
    // is implemented in the logic which sets the loading flag, so as soon as loading is true, we
    // want the scrim.
    // TODO: Is this in right place in hierarchy wrt navigation drawer?
    ScrimWithSpinner(visible = loading)
}

// TODO: ChatGPT magic but I think I do mostly understand
/*
@Composable
fun rememberSyncedTextFieldValue(modelState: String): State<TextFieldValue> {
    val tfv = remember { mutableStateOf(TextFieldValue(modelState)) }
    if (tfv.value.text != modelState) {
        tfv.value = TextFieldValue(modelState)
    }
    return tfv
}
*/
// TODO: This variant means we have to allow mutability, but can use "by" in callers
@Composable
fun rememberSyncedTextFieldValue(modelState: String): MutableState<TextFieldValue> {
    val tfv = remember { mutableStateOf(TextFieldValue(modelState)) }

    // If the model changes from the outside, resync tfv
    if (tfv.value.text != modelState) {
        tfv.value = TextFieldValue(modelState)
    }

    return tfv
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// TODO: I was thinking this screen would show the price history, but I am cooling on that. Not
// quite sure where we would show it, but I am not sure it's something we want cluttering up this
// in-store edit screen, or encouraging people to go into this "live edit" view where they might
// accidentally change data just to see the history. Maybe this could go on the overflow menu on
// home screen if we have all three things selected?
// TODO: I could maybe re-use the (bundled up in a composable) unit price display only but with
// variable unit on this screen, as it will might be useful to the user as a confirmation of the
// unit price on the shelf. On the other hand, it might just be extra clutter on a screen where
// the user is editing.
fun EditPriceScreen(
    vm: EditPriceViewModel,
    navController: NavHostController,
    requestClose: () -> Unit
) {
    val uiContent = vm.uiContent

// TODO: Some of this remember stuff should maybe move into the ViewModel

    val saveStatus by vm.generalEditScreenViewModel.saveStatus.collectAsStateWithLifecycle()

    fun onPackSizeOrPriceChange() {
        // On the first change to the pack size or price, we set the "to confirm" switch to true, on
        // the grounds that if the user is changing these values, they must be getting them from
        // somewhere and the assumption is that they have the actual current price/pack in front of
        // them. (We don't do this if they edit the notes; it's conceivable they are for example
        // trying the product at home and making a note that a certain brand isn't very nice and not
        // to consider it as acceptable in future.) We only do this on the first change so we don't
        // fight with the user if they toggle this back off afterwards.
        // TODO: We might want to gate this logic behind a Settings option, i.e. have an option to
        // let the confirm always stay off unless the user explicitly turns it on.
        if (!vm.firstPackSizeOrPriceChangeOccurred) {
            vm.setUIContentEditablePrice(uiContent.editablePrice.value.copy(toConfirm = true))
            vm.firstPackSizeOrPriceChangeOccurred = true
        }
    }

    // TODO: We could possibly try to "animate" the problematic text
    // field we just focused (e.g. pulse its border colour) to draw
    // attention to it further, but this feels surprisingly fiddly and I
    // am not sure it's necessary. My inclination is to leave this for
    // now and let the code settle down first before maybe trying to add
    // it.
    GeneralEditScreen(
        vm = vm.generalEditScreenViewModel,
        navController = navController,
        title = { Text("TODO: Dialog Title") }, // TODO: Do not use "Edit price" (even though we call it that internally, because it's the "price" table), you can also eg edit pack size and probably a free text notes field etc
        isDirty = {
            uiContent.editablePrice.value.copy(toConfirm = false) !=
                    uiContent.originalPrice.copy(toConfirm = false)
        },
        validateForSave = { vm.validateForSave() },
        performSave = { vm.performSave() },
        onIdle = {},
        requestClose = requestClose,
    ) {
        // TODO: Probably have a note elsewhere but these two disabled text fields are perhaps a bit ugly
        // and maybe not MD3-ish. Moving these into the title and subtitle of the top bar may be the
        // way to go. Or something else, but I do feel they look a bit ugly and maybe even confusing,
        // as the user might wonder "how" they could become enabled.
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Product") },
            value = uiContent.item.name,
            enabled = false,
            onValueChange = {})

        // TODO: Maybe pull this 16.dp form vertical spacing value out into a named constant.
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Store") },
            value = uiContent.source.name,
            enabled = false,
            onValueChange = {})

        Spacer(modifier = Modifier.height(16.dp))

        val units: List<MeasureUnit> =
            remember(uiContent.dataSet, uiContent.item.defaultUnit.quantityType) {
                getRelevantMeasureUnits(
                    uiContent.dataSet,
                    uiContent.item.defaultUnit.quantityType,
                    includeDisplayOnly = false
                )
            }
        var packSizeNumber by rememberSyncedTextFieldValue(
            uiContent.editablePrice.value.measureValue
        )
        // TODO: This box could just be around the actual "Pack size" text field, but I think it
        // makes sense for it to also cover the supportingText showing the actual problem. That
        // visually requires it to cover the whole screen width.
        // TODO: I wonder if this screen is actually a bit vertically squashed together, now I see
        // that I "need" offset = 4.dp here instead of the current default 6.dp. It might be I
        // should increase the vertical spacing of the components on this screen and then make this
        // 6.dp.
        BaseValidatedTextField(
            value = packSizeNumber.text,
            validationRules = vm.packSizeValidationRules,
            validationRulesKey = uiContent.editablePrice.value.measureUnit.id,
            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditPriceViewModel.EditableField.PACK_SIZE,
            errorHighlightOffset = 4.dp,
        ) { validationResult, interactionSource, scrollToFocusableHandle ->
            Row {
                // TODO: Using weight to size the components is also sucky, since we really
                // just want "a reasonable fixed size" for the unit with
                // the product taking whatever's left, but this will do for now.
                NumericTextField(
                    label = { Text("Pack size") },
                    value = packSizeNumber,
                    onValueChange = {
                        packSizeNumber = it
                        if (uiContent.editablePrice.value.measureValue != it.text) {
                            vm.setUIContentEditablePrice(
                                uiContent.editablePrice.value.copy(
                                    measureValue = it.text
                                )
                            )
                            onPackSizeOrPriceChange()
                        }
                    },
                    enabled = saveStatus.isNotBusy(),
                    isError = validationResult != null,
                    modifier = Modifier
                        .weight(1f)
                        .validationFocusRequester(scrollToFocusableHandle),
                    interactionSource = interactionSource
                )

                Spacer(modifier = Modifier.width(8.dp))

                MyExposedDropdownMenuBox(
                    enabled = saveStatus.isNotBusy(),
                    selectedId = uiContent.editablePrice.value.measureUnit.id,
                    onValueChange = {
                        val measureUnit = MeasureUnit.fromValue(it)
                        devCheck(measureUnit != null) {
                            "Expected non-null measureUnit to be selected; got $it"
                        }
                        if (uiContent.editablePrice.value.measureUnit != measureUnit!!) {
                            vm.setUIContentEditablePrice(
                                uiContent.editablePrice.value.copy(
                                    measureUnit = measureUnit
                                )
                            )
                            onPackSizeOrPriceChange()
                        }
                    },
                    label = { Text("Unit") },
                    items = units,
                    modifier = Modifier.weight(0.5f),
                    getId = { it.id },
                    getLabel = { it.symbol },
                )
            }

            if (validationResult != null) {
                SupportingText(
                    text = validationResult, isError = true,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp)
                    //.background(Color.Cyan) // TODO HACK
                )

            }
        }

        //Spacer(modifier = Modifier.height(500.dp))
        Spacer(modifier = Modifier.height(16.dp))

        var packPrice by rememberSyncedTextFieldValue(uiContent.editablePrice.value.price)
        val currencyFormat = vm.currencyFormat

        BaseValidatedTextField(
            value = packPrice.text,
            validationRules = currencyFormat.validationRules,
            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditPriceViewModel.EditableField.PRICE,
            errorHighlightOffset = 4.dp,
        ) { validationResult, interactionSource, scrollToFocusableHandle ->
            NumericTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .validationFocusRequester(scrollToFocusableHandle),
                label = { Text("Pack price") },
                value = packPrice,
                prefix = textOrNull(currencyFormat.prefix),
                suffix = textOrNull(currencyFormat.suffix),
                // TODO: Is it correct to right-align like this? I will assume it is for now.
                // Maybe there's an argument since the unit on the pack size is pseudo-suffixy,
                // we should right-align the pack size - but I think that might look ugly. But
                // maybe that means this looks ugly. But maybe it's different if you're used to
                // the currency symbol being on the right. Or maybe the currency symbol should
                // be on the left in this kind of form *anyway*. Very hard for me to know. Maybe
                // wait for user feedback?
                textStyle = if (currencyFormat.prefix == null && currencyFormat.suffix != null) LocalTextStyle.current.copy(
                    textAlign = TextAlign.End
                ) else LocalTextStyle.current,
                onValueChange = {
                    packPrice = it
                    if (uiContent.editablePrice.value.price != it.text) {
                        vm.setUIContentEditablePrice(uiContent.editablePrice.value.copy(price = it.text))
                        onPackSizeOrPriceChange()
                    }
                },
                enabled = saveStatus.isNotBusy(),
                isError = validationResult != null,
                supportingText = textOrNull(
                    validationResult,
                    color = MaterialTheme.colorScheme.error
                ),
                interactionSource = interactionSource,
            )
        }

        // TODO: FWIW I have a Grok conversation saved where it offered a TextMeasure class that
        // would give a width for an arbitrary string and we could use something like that to
        // size fields like this and/or the unit (albeit both have some extra window furniture -
        // but we could for example compute a "notional text size" taking font size into account
        // for " £  1234.00     " (spaces approximating margins/space for icons to pop in) and "
        // litre    " (ditto) and use those sizes as the weights - we don't want both things
        // fixed size as they won't fill the screen then, and we probably don't want one
        // "minimal" and the other filling rest of space - but then again, if you do that, a
        // fixed ratio is probably more or less the same since both will expand with font size
        // just the same, so maybe that would be pointless
        // TODO: TEMP NOTE PRESERVED FROM NUMERICTEXTFIELD TO BE MOVE INTO VALIDATIONTHING REWRITE We don't need a validationRulesKey here because the currency validation rules
        // cannot change while we are editing. They depend only on our DataSet and our
        // frozen locale.

        // We don't show the switch if this is the first price for an item and source; the price is confirmed, otherwise
        // why are we entering it?
        if (uiContent.editablePrice.value.id != 0L) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // TODO: WORDING FOR BOTH THESE MIGHT WANT TWEAKING
                    Text(
                        text = "Confirm pack size and price",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "The above details are correct right now",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    enabled = saveStatus.isNotBusy(),
                    checked = uiContent.editablePrice.value.toConfirm,
                    onCheckedChange = {
                        vm.setUIContentEditablePrice(
                            uiContent.editablePrice.value.copy(
                                toConfirm = it
                            )
                        )
                    })
            }
        } else {
            devCheck(uiContent.editablePrice.value.toConfirm) {
                "Expected toConfirm to be true as this is the first price, but it's false"
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Can/should I do something to scroll the screen when focus enters this and the caret is half-hidden?
        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Notes") },
            value = uiContent.editablePrice.value.details,
            onValueChange = {
                vm.setUIContentEditablePrice(uiContent.editablePrice.value.copy(details = it))
            },
            enabled = saveStatus.isNotBusy(),
        )
    }

    // TODO: It is probably hard, but *if* the a field with a validation error is currently
    // focused, it would be nice to animate the error highlight box and scroll to it if
    // necessary but *not* jump the focus to a different field with an error.
}

@Composable
fun SupportingText(text: String, isError: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text, modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// TODO: Rename? This is not actually a ViewModel, though it plays a similar role (I think).
// TODO: Can/should this be handled via rememberSaveable inside GeneralEditScreen? I think in some
// sense this saving-duration kind of data should be in the caller's ViewModel (via composition). In
// practice, especially given that we "trap" the user on the edit screen that isn't so important.
// There might be considerations around app death and resurrection and being in the caller's
// ViewModel (if they remember to serialise us) might help us survive, but "the process of actually
// saving" cannot be serialised so even if a save somehow takes ages and that isn't actually
// indicative of a serious problem, will it matter that our state has been serialised to a bundle!?
// I need to thinka bout this later when it's maybe clearer.
class GeneralEditScreenViewModel {
    val saveStatus = SyncedStateEvent(SaveStatus.Idle)
    var saveAttempted: MutableState<Boolean> = mutableStateOf(false)
}

fun runGeneralEditScreenOperation(
    vm: GeneralEditScreenViewModel,
    coroutineScope: CoroutineScope,
    isSafeToPerform: suspend () -> Boolean,
    perform: suspend () -> Unit,
) {
    coroutineScope.launch {
        if (isSafeToPerform()) {
            vm.saveStatus.update(SaveStatus.Busy)
            try {
                Log.d("MyAppRGE", "runGeneralEditScreenOperation about to call perform")
                perform()
                // delay(5000) // TODO HACK - DONE AFTER PERFORM SO IT GETS A CHANCE TO SET SAVING/DELETING FLAG TO TRUE
                vm.saveStatus.update(SaveStatus.Success)
            } catch (e: Exception) {
                Log.d("MyAppRGE", "runGeneralEditScreenOperation caught exception")
                vm.saveStatus.update(SaveStatus.Error) // TODO: can/should we preserve e and show it to user in UI?
            }
        }
    }
}

@Composable
fun GeneralEditScreen(
    vm: GeneralEditScreenViewModel,
    navController: NavHostController,
    title: @Composable () -> Unit,
    isDirty: () -> Boolean,
    validateForSave: suspend () -> Boolean,
    performSave: suspend () -> Unit,
    onIdle: () -> Unit,
    requestClose: () -> Unit,
    content: @Composable () -> Unit
) {
    val saveStatus by vm.saveStatus.collectAsStateWithLifecycle()
    Log.d("MyAppRGE", "GeneralEditScreen saveStatus=$saveStatus")

    val isBusy = !saveStatus.isNotBusy() // TODO: Add a isBusy()? get rid of isNotBusy()?
    var showConfirmDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var showBusySnackbar by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var saving by rememberSaveable { mutableStateOf(false) }

    // TODO: We may need to make this available to the content() so it can use it for scrolling to highlight errors, or it may be that we don't need it here at all and it can be entirely in the content()
    val scrollState = rememberScrollState()

    val requestCloseDebounced = dropUnlessResumed {
        requestClose()
    }

    fun requestDismiss() {
        if (isDirty()) {
            showConfirmDiscardDialog = true
        } else {
            requestCloseDebounced()
        }
    }

    BackHandler {
        if (!isBusy) {
            requestDismiss()
        } else {
            // I've discussed this with LLMs and it's not clear if - from a UI perspective - we
            // should do this or not, but I'll go with it for now.
            showBusySnackbar = true
        }
    }

    LaunchedEffect(Unit) {
        // TODO: I have thrown in a buffer() here voodoo-style based on an actual observed problem
        // in the case below. Come back to this later.
        vm.saveStatus.events.buffer().collect { event ->
            when (event) {
                SaveStatus.Busy -> {
                    // We expect the operation to complete quickly so we don't want the visual distraction
                    // of a progress indicator appearing straight away. Let the progress indicator kick
                    // in after a short delay if we're still here waiting.
                    delay(spinnerDelayMillis)
                    // The state might not be busy any more, so check first before updating to avoid a race condition.
                    if (vm.saveStatus.state.value == SaveStatus.Busy) {
                        vm.saveStatus.update(SaveStatus.BusyForAWhile)
                    }
                }

                else -> {}
            }
        }
    }

    // TODO: ChatGPT magic more or less
    LaunchedEffect(Unit) {
        // TODO: We use buffer here because we want to update() in the error case while we are
        // already collecting; we get a deadlock otherwise. I *think* this is OK, but be good to
        // come back to it later.
        vm.saveStatus.events.buffer().collect { event ->
            when (event) {
                SaveStatus.Idle -> {
                    Log.d("MyAppRGE", "collected idle")
                    saving = false
                    Log.d("MyAppRGE", "set saving to false")
                    onIdle()
                    Log.d("MyAppRGE", "called onIdle")
                }

                SaveStatus.Success -> {
                    Log.d("MyAppRGE", "collected success")
                    requestCloseDebounced()
                }

                SaveStatus.Error -> {
                    Log.d("MyAppRGE", "collected error")
                    vm.saveStatus.update(SaveStatus.Idle)
                    Log.d("MyAppRGE", "set state to idle")
                    showErrorDialog = true
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
                    IconButton(enabled = !isBusy, onClick = { requestDismiss() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                title = title,
                actions = {
                    // TODO: Just possibly instead of always calling onSave, onClick should call
                    // isDirty first and just dismiss without saving if it returns false - but that
                    // might be confusing and it's maybe optimising a corner case
                    TextButton(enabled = !isBusy, onClick = {
                        // TODO: I think the layout here is good and in fact better than it was,
                        // but note that unlike the EditPrice stuff this is being based on, here
                        // updateOrInsertFoo() does not (and probably cannot, since it's an
                        // internal detail here and not exposed) be messing with updating
                        // saveStatus.
                        vm.saveAttempted.value = true
                        runGeneralEditScreenOperation(
                            vm = vm,
                            coroutineScope = coroutineScope,
                            isSafeToPerform = validateForSave,
                            perform = {
                                saving = true
                                delay(5000) // TODO HACK
                                performSave()
                            }
                        )
                    }) {
                        // We do get rid of the spinner when we reach "success"; this might cause a
                        // small but legitimate visual glitch as the disabled "Save" button
                        // re-enables, but it feels confusing to close while showing the spinner,
                        // since it might suggest to the user we *haven't* finished but are for some
                        // reason closing anyway.
                        if (saving && saveStatus == SaveStatus.BusyForAWhile) {
                            SmallCircularProgressIndicator()
                        } else {
                            Text("Save")
                        }
                    }
                },
            )
        },
        snackbarHost = {
            // TODO: Make sure we have generic code to show saving please wait message if back pressed during save
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                // TODO: MD3 spec also has surfaceContainer background for "on-scroll", I am
                // struggling to find any non-LLM explanations here, but *maybe* *if we have
                // scrolled away from the top* we should change the background to surfaceContainer
                .background(/* Color.Cyan TODO TEMP FOR DEBUG, SHOULD BE */ MaterialTheme.colorScheme.surface) // because this is a full-screen dialog
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = fullScreenDialogBorder)
                .verticalScroll(scrollState)
        ) {
            // The two vertical spacers here are to create a vertical border which we *can* draw
            // over using ErrorHighlightBox. (If we add "vertical = fullScreenDialogVerticalBorder"
            // to the parent Column's .padding(), we can't draw over it.) I have been unable to find
            // a really clear answer if we should have a vertical space between the top app bar and
            // the first "real" thing (e.g. a TextField) in the content, so I am going to let the
            // need to be able to draw an ErrorHighlightBox around the first thing in the content
            // make the decision for me. We apply this here for consistency across all dialogs. (In
            // practice the top app bar's background and the content's background are the same, so
            // it isn't normally that noticeable either way. You can see the difference more easily
            // by using a non-standard background for the dialog.)
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
            content()
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
        }
    }

    if (showConfirmDiscardDialog) {
        // I copied the wording of this dialog directly from a screenshot in the M3 documentaion.
        AlertDialog(
            title = { Text("Discard unsaved changes?") },
            text = { Text("You have changes that won't be saved if you close.") },
            onDismissRequest = { showConfirmDiscardDialog = false },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDiscardDialog = false
                }) { Text("Keep editing") }
            },
            confirmButton = {
                TextButton(onClick = { requestCloseDebounced() }) {
                    Text(
                        "Discard"
                    )
                }
            },
        )
    }

    // TODO: Do we want to "re-use" this dialog for e.g. delete errors too? If so, how will the
    // change of wording be addressed? We may want to rename showErrorDialog to something more
    // suggestive depending on what kinds of error this code handles.
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
                TextButton(onClick = { showErrorDialog = false }) { Text("OK") }
            }
        )
    }

    LaunchedEffect(showBusySnackbar) {
        if (showBusySnackbar) {
            coroutineScope.launch {
                // TODO: It's probably OK to just say "Busy" (we could be saving, or generically
                // busy doing something like a delete which we don't control directly) but maybe
                // we need to make this string more controllable.
                snackbarHostState.showSnackbar("Busy, please wait...")
                showBusySnackbar = false
            }
        }
    }
}

@Composable
fun GeneralEditAndDeleteScreen(
    vm: GeneralEditScreenViewModel,
    navController: NavHostController,
    title: @Composable () -> Unit,
    isDirty: () -> Boolean,
    validateForSave: suspend () -> Boolean,
    performSave: suspend () -> Unit,
    onIdle: () -> Unit,
    requestClose: () -> Unit,
    deleteConfirmationDetails: Triple<Boolean, @Composable () -> Unit, @Composable () -> Unit>?,
    requestDelete: suspend () -> Unit,
    requestDeleteCancel: () -> Unit,
    content: @Composable (showDeleteSpinner: Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var deleting by rememberSaveable { mutableStateOf(false) }
    val saveStatus by vm.saveStatus.collectAsStateWithLifecycle()

    GeneralEditScreen(
        vm = vm,
        navController = navController,
        title = title,
        isDirty = isDirty,
        validateForSave = validateForSave,
        performSave = performSave,
        onIdle = {
            deleting = false
            onIdle()
        },
        requestClose = requestClose,
    ) {
        content(
            deleting && saveStatus == SaveStatus.BusyForAWhile,
            // TODO: NEEDED? deleting = deleting,
            /* TODO DELETE
            { isSimpleDelete: Boolean, dialogTitle: @Composable () -> Unit, dialogText: @Composable () -> Unit ->
                showDeleteConfirmDialog = true
                isSimpleDeleteTODO = isSimpleDelete
                dialogTitleTODO = dialogTitle
                dialogTextTODO = dialogText
            }, */

        )
    }
    // TODO (isSimpleDelete: Boolean, dialogTitle: @Composable () -> Unit, dialogText: @Composable () -> Unit),

    if (deleteConfirmationDetails != null) {
        val isSimpleDelete = deleteConfirmationDetails.first // TODO: Rename "showWarningIcon"?
        val dialogTitle = deleteConfirmationDetails.second
        val dialogText = deleteConfirmationDetails.third

        AlertDialog(
            icon = if (isSimpleDelete) null else {
                {
                    Icon( // TODO: Do I need to set the size of this icon explicitly?
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            title = dialogTitle,
            text = dialogText,
            onDismissRequest = { requestDeleteCancel() },
            dismissButton = {
                TextButton(onClick = { requestDeleteCancel() }) { Text("Cancel") }
            },
            confirmButton = {
                TextButton(onClick = {
                    requestDeleteCancel() // TODO: is it confusing to do this? rename "request no dialog" or similar??
                    runGeneralEditScreenOperation(
                        vm = vm,
                        coroutineScope = coroutineScope,
                        isSafeToPerform = { true },
                        perform = {
                            deleting = true
                            delay(5000) // TODO HACK
                            //throw IllegalStateException("TODO")
                            requestDelete()
                        }
                    )
                }) { Text("Delete" /* TODO? Would only want to do this for cascading deletes, but even so I'm not sure I like it , color = MaterialTheme.colorScheme.error */) }
            },
        )
    }
}

@Composable
fun EditItemScreen(
    vm: EditItemViewModel,
    navController: NavHostController,
    requestClose: () -> Unit
) {
    val uiContent = vm.uiContent

    // TODO: If itemReferenceCount > 0, we should probably disable changing the quantity type, maybe
    // with an explanatory note (although that feels maybe confusing). I think as per wafflings
    // elsewhere if the user wants to change it once data exists, they have to delete the product
    // (thereby deleting that data) and add it again. We could perhaps not use a radio button and
    // just show a simple text (probably *not* a TextField, just because that's probably not the
    // right look, just as it isn't elsewhere even though I've done it) "statement" that the product
    // is sold by weight, in this case (while still showing radio button if itemReferenceCount is 0
    // of course). I am not going to add this now partly as I'd like to mull over the UI choices in
    // background and partly because I don't want to restrict things which might help me investigate
    // the "java.lang.IllegalArgumentException: No enum constant
    // com.example.composetutorial.MeasureUnit.ĭ????" problem. (I don't think that's caused by
    // creating inconsistencies. You can make other things go wrong by creating inconsistencies
    // between the product's quantity type and the existing prices, but you have to navigate around
    // and that crash was triggered immediately on returning via overview menu.)
    val itemReferenceCount by vm.itemReferenceCountFlow.collectAsStateWithLifecycle()
    Log.d("MyApp", "itemReferenceCount $itemReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by vm.generalEditScreenViewModel.saveStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = itemReferenceCount == 0L
    GeneralEditAndDeleteScreen(
        vm = vm.generalEditScreenViewModel,
        navController = navController,
        // TODO: Different title for add vs edit? Title should maybe show data set name?
        title = { Text("TODO: TITLE") },
        isDirty = { uiContent.editableItem.value != uiContent.originalItem },
        validateForSave = { vm.validateForSave() },
        performSave = { vm.performSave() /* ; throw IllegalArgumentException("TODO2") */ },
        onIdle = {},
        requestClose = requestClose,
        deleteConfirmationDetails = if (!showDeleteConfirmDialog) null else Triple(
            isSimpleDelete,
            if (isSimpleDelete) {
                { Text("Delete product?") }
            } else {
                { Text("Delete product and prices?") }
            },
            if (isSimpleDelete) {
                { Text("This product has no associated prices so deleting it will not affect anything else.") }
            } else {
                // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
                { Text("Deleting this product will also delete its store prices. This action cannot be undone.") }
            }
        ),
        requestDelete = { vm.performDelete() },
        requestDeleteCancel = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(uiContent.editableItem.value.name)
        val nameValidationRules by vm.nameValidationRules.collectAsStateWithLifecycle()
        Log.d("MyApp", "nameValidationRules $nameValidationRules")
        ValidatedTextField2(
            label = { Text("Name") },
            value = name,
            maxLength = maxItemNameLength,
            onValueChange = {
                name = it
                vm.setUIContentEditableItem(uiContent.editableItem.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value,
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditItemViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Probably can/should factor out a lot of this radio button stuff which I have just
        // copied and pasted from EditSourceScreen for now.

        // TODO: Can I put these string versions inside QuantityType or won't that play well with i18n?
        val options = listOf(
            Triple(QuantityType.WEIGHT, "Weight", null),
            Triple(
                QuantityType.VOLUME,
                "Volume",
                null,
            ),
            // TODO: Don't be over-eager to have supportingText here - if we don't need it for any of them items that is fine, and we can then avoid this maybe-nonstandardness in this case at least, and revert to the standard item height of 40.dp - "Item" alone may be a fine option, or "Item or group of items" or something like that would probably be a fine option with no supporting text - think carefully about wording but don't assume we need supportingText
            Triple(QuantityType.ITEM, "Item", "Per item or pack of items") // TODO: POOR WORDING FOR BOTH SHORT NAME AND SUPPORTING TEXT? THINK
        )
        var selectedOption = uiContent.editableItem.value.quantityType
        // TODO: This radio group needs to be enabled iff saveStatus.isNotBusy()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)

            // TODO: colors?
            // TODO: elevation???
        ) {
            // We would like to use horizontal padding of 16.dp on this Column, but we don't want
            // the ripple effect on the radio button Rows to "stop" at the left edge of the circular
            // radio buttons. So we have to use 8.dp here and manually apply the remaining 8.dp
            // padding on each individual composable. I am not completely sure this looks great -
            // maybe it's a bit weird the ripple effect is "wider" than everything else - but it's
            // probably OK.
            Column(
                modifier = Modifier
                    // NB: We must do .animateContentSize() *before* .padding(), otherwise the clipping
                    // bounds the former imposes are too tight and will prevent ErrorHighlightBox
                    // drawing correctly.
                    .animateContentSize()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                // TODO: I'm far from sure what typography or colour this caption should have, but this
                // matches the caption on the TextFields so it is probably not a terrible choice. TODO: THIS IS FOR bodySmall - I can't help thinking titleSmall maybe looks better though. I am a bit worried the fonts are all over the place in general, but since MD3 is conspicuously silent outside of some very specific cases it is really hard to know what to do.
                Text(
                    "Product sold by", // TODO: Not necessarily great wording, especially since we also have a specific unit selection further down for weight/volume - not the only aspect, but maybe just "Sold by" would be fine (the whole dialog is about products and probably even says "product" in top app bar)
                    style = MaterialTheme.typography.titleSmall /* bodySmall */,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                //Spacer(modifier = Modifier.height(8.dp))
                options.forEach { (id, name, supportingText) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            //.background(Color.Blue)
                            .clickable {
                                vm.setUIContentEditableItem(
                                    uiContent.editableItem.value.copy(
                                        quantityType = id
                                    )
                                    // TODO: We also need to null-out the default unit or maybe give it a "default default", if this is a *change* of quantityType (not if it's just a reselection of same quantityType)
                                )
                            }
                            .padding(horizontal = 8.dp)
                            .height(48.dp) // 40.dp is MD3 spec but we want extra space for our supporting text while still having some spacing between items
                            //.padding(8.dp) // TODO: ChatGPT value to try to space things out now we have supportingText
                            .semantics {
                                role = Role.RadioButton
                            }, // for TalkBack / screen readers, since this is clickable not the RadioButton
                    ) {
                        RadioButton(
                            selected = (selectedOption == id),
                            onClick = null // TODO onClick
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = name,
                                /* TODO: not sure this looks right: style = MaterialTheme.typography.labelLarge, */ /* TODO: seems to be default anyway: color = MaterialTheme.colorScheme.onSurface */
                            )
                            Log.d("MyApp", "supportingText $supportingText")
                            if (supportingText != null) {
                                Text(
                                    text = supportingText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                if (selectedOption != QuantityType.ITEM) {
                    Spacer(modifier = Modifier.height(8.dp))

                    /* TODO COPY AND PASTE DELETE KEPT JUST IN CASE USEFUL FOR REF BUT PROBABLY NOT
                    var loyaltyPercentage by rememberSyncedTextFieldValue(uiContent.editableSource.value.loyaltyPercentage)
                    // TODO: Can/should we factor out this BaseValidatedTextField+NumericTextField combo?
                    Box(modifier = Modifier.padding(8.dp)) {
                        BaseValidatedTextField(
                            value = loyaltyPercentage.text,
                            validationRules = vm.loyaltyPercentageValidationRules,
                            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
                            validationFlow = vm.saveValidationEvents,
                            validationFlowFieldId = EditSourceViewModel.EditableField.LOYALTY_PERCENTAGE,
                        ) { validationResult, interactionSource, scrollToFocusableHandle ->
                            NumericTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .validationFocusRequester(scrollToFocusableHandle),
                                // TODO: I can't help feeling this looks a bit confusing when it's empty, maybe it's just lack of a "%" or something.
                                label = { Text("Loyalty scheme reward") },
                                value = loyaltyPercentage,
                                suffix = { Text("%") },
                                onValueChange = {
                                    loyaltyPercentage = it
                                    vm.setUIContentEditableSource(
                                        uiContent.editableSource.value.copy(
                                            loyaltyPercentage = it.text
                                        )
                                    )
                                },
                                enabled = saveStatus.isNotBusy(),
                                isError = validationResult != null,
                                supportingText = textOrNull(
                                    validationResult,
                                    color = MaterialTheme.colorScheme.error
                                ),
                                interactionSource = interactionSource,
                            )
                        }
                    }
                    */
                    // TODO: Not just here, but the use of abbreviations for units is maybe not ideal here, it's a bit confusing to just see e.g. a bare "l" instead of "litre"

                    // TODO: RelevantUnit* here are sort of copy and paste from ItemSourceInfo and could possibly be factored out along with the code using them
                    val relevantUnitFamilies =
                        remember(vm.uiContent.dataSet) { getRelevantUnitFamilies(vm.uiContent.dataSet) }

                    val relevantUnitList =
                        remember(vm.uiContent.dataSet, vm.uiContent.editableItem.value.quantityType) {
                            getRelevantMeasureUnits(
                                vm.uiContent.dataSet,
                                vm.uiContent.editableItem.value.quantityType,
                                includeDisplayOnly = false
                            )
                        }
                    MyExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        enabled = saveStatus.isNotBusy(),
                        selectedId = uiContent.editableItem.value.defaultUnit.id,
                        onValueChange = {
                            val defaultUnit = MeasureUnit.fromValue(it)
                            devCheck(defaultUnit != null) {
                                "Expected non-null defaultUnit to be selected; got $it"
                            }
                            if (uiContent.editableItem.value.defaultUnit != defaultUnit!!) {
                                val defaultUnitIdByQuantityTypeOrdinal =  uiContent.editableItem.value.defaultUnitIdByQuantityTypeOrdinal.toMutableList().also { it[uiContent.editableItem.value.quantityType.ordinal] = defaultUnit.id }
                                // TODO DELETE defaultUnitIdByQuantityTypeOrdinal[uiContent.editableItem.value.quantityType.ordinal] = defaultUnit.id
                                vm.setUIContentEditableItem(
                                    uiContent.editableItem.value.copy(
                                        defaultUnitIdByQuantityTypeOrdinal = defaultUnitIdByQuantityTypeOrdinal
                                    )
                                )
                            }
                        },
                        label = { Text("Default unit") },
                        supportingText = { Text("Used only as a default when entering a price for the first time. You can still choose another unit." ) },
                        items = relevantUnitList,
                        // Show dividers between unit families TODO: Copy and paste of code in ItemSourceInfo
                        getDividerBetween = { previousItem, item ->
                            val previousItemUnitFamily =
                                previousItem.unitFamilies.intersect(relevantUnitFamilies)
                            val itemUnitFamily =
                                item.unitFamilies.intersect(relevantUnitFamilies)
                            previousItemUnitFamily != itemUnitFamily
                        },
                        getId = { it.id },
                        getLabel = { it.symbol },
                    )
                    // TODO: If it's not too faffy, we should maybe remember the unit dropdown value (only in the edit UI of course) per-quantityType, so if the user flips back and forth between weight and volume they don't lose their previous selection
                }
            }
        }

        // TODO END COPY-AND-PASTE-ISH RADIO BUTTON CHUNK

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableItem.value.notes)
        FilteredTextField(
            label = { Text("Notes") },
            value = notes,
            onCandidateValueChange = makeOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                vm.setUIContentEditableItem(uiContent.editableItem.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        // TODO: We should take account of whether a product has any price data or not. Maybe not in
        // terms of labelling the button etc (though we could, albeit minor jank prospects as we'd
        // need to query async during recomposition, albeit the change might be small enough that on
        // this form jank might be minimal). At a minimum, when clicked, the alert dialog should
        // distinguish the cases where the product has prices and where it doesn't - the latter being
        // a much less scary delete.
        if (uiContent.editableItem.value.id != 0L) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showDeleteConfirmDialog = true },
                enabled = saveStatus.isNotBusy() && itemReferenceCount != null,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                // colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                if (showDeleteSpinner) {
                    SmallCircularProgressIndicator()
                } else {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    ) // TODO: tweak wording?
                }
                Spacer(Modifier.width(8.dp))
                Text("Delete product")
            }
        }
    }
}

@Composable
fun EditSourceScreen(
    vm: EditSourceViewModel,
    navController: NavHostController,
    requestClose: () -> Unit
) {
    val uiContent = vm.uiContent

    val sourceReferenceCount by vm.sourceReferenceCountFlow.collectAsStateWithLifecycle()
    Log.d("MyApp", "sourceReferenceCount $sourceReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by vm.generalEditScreenViewModel.saveStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = sourceReferenceCount == 0L
    GeneralEditAndDeleteScreen(
        vm = vm.generalEditScreenViewModel,
        navController = navController,
        // TODO: Different title for add vs edit? Title should maybe show data set name?
        title = { Text("TODO: TITLE") },
        isDirty = { uiContent.editableSource.value != uiContent.originalSource },
        validateForSave = { vm.validateForSave() },
        performSave = { vm.performSave() /* ; throw IllegalArgumentException("TODO2") */ },
        onIdle = {},
        requestClose = requestClose,
        deleteConfirmationDetails = if (!showDeleteConfirmDialog) null else Triple(
            isSimpleDelete,
            if (isSimpleDelete) {
                { Text("Delete store?") }
            } else {
                { Text("Delete store and prices?") }
            },
            if (isSimpleDelete) {
                { Text("This store has no associated prices so deleting it will not affect anything else.") }
            } else {
                // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
                { Text("Deleting this store will also delete its product prices. This action cannot be undone.") }
            }
        ),
        requestDelete = { vm.performDelete() },
        requestDeleteCancel = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(uiContent.editableSource.value.name)
        val nameValidationRules by vm.nameValidationRules.collectAsStateWithLifecycle()
        Log.d("MyApp", "nameValidationRules $nameValidationRules")
        ValidatedTextField2(
            label = { Text("Name") },
            value = name,
            maxLength = maxSourceNameLength,
            onValueChange = {
                name = it
                vm.setUIContentEditableSource(uiContent.editableSource.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value,
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditSourceViewModel.EditableField.NAME
        )

        // TODO: START EXPERIMENTAL
        Spacer(modifier = Modifier.height(16.dp))

        // TODO: We should almost certainly be doing this via an integer ID - we now have LoyaltyDiscountType
        // TODO: Can I put these string versions inside LoyaltyDiscountType or won't that play well with i18n?
        val options = listOf(
            Triple(LoyaltyDiscountType.NONE, "None", null),
            Triple(
                LoyaltyDiscountType.BONUS,
                "Store rewards",
                "Points or credit usable only at this store"
            ),
            Triple(LoyaltyDiscountType.DISCOUNT, "Discount", "Discount on basket or money back")
        )
        var selectedOption = uiContent.editableSource.value.loyaltyDiscountType
        // TODO: This radio group needs to be enabled iff saveStatus.isNotBusy()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)

            // TODO: colors?
            // TODO: elevation???
        ) {
            // We would like to use horizontal padding of 16.dp on this Column, but we don't want
            // the ripple effect on the radio button Rows to "stop" at the left edge of the circular
            // radio buttons. So we have to use 8.dp here and manually apply the remaining 8.dp
            // padding on each individual composable. I am not completely sure this looks great -
            // maybe it's a bit weird the ripple effect is "wider" than everything else - but it's
            // probably OK.
            Column(
                modifier = Modifier
                    // NB: We must do .animateContentSize() *before* .padding(), otherwise the clipping
                    // bounds the former imposes are too tight and will prevent ErrorHighlightBox
                    // drawing correctly.
                    .animateContentSize()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                // TODO: I'm far from sure what typography or colour this caption should have, but this
                // matches the caption on the TextFields so it is probably not a terrible choice. TODO: THIS IS FOR bodySmall - I can't help thinking titleSmall maybe looks better though. I am a bit worried the fonts are all over the place in general, but since MD3 is conspicuously silent outside of some very specific cases it is really hard to know what to do.
                Text(
                    "Loyalty scheme",
                    style = MaterialTheme.typography.titleSmall /* bodySmall */,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                //Spacer(modifier = Modifier.height(8.dp))
                options.forEach { (id, name, supportingText) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            //.background(Color.Blue)
                            .clickable {
                                vm.setUIContentEditableSource(
                                    uiContent.editableSource.value.copy(
                                        loyaltyDiscountType = id
                                    )
                                )
                            }
                            .padding(horizontal = 8.dp)
                            .height(48.dp) // 40.dp is MD3 spec but we want extra space for our supporting text while still having some spacing between items
                            //.padding(8.dp) // TODO: ChatGPT value to try to space things out now we have supportingText
                            .semantics {
                                role = Role.RadioButton
                            }, // for TalkBack / screen readers, since this is clickable not the RadioButton
                    ) {
                        RadioButton(
                            selected = (selectedOption == id),
                            onClick = null // TODO onClick
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = name,
                                /* TODO: not sure this looks right: style = MaterialTheme.typography.labelLarge, */ /* TODO: seems to be default anyway: color = MaterialTheme.colorScheme.onSurface */
                            )
                            Log.d("MyApp", "supportingText $supportingText")
                            if (supportingText != null) {
                                Text(
                                    text = supportingText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                if (selectedOption != LoyaltyDiscountType.NONE) {
                    Spacer(modifier = Modifier.height(8.dp))

                    var loyaltyPercentage by rememberSyncedTextFieldValue(uiContent.editableSource.value.loyaltyPercentage)
                    // TODO: Can/should we factor out this BaseValidatedTextField+NumericTextField combo?
                    Box(modifier = Modifier.padding(8.dp)) {
                        BaseValidatedTextField(
                            value = loyaltyPercentage.text,
                            validationRules = vm.loyaltyPercentageValidationRules,
                            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
                            validationFlow = vm.saveValidationEvents,
                            validationFlowFieldId = EditSourceViewModel.EditableField.LOYALTY_PERCENTAGE,
                        ) { validationResult, interactionSource, scrollToFocusableHandle ->
                            NumericTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .validationFocusRequester(scrollToFocusableHandle),
                                // TODO: I can't help feeling this looks a bit confusing when it's empty, maybe it's just lack of a "%" or something.
                                label = { Text("Loyalty scheme reward") },
                                value = loyaltyPercentage,
                                suffix = { Text("%") },
                                onValueChange = {
                                    loyaltyPercentage = it
                                    vm.setUIContentEditableSource(
                                        uiContent.editableSource.value.copy(
                                            loyaltyPercentage = it.text
                                        )
                                    )
                                },
                                enabled = saveStatus.isNotBusy(),
                                isError = validationResult != null,
                                supportingText = textOrNull(
                                    validationResult,
                                    color = MaterialTheme.colorScheme.error
                                ),
                                interactionSource = interactionSource,
                            )
                        }
                    }
                }
            }
        }

        // TODO END EXPERIMENTAL

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableSource.value.notes)
        FilteredTextField(
            label = { Text("Notes") },
            value = notes,
            onCandidateValueChange = makeOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                vm.setUIContentEditableSource(uiContent.editableSource.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        // TODO: We should take account of whether a product has any price data or not. Maybe not in
        // terms of labelling the button etc (though we could, albeit minor jank prospects as we'd
        // need to query async during recomposition, albeit the change might be small enough that on
        // this form jank might be minimal). At a minimum, when clicked, the alert dialog should
        // distinguish the cases where the product has prices and where it doesn't - the latter being
        // a much less scary delete.
        if (uiContent.editableSource.value.id != 0L) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showDeleteConfirmDialog = true },
                enabled = saveStatus.isNotBusy() && sourceReferenceCount != null,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                // colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                if (showDeleteSpinner) {
                    SmallCircularProgressIndicator()
                } else {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    ) // TODO: tweak wording?
                }
                Spacer(Modifier.width(8.dp))
                Text("Delete store")
            }
        }
    }
}

// TODO: This might turn out to be more re-usable than for just TextFields
@Composable
fun <T, U> BaseValidatedTextField( // TODO: TYPE LIST IS "BACKWARDS"
    value: U,
    validationRules: List<ValidationRule<U>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean = false,
    validationFlow: SharedFlow<T>,
    validationFlowFieldId: T,
    errorHighlightOffset: Dp = defaultErrorHighlightOffset,
    content: @Composable (
        validationResult: String?,
        interactionSource: MutableInteractionSource,
        scrollToFocusableHandle: ScrollToFocusableHandle,
    ) -> Unit
) {
    val scrollToFocusableHandle = rememberScrollToFocusable()

    val validationThing201 = rememberValidationThing(
        value = value,
        validationRules = validationRules,
        validationRulesKey = validationRulesKey,
        allowEmpty = allowEmpty
    )

    ErrorHighlightBox(
        hasError = scrollToFocusableHandle.errorHighlightBoxVisible.value,
        offset = errorHighlightOffset,
        validationTarget = scrollToFocusableHandle
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            // TODO: We could possibly pass validationThing201 directly. We could also maybe pass a Modifier.validationFocusRequester() instead of scrollToFocusableHandle.
            content(
                validationThing201.validationResult.value,
                validationThing201.interactionSource,
                scrollToFocusableHandle,
            )
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        validationFlow.collect { field ->
            Log.d("MyApp", "LaunchedEffect saveValidationError $field")
            when (field) {
                validationFlowFieldId -> {
                    scrollAndFocusTo(focusManager, scrollToFocusableHandle)
                }

                else -> {}
            }
        }
    }
}

@Composable
fun <T> ValidatedTextField2(
    label: @Composable() (() -> Unit)? = null,
    value: TextFieldValue,
    maxLength: Int,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean,
    validationRules: List<ValidationRule<String>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean = false,
    validationFlow: SharedFlow<T>,
    validationFlowFieldId: T
) {
    BaseValidatedTextField(
        value = value.text,
        validationRules = validationRules,
        validationRulesKey = validationRulesKey,
        allowEmpty = allowEmpty,
        validationFlow = validationFlow,
        validationFlowFieldId = validationFlowFieldId,
    ) { validationResult, interactionSource, scrollToFocusableHandle ->
        FilteredTextField(
            label = label,
            value = value,
            onCandidateValueChange = makeOnCandidateValueChangeMaxLength(maxLength),
            onValueChange = onValueChange,
            enabled = enabled,
            isError = validationResult != null,
            supportingText = textOrNull(
                validationResult,
                color = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .validationFocusRequester(scrollToFocusableHandle),
            interactionSource = interactionSource
        )
    }
}

@Composable
fun EditDataSetScreen(
    vm: EditDataSetViewModel,
    navController: NavHostController,
    requestClose: () -> Unit
) {
    val uiContent = vm.uiContent

    val dataSetReferenceCount by vm.dataSetReferenceCountFlow.collectAsStateWithLifecycle()
    Log.d("MyApp", "dataSetReferenceCount $dataSetReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by vm.generalEditScreenViewModel.saveStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = dataSetReferenceCount == 0L
    GeneralEditAndDeleteScreen(
        vm = vm.generalEditScreenViewModel,
        navController = navController,
        // TODO: Different title for add vs edit?
        title = { Text("TODO: TITLE") },
        isDirty = { uiContent.editableDataSet.value != uiContent.originalDataSet },
        validateForSave = { vm.validateForSave() },
        performSave = { vm.performSave(); /* throw IllegalArgumentException("TODO2") */ },
        onIdle = {},
        requestClose = requestClose,
        // TODO: WORDING FOR ALL OF THIS IS PARTICULARLY BAD AND NEEDS THOUGHT
        deleteConfirmationDetails = if (!showDeleteConfirmDialog) null else Triple(
            isSimpleDelete,
            if (isSimpleDelete) {
                { Text("Delete collection?") }
            } else {
                { Text("Delete collection and products, stores and prices?") }
            },
            if (isSimpleDelete) {
                { Text("This collection has no associated TODODATA so deleting it will not affect anything else.") }
            } else {
                // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
                { Text("Deleting this collection will also delete its TODOASSOCIATEDDATA. This action cannot be undone.") }
            }
        ),
        requestDelete = { vm.performDelete() },
        requestDeleteCancel = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(uiContent.editableDataSet.value.name)
        val nameValidationRules by vm.nameValidationRules.collectAsStateWithLifecycle()
        ValidatedTextField2(
            label = { Text("Name") },
            value = name,
            maxLength = maxDataSetNameLength,
            onValueChange = {
                name = it
                vm.setUIContentEditableDataSet(uiContent.editableDataSet.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value,
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp)) // TODO: Maybe 16.dp given general structure of this screen?

        // TODO: Should we specify an offset of 4.dp here? Or should we perhaps just improve spacing?
        BaseValidatedTextField(
            value = uiContent.editableDataSet.value.currencyCode,
            validationRules = vm.currencyValidationRules,
            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.CURRENCY_CODE
        ) { validationResult, interactionSource, scrollToFocusableHandle ->
            // TODO: When we do the "add data set" case, note that currency will be able to be null and we need to validate it isn't null on save.
            // TODO: According to a long comment I wrote elsewhere, we probably should be using a frozen
            // LocalConfiguration from when this screen was first opened here. However, at present it
            // includes no floating point values that are awkward if the locale changes, and being
            // responsive to any locale changes is both easy and may be helpful. If I keep doing it this
            // way, I need to update that long comment elsewhere accordingly and make a permanent note
            // here too.
            val currentLocalConfiguration = LocalConfiguration.current
            val currencyList = remember(currentLocalConfiguration.locales) {
                // TODO: Test this updates if we change locales on the fly?
                buildCurrencyList(currentLocalConfiguration.locales)
            }

            // TODO: Without getting sidetracked just yet into e.g. third party libraries to support
            // currency selection between, we try to do half-decent job by showing a gigantic list in
            // an unwieldy dropdown but putting the currencies the user is likely to care about at the
            // top. In the longer term apart from maybe investigating third party libraries I see two
            // options:
            // 1 - optionally allow the user to just enter a three letter currency code directly
            // 2 - optionally allow the user to define their own currency (in which case we don't care
            //     about three letter codes) by specifying prefix, suffix and decimal places
            // If option 2 is available, there may be no real need for option 1. We'd probably still
            // support currency selection in some form, but the specific escape hatch of being able to
            // type in a three letter code is not so important. But maybe we'd do both.
            //
            // We could of course create our own pop-up (probably not full screen) dialog to pick a
            // currency, but the chances are curating a list which isn't bloated with historical
            // currencies (which are not relevant to us) is something best left to a third party library
            // which is actively interested in this. For us it's rather tangential.
            //
            // We could also use our existing item selection dialog - which is substring search capable
            // - to help the user pick something out of the gigantic list of currencies instead of
            // scrolling through a giant dropdown.

            // TODO: This may expose a lurking bug in MyExposedDropdownMenuBox - the very last (I think)
            // item in the list is *not* entirely shown. I don't know if the same thing will happen with
            // other dropdowns which get long enough to need scrolling, but should definitely test as it
            // matters much more there. It's ugly and annoying and concerning here too, of course.
            MyExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .validationFocusRequester(scrollToFocusableHandle),

                selectedId = if (uiContent.editableDataSet.value.currencyCode != "") uiContent.editableDataSet.value.currencyCode else null,
                onValueChange = {
                    vm.setUIContentEditableDataSet(
                        uiContent.editableDataSet.value.copy(
                            currencyCode = it
                        )
                    )
                },
                enabled = saveStatus.isNotBusy(),
                label = { Text("Currency") },
                items = currencyList.second,
                getId = { it.first },
                getLabel = { it.second },
                getDividerBetween = { firstItem, _ -> firstItem.first == currencyList.first },
                supportingText = textOrNull(
                    validationResult,
                    color = MaterialTheme.colorScheme.error,
                ),
                // TODO!? interactionSource = interactionSource
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: MD3 Expressive deprecates this and says we should use a connected button group, but
        // the relevant library version is still in alpha so I'll just do it the old MD3 way for now
        // with a segmented button group.
        // TODO: Should this maybe be on a Card to help it match the "style" of the filledtextfields?
        // TODO: I'm far from sure what typography or colour this caption should have, but this
        // matches the caption on the TextFields so it is probably not a terrible choice.
        BaseValidatedTextField(
            value = Triple(
                uiContent.editableDataSet.value.allowMetric,
                uiContent.editableDataSet.value.allowImperial,
                uiContent.editableDataSet.value.allowUSCustomary
            ),
            validationRules = vm.measurementSystemValidationRules,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.MEASUREMENT_SYSTEM
        ) { validationResult, interactionSource, scrollToFocusableHandle ->
            Text(
                "Measurement units",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ) // TODO TWEAK TEXT, FONT, SIZE
            // "US Customary" doesn't fit (on my test "small" emulated phone) but based on a discussion
            // with ChatGPT "US Units" is better for a casual user anyway, even if we could fit "US
            // Customary".
            val options = listOf("Metric", "Imperial", "US units")
            val checkedStates = remember {
                mutableStateListOf(
                    uiContent.editableDataSet.value.allowMetric,
                    uiContent.editableDataSet.value.allowImperial,
                    uiContent.editableDataSet.value.allowUSCustomary
                )
            }
            // TODO: Following is hacky, use an enum class or something rather than hardcoding 1 and 2 as imperial/US

            // We *don't* call Modifier.validationFocusRequester() as you can't focus a segmented button,
            // and this will force a clear focus to happen instead.
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onCheckedChange = {
                            checkedStates[index] = it
                            // Don't allow Imperial and US Customary to be selected together. (We use
                            // the common but ambiguous names for the units, so this would cause UI
                            // confusion. We don't want to be showing "pint (US)" or "pt (US)" all the
                            // time to disambiguate.)
                            if (index > 0 && checkedStates[index]) {
                                checkedStates[if (index == 1) 2 else 1] = false
                            }
                            vm.setUIContentEditableDataSet(
                                uiContent.editableDataSet.value.copy(
                                    allowMetric = checkedStates[0],
                                    allowImperial = checkedStates[1],
                                    allowUSCustomary = checkedStates[2]
                                )
                            )
                        },
                        checked = checkedStates[index],
                        colors = SegmentedButtonDefaults.colors(),
                        icon = { SegmentedButtonDefaults.Icon(active = checkedStates[index]) },
                        enabled = true
                    ) {
                        Text(label)
                    }
                }
            }

            if (validationResult != null) {
                // TODO: I'm not sure this padding gives the ideal visual appearance, but this doesn't look too bad.
                // TODO: Should we show a red warning triangle e.g. at left or right of this text? Not sure, but we
                // do show one in the case of TextFields so although the layout isn't quite the same, maybe showing
                // one here is not a bad idea. Current gut feeling following some LLM discussion is that the
                // warning triangle is probably not a good idea, but it should be at the left if I do add it. And
                // maybe I should make the border of the segmented button red if we're in an error state as well,
                // although my inclination is that this might look ugly and is not particularly blessed as
                // standard.
                SupportingText(
                    validationResult,
                    isError = true,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableDataSet.value.notes)
        FilteredTextField(
            label = { Text("Notes") },
            value = notes,
            onCandidateValueChange = makeOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                vm.setUIContentEditableDataSet(uiContent.editableDataSet.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

        if (uiContent.editableDataSet.value.id != 0L) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { showDeleteConfirmDialog = true },
                enabled = saveStatus.isNotBusy() && dataSetReferenceCount != null,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                // colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
            ) {
                if (showDeleteSpinner) {
                    SmallCircularProgressIndicator()
                } else {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete"
                    ) // TODO: tweak wording?
                }
                Spacer(Modifier.width(8.dp)) // TODO: Maybe 16.dp given spacing around measurement units?
                Text("Delete collection")
            }
        }
    }
}

// TODO: Rename validationResult for brevity? And/or add a @Composable helper which returns
// validationResult.value to simplify callers?
// TODO: We could *maybe* include  MutableState<Boolean> in ValidationThing which is used to trigger
// the *on-save only* highlight boxes (not "there is an error but you haven't clicked save yet"
// case) - this might help keep things tidy and also provide somewhere I could shove a "launch {
// =true; delay; =false }" helper which would have the MutableState handy and thus *could* modify
// generically. We would of course still have the option to use the error highlight box with "var
// foo by remember { mutableStateOf(false) }" if we wanted, this would just be a convenient way to
// keep things together.
// TODO: So I suppose maybe we could also put a ScrollToFocusableHandle in here too??
// TODO: NumericTextField and ValidatedTextField want paring down to omit the validation and just do
// "input restriction", and their callers either using rememberValidationThing directly or we create
// some kind of new NumericTextField2/ValidatedTextField2 which wraps those pared-down input
// restriction only things with this. I'd probably have to start actually modifying the code to see
// what really works.
class ValidationThing(
    val interactionSource: MutableInteractionSource = MutableInteractionSource(),
    val validationResult: State<String?> // or Flow/LiveData/etc
)

@Composable
fun <T> rememberValidationThing(
    value: T,
    validationRules: List<ValidationRule<T>>,
    validationRulesKey: Any? = null,
    delayMillis: Long = defaultValidationMessageDelayMillis,
    // We default allowEmpty to false since this will be relatively obvious if we forget to specify
    // it somewhere it ought to have a more sophisticated condition ("add new X" will immediately
    // show a "name is emtpy" warning without waiting for a save attempt first). It is just about
    // worth having a default so cases where this isn't meaningful don't have to specify it.
    allowEmpty: Boolean = false
): ValidationThing {
    val interactionSource = remember { MutableInteractionSource() }
    val validationResult = remember { mutableStateOf<String?>(null) }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var failedValidationRule by remember(validationRulesKey) {
        mutableStateOf<ValidationRule<T>?>(
            null
        )
    }

    // TODO: This does not have the "change validation text immediately if there is already some and
    // the text changes" behaviour of my existing implementation - think about it, we probably *do*
    // want that. Likewise we probably want something here so there's no delay if the input has just
    // become valid. Do think about both of these though.
    // TODO: So what do we *want*?
    // - if validationRulesKey or allowEmpty changes (which is almost a form of validationRulesKey
    //   changing, just tracked separately), we should probably update immediately - these do not
    //   happen during "casual" editing (certainly not *of the field being validated*)
    // - if value changes that is going to be the user typing. We should evaluate immediately and
    //   if everything is OK, immediately update (to nothing). If something is wrong and a different
    //   something was wrong we should immediately update. Maybe. If the user is typing maybe we
    //   should minimise distractions and flicker and not change anything at all until they stop. but
    //   it does feel like there's an argument for not having an out of date error sticking around
    //   for 500ms or whatever after they finish typing.
    // - maybe simply waiting (say) 200ms after user input before we do anything and then doing it is
    //   the way to go. And just maybe have a setting option to switch between "slow" and "fast"
    //   validation or something like that. it may be best not to try being overly-clever up front,
    //   e.g. even if I am the only user, I won't really know how I feel about this until I've used
    //   it in anger on an actual smartphone with a touchscreen rather than typing on keyboard on
    //   PC or clicking awkwardly with the mouse on the on-screen keyboard on emulator.
    LaunchedEffect(value, validationRulesKey, allowEmpty, isFocused) {
        // TODO: The delay is breaking things a bit here when e.g. we have an empty "pack size" string
        // and click save - the validation message becomes eligible for display as allowEmpty is now
        // true, but it doesn't appear straight away and so it "misses" the highlight box and it
        // generally looks bad and a bit confusing. (This is less of a visual issue now I've dropped
        // the delay from 1000ms to 200ms, but it's probably best to address it properly. Maybe
        // put the delay back to 1000ms temporarily when working on this.) I suspect the fix is to
        // have a remembered oldValue, say "if (value != oldValue)" here instead of controlling based
        // on isFocused, and the obviously set oldValue = value after. Not tested this, maybe too
        // simplistic.
        if (isFocused) delay(delayMillis)

        val reorderedValidations =
            listOfNotNull(failedValidationRule) + validationRules
        failedValidationRule = null
        var shouldValidate = true// TODO: rename skipValidation or something to flip sense?
        when (value) {
            is String -> shouldValidate = !(allowEmpty && value.trim().isEmpty())
            else -> {} // allowEmpty has no meaning for other types
        }
        if (shouldValidate) {
            for (validationRule in reorderedValidations) {
                if (!validationRule.validate(value)) {
                    failedValidationRule = validationRule
                    break
                }
            }
        }

        validationResult.value = failedValidationRule?.message
    }

    return ValidationThing(interactionSource, validationResult)
}


fun getCurrencyForLocale(locale: Locale): Currency? {
    try {
        return Currency.getInstance(locale)
    } catch (e: IllegalArgumentException) {
        // Some locales (e.g. zz_ZZ) might not have a valid currency.
        return null
    }
}

// This list is a combination of the currency codes from list two (fund codes,
// https://www.six-group.com/dam/download/financial-information/data-center/iso-currrency/lists/list-two.doc)
// and list three (historic currencies and funds,
// https://www.six-group.com/dam/download/financial-information/data-center/iso-currrency/lists/list-three.xls).
// I have then de-blacklisted the following which also appear on list one
// (https://www.six-group.com/dam/download/financial-information/data-center/iso-currrency/lists/list-one.xls)
// and to my non-expert eye look like currencies which are potentially in use: EUR, MWK, PEN, RON,
// SDG, SZL, TRY. There's obviously some contextual information in lists two and three which just
// taking the list of currencies ignores.
// @formatter:off
val blacklistedCurrencyCodes = setOf(
    "ADP", "AFA", "ALK", "ANG", "AOK", "AON", "AOR", "ARA", "ARP", "ARY", "ATS", "AYM", "AZM",
    "BAD", "BEC", "BEF", "BEL", "BGJ", "BGK", "BGL", "BOP", "BOV", "BRB", "BRC", "BRE", "BRN",
    "BRR", "BUK", "BYB", "BYR", "CHC", "CHE", "CHW", "CLF", "COU", "CSD", "CSJ", "CSK", "CUC",
    "CYP", "DDM", "DEM", "ECS", "ECV", "EEK", "ESA", "ESB", "ESP", "FIM", "FRF", "GEK", "GHC",
    "GHP", "GNE", "GNS", "GQE", "GRD", "GWE", "GWP", "HRD", "HRK", "IDR", "IEP", "ILP", "ILR",
    "ISJ", "ITL", "LAJ", "LSM", "LTL", "LTT", "LUC", "LUF", "LUL", "LVL", "LVR", "MGF", "MLF",
    "MRO", "MTL", "MTP", "MVQ", "MXP", "MXV", "MZE", "MZM", "NIC", "NLG", "PEH", "PEI", "PES",
    "PLZ", "PTE", "RHD", "ROK", "ROL", "RUR", "SDD", "SDP", "SIT", "SKK", "SLL", "SRG", "STD",
    "SUR", "TJR", "TMM", "TPE", "TRL", "UAK", "UGS", "UGW", "USN", "USS", "UYI", "UYN", "UYP",
    "UYW", "VEB", "VEF", "VNC", "XAD", "XEU", "XFO", "XFU", "XRE", "YDD", "YUD", "YUM", "YUN",
    "ZAL", "ZMK", "ZRN", "ZRZ", "ZWC", "ZWD", "ZWL", "ZWN", "ZWR"
)
// @formatter:on

// Returns a list of (currency codes as IDs, currency display names) with the most likely ones (based
// on the current locales) at the top. The last of the "most likely" currency codes is also returned
// as a string so we can use it to add a divider after this entry.
//
// In Spanish, the display names are all lower case with no initial capital. ChatGPT assures me that
// this is what a native speaker would expect, so I'm not coercing the first character into upper
// case to appease my native English speaker brain. I will trust that getDisplayName() does the
// right thing for the current locale, until an actual native speaker of some non-English language
// tells me otherwise.
fun buildCurrencyList(locales: LocaleList): Pair<String, List<Pair<String, String>>> {
    fun buildPair(currency: Currency): Pair<String, String> {
        val currencyCode = currency.currencyCode
        val displayName = currency.getDisplayName(locales[0])
        if (displayName.contains(currencyCode)) {
            Log.d("MyApp", "not adding $currencyCode for $displayName")
            return Pair(currency.currencyCode, displayName)
        } else {
            return Pair(currency.currencyCode, "$displayName ($currencyCode)")
        }
    }

    val mainCurrencyList = mutableListOf<Pair<String, String>>()
    val mainCurrencyCodeSet = mutableSetOf<String>()
    for (i in 0 until locales.size()) {
        val locale = locales[i]
        val currency = getCurrencyForLocale(locale)
        if (currency != null && currency.currencyCode !in mainCurrencyCodeSet) {
            mainCurrencyList.add(buildPair(currency))
            mainCurrencyCodeSet.add(currency.currencyCode)
        }
    }

    // getAvailableCurrencies() seems to return a lot of junk. At some point it's likely to be
    // easier just to use a curated list as the starting point, but for now let's persist with the
    // system values in the name of flexibility. We blacklist some specific codes which we know to
    // be historic or for funds instead of currencies; it seems fair to assume these will never be
    // used in a context we're interested in. We also filter out currency codes starting with X and
    // currency codes where the system re-uses the currency code as the display name.
    val otherCurrencyList =
        Currency.getAvailableCurrencies().mapNotNull { currency ->
            if (currency.currencyCode in mainCurrencyCodeSet ||
                currency.currencyCode.startsWith("X") ||
                currency.getDisplayName(locales[0]) == currency.currencyCode ||
                currency.currencyCode in blacklistedCurrencyCodes
            ) null else buildPair(currency)
        }

    // TODO: ChatGPT magic, check later
    val collator = Collator.getInstance(locales[0]).apply {
        strength = Collator.PRIMARY // case-insensitive, diacritic-aware
    }
    return Pair(
        mainCurrencyList.last().first,
        mainCurrencyList.toList() + otherCurrencyList.sortedWith { lhs, rhs ->
            collator.compare(
                lhs.second,
                rhs.second
            )
        })
}


// TODO: Maybe rename - the idea here is this does not insist the input is actually parseable as a
// decimal (for example, we allow "24.2.3" so the user can enter a new decimal point *and then later
// go delete the old one*), but that it rejects obviously incorrect things. We allow digits, commas,
// full stops and spaces - the interpretation of these is locale-dependent, but this should allow
// valid decimals to be entered with no annoying quirks in any locale.
fun isValidTransitionalDecimal(input: String): Boolean {
// Regular expression to match any character that is not a digit, comma, period, or space
    val regex = Regex("[^\\d,.\\s]")
    return !regex.containsMatchIn(input)
}

@Parcelize // TODO: May not be needed now - are we still using these in rememberSaveable?
data class ValidationRule<T>(val validate: (T) -> Boolean, val message: String) : Parcelable

// TODO: Do we use this everywhere we could? Would returning failed rule or null make it
// more reusable?
fun <T> validationRulesOk(validationRules: List<ValidationRule<T>>, value: T): Boolean {
    for (validationRule in validationRules) {
        if (!validationRule.validate(value)) {
            return false
        }
    }
    return true
}


// TODO: This duplicates code in numericValidationRules(). It may be as well to move some of these
// functions and associated logic onto the ViewModel, as it is already locale-aware and is notified
// when the locale changes. That said, this isn't ideal as multiple different screens/ViewModels
// could all want to use this code and we don't really want to duplicate it. Can/should we have
// an object which is included by composition in all ViewModels that want it? Inheritance? Something
// else?
// TODO: There might be an argument for *not* allowing grouping characters in strings - not just
// here, perhaps we even would always allow them here, but maybe more in TextFields. If a user is
// mixing different "regions", they might get confused and type "." when the decimal separator is
// "," or vice versa and enter junk, whereas if typing "." when it isn't the decimal separator is
// discarded it's a little more obvious. Maybe this isn't a concern, given the device's region is
// what (I think) matters here, not the dataSet's setting, so the user will always be using their
// native symbols. This could be a setting of course but don't rush to make it one.
fun parseStringAsDoubleOrNull(locale: Locale, string: String): Double? {
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    // If input filtering allowed "-" characters through they are significant, so we don't strip
    // them out here. This is harmless if they were never allowed through, of course.
    val insignificantCharsRegex = "[^-0-9${Regex.escape(decimalSeparator.toString())}]".toRegex()
    return string
        .replace(insignificantCharsRegex, "")
        .replace(decimalSeparator, '.')
        .toDoubleOrNull() // not locale aware, decimal separator is always "."
}

// This assumes input filtering has already excluded characters other than digits, space, comma and
// full stop.
fun numericValidationRules(
    locale: Locale,
    allowDecimals: Boolean = true,
    allowZero: Boolean = true,
    maxDecimals: Int? = null,
): List<ValidationRule<String>> {
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    val maxDecimalSeparators = if (allowDecimals) 1 else 0

    // Create a function to strip fluff like spaces and the grouping symbol if the user typed it in.
    val insignificantCharsRegex = "[^-0-9${Regex.escape(decimalSeparator.toString())}]".toRegex()
    fun sanitiseCandidate(candidate: String) = candidate.replace(insignificantCharsRegex, "")
    fun attemptedParse(candidate: String): Double? =
        sanitiseCandidate(candidate).replace(decimalSeparator, '.').toDoubleOrNull()

    // TODO: Now I am (working on) allowEmpty in my validation logic driven by save having been
    // clicked, we should probably add an explicit validation rule in here saying it can't be empty
    // or whatever wording is appropriate, rather than letting this be handled by the default
    // "Invalid number" case at the end.

    return listOfNotNull(
        ValidationRule(
            { it.count { char -> char == decimalSeparator } <= maxDecimalSeparators },
            // TODO: Just possibly we should not consider a single decimal separator with nothing
            // significant following it as violating "only whole numbers allowed".
            if (allowDecimals) "Only one decimal point allowed" else "Only whole numbers allowed"
        ),

        if (maxDecimals != null) {
            // TODO: We could allow extra decimal places if they are all zeros? I could see arguments either way.
            // TODO: This message will be ungrammatical if maxDecimals == 1
            ValidationRule({
                val parts = sanitiseCandidate(it).split(decimalSeparator)
                parts.size != 2 || parts[1].length <= maxDecimals
            }, "No more than $maxDecimals decimal places allowed")
        } else {
            null
        },

        if (!allowZero) {
            // TODO: This message assumes you can't enter a negative value in the first place.
            ValidationRule({ attemptedParse(it) != 0.0 }, "Must be greater than zero")
        } else {
            null
        },

        // This is a catch-all; in practice we expect to catch all problems before this, but we
        // don't want to have a string which can't be converted (which would cause an error on
        // trying to save) which the user hasn't been warned about.
        ValidationRule({ attemptedParse(it) != null }, "Invalid number"),
    )
}

// A simple wrapper around FilteredTextField which performs filtering for numeric input.
@Composable
fun NumericTextField(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    value: TextFieldValue,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    interactionSource: MutableInteractionSource? = null,
) {
    FilteredTextField(
        label = label,
        value = value,
        prefix = prefix,
        suffix = suffix,
        textStyle = textStyle,
        // TODO: We don't (we could, but probably no point) allow arbitrary onCandidateValueChange
        // functions to be supplied by our caller. We just hardcode this for now. We could
        // potentially accept some options from our caller which say whether decimal point (locale
        // sensitive) or minus signs are allowed and tweak the internally-assigned onCandidate...
        // function here.
        // TODO: The length limit is a bit arbitrary but we're just trying to avoid the user filling
        // the box full of junk. I picked 11 because with my current layout on a small phone this
        // avoids wrapping, and it feels very generous anyway. This allows just under a million with
        // two decimal places and a (manually entered) thousands separator.
        onCandidateValueChange = { isValidTransitionalDecimal(it) && it.length <= 11 },
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        modifier = modifier,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
    )
}

// TODO: This function itself is fine, but it provides an easy demonstration that if the text is
// *already* over the limit, we can not edit it to delete anything. In practice you shouldn't get
// into this kind of hole, but it might be worth changing the API of onCandidateValueChange so
// it receives the old text as well, so here we could allow the new text if it's shorter than the
// old text even if it's still too long.
// TODO: We don't show a current/maximum count on our ValidatedTextFields because the length limit
// is just there to keep things tidy and in practice we don't expect a user to run up against it, so
// it would be unwanted visual fluff (imagine a price field where the user thinks of it as a decimal
// value which seems to be keen on counting characters - they don't use this function but do use
// equivalent logic to impose a length limit). I think it's OK if a user does hit a text field size
// limit that their keystrokes are just ignored, but it feels slightly off. I don't really see a
// good way to communicate this though - the best I can think of is a transitory supportingText (not
// generated via the more persistent validationrule stuff), but that might be annoying. It might
// work and perhaps the biggest difficutly is combining it with the validation rule logic - so maybe
// worth revisiting this later. Maybe just ignoring silently is actually best though, regardless of
// the work involved - think about it fresh later.
// TODO: Maybe "build" instead of "make" (in other places too) would be more idiomatic? I suspect "create" might be most idiomatic.
fun makeOnCandidateValueChangeMaxLength(maxLength: Int): (String) -> Boolean =
    { it.length <= maxLength }


// Like TextField, but with some simple logic to allow input to be filtered and discarded via an
// onCandidateValueChange callback. It also - although this is just a convenience and isn't
// fundamental - automatically drives the internal TextField's trailingIcon from the isError
// parameter.
@Composable
fun FilteredTextField(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    value: TextFieldValue,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onCandidateValueChange: ((String) -> Boolean),
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    interactionSource: MutableInteractionSource? = null
) {
    TextField(
        label = label,
        value = value,
        prefix = prefix,
        suffix = suffix,
        textStyle = textStyle,
        onValueChange = { newValue: TextFieldValue ->
            if (onCandidateValueChange(newValue.text)) {
                onValueChange(newValue)
            }
        },
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        supportingText = supportingText,
        trailingIcon = if (isError) {
            {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        } else null,
        isError = isError,
        interactionSource = interactionSource,
    )
}

// Format a double to be edited by the user as a string in a TextField. Grouping is *not* used -
// since this is for editing via a text field and the grouping characters (if any) won't
// automagically stay in place as the user edits, we don't want any. As far as I can tell, general
// consensus is that "clever" edit fields which automatically insert or maintain grouping separators
// are frowned on these days, this isn't just laziness on my part. (It's not part of this function,
// but we do allow the user to add their own grouping separators if they want; we just ignore them
// when parsing the string later.)
fun formatDoubleForEditing(value: Double, minDecimals: Int, maxDecimals: Int, locale: Locale) =
    formatDouble(
        value,
        minDecimals = minDecimals,
        maxDecimals = maxDecimals,
        useLocaleGrouping = false,
        locale = locale,
    )

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
}

// TODO: This is a bit of a mess but probably best leave it alone until I either gain more
// experience or do more testing with different Android versions.
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
                .penaltyLog() // TODO .penaltyDeath() // TODO .penaltyLog()  // logs violations; you can also add .penaltyDeath() to crash on violation
                .build()
        )

        /*
        // TODO: Experiment with adding a Settings activity and make the dark/light/follow system available and grey out (with some text saying why) follow system on Android < 10
        val isDarkTheme = true when (userThemePref) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    } */
        setContent {
            val darkTheme = isSystemInDarkTheme()

            ComposeTutorialTheme(darkTheme = darkTheme) {
                /* TODO: DELETE?
                val window = (this as ComponentActivity).window

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
                // TODO: We may not need this surface any more, I can't see the test green colour
                // appearing.
                Surface(
                    modifier = Modifier
                        .fillMaxSize()/* .safeDrawingPadding() */.imePadding(),
                    color = Color.Green /* MaterialTheme.colorScheme.background */
                ) {
                    // TODO: It may be we can get rid of this Box now it does nothing
                    Box(/* TODO: Delete modifier = Modifier.safeDrawingPadding() */) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

data class EditItemsScreenUIContent(
    val itemList: List<Item>,
    val dataSet: DataSet
) {
    fun saveState(handle: SavedStateHandle) {
        handle[ITEM_LIST_KEY] = itemList
        handle[DATA_SET_KEY] = dataSet
    }

    companion object {
        private const val ITEM_LIST_KEY = "itemList"
        private const val DATA_SET_KEY = "dataSet"

        fun fromSavedState(handle: SavedStateHandle): EditItemsScreenUIContent? {
            val savedItemList: List<Item>? = handle[ITEM_LIST_KEY]
            val savedDataSet: DataSet? = handle[DATA_SET_KEY]
            if (savedItemList != null && savedDataSet != null) {
                Log.d("MyApp", "reconstructed EditItemsScreenUIContent")
                return EditItemsScreenUIContent(savedItemList, savedDataSet)
            } else {
                Log.d("MyApp", "couldn't reconstruct EditItemsScreenUIContent")
                return null
            }
        }
    }
}

// Shared ViewModel to pass data between screens
// TODO: Some inconsistency between "UIContent" and "Content" here - think about renaming.
class SharedViewModel : ViewModel() {
    // This is only nullable to provide us with an easy initial value to use. In use
    // setEditPriceScreenState() should always have been called before it is used.
    // TODO: Should we be using get/set functions or a read-only property and a set?
    var editPriceScreenUIContent: EditPriceScreenUIContent? = null

    // frozenLocale becomes part of the edit screen state - it was used to convert the doubles to
    // strings, and we will use it to convert the strings back to doubles if the user saves. If the
    // user changes the locale while on the edit screen, we do *not* want to reflect that change
    // immediately because it makes parsing the strings ambiguous. (TODO: This is not heavily tested
    // and is not all that an important case, but I am at least trying to do things right.)
    fun setEditPriceScreenContent(
        uiContent: HomeScreenUIContent,
        frozenLocale: Locale
    ) {
        // !! is justified because uiContent was shown on the home screen and the edit price button
        // was visible, which can only happen if we have all three available.
        val dataSet = uiContent.dataSet!!
        val item = uiContent.item!!
        val source = uiContent.source!!

        val price =
            uiContent.priceList.find { it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id }

        val editablePrice = if (price != null)
            EditablePrice(price, frozenLocale, getCurrencyFormat(dataSet, frozenLocale))
        else EditablePrice(
            dataSetId = dataSet.id,
            itemId = item.id,
            sourceId = source.id,
            itemDefaultUnit = item.defaultUnit
        )
        editPriceScreenUIContent = EditPriceScreenUIContent(
            editablePrice = mutableStateOf(editablePrice),
            originalPrice = editablePrice,
            dataSet = dataSet,
            item = item,
            source = source,
            frozenLocale = frozenLocale,
        )
    }

    // TODO: ALL EXPERIMENTAL NEW BELOW HERE

    // TODO: Not just here, but e.g. EditSourceScreen vs EditSource*s*Screen is way too subtle for this already rather confusing code. Might (apart from other possible improvements) be better to
    // talk about "Edit source" (singular) but "List sources" (plural) internally, even if we continue to use "Edit sources" in the UI labels.

    // TODO: Rename the following now they are just List<T>? not a UIContent structure
    var editDataSetsScreenUIContent: List<DataSet>? = null
    var editItemsScreenUIContent: EditItemsScreenUIContent? = null
    var editSourcesScreenUIContent: List<Source>? = null

    // TODO: The "doubling" in the next three functions is a temporary hack to show that we use the
    // initial list and then it gets replaced by the query results from the database. The map step
    // is because we use the IDs as keys on LazyColumn and if there are duplicate IDs it gets upset;
    // of course with real data there won't be duplicate IDs at all.

    fun setEditDataSetsScreenContent(uiContent: HomeScreenUIContent) {
        editDataSetsScreenUIContent =
            uiContent.dataSetList + uiContent.dataSetList.map { it -> it.copy(id = it.id * 1000) }
    }

    fun setEditItemsScreenContent(uiContent: HomeScreenUIContent) {
        editItemsScreenUIContent = EditItemsScreenUIContent(
            uiContent.itemList + uiContent.itemList.map { it -> it.copy(id = it.id * 1000) },
            uiContent.dataSet!!
        )
    }

    fun setEditSourcesScreenContent(uiContent: HomeScreenUIContent) {
        editSourcesScreenUIContent =
            uiContent.sourceList + uiContent.sourceList.map { it -> it.copy(id = it.id * 1000) }
    }

    // TODO: MORE NEW EXPERIMENTAL

    var editDataSetScreenUIContent: EditDataSetScreenUIContent? = null

    fun setEditDataSetScreenContent(dataSet: DataSet?) {
        val editableDataSet = EditableDataSet.fromDataSet(dataSet)
        editDataSetScreenUIContent = EditDataSetScreenUIContent(
            editableDataSet = mutableStateOf(editableDataSet),
            originalDataSet = editableDataSet,
        )
    }

    var editItemScreenUIContent: EditItemScreenUIContent? = null

    fun setEditItemScreenContent(item: Item?, dataSet: DataSet) {
        val editableItem = EditableItem.fromItem(item, dataSet)
        editItemScreenUIContent = EditItemScreenUIContent(
            editableItem = mutableStateOf(editableItem),
            originalItem = editableItem,
            dataSet = dataSet,
        )
    }

    var editSourceScreenUIContent: EditSourceScreenUIContent? = null

    fun setEditSourceScreenContent(
        // TODO: name should include "FromBlah"? or maybe that's a silly convention?
        source: Source?,
        dataSetId: Long,
        frozenLocale: Locale
    ) {
        val editableSource = EditableSource.fromSource(source, dataSetId, frozenLocale)
        editSourceScreenUIContent = EditSourceScreenUIContent(
            editableSource = mutableStateOf(editableSource),
            originalSource = editableSource,
            frozenLocale = frozenLocale,
        )
    }
}

// Return the non-digit prefix and suffix around a digit-containing string. Given "foo123bar4 baz56
// quux", this returns ("foo", " quux").
fun splitAroundDigits(input: String): Pair<String, String> {
    var firstDigitIndex = input.indexOfFirst { it.isDigit() }
    if (firstDigitIndex == -1) {
        firstDigitIndex = 0
    }
    val prefix = input.substring(0, firstDigitIndex)

    val lastDigitIndex = input.indexOfLast { it.isDigit() }
    val suffix = if (lastDigitIndex == -1) {
        ""
    } else {
        input.substring(lastDigitIndex + 1)
    }

    return Pair(prefix, suffix)
}

// TODO: ChatGPT magic
inline fun <reified VM : ViewModel> viewModelFactoryWithHandle(
    crossinline builder: (MyApplication, SavedStateHandle) -> VM
): ViewModelProvider.Factory {
    return viewModelFactory {
        initializer {
            val handle = createSavedStateHandle()
            // As written by ChatGPT, this passed "this", a CreationExtras, as the first argument of
            // builder. Given how we actually use this, it saves code duplication to just extract a
            // MyApplication here and pass that instead.
            builder(
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication,
                handle
            )
        }
    }
}

data class GeneralSelectorScreenUIContent<T>(
    val initialList: List<T>?
)

// TODO: This function does not handle non-English languages very well. As far as I can tell from
// discussing with LLMs and doing my own web searches, we really need something like the ICU string
// search service (https://unicode-org.github.io/icu/userguide/collation/string-search) but although
// Android has some ICU stuff by default, it apparently doesn't have this. I am going to use this
// basic implementation (which I believe won't handle the German sharp S correctly, just as an
// example) for now and can revisit it later if any non-English users turn up.
fun isCaseInsensitiveSubstring(lhs: String, rhs: String, locale: Locale) =
    rhs.lowercase(locale).contains(lhs.lowercase(locale))

// TODO: We probably *can* do a half-decent job of implementing this locale-sensitive, probably something to do with collate(), but need to look into it. This is different to isCaseInsensitiveSubstring() because we are dealing with the string as a whole, not substrings. But for now I will hack it with this English-ish version.
// TODO: Even in English-only, it might be good to squash sequences of whitespace down to a single space for comparison so "foo  bar" == "foo bar" != "foobar"
fun areHumanEqual(lhs: String, rhs: String) =
    lhs.trim().lowercase() == rhs.trim().lowercase()

class EditItemsViewModel(
    savedStateHandle: SavedStateHandle,
    getName: (Item) -> String,
    val uiContent: EditItemsScreenUIContent,
    // TODO DELETE initialList: List<Item>?,
    dataQuery: Flow<List<Item>>,
    // TODO DELETE public val dataSet: DataSet
) : GeneralSelectorViewModel<Item>(savedStateHandle, getName, uiContent.itemList /* TODO: rename initialList for consistency with other cases? */, dataQuery) {
    init {
        uiContent.saveState(savedStateHandle)
    }
}

// TODO: This may not actually need the repository passing in given we pass in a query
open class GeneralSelectorViewModel<T>(
    private val savedStateHandle: SavedStateHandle,
    private val getName: (T) -> String,
    private val initialList: List<T>?,
    private val dataQuery: Flow<List<T>>,
) : ViewModel() {
    // The idea here is that as we have no real state other than the results of dataQuery, we
    // optimise by having our caller provide initialList to give a good first composition during
    // normal navigation, but we can manage without it if we are reincarnated.
    // TODO: This works and it is probably fine but not that for EditItemsViewModel we do actually
    // serialise, even though the general code doesn't require it. (We need it so we can pass a
    // DataSet through to EditItemScreen.)

    // This will *not* filter uiContent.initialList, but that's OK because we know the initial filter doesn't exclude anything.
    // TODO: We could persist the search string via savedStateHandle. That might not be
    // unreasonable, and unless I gain a lot in the navcontroller by not making a savestatehandle
    // available there is probably no real downside, but I won't do it just yet until I finish the
    // current refactor. Might need to be careful to ensure we don't have a leftover search string when navigating in fresh, especially since we wouldn't even apply it.
    val searchStringFlow = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val dataFlow = combine(
        dataQuery.flatMapLatest { data -> /* TODO HACK delay(5000); */ flowOf(data) },
        searchStringFlow
    ) { data, query ->
        data.filter {
            isCaseInsensitiveSubstring(
                query.trim(),
                getName(it),
                Locale.getDefault() /* TODO VERY TEMP HACK - WE ARE NOT SUPPOSED TO BE USING THIS FUNCTION */
            )
        }
    }
        .onEach { emittedList -> /* delay(5000); */ Log.d(
            "MyAppGS",
            "Room emitted list: ${System.identityHashCode(emittedList)}"
        )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = initialList ?: emptyList()
        )
}

@Composable
fun topAppBarTitle(title: String, subtitle: String?): @Composable (() -> Unit) =
    if (subtitle != null) {
        {
            // TODO: No idea if these sizes are MD3 compliant, spec talks about actual sizes etc
            // but I really feel I ought to be using the MaterialTheme.typography stuff. Maybe
            // I'm wrong. I think this does look about right anyway.
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    /*
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    */
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    } else {
        { Text(title) }
    }

@Composable
fun <T> GeneralSelectorScreen(
    vm: GeneralSelectorViewModel<T>,
    navController: NavHostController,
    title: @Composable () -> Unit,
    getId: (T) -> Long,
    getName: (T) -> String,
    onAddClick: (() -> Unit)? = null,
    // TODO: We pass the actual T to onItemSelected to try to avoid race conditions, I am not
    // completely sure about this but let's see how it goes. Do I need to do this in any other
    // contexts as well?
    onItemSelected: (T) -> Unit,
    showSearch: Boolean = false,
) {
    val dataList by vm.dataFlow.collectAsStateWithLifecycle()
    val searchString by vm.searchStringFlow.collectAsStateWithLifecycle()
    Log.d("MyAppGS", "dataList $dataList")

    val floatingActionButton: (@Composable () -> Unit) = if (onAddClick == null) {
        {}
    } else {
        @Composable {
            // The commented out options here would (I think) be MD3 compliant (picking the
            // "default" colour combination) but they seem to be the defaults anyway.
            FloatingActionButton(
                onClick = dropUnlessResumed { onAddClick() },

                // containerColor = MaterialTheme.colorScheme.primaryContainer,
                // contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                // shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    // modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add item" // TODO: CALLER SHOULD SUPPLY THIS
                )
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // TODO: I am wondering if title and subtitle should swap roles here? Keep the data set
            // name as the title as on the home screen? And if we go with this, *maybe* the subtitle
            // is just "Products" (for example) not "Edit products"?? Although this approach won't
            // work for "edit collections", so would it be inconsistent for it to have that as its
            // title? Maybe it would be OK.
            TopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        },
        floatingActionButton = floatingActionButton,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(innerPadding)
            // TODO: experimentally not using this here so list can be edge-to-edge .padding(screenBorder)

            // TODO: copied from Home, maybe want this but put it in when we do .verticalScroll(androidx.compose.foundation.rememberScrollState())
        ) {
            if (showSearch) {
                TextField(
                    value = searchString,
                    onValueChange = { it -> vm.searchStringFlow.value = it },
                    label = { Text("Search") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            // tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search text",
                            // TODO: If we make the search string persist to SavedStateHandle, next line would have to call a vm function to update it and write it to the SSH
                            modifier = Modifier.clickable { vm.searchStringFlow.value = "" },
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = screenBorder)
                        .padding(bottom = 8.dp),
                )
            }

            Box(
                modifier = Modifier
                    .background(Color.Green /* TODO! */)
                    .fillMaxWidth()
            ) {
                dataList.forEach { println("Item: $it, ID: ${getId(it)}") }
                LazyColumn {
                    items(
                        items = dataList,
                        key = { item -> getId(item) }
                    ) { item ->
                        GeneralSelectorListItem(
                            id = getId(item),
                            name = getName(item),
                            onItemSelected = dropUnlessResumed { onItemSelected(item) },
                        )
                    }
                }
            }

            // TODO: We *might* want optional support for a delete action on items, but I suspect we
            // won't - deleting is rare and we don't want to make it too easy and it can be done
            // from the "edit individual item" screen. Don't forget to implement this though!
        }
    }
}

// TODO: We could optionally add switches or check boxes to the list items to allow them to be enabled or disabled - but this may well be better done at the edit X individual screen level
// TODO: This function might well be better just folded into GeneralSelectorScreen
@Composable
fun GeneralSelectorListItem(id: Long, name: String, onItemSelected: () -> Unit) {
    ListItem(
        headlineContent = { Text(name) },
        modifier = Modifier.clickable { onItemSelected() },
    )
}

// TODO: Here, and possibly in other ViewModels, there is a tendency to be passing parameters into
// functions which are actually just taken out of the ViewModel's own state anyway. It may well be
// worth removing these redundant parameters, but I will hold off for now on the vague grounds that
// the parameters being explicit may be useful for unit testing later on. I can always refactor when
// I've had a go at writing some tests.
class EditPriceViewModel(
    private val priceTrackerRepository: PriceTrackerRepository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditPriceScreenUIContent,
) : ViewModel() {
    private val instanceId = UUID.randomUUID().toString() // TODO FOR DEBUG

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    var packSizeValidationRules = generatePackSizeValidationRules()
    var currencyFormat = getCurrencyFormat(uiContent.dataSet, uiContent.frozenLocale)

    init {
        Log.d("MyApp", "EditPriceScreenViewModel $instanceId $this")
        Log.d("MyApp", "EditPriceScreenViewModel.init($uiContent)")
        uiContent.saveState(savedStateHandle)
    }

    fun setUIContentEditablePrice(newEditablePrice: EditablePrice) {
        Log.d("MyApp", "EditPriceScreenViewModel.setUIContentEditablePrice($newEditablePrice)")
        // TODO: We could potentially refactor so that if newEditablePrice has the same measure unit
        // as uiContent before we update it, we don't regenerate the pack size validation rules.
        uiContent.editablePrice.value = newEditablePrice
        uiContent.saveEditablePriceState(savedStateHandle)
        packSizeValidationRules = generatePackSizeValidationRules()
    }

    // TODO: This is called "generate" not "get" in part to show it performs work and isn't just
    // returning a cached value, but also to avoid a Kotlin/JVM clash with the
    // packSizeValidationRules property. I think I am generally a bit inconsistent in naming here
    // anyway (e.g. numericValidationRules() also performs work) and some kind of tidying up of the
    // naming generally might be in order.
    private fun generatePackSizeValidationRules(): List<ValidationRule<String>> {
        val maxDecimals = uiContent.editablePrice.value.measureUnit.maxDecimals
        return numericValidationRules(
            uiContent.frozenLocale,
            allowDecimals = maxDecimals > 0,
            allowZero = false,
            maxDecimals = maxDecimals
        )
    }

    // TODO: I suspect this should *either* be moved down into a rememberSaveable inside the composable,
    // *or* it should be preserved across process death (perhaps, but not necessarily, by being moved
    // into EditPriceScreenUIContent).
    var firstPackSizeOrPriceChangeOccurred: Boolean = false

    enum class EditableField {
        PACK_SIZE,
        PRICE
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    // TODO: It's tempting to think this should be on EditablePrice itself, but the whole point is
    // that it will apply (sharing as much as possible) the same validation rules that the
    // ValidatedTextFields are using - and those aren't available to EditablePrice, and based on
    // discussion with ChatGPT I think it's better to have this function here than pass this
    // ViewModel as an argument to EditablePrice.toDomain()
    suspend fun validateForSave(): Boolean {
        if (!validationRulesOk(
                packSizeValidationRules,
                uiContent.editablePrice.value.measureValue
            )
        ) {
            _saveValidationEvents.emit(EditableField.PACK_SIZE)
            return false
        }
        if (!validationRulesOk(
                currencyFormat.validationRules,
                uiContent.editablePrice.value.price
            )
        ) {
            _saveValidationEvents.emit(EditableField.PRICE)
            return false
        }
        return true
    }

    suspend fun performSave() {
        val price = uiContent.editablePrice.value.toDomain(uiContent.frozenLocale)
        Log.d("MyApp", "saveEditablePrice price $price")
        if (price == null) {
            throw IllegalStateException("saveEditablePrice() called with an inconvertible editablePrice: ${uiContent.editablePrice.value}")
        }
        priceTrackerRepository.updateOrInsertPrice(price)
    }

    // TODO: Is there really no standard abstraction which will wrap all this hellish savestatus crap up?

    // TODO DELETE? val saveStatus = SyncedStateEvent(SaveStatus.Idle)
}

// TODO: This is not just a *save* status any more - rename
enum class SaveStatus {
    Idle, Busy, BusyForAWhile, Success, Error;

    // We count "success" as busy here, since it doesn't make sense to re-enable buttons after we
    // succeeded and are about to close.
    fun isNotBusy(): Boolean {
        return this != Busy && this != BusyForAWhile && this != Success
    }
}

@Composable
fun SmallCircularProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(16.dp),
        strokeWidth = 2.dp,
    )
}

class EditSourceViewModel(
    private val priceTrackerRepository: PriceTrackerRepository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditSourceScreenUIContent,
) : ViewModel() {
    init {
        uiContent.saveState(savedStateHandle)
    }

    val sourceReferenceCountFlow = uiContent.editableSource.value.id.let { sourceId ->
        if (sourceId != 0L) {
            priceTrackerRepository.countPricesForSource(sourceId)
        } else {
            flowOf(0L) // new sources have no references
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUIContentEditableSource(newEditableSource: EditableSource) {
        uiContent.editableSource.value = newEditableSource
        uiContent.saveEditableSourceState(savedStateHandle)
    }

    // TODO: There just might be an argument for not using emptyList() in stateIn, so we can head
    // off a theoretical possibility of the user entering invalid data (maybe just leaving the
    // form empty when creating a new entry) and starting a save before the validation rules are
    // present, which will pass (because no validation rules) and then they either insert invalid
    // data or get a database level constraint validation. If we have null, we can make sure the
    // validation rules *are present* during save validation.
    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        priceTrackerRepository.getAllSources(uiContent.editableSource.value.dataSetId)
            .map { sourceList ->
                buildNameValidationRules(
                    sourceList.filter { source -> source.id != uiContent.editableSource.value.id }
                        .map { source -> source.name }
                )
            }
            .withVersion()
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialVersioned(emptyList()))

    // TODO: Maybe we should allow zero here? We might need to tweak some messages accordingly. Zero isn't necessary as you can choose "None", but maybe it's a bit persnickety not to allow the user just to type 0 directly with one of the other options as well.
    // TODO: Should we impose an upper bound? At the very least something like 100% is probably safe.
    val loyaltyPercentageValidationRules = numericValidationRules(
        uiContent.frozenLocale,
        allowDecimals = true,
        allowZero = false,
        maxDecimals = 2
    )

    enum class EditableField {
        NAME,
        LOYALTY_PERCENTAGE,
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        Log.d("MyAppESS", "validateForSave")
        if (!validationRulesOk(
                nameValidationRules.value.value,
                uiContent.editableSource.value.name
            )
        ) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }
        // TODO: IS IT OK TO EXPLCIITLY CHECK loyaltyDiscountType HERE? CAN/SHOULD THIS BE FOLDED INTO VALIODATION RULES, E.G. BY VALIDATING A PAIR<DISCOUNTTYPE,STRINGDISCOUNTPERCENTAGE>??
        if (uiContent.editableSource.value.loyaltyDiscountType != LoyaltyDiscountType.NONE && !validationRulesOk(
                loyaltyPercentageValidationRules,
                uiContent.editableSource.value.loyaltyPercentage
            )
        ) {
            _saveValidationEvents.emit(EditableField.LOYALTY_PERCENTAGE)
            return false
        }
        Log.d("MyAppESS", "validateForSave passed")
        // TODO: MORE
        return true
    }

    suspend fun performSave() {
        val source = uiContent.editableSource.value.toDomain(uiContent.frozenLocale)
        if (source == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableSource: ${uiContent.editableSource.value}")
        }
        priceTrackerRepository.updateOrInsertSource(source)
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val sourceId = uiContent.editableSource.value.id
        devCheck(sourceId != 0L) { "Expected to delete an actual source but have ID 0" }
        val rowsDeleted = priceTrackerRepository.deleteSourceById(sourceId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with sourceId $sourceId")
    }
}

class EditItemViewModel(
    private val priceTrackerRepository: PriceTrackerRepository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditItemScreenUIContent,
) : ViewModel() {
    init {
        uiContent.saveState(savedStateHandle)
    }

    val itemReferenceCountFlow = uiContent.editableItem.value.id.let { itemId ->
        if (itemId != 0L) {
            priceTrackerRepository.countPricesForItem(itemId)
        } else {
            flowOf(0L) // new items have no references
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUIContentEditableItem(newEditableItem: EditableItem) {
        uiContent.editableItem.value = newEditableItem
        uiContent.saveEditableItemState(savedStateHandle)
    }

    // TODO: There just might be an argument for not using emptyList() in stateIn, so we can head
    // off a theoretical possibility of the user entering invalid data (maybe just leaving the
    // form empty when creating a new entry) and starting a save before the validation rules are
    // present, which will pass (because no validation rules) and then they either insert invalid
    // data or get a database level constraint validation. If we have null, we can make sure the
    // validation rules *are present* during save validation.
    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        priceTrackerRepository.getAllItems(uiContent.editableItem.value.dataSetId)
            .map { itemList ->
                buildNameValidationRules(
                    itemList.filter { item -> item.id != uiContent.editableItem.value.id }
                        .map { item -> item.name }
                )
            }
            .withVersion()
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialVersioned(emptyList()))

    enum class EditableField {
        NAME
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        Log.d("MyAppESS", "validateForSave")
        if (!validationRulesOk(
                nameValidationRules.value.value,
                uiContent.editableItem.value.name
            )
        ) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }
        // TODO: MORE
        Log.d("MyAppESS", "validateForSave passed")
        return true
    }

    suspend fun performSave() {
        val item = uiContent.editableItem.value.toDomain()
        if (item == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableItem: ${uiContent.editableItem.value}")
        }
        priceTrackerRepository.updateOrInsertItem(item)
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val itemId = uiContent.editableItem.value.id
        devCheck(itemId != 0L) { "Expected to delete an actual item but have ID 0" }
        val rowsDeleted = priceTrackerRepository.deleteItemById(itemId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with itemId $itemId")
    }
}

// TODO: Not here specifically, I almost wonder if the lambdas should have the *option* (not
// obligation) to modify the value for later lambdas in the chain, and the validation process
// returns the final one. This *might* provide a natural way to implement things like "strip
// spaces" or "strip insignificant fluff in a double-as-string" as an initial step, avoid
// redoing that work in subsequent lambdas which want the same sanitising and help to avoid the
// situation where for example the validation is all based on a trim()ed string but I forget to
// manually apply the trim() when writing the string to the database. On the other hand, applying
// the validation rule changes to a data class via copy() might be finicky and error prone.
fun buildNameValidationRules(existingNameList: List<String>): List<ValidationRule<String>> {
    return listOf(
        ValidationRule<String>({ it.isNotEmpty() }, "Must have a name"),
        // TODO! ValidationRule({ it -> 'x' in it }, "Must contain 'x' to be cool"),
    ) + existingNameList.map { name ->
        ValidationRule(
            { candidateName -> !areHumanEqual(candidateName, name) },
            "Name must be unique"
        ) // TODO: Tweak wording?
    }
}

// TODO: There is a huge amount of pseudo copy and paste in all the Edit*{Screen,ViewModel} stuff.
// Probably just going to accept it as I do the initial implementation so I don't tie myself in
// knots coping with generic attempts that don't quite match reality, but later on it would be good
// to see what can be factored out.
class EditDataSetViewModel(
    private val priceTrackerRepository: PriceTrackerRepository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditDataSetScreenUIContent,
) : ViewModel() {
    init {
        uiContent.saveState(savedStateHandle)
    }

    // TODO: 42 is obviously a hack. Data sets can be referenced by items, source *and* prices. In
    // some ways being referenced by prices is "scariest", but it's also not nice if the user wipes
    // out a dataset with items and sources associated even if they are no prices. I need to decide
    // what I will consider here, which is all about generating warnings to the user. I could just
    // sum the counts across all three reference types for example. I may want to count each thing
    // separately and tweak the UI delete warnings accordingly.
    val dataSetReferenceCountFlow = flowOf(42L)
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUIContentEditableDataSet(newEditableDataSet: EditableDataSet) {
        uiContent.editableDataSet.value = newEditableDataSet
        uiContent.saveEditableDataSetState(savedStateHandle)
    }

    // TODO: There just might be an argument for not using emptyList() in stateIn, so we can head
    // off a theoretical possibility of the user entering invalid data (maybe just leaving the
    // form empty when creating a new entry) and starting a save before the validation rules are
    // present, which will pass (because no validation rules) and then they either insert invalid
    // data or get a database level constraint validation. If we have null, we can make sure the
    // validation rules *are present* during save validation.
    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        priceTrackerRepository.getAllDataSets()
            .map { dataSetList ->
                buildNameValidationRules(
                    dataSetList.filter { dataSet -> dataSet.id != uiContent.editableDataSet.value.id }
                        .map { dataSet -> dataSet.name }
                )
            }
            .withVersion()
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialVersioned(emptyList()))

    val currencyValidationRules = listOf(
        ValidationRule<String>(
            { it.isNotEmpty() },
            "Currency must be specified"
        ) // TODO: poor wording?
    )

    // TODO: I should probably replace the Triple<3xBoolean> with a data class for readability.
    // Maybe it should even be used in a domain-level DataSet class with the current raw database
    // one being renamed DataSetEntity? We could potentially pass it into various functions and that
    // might simplify the code - but check before blindly doing this, it may not be a big enough
    // win.
    val measurementSystemValidationRules = listOf(
        // We say "measurement system" in the error message here even though the caption above the
        // segmented button is "measurement units". The former is technically correct, the latter is
        // more colloquial and I think it works well as a caption, but I think in this error message
        // context, "measurement unit" does not work - it sounds as if the user is expected to
        // choose at least one thing like "miles" or "litres". If "measurement system" is a bit
        // technical, I hope the overall context with the caption above will make it clear.
        ValidationRule<Triple<Boolean, Boolean, Boolean>>(
            { it -> it.first || it.second || it.third },
            "At least one measurement system must be selected"
        ),
        // This next rule is enforced by UI logic, but let's go belt and braces.
        ValidationRule<Triple<Boolean, Boolean, Boolean>>(
            { !(it.second && it.third) },
            "Imperial and US units cannot be selected together"
        ),
    )

    enum class EditableField {
        NAME,
        CURRENCY_CODE, // TODO: Just "CURRENCY"? THAT'S WHAT FIELD-ON-SCREEN AND SOME OTHER VARS ARE CALLED
        MEASUREMENT_SYSTEM,
        // TODO: MORE
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        Log.d("MyAppESS", "validateForSave")
        val editableDataSet = uiContent.editableDataSet.value
        if (!validationRulesOk(nameValidationRules.value.value, editableDataSet.name)) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }

        if (!validationRulesOk(currencyValidationRules, editableDataSet.currencyCode)) {
            _saveValidationEvents.emit(EditableField.CURRENCY_CODE)
            return false
        }

        // TODO: We will need to validate currencyCode is not empty, for the add new data set case
        // TODO: Should maybe factor out forming this Boolean Triple from editableDataSet into a function
        if (!validationRulesOk(
                measurementSystemValidationRules,
                Triple(
                    editableDataSet.allowMetric,
                    editableDataSet.allowImperial,
                    editableDataSet.allowUSCustomary
                )
            )
        ) {
            _saveValidationEvents.emit(EditableField.MEASUREMENT_SYSTEM)
            return false
        }
        Log.d("MyAppESS", "validateForSave passed")
        // TODO: MORE
        return true
    }

    suspend fun performSave() {
        val dataSet = uiContent.editableDataSet.value.toDomain()
        if (dataSet == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableDataSet: ${uiContent.editableDataSet.value}")
        }
        priceTrackerRepository.updateOrInsertDataSet(dataSet)
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val dataSetId = uiContent.editableDataSet.value.id
        devCheck(dataSetId != 0L) { "Expected to delete an actual data set but have ID 0" }
        val rowsDeleted = priceTrackerRepository.deleteDataSetById(dataSetId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with dataSetId $dataSetId")
    }
}

// TODO: ChatGPT magic
@Composable
inline fun <reified VM : ViewModel, UIContent> screenWithViewModel( // TODO: UNUSED TYPE ARG!
    backStackEntry: NavBackStackEntry,
    noinline clearUIContent: () -> Unit,
    noinline buildViewModel: @DisallowComposableCalls (MyApplication, SavedStateHandle) -> VM,
    crossinline content: @Composable (VM) -> Unit
) {
    val factory = remember(backStackEntry) {
        viewModelFactoryWithHandle { app, handle -> buildViewModel(app, handle) }
    }

    LaunchedEffect(Unit) {
        clearUIContent()
    }

    val vm: VM = viewModel(
        viewModelStoreOwner = backStackEntry,
        factory = factory
    )

    content(vm)
}

@Composable
// TODO inline fun <reified VM : ViewModel, UIContent> todoRenameMe(
// TODO: I suspect this shows that it makes more sense just to use screenWithViewModel directly and how to do it
fun <T> todoRenameMe(
    backStackEntry: NavBackStackEntry,
    clearUIContent: () -> Unit,
    buildViewModel: (MyApplication, SavedStateHandle) -> GeneralSelectorViewModel<T>,
    content: @Composable (GeneralSelectorViewModel<T>) -> Unit // TODO: crossinline is copy and paste voodoo
) {
    screenWithViewModel<GeneralSelectorViewModel<T>, Int>(
        backStackEntry = backStackEntry,
        clearUIContent = clearUIContent,
        buildViewModel = buildViewModel,
    ) { vm ->
        content(vm)
    }
}

// TODO: Navigation is a mess - I'm completely unclear how the mysterious back stack and routes and
// viewmodels being reused and various different kinds of composition and activity and process
// destruction and reconstruction are supposed to interact.
// TODO: Random Grok suggestion to maybe play with later: Use LinearOutSlowInEasing for enter
// transitions (starts fast, slows down) and FastOutLinearInEasing for exit transitions (starts
// slow, speeds up) to make the slide feel natural.
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel =
        viewModel(LocalContext.current as ComponentActivity) // TODO: perplexity voodoo
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        // TODO: Move these up to where the other global-ish constants are defined?
        val tweenDurationMillisEnter = 700 // TODO: should probably be 300 in final version
        val tweenDurationMillisExit = 700 // TODO: should probably be 250 in final version

        // TODO: The syntax required to factor these animations out into re-usable functions is pure
        // ChatGPT voodoo (and it took several attempts to get it right, unless I just kept messing
        // up myself).

        fun AnimatedContentTransitionScope<NavBackStackEntry>.slideLeftTransition(): EnterTransition =
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,

                animationSpec = tween(
                    durationMillis = tweenDurationMillisEnter,
                    easing = LinearOutSlowInEasing
                ),
            )

        fun AnimatedContentTransitionScope<NavBackStackEntry>.slideRightTransition(): ExitTransition =
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(
                    durationMillis = tweenDurationMillisExit,
                    easing = FastOutLinearInEasing
                )
            )

        fun AnimatedContentTransitionScope<NavBackStackEntry>.slideUpTransition(): EnterTransition =
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,

                animationSpec = tween(
                    durationMillis = tweenDurationMillisEnter,
                    easing = LinearOutSlowInEasing
                ),
            )

        fun AnimatedContentTransitionScope<NavBackStackEntry>.slideDownTransition(): ExitTransition =
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(
                    durationMillis = tweenDurationMillisExit,
                    easing = FastOutLinearInEasing
                )
            )

        // Note that we explicitly request a fresh ViewModel each time (because it's tied to the
        // backStackEntry) - this avoids stale data causing problems.

        composable(
            "home",
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
        ) { backStackEntry ->
            Log.d("MyApp", "backStackEntry.id ${backStackEntry.id}")
            val locale by rememberUpdatedState(LocalConfiguration.current.locales[0])
            HomeScreen(
                viewModel(backStackEntry, factory = AppViewModelProvider.Factory),
                navController,
                onEditPriceClick = { uiContent ->
                    sharedViewModel.setEditPriceScreenContent(
                        uiContent,
                        locale
                    )
                    navController.navigate("editPrice")
                },
                onEditDataSetsClick = { uiContent ->
                    sharedViewModel.setEditDataSetsScreenContent(
                        uiContent
                    )
                    navController.navigate("editDataSets")
                },
                onEditProductsClick = { uiContent ->
                    sharedViewModel.setEditItemsScreenContent(
                        uiContent
                    )
                    navController.navigate("editItems/${uiContent.dataSet!!.id}/${uiContent.dataSet.name}")
                },
                onEditSourcesClick = { uiContent ->
                    sharedViewModel.setEditSourcesScreenContent(
                        uiContent
                    )
                    navController.navigate("editSources/${uiContent.dataSet!!.id}/${uiContent.dataSet.name}")
                },
            )
        }

        composable(
            "settings", enterTransition = { slideLeftTransition() },
            popExitTransition = { slideRightTransition() },
        ) {
            SettingsScreen(navController)
        }

        composable(
            "editDataSets", enterTransition = { slideLeftTransition() },
            popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            screenWithViewModel<GeneralSelectorViewModel<DataSet>, Int /* TODO DUMMY */>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editDataSetsScreenUIContent = null },
                buildViewModel = { app, handle ->
                    GeneralSelectorViewModel(
                        savedStateHandle = handle,
                        getName = { it -> it.name },  // TODO: not actually used, allow null?
                        initialList = sharedViewModel.editDataSetsScreenUIContent,
                        dataQuery = app.priceTrackerRepository.getAllDataSets()
                    )
                }
            ) { viewModel ->
                GeneralSelectorScreen(
                    viewModel,
                    navController,
                    title = topAppBarTitle("Edit collections", null),
                    getId = { it.id },
                    getName = { it.name },
                    onAddClick = {
                        Log.d("MyAppGS", "Add data set")
                        sharedViewModel.setEditDataSetScreenContent(null)
                        navController.navigate("editDataSet")
                    },
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        sharedViewModel.setEditDataSetScreenContent(it)
                        navController.navigate("editDataSet")
                    })
            }
        }

        composable(
            "editItems/{dataSetId}/{dataSetName}", enterTransition = { slideLeftTransition() },
            popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            // TODO: We now have an actual DataSet passed to us so we can and perhaps should get rid of dataSetId and dataSetName
            val dataSetId = backStackEntry.arguments?.getString("dataSetId")!!.toLong()
            val dataSetName = backStackEntry.arguments?.getString("dataSetName")
            screenWithViewModel<EditItemsViewModel, Int /* TODO DUMMY */>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editItemsScreenUIContent = null },
                buildViewModel = { app, handle ->
                    EditItemsViewModel(
                        savedStateHandle = handle,
                        getName = { it -> it.name },
                        sharedViewModel.editItemsScreenUIContent ?: EditItemsScreenUIContent.fromSavedState(handle)!!,
                        // TODO: DELETE initialList = sharedViewModel.editItemsScreenUIContent?.itemList,
                        dataQuery = app.priceTrackerRepository.getAllItems(dataSetId),
                        // TODO: DELETE dataSet = sharedViewModel.editItemsScreenUIContent!!.dataSet
                    )
                }
            ) { viewModel ->
                GeneralSelectorScreen(
                    viewModel,
                    navController,
                    title = topAppBarTitle("Edit products", dataSetName),
                    getId = { it.id },
                    getName = { it.name },
                    onAddClick = {
                        Log.d("MyAppGS", "Add item")
                        sharedViewModel.setEditItemScreenContent(null, viewModel.uiContent.dataSet)
                        navController.navigate("editItem")
                    },
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        sharedViewModel.setEditItemScreenContent(it, viewModel.uiContent.dataSet)
                        navController.navigate("editItem")
                    },
                    showSearch = true
                )
            }
        }

        // TODO: Can we factor out a lot of the commonality in the GeneralSelector-based composables here?
        composable(
            "editSources/{dataSetId}/{dataSetName}", enterTransition = { slideLeftTransition() },
            popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            val dataSetId = backStackEntry.arguments?.getString("dataSetId")!!.toLong()
            val dataSetName = backStackEntry.arguments?.getString("dataSetName")
            screenWithViewModel<GeneralSelectorViewModel<Source>, Int /* TODO DUMMY */>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editSourcesScreenUIContent = null },
                buildViewModel = { app, handle ->
                    GeneralSelectorViewModel(
                        savedStateHandle = handle,
                        getName = { it -> it.name },
                        initialList = sharedViewModel.editSourcesScreenUIContent,
                        dataQuery = app.priceTrackerRepository.getAllSources(dataSetId)
                    )
                }
            ) { viewModel ->
                // TODO: Is this locale wrong? Will this *pick up* changes to the locale, defeating the
                // whole point of having a frozen locale? Do we need to be setting this in the navhost screen which *calls* us? That's
                // what (albeit via sharedViewModel - do we have to use that here now?) happens for the edit price screen.
                val locale by rememberUpdatedState(LocalConfiguration.current.locales[0])
                GeneralSelectorScreen(
                    viewModel,
                    navController,
                    title = topAppBarTitle("Edit stores", dataSetName),
                    getId = { it.id },
                    getName = { it.name },
                    onAddClick = {
                        Log.d("MyAppGS", "Add source")
                        sharedViewModel.setEditSourceScreenContent(null, dataSetId, locale)
                        navController.navigate("editSource")
                    },
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        sharedViewModel.setEditSourceScreenContent(it, dataSetId, locale)
                        navController.navigate("editSource")
                    })
            }
        }

        composable(
            "editPrice", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },
        ) { backStackEntry ->
            screenWithViewModel<EditPriceViewModel, EditPriceScreenUIContent>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editPriceScreenUIContent = null },
                buildViewModel = { app, handle ->
                    EditPriceViewModel(
                        app.priceTrackerRepository,
                        handle,
                        sharedViewModel.editPriceScreenUIContent
                            ?: EditPriceScreenUIContent.fromSavedState(handle)!!
                    )
                }, // TODO !! IS MAYBE A HACK - TBH COULD I JUST MAKE FROMSAVEDSTATE RETURN NON-NULL? NOT TOO KEEN
            ) { viewModel ->
                // TODO: Be good to test fairly late on with two datasets with different currencies - I vaguely wonder
                // if re-use of this composable (maybe prevented via randomUUID route hack?) will not pick up the
                // changes.
                EditPriceScreen(
                    viewModel, navController,
                    requestClose = { navController.popBackStack() }
                )
            }
        }

        composable(
            "editDataSet", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },
        ) { backStackEntry ->
            screenWithViewModel<EditDataSetViewModel, EditDataSetScreenUIContent>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editDataSetScreenUIContent = null },
                buildViewModel = { app, handle ->
                    EditDataSetViewModel(
                        app.priceTrackerRepository,
                        handle,
                        sharedViewModel.editDataSetScreenUIContent
                            ?: EditDataSetScreenUIContent.fromSavedState(handle)!!
                    )
                }
            ) { viewModel ->
                EditDataSetScreen(
                    viewModel, navController,
                    requestClose = {
                        navController.popBackStack()
                    })
            }
        }

        /* TODO: ChatGPT suggestion for factoring out editX screen commonality (it didn't have full context so the ideas is the thing not the code):
@Composable
fun <T : Any> LoadOrInitUIState(
    existing: T?,
    fromSavedState: (SavedStateHandle) -> T,
    content: @Composable (T) -> Unit
) {
    val savedStateHandle = checkNotNull(LocalSavedStateHandle.current)
    val value = remember(existing) {
        existing ?: fromSavedState(savedStateHandle)
    }
    content(value)
}
LoadOrInitUIState(
    existing = sharedViewModel.editSourceScreenUIContent,
    fromSavedState = { EditSourceScreenUIContent.fromSavedState(it) }
) { uiState ->
    EditSourceViewModel(uiState)
}

Don't forget (I didn't even ask ChatGPT) these code fragments are just composables and I can push the LaunchedEffect(Unit) in there too.

Grok suggested:
@Composable
inline fun <reified VM : ViewModel, T> NavHostScreen(
    navBackStackEntry: NavBackStackEntry,
    sharedState: T?,
    viewModelFactory: (SavedStateHandle) -> VM,
    stateInitializer: (SavedStateHandle) -> T,
    content: @Composable (VM, T) -> Unit
) {
    // Create ViewModel using the provided factory
    val viewModel: VM = viewModel(
        viewModelStoreOwner = navBackStackEntry,
        factory = viewModelFactory(navBackStackEntry.savedStateHandle)
    )

    // Initialize state: use sharedState if available, otherwise create from savedStateHandle
    val state = sharedState ?: stateInitializer(navBackStackEntry.savedStateHandle)

    // Render the content with the ViewModel and state
    content(viewModel, state)
}

The function uses reified VM : ViewModel to ensure the ViewModel type is a subclass of ViewModel,
and T for the state type (e.g., EditSourceScreenUIContent). The reified keyword allows type-safe
ViewModel creation without requiring an interface for the state type.

In Kotlin, you can use the :: operator to reference a function, such as ::MyFunction. [instead of an "identity lambda"] or MyClass::MyFunction or myClassInstance::MyFunction

Grok variant on ChatGPT code when I gave Grok the var factory = code in full:
@Composable
inline fun <reified VM : ViewModel, T : Any> LoadOrInitUIState(
    navBackStackEntry: NavBackStackEntry,
    existing: T?,
    fromSavedState: (SavedStateHandle) -> T,
    factory: (SavedStateHandle, T) -> VM,
    content: @Composable (VM, T) -> Unit
) {
    val state = remember(existing) { existing ?: fromSavedState(navBackStackEntry.savedStateHandle) }
    val viewModel: VM = viewModel(
        viewModelStoreOwner = navBackStackEntry,
        factory = viewModelFactory { factory(navBackStackEntry.savedStateHandle, state) }
    )
    content(viewModel, state)
}
This may be complete crap. The example of how to use it is probably as long as the unfactored out code, and it doesn't seem to use viewModelFactoryWithHandle. This may or may not mean the idea doesn't work. Just noting down these code fragments before I lose them for consideration later more calmly.

*/

        composable(
            "editItem", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },
        ) { backStackEntry ->
            screenWithViewModel<EditItemViewModel, EditItemScreenUIContent>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editItemScreenUIContent = null },
                buildViewModel = { app, handle ->
                    EditItemViewModel(
                        app.priceTrackerRepository,
                        handle,
                        sharedViewModel.editItemScreenUIContent
                            ?: EditItemScreenUIContent.fromSavedState(handle)!!
                    )
                }
            ) { viewModel ->
                EditItemScreen(
                    viewModel, navController,
                    requestClose = {
                        navController.popBackStack()
                    })
            }
        }


        composable(
            "editSource", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },
        ) { backStackEntry ->
            screenWithViewModel<EditSourceViewModel, EditSourceScreenUIContent>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editSourceScreenUIContent = null },
                buildViewModel = { app, handle ->
                    EditSourceViewModel(
                        app.priceTrackerRepository,
                        handle,
                        sharedViewModel.editSourceScreenUIContent
                            ?: EditSourceScreenUIContent.fromSavedState(handle)!!
                    )
                }
            ) { viewModel ->
                EditSourceScreen(
                    viewModel, navController,
                    requestClose = {
                        navController.popBackStack()
                    })
            }
        }
    }
}

// TODO: ~/pc-sync/ai-chat-misc-to-move/grok-combo-box-and-alternate-ui.txt is a potentially
// valuable discussion, touching on some implementation ideas, design ideas (small tweaks and
// alternatives) etc and would probably be worth a re-read later.


// TODO: I should probably lock the app to portrait mode

// TODO: There is no colour in the app at all when running on P7! Material You active without me
// realising it? I suspect so - look at Theme.kt, which appears to support dynamic colours. This
// isn't a problem as such, but should make a note about it, and I may want to offer a setting which
// allows newer Android versions to choose the app's native theme.


// General note type comments to put somewhere appropriate in long term:

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
// Popup: This does (I think) "guarantee" that the stuff on the popup is "on top", although it still
// requires finicky hacks to trap focus and avoid touch input sometimes going to the screen
// underneath. The killer problem for me was that a simple editable TextField didn't work on it,
// even using a hardware keyboard in the emulator I never got to the point of trying it with an
// on-screen keyboard.
//
// Box with high Z-order: This visually ensures our fake dialog's stuff is "on top", but (as with
// Popup) in ways I don't fully understand, you need to stop touch input sometimes going to the
// screen underneath and without the separate context (?) created by Popup, the touch input hacks
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

// TODO: I just may need to enable Java desugaring to support older Android versions - this is
// probably just a one-off config.

// TODO: For some bizarre reason beyond my comprehension, check() and require() sometimes kill the
// app but without leaving a clear logcat trace, which makes it very hard to figure out what went
// wrong. So we use these instead.

inline fun devCheck(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        val msg = lazyMessage()
        Log.e("DevCheck", "FAILED CHECK: $msg", Throwable())
        throw IllegalStateException(msg) // same as check()
    }
}

// TODO: Technically this should throw IllegalArgumentException but I don't care. Using the two
// names allows me to preserve the distinction in the code FWIW but without duplicating the body of
// devCheck.
inline fun devRequire(condition: Boolean, lazyMessage: () -> String) =
    devCheck(condition, lazyMessage)

data class CurrencyFormat(
    val decimalPlaces: Int, // TODO: We may not actually need this, if it's baked into validation rules and not used elsewhere
    val prefix: String?,
    val suffix: String?,
    val validationRules: List<ValidationRule<String>>
)

// TODO: This takes a DataSet not a currency code because later on a DataSet may allow custom
// currency formatting which overrides whatever the current locale wants to do.
// TODO: Rename this from "getFoo" syntax to make it clear it's not "cheap"?
fun getCurrencyFormat(dataSet: DataSet, locale: Locale): CurrencyFormat {
    val currencyInstance = Currency.getInstance(dataSet.currencyCode)
    // currencyInstance will give us the number of decimal places, but it won't give us a
    // prefix or suffix to use - which we need for currency TextFields. So we ask it to
    // format a sample price and take the prefix and suffix from that.
    val numberFormat = NumberFormat.getCurrencyInstance(locale).apply {
        currency = currencyInstance
    }
    val sampleFormattedCurrency = numberFormat.format(1.0)
    Log.d(
        "MyApp",
        "sampleFormattedCurrency for ${dataSet.currencyCode} is '$sampleFormattedCurrency'"
    )
    val (prefix, suffix) = splitAroundDigits(sampleFormattedCurrency)
    return CurrencyFormat(
        decimalPlaces = currencyInstance.defaultFractionDigits,
        prefix = prefix.trim().ifBlank { null },
        suffix = suffix.trim().ifBlank { null },
        validationRules = numericValidationRules(
            locale,
            allowDecimals = true,
            allowZero = false,
            maxDecimals = currencyInstance.defaultFractionDigits
        )
    )
}

// TODO: Just possibly this could be used in the consistency hell stuff in home screen's flow pipeline
data class Versioned<T>(
    val version: Long,
    val value: T
)

fun <T> Flow<T>.withVersion(): Flow<Versioned<T>> = flow {
    var version = 0L
    collect { value ->
        emit(Versioned(version, value))
        version++
    }
}

fun <T> initialVersioned(initialValue: T): Versioned<T> =
    Versioned(version = -1L, value = initialValue)

// TODO: Rename something like ValidationTargetHandle?
// TODO: Might be nice to make members private, which probably requires moving to a file on its own
// along with the custom Modifier and using internal vsibility. This would stop e.g. "accidentally"
// passing the FocusRequester to Modifier.focusRequester() and avoiding the initialisation flag
// being updated.
class ScrollToFocusableHandle @OptIn(ExperimentalFoundationApi::class) constructor(
    val focusRequester: FocusRequester = FocusRequester(),
    var focusRequesterInitialised: Boolean = false,
    val bringIntoViewRequester: BringIntoViewRequester = BringIntoViewRequester(),
    var bringIntoViewOffset: Float = 0f,
    var bringIntoViewHeight: Int = 0,
    val errorHighlightBoxVisible: MutableState<Boolean> = mutableStateOf(false),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.scrollToFocusable(handle: ScrollToFocusableHandle, offset: Dp = 0.dp): Modifier {
    // Specifying a negative offset allows us to scroll to a bit above this composable. This is
    // useful when it may be wrapped in an ErrorHighlightBox.
    // TODO: Maybe we should attach this modifier to the ErrorHighlightBox, but there's tension
    // there as we also want to attach it to the "real" composable for focusing purposes, and if we
    // split the two things up we're losing a lot of the convenience of having it all in one place.
    handle.bringIntoViewOffset = with(LocalDensity.current) { offset.toPx() }
    return this
        //.focusRequester(handle.focusRequester)
        .onGloballyPositioned { coordinates ->
            handle.bringIntoViewHeight = coordinates.size.height
        }
        .bringIntoViewRequester(handle.bringIntoViewRequester)

}

// TODO: Not necessarily the best name, but although we could overload the focusRequester name, it feels confusing to do it.
fun Modifier.validationFocusRequester(handle: ScrollToFocusableHandle): Modifier {
    handle.focusRequesterInitialised = true
    return this.focusRequester(handle.focusRequester)
}

@OptIn(ExperimentalFoundationApi::class)
suspend fun scrollAndFocusTo(focusManager: FocusManager, handle: ScrollToFocusableHandle) {
    Log.d("MyAppScroll", "${handle.bringIntoViewOffset} ${handle.bringIntoViewHeight}")

    if (!handle.focusRequesterInitialised) {
        // If we didn't (couldn't meaningfully) initialise the focusRequester, that means the target
        // can't be focused. We therefore content ourselves with removing the focus from anything
        // else that has it. We do this before calling bringIntoView() since it may dismiss the
        // on-screen keyboard and in practice it looks much nicer to do it in this order.
        // TODO: I half wonder if we should be using Modifier.focusTarget() to make it possible to
        // focus things like segmented buttons. However, this seems to work and I haven't
        // experimented with alternatives.
        Log.d("MyApp", "clearFocus")
        focusManager.clearFocus()
    }

    val totalBorderThickness = handle.bringIntoViewOffset
    handle.bringIntoViewRequester.bringIntoView(
        Rect(
            left = 0f,
            top = -handle.bringIntoViewOffset,
            right = 0f,
            bottom = handle.bringIntoViewHeight + 2 * handle.bringIntoViewOffset
        )
    )

    if (handle.focusRequesterInitialised) {
        // I am a bit unsure as to why, but it seems to work much better to do requestFocus() *after*
        // bringIntoView(). The precise behaviour depends on whether the control already has the focus
        // and maybe whether there is a keyboard on screen already and what type it is.
        Log.d("MyApp", "requestFocus")
        // TODO: Should we maybe do a clearfocus first? That may or may not help if the problematic control already has the focus.
        handle.focusRequester.requestFocus()
        // TODO: Can/should we focus TextFields with the cursor at the end of the text?
    }

    // TODO: Highly speculative, but would a small delay before doing this give things like the keyboard time to appear first and improve visual appeareance? Or maybe we should set it to visible right at the top of this function so it's fully visible and gets a chance to influence things like bringinto view!??!?! probably doesn't work that way but maybe worth experimenting
    handle.errorHighlightBoxVisible.value = true
    delay(errorHighlightBoxVisibleTimeMillis)
    handle.errorHighlightBoxVisible.value = false
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun rememberScrollToFocusable(): ScrollToFocusableHandle {
    return remember {
        ScrollToFocusableHandle()
    }
}

// TODO: Grok magic, tweaked with help from my own brain and ChatGPT and Perplexity
@Composable
fun ErrorHighlightBox(
    hasError: Boolean, // TODO: rename "visible" or something, what's standard? "enabled"? It's not about "having an error", it's about our visibility.
    borderWidth: Dp = 2.dp,
    offset: Dp = defaultErrorHighlightOffset,
    modifier: Modifier = Modifier,
    validationTarget: ScrollToFocusableHandle,
    content: @Composable () -> Unit
) {
    var alpha = remember { Animatable(0f) }
    LaunchedEffect(hasError) {
        if (hasError) {
            // Start animating from completely transparent.
            alpha.snapTo(0f)

            // Pulse alpha while we're supposed to be visible.
            while (hasError) {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(1000, easing = LinearEasing)
                )
                alpha.animateTo(
                    targetValue = 0.1f, // TODO: experimental, was 0f
                    animationSpec = tween(1000, easing = LinearEasing)
                )
            }
        } else {
            // Fade out smoothly once we're no longer animating.
            // TODO: It would maybe be nice if we could always get to 1f *then* do this fade out
            // but it's probably faffy as hell.
            alpha.animateTo(targetValue = 0f, animationSpec = tween(500))
        }
    }

    val borderColor = MaterialTheme.colorScheme.error
    Box(
        modifier = modifier
            .drawWithContent {
                // Draw the content (e.g., TextField or SegmentedButton)
                // Useful for debugging: drawRect(Color.Green.copy(alpha=0.3f))
                drawContent()
                if (true /* hasError */) { // TODO: GET RID OF IF
                    // Draw an outline slightly larger than the content
                    val borderWidthPx = borderWidth.toPx()
                    val offsetPx = offset.toPx()
                    drawRect(
                        color = borderColor,
                        alpha = alpha.value,
                        style = Stroke(width = borderWidthPx),
                        topLeft = Offset(-offsetPx, -offsetPx),
                        size = size.copy(
                            width = size.width + 2 * offsetPx,
                            height = size.height + 2 * offsetPx
                        )
                    )
                }
            }
            .scrollToFocusable(validationTarget, offset = offset + 2 * borderWidth)
        // Add padding to ensure the outline isn't clipped
        //.padding(4.dp)
    ) {
        content()
    }

    /* TODO DELETE

    val borderColor = MaterialTheme.colorScheme.error
    val density = LocalDensity.current
    val borderWidth = 2.dp
    val borderPadding = 2.dp
    var contentSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .graphicsLayer(clip = false)
            .drawWithContent {
                drawContent()
                if (hasError) {
                    val strokeWidth = borderWidth.toPx()
                    val padding = borderPadding.toPx()

                    drawRect(
                        color = borderColor,
                        style = Stroke(width = strokeWidth),
                        topLeft = Offset(0f /* -padding */, -padding),
                        size = Size(
                            width = contentSize.width.toFloat()/* + 2 * padding*/,
                            height = contentSize.height.toFloat() + 2 * padding
                        )
                    )
                }
            }
    ) {
        // SubcomposeLayout gives us size of actual content
        SubcomposeLayout { constraints ->
            val placeables = subcompose("content", content).map {
                it.measure(constraints)
            }

            val width = placeables.maxOfOrNull { it.width } ?: 0
            val height = placeables.maxOfOrNull { it.height } ?: 0

            // Update size used by drawBehind
            contentSize = IntSize(width, height)

            layout(width, height) {
                placeables.forEach { it.place(0, 0) }
            }
        }
    }

    */

    /* TODO DELETE
    val borderWidth = 2.dp
    val borderPadding = 2.dp
    val borderColor = MaterialTheme.colorScheme.error
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { borderWidth.toPx() }
    val paddingPx = with(density) { borderPadding.toPx() }

    Layout(
        content = content,
        modifier = modifier.graphicsLayer(clip = false).drawWithContent {
            drawRect(Color.Green.copy(alpha = 0.3f))
            drawContent()
            if (hasError) {
                drawRect(
                    color = borderColor,
                    style = Stroke(width = strokeWidthPx),
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height)
                )
            }
        }
    ) { measurables, constraints ->

        val placeable = measurables.first().measure(constraints)

        // Increase size by borderPadding * 2 to reserve space around child
        val width = placeable.width + (paddingPx * 2).toInt()
        val height = placeable.height + (paddingPx * 2).toInt()

        layout(width, height) {
            // Place the child inset by borderPadding, so it doesn't get shrunk
            placeable.place(paddingPx.toInt(), paddingPx.toInt())
        }
    }
    */
}
/* TODO: Perplexity fragment which says it uses dp. for reference/tweaking later:
Box(
    modifier = modifier.drawWithContent {
        drawContent()
        if (hasError) {
            val borderWidthPx = 2.dp.toPx()
            val offsetPx = borderWidthPx / 2
            drawRect(
                color = pulseColor.copy(alpha = borderAlpha),
                style = Stroke(width = borderWidthPx),
                topLeft = Offset(-offsetPx, -offsetPx),
                size = Size(size.width + borderWidthPx, size.height + borderWidthPx)
            )
        }
    }
)

*/

// TODO: ChatGPT magic, review if keep - have hacked animations from 150ms to 1500ms just to test
@Composable
fun AnimatedSupportingText(
    text: String?,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.error,
    style: TextStyle = MaterialTheme.typography.bodySmall
) {
    var lastNonNullText by remember { mutableStateOf<String?>(null) }
    val visible = text != null
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "supportingTextAlpha"
    )

    // Update the last known good message only when new text is non-null
    if (text != null) {
        lastNonNullText = text
    }

    // Only show the text if it's supposed to be visible or still fading out
    if (lastNonNullText != null && alpha > 0f) {
        Box(
            modifier = modifier
                .animateContentSize(animationSpec = tween(1500))
                .alpha(alpha)
        ) {
            Text(
                text = lastNonNullText!!,
                color = color,
                style = style
            )
        }
    } else if (text != null) {
        // fresh message with alpha = 1
        Box(
            modifier = modifier
            //.animateContentSize(animationSpec = tween(1500))
        ) {
            Text(
                text = text,
                color = color,
                style = style
            )
        }
    }
}

data class PriceAnalysis(
    val augmentedPriceList: List<AugmentedPrice>,
    val priceClassificationThresholds: PriceClassificationThresholds?
)


// TODO: We may want to return a Price with swizzled internal double price value rather than having a custom AugmentedPrice, let's see how it goes.
data class AugmentedPrice( // TODO: not sure about name but experimenting
    val basePrice: Price, // TODO: just possibly we don't even want this embedded in here
    val sourceName: String, // saves faffing with associatedBy and remember in UI code
    val loyaltyPrice: Double,
    val ageDays: Long,
    val inflatedLoyaltyPrice: Double,
    val unitPrice: UnitPrice,
    val priceJudgement: PriceJudgement,
)

val inflationThresholdDays = 30L // TODO: rename staleThreshold or something? we use it for inflation, but it's about how we define "stale" really, and inflation only kicks in for stale prices
fun inflationAdjustedPrice(price: Double, ageDays: Long): Double {
    // TODO: Hard-coded threshold and inflation rate should be taken from settings
    if (ageDays < inflationThresholdDays) {
        return price
    } else {
        val annualInflationPercent = 5.0
        return price * (1.0 + annualInflationPercent / 100.0).pow((ageDays - inflationThresholdDays) / 365.25)
    }
}

enum class PriceJudgement {
    NONE,
    STALE, // TODO: rename TOO_OLD? this is probably for the ">=180 days" case, not the ">=30 days" case
    GOOD,
    OK,
    BAD
}

// TODO: Should this be a member of PriceJudgement??
fun judgePrice(augmentedPrice: AugmentedPrice, priceClassificationThresholds: PriceClassificationThresholds?): PriceJudgement {
    if (augmentedPrice.ageDays >= tooOldThresholdDays) {
        return PriceJudgement.STALE
    } else if (priceClassificationThresholds == null) {
        return PriceJudgement.NONE
    } else if (augmentedPrice.inflatedLoyaltyPrice < priceClassificationThresholds.good) {
        return PriceJudgement.GOOD
    } else if (augmentedPrice.inflatedLoyaltyPrice <= priceClassificationThresholds.bad) {
        return PriceJudgement.OK
    } else {
        return PriceJudgement.BAD
    }
}

// TODO: Should this be a companion function/constructor on AugmentedPrice or something like that?
fun augmentPrice(price: Price, source: Source, unitPriceDenominator: MeasureUnit?, candidateUnitPriceDenominators: List<MeasureUnit>): AugmentedPrice {
    val loyaltyPrice = price.price * source.loyaltyMultiplier
    // TODO: We could convert to floating point ageDays by getting .seconds and dividing by 86400, but it probably makes little difference in practice.
    val ageDays = Duration.between(price.confirmed, Instant.now()).toDays()
    val inflatedLoyaltyPrice = inflationAdjustedPrice(loyaltyPrice, ageDays)
    return AugmentedPrice(
        basePrice = price,
        sourceName = source.name,
        loyaltyPrice = loyaltyPrice,
        ageDays = ageDays,
        inflatedLoyaltyPrice = inflatedLoyaltyPrice,
        // TODO: It feels slightly off that we have to specify a denominator for our unit prices here, but I suppose it's OK - but maybe we could improve the API. We can't choose a "friendly" unit at this point since we don't have all the data across all sources yet (we're building it up).
        unitPrice = if (unitPriceDenominator != null) {
            getUnitPrice(inflatedLoyaltyPrice, price.measure, unitPriceDenominator)
        } else {
            getFriendlyUnitPrice(inflatedLoyaltyPrice, price.measure, candidateUnitPriceDenominators)
        },
        priceJudgement = PriceJudgement.NONE
    )
}
// TODO: I have a suspicion when I format prices to 2 d.p., it is truncating not rounding. May want to investigate this - do some tests with the different format functions, if more than one - and perhaps tweak options. Right now SuperiorStore milk is €2.86 for 2 litres, which shows as €0.81/pint, but some hacky debug output suggests that is really €0.8162 so it ought to round to €0.82/pint. OK, it would be good to check, but I suspect this was just me getting confused over a small inflation adjustment to the price between the ItemSourceInfo and the list across stores. Check. Even if that's right, this may suggest users could get confused too - but there may be little I can do about it, but think/discuss with LLM.

data class PriceClassificationThresholds(
    val good: Double,
    val bad: Double)

fun quantile(sortedValues: List<Double>, q: Double): Double {
    devRequire(q in 0.0..1.0) { "Expected q in [0, 1] but got $q" }

    // We could return null for empty, but in reality we don't expect this to happen and it feels
    // better to avoid making the result nullable.
    devRequire(sortedValues.isNotEmpty()) { "Expected non-empty list" }

    // It's slightly inefficient to be checking sortedValues is sorted every time, but for our tiny
    // lists it is very cheap and it might catch a bug causing invalid results to be generated.
    devRequire(sortedValues.zipWithNext().all { (a, b) -> a <= b }) { "Expected sortedValues to be sorted but got $sortedValues" }

    val doubleIndex = q * (sortedValues.size - 1)
    val lowerIndex = doubleIndex.toInt()
    // min() here is just paranoia in case of floating point rounding.
    val upperIndex = kotlin.math.min(ceil(doubleIndex).toInt(), sortedValues.size - 1)
    val fractionalIndex = doubleIndex - lowerIndex
    return sortedValues[lowerIndex] * (1 - fractionalIndex) + sortedValues[upperIndex] * fractionalIndex
}

val tooOldThresholdDays = 180L // TODO: should be in settings

// TODO: This code feels a bit awkward somehow, maybe the unit price calculation code needs refactoring and maybe augmentPrice should be inlined as this is its only caller and that *might* help. It also feels like we're having to pass far too much random stuff in as parameters.
fun analysePrices(dataSet: DataSet?, priceList: List<Price>, sourceList: List<Source>): PriceAnalysis {
    val sourceById = sourceList.associateBy { it.id }
    if (dataSet == null) {
        // TODO: having to do this so explicitly feels awful - the result is fine, but the implementation feels bad
        return PriceAnalysis(emptyList(), null)
    }
    if (priceList.isEmpty()) {
        // TODO: again, return value is fine but writing this here feels like a hack. this is all kind of related to
        // how the following code is evolving and it may be I need to refactor augmentPrice() and or some of the unitprice
        // stuff so I can just have things "flow through" more naturally even if some things aren't available. (remember
        // we may be in a case where the home screen is basically null all the way - it's not about data not being available yet,
        // it's about corner cases where there *is* no data.)
        return PriceAnalysis(emptyList(), null)
    }

    // TODO: I am not sure we exactly want "sibling" here - we don't necessarily have a single
    // system to pick from, or we maybe do/should but it's not so obvious - because we are in the
    // context of multiple prices and they might be using a mixture of the user's available systems
    // - so it's not as simple as "translating" price.measure.unit
    // TODO: It's not so obvious which unit family we want to use here - in the context of showing the price at a
    // particular source in ItemSourceInfo, we have "the unit the price was entered in for that source" to implicitly
    // select a family, but here we are working with multiple prices which may use a mix of families (imagine milk,
    // where in the UK we may have pints and litres at different stores). We probably don't want to ask the user to
    // specify a *preferred* unit family. We could potentially have each source price vote with its unit family, but
    // instead - and this is probably best, but will see how I feel later - we take the unit family of the cheapest
    // price. (At risk of stating the obvious, but it's easy to get lost in the details here, we are showing the
    // unit prices sorted as a list, so they all need to use the same denominator otherwise the list is not much
    // use.)
    val candidateUnitPriceDenominators = getSiblingMeasureUnits(
        dataSet,
        priceList.first().measure.unit,
        includeDisplayOnly = true
    )
    var unitPriceDenominator: MeasureUnit? = null
    var augmentedPriceList = priceList.mapNotNull { price ->
        // TODO: I don't think we can really be in a case where we have a Price but do not have the corresponding Source, but probably best to play it safe. (We fetched all the data "atomically" by combining flows so we shouldn't still be waiting for a query result, but maybe there's a corner case.)
        sourceById[price.sourceId]?.let { source ->
            val augmentedPrice = augmentPrice(price, source, unitPriceDenominator, candidateUnitPriceDenominators)
            unitPriceDenominator = augmentedPrice.unitPrice.denominator
            augmentedPrice
        }
    }.sortedBy { it.unitPrice }
    val recentEnoughPriceList = augmentedPriceList.mapNotNull { augmentedPrice ->
        if (augmentedPrice.ageDays >= tooOldThresholdDays) { null } else { augmentedPrice.inflatedLoyaltyPrice }
    }
    val priceClassificationThresholds = if (recentEnoughPriceList.size <= 2) { null } else {
        val lowerQuartile = quantile(recentEnoughPriceList, 0.25)
        val upperQuartile = quantile(recentEnoughPriceList, 0.75)
        val k = 0.1 // TODO: should be in settings?
        PriceClassificationThresholds(lowerQuartile * (1 - k), upperQuartile * (1 + k))
    }
    // TODO: This will happily return "all-OK" judgements if the prices are clustered. I think this
    // is probably a good thing - if we think the price is OK, we should have the at-a-glance
    // indicator say so, rathern than the user wondering if it's missing because we don't have
    // enough data or we're just in an "all OK, none good or bad" case.
    augmentedPriceList = augmentedPriceList.map { augmentedPrice ->
        // TODO: This *will* classify prices even if they are themselves stale - this is probably good, *but* the UI should show
        // the "confirmed x days ago" thing in error color if the price is stale. (We do want to show the recommendation anyway,
        // since maybe the user is checking the store out at home before deciding if they want to go there, so showing the
        // recommendation is probably desirable.)
        augmentedPrice.copy(priceJudgement = judgePrice(augmentedPrice, priceClassificationThresholds))

    }
    return PriceAnalysis(augmentedPriceList, priceClassificationThresholds)
}

/* TODO TEMP TEST CODE FOR MEASUREDVALUE
val foo = MeasuredValue(5.0, MeasureUnit.KG)
val bar = MeasuredValue(2.3, MeasureUnit.ML)
val quux = bar.to(MeasureUnit.FLOZ)
Log.d("MyApp", quux.toString())
var baz = foo + bar
Log.d("MyApp", baz.toString())
*/

// TODO: I have completely ignore "unlikely" errors (like exceptions being thrown when accessing the
// database) in most of this code - what can/should we do about this? I suspect most such errors are
// basically unrecoverable and it's semi-OK if the process just dies, but I'm not sure and it would
// be good to read up on best practices.

// TOOD: I should probably limit all text fields to approx 1000 characters just to stop the user going crazy.

// Note to self: Locale.getDefault() is initialised to the current locale when our app process
// starts and is not automatically updated if the user changes the system locale while the app is
// running. However, Compose's LocalConfiguration.current.locales[0] is updated live and immediately
// reflects locale changes, triggering recompositions as needed.
//
// It is possible (though risky, due to race conditions with recomposition and non-composable code)
// to update Locale.getDefault() via Locale.setDefault() to match
// LocalConfiguration.current.locales[0], but we avoid this.
//
// Our strategy:
// - For read-only screens, we react live to locale changes using
//   LocalConfiguration.current.locales[0], passing it as needed to non-composable code.
// - For editing screens, we "freeze" the locale from LocalConfiguration.current.locales[0] at the
//   start of editing (when locale-sensitive string representations are generated) and use this
//   locale for the duration of the editing session. This avoids issues like ambiguous
//   interpretation of "," or "." as decimal/grouping separators. (As string representations of
//   doubles may be temporarily un-parseable during editing, we cannot reliably parse them to double
//   in the old locale and re-stringify in the new locale.)
//
// Editing screens without locale-sensitive data can be treated as read-only from this perspective.
//
// In general, we avoid Locale.getDefault() and require explicit locale parameters to functions
// (without defaults) to ensure we always consider the source of our locale.

// TODO: Eventually will need to remove misc Log.d() lines and/or replace them with permanent well-thought-out ones if that is not inefficient.

// TODO: If I double click on e.g. Newco when editing sources, I seem to get the edit screen open twice. I suspect this is one of those cases where I need to add debouncing.

// TODO: Note that when we save a source/dataset/item after editing, we need to refuse to save if
// there is another entry (excluding any old version of us) with the same name, and ideally with a
// "very similar" name (e.g. up to case and with inter-word whitespace squashed and leading/trailing
// space trimmed), to avoid confusion. - OK, I think I have fixed this but test all the different
// edit static screens later.

// TODO: Should we remember current product and source (remember they *may* be null anyway) for each
// data set?

// TODO: Maybe I should have a settings option which completely hides or just disables all the
// "delete" buttons. Users can turn that off if it makes them feel safer. We could possibly, if it
// isn't a UI nightmare, allow delete to be enabled for the next 10 minutes or something, then
// automatically re-disable. My thinking here is deletes could be very destructive of valuable data
// and in general you do not really want to delete stuff, unless you manage to add something
// completely junky rather than just adding something with a typo and needing to edit it to fix it,
// or cancelling the add before you finish it. We could also make the settings option tri-state, with
// an intermediate setting (which could perhaps even be the default) where delete buttons are shown/enabled (whatever I think best)
// for "non scary" deletes (product X is in the database *but no price data is attached* etc) but
// hidden/disabled for "scary" deletes (price data exists which would get cascade deleted).

// TODO: M3 recommends using a "container transform pattern" to transform FAB into a full-screen
// dialog. Not sure if I can or should do this, but might be worth trying. (Do remember that as
// noted elsewhere, my "full screen dialogs" are actually full screens in their own right and I don't
// have enough hair to switch away from that, especially not just to make an animation work. The
// animation may not depend on being a "true dialog", of course.) I do wonder - not seen
// anything in docs - if this also suggests some kind of "expansion" animation should happen from
// the clicked-on source/item/dataset into the full screen dialog to edit it. Currently the code is
// doing the "standard" full screen dialog slide in from bottom animation anyway. (I had some
// discussions with LLMs about what to do for edit not add cases, where you click on a list item to
// open the edit dialog - from a UI design perspective, not how/ease of implementation. Using the
// standard slide in transform over a "container transform" was favoured 2:1 here. See how I feel
// later, and I'm far from confident I can do the FAB container transofrm anyway and that would
// definitely be the thing to try first (as it *is* called out in MD3 specs).)

// TODO: I do wonder if I've over-exaggerated the need to pass data between screens for perfect
// first compositions. For screens with complex data-dependent layouts I could see this, and
// although there's no passing data in, the home screen probably would be janky without the "all or
// nothing" combine-driven flow. For simple list type screens like the "Edit X" stuff it may be fine
// (and I could experiment by removing the initial list pass through). For stuff like Edit price I
// am not sure, but it's pssible these might look OK, although they might not (e.g. the text field
// titles would "slide up" from the "no data" prompt position to the "label above actual data"
// position as the data filled in.

// TODO: If/when we have some kind of auto-backup or export state thing, it might be nice to hook
// this into delete operations (perhaps just cascading ones???) and auto-backup before deleting.
// Minor concern here if the user is doing a lot of deletions that we don't end up with lots of
// auto-backups, we could just possibly try to be clever and only do this if we haven't done an
// auto-backup within the last hour or so. This limits the window of data loss while keeping backup
// volume down.

// TODO: When I finally write some actual code to "analyse" the price data coming back from the db
// on the home screen (inside the viewmodel, probably inside its data pipeline before we emit to the
// UI), it will probably be fine as the calculation is unlikely to be that heavy but I should keep
// an eye open for the possibility of needing to move that work over to a new thread
// (Dispatchers.IO) with some care about thread sync at that point, so as not to block the main
// thread - if my code runs on the main thread and is not calling suspend functions, that blocks all
// the other stuff including the Ui and may make it unresonsive. There is value in the UI being
// responsive because at least the user can e.g. change the current item or source if they change
// their mind without being blocked. But don't rush into making this multi-threaded for no reason,
// as I say the calculation is probably pretty light, just be aware and maybe put a TODO in the
// final version if it isn't multi-threaded to keep an eye on this once it's maybe pulling a bit
// more data in a "realistic" database. Probably fine even then, given we're filtered down to a
// single product and there can only be one price per store.

// TODO: What happens/should happen if e.g. a user's "display unit" for a save price is in imperial
// and then they turn imperial off for the collection? Will we still show in imperial by default but
// not allow it in the drop down? Will we force display into a unit (picking the best on a
// significant digits basis or something) in an allowed system?

// TODO: ErrorHighlightBoxes and their offsets and the general layout of the forms they highlight is
// probably a bit inconsistent and could do with a review.

// TODO: General Kotlin point which may simplify my code - unlike in say C++, you can apparently
// "call methods" on nulls, e.g. stringvariable.orEmpty().

// TODO: General note: when we're editing a thing and changing its name, there is some possibility
// of the user getting confused e.g. about what they are deleting or (wrt the "unique name" check)
// what the name "should be". Wrt deleting, this problem is solved by having the delete on the list
// screen not the edit screen, but I really don't like that as I don't want delete to be that
// "prominent". This doesn't solve the "user changed name on screen and doesn't know what it
// originall was" concern. In practice this is unlikely and not a huge deal. I do half wonder if we
// should show the original name somewhere in the top app bar, but then that could get confusing
// when they are editing it ("which is current?"). Probably best as it is, but wanted to make a note
// to think about this. One possibility might be for the delete confirmation dialog to mention the
// original name *if* it has been changed, but that might also be confusing (especially if the
// change is minor, not a wholesale replacement of "Coffee" with "Eggs" or somethimg like that) and
// it might be slightly fiddly to implement. We could also potentially show the original name of
// the item on the main form (indented or greyed out or whatever), although right now that feels a
// bit awkward. (I suppose we could maybe use the name textfield's supportingText for this when it
// isn't showing an error, although I am still not sure it would look right even ignoring the error
// use, and users might find themselves wondering how to get back to the grey message when an error
// occurs, although again probably not and we can only second-guess hapless user behaviour so much.)

// TODO: On Lenovo laptop, the "main" Android Studio text window with the two toolbar things down
// the left and right would fit better with 95 character lines than 100. Since there's not much in
// it, maybe we should adopt that as our standard line width?

// TODO: May want to semi-formally document that "state" for a screen is "what's in the screen's
// view model" (and arguably also in remembered stuff in composable etc), while "content" is what
// gets passed in from the "caller" via the sharedviewmodel mechanism. This may help me feel better
// and be more consistent about naming variables functions around the whole sharedviewmodel thing
// and also the resulting structure inside the fooscreenviewmodel.

// TODO: We should probably implement a "recycle bin" type delete - have a "deleted" flag on all the
// tables, and when something is deleted we set that. (We would not cascade-set this if we e.g.
// delete a data set.) We can then undelete (subject to verifying names are still unique - deleted
// things would not count towards uniqueness checks). This is a UI faff because it means three-ish
// screens to select thigns to undelete, and maybe some other facility somewhere else to purge
// some/all waste bin things for real. But it probably is the way to go long term, even if it's not
// part of MVP.

// TODO: The list of prices for product across stores at bottom of home screen should probably be
// clickable per item to expand into a read-only explanation of how the "effective price" was
// arrived at (store level discounts, pseudo-inflation penalties, etc) and maybe also the same
// "Good/bad/whatever price" recommendation we show in the "specific store" card (calculated the
// same way).

// TODO: For the record, I used scaling 61% when importing app-icon-4.svg as a new image asset for
// the icon.

// TODO: Validation text might be slightly warmer but still brief (which I think is desirable) e.g.
// "Please enter a value" or "Pack size is required" or "Enter a valid number". (vs "Can't be
// empty", "Must specify a pack size", "Invalid number" - I just made these up talking to ChatGPT,
// they are not necessarily what the code actually says, I didn't check.) Perhaps don't use "Please"
// - it may seem repetitive if we have lots of validation failures.

// TODO: It may be desirable for users to be able to outright delete the price of a product at a
// store - imagine they haven't visitied the store in a year and don't plan to and are sick of
// seeing the outdated inflation-adjusted price with little connection to reality. Of course they
// could just delete the store, but it's nice to give them the choice to delete just some prices -
// maybe the store sells some items nowhere else does, so keeping *those* item prices around is
// worth something.

// TODO: I am thinking following extensive and confusing discussion with ChatGPT that we make
// good/OK/bad judgements based on Buffered IQR=[Q1×(1−k), Q3×(1+k)] where k =0.05 or 0.1 (configurable by user of course). Q1 is 25th percentile, Q3 is 75th percentile.
// We only ever offer judgement if:
// - our store's price is <=inflation threshold (30?) days old (if it's older, our "judgement" is "Stale price - please check")
// - there are two other stores with prices <=old data threshold (180 days?)
// - our judgement is "good" if adjusted price is < low end of buffered IQR, "OK" if inside buffered IQR, "bad" if > high end of buffered IQR
// - if all prices are "OK" we don't offer any judgement (though I guess we could? not sure)
// The idea of buffered IQR is that if the data is tightly clustered, the 5th percentile or whatever is not really significantly "better" than the 95th percentile. We don't want to use absolute amounts to make this kind of judgement.
// We should probably call our inflation "pessimistic inflation rate" in UI, and default it to 5% or 10% per year. For prices >inflation thresold old, we start applying it compounded daily *from the threshold* (not from day 0) - we don't want a sudden big inflation jump just because a price became "eligible" for inflation.

// TODO: "effective price" (after loyalty scheme and inflation) may be better than the "adjusted
// price" terminology I think I have been using

// TODO: Possibly tapping on a row in the list of unit price by store should either a) open a "stats
// for nerds" page (maybe not too heavy a one), which might for example help the user understand
// what any icons-with-no-text (e.g. "this is stale") icons mean and/or might show where the
// effective price comes from. Or b) set the "Store" dropdown to that store??

// TODO: I am half wondering if I could somehow (the full-width dropdown doesn't help) squeeze a
// triple dot menu at the top right of the "store" card on the home screen to allow access to things
// like price history or more detailed info. MD3 card guidelines do say this can be at the upper *or
// lower* right corner of the card, so maybe if I moved "Edit" and "Confirm" to be left aligned
// (though this feels a bit of a shame) and edged the "Good price" recommendation up onto another
// line or put it between those now-left-aligned buttons and an overflow button, that *might* work.

// TODO: I am starting to think (and have already said so in other TODOs, probably) that the
// ItemSourceInfo card should perhaps be expanded a bit and have text messages saying things like
// "Price is old, please try to update it" *as well as* the judgement (if any).