@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.composetutorial // TODO: change this!

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.core.DataStore
//import androidx.datastore.core.dataStore
//import com.example.composetutorial.datastore.proto.UserPrefs
//import com.example.composetutorial.datastore.proto.UserPrefs.DatasetSelection
import com.google.protobuf.InvalidProtocolBufferException
import UserPrefs
import com.example.composetutorial.ui.components.ValidationInputHandle
import com.example.composetutorial.domain.UnitFamily
import com.example.composetutorial.domain.getMeasurementUnitsOfSameQuantityTypeAndUnitFamily
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.domain.MeasuredValue
import com.example.composetutorial.models.EditableDataSet
import com.example.composetutorial.models.Item
import com.example.composetutorial.models.EditableItem
import com.example.composetutorial.models.Source
import com.example.composetutorial.models.EditableSource
import com.example.composetutorial.models.PriceEntity
import com.example.composetutorial.models.Price
import com.example.composetutorial.models.PriceWithItemEntity
import com.example.composetutorial.models.PriceHistory
import com.example.composetutorial.models.toDomain
import androidx.compose.ui.platform.LocalUriHandler
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.Canvas
import android.app.Activity
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
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import java.time.Duration
//import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.icu.text.Collator
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.navigation.compose.rememberNavController
import android.os.Bundle
import android.os.LocaleList
import android.os.Parcelable
import android.os.StrictMode
import android.text.format.DateUtils
import android.util.Log
import android.util.Log.e
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.material3.DrawerState
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
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.semantics.SemanticsProperties.Role
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.datastore.dataStore
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
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import androidx.room.withTransaction
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
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.createSavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.composetutorial.domain.MeasurementUnit
import com.example.composetutorial.domain.QuantityType
import com.example.composetutorial.models.RepositoryImpl
import com.example.composetutorial.domain.UnitPrice
import com.example.composetutorial.domain.getRelevantMeasurementUnits
import com.example.composetutorial.domain.getRelevantUnitFamilies
import com.example.composetutorial.domain.withFriendlyDenominator
import com.example.composetutorial.models.EditablePrice
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.domain.format
import com.example.composetutorial.models.toEditable
import com.example.composetutorial.ui.components.rememberValidationInputHandle
import com.example.composetutorial.ui.components.requestUserAttention
import com.example.composetutorial.ui.components.validationInputHandleBringIntoViewRequester
import com.example.composetutorial.ui.components.validationInputHandleFocusRequester
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.DecimalFormatSymbols
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.concurrent.Executors
import kotlin.math.ceil
import kotlin.math.pow



fun getDefaultUnitFamilies(locale: Locale): Set<UnitFamily> = when (locale.country.uppercase()) {
    // ChatGPT suggests it's common to have dual metric and US Customary labelling in US
    // supermarkets and that some users may want to use metric, so we enable it by default. I'll do
    // the same for Liberia and Myanmar too for now.
    "US", "LR", "MM" -> setOf(UnitFamily.ITEM, UnitFamily.METRIC, UnitFamily.US_CUSTOMARY)
    "GB" -> setOf(UnitFamily.ITEM, UnitFamily.METRIC, UnitFamily.IMPERIAL)
    else -> setOf(UnitFamily.ITEM, UnitFamily.METRIC)
}


// The arguments are mandatory here so we're forced to think about what's correct when we call this.
// For miscellaneous debug output we can just use string interpolation of course.
fun formatDouble(
    value: Double,
    minDecimals: Int,
    maxDecimals: Int,
    useLocaleGrouping: Boolean,
    locale: Locale
): String {
    val numberFormat = NumberFormat.getNumberInstance(locale)
    numberFormat.minimumFractionDigits = minDecimals
    numberFormat.maximumFractionDigits = maxDecimals
    if (!useLocaleGrouping) {
        numberFormat.isGroupingUsed = false
    }
    return numberFormat.format(value)
}


const val DB_NAME = "main.db" // TODO: should I change this filename?
const val DB_VERSION = 1
@Database(
    entities = [DataSet::class, Item::class, Source::class, PriceEntity::class, PriceHistory::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataSetDao(): DataSetDao
    abstract fun productDao(): ItemDao
    abstract fun sourceDao(): SourceDao
    abstract fun priceDao(): PriceDao
    abstract fun priceHistoryDao(): PriceHistoryDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                    // TODO: Disable query logging in final version of course
                    .setQueryCallback({ sqlQuery, bindArgs ->
                        Log.d("MyApp", "SQL Query: $sqlQuery SQL Args: $bindArgs")
                    }, Executors.newSingleThreadExecutor())
                    /* TODO DELETE
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch { populateDemoData(context) }
                        }
                    })
                    */
                    .build()
                    .also { Instance = it }
            }
        }

        // TODO: ChatGPT magic
        fun clearInstance() {
            Instance?.close()
            Instance = null
        }
    }
}

suspend fun populateDemoData(repository: Repository, context: Context) {
    // ENHANCE: We could pick one of IMPERIAL or US_CUSTOMARY based on the current locale, but in
    // practice we just want to show we support multiple units, and it isn't as if a native US
    // customary user is going to get too confused (if they even notice) that "pint" (for example)
    // has the wrong metric equivalent here - it's just demo data.
    // ENHANCE: We could add some demo products which are (fake) branded products rather than
    // generic categories, as this is a legitimate thing to do where the user is brand-sensitive.
    // It probably doesn't really matter though.
    val currency = Currency.getInstance(Locale.getDefault())
    // The demo data uses 2 decimal places so we scale it by currencyMultiplier when inserting so
    // we have unrealistic but at least workable prices for things like JPY. The prices aren't meant
    // to be realistic anyway.
    val currencyMultiplier = 10.0.pow(2 - currency.defaultFractionDigits)
    val dataSetId = repository.updateOrInsertDataSet(
        DataSet(
            name = "Groceries (demo)",
            currencyCode = currency.currencyCode,
            allowMetric = true,
            allowImperial = true,
            allowUSCustomary = false,
            notes = "A sample collection of unrealistic grocery prices for imaginary stores. This is intended to give you something to play with when you first install the app.",
        )
    )
    val itemIdGroundCoffee = repository.updateOrInsertItem(
        Item(
            dataSetId = dataSetId,
            name = "Coffee (ground)",
            defaultUnit = MeasurementUnit.G,
            allowMultipack = false,
            notes = ""
        )
    )
    val itemIdWholeMilk = repository.updateOrInsertItem(
        Item(
            dataSetId = dataSetId,
            name = "Milk (whole)",
            defaultUnit = MeasurementUnit.L,
            allowMultipack = false,
            notes = "",
        )
    )
    val itemIdTeabags = repository.updateOrInsertItem(
        Item(
            dataSetId = dataSetId,
            name = "Teabags",
            defaultUnit = MeasurementUnit.EACH,
            allowMultipack = false,
            notes = "",
            )
    )
    val itemIdCola = repository.updateOrInsertItem(
        Item(
            dataSetId = dataSetId,
            name = "Cola",
            defaultUnit = MeasurementUnit.ML,
            allowMultipack = true,
            notes = ""
        )
    )
    // We have three sources with sample prices, because you need three non-ancient prices in order
    // to get good/OK/bad judgments and we want to show those off to new users.
    val sourceIdValueMart = repository.updateOrInsertSource(
        Source(
            dataSetId = dataSetId,
            name = "ValueMart",
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = ""
        )
    )
    val sourceIdSuperiorStore = repository.updateOrInsertSource(
        Source(
            dataSetId = dataSetId,
            name = "SuperiorStore",
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = ""
        )
    )
    val sourceIdGrandways = repository.updateOrInsertSource(
        Source(
            dataSetId = dataSetId,
            name = "Grandways",
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = ""
        )
    )
    // Newco deliberately has no prices to start with.
    repository.updateOrInsertSource(
        Source(
            dataSetId = dataSetId,
            name = "Newco",
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = "Only just opened but I hope their prices will be good."
        )
    )
    val now = Instant.now()
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdGroundCoffee,
            sourceId = sourceIdValueMart,
            price = 2.03 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(500.0, MeasurementUnit.G),
            confirmedAt = now.minus(2, ChronoUnit.MINUTES),
            notes = "Large pack own brand",
            itemDefaultUnit = MeasurementUnit.G,
            modifiedAt = now.minus(2, ChronoUnit.MINUTES)
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdGroundCoffee,
            sourceId = sourceIdSuperiorStore,
            price = 1.50 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(227.0, MeasurementUnit.G),
            confirmedAt = now.minus(4, ChronoUnit.DAYS),
            notes = "Own brand",
            itemDefaultUnit = MeasurementUnit.G,
            modifiedAt = now.minus(4, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdGroundCoffee,
            sourceId = sourceIdGrandways,
            price = 1.64 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(350.0, MeasurementUnit.G),
            confirmedAt = now.minus(9, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.G,
            modifiedAt = now.minus(9, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdWholeMilk,
            sourceId = sourceIdValueMart,
            price = 1.99 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(
                4.0,
                MeasurementUnit.IMPERIAL_PINT
            ),
            confirmedAt = now,
            notes = "",
            itemDefaultUnit = MeasurementUnit.L,
            modifiedAt = now,
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdWholeMilk,
            sourceId = sourceIdSuperiorStore,
            price = 2.86 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(2.0, MeasurementUnit.L),
            confirmedAt = now.minus(63, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.L,
            modifiedAt = now.minus(63, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdWholeMilk,
            sourceId = sourceIdGrandways,
            price = 3.28 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(
                6.0,
                MeasurementUnit.IMPERIAL_PINT
            ),
            confirmedAt = now.minus(14, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.L,
            modifiedAt = now.minus(14, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdTeabags,
            sourceId = sourceIdValueMart,
            price = 0.76 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(40.0, MeasurementUnit.EACH),
            confirmedAt = now.minus(7, ChronoUnit.DAYS),
            notes = "Soft pack own brand",
            itemDefaultUnit = MeasurementUnit.EACH,
            modifiedAt = now.minus(7, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdTeabags,
            sourceId = sourceIdSuperiorStore,
            price = 0.60 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(20.0, MeasurementUnit.EACH),
            confirmedAt = now.minus(4, ChronoUnit.HOURS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.EACH,
            modifiedAt = now.minus(4, ChronoUnit.HOURS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdTeabags,
            sourceId = sourceIdGrandways,
            price = 1.25 * currencyMultiplier,
            count = 1,
            quantity = MeasuredValue(50.0, MeasurementUnit.EACH),
            confirmedAt = now.minus(12, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.EACH,
            modifiedAt = now.minus(12, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdCola,
            sourceId = sourceIdValueMart,
            price = 6.30 * currencyMultiplier,
            count = 12,
            quantity = MeasuredValue(400.0, MeasurementUnit.ML),
            confirmedAt = now.minus(6, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.ML,
            modifiedAt = now.minus(6, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdCola,
            sourceId = sourceIdSuperiorStore,
            price = 2.79 * currencyMultiplier,
            count = 4,
            quantity = MeasuredValue(330.0, MeasurementUnit.ML),
            confirmedAt = now.minus(31, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.ML,
            modifiedAt = now.minus(31, ChronoUnit.DAYS),
        )
    )
    repository.updateOrInsertPrice(
        Price(
            dataSetId = dataSetId,
            itemId = itemIdCola,
            sourceId = sourceIdGrandways,
            price = 3.82 * currencyMultiplier,
            count = 6,
            quantity = MeasuredValue(330.0, MeasurementUnit.ML),
            confirmedAt = now.minus(18, ChronoUnit.DAYS),
            notes = "",
            itemDefaultUnit = MeasurementUnit.ML,
            modifiedAt = now.minus(18, ChronoUnit.DAYS),
        )
    )
    // Set some defaults for the first run so the user isn't left with a screen with no data
    // wondering what to do.
    // TODO: NOT JUST HERE - I AM A BIT MIXED UP ON "SET/GET CURRENT X" vs "SET/GET SELECTED X" AS FAR AS TERMINOLOGY GOES - NEED TO BE CONSISTENT
        setCurrentDataSetId(context, dataSetId)
        setCurrentItemId(context, dataSetId, itemIdTeabags)
        setCurrentSourceId(context, dataSetId, sourceIdSuperiorStore)
}

suspend fun setCurrentDataSetId(context: Context, dataSetId: Long) {
    updateUserPreferences(context) { builder -> builder.setCurrentDataSetId(dataSetId) }
}

suspend fun setCurrentItemId(context: Context, dataSetId: Long, itemId: Long) {
    updateUserPreferences(context) { builder -> builder.putCurrentItemIdForDataSetId(dataSetId, itemId) }
}

suspend fun setCurrentSourceId(context: Context, dataSetId: Long, sourceId: Long?) {
    updateUserPreferences(context) { builder -> builder.putCurrentSourceIdForDataSetId(dataSetId, sourceId ?: -1L) } // TODO: _1L for null is a bit of a hack - we also don't do any special handling of -1 when we read this "pref", although in practice it probably works fine - come back to this later
}

suspend fun updateUserPreferences(context: Context, update: (UserPrefs.UserPreferences.Builder) -> Unit) {
    context.userPreferencesStore.updateData { prefs ->
        prefs.toBuilder().apply(update).build() }
}


fun <T> List<T>.sortedByLocale(
    selector: (T) -> String,
    locale: Locale
): List<T> {
    val collator = Collator.getInstance(locale).apply {
        strength = Collator.PRIMARY
    }

    return this.sortedWith { lhs, rhs ->
        collator.compare(selector(lhs), selector(rhs))
    }
}

@Composable
fun <T> List<T>.rememberSortedByLocale(
    selector: (T) -> String,
): List<T> {
    val locale = LocalConfiguration.current.locales[0]
    return remember(this, locale) {
        this.sortedByLocale(selector, locale)
    }
}


// AppViewModelProvider.Factory allows us to control the arguments passed to our ViewModel
// constructors when viewModel() is called.
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer<HomeViewModel> {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
            HomeViewModel(app.repository, app)
        }
        initializer<SettingsViewModel> {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
            SettingsViewModel(app)
        }
        /* TODO DELETE LATER, KEEPING AROUND FOR REF FOR OTHER VIEWS IF NEC FOR NOW
        initializer {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
            val savedStateHandle = createSavedStateHandle()
            EditPriceViewModel(app.repository, savedStateHandle)
        }
        */
    }
}

class SettingsViewModel(
    application: Application) : ViewModel() {
    val settingsRepository = SettingsRepository(application.dataStore)
}


// Using AppScope.io signals: "This is an app-wide background task that must be allowed to complete
// without being cancelled when the user leaves the screen, and it *will not* touch any UI or
// short-lived objects which may have gone out of scope." This is safe and desired for things like
// DataStore updates. Practically speaking, CoroutineScope(Dispatchers.IO).launch {} is equivalent
// in behaviour (new scope per call, GC'd safely), but AppScope.io better documents intent and
// avoids allocation.
object AppScope {
    val io = CoroutineScope(Dispatchers.IO + SupervisorJob())
}

// TODO: Lots of AI voodoo here, probably worth reading up later on how MyApplication should behave
// and what it maybe ought to be doing.
class MyApplication : Application() {
    val repository: RepositoryImpl by lazy {
        val db = AppDatabase.getDatabase(this)
        RepositoryImpl(
            db,
            db.dataSetDao(),
            db.productDao(),
            db.sourceDao(),
            db.priceDao(),
            db.priceHistoryDao()
        )
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

    override fun onCreate() {
        super.onCreate()

        AppScope.io.launch {
            val demoDataInsertedKey = booleanPreferencesKey("demo_data_inserted") // TODO: MOVE TO SOME MASTER LIST OF KEYS!?
            val demoDataInserted = dataStore.data
                .map { prefs -> prefs[demoDataInsertedKey] ?: false }
                .first()
            if (!demoDataInserted) {
                val db = AppDatabase.getDatabase(this@MyApplication)

                db.withTransaction {
                    // Manually adjust the starting sequence values for various tables. This
                    // increases the chances that foreign key bugs cause constraint violations,
                    // rather than silently referencing the wrong record. It also makes it easier to
                    // identify the type of ID during debugging based on its numeric range. We don't
                    // rely on IDs being non-overlapping for correctness.
                    //
                    // We leave data_set's sequence alone and let it start IDs at 1.
                    db.openHelper.writableDatabase.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('source', 1000)")
                    db.openHelper.writableDatabase.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('item', 2000)")
                    db.openHelper.writableDatabase.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('price', 10000)")
                    db.openHelper.writableDatabase.execSQL("INSERT INTO sqlite_sequence (name, seq) VALUES ('price_history', 100000)")

                    populateDemoData(repository, this@MyApplication)
                }

                dataStore.edit() { it[demoDataInsertedKey] = true }
            }
        }
    }
}

class Converters {
    // We don't have any QuantityType fields in the database at the moment, but we'll keep the
    // converters for it around so if we add one in the future it will just work rather than ending
    // up being stored as a string.

    @Suppress("unused")
    @TypeConverter
    fun fromQuantityType(quantityType: QuantityType?): Int? {
        return quantityType?.id
    }

    @Suppress("unused")
    @TypeConverter
    fun toQuantityType(value: Int?): QuantityType? {
        return value?.let { QuantityType.fromId(it) }
    }

    @TypeConverter
    fun fromMeasurementUnit(measurementUnit: MeasurementUnit?): Long? {
        return measurementUnit?.id
    }

    @TypeConverter
    fun toMeasurementUnit(value: Long?): MeasurementUnit? {
        return value?.let { MeasurementUnit.fromId(it) }
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
    fun fromLoyaltyType(loyaltyType: LoyaltyType?): Long? {
        return loyaltyType?.id
    }

    @TypeConverter
    fun toLoyaltyType(value: Long?): LoyaltyType? {
        return value?.let { LoyaltyType.fromValue(it) }
    }
}

enum class LoyaltyType(val id: Long) {
    NONE(1),
    BONUS(2),
    DISCOUNT(3);

    companion object {
        private val loyaltyTypeById = LoyaltyType.entries.associateBy { it.id }

        fun fromValue(loyaltyDiscountTypeId: Long): LoyaltyType? =
            loyaltyTypeById[loyaltyDiscountTypeId]
    }
}

fun baseUnitForQuantityType(quantityType: QuantityType) = when (quantityType) {
    QuantityType.WEIGHT -> MeasurementUnit.G
    QuantityType.VOLUME -> MeasurementUnit.ML
    QuantityType.ITEM -> MeasurementUnit.EACH
}

// NB: We cannot rely on the database to order our results by name as it isn't locale-sensitive, so
// we have to sort the results in memory later. We could therefore omit ORDER BY clauses completely,
// (ENHANCE: and doing this later on would give a small performance/efficiency improvement) but
// instead we use a deliberately wrong ORDER BY DESC to make it obvious if we are failing to apply
// sorting to the results before showing them.

@Dao
interface DataSetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dataSet: DataSet): Long

    @Upsert
    suspend fun upsert(dataSet: DataSet): Long

    @Query("SELECT * FROM data_set ORDER BY name DESC")
    fun getAllDataSets(): Flow<List<DataSet>>

    @Query("DELETE FROM data_set WHERE id = :dataSetId")
    suspend fun deleteById(dataSetId: Long): Int
}

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item): Long

    @Upsert
    suspend fun upsert(item: Item): Long

    @Query("SELECT * FROM item WHERE data_set_id = :dataSetId ORDER BY name DESC")
    fun getAllItems(dataSetId: Long): Flow<List<Item>>

    @Query("DELETE FROM item WHERE id = :itemId")
    suspend fun deleteById(itemId: Long): Int
}

@Dao
interface SourceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dataSet: Source): Long

    @Upsert
    suspend fun upsert(source: Source): Long

    @Query("SELECT * FROM source WHERE data_set_id = :dataSetId ORDER BY name DESC")
    fun getAllSources(dataSetId: Long): Flow<List<Source>>

    @Query("DELETE FROM source WHERE id = :sourceId")
    suspend fun deleteById(sourceId: Long): Int
}

@Dao
interface PriceDao {
    @Upsert
    suspend fun upsert(price: PriceEntity): Long

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

    @Query("DELETE FROM price WHERE id = :priceId")
    suspend fun deleteById(priceId: Long): Int
}

@Dao
interface PriceHistoryDao {
    @Insert
    suspend fun insert(priceHistory: PriceHistory): Long

    // Note that we get price history without using the price_id. This means that if a price is
    // deleted and subsequently a new price is added (which will allocate a new price_id), both
    // segments of the price history will be retrieved.
    @Query("SELECT * FROM price_history WHERE data_set_id = :dataSetId AND item_id = :itemId AND source_id = :sourceId ORDER BY modified_at DESC")
    fun getPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long): Flow<List<PriceHistory>>

    @Query("SELECT COUNT(*) FROM price_history WHERE data_set_id = :dataSetId AND item_id = :itemId AND source_id = :sourceId")
    fun countPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long): Flow<Long>

    @Query("DELETE FROM price_history WHERE id = :priceHistoryId")
    suspend fun deleteById(priceHistoryId: Long): Int
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

// TODO: SOME CODE DUPLICATION AND GENERAL SHITTINESS WITH OTHER VERSIONS OF THIS "SAVE" CODE - THIS COMMENT *MAY* BE OUTDATED NOW BUT NEED TO REVIEW CAREFULLY
fun setCurrentDataSetIdAsync(context: Context, dataSetId: Long) {
    AppScope.io.launch {
        setCurrentDataSetId(context, dataSetId)
    }
}
fun setCurrentItemIdAsync(context: Context, dataSetId: Long, itemId: Long) {
    AppScope.io.launch {
        setCurrentItemId(context, dataSetId, itemId)
    }
}
fun setCurrentSourceIdAsync(context: Context, dataSetId: Long, sourceId: Long?) {
    AppScope.io.launch {
        setCurrentSourceId(context, dataSetId, sourceId)
    }
}

// TODO: ChatGPT magic
sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data object Empty : LoadState<Nothing>
    data class Loaded<T>(val value: T) : LoadState<T>
}

fun <T> LoadState<T>.valueOrNull(): T? =
    when (this) {
        is LoadState.Loaded -> value
        else -> null
    }

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: Repository,
    application: Application
) : ViewModel() {
    init {
        Log.d("MyApp", "HomeScreenViewModel created: $this")
    }

    private val app = application

    fun setCurrentDataSetId(dataSetId: Long) {
        setCurrentDataSetIdAsync(app, dataSetId)
    }
    fun setCurrentItemId(itemId: Long) {
            val dataSetId = selectedDataSetIdStateFlow.value.valueOrNull()
            if (dataSetId != null) { // TODO: Not sure this is really possible?! Harmless but can we avoid?
                setCurrentItemIdAsync(app, dataSetId, itemId)
        }
    }
    fun setCurrentSourceId(sourceId: Long?) {
            val dataSetId = selectedDataSetIdStateFlow.value.valueOrNull()
            if (dataSetId != null) { // TODO: Not sure this is really possible?! Harmless but can we avoid?
                setCurrentSourceIdAsync(app, dataSetId, sourceId)
            }
    }

    private fun <T> Flow<T>.asLoadState(): StateFlow<LoadState<T>> = this
        .map<T, LoadState<T>> { LoadState.Loaded(it) }
        .distinctUntilChanged()
        .onStart { emit(LoadState.Loading) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LoadState.Loading)

    private val prefsFlow = app.userPreferencesStore.data
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    private val selectedDataSetIdStateFlow: StateFlow<LoadState<Long>> = prefsFlow
        .map { it.currentDataSetId }
        .asLoadState()

    private val selectedItemIdStateFlow: StateFlow<LoadState<Long>> = prefsFlow
        .map { prefs ->
            prefs.currentItemIdForDataSetIdMap[prefs.currentDataSetId] ?: 0L // TODO 0L IS A BIT OF HACK - WE MAY BE ABLE TO MAKE NULL WORK, BUT SINCE THE OLDER STYLE FLOWS DID NOT HAVE NULLABILITY, I WANT TO AVOID DEALING WITH THAT RIGHT NOW
        }
        .asLoadState()

    private val selectedSourceIdStateFlow: StateFlow<LoadState<Long>> = prefsFlow
        .map { prefs ->
            prefs.currentSourceIdForDataSetIdMap[prefs.currentDataSetId] ?: 0L // TODO 0L IS A BIT OF HACK - WE MAY BE ABLE TO MAKE NULL WORK, BUT SINCE THE OLDER STYLE FLOWS DID NOT HAVE NULLABILITY, I WANT TO AVOID DEALING WITH THAT RIGHT NOW
        }
        .asLoadState()

    // TODO: Rename UIContent->HomeScreenUIContent and/or scope it to this ViewModel?
    private val _uiState = MutableStateFlow(
        Pair(
            false,
            HomeScreenUIContent.createEmpty()
        )
    )
    val uiState = _uiState.asStateFlow()

    val settingsRepository = SettingsRepository(app.dataStore)

    private val loadingTrigger = MutableStateFlow(0L) // bump on user input // TODO GROK MAGIC
    // TODO: This is quite long and could probably be improved by factoring stuff out into helper
    // functions.
    init {
        // At some point in development, an LLM suggested I add the following comment and code:
        //     // This forces the delegate to initialize safely on the main thread
        //     @Suppress("UNUSED_VARIABLE") val unused = app.dataStore
        // I am fairly sure this is not doing anything useful any more, if it ever was.
        // preferencesDataStore should be fine to initialise on any thread.
        // TODO: Delete this comment completely later on. I'm keeping it around for now just in case
        // any mysterious crashes occur so I can try reinstating it.

        // ENHANCE: I suspect this tree of flows is over-complex. In part we are trying to work
        // around problems where a getAllItems(dataSetId) or getAllSources(dataSetId) flow is not
        // inherently tagged with its parameter, and thus if the user changes the data set ID from 1
        // to 2 we might see the items for data set 1 with the sources for data set 2 briefly before
        // the items for data set 2 arrive. Using SQLDelight might help avoid this complexity. It's
        // quite possible there is over-complexity in other areas too.

        val dataSetFlow = repository.getAllDataSets()

        val dataSetOnlyDatabaseFlow = selectedDataSetIdStateFlow.flatMapLatest { dataSetIdState ->
            // dataSetId can be null here (e.g. during startup when we haven't yet got the
            // preference yet, and maybe also if the user deletes all the data in the database) so
            // we need to deal with it. I think it would be wrong to use filterNotNull(), because we
            // do want to emit something - in particular, during startup, if datasetId is null and
            // *stays* null (e.g. empty database and SELECTED_DATA_SET_ID_KEY has been set to null
            // as a result), any flow that combine()s this one would never see combine() emit. This
            // just might work out OK, but it feels dangerous. I think empty lists are perfect valid
            // results to emit in the null case.
            // We are combining freshly-created DAO flows, so we cannot see "stale" data here, so
            // the dataSetId we are tagging the results with will be correct. (In practice non-empty
            // lists of results for these queries are self-tagging, but we need to handle empty
            // lists correctly too.)
            val dataSetId = dataSetIdState.valueOrNull() // TODO DELETE FOLLOWING when (dataSetIdState) { is LoadState.Loaded -> { (dataSetIdState as LoadState.Loaded).value } else -> null }
            Log.d("MyFlow", "dataSetOnlyDatabaseFlow dataSetId $dataSetId")
            combine(
                flowOf(dataSetId),
                if (dataSetId != null) repository.getAllItems(dataSetId) else flowOf(
                    emptyList()
                ),
                if (dataSetId != null) repository.getAllSources(dataSetId) else flowOf(
                    emptyList()
                ),
                ::Triple
            )
        }

        val dataSetIdAndItemIdFlow = combine(
            selectedDataSetIdStateFlow,
            selectedItemIdStateFlow,
            ::Pair
        )

        val dataSetIdAndItemIdDatabaseFlow =
            dataSetIdAndItemIdFlow.flatMapLatest { (dataSetIdState, itemIdState) ->
                val dataSetId = dataSetIdState.valueOrNull()
                val itemId = itemIdState.valueOrNull()
                Log.d(
                    "MyFlow",
                    "dataSetIdAndItemIdDatabaseFlow dataSetId $dataSetId, itemId $itemId"
                )
                val priceFlow = if (dataSetId != null && itemId != null)
                    repository.getPricesForItem(dataSetId = dataSetId, itemId = itemId)
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

        val combinedDatabaseFlow = combine(
            dataSetFlow,
            dataSetOnlyDatabaseFlow,
            dataSetIdAndItemIdDatabaseFlow,
            ::Triple)

        val allUserInputFlow = combine(
            selectedDataSetIdStateFlow,
            selectedItemIdStateFlow,
            selectedSourceIdStateFlow,
            ::Triple
        )

        val todoRenameMeFlow = combine(
            selectedSourceIdStateFlow,
            combinedDatabaseFlow,
            settingsRepository.priceAgeSettingsFlow
        ) { _, databaseResults, priceAgeSettings -> Pair(databaseResults, priceAgeSettings) }

        // completeUIStateFlow delivers complete, consistent results which reflect the user's
        // selection. However, it doesn't make any guarantees as to how long it takes to emit after
        // allUserInputFlow emits.
        val completeUIStateFlow =
            todoRenameMeFlow.flatMapLatest { (databaseResults, priceAgeSettings) ->
                Log.d("MyAppPAS", "priceAgeSettings $priceAgeSettings")
                val (dataSetList, taggedItemListAndSourceList, taggedPriceList) = databaseResults
                // We can take the current UI values here because ultimately that's all we care
                // about; if the current flow value we're processing is older, we want to discard it
                // anyway and because the flows are dependent on these parameters, they will emit
                // new values once they finish querying. It feels somewhat ridiculous to have to
                // discard stale values like this but as far as I can tell you either do something
                // like this, accept a mixture of stale values or re-run all your queries every
                // single time even if most of them haven't had a parameter change. Maybe I am doing
                // something silly.
                val dataSetIdState = selectedDataSetIdStateFlow.value
                val itemIdState = selectedItemIdStateFlow.value
                val sourceIdState = selectedSourceIdStateFlow.value
                val dataSetId = dataSetIdState.valueOrNull()
                val itemId = itemIdState.valueOrNull()
                val sourceId = sourceIdState.valueOrNull()

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
                                dataSetIdState,
                                itemIdState
                            )
                        }"
                    )
                    emptyFlow()
                } else {
                    val itemList = taggedItemListAndSourceList.second
                    val sourceList = taggedItemListAndSourceList.third
                    var priceList = taggedPriceList.second

                    val dataSet = dataSetList.find { it.id == dataSetId }
                    val item = itemList.find { it.id == itemId }
                    val source = sourceList.find { it.id == sourceId }

                    Log.d(
                        "MyFlow",
                        "completeUIStateFlow dataSetId ${selectedDataSetIdStateFlow.value} ${dataSet?.id} (list size ${dataSetList.size}), itemId ${item?.id} (list size ${itemList.size}), sourceId ${source?.id} (list size ${sourceList.size})"
                    )

                    if (dataSet != null) {
                        priceList = sanitisePriceUnits(dataSet, priceList)
                    }

                    // ENHANCE: I suspect in practice this analysis is lightweight enough we are
                    // fine doing it in this coroutine on the main thread, but just possibly we
                    // should shift (probably the whole database flow, but maybe just this work)
                    // onto a coroutine on a worker thread?
                    val priceAnalysis = analysePrices(dataSet, priceList, sourceList, priceAgeSettings)

                    Log.d("MyFlow", "derived analysedPriceList")

                    //delay(5000) // TODO HACK
                    flowOf(
                        HomeScreenUIContent(
                            dataSetIdState,
                            dataSet,
                            dataSetList,
                            item,
                            itemList,
                            sourceIdState,
                            source,
                            sourceList,
                            priceAnalysis
                        )
                    )
                }
            }

        val TodoSomeSortOfUiStateFlow = allUserInputFlow
            .flatMapLatest { input ->
                channelFlow /* TODO DELETE <Pair<Boolean, HomeScreenUIContent>> */ {
                    var loadingJob: Job? = null

                    // Subscribe to data
                    val dataJob = launch {
                        completeUIStateFlow.collect { data ->
                            // Cancel loading if it's pending
                            loadingJob?.cancel()
                            loadingJob = null

                            // Emit data
                            send(Pair(false, data))
                        }
                    }

                    // Start loading timer
                    loadingJob = launch {
                        delay(spinnerDelayMillis)
                        // Only emit loading if not already canceled
                        if (isActive) {
                            send(Pair(true, _uiState.value.second))
                        }
                    }

                    // Cleanup on cancel (input change)
                    // TODO: NOT NEEDED AFTER ALL!?!?!?! awaitCancellation()
                }
                // TODO!? .catch { emit(Pair(false) }
            }

        viewModelScope.launch(Dispatchers.Default) {
            // TODO: MORE GROK MAGIC
            // TODO: This *might* actually be correct. I need to look at it calmly and fresh, read
            // up on channelFlow, give it more testing. But I think there is a chance it's sound.
            allUserInputFlow
                .flatMapLatest { input ->
                    channelFlow {
                        var loadingJob: Job? = null

                        // Data stream
                        /* val dataJob = */ launch {
                            completeUIStateFlow.collect { data ->
                                //lastKnownData = data
                                loadingJob?.cancel()
                                send(false to data)
                            }
                        }

                        // Loading timer
                        loadingJob = launch {
                            delay(spinnerDelayMillis)
                            if (isActive) {
                                send(true to _uiState.value.second)
                            }
                        }
                    }
                }
                .collectLatest { (isLoading, data) ->
                    _uiState.value = isLoading to data
                }
        }
    }

    var previousPrice: MutableState<Price?> = mutableStateOf(null)

    fun confirmPrice(price: Price) {
        val now = Instant.now()
        val newPrice = price.copy(confirmedAt = now, modifiedAt = now)
        viewModelScope.launch {
            asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                //delay(5000) // TODO TEMP HACK
                repository.updateOrInsertPrice(newPrice)
                previousPrice.value = price
                asyncOperationStatus.update(AsyncOperationStatus.Success(null))
            } catch (e: Exception) {
                e("HomeViewModel", "Unexpected exception", e)
                asyncOperationStatus.update(AsyncOperationStatus.Error("updatePrice failed: ${e.toString()}"))
            }
        }
    }

    fun undoConfirmPrice(priceBeforeRevert: Price, priceAfterRevert: Price) {
        viewModelScope.launch {
            asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                //delay(5000) // TODO TEMP HACK
                repository.revertPrice(
                    priceBeforeRevert = priceBeforeRevert,
                    priceAfterRevert = priceAfterRevert
                )
                previousPrice.value = null
                asyncOperationStatus.update(AsyncOperationStatus.Success(null))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected exception", e)
                asyncOperationStatus.update(AsyncOperationStatus.Error("undoConfirmPrice failed: ${e.toString()}"))
            }
        }
    }

    fun deletePrice(price: Price) {
        viewModelScope.launch {
            asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                //delay(5000) // TODO TEMP HACK
                repository.deletePriceById(price.id)
                previousPrice.value = null
                asyncOperationStatus.update(AsyncOperationStatus.Success(null))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected exception", e)
                asyncOperationStatus.update(AsyncOperationStatus.Error("deletePrice failed: ${e.toString()}"))
            }
        }
    }

    fun countPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long) = repository.countPriceHistory(dataSetId, itemId, sourceId)

    val asyncOperationStatus = SyncedStateEvent<AsyncOperationStatus>(AsyncOperationStatus.Idle)

}

