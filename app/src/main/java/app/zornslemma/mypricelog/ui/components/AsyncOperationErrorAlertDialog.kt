package app.zornslemma.mypricelog.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.zornslemma.mypricelog.R

@Composable
fun AsyncOperationErrorAlertDialog(onDismissRequest: () -> Unit, message: String) {
    // We use an AlertDialog not a snackbar here. This is a local database save which is
    // failing so it is very unlikely to be transient. We also don't want the user
    // missing the snackbar, thinking the app is buggy ("I already saved, why didn't the
    // dialog close?") and then tapping the close icon without realising their changes
    // have not been saved. (If transient failure was a possibility - e.g. we needed to
    // perform network activity - there might be value in showing a snackbar, maybe with
    // a fallback to an AlertDialog if things keep failing.)
    AlertDialog(
        // The title and text are generic because a) this is not really expected to happen b) we
        // don't want to have to pass in strings saying whether this is a save or delete or
        // something else. The message is unlikely to be user-friendly, but if this fails the
        // chances are there's a bug rather than a transient failure anyway.
        title = { Text(stringResource(R.string.title_error)) },
        text = { Text(message) },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(stringResource(R.string.button_ok))
            }
        },
    )
}
