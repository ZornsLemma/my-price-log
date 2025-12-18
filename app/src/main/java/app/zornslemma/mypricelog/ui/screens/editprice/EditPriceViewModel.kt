package app.zornslemma.mypricelog.ui.screens.editprice

import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.EditablePrice
import app.zornslemma.mypricelog.data.Item
import app.zornslemma.mypricelog.data.Source
import app.zornslemma.mypricelog.data.toDomain
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.domain.createCurrencyFormat
import app.zornslemma.mypricelog.ui.common.PersistentUiContent
import app.zornslemma.mypricelog.ui.common.ValidationRule
import app.zornslemma.mypricelog.ui.common.validationRulesOk
import app.zornslemma.mypricelog.ui.components.generaledit.GeneralEditScreenStateHolder
import app.zornslemma.mypricelog.ui.components.numericValidationRules
import java.util.Locale
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.parcelize.Parcelize

private const val TAG = "EditPriceViewModel"

@Parcelize
data class EditPriceScreenStaticContent(
    val dataSet: DataSet,
    val item: Item,
    val source: Source,
    val nonLinearEdit: Boolean,
    val frozenLocale: Locale,
) : Parcelable

class EditPriceViewModel(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialEditableContent: EditablePrice?,
    initialStaticContent: EditPriceScreenStaticContent?,
) : ViewModel() {
    val uiContent =
        PersistentUiContent(
            this,
            savedStateHandle,
            "Price",
            initialEditableContent,
            initialStaticContent,
        )

    val generalEditScreenStateHolder = GeneralEditScreenStateHolder(savedStateHandle)

    // "Count" is visible if the item explicitly allows multipacks or if (presumably because it
    // used to) we have a count > 1, which we must not hide or silently throw away. Note that
    // uiContent.originalPrice.count can be an empty string if we are adding a first price.
    val showPackCount =
        uiContent.staticContent.item.allowMultipack ||
            (uiContent.originalContent.count.toLongOrNull() ?: 1) > 1

    // ENHANCE: We could add a setting to control whether the pack count is allowed to be empty
    // (meaning 1) or it must be explicitly specified. Let's hard-code this for now to avoid making
    // the settings over-complex. We allow it to be empty for a new price, but after that we require
    // it. For an edit it starts out non-empty and if it is left empty the chances are the user was
    // editing it and messed up, rather than deliberately trying to set it to 1 by leaving it empty.
    val packCountValidationRules =
        if (showPackCount)
            numericValidationRules(
                uiContent.staticContent.frozenLocale,
                allowDecimals = false,
                allowZero = false,
                required = uiContent.originalContent.id != 0L,
            )
        else emptyList()
    var packSizeValidationRules = packSizeValidationRules()
    var currencyFormat =
        uiContent.staticContent.dataSet.createCurrencyFormat(uiContent.staticContent.frozenLocale)

    fun setUiContentEditablePrice(newEditablePrice: EditablePrice) {
        uiContent.update(newEditablePrice)
        // ENHANCE: We could potentially refactor so that if newEditablePrice has the same measure
        // unit as uiContent before we update it, we don't regenerate the pack size validation
        // rules.
        // ENHANCE: Possibly we could make this a flow mapped from the editableContent flow, but
        // that may be more trouble than it's worth - think about it when not in middle of refactor
        // though.
        packSizeValidationRules = packSizeValidationRules()
    }

    private fun packSizeValidationRules(): List<ValidationRule<String>> {
        val maxDecimals = uiContent.editableContent.value.measurementUnit.maxDecimals
        return numericValidationRules(
            uiContent.staticContent.frozenLocale,
            allowDecimals = maxDecimals > 0,
            allowZero = false,
            maxDecimals = maxDecimals,
        )
    }

    @OptIn(SavedStateHandleSaveableApi::class)
    var firstPackSizeOrPriceChangeOccurred by savedStateHandle.saveable { mutableStateOf(false) }

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

        if (
            !validationRulesOk(
                currencyFormat.validationRules,
                uiContent.editableContent.value.price,
            )
        ) {
            _saveValidationEvents.emit(EditableField.PRICE)
            return false
        }
        if (!validationRulesOk(packCountValidationRules, uiContent.editableContent.value.count)) {
            _saveValidationEvents.emit(EditableField.PACK_COUNT)
            return false
        }
        if (
            !validationRulesOk(
                packSizeValidationRules,
                uiContent.editableContent.value.measureValue,
            )
        ) {
            _saveValidationEvents.emit(EditableField.PACK_SIZE)
            return false
        }
        return true
    }

    suspend fun performSave(): Long {
        // nonLinearEdit indicates that we are editing an old historical record as a candidate for
        // updating the current record, so if the user clicks save it *is* a change even if
        // editablePrice and originalPrice are the same. (We don't just try to hack originalPrice
        // because we don't want to warn the user about losing non-existent changes if they click
        // close instead of save.)
        if (
            !uiContent.staticContent.nonLinearEdit &&
                uiContent.editableContent.value == uiContent.originalContent
        ) {
            Log.d(TAG, "performSave() is a no-op; returning early to avoid bloating price history")
            return uiContent.editableContent.value.id
        }
        val price =
            uiContent.editableContent.value.toDomain(uiContent.staticContent.frozenLocale)
                ?: throw IllegalStateException(
                    "saveEditablePrice() called with an inconvertible editablePrice: ${uiContent.editableContent.value}"
                )
        return repository.updateOrInsertPrice(price)
    }
}
