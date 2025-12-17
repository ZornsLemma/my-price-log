package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import app.zornslemma.mypricelog.ui.maxDecimalLength
import java.text.DecimalFormatSymbols
import java.util.Locale

// A simple wrapper around FilteredTextField which performs filtering for numeric input.
@Composable
fun NumericTextField(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    value: TextFieldValue,
    locale: Locale,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions,
    interactionSource: MutableInteractionSource? = null,
    filled: Boolean = true,
) {
    FilteredTextField(
        label = label,
        value = value,
        prefix = prefix,
        suffix = suffix,
        textStyle = textStyle,
        // ENHANCE: We don't (we could, but probably no point) allow arbitrary
        // onCandidateValueChange functions to be supplied by our caller. We just hardcode this for
        // now. We could potentially accept some options from our caller which say whether decimal
        // point (locale sensitive) or minus signs are allowed and tweak the internally-assigned
        // onCandidate... function here.
        onCandidateValueChange = {
            isValidTransitionalDecimal(locale, it) && it.length <= maxDecimalLength
        },
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        modifier = modifier,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        interactionSource = interactionSource,
        filled = filled,
    )
}

// The idea here is this does not insist the input is actually parseable as a decimal (for example,
// we allow "24.2.3" so the user can enter a new decimal point and then go delete the old one
// afterwards), but that it rejects obviously incorrect things.
fun isValidTransitionalDecimal(locale: Locale, input: String): Boolean {
    // For the moment, we allow digits and the locale's decimal separator. We do not allow "-" as we
    // don't need negative numbers in this app. We also don't allow the locale's grouping separator,
    // although other parts of the code will ignore grouping separators if this function lets them
    // through. I am not sure grouping separators are really useful, so it feels better to allow
    // them if/when someone expresses an interest, rather than allow them now and then upset people
    // by perhaps taking them away later. To allow grouping separators, we'd change the regex below
    // to:
    //         val regex = Regex("[^\\d,.\\s]")
    // ENHANCE: If we don't have grouping separators, we could possibly - I am not saying it would
    // be a good idea - convert string representations of numbers when the locale changes at runtime
    // rather than using frozen locales. We could simply replace anything which is not a digit with
    // the current locale's decimal separator, since there'd be no ambiguity. (We already could have
    // tried to be cleverer and track the old and new locale specifically and change the old decimal
    // and grouping separators, I suppose.)
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    val regex = "[^0-9${Regex.escape(decimalSeparator.toString())}]".toRegex()
    return !regex.containsMatchIn(input)
}
