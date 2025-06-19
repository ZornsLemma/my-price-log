@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.composetutorial

import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.material3.Button
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.rememberNavController
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

val screenBorder = 8.dp

// Start Grok chunk
@Composable
fun MainScreen() {
    // TODO: Note that because category and product use a TextField, they have the (I think) nice
    // property that the label expands into a sort of big hint when they are empty. We should
    // probably take advantage of this where having them empty makes sense - and it probably does
    // everywhere, even if it's rare, because the user *could* go and delete every single item in
    // the database in theory. TODO: We should make sure we have the same behaviour for Source,
    // because that actually *should* allow the user to easily set it to empty/none.
    var selectedCategory by remember { mutableStateOf("" /* "Dairy" */) }
    var selectedProduct by remember { mutableStateOf("" /* "Beans" */) }
    var showProductSheet by remember { mutableStateOf(false) }
    val categories = listOf("Demo", "Groceries (home)", "Groceries (Manchester)")
    val products = listOf("Beans", "Milk", "Bread", "Chicken" /* ... */)
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Category Selector
        // TODO: I am starting to think this is the best drop down menu implementation (needs renaming to avoid confusion). We probably don't *want* the primary colour underline highlight here, given that e.g. "buttons" get highlighted by an overall colour change as this does rather than an "underline" - TextFields obviously *do* get this underline for whatever reason known only to MD3 specs, but our TextField is not a "real" TextField so this "darken whole thing" approach is probably consistent
        // TODO: We *may* want to disable the on click ripple whatsit for this, based on how the "official" experimental ExposedDropdownMenuBox behaves - although having thoughts about it and chatted with Grok and ChatGPT, maybe this is *good* and it is a weird quirk of (my impl) of the experimental "official" one that is weird
        ExposedDropdownMenuBox(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            value = selectedCategory,
            onValueChange = { selectedCategory = it },
            label = { Text("Category") },
            items = categories
        )

        // Product Selector
        TextField(
            value = selectedProduct,
            onValueChange = { /* No-op, read-only */ },
            label = { Text("Product") },
            enabled = false, // TODO: this is necessary to make "clickable" work, it looks wrong but this is all an experimental hack anyway
            modifier = Modifier
                .fillMaxWidth()
                .clickable { Log.d("MyApp", "SPS"); showProductSheet = true },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Products",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },/* colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ) */
            colors = myTextFieldColors()
        )

        // Product Modal Bottom Sheet
        if (showProductSheet) {
            ModalBottomSheet(onDismissRequest = { showProductSheet = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search Products") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search, contentDescription = "Search"
                            )
                        })
                    LazyColumn {
                        items(products.filter {
                            it.contains(searchQuery, ignoreCase = true)
                        }) { product ->
                            ListItem(
                                headlineContent = { Text(product) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedProduct = product
                                        showProductSheet = false
                                    })
                        }
                    }
                }
            }
        }
    }
}

// Sometimes we have to make a TextField "enabled = false" for it to be clickable, so we need
// to override the colours to make it look like it is enabled.
@Composable
fun myTextFieldColors() = TextFieldDefaults.colors(
    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    disabledLabelColor = MaterialTheme.colorScheme.primary, // TODO: experimental, "should be" MaterialTheme.colorScheme.onSurfaceVariant, - the idea is that in practice we use these colors for our dropdown selectors and they are "interactive" and maybe deserve highlighting - I have mixed feelings about how good this looks, but will go with primary for the moment
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledIndicatorColor = /* indicatorColor */ MaterialTheme.colorScheme.onSurfaceVariant,
// focusedIndicatorColor = MaterialTheme.colorScheme.primary, // TODO NOT WORKING
)

@Composable
fun ExposedDropdownMenuBox( // TODO: Rename this if keep, it clashes confusingly with the m3 component
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    supportingText: @Composable (() -> Unit)? = null,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }/*
    val focusedColor   = MaterialTheme.colorScheme.error
    val unfocusedColor = MaterialTheme.colorScheme.tertiary //     onSurfaceVariant
    var indicatorColor by remember { mutableStateOf(unfocusedColor) } // TODO: ALL THIS STUFF ISN'T WORKING, I SUSPECT THE *FOCUS* ISN'T HITTING THE CONTROL AS IT'S DISABLED, BUT *SOMETHING* IS HITTING IT AND TOGGLING ITS COLOUR BUT IT ISN'T THIS, NOT SURE
    */
    var textFieldWidth by remember { mutableStateOf(0) }
    Box(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = { /* No-op, handled by dropdown */ },
            label = label,
            supportingText = supportingText,
            readOnly = true,
            enabled = false, // TODO HACK
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    /* TODO modifier = Modifier.rotate(if (expanded) 180f else 0f) */
                )
            },
            modifier = Modifier
                .clickable { Log.d("MyApp", "CATCLICK"); expanded = true }
                //.onFocusChanged { Log.d("MyApp", if (it.isFocused) "Foc" else "Unfoc"); indicatorColor = if (it.isFocused) focusedColor else unfocusedColor  }
                .fillMaxWidth()
                .onGloballyPositioned { coordinates -> textFieldWidth = coordinates.size.width },/* colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ) */
            colors = myTextFieldColors()
        )
        DropdownMenu(
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldWidth.toDp() })
                .background(MaterialTheme.colorScheme.surfaceContainer), // TODO: REDUNDANT?
            expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                // TODO: THE TEXT IN THIS DROPDOWN DOESN'T LEFT-ALIGN WITH THE PARENT TEXTFIELD
                DropdownMenuItem(text = {
                    Text(
                        item,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }, contentPadding = PaddingValues(start = 16.dp), onClick = {
                    onValueChange(item)
                    expanded = false
                })
            }
        }
    }
}
// End Grok chunk