// Returns a version of priceList where any prices which are expressed in units not supported by the
// data set are replaced by a unit that is. This can happen if the user removes a unit family from a
// data set. We don't try to patch this up in the database because then the user would immediately
// lose the original unit and can't undo the change if they realise it was a mistake. We could in
// theory handle this individually in other parts of the code, but that's an invitation to forget in
// one place in the future and have the code blow up. (Allowing the "bad" unit to persist as long as
// possible also opens up a corner case where a price was expressed in e.g. US pints, the user
// changes the data set to imperial and the price shows up in "pints" (with no qualifier) but it is
// a US pint and not an imperial pint and maybe this unit confusion is even allowed to persist when
// the user edits without changing the unit from the dropdown.) By fixing up the data as we read it
// out of the database, we avoid all this. TODO: REVIEW THIS COMMENT LATER
// TODO: Just possibly this should be an extension on DataSet??
fun sanitisePriceUnits(dataSet: DataSet, priceList: List<Price>): List<Price> {
    val relevantUnitFamilies = getRelevantUnitFamilies(dataSet)
    myCheck(relevantUnitFamilies.isNotEmpty()) { "Expected at least one relevant unit family for dataSet ${dataSet.id}" }
    // getRelevantUnitFamilies() will in practice generate a LinkedHashSet, so first() here will be
    // deterministic and return the first family inserted. If this were to change in future, it
    // wouldn't be the end of the world, we'd just see some modest inconsistency in the results for
    // what is already a corner case.
    val replacementUnitFamily = relevantUnitFamilies.first()
    return priceList.map { price ->
        if (price.quantity.unit.unitFamilies.any { it in relevantUnitFamilies }) {
            price
        } else {
            price.copy(quantity = price.quantity.to(MeasurementUnit.entries.first { replacementUnitFamily in it.unitFamilies && price.quantity.unit.quantityType == it.quantityType }))
        }
    }
}

// TODO: It's annoying to have to write near-identical code here for PriceHistory. We can possibly factor out some commonality, I just based it out for now.
fun sanitisePriceHistoryUnits(dataSet: DataSet, priceHistoryList: List<PriceHistory>): List<PriceHistory> {
    val relevantUnitFamilies = getRelevantUnitFamilies(dataSet)
    myCheck(relevantUnitFamilies.isNotEmpty()) { "Expected at least one relevant unit family for dataSet ${dataSet.id}" }
    // getRelevantUnitFamilies() will in practice generate a LinkedHashSet, so first() here will be
    // deterministic and return the first family inserted. If this were to change in future, it
    // wouldn't be the end of the world, we'd just see some modest inconsistency in the results for
    // what is already a corner case.
    val replacementUnitFamily = relevantUnitFamilies.first()
    return priceHistoryList.map { priceHistory ->
        if (priceHistory.userUnit.unitFamilies.any { it in relevantUnitFamilies }) {
            priceHistory
        } else {
            priceHistory.copy(userUnit = MeasurementUnit.entries.first { replacementUnitFamily in it.unitFamilies && priceHistory.userUnit.quantityType == it.quantityType })
        }
    }
}

private const val appName = "My Price Log"

private const val multiplicationSign = "\u00d7"
private const val emDash = "\u2014"
private const val copyrightSymbol = "\u00a9"
private const val bulletPoint = "\u2022"

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
// TODO: a whole second feels insanely slow
const val defaultValidationMessageDelayMillis = 200L

// TODO: If this is too long, the user can break something different, click Save again and have to
// wait until the first animation finishes. Let's start with 1000 and see how it goes.
const val errorHighlightBoxVisibleTimeMillis = 1000L

// TODO: https://m3.material.io/foundations/layout/applying-layout/compact says 16dp left and right
// margins - maybe change this? Then again there are places where I've used edge-to-edge for lists
// so I just don't know. For that matter, are there 16.dp margins supposed to be added to left and
// right of the top app bar as well? Or just the body content below it?
val screenBorder = 8.dp

// MD3 specs say there should be a 24.dp horizontal border, but this seems quite ugly. The left hand
// edge of the dialog's body controls don't line up with the close icon and the right hand edges
// don't line up with the right hand edge of the "Save" text button. Some of the screenshots in the
// documentation seem to show some but not all of these misalignments. It just feels half-baked and
// inconsistent so I'm going to go with this.
val fullScreenDialogHorizontalBorder = 16.dp

val fullScreenDialogVerticalBorder = 8.dp

// MD3 says 12.dp but MyExposedDropdownMenuBox's dropdown item text doesn't line up with the parent
// TextField text with that. TODO: We could override it for that specific case and use 12.dp for
// other menus?
val menuLeftPadding = 16.dp
// Seems best to make the right padding symmetrical.
val menuRightPadding = menuLeftPadding

val defaultErrorHighlightOffset = 6.dp

// MD3 (while deprecating the navigation drawer anyway) says the width should be 360.dp. We don't
// properly respect that because I think it looks bad on a phone to have the drawer fill the whole
// screen, but we do respect it as far as using it as a maximum width. This will probably never kick
// in unless someone is using the app on a tablet, but still.
val maxNavigationDrawerWidth = 360.dp

// MD3 standard values
val oneLineListItemHeight = 56.dp
val listItemHorizontalPadding = 16.dp

// These arbitrary lengths apply to the UI only (not the database) and are just intended to stop the
// user typing insane amounts of text into TextFields and breaking layouts. They may need to be
// tweaked later.
const val maxDataSetNameLength = 32
const val maxItemNameLength = 32
const val maxSourceNameLength = 32
const val maxNotesLength = 1024
const val maxSearchLength = 32

// 11 is a bit arbitrary but we're just trying to avoid the user filling the TextField with hundreds
// of characters of junk and breaking the screen layout badly. 11 is pretty generous as it allows
// just under a million with two decimal places and a (manually entered) thousands separator, so we
// could tighten this up a bit if desirable.
const val maxDecimalLength = 11

