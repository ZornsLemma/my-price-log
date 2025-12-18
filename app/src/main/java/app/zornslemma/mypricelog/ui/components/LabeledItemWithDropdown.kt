package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity

@Composable
fun <T, ID : Comparable<ID>> LabeledItemWithDropdown(
    modifier: Modifier = Modifier,
    selectedId: ID?,
    label: String,
    text: String,
    onItemSelected: (ID) -> Unit,
    dropdownContentDescription: String,
    items: List<T>,
    getId: (T) -> ID,
    getItemText: (T) -> String,
    getDividerBetween: ((T, T) -> Boolean)? = null,
    enabled: Boolean = true,
) {
    // fontSize/iconSize are used here so that the drop down icon scales correctly when the user
    // changes the system font size.
    val fontSize = MaterialTheme.typography.bodyLarge.fontSize
    val iconSize = with(LocalDensity.current) { fontSize.toDp() }

    ItemWithDropdown(
        modifier = modifier,
        selectedId = selectedId,
        onItemSelected = onItemSelected,
        enabled = enabled,
        items = items,
        getId = getId,
        getItemText = getItemText,
        getDividerBetween = getDividerBetween,
    ) { expanded ->
        LabeledItem(label = label) {
            Row {
                Box {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // ENHANCE: This text doesn't change colour when enabled is false, TBH this
                        // probably looks OK and it might actually look ugly if it did in my
                        // specific UI, but maybe it ought to. And equally maybe the LabeledItem
                        // itself should change colour when disabled.
                        Text(text)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = dropdownContentDescription,
                            modifier = Modifier.size(iconSize).rotate(if (expanded) 180f else 0f),
                        )
                    }
                }
            }
        }
    }
}
