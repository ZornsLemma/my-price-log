@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.composetutorial // TODO: change this!

import com.example.composetutorial.ui.screens.editdataset.EditDataSetViewModel
import com.example.composetutorial.ui.screens.edititem.EditItemViewModel
import com.example.composetutorial.ui.screens.editsource.EditSourceViewModel
import com.example.composetutorial.ui.components.ErrorHighlightBox
import com.example.composetutorial.ui.screens.home.HomeViewModel
import androidx.compose.ui.semantics.contentDescription
import com.example.composetutorial.ui.defaultValidationMessageDelayMillis
import com.example.composetutorial.ui.spinnerDelayMillis
import com.example.composetutorial.ui.fullScreenDialogHorizontalBorder
import com.example.composetutorial.ui.fullScreenDialogVerticalBorder
import com.example.composetutorial.ui.maxNotesLength
import com.example.composetutorial.ui.maxNavigationDrawerWidth
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
import android.content.res.Resources
import android.database.sqlite.SQLiteDatabase
import android.icu.text.Collator
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.navigation.compose.rememberNavController
import android.os.Bundle
import android.os.LocaleList
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
import androidx.annotation.AnyRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
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
import com.example.composetutorial.debug.DebugFlags
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
import com.example.composetutorial.models.DataSetUnitPreferences
import com.example.composetutorial.models.toEditable
import com.example.composetutorial.ui.AppNavigation
import com.example.composetutorial.ui.bulletPoint
import com.example.composetutorial.ui.components.ItemWithDropdown
import com.example.composetutorial.ui.components.LabeledItem
import com.example.composetutorial.ui.components.LabeledItemWithDropdown
import com.example.composetutorial.ui.components.MyExposedDropdownMenuBox
import com.example.composetutorial.ui.components.rememberValidationInputHandle
import com.example.composetutorial.ui.components.requestUserAttention
import com.example.composetutorial.ui.components.validationInputHandleBringIntoViewRequester
import com.example.composetutorial.ui.components.validationInputHandleFocusRequester
import com.example.composetutorial.ui.copyrightSymbol
import com.example.composetutorial.ui.defaultErrorHighlightOffset
import com.example.composetutorial.ui.emDash
import com.example.composetutorial.ui.listItemHorizontalPadding
import com.example.composetutorial.ui.maxDataSetNameLength
import com.example.composetutorial.ui.maxDecimalLength
import com.example.composetutorial.ui.maxItemNameLength
import com.example.composetutorial.ui.maxSearchLength
import com.example.composetutorial.ui.maxSourceNameLength
import com.example.composetutorial.ui.menuLeftPadding
import com.example.composetutorial.ui.menuRightPadding
import com.example.composetutorial.ui.nonBreakingSpace
import com.example.composetutorial.ui.oneLineListItemHeight
import com.example.composetutorial.ui.screenBorder
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
import java.text.Normalizer
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.concurrent.Executors
import kotlin.collections.map
import kotlin.math.ceil
import kotlin.math.pow
import com.example.composetutorial.models.AppDatabase
import com.example.composetutorial.models.DB_NAME
import com.example.composetutorial.models.DB_VERSION
import com.example.composetutorial.ui.screens.editprice.EditPriceViewModel
import com.example.composetutorial.ui.screens.settings.SettingsViewModel


fun getDefaultUnitFamilies(locale: Locale): Set<UnitFamily> = when (locale.country.uppercase()) {
    // ChatGPT suggests it's common to have dual metric and US customary labelling in US
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




suspend fun populateDemoData(repository: Repository, context: Context) {
    // ENHANCE: We could pick one of IMPERIAL or US_CUSTOMARY based on the current locale, but in
    // practice we just want to show we support multiple units, and it isn't as if a native US
    // customary user is going to get too confused (if they even notice) that "pint" (for example)
    // is imperial here - it's just demo data.
    // ENHANCE: We could add some demo products which are (fake) branded products rather than
    // generic categories, as this is a legitimate thing to do where the user is brand-sensitive.
    // It probably doesn't really matter though.
    val currency = Currency.getInstance(Locale.getDefault())
    // The demo data uses 2 decimal places so we scale it by currencyMultiplier when inserting so we
    // have unrealistic but at least workable prices for currencies like JPY. The prices aren't
    // meant to be realistic anyway.
    val currencyMultiplier = 10.0.pow(2 - currency.defaultFractionDigits)
    val dataSetId = repository.updateOrInsertDataSet(
        DataSet(
            name = context.getString(R.string.demo_groceries_data_set_name),
            currencyCode = currency.currencyCode,
            allowMetric = true,
            allowImperial = true,
            allowUSCustomary = false,
            notes = context.getString(R.string.demo_groceries_data_set_notes),
        )
    )
    val itemIdGroundCoffee = repository.updateOrInsertItem(
        Item(
            dataSetId = dataSetId,
            name = context.getString(R.string.demo_groceries_item_name_coffee_ground),
            defaultUnit = MeasurementUnit.G,
            allowMultipack = false,
            notes = ""
        )
    )
    val itemIdWholeMilk = repository.updateOrInsertItem(
        Item(
            dataSetId = dataSetId,
            name = context.getString(R.string.demo_groceries_item_name_milk_whole),
            defaultUnit = MeasurementUnit.L,
            allowMultipack = false,
            notes = "",
        )
    )
    val itemIdTeabags = repository.updateOrInsertItem(
        Item(
            dataSetId = dataSetId,
            name = context.getString(R.string.demo_groceries_item_name_teabags),
            defaultUnit = MeasurementUnit.EACH,
            allowMultipack = false,
            notes = "",
            )
    )
    val itemIdCola = repository.updateOrInsertItem(
        Item(
            dataSetId = dataSetId,
            name = context.getString(R.string.demo_groceries_item_name_cola),
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
            name = context.getString(R.string.demo_groceries_source_name_valuemart),
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = ""
        )
    )
    val sourceIdSuperiorStore = repository.updateOrInsertSource(
        Source(
            dataSetId = dataSetId,
            name = context.getString(R.string.demo_groceries_source_name_superiorstore),
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = ""
        )
    )
    val sourceIdGrandways = repository.updateOrInsertSource(
        Source(
            dataSetId = dataSetId,
            name = context.getString(R.string.demo_groceries_source_name_grandways),
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = ""
        )
    )
    // Newco deliberately has no prices to start with.
    repository.updateOrInsertSource(
        Source(
            dataSetId = dataSetId,
            name = context.getString(R.string.demo_groceries_source_name_newco),
            loyaltyType = LoyaltyType.NONE,
            loyaltyMultiplier = 1.0,
            notes = context.getString(R.string.demo_groceries_source_notes_newco)
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
            notes = context.getString(R.string.demo_groceries_notes_large_pack_own_brand),
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
            notes = context.getString(R.string.demo_groceries_notes_own_brand),
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
            notes = context.getString(R.string.demo_groceries_notes_soft_pack_own_brand),
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
        setSelectedDataSetId(context, dataSetId)
        setSelectedItemId(context, dataSetId, itemIdTeabags)
        setSelectedSourceId(context, dataSetId, sourceIdSuperiorStore)
}

// TODO MOVE - MAYBE INTO SourceModels.kt? Or is that a bit off as it's more of a UI concept?
// We could make things work so a null sourceId represents "None", but in practice it's more trouble
// than it's worth. (We could remove the UserPreferences map entry for the data set ID key to
// represent a null value being associated with it.)
val sourceIdNone = -1L
// Null item IDs are even less of a thing outside transitional async loading delays. Using a -1 here
// to avoid adding nullability to the selectedItemIdStateFlow is harmless and slightly reduces
// complexity.
val itemIdNone = -1L

suspend fun setSelectedDataSetId(context: Context, dataSetId: Long) {
    updateUserPreferences(context) { builder -> builder.setSelectedDataSetId(dataSetId) }
}

suspend fun setSelectedItemId(context: Context, dataSetId: Long, itemId: Long) {
    updateUserPreferences(context) { builder -> builder.putSelectedItemIdForDataSetId(dataSetId, itemId) }
}

suspend fun setSelectedSourceId(context: Context, dataSetId: Long, sourceId: Long) {
    updateUserPreferences(context) { builder -> builder.putSelectedSourceIdForDataSetId(dataSetId, sourceId) }
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
    }
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
            val demoDataInsertedKey = booleanPreferencesKey("demo_data_inserted")
            val demoDataInserted = dataStore.data
                .map { prefs -> prefs[demoDataInsertedKey] ?: false }
                .first()
            if (!demoDataInserted) {
                // To guard against the corner case where the demo data transaction succeeded but
                // we were killed before setting demoDataInsertedKey to true, we don't actually
                // do anything here if there are any data sets.
                if (repository.getAllDataSets().first().isEmpty()) {
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
                }

                dataStore.edit() { it[demoDataInsertedKey] = true }
            }
        }
    }
}