@Composable
fun ItemSourceSelector(
    asyncOperationStatus: AsyncOperationStatus,
    source: Source?,
    sourceList: List<Source>,
    item: Item?,
    itemList: List<Item>,
    onSelectedItemIdChange: (Long) -> Unit,
    onSelectedSourceIdChange: (Long?) -> Unit,
    onItemSearchClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Item selector
        val clickableModifier = if (asyncOperationStatus.isNotBusy()) {
            Modifier.clickable { onItemSearchClick() }
        } else {
            Modifier
        }
        // For reasons I don't quite understand, using key() here avoids a frame or two of delay in
        // applying the new colors= selection when asyncOperationStatus changes. I think the basic
        // idea (according to ChatGPT) is that this forces the whole thing to be recomposed, but it
        // is a bit voodoo.
        key(asyncOperationStatus) {
            TextField(
                value = item?.name ?: "",
                onValueChange = { /* No-op, read-only */ },
                label = { Text("Product") },
                enabled = false, // so Modifier.clickable() works
                modifier = Modifier
                    .fillMaxWidth()
                    .then(clickableModifier),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search products",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                // There might be an argument that this should "sometimes" get the focused colours,
                // but since clicking on it immediately opens a full screen dialog, I think it's
                // probably reasonable to hard-code false here.
                colors = if (asyncOperationStatus.isNotBusy()) myTextFieldColors(false) else TextFieldDefaults.colors(),
                // It is rare to have no item selected, but if this happens and some items are
                // defined, the user should fairly easily figure out what's happening (they just
                // need to tap this TextField to open the selector). So we show a supportingText
                // only if there are no items at all.
                supportingText = if (item != null || itemList.isNotEmpty()) null else { {
                        Text("There are no products in this collection. Add one using the overflow menu at the top right.")
                } }
            )
        }

        Spacer(
            modifier = Modifier
                .height(
                    16.dp
                )
                .fillMaxWidth()
            //.background(color = Color.Red) // TODO DEBUG HACK
        )

        // If sourceList is empty this will generate a single-item menu with just "None" in,
        // but that is probably better than the "skeleton" menu we get with no items in.
        val locale = LocalConfiguration.current.locales[0]
        val sourceListSorted = remember(sourceList, locale) {
            listOf(Pair(-1L, "None")) + sourceList.sortedByLocale({ it.name }, locale)
                .map { Pair(it.id, it.name) }
        }
        Log.d("MyApp", "sourceListSorted $sourceListSorted")
        // ENHANCE: Did wonder if MyExposedDropdownMenuBox should allow null IDs to avoid the need
        // for the "-1" hack here, but I really didn't want to have to make every user of it be
        // null-tolerant when it *won't* hand you a null itself unless you gave it one in the input
        // item list, so this is perhaps best but I'm not too sure. I did try wrapping the null
        // inside a simple Nullable<T> so it could "pass through" MyExposedDropdownMenuBox without
        // altering the API and I think the idea is sound but I started to run into incomprehensible
        // "out"/covariance stuff and it just felt too much just to fix this where -1L is an easy
        // hack.
        key(asyncOperationStatus) { // TODO: as above
            MyExposedDropdownMenuBox(
                modifier = Modifier
                    // .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                // Note that if source is null, we pass that null through to selectedId so the
                // dropdown starts off with nothing selected and the "Store" label expands to form a
                // large "prompt". We could turn null into -1L and have "None" shown, but it's
                // probably nicer this way.
                selectedId = source?.id, /* ?: -1L */
                onItemSelected = { onSelectedSourceIdChange(if (it == -1L) null else it) },
                enabled = asyncOperationStatus.isNotBusy(),
                label = { Text("Store") },
                // It's normal to have no source selected, but if there are no sources defined at
                // all it seems best to offer the user a hint.
                supportingText = if (sourceList.isNotEmpty()) null else { {
                    Text("There are no stores in this collection. Add one using the overflow menu at the top right.")
                } },
                items = sourceListSorted,
                getId = { it.first },
                getItemText = { it.second },
            )
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
    disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

// ENHANCE: I am not sure if we should disable the on-click ripple here when opening the menu. It's
// not that clear to me if the guidelines at https://m3.material.io/components/menus/guidelines also
// suggest other behaviours we don't have, although the core of this implementation is the standard
// DropdownMenu. Maybe there's some element of MD3 Expressive in those guidelines. It might be worth
// trying the experimental ExposedDropdownMenuBox again at some point, although up to now I have
// found it not to work very well. Maybe there is or will be another standard component worth using
// here.
@Composable
fun <T, ID : Comparable<ID>> MyExposedDropdownMenuBox(
    modifier: Modifier = Modifier,
    selectedId: ID?,
    onItemSelected: (ID) -> Unit,
    enabled: Boolean = true,
    label: @Composable () -> Unit,
    supportingText: @Composable (() -> Unit)? = null,
    items: List<T>,
    getId: (T) -> ID,
    getItemText: (T) -> String,
    getCollapsedItemText: ((T) -> String)? = null,
    getDividerBetween: ((T, T) -> Boolean)? = null,
) {
    var textFieldWidth by remember { mutableIntStateOf(0) }
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ItemWithDropdown(
            // We use .widthIn to force the dropdown to be at least as wide as its parent TextField
            // while allowing it to be wider (mainly for dropdowns on TextFields which don't occupy
            // the full screen width). ENHANCE: In practice this probably works well, but we might
            // want to add parameters to allow our caller to force exact width or other variations.
            dropdownModifier = Modifier.widthIn(min = with(LocalDensity.current) { textFieldWidth.toDp() }),
            selectedId = selectedId,
            onItemSelected = onItemSelected,
            enabled = enabled,
            onExpand = { isExpanded = it },
            items = items,
            getId = getId,
            getItemText = getItemText,
            getDividerBetween = getDividerBetween,
        ) {
            val itemMap = items.associateBy { getId(it) }
            val valueString = if (selectedId == null) "" else {
                val item = itemMap[selectedId]
                if (item != null) (getCollapsedItemText ?: getItemText)(item) else "Invalid ID $selectedId"
            }
            TextField(
                value = valueString, // pulled out just to improve code formatting
                onValueChange = { /* No-op, handled by dropdown */ },
                label = label,
                readOnly = true,
                enabled = false, // so Modifier.clickable() works
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        // Not 100% sure about this rotation behaviour, but e.g. the screenshot of
                        // the "Text field" configuration at the bottom of
                        // https://m3.material.io/components/menus/specs seems to show this, so
                        // let's go with it.
                        modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    },
                // ENHANCE: It isn't ideal to use isExpanded as a substitute for focus here, but it
                // doesn't look too bad in practice. Because we have to have the TextField disabled
                // in order to make it clickable, it doesn't seem to actually get focus as far as
                // onFocusChanged is concerned (even when it gets that "it's focus but it's not
                // focus" D-pad navigation focus).
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

@Composable
fun RelativeTimeText(augmentedPrice: AugmentedPrice) {
    val confirmedAt = augmentedPrice.basePrice.confirmedAt
    var now by remember(confirmedAt) { mutableStateOf(Instant.now()) }
    val ageInSeconds = Duration.between(confirmedAt, now).seconds
    val secondsPerDay = 24 * 60 * 60

    // This LaunchedEffect causes the *state variable* "now" to update periodically, forcing a
    // recomposition so the user can see the age increasing.
    LaunchedEffect(confirmedAt) {
        // NB: The captured ageInSeconds will *not* update in here - this coroutine is launched once
        // on the first composition for a specific value of "instant".
        while (true) {
            // ENHANCE: We could maybe sleep until "the next minute boundary" when ageInMinutes<60,
            // so we're not executing every second for the first minute when the display only has
            // minute resolution.
            val ageInMinutes = Duration.between(confirmedAt, Instant.now()).toMinutes()
            Log.d("MyAppRTT", "ageInMinutes: $ageInMinutes")
            val delayDuration = when {
                ageInMinutes < 1       -> 1_000L           // update every second for first minute
                ageInMinutes < 24 * 60 -> 60_000L          // every minute for first day
                else                   -> 60 * 60 * 1_000L // every hour after that
            }
            delay(delayDuration)
            now = Instant.now()
            Log.d("MyAppRTT", "updated now: $now")
        }
    }

    // getRelativeTimeSpanString() returns "0 min. ago" in English for ages under 60 seconds, and
    // presumably similar in other languages, so we special-case this.
    Log.d("MyAppRTT", "$ageInSeconds $confirmedAt $now")
    val relativeTime = if (ageInSeconds < 60) "now" else DateUtils.getRelativeTimeSpanString(
        confirmedAt.toEpochMilli(),
        now.toEpochMilli(),
        DateUtils.MINUTE_IN_MILLIS,
        DateUtils.FORMAT_ABBREV_RELATIVE
    ).toString()
    // ENHANCE: I don't know if it's slightly weird to color this to indicate it's stale without
    // showing a stale icon or having some supporting text. May want to revisit this in the future.
    Text(
        relativeTime,
        color = if (augmentedPrice.ageClass == AgeClass.FRESH) Color.Unspecified else MaterialTheme.colorScheme.error
    )
}

fun formatPrice(price: Double, dataSet: DataSet, locale: Locale): String {
    // At least on Android this doesn't throw for invalid three-letter currency codes but it will
    // throw if given currency code "AAAA", so it seems safest to catch exceptions and have a
    // fallback, even if it's not great.
    try {
        val numberFormat = NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance(dataSet.currencyCode)
        }
        return numberFormat.format(price)
    } catch (e: Exception) {
        // Generate a generic-ish "USD 1234" value as a fallback, without trying to use any
        // localisation settings.
        // ENHANCE: Eventually we might want to see if there's any useful data in a currency
        // prefix/suffix/decimal places set of fields in dataSet, but we don't have those yet. But
        // even if we did, we'd probably already be using those in preference to
        // getCurrencyInstance(), so they wouldn't help us at this point.
        val numberFormat = NumberFormat.getNumberInstance()
        // TODO: The "x" instead of a space in the next line is temporary, just to make it more
        // obvious if this code is coming into play while I am developing/testing.
        return "${dataSet.currencyCode}x${numberFormat.format(price)}"
    }
}


fun Double.roundTo(decimalPlaces: Int): Double {
    val factor = 10.0.pow(decimalPlaces)
    return kotlin.math.round(this * factor) / factor
}

// ENHANCE: Note that selectedId is not used. I would like to use this to focus the previously
// selected item when expanding the dropdown using a D-pad, instead of defaulting to the first item.
// However, this appears to be ninja-grade development and I tried tweaking multiple AI-suggested
// solutions and got nothing but crashes.
@Composable
fun <T, ID : Comparable<ID>> ItemWithDropdown(
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier,
    @Suppress("UNUSED_PARAMETER") selectedId: ID?, // see above
    onItemSelected: (ID) -> Unit,
    enabled: Boolean = true,
    onExpand: (Boolean) -> Unit = {},
    items: List<T>,
    getId: (T) -> ID,
    getItemText: (T) -> String,
    getDividerBetween: ((T, T) -> Boolean)? = null,
    content: @Composable () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    Box(
        modifier = modifier.then(
            if (enabled) Modifier.clickable {
                // We remove focus from anything else that has it in order to "fake" this component
                // getting the focus. Without this, if a TextField has focus it retains it (including
                // its focused colors) when the dropdown appears, which feels wrong.
                focusManager.clearFocus(force = true)
                expanded = true
                @Suppress("KotlinConstantConditions") onExpand(expanded)
            }
            else Modifier)
    ) {
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
                        Text(getItemText(item))
                    },
                    onClick = {
                        onItemSelected(getId(item))
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
    modifier: Modifier = Modifier,
    selectedId: ID?,
    label: String,
    text: String,
    onItemSelected: (ID) -> Unit,
    dropdownContentDescription: String,
    items: List<T>,
    getId: (T) -> ID,
    getItemText: (T) -> String,
    getDividerBetween: ((T, T) -> Boolean)? = null,
    enabled: Boolean = true,
) {
    // fontSize/iconSize are used here so that the drop down icon scales correctly when the user
    // changes the system font size.
    val fontSize = MaterialTheme.typography.bodyLarge.fontSize
    val iconSize = with(LocalDensity.current) { fontSize.toDp() }

    ItemWithDropdown(
        modifier = modifier,
        selectedId = selectedId,
        onItemSelected = onItemSelected,
        enabled = enabled,
        items = items,
        getId = getId,
        getItemText = getItemText,
        getDividerBetween = getDividerBetween,
    ) {
        LabeledItem(label = label) {
            Row {

                Box {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ENHANCE: This text doesn't change colour when enabled is false, TBH this
                        // probably looks OK and it might actually look ugly if it did in my specific
                        // UI, but maybe it ought to. And equally maybe the LabeledItem itself should
                        // change colour when disabled.
                        Text(text)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = dropdownContentDescription,
                            modifier = Modifier.size(iconSize /* 16.dp */)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemSourceInfoLive(
    vm: HomeViewModel,
    asyncOperationStatus: AsyncOperationStatus,
    dataSet: DataSet,
    item: Item?,
    source: Source?,
    sourceList: List<Source>,
    augmentedPrice: AugmentedPrice?,
    onEditPriceClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onDeletePriceClick: () -> Unit,
) {
    // TODO: Maybe this should live on the viewmodel
    OnAppLifecycleEvent { event ->
        if (event == Lifecycle.Event.ON_STOP) { // app has left the foreground
            vm.previousPrice.value =
                null // TODO: arguably we should do all this via a "call up to top level", but not sure it's necessary - perhaps more to the point we should be calling a function on viewmodel to do this
        }
    }

    // ENHANCE: Will we have a "special offer"/"short term price" flag and maybe associated data?
    // Gut feeling is no, how to handle expiry/deletion gets complex from UI and internal
    // perspective. It's not as if the offer duration is usually clearly stated. Free text note
    // probably can be used for this.
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Box {
            StorePriceCardBody(vm, asyncOperationStatus, dataSet, augmentedPrice, onEditPriceClick)
            StorePriceCardMenu(vm, asyncOperationStatus, dataSet, item, source, augmentedPrice, onViewHistoryClick, onDeletePriceClick, menuModifier = Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
fun StorePriceCardBody(
    vm: HomeViewModel,
    asyncOperationStatus: AsyncOperationStatus,
    dataSet: DataSet,
    augmentedPrice: AugmentedPrice?,
    onEditPriceClick: () -> Unit,
) {
    // ENHANCE: When the card expands, the button(s) on the "bottom" row of the card jump
    // down instead of animating smoothly "following" the bottom of the card - probably
    // because this layout is sort of "top to bottom". I suspect this can be worked around
    // by using a box and having most of the content inside a column with
    // .align(Alignment.TopStart) and then follow that by the button row with
    // .align(Alignment.BottomCenter) or something along these lines. The trouble with the
    // code as currently structured is that the buttons are generated in conditional code
    // and getting the right layout of composables isn't trivial. It is probably worth
    // tweaking this for visual polish - it might make things clearer anyway, e.g. if we
    // factor out some sub-composables - but I'm not going to get involved with it right
    // now. We may need to attach .animateContentSize() to the Card instead of the Column.
    // All this said, because the "Store" dropdown tends to obscure this card in practice,
    // this isn't all that noticeable.
    Column(
        modifier = Modifier
            .animateContentSize()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
    ) {
        CardTitle("Store price")

        if (true) {
            if (augmentedPrice == null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("There is no price recorded for this product at this store yet.")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FilledTonalButton(
                            onClick = onEditPriceClick,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Add")
                        }
                    }
                }
            } else {
                val price = augmentedPrice.basePrice

                PackPriceAndSizeRow(
                    price.price,
                    price.count,
                    price.quantity,
                    dataSet,
                    asyncOperationStatus
                )

                LabeledItem(
                    modifier = Modifier.padding(bottom = 8.dp),
                    label = "Confirmed" /* "Last checked" */
                ) {
                    RelativeTimeText(augmentedPrice)
                }

                if (price.notes.isNotEmpty()) {
                    Row(modifier = Modifier.padding(bottom = 8.dp)) {
                        LabeledItem("Notes") {
                            Text(price.notes)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriceJudgementIndicator(augmentedPrice.priceJudgement)
                    EditConfirmButtons(vm, asyncOperationStatus, augmentedPrice, onEditPriceClick)
                }
            }
        }
    }
}

@Composable
fun OverflowMenu(
    enabled: Boolean = true,
    modifier: Modifier,
    content: @Composable (requestMenuClose: () -> Unit) -> Unit
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    IconButton(
        enabled = enabled,
        onClick = { menuExpanded = true },
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More options"
        )
        DropdownMenu(
            expanded = menuExpanded, onDismissRequest = { menuExpanded = false }
        ) {
            content({menuExpanded = false })
        }
    }
}

@Composable
fun StorePriceCardMenu(
    vm: HomeViewModel,
    asyncOperationStatus: AsyncOperationStatus,
    dataSet: DataSet,
    item: Item?,
    source: Source?,
    augmentedPrice: AugmentedPrice?,
    onViewHistoryClick: () -> Unit,
    onDeletePriceClick: () -> Unit,
    menuModifier: Modifier,
    ) {
    val priceHistoryCount by remember(dataSet.id, item?.id, source?.id) {
        if (item != null && source != null) {
            vm.countPriceHistory(dataSet.id, item.id, source.id)
        } else {
            flowOf(0L)
        }
    }.collectAsStateWithLifecycle(initialValue = 0L)

    OverflowMenu(
        enabled = asyncOperationStatus.isNotBusy(),
        modifier = menuModifier
    ) { requestMenuClose ->
        MyDropdownMenuItem(
            text = { Text("View history") },
            enabled = priceHistoryCount > 0,
            onClick = { requestMenuClose(); onViewHistoryClick() }
        )
        MyDropdownMenuItem(
            text = { Text("Delete price") },
            enabled = augmentedPrice != null,
            onClick = { requestMenuClose(); onDeletePriceClick() }
        )
    }
}


@Composable
fun PriceJudgementIndicator(priceJudgement: PriceJudgement) {
    Row {
        when (priceJudgement) {
            PriceJudgement.NONE -> {}
            PriceJudgement.GOOD -> {
                GoodPriceIcon()
                Spacer(modifier = Modifier.width(4.dp))
                Text("Good price")
            }

            PriceJudgement.OK -> {
                OkPriceIcon()
                Spacer(modifier = Modifier.width(4.dp))
                Text("OK price")
            }

            PriceJudgement.BAD -> {
                BadPriceIcon()
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bad price")
            }
        }
    }
}

@Composable
fun EditConfirmButtons(
    vm: HomeViewModel,
    asyncOperationStatus: AsyncOperationStatus,
    augmentedPrice: AugmentedPrice,
    onEditPriceClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        FilledTonalButton(
            onClick = onEditPriceClick,
            shape = MaterialTheme.shapes.small,
            enabled = asyncOperationStatus.isNotBusy(),
        ) {
            Text("Edit")
        }

        Spacer(modifier = Modifier.width(8.dp))

        // ENHANCE: A couple of possible polish opportunities here:
        // - We should maybe disable the "Confirm" button if the label is "now", although arguably
        //   this is a bit unnecessary and leads to a small visual distraction if the user is
        //   looking at the screen when it ticks over to 1 minute and is re-enabled.
        // - We should maybe animate changes to the "Confirmed" text label if it changes due to a
        //   confirm/undo click (rather than just because time passed and it ticked to the next
        //   value). This would help make it more obvious to the user what Confirm/Undo are actually
        //   affecting.

        // The "Confirm" button is the primary button - we expect it to be the
        // button users click on most on this card (most of the time prices
        // won't have changed on subsequent visits) - so it gets the position on
        // the right.
        val locale = LocalConfiguration.current.locales[0]
        val showConfirmButton = vm.previousPrice.value == null
        FilledTonalButton(
            /* modifier = Modifier.width(confirmButtonWidth) ,*/
            onClick = {
                if (showConfirmButton) {
                    vm.confirmPrice(augmentedPrice.basePrice)
                } else {
                    vm.undoConfirmPrice(
                        augmentedPrice.basePrice, vm.previousPrice.value!!
                    )
                }
            },
            shape = MaterialTheme.shapes.small,
            enabled = asyncOperationStatus.isNotBusy(),
        ) {
            AnimatedContent(targetState = showConfirmButton) { showConfirm ->
                // ENHANCE: "Undo" is perhaps borderline unclear as to what it is undoing (although
                // I hope the user observing the transition from "Confirm"->"Undo" will act as a
                // hint), but at least on my small emulated phone, "Undo confirm" looks a bit ugly
                // or (with "Good price") doesn't fit and causes the button to become multi-line.
                Text(if (showConfirm) "Confirm" else "Undo")
            }
        }
    }

}

@Composable
fun <T> DataTable(
    header: List<String>,
    items: List<T>,
    columns: List<@Composable (T) -> Unit>,
    highlightRow: Int? = null,
    columnWeights: List<Float> = List(header.size) { 1f },
    columnAlignments: List<CellAlignment> = List(header.size) { CellAlignment.Start },
    onClick: ((T) -> Unit)? = null,
) {
    myRequire(header.size == columns.size) { "Expected same header and columns size but have ${header.size} and ${columns.size} respectively" }
    myRequire(header.size == columnWeights.size) { "Expected same header and columnWeights size but have ${header.size} and ${columnWeights.size} respectively" }
    myRequire(header.size == columnAlignments.size) { "Expected same header and columnAlignments size but have ${header.size} and ${columnAlignments.size} respectively" }

    fun alignmentModifier(cellAlignment: CellAlignment): Modifier = when (cellAlignment) {
        CellAlignment.Start -> Modifier.wrapContentWidth(Alignment.Start)
        CellAlignment.Center -> Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        CellAlignment.End -> Modifier.wrapContentWidth(Alignment.End)
    }

    Column {
        // ENHANCE: If we allow user-selectable units in this header via a dropdown, its height may
        // need increasing to oneLineListItemHeight.
        Row(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainerHighest),
            verticalAlignment = Alignment.CenterVertically
        ) {
            header.forEachIndexed { colIndex, title ->
                Box(
                    Modifier
                        .weight(columnWeights[colIndex])
                        .padding(8.dp)
                        .then(alignmentModifier(columnAlignments[colIndex]))
                ) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp, color = MaterialTheme.colorScheme.outline /* Variant */
        )

        items.forEachIndexed { rowIndex, item ->
            val isHighlighted = rowIndex == highlightRow
            val textStyle = if (isHighlighted) {
                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            } else {
                MaterialTheme.typography.bodyLarge
            }
            val textColor = if (isHighlighted) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
            val rowBackground = if (isHighlighted) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }


            CompositionLocalProvider(
                LocalTextStyle provides textStyle,
                LocalContentColor provides textColor
            ) {
                Row(
                    modifier = Modifier
                        .background(rowBackground)
                        .height(oneLineListItemHeight)
                        .then(if (onClick != null) Modifier.clickable { onClick(item) } else Modifier)
                    , verticalAlignment = Alignment.CenterVertically
                ) {
                    columns.forEachIndexed { colIndex, cell ->
                        Box(
                            Modifier
                                .weight(columnWeights[colIndex])
                                .padding(8.dp)
                                .then(alignmentModifier(columnAlignments[colIndex]))
                        ) {
                            cell(item)
                        }
                    }
                }
            }
        }
    }
}

// A simple wrapper around DropdownMenuItem applying MD3 formatting.
// ENHANCE: This isn't fully general as I don't want to add stuff that isn't going to get tested; I
// can always expand it later.
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

data class PriceAgeSettings(val stalePriceThreshold: Int, val ancientPriceThresholdDays: Int, val annualInflationPercent: Int)

// TODO: ChatGPT magic
class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    private object Keys {
        val STALE_PRICE_THRESHOLD_KEY =
            intPreferencesKey("stale_price_threshold") // TODO: RENAME THIS AND ALL ASSOCIATED VARS TO INCLUDE "DAYS"?
        val ANCIENT_PRICE_THRESHOLD_DAYS_KEY = intPreferencesKey("ancient_price_threshold_days")
        val ANNUAL_INFLATION_PERCENT_KEY = intPreferencesKey("annual_inflation_percent")
    }

    val stalePriceThresholdFlow: Flow<Int> = dataStore.data
        .map { prefs -> prefs[Keys.STALE_PRICE_THRESHOLD_KEY] ?: defaultStalePriceThreshold }

    val ancientPriceThresholdDaysFlow: Flow<Int> = dataStore.data.map { prefs -> prefs[Keys.ANCIENT_PRICE_THRESHOLD_DAYS_KEY] ?: defaultAncientPriceThresholdDays }

    val annualInflationPercentFlow: Flow<Int> = dataStore.data.map { prefs -> prefs[Keys.ANNUAL_INFLATION_PERCENT_KEY] ?: defaultAnnualInflationPercent }

    val priceAgeSettingsFlow: Flow<PriceAgeSettings> = combine(stalePriceThresholdFlow, ancientPriceThresholdDaysFlow, annualInflationPercentFlow) { stalePriceThreshold, ancientPriceThresholdDays, annualInflationPercent ->
        PriceAgeSettings(stalePriceThreshold, ancientPriceThresholdDays, annualInflationPercent)
    }

    fun setStalePriceThresholdAsync(stalePriceThreshold: Int) {
        setValueAsync(Keys.STALE_PRICE_THRESHOLD_KEY, stalePriceThreshold)
    }

    fun setAncientPriceThresholdDaysAsync(ancientPriceThresholdDays: Int) {
        setValueAsync(Keys.ANCIENT_PRICE_THRESHOLD_DAYS_KEY, ancientPriceThresholdDays)
    }

    fun setAnnualInflationPercentAsync(annualInflationPercent: Int) {
        setValueAsync(Keys.ANNUAL_INFLATION_PERCENT_KEY, annualInflationPercent)
    }

    // TODO!?
    private fun <T> setValueAsync(key: Preferences.Key<T>, value: T) {
        AppScope.io.launch {
            dataStore.edit { it[key] = value }
        }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// TODO: MOVE THE FOLLOWING!?
const val defaultStalePriceThreshold = 30
const val defaultAncientPriceThresholdDays = 180
const val defaultAnnualInflationPercent = 5

data class HomeScreenUIContent(
    val dataSetIdState: LoadState<Long>,
    val dataSet: DataSet?,
    val dataSetList: List<DataSet>,
    val item: Item?,
    val itemList: List<Item>,
    val sourceIdState: LoadState<Long>,
    val source: Source?,
    val sourceList: List<Source>,
    val priceAnalysis: PriceAnalysis,
) {
    companion object {
        fun createEmpty(): HomeScreenUIContent {
            return HomeScreenUIContent(
                dataSetIdState = LoadState.Loading,
                dataSet = null,
                dataSetList = emptyList(),
                item = null,
                itemList = emptyList(),
                sourceIdState = LoadState.Loading,
                source = null,
                sourceList = emptyList(),
                priceAnalysis = PriceAnalysis(emptyList(), null),
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
    val dataSet: DataSet,
    val item: Item,
    val source: Source,
    val nonLinearEdit: Boolean,
    val frozenLocale: Locale,
) {
    fun saveState(handle: SavedStateHandle) {
        saveEditablePriceState(handle)
        handle[ORIGINAL_PRICE_KEY] = originalPrice
        handle[DATA_SET_KEY] = dataSet
        handle[ITEM_KEY] = item
        handle[SOURCE_KEY] = source
        handle[NON_LINEAR_EDIT_KEY] = nonLinearEdit
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
        private const val NON_LINEAR_EDIT_KEY = "non_linear_edit"
        private const val LOCALE_TAG = "localeTag"

        fun fromSavedState(handle: SavedStateHandle): EditPriceScreenUIContent? {
            val savedEditablePrice: EditablePrice? = handle[EDITABLE_PRICE_KEY]
            Log.d("MyApp", "fromSavedState savedEditablePrice $savedEditablePrice")
            val savedOriginalPrice: EditablePrice? = handle[ORIGINAL_PRICE_KEY]
            val savedDataSet: DataSet? = handle[DATA_SET_KEY]
            val savedItem: Item? = handle[ITEM_KEY]
            val savedSource: Source? = handle[SOURCE_KEY]
            val savedNonLinearEdit: Boolean? = handle[NON_LINEAR_EDIT_KEY]
            val savedLocaleTag: String? = handle[LOCALE_TAG]
            if (savedEditablePrice != null && savedOriginalPrice != null && savedDataSet != null && savedItem != null && savedSource != null && savedNonLinearEdit != null && savedLocaleTag != null) {
                Log.d("MyApp", "reconstructed EditPriceScreenUIContent")
                return EditPriceScreenUIContent(
                    mutableStateOf(savedEditablePrice),
                    savedOriginalPrice,
                    savedDataSet,
                    savedItem,
                    savedSource,
                    savedNonLinearEdit,
                    Locale.forLanguageTag(savedLocaleTag)
                )
            } else {
                Log.d("MyApp", "couldn't reconstruct EditPriceScreenUIContent")
                return null
            }
        }
    }
}

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
    val dataSet: DataSet,
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
        private const val DATA_SET_KEY = "dataSet"
        private const val LOCALE_TAG = "localeTag"

        fun fromSavedState(savedStateHandle: SavedStateHandle): EditSourceScreenUIContent? {
            val savedEditableSource: EditableSource? = savedStateHandle[EDITABLE_SOURCE_KEY]
            val savedOriginalSource: EditableSource? = savedStateHandle[ORIGINAL_SOURCE_KEY]
            val savedDataSet: DataSet? = savedStateHandle[DATA_SET_KEY]
            val savedLocaleTag: String? = savedStateHandle[LOCALE_TAG]
            if (savedEditableSource != null && savedOriginalSource != null && savedDataSet != null && savedLocaleTag != null) {
                return EditSourceScreenUIContent(
                    mutableStateOf(savedEditableSource),
                    savedOriginalSource,
                    savedDataSet,
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

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    navController: NavHostController,
    onEditPriceClick: (HomeScreenUIContent) -> Unit,
    onItemSearchClick: (HomeScreenUIContent) -> Unit,
    onViewHistoryClick: (HomeScreenUIContent) -> Unit,
    onEditDataSetsClick: (HomeScreenUIContent) -> Unit,
    onEditItemsClick: (HomeScreenUIContent) -> Unit,
    onEditSourcesClick: (HomeScreenUIContent) -> Unit,
    onSettingsClick: () -> Unit,
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
    Log.d("MyAppBG", "uiContent.dataSetIdState ${uiContent.dataSetIdState} uiContent.sourceIdState ${uiContent.sourceIdState}")

    if (uiContent.dataSetIdState is LoadState.Loading) {
        // Just leave the home screen blank until we get the first async population of the selected
        // data set. We don't want to briefly show the scaffold with the app title before flashing
        // over to the current collection, nor do we want to show the "no collection is selected"
        // fallback texts.
    } else {
        // TODO: HomeScreenScaffold could take uiContent instead of splitting it out here - that
        // wouldn't be unreasonable, *it* would split stuff out, but it would save boilerplate here. We
        // could almost inline HomeScreenScaffold given how trivial the above code now is, and perhaps
        // we should.
        HomeScreenScaffold(
            navController,
            vm,
            loading,
            uiContent.dataSetIdState,
            uiContent.dataSet,
            uiContent.dataSetList,
            onSelectedDataSetIdChange = {
                vm.previousPrice.value = null
                vm.setCurrentDataSetId(it)
            },
            uiContent.item,
            uiContent.itemList,
            onSelectedItemIdChange = {
                vm.previousPrice.value = null
                vm.setCurrentItemId(it)
            },
            uiContent.sourceIdState,
            uiContent.source,
            uiContent.sourceList,
            onSelectedSourceIdChange = {
                vm.previousPrice.value = null
                vm.setCurrentSourceId(it)
            },
            uiContent.priceAnalysis,
            onEditPriceClick = { onEditPriceClick(uiContent) },
            onItemSearchClick = { onItemSearchClick(uiContent) },
            onViewHistoryClick = { onViewHistoryClick(uiContent) },
            onEditDataSetsClick = { onEditDataSetsClick(uiContent) },
            onEditItemsClick = { onEditItemsClick(uiContent) },
            onEditSourcesClick = { onEditSourcesClick(uiContent) },
            onSettingsClick = onSettingsClick,
        )
    }
}

// ENHANCE: Should this have a (fairly rapid) fade in and/or fade out? I am not sure. It's not a
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
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

        }
    }
}

// TODO: Grok magic
data class TimedData<T>(val data: T, val triggerTime: Long)

@Composable
fun HomeScreenNavigationDrawer(
    drawerState: DrawerState,
    dataSet: DataSet?,
    dataSetListSorted: List<DataSet>,
    onSelectedDataSetIdChange: (Long) -> Unit,
    content: @Composable () -> Unit
) {
    // ENHANCE: Navigation drawer is being deprecated in favour of expanded navigation rail in
    // Material 3 Expressive from May 2025. However, it appears to be a rotten fit for my
    // requirements here - it wants (in its non-expanded form) to be permanently on screen, and I
    // don't have the space, and it seems to be intended for "a few" designer-selected things, not
    // maybe 5-10+ user-defined categories. It also seems to want to live at the bottom of the
    // screen on a portrait smartphone layout. So I am going to stick with the navigation drawer for
    // now.

    val coroutineScope = rememberCoroutineScope()

    // ENHANCE: The navigation drawer appears to flicker in very briefly on the first composition
    // when the app is opened "cold". ChatGPT and Grok both tell me this is a known issue and offer
    // workarounds which don't work at all. I will just live with it for now. (Not composing
    // ModalNavigationDrawer until the first time the user clicks on the hamburger menu sort of
    // works, but the first appearance of the drawer is then ugly/badly animated somehow, so it's
    // probably worse than the problem it's trying to fix.)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // We cap the drawer width at 2/3 of the screen width because although it's not MD3
            // standard, I really don't like the default behaviour of it taking the full screen
            // width on a portrait smartphone. If nothing else, that makes how to dismiss it feel
            // less discoverable.
            ModalDrawerSheet(
                modifier = Modifier
                    .wrapContentWidth()
                    .widthIn(
                        max = min(
                            LocalConfiguration.current.screenWidthDp.dp * 2f / 3f,
                            maxNavigationDrawerWidth
                        )
                    )
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .height(oneLineListItemHeight)
                            .padding(start = listItemHorizontalPadding),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Collections",
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                    LazyColumn {
                        items(dataSetListSorted) { item ->
                            val selected = dataSet?.id == item.id
                            NavigationDrawerItem(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .height(oneLineListItemHeight),
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
        content()
    }
}

@Composable
fun HomeScreenActualScaffold( // TODO: RENAME
    navController: NavHostController,
    drawerState: DrawerState,
    dataSet: DataSet?,
    onEditDataSetsClick: () -> Unit,
    onEditItemsClick: () -> Unit,
    onEditSourcesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    asyncOperationStatus: AsyncOperationStatus,
    content: @Composable (innerPadding: PaddingValues) -> Unit

) {
    val coroutineScope = rememberCoroutineScope()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red /* TODO DEBUG HACK */),
        topBar = {
            TopAppBar(
                // We will almost always always have a DataSet to show the name of but we might as
                // well show the app name if we don't.
                title = { Text(dataSet?.name ?: "$appName") },
                navigationIcon = {
                    IconButton(
                        enabled = asyncOperationStatus.isNotBusy(),
                        onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open drawer"
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = asyncOperationStatus.isNotBusy(),
                        onClick = { menuExpanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu"
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
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
                            onSettingsClick()
                        })
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface /* TODO? */)
            )
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// TODO: Function might be misnamed if we introduce navigation drawer, but I probably want to
// refactor a lot of the composables anyway in order to get away from gigantic massively independent
// functions.
fun HomeScreenScaffold(
    navController: NavHostController,
    vm: HomeViewModel,
    loading: Boolean,
    dataSetIdState: LoadState<Long>,
    dataSet: DataSet?,
    dataSetList: List<DataSet>,
    onSelectedDataSetIdChange: (Long) -> Unit,
    item: Item?,
    itemList: List<Item>,
    onSelectedItemIdChange: (Long) -> Unit,
    sourceIdState: LoadState<Long>,
    source: Source?,
    sourceList: List<Source>,
    onSelectedSourceIdChange: (Long?) -> Unit,
    priceAnalysis: PriceAnalysis,
    onEditPriceClick: () -> Unit,
    onItemSearchClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onEditDataSetsClick: () -> Unit,
    onEditItemsClick: () -> Unit,
    onEditSourcesClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    // TODO: We need to disable all forms of interaction (navdrawer, dropdowns, menu, etc) while
    // this is "busy"
    val asyncOperationStatus by vm.asyncOperationStatus.collectAsStateWithLifecycle()

    // Unlike GeneralEditScreen(), we don't try to trap "back" and show a busy snackbar. We probably
    // could but:
    // - The data being saved here is "just" a confirm/undo confirm, it's not quite so critical or
    //   "user has put effort into this data entry" as in GeneralEditScreen.
    // - "Back" from the home screen would leave the app. It's not so clear we should even try to
    //   stop the user doing that.
    // - The user can use the home or overview buttons/gestures to do leave the app, and we probably
    //   can't and almost certainly shouldn't trap those if we are saving. (They can also do this
    //   during GeneralEditScreen too. It's just that there "back" has an in-app meaning and is
    //   a particularly expected case where we can reasonably interfere.)
    // - A slow save is extremely unlikely anyway.

    val dataSetListSorted = dataSetList.rememberSortedByLocale { it.name }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    //val drawerRequested by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry == null) {
            // This screen has been navigated away from.
            vm.previousPrice.value = null
        }
    }

    HomeScreenNavigationDrawer(drawerState, dataSet, dataSetListSorted, onSelectedDataSetIdChange) {

        HomeScreenActualScaffold(navController, drawerState, dataSet, onEditDataSetsClick, onEditItemsClick, onEditSourcesClick, onSettingsClick, asyncOperationStatus)
 { innerPadding ->
                HomeScreenContent(
                    vm,
                    dataSetIdState,
                    dataSet,
                    dataSetList,
                    item,
                    itemList,
                    onSelectedItemIdChange,
                    sourceIdState,
                    source,
                    sourceList,
                    onSelectedSourceIdChange,
                    priceAnalysis,
                    onEditPriceClick,
                    onItemSearchClick,
                    onViewHistoryClick,
                    asyncOperationStatus,
                    innerPadding
                )
        }
    }

    HomeScreenStateManager(vm, loading, asyncOperationStatus)
}

@Composable
fun HomeScreenStateManager(
    vm: HomeViewModel,
    loading: Boolean,
    asyncOperationStatus: AsyncOperationStatus
) {
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // TODO: I have thrown in a buffer() here voodoo-style based on an actual observed problem
        // in other cases. Not sure if it's really necessary or best practice here.
        vm.asyncOperationStatus.events.buffer().collect { event ->
            when (event) {
                AsyncOperationStatus.Busy -> {
                    // We expect the operation to complete quickly so we don't want the visual distraction
                    // of a progress indicator appearing straight away. Let the progress indicator kick
                    // in after a short delay if we're still here waiting.
                    delay(spinnerDelayMillis)
                    // The state might not be busy any more, so check first before updating to avoid a race condition.
                    if (vm.asyncOperationStatus.state.value == AsyncOperationStatus.Busy) {
                        vm.asyncOperationStatus.update(AsyncOperationStatus.BusyForAWhile)
                    }
                }

                is AsyncOperationStatus.Success -> {
                    vm.asyncOperationStatus.update(AsyncOperationStatus.Idle)
                }

                is AsyncOperationStatus.Error -> { // TODO: We might want to destructure the parameter here so we can save/show the error
                    vm.asyncOperationStatus.update(AsyncOperationStatus.Idle)
                    showErrorDialog = true
                }

                else -> {}
            }
        }
    }

    // We use this scrim with spinner to handle the (unlikely) cases where:
    // - The initial data load takes a long time.
    // - Saving a confirm/undo confirm to the database takes a long time.
    //
    // The latter could be handled via showing a spinner on the confirm/undo confirm button itself
    // (and continuing to disable all controls while waiting for the save to complete, as we already
    // do), but for such an unlikely case it seems best to keep things simple.
    //
    // In an ideal world the scrim with spinner for loading would cover only the lower two cards and
    // leave the rest of the home screen functional; it would be legitimate to abandon a slow load
    // and choose to load something different. (It would not be legitimate to do this while waiting
    // for a save to complete.) I experimented with doing this and although I think I
    // could have made it work, it felt incredibly brittle and likely to go wrong depending on
    // Android version and things like edge-to-edge and the SDK implementing that differently on
    // different Android versions etc. Given how rarely we expect the spinner to appear at all (and
    // therefore also how little testing it would get), it seemed best to go with this relatively
    // simple full screen spinner. (It is just possible I had some buggy/sub-optimal setup of the
    // higher level composables which made this seem harder than it should have been, but I'm not
    // sure.)
    //
    // Note that we do not pass a delayMillis parameter here. The delay before the scrim appears is
    // implemented in the logic which sets the loading flag or BusyForAWhile state, so as soon as
    // either is true we want to show the scrim.
    ScrimWithSpinner(visible = loading || asyncOperationStatus == AsyncOperationStatus.BusyForAWhile)

    if (showErrorDialog) {
        SaveErrorAlertDialog(requestClose = { showErrorDialog = false })
    }
}

@Composable
fun HomeScreenContent(
    vm: HomeViewModel,
    dataSetIdState: LoadState<Long>,
    dataSet: DataSet?,
    dataSetList: List<DataSet>,
    item: Item?,
    itemList: List<Item>,
    onSelectedItemIdChange: (Long) -> Unit,
    sourceIdState: LoadState<Long>,
    source: Source?,
    sourceList: List<Source>,
    onSelectedSourceIdChange: (Long?) -> Unit,
    priceAnalysis: PriceAnalysis,
    onEditPriceClick: () -> Unit,
    onItemSearchClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    asyncOperationStatus: AsyncOperationStatus,
    innerPadding: PaddingValues,
) {
    var showDeletePriceConfirmDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            // .background(MaterialTheme.colorScheme.secondary) // TODO debug hack
            .background(MaterialTheme.colorScheme.background) // TODO?
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
            .padding(screenBorder)
    ) {
        Log.d("MyAppSU", "dataSet $dataSet dataSetList $dataSetList")
        if (dataSet == null) {
            // These are corner cases, caused by the current data set being deleted or all data
            // sets being deleted. It wouldn't technically hurt to show the normal screen content,
            // but it seems friendlier to explain what's going on.
            if (dataSetList.isEmpty()) {
                Text("There are no collections. Add one using the overflow menu at the top right.")
            } else {
                Text("No collection is selected. Use the hamburger menu at the top left to select one.")
            }
        } else {
            ItemSourceSelector(
                asyncOperationStatus = asyncOperationStatus,
                source = source,
                sourceList = sourceList,
                item = item,
                itemList = itemList,
                onSelectedItemIdChange = onSelectedItemIdChange,
                onSelectedSourceIdChange = onSelectedSourceIdChange,
                onItemSearchClick = onItemSearchClick,
            )

            Spacer(
                modifier = Modifier
                    .height(
                        16.dp
                    )
                    .fillMaxWidth()
                //.background(color = Color.Red) // TODO DEBUG HACK
            )

            if (dataSet != null) {
                // TODO: While it has told me so much crap I don't trust it, ChatGPT suggests:
                // var lastFoo by remember { mutableStateOf<Foo?>(null) }
                //if (foo != null) lastFoo = foo
                // and then using  lastFoo?.let { safeFoo ->
                // to compose the contents of the AnimatedVisibility. This (might) give us
                // consistent appearance as we animate out without requiring actual ability to
                // handle null source/item  inside the content, and would (if this works) actually
                // make things mildly *less* janky as the content would be *the same* not some
                // null-based approximation. But there may well be subtleties.
                AnimatedVisibility(
                    visible = item != null && source != null,
                    // enter = TODO?
                    // exit = TODO?
                ) {
                    Column {
                        Log.d("MyApp", "HSS dataSet $dataSet")
                        Log.d("MyApp", "HSS item $item")
                        ItemSourceInfoLive(
                            vm = vm,
                            asyncOperationStatus = asyncOperationStatus,
                            dataSet = dataSet,
                            item = item,
                            source = source,
                            sourceList = sourceList,
                            augmentedPrice = priceAnalysis.augmentedPriceList.singleOrNull { it.basePrice.sourceId == source?.id },
                            onEditPriceClick = onEditPriceClick,
                            onViewHistoryClick = onViewHistoryClick,
                            onDeletePriceClick = { showDeletePriceConfirmDialog = true },
                        )

                        Spacer(
                            modifier = Modifier.height(
                                16.dp
                            )
                            //.background(color = Color.Red) // TODO DEBUG HACK
                        )
                    }
                }

                // TODO: Just possibly we should use AnimatedVisibility here. However, it's not
                // that big a deal (but maybe do look into it) as the only way to have item be
                // null is if there *are* no items - unlike source, you can't deliberately set
                // it to null. So this is not a particularly common case and the animation would
                // only be firing if we were navigating back from an edit item screen where
                // we've removed the last item or something like that - it's not a "something
                // changed within the screen itself" animation like having source go between
                // null and non-null is.
                if (item != null) {
                    // Clicking on one of the items on this card selects its source, just as if it had
                    // been selected via the source dropdown. This is technically redundant but I found
                    // myself wanting to do it all the time to quickly see the details of a price, so
                    // I've implemented it. (The dropdown is still needed, as it's the only way to
                    // select sources which don't appear on the price comparison card.)
                    PriceComparisonCard(
                        dataSet,
                        source,
                        priceAnalysis,
                        onClick = { onSelectedSourceIdChange(it) },
                        asyncOperationStatus)
                }

            }
        }
    }

    if (showDeletePriceConfirmDialog) {
        val augmentedPrice = priceAnalysis.augmentedPriceList.single { it.basePrice.sourceId == source?.id }
        DeletePriceConfirmDialog(vm, augmentedPrice, requestClose = { showDeletePriceConfirmDialog = false })
    }
}

@Composable
fun DeletePriceConfirmDialog(
    vm: HomeViewModel,
    augmentedPrice: AugmentedPrice,
    requestClose: () -> Unit // TODO: Rename onDismissRequest? see SaveErrorAlertDialog()
) {
    AlertDialog(
        icon = null,
        title = { Text("Delete price") },
        // TODO: When/if it works, we could possibly mention being able to recover it via the
        // history. But it may be best to keep the message simple anyway.
        text = { Text("Are you sure you want to delete this price?") },
        onDismissRequest = requestClose,
        dismissButton = {
            TextButton(onClick = requestClose) { Text("Cancel") }
        },
        confirmButton = {
            TextButton(onClick = {
                requestClose()
                vm.deletePrice(augmentedPrice.basePrice)
            }) { Text("Delete") }
        }
    )
}

// ENHANCE: We use primary/secondary/tertiary for good/OK/bad here. This isn't necessarily ideal
// but it does avoid problem where a fixed green/grey-or-amber/red set of colours clashes with
// a Material You-generated colour scheme.

@Composable
fun GoodPriceIcon() {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Good value",
        tint = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun OkPriceIcon() {
    Icon(
        // ENHANCE: Maybe we could have a better icon for this. There is a vague hint of the UK "no
        // entry" road sign about this one which doesn't quite fit with "OK" for me.
        painter = painterResource(R.drawable.baseline_remove_circle_24),
        contentDescription = "OK value",
        tint = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
fun BadPriceIcon() {
    Icon(
        painter = painterResource(R.drawable.baseline_cancel_24),
        contentDescription = "Bad value",
        tint = MaterialTheme.colorScheme.tertiary,
    )
}

@Composable
fun StalePriceIcon() {
    Icon(
        // Idea with this icon is "the 'fresh' period is over, we started a timer now it's stale".
        // ENHANCE: Just possibly create my own hourglass_middle icon and use that here instead? We
        // probably would keep to no icon for fresh rather than using hourglass top, but the
        // "tri-state metaphor" would maybe be a bit more obvious to users.
        painter = painterResource(R.drawable.baseline_hourglass_top_24),
        contentDescription = "Stale price",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun AncientPriceIcon() {
    Icon(
        // Idea with this icon is that the "stale timer" we started has now run out.
        painter = painterResource(R.drawable.baseline_hourglass_bottom_24),
        contentDescription = "Ancient price",
        tint = MaterialTheme.colorScheme.error,
    )
}

@Composable
fun PriceComparisonCard(
    dataSet: DataSet,
    source: Source?,
    priceAnalysis: PriceAnalysis,
    onClick: (Long) -> Unit,
    asyncOperationStatus: AsyncOperationStatus,
) {
    // TODO: The "£/100g" (or whatever, when it's dynamically constructed) should have a
    // contextDescription for screen readers which is "Price per 100g", so it gets read out
    // properly. I think "Price per" is OK (better than "Pounds per", actually), because the rows
    // themselves contain the currency symbol.
    // ENHANCE: We could make denominator user-selectable in this list header. If so it should
    // probably offer all the user's selected units of the right type, as the unit price dropdown on
    // ItemSourceInfo does.
    val locale = LocalConfiguration.current.locales[0]
    val currencyFormat = remember(dataSet, locale) {
        dataSet.createCurrencyFormat(locale)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // The extra padding at the bottom compared to the top is to try to visually keep the sharp
        // corners of the table away from the rounded edges of the card.
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 16.dp
            )
        ) {
            CardTitle(
                title = "Price comparison",
                subtitle = "Adjusted for loyalty discounts and old prices"
            )

            if (priceAnalysis.augmentedPriceList.isEmpty()) {
                Text("There are no prices recorded for this product at any store yet.")
            } else {
                // It may be technically incorrect to show the currency symbol both in the header
                // ("£/100g") and on the individual unit prices, but I think that for practical
                // purposes this is the least confusing way to show it. An "incomplete" header
                // ("/100g") feels unclear, as does having prices which aren't marked with a
                // currency symbol.

                val bestValueAugmentedPrice = priceAnalysis.augmentedPriceList.first()
                val headerUnitPriceDenominator = remember(bestValueAugmentedPrice) {
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
                    val candidateDenominators = getMeasurementUnitsOfSameQuantityTypeAndUnitFamily(
                        dataSet,
                        bestValueAugmentedPrice.basePrice.quantity.unit,
                        includeDisplayOnly = true
                    )
                    bestValueAugmentedPrice.unitPrice.withFriendlyDenominator(
                        preferredUnit = bestValueAugmentedPrice.basePrice.quantity.unit,
                        currencyDecimalPlaces = currencyFormat.decimalPlaces,
                        candidateDenominators = candidateDenominators
                    ).denominator
                }
                // We use "prefix or suffix" in the header because although the prefix or suffix
                // nature of a currency symbol in a locale matters in some other places, here it is
                // appearing in isolation *without* a price next to it.
                val header = listOf(
                    "Store",
                    "${currencyFormat.prefix ?: currencyFormat.suffix ?: ""}${headerUnitPriceDenominator.perSymbol}${headerUnitPriceDenominator.symbol}",
                    ""
                )

                val highlightRow =
                    priceAnalysis.augmentedPriceList.indexOfFirst { it.sourceName == source?.name }
                        .takeIf { it != -1 }

                val columns = remember(dataSet, locale, headerUnitPriceDenominator) {
                    listOf<@Composable (AugmentedPrice) -> Unit>(
                        { augmentedPrice -> Text(augmentedPrice.sourceName) },
                        { augmentedPrice ->
                            Log.d("MyAppAA", "inside columns, unitPrice ${augmentedPrice.unitPrice}, headerUnitPriceDenominator $headerUnitPriceDenominator")
                            Text(
                                formatPrice(
                                    augmentedPrice.unitPrice.withDenominator(headerUnitPriceDenominator).numerator,
                                    dataSet,
                                    locale
                                )
                            )
                        },
                        // ENHANCE: We could add blank icons here so we have a column of "judgement"
                        // icons and a column of "age class" icons. Not sure if that would look
                        // better or not.
                        { augmentedPrice ->
                            Row {
                                if (augmentedPrice.ageClass != AgeClass.ANCIENT) {
                                    when (augmentedPrice.priceJudgement) {
                                        PriceJudgement.NONE -> {}
                                        PriceJudgement.GOOD -> GoodPriceIcon()
                                        PriceJudgement.OK -> OkPriceIcon()
                                        PriceJudgement.BAD -> BadPriceIcon()
                                    }
                                }

                                if (augmentedPrice.ageClass == AgeClass.STALE) {
                                    StalePriceIcon()
                                } else if (augmentedPrice.ageClass == AgeClass.ANCIENT) {
                                    AncientPriceIcon()
                                }
                            }
                        },
                    )
                }
                DataTable(
                    header = header,
                    items = priceAnalysis.augmentedPriceList,
                    columns = columns,
                    highlightRow = highlightRow,
                    // ENHANCE: It might be better to calculate the space needed for the longest unit
                    // price and the longest number of icons, then assign anything left over to the
                    // source name. In practice these simple fixed weights seem to be working quite
                    // well for now.
                    columnWeights = listOf(1.7f, 1f, 0.8f),
                    columnAlignments = listOf(
                        CellAlignment.Start,
                        CellAlignment.End,
                        CellAlignment.Start
                    ),
                    onClick = if (asyncOperationStatus.isNotBusy()) { { augmentedPrice -> onClick(augmentedPrice.basePrice.sourceId) } } else null,
                )
            }
        }
    }
}

enum class CellAlignment { Start, Center, End }

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

fun areDifferentUnitFamilies(lhs: MeasurementUnit, rhs: MeasurementUnit) =
    lhs.unitFamilies.intersect(rhs.unitFamilies).isEmpty()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPriceScreen(
    vm: EditPriceViewModel,
    navController: NavHostController,
    requestClose: (Long?) -> Unit
) {
    val uiContent = vm.uiContent

// TODO: Some of this remember stuff should maybe move into the ViewModel

    val saveStatus by vm.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    fun onPackSizeOrPriceChange() {
        // On the first change to the pack size or price, we set the "to confirm" switch to true, on
        // the grounds that if the user is changing these values, they must be getting them from
        // somewhere and the assumption is that they have the actual current price/pack in front of
        // them. (We don't do this if they edit the notes; it's conceivable they are for example
        // trying the product at home and making a note that a certain brand isn't very nice and not
        // to consider it as acceptable in future.) We only do this on the first change so we don't
        // fight with the user if they toggle this back off afterwards.
        // ENHANCE: We might want to gate this logic behind a Settings option, i.e. have an option to
        // let the confirm always stay off unless the user explicitly turns it on. That said, in my
        // own personal use, this logic seems to work well.
        if (!vm.firstPackSizeOrPriceChangeOccurred) {
            vm.setUIContentEditablePrice(uiContent.editablePrice.value.copy(toConfirm = true))
            vm.firstPackSizeOrPriceChangeOccurred = true
        }
    }

    GeneralEditScreen(
        vm = vm.generalEditScreenViewModel,
        navController = navController,
        title = topAppBarTitle(vm.uiContent.item.name, vm.uiContent.source.name),
        isDirty = {
            uiContent.editablePrice.value.copy(toConfirm = false) !=
                    uiContent.originalPrice.copy(toConfirm = false)
        },
        validateForSave = { vm.validateForSave() },
        performSave = { vm.performSave() },
        onIdle = {},
        requestClose = requestClose,
    ) {
        // We put the price above the pack size. This matches the order we show things (at least in
        // English) on the read-only home screen. It also ties in with the price usually being the
        // primary item on a shelf label. ENHANCE: If anyone expresses an interest, we could make
        // the ordering of these translation-configurable. (Don't forget to alter the order we
        // check for validation failures to match, as well as re-ordering the actual composables
        // here.)

        EditPriceScreenPrice(vm, ::onPackSizeOrPriceChange)

        //Spacer(modifier = Modifier.height(500.dp))
        Spacer(modifier = Modifier.height(16.dp))

        EditPriceScreenPackSize(vm, ::onPackSizeOrPriceChange)

        // We don't show the switch if this is the first price for an item and source; the price is
        // confirmed, otherwise why are we entering it? Note that this is not the same as id being
        // 0, because if we deleted the price and are re-creating it from the history, we have no
        // ID but toConfirm will be false so we can preserve the old confirmation date by default.
        if (!uiContent.originalPrice.toConfirm) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
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
            myCheck(uiContent.editablePrice.value.toConfirm) {
                "Expected toConfirm to be true as this is the first price, but it's false"
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Can/should I do something to scroll the screen when focus enters this and the caret
        // is half-hidden?
        var notes by rememberSyncedTextFieldValue(uiContent.editablePrice.value.notes)
        FilteredTextField(
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Notes") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                vm.setUIContentEditablePrice(uiContent.editablePrice.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
        )
    }

    // TODO: It is probably hard, but *if* two fields have validation errors and a field with a
    // validation error is currently focused, it would be nice to use the already-focused one as the
    // scroll-and-highlight target, not "whichever one our internal logic considers first".
}

@Composable
fun EditPriceScreenPrice(
    vm: EditPriceViewModel,
    onChange: () -> Unit
) {
    val uiContent = vm.uiContent

    val saveStatus by vm.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    var packPrice by rememberSyncedTextFieldValue(uiContent.editablePrice.value.price)
    val currencyFormat = vm.currencyFormat

    BaseValidatedTextField(
        value = packPrice.text,
        validationRules = currencyFormat.validationRules,
        // No validationRulesKey is needed as the validation rules depend only on our fixed
        // DataSet and frozen locale.
        allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
        validationFlow = vm.saveValidationEvents,
        validationFlowFieldId = EditPriceViewModel.EditableField.PRICE,
        errorHighlightOffset = 4.dp,
    ) { validationResult, interactionSource, validationInputHandle ->
        NumericTextField(
            modifier = Modifier
                .fillMaxWidth()
                .validationInputHandleFocusRequester(validationInputHandle),
            label = { Text("Pack price") },
            value = packPrice,
            prefix = textOrNull(currencyFormat.prefix),
            suffix = textOrNull(currencyFormat.suffix),
            textStyle = if (currencyFormat.prefix == null && currencyFormat.suffix != null) LocalTextStyle.current.copy(
                textAlign = TextAlign.End
            ) else LocalTextStyle.current,
            onValueChange = {
                packPrice = it
                if (uiContent.editablePrice.value.price != it.text) {
                    vm.setUIContentEditablePrice(uiContent.editablePrice.value.copy(price = it.text))
                    onChange()
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
}

@Composable
fun EditPriceScreenPackSize(
    vm: EditPriceViewModel,
    onChange: () -> Unit
) {
    val uiContent = vm.uiContent

    val saveStatus by vm.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val units: List<MeasurementUnit> =
        remember(uiContent.dataSet, uiContent.item.defaultUnit.quantityType) {
            getRelevantMeasurementUnits(
                uiContent.dataSet,
                uiContent.item.defaultUnit.quantityType,
                includeDisplayOnly = false
            )
        }
    var packCountNumber by rememberSyncedTextFieldValue(uiContent.editablePrice.value.count)
    var packSizeNumber by rememberSyncedTextFieldValue(
        uiContent.editablePrice.value.measureValue
    )

    // TODO: I wonder if this screen is actually a bit vertically (and even horizontally?) squashed
    // together, now I see that I "need" offset = 4.dp here instead of the current default 6.dp. It
    // might be I should increase the vertical spacing of the components on this screen and then
    // make this 6.dp. (I don't know, but I may have already increased the vertical spacing. So try
    // 6.dp here again - and check what other bits of the code use for their error offsets - before
    // automatically increasing the spacing.)

    // TODO: ALL THE WEIGHTS HERE INCLUDING THE LEVELS AT WHICH THEY ARE APPLIED ARE UP IN THE AIR AND SHOULD BE CHECKED

    Row {
        if (vm.showPackCount) {
            BaseValidatedTextField(
                value = packCountNumber.text,
                validationRules = vm.packCountValidationRules,
                // TODO DON'T THINK WE NEED THIS BUT CHECK, WIP RIGHT NOW validationRulesKey = uiContent.editablePrice.value.measurementUnit.id,
                allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
                validationFlow = vm.saveValidationEvents,
                validationFlowFieldId = EditPriceViewModel.EditableField.PACK_COUNT,
                errorHighlightOffset = 4.dp, // TODO!?
                modifier = Modifier.weight(1f)
            ) { validationResult, interactionSource, validationInputHandle ->
                Row { // TODO EXPERIMENTAL
                    NumericTextField(
                        label = { Text("Count") },
                        value = packCountNumber,
                        onValueChange = {
                            packCountNumber = it
                            if (uiContent.editablePrice.value.count != it.text) {
                                vm.setUIContentEditablePrice(
                                    uiContent.editablePrice.value.copy(
                                        count = it.text
                                    )
                                )
                                onChange()
                            }
                        },
                        enabled = saveStatus.isNotBusy(),
                        isError = validationResult != null,
                        supportingText = if (validationResult == null) null else { { SupportingText(validationResult, true) } }, // TODO EXPERIMENTAL
                        modifier = Modifier
                            .validationInputHandleFocusRequester(validationInputHandle),
                        interactionSource = interactionSource
                    )
                }
            }
            // We want the multiplication sign to be roughly centred vertically (ideally it would
            // share a baseline with the user-entered text, but that is impossible to do, at least
            // without loads of additional code) but we can't just vertically centre it, because the
            // Row's bounding box will grow when supportingText appears and we don't want the "x" to
            // move then. This is our attempt to try to get the
            // TODO: Test this with font scaling
            Text(multiplicationSign, modifier = Modifier.padding(horizontal = 4.dp, vertical = 16.dp * LocalDensity.current.fontScale))
        }
        BaseValidatedTextField(
            value = packSizeNumber.text,
            validationRules = vm.packSizeValidationRules,
            validationRulesKey = uiContent.editablePrice.value.measurementUnit.id,
            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditPriceViewModel.EditableField.PACK_SIZE,
            errorHighlightOffset = 4.dp,
            modifier = Modifier.weight(1f)
        ) { validationResult, interactionSource, validationInputHandle ->
                NumericTextField(
                    // TODO: Now we have multipack support, "pack size" is arguably confusing.
                    // There's also a practical consideration that when it grows larger because the
                    // field is empty, it really doesn't fit horizontally on my small phone - so a
                    // shorter label would be good there too.
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
                            onChange()
                        }
                    },
                    enabled = saveStatus.isNotBusy(),
                    isError = validationResult != null,
                    supportingText = if (validationResult == null) null else { { SupportingText(validationResult, true) } },
                    modifier = Modifier
                        // TODO DELETE? .weight(1f)
                        .fillMaxSize()
                        .validationInputHandleFocusRequester(validationInputHandle),
                    interactionSource = interactionSource
                )
        }


        if (uiContent.item.defaultUnit.quantityType != QuantityType.ITEM) {
            Spacer(modifier = Modifier.width(8.dp))

            // fontSizeDp is used here so that the minimum width we request scales
            // correctly (TODO: we hope - not tested) when the user changes the system font
            // size.
            val fontSize = MaterialTheme.typography.bodyLarge.fontSize
            val fontSizeDp = with(LocalDensity.current) { fontSize.toDp() }

            MyExposedDropdownMenuBox(
                enabled = saveStatus.isNotBusy(),
                selectedId = uiContent.editablePrice.value.measurementUnit.id,
                onItemSelected = {
                    val measurementUnit = MeasurementUnit.fromId(it)
                    myCheck(measurementUnit != null) {
                        "Expected non-null measurementUnit to be selected; got $it"
                    }
                    if (uiContent.editablePrice.value.measurementUnit != measurementUnit!!) {
                        vm.setUIContentEditablePrice(
                            uiContent.editablePrice.value.copy(
                                measurementUnit = measurementUnit
                            )
                        )
                        onChange()
                    }
                },
                label = { Text("Unit") },
                items = units,
                // Although this could be a problem (particularly with i18n), we give the dropdown
                // "about enough horizontal space" by calculating a hand-tuned multiplier of
                // fontSizeDp. (I cannot get it to size itself to its non-dropdown width and use
                // wrapContentWidth(), which would otherwise be ideal.) We could just give it equal
                // weight with the pack count and pack size fields and let the system size them all.
                // However, since pack count and pack size need to be able to show supportingText
                // underneath them for errors, we want to give them as much space as possible.
                modifier = Modifier.width(6 * fontSizeDp), // wrapContentWidth(), // weight(0.75f), // TODO: *May* need to make this 0.5 if we don't have a count, maybe we can find something that works in both cases
                getId = { it.id },
                getCollapsedItemText = { it.symbol },
                getItemText = { "${it.fullName} (${it.symbol})" },
                getDividerBetween = { previousItem, item ->
                    areDifferentUnitFamilies(
                        previousItem,
                        item
                    )
                },
            )
        }

    }
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
    val asyncOperationStatus = SyncedStateEvent<AsyncOperationStatus>(AsyncOperationStatus.Idle)
    var saveAttempted: MutableState<Boolean> = mutableStateOf(false)
}

fun runGeneralEditScreenOperation(
    vm: GeneralEditScreenViewModel,
    coroutineScope: CoroutineScope,
    isSafeToPerform: suspend () -> Boolean,
    perform: suspend () -> Long,
) {
    coroutineScope.launch {
        if (isSafeToPerform()) {
            vm.asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                Log.d("MyAppRGE", "runGeneralEditScreenOperation about to call perform")
                val id = perform()
                Log.d("MyAppQZ", "perform() returned id $id")
                // delay(5000) // TODO HACK - DONE AFTER PERFORM SO IT GETS A CHANCE TO SET SAVING/DELETING FLAG TO TRUE
                vm.asyncOperationStatus.update(AsyncOperationStatus.Success(id))
            } catch (e: Exception) {
                Log.d("MyAppRGE", "runGeneralEditScreenOperation caught exception")
                vm.asyncOperationStatus.update(AsyncOperationStatus.Error("runGeneralEditScreenOperation failed: ${e.toString()}"))
            }
        }
    }
}

// TODO: This is a very long function, can we split it up?
@Composable
fun GeneralEditScreen(
    vm: GeneralEditScreenViewModel,
    navController: NavHostController,
    title: @Composable () -> Unit,
    isDirty: () -> Boolean,
    validateForSave: suspend () -> Boolean,
    performSave: suspend () -> Long,
    onIdle: () -> Unit,
    requestClose: (Long?) -> Unit,
    content: @Composable () -> Unit
) {
    val saveStatus by vm.asyncOperationStatus.collectAsStateWithLifecycle()
    Log.d("MyAppRGE", "GeneralEditScreen saveStatus=$saveStatus")

    val isNotBusy = saveStatus.isNotBusy()
    var showConfirmDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialog by rememberSaveable { mutableStateOf(false) }
    var showBusySnackbar by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var saving by rememberSaveable { mutableStateOf(false) }

    // TODO: We may need to make this available to the content() so it can use it for scrolling to
    // highlight errors, or it may be that we don't need it here at all and it can be entirely in
    // the content()
    val scrollState = rememberScrollState()

    // We can't use dropUnlessResumed here as we have a parameter, so pseudo-inline it.
    val localLifecycleOwner = LocalLifecycleOwner.current
    fun requestCloseDebounced(id: Long?) {
        if (localLifecycleOwner.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
            requestClose(id)
        }
    }

    fun requestDismiss() {
        if (isDirty()) {
            showConfirmDiscardDialog = true
        } else {
            requestCloseDebounced(null)
        }
    }

    BackHandler {
        if (isNotBusy) {
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
        vm.asyncOperationStatus.events.buffer().collect { event ->
            when (event) {
                AsyncOperationStatus.Busy -> {
                    // We expect the operation to complete quickly so we don't want the visual distraction
                    // of a progress indicator appearing straight away. Let the progress indicator kick
                    // in after a short delay if we're still here waiting.
                    delay(spinnerDelayMillis)
                    // The state might not be busy any more, so check first before updating to avoid a race condition.
                    if (vm.asyncOperationStatus.state.value == AsyncOperationStatus.Busy) {
                        vm.asyncOperationStatus.update(AsyncOperationStatus.BusyForAWhile)
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
        vm.asyncOperationStatus.events.buffer().collect { event ->
            when (event) {
                AsyncOperationStatus.Idle -> {
                    Log.d("MyAppRGE", "collected idle")
                    saving = false
                    Log.d("MyAppRGE", "set saving to false")
                    onIdle()
                    Log.d("MyAppRGE", "called onIdle")
                }

                is AsyncOperationStatus.Success -> {
                    Log.d("MyAppRGE", "collected success")
                    requestCloseDebounced(event.id)
                }

                is AsyncOperationStatus.Error -> {
                    Log.d("MyAppRGE", "collected error")
                    vm.asyncOperationStatus.update(AsyncOperationStatus.Idle)
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
                    IconButton(enabled = isNotBusy, onClick = { requestDismiss() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                title = title,
                actions = {
                    // TODO: Just possibly instead of always calling onSave, onClick should call
                    // isDirty first and just dismiss without saving if it returns false - but that
                    // might be confusing and it's maybe optimising a corner case
                    TextButton(enabled = isNotBusy, onClick = {
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
                                //delay(5000) // TODO HACK
                                performSave()
                            }
                        )
                    }) {
                        // We do get rid of the spinner when we reach "success"; this might cause a
                        // small but legitimate visual glitch as the disabled "Save" button
                        // re-enables, but it feels confusing to close while showing the spinner,
                        // since it might suggest to the user we *haven't* finished but are for some
                        // reason closing anyway.
                        if (saving && saveStatus == AsyncOperationStatus.BusyForAWhile) {
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
                .background(/* Color.Cyan TODO TEMP FOR DEBUG, SHOULD BE */ MaterialTheme.colorScheme.surface) // because this is a full-screen dialog
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = fullScreenDialogHorizontalBorder)
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
                TextButton(onClick = { requestCloseDebounced(null) }) {
                    Text(
                        "Discard"
                    )
                }
            },
        )
    }


    if (showErrorDialog) {
        SaveErrorAlertDialog(requestClose = { showErrorDialog = false })
    }

    LaunchedEffect(showBusySnackbar) {
        if (showBusySnackbar) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Busy, please wait...")
                showBusySnackbar = false
            }
        }
    }
}

@Composable
fun SaveErrorAlertDialog(requestClose: () -> Unit) { // TODO: rename argument onDismissRequest?
    // We use an AlertDialog not a snackbar here. This is a local database save which is
    // failing so it is very unlikely to be transient. We also don't want the user
    // missing the snackbar, thinking the app is buggy ("I already saved, why didn't the
    // dialog close?") and then tapping the close icon without realising their changes
    // have not been saved. (If transient failure was a possibility - e.g. we needed to
    // perform network activity - there might be value in showing a snackbar, maybe with
    // a fallback to an AlertDialog if things keep failing.)
    // TODO: Do we want to "re-use" this dialog for e.g. delete errors too? If so, how will the
    // change of wording be addressed? We may want to rename showErrorDialog to something more
    // suggestive depending on what kinds of error this code handles.
    AlertDialog(
        title = { Text("Unable to save changes") },
        text = { Text("An error occurred while saving the changes.") },
        onDismissRequest = { requestClose() },
        confirmButton = {
            TextButton(onClick = { requestClose() }) { Text("OK") }
        }
    )
}


@Composable
fun GeneralEditAndDeleteScreen(
    vm: GeneralEditScreenViewModel,
    navController: NavHostController,
    title: @Composable () -> Unit,
    isDirty: () -> Boolean,
    validateForSave: suspend () -> Boolean,
    performSave: suspend () -> Long,
    onIdle: () -> Unit,
    requestClose: (Long?) -> Unit,
    deleteConfirmationDetails: Triple<Boolean, @Composable () -> Unit, @Composable () -> Unit>?,
    requestDelete: suspend () -> Unit,
    requestDeleteCancel: () -> Unit,
    content: @Composable (showDeleteSpinner: Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var deleting by rememberSaveable { mutableStateOf(false) }
    val saveStatus by vm.asyncOperationStatus.collectAsStateWithLifecycle()

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
            deleting && saveStatus == AsyncOperationStatus.BusyForAWhile
        )
    }

    if (deleteConfirmationDetails != null) {
        val isSimpleDelete = deleteConfirmationDetails.first
        val dialogTitle = deleteConfirmationDetails.second
        val dialogText = deleteConfirmationDetails.third

        AlertDialog(
            icon = if (isSimpleDelete) null else { { WarningIcon(contentDescription = "Warning") } },
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
                            //delay(5000) // TODO HACK
                            //throw IllegalStateException("TODO")
                            requestDelete()
                            0 // TODO: feels like a hack - we have to return an ID, but for deletion that makes little sense - should we use null? do somethng else?
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
    requestClose: (newSelectedItemId: Long?) -> Unit
) {
    val uiContent = vm.uiContent

    val itemReferenceCount by vm.itemReferenceCountFlow.collectAsStateWithLifecycle()
    Log.d("MyApp", "itemReferenceCount $itemReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by vm.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = itemReferenceCount == 0L
    GeneralEditAndDeleteScreen(
        vm = vm.generalEditScreenViewModel,
        navController = navController,
        title = topAppBarTitle( if (vm.uiContent.editableItem.value.id == 0L) "Add product" else "Edit product", vm.uiContent.dataSet.name),
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
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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
            singleLine = true,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditItemViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Probably can/should factor out a lot of this radio button stuff which I have just
        // copied and pasted from EditSourceScreen for now.

        // TODO: Can I put these string versions inside QuantityType or won't that play well with i18n?
        val options = listOf(
            // TODO: Don't be over-eager to have supportingText here - if we don't need it for any of them items that is fine, and we can then avoid this maybe-nonstandardness in this case at least, and revert to the standard item height of 40.dp - "Item" alone may be a fine option, or "Item or group of items" or something like that would probably be a fine option with no supporting text - think carefully about wording but don't assume we need supportingText
            Triple(
                QuantityType.ITEM,
                "Item",
                "Per item or pack of items"
            ), // TODO: POOR WORDING FOR BOTH SHORT NAME AND SUPPORTING TEXT? THINK
            Triple(QuantityType.WEIGHT, "Weight", null),
            Triple(
                QuantityType.VOLUME,
                "Volume",
                null,
            ),
        )
        var selectedOption = uiContent.editableItem.value.quantityType
        // TODO: This radio group needs to be enabled iff saveStatus.isNotBusy()

        // ENHANCE: If itemReferenceCount != 0L and we disallow changing "sold by" as a result, just
        // maybe we should switch to displaying a disabled TextField or similar with a
        // supportingText instead of the radio buttons. I half suspect that might look ugly and be
        // confusingly different, but maybe it wouldn't.
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
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
                Text(
                    "Sold by",
                    style = MaterialTheme.typography.titleSmall /* bodySmall */,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                //Spacer(modifier = Modifier.height(8.dp))
                options.forEach { (id, name, supportingText) ->
                    val clickableModifier = if (itemReferenceCount != 0L) Modifier else Modifier.clickable {
                        vm.setUIContentEditableItem(
                            uiContent.editableItem.value.copy(
                                quantityType = id
                            )
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            //.background(Color.Blue)
                            .then(clickableModifier)
                            .padding(horizontal = 8.dp)
                            .height(48.dp) // 40.dp is MD3 spec but we want extra space for our supporting text while still having some spacing between items
                            .semantics {
                                role = Role.RadioButton
                            }, // for TalkBack / screen readers, since this is clickable not the RadioButton
                    ) {
                        RadioButton(
                            selected = (selectedOption == id),
                            enabled = itemReferenceCount == 0L,
                            onClick = null // the enclosing Row is clickable instead
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = name
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
                if (itemReferenceCount != 0L) {
                    SupportingText(
                        "The 'Sold by' setting can’t be changed because there are prices recorded for this product.",
                        isError = false,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (selectedOption != QuantityType.ITEM) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // TODO: RelevantUnit* here are sort of copy and paste from ItemSourceInfo and
                    // could possibly be factored out along with the code using them
                    val relevantUnitFamilies =
                        remember(vm.uiContent.dataSet) { getRelevantUnitFamilies(vm.uiContent.dataSet) }

                    val relevantUnitList =
                        remember(
                            vm.uiContent.dataSet,
                            vm.uiContent.editableItem.value.quantityType
                        ) {
                            getRelevantMeasurementUnits(
                                vm.uiContent.dataSet,
                                vm.uiContent.editableItem.value.quantityType,
                                includeDisplayOnly = false
                            )
                        }
                    MyExposedDropdownMenuBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        enabled = saveStatus.isNotBusy(),
                        selectedId = uiContent.editableItem.value.defaultUnit.id,
                        onItemSelected = {
                            val defaultUnit = MeasurementUnit.fromId(it)
                            myCheck(defaultUnit != null) {
                                "Expected non-null defaultUnit to be selected; got $it"
                            }
                            if (uiContent.editableItem.value.defaultUnit != defaultUnit!!) {
                                val defaultUnitIdByQuantityTypeOrdinal =
                                    uiContent.editableItem.value.defaultUnitIdByQuantityTypeOrdinal.toMutableList()
                                        .also {
                                            it[uiContent.editableItem.value.quantityType.ordinal] =
                                                defaultUnit.id
                                        }
                                vm.setUIContentEditableItem(
                                    uiContent.editableItem.value.copy(
                                        defaultUnitIdByQuantityTypeOrdinal = defaultUnitIdByQuantityTypeOrdinal
                                    )
                                )
                            }
                        },
                        label = { Text("Default unit") },
                        supportingText = { Text("Used only as a default when entering a price for the first time. You can still choose another unit.") },
                        items = relevantUnitList,
                        getDividerBetween = { previousItem, item -> areDifferentUnitFamilies(previousItem, item) },
                        getId = { it.id },
                        getItemText = { "${it.fullName} (${it.symbol})" },
                    )
                }
            }
        }

        // TODO END COPY-AND-PASTE-ISH RADIO BUTTON CHUNK

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "May be sold in multipacks",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    // TODO: If I change the "pack size" terminology elsewhere, need to change this too
                    text = "Allow entering a count as well as a pack size",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                enabled = saveStatus.isNotBusy(),
                checked = uiContent.editableItem.value.allowMultipack,
                onCheckedChange = {
                    vm.setUIContentEditableItem(
                        uiContent.editableItem.value.copy(
                            allowMultipack = it
                        )
                    )
                })
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableItem.value.notes)
        FilteredTextField(
            label = { Text("Notes") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                vm.setUIContentEditableItem(uiContent.editableItem.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

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
                    )
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
    requestClose: (Long?) -> Unit
) {
    val uiContent = vm.uiContent

    val sourceReferenceCount by vm.sourceReferenceCountFlow.collectAsStateWithLifecycle()
    Log.d("MyApp", "sourceReferenceCount $sourceReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by vm.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = sourceReferenceCount == 0L
    GeneralEditAndDeleteScreen(
        vm = vm.generalEditScreenViewModel,
        navController = navController,
        title = topAppBarTitle(if (vm.uiContent.editableSource.value.id == 0L) "Add store" else "Edit store", vm.uiContent.dataSet.name),
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
            // We use Words here because this is likely to be a store brand.
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
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
            singleLine = true,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditSourceViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Can I put these string versions inside LoyaltyDiscountType or won't that play well with i18n?
        val options = listOf(
            Triple(LoyaltyType.NONE, "None", null),
            Triple(
                LoyaltyType.BONUS,
                "Store rewards",
                "Points or credit usable only at this store"
            ),
            Triple(LoyaltyType.DISCOUNT, "Discount", "Discount on basket or money back")
        )
        var selectedOption = uiContent.editableSource.value.loyaltyType
        // TODO: This radio group needs to be enabled iff saveStatus.isNotBusy()

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
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
                                        loyaltyType = id
                                    )
                                )
                            }
                            .padding(horizontal = 8.dp)
                            .height(48.dp) // 40.dp is MD3 spec but we want extra space for our supporting text while still having some spacing between items
                            .semantics {
                                role = Role.RadioButton
                            }, // for TalkBack / screen readers, since this is clickable not the RadioButton
                    ) {
                        RadioButton(
                            selected = (selectedOption == id),
                            onClick = null // Row's Modifier.clickable() handles this
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = name
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

                if (selectedOption != LoyaltyType.NONE) {
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
                        ) { validationResult, interactionSource, validationInputHandle ->
                            NumericTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .validationInputHandleFocusRequester(validationInputHandle),
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

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableSource.value.notes)
        FilteredTextField(
            label = { Text("Notes") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                vm.setUIContentEditableSource(uiContent.editableSource.value.copy(notes = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            modifier = Modifier.fillMaxWidth(),
        )

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
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text("Delete store")
            }
        }
    }
}

// TODO: Rename this - it's not about TextFields and is used with various combinations of
// composables, it is about wrapping up an ErrorHighlightBox with some validation logic.
@Composable
fun <T, U> BaseValidatedTextField( // TODO: TYPE LIST IS "BACKWARDS"
    value: U,
    validationRules: List<ValidationRule<U>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean = false,
    validationFlow: SharedFlow<T>,
    validationFlowFieldId: T,
    errorHighlightOffset: Dp = defaultErrorHighlightOffset,
    modifier: Modifier = Modifier,
    content: @Composable (
        validationResult: String?,
        interactionSource: MutableInteractionSource,
        validationInputHandle: ValidationInputHandle,
    ) -> Unit
) {
    val validationInputHandle = rememberValidationInputHandle()

    val validationThing201 = rememberValidationThing(
        value = value,
        validationRules = validationRules,
        validationRulesKey = validationRulesKey,
        allowEmpty = allowEmpty
    )


    ErrorHighlightBox(
        visible = validationInputHandle.errorHighlightBoxVisible.value,
        offset = errorHighlightOffset,
        validationTarget = validationInputHandle,
        modifier = modifier
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            // TODO: We could possibly pass validationThing201 directly. We could also maybe pass a
            // Modifier.validationFocusRequester() instead of scrollToFocusableHandle.
            content(
                validationThing201.validationResult.value,
                validationThing201.interactionSource,
                validationInputHandle,
            )
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        validationFlow.collect { field ->
            Log.d("MyApp", "LaunchedEffect saveValidationError $field")
            when (field) {
                validationFlowFieldId -> {
                    validationInputHandle.requestUserAttention(focusManager)
                }

                else -> {}
            }
        }
    }
}

// TODO: RENAME
@Composable
fun <T> ValidatedTextField2(
    label: @Composable() (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    value: TextFieldValue,
    maxLength: Int,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean,
    validationRules: List<ValidationRule<String>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean = false,
    singleLine: Boolean = false,
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
    ) { validationResult, interactionSource, validationInputHandle ->
        FilteredTextField(
            label = label,
            value = value,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxLength),
            onValueChange = onValueChange,
            enabled = enabled,
            isError = validationResult != null,
            supportingText = textOrNull(
                validationResult,
                color = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .fillMaxWidth()
                .validationInputHandleFocusRequester(validationInputHandle),
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            interactionSource = interactionSource
        )
    }
}

@Composable
fun EditDataSetScreen(
    vm: EditDataSetViewModel,
    navController: NavHostController,
    requestClose: (Long?) -> Unit
) {
    val uiContent = vm.uiContent

    val dataSetReferenceCount by vm.dataSetReferenceCountFlow.collectAsStateWithLifecycle()
    Log.d("MyApp", "dataSetReferenceCount $dataSetReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by vm.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = dataSetReferenceCount == 0L
    GeneralEditAndDeleteScreen(
        vm = vm.generalEditScreenViewModel,
        navController = navController,
        title = { Text(if (uiContent.editableDataSet.value.id == 0L) "Add collection" else "Edit collection") },
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
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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
            singleLine = true,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Should we specify an offset of 4.dp here? Or should we perhaps just improve spacing?
        BaseValidatedTextField(
            value = uiContent.editableDataSet.value.currencyCode,
            validationRules = vm.currencyValidationRules,
            allowEmpty = !vm.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = vm.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.CURRENCY_CODE
        ) { validationResult, interactionSource, validationInputHandle ->
            // TODO: According to a long comment I wrote elsewhere, we probably should be using a
            // frozen LocalConfiguration from when this screen was first opened here. However, at
            // present it includes no floating point values that are awkward if the locale changes,
            // and being responsive to any locale changes is both easy and may be helpful. If I keep
            // doing it this way, I need to update that long comment elsewhere accordingly and make
            // a permanent note here too.
            val currentLocalConfiguration = LocalConfiguration.current
            val currencyList = remember(currentLocalConfiguration.locales) {
                // TODO: Test this updates if we change locales on the fly?
                createCurrencyList(currentLocalConfiguration.locales)
            }

            // We try to do half-decent job by showing a gigantic list in an unwieldy dropdown but
            // putting the currencies the user is likely to care about at the top.
            // ENHANCE: In the longer term I see three options:
            // 1 - optionally allow the user to just enter a three letter currency code directly
            // 2 - optionally allow the user to define their own currency (in which case we don't
            //     care about three letter codes) by specifying prefix, suffix and decimal places
            // 3 - investigate third party libraries to help with this
            // If option 2 is available, there may be no real need for option 1. We'd probably still
            // support currency selection in some form, but the specific escape hatch of being able
            // to type in a three letter code is not so important. But maybe we'd do both.
            //
            // We could create our own pop-up (maybe full screen?) dialog to pick a currency.
            // We could also use our existing item selection dialog - which is substring search
            // capable - to help the user pick something out of the gigantic list of currencies
            // instead of scrolling through a giant dropdown.

            // TODO: This may expose a lurking bug in MyExposedDropdownMenuBox - the very last (I
            // think) item in the list is *not* entirely shown. I don't know if the same thing will
            // happen with other dropdowns which get long enough to need scrolling, but should
            // definitely test as it matters much more there. It's ugly and annoying and concerning
            // here too, of course. (This doesn't seem to happen on the O6!?)
            MyExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .validationInputHandleFocusRequester(validationInputHandle),

                selectedId = if (uiContent.editableDataSet.value.currencyCode != "") uiContent.editableDataSet.value.currencyCode else null,
                onItemSelected = {
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
                getItemText = { it.second },
                getDividerBetween = { firstItem, _ -> firstItem.first == currencyList.first },
                supportingText = textOrNull(
                    validationResult,
                    color = MaterialTheme.colorScheme.error,
                ),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ENHANCE: MD3 Expressive deprecates this and says we should use a connected button group,
        // but the relevant library version is still in alpha so I'll just do it the old MD3 way for
        // now with a segmented button group.
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
            )
            // "US customary" doesn't fit (on my test "small" emulated phone) but based on a discussion
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
            // TODO: Following is hacky, use an enum class or something rather than hardcoding 1 and
            // 2 as imperial/US

            // We *don't* call Modifier.validationFocusRequester() as you can't focus a segmented
            // button, and this will force a clear focus to happen on validation errors instead.
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
                            // Don't allow imperial and US customary to be selected together. (We use
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
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
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
                    )
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
    // show a "name is empty" warning without waiting for a save attempt first). It is just about
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
        // TODO: The delay is breaking things a bit here when e.g. we have an empty "pack size"
        // string and click save - the validation message becomes eligible for display as allowEmpty
        // is now true, but it doesn't appear straight away and so it "misses" the highlight box and
        // it generally looks bad and a bit confusing. (This is less of a visual issue now I've
        // dropped the delay from 1000ms to 200ms, but it's probably best to address it properly.
        // Maybe put the delay back to 1000ms temporarily when working on this.) I suspect the fix
        // is to have a remembered oldValue, say "if (value != oldValue)" here instead of
        // controlling based on isFocused, and the obviously set oldValue = value after. Not tested
        // this, maybe too simplistic.
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


fun Locale.currencyOrNull(): Currency? {
    try {
        return Currency.getInstance(this)
    } catch (e: IllegalArgumentException) {
        // Some locales (e.g. zz_ZZ) might not have a valid currency.
        return null
    }
}

// I would have preferred to use Android's own list of valid currency codes, but there seems to be
// so much junk (e.g. historical currency codes, which are irrelevant for our purposes) that I had
// to give up on the idea. The following list is a manual combination of the results from the
// following lists:
// - https://www.six-group.com/dam/download/financial-information/data-center/iso-currrency/lists/list-one.xls
// - https://www.six-group.com/dam/download/financial-information/data-center/iso-currrency/lists/list-two.doc
// - https://www.six-group.com/dam/download/financial-information/data-center/iso-currrency/lists/list-three.xls
// with a few additional tweaks.
// @formatter:off
val validCurrencyCodes = setOf(
    "AED", "AFN", "ALL", "AMD", "AOA", "ARS", "AUD", "AWG", "AZN", "BAM", "BBD", "BDT", "BGN",
    "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTN", "BWP", "BYN", "BZD", "CAD", "CDF",
    "CHF", "CLP", "CNY", "COP", "CRC", "CUP", "CVE", "CZK", "DJF", "DKK", "DOP", "DZD", "EGP",
    "ERN", "ETB", "EUR", "FJD", "FKP", "GBP", "GEL", "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD",
    "HKD", "HNL", "HTG", "HUF", "ILS", "INR", "IQD", "IRR", "ISK", "JMD", "JOD", "JPY", "KES",
    "KGS", "KHR", "KMF", "KPW", "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL",
    "LYD", "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP", "MRU", "MUR", "MVR", "MWK", "MXN",
    "MYR", "MZN", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", "PHP",
    "PKR", "PLN", "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG", "SEK",
    "SGD", "SHP", "SLE", "SOS", "SRD", "SSP", "STN", "SVC", "SYP", "SZL", "THB", "TJS", "TMT",
    "TND", "TOP", "TRY", "TTD", "TWD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "VED", "VES",
    "VND", "VUV", "WST", "XAF", "XCD", "XCG", "XOF", "XPF", "YER", "ZAR", "ZMW", "ZWG"
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
fun createCurrencyList(locales: LocaleList): Pair<String, List<Pair<String, String>>> {
    fun createPair(currency: Currency): Pair<String, String> {
        val currencyCode = currency.currencyCode
        val displayName = currency.getDisplayName(locales[0])
        if (displayName.contains(currencyCode)) {
            Log.d("MyApp", "not adding $currencyCode for $displayName")
            return Pair(currency.currencyCode, displayName)
        } else {
            return Pair(currency.currencyCode, "$displayName ($currencyCode)")
        }
    }

    // We accept the currencies for the current locales even if they are not in validCurrencyCodes.
    // ENHANCE: For all I know this isn't smart - maybe some locales include historic currency codes
    // and we'd be better off filtering using validCurrencyCodes even here - but for now it seems
    // best to err on the side of caution. This significantly reduces the chances of a user not
    // being able to select a currency they care about. The amount of noise is likely to be
    // relatively small; any given locale is going to have only a few historic currency codes and
    // the user is going to have a small number of current locales.
    val mainCurrencyList = mutableListOf<Pair<String, String>>()
    val mainCurrencyCodeSet = mutableSetOf<String>()
    for (i in 0 until locales.size()) {
        val locale = locales[i]
        val currency = locale.currencyOrNull()
        if (currency != null && currency.currencyCode !in mainCurrencyCodeSet) {
            mainCurrencyList.add(createPair(currency))
            mainCurrencyCodeSet.add(currency.currencyCode)
        }
    }

    // We intersect the results of getAvailableCurrencies() with validCurrencyCodes. The former
    // includes a lot of irrelevant junk for our purposes, but we don't want to try to use a code
    // from validCurrencyCodes if the system doesn't understand it.
    val otherCurrencyList =
        Currency.getAvailableCurrencies().mapNotNull { currency ->
            if (currency.currencyCode in mainCurrencyCodeSet ||
                currency.currencyCode !in validCurrencyCodes) {
                null } else { createPair(currency) }
        }

    return Pair(
        mainCurrencyList.last().first,
        mainCurrencyList.toList() + otherCurrencyList.sortedByLocale( { it.second }, locales[0])
    )
}

// The idea here is this does not insist the input is actually parseable as a decimal (for example,
// we allow "24.2.3" so the user can enter a new decimal point and then go delete the old one
// afterwards), but that it rejects obviously incorrect things. We allow digits, commas, full stops
// and spaces - the interpretation of these is locale-dependent, but this should allow valid
// decimals to be entered with no annoying quirks in any locale.
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

// TODO: Temporary semi-copy of validationRulesOk - I can probably convert vROk callers to use this
// TODO: Call this "failedValidationRule"? Maybe makes null=none clearer?
fun <T> validationRulesCheck(validationRules: List<ValidationRule<T>>, value: T): ValidationRule<T>? {
    for (validationRule in validationRules) {
        if (!validationRule.validate(value)) {
            return validationRule
        }
    }
    return null
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
    maxValue: Int? = null,
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
            // This message assumes you can't enter a negative value because input filtering rejects
            // '-'.
            ValidationRule({ attemptedParse(it) != 0.0 }, "Must be greater than zero")
        } else {
            null
        },

        if (maxValue != null) {
            ValidationRule( { (attemptedParse(it) ?: 0.0) <= maxValue }, "Must be no greater than $maxValue")
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
        // ENHANCE: We don't (we could, but probably no point) allow arbitrary
        // onCandidateValueChange functions to be supplied by our caller. We just hardcode this for
        // now. We could potentially accept some options from our caller which say whether decimal
        // point (locale sensitive) or minus signs are allowed and tweak the internally-assigned
        // onCandidate... function here.
        onCandidateValueChange = { isValidTransitionalDecimal(it) && it.length <= maxDecimalLength },
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        modifier = modifier,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        interactionSource = interactionSource,
    )
}

// TODO: This function itself is fine, but it provides an easy demonstration that if the text is
// *already* over the limit, we can not edit it to delete anything. In practice you shouldn't get
// into this kind of hole, but it might be worth changing the API of onCandidateValueChange so
// it receives the old text as well, so here we could allow the new text if it's shorter than the
// old text even if it's still too long.
// ENHANCE: The length limit on our ValidatedTextFields is just there to keep things tidy and in
// practice we don't expect a user to run up against it. We therefore don't show a current/max
// character count, as it would probably be more confusing than helpful. (Imagine editing a
// notionally decimal value in a text field with current/max character counts under it.) It's not
// absolutely ideal that the user's input is just silently ignored if they do hit the length limit,
// but it's not a likely case and I can't think of a nice way to show this. We could maybe show some
// kind of transitory supportingText message (not one of the more persistent ones our validation
// infrastructure generates), but even ignoring the implementation difficulties I am not sure that
// would be better than just silently dropping input.
fun createOnCandidateValueChangeMaxLength(maxLength: Int): (String) -> Boolean =
    { it.length <= maxLength }


// Like TextField, but with some simple logic to allow input to be filtered and discarded via an
// onCandidateValueChange callback. It also - although this is just a convenience and isn't
// fundamental - automatically drives the internal TextField's trailingIcon from the isError
// parameter if it's not explictly specified.
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
    singleLine: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
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
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
            ?: if (isError) {
                {
                    WarningIcon(contentDescription = "Error")
                }
            } else null,
        isError = isError,
        singleLine = singleLine,
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
fun SettingsScreen(
    vm: SettingsViewModel,
    navController: NavHostController,
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val stalePriceThreshold by vm.settingsRepository.stalePriceThresholdFlow.collectAsStateWithLifecycle(initialValue = defaultStalePriceThreshold)
    val ancientPriceThresholdDays by vm.settingsRepository.ancientPriceThresholdDaysFlow.collectAsStateWithLifecycle(initialValue =defaultAncientPriceThresholdDays)
    val annualInflationPercent by vm.settingsRepository.annualInflationPercentFlow.collectAsStateWithLifecycle(initialValue = defaultAnnualInflationPercent)
    var showStalePriceThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var showAncientPriceThresholdDialog by rememberSaveable { mutableStateOf(false) }
    var showAnnualInflationPercentDialog by rememberSaveable { mutableStateOf(false) }
    var showRestoreConfirmDialog by rememberSaveable { mutableStateOf(false) }

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
                //.background(MaterialTheme.colorScheme.primary) // TODO: debug hack
                .fillMaxSize()
                .padding(innerPadding)
                // Padding here follows the same approach as GeneralSelectorScreen() - see the
                // comment there.
                .padding(vertical = screenBorder)
                .verticalScroll(rememberScrollState())
        ) {
            // ENHANCE: Since stale price threshold and ancient price threshold have interrelated
            // validation, there just might be an argument for allowing them to be edited
            // simultaneously to help avoid the annoyance of wanting to change one and having to
            // cancel out and go change the other first. This is probably not a huge deal in
            // practice. Editing these should not be an everyday activity so even if it is a bit
            // fiddly it doesn't matter that much.

            // TODO: I half wonder if we should do *<=* stale/ancient threshold - currently we use < in code for stale at least - just to match this description (subtitle) which feels more natural, but maybe we can reword this.

            SettingsTile(
                title = "Stale price threshold",
                subtitle = "Prices considered stale after $stalePriceThreshold days",
                onClick = {
                    showStalePriceThresholdDialog = true
                }
            )

            SettingsTile(
                title = "Ancient price threshold",
                subtitle = "Prices considered ancient after $ancientPriceThresholdDays days",
                onClick = {
                    showAncientPriceThresholdDialog = true
                }
            )

            SettingsTile(
                title = "Annual inflation",
                subtitle = "Stale prices increase by $annualInflationPercent% per year",
                onClick = {
                    showAnnualInflationPercentDialog = true
                }
            )

            SettingsTile(
                title = "Backup",
                subtitle = "Back up your data to a file to keep it safe",
                onClick = onBackupClick
            )

            SettingsTile(
                title = "Restore",
                subtitle = "Replace all data with a backup you made earlier",
                onClick = { showRestoreConfirmDialog = true }
            )

            SettingsTile(
                title = "About $appName",
                subtitle = "", // empty subtitle gives consistent layout with other tiles
                onClick = onAboutClick
            )
        }

        if (showStalePriceThresholdDialog) {
            SettingsDialog(
                title = "Stale price threshold",
                subtitle = "Stale prices (confirmed more than this many days ago) have an inflation adjustment applied when comparing across stores.",
                label = "Days",
                initialValue = stalePriceThreshold.toString(),
                validationRules = listOfNotNull(
                    ValidationRule(
                            {
                                val days = it.toIntOrNull()
                                days != null && days >= 1
                            },
                    "Must be positive"
                    ),
                    ValidationRule(
                        {
                            val days = it.toIntOrNull()
                            days != null && days < ancientPriceThresholdDays
                        },
                        "Must be less than $ancientPriceThresholdDays (ancient price threshold)"
                    )
                ),
                onConfirm = { stalePriceThresholdString ->
                    showStalePriceThresholdDialog = false
                    vm.settingsRepository.setStalePriceThresholdAsync(stalePriceThresholdString.toInt())
                },
                onDismissRequest = {
                    showStalePriceThresholdDialog = false
                }
            )
        }

        if (showAncientPriceThresholdDialog) {
            SettingsDialog(
                title = "Ancient price threshold",
                subtitle = "Ancient prices (confirmed more than this many days ago) are ignored when classifying prices as good/OK/bad.",
                label = "Days",
                initialValue = ancientPriceThresholdDays.toString(),
                validationRules = listOfNotNull(
                    ValidationRule(
                        {
                            val days = it.toIntOrNull()
                            days != null && days > stalePriceThreshold
                        },
                        "Must be greater than $stalePriceThreshold (stale price threshold)"
                    ),
                    // TODO: Just maybe drop this upper bound? Or make it something like 9999999
                    // just for sanity? Maybe users don't want an upper bound (unlikely but possible).
                    ValidationRule(
                        {
                            val days = it.toIntOrNull()
                            days != null && days <= 365
                        },
                    "Must be no greater than 365"
                    ),
                ),
                onConfirm = { ancientPriceThresholdDaysString ->
                    showAncientPriceThresholdDialog = false
                    vm.settingsRepository.setAncientPriceThresholdDaysAsync(
                                ancientPriceThresholdDaysString.toInt())
                },
                onDismissRequest = {
                    showAncientPriceThresholdDialog = false
                }
            )
        }

        if (showAnnualInflationPercentDialog) {
            SettingsDialog(
                title = "Annual inflation",
                subtitle = "Stale prices increase by this annual rate. This is only an estimate $emDash update prices when you can for better accuracy.",
                label = "%", // TODO: Can/should I put a suffix on the OutlinedTextField instead?
                initialValue = annualInflationPercent.toString(),
                validationRules = listOfNotNull(
                    ValidationRule(
                        {
                            val inflation = it.toIntOrNull()
                            inflation != null
                        },
                        "Must be a whole number"
                    ),
                    ValidationRule(
                        {
                            val inflation = it.toIntOrNull()
                            inflation != null && inflation >= 0
                        },
                        "Must be zero or greater"
                    ),
                    ValidationRule(
                        {
                            val inflation = it.toIntOrNull()
                            inflation != null && inflation <= 1000
                        },
                        "Must be no greater than 1000%"
                    ),
                ),
                onConfirm = { annualInflationPercentString ->
                    showAnnualInflationPercentDialog = false
                    vm.settingsRepository.setAnnualInflationPercentAsync(
                                annualInflationPercentString.toInt())
                },
                onDismissRequest = {
                    showAnnualInflationPercentDialog = false
                }
            )
        }

        if (showRestoreConfirmDialog) {
            // ENHANCE: I don't want to overdo it - but this is both destructive and rare - but
            // should we show another final "are you sure?" dialog after the user has chosen a file
            // inside onRestoreClick() before we actually go ahead? I think it is still good to have
            // a dialog at this point, as it immediately makes it obvious if the user mis-tapped on
            // restore when they meant to tap on backup - both of which would otherwise go straight
            // into a system file selection dialog.
            AlertDialog(
                icon = { WarningIcon(contentDescription = "Warning") },
                title = { Text("Restore from backup") },
                text = { Text("This will replace all current app data with the data from your backup. This action is permanent and cannot be undone. Do you want to continue?") },
                onDismissRequest = { showRestoreConfirmDialog = false },
                dismissButton = {
                    TextButton(onClick = { showRestoreConfirmDialog = false }) { Text("Cancel") }
                },
                confirmButton = {
                    TextButton(onClick = { showRestoreConfirmDialog = false; onRestoreClick() }) { Text("Restore") }
                }
            )
        }
    }
}

@Composable
fun WarningIcon(contentDescription: String) {
    Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.error
    )
}

@Composable
fun SettingsTile(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

// TODO: I could tweak this to be more like the full screen dialogs, in that Save is *always*
// enabled but if you click it that then "enables" warnings about the field being empty. However, I
// don't think we necessarily need to overdo this consistency - since there is literally a single
// text field in play here, it's much more obvious that the reason you can't save is that it's
// blank.
// TODO: Can/should this have a small lag in updating supportingText as I think our normal full
// screen edit dialogs did at one point (not sure if they still do)?
@Composable
fun SettingsDialog(
    title: String,
    subtitle: String,
    label: String,
    initialValue: String,
    validationRules: List<ValidationRule<String>>,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var currentValue by rememberSaveable { mutableStateOf(initialValue) }
    var textFieldValue by remember { mutableStateOf(
        TextFieldValue(
            text = currentValue,
            // Put the caret at the end of the string - this is why we need a TextFieldValue.
            selection = TextRange(currentValue.length))) }
    var error by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Column {
                Text(subtitle, modifier = Modifier.padding(bottom = 16.dp))
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it; currentValue = it.text; error = validationRulesCheck(validationRules, it.text.trim())?.message },
                    label = { Text(label) },
                    supportingText = {
                        if (error != null) Text(
                            error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    isError = error != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (error == null) {
                        onConfirm(currentValue.trim())
                    }
                },
                enabled = currentValue.trim().isNotEmpty() && error == null
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    )

    LaunchedEffect(Unit) {
        // This delay is a ChatGPT-suggested magic value to let the dialog animation complete before showing the keyboard. Apparently some versions of Android may not show the keyboard if focus is requested before this point.
        delay(150)
        focusRequester.requestFocus()
    }

}

// TODO: ChatGPT magic
fun Drawable.toBitmap(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)
    return bitmap
}

// TODO: ChatGPT magic
@Composable

fun LauncherIcon(size: Dp = 120.dp) {
    val context = LocalContext.current
    val density = LocalDensity.current

    // Convert Dp to pixels for toBitmap()
    val px = with(density) { size.toPx().toInt() }

    val drawable = context.packageManager.getApplicationIcon(context.packageName)
    val bitmap = drawable.toBitmap(px, px)

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "App Icon",
        modifier = Modifier.size(size) // still use Dp for layout
    )
}

// TODO: ChatGPT magic
@Composable
fun getAppVersion(): String {
    val context = LocalContext.current
    return remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            // pInfo.versionName is the human-readable version (e.g., "1.2.3")
            val versionName = pInfo.versionName
            val versionCode = if (android.os.Build.VERSION.SDK_INT >= 28) {
                pInfo.longVersionCode
            } else {
                pInfo.versionCode.toLong()
            }
            "Version $versionName" // could add " ($versionCode)"?
        } catch (e: PackageManager.NameNotFoundException) {
            "Version unknown"
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClickableLink(text: String, url: String, showRawUrl: Boolean = true) {
    val uriHandler = LocalUriHandler.current

    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .clickable { uriHandler.openUri(url) }
                // This padding feels slightly visually unattractive (although it's growing on me a
                // bit), but we want to allow some clearance so the "tappable area" to click on the
                // links isn't too small, roughly in accordance with MD3 guidelines even if we're
                // not following them formally here.
                .padding(vertical = 8.dp)
        )

        if (showRawUrl) {
            // SelectionContainer allows the user to select the link so they can copy it to their
            // clipboard for further use.
            SelectionContainer {
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun BulletPoint(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("$bulletPoint ", style = MaterialTheme.typography.bodyMedium)
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun AboutScreen(navController: NavHostController, onViewLegalClick: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("About $appName") }, navigationIcon = {
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
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = fullScreenDialogHorizontalBorder)
                .verticalScroll(rememberScrollState()),
        ) {
            // We manually implement the vertical border so it is part of the scrollable region, not
            // something which reduces the size of the scrollable region. This feels a bit better to
            // me and (albeit not for the same reason) matches what we do in GeneralEditScreen().
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                // App icon
                LauncherIcon(size = 96.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Version
                Text(
                    text = getAppVersion(),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Links card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Resources",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Links below will open in your browser.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ClickableLink("User manual", "https://yourappdocs.example.com", showRawUrl = true) // TODO!
                    Spacer(modifier = Modifier.height(8.dp))
                    ClickableLink("Source code on GitHub", "https://github.com/yourusername/yourapp", showRawUrl = true) // TODO!
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attributions card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // This is where we give credit for third-party components we are using in a
                    // readable way. The full legally compliant stuff which is not actually readable
                    // doesn't go here, it goes on LegalScreen().
                    Text(
                        "Attributions",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BulletPoint("Material Design icons (Google, Apache 2.0)")
                    /* For future reference:
                    BulletPoint("ExampleLibrary (MIT) — placeholder for future third-party library")
                    */
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to legal screen
            FilledTonalButton(onClick = onViewLegalClick,    shape = MaterialTheme.shapes.small) {
                Text("View full legal information")
            }
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
        }
    }
}

@Composable
fun LegalScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Legal information") }, navigationIcon = {
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
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = fullScreenDialogHorizontalBorder)
                //  TODO!? .padding(16.dp) - this is chatgpt, maybe redundant now i have scaffold
        ) {
            // We manually implement the vertical border so it is part of the scrollable region, not
            // something which reduces the size of the scrollable region. This feels a bit better to
            // me and (albeit not for the same reason) matches what we do in GeneralEditScreen().
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))

            // Our license
            Text(
                text = "$appName $emDash MIT License",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Copyright $copyrightSymbol 2025 TODOMYNAME",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            LicenseText(
                licenseText = "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n\nThe above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n\nTHE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."
            )

            /* Third-party licences, if/when we have some:

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Third-Party Licenses",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Each third-party library gets a separate block
            ThirdPartyLicense(
                libraryName = "ExampleLibrary",
                licenseName = "MIT License",
                licenseText = "[full MIT license text for ExampleLibrary]"
            )
            ThirdPartyLicense(
                libraryName = "ExampleLibrary2",
                licenseName = "MIT License",
                licenseText = "[full MIT license text for ExampleLibrary]"
            )
            */

            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
        }
    }
}

@Suppress("unused")
@Composable
fun ThirdPartyLicense(
    libraryName: String,
    licenseName: String,
    licenseText: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "$libraryName $emDash $licenseName",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        LicenseText(licenseText)
    }
}

@Composable
fun LicenseText(licenseText: String) {
    Text(
        text = licenseText,
        style = MaterialTheme.typography.bodySmall
    )
}

// TODO: ChatGPT magic
private fun Context.isPhone(): Boolean {
    val metrics = resources.configuration
    val smallestWidthDp = metrics.smallestScreenWidthDp
    return smallestWidthDp < 600  // Common cutoff: <600dp = phone
}

// TODO: This is a bit of a mess but probably best leave it alone until I either gain more
// experience or do more testing with different Android versions.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // I understand Google are discouraging apps from simply locking to portrait orientation, to
        // support tablets/ChromeOS devices better. This app is probably always going to want to run
        // in portrait on a (non-foldable) phone and I don't see any value in putting effort into
        // layouts to allow landscape to work properly on a phone. ChatGPT suggested locking to
        // portrait only on phones and I think that's a reasonable compromise for now. I haven't
        // tested on tablets or similar devices, but I suspect the app will work fine on larger
        // screens in portrait or landscape, even if maybe looks a bit odd.
        // ENHANCE: In the future it might be nice to add alternative layouts to work better on
        // larger devices like tablets or foldables in both landscape and portrait mode. This is
        // probably not a common use case though.
        if (isPhone()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

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
                    color = /* Color.Green */ MaterialTheme.colorScheme.background
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


data class EditSourcesScreenUIContent(
    val sourceList: List<Source>,
    val dataSet: DataSet
) {
    fun saveState(handle: SavedStateHandle) {
        handle[SOURCE_LIST_KEY] = sourceList
        handle[DATA_SET_KEY] = dataSet
    }

    companion object {
        private const val SOURCE_LIST_KEY = "sourceList"
        private const val DATA_SET_KEY = "dataSet"

        fun fromSavedState(handle: SavedStateHandle): EditSourcesScreenUIContent? {
            val savedSourceList: List<Source>? = handle[SOURCE_LIST_KEY]
            val savedDataSet: DataSet? = handle[DATA_SET_KEY]
            if (savedSourceList != null && savedDataSet != null) {
                Log.d("MyApp", "reconstructed EditSourcesScreenUIContent")
                return EditSourcesScreenUIContent(savedSourceList, savedDataSet)
            } else {
                Log.d("MyApp", "couldn't reconstruct EditSourcesScreenUIContent")
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

    // TODO: As editPriceScreenUIContent
    var viewPriceHistoryScreenUIContent: ViewPriceHistoryScreenUIContent? = null

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
            uiContent.priceAnalysis.augmentedPriceList.map { it.basePrice }.find { it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id }

        val editablePrice = if (price != null)
            price.toEditable(frozenLocale, dataSet.createCurrencyFormat(frozenLocale))
        else EditablePrice.forNew(
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
            nonLinearEdit = false,
            frozenLocale = frozenLocale,
        )
    }

    // TODO: It might be possible to share some code with the non-2 version or refactor but let's just
    // bash this out for now.
    fun setEditPriceScreenContent2(
        dataSet: DataSet,
        item: Item,
        source: Source,
        editablePrice: EditablePrice,
        frozenLocale: Locale
    ) {
        editPriceScreenUIContent = EditPriceScreenUIContent(
            editablePrice = mutableStateOf(editablePrice),
            originalPrice = editablePrice,
            dataSet = dataSet,
            item = item,
            source = source,
            nonLinearEdit = true,
            frozenLocale = frozenLocale,
        )
    }

    // TODO: Some overlap with setEditPriceScreenContent()?
    fun setViewPriceHistoryScreenContent(
        uiContent: HomeScreenUIContent,
        frozenLocale: Locale
    ) {
        // !! is justified because uiContent was shown on the home screen and the view history option
        // was enabled, which can only happen if we have all three available.
        val dataSet = uiContent.dataSet!!
        val item = uiContent.item!!
        val source = uiContent.source!!
        val price =
            uiContent.priceAnalysis.augmentedPriceList.map { it.basePrice }.find { it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id }

        viewPriceHistoryScreenUIContent = ViewPriceHistoryScreenUIContent(
            dataSet = dataSet,
            item = item,
            source = source,
            price = price,
        )
    }

    // TODO: Not just here, but e.g. EditSourceScreen vs EditSource*s*Screen is way too subtle for this already rather confusing code. Might (apart from other possible improvements) be better to
    // talk about "Edit source" (singular) but "List sources" (plural) internally, even if we continue to use "Edit sources" in the UI labels.

    // TODO: Rename the following now they are just List<T>? not a UIContent structure? Or is the "UIContent" convention more valuable?
    var editDataSetsScreenUIContent: List<DataSet>? = null
    var editItemsScreenUIContent: EditItemsScreenUIContent? = null
    var editSourcesScreenUIContent: EditSourcesScreenUIContent? = null

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
        editSourcesScreenUIContent = EditSourcesScreenUIContent(
            uiContent.sourceList + uiContent.sourceList.map { it -> it.copy(id = it.id * 1000) },
            uiContent.dataSet!!
        )
    }

    var editDataSetScreenUIContent: EditDataSetScreenUIContent? = null

    fun setEditDataSetScreenContent(dataSet: DataSet?, locale: Locale) {
        val editableDataSet = dataSet.toEditable(locale)
        editDataSetScreenUIContent = EditDataSetScreenUIContent(
            editableDataSet = mutableStateOf(editableDataSet),
            originalDataSet = editableDataSet,
        )
    }

    var editItemScreenUIContent: EditItemScreenUIContent? = null

    fun setEditItemScreenContent(item: Item?, dataSet: DataSet) {
        val editableItem = item.toEditable(dataSet)
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
        dataSet: DataSet,
        frozenLocale: Locale
    ) {
        val editableSource = source.toEditable(dataSet.id, frozenLocale)
        editSourceScreenUIContent = EditSourceScreenUIContent(
            editableSource = mutableStateOf(editableSource),
            originalSource = editableSource,
            dataSet = dataSet,
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

// ENHANCE: This function does not handle non-English languages very well. As far as I can tell from
// discussing with LLMs and doing my own web searches, we really need something like the ICU string
// search service (https://unicode-org.github.io/icu/userguide/collation/string-search) but although
// Android has some ICU stuff by default, it apparently doesn't have this. I am going to use this
// basic implementation (which I believe won't handle the German sharp S correctly, just as an
// example) for now and can revisit it later if any non-English users turn up.
fun isCaseInsensitiveSubstring(lhs: String, rhs: String, locale: Locale) =
    rhs.lowercase(locale).contains(lhs.lowercase(locale))

fun squashSpaces(s: String) = s.trim().replace(Regex("\\s+"), " ")

// ENHANCE: We probably *can* do a half-decent job of implementing this locale-sensitive, probably
// something to do with collate(), but need to look into it. This is different to
// isCaseInsensitiveSubstring() because we are dealing with the string as a whole, not substrings.
// But for now I will hack it with this English-ish version.
fun areHumanEqual(lhs: String, rhs: String): Boolean {
    return squashSpaces(lhs.lowercase()) == squashSpaces(rhs.lowercase())
}

class EditItemsViewModel(
    savedStateHandle: SavedStateHandle,
    getName: (Item) -> String,
    val uiContent: EditItemsScreenUIContent,
    dataQuery: Flow<List<Item>>,
) : GeneralSelectorViewModel<Item>(
    savedStateHandle,
    getName,
    uiContent.itemList /* TODO: rename initialList for consistency with other cases? */,
    dataQuery
) {
    init {
        uiContent.saveState(savedStateHandle)
    }
}

class EditSourcesViewModel(
    savedStateHandle: SavedStateHandle,
    getName: (Source) -> String,
    val uiContent: EditSourcesScreenUIContent,
    dataQuery: Flow<List<Source>>,
) : GeneralSelectorViewModel<Source>(
    savedStateHandle,
    getName,
    uiContent.sourceList /* TODO: rename initialList for consistency with other cases? */,
    dataQuery
) {
    init {
        uiContent.saveState(savedStateHandle)
    }
}

open class GeneralSelectorViewModel<T>(
    private val savedStateHandle: SavedStateHandle,
    private val getName: (T) -> String,
    private val initialList: List<T>?,
    private val dataQuery: Flow<List<T>>,
) : ViewModel() {
    // The idea here is that as we have no real state other than the results of dataQuery, we
    // optimise by having our caller provide initialList to give a good first composition during
    // normal navigation, but we can manage without it if we are reincarnated.
    // TODO: This works and it is probably fine but note that for EditItemsViewModel we do actually
    // serialise, even though the general code doesn't require it. (We need it so we can pass a
    // DataSet through to EditItemScreen.)

    // This will *not* filter uiContent.initialList, but that's OK because we know the initial
    // filter doesn't exclude anything.
    // ENHANCE: We could persist the search string via savedStateHandle.
    val searchStringFlow = MutableStateFlow(TextFieldValue(""))

    @OptIn(ExperimentalCoroutinesApi::class)
    val dataFlow = combine(
        dataQuery.flatMapLatest { data -> /* TODO HACK delay(5000); */ flowOf(data) },
        searchStringFlow
    ) { data, query ->
        data.filter {
            isCaseInsensitiveSubstring(
                query.text.trim(),
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
            // I don't know if this is MD3 compliant, the spec talks about actual sizes but I feel
            // I ought to be using MaterialTheme.typography.* styles. This does seem to look about
            // right anyway.
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
    addContentDescription: String,
    // TODO: We pass the actual T to onItemSelected to try to avoid race conditions, I am not
    // completely sure about this but let's see how it goes. Do I need to do this in any other
    // contexts as well?
    onItemSelected: (T) -> Unit,
    showSearch: Boolean = false,
) {
    val dataList by vm.dataFlow.collectAsStateWithLifecycle()
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
                    contentDescription = addContentDescription
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
            // We apply innerPadding and a vertical screenBorder but no horizontal padding here so
            // the list can be edge-to-edge. The individual list items still have horizontal padding
            // between the screen edge and their text, but e.g. the ripple effect on click goes
            // right to the edge of the screen, which I think is how MD3 likes it. The vertical
            // screenBorder padding is arguably unnecessary, but although mostly invisible (in
            // practice the background colour of the top app bar and the screen content are the
            // same), it adds some consistency - particularly when the search field is present -
            // with the vertical spacing on other screens.
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = screenBorder)
            // TODO: copied from Home, maybe want this but put it in when we do .verticalScroll(androidx.compose.foundation.rememberScrollState())
        ) {
            // ENHANCE: We could show a warning icon and/or some supporting text if nothing matches
            // the substring, rather than just showing an empty list.
            if (showSearch) {
                val searchString by vm.searchStringFlow.collectAsStateWithLifecycle()
                FilteredTextField(
                    value = searchString,
                    onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxSearchLength),
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
                            modifier = Modifier.clickable { vm.searchStringFlow.value = TextFieldValue("") },
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = screenBorder)
                        .padding(bottom = 8.dp),
                    singleLine = true,
                )
            }

            val dataListSorted = dataList.rememberSortedByLocale { getName(it) }
            Box(
                modifier = Modifier
                    //.background(Color.Green /* TODO! */)
                    .fillMaxWidth()
            ) {
                dataList.forEach { println("Item: $it, ID: ${getId(it)}") }
                LazyColumn {
                    items(
                        items = dataListSorted,
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

            // ENHANCE: We could offer support for deleting items here, e.g. via "swipe to reveal a
            // bin icon". This would probably be more useful if this code is re-used. In this app,
            // deleting is rare and potentially scary and we prefer to hide it away a bit by putting
            // it on the individual edit screens.
        }
    }
}

// This is only used once and I'd like to inline it, as if anything it obfuscates what's happening
// rather than simplifying the code. But inlining causes problems with dropUnlessResumed and the
// possible workarounds feel worse than just keeping this function.
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
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditPriceScreenUIContent,
) : ViewModel() {
    private val instanceId = UUID.randomUUID().toString() // TODO FOR DEBUG

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    // "Count" is visible if the item explicitly allows multipacks or if (presumably because it
    // used to) we have a count > 1, which we must not hide or silently throw away. Note that
    // uiContent.originalPrice.count can be an empty string if we are adding a first price.
    val showPackCount = uiContent.item.allowMultipack || (uiContent.originalPrice.count.toLongOrNull() ?: 1) > 1

    val packCountValidationRules = if (showPackCount) numericValidationRules(uiContent.frozenLocale, allowDecimals = false, allowZero = false) else emptyList()
    var packSizeValidationRules = generatePackSizeValidationRules()
    var currencyFormat = uiContent.dataSet.createCurrencyFormat(uiContent.frozenLocale)

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
        val maxDecimals = uiContent.editablePrice.value.measurementUnit.maxDecimals
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
        PRICE,
        PACK_COUNT,
        PACK_SIZE,
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        if (!validationRulesOk(
                currencyFormat.validationRules,
                uiContent.editablePrice.value.price
            )
        ) {
            _saveValidationEvents.emit(EditableField.PRICE)
            return false
        }
        if (!validationRulesOk(
                packCountValidationRules,
                uiContent.editablePrice.value.count
            )
        ) {
            Log.d("MyAppPC", "Pack count failed validation")
            _saveValidationEvents.emit(EditableField.PACK_COUNT)
            return false
        }
        if (!validationRulesOk(
                packSizeValidationRules,
                uiContent.editablePrice.value.measureValue
            )
        ) {
            _saveValidationEvents.emit(EditableField.PACK_SIZE)
            return false
        }
        return true
    }

    suspend fun performSave() : Long {
        // nonLinearEdit indicates that we are editing an old historical value as a candidate for
        // updating the current record, so if the user clicks save it *is* a change even if
        // editablePrice and originalPrice are the same. (We don't just try to hack originalPrice
        // because we don't want to warn the user about losing non-existent changes if they click
        // close instead of save.)
        // TODO: Double check the handling of toConfirm here. My thinking is that if editablePrice
        // has toConfirm set that constitutes a change, so by using the real value in editablePrice
        // and forcing originalPrice to have toConfirm false that does what we want there, and will
        // also pick up any other changes.
        if (!uiContent.nonLinearEdit && uiContent.editablePrice.value == uiContent.originalPrice.copy(
                toConfirm = false
            )
        ) {
            Log.d(
                "MyApp",
                "performSave() is a no-op; returning early to avoid bloating price history"
            )
            return uiContent.editablePrice.value.id
        }
        val price = uiContent.editablePrice.value.toDomain(uiContent.frozenLocale)
        Log.d("MyApp", "saveEditablePrice price $price")
        if (price == null) {
            throw IllegalStateException("saveEditablePrice() called with an inconvertible editablePrice: ${uiContent.editablePrice.value}")
        }
        return repository.updateOrInsertPrice(price)
    }
}

sealed class AsyncOperationStatus {
    object Idle : AsyncOperationStatus()
    object Busy : AsyncOperationStatus()
    object BusyForAWhile : AsyncOperationStatus()
    data class Success(val id: Long?) : AsyncOperationStatus()
    data class Error(val message: String) : AsyncOperationStatus()
}

fun AsyncOperationStatus.isNotBusy(): Boolean {
    return when (this) {
        is AsyncOperationStatus.Busy,
        is AsyncOperationStatus.BusyForAWhile,
        is AsyncOperationStatus.Success -> false
        else -> true
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
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditSourceScreenUIContent,
) : ViewModel() {
    init {
        uiContent.saveState(savedStateHandle)
    }

    val sourceReferenceCountFlow = uiContent.editableSource.value.id.let { sourceId ->
        if (sourceId != 0L) {
            repository.countPricesForSource(sourceId)
        } else {
            flowOf(0L) // new sources have no references
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUIContentEditableSource(newEditableSource: EditableSource) {
        uiContent.editableSource.value = newEditableSource
        uiContent.saveEditableSourceState(savedStateHandle)
    }

    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        repository.getAllSources(uiContent.editableSource.value.dataSetId)
            .map { sourceList ->
                createNameValidationRules(
                    sourceList.filter { source -> source.id != uiContent.editableSource.value.id }
                        .map { source -> source.name }
                )
            }
            .withVersion()
            // initialValue here is set to an unsatisfiable validation list to avoid a theoretical
            // corner case. If we defaulted to emptyList(), the user might be able to save with an
            // invalid name before the real validation rules become available.
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialVersioned(listOf(ValidationRule({ false }, ""))))

    // ENHANCE: Maybe we should allow zero here? We might need to tweak some messages accordingly.
    // Zero isn't necessary as you can choose "None", but maybe it's a bit persnickety not to allow
    // the user just to type 0 directly with one of the other options as well.
    val loyaltyPercentageValidationRules = numericValidationRules(
        uiContent.frozenLocale,
        allowDecimals = true,
        allowZero = false,
        maxDecimals = 2,
        // A discount of 100% or more might lead to corner cases, so let's choose an already
        // unrealistically high maximum of 99% as an easy workaround.
        maxValue = 99,
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
        if (uiContent.editableSource.value.loyaltyType != LoyaltyType.NONE && !validationRulesOk(
                loyaltyPercentageValidationRules,
                uiContent.editableSource.value.loyaltyPercentage
            )
        ) {
            _saveValidationEvents.emit(EditableField.LOYALTY_PERCENTAGE)
            return false
        }
        Log.d("MyAppESS", "validateForSave passed")
        return true
    }

    suspend fun performSave(): Long {
        val source = uiContent.editableSource.value.toDomain(uiContent.frozenLocale)
        if (source == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableSource: ${uiContent.editableSource.value}")
        }
        // updateOrInsertSource() returns -1 if it's an update or the new ID if it was an insert.
        val newId = repository.updateOrInsertSource(source)
        return if (newId == -1L) source.id else newId
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val sourceId = uiContent.editableSource.value.id
        myCheck(sourceId != 0L) { "Expected to delete an actual source but have ID 0" }
        val rowsDeleted = repository.deleteSourceById(sourceId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with sourceId $sourceId")
    }
}

class EditItemViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditItemScreenUIContent,
) : ViewModel() {
    init {
        uiContent.saveState(savedStateHandle)
    }

    val itemReferenceCountFlow = uiContent.editableItem.value.id.let { itemId ->
        if (itemId != 0L) {
            repository.countPricesForItem(itemId)
        } else {
            flowOf(0L) // new items have no references
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = null)

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUIContentEditableItem(newEditableItem: EditableItem) {
        uiContent.editableItem.value = newEditableItem
        uiContent.saveEditableItemState(savedStateHandle)
    }

    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        repository.getAllItems(uiContent.editableItem.value.dataSetId)
            .map { itemList ->
                createNameValidationRules(
                    itemList.filter { item -> item.id != uiContent.editableItem.value.id }
                        .map { item -> item.name }
                )
            }
            .withVersion()
            // initialValue here is set to an unsatisfiable validation list to avoid a theoretical
            // corner case. If we defaulted to emptyList(), the user might be able to save with an
            // invalid name before the real validation rules become available.
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialVersioned(listOf(ValidationRule({ false }, ""))))

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
        Log.d("MyAppESS", "validateForSave passed")
        return true
    }

    suspend fun performSave() : Long {
        val item = uiContent.editableItem.value.toDomain()
        if (item == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableItem: ${uiContent.editableItem.value}")
        }
        // updateOrInsertItem() returns -1 if it's an update or the new ID if it was an insert.
        val newId =  repository.updateOrInsertItem(item)
        Log.d("MyAppQZ", "updateOrInsertItem returned $newId")
        return if (newId == -1L) item.id else newId
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val itemId = uiContent.editableItem.value.id
        myCheck(itemId != 0L) { "Expected to delete an actual item but have ID 0" }
        val rowsDeleted = repository.deleteItemById(itemId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with itemId $itemId")
    }
}

data class ViewPriceHistoryScreenUIContent(
    val dataSet: DataSet,
    val item: Item,
    val source: Source,
    val price: Price?,
) {
    fun saveState(handle: SavedStateHandle) {
        handle[DATA_SET_KEY] = dataSet
        handle[ITEM_KEY] = item
        handle[SOURCE_KEY] = source
        handle[PRICE_KEY] = price
    }

    companion object {
        private const val DATA_SET_KEY = "dataSet"
        private const val ITEM_KEY = "item"
        private const val SOURCE_KEY = "source"
        private const val PRICE_KEY = "price"

        fun fromSavedState(handle: SavedStateHandle): ViewPriceHistoryScreenUIContent? {
            val savedDataSet: DataSet? = handle[DATA_SET_KEY]
            val savedItem: Item? = handle[ITEM_KEY]
            val savedSource: Source? = handle[SOURCE_KEY]
            val savedPrice: Price? = handle[PRICE_KEY]
            // We don't check savedPrice in the next line, because it can be null even if we have
            // saved state. The other non-nullable keys act as our sentinels.
            if (savedDataSet != null && savedItem != null && savedSource != null) {
                Log.d("MyApp", "reconstructed ViewPriceHistoryScreenUIContent")
                return ViewPriceHistoryScreenUIContent(
                    savedDataSet,
                    savedItem,
                    savedSource,
                    savedPrice
                )
            } else {
                Log.d("MyApp", "couldn't reconstruct ViewPriceHistoryScreenUIContent")
                return null
            }
        }
    }
}

data class PriceHistoryDelta(
    val priceHistory: PriceHistory, // TODO: having this here feels a bit crap, maybe it's OK
    val price: Double?,
    val count: Long?,
    val quantity: MeasuredValue?,
    // confirmedAt is a string so we can do "user-resolution" de-duplication
    val confirmedAt: String?,
    val notes: String?,
    val modifiedAt: Instant
)

fun PriceHistory.toPriceHistoryDelta(confirmedAtFormatter: DateTimeFormatter): PriceHistoryDelta =
    PriceHistoryDelta(
        priceHistory = this,
        price = price,
        count = count,
        quantity = MeasuredValue(quantityInBaseUnit, baseUnitForQuantityType(userUnit.quantityType)).to(
            userUnit
        ),
        confirmedAt = confirmedAtFormatter.format(confirmedAt),
        notes = notes,
        modifiedAt = modifiedAt
    )

// TODO: Where does this belong and what naming and calling convention should it have?!?!?!
fun diff(
    lhs: PriceHistory,
    rhs: PriceHistory,
    confirmedAtFormatter: DateTimeFormatter
): PriceHistoryDelta? {
    val rhsQuantity = MeasuredValue(
        rhs.quantityInBaseUnit,
        baseUnitForQuantityType(rhs.userUnit.quantityType)
    ).to(rhs.userUnit)
    // Note that by using confirmedAtFormatter here and PriceHistory.confirmedAt being the resulting
    // string, if two PriceHistory records have visually indistinguishable confirmedAt values we
    // won't show them, and if there are no other differences we will hide the extra record
    // entirely.
    val lhsConfirmedAt = confirmedAtFormatter.format(lhs.confirmedAt)
    val rhsConfirmedAt = confirmedAtFormatter.format(rhs.confirmedAt)
    Log.d("MyApp", "lhsConfirmedAt $lhsConfirmedAt rhsConfirmedAt $rhsConfirmedAt")
    val confirmedAt = if (lhsConfirmedAt == rhsConfirmedAt) null else rhsConfirmedAt
    val notes = if (lhs.notes.trim() == rhs.notes.trim()) null else rhs.notes
    val priceOrQuantityChanged = (lhs.price != rhs.price) || (lhs.count != rhs.count) || (lhs.quantityInBaseUnit != rhs.quantityInBaseUnit)
    if (priceOrQuantityChanged || confirmedAt != null || notes != null) {
        // The notes field is a legitimate source of diffs, but in practice it sometimes looks a bit
        // odd showing it when it's empty. For the moment we elide diffs where the only change is
        // that a note used to be empty and now it isn't. ENHANCE: There is probably scope for
        // tweaking this.
        if (!priceOrQuantityChanged && confirmedAt == null && notes != null && notes.trim().isEmpty()) {
            return null
        }
        return PriceHistoryDelta(
            priceHistory = rhs,
            price = if (!priceOrQuantityChanged) null else rhs.price,
            count = if (!priceOrQuantityChanged) null else rhs.count,
            quantity = if (!priceOrQuantityChanged) null else rhsQuantity,
            confirmedAt = confirmedAt,
            notes = notes,
            modifiedAt = rhs.modifiedAt
        )
    } else {
        return null
    }
}

class ViewPriceHistoryViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: ViewPriceHistoryScreenUIContent,
) : ViewModel() {
    init {
        Log.d("MyApp", "ViewPriceHistoryViewModel.init($uiContent)")
        uiContent.saveState(savedStateHandle)
    }

    val priceHistoryListFlow = repository.getPriceHistory(
        uiContent.dataSet.id,
        uiContent.item.id,
        uiContent.source.id
    ).map { priceHistoryList -> sanitisePriceHistoryUnits(uiContent.dataSet, priceHistoryList) }

    // TODO: It would be good to include "the price ID changed => the price was deleted" in this
    // history. I don't believe we can know the date of the deletion, but we could show a trivial
    // diff card which just says "price was deleted", so we can at least see it happened.
    fun generatePriceHistoryDeltaList(
        priceHistoryList: List<PriceHistory>,
        locale: Locale,
        confirmedAtFormatter: DateTimeFormatter
    ) =
    // Remember that we are doing a "backwards delta" here - we show the very latest element in full,
    // and for older elements we show differences between them and the next newest element. This zip
        // has every member of priceHistoryList appear exactly once as oldPriceHistory.
        (listOf(null) + priceHistoryList).zip(priceHistoryList)
            .mapNotNull { (newPriceHistory, oldPriceHistory) ->
                if (newPriceHistory == null) oldPriceHistory.toPriceHistoryDelta(
                    confirmedAtFormatter
                ) else diff(newPriceHistory, oldPriceHistory, confirmedAtFormatter)
            }

    // TODO: dataSetFlow is probably a temp hack
    val dataSetFlow = repository.getAllDataSets()

}

// ENHANCE: Not here specifically, I almost wonder if the lambdas should have the *option* (not
// obligation) to modify the value for later lambdas in the chain, and the validation process
// returns the final one. This *might* provide a natural way to implement things like "strip
// spaces" or "strip insignificant fluff in a double-as-string" as an initial step, avoid
// redoing that work in subsequent lambdas which want the same sanitising and help to avoid the
// situation where for example the validation is all based on a trim()ed string but I forget to
// manually apply the trim() when writing the string to the database. On the other hand, applying
// the validation rule changes to a data class via copy() might be finicky and error prone, and
// this would perhaps add subtle behavioural quirks around the ordering of the list which might
// be brittle. (Then again, with respect to brittleness, some rules' error messages might implicitly
// assume earlier rules already filtered out some unacceptable cases anyway.)
fun createNameValidationRules(existingNameList: List<String>): List<ValidationRule<String>> {
    return listOf(
        ValidationRule<String>({ it.isNotEmpty() }, "Must have a name"),
    ) + existingNameList.map { name ->
        ValidationRule(
            { candidateName -> !areHumanEqual(candidateName, name) },
            "Name must be unique"
        )
    }
}

// TODO: There is a huge amount of pseudo copy and paste in all the Edit*{Screen,ViewModel} stuff.
// Probably just going to accept it as I do the initial implementation so I don't tie myself in
// knots coping with generic attempts that don't quite match reality, but later on it would be good
// to see what can be factored out.
class EditDataSetViewModel(
    private val repository: Repository,
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

    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        repository.getAllDataSets()
            .map { dataSetList ->
                createNameValidationRules(
                    dataSetList.filter { dataSet -> dataSet.id != uiContent.editableDataSet.value.id }
                        .map { dataSet -> dataSet.name }
                )
            }
            .withVersion()
            // initialValue here is set to an unsatisfiable validation list to avoid a theoretical
            // corner case. If we defaulted to emptyList(), the user might be able to save with an
            // invalid name before the real validation rules become available.
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialVersioned(listOf(ValidationRule({ false }, ""))))

    val currencyValidationRules = listOf(
        ValidationRule<String>(
            { it.isNotEmpty() },
            "Currency must be specified"
        )
    )

    // ENHANCE: I should probably replace the Triple<3xBoolean> with a data class for readability.
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
        CURRENCY_CODE,
        MEASUREMENT_SYSTEM,
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
        return true
    }

    suspend fun performSave(): Long {
        val dataSet = uiContent.editableDataSet.value.toDomain()
        if (dataSet == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableDataSet: ${uiContent.editableDataSet.value}")
        }
        // updateOrInsertDataSet() returns -1 if it's an update or the new ID if it was an insert.
        val newId = repository.updateOrInsertDataSet(dataSet)
        return if (newId == -1L) dataSet.id else newId
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val dataSetId = uiContent.editableDataSet.value.id
        myCheck(dataSetId != 0L) { "Expected to delete an actual data set but have ID 0" }
        val rowsDeleted = repository.deleteDataSetById(dataSetId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with dataSetId $dataSetId")
    }
}

// ENHANCE: This function was mostly written by ChatGPT. I'm loosely aware of what it does but I
// don't pretend to understand the details at this point.
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

fun safeRestartApp(context: Context) {
    // ChatGPT told me to do this with AlarmManager, but that didn't work and I believe it isn't
    // the recommended approach on Android 10+. Perplexity recommended the following approach, which
    // feels like it has a subtle theoretical hole in it where the app might kill itself without
    // restarting, but I don't think it's possible to do better and in practice this probably won't
    // happen, and if it does it's a bit user-unfriendly but at least safe (the user just has to
    // restart the app manually).
    val packageManager = context.packageManager
    val launchIntent = packageManager.getLaunchIntentForPackage(context.packageName) ?: return
    val componentName = launchIntent.component ?: return
    val restartIntent = Intent.makeRestartActivityTask(componentName)
    context.startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel =
        viewModel(LocalContext.current as ComponentActivity) // TODO: perplexity voodoo

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showRestartDialog by rememberSaveable { mutableStateOf(false) }

    // TODO: ChatGPT magic

    val context = LocalContext.current
    val activity = context as? Activity

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri ->
            if (uri != null) {
                try {
                    backupDatabase(context, uri)
                } catch (e: Exception) {
                    errorMessage = e.localizedMessage ?: "An unknown error occurred."
                }
            }
        }
    )

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    restoreDatabase(context, uri)
                    // All sorts of internal state is probably outdated. This is a rare operation
                    // and we don't want to massively complicate our code (e.g. the flows feeding
                    // the home screen) to handle it, so we just force a restart.
                    showRestartDialog = true
                } catch (e: Exception) {
                    errorMessage = e.localizedMessage ?: "An unknown error occurred."
                }
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        // I think these are fairly standard values, and Android's system level support for tweaking
        // animation speeds mean there's no need to allow these to be tweakable via Settings.
        val tweenDurationMillisEnter = 300
        val tweenDurationMillisExit = 250

        // ENHANCE: It might be good to look at adding a fade to some of these animations - maybe a
        // fade added to "top" screen and perhaps a fade added to the "bottom" screen as well. I
        // don't think it's a huge deal and it is "correct", but the border on the incoming screen
        // can - if you're really looking at the transition with paranoid eyes - give an impression
        // of the outgoing non-background content "flickering away" before being replaced by new
        // content. I say "correct" because if we're imagining cards sliding on top of one another
        // in a stack the border would indeed cause this kind of "flicker", but it might still look
        // nicer with some fading.
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
                onItemSearchClick = { uiContent ->
                    sharedViewModel.setEditItemsScreenContent(uiContent)
                    navController.navigate("editItems/select")
                },
                onViewHistoryClick = { uiContent ->
                    // We navigate giving this ID triplet instead of the price ID here, so that if a
                    // price gets deleted, we can still see the full history (and we can tell where
                    // deletions occurred by discontinuities in the price ID, albeit we won't know
                    // the precise time they happened).
                    sharedViewModel.setViewPriceHistoryScreenContent(uiContent, locale)
                    navController.navigate(route = "viewPriceHistory")
                },
                onEditDataSetsClick = { uiContent ->
                    sharedViewModel.setEditDataSetsScreenContent(
                        uiContent
                    )
                    navController.navigate("editDataSets")
                },
                onEditItemsClick = { uiContent ->
                    sharedViewModel.setEditItemsScreenContent(
                        uiContent
                    )
                    navController.navigate("editItems/edit")
                },
                onEditSourcesClick = { uiContent ->
                    sharedViewModel.setEditSourcesScreenContent(
                        uiContent
                    )
                    navController.navigate("editSources/${uiContent.dataSet!!.id}/${uiContent.dataSet.name}")
                },
                onSettingsClick = { navController.navigate("settings") },
            )
        }

        composable(
            "settings", enterTransition = { slideLeftTransition() }, popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            SettingsScreen(
                viewModel(backStackEntry, factory = AppViewModelProvider.Factory),
                navController,
                onBackupClick = {
                    backupLauncher.launch("price_tracker_backup.db")
                },
                onRestoreClick = {
                    restoreLauncher.launch(arrayOf("*/*"))
                },
                onAboutClick = { navController.navigate("about") })
        }

        composable(
            "about", enterTransition = { slideLeftTransition() }, popEnterTransition = { null }, popExitTransition = { slideRightTransition() }, ) {
            AboutScreen(navController, onViewLegalClick = { navController.navigate("legal") })
        }

        composable(
            "legal", enterTransition = { slideLeftTransition() }, popEnterTransition = { null }, popExitTransition = { slideRightTransition() }, ) {
            LegalScreen(navController)
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
                        getName = { it -> it.name },
                        initialList = sharedViewModel.editDataSetsScreenUIContent,
                        dataQuery = app.repository.getAllDataSets()
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
                    title = topAppBarTitle("Edit collections", null),
                    getId = { it.id },
                    getName = { it.name },
                    onAddClick = {
                        Log.d("MyAppGS", "Add data set")
                        sharedViewModel.setEditDataSetScreenContent(null, locale)
                        navController.navigate("editDataSet")
                    },
                    addContentDescription = "Add data set",
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        sharedViewModel.setEditDataSetScreenContent(it, locale)
                        navController.navigate("editDataSet")
                    })
            }
        }

        composable(
            "editItems/{action}", enterTransition = { slideLeftTransition() },
            popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            val action = backStackEntry.arguments?.getString("action")
            myRequire(action == "edit" || action == "select") { "Invalid action: $action" }
            val select = action == "select"
            screenWithViewModel<EditItemsViewModel, Int /* TODO DUMMY */>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editItemsScreenUIContent = null },
                buildViewModel = { app, handle ->
                    val uiContent = sharedViewModel.editItemsScreenUIContent
                        ?: EditItemsScreenUIContent.fromSavedState(handle)!!
                    EditItemsViewModel(
                        savedStateHandle = handle,
                        getName = { it -> it.name },
                        uiContent,
                        dataQuery = app.repository.getAllItems(uiContent.dataSet.id),
                    )
                }
            ) { viewModel ->
                val dataStore = LocalContext.current.applicationContext.dataStore
                val context = LocalContext.current.applicationContext
                GeneralSelectorScreen(
                    viewModel,
                    navController,
                    title = topAppBarTitle(if (!select) "Edit products" else "Select product", viewModel.uiContent.dataSet.name),
                    getId = { it.id },
                    getName = { it.name },
                    onAddClick = {
                        // We don't alter our behaviour here depending whether or not we're being
                        // used to select an item directly from the home screen or via "Edit
                        // products". It's handy to be able to directly add a missing item when
                        // searching from the home screen.
                        Log.d("MyAppGS", "Add item")
                        sharedViewModel.setEditItemScreenContent(null, viewModel.uiContent.dataSet)
                        navController.navigate("editItem")
                    },
                    addContentDescription = "Add item",
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        if (!select) {
                            sharedViewModel.setEditItemScreenContent(
                                it,
                                viewModel.uiContent.dataSet
                            )
                            navController.navigate("editItem")
                        } else {
                            setCurrentItemIdAsync(context, viewModel.uiContent.dataSet.id, it.id)
                            navController.popBackStack() // return to home screen
                        }
                    },
                    showSearch = true
                )
            }
        }

        composable(
            "editSources/{dataSetId}/{dataSetName}", enterTransition = { slideLeftTransition() },
            popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            val dataSetId = backStackEntry.arguments?.getString("dataSetId")!!.toLong()
            val dataSetName = backStackEntry.arguments?.getString("dataSetName")
            screenWithViewModel<EditSourcesViewModel, Int /* TODO DUMMY */>(
                backStackEntry = backStackEntry,
                // TODO: Could should sharedViewModel have a clearAllContent() or similar function
                // and we just call that in clearUIContent? That way we could be sure *no* old
                // content is lurking around.
                clearUIContent = { sharedViewModel.editSourcesScreenUIContent = null },
                buildViewModel = { app, handle ->
                    val uiContent = sharedViewModel.editSourcesScreenUIContent ?: EditSourcesScreenUIContent.fromSavedState(handle)!!
                    EditSourcesViewModel(
                        savedStateHandle = handle,
                        getName = { it -> it.name },
                        uiContent,
                        dataQuery = app.repository.getAllSources(dataSetId)
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
                        sharedViewModel.setEditSourceScreenContent(null, viewModel.uiContent.dataSet, locale)
                        navController.navigate("editSource")
                    },
                    addContentDescription = "Add source",
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        sharedViewModel.setEditSourceScreenContent(it, viewModel.uiContent.dataSet, locale)
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
                        app.repository,
                        handle,
                        sharedViewModel.editPriceScreenUIContent
                            ?: EditPriceScreenUIContent.fromSavedState(handle)!!
                    )
                }, // TODO !! IS MAYBE A HACK - TBH COULD I JUST MAKE FROMSAVEDSTATE RETURN NON-NULL? NOT TOO KEEN
            ) { viewModel ->
                EditPriceScreen(
                    viewModel, navController,
                    requestClose = { id ->
                        if (id == null) {
                            navController.popBackStack()
                        } else {
                            navController.popBackStack("home", inclusive = false)
                        }
                    }
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
                        app.repository,
                        handle,
                        sharedViewModel.editDataSetScreenUIContent
                            ?: EditDataSetScreenUIContent.fromSavedState(handle)!!
                    )
                }
            ) { viewModel ->
                val dataStore = LocalContext.current.applicationContext.dataStore
                val context = LocalContext.current.applicationContext
                EditDataSetScreen(
                    viewModel, navController,
                    requestClose = { newSelectedDataSetId ->
                        if (newSelectedDataSetId == null) {
                            navController.popBackStack()
                        } else {
                            setCurrentDataSetIdAsync(context, newSelectedDataSetId)
                            navController.popBackStack("home", inclusive = false)
                        }
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
                        app.repository,
                        handle,
                        sharedViewModel.editItemScreenUIContent
                            ?: EditItemScreenUIContent.fromSavedState(handle)!!
                    )
                }
            ) { viewModel ->
                val dataStore = LocalContext.current.applicationContext.dataStore
                EditItemScreen(
                    viewModel, navController,
                    requestClose = { newSelectedItemId ->
                        // It might be somewhat logical to just do popBackStack() here, but in
                        // reality if I've added or edited an item it's almost always because I want
                        // to actually work with it on the home screen.
                        // ENHANCE: Just possibly there should be a setting to always do a simple
                        // popBackStack() here instead of immediately selecting an item which we
                        // just added/edited.
                        if (newSelectedItemId == null) {
                            // The user cancelled the edit, so just go back one step.
                            navController.popBackStack()
                        } else {
                            // The used saved the edit, so select the edited item and return to the
                            // home screen.
                            Log.d("MyAppQZ", "newSelectedItemId=$newSelectedItemId")
                            setCurrentItemIdAsync(context, viewModel.uiContent.dataSet.id, newSelectedItemId)
                            navController.popBackStack("home", inclusive = false)
                        }
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
                        app.repository,
                        handle,
                        sharedViewModel.editSourceScreenUIContent
                            ?: EditSourceScreenUIContent.fromSavedState(handle)!!
                    )
                }
            ) { viewModel ->
                val dataStore = LocalContext.current.applicationContext.dataStore
                val context = LocalContext.current.applicationContext
                EditSourceScreen(
                    viewModel, navController,
                    requestClose = { newSelectedSourceId ->
                        if (newSelectedSourceId == null) {
                            navController.popBackStack()
                        } else {
                            setCurrentSourceIdAsync(context, viewModel.uiContent.dataSet.id, newSelectedSourceId)
                            navController.popBackStack("home", inclusive = false)
                        }
                    })
            }
        }

        composable(
            "viewPriceHistory",
            enterTransition = { slideLeftTransition() },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            val locale by rememberUpdatedState(LocalConfiguration.current.locales[0])
            screenWithViewModel<ViewPriceHistoryViewModel, ViewPriceHistoryScreenUIContent>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.viewPriceHistoryScreenUIContent = null },
                buildViewModel = { app, handle ->
                    ViewPriceHistoryViewModel(
                        app.repository,
                        handle,
                        sharedViewModel.viewPriceHistoryScreenUIContent
                            ?: ViewPriceHistoryScreenUIContent.fromSavedState(handle)!!
                    )
                }
            ) { viewModel ->
                ViewPriceHistoryScreen(
                    viewModel, navController,
                    requestClose = {
                        navController.popBackStack()
                    },
                    requestEditAsNew = { priceHistory ->
                        // ENHANCE: There might be some value into copying priceHistory.id onto the
                        // EditablePrice here (and from there onto PriceEntity/PriceHistory if the
                        // user saves it, so there's be a nullable "based_on_price_history_id"
                        // column on price/price_history tables), so there is a pseudo-audit trail
                        // showing the new price was generated by this route instead of the regular
                        // add/edit buttons on the home screen. We don't need solid forensic grade
                        // history though, and the user can edit the data before it's even saved, so
                        // I don't think this is a big deal. It might be interesting/useful for
                        // support/self-support purposes. ("Why did my notes disappear on this
                        // price? Oh, I rolled back to a historical price which didn't have them
                        // either.")

                        Log.d("MyApp", "TODO: requestEditAsNew $priceHistory")
                        sharedViewModel.setEditPriceScreenContent2(
                            viewModel.uiContent.dataSet,
                            viewModel.uiContent.item,
                            viewModel.uiContent.source,
                            editablePrice = priceHistory.toEditable(
                                // It's important we provide the current price ID, since we must
                                // update the current existing record instead of adding a new one.
                                // The price ID might in principle have changed since the history
                                // record was created.
                                priceId = viewModel.uiContent.price?.id ?: 0,
                                locale,
                                viewModel.uiContent.dataSet
                            ),
                            locale
                        )
                        navController.navigate("editPrice")
                    })
            }
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }

    if (showRestartDialog) {
        LaunchedEffect(Unit) {
            Log.d("MyAppFFS", "activity $activity")
            delay(1500)
            safeRestartApp(activity!!)
        }

        AlertDialog(
            onDismissRequest = { /* prevent dismissal */ },
            title = { Text("App will restart") },
            text = { Text("Applying restored data. Please wait...") },
            confirmButton = {}
        )
    }
}

@Composable
fun PackPriceAndSizeRow(
    price: Double,
    count: Long,
    measure: MeasuredValue,
    dataSet: DataSet,
    asyncOperationStatus: AsyncOperationStatus
) {
    // The two elements of this row share the space 60%/40%. The shelf price can get quite long for
    // multipack items and 50%/50% starts to get right on small phones. We don't really need that
    // much space for the unit price either. This might ruin a proper 2x2 grid, but in practice at
    // least for now the row below is "Confirmed" and it theoretically has the full width of the
    // display.

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        LabeledItem(
            modifier = Modifier.weight(0.6f), label = "Shelf price"
        ) {
            Text(
                "${
                    formatPrice(
                        price,
                        dataSet,
                        LocalConfiguration.current.locales[0]
                    )
                } for ${if (count > 1) "${count}${multiplicationSign}" else ""}${
                    measure.toDisplayString(LocalConfiguration.current.locales[0])
                }" /*, color = MaterialTheme.colorScheme.onSurface*/
            )
        }

        val relevantUnitFamilies =
            remember(dataSet) { getRelevantUnitFamilies(dataSet) }

        val relevantUnitList =
            remember(dataSet, measure.unit.quantityType) {
                getRelevantMeasurementUnits(
                    dataSet,
                    measure.unit.quantityType,
                    includeDisplayOnly = true
                )
            }
        Log.d("MyAppQA", "measure identityHashCode=${System.identityHashCode(measure)}")
        // NB: We are using remember() here to avoid redoing an expensive computation on every
        // recomposition. We *must not* use rememberSaveable(), because it does *not* recompute when
        // navigating back after a new item is selected in another screen, due to saved state
        // restoration behaviour. We could force recomputation by adding a composite key like
        // "$dataSet-$price-$measure", but that's a hack and not an ideal solution.
        // ENHANCE: That's not even the whole story. selectedUnitPriceUnit is
        // *initialised* by an expensive computation, but the user can change it, and we really
        // ought to be remembering what they select fairly persistently, at least across config
        // changes (e.g. dark mode toggle). This would seem to argue for some use of rememberSaveable()
        // but there is some hellishly subtle behaviour here whether with keys or "inputs=" and if
        // we're not careful we end up crashing as well because we preserve a denominator of the
        // wrong quantity type as the item changes. On top of these technical complexities, I am
        // not even sure when we should preserve the user's value - if for example the price changes
        // enough that our recommended denominator changes, should we override the user's selection?
        var selectedUnitPriceUnit by remember(dataSet, price, count, measure) {
            Log.d("MyAppQA", "rememberSaveable $price $measure")
            val candidateDenominators = getMeasurementUnitsOfSameQuantityTypeAndUnitFamily(
                dataSet,
                measure.unit,
                includeDisplayOnly = true
            )
            val friendlyUnitPrice = UnitPrice.calculate(price, count, measure).withFriendlyDenominator(
                measure.unit,
                getCurrencyDecimalPlaces(dataSet),
                candidateDenominators
            )
            Log.d("MyAppQA", "rememberSaveable returning $friendlyUnitPrice")
            mutableStateOf(friendlyUnitPrice.denominator)
        }
        // If the user chooses a "bad" unit price denominator, we might end up with the unit price
        // being formatted to 0 with the available decimal places. I don't think this is really a
        // big deal and the alternatives (e.g. adding extra decimal places beyond the currency's
        // standard dps or rounding up instead of to nearest so the value isn't zero) are probably
        // worse.
        Log.d("MyAppQA", "calling formatUnitPrice $price $measure $selectedUnitPriceUnit")
        val unitPriceString =
            UnitPrice.calculate(
                price,
                count,
                measure,
                selectedUnitPriceUnit,
            ).format(dataSet,LocalConfiguration.current.locales[0])
        LabeledItemWithDropdown(
            modifier = Modifier.weight(0.4f), label = "Unit price",
            dropdownContentDescription = "Select unit",
            text = unitPriceString,
            enabled = asyncOperationStatus.isNotBusy(),
            items = relevantUnitList,
            getId = { it },
            getItemText = { "${it.perSymbol}${it.symbol}".trim() },
            getDividerBetween = { previousItem, item -> areDifferentUnitFamilies(previousItem, item) },
            selectedId = selectedUnitPriceUnit,
            onItemSelected = { selectedUnitPriceUnit = it })
    }
}

fun backupDatabase(context: Context, targetUri: Uri) {
    val db = AppDatabase.getDatabase(context)
    // The next line is voodoo which Grok suggested "might" be necessary and ChatGPT seemed to
    // agree there could be borderline cases. I am not convinced but I guess it's likely harmless
    // at worst.
    db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)")
    val dbPath = checkNotNull(db.openHelper.writableDatabase.path) { "Expected non-null database path" }

    // Use a temp file to dump to.
    // ENHANCE: Could/should we take steps to try to delete this afterwards if an exception occurs?
    // I imagine it isn't too critical as we will have at most one temp file and thus at worst we
    // double the size of our data storage, and our database isn't likely to be that big in the
    // first place. It's probably as simple as a try-finally block though, just needs a bit of
    // testing.
    val backupFile = File(context.cacheDir, "backup_temp.db")

    // VACUUM INTO the temp file. I tried using copy() but the WAL files make this unreliable, and
    // based on discussions with both ChatGPT and Grok there is no simple workaround. VACUUM INTO
    // needs a minSdk>=30 and if that proves annoying, it might be worth investigating the rather
    // tricksy alternatives later on.
    val rawDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
    rawDb.execSQL("VACUUM INTO '${backupFile.absolutePath.replace("'", "''")}'")
    rawDb.close()

    // Copy the temp file to the user-selected URI.
    context.contentResolver.openOutputStream(targetUri)?.use { output ->
        FileInputStream(backupFile).use { input ->
            input.copyTo(output)
        }
    }

    backupFile.delete() // Clean up temp file
}

fun restoreDatabase(context: Context, sourceUri: Uri) {
    val dbFile = context.getDatabasePath(DB_NAME)

    // Create a temp file from the URI.
    val tempFile = File(context.cacheDir, "temp_backup.db")
    try {
        // Copy sourceUri to temp file.
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IOException("Failed to open input stream for URI: $sourceUri")

        // Validate version on the backup file.
        // TODO: This really needs testing, either by faking a newer DB_VERSION at some point or
        // when we do actually have a newer one.
        val restoredDbVersion = getDatabaseVersion(tempFile.path)
        if (restoredDbVersion > DB_VERSION) {
            throw IllegalStateException("The database to restore is a newer version ($restoredDbVersion) than this version of the app supports ($DB_VERSION).")
        }

        // ENHANCE: It might be nice to do a sanity check on the backup file, e.g. checking that
        // the tables defined on it are a subset of the ones we expect or at least that one or two
        // key tables like "price" exist. The idea is not to even try to prevent deliberately bad
        // databases being installed - if the user wants to grief themselves that is their business
        // - but to catch accidental restoration of other sqlite databases which aren't from this
        // app.

        // The backup file is OK, so we'll go ahead and overwrite our internal database now.

        // Close Room to avoid conflicts.
        AppDatabase.clearInstance()

        // Delete existing database files for clean slate. I don't know if this is necessary but at
        // one point Grok suggested this might be useful to avoid old SHM/WAL files hanging around and
        // confusing things. I don't think this will hurt so let's be cautious.
        context.deleteDatabase(DB_NAME)

        // Copy tempFile to internal database location.
        FileInputStream(tempFile).use { input ->
            FileOutputStream(dbFile, false).use { output ->
                input.copyTo(output)
            }
        }
    } finally {
        tempFile.delete() // Clean up temp file
    }
}

fun getDatabaseVersion(dbPath: String): Int {
    val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
    val version = db.version
    db.close()
    return version
}

@Composable
fun CardTitle(title: String, subtitle: String? = null) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
    if (subtitle != null) {
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ItemSourceInfoHistory(
    dataSet: DataSet,
    priceHistoryDelta: PriceHistoryDelta,
    modifiedAtTitleFormatter: DateTimeFormatter,
    modifiedAtSubtitleFormatter: DateTimeFormatter,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
        ) {
            CardTitle(
                title = modifiedAtTitleFormatter.format( priceHistoryDelta.modifiedAt),
                subtitle = modifiedAtSubtitleFormatter.format(priceHistoryDelta.modifiedAt)
            )

            if (priceHistoryDelta.price != null || priceHistoryDelta.count != null || priceHistoryDelta.quantity != null) {
                myCheck(priceHistoryDelta.price != null && priceHistoryDelta.count != null && priceHistoryDelta.quantity != null) {
                    "Expected price, count and quantity to all be non-null since one is"
                }
                PackPriceAndSizeRow(priceHistoryDelta.price!!, priceHistoryDelta.count!!, priceHistoryDelta.quantity!!, dataSet, AsyncOperationStatus.Idle)
            }

            if (priceHistoryDelta.confirmedAt != null) {
                LabeledItem(
                    modifier = Modifier.padding(bottom = 8.dp),
                    label = "Confirmed" /* "Last checked" */
                ) {
                    Text(priceHistoryDelta.confirmedAt)
                }
            }

            if (priceHistoryDelta.notes != null) {
                    Row(modifier = Modifier.padding(bottom = 8.dp)) {
                        LabeledItem("Notes") {
                            Text(priceHistoryDelta.notes)
                        }
                    }
            }
        }
    }
}

@Composable
fun ViewPriceHistoryScreen(
    viewModel: ViewPriceHistoryViewModel,
    navController: NavHostController,
    requestClose: () -> Unit,
    requestEditAsNew: (priceHistory: PriceHistory) -> Unit
) {
    val locale = LocalConfiguration.current.locales[0]
    val zoneId = ZoneId.systemDefault()
    val confirmedAtFormatter = remember(locale, zoneId) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
            .withZone(zoneId)
    }
    val priceHistoryList by viewModel.priceHistoryListFlow.collectAsStateWithLifecycle(emptyList())
    val priceHistoryDeltaList = remember(priceHistoryList, locale) {
        viewModel.generatePriceHistoryDeltaList(priceHistoryList, locale, confirmedAtFormatter)
    }

    Log.d("MyApp", "priceHistoryDeltaList $priceHistoryDeltaList")

    val dataSet = viewModel.uiContent.dataSet

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { requestClose() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = topAppBarTitle(viewModel.uiContent.item.name, viewModel.uiContent.source.name),
            )
        },

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(screenBorder)
        ) {

            // TODO: Do I need to specify a key for the rows?
            // TODO: It's likely inefficient to be doing the conversions inside LazyColumn and we should really be pre-filtering the list with val displayItems = remember(priceHistoryList) { priceHistoryList.map { } } or something, but I'm just going to hack it for now
            val dateFormatter = remember(locale, zoneId) {
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
                    .withZone(zoneId)
            }


            val timeFormatter = remember(locale, zoneId) {
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)
                    .withZone(zoneId)
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(priceHistoryDeltaList) { priceHistoryDelta ->
                    Box {
                        ItemSourceInfoHistory(
                            dataSet,
                            priceHistoryDelta,
                            dateFormatter,
                            timeFormatter
                        )

                        OverflowMenu(modifier = Modifier.align(Alignment.TopEnd)) { requestMenuClose ->
                                MyDropdownMenuItem(
                                    text = { Text("Edit as new price") },
                                    onClick = { requestMenuClose(); requestEditAsNew(priceHistoryDelta.priceHistory) }
                                )
                        }
                    }
                }
            }
        }
    }
}

// At least early in development, check() and require() would sometimes kill the app but without
// leaving a clear logcat trace, making it very hard to figure out what went wrong. I am not 100%
// sure I didn't get confused, I am struggling to reproduce this now. Talking to ChatGPT/Grok
// suggests this really does happen but I am not entirely convinced they're right. I created these
// replacements with more explicit logging and they did seem to help, so I guess there's no harm in
// continuing to use them. I'm just not certain they are necessary. It's possible that having the
// Log.e() occur *before* an exception is thrown increases the chances the log entry makes it to
// logcat.


inline fun myCheck(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        val msg = lazyMessage()
        val ex = IllegalStateException(msg) // same as check()
        Log.e("MyCheck", "FAILED CHECK: $msg", ex)
        throw ex
    }
}

inline fun myRequire(condition: Boolean, lazyMessage: () -> String) {
    if (!condition) {
        val msg = lazyMessage()
        val ex = IllegalArgumentException(msg) // same as require()
        Log.e("MyRequire", "FAILED REQUIRE: $msg", ex)
        throw ex
    }
}

data class CurrencyFormat(
    val decimalPlaces: Int,
    val prefix: String?,
    val suffix: String?,
    val validationRules: List<ValidationRule<String>>
)

// This is an extension function on DataSet rather than a top-level function taking a currency code
// because at some point a DataSet may contain custom currency formatting which overrides whatever
// the current locale says to do.
fun DataSet.createCurrencyFormat(locale: Locale): CurrencyFormat {
    val currencyInstance = Currency.getInstance(currencyCode)
    // currencyInstance will give us the number of decimal places, but it won't give us a
    // prefix or suffix to use - which we need for currency TextFields. So we ask it to
    // format a sample price and take the prefix and suffix from that.
    val numberFormat = NumberFormat.getCurrencyInstance(locale).apply {
        currency = currencyInstance
    }
    val sampleFormattedCurrency = numberFormat.format(1.0)
    Log.d(
        "MyApp",
        "sampleFormattedCurrency for $currencyCode is '$sampleFormattedCurrency'"
    )
    val (prefix, suffix) = splitAroundDigits(sampleFormattedCurrency)
    return CurrencyFormat(
        decimalPlaces = currencyInstance.defaultFractionDigits,
        prefix = prefix.trim().ifBlank { null },
        suffix = suffix.trim().ifBlank { null },
        validationRules = numericValidationRules(
            locale,
            allowDecimals = currencyInstance.defaultFractionDigits > 0,
            allowZero = false,
            maxDecimals = currencyInstance.defaultFractionDigits
        )
    )
}

fun getCurrencyDecimalPlaces(dataSet: DataSet) =
    Currency.getInstance(dataSet.currencyCode).defaultFractionDigits

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

// TODO: SHOULD THIS BE A COMPANION OBJECT FUNCTION OR SOMETHING?
fun <T> initialVersioned(initialValue: T): Versioned<T> =
    Versioned(version = -1L, value = initialValue)


@Composable
fun ErrorHighlightBox(
    visible: Boolean,
    borderWidth: Dp = 2.dp,
    offset: Dp = defaultErrorHighlightOffset,
    modifier: Modifier = Modifier,
    validationTarget: ValidationInputHandle,
    content: @Composable () -> Unit
) {
    var alpha = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            // Start animating from completely transparent.
            alpha.snapTo(0f)

            // Pulse alpha while we're supposed to be visible.
            while (visible) {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(1000, easing = LinearEasing)
                )
                alpha.animateTo(
                    // We don't animate down to 0% alpha, as it's kind of jarring having the box
                    // completely disappear.
                    targetValue = 0.1f,
                    animationSpec = tween(1000, easing = LinearEasing)
                )
            }
        } else {
            // Fade out smoothly once we're no longer animating.
            // ENHANCE: It would maybe be nice if we could always get to 1f *then* do this fade out
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
                // Draw an outline slightly larger than the content
                val borderWidthPx = borderWidth.toPx()
                val offsetPx = offset.toPx()
                drawRect(
                    color = borderColor,
                    alpha = alpha.value,
                    style = Stroke(width = borderWidthPx),
                    topLeft = Offset(-offsetPx, -offsetPx),
                    size = size.copy(
                        width = size.width + 2 * offsetPx, height = size.height + 2 * offsetPx
                    )
                )

            }
            .validationInputHandleBringIntoViewRequester(
                validationTarget,
                offset = offset + 2 * borderWidth
            )
    ) {
        content()
    }
}

data class PriceAnalysis(
    val augmentedPriceList: List<AugmentedPrice>,
    val priceClassificationThresholds: PriceClassificationThresholds?
)

data class AugmentedPrice(
    val basePrice: Price,
    val sourceName: String, // technically redundant but saves much id->name lookup faff
    val loyaltyPrice: Double,
    val ageDays: Long,
    val ageClass: AgeClass,
    val inflatedLoyaltyPrice: Double,
    val unitPrice: UnitPrice,
    val priceJudgement: PriceJudgement,
)

fun inflationAdjustedPrice(price: Double, ageDays: Long, priceAgeSettings: PriceAgeSettings): Double {
    if (ageDays < priceAgeSettings.stalePriceThreshold) {
        return price
    } else {
        // Note that inflation starts to apply only from stalePriceThreshold; the exponent here is
        // ageDays - stalePriceThreshold. We don't want to suddenly apply the previous
        // stalePriceThreshold days' worth of inflation the instant a price becomes stale.
        return price * (1.0 + priceAgeSettings.annualInflationPercent / 100.0).pow((ageDays - priceAgeSettings.stalePriceThreshold) / 365.25)
    }
}

enum class AgeClass {
    FRESH,
    STALE,
    ANCIENT
}

enum class PriceJudgement {
    NONE,
    GOOD,
    OK,
    BAD
}

// TODO: Should this be a member of PriceJudgement?? Or AugmentedPrice?
fun judgePrice(
    augmentedPrice: AugmentedPrice,
    priceClassificationThresholds: PriceClassificationThresholds?
): PriceJudgement {
    if (priceClassificationThresholds == null) {
        return PriceJudgement.NONE
    } else if (augmentedPrice.unitPrice < priceClassificationThresholds.good) {
        return PriceJudgement.GOOD
    } else if (augmentedPrice.unitPrice <= priceClassificationThresholds.bad) {
        return PriceJudgement.OK
    } else {
        return PriceJudgement.BAD
    }
}

// TODO: Should this be a companion function/constructor on AugmentedPrice or something like that? Or an extension function on Price?
fun augmentPrice(
    price: Price,
    source: Source,
    priceAgeSettings: PriceAgeSettings
): AugmentedPrice {
    val loyaltyPrice = price.price * source.loyaltyMultiplier
    // We use an integer ageDays as there's little value in working to sub-day resolution and it
    // will make the calculation a bit more repeatable/easy to follow for humans. If we add a screen
    // showing how the calculation was done, ageDays will not be constantly increasing slightly
    // every time it's shown.
    val ageDays = Duration.between(price.confirmedAt, Instant.now()).toDays()
    val inflatedLoyaltyPrice = inflationAdjustedPrice(loyaltyPrice, ageDays, priceAgeSettings)
    return AugmentedPrice(
        basePrice = price,
        sourceName = source.name,
        loyaltyPrice = loyaltyPrice,
        ageDays = ageDays,
        ageClass = if (ageDays < priceAgeSettings.stalePriceThreshold) {
            AgeClass.FRESH
        } else if (ageDays < priceAgeSettings.ancientPriceThresholdDays) {
            AgeClass.STALE
        } else {
            AgeClass.ANCIENT
        },
        inflatedLoyaltyPrice = inflatedLoyaltyPrice,
        unitPrice = UnitPrice.calculate(inflatedLoyaltyPrice, price.count, price.quantity),
        priceJudgement = PriceJudgement.NONE
    )
}

data class PriceClassificationThresholds(
    val good: UnitPrice,
    val bad: UnitPrice
)

fun quantile(sortedValues: List<Double>, q: Double): Double {
    myRequire(q in 0.0..1.0) { "Expected q in [0, 1] but got $q" }

    // We could return null for empty, but in reality we don't expect this to happen and it feels
    // better to avoid making the result nullable.
    myRequire(sortedValues.isNotEmpty()) { "Expected non-empty list" }

    // It's slightly inefficient to be checking sortedValues is sorted every time, but for our tiny
    // lists it is very cheap and it might catch a bug causing invalid results to be generated.
    myRequire(
        sortedValues.zipWithNext()
            .all { (a, b) -> a <= b }) { "Expected sortedValues to be sorted but got $sortedValues" }

    val doubleIndex = q * (sortedValues.size - 1)
    val lowerIndex = doubleIndex.toInt()
    // min() here is just paranoia in case of floating point imprecision.
    val upperIndex = kotlin.math.min(ceil(doubleIndex).toInt(), sortedValues.size - 1)
    val fractionalIndex = doubleIndex - lowerIndex
    return sortedValues[lowerIndex] * (1 - fractionalIndex) + sortedValues[upperIndex] * fractionalIndex
}


// TODO: This code feels a bit awkward somehow, maybe the unit price calculation code needs
// refactoring and maybe augmentPrice should be inlined as this is its only caller and that *might*
// help. It also feels like we're having to pass far too much random stuff in as parameters.
fun analysePrices(
    dataSet: DataSet?,
    priceList: List<Price>,
    sourceList: List<Source>,
    priceAgeSettings: PriceAgeSettings,
): PriceAnalysis {
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

    var augmentedPriceList = priceList.mapNotNull { price ->
        // I don't think we can have a Price but not the corresponding Source, but we play it safe
        // just in case.
        sourceById[price.sourceId]?.let { source ->
                augmentPrice(price, source, priceAgeSettings)
        }
    }.sortedBy { it.unitPrice }

    // augmentPrice() should have generated all unit prices using the base unit, but let's check
    // as otherwise recentEnoughPriceList (which discards the denominators) will be meaningless.
    val unitPriceDenominator = augmentedPriceList.first().unitPrice.denominator
    myCheck(augmentedPriceList.all { it.unitPrice.denominator == unitPriceDenominator }) {
        "Not all augmentedPriceList values have identical unitPrice denominators"
    }

    val recentEnoughPriceList = augmentedPriceList.mapNotNull { augmentedPrice ->
        if (augmentedPrice.ageClass == AgeClass.ANCIENT) {
            null
        } else {
            augmentedPrice.unitPrice.numerator
        }
    }

    Log.d("MyApp", "recentEnoughPriceList $recentEnoughPriceList")
    val priceClassificationThresholds = if (recentEnoughPriceList.size <= 2) {
        null
    } else {
        // This isn't necessarily the ideal way to classify things but it's what I settled on after
        // much discussion with ChatGPT and thinking about it. We have so little data that we can't
        // go full on stats nerd. We calculate a buffered IQR [Q1*(1-k), Q3+(1+k)] and use that to
        // classify prices as good, OK or bad. The idea is not to obsess over small price variations
        // when making our recommendation. Note that we only do this if we have at least three
        // recent enough prices to work with. There are numerous flaws with this, but we're just
        // trying to give an at-a-glance recommendation which is reasonably trustworthy. Users can
        // obviously see the actual list of unit prices by store and judge from that if they prefer.
        val lowerQuartile = quantile(recentEnoughPriceList, 0.25)
        val upperQuartile = quantile(recentEnoughPriceList, 0.75)
        val k = 0.1 // ENHANCE: Make this configurable in settings? May be too "advanced"...
        PriceClassificationThresholds(good = UnitPrice(lowerQuartile * (1 - k), unitPriceDenominator), bad = UnitPrice(upperQuartile * (1 + k), unitPriceDenominator))
    }
    augmentedPriceList = augmentedPriceList.map { augmentedPrice ->
        // We classify prices even if they aren't fresh. This seems best, they are marked as stale
        // so the user can tell, but it's not unreasonable to offer a judgement.
        // ENHANCE: Possibly we should not offer a judgement on ancient prices?
        augmentedPrice.copy(
            priceJudgement = judgePrice(
                augmentedPrice,
                priceClassificationThresholds
            )
        )

    }
    return PriceAnalysis(augmentedPriceList, priceClassificationThresholds)
}

// TODO: ChatGPT code, not tried to understand yet - it may be there's an easier way, or this may be buggy for all I know, or maybe I should just use this approach and not wrap it in this composable
@Composable
fun OnAppLifecycleEvent(
    onEvent: (Lifecycle.Event) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            onEvent(event)
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

// TODO: ChatGPT/Grok magic
object UserPreferencesSerializer : Serializer<UserPrefs.UserPreferences> {
    override val defaultValue: UserPrefs.UserPreferences = UserPrefs.UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPrefs.UserPreferences {
        try {
            return UserPrefs.UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserPrefs.UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}

// TODO: ChatGPT magic
val Context.userPreferencesStore: DataStore<UserPrefs.UserPreferences> by dataStore(
    fileName = "user_prefs.pb",
    serializer = UserPreferencesSerializer
)

/* TODO TEMP TEST CODE FOR MEASUREDVALUE
val foo = MeasuredValue(5.0, MeasurementUnit.KG)
val bar = MeasuredValue(2.3, MeasurementUnit.ML)
val quux = bar.to(MeasurementUnit.FLOZ)
Log.d("MyApp", quux.toString())
var baz = foo + barq
Log.d("MyApp", baz.toString())
*/

// ENHANCE: I have completely ignored "unlikely" errors (like exceptions being thrown when accessing
// the database) in most of this code - what can/should we do about this? I suspect most such errors
// are basically unrecoverable and it's more-or-less OK if the process just dies, but I'm not sure
// and we may be able to do better.

// TODO: Move this into coding-notes.md once I'm sure I am respecting it and that it is in fact
// appropriate etc.
//
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

// TODO: Eventually will need to remove misc Log.d() lines and/or replace them with permanent
// well-thought-out ones if that is not inefficient.

// TODO: Should we remember current product and source (remember they *may* be null anyway) for each
// data set? TODONOW

// ENHANCE: Maybe I should have a settings option which completely hides or just disables all the
// "delete" buttons. Users can turn that off if it makes them feel safer. We could possibly, if it
// isn't a UI nightmare, allow delete to be enabled for the next 10 minutes or something, then
// automatically re-disable. My thinking here is deletes could be very destructive of valuable data
// and in general you do not really want to delete stuff, unless you manage to add something
// completely junky rather than just adding something with a typo and needing to edit it to fix it,
// or cancelling the add before you finish it. We could also make the settings option tri-state,
// with an intermediate setting (which could perhaps even be the default) where delete buttons are
// shown/enabled (whatever I think best) for "non scary" deletes (product X is in the database *but
// no price data is attached* etc) but hidden/disabled for "scary" deletes (price data exists which
// would get cascade deleted).

// ENHANCE: M3 recommends using a "container transform pattern" to transform FAB into a full-screen
// dialog. Not sure if I can or should do this, but might be worth trying. (Do remember that as
// noted elsewhere, my "full screen dialogs" are actually full screens in their own right and I
// don't have enough hair to switch away from that, especially not just to make an animation work.
// The animation may not depend on being a "true dialog", of course.) I do wonder - not seen
// anything in docs - if this also suggests some kind of "expansion" animation should happen from
// the clicked-on source/item/dataset into the full screen dialog to edit it. Currently the code is
// doing the "standard" full screen dialog slide in from bottom animation anyway. (I had some
// discussions with LLMs about what to do for edit not add cases, where you click on a list item to
// open the edit dialog - from a UI design perspective, not how/ease of implementation. Using the
// standard slide in transform over a "container transform" was favoured 2:1 here. See how I feel
// later, and I'm far from confident I can do the FAB container transform anyway and that would
// definitely be the thing to try first (as it *is* called out in MD3 specs).)

// ENHANCE: If/when we have some kind of auto-backup or export state thing, it might be nice to hook
// this into delete operations (perhaps just cascading ones???) and auto-backup before deleting.
// Minor concern here if the user is doing a lot of deletions that we don't end up with lots of
// auto-backups, we could just possibly try to be clever and only do this if we haven't done an
// auto-backup within the last hour or so. This limits the window of data loss while keeping backup
// volume down.

// TODO: ErrorHighlightBoxes and their offsets and the general layout of the forms they highlight is
// probably a bit inconsistent and could do with a review.

// TODO: General Kotlin point which may simplify my code - unlike in say C++, you can apparently
// "call methods" on nulls, e.g. stringvariable.orEmpty().

// ENHANCE: Is it worth worrying about the case where the user is editing (say) an item, changes it
// name completely ("Coffee" -> "Eggs"), forgets about having done that and then hits "Delete"
// thinking they are deleting "Eggs" when they are really deleting "Coffee" (and all associated
// data)? This is probably sufficiently implausible it's not a big deal. We could find some way to
// show the "original" name on screen as a kind of reminder, but that might be confusing or clunky.
// Possibly the delete confirmation dialog could show the original name and the edited name if both
// are different, but that could be confusing if the names have just had cosmetic tweaks. Moving the
// delete operation onto the "list" screen rather than the individual item edit dialog would help
// with this, but I really don't want delete to be implemented on the list as it is a very rare and
// potentially devastating operation.

// TODO: May want to semi-formally document that "state" for a screen is "what's in the screen's
// view model" (and arguably also in remembered stuff in composable etc), while "content" is what
// gets passed in from the "caller" via the sharedviewmodel mechanism. This may help me feel better
// and be more consistent about naming variables functions around the whole sharedviewmodel thing
// and also the resulting structure inside the fooscreenviewmodel.

// ENHANCE: We should probably implement a "recycle bin" type delete for data set/item/source - have
// a "deleted" flag on all the tables, and when something is deleted we set that. (We would not
// cascade-set this if we delete a data set; being unable to select the data set would effectively
// hide the items/sources in it anyway, and no point forcing extra work to cacade set or unset on
// delete.) Most queries would then simply have a "deleted=false" condition to ignore deleted
// things. We can then undelete (subject to verifying names are still unique - deleted things would
// not count towards uniqueness checks, so you could create a potential duplicate after deleting
// something) simply by clearing the deleted flag. This is a UI faff because it means three-ish
// screens to select things to undelete, and maybe some other facility somewhere else to purge
// some/all things in the "recycle bin" for real. But it probably is the way to go long term.

// ENHANCE: The list of prices for product across stores at bottom of home screen should probably
// have some way of expanding in place or (more likely) opening a new screen showing a read-only
// explanation of how the augmented price was arrived at (store level discounts, pseudo-inflation
// penalties, etc). This screen should probably start with the raw shelf unit price in absolute
// form, then have subsequent lines like "Inflation adjustment +$0.04" or "Loyalty discount (5%)
// -$0.03" with a final total at the end.

// Note to self: I used scaling 61% when importing app-icon-4.svg as a new image asset for the icon.

// ENHANCE: It might be nice to offer an "are you sure? this is x% more/less than before" type
// confirmation dialog when saving a price change where the (unit price? pack price? pack size?) has
// changed by more than a threshold, to help catch typos early.

// ENHANCE: I don't think it's that important, but some history editing support might be nice:
// - maybe allow the notes field to be edited in history entries ("this was a price typo")
// - maybe allow history entries to be outright deleted (expunge mistakes completely)

/* TODO: When I add translation, I should probably allow the translator to indirectly specify
   keyboard hints - e.g. product name in English probably wants Words, but in Spanish probably None.
   Grok sketched out:
val capString = stringResource(R.string.grocery_capitalization)
val capitalization = when (capString) {
    "sentences" -> KeyboardCapitalization.Sentences
    "words" -> KeyboardCapitalization.Words
    else -> KeyboardCapitalization.None
}
        with English using res/values/strings.xml:
        <string name="grocery_capitalization">words</string>
        and Spanish using:
        <string name="grocery_capitalization">none</string>
*/

// ENHANCE: I sometimes start typing the name of a product into the search box at the top of the
// "edit products" screen, realise it's not there and want to add it. It might be worth (maybe gated
// by a setting) copying the search string from that screen into the name field on the add product
// screen when you click the add button in this case. This would save having to re-type it.

// ENHANCE: I perhaps ought to be more aggressive at forcing focus into text fields, e.g. when
// editing a product/source/dataset. I think there is probably an argument for *not* forcing this
// when using the "product list" screen with a search box, because the user might want to just
// scroll the list, but for the edit screens the user is going to want to edit something. It may be
// the best compromise to only do this if it is a brand new something though, as if there is already
// data, the user may not want to edit the name, which comes first and is probably what we'd force
// focus on to. And it may in practice just be best not to force it. No idea what is "standard" or
// "advised" by MD3 or general Android conventions, a chat with an LLM might offer some perspectives
// even if they're not guaranteed to be correct. It might also be a good idea to have a setting
// which controls whether we force focus onto the search control on the product selection dialog -
// some people (including me?) might nearly always want to do a text search rather than scrolling
// the list to browse, and in that case the experience is nicer if you can avoid needing to tap to
// focus.

// ENHANCE: It might be a good idea to have a setting which controls whether the price history view
// elides diffs which are nothing but confirmation date changes. And/or have a tick box on the
// screen itself to toggle this, maybe with the initial value of that tick box being set based on
// a setting. Or maybe we'd just persist the value of that tick box to a saved preference and avoid
// complicating the settings with it.

// ENHANCE: Some sort of feature for showing best ever price for a product across all stores, or
// probably better some variant on this where we show some (not too stats nerdy) "best price range"
// for data over the last n days for a product. Where I'm going with this (though there may be other
// uses) is that for products I buy rarely and on demand and am relatively price sensitive for (e.g.
// beer), I sometimes find myself reluctant to update the current price away from a temporary good
// offer price, because I probably won't be buying it tomorrow (so I don't "need" the correct price
// shown, although logically that's how the app should work/be used) and I want to record the good
// price so I know it's good when I see it again. If there was an easy way to see "best price over
// last n days" (actually showing this for say n=30/60/90/180/365 simultaneously) might not be a bad
// way to show the "spread" in a non-stats-nerd and useful way), I wouldn't feel ths reluctance to
// update the price.

// ENHANCE: A standalone unit (price) converter, although in some ways it would be nice (but not
// sure Android really has this sort of thing) if it could pop up nicely "with" other screens. But
// something where you can enter a price in one unit and have it show the unit price in any
// specified unit, a bit like a no-db version of the "Store price" card on the home screen. Maybe
// with the option to just do unit conversions (454g->lb) with no price. And maybe some sort of
// semi-persistent "printing tape" and you can press a button to "print" the current conversion onto
// it for reference, if you want to compare a few things ad-hoc without having to remember (or have
// every single thing you type "printed" and clogging up the screen). The idea being that if you're
// evaluating a different product variant to the one you already have at this store, you might want
// to explore this without relying on a unit price (if any) shown on the shelf without actually
// updating the db and finding the new price is worse. I'd envisage this being available via the
// overflow menu at top right of home screen. I'd imagine the three button metric/imperial/US
// customary selector from the dataset configuration being shown on this screen, initialised with
// the current dataset configuration, so you can choose which units appear in the dropdowns.

// TODO: Should we allow empty strings when adding/editing a "count" for a price and treat that as 1
// in the database? And/or should we default count to 1 rather than an empty string when adding a
// brand new price rather than editing an existing one?

// ENHANCE: On an emulated Android 11 phone, there is a strange pink tinge to the app. The home
// screen background is pinkish, but beyond LLMs burbling at me about emulator bugs, I can't see why
// that's relevant - there is not supposed to be any Material You auto-theming based on this on
// Android 11. LLMs also babble about my "default background" being translucent and/or a Material 2
// theme being used but none of their suggestions fixed this in the emulator. I don't have a real
// Android 11 device to test on and I have zero confidence in what the LLMs are telling me here. I
// have reverted all the temporary changes the LLMs had me make and will wait for feedback from real
// users (if any) before attempting to address this.
// TODO: I'm not actually sure this isn't just my imagination, come back to it.

// ENHANCE: Add a settings option which allows toggling between explicit themes, e.g. at a minimum
// light/dark/system. It may be - really not sure - only newer Android versions with Material You
// *have* a concept of a system theme in a way that's relevant to our app, in which case we might
// want to hide or grey out the "system" option on these versions. Need to think this through at the
// time and find out what's normal and what the possibilities are.

// TODO: I need to carefully test things around deleting prices and adding new ones via main GUI
// and "reverting" to old ones via history. A quick test now suggests this is working but at one
// point I did end up with the same store appearing multiple times in the price comparison on the
// home screen for a single item. I suspect this was caused by bugs during development but it's
// hard to be sure. Since then I have added a unique index on (item_id, source_id) so any remaining
// bugs here (which I am not sure exist) will probably trigger an error on database writes rather
// than breaking in precisely this way, but I still need to test to make sure all is working OK. I
// didn't necessarily test this all that thoroughly to start with but the most likely source of
// breakage is the fact that when "restoring" a historical price after a deletion, there is no
// "current" price ID to update, we are instead inserting a new price with a new ID.

// ENHANCE: It is possible that using SQLDelight would simplify the database queries. In particular,
// it may avoid the problem where Room flows are not clearly tagged with the query parameters that
// originated them, which I think is responsible for some of my data flow complexity in
// HomeViewModel.

// TODO: At some point I should apply the spotless auto-formatting, but that will obviously
// break diffs so I should be careful when I do it - maybe when the code is very stable and
// shortly before release?

/* TODO: Temp copy from ChatGPT for possible lightweight-ish subpackage structure
com.example.myapp/
│
├── MainActivity.kt
│
├── ui/
│   ├── theme/          # (standard Compose auto-generated package)
│   ├── components/     # optional: reusable composables, dialogs, etc.
│   ├── screens/        # optional: one file per screen if you have >1
│
├── data/
│   ├── models/         # your entity + domain objects
│   └── maybe local db or repository (if needed)
│
└── util/ (optional)    # small helpers, extensions, etc.

but also:

com.example.myapp/
│
├── MainActivity.kt
│
├── ui/
│   ├── theme/
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   └── settings/
│       ├── SettingsScreen.kt
│       └── SettingsViewModel.kt
│
├── data/
│   └── models/

*/

// TODO: We maybe want some kind of tie break on alphabetical order of sourceName for AugmentedPrice
// comparisons - yes it's floating point, but it's perfectly possible the numerator and denominator
// of the division are identical across two stores, and we want consistent ordering of sorted
// results
