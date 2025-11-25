package com.example.composetutorial.ui.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import com.example.composetutorial.rememberValidationThing
import com.example.composetutorial.ui.common.ValidationRule
import com.example.composetutorial.ui.defaultErrorHighlightOffset
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun <T, U> ValidationErrorHighlightBox(
    value: T,
    validationRules: List<ValidationRule<T>>,
    validationRulesKey: Any? = null,
    allowEmpty: Boolean = false,
    validationFlow: SharedFlow<U>,
    validationFlowFieldId: U,
    errorHighlightOffset: Dp = defaultErrorHighlightOffset,
    modifier: Modifier = Modifier,
    content: @Composable (
        validationResult: String?,
        interactionSource: MutableInteractionSource,
        validationInputHandle: ValidationInputHandle,
    ) -> Unit
) {
    val validationInputHandle = rememberValidationInputHandle()

    val validationThing201 = rememberValidationThing(
        value = value,
        validationRules = validationRules,
        validationRulesKey = validationRulesKey,
        allowEmpty = allowEmpty
    )

    ErrorHighlightBox(
        offset = errorHighlightOffset,
        validationTarget = validationInputHandle,
        modifier = modifier
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            // TODO: We could possibly pass validationThing201 directly.
            content(
                validationThing201.validationResult.value,
                validationThing201.interactionSource,
                validationInputHandle,
            )
        }
    }

    val focusManager = LocalFocusManager.current
    LaunchedEffect(Unit) {
        validationFlow.collect { field ->
            Log.d("MyApp", "LaunchedEffect saveValidationError $field")
            when (field) {
                validationFlowFieldId -> {
                    validationInputHandle.requestUserAttention(focusManager)
                }
                else -> {}
            }
        }
    }
}