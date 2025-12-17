package app.zornslemma.mypricelog.ui.components.generaledit

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.debug.debugDelay
import app.zornslemma.mypricelog.debug.debugThrow
import app.zornslemma.mypricelog.ui.common.AsyncOperationStatus
import app.zornslemma.mypricelog.ui.components.WarningIcon

@Composable
fun GeneralEditAndDeleteScreen(
    stateHolder: GeneralEditScreenStateHolder,
    title: @Composable () -> Unit,
    isDirty: () -> Boolean,
    validateForSave: suspend () -> Boolean,
    performSave: suspend () -> Long,
    onIdle: () -> Unit,
    requestClose: (Long?) -> Unit,
    deleteConfirmationDetails: Triple<Boolean, @Composable () -> Unit, @Composable () -> Unit>?,
    performDelete: suspend () -> Unit,
    onDeleteConfirmDismissRequest: () -> Unit,
    content: @Composable (showDeleteSpinner: Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var deleting by rememberSaveable { mutableStateOf(false) }
    val saveStatus by stateHolder.asyncOperationStatus.collectAsStateWithLifecycle()

    GeneralEditScreen(
        stateHolder = stateHolder,
        title = title,
        isDirty = isDirty,
        validateForSave = validateForSave,
        performSave = performSave,
        onIdle = {
            deleting = false
            onIdle()
        },
        requestClose = requestClose,
    ) {
        content(deleting && saveStatus == AsyncOperationStatus.BusyForAWhile)
    }

    if (deleteConfirmationDetails != null) {
        val isSimpleDelete = deleteConfirmationDetails.first
        val dialogTitle = deleteConfirmationDetails.second
        val dialogText = deleteConfirmationDetails.third

        val contentDescriptionWarning = stringResource(R.string.content_description_warning)
        AlertDialog(
            icon =
                if (isSimpleDelete) null
                else {
                    { WarningIcon(contentDescription = contentDescriptionWarning) }
                },
            title = dialogTitle,
            text = dialogText,
            onDismissRequest = { onDeleteConfirmDismissRequest() },
            dismissButton = {
                TextButton(onClick = { onDeleteConfirmDismissRequest() }) {
                    Text(stringResource(R.string.button_cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteConfirmDismissRequest()
                        runGeneralEditScreenOperation(
                            stateHolder = stateHolder,
                            coroutineScope = coroutineScope,
                            isSafeToPerform = { true },
                            perform = {
                                deleting = true
                                debugDelay()
                                debugThrow()
                                performDelete()
                                // We return null since we don't want to change the selected entity
                                // on the home screen.
                                null
                            },
                        )
                    }
                ) {
                    Text(stringResource(R.string.button_delete))
                }
            },
        )
    }
}
