package app.zornslemma.mypricelog.ui.screens.selectitem

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.Item
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.ui.common.EmptyParcelable
import app.zornslemma.mypricelog.ui.common.PersistentUiContent
import app.zornslemma.mypricelog.ui.components.generalselector.GeneralSelectorScreenStateHolder
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectItemScreenStaticContent(val initialList: List<Item>, val dataSet: DataSet) :
    Parcelable

class SelectItemViewModel(
    repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialStaticContent: SelectItemScreenStaticContent?,
) : ViewModel() {
    val uiContent =
        PersistentUiContent(
            this,
            savedStateHandle,
            "SelectItem",
            EmptyParcelable(),
            initialStaticContent,
        )

    val generalSelectorScreenStateHolder =
        GeneralSelectorScreenStateHolder<Item>(
            savedStateHandle,
            getName = { it.name },
            uiContent.staticContent.initialList,
            repository.getAllItems(uiContent.staticContent.dataSet.id),
            viewModelScope,
        )
}
