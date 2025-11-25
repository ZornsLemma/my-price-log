package com.example.composetutorial.ui.screens.home

import android.app.Application
import android.util.Log
import android.util.Log.e
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.domain.AugmentedPrice
import com.example.composetutorial.HomeScreenUIContent
import com.example.composetutorial.domain.PriceJudgement
import com.example.composetutorial.R
import com.example.composetutorial.domain.SettingsRepository
import com.example.composetutorial.domain.analysePrices
import com.example.composetutorial.domain.dataStore
import com.example.composetutorial.debug.myCheck
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.Item
import com.example.composetutorial.models.Price
import com.example.composetutorial.models.Source
import com.example.composetutorial.domain.sanitisePriceUnits
import com.example.composetutorial.ui.common.setSelectedDataSetIdAsync
import com.example.composetutorial.ui.common.setSelectedItemIdAsync
import com.example.composetutorial.ui.common.setSelectedSourceIdAsync
import com.example.composetutorial.ui.common.AsyncOperationStatus
import com.example.composetutorial.ui.common.LoadState
import com.example.composetutorial.ui.common.SyncedStateEvent
import com.example.composetutorial.ui.common.valueOrNull
import com.example.composetutorial.ui.spinnerDelayMillis
import com.example.composetutorial.ui.common.userPreferencesStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Locale

// We could make things work so a null sourceId represents "None", but in practice it's more trouble
// than it's worth. (We could remove the UserPreferences map entry for the data set ID key to
// represent a null value being associated with it.)
const val sourceIdNone = -1L
// Null item IDs are even less of a thing outside transitional async loading delays. Using a -1 here
// to avoid adding nullability to the selectedItemIdStateFlow is harmless and slightly reduces
// complexity.
const val itemIdNone = -1L

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val repository: Repository,
    application: Application
) : ViewModel() {
    init {
        Log.d("MyApp", "HomeScreenViewModel created: $this")
    }

    private val app = application

    fun setSelectedDataSetId(dataSetId: Long) {
        setSelectedDataSetIdAsync(app, dataSetId)
    }
    fun setSelectedItemId(itemId: Long) {
        val dataSetId = selectedDataSetIdStateFlow.value.valueOrNull()
        // We don't have the concept of a null selected data set ID (if a data set is deleted we
        // keep the old ID "selected", as we do for sources and items, and just end up with null
        // objects arising from our failure to find the selected ID in the database results), so
        // valueOrNull() can only return null during initial async data loading. But if we haven't
        // even loaded the current data set ID it shouldn't be possible for the user to see any
        // items, let alone select one.
        myCheck(dataSetId != null) {
            "dataSetId is null even though we are selecting an item"
        }
        setSelectedItemIdAsync(app, dataSetId!!, itemId)
    }

    fun setSelectedSourceId(sourceId: Long) {
        val dataSetId = selectedDataSetIdStateFlow.value.valueOrNull()
        // See comment in setSelectedItemId() for more on this check.
        myCheck(dataSetId != null) {
            "dataSetId is null even though we are selecting a source"
        }
        setSelectedSourceIdAsync(app, dataSetId!!, sourceId)
    }

    private fun <T> Flow<T>.asLoadState(): StateFlow<LoadState<T>> = this
        .map<T, LoadState<T>> { LoadState.Loaded(it) }
        .distinctUntilChanged()
        .onStart { emit(LoadState.Loading) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LoadState.Loading)

    private val prefsFlow = app.userPreferencesStore.data
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    private val selectedDataSetIdStateFlow: StateFlow<LoadState<Long>> = prefsFlow
        .map { it.selectedDataSetId }
        .asLoadState()

    private val selectedItemIdStateFlow: StateFlow<LoadState<Long>> = prefsFlow
        .map { prefs ->
            prefs.selectedItemIdForDataSetIdMap[prefs.selectedDataSetId] ?: itemIdNone
        }
        .asLoadState()

    private val selectedSourceIdStateFlow: StateFlow<LoadState<Long>> = prefsFlow
        .map { prefs ->
            prefs.selectedSourceIdForDataSetIdMap[prefs.selectedDataSetId] ?: sourceIdNone
        }
        .asLoadState()

    private val _localeFlow = MutableStateFlow(Locale.getDefault())
    private val localeFlow: StateFlow<Locale> = _localeFlow

    fun updateLocale(locale: Locale) {
        _localeFlow.value = locale
    }

    // TODO: Rename UIContent->HomeScreenUIContent and/or scope it to this ViewModel?
    private val _uiState = MutableStateFlow(
        Pair(
            false,
            HomeScreenUIContent.createEmpty()
        )
    )
    val uiState = _uiState.asStateFlow()

    val settingsRepository = SettingsRepository(app.dataStore)

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
            val dataSetId = dataSetIdState.valueOrNull()
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

        val todoRenameMeFlow = combine(
            selectedSourceIdStateFlow,
            combinedDatabaseFlow,
            settingsRepository.priceAgeSettingsFlow,
            localeFlow
        ) { _, databaseResults, priceAgeSettings, locale -> Triple(databaseResults, priceAgeSettings, locale) }

        // completeUIStateFlow delivers complete, consistent results which reflect the user's
        // selection. However, it doesn't make any guarantees as to how long it takes to emit after
        // allUserInputFlow emits.
        val completeUIStateFlow =
            todoRenameMeFlow.flatMapLatest { (databaseResults, priceAgeSettings, locale) ->
                Log.d("MyAppPAS", "priceAgeSettings $priceAgeSettings")
                Log.d("MyAppLO", "locale $locale")
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
                    val priceAnalysis = analysePrices(priceList, sourceList, priceAgeSettings, locale)

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

        viewModelScope.launch(Dispatchers.Default) {
            // TODO: MORE GROK MAGIC
            // TODO: This *might* actually be correct. I need to look at it calmly and fresh, read
            // up on channelFlow, give it more testing. But I think there is a chance it's sound.
            prefsFlow.distinctUntilChanged() // emits when a user input changes
                .flatMapLatest {
                    channelFlow {
                        var loadingJob: Job? = null

                        // Data stream
                        /* val dataJob = */ launch {
                        completeUIStateFlow.collect { data ->
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
                //throw IllegalStateException("TODO FAKE CONFIRM ERROR")
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
