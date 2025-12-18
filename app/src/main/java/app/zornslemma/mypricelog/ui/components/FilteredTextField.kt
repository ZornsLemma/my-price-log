package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import app.zornslemma.mypricelog.R

// Like TextField, but with some simple logic to allow input to be filtered and discarded via an
// onCandidateValueChange callback. It also - although this is just a convenience and isn't
// fundamental - automatically drives the internal TextField's trailingIcon from the isError
// parameter if it's not explicitly specified.
@Composable
fun FilteredTextField(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    value: TextFieldValue,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    onCandidateValueChange: ((String) -> Boolean),
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    filled: Boolean = true,
) {
    if (filled) {
        TextField(
            label = label,
            value = value,
            prefix = prefix,
            suffix = suffix,
            textStyle = textStyle,
            onValueChange = { newValue: TextFieldValue ->
                if (onCandidateValueChange(newValue.text)) {
                    onValueChange(newValue)
                }
            },
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            modifier = modifier,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            trailingIcon =
                trailingIcon
                    ?: if (isError) {
                        {
                            WarningIcon(
                                contentDescription =
                                    stringResource(R.string.content_description_error)
                            )
                        }
                    } else null,
            isError = isError,
            singleLine = singleLine,
            interactionSource = interactionSource,
        )
    } else {
        OutlinedTextField(
            label = label,
            value = value,
            prefix = prefix,
            suffix = suffix,
            textStyle = textStyle,
            onValueChange = { newValue: TextFieldValue ->
                if (onCandidateValueChange(newValue.text)) {
                    onValueChange(newValue)
                }
            },
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            modifier = modifier,
            supportingText = supportingText,
            leadingIcon = leadingIcon,
            trailingIcon =
                trailingIcon
                    ?: if (isError) {
                        {
                            WarningIcon(
                                contentDescription =
                                    stringResource(R.string.content_description_error)
                            )
                        }
                    } else null,
            isError = isError,
            singleLine = singleLine,
            interactionSource = interactionSource,
        )
    }
}

// Note that if maxLength is reduced, the generated onCandidateValueChange will disallow any edits
// to existing values which are over the new limit (which we previously valid). We could fix this by
// passing the old value into onCandidateValueChange and extending the condition here to "... ||
// it.length < oldValue.length", but unless/until this is a real concern, it feels better to avoid
// having to jump through hoops to make the old value available.
//
// ENHANCE: The length limit on our ValidatedTextFields is just there to keep things tidy and in
// practice we don't expect a user to run up against it. We therefore don't show a current/max
// character count, as it would probably be more confusing than helpful. (Imagine editing a
// notionally decimal value in a text field with current/max character counts under it.) It's not
// absolutely ideal that the user's input is just silently ignored if they do hit the length limit,
// but it's not a likely case and I can't think of a nice way to show this. We could maybe show some
// kind of transitory supportingText message (not one of the more persistent ones our validation
// infrastructure generates), but even ignoring the implementation difficulties I am not sure that
// would be better than just silently dropping input.
fun createOnCandidateValueChangeMaxLength(maxLength: Int): (String) -> Boolean = {
    it.length <= maxLength
}
