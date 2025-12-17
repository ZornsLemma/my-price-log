package app.zornslemma.mypricelog.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import app.zornslemma.mypricelog.ui.defaultErrorHighlightOffset

@Composable
fun ErrorHighlightBox(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    offset: Dp = defaultErrorHighlightOffset,
    validationInputHandle: ValidationInputHandle,
    content: @Composable () -> Unit,
) {
    val visible = validationInputHandle.errorHighlightBoxVisible.value
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        if (visible) {
            // Start animating from completely transparent.
            alpha.snapTo(0f)

            // Pulse alpha while we're supposed to be visible.
            while (true) {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(1000, easing = LinearEasing),
                )
                alpha.animateTo(
                    // We don't animate down to 0% alpha, as it's kind of jarring having the box
                    // completely disappear.
                    targetValue = 0.1f,
                    animationSpec = tween(1000, easing = LinearEasing),
                )
            }
        } else {
            // Fade out smoothly once we're no longer animating.
            // ENHANCE: It would maybe be nice if we could always get to 1f *then* do this fade out
            // but it's probably faffy as hell.
            alpha.animateTo(targetValue = 0f, animationSpec = tween(500))
        }
    }

    val borderColor = MaterialTheme.colorScheme.error
    Box(
        modifier =
            modifier
                .drawWithContent {
                    // Draw the content (e.g., TextField or SegmentedButton)
                    // Useful for debugging: drawRect(Color.Green.copy(alpha=0.3f))
                    drawContent()
                    // Draw an outline slightly larger than the content
                    val borderWidthPx = borderWidth.toPx()
                    val offsetPx = offset.toPx()
                    drawRect(
                        color = borderColor,
                        alpha = alpha.value,
                        style = Stroke(width = borderWidthPx),
                        topLeft = Offset(-offsetPx, -offsetPx),
                        size =
                            size.copy(
                                width = size.width + 2 * offsetPx,
                                height = size.height + 2 * offsetPx,
                            ),
                    )
                }
                .validationInputHandleBringIntoViewRequester(
                    validationInputHandle,
                    offset = offset + 2 * borderWidth,
                )
    ) {
        content()
    }
}
