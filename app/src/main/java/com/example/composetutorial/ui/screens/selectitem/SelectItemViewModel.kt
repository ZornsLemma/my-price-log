package com.example.composetutorial.ui.screens.selectitem

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.Item
import com.example.composetutorial.ui.common.EmptyParcelable
import com.example.composetutorial.ui.common.PersistentUiContent
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorStateHolder
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectItemScreenStaticContent(
    val itemList: List<Item>,
    val dataSet: DataSet
) : Parcelable

class SelectItemViewModel(
    repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialStaticContent: SelectItemScreenStaticContent?,
)  : ViewModel() {
    val uiContent = PersistentUiContent(
        this,
        savedStateHandle,
        "SelectItem",
        EmptyParcelable(),
        initialStaticContent
    )

    val generalSelectorStateHolder =  GeneralSelectorStateHolder<Item>(
    savedStateHandle,
    getName = { it.name },
    uiContent.staticContent.itemList /* TODO: rename initialList for consistency with other cases? */,
    repository.getAllItems(uiContent.staticContent.dataSet.id),
        viewModelScope
    )
}