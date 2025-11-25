package com.example.composetutorial.ui.screens.selectitem

import androidx.lifecycle.SavedStateHandle
import com.example.composetutorial.SelectItemScreenUIContent
import com.example.composetutorial.models.Item
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorViewModel
import kotlinx.coroutines.flow.Flow

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