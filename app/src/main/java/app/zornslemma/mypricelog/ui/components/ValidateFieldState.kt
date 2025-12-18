package app.zornslemma.mypricelog.ui.components

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import app.zornslemma.mypricelog.ui.common.ValidationRule
import app.zornslemma.mypricelog.ui.common.failedValidationRuleOrNull
import app.zornslemma.mypricelog.ui.defaultValidationMessageDelayMillis
import kotlinx.coroutines.delay

private const val TAG = "ValidateFieldState"

class ValidatedFieldState(
    val interactionSource: MutableInteractionSource = MutableInteractionSource(),
    val validationResult: State<String?>,
)

@Composable
fun <T> validateFieldState(
    value: T,
    validationRules: List<ValidationRule<T>>,
    validationRulesKey: Any? = null,
    delayMillis: Long = defaultValidationMessageDelayMillis,
    // We default allowEmpty to false since this will be relatively obvious if we forget to specify
    // it somewhere it ought to have a more sophisticated condition ("add new X" will immediately
    // show a "name is empty" warning without waiting for a save attempt first). It is just about
    // worth having a default so cases where this isn't meaningful don't have to specify it.
    allowEmpty: Boolean = false,
): ValidatedFieldState {
    // We create our own MutableInteractionSource which needs to be passed through to the TextField
    // we want to validate, so that we can track when that TextField is/is not focused.
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val validationResult = remember { mutableStateOf<String?>(null) }
    var failedValidationRule by
        remember(validationRulesKey) { mutableStateOf<ValidationRule<T>?>(null) }

    val context = LocalContext.current
    var oldValue by remember { mutableStateOf(value) }
    LaunchedEffect(context, value, validationRulesKey, allowEmpty, isFocused) {
        // We apply a delay when this field is focused and the actual value has changed (which means
        // the user is editing it). The idea is to reduce visual distraction as the user is typing,
        // but update immediately e.g. if allowEmpty changes because the user clicks Save. (To see
        // the need for this, try adding a brand new price and clicking Save
        // without filling any fields in. With the "value != oldValue" check removed, the two
        // validation failures appear at different times.)
        // ENHANCE: We might not actually need isFocused here, although it may come in handy for
        // other tweaks in this function so it's probably worth keeping the variable around anyway.
        // ENHANCE: It's possible this algorithm should be fancier, but it's hard to be sure. We
        // could do things like remove a validation error which has been fixed by user input
        // immediately without a delay (perhaps depending on whether a different validation error
        // then comes into play). There is a trade-off between showing outdated information vs
        // distracting the user with changing messages. For the moment I think the best compromise
        // is just to keep delayMillis relatively low and not try to be over-clever.
        if (isFocused && value != oldValue) delay(delayMillis)

        // Re-evaluate failedValidationRule. We copy it to the front of the list (it's harmless if
        // we end up with two copies of it) so that if multiple validation rules are failing, we
        // don't flip-flop between them - once a rule is reported as failing it is "sticky" until is
        // fixed.
        val shouldValidate =
            when (value) {
                is String -> !(allowEmpty && value.trim().isEmpty())
                else -> true // allowEmpty has no meaning for other types
            }
        failedValidationRule =
            if (shouldValidate)
                failedValidationRuleOrNull(
                    listOfNotNull<ValidationRule<T>>(failedValidationRule) + validationRules,
                    value,
                )
            else null

        Log.d(TAG, "failedValidationRule: $failedValidationRule")
        validationResult.value = failedValidationRule?.message?.asString(context)
    }

    return ValidatedFieldState(interactionSource, validationResult)
}
