@file:OptIn(ExperimentalMaterial3Api::class)

package app.zornslemma.mypricelog.ui.screens.home

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.Item
import app.zornslemma.mypricelog.data.Source
import app.zornslemma.mypricelog.domain.AgeClass
import app.zornslemma.mypricelog.domain.AugmentedPrice
import app.zornslemma.mypricelog.domain.PriceAnalysis
import app.zornslemma.mypricelog.domain.PriceJudgement
import app.zornslemma.mypricelog.domain.createCurrencyFormat
import app.zornslemma.mypricelog.domain.getMeasurementUnitsOfSameQuantityTypeAndUnitFamily
import app.zornslemma.mypricelog.domain.withFriendlyDenominator
import app.zornslemma.mypricelog.ui.common.AsyncOperationStatus
import app.zornslemma.mypricelog.ui.common.LoadState
import app.zornslemma.mypricelog.ui.common.formatPrice
import app.zornslemma.mypricelog.ui.common.isNotBusy
import app.zornslemma.mypricelog.ui.common.rememberSortedByLocale
import app.zornslemma.mypricelog.ui.common.sortedByLocale
import app.zornslemma.mypricelog.ui.components.AsyncOperationErrorAlertDialog
import app.zornslemma.mypricelog.ui.components.CardTitle
import app.zornslemma.mypricelog.ui.components.CellAlignment
import app.zornslemma.mypricelog.ui.components.DataTable
import app.zornslemma.mypricelog.ui.components.LabeledItem
import app.zornslemma.mypricelog.ui.components.MyDropdownMenuItem
import app.zornslemma.mypricelog.ui.components.MyExposedDropdownMenuBox
import app.zornslemma.mypricelog.ui.components.OnAppLifecycleEvent
import app.zornslemma.mypricelog.ui.components.OverflowMenu
import app.zornslemma.mypricelog.ui.components.PackPriceAndSizeRow
import app.zornslemma.mypricelog.ui.components.myTextFieldColors
import app.zornslemma.mypricelog.ui.listItemHorizontalPadding
import app.zornslemma.mypricelog.ui.maxNavigationDrawerWidth
import app.zornslemma.mypricelog.ui.oneLineListItemHeight
import app.zornslemma.mypricelog.ui.screenHorizontalBorder
import app.zornslemma.mypricelog.ui.screenVerticalBorder
import app.zornslemma.mypricelog.ui.spinnerDelayMillis
import app.zornslemma.mypricelog.ui.storePriceGridGutterWidth
import app.zornslemma.mypricelog.ui.storePriceGridLeftColumnWeight
import app.zornslemma.mypricelog.ui.storePriceGridRightColumnWeight
import java.time.Duration
import java.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    onEditPriceClick: (HomeScreenUiContent) -> Unit,
    onItemSearchClick: (HomeScreenUiContent) -> Unit,
    onViewHistoryClick: (HomeScreenUiContent) -> Unit,
    onSelectDataSetClick: (HomeScreenUiContent) -> Unit,
    onSelectItemClick: (HomeScreenUiContent) -> Unit,
    onSelectSourceClick: (HomeScreenUiContent) -> Unit,
    onSettingsClick: () -> Unit,
) {
    // In order to minimise jank, we want the previous UI state to be available during the *very
    // first composition* when this screen is re-entered (e.g. after navigating back from another
    // screen).
    //
    // If the first composition is based on null data, even if we manage to recompose with
    // up-to-date data before the first frame, there can still be visual jank: animated components
    // may animate themselves from their initial "null" size to a "non-null" layout. If the very
    // first composition sees non-null data, there's no animation - which is what we want.
    //
    // This is particularly important when returning from a screen that was overlaid on top of this
    // one (via Navigation's backstack), where the user expects this screen to "still be there" —
    // not to visibly reinitialise.
    //
    // This is addressed by having the ViewModel hold the UI state in a hot flow, so when we
    // return to this composable after having navigated elsewhere, the correct state is available
    // for the very first frame.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val (loading, uiContent) = uiState

    if (uiContent.dataSetIdState is LoadState.Loading) {
        // Just leave the home screen blank until we get the first async population of the selected
        // data set. We don't want to briefly show the scaffold with the app title before flashing
        // over to the current collection, nor do we want to show the "no collection is selected"
        // fallback texts.
    } else {
        val asyncOperationStatus by viewModel.asyncOperationStatus.collectAsStateWithLifecycle()
        // Unlike GeneralEditScreen(), we don't try to trap "back" and show a busy snackbar. We
        // probably could but:
        // - The data being saved here is "just" a confirm/undo confirm, it's not quite so critical
        //   or "user has put effort into this data entry" as in GeneralEditScreen.
        // - "Back" from the home screen would leave the app. It's not so clear we should even try
        //   to stop the user doing that.
        // - The user can use the home or overview buttons/gestures to leave the app, and we
        //   probably can't and almost certainly shouldn't trap those if we are saving. (They can
        //   also do this during GeneralEditScreen too. It's just that there "back" has an in-app
        //   meaning and is a particularly expected case where we can reasonably interfere.)
        // - A slow save is extremely unlikely anyway.
        val dataSetListSorted = uiContent.dataSetList.rememberSortedByLocale { it.name }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        LaunchedEffect(navBackStackEntry) {
            if (navBackStackEntry == null) {
                // This screen has been navigated away from.
                viewModel.previousPrice.value = null
            }
        }
        HomeScreenNavigationDrawer(
            drawerState,
            uiContent.dataSet,
            dataSetListSorted,
            onSelectedDataSetIdChange = { it: Long ->
                viewModel.previousPrice.value = null
                viewModel.setSelectedDataSetId(it)
            },
        ) {
            HomeScreenScaffold(
                drawerState,
                uiContent.dataSet,
                onSelectDataSetClick = { onSelectDataSetClick(uiContent) },
                onSelectItemClick = { onSelectItemClick(uiContent) },
                onSelectSourceClick = { onSelectSourceClick(uiContent) },
                onSettingsClick = onSettingsClick,
                asyncOperationStatus = asyncOperationStatus,
            ) { innerPadding ->
                HomeScreenContent(
                    viewModel,
                    uiContent,
                    onSelectedSourceIdChange = { it: Long ->
                        viewModel.previousPrice.value = null
                        viewModel.setSelectedSourceId(it)
                    },
                    onEditPriceClick = { onEditPriceClick(uiContent) },
                    onItemSearchClick = { onItemSearchClick(uiContent) },
                    onViewHistoryClick = { onViewHistoryClick(uiContent) },
                    asyncOperationStatus = asyncOperationStatus,
                    innerPadding = innerPadding,
                )
            }
        }
        HomeScreenStateManager(viewModel, loading, asyncOperationStatus)
    }
}

