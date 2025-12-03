package com.example.composetutorial.ui.screens.selectsource

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.data.DataSet
import com.example.composetutorial.data.Source
import com.example.composetutorial.ui.common.EmptyParcelable
import com.example.composetutorial.ui.common.PersistentUiContent
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorStateHolder
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectSourceScreenStaticContent(
    val sourceList: List<Source>,
    val dataSet: DataSet
): Parcelable

class SelectSourceViewModel(
    repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialStaticContent: SelectSourceScreenStaticContent?,
) : ViewModel()
{
    val uiContent = PersistentUiContent(
        this,
        savedStateHandle,
        "SelectSource",
        EmptyParcelable(),
        initialStaticContent
    )

    val generalSelectorStateHolder =
        GeneralSelectorStateHolder<Source>(
            savedStateHandle,
            getName = { it.name },
            uiContent.staticContent.sourceList /* TODO: rename initialList for consistency with other cases? */,
            repository.getAllSources(uiContent.staticContent.dataSet.id),
            viewModelScope
        )

    }