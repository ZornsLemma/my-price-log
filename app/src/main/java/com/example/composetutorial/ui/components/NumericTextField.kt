package com.example.composetutorial.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.example.composetutorial.isValidTransitionalDecimal
import com.example.composetutorial.ui.maxDecimalLength

// A simple wrapper around FilteredTextField which performs filtering for numeric input.
@Composable
fun NumericTextField(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    value: TextFieldValue,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    interactionSource: MutableInteractionSource? = null,
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
        onCandidateValueChange = { isValidTransitionalDecimal(it) && it.length <= maxDecimalLength },
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        modifier = modifier,
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        interactionSource = interactionSource,
    )
}