package com.example.composetutorial.ui.screens.selectdataset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.Item
import com.example.composetutorial.ui.common.EmptyParcelable
import com.example.composetutorial.ui.common.PersistentUiContent
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorViewModel
import com.example.composetutorial.ui.screens.selectitem.SelectItemScreenStaticContent
import kotlinx.coroutines.flow.Flow

class SelectDataSetViewModel(
    savedStateHandle: SavedStateHandle,
    getName: (DataSet) -> String,
    initialList: List<DataSet>?,
    dataQuery: Flow<List<DataSet>>,
    // TODO DELETE? dataQuery: Flow<List<Item>>,
)  : ViewModel() {
    val generalSelectorViewModel =  GeneralSelectorViewModel<DataSet>(
        savedStateHandle, // TODO!?
        getName,
        initialList,
        dataQuery,
        viewModelScope
    )
}