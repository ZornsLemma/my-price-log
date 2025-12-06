package com.example.composetutorial.ui.screens.editsource

import android.os.Parcelable
import com.example.composetutorial.ui.components.numericValidationRules
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetutorial.debug.debugDelay
import com.example.composetutorial.data.LoyaltyType
import com.example.composetutorial.ui.common.ValidationRule
import com.example.composetutorial.ui.common.createNameValidationRules
import com.example.composetutorial.debug.myCheck
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.data.DataSet
import com.example.composetutorial.data.EditableSource
import com.example.composetutorial.data.toDomain
import com.example.composetutorial.ui.common.PersistentUiContent
import com.example.composetutorial.ui.common.Versioned
import com.example.composetutorial.ui.common.withVersion
import com.example.composetutorial.ui.components.generaledit.GeneralEditScreenStateHolder
import com.example.composetutorial.ui.common.validationRulesOk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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

    val generalEditScreenStateHolder = GeneralEditScreenStateHolder(savedStateHandle)

    fun setUiContentEditableSource(newEditableSource: EditableSource) {
        uiContent.update(newEditableSource)
    }

    val nameValidationRules =
        nameValidationRulesFlow(
            repository.getAllSources(uiContent.originalContent.dataSetId).map { sourceList ->
                sourceList.mapNotNull { source -> if (source.id != uiContent.originalContent.id) source.name else null } },
            viewModelScope)

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
        val nameValidationRules = nameValidationRules.value.value ?: return false
        if (!validationRulesOk(
                nameValidationRules,
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

// TODO: MOVE THIS IF IT LIVES - JUST HACKING IT IN HERE - also don't really like its name, we don't generall put "Flow" on the end of function names or variables, but if we take it off it becomes very clashy with the viewmodel variables it is used to initialise
// Create a name validation rules flow which will be null initially while we wait for the database
// results to become available. By making composables which apply the rules treat null as "no rules"
// and the view model's validateForSave() silently return false without emitting a validation event,
// we get practically correct behaviour and fix a theoretical corner case where the user manages to
// click Save before the rules have loaded and thereby skips validation. Instead Save will just be a
// no-op in this case, which isn't ideal but it won't happen in practice and it protects the
// integrity of the database. (It might be nice to disable the Save button until the rules load, but
// this would cause a small unnecessary visual glitch and is also more intrusive to arrange than
// it's worth.)
fun nameValidationRulesFlow(
    otherNameListFlow: Flow<List<String>>,
    viewModelScope: CoroutineScope,
) :  StateFlow<Versioned<out List<ValidationRule<String>>?>> {
    return otherNameListFlow.map { createNameValidationRules(it) }
    .withVersion().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        Versioned.initial(null as List<ValidationRule<String>>?)
    )
}