// ENHANCE: Should this have a (fairly rapid) fade in and/or fade out? I am not sure. It's not a
// massive deal given how little I expect it to actually be visible, but I might use it in other
// situations and it might be a nice little bit of polish. If we do fade, remember it probably needs
// to be quick, since it won't even start to fade in until ~150ms has elapsed, and the query could
// return any millisecond now and the scrim disappear before it even got to full opacity. It might
// be that since the scrim is translucent, it looks OK to just pop in. We could also *force* the
// scrim to last for at least the (short, 80ms?) fade in time, but that feels ridiculous -
// especially since it is then only just visible at full "intensity" for one frame maybe before
// disappearing, and we're adding extra slowdown to the app (albeit it might *feel* smoother), and
// we have complex logic to deal with this already unlikely case. I suspect for this specific app
// this is overkill.
@Composable
private fun ScrimWithSpinner(visible: Boolean, delayMillis: Long? = null) {
    if (visible) {
        var showScrim by remember { mutableStateOf(false) }

        if (delayMillis != null) {
            LaunchedEffect(Unit) {
                delay(delayMillis)
                showScrim = true
            }
        } else {
            showScrim = true
        }

        if (showScrim) {
            val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            var pendingBackPressed by rememberSaveable { mutableStateOf(false) }
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = {
                    // We are trying to emulate the user pressing the back button here.
                    // navController.popBackStack() empirically doesn't work, I think because it's
                    // for our internal back stack. The idea is that if the activity wasn't blocked
                    // by the spinner, the user could go back to some other activity (outside our
                    // app, probably), and we should still allow that while the spinner is up. I
                    // don't know if the debounce via pendingBackPressed is really necessary, I'm
                    // just playing it safe.
                    if (!pendingBackPressed) {
                        pendingBackPressed = true
                        dispatcher?.onBackPressed()
                    }
                },
                properties =
                    PopupProperties(
                        focusable = true, // prevent touches from going through
                        dismissOnBackPress = true,
                        dismissOnClickOutside = false,
                    ),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun HomeScreenNavigationDrawer(
    drawerState: DrawerState,
    dataSet: DataSet?,
    dataSetListSorted: List<DataSet>,
    onSelectedDataSetIdChange: (Long) -> Unit,
    content: @Composable () -> Unit,
) {
    // ENHANCE: Navigation drawer is being deprecated in favour of expanded navigation rail in
    // Material 3 Expressive from May 2025. However, it appears to be a rotten fit for my
    // requirements here - it wants (in its non-expanded form) to be permanently on screen, and I
    // don't have the space, and it seems to be intended for "a few" designer-selected things, not
    // maybe 5-10+ user-defined categories. It also seems to want to live at the bottom of the
    // screen on a portrait smartphone layout. So I am going to stick with the navigation drawer for
    // now.

    val coroutineScope = rememberCoroutineScope()

    // ENHANCE: The navigation drawer appears to flicker in very briefly on the first composition
    // when the app is opened "cold". ChatGPT and Grok both tell me this is a known issue and offer
    // workarounds which don't work at all. I will just live with it for now. (Not composing
    // ModalNavigationDrawer until the first time the user clicks on the hamburger menu sort of
    // works, but the first appearance of the drawer is then ugly/badly animated somehow, so it's
    // probably worse than the problem it's trying to fix.)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // We cap the drawer width at 2/3 of the screen width because although it's not MD3
            // standard, I really don't like the default behaviour of it taking the full screen
            // width on a portrait smartphone. If nothing else, that makes how to dismiss it feel
            // less discoverable.
            ModalDrawerSheet(
                modifier =
                    Modifier.wrapContentWidth()
                        .widthIn(
                            max =
                                min(
                                    LocalWindowInfo.current.containerSize.width.dp * 2f / 3f,
                                    maxNavigationDrawerWidth,
                                )
                        )
            ) {
                Column {
                    Box(
                        modifier =
                            Modifier.height(oneLineListItemHeight)
                                .padding(start = listItemHorizontalPadding),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = stringResource(R.string.label_data_set),
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                    LazyColumn {
                        items(dataSetListSorted) { item ->
                            val selected = dataSet?.id == item.id
                            NavigationDrawerItem(
                                modifier =
                                    Modifier.padding(horizontal = 12.dp)
                                        .height(oneLineListItemHeight),
                                label = {
                                    Text(
                                        item.name,
                                        // color = if (selected)
                                        // MaterialTheme.colorScheme.onSecondaryContainer else
                                        // MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    coroutineScope.launch {
                                        onSelectedDataSetIdChange(item.id)
                                        drawerState.close()
                                    }
                                },
                            )
                        }
                    }
                }
            }
        },
    ) {
        content()
    }
}

@Composable
private fun HomeScreenScaffold(
    drawerState: DrawerState,
    dataSet: DataSet?,
    onSelectDataSetClick: () -> Unit,
    onSelectItemClick: () -> Unit,
    onSelectSourceClick: () -> Unit,
    onSettingsClick: () -> Unit,
    asyncOperationStatus: AsyncOperationStatus,
    content: @Composable (innerPadding: PaddingValues) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                // We will almost always always have a DataSet to show the name of but we might as
                // well show the app name if we don't.
                title = { Text(dataSet?.name ?: stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(
                        enabled = asyncOperationStatus.isNotBusy(),
                        onClick = { coroutineScope.launch { drawerState.open() } },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription =
                                stringResource(R.string.content_description_open_drawer),
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = asyncOperationStatus.isNotBusy(),
                        onClick = { menuExpanded = true },
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.content_description_menu),
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        MyDropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_item_edit_data_sets)) },
                            onClick = {
                                menuExpanded = false
                                onSelectDataSetClick()
                            },
                        )
                        MyDropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_item_edit_items)) },
                            enabled = dataSet != null,
                            onClick = {
                                menuExpanded = false
                                onSelectItemClick()
                            },
                        )
                        MyDropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_item_edit_sources)) },
                            enabled = dataSet != null,
                            onClick = {
                                menuExpanded = false
                                onSelectSourceClick()
                            },
                        )
                        MyDropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_item_settings)) },
                            onClick = {
                                menuExpanded = false
                                onSettingsClick()
                            },
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun HomeScreenStateManager(
    viewModel: HomeViewModel,
    loading: Boolean,
    asyncOperationStatus: AsyncOperationStatus,
) {
    var showErrorDialogMessage by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // We use buffer() here because we want to update() while we are already collecting; we
        // might get a deadlock otherwise.
        viewModel.asyncOperationStatus.events.buffer().collect { event ->
            when (event) {
                AsyncOperationStatus.Busy -> {
                    // We expect the operation to complete quickly so we don't want the visual
                    // distraction of a progress indicator appearing straight away. Let the progress
                    // indicator kick in after a short delay if we're still here waiting.
                    delay(spinnerDelayMillis)
                    // The state might not be busy any more, so check first before updating to avoid
                    // a race condition.
                    if (viewModel.asyncOperationStatus.state.value == AsyncOperationStatus.Busy) {
                        viewModel.asyncOperationStatus.update(AsyncOperationStatus.BusyForAWhile)
                    }
                }

                is AsyncOperationStatus.Success -> {
                    viewModel.asyncOperationStatus.update(AsyncOperationStatus.Idle)
                }

                is AsyncOperationStatus.Error -> {
                    viewModel.asyncOperationStatus.update(AsyncOperationStatus.Idle)
                    showErrorDialogMessage = event.message
                }

                else -> {}
            }
        }
    }

    // We use this scrim with spinner to handle the (unlikely) cases where:
    // - The initial data load takes a long time.
    // - Saving a confirm/undo confirm to the database takes a long time.
    //
    // The latter could be handled via showing a spinner on the confirm/undo confirm button itself
    // (and continuing to disable all controls while waiting for the save to complete, as we already
    // do), but for such an unlikely case it seems best to keep things simple.
    //
    // In an ideal world the scrim with spinner for loading would cover only the lower two cards and
    // leave the rest of the home screen functional; it would be legitimate to abandon a slow load
    // and choose to load something different. (It would not be legitimate to do this while waiting
    // for a save to complete.) I experimented with doing this and although I think I
    // could have made it work, it felt incredibly brittle and likely to go wrong depending on
    // Android version and things like edge-to-edge and the SDK implementing that differently on
    // different Android versions etc. Given how rarely we expect the spinner to appear at all (and
    // therefore also how little testing it would get), it seemed best to go with this relatively
    // simple full screen spinner. (It is just possible I had some buggy/sub-optimal setup of the
    // higher level composables which made this seem harder than it should have been, but I'm not
    // sure.)
    //
    // Note that we do not pass a delayMillis parameter here. The delay before the scrim appears is
    // implemented in the logic which sets the loading flag or BusyForAWhile state, so as soon as
    // either is true we want to show the scrim.
    ScrimWithSpinner(
        visible = loading || asyncOperationStatus == AsyncOperationStatus.BusyForAWhile
    )

    if (showErrorDialogMessage != null) {
        AsyncOperationErrorAlertDialog(
            onDismissRequest = { showErrorDialogMessage = null },
            message = showErrorDialogMessage!!,
        )
    }
}

