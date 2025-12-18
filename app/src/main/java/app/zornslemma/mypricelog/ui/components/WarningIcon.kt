package app.zornslemma.mypricelog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun WarningIcon(contentDescription: String) {
    Icon(
        imageVector = Icons.Default.Warning,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.error,
    )
}
