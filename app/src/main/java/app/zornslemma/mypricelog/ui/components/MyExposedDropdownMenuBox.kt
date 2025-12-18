package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.ui.menuLeftPadding

// ENHANCE: I am not sure if we should disable the on-click ripple here when opening the menu. It's
// not that clear to me if the guidelines at https://m3.material.io/components/menus/guidelines also
// suggest other behaviours we don't have, although the core of this implementation is the standard
// DropdownMenu. Maybe there's some element of MD3 Expressive in those guidelines. It might be worth
// trying the experimental ExposedDropdownMenuBox again at some point, although up to now I have
// found it not to work very well. Maybe there is or will be another standard component worth using
// here.
@Composable
fun <T, ID : Comparable<ID>> MyExposedDropdownMenuBox(
    modifier: Modifier = Modifier,
    selectedId: ID?,
    onItemSelected: (ID) -> Unit,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    items: List<T>,
    getId: (T) -> ID,
    getItemText: (T) -> String,
    getCollapsedItemText: ((T) -> String)? = null,
    getDividerBetween: ((T, T) -> Boolean)? = null,
    addBottomSpace: Boolean = false,
) {
    var textFieldWidth by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ItemWithDropdown(
            // We use .widthIn to force the dropdown to be at least as wide as its parent TextField
            // while allowing it to be wider (mainly for dropdowns on TextFields which don't occupy
            // the full screen width). ENHANCE: In practice this probably works well, but we might
            // want to add parameters to allow our caller to force exact width or other variations.
            dropdownModifier =
                Modifier.widthIn(min = with(LocalDensity.current) { textFieldWidth.toDp() }),
            selectedId = selectedId,
            onItemSelected = onItemSelected,
            enabled = enabled,
            onExpand = { expanded = it },
            items = items,
            getId = getId,
            getItemText = getItemText,
            getDividerBetween = getDividerBetween,
            addBottomSpace = addBottomSpace,
        ) {
            val itemMap = items.associateBy { getId(it) }
            val valueString =
                if (selectedId == null) ""
                else {
                    val item = itemMap[selectedId]
                    if (item != null) (getCollapsedItemText ?: getItemText)(item)
                    else "Invalid ID $selectedId"
                }
            TextField(
                value = valueString, // pulled out just to improve code formatting
                onValueChange = { /* No-op, handled by dropdown */ },
                label = label,
                readOnly = true,
                enabled = false, // so Modifier.clickable() works
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.content_description_expand),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        // Not 100% sure about this rotation behaviour, but e.g. the screenshot of
                        // the "Text field" configuration at the bottom of
                        // https://m3.material.io/components/menus/specs seems to show this, so
                        // let's go with it.
                        modifier = Modifier.rotate(if (expanded) 180f else 0f),
                    )
                },
                modifier =
                    Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    },
                // ENHANCE: It isn't ideal to use expanded as a substitute for focus here, but it
                // doesn't look too bad in practice. Because we have to have the TextField disabled
                // in order to make it clickable, it doesn't seem to actually get focus as far as
                // onFocusChanged is concerned (even when it gets that "it's focus but it's not
                // focus" D-pad navigation focus).
                colors = if (enabled) myTextFieldColors(expanded) else TextFieldDefaults.colors(),
            )
        }
        // If we let TextField display supportingText itself it gets included in the bounding box
        // and the dropdown appears below the supportingText, whereas we want it to drop down over
        // the supportingText, "hanging off" the main TextField text box. So we jump through far too
        // many hoops to display it ourselves here.
        if (supportingText != null) {
            Box(modifier = Modifier.padding(start = menuLeftPadding, top = 4.dp)) {
                ProvideTextStyle(MaterialTheme.typography.bodySmall) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        supportingText.invoke()
                    }
                }
            }
        }
    }
}
