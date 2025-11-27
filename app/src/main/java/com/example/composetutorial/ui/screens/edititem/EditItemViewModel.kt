package com.example.composetutorial.ui.screens.edititem

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize

// TODO: MOVE?
// This class seems a bit pointless but it makes it clearer what is being retrieved from
// PersistentUiContent.staticContent.
// TODO: Can/should we use these StaticContent classes (for all Edit*, not just this one) inside the SharedViewModel instead of its custom local holder type?
@Parcelize
data class EditItemScreenStaticContent(
    val dataSet: DataSet,
) : Parcelable

// TODO: It is just possible that with the rework, we don't need things like dataSetId in EditableItem and ditto for other EditableFoo classes

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

    // TODO: May want to remove this function or tweak it but let's keep it in while we refactor
    fun setUIContentEditableItem(newEditableItem: EditableItem) {
        uiContent.update(newEditableItem)
    }


    // If we used emptyList() in initialVersioned(), the user might be able to save with an invalid
    // name before the real validation rules become available. Defaulting to a temporary "always
    // fail" rule list would stop this, but then we would see a brief validation error during the
    // initial composition. See EditItemViewModel.validateForSave() for more on this.
    val nameValidationRules =
        repository.getAllItems(uiContent.originalContent.dataSetId).map { itemList ->
            createNameValidationRules(itemList.filter { item -> item.id != uiContent.originalContent.id }
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
        //delay(5000) // TODO TEMP HACK
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
