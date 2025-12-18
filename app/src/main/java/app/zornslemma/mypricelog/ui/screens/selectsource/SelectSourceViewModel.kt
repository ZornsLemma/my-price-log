package app.zornslemma.mypricelog.ui.screens.selectsource

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.Source
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.ui.common.EmptyParcelable
import app.zornslemma.mypricelog.ui.common.PersistentUiContent
import app.zornslemma.mypricelog.ui.components.generalselector.GeneralSelectorScreenStateHolder
import kotlinx.parcelize.Parcelize

@Parcelize
data class SelectSourceScreenStaticContent(val initialList: List<Source>, val dataSet: DataSet) :
    Parcelable

class SelectSourceViewModel(
    repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialStaticContent: SelectSourceScreenStaticContent?,
) : ViewModel() {
    val uiContent =
        PersistentUiContent(
            this,
            savedStateHandle,
            "SelectSource",
            EmptyParcelable(),
            initialStaticContent,
        )

    val generalSelectorScreenStateHolder =
        GeneralSelectorScreenStateHolder<Source>(
            savedStateHandle,
            getName = { it.name },
            uiContent.staticContent.initialList,
            repository.getAllSources(uiContent.staticContent.dataSet.id),
            viewModelScope,
        )
}