// LabeledItem() attempts to mimic the label style of a TextField but for "read-only" content. It
// works best with a simple Text() child, but other things are possible.
@Composable
fun LabeledItem(
    label: String, modifier: Modifier = Modifier, content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        // Passing LocalTextStyle and LocalContentColor *tries* to influence these aspects of all
        // the content. Some components won't respect this, but many will, and if some don't it
        // does at least introduce a visual inconsistency which I might notice and fix, rather
        // than the content being consistent internally but the wrong size/colour.
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodyLarge,
            LocalContentColor provides MaterialTheme.colorScheme.onSurface
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFullScreenDialog(
    onDismiss: () -> Unit
) {
    // Use a ModalBottomSheet or a Dialog for full-screen effect
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("This is a full-screen dialog", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

// This composable provides the at-a-glance status of an item at a particular source. It won't always be visible because we may not have a current source, but when we do this should provide "most" of what a user wants to know:
// - is the item well-priced?
// - do we have an up-to-date price for this item?
// - make it easy for the user to confirm our current price or update it
// - (borderline?) do we have up-to-date prices for other sources? if not it's hard to know if this is well-priced or not no matter how up to the date the price at this source is.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ItemSourceInfo(onClickEdit: () -> Unit) {
    // TODO: Do we want any kind of "heading" or not? We may want some simple dividers, but those would be provided by the surrounding composables. Gut feeling is we don't want a heading, but think about it.
    var expanded by remember { mutableStateOf(false) }
    var currentUnit by remember { mutableStateOf("100g") }

    // fontSize/iconSize are used here so that the drop down icon scales correctly when the user
    // changes the system font size. (Even if we didn't do this, we'd still want to use a fixed
    // size() Modifier (16.dp works quite nicely at the default settings on my current emulator) to
    // improve the appearance, but it's nicer to take font size into account.)
    val fontSize = MaterialTheme.typography.bodyLarge.fontSize
    val iconSize = with(LocalDensity.current) { fontSize.toDp() }
    var selectedSource by remember { mutableStateOf("") }
    val sources = listOf("None", "Tesco", "Asda", "Sainsbury's Local", "Iceland")
    // TODO: Will we have a free-form text field on item-at-source? For eg things like noting the specific product to help find it again.
    // TODO: Will we have a "special offer"/"short term price" flag and maybe associated data? Gut feeling is no, how to handle expiry/deletion gets complex from UI and internal perspective, it's not as if the offer duration is usually clearly stated, free text note probably can be used for this among other things
    // TODO: Should we show free-form text or special offer information here?
    // TODO: Just possibly the card inside layout should be some kind of grid control rather than row+column?
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        // TODO: animateContentSize() is experimental. If I keep it, I may also want it on the lower card, which can change size when product changes (just not yet, in this mockup).
        // The odd padding here is because we want 8.dp at the left and right and 12.dp at the top and bottom to try to keep the square-ish
        // corners of the TextField away from the round-ish corners at the top of the card. Because the bottom of the card has two buttons
        // and these have "touchable but background colour" space around them to meet the minimum touch size (and we don't want to make them visually
        // larger), if we use 12.dp at the bottom we actually get a bit more because of that extra space "around" the buttons. So we manually
        // adjust the bottom padding to visually compensate for this while allowing the buttons to have their natural touch region.
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 8.dp)
        ) {
            // TODO: We need to allow this to be set to empty/None by the user - how best to do that? And if it is empty, we need to collapse all the stuff below it and replace it with a brief instructional string roughly "Select a store to see and edit product details" - check the ChatGPT discussion I saved for some wording
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                value = selectedSource,
                onValueChange = { selectedSource = if (it != "None") it else "" },
                label = { Text("Source") },
                supportingText = if (selectedSource != "") null else {
                    { Text("Select a source to view or change the price there") }
                },
                items = sources)
            if (selectedSource != "") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Price as sold"
                    ) { // TODO: quite like this, but maybe "Shelf price"?
                        Text("£5.75 for 250g" /*, color = MaterialTheme.colorScheme.onSurface*/)
                    }
                    LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Last checked") {
                        Text("5 days ago") // TODO: would it be helpful to color code this and/or show an icon ("!"?) if this is "old"? maybe even with an ascening amber/red "severity" (and correspondingly different icons?)
                    }
                    LabeledItem(/* modifier = Modifier.weight(1f), */ label = "Unit price") {
                        Row() {
                            Text("£2.30/")
                            Box {
                                Row(
                                    modifier = Modifier.clickable { expanded = true },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = currentUnit,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.alignBy(LastBaseline)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select unit",
                                        modifier = Modifier.size(iconSize /* 16.dp */)
                                    )
                                }
                                // TODO: I probably actually don't want this dropdown. It just *might* make sense to allow
                                // the unit to be temporarily changed here (some slightly contrived situation where we're
                                // looking at a new product on shelf and want to see if it's potentially cheaper but it
                                // uses a different unit price as shown on shelf, for example - but we're already not
                                // doing that well, if anything we want a "check new product" option which lets us enter
                                // its pack size and shelf price and compute unit price ourself, there may not be a unit
                                // price on shelf or it may not be correct if there's an offer), but it's far from clear,
                                // and if anything it might make more sense to have a screen-wide "temporarily use X as
                                // the unit price unit" setting which also affects the card with the cross-store prices
                                // on. I won't rip this out of the UI yet, but I suspect in a finished first version of
                                // the app this code will be gone, at least from specifically here.
                                DropdownMenu(
                                    expanded = expanded, onDismissRequest = { expanded = false }) {
                                    var availableUnits = listOf("100g", "kg", "oz")
                                    availableUnits.forEach { selectionOption ->
                                        DropdownMenuItem(
                                            text = { Text(selectionOption) },
                                            onClick = {
                                                currentUnit = selectionOption
                                                expanded = false
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
                // TODO: Notes row should probably just be omitted if there are no notes - this is read-only view
                // TODO: I suspect there's going to be inconsistent padding vertically with or without this, because "other" Rows around it will have 8dp on all sides the way they are currently specified, and if this is missing we'll get 2x8dp gap. But I can tweak this once the layout otherwise settles down (e.g. specify explicit top padding on top Row and bottom padding on bottom Row and do the rest consistently, or something)
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    LabeledItem("Notes") {
                        Text("Special offer price")
                    }
                    //Text("TODO DUMMY TO FILL SPACE", modifier=Modifier.padding(vertical=130.dp))
                }
                // TODO: Vertical spacing probably poor with or without notes field - needs tweaking/better "plan" for how to specify it - that said, the vertical space above this final row probably should be "a bit" larger than the space between the two "reporting our status" rows - the following row is a "summary plus action" row and does want some visual distinction
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row() {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Checked",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        ) // TODO: probably "primary"=good, default text color=neutral, "error"=bad
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Good price")
                    }
                    // TODO: *If* we consider "Confirm" to be the primary action (potentially users
                    // click it every time they buy this item), it should probably get the bottom
                    // right position, i.e. we should swap its position with "Edit".
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        // TODO: Confirm button sets last updated to "today" and turns itself into "Undo confirm" (or something) on being clicked, we should ideally make this as obvious as possible to the user, maybe some kind of animation
                        FilledTonalButton(onClick = {}, shape = MaterialTheme.shapes.small) {
                            Text("Confirm")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        FilledTonalButton(onClick = onClickEdit, shape = MaterialTheme.shapes.small) {
                            Text("Edit") // TODO: "Update"? (we do have a history-ish element, maybe)
                        }
                    }
                }
            }
        }
    }
}

