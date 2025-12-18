package app.zornslemma.mypricelog.ui.screens.home

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.Item
import app.zornslemma.mypricelog.data.Price
import app.zornslemma.mypricelog.data.Source
import app.zornslemma.mypricelog.debug.debugDelay
import app.zornslemma.mypricelog.debug.debugThrow
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.domain.PriceAnalysis
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.domain.SettingsRepository
import app.zornslemma.mypricelog.domain.analysePrices
import app.zornslemma.mypricelog.domain.dataStore
import app.zornslemma.mypricelog.domain.sanitiseItems
import app.zornslemma.mypricelog.domain.sanitisePriceUnits
import app.zornslemma.mypricelog.ui.common.AsyncOperationStatus
import app.zornslemma.mypricelog.ui.common.LoadState
import app.zornslemma.mypricelog.ui.common.SyncedStateEvent
import app.zornslemma.mypricelog.ui.common.setSelectedDataSetIdAsync
import app.zornslemma.mypricelog.ui.common.setSelectedSourceIdAsync
import app.zornslemma.mypricelog.ui.common.userPreferencesStore
import app.zornslemma.mypricelog.ui.common.valueOrNull
import app.zornslemma.mypricelog.ui.spinnerDelayMillis
import java.time.Instant
import java.util.Locale
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

private const val TAG = "HomeViewModel"

// We could make things work so a null sourceId represents "None", but in practice it's more trouble
// than it's worth. (We could remove the CurrentSelections map entry for the data set ID key to
// represent a null value being associated with it.)
const val sourceIdNone = -1L
// Null item IDs are even less of a thing outside transitional async loading delays. Using a -1 here
// to avoid adding nullability to the selectedItemIdStateFlow is harmless and slightly reduces
// complexity.
const val itemIdNone = -1L

