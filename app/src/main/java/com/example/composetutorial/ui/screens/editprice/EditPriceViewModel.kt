package com.example.composetutorial.ui.screens.editprice

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.composetutorial.EditPriceScreenUIContent
import com.example.composetutorial.ValidationRule
import com.example.composetutorial.createCurrencyFormat
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.models.EditablePrice
import com.example.composetutorial.models.toDomain
import com.example.composetutorial.numericValidationRules
import com.example.composetutorial.ui.components.generaledit.GeneralEditScreenViewModel
import com.example.composetutorial.validationRulesOk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID

// TODO: Here, and possibly in other ViewModels, there is a tendency to be passing parameters into
// functions which are actually just taken out of the ViewModel's own state anyway. It may well be
// worth removing these redundant parameters, but I will hold off for now on the vague grounds that
// the parameters being explicit may be useful for unit testing later on. I can always refactor when
// I've had a go at writing some tests.
class EditPriceViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: EditPriceScreenUIContent,
) : ViewModel() {
    private val instanceId = UUID.randomUUID().toString() // TODO FOR DEBUG

    val generalEditScreenViewModel = GeneralEditScreenViewModel()

    // "Count" is visible if the item explicitly allows multipacks or if (presumably because it
    // used to) we have a count > 1, which we must not hide or silently throw away. Note that
    // uiContent.originalPrice.count can be an empty string if we are adding a first price.
    val showPackCount = uiContent.item.allowMultipack || (uiContent.originalPrice.count.toLongOrNull() ?: 1) > 1

    // ENHANCE: We could add a setting to control whether the pack count is allowed to be empty
    // (meaning 1) or it must be explicitly specified. Let's hard-code this for now to avoid making
    // the settings over-complex. We allow it to be empty for a new price, but after that we require
    // it. For an edit it starts out non-empty and if it is left empty the chances are the user was
    // editing it and messed up, rather than deliberately trying to set it to 1 by leaving it empty.
    val packCountValidationRules = if (showPackCount) numericValidationRules(uiContent.frozenLocale, allowDecimals = false, allowZero = false, required = uiContent.originalPrice.id != 0L) else emptyList()
    var packSizeValidationRules = generatePackSizeValidationRules()
    var currencyFormat = uiContent.dataSet.createCurrencyFormat(uiContent.frozenLocale)

    init {
        Log.d("MyApp", "EditPriceScreenViewModel $instanceId $this")
        Log.d("MyApp", "EditPriceScreenViewModel.init($uiContent)")
        uiContent.saveState(savedStateHandle)
    }

    fun setUIContentEditablePrice(newEditablePrice: EditablePrice) {
        Log.d("MyApp", "EditPriceScreenViewModel.setUIContentEditablePrice($newEditablePrice)")
        // TODO: We could potentially refactor so that if newEditablePrice has the same measure unit
        // as uiContent before we update it, we don't regenerate the pack size validation rules.
        uiContent.editablePrice.value = newEditablePrice
        uiContent.saveEditablePriceState(savedStateHandle)
        packSizeValidationRules = generatePackSizeValidationRules()
    }

    // TODO: This is called "generate" not "get" in part to show it performs work and isn't just
    // returning a cached value, but also to avoid a Kotlin/JVM clash with the
    // packSizeValidationRules property. I think I am generally a bit inconsistent in naming here
    // anyway (e.g. numericValidationRules() also performs work) and some kind of tidying up of the
    // naming generally might be in order.
    private fun generatePackSizeValidationRules(): List<ValidationRule<String>> {
        val maxDecimals = uiContent.editablePrice.value.measurementUnit.maxDecimals
        return numericValidationRules(
            uiContent.frozenLocale,
            allowDecimals = maxDecimals > 0,
            allowZero = false,
            maxDecimals = maxDecimals
        )
    }

    // TODO: I suspect this should *either* be moved down into a rememberSaveable inside the composable,
    // *or* it should be preserved across process death (perhaps, but not necessarily, by being moved
    // into EditPriceScreenUIContent).
    var firstPackSizeOrPriceChangeOccurred: Boolean = false

    enum class EditableField {
        PRICE,
        PACK_COUNT,
        PACK_SIZE,
    }

    private val _saveValidationEvents = MutableSharedFlow<EditableField>()
    val saveValidationEvents = _saveValidationEvents.asSharedFlow()

    suspend fun validateForSave(): Boolean {
        // ENHANCE: It might be too hard to be worth it, but *if* there are multiple fields with
        // validation errors and one of those fields is already focused, it might be nice to request
        // user attention on that focused field rather than whichever one happened to fail
        // validation first. I suspect we'd need to query which field, if any, is focused and
        // re-order our validation checks to test that field first. Or maybe push all our possible
        // emit() calls here into a local list, do all the validation (no early return), then emit()
        // the already-focused field if there is one, otherwise the first field in the list. This
        // comment applies to all validation on all screens, not just this specific screen.

        if (!validationRulesOk(
                currencyFormat.validationRules,
                uiContent.editablePrice.value.price
            )
        ) {
            _saveValidationEvents.emit(EditableField.PRICE)
            return false
        }
        if (!validationRulesOk(
                packCountValidationRules,
                uiContent.editablePrice.value.count
            )
        ) {
            Log.d("MyAppPC", "Pack count failed validation")
            _saveValidationEvents.emit(EditableField.PACK_COUNT)
            return false
        }
        if (!validationRulesOk(
                packSizeValidationRules,
                uiContent.editablePrice.value.measureValue
            )
        ) {
            _saveValidationEvents.emit(EditableField.PACK_SIZE)
            return false
        }
        return true
    }

    suspend fun performSave() : Long {
        // nonLinearEdit indicates that we are editing an old historical value as a candidate for
        // updating the current record, so if the user clicks save it *is* a change even if
        // editablePrice and originalPrice are the same. (We don't just try to hack originalPrice
        // because we don't want to warn the user about losing non-existent changes if they click
        // close instead of save.)
        // TODO: Double check the handling of toConfirm here. My thinking is that if editablePrice
        // has toConfirm set that constitutes a change, so by using the real value in editablePrice
        // and forcing originalPrice to have toConfirm false that does what we want there, and will
        // also pick up any other changes.
        if (!uiContent.nonLinearEdit && uiContent.editablePrice.value == uiContent.originalPrice.copy(
                toConfirm = false
            )
        ) {
            Log.d(
                "MyApp",
                "performSave() is a no-op; returning early to avoid bloating price history"
            )
            return uiContent.editablePrice.value.id
        }
        val price = uiContent.editablePrice.value.toDomain(uiContent.frozenLocale)
        Log.d("MyApp", "saveEditablePrice price $price")
        if (price == null) {
            throw IllegalStateException("saveEditablePrice() called with an inconvertible editablePrice: ${uiContent.editablePrice.value}")
        }
        return repository.updateOrInsertPrice(price)
    }
}