// TODO: o4-mini code, review if keep
// TODO: I should probably display an arrow of some sort next to the column which controls the sort
// order, and I should probably make it clickable to reverse the order - the main thing being to
// visually indicate that the data is sorted, I don't think in practice changing the order is of
// much interest and I certainly don't see the need to allow sorting on other columns.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DataTable(
    header: List<String>,
    rows: List<List<String>>,
    columnWeights: List<Float> = List(header.size) { 1f }
) {
    // TODO: Is there an argument that the column headings should actually use the same appearance
    // as the labels like "Unit price" - they are arguably playing the same kind of informative
    // role and don't necessarily deserve the bold same-size (ish) text treatment they are currently
    // getting. Maybe this would look weird though - perhaps there is a strong expectation that
    // "tables" do have bold header rows which are "at least as big" as the data rows. Not sure,
    // need to experiment. It *might* be reasonable to use primary for the header background,
    // although I am not sure - and certainly if we did, we would not want to mix that with the
    // small text label style used by LabelledText(). To some extent, I need to consider the
    // appearance of the whole screen in deciding this.
    // TODO: The header should remain fixed even when the list scrolls. - this is now done, but the header now loses contrast when a dark zebra row is adjacent
    // TODO: Should the header and the last item of the list have rounded corners? I am not sure. Probably best square corners TBH.

    Column() {
        // optional header
        Surface(color = MaterialTheme.colorScheme.surfaceContainerHighest) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    header.forEachIndexed { index, title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .weight(columnWeights[index])
                                .padding(end = 8.dp)
                        )
                    }
                }
                HorizontalDivider(
                    thickness = 1.dp, color = MaterialTheme.colorScheme.outline /* Variant */
                )
            }
        }


        // data rows
        rows.forEachIndexed { rowIndex, rowData ->
            // TODO: Zebra-striping is experimental, not sure how I feel about it. Even if we do keep it, note that the header row has a somewhat inconsistent colour - it is darker than the "surface" rows (which is probably good) but lighter than the surfaceVariant rows, which is probably bad (this comment is assuming a light mode display)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 0.dp, horizontal = 0.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh /* if (rowIndex % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant */)
            ) {
                // TODO: This inner Row is only here for the zebra-striping - if we get rid of it, we can do without it (and move the padding to the parent Row)
                Column {
                    if (rowIndex > 0) {
                        HorizontalDivider(
                            thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }

                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                        rowData.forEachIndexed { index, cell ->
                            Text(
                                text = cell,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .weight(columnWeights[index])
                                    .padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// TODO: This probably needs to track state/events via parents
// TODO: A final version of this might want an internal (database, not linear) ID for each item and it might expose that ID as well as/instead of the associated String to the caller, but the ID is of course invisible to the UI
// TODO: OK - this just may fix my problems and/or simply be "right" - should I be using ExposedDropdownMenu(Box) - this may practically *be* a standard combo box? (see e.g.https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-exposed-dropdown-menu-box.html - TBH documentation on this feels oddly sparse) - FWIW https://m3.material.io/components/menus/guidelines under "Filtering" looks like precisely what I want
// TODO: Next problem to solve - if you have cursor in the combobox text and click the icon to make
// the dropdown appear, the cursor disappears (fine). If you click the icon again, the cursor
// reappears (also fine). But if instead of clicking the icon, you click "off" it (let's say in th
// middle of the screen, to be clear) the dropdown disappears (good) but the cursor remains in the
// label (bad). I have been unable to get ChatGPT or Grok to solve this. Note that as per comment
// below, you cannot trivially distinguish these two cases because we do *not* see a click on the
// dropdown icon when we close it. For what it's worth, both ChatGPT and Grok suggest that the
// dropdown is a Popup which overlays the entire screen and effectively blocks the
// clearFocusOnTapOutside code from executing in this case. (Not that it would necessarily do the
// right thing anyway, because it would clear the focus in all cases, which we don't want.)
// (Aside: since I *actually* want to be showing a filtered list of options below the combobox as
// soon as it contains any text at all, it is possible that this precise problem won't exist in
// a final version of this code - the situation where the cursor is in the combobox but the dropdown
// is not shown probably isn't possible. I suspect it will be instructive to try to address this
// anyway to build confidence with forcing Compose to actually do things which are sensible despite
// its natural inclinations. Still, if solving this particular problem eludes me, it might be as
// well to push on with the filtering always-show-dropdown version I actually want and see how
// that goes. I do actually wonder if the dropdown arrow is necessary in this final version,
// although maybe - I'm not sure right now - having it there is helpful as offering one explicit
// way to get rid of the dropdown and go with whatever text is currently in the TextField. In reality
// it's probably not necessary to do that, because you can click anywhere on the screen - modulo
// worries about that activating a button - to get the same behaviour, but it is probably nice to
// have the hint that you can click the dropdown arrow to close the dropdown list, as well as have
// it as a known safe place you can click without other side effects.)
@Composable
fun ComboBox(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    content: List<String>, // TODO: poor name for var?
    modifier: Modifier = Modifier
) {
    // TODO: I suspect we may not want to pass all value changes in the text box through to the onValueChange provided by our parent. We are encouraging the user to type partial substrings which are meaningless to the parent, and it also doesn't really care anyway until we have "finished" and have a possibly-valid (but we may not) string, either because the user typed it or because they clicked it in the list. For now I am not even trying to call the parent and just ignoring the value they supply.
    var isExpanded by remember { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Column() {
        Row(modifier = modifier) {
            TextField(
                label = { Text(label) },
                value = text,
                onValueChange = { newText -> text = newText },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            // Superficially we want "isExpanded = !isExpanded" here, but that's not
                            // how it works. Clicking on the icon in an attempt to close the
                            // dropdown actually just triggers onDismissRequest on the dropdown and
                            // this code never gets called, presumably because it is a click not on
                            // the dropdown. Of course the toggle code is not wrong, but the
                            // implication that it's called symmetrically is confusing when trying
                            // to get focus behaviour correct, so let's be explicit.
                            isExpanded = true
                        })
                })
        }
        // TODO: I may need to use LazyColumn inside DropdownMenu to get laziness, given my list could have approx 100 items. Not worrying about that just now.
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            for (item in content) {
                DropdownMenuItem(text = { Text(item) }, onClick = {
                    text = item
                    isExpanded = false
                    focusManager.clearFocus()
                })
            }
        }
    }
}

// TODO: ChatGPT code. Not tried to understand and I think it definitely has some flaws (which are
// not necessarily its fault) but want to play with this a bit anyway and see if it's viable.
// TODO: So, what's wrong with this?
// - on clicking, text box gets the cursor very briefly then loses it - it *may* be that there's
//   something inherent in "having the drop down on screen" which stops the text field having the
//   cursor.
// - after selecting an item from the drop down, the cursor remains active and the drop down disappears
// - the drop down is not aways present when the cursor is active. This is kind of the goal which
//   ties the above together, although it's valuable to note specific ways it can occur if we're
//   trying to fix it.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBoxSample(modifier: Modifier = Modifier) {
    val options = listOf("Apple", "Banana", "Cherry")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Apple") }

    ExposedDropdownMenuBox(
        modifier = modifier, expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            readOnly = true,
            // keyboardOptions = null,
            value = selectedOptionText,
            onValueChange = { selectedOptionText = it },
            label = { Text("Select a fruit") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                // TODO: This onKeyEvent is necessary to allow you to press Enter to open the
                // drop-down. I haven't tested it yet but Grok tells me this *won't* handle
                // a D-pad click and we may need to also recognize Key.DpadCenter to do that.
                // With this tweak, I suspect this implementation of a "no keyboard entry"
                // combo box is the nicest one going - since we are mostly using TextField
                // without active coercion and we have been able to leave it enabled, color
                // changes and keyboard navigation mostly seem to "just work".
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                        expanded = true
                        true // Consume the event
                    } else {
                        false // Let other events pass through
                    }
                }
            //.fillMaxWidth()
        )

        // TODO: The text in this dropdown doesn't left align with the text in the TextField
        ExposedDropdownMenu(
            // TODO: The font in this dropdown appears unnecessarily small - prob fixed
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer), // TODO: perhaps redundant?
            expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(text = {
                    Text(
                        selectionOption,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }, onClick = {
                    selectedOptionText = selectionOption
                    expanded = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewComboBox() {
    ComboBox(
        label = "Label3",
        value = "Value3",
        onValueChange = {},
        content = listOf("foo", "bar", "baz")
    )
}

@Preview
@Composable
fun TripleComboBox() {
    Row() {
        ComboBox(
            label = "Category",
            value = "Foo",
            onValueChange = {},
            content = listOf(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        ComboBox(
            label = "Item",
            value = "Bar",
            onValueChange = {},
            content = listOf(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        ComboBox(
            label = "Source",
            value = "Baz",
            onValueChange = {},
            content = listOf(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun TwoRowTripleComboBox() {
    Column() {
        Row() {
            ComboBox(
                label = "Category",
                value = "Foo",
                onValueChange = {},
                content = listOf(),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            ComboBox(
                label = "Source",
                value = "Baz",
                onValueChange = {},
                content = listOf(),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row() {
            ComboBox(
                label = "Item",
                value = "Bar",
                onValueChange = {},
                content = listOf(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ChatGPT wrote this for me after much wrangling. I 70% understand what's going on but there is
// definitely some voodoo here. Compose apparently has no real concept of clearing the focus on
// a TextField when we tap elsewhere on the screen. Instead, we need to arrange for this to happen
// ourselves. We try to wrap "elsewhere" in a Box and attach this modifier to it (although I think
// we could apply this modifier to multiple composables if we couldn't cover "elsewhere" in a single
// Box). Because it uses pointerInput() it gets to see pointer-related events *even if* one of the
// children (which get to see events first - they propagate child->parent) has consumed it, which is
// what we want. (It generally drives me nuts that tapping somewhere just to "close" some
// interaction also triggers new interaction if you tap something like a button, but this is
// apparently standard behaviour so we don't try to change it.)
//
// This still doesn't really work properly. If you drag to scroll the screen, that is picked up here
// and focus is cleared, which is apparently wrong. In practice I suspect this is fine for this app.
//
// The "proper" way to do this is apparently to explicitly call clearFocus() in *every* component on
// the screen which consumes clicks and which the user might choose to tap on when the TextField has
// focus. I think we would also need either this modifier or (maybe, and if so then it's probably
// cleaner, since pointerInput feels more wizard-level than onClick) an onClick handler on this
// "elsewhere" component to catch taps on non-interactive components like labels and actual empty
// space, but I am really not sure.
//
// I am thoroughly disgusted with this. I can't find any helpful discussion on the web generally and
// that such an apparently standard UI interaction requires sprinkling clearFocus() calls around
// every interactive component or convoluted hacks like this which bring their own corner cases
// (e.g. scrolling) feels wrong. But as far as I am able to tell, there is no official, clean
// solution to this and no one ever discusses it outside a couple of niche StackExchange questions
// which don't address the problem generally. I haven't even been able to find anyone saying that
// I shouldn't even want this style of interaction (clearing the focus on clicking elsewhere,
// even if that somewhere is itself an active element like a button) and to stop fighting the
// framework.
@Composable
fun Modifier.clearFocusOnTapOutside() = composed {
    val focusManager: FocusManager = LocalFocusManager.current
    pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent()
                if (event.changes.any { it.pressed && !it.previousPressed }) {
                    focusManager.clearFocus()
                }
            }
        }
    }
}


// TODO: TO INVESTIGATE (CHATGPT):
//Option A: Stay Traditional (zero fancy insets)
//✅ Don’t call enableEdgeToEdge()
//✅ Force system to do padding:
//
//WindowCompat.setDecorFitsSystemWindows(window, true)
//❌ Don’t manually add systemBarsPadding() or safeDrawingPadding()
//✅ Let Scaffold and standard layouts work as expected
//✅ Looks modern, but stays “out of the way”



@Composable
fun FullScreenDialog(onDismiss: () -> Unit) {
    /*
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) { */

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary /* background */)
            ) {
                Column {
                    Text("Full-Screen Dialog Content")
                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
    //}
}

@Composable
fun HomeScreen(navController: NavHostController) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var animatingDialog by remember { mutableStateOf(false) }

    // TODO: I added this Surface by analogy with the one in SettingsScreen, but it appears to have
    // no real effect - even if I set its color to Red or primary, nothing shows.
    // TODO: Actually it may or may not be this, but on the O6 at least there does seem to be a weird
    // extra background shade with a bit of the white background down the edges where the border is.
    // No - it is there, but even if I remove this surface it is still there. I will have to experiment further. Part of the issue may be that it's the top-level Nav thing which is responsible.
    // Surface(modifier = Modifier.fillMaxSize()/*, color=MaterialTheme.colorScheme.surface */) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("My App Name Here") },
                    actions = {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }) {
                            // TODO: FONTS AND PROB COLORS ON THIS LIST ARE PROB WRONG
                            DropdownMenuItem(
                                text = { Text("Edit product list") },
                                onClick = {
                                    menuExpanded = false
                                    // Handle navigation or action
                                })
                            DropdownMenuItem(
                                text = { Text("Edit categories") },
                                onClick = {
                                    menuExpanded = false
                                })
                            DropdownMenuItem(text = { Text("Settings") }, onClick = {
                                menuExpanded = false
                                navController.navigate("settings")
                            })
                        }
                    }
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = screenBorder)
                    .background(MaterialTheme.colorScheme.secondary) // TODO debug hack

                .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                MainScreen() // TODO: rename this

                androidx.compose.foundation.layout.Spacer(
                    modifier = androidx.compose.ui.Modifier.height(
                        8.dp
                    )
                )

                ItemSourceInfo(onClickEdit = { showEditDialog = true } )

                androidx.compose.foundation.layout.Spacer(
                    modifier = androidx.compose.ui.Modifier.height(
                        8.dp
                    )
                )

                // TODO: This mock data shows some questions:
                // - should we include Notes? If we do, should we maybe show the first line only (we can let the user put newlines in the text box, and that gives them some control over what shows in this list, albeit imperfectly). Or we could "..."-truncate the text to fit a single line here in the display. Or we could omit this - but I suppose if the note is "special offer price", that *is* helpful to see for "other" stores (the "selected" store's notes are shown in the other card always)?
                // - if we do include notes, since you can see the current source's notes in full in the other card, should we omit them from the table to save space? or apply special "ellipsis truncation" rules just to the current source' data even if we don't do this for others, or something like that? or is this going to cause confusion?
                // - should we duplicate the unit in the unit price column? we could make the header "Price/100g" or whatever. This might also help avoid column width problems as the data varies.
                // - we will probably want some kind of trailing icon and/or colourisation on the price (or the whole row?) to indicate at the very least "this price is very old" and/or "we have had to apply inflation-ish adjustments to this price"
                // - instead of showing the *user's* notes, we could replace the "Notes" column here with usually-empty stuff which perhaps comments on any icons we put on the price (e.g. "last updated 90 days ago" or "old price"), but this may not fit very nicely in the space available either
                // - should we show a rank on the table rows? probably not necessary really. it is likely to be fixed sort on unit price and it's not that long.
                // - it's not out of the question (but we wouldn't want to insist) the user can provide an icon for each *source* (there aren't that many), and we could use icons for the sources. That said, if they are in addition to the text names they do take up more space, and if they are instead of the text names that may not be super readable *even if* every source does have an icon, especially if these are "square" icons not arbitrary "company name as a logo bitmap" shaped things. Probably simplest to forget this and got with pure text.
                // That said, we are probably looking at 5-10 items in the list "realistic max" (scrolling will always be there as a fallback) but
                // I originally did think the list items might be "two rows high" so we don't have to stick to a precise "table" layout - it
                // can be a list of "double height info cards" if we prefer that. This doesn't necessarily change the decisions to be made here,
                // but things are slightly different if we go with this approach.
                // TODO: The "£/100g" (or whatever, when it's dynamically constructed) should have a contextDescription for screen readers which is "Price per 100g", so it gets read out properly. I think "Price per" is OK (better than "Pounds per", actually), because the rows themselves contain the currency symbol.
                val header = kotlin.collections.listOf("Source", "£/100g", "Notes")
                // TODO: With the £/100g header, it is arguably redundant/incorrect to include the £ on the data values, but I think it's a reasonable compromise for readability and use by non-technical users.
                val data = kotlin.collections.listOf(
                    kotlin.collections.listOf(
                        "Tesco",
                        "£2.13",
                        "Tesco Finest is actually cheapest"
                    ),
                    kotlin.collections.listOf("Sainsbury's Local", "£2.94", ""),
                    kotlin.collections.listOf("Asda", "£2.08", "KTC brand"),
                    kotlin.collections.listOf("Iceland", "£2.38", ""),
                    // …
                )

                // TODO: Price column should be right-aligned, of course
                androidx.compose.material3.Card(
                    modifier = androidx.compose.ui.Modifier
                        //.weight(1f, fill=false) // only component with weight, so fills all remaining space
                        .fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier.padding(
                            horizontal = 8.dp, vertical = 12.dp
                        )
                    ) {
                        DataTable(
                            header = header, rows = data,
                            // TODO: Manually tweaking these weights is annoying and risks not working for some user's set of sources. Being clever may help, but it's awkward given the somewhat free form source and the very free form notes.
                            columnWeights = kotlin.collections.listOf(1.6f, 1f, 2.2f)
                        )
                    }
                }
            }

            // MD3 spec sort of says that on Android we should be using an "expand" transition to
            // show this dialog, but after much discussion with an AI (because I can't find any
            // other source of advice), it might be better to slide in vertically from the bottom
            // and then out the same way. We do this vertically not horizontally because it is a
            // full screen dialog not a screen. TODO: I half wonder if I should try the expand.
            // TODO: No idea what proper MD3 animations should be here, just trying to get this to work at all for now
            // TODO: As the dialog will probably contain its own scaffold and topappbar, this animatedvisibility component should probably be outside our scaffold
            // TODO: If this two-bool approach works, maybe switch to a three-state enum type thing
            // TODO: https://github.com/JetBrains/compose-multiplatform/issues/4431
            // https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/ is linked to from issue 4431, suggesting it's "reputable"
            if (animatingDialog || showEditDialog) {
                Dialog(
                    onDismissRequest = { showEditDialog = false; animatingDialog = true },
                    properties = DialogProperties(usePlatformDefaultWidth = false))
                {
                    // This Box is crucial - without it, the "expand" animation starts from the bottom right of the screen, not the centre, despite our specified transformOrigin.
                    Box(modifier = Modifier.fillMaxSize()) {

                        val dialogWindow = getDialogWindow()
                        SideEffect {
                            dialogWindow.let { window ->
                                // Disable the standard scrim. As this is a full-screen dialog, it won't
                                // be visible once the animation has finished anyway, and it looks ugly
                                // to have the standard scrim appear instantly and then have our
                                // animation run. I don't think it makes sense to try to add our own
                                // animated scrim, since our dialog already has an opaque full screen
                                // background which will be animated in/out.
                                window?.setDimAmount(0f)
                                // window?.setWindowAnimations(-1) TODO: needed?
                            }
                        }


                        var animateIn by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { animateIn = true }
                        val tweenDurationMillisEnter = 300 // TODO: maybe 250 as this is a very utilitarian anim?
                        val tweenDurationMillisExit = 250 // TODO: maybe 200, ditto?
                        AnimatedVisibility(
                            visible = animateIn && showEditDialog,
                            //enter = slideInVertically(animationSpec = tween(durationMillis = 2000)) { it }, // Adjust duration here
                            //exit = slideOutVertically(animationSpec = tween(durationMillis = 2000)) { it }, // Adjust duration here
                            enter = scaleIn(
                                animationSpec = tween(durationMillis = tweenDurationMillisEnter, easing = FastOutLinearInEasing),
                                initialScale = 0.0f, // Start from nothing
                                transformOrigin = TransformOrigin.Center // Expand from center
                            ) + fadeIn(
                                animationSpec = tween(durationMillis = tweenDurationMillisEnter,
                                easing = FastOutLinearInEasing)),
                            exit = scaleOut(
                                animationSpec = tween(durationMillis = tweenDurationMillisExit, easing = LinearOutSlowInEasing),
                                targetScale = 0.0f,
                                transformOrigin = TransformOrigin.Center
                            ) + fadeOut(
                                animationSpec = tween(durationMillis = tweenDurationMillisExit, easing = LinearOutSlowInEasing)),
                            modifier = Modifier.zIndex(1f) // TODO: necessary?
                        ) {
                            Box( // TODO: I think this Box is a legacy of experiments and not needed
                                modifier = Modifier.fillMaxSize()
                                /*
                            .graphicsLayer {
                                // Set alpha to 0f when scale is exactly 0f (initial frame)
                                // This hides the content during the prep phase
                                alpha = if (this.scaleX < 0.5f) 0f else 1f
                            }
                            */
                            ) {
                                FullScreenDialog(
                                    // TODO: onDismiss notused any more, has moved to above inline
                                    onDismiss = { showEditDialog = false; animatingDialog = true }
                                )

                                DisposableEffect(Unit) {
                                    onDispose {
                                        animatingDialog = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    //}
}

// https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/
@ReadOnlyComposable
@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window


@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {

                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = screenBorder)
                .background(MaterialTheme.colorScheme.primary) // TODO: debug hack

                // TODO: copied from Home, maybe want this but put it in when we do .verticalScroll(androidx.compose.foundation.rememberScrollState())
        ) {
                Text("TODO SETTINGS")
            }
        }
    //}
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Target SDK >=35 directly enables edge-to-edge (see e.g. https://stackoverflow.com/questions/79018063/trying-to-understand-edge-to-edge-in-android). We don't particularly want this, but we can work with it so we don't try to fight it.
        // We call it here to be explicit. TODO: I am far from clear but you can pass some arguments to enableEdgeToEdge(), which may have some relevant effect on older and/or newer platforms. For now I will keep it simple but if there are nightmarish inconsistencies on older versions of Android this might be part of the puzzle.
        enableEdgeToEdge()

        // TODO: Experiment with adding a Settings activity and make the dark/light/follow system available and grey out (with some text saying why) follow system on Android < 10
        val isDarkTheme = true /* TODO when (userThemePref) {
            ThemePreference.DARK -> true
            ThemePreference.LIGHT -> false
            ThemePreference.SYSTEM -> isSystemInDarkTheme()
        } */
        setContent {
            ComposeTutorialTheme {
                // TODO: Grok told me I could/should shove a DisposableEffect() in here to futz around with isAppearanceLightStatusBars. I don't particularly trust it, but let's make a note in csae this is part of fixing any problems we might see on older Android versions later.
                // TODO: OK, I have added this Surface here because I wondered if I "should" as well as/instead of the Surfaces wrapping
                // the individual screens. Honestly don't know any more. There might be some slightly odd colours on the O6 but maybe
                // they are just its theme. I will have to play around with this and maybe it will become clearer as I write more code
                // etc. fillMaxHeight() is perhaps a bit unusual here but I was experimenting and thought I'd leave it in for now.
                Surface(modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(), color = Color.Red /* TODO SHOULD BE MaterialTheme.colorScheme.background */) {
                        AppNavigation()
                }
            }
            /* TODO
            // 1) Set up NavController
            val navController = rememberNavController()
            // 2) Observe the current entry; this is a State<NavBackStackEntry?>.
            //    Any time you navigate, Compose will recompose here.
            // TODO: This back stack stuff works *BUT* it is probably wrong, because in order to get
            // decent animation appearance I probably need TopAppBar to be part of each individual Screen composable,
            // *not* a single shared TopAppBar which is context-sensitive.
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStackEntry?.destination?.route
            val showBackButton = currentDestination != "todorenameme"
            val title = when (currentDestination) {
                "todorenameme" -> "My App Name Here"
                "settings" -> "Settings"
                else -> "MISSING TITLE"
            }


            var menuExpanded by remember { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current
            //val navBackStackEntry by navController.currentBackStackEntryAsState()
            //val canNavigateBack = navBackStackEntry?.previousBackStackEntry != null
            Box(Modifier.safeDrawingPadding()) {
                ComposeTutorialTheme(/* darkTheme = isDarkTheme */) {
                    Scaffold(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                        // TODO: Probably need to apply MD colors to topBar, just poss also font size but it may default to something sensible
                        topBar = {
                            TopAppBar(title = { Text(title) }, navigationIcon = {
                                // TODO: This is absolute 4o-mini voodoo, but it does seem to work,
                                // unlike just about every other solution I found on the web or which
                                // an AI gave me. Note that we also probably actually need to change the title according to the activity. I do wonder if I'm doing something massively wrong. One random code sample I saw on the web actually changed the TopAppBar each time.
                                if (showBackButton) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            }, actions = {
                                IconButton(onClick = { menuExpanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                                }

                                DropdownMenu(
                                    expanded = menuExpanded,
                                    onDismissRequest = { menuExpanded = false }) {
                                    // TODO: FONTS AND PROB COLORS ON THIS LIST ARE PROB WRONG
                                    DropdownMenuItem(
                                        text = { Text("Edit product list") },
                                        onClick = {
                                            menuExpanded = false
                                            // Handle navigation or action
                                        })
                                    DropdownMenuItem(
                                        text = { Text("Edit categories") },
                                        onClick = {
                                            menuExpanded = false
                                        })
                                    DropdownMenuItem(text = { Text("Settings") }, onClick = {
                                        menuExpanded = false
                                        navController.navigate("settings")
                                    })
                                }

                            })
                        }) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "todorenameme",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("todorenameme") { TodoRenameMe(navController) }
                            composable(
                                "settings",
                                enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) },
                                exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) }) { SettingsScreen(navController) }
                        }
                    }
                }
            }*/
        }
    }
}

// TODO: ChatGPT magic
// TODO: Random Grok suggestion to maybe play with later: Use LinearOutSlowInEasing for enter transitions (starts fast, slows down) and FastOutLinearInEasing for exit transitions (starts slow, speeds up) to make the slide feel natural.
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        // TODO!? modifier = Modifier.padding(innerPadding)
    ) {
        // TODO: The animation here is complete voodoo. This is a tweaked version of https://stackoverflow.com/questions/65643015/animating-between-composables-in-navigation-with-compose
        // and does actually seem to more-or-less behave (and consistently too). I didn't want to force 700ms, this feels a smidge fast at the (I think) default 300 but I think it is OK.
        // No, no, it isn't consistent. Sometimes the back animation is much faster than others. Not a clue. Not a f* clue.

        composable("home",
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            /*
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            } */
        ) {
            HomeScreen(navController)
        }
        val tweenDurationMillisEnter = 700; // TODO: should probably be 300 in final version
        val tweenDurationMillisExit = 700; // TODO: should probably be 250 in final version
        composable(
            "settings",
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,

                    animationSpec = tween(durationMillis = tweenDurationMillisEnter, easing = LinearOutSlowInEasing),
                )

            },
            /* TODO This is probably not used as this is a "leaf" screen
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(2000)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(2000)
                )
            },
            */
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = tweenDurationMillisExit, easing=FastOutLinearInEasing)
                )
            }
        ) {
            SettingsScreen(navController)
        }
    }
}

// TODO: ChatGPT-inspired (and maybe do my own searches too) libraries that may solve the ComboBox issue:
// https://github.com/Breens-Mbaka/Searchable-Dropdown-Menu-Jetpack-Compose
// https://composablehorizons.github.io/ComposeTheme/
// https://github.com/szeweq/desktopose combo-box (last commit three years ago though, but maybe it's perfect...)

// TODO: ~/pc-sync/ai-chat-misc-to-move/grok-combo-box-and-alternate-ui.txt is a potentially
// valuable discussion, touching on some implementation ideas, design ideas (small tweaks and
// alternatives) etc and would probably be worth a re-read later.


// TODO: It may be that on a real device more than 4.dp around the screen border looks nice. I should probably introduce a named constant for "screen border" and use that everywhere, it would probably help readability and it *is* a clearly defined concept I can identify, not some vague "it looks nicer with 8dp here" layout thing.

// TODO: I should probably lock the app to portrait mode

// TODO: There is no colour in the app at all when running on P7! Material You active without me realising it? I suspect so - look at Theme.kt, which appears to support dynamic colours. This isn't a problem as such, but should make a note about it, and I may want to offer a setting which allows newer Android versions to choose the app's native theme.

// TODO: There is an ugly animation glitch where the "T" of "TODO" on SettingsScreen hangs around far too long when transitioning between home and settings. This wasn't visible before I added the previously-lost border at the left and right of the main activity. It feels like this might be a clue to some problem with the animations but I am far from sure. In practice this will probably not be an issue if we add the same border to settings, but I am not sure that's a "fix" even if it does happen.

// TODO: In final version make sure e.g. going from home to settings to back doesn't lose category/product/source - I think it does now, but since it's all hacky that is fine in short term



// General note type comments to put somewhere appropriate in long term:
//
