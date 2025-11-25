package com.example.composetutorial.ui.screens.editdataset

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.EditDataSetScreenUIContent
import com.example.composetutorial.R
import com.example.composetutorial.UiText
import com.example.composetutorial.ValidationRule
import com.example.composetutorial.Versioned
import com.example.composetutorial.createNameValidationRules
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.initialVersioned
import com.example.composetutorial.models.DataSetUnitPreferences
import com.example.composetutorial.models.EditableDataSet
import com.example.composetutorial.models.toDomain
import com.example.composetutorial.myCheck
import com.example.composetutorial.ui.components.generaledit.GeneralEditScreenViewModel
import com.example.composetutorial.validationRulesOk
import com.example.composetutorial.withVersion
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EditDataSetViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditDataSetScreenUIContent,
) : ViewModel() {
    init {
        uiContent.saveState(savedStateHandle)
    }

    // There's no need to explicitly check for prices; we want to give a warning if there are any
    // items or sources associated with the data set even without prices, and there can't be any
    // prices without at least one item and one source.
    val dataSetReferenceCountFlow = uiContent.editableDataSet.value.id
        .takeIf { it != 0L }
        ?.let { dataSetId ->
            combine(
                repository.countItemsForDataSet(dataSetId),
                repository.countSourcesForDataSet(dataSetId)
            ) { itemReferenceCount, sourceReferenceCount ->
                itemReferenceCount + sourceReferenceCount
            }
        }
        ?: flowOf(0L) // If dataSetId is 0 (creating a new), return 0

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    fun setUIContentEditableDataSet(newEditableDataSet: EditableDataSet) {
        uiContent.editableDataSet.value = newEditableDataSet
        uiContent.saveEditableDataSetState(savedStateHandle)
    }

    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        repository.getAllDataSets()
            .map { dataSetList ->
                createNameValidationRules(
                    dataSetList.filter { dataSet -> dataSet.id != uiContent.editableDataSet.value.id }
                        .map { dataSet -> dataSet.name }
                )
            }
            .withVersion()
            // initialValue here is set to an unsatisfiable validation list to avoid a theoretical
            // corner case. If we defaulted to emptyList(), the user might be able to save with an
            // invalid name before the real validation rules become available.
            // TODO: AS ELSEWHERE I SUSPECT THIS IS WRONG AND WE NEED THE SOLUTION IMPLEMENTED FOR PRODUCT?
            .stateIn(viewModelScope, SharingStarted.Eagerly, initialVersioned(listOf(ValidationRule({ false }, UiText.Dynamic("")))))

    val currencyValidationRules = listOf(
        ValidationRule<String>(
            { it.isNotEmpty() },
            UiText.Res(R.string.supporting_text_currency_must_be_specified)
        )
    )

    // ENHANCE: I should probably replace the Triple<3xBoolean> with a data class for readability.
    // Maybe it should even be used in a domain-level DataSet class with the current raw database
    // one being renamed DataSetEntity? We could potentially pass it into various functions and that
    // might simplify the code - but check before blindly doing this, it may not be a big enough
    // win.
    val measurementSystemValidationRules = listOf(
        // We say "measurement system" in the error message here even though the caption above the
        // segmented button is "measurement units". The former is technically correct, the latter is
        // more colloquial and I think it works well as a caption, but I think in this error message
        // context, "measurement unit" does not work - it sounds as if the user is expected to
        // choose at least one thing like "miles" or "litres". If "measurement system" is a bit
        // technical, I hope the overall context with the caption above will make it clear.
        ValidationRule<DataSetUnitPreferences>(
            { it -> it.allowMetric || it.allowImperial || it.allowUSCustomary },
            UiText.Res(R.string.supporting_text_at_least_one_measurement_system_must_be_selected)
        ),
        // This next rule is enforced by UI logic, but let's go belt and braces.
        ValidationRule<DataSetUnitPreferences>(
            { !(it.allowImperial && it.allowUSCustomary) },
            UiText.Res(R.string.supporting_text_imperial_and_us_units_cannot_be_selected_together)
        ),
    )

    enum class EditableField {
        NAME,
        CURRENCY_CODE,
        MEASUREMENT_SYSTEM,
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        Log.d("MyAppESS", "validateForSave")
        val editableDataSet = uiContent.editableDataSet.value
        if (!validationRulesOk(nameValidationRules.value.value, editableDataSet.name)) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }

        if (!validationRulesOk(currencyValidationRules, editableDataSet.currencyCode)) {
            _saveValidationEvents.emit(EditableField.CURRENCY_CODE)
            return false
        }

        if (!validationRulesOk(
                measurementSystemValidationRules,
                editableDataSet.unitPreferences,
            )
        ) {
            _saveValidationEvents.emit(EditableField.MEASUREMENT_SYSTEM)
            return false
        }

        Log.d("MyAppESS", "validateForSave passed")
        return true
    }

    suspend fun performSave(): Long {
        val dataSet = uiContent.editableDataSet.value.toDomain()
        if (dataSet == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableDataSet: ${uiContent.editableDataSet.value}")
        }
        // updateOrInsertDataSet() returns -1 if it's an update or the new ID if it was an insert.
        val newId = repository.updateOrInsertDataSet(dataSet)
        return if (newId == -1L) dataSet.id else newId
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val dataSetId = uiContent.editableDataSet.value.id
        myCheck(dataSetId != 0L) { "Expected to delete an actual data set but have ID 0" }
        val rowsDeleted = repository.deleteDataSetById(dataSetId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with dataSetId $dataSetId")
    }
}
