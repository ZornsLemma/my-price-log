package com.example.composetutorial.ui.screens.selectdataset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.data.DataSet
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorScreenStateHolder
import kotlinx.coroutines.flow.Flow

class SelectDataSetViewModel(
    savedStateHandle: SavedStateHandle,
    initialList: List<DataSet>?,
    dataQuery: Flow<List<DataSet>>,
)  : ViewModel() {
    val generalSelectorScreenStateHolder =  GeneralSelectorScreenStateHolder<DataSet>(
        savedStateHandle,
        getName = { it.name },
        initialList,
        dataQuery,
        viewModelScope
    )
}