@Composable
private fun HomeScreenContent(
    viewModel: HomeViewModel,
    uiContent: HomeScreenUiContent,
    onSelectedSourceIdChange: (Long) -> Unit,
    onEditPriceClick: () -> Unit,
    onItemSearchClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    asyncOperationStatus: AsyncOperationStatus,
    innerPadding: PaddingValues,
) {
    val dataSet = uiContent.dataSet
    val dataSetList = uiContent.dataSetList
    val source = uiContent.source
    val sourceList = uiContent.sourceList
    val item = uiContent.item
    val itemList = uiContent.itemList
    val priceAnalysis = uiContent.priceAnalysis

    var showDeletePriceConfirmDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(horizontal = screenHorizontalBorder, vertical = screenVerticalBorder)
    ) {
        if (dataSet == null) {
            // These are corner cases, caused by the current data set being deleted or all data
            // sets being deleted. It wouldn't technically hurt to show the normal screen content,
            // but it seems friendlier to explain what's going on.
            if (dataSetList.isEmpty()) {
                Text(stringResource(R.string.message_no_data_sets))
            } else {
                Text(stringResource(R.string.message_no_data_set_selected))
            }
        } else {
            ItemSourceSelector(
                asyncOperationStatus = asyncOperationStatus,
                source = source,
                sourceList = sourceList,
                item = item,
                itemList = itemList,
                onSelectedSourceIdChange = onSelectedSourceIdChange,
                onItemSearchClick = onItemSearchClick,
            )

            Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())

            // ENHANCE: I don't know if this is remotely correct, but ChatGPT suggested:
            //     var lastFoo by remember { mutableStateOf<Foo?>(null) }
            //     if (foo != null) lastFoo = foo
            // and then using:
            //     lastFoo?.let { safeFoo ->
            // to compose the contents of the AnimatedVisibility. This (might) give us consistent
            // appearance as we animate out without requiring actual ability to handle null
            // source/item inside the content, and would (if this works) actually make things mildly
            // *less* janky as the content would be *the same* not some null-based approximation.
            // But there may well be subtleties.
            AnimatedVisibility(visible = item != null && source != null) {
                Column {
                    ItemSourceInfoLive(
                        viewModel = viewModel,
                        asyncOperationStatus = asyncOperationStatus,
                        dataSet = dataSet,
                        item = item,
                        source = source,
                        augmentedPrice =
                            priceAnalysis.augmentedPriceList.singleOrNull {
                                it.basePrice.sourceId == source?.id
                            },
                        onEditPriceClick = onEditPriceClick,
                        onViewHistoryClick = onViewHistoryClick,
                        onDeletePriceClick = { showDeletePriceConfirmDialog = true },
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ENHANCE: Just possibly we should use AnimatedVisibility here. However, it's not that
            // big a deal (but maybe do look into it) as the only way to have item be null is if
            // there *are* no items - unlike source, you can't deliberately set it to null. So this
            // is not a particularly common case and the animation would only be firing if we were
            // navigating back from an edit item screen where we've removed the last item or
            // something like that - it's not a "something changed within the screen itself"
            // animation like having source go between null and non-null is.
            if (item != null) {
                // Clicking on one of the items on this card selects its source, just as if it had
                // been selected via the source dropdown. This is technically redundant but I found
                // myself wanting to do it all the time to quickly see the details of a price, so
                // I've implemented it. (The dropdown is still needed, as it's the only way to
                // select sources which don't appear on the price comparison card.)
                PriceComparisonCard(
                    dataSet,
                    source,
                    priceAnalysis,
                    onClick = { onSelectedSourceIdChange(it) },
                    asyncOperationStatus,
                )
            }
        }
    }

    if (showDeletePriceConfirmDialog) {
        val augmentedPrice =
            priceAnalysis.augmentedPriceList.single { it.basePrice.sourceId == source?.id }
        DeletePriceConfirmDialog(
            viewModel,
            augmentedPrice,
            onDismissRequest = { showDeletePriceConfirmDialog = false },
        )
    }
}

@Composable
private fun DeletePriceConfirmDialog(
    viewModel: HomeViewModel,
    augmentedPrice: AugmentedPrice,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = null,
        title = { Text(stringResource(R.string.title_delete_price)) },
        // We could mention in the message here that you can recover the price using the history
        // (via "Edit as new price") but it's probably best not to over-explain. We just avoid any
        // scary messages about losing data.
        text = { Text(stringResource(R.string.message_confirm_delete_price)) },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.button_cancel)) }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    viewModel.deletePrice(augmentedPrice.basePrice)
                }
            ) {
                Text(stringResource(R.string.button_delete))
            }
        },
    )
}

