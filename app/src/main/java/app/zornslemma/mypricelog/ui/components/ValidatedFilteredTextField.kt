package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import app.zornslemma.mypricelog.ui.common.ValidationRule
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun <T> ValidatedFilteredTextField(
    label: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    value: TextFieldValue,
    maxLength: Int,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean,
    validationRules: List<ValidationRule<String>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean = false,
    singleLine: Boolean = false,
    validationFlow: SharedFlow<T>,
    validationFlowFieldId: T,
) {
    ValidationErrorHighlightBox(
        value = value.text,
        validationRules = validationRules,
        validationRulesKey = validationRulesKey,
        allowEmpty = allowEmpty,
        validationFlow = validationFlow,
        validationFlowFieldId = validationFlowFieldId,
    ) { validationResult, interactionSource, validationInputHandle ->
        FilteredTextField(
            label = label,
            value = value,
            onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxLength),
            onValueChange = onValueChange,
            enabled = enabled,
            isError = validationResult != null,
            supportingText = textOrNull(validationResult, color = MaterialTheme.colorScheme.error),
            modifier =
                Modifier.fillMaxWidth().validationInputHandleFocusRequester(validationInputHandle),
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            interactionSource = interactionSource,
        )
    }
}
