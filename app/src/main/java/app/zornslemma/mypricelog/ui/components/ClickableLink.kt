package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClickableLink(text: String, url: String, showRawUrl: Boolean = true) {
    val uriHandler = LocalUriHandler.current

    Column {
        Text(
            text = text,
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary,
                ),
            modifier =
                Modifier.clickable { uriHandler.openUri(url) }
                    // This padding feels slightly visually unattractive (although it's growing on
                    // me a bit), but we want to allow some clearance so the "tappable area" to
                    // click on the links isn't too small, roughly in accordance with MD3 guidelines
                    // even if we're not following them formally here.
                    .padding(vertical = 8.dp),
        )

        if (showRawUrl) {
            // SelectionContainer allows the user to select the link so they can copy it to their
            // clipboard for further use.
            SelectionContainer {
                Text(
                    text = url,
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                )
            }
        }
    }
}