fun baseUnitForQuantityType(quantityType: QuantityType) = when (quantityType) {
    QuantityType.WEIGHT -> MeasurementUnit.G
    QuantityType.VOLUME -> MeasurementUnit.ML
    QuantityType.ITEM -> MeasurementUnit.EACH
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

// NB: We cannot rely on the database to order our results by name as it isn't locale-sensitive, so
// we have to sort the results in memory later. We could therefore omit ORDER BY clauses completely,
// (ENHANCE: and doing this later on would give a small performance/efficiency improvement) but
// instead we use a deliberately wrong ORDER BY DESC to make it obvious if we are failing to apply
// sorting to the results before showing them.


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

fun setSelectedDataSetIdAsync(context: Context, dataSetId: Long) {
    AppScope.io.launch {
        setSelectedDataSetId(context, dataSetId)
    }
}
fun setSelectedItemIdAsync(context: Context, dataSetId: Long, itemId: Long) {
    AppScope.io.launch {
        setSelectedItemId(context, dataSetId, itemId)
    }
}
fun setSelectedSourceIdAsync(context: Context, dataSetId: Long, sourceId: Long) {
    AppScope.io.launch {
        setSelectedSourceId(context, dataSetId, sourceId)
    }
}

sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>

    // Empty isn't currently used, but it feels like it might be a good option in some future case
    // so I'll keep it around for now. T could be a nullable type to represent this concept, but
    // depending on the precise situation Empty+a non-nullable T might be better.
    @Suppress("unused")
    data object Empty : LoadState<Nothing>

    data class Loaded<T>(val value: T) : LoadState<T>
}

fun <T> LoadState<T>.valueOrNull(): T? =
    when (this) {
        is LoadState.Loaded -> value
        else -> null
    }


// Returns a version of priceList where any price measurements which are expressed in units not
// supported by the data set are changed to use a unit that is supported. This avoids some awkward
// corner cases.
//
// For example:
// - the data set allows metric and imperial
// - the user enters a price of £1.83 for 4 imperial pints of milk
// - the user changes the data set to allow only metric
//
// At this point we could:
// - Forcibly update the database to remove the no longer valid units on the price table, changing
//   our milk price to £1.83 for 2273.045ml. This would be lossy, especially if the user changed the
//   data set by accident and reverts the change later.
// - Try to ensure that all the unit-handling parts of the application take care of this specially,
//   rather than assuming that all units they encounter are currently valid according to the data
//   set's definition. In this example we might continue to show the price measurement in pints,
//   perhaps even allowing the user to edit it in pints, until they change the unit (at which point
//   they would not be able to set it back to pints, as that is not a valid unit for the data set).
//   This is mostly fine, but it feels like an invitation to subtle bugs and crashes if I forget to
//   allow for this somewhere, as well as complicating the UI code for relatively little benefit.
//   - As an unlikely but particularly awkward case, suppose we tried to accommodate the existing
//     unit for as long as possible (as just described) and the user instead had changed imperial to
//     US customary on the data set. We would be showing a price for 6 imperial pints but the
//     display would just say "pints" and the user would have no way to know the price was not in US
//     customary pints. They would perhaps even be allowed to edit the price, not realising they are
//     entering a value in imperial pints in this one case. (Note that we very deliberately do not
//     attempt to qualify ambiguous units like "pints" on the main screens for readability and
//     usability. This ambiguity is handled by not allowing both imperial and US customary for the
//     same data set. It is only the preserved unit on the price and the change of data set units
//     which re-introduce the ambiguity.)
//
// What we actually do is use this function when we read the prices out of the database, to act as
// if we forcibly updated the database to keep things consistent but without actually making those
// changes in the database itself. This avoids hidden bugs where "invalid" units can legitimately
// occur in parts of the UI code, at the minor cost of forcing the user to see the prices with
// now-invalid units in an odd but valid unit (probably ml, in our pint example). It also avoids any
// ambiguity in interpreting the units shown to the user.
//
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
        if (!intersectionIsEmpty(price.quantity.unit.unitFamilies, relevantUnitFamilies)) {
            price
        } else {
            price.copy(quantity = price.quantity.to(MeasurementUnit.entries.first { replacementUnitFamily in it.unitFamilies && price.quantity.unit.quantityType == it.quantityType }))
        }
    }
}

// This function is annoyingly similar to sanitisePriceUnits() but I don't see any way to factor out
// the commonality which isn't worse than the repetition.
// TODO: Just possibly this should be an extension on DataSet??
fun sanitisePriceHistoryUnits(dataSet: DataSet, priceHistoryList: List<PriceHistory>): List<PriceHistory> {
    val relevantUnitFamilies = getRelevantUnitFamilies(dataSet)
    myCheck(relevantUnitFamilies.isNotEmpty()) { "Expected at least one relevant unit family for dataSet ${dataSet.id}" }
    val replacementUnitFamily = relevantUnitFamilies.first() // see sanitisePriceUnits() comment
    return priceHistoryList.map { priceHistory ->
        if (!intersectionIsEmpty(priceHistory.userUnit.unitFamilies, relevantUnitFamilies)) {
            priceHistory
        } else {
            priceHistory.copy(userUnit = MeasurementUnit.entries.first { replacementUnitFamily in it.unitFamilies && priceHistory.userUnit.quantityType == it.quantityType })
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
    val relativeTime = if (ageInSeconds < 60) stringResource(R.string.relative_time_span_string_now) else DateUtils.getRelativeTimeSpanString(
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
        // Note that the returned string appears to use a non-breaking space as a separator.
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
        // obvious if this code is coming into play while I am developing/testing. It probably
        // ought to be a non-breaking space, albeit this code path should never really be used.
        return "${dataSet.currencyCode}x${numberFormat.format(price)}"
    }
}


fun Double.roundTo(decimalPlaces: Int): Double {
    val factor = 10.0.pow(decimalPlaces)
    return kotlin.math.round(this * factor) / factor
}







data class PriceAgeSettings(val stalePriceThreshold: Int, val ancientPriceThresholdDays: Int, val annualInflationPercent: Int)

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



// rememberSyncedTextFieldValue() is a thin wrapper around a straight remember (as in the "val tfv
// =" line) which re-creates the TextFieldValue if the external string changes. (I think this is
// harmless/correct and probably reasonably good practice in general, but I'm not sure we actually
// have a case where the external string can change independently of our TextField.)
@Composable
fun rememberSyncedTextFieldValue(modelState: String): MutableState<TextFieldValue> {
    val tfv = remember { mutableStateOf(TextFieldValue(modelState)) }

    // If the model changes from the outside, resync tfv. We don't want to do this if it's the same,
    // as that would lose the additional cursor and selection state preserved inside tfv.
    if (tfv.value.text != modelState) {
        tfv.value = TextFieldValue(modelState)
    }

    return tfv
}

fun <T> intersectionIsEmpty(lhs: Set<T>, rhs: Set<T>) = !(lhs.any { it in rhs })

// TODO: Move into Measurement.kt? Really not sure...
fun areDifferentUnitFamilies(lhs: MeasurementUnit, rhs: MeasurementUnit) =
    intersectionIsEmpty(lhs.unitFamilies, rhs.unitFamilies)


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
    viewModel: GeneralEditScreenViewModel,
    coroutineScope: CoroutineScope,
    isSafeToPerform: suspend () -> Boolean,
    perform: suspend () -> Long?,
) {
    coroutineScope.launch {
        if (isSafeToPerform()) {
            viewModel.asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                Log.d("MyAppRGE", "runGeneralEditScreenOperation about to call perform")
                //throw IllegalStateException("TODO TEST")
                val id = perform()
                Log.d("MyAppQZ", "perform() returned id $id")
                // delay(5000) // TODO HACK - DONE AFTER PERFORM SO IT GETS A CHANCE TO SET SAVING/DELETING FLAG TO TRUE
                viewModel.asyncOperationStatus.update(AsyncOperationStatus.Success(id))
            } catch (e: Exception) {
                Log.d("MyAppRGE", "runGeneralEditScreenOperation caught exception")
                viewModel.asyncOperationStatus.update(AsyncOperationStatus.Error("runGeneralEditScreenOperation failed: ${e.toString()}"))
            }
        }
    }
}

