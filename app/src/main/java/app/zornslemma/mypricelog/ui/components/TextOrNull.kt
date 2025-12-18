package app.zornslemma.mypricelog.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

fun textOrNull(
    string: String?,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
): @Composable (() -> Unit)? {
    if (string == null) {
        return string
    } else {
        return { Text(string, modifier = modifier, color = color) }
    }
}
