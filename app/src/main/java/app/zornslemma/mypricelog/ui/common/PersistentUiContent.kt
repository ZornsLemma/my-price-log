package app.zornslemma.mypricelog.ui.common

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.zornslemma.mypricelog.ui.inputPersistenceDebounceTimeMillis
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

private const val TAG = "PersistentUiContent"

// EmptyParcelable is used as a type parameter for PersistentUiContent where we don't want both
// editable content and static content. In practice this seems to save a lot of complexity compared
// to making these type parameters nullable.
@Parcelize class EmptyParcelable : Parcelable

/**
 * PersistentUiContent persists:
 * - a fixed original EditableContentType value
 * - a modifiable value of EditContentType, initialised from the original value
 * - a static value of StaticContentType
 *
 * across process death and restores them on reincarnation as transparently as possible to its
 * client.
 *
 * In order to try to reduce verbosity for clients, variable and property names for
 * EditableContentType are not qualified with an additional word like "editable" or "dynamic".
 */
@OptIn(FlowPreview::class)
class PersistentUiContent<EditableContentType : Parcelable, StaticContentType : Parcelable>(
    viewModel: ViewModel,
    val savedStateHandle: SavedStateHandle,
    keySuffix: String,
    initialContent: EditableContentType?,
    initialStaticContent: StaticContentType?,
) {
    private val editableKey = "editable$keySuffix"
    private val originalKey = "original$keySuffix"
    private val staticKey = "static$keySuffix"

    private fun <T> getKeyWithDefaultAndUpdate(
        savedStateHandle: SavedStateHandle,
        key: String,
        initialValue: T?,
    ): T {
        val savedValue: T? = savedStateHandle[key]
        if (savedValue != null) {
            if (initialValue == null) {
                Log.d(TAG, "Using $key saved value: $savedValue")
            } else {
                // We would normally expect initialValue to be null if there is a savedValue. Log
                // this but carry on, as it's probably fine in practice. It's also expected when we
                // have EmptyParcelable, where that is always present as initialValue.
                if (initialValue !is EmptyParcelable) {
                    Log.w(
                        TAG,
                        "Using $key saved value '$savedValue' but there is an initialValue of '$initialValue'",
                    )
                }
            }
            return savedValue
        } else {
            checkNotNull(initialValue) {
                "initialValue is null and nothing in SavedStateHandle for $key"
            }
            Log.d(TAG, "Initialising $key saved value: $initialValue")
            savedStateHandle[key] = initialValue
            return initialValue
        }
    }

    val staticContent: StaticContentType =
        getKeyWithDefaultAndUpdate(savedStateHandle, staticKey, initialStaticContent)
    val originalContent: EditableContentType =
        getKeyWithDefaultAndUpdate(savedStateHandle, originalKey, initialContent)

    private val _editableContent = run {
        getKeyWithDefaultAndUpdate(savedStateHandle, editableKey, initialContent)
        // initialValue is irrelevant on the next line, as the previous call ensured the key exists.
        savedStateHandle.getMutableStateFlow(editableKey, initialValue = originalContent)
    }
    val editableContent: StateFlow<EditableContentType> = _editableContent.asStateFlow()

    fun update(newEditableContent: EditableContentType) {
        // This will cause the editableContent to update so anything collecting it can observe the
        // change. It does not update the savedStateHandle to persist the change in case of process
        // death; that happens in the coroutine launched by init.
        _editableContent.value = newEditableContent
    }

    init {
        viewModel.viewModelScope.launch {
            _editableContent.debounce(inputPersistenceDebounceTimeMillis).collectLatest {
                savedStateHandle[editableKey] = it
                Log.d(TAG, "Saving $editableKey value: $it")
            }
        }
    }
}