// TODO: This is a very long function, can we split it up?
@Composable
fun GeneralEditScreen(
    viewModel: GeneralEditScreenViewModel,
    navController: NavHostController,
    title: @Composable () -> Unit,
    isDirty: () -> Boolean,
    validateForSave: suspend () -> Boolean,
    performSave: suspend () -> Long,
    onIdle: () -> Unit,
    requestClose: (Long?) -> Unit,
    content: @Composable () -> Unit
) {
    val saveStatus by viewModel.asyncOperationStatus.collectAsStateWithLifecycle()
    Log.d("MyAppRGE", "GeneralEditScreen saveStatus=$saveStatus")

    val isNotBusy = saveStatus.isNotBusy()
    var showConfirmDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialogMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showBusySnackbar by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var saving by rememberSaveable { mutableStateOf(false) }

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
        // We use buffer() here because we want to update() while we are already collecting; we
        // might get a deadlock otherwise.
        viewModel.asyncOperationStatus.events.buffer().collect { event ->
            when (event) {
                AsyncOperationStatus.Busy -> {
                    // We expect the operation to complete quickly so we don't want the visual distraction
                    // of a progress indicator appearing straight away. Let the progress indicator kick
                    // in after a short delay if we're still here waiting.
                    delay(spinnerDelayMillis)
                    // The state might not be busy any more, so check first before updating to avoid a race condition.
                    if (viewModel.asyncOperationStatus.state.value == AsyncOperationStatus.Busy) {
                        viewModel.asyncOperationStatus.update(AsyncOperationStatus.BusyForAWhile)
                    }
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        // We use buffer here because we want to update() in the error case while we are
        // already collecting; we get a deadlock otherwise.
        viewModel.asyncOperationStatus.events.buffer().collect { event ->
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
                    viewModel.asyncOperationStatus.update(AsyncOperationStatus.Idle)
                    Log.d("MyAppRGE", "set state to idle")
                    showErrorDialogMessage = event.message
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
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.content_description_close))
                    }
                },
                title = title,
                actions = {
                    TextButton(enabled = isNotBusy, onClick = {
                        // We could check isDirty here and just dismiss without saving if there's
                        // nothing to save, but it's probably best (given there's no history table
                        // which would get bloated) just to save regardless.
                        viewModel.saveAttempted.value = true
                        runGeneralEditScreenOperation(
                            viewModel = viewModel,
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
                            Text(stringResource(R.string.button_save))
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
        // I copied the wording of this dialog directly from a screenshot in the M3 documentation.
        AlertDialog(
            title = { Text(stringResource(R.string.title_discard_unsaved_changes)) },
            text = { Text(stringResource(R.string.message_unsaved_changes)) },
            onDismissRequest = { showConfirmDiscardDialog = false },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDiscardDialog = false
                }) { Text(stringResource(R.string.button_keep_editing)) }
            },
            confirmButton = {
                TextButton(onClick = { requestCloseDebounced(null) }) {
                    Text(
                        stringResource(R.string.button_discard)
                    )
                }
            },
        )
    }


    if (showErrorDialogMessage != null) {
        AsyncOperationErrorAlertDialog(onDismissRequest = { showErrorDialogMessage = null }, message = showErrorDialogMessage!!)
    }

    val messageBusyPleaseWait = stringResource(R.string.message_busy_please_wait)
    LaunchedEffect(showBusySnackbar, messageBusyPleaseWait) {
        if (showBusySnackbar) {
            // TODO: This compiles without the coroutine and launchedeffect already has a suspend body - so do we not need the launch? need to test. if this works, look for other places i may have needless launch blocks.
            //coroutineScope.launch {
                snackbarHostState.showSnackbar(messageBusyPleaseWait)
                showBusySnackbar = false
            //}
        }
    }
}

@Composable
fun AsyncOperationErrorAlertDialog(onDismissRequest: () -> Unit, message: String) {
    // We use an AlertDialog not a snackbar here. This is a local database save which is
    // failing so it is very unlikely to be transient. We also don't want the user
    // missing the snackbar, thinking the app is buggy ("I already saved, why didn't the
    // dialog close?") and then tapping the close icon without realising their changes
    // have not been saved. (If transient failure was a possibility - e.g. we needed to
    // perform network activity - there might be value in showing a snackbar, maybe with
    // a fallback to an AlertDialog if things keep failing.)
    AlertDialog(
        // The title and text are generic because a) this is not really expected to happen b) we
        // don't want to have to pass in strings saying whether this is a save or delete or
        // something else. The message is unlikely to be user-friendly, but if this fails the
        // chances are there's a bug rather than a transient failure anyway.
        title = { Text(stringResource(R.string.title_error)) },
        text = { Text(message) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) { Text(stringResource(R.string.button_ok)) }
        }
    )
}


@Composable
fun GeneralEditAndDeleteScreen(
    viewModel: GeneralEditScreenViewModel,
    navController: NavHostController,
    title: @Composable () -> Unit,
    isDirty: () -> Boolean,
    validateForSave: suspend () -> Boolean,
    performSave: suspend () -> Long,
    onIdle: () -> Unit,
    requestClose: (Long?) -> Unit,
    deleteConfirmationDetails: Triple<Boolean, @Composable () -> Unit, @Composable () -> Unit>?,
    performDelete: suspend () -> Unit,
    onDeleteConfirmDismissRequest: () -> Unit,
    content: @Composable (showDeleteSpinner: Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var deleting by rememberSaveable { mutableStateOf(false) }
    val saveStatus by viewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    GeneralEditScreen(
        viewModel = viewModel,
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

        val contentDescriptionWarning = stringResource(R.string.content_description_warning)
        AlertDialog(
            icon = if (isSimpleDelete) null else { { WarningIcon(contentDescription = contentDescriptionWarning) } },
            title = dialogTitle,
            text = dialogText,
            onDismissRequest = { onDeleteConfirmDismissRequest() },
            dismissButton = {
                TextButton(onClick = { onDeleteConfirmDismissRequest() }) { Text(stringResource(R.string.button_cancel)) }
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteConfirmDismissRequest()
                    runGeneralEditScreenOperation(
                        viewModel = viewModel,
                        coroutineScope = coroutineScope,
                        isSafeToPerform = { true },
                        perform = {
                            deleting = true
                            //delay(5000) // TODO HACK
                            //throw IllegalStateException("TODO")
                            performDelete()
                            // We return null since we don't want to change the selected entity on
                            // the home screen.
                            null
                        }
                    )
                }) { Text(stringResource(R.string.button_delete) /* TODO? Would only want to do this for cascading deletes, but even so I'm not sure I like it , color = MaterialTheme.colorScheme.error */) }
            },
        )
    }
}

