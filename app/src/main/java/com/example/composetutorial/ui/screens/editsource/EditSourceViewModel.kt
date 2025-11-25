package com.example.composetutorial.ui.screens.editsource

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.EditSourceScreenUIContent
import com.example.composetutorial.LoyaltyType
import com.example.composetutorial.UiText
import com.example.composetutorial.ValidationRule
import com.example.composetutorial.Versioned
import com.example.composetutorial.createNameValidationRules
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.initialVersioned
import com.example.composetutorial.models.EditableSource
import com.example.composetutorial.models.toDomain
import com.example.composetutorial.myCheck
import com.example.composetutorial.numericValidationRules
import com.example.composetutorial.ui.components.generaledit.GeneralEditScreenViewModel
import com.example.composetutorial.validationRulesOk
import com.example.composetutorial.withVersion
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EditSourceViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditSourceScreenUIContent,
) : ViewModel() {
    init {
        uiContent.saveState(savedStateHandle)
    }

    val sourceReferenceCountFlow = uiContent.editableSource.value.id.let { sourceId ->
        if (sourceId != 0L) {
            Log.d("MyAppQQ", "0L case occurring (probably just internally)")
            repository.countPricesForSource(sourceId)
        } else {
            flowOf(0L) // new sources have no references
        }
    }

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUIContentEditableSource(newEditableSource: EditableSource) {
        uiContent.editableSource.value = newEditableSource
        uiContent.saveEditableSourceState(savedStateHandle)
    }

    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        repository.getAllSources(uiContent.editableSource.value.dataSetId)
            .map { sourceList ->
                createNameValidationRules(
                    sourceList.filter { source -> source.id != uiContent.editableSource.value.id }
                        .map { source -> source.name }
                )
            }
            .withVersion()
            // initialValue here is set to an unsatisfiable validation list to avoid a theoretical
            // corner case. If we defaulted to emptyList(), the user might be able to save with an
            // invalid name before the real validation rules become available.
            // TODO: I think this is wrong, as in other cases this likely gives a transient error blip and we need the other solution
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialVersioned(listOf(ValidationRule({ false }, UiText.Dynamic("")))))

    // ENHANCE: Maybe we should allow zero here? We might need to tweak some messages accordingly.
    // Zero isn't necessary as you can choose "None", but maybe it's a bit persnickety not to allow
    // the user just to type 0 directly with one of the other options as well.
    val loyaltyPercentageValidationRules = numericValidationRules(
        uiContent.frozenLocale,
        allowDecimals = true,
        allowZero = false,
        maxDecimals = 2,
        // A discount of 100% or more might lead to corner cases, so let's choose an already
        // unrealistically high maximum of 99% as an easy workaround.
        maxValue = 99,
    )

    enum class EditableField {
        NAME,
        LOYALTY_PERCENTAGE,
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        Log.d("MyAppESS", "validateForSave")
        if (!validationRulesOk(
                nameValidationRules.value.value,
                uiContent.editableSource.value.name
            )
        ) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }
        if (uiContent.editableSource.value.loyaltyType != LoyaltyType.NONE && !validationRulesOk(
                loyaltyPercentageValidationRules,
                uiContent.editableSource.value.loyaltyPercentage
            )
        ) {
            _saveValidationEvents.emit(EditableField.LOYALTY_PERCENTAGE)
            return false
        }
        Log.d("MyAppESS", "validateForSave passed")
        return true
    }

    suspend fun performSave(): Long {
        val source = uiContent.editableSource.value.toDomain(uiContent.frozenLocale)
        if (source == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableSource: ${uiContent.editableSource.value}")
        }
        //delay(5000) // TODO TEMP HACK
        // updateOrInsertSource() returns -1 if it's an update or the new ID if it was an insert.
        val newId = repository.updateOrInsertSource(source)
        return if (newId == -1L) source.id else newId
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val sourceId = uiContent.editableSource.value.id
        myCheck(sourceId != 0L) { "Expected to delete an actual source but have ID 0" }
        val rowsDeleted = repository.deleteSourceById(sourceId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with sourceId $sourceId")
    }
}
