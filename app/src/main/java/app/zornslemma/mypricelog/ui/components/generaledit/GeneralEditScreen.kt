@file:OptIn(ExperimentalMaterial3Api::class)

package app.zornslemma.mypricelog.ui.components.generaledit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.debug.debugDelay
import app.zornslemma.mypricelog.debug.debugThrow
import app.zornslemma.mypricelog.ui.common.AsyncOperationStatus
import app.zornslemma.mypricelog.ui.common.isNotBusy
import app.zornslemma.mypricelog.ui.components.AsyncOperationErrorAlertDialog
import app.zornslemma.mypricelog.ui.components.SmallCircularProgressIndicator
import app.zornslemma.mypricelog.ui.fullScreenDialogHorizontalBorder
import app.zornslemma.mypricelog.ui.fullScreenDialogVerticalBorder
import app.zornslemma.mypricelog.ui.spinnerDelayMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch

@Composable
fun GeneralEditScreen(
    stateHolder: GeneralEditScreenStateHolder,
    title: @Composable () -> Unit,
    isDirty: () -> Boolean,
    validateForSave: suspend () -> Boolean,
    performSave: suspend () -> Long,
    onIdle: () -> Unit,
    requestClose: (Long?) -> Unit,
    content: @Composable () -> Unit,
) {
    val saveStatus by stateHolder.asyncOperationStatus.collectAsStateWithLifecycle()
    val isNotBusy = saveStatus.isNotBusy()
    var showConfirmDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showErrorDialogMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showBusySnackbar by rememberSaveable { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var saving by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // We can't use dropUnlessResumed here as we have a parameter, so pseudo-inline it.
    val localLifecycleOwner = LocalLifecycleOwner.current
    fun requestCloseDebounced(id: Long?) {
        if (
            localLifecycleOwner.lifecycle.currentState.isAtLeast(
                androidx.lifecycle.Lifecycle.State.RESUMED
            )
        ) {
            requestClose(id)
        }
    }

    fun requestDismiss() {
        if (isDirty()) {
            showConfirmDiscardDialog = true
        } else {
            requestCloseDebounced(null)
        }
    }

    BackHandler {
        if (isNotBusy) {
            requestDismiss()
        } else {
            // I've discussed this with LLMs and it's not clear if - from a UI perspective - we
            // should do this or not, but I'll go with it for now.
            showBusySnackbar = true
        }
    }

    LaunchedEffect(Unit) {
        // We use buffer() here because we want to update() while we are already collecting; we
        // might get a deadlock otherwise.
        stateHolder.asyncOperationStatus.events.buffer().collect { event ->
            when (event) {
                AsyncOperationStatus.Busy -> {
                    // We expect the operation to complete quickly so we don't want the visual
                    // distraction of a progress indicator appearing straight away. Let the progress
                    // indicator kick in after a short delay if we're still here waiting.
                    delay(spinnerDelayMillis)
                    // The state might not be busy any more, so check first before updating to avoid
                    // a race condition.
                    if (stateHolder.asyncOperationStatus.state.value == AsyncOperationStatus.Busy) {
                        stateHolder.asyncOperationStatus.update(AsyncOperationStatus.BusyForAWhile)
                    }
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        // We use buffer here because we want to update() in the error case while we are
        // already collecting; we get a deadlock otherwise.
        stateHolder.asyncOperationStatus.events.buffer().collect { event ->
            when (event) {
                AsyncOperationStatus.Idle -> {
                    saving = false
                    onIdle()
                }

                is AsyncOperationStatus.Success -> {
                    requestCloseDebounced(event.id)
                }

                is AsyncOperationStatus.Error -> {
                    stateHolder.asyncOperationStatus.update(AsyncOperationStatus.Idle)
                    showErrorDialogMessage = event.message
                }

                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(enabled = isNotBusy, onClick = { requestDismiss() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.content_description_close),
                        )
                    }
                },
                title = title,
                actions = {
                    TextButton(
                        enabled = isNotBusy,
                        onClick = {
                            // We could check isDirty here and just dismiss without saving if
                            // there's nothing to save, but it's probably best (given there's no
                            // history table which would get bloated) just to save regardless.
                            stateHolder.saveAttempted = true
                            runGeneralEditScreenOperation(
                                stateHolder = stateHolder,
                                coroutineScope = coroutineScope,
                                isSafeToPerform = validateForSave,
                                perform = {
                                    saving = true
                                    debugDelay()
                                    performSave()
                                },
                            )
                        },
                    ) {
                        // We do get rid of the spinner when we reach "success"; this might cause a
                        // small but legitimate visual glitch as the disabled "Save" button
                        // re-enables, but it feels confusing to close while showing the spinner,
                        // since it might suggest to the user we *haven't* finished but are for some
                        // reason closing anyway.
                        if (saving && saveStatus == AsyncOperationStatus.BusyForAWhile) {
                            SmallCircularProgressIndicator()
                        } else {
                            Text(stringResource(R.string.button_save))
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier.background(
                        MaterialTheme.colorScheme.surface
                    ) // because this is a full-screen dialog
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .padding(horizontal = fullScreenDialogHorizontalBorder)
                    .verticalScroll(scrollState)
                    .imePadding()
        ) {
            // The two vertical spacers here are to create a vertical border which we *can* draw
            // over using ErrorHighlightBox. (If we add "vertical = fullScreenDialogVerticalBorder"
            // to the parent Column's .padding(), we can't draw over it.) I have been unable to find
            // a really clear answer if we should have a vertical space between the top app bar and
            // the first "real" thing (e.g. a TextField) in the content, so I am going to let the
            // need to be able to draw an ErrorHighlightBox around the first thing in the content
            // make the decision for me. We apply this here for consistency across all dialogs. (In
            // practice the top app bar's background and the content's background are the same, so
            // it isn't normally that noticeable either way. You can see the difference more easily
            // by using a non-standard background for the dialog.)
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
            content()
            Spacer(modifier = Modifier.height(fullScreenDialogVerticalBorder))
        }
    }

    if (showConfirmDiscardDialog) {
        // I copied the wording of this dialog directly from a screenshot in the M3 documentation.
        AlertDialog(
            title = { Text(stringResource(R.string.title_discard_unsaved_changes)) },
            text = { Text(stringResource(R.string.message_unsaved_changes)) },
            onDismissRequest = { showConfirmDiscardDialog = false },
            dismissButton = {
                TextButton(onClick = { showConfirmDiscardDialog = false }) {
                    Text(stringResource(R.string.button_keep_editing))
                }
            },
            confirmButton = {
                TextButton(onClick = { requestCloseDebounced(null) }) {
                    Text(stringResource(R.string.button_discard))
                }
            },
        )
    }

    if (showErrorDialogMessage != null) {
        AsyncOperationErrorAlertDialog(
            onDismissRequest = { showErrorDialogMessage = null },
            message = showErrorDialogMessage!!,
        )
    }

    val messageBusyPleaseWait = stringResource(R.string.message_busy_please_wait)
    LaunchedEffect(showBusySnackbar, messageBusyPleaseWait) {
        if (showBusySnackbar) {
            snackbarHostState.showSnackbar(messageBusyPleaseWait)
            showBusySnackbar = false
        }
    }
}

fun runGeneralEditScreenOperation(
    stateHolder: GeneralEditScreenStateHolder,
    coroutineScope: CoroutineScope,
    isSafeToPerform: suspend () -> Boolean,
    perform: suspend () -> Long?,
) {
    coroutineScope.launch {
        if (isSafeToPerform()) {
            stateHolder.asyncOperationStatus.update(AsyncOperationStatus.Busy)
            try {
                debugThrow()
                val id = perform()
                debugDelay()
                stateHolder.asyncOperationStatus.update(AsyncOperationStatus.Success(id))
            } catch (e: Exception) {
                stateHolder.asyncOperationStatus.update(
                    AsyncOperationStatus.Error(
                        "runGeneralEditScreenOperation failed: ${e.toString()}"
                    )
                )
            }
        }
    }
}
