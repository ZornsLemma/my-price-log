package app.zornslemma.mypricelog.ui.components.generaledit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import app.zornslemma.mypricelog.ui.common.AsyncOperationStatus
import app.zornslemma.mypricelog.ui.common.SyncedStateEvent

class GeneralEditScreenStateHolder(savedStateHandle: SavedStateHandle) {
    val asyncOperationStatus = SyncedStateEvent<AsyncOperationStatus>(AsyncOperationStatus.Idle)
    @OptIn(SavedStateHandleSaveableApi::class)
    var saveAttempted by savedStateHandle.saveable { mutableStateOf(false) }
}
