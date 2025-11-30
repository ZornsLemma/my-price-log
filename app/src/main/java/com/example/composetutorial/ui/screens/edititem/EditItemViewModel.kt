package com.example.composetutorial.ui.screens.edititem

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.debug.debugDelay
import com.example.composetutorial.ui.common.ValidationRule
import com.example.composetutorial.ui.common.createNameValidationRules
import com.example.composetutorial.debug.myCheck
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.EditableItem
import com.example.composetutorial.models.toDomain
import com.example.composetutorial.ui.common.PersistentUiContent
import com.example.composetutorial.ui.common.initialVersioned
import com.example.composetutorial.ui.common.withVersion
import com.example.composetutorial.ui.components.generaledit.GeneralEditScreenViewModel
import com.example.composetutorial.ui.common.validationRulesOk
import com.example.composetutorial.ui.screens.editsource.nameValidationRulesFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize

// This class seems a bit pointless but it makes it clearer what is being retrieved from
// PersistentUiContent.staticContent.
@Parcelize
data class EditItemScreenStaticContent(
    val dataSet: DataSet,
) : Parcelable

class EditItemViewModel(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialEditableContent: EditableItem?,
    initialStaticContent: EditItemScreenStaticContent?,
) : ViewModel() {
    val uiContent = PersistentUiContent(
        this,
        savedStateHandle,
        "Item",
        initialEditableContent,
        initialStaticContent
    )

    val itemReferenceCountFlow = uiContent.originalContent.id.let { itemId ->
        if (itemId != 0L) {
            repository.countPricesForItem(itemId)
        } else {
            flowOf(0L) // new items have no references
        }
    }

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUiContentEditableItem(newEditableItem: EditableItem) {
        uiContent.update(newEditableItem)
    }

    val nameValidationRules = nameValidationRulesFlow(
        repository.getAllItems(uiContent.originalContent.dataSetId).map { itemList ->
            itemList.mapNotNull { item -> if (item.id != uiContent.originalContent.id ) item.name else null } },
        viewModelScope)

    enum class EditableField {
        NAME
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        Log.d("MyAppESS", "validateForSave")
        val nameValidationRules = nameValidationRules.value.value ?: return false
        if (!validationRulesOk(
                nameValidationRules,
                uiContent.editableContent.value.name
            )
        ) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }
        Log.d("MyAppESS", "validateForSave passed")
        return true
    }

    suspend fun performSave() : Long {
        val item = uiContent.editableContent.value.toDomain()
        if (item == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableItem: ${uiContent.editableContent.value}")
        }
        debugDelay()
        // updateOrInsertItem() returns -1 if it's an update or the new ID if it was an insert.
        val newId =  repository.updateOrInsertItem(item)
        Log.d("MyAppQZ", "updateOrInsertItem returned $newId")
        return if (newId == -1L) item.id else newId
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val itemId = uiContent.editableContent.value.id
        myCheck(itemId != 0L) { "Expected to delete an actual item but have ID 0" }
        val rowsDeleted = repository.deleteItemById(itemId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with itemId $itemId")
    }
}