// ENHANCE: We use primary/secondary/tertiary for good/OK/bad here. This isn't necessarily ideal
// but it does avoid problem where a fixed green/grey-or-amber/red set of colours clashes with
// a Material You-generated colour scheme.

@Composable
private fun GoodPriceIcon() {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = stringResource(R.string.content_description_good_price),
        tint = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun OkPriceIcon() {
    Icon(
        // ENHANCE: Maybe we could have a better icon for this. There is a vague hint of the UK "no
        // entry" road sign about this one which doesn't quite fit with "OK" for me.
        painter = painterResource(R.drawable.baseline_remove_circle_24),
        contentDescription = stringResource(R.string.content_description_ok_price),
        tint = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
private fun BadPriceIcon() {
    Icon(
        painter = painterResource(R.drawable.baseline_cancel_24),
        contentDescription = stringResource(R.string.content_description_bad_price),
        tint = MaterialTheme.colorScheme.tertiary,
    )
}

@Composable
private fun StalePriceIcon() {
    Icon(
        // Idea with this icon is "the 'fresh' period is over, we started a timer now it's stale".
        // ENHANCE: Just possibly create my own hourglass_middle icon and use that here instead? We
        // probably would keep to no icon for fresh rather than using hourglass top, but the
        // "tri-state metaphor" would maybe be a bit more obvious to users.
        painter = painterResource(R.drawable.baseline_hourglass_top_24),
        contentDescription = stringResource(R.string.content_description_stale_price),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun AncientPriceIcon() {
    Icon(
        // Idea with this icon is that the "stale timer" we started has now run out.
        painter = painterResource(R.drawable.baseline_hourglass_bottom_24),
        contentDescription = stringResource(R.string.content_description_ancient_price),
        tint = MaterialTheme.colorScheme.error,
    )
}

@Composable
private fun PriceComparisonCard(
    dataSet: DataSet,
    source: Source?,
    priceAnalysis: PriceAnalysis,
    onClick: (Long) -> Unit,
    asyncOperationStatus: AsyncOperationStatus,
) {
    // ENHANCE: We could make denominator user-selectable in this list header. If so it should
    // probably offer all the user's selected units of the right type, as the unit price dropdown on
    // ItemSourceInfo does.
    val locale = LocalConfiguration.current.locales[0]
    val currencyFormat = remember(dataSet, locale) { dataSet.createCurrencyFormat(locale) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        // The extra padding at the bottom compared to the top is to try to visually keep the sharp
        // corners of the table away from the rounded edges of the card.
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)
        ) {
            CardTitle(
                title = stringResource(R.string.title_price_comparison),
                subtitle = stringResource(R.string.subtitle_adjusted_for_discounts_and_age),
            )

            if (priceAnalysis.augmentedPriceList.isEmpty()) {
                Text(stringResource(R.string.message_no_prices_for_item_at_source))
            } else {
                // It may be technically incorrect to show the currency symbol both in the header
                // ("£/100g") and on the individual unit prices, but I think that for practical
                // purposes this is the least confusing way to show it. An "incomplete" header
                // ("/100g") feels unclear, as does having prices which aren't marked with a
                // currency symbol.

                // We use the unit family of the lowest unit price to pick a friendly
                // denominator and (of course) use that same denominator for all the unit
                // prices.
                val bestValueAugmentedPrice = priceAnalysis.augmentedPriceList.first()
                val headerUnitPriceDenominator =
                    remember(bestValueAugmentedPrice) {
                        val candidateDenominators =
                            dataSet.getMeasurementUnitsOfSameQuantityTypeAndUnitFamily(
                                bestValueAugmentedPrice.basePrice.quantity.unit,
                                includeDisplayOnly = true,
                            )
                        bestValueAugmentedPrice.unitPrice
                            .withFriendlyDenominator(
                                preferredUnit = bestValueAugmentedPrice.basePrice.quantity.unit,
                                currencyDecimalPlaces = currencyFormat.decimalPlaces,
                                candidateDenominators = candidateDenominators,
                            )
                            .denominator
                    }
                // We use "prefix or suffix" in the header because although the prefix or suffix
                // nature of a currency symbol in a locale matters in some other places, here it is
                // appearing in isolation *without* a price next to it.
                val header =
                    listOf(
                        stringResource(R.string.label_source),
                        "${currencyFormat.prefix ?: currencyFormat.suffix ?: ""}${headerUnitPriceDenominator.perSymbol}${stringResource(headerUnitPriceDenominator.symbol)}",
                        "",
                    )
                // We use a custom content description with the idea that a screen reader may not be
                // able to read "£/oz" out correctly, particularly when it appears like that with no
                // context rather than in a sentence. Using the word "Price" instead of the currency
                // shouldn't be a problem because each individual price includes the currency
                // symbol.
                // ENHANCE: It wouldn't surprise me in the least if the localisation here is
                // unusable for some languages, but let's start here and address issues as they
                // arise. We might be better off just having a localised string on each unit with
                // the full equivalent of "Price (per) <this unit full name>".
                val headerPriceContentDescription =
                    if (headerUnitPriceDenominator.perSymbol.trim().isNotEmpty()) {
                        stringResource(
                            R.string.content_description_header_price_per,
                            stringResource(headerUnitPriceDenominator.fullName),
                        )
                    } else {
                        stringResource(
                            R.string.content_description_header_price_no_per,
                            stringResource(headerUnitPriceDenominator.fullName),
                        )
                    }
                Log.d(TAG, "headerPriceContentDescription: $headerPriceContentDescription")

                val headerTextModifiers =
                    listOf(
                        Modifier,
                        Modifier.semantics { contentDescription = headerPriceContentDescription },
                        Modifier,
                    )
                val highlightRow =
                    priceAnalysis.augmentedPriceList
                        .indexOfFirst { it.sourceName == source?.name }
                        .takeIf { it != -1 }

                val columns =
                    remember(dataSet, locale, headerUnitPriceDenominator) {
                        listOf<@Composable (AugmentedPrice) -> Unit>(
                            { augmentedPrice -> Text(augmentedPrice.sourceName) },
                            { augmentedPrice ->
                                Text(
                                    formatPrice(
                                        augmentedPrice.unitPrice
                                            .withDenominator(headerUnitPriceDenominator)
                                            .numerator,
                                        dataSet,
                                        locale,
                                    )
                                )
                            },
                            // ENHANCE: We could add blank icons here so we have a column of
                            // "judgement" icons and a column of "age class" icons. Not sure if that
                            // would look better or not.
                            { augmentedPrice ->
                                Row {
                                    if (augmentedPrice.ageClass != AgeClass.ANCIENT) {
                                        when (augmentedPrice.priceJudgement) {
                                            PriceJudgement.NONE -> {}
                                            PriceJudgement.GOOD -> GoodPriceIcon()
                                            PriceJudgement.OK -> OkPriceIcon()
                                            PriceJudgement.BAD -> BadPriceIcon()
                                        }
                                    }

                                    if (augmentedPrice.ageClass == AgeClass.STALE) {
                                        StalePriceIcon()
                                    } else if (augmentedPrice.ageClass == AgeClass.ANCIENT) {
                                        AncientPriceIcon()
                                    }
                                }
                            },
                        )
                    }
                DataTable(
                    header = header,
                    headerTextModifiers = headerTextModifiers,
                    items = priceAnalysis.augmentedPriceList,
                    columns = columns,
                    highlightRow = highlightRow,
                    // ENHANCE: It might be better to calculate the space needed for the longest
                    // unit price and the longest number of icons, then assign anything left over to
                    // the source name. In practice these simple fixed weights seem to be working
                    // quite well for now.
                    columnWeights = listOf(1.7f, 1.1f, 0.8f),
                    columnAlignments =
                        listOf(CellAlignment.Start, CellAlignment.End, CellAlignment.Start),
                    onClick =
                        if (asyncOperationStatus.isNotBusy()) {
                            { augmentedPrice -> onClick(augmentedPrice.basePrice.sourceId) }
                        } else null,
                )
            }
        }
    }
}

@Composable
private fun PriceJudgementIndicator(priceJudgement: PriceJudgement) {
    Row {
        when (priceJudgement) {
            PriceJudgement.NONE -> {}
            PriceJudgement.GOOD -> {
                GoodPriceIcon()
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.message_good_price))
            }

            PriceJudgement.OK -> {
                OkPriceIcon()
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.message_ok_price))
            }

            PriceJudgement.BAD -> {
                BadPriceIcon()
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.message_bad_price))
            }
        }
    }
}

