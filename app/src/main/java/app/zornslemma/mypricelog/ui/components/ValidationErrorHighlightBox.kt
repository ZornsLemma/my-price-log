package app.zornslemma.mypricelog.ui.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import app.zornslemma.mypricelog.ui.common.ValidationRule
import app.zornslemma.mypricelog.ui.defaultErrorHighlightOffset
import kotlinx.coroutines.flow.SharedFlow

private const val TAG = "ValidationErrorHighlightBox"

@Composable
fun <T, U> ValidationErrorHighlightBox(
    value: T,
    validationRules: List<ValidationRule<T>>,
    validationFlow: SharedFlow<U>,
    validationFlowFieldId: U,
    modifier: Modifier = Modifier,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean = false,
    errorHighlightOffset: Dp = defaultErrorHighlightOffset,
    content:
        @Composable
        (
            validationResult: String?,
            interactionSource: MutableInteractionSource,
            validationInputHandle: ValidationInputHandle,
        ) -> Unit,
) {
    val validationInputHandle = rememberValidationInputHandle()

    val validatedFieldState =
        validateFieldState(
            value = value,
            validationRules = validationRules,
            validationRulesKey = validationRulesKey,
            allowEmpty = allowEmpty,
        )

    ErrorHighlightBox(
        offset = errorHighlightOffset,
        validationInputHandle = validationInputHandle,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            content(
                validatedFieldState.validationResult.value,
                validatedFieldState.interactionSource,
                validationInputHandle,
            )
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        validationFlow.collect { field ->
            Log.d(TAG, "LaunchedEffect collected error: $field")
            when (field) {
                validationFlowFieldId -> {
                    validationInputHandle.requestUserAttention(focusManager)
                }
                else -> {}
            }
        }
    }
}
