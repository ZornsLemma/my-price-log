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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.view.WindowCompat

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

// TODO: This probably needs to track state/events via parents
// TODO: A final version of this might want an internal (database, not linear) ID for each item and it might expose that ID as well as/instead of the associated String to the caller, but the ID is of course invisible to the UI
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
                            isExpanded = !isExpanded
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("TODO2", "TODO2")
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
                        ComboBox("Label", "Value", onValueChange = {}, content = listOf("c1", "c2"))
                        Box(modifier = Modifier.pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    Log.d("POINTER", "Got pointer: $event")
                                    focusManager.clearFocus()
                                }
                            }
                        }) {
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
