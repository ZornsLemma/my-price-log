package com.example.composetutorial.ui.screens.selectsource

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.SelectSourceScreenUIContent
import com.example.composetutorial.models.Source
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorViewModel
import kotlinx.coroutines.flow.Flow

class SelectSourceViewModel(
    savedStateHandle: SavedStateHandle,
    getName: (Source) -> String,
    val uiContent: SelectSourceScreenUIContent,
    dataQuery: Flow<List<Source>>,
) : ViewModel()
{
    init {
        uiContent.saveState(savedStateHandle)
    }

        val generalSelectorViewModel =
        GeneralSelectorViewModel<Source>(
            savedStateHandle,
            getName,
            uiContent.sourceList /* TODO: rename initialList for consistency with other cases? */,
            dataQuery,
            viewModelScope
        )

    }