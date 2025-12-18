@file:OptIn(ExperimentalFoundationApi::class)

package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.zornslemma.mypricelog.ui.errorHighlightBoxVisibleTimeMillis
import kotlinx.coroutines.delay

/**
 * A ValidationInputHandle represents the idea of a UI fragment which is a user-supplied input that
 * can cause validation failures, so we need to be able to attract the user's attention to it and
 * (if appropriate; not all composables can receive focus) focus it ready for them to fix the
 * problem. It is initialised by using validationInputHandleBringIntoViewRequester() and
 * (optionally, and possibly on a different composable) validationInputHandleFocusRequester().
 */
class ValidationInputHandle(
    // Properties are internal so they can be accessed by functions below but we avoid the
    // temptation for clients to e.g. initialise focusRequester without setting
    // focusRequesterInitialised.
    internal val focusRequester: FocusRequester = FocusRequester(),
    internal var focusRequesterInitialised: Boolean = false,
    internal val bringIntoViewRequester: BringIntoViewRequester = BringIntoViewRequester(),
    internal var bringIntoViewOffset: Float = 0f,
    internal var bringIntoViewHeight: Int = 0,
    internal val errorHighlightBoxVisible: MutableState<Boolean> = mutableStateOf(false),
)

@Composable
fun rememberValidationInputHandle(): ValidationInputHandle {
    return remember { ValidationInputHandle() }
}

@Composable
fun Modifier.validationInputHandleBringIntoViewRequester(
    handle: ValidationInputHandle,
    offset: Dp = 0.dp,
): Modifier {
    handle.bringIntoViewOffset = with(LocalDensity.current) { offset.toPx() }
    return this.onGloballyPositioned { coordinates ->
            handle.bringIntoViewHeight = coordinates.size.height
        }
        .bringIntoViewRequester(handle.bringIntoViewRequester)
}

fun Modifier.validationInputHandleFocusRequester(handle: ValidationInputHandle): Modifier {
    handle.focusRequesterInitialised = true
    return focusRequester(handle.focusRequester)
}

suspend fun ValidationInputHandle.requestUserAttention(focusManager: FocusManager) {
    if (!focusRequesterInitialised) {
        // If we didn't (couldn't meaningfully) initialise the focusRequester, that means the target
        // can't be focused. We therefore content ourselves with removing the focus from anything
        // else that has it. We do this before calling bringIntoView() since it may dismiss the
        // on-screen keyboard and in practice it looks much nicer to do it in this order.
        // ENHANCE: I half wonder if we should be using Modifier.focusTarget() to make it possible
        // to focus things like segmented buttons. However, this seems to work and I haven't
        // experimented with alternatives.
        focusManager.clearFocus()
    }

    bringIntoViewRequester.bringIntoView(
        Rect(
            left = 0f,
            top = -bringIntoViewOffset,
            right = 0f,
            bottom = bringIntoViewHeight + 2 * bringIntoViewOffset,
        )
    )

    if (focusRequesterInitialised) {
        // I am a bit unsure as to why, but it seems to work much better to do requestFocus()
        // *after* bringIntoView(). The precise behaviour depends on whether the control already has
        // the focus and maybe whether there is a keyboard on screen already and what type it is.
        // ENHANCE: It's possible (speculative) that doing a clearFocus() before requestFocus()
        // would help with some corner cases.
        focusRequester.requestFocus()
        // ENHANCE: It might be good to focus TextFields with the cursor at the end of the text.
    }

    // ENHANCE: I haven't experimented and this is very speculative, but we might be able to
    // improve things:
    // - Allow a small delay before making the errorHighlightBox visible to give an on-screen
    //   keyboard time to animate in.
    // - Set the errorHighlightBox to visible right at the start of this function so it's fully
    //   visible, which might alter how bringIntoView() treats it.
    errorHighlightBoxVisible.value = true
    delay(errorHighlightBoxVisibleTimeMillis)
    errorHighlightBoxVisible.value = false
}
