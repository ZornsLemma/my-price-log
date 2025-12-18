package app.zornslemma.mypricelog.ui.screens.editsource

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.EditableSource
import app.zornslemma.mypricelog.data.LoyaltyType
import app.zornslemma.mypricelog.data.toDomain
import app.zornslemma.mypricelog.debug.debugDelay
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.ui.common.PersistentUiContent
import app.zornslemma.mypricelog.ui.common.nameValidationRulesFlow
import app.zornslemma.mypricelog.ui.common.validationRulesOk
import app.zornslemma.mypricelog.ui.components.generaledit.GeneralEditScreenStateHolder
import app.zornslemma.mypricelog.ui.components.numericValidationRules
import java.util.Locale
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditSourceScreenStaticContent(val dataSet: DataSet, val frozenLocale: Locale) :
    Parcelable

class EditSourceViewModel(
    private val repository: Repository,
    val savedStateHandle: SavedStateHandle,
    initialEditableContent: EditableSource?,
    initialStaticContent: EditSourceScreenStaticContent?,
) : ViewModel() {
    val uiContent =
        PersistentUiContent(
            this,
            savedStateHandle,
            "Source",
            initialEditableContent,
            initialStaticContent,
        )

    val sourceReferenceCountFlow =
        uiContent.originalContent.id.let { sourceId ->
            if (sourceId != 0L) {
                repository.countPricesForSource(sourceId)
            } else {
                flowOf(0L) // new sources have no references
            }
        }

    val generalEditScreenStateHolder = GeneralEditScreenStateHolder(savedStateHandle)

    fun setUiContentEditableSource(newEditableSource: EditableSource) {
        uiContent.update(newEditableSource)
    }

    val nameValidationRules =
        nameValidationRulesFlow(
            repository.getAllSources(uiContent.originalContent.dataSetId).map { sourceList ->
                sourceList.mapNotNull { source ->
                    if (source.id != uiContent.originalContent.id) source.name else null
                }
            },
            viewModelScope,
        )

    // ENHANCE: Maybe we should allow zero here? We might need to tweak some messages accordingly.
    // Zero isn't necessary as you can choose "None", but maybe it's a bit persnickety not to allow
    // the user just to type 0 directly with one of the other options as well.
    val loyaltyPercentageValidationRules =
        numericValidationRules(
            uiContent.staticContent.frozenLocale,
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
        val nameValidationRules = nameValidationRules.value.value ?: return false
        if (!validationRulesOk(nameValidationRules, uiContent.editableContent.value.name)) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }
        if (
            uiContent.editableContent.value.loyaltyType != LoyaltyType.NONE &&
                !validationRulesOk(
                    loyaltyPercentageValidationRules,
                    uiContent.editableContent.value.loyaltyPercentage,
                )
        ) {
            _saveValidationEvents.emit(EditableField.LOYALTY_PERCENTAGE)
            return false
        }
        return true
    }

    suspend fun performSave(): Long {
        val source =
            uiContent.editableContent.value.toDomain(uiContent.staticContent.frozenLocale)
                ?: throw IllegalStateException(
                    "performSave() called with an inconvertible EditableSource: ${uiContent.editableContent.value}"
                )
        debugDelay()
        // updateOrInsertSource() returns -1 if it's an update or the new ID if it was an insert.
        val newId = repository.updateOrInsertSource(source)
        return if (newId == -1L) source.id else newId
    }

    suspend fun performDelete() {
        val sourceId = uiContent.editableContent.value.id
        myCheck(sourceId != 0L) { "Expected to delete an actual source but have ID 0" }
        repository.deleteSourceById(sourceId)
    }
}
