package com.example.composetutorial.ui.components.generaledit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.composetutorial.ui.common.AsyncOperationStatus
import com.example.composetutorial.ui.common.SyncedStateEvent

class GeneralEditScreenStateHolder(savedStateHandle: SavedStateHandle) {
    val asyncOperationStatus = SyncedStateEvent<AsyncOperationStatus>(AsyncOperationStatus.Idle)
    @OptIn(SavedStateHandleSaveableApi::class)
    var saveAttempted by savedStateHandle.saveable { mutableStateOf(false) }
}
