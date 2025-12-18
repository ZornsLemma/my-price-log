package app.zornslemma.mypricelog.ui.screens.edititem

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.EditableItem
import app.zornslemma.mypricelog.data.toDomain
import app.zornslemma.mypricelog.debug.debugDelay
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.ui.common.PersistentUiContent
import app.zornslemma.mypricelog.ui.common.nameValidationRulesFlow
import app.zornslemma.mypricelog.ui.common.validationRulesOk
import app.zornslemma.mypricelog.ui.components.generaledit.GeneralEditScreenStateHolder
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize

// This class seems a bit pointless but it makes it clearer what is being retrieved from
// PersistentUiContent.staticContent.
@Parcelize data class EditItemScreenStaticContent(val dataSet: DataSet) : Parcelable

class EditItemViewModel(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialEditableContent: EditableItem?,
    initialStaticContent: EditItemScreenStaticContent?,
) : ViewModel() {
    val uiContent =
        PersistentUiContent(
            this,
            savedStateHandle,
            "Item",
            initialEditableContent,
            initialStaticContent,
        )

    val itemReferenceCountFlow =
        uiContent.originalContent.id.let { itemId ->
            if (itemId != 0L) {
                repository.countPricesForItem(itemId)
            } else {
                flowOf(0L) // new items have no references
            }
        }

    val generalEditScreenStateHolder = GeneralEditScreenStateHolder(savedStateHandle)

    fun setUiContentEditableItem(newEditableItem: EditableItem) {
        uiContent.update(newEditableItem)
    }

    val nameValidationRules =
        nameValidationRulesFlow(
            repository.getAllItems(uiContent.originalContent.dataSetId).map { itemList ->
                itemList.mapNotNull { item ->
                    if (item.id != uiContent.originalContent.id) item.name else null
                }
            },
            viewModelScope,
        )

    enum class EditableField {
        NAME
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        val nameValidationRules = nameValidationRules.value.value ?: return false
        if (!validationRulesOk(nameValidationRules, uiContent.editableContent.value.name)) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }
        return true
    }

    suspend fun performSave(): Long {
        val item =
            uiContent.editableContent.value.toDomain()
                ?: throw IllegalStateException(
                    "performSave() called with an inconvertible EditableItem: ${uiContent.editableContent.value}"
                )
        debugDelay()
        // updateOrInsertItem() returns -1 if it's an update or the new ID if it was an insert.
        val newId = repository.updateOrInsertItem(item)
        return if (newId == -1L) item.id else newId
    }

    suspend fun performDelete() {
        val itemId = uiContent.editableContent.value.id
        myCheck(itemId != 0L) { "Expected to delete an actual item but have ID 0" }
        repository.deleteItemById(itemId)
    }
}
