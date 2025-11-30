package com.example.composetutorial.ui.screens.selectdataset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorStateHolder
import kotlinx.coroutines.flow.Flow

class SelectDataSetViewModel(
    savedStateHandle: SavedStateHandle,
    getName: (DataSet) -> String,
    initialList: List<DataSet>?,
    dataQuery: Flow<List<DataSet>>,
    // TODO DELETE? dataQuery: Flow<List<Item>>,
)  : ViewModel() {
    val generalSelectorStateHolder =  GeneralSelectorStateHolder<DataSet>(
        savedStateHandle, // TODO!?
        getName,
        initialList,
        dataQuery,
        viewModelScope
    )
}