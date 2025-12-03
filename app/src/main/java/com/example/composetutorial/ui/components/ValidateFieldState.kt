package com.example.composetutorial.ui.components

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
import com.example.composetutorial.ui.common.ValidationRule
import com.example.composetutorial.ui.common.failedValidationRuleOrNull
import com.example.composetutorial.ui.defaultValidationMessageDelayMillis
import kotlinx.coroutines.delay

// TODO: This is not a data class and I never even thought about it but although I find the
// distinction very confusing in practical Compose, FWIW ChatGPT was very clear that this *should
// not* be a data class (we might get away with it, but it would be prone to misuse if someone used
// copy() on it and that could break things, I think). Once I refactor this and feel more
// comfortable wit how the code works, it might be helpful to think about why (assuming ChatGPT is
// correct, but no reason to think it's not here) this should be and maybe even must be a "class"
// not a "data class", and perhaps have a more targeted discussion with an LLM about this, in order
// to clarify my mental model of Kotlin and/or Compose.
class ValidatedFieldState(
    val interactionSource: MutableInteractionSource = MutableInteractionSource(),
    val validationResult: State<String?>
)

@Composable
fun <T> ValidateFieldState(
    value: T,
    validationRules: List<ValidationRule<T>>,
    validationRulesKey: Any? = null,
    delayMillis: Long = defaultValidationMessageDelayMillis,
    // We default allowEmpty to false since this will be relatively obvious if we forget to specify
    // it somewhere it ought to have a more sophisticated condition ("add new X" will immediately
    // show a "name is empty" warning without waiting for a save attempt first). It is just about
    // worth having a default so cases where this isn't meaningful don't have to specify it.
    allowEmpty: Boolean = false
): ValidatedFieldState {
    // We create our own MutableInteractionSource which needs to be passed through to the TextField
    // we want to validate, so that we can track when that TextField is/is not focused.
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val validationResult = remember { mutableStateOf<String?>(null) }
    var failedValidationRule by remember(validationRulesKey) {
        mutableStateOf<ValidationRule<T>?>(
            null
        )
    }

    // TODO: This does not have the "change validation text immediately if there is already some and
    // the text changes" behaviour of my existing implementation - think about it, we probably *do*
    // want that. Likewise we probably want something here so there's no delay if the input has just
    // become valid. Do think about both of these though.
    // TODO: So what do we *want*?
    // - if validationRulesKey or allowEmpty changes (which is almost a form of validationRulesKey
    //   changing, just tracked separately), we should probably update immediately - these do not
    //   happen during "casual" editing (certainly not *of the field being validated*)
    // - if value changes that is going to be the user typing. We should evaluate immediately and
    //   if everything is OK, immediately update (to nothing). If something is wrong and a different
    //   something was wrong we should immediately update. Maybe. If the user is typing maybe we
    //   should minimise distractions and flicker and not change anything at all until they stop. but
    //   it does feel like there's an argument for not having an out of date error sticking around
    //   for 500ms or whatever after they finish typing.
    // - maybe simply waiting (say) 200ms after user input before we do anything and then doing it is
    //   the way to go. And just maybe have a setting option to switch between "slow" and "fast"
    //   validation or something like that. it may be best not to try being overly-clever up front,
    //   e.g. even if I am the only user, I won't really know how I feel about this until I've used
    //   it in anger on an actual smartphone with a touchscreen rather than typing on keyboard on
    //   PC or clicking awkwardly with the mouse on the on-screen keyboard on emulator.
    val context = LocalContext.current
    LaunchedEffect(context, value, validationRulesKey, allowEmpty, isFocused) {
        // TODO: The delay is breaking things a bit here when e.g. we have an empty "pack size"
        // string and click save - the validation message becomes eligible for display as allowEmpty
        // is now true, but it doesn't appear straight away and so it "misses" the highlight box and
        // it generally looks bad and a bit confusing. (This is less of a visual issue now I've
        // dropped the delay from 1000ms to 200ms, but it's probably best to address it properly.
        // Maybe put the delay back to 1000ms temporarily when working on this.) I suspect the fix
        // is to have a remembered oldValue, say "if (value != oldValue)" here instead of
        // controlling based on isFocused, and the obviously set oldValue = value after. Not tested
        // this, maybe too simplistic.
        if (isFocused) delay(delayMillis)

        // Re-evaluate failedValidationRule. We copy it to the front of the list (it's harmless if
        // we end up with two copies of it) so that if multiple validation rules are failing, we
        // don't flip-flop between them - once a rule is reported as failing it is "sticky" until is
        // fixed.
        var shouldValidate = when (value) {
            is String -> !(allowEmpty && value.trim().isEmpty())
            else -> true // allowEmpty has no meaning for other types
        }
        failedValidationRule = if (shouldValidate) failedValidationRuleOrNull(
            listOfNotNull<ValidationRule<T>>(failedValidationRule) + validationRules,
            value
        ) else null

        Log.d("MyAppXQ", "failedValidationRule: $failedValidationRule")
        validationResult.value = failedValidationRule?.message?.asString(context)
    }

    return ValidatedFieldState(interactionSource, validationResult)
}
