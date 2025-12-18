package app.zornslemma.mypricelog.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue

// rememberSyncedTextFieldValue() is a thin wrapper around a straight remember (as in the "val tfv
// =" line) which re-creates the TextFieldValue if the external string changes. (I think this is
// harmless/correct and probably reasonably good practice in general, but I'm not sure we actually
// have a case where the external string can change independently of our TextField.)
@Composable
fun rememberSyncedTextFieldValue(modelState: String): MutableState<TextFieldValue> {
    val tfv = remember { mutableStateOf(TextFieldValue(modelState)) }

    // If the model changes from the outside, resync tfv. We don't want to do this if it's the same,
    // as that would lose the additional cursor and selection state preserved inside tfv.
    if (tfv.value.text != modelState) {
        tfv.value = TextFieldValue(modelState)
    }

    return tfv
}
