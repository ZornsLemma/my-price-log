@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.composetutorial // TODO: change this!

import com.example.composetutorial.domain.dataStore
import com.example.composetutorial.domain.PriceAgeSettings
import com.example.composetutorial.ui.components.numericValidationRules
import com.example.composetutorial.ui.common.ValidationRule
import com.example.composetutorial.ui.common.failedValidationRuleOrNull
import com.example.composetutorial.debug.myCheck
import com.example.composetutorial.debug.myRequire
import com.example.composetutorial.ui.common.LoadState
import com.example.composetutorial.ui.components.generaledit.GeneralEditScreenViewModel
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorViewModel
import com.example.composetutorial.ui.common.AsyncOperationStatus
import com.example.composetutorial.models.populateDemoData
import com.example.composetutorial.ui.screens.home.HomeViewModel
import com.example.composetutorial.ui.defaultValidationMessageDelayMillis
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.core.DataStore
//import androidx.datastore.core.dataStore
//import com.example.composetutorial.datastore.proto.UserPrefs
//import com.example.composetutorial.datastore.proto.UserPrefs.DatasetSelection
import com.google.protobuf.InvalidProtocolBufferException
import UserPrefs
import com.example.composetutorial.domain.UnitFamily
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.EditableDataSet
import com.example.composetutorial.models.Item
import com.example.composetutorial.models.EditableItem
import com.example.composetutorial.models.Source
import com.example.composetutorial.models.EditableSource
import com.example.composetutorial.models.Price
import com.example.composetutorial.models.PriceHistory
import kotlinx.coroutines.flow.combine
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import java.time.Duration
//import androidx.compose.foundation.layout.*
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.icu.text.Collator
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.semantics.SemanticsProperties.Role
import androidx.compose.ui.text.input.TextFieldValue
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.withTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant
import java.util.Currency
import java.util.Locale
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import com.example.composetutorial.debug.DebugFlags
import com.example.composetutorial.domain.MeasurementUnit
import com.example.composetutorial.domain.QuantityType
import com.example.composetutorial.models.RepositoryImpl
import com.example.composetutorial.domain.UnitPrice
import com.example.composetutorial.domain.getRelevantUnitFamilies
import com.example.composetutorial.models.EditablePrice
import com.example.composetutorial.ui.AppNavigation
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import java.io.InputStream
import java.io.OutputStream
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import kotlin.collections.map
import kotlin.math.ceil
import kotlin.math.pow
import com.example.composetutorial.models.AppDatabase
import com.example.composetutorial.ui.common.UiText
import com.example.composetutorial.ui.common.setSelectedDataSetId
import com.example.composetutorial.ui.common.setSelectedItemId
import com.example.composetutorial.ui.common.setSelectedSourceId
import com.example.composetutorial.ui.screens.settings.SettingsViewModel






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






// NB: We cannot rely on the database to order our results by name as it isn't locale-sensitive, so
// we have to sort the results in memory later. We could therefore omit ORDER BY clauses completely,
// (ENHANCE: and doing this later on would give a small performance/efficiency improvement) but
// instead we use a deliberately wrong ORDER BY DESC to make it obvious if we are failing to apply
// sorting to the results before showing them.




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

/* TODO: Grok suggests this to simplify EditDataSetScreenUIContent and similar - I have not thought this through properly yet but I should at least give it some thought as if it does work it may simplify things a lot:

    The real, battle-tested solutions that preserve TextField reactivity and process-death survival
Winner for 2025: Option 3 — Two StateFlows in the ViewModel (recommended)
Kotlin
class EditDataSetViewModel(
    savedStateHandle: SavedStateHandle,
    initialDataSet: EditableDataSet
) : ViewModel() {

    // This survives process death automatically
    private val _editable = savedStateHandle.getStateFlow(
        key = "editable_dataset",
        initialValue = initialDataSet.copy()  // or however you create draft
    )

    val editableDataSet: StateFlow<EditableDataSet> = _editable.asStateFlow()
    val originalDataSet: EditableDataSet = initialDataSet

    // Call this from your composables — fully reactive!
    fun updateEditable(transform: (EditableDataSet) -> EditableDataSet) {
        _editable.update(transform)
    }

    // Optional: auto-save every change (throttled)
    init {
        viewModelScope.launch {
            _editable
                .debounce(300)  // or drop oldest, etc.
                .collectLatest { savedStateHandle["editable_dataset"] = it }
        }
    }
}

In Compose:
val editable by viewModel.editableDataSet.collectAsState()

TextField(
    value = editable.name,
    onValueChange = { viewModel.updateEditable { copy(name = it) } }
)

Pros:

Zero boilerplate beyond the ViewModel
Full two-way binding with TextFields
Survives process death out of the box
You can still have originalDataSet for cancel/dirty checking
EditableDataSet stays a pure, parcelable data class

This is what most serious production apps have converged on in 2024–2025.

*/

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





fun <T> intersectionIsEmpty(lhs: Set<T>, rhs: Set<T>) = !(lhs.any { it in rhs })

// TODO: Move into Measurement.kt? Really not sure...
fun areDifferentUnitFamilies(lhs: MeasurementUnit, rhs: MeasurementUnit) =
    intersectionIsEmpty(lhs.unitFamilies, rhs.unitFamilies)

























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












@Composable
fun WarningIcon(contentDescription: String) {
    Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.error
    )
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




// TODO: ChatGPT semi-magic




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
// wrong and they should start with lower case letters not capitals? That said, even if that's true,
// is there a competing issue where the fact these are use as screen titles (home screen, edit
// price) creates some conflict? Should we even optionally (translator specified) allow upper-casing
// of initial letter in titles?

// TODO: Spanish translation of message_no_data_set_selected seems to miss the *top* part out, but
// the message_no_data_sets has it. Maybe worth querying this. - I have had a confusing chat with
// Grok and ChatGPT and have tweaked this. However, I am far from convinced it's right even now but we're going round in circles. Even if it is right, I MAY STILL NEED TO FIX SOME OTHER USES OF HAMBURGUESA
// AND DESBORDE IN OTHER MENU ITEMS FOR CONSISTENCY NOW WE AREN'T USING THEM IN THESE REVISED ONES.

// TODO: Is the "hamburger" and "overflow menu" terminology OK *in English*?!