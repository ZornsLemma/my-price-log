@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.composetutorial // TODO: change this!

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
import androidx.activity.compose.BackHandler
import androidx.navigation.compose.rememberNavController
import android.os.Bundle
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
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.navigation.NavBackStackEntry
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withTimeoutOrNull
import java.text.DecimalFormatSymbols
import java.util.concurrent.Executors

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

    companion object {
        fun fromValue(value: Int): QuantityType? {
            return entries.find { it.value == value }
        }
    }
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
        false),
    IMPERIAL_GAL(
        206,
        setOf(UnitFamily.IMPERIAL),
        QuantityType.VOLUME,
        "gal",
        3, // allow for eighths
        4546.09,
        false),

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
    val relevantMeasureUnits = MeasureUnit.entries.filter {
        it.quantityType == quantityType &&
                it.unitFamilies.any { it in relevantUnitFamilies } &&
                (!it.displayOnly || includeDisplayOnly)
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
    val quantityType: QuantityType get() = unit.quantityType

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
        "${formatDouble(value, minDecimals = 0, maxDecimals = unit.maxDecimals, useLocaleGrouping = false, locale)} ${unit.symbol}"
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
                    .setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                        Log.d("MyApp", "SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, Executors.newSingleThreadExecutor())
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
                                    // TODO: I should probably have a demo set using a currency like JPY which doesn't have 2dp - or perhaps better, have something I can turn on for debug builds which will do that, but don't pollute the user initial database with it
                                    // TODO: We should maybe - perhaps not worth worrying about - avoid using the demo data designed for 2dp currencies with e.g. JPY, if only by forcing the currency to be something else even if that's the system default, or perhaps applying a multiplier of 10^(2-currencydps) to all the prices just so they are "readable"
                                    val dataSetId = db.dataSetDao().insert(
                                        DataSet(
                                            name = "Demo",
                                            currencyCode = "EUR", // TODO TEMP HACK Currency.getInstance(Locale.getDefault()).currencyCode,
                                            allowMetric = true,
                                            allowImperial = true,
                                            allowUSCustomary = false
                                        )
                                    )
                                    val dataSetId2 = db.dataSetDao().insert(
                                        DataSet(
                                            name = "Demo 2",
                                            currencyCode = "AUD",
                                            allowMetric = true,
                                            allowImperial = false,
                                            allowUSCustomary = true
                                        )
                                    ) // TODO TEMP HACK
                                    val dataSetId3 = db.dataSetDao().insert(
                                        DataSet(
                                            name = "Demo 3",
                                            currencyCode = "AUD",
                                            allowMetric = true,
                                            allowImperial = false,
                                            allowUSCustomary = true
                                        )
                                    ) // TODO TEMP HACK
                                    val item21 = db.productDao().insert(
                                        Item(
                                            dataSetId = dataSetId2,
                                            name = "Demo 2 Item",
                                            defaultUnit = MeasureUnit.G
                                        )
                                    )
                                    val itemIdGroundCoffee = db.productDao().insert(
                                        Item(
                                            dataSetId = dataSetId,
                                            name = "Coffee (ground)",
                                            defaultUnit = MeasureUnit.G
                                        )
                                    )
                                    val itemIdWholeMilk = db.productDao().insert(
                                        Item(
                                            dataSetId = dataSetId,
                                            name = "Milk (whole)",
                                            defaultUnit = MeasureUnit.L
                                        )
                                    )
                                    val itemIdTeabags = db.productDao().insert(
                                        Item(
                                            dataSetId = dataSetId,
                                            name = "Teabags",
                                            defaultUnit = MeasureUnit.EACH
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
                                    val sourceIdNewco = db.sourceDao().insert(
                                        Source(
                                            dataSetId = dataSetId,
                                            name = "Newco"
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

// TODO: This interface is here to help with mocking the database during testing. I may want to do
// this, so let's keep it around for now.
interface PriceTrackerRepository {
    fun getAllDataSets(): Flow<List<DataSet>>
    fun getAllItems(dataSetId: Long): Flow<List<Item>>
    fun getAllSources(dataSetId: Long): Flow<List<Source>>

    fun getPricesForItem(dataSetId: Long, itemId: Long): Flow<List<Price>>

    suspend fun updateOrInsertPrice(price: Price)
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

    // TODO: Tempish note (maybe make permanent) - I discussed with ChatGPT and it seemed to make
    // sense - the repository should take "validated domain level" entities (where we aren't just
    // reusing the database entities throughout all levels for simplicity - which we aren't with
    // Price). So this should take a *Price* and convert it to a PriceEntity for writing, and there
    // shouldn't be any user-error-catching validation here - this might go wrong, but it would be
    // down to hardware failures or bugs in my code. The viewmodel-ish layer code is responsbile
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
        initializer {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
            val savedStateHandle = createSavedStateHandle()
            EditPriceViewModel(app.priceTrackerRepository, savedStateHandle)
        }
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
    @TypeConverter
    fun fromQuantityType(quantityType: QuantityType?): Int? {
        return quantityType?.value
    }

    @TypeConverter
    fun toQuantityType(value: Int?): QuantityType? {
        return value?.let { QuantityType.fromValue(it) }
    }

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
    @ColumnInfo(name = "allow_us_customary") val allowUSCustomary: Boolean
) : Parcelable

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
    val name: String
) : Parcelable

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
    val price: Double,
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
        // TODO: I think this check is technically redundant because using itemQuantityType to
        // determine the base unit will cause an internal check error if measure's own unit is a
        // different type - but this is maybe a bit more explicit.
        val measureQuantityType = measure.unit.quantityType
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

    // TODO: Is this sort case-insensitive? If not I may need to sort myself after, and thus don't
    // need this order by here
    @Query("SELECT * FROM data_set ORDER BY name ASC")
    fun getAllDataSets(): Flow<List<DataSet>>
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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(price: PriceEntity): Long

    @Upsert()
    suspend fun upsert(price: PriceEntity)

    @Query(
        "SELECT price.*, item.default_unit FROM price JOIN item ON price.item_id = item.id " +
                "WHERE price.data_set_id = :dataSetId AND price.item_id = :itemId"
    )
    fun getPriceWithItemEntityForItem(
        dataSetId: Long,
        itemId: Long,
    ): Flow<List<PriceWithItemEntity>>
}

// TODO: ChatGPT magic
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
    private val _uiState = MutableStateFlow<Pair<Boolean /* loading */, HomeScreenUIContent>>(
        Pair(
            false,
            HomeScreenUIContent.createEmpty()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        // This forces the delegate to initialize safely on the main thread TODO: VOODOO
        val unused = app.dataStore

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

        val TODORENAMEMEFLOW = combine(
            selectedSourceIdFlow,
            combinedDatabaseFlow
        ) { _, it -> it }

        // completeUIStateFlow delivers complete, consistent results which reflect the user's
        // selection. However, it doesn't make any guarantees as to how long it takes to emit after
        // allUserInputFlow emits.
        val completeUIStateFlow =
            TODORENAMEMEFLOW.flatMapLatest { (dataSetList, taggedItemListAndSourceList, taggedPriceList) ->
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
                    // delay(5000) // TODO HACK
                    flowOf(
                        HomeScreenUIContent(
                            dataSet,
                            dataSetList,
                            item,
                            itemList,
                            source,
                            sourceList,
                            priceList
                        )
                    )
                }
            }

        viewModelScope.launch(Dispatchers.Default) {
            // Add the "loading" flag to the UI state flow, rather than allowing arbitrarily long
            // delays before the user sees any kind of response. Note that because we use
            // collectLatest(), if the user changes the inputs the timeout starts again, which is
            // what we want.
            val TODO1 = allUserInputFlow.flatMapLatest { it -> // TODO: RENAME "it"
                var newUIContent = withTimeoutOrNull(spinnerDelayMillis) {
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

            val TODO2 = completeUIStateFlow.map { Pair(false /* loading */, it) }

            // TODO: Is there a risk with merge().collectLatest() here that a "loading" state will
            // somehow come *after* the corresponding *loaded* state? If so we'd end up stuck with
            // the scrim up forever. I am not sure there *is* a risk, but one possible fix *might*
            // be to have the "allUserInput-only" flow (the one with the timeout) *redo the
            // collection* in the "we timed out" branch after it emits the "loading=true" state -
            // there should not be any reordering *within* flows from the merge, right? And if we
            // put a distinctUntilChanged() after the merge that will catch any cases where we get a
            // duplicate emission because the database flow also emits the same thing at
            // approximately the same time
            val TODO3 = merge(TODO1, TODO2)

            TODO3.collectLatest { todoRename ->
                Log.d("MyFoo", "newUIState")
                _uiState.value = todoRename
            }
        }
    }
}

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

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
const val spinnerDelayMillis = 200L

// This value is a trade-off between showing the user validation failures ASAP and not annoying them
// by showing transient validation failures while they are in the middle of actively editing. This
// feels reasonable-ish and we can always tweak it later.
const val defaultValidationMessageDelayMillis = 1000L

val screenBorder = 8.dp

// TODO: MD3 specs say there should be a 24.dp horizontal border, but this seems quite ugly. The
// left hand edge of the dialog's body controls don't line up with the close icon and the right hand
// edges don't line up with the right hand edge of the "Save" text button. Some of the screenshots
// in the documentation seem to show some but not all of these misalignments. It just feels
// half-baked and inconsistent so I'm going to go with this.
val fullScreenDialogBorder = 16.dp

// MD3 says 12.dp but MyExposedDropdownMenuBox's dropdown item text doesn't line up with the parent
// TextField text with that. TODO: We could override it for that specific case and use 12.dp for
// other menus?
val menuLeftPadding = 16.dp

// Seems best to make the right padding symmetrical.
val menuRightPadding = menuLeftPadding

// TODO: RENAME THIS IF IT SURVIVES REFACTORING
@Composable
fun MainScreen(
    dataSet: DataSet?, dataSetList: List<DataSet>, onSelectedDataSetIdChange: (Long) -> Unit,
    item: Item?, itemList: List<Item>, onSelectedItemIdChange: (Long) -> Unit
) {
    var showItemSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Collection Selector
        MyExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth(),
            selectedId = dataSet?.id,
            onValueChange = { onSelectedDataSetIdChange(it) },
            label = { Text("Collection") },
            items = dataSetList ?: emptyList(),
            getId = { it.id },
            getLabel = { it.name },
        )
        // TODO: If we have no data sets, we should (analogous to how the source dropdown works)
        // show a supportingText about selecting one *and hide the rest of the UI*. Nothing makes
        // sense without a dataset, there is no way to pick a product or source. This probably means
        // we need support from our parent (or this needs moving up into the parent) to do that.

        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(color = Color.Red)
        )

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
                    contentDescription = "Search Products",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            colors = myTextFieldColors(true) // TODO: "true" is a hack, we should set parameter based on focus roughly as we do in MyExposedDropdownMenu
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
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ItemWithDropdown(
            dropdownModifier = Modifier.width(with(LocalDensity.current) { textFieldWidth.toDp() }),
            selectedId = selectedId,
            onValueChange = onValueChange,
            onExpand = { isExpanded = it },
            items = items,
            getId = getId,
            getLabel = getLabel,
        ) {
            val itemMap = items.associateBy { getId(it) }
            val TODOPULLEDOUT: String = if (selectedId == null) "" else {
                val item = itemMap[selectedId]
                if (item != null) getLabel(item) else "Invalid ID $selectedId"
            }
            TextField(
                value = TODOPULLEDOUT,
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
                colors = myTextFieldColors(isExpanded)
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

fun formatPrice(amount: Double, dataSet: DataSet, locale: Locale): String {
    // At least on Android this doesn't throw for invalid three-letter currency codes but it will
    // throw if given currency code "AAAA", so it seems safest to catch exceptions and have a
    // fallback, even if it's not great.
    try {
        val numberFormat = NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance(dataSet.currencyCode)
        }
        return numberFormat.format(amount)
    }
    catch (e: Exception) {
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
data class UnitPrice(val numerator: Double, val denominator: MeasureUnit)

fun getUnitPrice(amount: Double, measure: MeasuredValue, denominator: MeasureUnit): UnitPrice =
    UnitPrice(amount / measure.asValue(denominator), denominator)

// TODO: Note that we don't currently use the numerator of the returned UnitPrice - this might be
// fine, but it suggests we could simplify the return type to just MeasureUnit. OTOH, we've got to
// *calculate* the numerator anyway, so maybe we might as well pass it back in case it's handy in
// some other case?
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
    selectedId: ID?,
    onValueChange: (ID) -> Unit, // TODO: follow naming convention of MyExposedDropdownMenUBox
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

    Box(modifier = modifier.clickable { expanded = true; onExpand(expanded) }) {
        content()

        var previousItem: T? = null
        DropdownMenu(
            modifier = dropdownModifier,
            expanded = expanded,
            onDismissRequest = { expanded = false; onExpand(expanded) }) {
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
                        onExpand(expanded)
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
                // TODO: FWIW a quick discussion with ChatGPT suggests it is reasonable for i18n to
                // have some kind of format substitition to generate a unit price string analogous
                // to the one I'm using here. So having a single "Unit price" field is probably
                // reasonable, and it does feel like the clearest way to express it.
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
@OptIn(ExperimentalMaterial3Api::class)
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
            Log.d("MyApp", "ISI dataset ${dataSet}")
            Log.d("MyApp", "ISI item ${item}")
            Log.d("MyApp", "ISI source ${item}")
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
                val priceList = itemPriceList.filter { it.sourceId == source!!.id }

                if (priceList.isEmpty()) {
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
                    devCheck(priceList.size == 1) { "Expected one prices for a product and store, but got ${priceList.size}" }
                    // TODONOW: Should we do: "val price = priceList[0]" and simplify all the following code?

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
                        ) { // TODO: quite like this, but maybe "Shelf price"?
                            // TODO: There might be an argument for designing the UI to separate the
                            // price and quantity here, then we side-step the internationalisation
                            // issues of "for", which is *probably* tractable but might be a
                            // problem. If I really prefer the UI with a single text string
                            // containing "for", don't let this put me off sticking with it.
                            Text(
                                "${
                                    formatPrice(
                                        priceList[0].price,
                                        dataSet,
                                        LocalConfiguration.current.locales[0]
                                    )
                                } for ${
                                    priceList[0].measure.toDisplayString(LocalConfiguration.current.locales[0])
                                }" /*, color = MaterialTheme.colorScheme.onSurface*/
                            )
                        }

                        // TODO: Label this "Confirmed" to match the button? Or "Last confirmed", but bit long?
                        LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Confirmed" /* "Last checked" */) {
                            RelativeTimeText(priceList[0].confirmed)
                            // TODO: would it be helpful to color code this and/or show an icon
                            // ("!"?) if this is "old"? maybe even with an ascending amber/red
                            // "severity" (and correspondingly different icons?)
                        }

                        val relevantUnitFamilies =
                            remember(dataSet) { getRelevantUnitFamilies(dataSet) }

                        val relevantUnitList =
                            remember(dataSet, priceList[0].measure.unit.quantityType) {
                                getRelevantMeasureUnits(
                                    dataSet,
                                    priceList[0].measure.unit.quantityType,
                                    includeDisplayOnly = true
                                )
                            }
                        var selectedUnitPriceUnit by rememberSaveable(dataSet, priceList) {
                            val candidateDenominators = getSiblingMeasureUnits(
                                dataSet,
                                priceList[0].measure.unit,
                                includeDisplayOnly = true
                            )
                            val friendlyUnitPrice = getFriendlyUnitPrice(
                                priceList[0].price,
                                priceList[0].measure,
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
                                priceList[0].price,
                                priceList[0].measure,
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
                                var previousItemUnitFamily =
                                    previousItem.unitFamilies.intersect(relevantUnitFamilies)
                                var itemUnitFamily =
                                    item.unitFamilies.intersect(relevantUnitFamilies)
                                previousItemUnitFamily != itemUnitFamily
                            },
                            selectedId = selectedUnitPriceUnit,
                            onValueChange = { selectedUnitPriceUnit = it })

                    }

                    if (priceList[0].details.isNotEmpty()) {
                        Row(modifier = Modifier.padding(bottom = 8.dp)) {
                            LabeledItem("Notes") {
                                Text(priceList[0].details)
                            }
                        }
                    }

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
        if (priceDouble == null || measureValueDouble == null) {
            return null
        } else {
            return Price(
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

fun TextOrNull(string: String?): @Composable() (() -> Unit)? {
    if (string == null) {
        return string
    } else {
        return { Text(string) }
    }
}

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
            if (savedEditablePrice != null && savedOriginalPrice != null && savedDataSet != null && savedItem != null && savedSource != null) {
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

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    navController: NavHostController,
    onEditPriceClick: (HomeScreenUIContent) -> Unit
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
        onEditPriceClick = { onEditPriceClick(uiContent) } /* TODO DELETE (uiContent)
            vm.setEditPriceScreenStateFromHomeScreenState(uiContent)
            // TODO: I don't know if this random UUID is necessary or helpful or harmful any more,
            // need to experiment/think about this once I finish re-implementing the price edit
            // screen.
            navController.navigate("fullScreenDialog/${UUID.randomUUID()}")
        } */
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
            LaunchedEffect(visible) {
                if (visible) {
                    delay(delayMillis)
                    showScrim = true
                } else {
                    showScrim = false
                }
            }
        } else {
            showScrim = visible
        }

        if (showScrim) {
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = {
                    Log.d("MyApp", "ODR");
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

@Composable
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
    sourceListRaw: List<Source>,
    onSelectedSourceIdChange: (Long?) -> Unit,
    itemPriceListRaw: List<Price>,
    onEditPriceClick: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

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
                        MyDropdownMenuItem(text = { Text("Edit product list") }, onClick = {
                            menuExpanded = false
                            // TODO: Handle navigation or action
                        })
                        MyDropdownMenuItem(text = { Text("Edit categories") }, onClick = {
                            menuExpanded = false
                            // TODO: Handle navigation or action
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
                onSelectedItemIdChange = onSelectedItemIdChange
            ) // TODO: rename this

            Spacer(
                modifier = androidx.compose.ui.Modifier
                    .height(
                        8.dp
                    )
                    .fillMaxWidth()
                    .background(color = Color.Red) // TODO DEBUG HACK
            )

            if (dataSet != null) {
                Log.d("MyApp", "HSS dataSet ${dataSet}")
                Log.d("MyApp", "HSS item ${item}")
                ItemSourceInfo(
                    dataSet = dataSet,
                    item = item,
                    source = source,
                    sourceList = sourceListRaw,
                    onSelectedSourceIdChange = onSelectedSourceIdChange,
                    itemPriceList = itemPriceListRaw,
                    onEditPriceClick = onEditPriceClick
                )
            }

            Spacer(
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
    Log.d("MyApp", "EditPriceScreenViewModel $vm uiContent=${vm.uiContent}")
    devCheck(vm.uiContent != null) {
        "EditPriceScreenViewModel's uIContent should have been set to non-null before navigating to screen"
    }
    val uiContent = vm.uiContent!!

// TODO: Can I get rid of saveInitiated and instead set the state inside the viewmodel to "idle"
// when we are not saving? The frequency with which we check it suggests it might be more
// painful to get rid of it. but if we track this, the distinction between idle and saving is
// mostly meainingless (the state never gets set back to idle) and we should maybe merge those
// states into a vague "meh" state.
// TODO: Some of this remember stuff should maybe move into the ViewModel
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
// I can't help thinking we can simplify this by storing a close-debounce flag in the ViewModel, which
// ought to be re-created from scratch every time we are "truly re-entered" (either because popBackStack()
// discards the old state or because the random UUID trick effectively guarantees this - I am far from
// clear what the actual reality of how popBackStack() works is).
    var isNavigating by remember(navController.currentBackStackEntry) {
        mutableStateOf(false)
    }

    fun requestCloseDebounced() {
        // We need isNavigating to de-bounce the close button so we don't invoke requestClose()
        // (which probably calls popBackStack() and is therefore not idempotent) if the user double
        // taps the close button quickly. (We may not need this for other ways of closing, but it
        // shouldn't hurt and is probably safer.)
        if (!isNavigating) {
            isNavigating = true;
            requestClose()
        }
    }

    fun requestDismiss() {
        if (uiContent.editablePrice.value != uiContent.originalPrice) {
            showConfirmDialog = true
        } else {
            requestCloseDebounced()
        }
    }

    BackHandler {
        if (!saveInitiated) {
            requestDismiss()
        } else {
            // I've discussed this with LLMs and it's not clear if - from a UI perspective - we
            // should do this or not, but I'll go with it for now.
            showSavingSnackbar = true;
        }
    }

    LaunchedEffect(saveInitiated) {
        if (saveInitiated) {
            // We expect the save to complete quickly so we don't want the visual distraction
            // of a progress indicator appearing straight away. Let the progress indicator kick
            // in after a short delay if we're still here waiting for the save to complete.
            delay(spinnerDelayMillis)
            showSaveProgressIndicator = true
        }
        // TODO: I don't think we need to set it back to false in else, but maybe revise all
        // this later.
    }

// TODO: ChatGPT magic more or less
    LaunchedEffect(Unit) {
        vm.saveEvents.collect { event ->
            when (event) {
                EditPriceViewModel.SaveStatus.Success -> {
                    requestCloseDebounced()
                }

                EditPriceViewModel.SaveStatus.Error -> {
                    saveInitiated = false;
                    showErrorDialog = true;
                }

                else -> {}
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val packSizeFocusRequester = remember { FocusRequester() }
    var packSizeY by remember { mutableStateOf(0) }
    val priceFocusRequester = remember { FocusRequester() }
    var priceY by remember { mutableStateOf(0) }

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
//
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(enabled = !saveInitiated, onClick = { requestDismiss() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                title = { Text("TODO: Dialog Title") }, // TODO: Do not use "Edit price" (even though we call it that internally, because it's the "price" table), you can also eg edit pack size and probably a free text notes field etc
                actions = {
                    TextButton(enabled = !saveInitiated, onClick = {
                        coroutineScope.launch {
                            // TODO: Maybe we shouldn't be passing editablePrice around as a
                            // parameter so much, when it's implicit in the ViewModel? This would
                            // apply elsewhere, not just here.
                            when (vm.validateEditablePrice(uiContent.editablePrice.value)) {
                                EditPriceViewModel.ValidationState.OK -> {
                                    saveInitiated = true
                                    // delay(5000) // TODO HACK
                                    vm.saveEditablePrice(uiContent.editablePrice.value)
                                }
                                // TODO: We could possibly try to "animate" the problematic text
                                // field we just focused (e.g. pulse its border colour) to draw
                                // attention to it further, but this feels surprisingly fiddly and I
                                // am not sure it's ncessary. My inclination is to leave this for
                                // now and let the code settle down first before maybe trying to add
                                // it.
                                EditPriceViewModel.ValidationState.PACK_SIZE_INVALID -> {
                                    scrollState.animateScrollTo(packSizeY)
                                    packSizeFocusRequester.requestFocus()
                                    // TODO GENERATE ERROR - EG A SNACKBAR
                                }

                                EditPriceViewModel.ValidationState.PRICE_INVALID -> {
                                    scrollState.animateScrollTo(priceY)
                                    priceFocusRequester.requestFocus()
                                    // TODO GENERATE ERROR - EG A SNACKBAR
                                }
                            }
                        }
                    }) {
                        if (showSaveProgressIndicator) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Save")
                        }
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                // TODO: MD3 spec also has surfaceContainer background for "on-scroll", I am
                // struggling to find any non-LLM explanations here, but *maybe* *if we have
                // scrolled away from the top* we should change the background to surfaceContainer
                .background(MaterialTheme.colorScheme.surface) // because this is a full-screen dialog
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = fullScreenDialogBorder)
                .verticalScroll(scrollState)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Product") },
                value = uiContent.item.name,
                enabled = false,
                onValueChange = {})

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Store") },
                value = uiContent.source.name,
                enabled = false,
                onValueChange = {})

            Spacer(modifier = Modifier.height(8.dp))

            val units: List<MeasureUnit> =
                remember(uiContent.dataSet, uiContent.item.defaultUnit.quantityType) {
                    getRelevantMeasureUnits(
                        uiContent.dataSet,
                        uiContent.item.defaultUnit.quantityType,
                        includeDisplayOnly = false
                    )
                }
            var packSizeSupportingText by remember {
                mutableStateOf<Pair<Boolean, String?>>(
                    Pair(
                        false,
                        null
                    )
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        packSizeY = coordinates.positionInParent().y.toInt()
                    }) {
                Row {
                    // TODO: Using weight to size the components is also sucky, since we really
                    // just want "a reasonable fixed size" for the unit with
                    // the product taking whatever's left, but this will do for now.
                    var packSizeNumber by rememberSyncedTextFieldValue(
                        uiContent.editablePrice.value.measureValue ?: ""
                    ) // TODONOW: Just stop it being nullable rather than converting null to "" here?
                    NumericTextField(
                        label = { Text("Pack size") },
                        value = packSizeNumber,
                        validationRules = vm.packSizeValidationRules,
                        // TODO: If this works we need a vrkey on other numerictextfields too
                        validationRulesKey = uiContent.editablePrice.value.measureUnit.id,
                        // TODONOW: next line is probably never going to generate a null, suggesting our nullness in EditablePrice is pointless
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
                        onSupportingTextChange = { isError, supportingText ->
                            packSizeSupportingText = Pair(isError, supportingText)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(packSizeFocusRequester)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    MyExposedDropdownMenuBox(
                        selectedId = uiContent.editablePrice.value.measureUnit.id,
                        onValueChange = {
                            val measureUnit = MeasureUnit.fromValue(it)
                            devCheck(measureUnit != null) {
                                "Expected non-null measureUnit to be selected; got $it"
                            }
                            if (uiContent.editablePrice.value.measureUnit != measureUnit!!) {
                                vm.setUIContentEditablePrice(
                                    uiContent.editablePrice.value.copy(
                                        measureUnit = measureUnit!!
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
            }
            if (packSizeSupportingText.second != null) {
                Text(
                    text = packSizeSupportingText.second!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (packSizeSupportingText.first) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            /* TODO DELETE - JUST TEMP TO CHECK MY "FAKE" SUPPORTING TEXT MATCHES IN SPACING AND APPEARANCE
    TextField(value="TODOTEMP", onValueChange = {}, modifier = Modifier.fillMaxWidth(), label={ Text("TODOTEMP") }, supportingText = { Text("Comparison supporting text") })
    */
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
            var packPrice by rememberSyncedTextFieldValue(
                uiContent.editablePrice.value.price ?: ""
            ) // TODONOW: Just stop it being nullable rather than converting null to "" here?
            // TODO: This is perhaps inconsistent. The packSizeValidationRules are stored on the
            // ViewModel, but we cache the (actually unchanging - frozen locale, remember, and
            // dataset can't change either) currencyFormat here (it includes validation rules). We
            // should probably keep both on the viewmodel.
            val currencyFormat = remember { getCurrencyFormat(uiContent.dataSet, uiContent.frozenLocale) }
            Box(modifier = Modifier.onGloballyPositioned { coordinates ->
                priceY = coordinates.positionInParent().y.toInt()
            }) {
                NumericTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(priceFocusRequester),
                    label = { Text("Pack price") },
                    value = packPrice,
                    prefix = TextOrNull(currencyFormat.prefix),
                    suffix = TextOrNull(currencyFormat.suffix),
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
                    validationRules = currencyFormat.validationRules,
                    // TODONOW: next line is probably never going to generate a null, suggesting our nullness in EditablePrice is pointless
                    onValueChange = {
                        packPrice = it
                        if (uiContent.editablePrice.value.price != it.text) {
                            vm.setUIContentEditablePrice(uiContent.editablePrice.value.copy(price = it.text))
                            onPackSizeOrPriceChange()
                        }
                    },
                    supportingText = "This is more supporting text just as a test.",
                )
            }

            // We don't show the switch if this is the first price for an item and source; the price is confirmed, otherwise
            // why are we entering it?
            if (uiContent.editablePrice.value.id != 0L) {
                Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

            // TODO: Can/should I do something to scroll the screen when focus enters this and the caret is half-hidden?
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notes") },
                value = uiContent.editablePrice.value.details,
                onValueChange = {
                    vm.setUIContentEditablePrice(uiContent.editablePrice.value.copy(details = it))
                },
            )
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
                    TextButton(onClick = { requestCloseDebounced() }) {
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

        LaunchedEffect(showSavingSnackbar) {
            if (showSavingSnackbar) {
                scope.launch {
                    snackbarHostState.showSnackbar("Saving, please wait...")
                    showSavingSnackbar = false
                }
            }
        }
    }
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
data class ValidationRule(val validate: (String) -> Boolean, val message: String) : Parcelable

fun validationRulesOk(validationRules: List<ValidationRule>, value: String): Boolean {
    for (validationRule in validationRules) {
        if (!validationRule.validate(value)) {
            return false;
        }
    }
    return true;
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
): List<ValidationRule> {
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    val maxDecimalSeparators = if (allowDecimals) 1 else 0

// Create a function to strip fluff like spaces and the grouping symbol if the user typed it in.
    val insignificantCharsRegex = "[^-0-9${Regex.escape(decimalSeparator.toString())}]".toRegex()
    fun sanitiseCandidate(candidate: String) = candidate.replace(insignificantCharsRegex, "")
    fun attemptedParse(candidate: String): Double? =
        sanitiseCandidate(candidate).replace(decimalSeparator, '.').toDoubleOrNull()

    return listOfNotNull(
        ValidationRule(
            { it.count { it == decimalSeparator } <= maxDecimalSeparators },
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

@Composable
fun NumericTextField(
    label: @Composable() (() -> Unit)? = null,
    value: TextFieldValue,
    prefix: @Composable() (() -> Unit)? = null,
    suffix: @Composable() (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    // TODO: I am not completely happy about defaulting to the current locale here, since I am
    // generally trying to make sure I think about the correct locale when I need one. This is a
    // theoretically re-usable component and this isn't a ridiculous default in general, but it's
    // not ideal for this app.
    validationRules: List<ValidationRule>? = numericValidationRules(LocalConfiguration.current.locales[0]),
    validationRulesKey: Any? = null,
    onValueChange: (TextFieldValue) -> Unit,
    onSupportingTextChange: ((Boolean, String?) -> Unit)? = null,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    messageDelayMillis: Long = defaultValidationMessageDelayMillis,
) {
    ValidatedTextField(
        label = label,
        value = value,
        prefix = prefix,
        suffix = suffix,
        textStyle = textStyle,
        validationRules = (validationRules ?: emptyList()),
        validationRulesKey = validationRulesKey,
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
        onSupportingTextChange = onSupportingTextChange,
        modifier = modifier,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        messageDelayMillis = messageDelayMillis
    )
}

@Composable
fun ValidatedTextField(
    label: @Composable() (() -> Unit)? = null,
    value: TextFieldValue,
    prefix: @Composable() (() -> Unit)? = null,
    suffix: @Composable() (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    validationRules: List<ValidationRule>? = null,
    validationRulesKey: Any? = null,
    onCandidateValueChange: ((String) -> Boolean),
    onValueChange: (TextFieldValue) -> Unit,
    onSupportingTextChange: ((Boolean, String?) -> Unit)? = null,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    messageDelayMillis: Long = defaultValidationMessageDelayMillis,
) {
    Log.d("MyAppVTF", "input == previousInput? ${remember { validationRules }} == $validationRules")
    var failedValidationSupportingText by rememberSaveable(validationRulesKey) { mutableStateOf<String?>(null) }
    var failedValidationRule by remember(validationRulesKey) { mutableStateOf<ValidationRule?>(null) }
    Log.d("MyAppVTF", "validationRules?.size ${validationRules?.size}")
    Log.d("MyAppVTF", "fVST $failedValidationSupportingText")
    Log.d("MyAppVTF", "fVR $failedValidationRule")
    var delayJob by remember { mutableStateOf<Job?>(null) }
    var isFocused by remember { mutableStateOf(false) }

    // TODO: RENAME THIS FN IF NEW STRUCTURE WORKS
    fun updateFailedValidationRule(newValue: String, innerMessageDelayMillis: Long) {
        // We don't want to generate validation failures just because the field is empty. For the
        // moment we consider the field empty if it's nothing but whitespace. At least for my
        // purposes here I think this is fine. Obviously we could add a parameter to allow the
        // caller to configure this.
        // TODO: Possibly once the user clicks "Save" and gets validation failures, we should pass
        // a flag into this composable to tell it not to filter out empty strings, and make sure
        // the validation rules do include "Cannot be empty" validations? That way we won't nag the
        // user with a sea of red "Cannot be empty" validations on first appearance, but they will
        // get a message if they try to save without realising the value is mandatory.
        if (newValue.trim().isEmpty()) {
            failedValidationRule = null
            failedValidationSupportingText = null
            return
        }

        // In order to give "consistent" supportingText, we give precedence  whichever
        // validation generated the current supporting text.
        val reorderedValidations =
            listOfNotNull(failedValidationRule) + (validationRules ?: emptyList())
        failedValidationRule = null
        for (validationRule in reorderedValidations) {
            if (!validationRule.validate(newValue)) {
                failedValidationRule = validationRule
                Log.d("MyAppVTF", "inside ufvr $failedValidationRule")
                break
            }
        }

        Log.d("MyAppVTF", "after ufvr $failedValidationRule")
        if (failedValidationRule == null) {
            // Everything's OK. Clear any supporting text immediately.
            failedValidationSupportingText = null
        } else {
            // Something's wrong.
            //
            // If there is currently no supporting text and the user is actively editing (as
            // determined by our caller passing a non-0 value for innerMessageDelayMillis), we don't
            // want to distract the user by popping some in when they may be in the middle of typing
            // and will correct the problem themselves, so we only show supporting text after
            // they've stopped typing. (Imagine they are moving the decimal point; they type in a
            // "new" one in the correct place and then go to delete the "old" one. It's annoying if
            // a nagging message pops up after typing the new one telling them they have two decimal
            // points when they were already addressing the problem.)
            //
            // If there is already supporting text, it's probably less annoying to keep
            // showing some (currently valid) supporting text, rather than removing it while
            // the user types and possibly having it pop back in again afterwards.
            if (failedValidationSupportingText == null && innerMessageDelayMillis != 0L) {
                delayJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(innerMessageDelayMillis)
                    failedValidationSupportingText = failedValidationRule!!.message
                }
            } else {
                failedValidationSupportingText = failedValidationRule!!.message
            }
        }
    }

    LaunchedEffect(validationRulesKey) {
        Log.d("MyAppVTF", "LAUNCHED EFFECT")
        updateFailedValidationRule(value.text, 0)
    }

    // We have this function to make it easier to pass a literal null to TextField's supportingText
    // when we don't want anything, to prevent it allocating visual space for supportingText. TODO:
    // Some overlap with TextOrNull()?
    fun getSupportingText(): @Composable (() -> Unit)? {
        if (onSupportingTextChange == null) {
            if (failedValidationSupportingText != null) {
                return {
                    Text(
                        failedValidationSupportingText!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (supportingText != null) {
                return { Text(supportingText) }
            }
        }
        return null
    }
    TextField(
        label = label,
        value = value,
        prefix = prefix,
        suffix = suffix,
        textStyle = textStyle,
        onValueChange = { newValue ->
            delayJob?.cancel()
            if (onCandidateValueChange(newValue.text)) {
                updateFailedValidationRule(newValue.text, messageDelayMillis)


                onValueChange(newValue)
            }
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier.onFocusChanged { focusState ->
            Log.d("MyApp", "focus changed")
            isFocused = focusState.isFocused
            if (!focusState.isFocused) {
                Log.d("MyApp", "lost focus")
                // This case occurs when we are first composed, so we get to immediately show any
                // supportingText then, as well as doing it when we lose focus and want to show
                // any previously-delayed message.
                updateFailedValidationRule(value.text, 0)
                // TODO? failedValidationSupportingText = failedValidationRule?.message
            }
        },
        supportingText = getSupportingText(),
        trailingIcon = if (failedValidationSupportingText != null) {
            {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        } else null,
        isError = failedValidationSupportingText != null
    )

    if (onSupportingTextChange != null) {
        LaunchedEffect(failedValidationSupportingText) {
            Log.d("MyAppVTF", "LE $failedValidationSupportingText")
            if (failedValidationSupportingText != null) {
                onSupportingTextChange(true, failedValidationSupportingText)
            } else {
                onSupportingTextChange(false, supportingText)
            }
        }
    }
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

// Shared ViewModel to pass data between screens
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
    // TODO: Inconsistent use of "State" and "Content" here - rename everything consistently
    fun setEditPriceScreenStateFromHomeScreenState(uiContent: HomeScreenUIContent, frozenLocale: Locale) {
        // !! is justified because uiContent was shown on the home screen and the edit price button
        // was visible, which can only happen if we have all three available.
        val dataSet = uiContent.dataSet!!
        val item = uiContent.item!!
        val source = uiContent.source!!

        val price =
            uiContent.priceList.find { it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id }

        val editablePrice = if (price != null) EditablePrice(price, frozenLocale, getCurrencyFormat(dataSet, frozenLocale)) else EditablePrice(
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
}

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

class EditPriceViewModel(
    private val priceTrackerRepository: PriceTrackerRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val instanceId = UUID.randomUUID().toString() // TODO FOR DEBUG

    init {
        Log.d("MyApp", "EditPriceScreenViewModel $instanceId $this")
    }

    // This is only nullable because we may not have a saved state and it may be some time before
    // the "real" state is used to overwrite it, although that should happen before anything that
    // cares can observe it.
    private var _uiContent: EditPriceScreenUIContent? =
        EditPriceScreenUIContent.fromSavedState(savedStateHandle)
    val uiContent get() = _uiContent

    fun setUIContent(newUIContent: EditPriceScreenUIContent) {
        Log.d("MyApp", "EditPriceScreenViewModel.setUIContent($newUIContent)")
        _uiContent = newUIContent
        newUIContent.saveState(savedStateHandle)
        updateAfterSetUIContentEditablePrice(newUIContent.editablePrice.value)
    }

    fun setUIContentEditablePrice(newEditablePrice: EditablePrice) {
        Log.d("MyApp", "EditPriceScreenViewModel.setUIContentEditablePrice($newEditablePrice)")
        // TODO: NOT SURE IF !! OK, IT PROBABLY IS BUT NEED TO THINK
        uiContent!!.editablePrice.value = newEditablePrice
        uiContent!!.saveEditablePriceState(savedStateHandle)
        updateAfterSetUIContentEditablePrice(newEditablePrice)
    }

    // TODO: I don't like having to call this function in two places, but it fixes a bug for now (we
    // weren't setting packSizeValidationRules on first entry to the edit screen) and I can think
    // about refactoring later.
    fun updateAfterSetUIContentEditablePrice(newEditablePrice: EditablePrice) {
        val maxDecimals = newEditablePrice.measureUnit.maxDecimals
        packSizeValidationRules = numericValidationRules(uiContent!!.frozenLocale, allowDecimals = if (maxDecimals > 0) true else false, allowZero = false, maxDecimals = maxDecimals)
    }

    // TODO: I suspect this should *either* be moved down into a rememberSaveable inside the composable,
    // *or* it should be preserved across process death (perhaps, but not necessarily, by being moved
    // into EditPriceScreenUIContent).
    var firstPackSizeOrPriceChangeOccurred: Boolean = false

    // This default is just to make initialisation possible; in reality we expect this to be
    // overwritten before it's used.
    var packSizeValidationRules = emptyList<ValidationRule>()

    /* TODO DELETE
    // TODONOW: HARDCODING 2 DP IS A HACK - WE REALLY OUGHT TO GET THIS FROM LOCALE, AND WE OUGHT TO PROBABLY CONSTRUCT PRICEVALIDATIONRULES IN OUR NAVHOST COMPOSABLE VIA REMEMBER AND PASS IT IN SO IT'S REGENERATED IF USER CHANGES LOCAL
    private fun getPriceValidationRules(locale: Locale) =
        numericValidationRules(uiContent!!.frozenLocale, allowDecimals = true, allowZero = false, maxDecimals = 2)
     */

    // TODONOW: There's probably a lot of redundancy with the currency stuff given how it's evolved

    /* TODO DELETE
    // TODO: We are implementing this as a map (maybe rename it to cache) because it's locale dependent but we don't have the data set handy when we do updateLocaleDependencies(). So we lazily look up the currency details (which is completely acceptable main thread work, but just fiddly enough we don't want to be doing it *constantly*) and cache it in here on first up.
    // TODO: MutableMap is not thread safe. I don't think this is a problem, but be aware of it - I think we could switch to non-mutable Map and replace-in-place if necessary
    val currencyFormatMap: MutableMap<String, CurrencyFormat> = mutableMapOf()
    */

    /* TODO DELETE
    // TODO: Even if Locale.getDefault() is sub-optimal, this is fine as it's really only a default. updateLocaleDependencies() should be called almost immediately - maybe do some test logging to check that?
    var locale: Locale = Locale.getDefault()
    */

    /* TODO DELETE
    fun updateLocaleDependencies(locale: Locale) {
        this.locale = locale
        currencyFormatMap.clear()
    }
    */
    enum class ValidationState {
        OK,
        PACK_SIZE_INVALID,
        PRICE_INVALID
    }

    // TODO: It's tempting to think this should be on EditablePrice itself, but the whole point is
    // that it will apply (sharing as much as possible) the same validation rules that the
    // ValidatedTextFields are using - and those aren't available to EditablePrice, and based on
    // discussion with ChatGPT I think it's better to have this function here than pass this
    // ViewModel as an argument to EditablePrice.toDomain()
    fun validateEditablePrice(editablePrice: EditablePrice): ValidationState {
        if (!validationRulesOk(packSizeValidationRules, editablePrice.measureValue)) {
            return ValidationState.PACK_SIZE_INVALID
        }
        if (!validationRulesOk(
                getCurrencyFormat(uiContent!!.dataSet, uiContent!!.frozenLocale).validationRules,
                editablePrice.price
            )
        ) {
            return ValidationState.PRICE_INVALID
        }
        // TODO: MORE?
        return ValidationState.OK
    }

    fun saveEditablePrice(editablePrice: EditablePrice) {
        val price = editablePrice.toDomain(uiContent!!.frozenLocale)
        Log.d("MyApp", "saveEditablePrice price $price")
        if (price != null) {
            updateOrInsertPrice(price)
        } else {
            // This is an internal logic error. Our caller should have got confirmation from
            // validateEditablePrice() that editablePrice is OK, and if that says it's OK toDomain()
            // should not fail.
            throw IllegalStateException("saveEditablePrice() called with an inconvertible editablePrice: $editablePrice")
        }
    }

    // TODO: Is there really no standard abstraction which will wrap all this hellish savestatus crap up?

    enum class SaveStatus { Idle, Saving, Success, Error }

    private val _saveStatus = SingleEventState(SaveStatus.Idle)

    // TODO: DELETE? val saveStatus = _saveStatus.state
    val saveEvents = _saveStatus.events

    // TODO: Use upsert in name?
    private fun updateOrInsertPrice(price: Price) {
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

// TODO: Navigation is a mess - I'm completely unclear how the mysterious back stack and routes and
// viewmodels being reused and various different kinds of composition and activity and process
// destruction and reconstruction are supposed to interact.
// TODO: Random Grok suggestion to maybe play with later: Use LinearOutSlowInEasing for enter
// transitions (starts fast, slows down) and FastOutLinearInEasing for exit transitions (starts
// slow, speeds up) to make the slide feel natural.
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel =
        viewModel(LocalContext.current as ComponentActivity) // TODO: perplexity voodoo
    NavHost(
        navController = navController,
        startDestination = "home",
        // TODO!? modifier = Modifier.padding(innerPadding)
    ) {
        // TODO: The animation here is complete voodoo. This is a tweaked version of https://stackoverflow.com/questions/65643015/animating-between-composables-in-navigation-with-compose
        // and does actually seem to more-or-less behave (and consistently too). I didn't want to force 700ms, this feels a smidge fast at the (I think) default 300 but I think it is OK.
        // No, no, it isn't consistent. Sometimes the back animation is much faster than others. Not a clue. Not a f* clue.

        val tweenDurationMillisEnter = 700; // TODO: should probably be 300 in final version
        val tweenDurationMillisExit = 700; // TODO: should probably be 250 in final version

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

        composable(
            "home",
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
        ) { backStackEntry ->
            val vm: HomeViewModel =
                viewModel(backStackEntry, factory = AppViewModelProvider.Factory)
            Log.d("MyApp", "backStackEntry.id ${backStackEntry.id}")
            val locale by rememberUpdatedState(LocalConfiguration.current.locales[0])
            HomeScreen(vm, navController, onEditPriceClick = { uiContent ->
                sharedViewModel.setEditPriceScreenStateFromHomeScreenState(uiContent, locale)
                navController.navigate("editPrice")
            })
        }

        composable(
            "settings", enterTransition = { slideLeftTransition() },
            popExitTransition = { slideRightTransition() },

            ) {
            SettingsScreen(navController)
        }

        composable(
            "editPrice", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },

        ) { backStackEntry ->
            // Note that we explicitly request a fresh ViewModel each time (because it's tied to the
            // backStackEntry) - this avoids stale data causing problems.
            val vm: EditPriceViewModel =
                viewModel(backStackEntry, factory = AppViewModelProvider.Factory)

            // TODO: Be good to test fairly late on with two datasets with different currencies - I vaguely wonder
            // if re-use of this composable (maybe prevented via randomUUID route hack?) will not pick up the
            // changes.

            /* TODO: DELETE?
            // TODO: Test this but doing it this way ought to mean we correctly pick up locale changes while we are on screen
            LaunchedEffect(Locale.getDefault()) {
                vm.updateLocaleDependencies(Locale.getDefault())
            }
            */

            // TODO: Maybe editPriceScreenUIContent should be private or read-only and the
            // set-to-null at least should be done via a function call.
            if (sharedViewModel.editPriceScreenUIContent != null) {
                vm.setUIContent(sharedViewModel.editPriceScreenUIContent!!)
                sharedViewModel.editPriceScreenUIContent = null
            }

            EditPriceScreen(
                vm, navController,
                requestClose = {
                    navController.popBackStack()
                })
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
    val validationRules: List<ValidationRule>
)

// TODO: This takes a DataSet not a currency code because later on a DataSet may allow custom currency formatting which overrides whatever the current locale wants to do.
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

// TODONOW: ChatGPT on locales:
// TL;DR
//
//    🔄 In Compose: use LocalConfiguration.current.locales[0] — it’s reactive and accurate.
//
//    ⚙️ In ViewModels / non-UI: Locale.getDefault() is okay, but may not reflect immediate user changes.
//
//    🎯 Pass the current Compose locale to your ViewModel using LaunchedEffect(currentLocale) to keep everything in sync
//
// Do I need to switch away from using Locale.getDefault()? Perhaps I should have a LaunchedEffect(currentLocale) which passes the locale to the viewmodel, then we will have it available everywhere via that which should be up to date. But need to check ChatGPT is right of course!

// TODONOW: It feels like I have a lot of similar-but-not-quite-the-same code to do things like
// locale sensitive number formatting and parsing. This ought to be rationalised.

// TODO: Eventually will need to remove misc Log.d() lines and/or replace them with permanent well-thought-out ones if that is not inefficient.
