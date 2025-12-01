package com.example.composetutorial.ui.screens.selectdataset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorStateHolder
import kotlinx.coroutines.flow.Flow

class SelectDataSetViewModel(
    savedStateHandle: SavedStateHandle,
    initialList: List<DataSet>?,
    dataQuery: Flow<List<DataSet>>,
)  : ViewModel() {
    val generalSelectorStateHolder =  GeneralSelectorStateHolder<DataSet>(
        savedStateHandle,
        getName = { it.name },
        initialList,
        dataQuery,
        viewModelScope
    )
}