data class HomeScreenUiContent(
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
        fun createEmpty(): HomeScreenUiContent {
            return HomeScreenUiContent(
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repository: Repository, application: Application) : ViewModel() {
    private val app = application

    fun setSelectedDataSetId(dataSetId: Long) {
        setSelectedDataSetIdAsync(app, dataSetId)
    }

    fun setSelectedSourceId(sourceId: Long) {
        val dataSetId = selectedDataSetIdStateFlow.value.valueOrNull()
        // We don't have the concept of a null selected data set ID (if a data set is deleted we
        // keep the old ID "selected", as we do for sources and items, and just end up with null
        // objects arising from our failure to find the selected ID in the database results), so
        // valueOrNull() can only return null during initial async data loading. But if we haven't
        // even loaded the current data set ID it shouldn't be possible for the user to see any
        // items, let alone select one.
        myCheck(dataSetId != null) { "dataSetId is null even though we are selecting a source" }
        setSelectedSourceIdAsync(app, dataSetId!!, sourceId)
    }

    private fun <T> Flow<T>.asLoadState(): StateFlow<LoadState<T>> =
        this.map<T, LoadState<T>> { LoadState.Loaded(it) }
            .distinctUntilChanged()
            .onStart { emit(LoadState.Loading) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, LoadState.Loading)

    private val prefsFlow =
        app.userPreferencesStore.data.shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    private val selectedDataSetIdStateFlow: StateFlow<LoadState<Long>> =
        prefsFlow.map { it.selectedDataSetId }.asLoadState()

    private val selectedItemIdStateFlow: StateFlow<LoadState<Long>> =
        prefsFlow
            .map { prefs ->
                prefs.selectedItemIdForDataSetIdMap[prefs.selectedDataSetId] ?: itemIdNone
            }
            .asLoadState()

    private val selectedSourceIdStateFlow: StateFlow<LoadState<Long>> =
        prefsFlow
            .map { prefs ->
                prefs.selectedSourceIdForDataSetIdMap[prefs.selectedDataSetId] ?: sourceIdNone
            }
            .asLoadState()

    private val _localeFlow = MutableStateFlow(Locale.getDefault())
    private val localeFlow: StateFlow<Locale> = _localeFlow

    fun updateLocale(locale: Locale) {
        _localeFlow.value = locale
    }

    private val _uiState = MutableStateFlow(Pair(false, HomeScreenUiContent.createEmpty()))
    val uiState = _uiState.asStateFlow()

    val settingsRepository = SettingsRepository(app.dataStore)

    init {
        // ENHANCE: I suspect this tree of flows is over-complex. In part we are trying to work
        // around problems where a getAllItems(dataSetId) or getAllSources(dataSetId) flow is not
        // inherently tagged with its parameter, and thus if the user changes the data set ID from 1
        // to 2 we might see the items for data set 1 with the sources for data set 2 briefly before
        // the items for data set 2 arrive. Using SQLDelight might help avoid this complexity. It's
        // quite possible there is over-complexity in other areas too.

        val dataSetFlow = repository.getAllDataSets()

        val dataSetOnlyDatabaseFlow =
            selectedDataSetIdStateFlow.flatMapLatest { dataSetIdState ->
                // dataSetId can be null here (e.g. during startup when we haven't yet got the
                // preference yet, and maybe also if the user deletes all the data in the database)
                // so we need to deal with it. I think it would be wrong to use filterNotNull(),
                // because we do want to emit something - in particular, during startup, if
                // datasetId is null and *stays* null (e.g. empty database and
                // SELECTED_DATA_SET_ID_KEY has been set to null as a result), any flow that
                // combine()s this one would never see combine() emit. This just might work out OK,
                // but it feels dangerous. I think empty lists are perfect valid results to emit in
                // the null case. We are combining freshly-created DAO flows, so we cannot see
                // "stale" data here, so the dataSetId we are tagging the results with will be
                // correct. (In practice non-empty lists of results for these queries are
                // self-tagging, but we need to handle empty lists correctly too.)
                val dataSetId = dataSetIdState.valueOrNull()
                combine(
                    flowOf(dataSetId),
                    if (dataSetId != null) repository.getAllItems(dataSetId)
                    else flowOf(emptyList()),
                    if (dataSetId != null) repository.getAllSources(dataSetId)
                    else flowOf(emptyList()),
                    ::Triple,
                )
            }

        val dataSetIdAndItemIdFlow =
            combine(selectedDataSetIdStateFlow, selectedItemIdStateFlow, ::Pair)

        val dataSetIdAndItemIdDatabaseFlow =
            dataSetIdAndItemIdFlow.flatMapLatest { (dataSetIdState, itemIdState) ->
                val dataSetId = dataSetIdState.valueOrNull()
                val itemId = itemIdState.valueOrNull()
                val priceFlow =
                    if (dataSetId != null && itemId != null)
                        repository.getPricesForItem(dataSetId = dataSetId, itemId = itemId)
                    else flowOf(emptyList())
                // We are creating a flow based on a freshly created DAO flow, so we cannot see
                // "stale" data here and thus the IDs we are tagging the results with will be
                // correct.
                priceFlow.flatMapLatest { priceList ->
                    flowOf(Pair(Pair(dataSetId, itemId), priceList))
                }
            }

        val combinedDatabaseFlow =
            combine(dataSetFlow, dataSetOnlyDatabaseFlow, dataSetIdAndItemIdDatabaseFlow, ::Triple)

        val allUiStateInputsFlow =
            combine(
                selectedSourceIdStateFlow,
                combinedDatabaseFlow,
                settingsRepository.priceAgeSettingsFlow,
                localeFlow,
            ) { _, databaseResults, priceAgeSettings, locale ->
                Triple(databaseResults, priceAgeSettings, locale)
            }

        // completeUiStateFlow delivers complete, consistent results which reflect the user's
        // selection. However, it doesn't make any guarantees as to how long it takes to emit after
        // allUserInputFlow emits.
        val completeUiStateFlow =
            allUiStateInputsFlow.flatMapLatest { (databaseResults, priceAgeSettings, locale) ->
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
                        TAG,
                        "completeUiStateFlow discarding dataSetId ${taggedItemListAndSourceList.first}, want $dataSetId",
                    )
                    emptyFlow()
                } else if (taggedPriceList.first != Pair(dataSetId, itemId)) {
                    Log.d(
                        TAG,
                        "completeUiStateFlow discarding (dataSetId, itemId) ${taggedPriceList.first}, want ${
                            Pair(
                                dataSetIdState,
                                itemIdState,
                            )
                        }",
                    )
                    emptyFlow()
                } else {
                    var itemList = taggedItemListAndSourceList.second
                    val sourceList = taggedItemListAndSourceList.third
                    var priceList = taggedPriceList.second

                    val dataSet = dataSetList.find { it.id == dataSetId }

                    if (dataSet != null) {
                        itemList = dataSet.sanitiseItems(itemList)
                        priceList = dataSet.sanitisePriceUnits(priceList)
                    }

                    val item = itemList.find { it.id == itemId }
                    val source = sourceList.find { it.id == sourceId }
                    Log.d(
                        TAG,
                        "completeUiStateFlow received dataSetId ${selectedDataSetIdStateFlow.value} ${dataSet?.id} (list size ${dataSetList.size}), itemId ${item?.id} (list size ${itemList.size}), sourceId ${source?.id} (list size ${sourceList.size})",
                    )

                    // ENHANCE: I suspect in practice this analysis is lightweight enough we are
                    // fine doing it in this coroutine on the main thread, but just possibly we
                    // should shift (probably the whole database flow, but maybe just this work)
                    // onto a coroutine on a worker thread?
                    val priceAnalysis =
                        analysePrices(priceList, sourceList, priceAgeSettings, locale)

                    debugDelay()
                    flowOf(
                        HomeScreenUiContent(
                            dataSetIdState,
                            dataSet,
                            dataSetList,
                            item,
                            itemList,
                            sourceIdState,
                            source,
                            sourceList,
                            priceAnalysis,
                        )
                    )
                }
            }

        viewModelScope.launch(Dispatchers.Default) {
            prefsFlow
                .distinctUntilChanged() // emits when a user input changes
                .flatMapLatest {
                    channelFlow {
                        var loadingJob: Job? = null

                        // Data stream
                        /* val dataJob = */ launch {
                            completeUiStateFlow.collect { data ->
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
                .collectLatest { (isLoading, data) -> _uiState.value = isLoading to data }
        }
    }

    var previousPrice: MutableState<Price?> = mutableStateOf(null)

    fun confirmPrice(price: Price) {
        val now = Instant.now()
        val newPrice = price.copy(confirmedAt = now, modifiedAt = now)
        viewModelScope.launch {
            asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                debugDelay()
                debugThrow()
                repository.updateOrInsertPrice(newPrice)
                previousPrice.value = price
                asyncOperationStatus.update(AsyncOperationStatus.Success(null))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected exception", e)
                asyncOperationStatus.update(
                    AsyncOperationStatus.Error("updatePrice failed: ${e.toString()}")
                )
            }
        }
    }

    fun undoConfirmPrice(priceBeforeRevert: Price, priceAfterRevert: Price) {
        viewModelScope.launch {
            asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                debugDelay()
                repository.revertPrice(
                    priceBeforeRevert = priceBeforeRevert,
                    priceAfterRevert = priceAfterRevert,
                )
                previousPrice.value = null
                asyncOperationStatus.update(AsyncOperationStatus.Success(null))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected exception", e)
                asyncOperationStatus.update(
                    AsyncOperationStatus.Error("undoConfirmPrice failed: ${e.toString()}")
                )
            }
        }
    }

    fun deletePrice(price: Price) {
        viewModelScope.launch {
            asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                debugDelay()
                repository.deletePriceById(price.id)
                previousPrice.value = null
                asyncOperationStatus.update(AsyncOperationStatus.Success(null))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Unexpected exception", e)
                asyncOperationStatus.update(
                    AsyncOperationStatus.Error("deletePrice failed: ${e.toString()}")
                )
            }
        }
    }

    fun countPriceHistory(dataSetId: Long, itemId: Long, sourceId: Long) =
        repository.countPriceHistory(dataSetId, itemId, sourceId)

    val asyncOperationStatus = SyncedStateEvent<AsyncOperationStatus>(AsyncOperationStatus.Idle)
}
