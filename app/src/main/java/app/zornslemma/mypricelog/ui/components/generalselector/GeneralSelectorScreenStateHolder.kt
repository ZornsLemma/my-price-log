package app.zornslemma.mypricelog.ui.components.generalselector

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import app.zornslemma.mypricelog.debug.debugDelay
import app.zornslemma.mypricelog.ui.common.normalizedForSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

class GeneralSelectorScreenStateHolder<T>(
    @Suppress("unused") private val savedStateHandle: SavedStateHandle,
    private val getName: (T) -> String,
    initialList: List<T>?,
    dataQuery: Flow<List<T>>,
    viewModelScope: CoroutineScope,
) {
    // We don't try to persist initialList or dataQuery's results to savedStateHandle, because
    // if we are killed and reincarnated it's not a big deal to have a briefly empty list while
    // dataQuery executes and we get the results asynchronously. Under normal operation the caller
    // provides initialList so our first composition can be perfect.

    // This will *not* filter uiContent.initialList, but that's OK because we know the initial
    // filter doesn't exclude anything.
    // ENHANCE: We could persist the search string via savedStateHandle. If we do that, it probably
    // would require that we filter uiContent.initialList.
    val searchStringFlow = MutableStateFlow(TextFieldValue(""))

    @OptIn(ExperimentalCoroutinesApi::class)
    val dataFlow =
        combine(
                dataQuery.flatMapLatest { data ->
                    debugDelay()
                    flowOf(data)
                },
                searchStringFlow.map { searchString -> searchString.text.normalizedForSearch() },
            ) { data, normalizedQuery ->
                data.filter { getName(it).normalizedForSearch().contains(normalizedQuery) }
            }
            .onEach { emittedList -> debugDelay() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = initialList ?: emptyList(),
            )
}
