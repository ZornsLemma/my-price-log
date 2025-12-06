package com.example.composetutorial.ui.components.generaledit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.composetutorial.ui.common.AsyncOperationStatus
import com.example.composetutorial.ui.common.SyncedStateEvent

// TODO: Can/should this be handled via rememberSaveable inside GeneralEditScreen? I think in some
// sense this saving-duration kind of data should be in the caller's ViewModel (via composition). In
// practice, especially given that we "trap" the user on the edit screen that isn't so important.
// There might be considerations around app death and resurrection and being in the caller's
// ViewModel (if they remember to serialise us) might help us survive, but "the process of actually
// saving" cannot be serialised so even if a save somehow takes ages and that isn't actually
// indicative of a serious problem, will it matter that our state has been serialised to a bundle!?
// I need to thinka bout this later when it's maybe clearer.
class GeneralEditScreenStateHolder(
    savedStateHandle: SavedStateHandle
){
    val asyncOperationStatus = SyncedStateEvent<AsyncOperationStatus>(AsyncOperationStatus.Idle)
    // TODO DELETE var saveAttempted = SavedStateBoolean(savedStateHandle, "saveAttempted")
    @OptIn(SavedStateHandleSaveableApi::class)
    var saveAttempted by savedStateHandle.saveable { mutableStateOf(false) }
}

/* TODO DELETE
// TODO CHAT GPT SEMI-MAGIC, MOVE TO OWN FILE IF KEEP - MAYBE MAKE GENERIC?
class SavedStateBoolean(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    initialValue: Boolean = false
) {
    private val _state = mutableStateOf(savedStateHandle.get<Boolean>(key) ?: initialValue)
    val state: MutableState<Boolean> get() = _state

    // Setter that automatically updates the SavedStateHandle
    var value: Boolean
        get() = _state.value
        set(newValue) {
            _state.value = newValue
            savedStateHandle[key] = newValue
        }
}
*/