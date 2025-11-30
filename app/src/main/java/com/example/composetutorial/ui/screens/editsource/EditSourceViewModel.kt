package com.example.composetutorial.ui.screens.editsource

import android.os.Parcelable
import com.example.composetutorial.ui.components.numericValidationRules
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.debug.debugDelay
import com.example.composetutorial.models.LoyaltyType
import com.example.composetutorial.ui.common.ValidationRule
import com.example.composetutorial.ui.common.createNameValidationRules
import com.example.composetutorial.debug.myCheck
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.EditableItem
import com.example.composetutorial.models.EditableSource
import com.example.composetutorial.models.toDomain
import com.example.composetutorial.ui.common.PersistentUiContent
import com.example.composetutorial.ui.common.UiText
import com.example.composetutorial.ui.common.Versioned
import com.example.composetutorial.ui.common.initialVersioned
import com.example.composetutorial.ui.common.withVersion
import com.example.composetutorial.ui.components.generaledit.GeneralEditScreenViewModel
import com.example.composetutorial.ui.common.validationRulesOk
import com.example.composetutorial.ui.screens.edititem.EditItemScreenStaticContent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.parcelize.Parcelize
import java.util.Locale

@Parcelize
data class EditSourceScreenStaticContent(
    val dataSet: DataSet,
    val frozenLocale: Locale
) : Parcelable

class EditSourceViewModel(
    private val repository: Repository,
    val savedStateHandle: SavedStateHandle,
    initialEditableContent: EditableSource?,
    initialStaticContent: EditSourceScreenStaticContent?,
    ) : ViewModel() {
    val uiContent = PersistentUiContent(
        this,
        savedStateHandle,
        "Source",
        initialEditableContent,
        initialStaticContent
    )

    val sourceReferenceCountFlow = uiContent.originalContent.id.let { sourceId ->
        if (sourceId != 0L) {
            Log.d("MyAppQQ", "0L case occurring (probably just internally)")
            repository.countPricesForSource(sourceId)
        } else {
            flowOf(0L) // new sources have no references
        }
    }

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    // TODO: May want to remove this function or tweak it but let's keep it in while we refactor
    fun setUiContentEditableSource(newEditableSource: EditableSource) {
        uiContent.update(newEditableSource)
    }


    val nameValidationRules: StateFlow<Versioned<List<ValidationRule<String>>>> =
        repository.getAllSources(uiContent.originalContent.dataSetId)
            .map { sourceList ->
                createNameValidationRules(
                    sourceList.filter { source -> source.id != uiContent.originalContent.id }
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
        Log.d("MyAppESS", "validateForSave")
        if (!validationRulesOk(
                nameValidationRules.value.value,
                uiContent.editableContent.value.name
            )
        ) {
            _saveValidationEvents.emit(EditableField.NAME)
            return false
        }
        if (uiContent.editableContent.value.loyaltyType != LoyaltyType.NONE && !validationRulesOk(
                loyaltyPercentageValidationRules,
                uiContent.editableContent.value.loyaltyPercentage
            )
        ) {
            _saveValidationEvents.emit(EditableField.LOYALTY_PERCENTAGE)
            return false
        }
        Log.d("MyAppESS", "validateForSave passed")
        return true
    }

    suspend fun performSave(): Long {
        val source = uiContent.editableContent.value.toDomain(uiContent.staticContent.frozenLocale)
        if (source == null) {
            throw IllegalStateException("performSave() called with an inconvertible EditableSource: ${uiContent.editableContent.value}")
        }
        debugDelay()
        // updateOrInsertSource() returns -1 if it's an update or the new ID if it was an insert.
        val newId = repository.updateOrInsertSource(source)
        return if (newId == -1L) source.id else newId
    }

    suspend fun performDelete() {
        Log.d("MyApp", "entered performDelete")
        val sourceId = uiContent.editableContent.value.id
        myCheck(sourceId != 0L) { "Expected to delete an actual source but have ID 0" }
        val rowsDeleted = repository.deleteSourceById(sourceId)
        Log.d("MyApp", "Deleted $rowsDeleted rows with sourceId $sourceId")
    }
}