@Composable
private fun SourcePriceCardBody(
    viewModel: HomeViewModel,
    asyncOperationStatus: AsyncOperationStatus,
    dataSet: DataSet,
    augmentedPrice: AugmentedPrice?,
    onEditPriceClick: () -> Unit,
) {
    // ENHANCE: When the card expands, the button(s) on the "bottom" row of the card jump
    // down instead of animating smoothly "following" the bottom of the card - probably
    // because this layout is sort of "top to bottom". I suspect this can be worked around
    // by using a box and having most of the content inside a column with
    // .align(Alignment.TopStart) and then follow that by the button row with
    // .align(Alignment.BottomCenter) or something along these lines. The trouble with the
    // code as currently structured is that the buttons are generated in conditional code
    // and getting the right layout of composables isn't trivial. It is probably worth
    // tweaking this for visual polish - it might make things clearer anyway, e.g. if we
    // factor out some sub-composables - but I'm not going to get involved with it right
    // now. We may need to attach .animateContentSize() to the Card instead of the Column.
    // All this said, because the "Store" dropdown tends to obscure this card in practice,
    // this isn't all that noticeable.
    Column(
        modifier =
            Modifier.animateContentSize()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
    ) {
        CardTitle(stringResource(R.string.title_source_price))

        if (augmentedPrice == null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.message_no_price_for_item_at_source))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    FilledTonalButton(
                        onClick = onEditPriceClick,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(stringResource(R.string.button_add))
                    }
                }
            }
        } else {
            val price = augmentedPrice.basePrice

            PackPriceAndSizeRow(
                price.price,
                price.count,
                price.quantity,
                dataSet,
                asyncOperationStatus,
            )

            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(storePriceGridGutterWidth),
            ) {
                LabeledItem(
                    modifier = Modifier.weight(storePriceGridLeftColumnWeight),
                    label = stringResource(R.string.label_confirmed),
                ) {
                    RelativeTimeText(augmentedPrice)
                }

                Box(
                    modifier =
                        Modifier.weight(storePriceGridRightColumnWeight)
                            .fillMaxSize()
                            .align(Alignment.CenterVertically)
                ) {
                    PriceJudgementIndicator(augmentedPrice.priceJudgement)
                }
            }

            if (price.notes.isNotEmpty()) {
                Row(modifier = Modifier.padding(bottom = 8.dp)) {
                    LabeledItem(stringResource(R.string.label_notes)) { Text(price.notes) }
                }
            }

            EditConfirmButtons(viewModel, asyncOperationStatus, augmentedPrice, onEditPriceClick)
        }
    }
}

