package app.zornslemma.mypricelog.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

// Sometimes we have to make a TextField "enabled = false" for it to be clickable, so we need
// to override the colours to make it look like it is enabled.
@Composable
fun myTextFieldColors(isFocused: Boolean) =
    TextFieldDefaults.colors(
        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        disabledLabelColor =
            if (isFocused) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        // We can't make the indicator thicker when mock-focused, but we can at least change the
        // colour.
        disabledIndicatorColor =
            if (isFocused) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
