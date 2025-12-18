package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp

// ENHANCE: Note that selectedId is not used. I would like to use this to focus the previously
// selected item when expanding the dropdown using a D-pad, instead of defaulting to the first item.
// However, this appears to be ninja-grade development and I tried tweaking multiple AI-suggested
// solutions and got nothing but crashes.
@Composable
fun <T, ID : Comparable<ID>> ItemWithDropdown(
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier,
    @Suppress("unused", "RedundantSuppression")
    selectedId: ID?, // see above
    onItemSelected: (ID) -> Unit,
    enabled: Boolean = true,
    onExpand: (Boolean) -> Unit = {},
    items: List<T>,
    getId: (T) -> ID,
    getItemText: (T) -> String,
    getDividerBetween: ((T, T) -> Boolean)? = null,
    @Suppress("unused") addBottomSpace: Boolean = false,
    content: @Composable (expanded: Boolean) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    Box(
        modifier =
            modifier.then(
                if (enabled)
                    Modifier.clickable {
                        // We remove focus from anything else that has it in order to "fake" this
                        // component getting the focus. Without this, if a TextField has focus it
                        // retains it (including its focused colors) when the dropdown appears,
                        // which feels wrong.
                        focusManager.clearFocus(force = true)
                        expanded = true
                        @Suppress("KotlinConstantConditions") onExpand(expanded)
                    }
                else Modifier
            )
    ) {
        content(expanded)

        var previousItem: T? = null
        DropdownMenu(
            modifier = dropdownModifier,
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                @Suppress("KotlinConstantConditions") onExpand(expanded)
            },
        ) {
            items.forEach { item ->
                // We could make the first argument of getDividerBetween take null and call it every
                // time, but I'm fairly sure it makes no sense to have a divider at the very top
                // of the menu anyway.
                if (
                    previousItem != null && getDividerBetween?.invoke(previousItem!!, item) == true
                ) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
                previousItem = item

                MyDropdownMenuItem(
                    text = { Text(getItemText(item)) },
                    onClick = {
                        onItemSelected(getId(item))
                        expanded = false
                        @Suppress("KotlinConstantConditions") onExpand(expanded)
                    },
                )
            }

            /* ENHANCE: Remove this workaround later on. It no longer seems to be necessary on
               any of my test phones. I suspect updating the dependency versions pulled in an
               upstream fix.
            if (addBottomSpace) {
                // This is a workaround suggested by Grok which seems to fix a problem (at least on
                // Android 16 in the emulator) where the last bottom item in a very long dropdown
                // menu won't scroll onto the screen fully so cannot be selected, or is at least
                // hard to read. It may be that the irregular height caused by my horizontal divider
                // exacerbates this, but I don't believe that's an unreasonable thing to do in
                // itself. In practice we really shouldn't be using such long dropdowns anyway, but
                // while we are, this is an effective workaround.
                Spacer(Modifier.height(48.dp))
            }
            */
        }
    }
}