@Composable
fun EditItemScreen(
    viewModel: EditItemViewModel,
    navController: NavHostController,
    requestClose: (newSelectedItemId: Long?) -> Unit
) {
    val uiContent = viewModel.uiContent

    val itemReferenceCount by viewModel.itemReferenceCountFlow.collectAsStateWithLifecycle(null)
    Log.d("MyApp", "itemReferenceCount $itemReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = itemReferenceCount == 0L
    val dialogTitle = stringResource(if (isSimpleDelete) R.string.title_delete_item else R.string.title_delete_item_and_prices)
    // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
    val dialogSubtitle = stringResource(if (isSimpleDelete) R.string.message_delete_item_no_associated_prices else R.string.message_delete_item_associated_prices)
    GeneralEditAndDeleteScreen(
        viewModel = viewModel.generalEditScreenViewModel,
        navController = navController,
        title = topAppBarTitle( if (viewModel.uiContent.editableItem.value.id == 0L) stringResource(R.string.title_add_item) else stringResource(
            R.string.title_edit_item
        ), viewModel.uiContent.dataSet.name),
        isDirty = { uiContent.editableItem.value != uiContent.originalItem },
        validateForSave = { viewModel.validateForSave() },
        performSave = { viewModel.performSave() /* ; throw IllegalArgumentException("TODO2") */ },
        onIdle = {},
        requestClose = requestClose,
        deleteConfirmationDetails = if (!showDeleteConfirmDialog) null else Triple(
            isSimpleDelete,
            { Text(dialogTitle) },
            { Text(dialogSubtitle) },
        ),
        performDelete = { viewModel.performDelete() },
        onDeleteConfirmDismissRequest = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(uiContent.editableItem.value.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        Log.d("MyApp", "nameValidationRules $nameValidationRules")
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions = KeyboardOptions(capitalization = keyboardCapitalization(R.string.keyboard_capitalization_item_name)),
            value = name,
            maxLength = maxItemNameLength,
            onValueChange = {
                name = it
                viewModel.setUIContentEditableItem(uiContent.editableItem.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value ?: emptyList(),
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditItemViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Probably can/should factor out a lot of this radio button stuff which I have just
        // copied and pasted from EditSourceScreen for now.

        // TODO: Can I put these string versions inside QuantityType or won't that play well with i18n?
        val options = listOf(
            Triple<QuantityType,String,String?>(
                QuantityType.ITEM,
                stringResource(R.string.label_sold_by_item),
                null // was "Per item or pack of items" but probably clearer without it
            ),
            Triple(QuantityType.WEIGHT, stringResource(R.string.label_sold_by_weight), null),
            Triple(
                QuantityType.VOLUME,
                stringResource(R.string.label_sold_by_volume),
                null,
            ),
        )
        var selectedOption = uiContent.editableItem.value.quantityType

        // ENHANCE: When we disallow changing "sold by" because there are prices for the product,
        // just maybe we should switch to displaying a disabled TextField or similar with a
        // supportingText instead of the radio buttons. I half suspect that might look ugly and be
        // confusingly different, but maybe it wouldn't.
        val radioButtonsEnabled = saveStatus.isNotBusy() && isSimpleDelete
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
                    stringResource(R.string.label_sold_by),
                    style = MaterialTheme.typography.titleSmall /* bodySmall */,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                //Spacer(modifier = Modifier.height(8.dp))
                options.forEach { (id, name, supportingText) ->
                    val clickableModifier = if (!radioButtonsEnabled) Modifier else Modifier.clickable {
                        viewModel.setUIContentEditableItem(
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
                            enabled = radioButtonsEnabled,
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
                if (!isSimpleDelete) {
                    SupportingText(
                        stringResource(R.string.supporting_text_sold_by_cant_be_changed),
                        isError = false,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                if (selectedOption != QuantityType.ITEM) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // TODO: RelevantUnit* here are sort of copy and paste from ItemSourceInfo and
                    // could possibly be factored out along with the code using them
                    val relevantUnitFamilies =
                        remember(viewModel.uiContent.dataSet) { getRelevantUnitFamilies(viewModel.uiContent.dataSet) }

                    val relevantUnitList =
                        remember(
                            viewModel.uiContent.dataSet,
                            viewModel.uiContent.editableItem.value.quantityType
                        ) {
                            getRelevantMeasurementUnits(
                                viewModel.uiContent.dataSet,
                                viewModel.uiContent.editableItem.value.quantityType,
                                includeDisplayOnly = false
                            )
                        }
                    val context = LocalContext.current
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
                                viewModel.setUIContentEditableItem(
                                    uiContent.editableItem.value.copy(
                                        defaultUnitIdByQuantityTypeOrdinal = defaultUnitIdByQuantityTypeOrdinal
                                    )
                                )
                            }
                        },
                        label = { Text(stringResource(R.string.label_default_unit)) },
                        supportingText = { Text(stringResource(R.string.supporting_text_default_unit)) },
                        items = relevantUnitList,
                        getDividerBetween = { previousItem, item -> areDifferentUnitFamilies(previousItem, item) },
                        getId = { it.id },
                        getItemText = { "${context.getString(it.fullName)} (${context.getString(it.symbol)})" },
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
                    text = stringResource(R.string.label_may_be_sold_in_multipacks),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    // TODO: If I change the "pack size" terminology elsewhere, need to change this too
                    text = stringResource(R.string.supporting_text_may_be_sold_in_multipacks),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                enabled = saveStatus.isNotBusy(),
                checked = uiContent.editableItem.value.allowMultipack,
                onCheckedChange = {
                    viewModel.setUIContentEditableItem(
                        uiContent.editableItem.value.copy(
                            allowMultipack = it
                        )
                    )
                })
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableItem.value.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUIContentEditableItem(uiContent.editableItem.value.copy(notes = it.text))
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
                        contentDescription = stringResource(R.string.content_description_delete)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.button_delete_item))
            }
        }
    }
}

@Composable
fun EditSourceScreen(
    viewModel: EditSourceViewModel,
    navController: NavHostController,
    requestClose: (Long?) -> Unit
) {
    val uiContent = viewModel.uiContent

    val sourceReferenceCount by viewModel.sourceReferenceCountFlow.collectAsStateWithLifecycle(null)
    Log.d("MyApp", "sourceReferenceCount $sourceReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = sourceReferenceCount == 0L
    val dialogTitle = stringResource(if (isSimpleDelete) R.string.title_delete_source else R.string.title_delete_source_and_prices)
    // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
    val dialogSubtitle = stringResource(if (isSimpleDelete) R.string.message_delete_source_no_associated_prices else R.string.message_delete_source_associated_prices)

    GeneralEditAndDeleteScreen(
        viewModel = viewModel.generalEditScreenViewModel,
        navController = navController,
        title = topAppBarTitle(if (viewModel.uiContent.editableSource.value.id == 0L) stringResource(R.string.title_add_source) else stringResource(
            R.string.title_edit_source
        ), viewModel.uiContent.dataSet.name),
        isDirty = { uiContent.editableSource.value != uiContent.originalSource },
        validateForSave = { viewModel.validateForSave() },
        performSave = { viewModel.performSave() /* ; throw IllegalArgumentException("TODO2") */ },
        onIdle = {},
        requestClose = requestClose,
        deleteConfirmationDetails = if (!showDeleteConfirmDialog) null else Triple(
            isSimpleDelete,
            { Text(dialogTitle) },
            { Text(dialogSubtitle) },
        ),
        performDelete = { viewModel.performDelete() },
        onDeleteConfirmDismissRequest = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(uiContent.editableSource.value.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        Log.d("MyApp", "nameValidationRules $nameValidationRules")
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_source_name)),
            value = name,
            maxLength = maxSourceNameLength,
            onValueChange = {
                name = it
                viewModel.setUIContentEditableSource(uiContent.editableSource.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value,
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditSourceViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Can I put these string versions inside LoyaltyDiscountType or won't that play well with i18n?
        val options = listOf(
            Triple(LoyaltyType.NONE, stringResource(R.string.loyalty_type_none), null),
            Triple(
                LoyaltyType.BONUS,
                stringResource(R.string.loyalty_type_bonus),
                stringResource(R.string.loyalty_type_bonus_supporting_text)
            ),
            Triple(LoyaltyType.DISCOUNT,
                stringResource(R.string.loyalty_type_discount),
                stringResource(R.string.loyalty_type_discount_supporting_text)
            )
        )
        var selectedOption = uiContent.editableSource.value.loyaltyType

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
                    stringResource(R.string.title_loyalty_scheme),
                    style = MaterialTheme.typography.titleSmall /* bodySmall */,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                //Spacer(modifier = Modifier.height(8.dp))
                options.forEach { (id, name, supportingText) ->
                    val clickableModifier = if (!saveStatus.isNotBusy()) Modifier else Modifier.clickable {
                        viewModel.setUIContentEditableSource(
                            uiContent.editableSource.value.copy(
                                loyaltyType = id
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
                            enabled = saveStatus.isNotBusy(),
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
                    Box(modifier = Modifier.padding(8.dp)) {
                        ValidatedNumericTextField(
                            value = loyaltyPercentage,
                            validationRules = viewModel.loyaltyPercentageValidationRules,
                            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
                            validationFlow = viewModel.saveValidationEvents,
                            validationFlowFieldId = EditSourceViewModel.EditableField.LOYALTY_PERCENTAGE,
                            numericTextFieldModifier = Modifier
                                .fillMaxWidth(),
                            // TODO: I can't help feeling this looks a bit confusing when it's
                            // empty, maybe it's just lack of a "%" or something.
                            label = { Text(stringResource(R.string.label_loyalty_scheme_reward)) },
                            suffix = { Text("%") },
                            onValueChange = {
                                loyaltyPercentage = it
                                viewModel.setUIContentEditableSource(
                                    uiContent.editableSource.value.copy(
                                        loyaltyPercentage = it.text
                                    )
                                )
                            },
                            enabled = saveStatus.isNotBusy(),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var notes by rememberSyncedTextFieldValue(uiContent.editableSource.value.notes)
        FilteredTextField(
            label = { Text(stringResource(R.string.label_notes)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            value = notes,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUIContentEditableSource(uiContent.editableSource.value.copy(notes = it.text))
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
                        contentDescription = stringResource(R.string.content_description_delete)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.button_delete_store))
            }
        }
    }
}

@Composable
fun <T> ValidatedNumericTextField(
    value: TextFieldValue,
    validationRules: List<ValidationRule<String>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean, // TODO default to = false or force explicit?,
    validationFlow: SharedFlow<T>,
    validationFlowFieldId: T,
    errorHighlightOffset: Dp = defaultErrorHighlightOffset, // TODO JUST MAYBE GET RID OF DEFAULT?
    baseValidatedTextFieldModifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean, // TODO: default to true or force explicit?
    numericTextFieldModifier: Modifier = Modifier,
) {
    ValidationErrorHighlightBox(
        value.text,
        validationRules,
        validationRulesKey,
        allowEmpty,
        validationFlow,
        validationFlowFieldId,
        errorHighlightOffset,
        modifier = baseValidatedTextFieldModifier
    ) { validationResult, interactionSource, validationInputHandle ->
        NumericTextField(
            modifier = numericTextFieldModifier.validationInputHandleFocusRequester(validationInputHandle),
            label = label,
            value = value,
            prefix = prefix,
            suffix = suffix,
            textStyle = textStyle,
            onValueChange = onValueChange,
            enabled = enabled,
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
fun <T, U> ValidationErrorHighlightBox(
    value: T,
    validationRules: List<ValidationRule<T>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean = false,
    validationFlow: SharedFlow<U>,
    validationFlowFieldId: U,
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
        offset = errorHighlightOffset,
        validationTarget = validationInputHandle,
        modifier = modifier
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            // TODO: We could possibly pass validationThing201 directly.
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

@Composable
fun <T> ValidatedFilteredTextField(
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
    ValidationErrorHighlightBox(
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

private enum class UnitPreferenceOption { METRIC, IMPERIAL, US_CUSTOMARY }
// TODO: Seems quite a long function, can we factor out (even single use) chunks for readability?
@Composable
fun EditDataSetScreen(
    viewModel: EditDataSetViewModel,
    navController: NavHostController,
    requestClose: (Long?) -> Unit
) {
    val uiContent = viewModel.uiContent

    val dataSetReferenceCount by viewModel.dataSetReferenceCountFlow.collectAsStateWithLifecycle(null)
    Log.d("MyApp", "dataSetReferenceCount $dataSetReferenceCount")

    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val saveStatus by viewModel.generalEditScreenViewModel.asyncOperationStatus.collectAsStateWithLifecycle()

    val isSimpleDelete = dataSetReferenceCount == 0L
    val dialogTitle = stringResource(if (isSimpleDelete) R.string.title_delete_data_set else R.string.title_delete_data_set_and_associated_data)
    // TODO: No delete can be undone, is it inconsistent to mention it in this case and not the other?
    val dialogSubtitle = stringResource(if (isSimpleDelete) R.string.message_delete_data_set_no_associated_data else R.string.message_delete_data_set_associated_data)

    GeneralEditAndDeleteScreen(
        viewModel = viewModel.generalEditScreenViewModel,
        navController = navController,
        title = { Text(if (uiContent.editableDataSet.value.id == 0L) stringResource(R.string.title_add_data_set) else stringResource(
            R.string.title_edit_data_set
        )) },
        isDirty = { uiContent.editableDataSet.value != uiContent.originalDataSet },
        validateForSave = { viewModel.validateForSave() },
        performSave = { viewModel.performSave(); /* throw IllegalArgumentException("TODO2") */ },
        onIdle = {},
        requestClose = requestClose,
        // TODO: WORDING FOR ALL OF THIS IS PARTICULARLY BAD AND NEEDS THOUGHT
        deleteConfirmationDetails = if (!showDeleteConfirmDialog) null else Triple(
            isSimpleDelete,
            { Text(dialogTitle) },
            { Text(dialogSubtitle) },
        ),
        performDelete = { viewModel.performDelete() },
        onDeleteConfirmDismissRequest = { showDeleteConfirmDialog = false },
    ) { showDeleteSpinner ->
        var name by rememberSyncedTextFieldValue(uiContent.editableDataSet.value.name)
        val nameValidationRules by viewModel.nameValidationRules.collectAsStateWithLifecycle()
        ValidatedFilteredTextField(
            label = { Text(stringResource(R.string.label_name)) },
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_data_set_name)),
            value = name,
            maxLength = maxDataSetNameLength,
            onValueChange = {
                name = it
                viewModel.setUIContentEditableDataSet(uiContent.editableDataSet.value.copy(name = it.text))
            },
            enabled = saveStatus.isNotBusy(),
            validationRules = nameValidationRules.value,
            validationRulesKey = nameValidationRules.version,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            singleLine = true,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.NAME
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TODO: Should we specify an offset of 4.dp here? Or should we perhaps just improve spacing?
        ValidationErrorHighlightBox(
            value = uiContent.editableDataSet.value.currencyCode,
            validationRules = viewModel.currencyValidationRules,
            allowEmpty = !viewModel.generalEditScreenViewModel.saveAttempted.value,
            validationFlow = viewModel.saveValidationEvents,
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

            MyExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .validationInputHandleFocusRequester(validationInputHandle),

                selectedId = if (uiContent.editableDataSet.value.currencyCode != "") uiContent.editableDataSet.value.currencyCode else null,
                onItemSelected = {
                    viewModel.setUIContentEditableDataSet(
                        uiContent.editableDataSet.value.copy(
                            currencyCode = it
                        )
                    )
                },
                enabled = saveStatus.isNotBusy(),
                label = { Text(stringResource(R.string.label_currency)) },
                items = currencyList.second,
                getId = { it.first },
                getItemText = { it.second },
                getDividerBetween = { firstItem, _ -> firstItem.first == currencyList.first },
                supportingText = textOrNull(
                    validationResult,
                    color = MaterialTheme.colorScheme.error,
                ),
                addBottomSpace = true,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ENHANCE: MD3 Expressive deprecates this and says we should use a connected button group,
        // but the relevant library version is still in alpha so I'll just do it the old MD3 way for
        // now with a segmented button group.
        ValidationErrorHighlightBox(
            value = uiContent.editableDataSet.value.unitPreferences,
            validationRules = viewModel.measurementSystemValidationRules,
            validationFlow = viewModel.saveValidationEvents,
            validationFlowFieldId = EditDataSetViewModel.EditableField.MEASUREMENT_SYSTEM
        ) { validationResult, interactionSource, scrollToFocusableHandle ->
            Text(
                stringResource(R.string.label_measurement_units),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // "US customary" doesn't fit (on my test "small" emulated phone) but based on a discussion
            // with ChatGPT "US units" is better for a casual user anyway, even if we could fit "US
            // customary".
            // TODO: Can/should I move these names into UnitPreferenceOption? enum class
            // UnitPreferenceOption(val name: String) { METRIC("Metric"), ... }? This would make it near
            // impossible to get them out of sync and might be cleaner. I don't know if this would
            // cause i18n problems though (Grok says it's fine), so maybe leave trying this until
            // later.
            val options = listOf(stringResource(R.string.label_metric),
                stringResource(R.string.label_imperial), stringResource(R.string.label_us_units)
            ) // must match UnitPreferenceOption
            // We *don't* call Modifier.validationFocusRequester() as you can't focus a segmented
            // button, and this will force a clear focus to happen on validation errors instead.
            MultiChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    val unit = UnitPreferenceOption.entries[index]
                    val oldUnitPreferences = uiContent.editableDataSet.value.unitPreferences
                    val checked = when (unit) {
                        UnitPreferenceOption.METRIC -> oldUnitPreferences.allowMetric
                        UnitPreferenceOption.IMPERIAL -> oldUnitPreferences.allowImperial
                        UnitPreferenceOption.US_CUSTOMARY -> oldUnitPreferences.allowUSCustomary
                    }
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onCheckedChange = {
                            // If imperial is selected, we force US customary to be deselected and
                            // vice versa. This allows us to use shorter names like "pt" instead of
                            // "pt (US)" without practical ambiguity.
                            val newUnitPreferences = when (unit) { // TODO INLINE newUnitPreferences?
                                UnitPreferenceOption.METRIC -> oldUnitPreferences.copy(allowMetric = it)
                                UnitPreferenceOption.IMPERIAL -> oldUnitPreferences.copy(allowImperial = it, allowUSCustomary = !it && oldUnitPreferences.allowUSCustomary)
                                UnitPreferenceOption.US_CUSTOMARY -> oldUnitPreferences.copy(allowUSCustomary = it, allowImperial = !it && oldUnitPreferences.allowImperial)
                            }
                            viewModel.setUIContentEditableDataSet(
                                uiContent.editableDataSet.value.copy(unitPreferences = newUnitPreferences)
                            )
                        },
                        checked = checked,
                        colors = SegmentedButtonDefaults.colors(),
                        icon = { SegmentedButtonDefaults.Icon(active = checked) },
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
            label = { Text(stringResource(R.string.label_notes)) },
            value = notes,
            keyboardOptions = KeyboardOptions(keyboardCapitalization(R.string.keyboard_capitalization_notes)),
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxNotesLength),
            onValueChange = {
                notes = it
                viewModel.setUIContentEditableDataSet(uiContent.editableDataSet.value.copy(notes = it.text))
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
                        contentDescription = stringResource(R.string.content_description_delete)
                    )
                }
                Spacer(Modifier.width(8.dp)) // TODO: Maybe 16.dp given spacing around measurement units?
                Text(stringResource(R.string.button_delete_collection))
            }
        }
    }
}

// TODO: Obviously "ValidationThing" isn't a good name
// TODO: It's a casual discussion not directly related to this but just FWIW ChatGPT uses
// "FieldValidation" or "ValidatedFieldState" as a name for this, maybe worth considering or riffing on.
// TODO: This is not a data class and I never even thought about it but although I find the
// distinction very confusing in practical Compose, FWIW ChatGPT was very clear that this *should
// not* be a data class (we might get away with it, but it would be prone to misuse if someone used
// copy() on it and that could break things, I think). Once I refactor this and feel more
// comfortable wit how the code works, it might be helpful to think about why (assuming ChatGPT is
// correct, but no reason to think it's not here) this should be and maybe even must be a "class"
// not a "data class", and perhaps have a more targeted discussion with an LLM about this, in order
// to clarify my mental model of Kotlin and/or Compose.
class ValidationThing(
    val interactionSource: MutableInteractionSource = MutableInteractionSource(),
    val validationResult: State<String?>
)

// TODO: Even ignoring that "ValidationThing" needs renaming (just maybe refactoring too, but not
// necessarily), this function is probably poorly named. It is not just "remembering" a
// validationthing, its key value is its reusable LaunchedEffect which actually carries out
// validation "on the fly" using the supplied rules.
// TODO: I am still re-figuring out how it works, but I think what this really is is a live
// validation rule "applier" (poor word). You give it some validation rules and then it will live
// validate a value (tweaking its behaviour based on focus (which it can monitor because the
// caller attaches the interactionSource it generates to a composable)) and feeding back a validation
// result for display.
// TODO: Discussion (possibly incorrect, but kind of convincing) with ChatGPT suggests that as the
// "main" purpose of this is the LaunchedEffect() not just "creating a state object the user cares
// about as such", it should not be called rememberFoo(). FWIW ChatGPT was using
// "ValidateFieldState" as the function name and "ValidatedFieldState" as the return value.
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
    // We create our own MutableInteractionSource which needs to be passed through to the TextField
    // we want to validate, so that we can track when that TextField is/is not focused.
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val validationResult = remember { mutableStateOf<String?>(null) }
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
    val context = LocalContext.current
    LaunchedEffect(context, value, validationRulesKey, allowEmpty, isFocused) {
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

        // Re-evaluate failedValidationRule. We copy it to the front of the list (it's harmless if
        // we end up with two copies of it) so that if multiple validation rules are failing, we
        // don't flip-flop between them - once a rule is reported as failing it is "sticky" until is
        // fixed.
        var shouldValidate = when (value) {
            is String -> !(allowEmpty && value.trim().isEmpty())
            else -> true // allowEmpty has no meaning for other types
        }
        failedValidationRule = if (shouldValidate) failedValidationRuleOrNull(
            listOfNotNull<ValidationRule<T>>(failedValidationRule) + validationRules,
            value
        ) else null

        Log.d("MyAppXQ", "failedValidationRule: $failedValidationRule")
        validationResult.value = failedValidationRule?.message?.asString(context)
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

data class ValidationRule<T>(val validate: (T) -> Boolean, val message: UiText)

fun <T> failedValidationRuleOrNull(validationRules: List<ValidationRule<T>>, value: T): ValidationRule<T>? {
    for (validationRule in validationRules) {
        if (!validationRule.validate(value)) {
            return validationRule
        }
    }
    return null
}

fun <T> validationRulesOk(validationRules: List<ValidationRule<T>>, value: T) =
    failedValidationRuleOrNull(validationRules, value) == null


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
    required: Boolean = false,
): List<ValidationRule<String>> {
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    val maxDecimalSeparators = if (allowDecimals) 1 else 0

    // Create a function to strip fluff like spaces and the grouping symbol if the user typed it in.
    val insignificantCharsRegex = "[^-0-9${Regex.escape(decimalSeparator.toString())}]".toRegex()
    fun sanitiseCandidate(candidate: String) = candidate.replace(insignificantCharsRegex, "")
    fun attemptedParse(candidate: String): Double? =
        sanitiseCandidate(candidate).replace(decimalSeparator, '.').toDoubleOrNull()

    return listOfNotNull(
        if (required) {
        ValidationRule({ it.trim().isNotEmpty() },
            UiText.Res(R.string.supporting_text_required)) } else null,

        ValidationRule(
            { it.count { char -> char == decimalSeparator } <= maxDecimalSeparators },
            // TODO: Just possibly we should not consider a single decimal separator with nothing
            // significant following it as violating "only whole numbers allowed".
            if (allowDecimals) UiText.Res(R.string.supporting_text_only_one_decimal_point_allowed) else UiText.Res(R.string.supporting_text_only_whole_numbers_allowed)
        ),

        if (maxDecimals != null) {
            // TODO: We could allow extra decimal places if they are all zeros? I could see arguments either way.
            ValidationRule({
                val parts = sanitiseCandidate(it).split(decimalSeparator)
                parts.size != 2 || parts[1].length <= maxDecimals
            }, UiText.PluralsRes(R.plurals.supporting_text_no_more_than_x_decimal_places_allowed, maxDecimals, listOf(maxDecimals)))
        } else {
            null
        },

        if (!allowZero) {
            // This message assumes you can't enter a negative value because input filtering rejects
            // '-'.
            ValidationRule({ attemptedParse(it) != 0.0 },
                UiText.Res(R.string.supporting_text_must_be_greater_than_zero))
        } else {
            null
        },

        if (maxValue != null) {
            ValidationRule( { (attemptedParse(it) ?: 0.0) <= maxValue },
                UiText.Res(
                    R.string.supporting_text_must_be_no_greater_than_x, listOf(maxValue)))
        } else {
            null
        },

        // This is a catch-all; in practice we expect to catch all problems before this, but we
        // don't want to have a string which can't be converted (which would cause an error on
        // trying to save) which the user hasn't been warned about.
        ValidationRule({ (!required && it.trim().isEmpty()) || attemptedParse(it) != null },
            UiText.Res(R.string.supporting_text_invalid_number)),
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

// Note that if maxLength is reduced, the generated onCandidateValueChange will disallow any edits
// to existing values which are over the new limit (which we previously valid). We could fix this by
// passing the old value into onCandidateValueChange and extending the condition here to "... ||
// it.length < oldValue.length", but unless/until this is a real concern, it feels better to avoid
// having to jump through hoops to make the old value available.
//
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
// parameter if it's not explicitly specified.
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
                    WarningIcon(contentDescription = stringResource(R.string.content_description_error))
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
fun WarningIcon(contentDescription: String) {
    Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.error
    )
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



private fun Context.isPhone(): Boolean = resources.configuration.smallestScreenWidthDp < 600

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
        // TODO: Since rotations are a good way to trigger recompositions in emulator for testing,
        // might be worth addig a debug build flag to always allow rotations.
        if (isPhone()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        // Target SDK >=35 directly enables edge-to-edge (see e.g. https://stackoverflow.com/questions/79018063/trying-to-understand-edge-to-edge-in-android). We don't particularly want this, but we can work with it so we don't try to fight it.
        // We call it here to be explicit. TODO: I am far from clear but you can pass some arguments to enableEdgeToEdge(), which may have some relevant effect on older and/or newer platforms. For now I will keep it simple but if there are nightmarish inconsistencies on older versions of Android this might be part of the puzzle.
        enableEdgeToEdge()

        if (DebugFlags.USE_STRICT_MODE) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog() // TODO .penaltyDeath() // TODO .penaltyLog()  // logs violations; you can also add .penaltyDeath() to crash on violation
                    .build()
            )
        }

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

data class SelectItemScreenUIContent(
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

        fun fromSavedState(handle: SavedStateHandle): SelectItemScreenUIContent? {
            val savedItemList: List<Item>? = handle[ITEM_LIST_KEY]
            val savedDataSet: DataSet? = handle[DATA_SET_KEY]
            if (savedItemList != null && savedDataSet != null) {
                Log.d("MyApp", "reconstructed SelectItemScreenUIContent")
                return SelectItemScreenUIContent(savedItemList, savedDataSet)
            } else {
                Log.d("MyApp", "couldn't reconstruct SelectItemScreenUIContent")
                return null
            }
        }
    }
}


data class SelectSourceScreenUIContent(
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

        fun fromSavedState(handle: SavedStateHandle): SelectSourceScreenUIContent? {
            val savedSourceList: List<Source>? = handle[SOURCE_LIST_KEY]
            val savedDataSet: DataSet? = handle[DATA_SET_KEY]
            if (savedSourceList != null && savedDataSet != null) {
                Log.d("MyApp", "reconstructed SelectSourceScreenUIContent")
                return SelectSourceScreenUIContent(savedSourceList, savedDataSet)
            } else {
                Log.d("MyApp", "couldn't reconstruct SelectSourceScreenUIContent")
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
    var editPriceScreenUIContent: EditPriceScreenUIContent? = null

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

    // TODO: Rename the following now they are just List<T>? not a UIContent structure? Or is the "UIContent" convention more valuable?
    var selectDataSetScreenUIContent: List<DataSet>? = null
    var selectItemScreenUIContent: SelectItemScreenUIContent? = null
    var selectSourceScreenUIContent: SelectSourceScreenUIContent? = null

    // TODO: The "doubling" in the next three functions is a temporary hack to show that we use the
    // initial list and then it gets replaced by the query results from the database. The map step
    // is because we use the IDs as keys on LazyColumn and if there are duplicate IDs it gets upset;
    // of course with real data there won't be duplicate IDs at all.

    fun setSelectDataSetScreenContent(uiContent: HomeScreenUIContent) {
        selectDataSetScreenUIContent =
            uiContent.dataSetList + uiContent.dataSetList.map { it -> it.copy(id = it.id * 1000) }
    }

    fun setSelectItemScreenContent(uiContent: HomeScreenUIContent) {
        selectItemScreenUIContent = SelectItemScreenUIContent(
            uiContent.itemList + uiContent.itemList.map { it -> it.copy(id = it.id * 1000) },
            uiContent.dataSet!!
        )
    }

    fun setSelectSourceScreenContent(uiContent: HomeScreenUIContent) {
        selectSourceScreenUIContent = SelectSourceScreenUIContent(
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

// From discussion with LLMs and doing my own web searches, we need something like the ICU string
// search service (https://unicode-org.github.io/icu/userguide/collation/string-search) to do really
// good substring searches in different languages. This is apparently quite large and Android
// doesn't include it by default, even though it has some ICU stuff. Further LLM discussion suggests
// that using this form of normalization is the usual compromise.
//
// Note that we deliberately use the root locale here, not the current locale. Apparently using (for
// example) the Turkish locale would break well-established expectations for Turkish users (the
// "Turkish I problem"). I think the key point here is that for *searching* we want to use very
// loose rules, not the linguistically correct ones for the locale, because users don't want to
// fiddle around press-and-holding keys to get the correct characters for ephemeral input. In the
// Turkish case users want to search for "istanbul" and have it match "İstanbul" even though they
// are different according to the locale, simply because it's a lot more convenient to type the
// former.
// TODO: It might be good to fake up some data with these strings (copy and paste into emulator!)
// and see if we are apparently getting this right.
fun String.normalizedForSearch() = Normalizer.normalize(this, Normalizer.Form.NFD)
        // Remove diacritics
        .replace("\\p{M}".toRegex(), "")
        // Remove all forms of apostrophes/quotes
        .replace("['’ʻʼʽʾˮˈˌʹʺ˝]".toRegex(), "")
        // Replace all remaining punctuation and symbols with spaces
        .replace("[\\p{Punct}\\p{Symbol}]".toRegex(), " ")
        // Collapse adjacent whitespace into single spaces
        .replace("\\s+".toRegex(), " ")
        // Trim leading/trailing whitespace
        .trim()
        // Lowercase using invariant rules to ensure case insensitivity
        .lowercase(Locale.ROOT)

class SelectItemViewModel(
    savedStateHandle: SavedStateHandle,
    getName: (Item) -> String,
    val uiContent: SelectItemScreenUIContent,
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

class SelectSourceViewModel(
    savedStateHandle: SavedStateHandle,
    getName: (Source) -> String,
    val uiContent: SelectSourceScreenUIContent,
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
    // TODO: This works and it is probably fine but note that for SelectItemViewModel we do actually
    // serialise, even though the general code doesn't require it. (We need it so we can pass a
    // DataSet through to EditItemScreen.)

    // This will *not* filter uiContent.initialList, but that's OK because we know the initial
    // filter doesn't exclude anything.
    // ENHANCE: We could persist the search string via savedStateHandle.
    val searchStringFlow = MutableStateFlow(TextFieldValue(""))

    @OptIn(ExperimentalCoroutinesApi::class)
    val dataFlow = combine(
        dataQuery.flatMapLatest { data -> /* TODO HACK delay(5000); */ flowOf(data) },
        searchStringFlow.map { searchString -> searchString.text.normalizedForSearch() }
    ) { data, normalizedQuery ->
        data.filter {
            getName(it).normalizedForSearch().contains(normalizedQuery)
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
    viewModel: GeneralSelectorViewModel<T>,
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
    val dataList by viewModel.dataFlow.collectAsStateWithLifecycle()
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
            TopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back)
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
            //
            // We don't need Modifier.verticalScroll(rememberScrollState()) here - probably because
            // of the LazyColumn - and in fact adding it causes a crash.
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = screenBorder)
        ) {
            // ENHANCE: We could show a warning icon and/or some supporting text if nothing matches
            // the substring, rather than just showing an empty list.
            if (showSearch) {
                val searchString by viewModel.searchStringFlow.collectAsStateWithLifecycle()
                FilteredTextField(
                    value = searchString,
                    onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxSearchLength),
                    onValueChange = { it -> viewModel.searchStringFlow.value = it },
                    label = { Text(stringResource(R.string.label_search)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.content_description_search),
                            // tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.content_description_clear_search_text),
                            modifier = Modifier.clickable { viewModel.searchStringFlow.value = TextFieldValue("") },
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
    val priceHistory: PriceHistory,
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
    // We could use Collator.PRIMARY to do this comparison (probably combined with squashing spaces and trim()-ing) but it's probably better to use normalizedForSearch() here.
    // TODO: Can/should we just return a single ValidationRule here which does an internal check
    // against a set pregenerated from existingNameList?
    val TODO0 =         ValidationRule<String>({ it.isNotEmpty() }, UiText.Res(R.string.supporting_text_required))
    val TODO1 = listOf(TODO0)

    val TODO2 = existingNameList.map { existingName ->
        val normalizedExistingName = existingName.normalizedForSearch()
        ValidationRule<String>(
            { candidateName -> candidateName.normalizedForSearch() != normalizedExistingName },
            UiText.Res(R.string.supporting_text_name_must_be_unique)
        )
    }
    return TODO1 + TODO2
}

// TODO: There is a huge amount of pseudo copy and paste in all the Edit*{Screen,ViewModel} stuff.
// Probably just going to accept it as I do the initial implementation so I don't tie myself in
// knots coping with generic attempts that don't quite match reality, but later on it would be good
// to see what can be factored out.

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

    val viewModel: VM = viewModel(
        viewModelStoreOwner = backStackEntry,
        factory = factory
    )

    content(viewModel)
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
        null
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw IOException(
            context.getString(
                R.string.message_failed_to_open_input_stream_for_uri,
                sourceUri
            ))

        // Validate the backup file.
        checkDatabaseRestoreCandidate(context, tempFile.path)

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

fun checkDatabaseRestoreCandidate(context: Context, dbPath: String) {
    val db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
    db.use { db ->
        val version = db.version
        if (version > DB_VERSION) {
            throw IllegalStateException(
                context.getString(
                    R.string.message_database_to_restore_too_new,
                    version,
                    DB_VERSION
                ))
        }

        // Sanity check this isn't a database from some other random app. We're not trying to guard
        // against malicious inputs here, just the user accidentally picking the wrong database.
        val expectedTables = listOf("data_set", "item", "price", "price_history", "source")
        expectedTables.forEach { table ->
            val cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?", arrayOf(table)
            )
            cursor.use { cursor ->
                val tableExists = cursor.moveToFirst()
                Log.d("MyAppRS", "tableExists $table: $tableExists")
                if (!tableExists) {
                    throw IllegalStateException(context.getString(R.string.message_the_database_to_restore_was_not_created_with_this_app))
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

fun analysePrices(
    priceList: List<Price>,
    sourceList: List<Source>,
    priceAgeSettings: PriceAgeSettings,
    locale: Locale,
): PriceAnalysis {
    if (priceList.isEmpty()) {
        return PriceAnalysis(emptyList(), null)
    }

    // It's important for our calls to quantile() below that augmentedPriceList is sorted on unit
    // price. We use sourceName as a tie breaker just to improve visual consistency of the results
    // when shown to the user.
    val sourceById = sourceList.associateBy { it.id }
    val collator = Collator.getInstance(locale).apply {
        strength = Collator.PRIMARY
    }
    var augmentedPriceList = priceList.mapNotNull { price ->
        // I don't think we can have a Price but not the corresponding Source, but we play it safe
        // just in case.
        sourceById[price.sourceId]?.let { source -> augmentPrice(price, source, priceAgeSettings) }
    }.sortedWith(
        compareBy<AugmentedPrice> { it.unitPrice }
            .thenComparing({ it.sourceName }, collator)
    )

    // augmentPrice() should have generated all unit prices using the base unit, but let's check
    // as otherwise recentEnoughPriceList (which discards the denominators) will be meaningless.
    val unitPriceDenominator = augmentedPriceList.first().unitPrice.denominator
    myCheck(augmentedPriceList.all { it.unitPrice.denominator == unitPriceDenominator }) {
        "Not all augmentedPriceList values have identical unitPrice denominators"
    }

    val recentEnoughPriceList = augmentedPriceList
        .filter { it.ageClass != AgeClass.ANCIENT }
        .map { it.unitPrice.numerator }

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

// TODO: ChatGPT semi-magic
sealed class UiText {
    // TODO: I am not sure, but use of Dynamic in "final" code might be a sign something isn't
    // right. It might be that it has genuine uses in complex cases where we construct a localised
    // string via some other means, but it might be a good idea to leave it present but commented
    // out once I no longer need it.
    data class Dynamic(val text: String) : UiText()
    data class Res(@param:StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText()
    data class PluralsRes(@param:androidx.annotation.PluralsRes val resId: Int, val quantity: Int, val args: List<Any> = emptyList()) : UiText()

    fun asString(context: Context): String = when (this) {
        is Dynamic -> text
        is Res -> context.getString(resId, *args.toTypedArray())
        is UiText.PluralsRes -> context.resources.getQuantityString(resId, quantity, *args.toTypedArray())
    }

    @Composable
    fun asString(): String = asString(LocalContext.current)
    }

@Composable
fun keyboardCapitalization(@StringRes resId: Int): KeyboardCapitalization =
    when (val str = stringResource(resId)) { // TODO: rename "str"?
        "characters" -> KeyboardCapitalization.Characters
        "none" -> KeyboardCapitalization.None
        "sentences" -> KeyboardCapitalization.Sentences
        "words" -> KeyboardCapitalization.Words
        else -> {
            Log.i("TODO", "Resource '${resourceName(LocalContext.current, resId)}' has unknown keyboard capitalization string '$str'")
            KeyboardCapitalization.None
        }
    }

fun resourceName(context: Context, @AnyRes resId: Int): String =
 try {
    context.resources.getResourceEntryName(resId)
} catch (e: Resources.NotFoundException) {
    "unknown resource $resId"
}

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

// TODO: When validating fields, we control allowEmpty based on whether a save has been attempted or
// not. Given we do generally validate everything live, I am wondering if instead we should control
// allowEmpty based off this *and* whether we are editing an existing item or adding a new one. I
// think the main motivation for this exception was that when you start adding a new entry,
// everything is blank and you don't want a sea of validation errors. But if you're editing a new
// entry, is it any more reasonable/unreasoanble to get a validation error when you temporarily
// blank out a field to enter something new than it is if you enter "3.25" in a field which only
// allows 1dp? I suppose there is an argument that it's slightly difference because you're perhaps
// (?) more likely to blank out a field to replace it in a moment than you are to temporarily edit a
// number into an invalid state while changing it.

// TODO: Make sure to do some testing and check the log for strict mode violations towards end of
// dev.

// ENHANCE: It would be nice to add automated tests. At the very least, MeasuredValue could be
// usefully tested. It would also be interesting and perhaps useful to add some unit tests for more
// of the business logic, mocking the repository, etc.

// TODO: I think I made a mistake *intending* to change "Good/OK/bad price" to "G/O/B *value*" and
// only changed the contentDescription versions, not the on-screen versions. For the moment I've
// reverted to price everywhere but may want to change to "value" everywhere.

// TODO: Some and probably all of the settingsdialog things give silly error messages if you type
// "-3a" or something. Do we need to tweak validation? Maybe have an initial "invalid number" check?
// Can/should we be restricting to numeric input and/or hinting at using a numeric on screen
// keyboard? Do we need to impose a maximum length? Should we be using (a variant of?) our existing
// NumericTextField?

// TODO: In Spanish (but also probably in English) with USD prices in non-USD locale (hence "US$"
// not just "$"), my small emulator is not fitting an (admittedly fake, but not insane in this
// context) US$69,30 por 12x400ml shelf price (for cola) in the space available. And there is likely
// no way precio por unidad is fitting at all. TBH it *may* be that given this is just borderline
// too long I should accept it, rather than switching away from the grid layout. Maybe I could use a
// non-breaking space e.g. between the price and por/for or after to improve the layout if it does
// wrap. Or actually probably a non-breaking space between 400 and ml would maybe be good. OK, I have
// experimentally added a non-breaking space in MeasuredValue.toDisplayString() and we'll see if
// that's enough or if I want to make more tweaks. I've also added a horizontal spacer to stop
// this text "touching" the unit price to its right if it is just on the borderline of needing
// to wrap.

// TODO: I've shoved in "cada uno" as a Spanish translation for "each" but this may not be right,
// need to talk to LLMs. This is so I can test if it maybe works with my string constructions etc.
// It is maybe a bit crap on the spacing (e.g. "$US cada undo" - albeit extreme - wraps in the
// comparison table, and it is a smidge but borderline OK in the precio en la tienda unit price -
// the chevron is pushed off screen!). I don't know if "cada uno" is correct or if it sometimes
// needs to be "una." It is possible something like "c/u" is normal, but does that work with "0,25
// US$/c/u" for example?

// TODO: Given the advice I received from both Grok and ChatGPT about keyboard capitalization for
// Spanish, does this mean the existing translations of the demo data set name and products are
// wrong and they should start with lower case letters not capitals?

// TODO: Spanish translation of message_no_data_set_selected seems to miss the *top* part out, but
// the message_no_data_sets has it. Maybe worth querying this. - I have had a confusing chat with
// Grok and ChatGPT and have tweaked this. However, I am far from convinced it's right even now but we're going round in circles. Even if it is right, I MAY STILL NEED TO FIX SOME OTHER USES OF HAMBURGUESA
// AND DESBORDE IN OTHER MENU ITEMS FOR CONSISTENCY NOW WE AREN'T USING THEM IN THESE REVISED ONES.

// TODO: Is the "hamburger" and "overflow menu" terminology OK *in English*?!