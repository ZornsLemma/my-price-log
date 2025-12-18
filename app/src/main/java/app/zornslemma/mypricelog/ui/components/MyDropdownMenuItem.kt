package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import app.zornslemma.mypricelog.ui.menuLeftPadding
import app.zornslemma.mypricelog.ui.menuRightPadding

// A simple wrapper around DropdownMenuItem applying MD3 formatting.
// ENHANCE: This isn't fully general as I don't want to add stuff that isn't going to get tested; I
// can always expand it later.
@Composable
fun MyDropdownMenuItem(text: @Composable () -> Unit, onClick: () -> Unit, enabled: Boolean = true) {
    DropdownMenuItem(
        text = {
            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                // Default colour seems to be correct so don't fiddle with it.
                text()
            }
        },
        contentPadding = PaddingValues(start = menuLeftPadding, end = menuRightPadding),
        enabled = enabled,
        onClick = onClick,
    )
}
