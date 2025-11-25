package com.example.composetutorial.ui.components.generalselector

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.normalizedForSearch
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
