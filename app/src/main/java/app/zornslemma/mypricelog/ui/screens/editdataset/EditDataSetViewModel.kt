package app.zornslemma.mypricelog.ui.screens.editdataset

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.data.EditableDataSet
import app.zornslemma.mypricelog.data.UnitPreferences
import app.zornslemma.mypricelog.data.toDomain
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.ui.common.EmptyParcelable
import app.zornslemma.mypricelog.ui.common.PersistentUiContent
import app.zornslemma.mypricelog.ui.common.UiText
import app.zornslemma.mypricelog.ui.common.ValidationRule
import app.zornslemma.mypricelog.ui.common.nameValidationRulesFlow
import app.zornslemma.mypricelog.ui.common.validationRulesOk
import app.zornslemma.mypricelog.ui.components.generaledit.GeneralEditScreenStateHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class EditDataSetViewModel(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialEditableContent: EditableDataSet?,
) : ViewModel() {
    val uiContent =
        PersistentUiContent(
            this,
            savedStateHandle,
            "DataSet",
            initialEditableContent,
            EmptyParcelable(),
        )

    // There's no need to explicitly check for prices; we want to give a warning if there are any
    // items or sources associated with the data set even without prices, and there can't be any
    // prices without at least one item and one source.
    @OptIn(ExperimentalCoroutinesApi::class)
    val dataSetReferenceCountFlow =
        uiContent.editableContent
            .map { it.id }
            .flatMapLatest { dataSetId ->
                if (dataSetId == 0L) {
                    flowOf(0L) // if dataSetId is 0 (creating a new), return 0
                } else {
                    combine(
                        repository.countItemsForDataSet(dataSetId),
                        repository.countSourcesForDataSet(dataSetId),
                    ) { itemReferenceCount, sourceReferenceCount ->
                        itemReferenceCount + sourceReferenceCount
                    }
                }
            }

    val generalEditScreenStateHolder = GeneralEditScreenStateHolder(savedStateHandle)

    fun setUiContentEditableDataSet(newEditableDataSet: EditableDataSet) {
        uiContent.update(newEditableDataSet)
    }

    val nameValidationRules =
        nameValidationRulesFlow(
            repository.getAllDataSets().map { dataSetList ->
                dataSetList.mapNotNull { dataSet ->
                    if (dataSet.id != uiContent.originalContent.id) dataSet.name else null
                }
            },
            viewModelScope,
        )

    // ENHANCE: I don't think these validation rules can ever fail, as we aren't enforcing "is a
    // valid code" (because it's redundant when the user can't enter free text) and we always
    // default to a currency for new data sets and don't allow it to ever be set to empty. So this
    // could possibly be deleted, although it is harmless.
    val currencyValidationRules =
        listOf(
            ValidationRule<String>(
                { it.isNotEmpty() },
                UiText.Res(R.string.supporting_text_currency_must_be_specified),
            )
        )

    val measurementSystemValidationRules =
        listOf(
            // We say "measurement system" in the error message here even though the caption above
            // the segmented button is "measurement units". The former is technically correct, the
            // latter is more colloquial and I think it works well as a caption, but I think in this
            // error message context, "measurement unit" does not work - it sounds as if the user is
            // expected to choose at least one thing like "miles" or "litres". If "measurement
            // system" is a bit technical, I hope the overall context with the caption above will
            // make it clear.
            ValidationRule<UnitPreferences>(
                { it.allowMetric || it.allowImperial || it.allowUSCustomary },
                UiText.Res(
                    R.string.supporting_text_at_least_one_measurement_system_must_be_selected
                ),
            ),
            // This next rule is enforced by UI logic, but let's go belt and braces.
            ValidationRule<UnitPreferences>(
                { !(it.allowImperial && it.allowUSCustomary) },
                UiText.Res(
                    R.string.supporting_text_imperial_and_us_units_cannot_be_selected_together
                ),
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
        val nameValidationRules = nameValidationRules.value.value ?: return false
        val editableDataSet = uiContent.editableContent.value
        if (!validationRulesOk(nameValidationRules, editableDataSet.name)) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }

        if (!validationRulesOk(currencyValidationRules, editableDataSet.currencyCode)) {
            _saveValidationEvents.emit(EditableField.CURRENCY_CODE)
            return false
        }

        if (!validationRulesOk(measurementSystemValidationRules, editableDataSet.unitPreferences)) {
            _saveValidationEvents.emit(EditableField.MEASUREMENT_SYSTEM)
            return false
        }

        return true
    }

    suspend fun performSave(): Long {
        val dataSet =
            uiContent.editableContent.value.toDomain()
                ?: throw IllegalStateException(
                    "performSave() called with an inconvertible EditableDataSet: ${uiContent.editableContent.value}"
                )
        // updateOrInsertDataSet() returns -1 if it's an update or the new ID if it was an insert.
        val newId = repository.updateOrInsertDataSet(dataSet)
        return if (newId == -1L) dataSet.id else newId
    }

    suspend fun performDelete() {
        val dataSetId = uiContent.editableContent.value.id
        myCheck(dataSetId != 0L) { "Expected to delete an actual data set but have ID 0" }
        repository.deleteDataSetById(dataSetId)
    }
}
