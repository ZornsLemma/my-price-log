@file:OptIn(ExperimentalMaterial3Api::class)

package app.zornslemma.mypricelog.ui.components.generalselector

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.ui.common.rememberSortedByLocale
import app.zornslemma.mypricelog.ui.components.FilteredTextField
import app.zornslemma.mypricelog.ui.components.createOnCandidateValueChangeMaxLength
import app.zornslemma.mypricelog.ui.maxSearchLength
import app.zornslemma.mypricelog.ui.screenHorizontalBorder
import app.zornslemma.mypricelog.ui.screenVerticalBorder

// ENHANCE: Unlike other re-usable composables in this app, here we pass the actual T to
// onItemSelected instead of an ID. I believe the original reason for this was an LLM suggestion
// that doing so would avoid race conditions, although I don't think this is likely to be a concern
// in practice for this app. This is a bit inconsistent, so maybe I should change consistently to
// one or the other.
@Composable
fun <T> GeneralSelectorScreen(
    stateHolder: GeneralSelectorScreenStateHolder<T>,
    navController: NavHostController,
    title: @Composable () -> Unit,
    getId: (T) -> Long,
    getName: (T) -> String,
    onAddClick: (() -> Unit)? = null,
    addContentDescription: String,
    onItemSelected: (T) -> Unit,
    showSearch: Boolean = false,
) {
    val dataList by stateHolder.dataFlow.collectAsStateWithLifecycle()

    val floatingActionButton: (@Composable () -> Unit) =
        if (onAddClick == null) {
            {}
        } else {
            @Composable {
                // The commented out options here would (I think) be MD3 compliant (picking the
                // "default" colour combination) but they seem to be the defaults anyway.
                FloatingActionButton(
                    onClick = dropUnlessResumed { onAddClick() }
                    // containerColor = MaterialTheme.colorScheme.primaryContainer,
                    // contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    // shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        // modifier = Modifier.size(24.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = addContentDescription,
                    )
                }
            }
        }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                        )
                    }
                },
            )
        },
        floatingActionButton = floatingActionButton,
    ) { innerPadding ->
        Column(
            // We apply innerPadding and a vertical screenBorder but no horizontal padding here so
            // the list can be edge-to-edge. The individual list items still have horizontal padding
            // between the screen edge and their text, but e.g. the ripple effect on click goes
            // right to the edge of the screen, which I think is how MD3 likes it. The vertical
            // screenBorder padding is arguably unnecessary, but although mostly invisible (in
            // practice the background colour of the top app bar and the screen content are the
            // same), it adds some consistency - particularly when the search field is present -
            // with the vertical spacing on other screens.
            //
            // We don't need Modifier.verticalScroll(rememberScrollState()) here - probably because
            // of the LazyColumn - and in fact adding it causes a crash.
            modifier =
                Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .padding(vertical = screenVerticalBorder)
                    .imePadding()
        ) {
            // ENHANCE: We could show a warning icon and/or some supporting text if nothing matches
            // the substring, rather than just showing an empty list.
            if (showSearch) {
                val searchString by stateHolder.searchStringFlow.collectAsStateWithLifecycle()
                FilteredTextField(
                    value = searchString,
                    onCandidateValueChange = createOnCandidateValueChangeMaxLength(maxSearchLength),
                    onValueChange = { stateHolder.searchStringFlow.value = it },
                    label = { Text(stringResource(R.string.label_search)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.content_description_search),
                            // tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription =
                                stringResource(R.string.content_description_clear_search_text),
                            modifier =
                                Modifier.clickable {
                                    stateHolder.searchStringFlow.value = TextFieldValue("")
                                },
                        )
                    },
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = screenHorizontalBorder)
                            .padding(bottom = 8.dp),
                    singleLine = true,
                )
            }

            val dataListSorted = dataList.rememberSortedByLocale { getName(it) }
            Box(modifier = Modifier.fillMaxWidth()) {
                dataList.forEach { println("Item: $it, ID: ${getId(it)}") }
                LazyColumn {
                    items(items = dataListSorted, key = { item -> getId(item) }) { item ->
                        GeneralSelectorScreenListItem(
                            name = getName(item),
                            onItemSelected = dropUnlessResumed { onItemSelected(item) },
                        )
                    }
                }
            }

            // ENHANCE: We could offer support for deleting items here, e.g. via "swipe to reveal a
            // bin icon". This would probably be more useful if this code is re-used. In this app,
            // deleting is rare and potentially scary and we prefer to hide it away a bit by putting
            // it on the individual edit screens.
        }
    }
}

// This is only used once and I'd like to inline it, as if anything it obfuscates what's happening
// rather than simplifying the code. But inlining causes problems with dropUnlessResumed and the
// possible workarounds feel worse than just keeping this function.
@Composable
private fun GeneralSelectorScreenListItem(name: String, onItemSelected: () -> Unit) {
    ListItem(headlineContent = { Text(name) }, modifier = Modifier.clickable { onItemSelected() })
}