@Composable
private fun ItemSourceInfoLive(
    viewModel: HomeViewModel,
    asyncOperationStatus: AsyncOperationStatus,
    dataSet: DataSet,
    item: Item?,
    source: Source?,
    augmentedPrice: AugmentedPrice?,
    onEditPriceClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onDeletePriceClick: () -> Unit,
) {
    OnAppLifecycleEvent { event ->
        if (event == Lifecycle.Event.ON_STOP) { // app has left the foreground
            viewModel.previousPrice.value = null
        }
    }

    // ENHANCE: Will we have a "special offer"/"short term price" flag and maybe associated data?
    // Gut feeling is no, how to handle expiry/deletion gets complex from UI and internal
    // perspective. It's not as if the offer duration is usually clearly stated. Free text note
    // probably can be used for this.
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Box {
            SourcePriceCardBody(
                viewModel,
                asyncOperationStatus,
                dataSet,
                augmentedPrice,
                onEditPriceClick,
            )
            SourcePriceCardMenu(
                viewModel,
                asyncOperationStatus,
                dataSet,
                item,
                source,
                augmentedPrice,
                onViewHistoryClick,
                onDeletePriceClick,
                menuModifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun SourcePriceCardMenu(
    viewModel: HomeViewModel,
    asyncOperationStatus: AsyncOperationStatus,
    dataSet: DataSet,
    item: Item?,
    source: Source?,
    augmentedPrice: AugmentedPrice?,
    onViewHistoryClick: () -> Unit,
    onDeletePriceClick: () -> Unit,
    @SuppressLint("ModifierParameter") menuModifier: Modifier,
) {
    val priceHistoryCount by
        remember(dataSet.id, item?.id, source?.id) {
                if (item != null && source != null) {
                    viewModel.countPriceHistory(dataSet.id, item.id, source.id)
                } else {
                    flowOf(0L)
                }
            }
            .collectAsStateWithLifecycle(initialValue = 0L)

    OverflowMenu(enabled = asyncOperationStatus.isNotBusy(), modifier = menuModifier) {
        requestMenuClose ->
        MyDropdownMenuItem(
            text = { Text(stringResource(R.string.menu_item_view_history)) },
            enabled = priceHistoryCount > 0,
            onClick = {
                requestMenuClose()
                onViewHistoryClick()
            },
        )
        MyDropdownMenuItem(
            text = { Text(stringResource(R.string.menu_item_delete_price)) },
            enabled = augmentedPrice != null,
            onClick = {
                requestMenuClose()
                onDeletePriceClick()
            },
        )
    }
}

@Composable
private fun EditConfirmButtons(
    viewModel: HomeViewModel,
    asyncOperationStatus: AsyncOperationStatus,
    augmentedPrice: AugmentedPrice,
    onEditPriceClick: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        FilledTonalButton(
            onClick = onEditPriceClick,
            shape = MaterialTheme.shapes.small,
            enabled = asyncOperationStatus.isNotBusy(),
        ) {
            Text(stringResource(R.string.button_edit))
        }

        Spacer(modifier = Modifier.width(8.dp))

        // ENHANCE: Confirm and Undo are quick, but because they have a background save, the
        // asyncOperationStatus will (correctly) cause various controls on the screen to be disabled
        // instantly but briefly during the save, then re-enabled shortly after. This manifests (at
        // least in the emulator) as a slightly ugly flicker. (This isn't jank in the usual sense,
        // the UI is correctly reflecting what we've coded.) It might be best if we could make them
        // inactive (so you can't get into a race condition by editing or changing things during the
        // save) instantly but defer the visible greying out for (say) 50ms. I have had some
        // confused chats with LLMs about this and it may be a reasonable thing to do. It may also
        // be somewhat fiddly and I'm not sure what the best way to do it is, even if it is a good
        // idea. It's possible similar behaviour can occur in other parts of the app, but in
        // practice it's probably less obvious as on a full screen dialog we will animate away as
        // soon as the save actually completes.

        // ENHANCE: A couple of possible polish opportunities here:
        // - We should maybe disable the "Confirm" button if the label is "now", although arguably
        //   this is a bit unnecessary and leads to a small visual distraction if the user is
        //   looking at the screen when it ticks over to 1 minute and is re-enabled.
        // - We should maybe animate changes to the "Confirmed" text label if it changes due to a
        //   confirm/undo click (rather than just because time passed and it ticked to the next
        //   value). This would help make it more obvious to the user what Confirm/Undo are actually
        //   affecting.

        // The "Confirm" button is the primary button - we expect it to be the
        // button users click on most on this card (most of the time prices
        // won't have changed on subsequent visits) - so it gets the position on
        // the right.
        val showConfirmButton = viewModel.previousPrice.value == null
        FilledTonalButton(
            /* modifier = Modifier.width(confirmButtonWidth) ,*/
            onClick = {
                if (showConfirmButton) {
                    viewModel.confirmPrice(augmentedPrice.basePrice)
                } else {
                    viewModel.undoConfirmPrice(
                        augmentedPrice.basePrice,
                        viewModel.previousPrice.value!!,
                    )
                }
            },
            shape = MaterialTheme.shapes.small,
            enabled = asyncOperationStatus.isNotBusy(),
        ) {
            AnimatedContent(targetState = showConfirmButton) { showConfirm ->
                // ENHANCE: "Undo" is perhaps borderline unclear as to what it is undoing (although
                // I hope the user observing the transition from "Confirm"->"Undo" will act as a
                // hint), but at least on my small emulated phone, "Undo confirm" looks a bit ugly
                // or (with "Good price") doesn't fit and causes the button to become multi-line.
                Text(
                    if (showConfirm) stringResource(R.string.button_confirm)
                    else stringResource(R.string.button_undo)
                )
            }
        }
    }
}

@Composable
private fun ItemSourceSelector(
    asyncOperationStatus: AsyncOperationStatus,
    source: Source?,
    sourceList: List<Source>,
    item: Item?,
    itemList: List<Item>,
    onSelectedSourceIdChange: (Long) -> Unit,
    onItemSearchClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Item selector
        val clickableModifier =
            if (asyncOperationStatus.isNotBusy()) {
                Modifier.clickable { onItemSearchClick() }
            } else {
                Modifier
            }
        // For reasons I don't quite understand, using key() here avoids a frame or two of delay in
        // applying the new colors= selection when asyncOperationStatus changes. I think the basic
        // idea (according to ChatGPT) is that this forces the whole thing to be recomposed, but it
        // is a bit voodoo.
        key(asyncOperationStatus) {
            TextField(
                value = item?.name ?: "",
                onValueChange = { /* No-op, read-only */ },
                label = { Text(stringResource(R.string.label_item)) },
                enabled = false, // so Modifier.clickable() works
                modifier = Modifier.fillMaxWidth().then(clickableModifier),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription =
                            stringResource(R.string.content_description_search_products),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                // There might be an argument that this should "sometimes" get the focused colours,
                // but since clicking on it immediately opens a full screen dialog, I think it's
                // probably reasonable to hard-code false here.
                colors =
                    if (asyncOperationStatus.isNotBusy()) myTextFieldColors(false)
                    else TextFieldDefaults.colors(),
                // It is rare to have no item selected, but if this happens and some items are
                // defined, the user should fairly easily figure out what's happening (they just
                // need to tap this TextField to open the selector). So we show a supportingText
                // only if there are no items at all.
                supportingText =
                    if (item != null || itemList.isNotEmpty()) null
                    else {
                        { Text(stringResource(R.string.supporting_text_no_items_in_data_set)) }
                    },
            )
        }

        Spacer(modifier = Modifier.height(16.dp).fillMaxWidth())

        // If sourceList is empty this will generate a single-item menu with just "None" in,
        // but that is probably better than the "skeleton" menu we get with no items in.
        val locale = LocalConfiguration.current.locales[0]
        val sourceNameNone = stringResource(R.string.source_name_none)
        val sourceListSorted =
            remember(sourceNameNone, sourceList, locale) {
                listOf(Pair(sourceIdNone, sourceNameNone)) +
                    sourceList.sortedByLocale({ it.name }, locale).map { Pair(it.id, it.name) }
            }
        // ENHANCE: I did wonder if MyExposedDropdownMenuBox should allow null IDs in "items" to
        // avoid the need for the sourceIdNone hack here, but I really didn't want to have to make
        // every user of it be null-tolerant when it won't hand you a null itself unless you gave it
        // one in the input item list. I did try wrapping the null inside a simple Nullable<T> so it
        // could "pass through" MyExposedDropdownMenuBox without altering the API and I think the
        // idea is sound but I started to run into incomprehensible "out"/covariance stuff and it
        // just felt too much just to fix this where sourceIdNone is an easy hack.
        key(asyncOperationStatus) { // improves appearance, see similar key() above
            MyExposedDropdownMenuBox(
                modifier =
                    Modifier
                        // .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                // Note that if source is null, we pass that null through to selectedId so the
                // dropdown starts off with nothing selected and the "Store" label expands to form a
                // large "prompt". We could turn null into sourceIdNone and have "None" shown, but
                // it's probably nicer this way.
                selectedId = source?.id, /* ?: sourceIdNone */
                onItemSelected = { onSelectedSourceIdChange(it) },
                enabled = asyncOperationStatus.isNotBusy(),
                label = { Text(stringResource(R.string.label_source)) },
                // It's normal to have no source selected, but if there are no sources defined at
                // all it seems best to offer the user a hint.
                supportingText =
                    if (sourceList.isNotEmpty()) null
                    else {
                        { Text(stringResource(R.string.supporting_text_no_sources_in_data_set)) }
                    },
                items = sourceListSorted,
                getId = { it.first },
                getItemText = { it.second },
            )
        }
    }
}

@Composable
private fun RelativeTimeText(augmentedPrice: AugmentedPrice) {
    val confirmedAt = augmentedPrice.basePrice.confirmedAt
    var now by remember(confirmedAt) { mutableStateOf(Instant.now()) }
    val ageInSeconds = Duration.between(confirmedAt, now).seconds

    // This LaunchedEffect causes the *state variable* "now" to update periodically, forcing a
    // recomposition so the user can see the age increasing.
    LaunchedEffect(confirmedAt) {
        // NB: The captured ageInSeconds will *not* update in here - this coroutine is launched once
        // on the first composition for a specific value of "instant".
        while (true) {
            // ENHANCE: We could maybe sleep until "the next minute boundary" when ageInMinutes<60,
            // so we're not executing every second for the first minute when the display only has
            // minute resolution.
            val ageInMinutes = Duration.between(confirmedAt, Instant.now()).toMinutes()
            val delayDuration =
                when {
                    ageInMinutes < 1 -> 1_000L // update every second for first minute
                    ageInMinutes < 24 * 60 -> 60_000L // every minute for first day
                    else -> 60 * 60 * 1_000L // every hour after that
                }
            delay(delayDuration)
            now = Instant.now()
        }
    }

    // getRelativeTimeSpanString() returns "0 min. ago" in English for ages under 60 seconds, and
    // presumably similar in other languages, so we special-case this.
    val relativeTime =
        if (ageInSeconds < 60) stringResource(R.string.relative_time_span_string_now)
        else
            DateUtils.getRelativeTimeSpanString(
                    confirmedAt.toEpochMilli(),
                    now.toEpochMilli(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE,
                )
                .toString()
    // ENHANCE: I don't know if it's slightly weird to color this to indicate it's stale without
    // showing a stale icon or having some supporting text. May want to revisit this in the future.
    Text(
        relativeTime,
        color =
            if (augmentedPrice.ageClass == AgeClass.FRESH) Color.Unspecified
            else MaterialTheme.colorScheme.error,
    )
}
