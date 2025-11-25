package com.example.composetutorial.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import com.example.composetutorial.NumericTextField
import com.example.composetutorial.ValidationRule
import com.example.composetutorial.textOrNull
import com.example.composetutorial.ui.defaultErrorHighlightOffset
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun <T> ValidatedNumericTextField(
    value: TextFieldValue,
    validationRules: List<ValidationRule<String>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean, // TODO default to = false or force explicit?,
    validationFlow: SharedFlow<T>,
    validationFlowFieldId: T,
    errorHighlightOffset: Dp = defaultErrorHighlightOffset, // TODO JUST MAYBE GET RID OF DEFAULT?
    baseValidatedTextFieldModifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean, // TODO: default to true or force explicit?
    numericTextFieldModifier: Modifier = Modifier,
) {
    ValidationErrorHighlightBox(
        value.text,
        validationRules,
        validationRulesKey,
        allowEmpty,
        validationFlow,
        validationFlowFieldId,
        errorHighlightOffset,
        modifier = baseValidatedTextFieldModifier
    ) { validationResult, interactionSource, validationInputHandle ->
        NumericTextField(
            modifier = numericTextFieldModifier.validationInputHandleFocusRequester(validationInputHandle),
            label = label,
            value = value,
            prefix = prefix,
            suffix = suffix,
            textStyle = textStyle,
            onValueChange = onValueChange,
            enabled = enabled,
            isError = validationResult != null,
            supportingText = textOrNull(
                validationResult,
                color = MaterialTheme.colorScheme.error
            ),
            interactionSource = interactionSource,
        )
    }
}
