package com.example.composetutorial.ui.screens.edititem

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.EditItemScreenUIContent
import com.example.composetutorial.ValidationRule
import com.example.composetutorial.createNameValidationRules
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.initialVersioned
import com.example.composetutorial.models.EditableItem
import com.example.composetutorial.models.toDomain
import com.example.composetutorial.myCheck
import com.example.composetutorial.ui.components.generaledit.GeneralEditScreenViewModel
import com.example.composetutorial.validationRulesOk
import com.example.composetutorial.withVersion
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EditItemViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditItemScreenUIContent,
) : ViewModel() {
    init {
        uiContent.saveState(savedStateHandle)
    }

    val itemReferenceCountFlow = uiContent.editableItem.value.id.let { itemId ->
        if (itemId != 0L) {
            repository.countPricesForItem(itemId)
        } else {
            flowOf(0L) // new items have no references
        }
    }

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUIContentEditableItem(newEditableItem: EditableItem) {
        uiContent.editableItem.value = newEditableItem
        uiContent.saveEditableItemState(savedStateHandle)
    }


    // If we used emptyList() in initialVersioned(), the user might be able to save with an invalid
    // name before the real validation rules become available. Defaulting to a temporary "always
    // fail" rule list would stop this, but then we would see a brief validation error during the
    // initial composition. See EditItemViewModel.validateForSave() for more on this.
    val nameValidationRules =
        repository.getAllItems(uiContent.editableItem.value.dataSetId).map { itemList ->
            createNameValidationRules(itemList.filter { item -> item.id != uiContent.editableItem.value.id }
                .map { item -> item.name })
        }.withVersion().stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialVersioned(null as List<ValidationRule<String>>?)
        )

    enum class EditableField {
        NAME
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        Log.d("MyAppESS", "validateForSave")
        // There is a brief window during initial composition when nameValidationRules.value.value
        // can be null, because they are collected asynchronously as they depend on a database
        // query. There is a practically impossible corner case where the user is able to click
        // "Save" before the rules load and thus bypass validation. Disabling the "Save" button
        // would be nice, although it would cause a small unnecessary visual glitch, but doing this
        // is more intrusive than it's worth. So we just return false here without emitting a
        // validation event, which in practice makes the save button silently do nothing.
        val nameValidationRules = nameValidationRules.value.value ?: return false
        if (!validationRulesOk(
                nameValidationRules,
                uiContent.editableItem.value.name
            )
        ) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }
        Log.d("MyAppESS", "validateForSave passed")
        return true
    }

    suspend fun performSave() : Long {
        val item = uiContent.editableItem.value.toDomain()
        if (item == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableItem: ${uiContent.editableItem.value}")
        }
        //delay(5000) // TODO TEMP HACK
        // updateOrInsertItem() returns -1 if it's an update or the new ID if it was an insert.
        val newId =  repository.updateOrInsertItem(item)
        Log.d("MyAppQZ", "updateOrInsertItem returned $newId")
        return if (newId == -1L) item.id else newId
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val itemId = uiContent.editableItem.value.id
        myCheck(itemId != 0L) { "Expected to delete an actual item but have ID 0" }
        val rowsDeleted = repository.deleteItemById(itemId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with itemId $itemId")
    }
}
