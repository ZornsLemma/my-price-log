package app.zornslemma.mypricelog.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.zornslemma.mypricelog.R

@Composable
fun OverflowMenu(
    enabled: Boolean = true,
    modifier: Modifier,
    content: @Composable (requestMenuClose: () -> Unit) -> Unit,
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    IconButton(enabled = enabled, onClick = { menuExpanded = true }, modifier = modifier) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.content_description_more_options),
        )
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            content { menuExpanded = false }
        }
    }
}
