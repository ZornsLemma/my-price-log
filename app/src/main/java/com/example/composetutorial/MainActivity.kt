package com.example.composetutorial

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.view.WindowCompat

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

// This composable provides the at-a-glance status of an item at a particular source. It won't always be visible because we may not have a current source, but when we do this should provide "most" of what a user wants to know:
// - is the item well-priced?
// - do we have an up-to-date price for this item?
// - make it easy for the user to confirm our current price or update it
// - (borderline?) do we have up-to-date prices for other sources? if not it's hard to know if this is well-priced or not no matter how up to the date the price at this source is.
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ItemSourceInfo() {
    // TODO: Do we want any kind of "heading" or not? We may want some simple dividers, but those would be provided by the surrounding composables. Gut feeling is we don't want a heading, but think about it.
    var expanded by remember { mutableStateOf(false) }
    var currentUnit by remember { mutableStateOf("100g") }
    Row {
        Column {
            Row {
                Text("£5.75 for 250g (£2.30/")
                Box {
                    Row(
                        modifier = Modifier.clickable { expanded = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(text = currentUnit, style = MaterialTheme.typography.bodyLarge)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select unit"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                        var availableUnits = listOf("100g", "kg", "oz")
                        availableUnits.forEach { selectionOption ->
                            DropdownMenuItem(text = { Text(selectionOption) }, onClick = {
                                currentUnit = selectionOption
                                expanded = false
                            })
                        }
                    }
                }
                Text(")")
            }
            Text("Price confirmed 02/04/2025")
            Text("Competitively priced")
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
fun ComboBoxSample() {
    val options = listOf("Apple", "Banana", "Cherry")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            value = selectedOptionText,
            onValueChange = { selectedOptionText = it },
            label = { Text("Select a fruit") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { selectionOption ->
                DropdownMenuItem(text = { Text(selectionOption) }, onClick = {
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

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    ComposeTutorialTheme {
        Conversation(SampleData.conversationSample)
    }
}

@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // We keep track if the message is expanded or not in this
        // variable
        var isExpanded by remember { mutableStateOf(false) }

        // We toggle the isExpanded variable when we click on this Column
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // TODO: Experiment with adding a Settings activity and make the dark/light/follow system available and grey out (with some text saying why) follow system on Android < 10
        val isDarkTheme = true /* TODO when (userThemePref) {
            ThemePreference.DARK -> true
            ThemePreference.LIGHT -> false
            ThemePreference.SYSTEM -> isSystemInDarkTheme()
        } */
        setContent {
            val focusManager = LocalFocusManager.current
            Box(Modifier.safeDrawingPadding())
            ComposeTutorialTheme(/* darkTheme = isDarkTheme */) {
                Scaffold(modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
                    Column(modifier = Modifier.padding(it)) {
                        ItemSourceInfo()
                        // ComboBox("Label", "Value", onValueChange = {}, content = listOf("thing 1", "thing 2"))
                        //ComboBoxSample()
                        // TODO: Just possible we don't need clearFocusOnTapOutside hack now, but
                        // we probably do. Try taking it out later. If we don't need it, we don't
                        // need the Box, which is just there to hook clearFocus... on.
                        Box(modifier = Modifier.clearFocusOnTapOutside()) {
                            Conversation(SampleData.conversationSample)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, name = "Dark Mode"
)
@Composable
fun PreviewMessageCard() {
    ComposeTutorialTheme {
        Surface {
            MessageCard(
                msg = Message("Lexi", "Take a look at Jetpack Compose, it's great!")
            )
        }
    }
}


data class Message(val author: String, val body: String)

// TODO: ChatGPT-inspired (and maybe do my own searches too) libraries that may solve the ComboBox issue:
// https://github.com/Breens-Mbaka/Searchable-Dropdown-Menu-Jetpack-Compose
// https://composablehorizons.github.io/ComposeTheme/
// https://github.com/szeweq/desktopose combo-box (last commit three years ago though, but maybe it's perfect...)

// TODO: ~/pc-sync/ai-chat-misc-to-move/grok-combo-box-and-alternate-ui.txt is a potentially
// valuable discussion, touching on some implementation ideas, design ideas (small tweaks and
// alternatives) etc and would probably be worth a re-read later.