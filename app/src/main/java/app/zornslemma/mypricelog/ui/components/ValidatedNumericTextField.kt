package app.zornslemma.mypricelog.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.ui.common.UiText
import app.zornslemma.mypricelog.ui.common.ValidationRule
import app.zornslemma.mypricelog.ui.defaultErrorHighlightOffset
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun <T> ValidatedNumericTextField(
    value: TextFieldValue,
    locale: Locale,
    validationRules: List<ValidationRule<String>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean,
    validationFlow: SharedFlow<T>,
    validationFlowFieldId: T,
    errorHighlightOffset: Dp = defaultErrorHighlightOffset,
    @SuppressLint("ModifierParameter") baseValidatedTextFieldModifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean,
    numericTextFieldModifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    filled: Boolean = true,
) {
    ValidationErrorHighlightBox(
        value = value.text,
        validationRules = validationRules,
        validationFlow = validationFlow,
        validationFlowFieldId = validationFlowFieldId,
        modifier = baseValidatedTextFieldModifier,
        validationRulesKey = validationRulesKey,
        allowEmpty = allowEmpty,
        errorHighlightOffset = errorHighlightOffset,
    ) { validationResult, interactionSource, validationInputHandle ->
        NumericTextField(
            modifier =
                numericTextFieldModifier.validationInputHandleFocusRequester(validationInputHandle),
            label = label,
            value = value,
            locale = locale,
            prefix = prefix,
            suffix = suffix,
            textStyle = textStyle,
            onValueChange = onValueChange,
            enabled = enabled,
            isError = validationResult != null,
            supportingText = textOrNull(validationResult, color = MaterialTheme.colorScheme.error),
            keyboardOptions = keyboardOptions,
            interactionSource = interactionSource,
            filled = filled,
        )
    }
}

// This assumes input filtering has already excluded characters other than digits, space, comma and
// full stop.
// ENHANCE: It might be nice if the returned value had a keyboardType or a "maxDecimals" value
// embedded in it, to avoid duplicating logic when deciding on keyboardType for text fields.
fun numericValidationRules(
    locale: Locale,
    allowDecimals: Boolean = true,
    allowZero: Boolean = true,
    maxDecimals: Int? = null,
    maxValue: Int? = null,
    required: Boolean = true,
): List<ValidationRule<String>> {
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    val maxDecimalSeparators = if (allowDecimals) 1 else 0

    // Create a function to strip fluff like spaces and the grouping symbol if the user typed it in.
    val insignificantCharsRegex = "[^-0-9${Regex.escape(decimalSeparator.toString())}]".toRegex()
    fun sanitiseCandidate(candidate: String) = candidate.replace(insignificantCharsRegex, "")
    fun attemptedParse(candidate: String): Double? =
        sanitiseCandidate(candidate).replace(decimalSeparator, '.').toDoubleOrNull()

    return listOfNotNull(
        if (required) {
            ValidationRule(
                { it.trim().isNotEmpty() },
                UiText.Res(R.string.supporting_text_required),
            )
        } else null,
        ValidationRule(
            { it.count { char -> char == decimalSeparator } <= maxDecimalSeparators },
            // ENHANCE: There might be an argument for allowing a single decimal separator with no
            // trailing contents even when only whole numbers are allowed.
            if (allowDecimals) UiText.Res(R.string.supporting_text_only_one_decimal_point_allowed)
            else UiText.Res(R.string.supporting_text_only_whole_numbers_allowed),
        ),
        if (maxDecimals != null) {
            ValidationRule(
                {
                    val parts = sanitiseCandidate(it).split(decimalSeparator)
                    parts.size != 2 || parts[1].length <= maxDecimals
                },
                UiText.PluralsRes(
                    R.plurals.supporting_text_no_more_than_x_decimal_places_allowed,
                    maxDecimals,
                    listOf(maxDecimals),
                ),
            )
        } else {
            null
        },
        if (!allowZero) {
            // This message assumes you can't enter a negative value because input filtering rejects
            // '-'.
            ValidationRule(
                { attemptedParse(it) != 0.0 },
                UiText.Res(R.string.supporting_text_must_be_greater_than_zero),
            )
        } else {
            null
        },
        if (maxValue != null) {
            ValidationRule(
                { (attemptedParse(it) ?: 0.0) <= maxValue },
                UiText.Res(R.string.supporting_text_must_be_no_greater_than_x, listOf(maxValue)),
            )
        } else {
            null
        },

        // This is a catch-all; in practice we expect to catch all problems before this, but we
        // don't want to have a string which can't be converted (which would cause an error on
        // trying to save) which the user hasn't been warned about.
        ValidationRule(
            { (!required && it.trim().isEmpty()) || attemptedParse(it) != null },
            UiText.Res(R.string.supporting_text_invalid_number),
        ),
    )
}
