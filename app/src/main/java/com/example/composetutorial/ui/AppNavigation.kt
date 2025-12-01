package com.example.composetutorial.ui

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composetutorial.app.MyApplication
import com.example.composetutorial.R
import com.example.composetutorial.ui.screens.selectitem.SelectItemViewModel
import com.example.composetutorial.ui.screens.selectsource.SelectSourceViewModel
import com.example.composetutorial.app.safeRestartApp
import com.example.composetutorial.domain.dataStore
import com.example.composetutorial.debug.myRequire
import com.example.composetutorial.models.backupDatabase
import com.example.composetutorial.models.restoreDatabase
import com.example.composetutorial.models.toEditable
import com.example.composetutorial.ui.common.setSelectedDataSetIdAsync
import com.example.composetutorial.ui.common.setSelectedItemIdAsync
import com.example.composetutorial.ui.common.setSelectedSourceIdAsync
import com.example.composetutorial.ui.components.SharedViewModel
import com.example.composetutorial.ui.components.generalselector.GeneralSelectorScreen
import com.example.composetutorial.ui.components.topAppBarTitle
import com.example.composetutorial.ui.screens.about.AboutScreen
import com.example.composetutorial.ui.screens.editdataset.EditDataSetScreen
import com.example.composetutorial.ui.screens.editdataset.EditDataSetViewModel
import com.example.composetutorial.ui.screens.edititem.EditItemScreen
import com.example.composetutorial.ui.screens.edititem.EditItemScreenStaticContent
import com.example.composetutorial.ui.screens.edititem.EditItemViewModel
import com.example.composetutorial.ui.screens.editprice.EditPriceScreen
import com.example.composetutorial.ui.screens.editprice.EditPriceScreenStaticContent
import com.example.composetutorial.ui.screens.editprice.EditPriceViewModel
import com.example.composetutorial.ui.screens.editsource.EditSourceScreen
import com.example.composetutorial.ui.screens.editsource.EditSourceScreenStaticContent
import com.example.composetutorial.ui.screens.editsource.EditSourceViewModel
import com.example.composetutorial.ui.screens.home.HomeScreen
import com.example.composetutorial.ui.screens.home.HomeViewModel
import com.example.composetutorial.ui.screens.legal.LegalScreen
import com.example.composetutorial.ui.screens.selectdataset.SelectDataSetViewModel
import com.example.composetutorial.ui.screens.selectitem.SelectItemScreenStaticContent
import com.example.composetutorial.ui.screens.selectsource.SelectSourceScreenStaticContent
import com.example.composetutorial.ui.screens.settings.SettingsScreen
import com.example.composetutorial.ui.screens.settings.SettingsViewModel
import com.example.composetutorial.ui.screens.viewpricehistory.ViewPriceHistoryScreen
import com.example.composetutorial.ui.screens.viewpricehistory.ViewPriceHistoryScreenStaticContent
import com.example.composetutorial.ui.screens.viewpricehistory.ViewPriceHistoryViewModel
import kotlinx.coroutines.delay

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel =
        viewModel(LocalContext.current as ComponentActivity) // TODO: perplexity voodoo

    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var showRestartDialog by rememberSaveable { mutableStateOf(false) }

    // TODO: ChatGPT magic

    val context = LocalContext.current
    val activity = context as? Activity

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri ->
            if (uri != null) {
                try {
                    backupDatabase(context, uri)
                } catch (e: Exception) {
                    errorMessage = e.localizedMessage ?: context.getString(R.string.message_an_unknown_error_occurred)
                }
            }
        }
    )

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    restoreDatabase(context, uri)
                    // All sorts of internal state is probably outdated. This is a rare operation
                    // and we don't want to massively complicate our code (e.g. the flows feeding
                    // the home screen) to handle it, so we just force a restart.
                    showRestartDialog = true
                } catch (e: Exception) {
                    errorMessage = e.localizedMessage ?: context.getString(R.string.message_an_unknown_error_occurred)
                }
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        // I think these are fairly standard values, and Android's system level support for tweaking
        // animation speeds mean there's no need to allow these to be tweakable via Settings.
        val tweenDurationMillisEnter = 300
        val tweenDurationMillisExit = 250

        // ENHANCE: It might be good to look at adding a fade to some of these animations - maybe a
        // fade added to "top" screen and perhaps a fade added to the "bottom" screen as well. I
        // don't think it's a huge deal and it is "correct", but the border on the incoming screen
        // can - if you're really looking at the transition with paranoid eyes - give an impression
        // of the outgoing non-background content "flickering away" before being replaced by new
        // content. I say "correct" because if we're imagining cards sliding on top of one another
        // in a stack the border would indeed cause this kind of "flicker", but it might still look
        // nicer with some fading.
        fun AnimatedContentTransitionScope<NavBackStackEntry>.slideLeftTransition(): EnterTransition =
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,

                animationSpec = tween(
                    durationMillis = tweenDurationMillisEnter,
                    easing = LinearOutSlowInEasing
                ),
            )

        fun AnimatedContentTransitionScope<NavBackStackEntry>.slideRightTransition(): ExitTransition =
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(
                    durationMillis = tweenDurationMillisExit,
                    easing = FastOutLinearInEasing
                )
            )

        fun AnimatedContentTransitionScope<NavBackStackEntry>.slideUpTransition(): EnterTransition =
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,

                animationSpec = tween(
                    durationMillis = tweenDurationMillisEnter,
                    easing = LinearOutSlowInEasing
                ),
            )

        fun AnimatedContentTransitionScope<NavBackStackEntry>.slideDownTransition(): ExitTransition =
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(
                    durationMillis = tweenDurationMillisExit,
                    easing = FastOutLinearInEasing
                )
            )

        // Note that we explicitly request a fresh ViewModel each time (because it's tied to the
        // backStackEntry) - this avoids stale data causing problems.

        composable(
            "home",
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
        ) { backStackEntry ->
            Log.d("MyApp", "backStackEntry.id ${backStackEntry.id}")
            val locale by rememberUpdatedState(LocalConfiguration.current.locales[0])
            val viewModel = viewModel<HomeViewModel>(backStackEntry, factory = AppViewModelProvider.Factory)
            LaunchedEffect(locale) {
                viewModel.updateLocale(locale)
            }
            HomeScreen(
                viewModel,
                navController,
                onEditPriceClick = { uiContent ->
                    sharedViewModel.setEditPriceScreenInitialUiContent(
                        uiContent,
                        locale
                    )
                    navController.navigate("editPrice")
                },
                onItemSearchClick = { uiContent ->
                    sharedViewModel.setSelectItemScreenInitialUiContent(uiContent)
                    navController.navigate("editItems/select")
                },
                onViewHistoryClick = { uiContent ->
                    // We navigate giving this ID triplet instead of the price ID here, so that if a
                    // price gets deleted, we can still see the full history (and we can tell where
                    // deletions occurred by discontinuities in the price ID, albeit we won't know
                    // the precise time they happened).
                    sharedViewModel.setViewPriceHistoryScreenInitialUiContent(uiContent, locale)
                    navController.navigate(route = "viewPriceHistory")
                },
                onSelectDataSetClick = { uiContent ->
                    sharedViewModel.setSelectDataSetScreenContent(
                        uiContent
                    )
                    navController.navigate("editDataSets")
                },
                onSelectItemClick = { uiContent ->
                    sharedViewModel.setSelectItemScreenInitialUiContent(
                        uiContent
                    )
                    navController.navigate("editItems/edit")
                },
                onSelectSourceClick = { uiContent ->
                    sharedViewModel.setSelectSourceScreenInitialUiContent(
                        uiContent
                    )
                    navController.navigate("editSources/${uiContent.dataSet!!.id}/${uiContent.dataSet.name}")
                },
                onSettingsClick = { navController.navigate("settings") },
            )
        }

        composable(
            "settings", enterTransition = { slideLeftTransition() }, popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            SettingsScreen(
                viewModel(backStackEntry, factory = AppViewModelProvider.Factory),
                navController,
                onBackupClick = {
                    backupLauncher.launch(defaultDatabaseBackupName)
                },
                onRestoreClick = {
                    restoreLauncher.launch(arrayOf("*/*"))
                },
                onAboutClick = { navController.navigate("about") })
        }

        composable(
            "about", enterTransition = { slideLeftTransition() }, popEnterTransition = { null }, popExitTransition = { slideRightTransition() }, ) {
            AboutScreen(navController, onViewLegalClick = { navController.navigate("legal") })
        }

        composable(
            "legal", enterTransition = { slideLeftTransition() }, popEnterTransition = { null }, popExitTransition = { slideRightTransition() }, ) {
            LegalScreen(navController)
        }

        composable(
            "editDataSets", enterTransition = { slideLeftTransition() },
            popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            screenWithViewModel<SelectDataSetViewModel>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.selectDataSetScreenUIContent = null },
                buildViewModel = { app, handle ->
                    SelectDataSetViewModel(
                        savedStateHandle = handle,
                        getName = { it -> it.name },
                        initialList = sharedViewModel.selectDataSetScreenUIContent,
                        dataQuery = app.repository.getAllDataSets()
                    )
                }
            ) { viewModel ->
                // TODO: Is this locale wrong? Will this *pick up* changes to the locale, defeating the
                // whole point of having a frozen locale? Do we need to be setting this in the navhost screen which *calls* us? That's
                // what (albeit via sharedViewModel - do we have to use that here now?) happens for the edit price screen.
                val locale by rememberUpdatedState(LocalConfiguration.current.locales[0])
                GeneralSelectorScreen(
                    viewModel.generalSelectorStateHolder,
                    navController,
                    title = topAppBarTitle(stringResource(R.string.title_edit_data_sets), null),
                    getId = { it.id },
                    getName = { it.name },
                    onAddClick = {
                        Log.d("MyAppGS", "Add data set")
                        sharedViewModel.setEditDataSetScreenInitialUiContent(null, locale)
                        navController.navigate("editDataSet")
                    },
                    addContentDescription = stringResource(R.string.content_description_add_data_set),
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        sharedViewModel.setEditDataSetScreenInitialUiContent(it, locale)
                        navController.navigate("editDataSet")
                    })
            }
        }

        composable(
            "editItems/{action}", enterTransition = { slideLeftTransition() },
            popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            val action = backStackEntry.arguments?.getString("action")
            myRequire(action == "edit" || action == "select") { "Invalid action: $action" }
            val select = action == "select"
            screenWithViewModel<SelectItemViewModel>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.selectItemScreenInitialUiContent = null },
                buildViewModel = { app, handle ->
                    SelectItemViewModel(
                        repository = app.repository,
                        savedStateHandle = handle,
                        initialStaticContent = sharedViewModel.selectItemScreenInitialUiContent?.let {
                            SelectItemScreenStaticContent(it.itemList, it.dataSet)
                        },
                        getName = { it -> it.name },
                    )
                }
            ) { viewModel ->
                val dataStore = LocalContext.current.applicationContext.dataStore
                val context = LocalContext.current.applicationContext
                GeneralSelectorScreen(
                    viewModel.generalSelectorStateHolder,
                    navController,
                    title = topAppBarTitle(if (!select) stringResource(R.string.title_edit_items) else stringResource(
                        R.string.title_select_item
                    ), viewModel.uiContent.staticContent.dataSet.name),
                    getId = { it.id },
                    getName = { it.name },
                    onAddClick = {
                        // We don't alter our behaviour here depending whether or not we're being
                        // used to select an item directly from the home screen or via "Edit
                        // products". It's handy to be able to directly add a missing item when
                        // searching from the home screen.
                        Log.d("MyAppGS", "Add item")
                        sharedViewModel.setEditItemScreenInitialUiContent(null, viewModel.uiContent.staticContent.dataSet)
                        navController.navigate("editItem")
                    },
                    addContentDescription = stringResource(R.string.content_description_add_item),
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        if (!select) {
                            sharedViewModel.setEditItemScreenInitialUiContent(
                                it,
                                viewModel.uiContent.staticContent.dataSet
                            )
                            navController.navigate("editItem")
                        } else {
                            setSelectedItemIdAsync(context, viewModel.uiContent.staticContent.dataSet.id, it.id)
                            navController.popBackStack() // return to home screen
                        }
                    },
                    showSearch = true
                )
            }
        }

        composable( // TODO: Should editSources path be renamed selectSources? And ditto for other edit*?
            "editSources/{dataSetId}/{dataSetName}", enterTransition = { slideLeftTransition() },
            popEnterTransition = { null },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            val dataSetId = backStackEntry.arguments?.getString("dataSetId")!!.toLong()
            val dataSetName = backStackEntry.arguments?.getString("dataSetName")
            screenWithViewModel<SelectSourceViewModel>(
                backStackEntry = backStackEntry,
                // TODO: Could should sharedViewModel have a clearAllContent() or similar function
                // and we just call that in clearUIContent? That way we could be sure *no* old
                // content is lurking around.
                clearUIContent = { sharedViewModel.selectSourceScreenInitialUiContent = null },
                buildViewModel = { app, handle ->
                    SelectSourceViewModel(
                        repository = app.repository,
                        savedStateHandle = handle,
                        getName = { it -> it.name }, // TODO: I suspect the screens using this pattern no longer need to pass getName here - let the thing-specific viewmodel do it
                        initialStaticContent = sharedViewModel.selectSourceScreenInitialUiContent?.let {
                            SelectSourceScreenStaticContent(it.sourceList, it.dataSet)
                        },
                    )
                }
            ) { viewModel ->
                // TODO: Is this locale wrong? Will this *pick up* changes to the locale, defeating the
                // whole point of having a frozen locale? Do we need to be setting this in the navhost screen which *calls* us? That's
                // what (albeit via sharedViewModel - do we have to use that here now?) happens for the edit price screen.
                val locale by rememberUpdatedState(LocalConfiguration.current.locales[0])
                GeneralSelectorScreen(
                    viewModel.generalSelectorStateHolder,
                    navController,
                    title = topAppBarTitle(stringResource(R.string.title_edit_sources), dataSetName),
                    getId = { it.id },
                    getName = { it.name },
                    onAddClick = {
                        Log.d("MyAppGS", "Add source")
                        sharedViewModel.setEditSourceScreenInitialUiContent(null, viewModel.uiContent.staticContent.dataSet, locale)
                        navController.navigate("editSource")
                    },
                    addContentDescription = stringResource(R.string.content_description_add_source),
                    onItemSelected = {
                        Log.d("MyAppGS", "selected $it")
                        sharedViewModel.setEditSourceScreenInitialUiContent(it, viewModel.uiContent.staticContent.dataSet, locale)
                        navController.navigate("editSource")
                    })
            }
        }

        composable(
            "editPrice", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },
        ) { backStackEntry ->
            screenWithViewModel<EditPriceViewModel>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editPriceScreenInitialUiContent = null },
                buildViewModel = { app, handle ->
                    EditPriceViewModel(
                        app.repository,
                        handle,
                        sharedViewModel.editPriceScreenInitialUiContent?.editablePrice,
                        sharedViewModel.editPriceScreenInitialUiContent?.staticContent
                    )
                },
            ) { viewModel ->
                EditPriceScreen(
                    viewModel, navController,
                    requestClose = { id ->
                        if (id == null) {
                            navController.popBackStack()
                        } else {
                            navController.popBackStack("home", inclusive = false)
                        }
                    }
                )
            }
        }

        composable(
            "editDataSet", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },
        ) { backStackEntry ->
            screenWithViewModel<EditDataSetViewModel>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editDataSetScreenInitialUiContent = null },
                buildViewModel = { app, handle ->
                    EditDataSetViewModel(
                        app.repository,
                        handle,
                        sharedViewModel.editDataSetScreenInitialUiContent
                    )
                }
            ) { viewModel ->
                val dataStore = LocalContext.current.applicationContext.dataStore
                val context = LocalContext.current.applicationContext
                EditDataSetScreen(
                    viewModel, navController,
                    requestClose = { newSelectedDataSetId ->
                        if (newSelectedDataSetId == null) {
                            navController.popBackStack()
                        } else {
                            setSelectedDataSetIdAsync(context, newSelectedDataSetId)
                            navController.popBackStack("home", inclusive = false)
                        }
                    })
            }
        }

        composable(
            "editItem", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },
        ) { backStackEntry ->
            screenWithViewModel<EditItemViewModel>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editItemScreenInitialUiContent = null },
                buildViewModel = { app, handle ->
                    EditItemViewModel(
                        app.repository,
                        handle,
                        sharedViewModel.editItemScreenInitialUiContent?.editableItem,
                        sharedViewModel.editItemScreenInitialUiContent?.dataSet?.let(::EditItemScreenStaticContent)
                    )
                }
            ) { viewModel ->
                val dataStore = LocalContext.current.applicationContext.dataStore
                EditItemScreen(
                    viewModel, navController,
                    requestClose = { newSelectedItemId ->
                        // It might be somewhat logical to just do popBackStack() here, but in
                        // reality if I've added or edited an item it's almost always because I want
                        // to actually work with it on the home screen.
                        // ENHANCE: Just possibly there should be a setting to always do a simple
                        // popBackStack() here instead of immediately selecting an item which we
                        // just added/edited.
                        if (newSelectedItemId == null) {
                            // The user cancelled the edit, so just go back one step.
                            navController.popBackStack()
                        } else {
                            // The user saved the edit, so select the edited item and return to the
                            // home screen.
                            Log.d("MyAppQZ", "newSelectedItemId=$newSelectedItemId")
                            setSelectedItemIdAsync(context, viewModel.uiContent.staticContent.dataSet.id, newSelectedItemId)
                            navController.popBackStack("home", inclusive = false)
                        }
                    })
            }
        }


        composable(
            "editSource", enterTransition = { slideUpTransition() },
            popExitTransition = { slideDownTransition() },
        ) { backStackEntry ->
            screenWithViewModel<EditSourceViewModel>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.editSourceScreenInitialUiContent = null },
                buildViewModel = { app, handle ->
                    EditSourceViewModel(
                        app.repository,
                        handle,
                         sharedViewModel.editSourceScreenInitialUiContent?.editableSource,
                        sharedViewModel.editSourceScreenInitialUiContent?.staticContent
                    )
                }
            ) { viewModel ->
                val dataStore = LocalContext.current.applicationContext.dataStore
                val context = LocalContext.current.applicationContext
                EditSourceScreen(
                    viewModel, navController,
                    requestClose = { newSelectedSourceId ->
                        if (newSelectedSourceId == null) {
                            navController.popBackStack()
                        } else {
                            setSelectedSourceIdAsync(context, viewModel.uiContent.staticContent.dataSet.id, newSelectedSourceId)
                            navController.popBackStack("home", inclusive = false)
                        }
                    })
            }
        }

        composable(
            "viewPriceHistory",
            enterTransition = { slideLeftTransition() },
            popExitTransition = { slideRightTransition() },
        ) { backStackEntry ->
            val locale by rememberUpdatedState(LocalConfiguration.current.locales[0])
            screenWithViewModel<ViewPriceHistoryViewModel>(
                backStackEntry = backStackEntry,
                clearUIContent = { sharedViewModel.viewPriceHistoryScreenInitialUiContent = null },
                buildViewModel = { app, handle ->
                    ViewPriceHistoryViewModel(
                        app.repository,
                        handle,
                        sharedViewModel.viewPriceHistoryScreenInitialUiContent?.let {
                            ViewPriceHistoryScreenStaticContent(it.dataSet, it.item, it.source, it.price)
                        }
                    )
                }
            ) { viewModel ->
                ViewPriceHistoryScreen(
                    viewModel, navController,
                    requestClose = {
                        navController.popBackStack()
                    },
                    requestEditAsNew = { priceHistory ->
                        // ENHANCE: There might be some value into copying priceHistory.id onto the
                        // EditablePrice here (and from there onto PriceEntity/PriceHistory if the
                        // user saves it, so there's be a nullable "based_on_price_history_id"
                        // column on price/price_history tables), so there is a pseudo-audit trail
                        // showing the new price was generated by this route instead of the regular
                        // add/edit buttons on the home screen. We don't need solid forensic grade
                        // history though, and the user can edit the data before it's even saved, so
                        // I don't think this is a big deal. It might be interesting/useful for
                        // support/self-support purposes. ("Why did my notes disappear on this
                        // price? Oh, I rolled back to a historical price which didn't have them
                        // either.")

                        Log.d("MyApp", "TODO: requestEditAsNew $priceHistory")
                        sharedViewModel.setEditPriceScreenContent2(
                            viewModel.uiContent.staticContent.dataSet,
                            viewModel.uiContent.staticContent.item,
                            viewModel.uiContent.staticContent.source,
                            editablePrice = priceHistory.toEditable(
                                // It's important we provide the current price ID, since we must
                                // update the current existing record instead of adding a new one.
                                // The price ID might in principle have changed since the history
                                // record was created.
                                priceId = viewModel.uiContent.staticContent.price?.id ?: 0,
                                locale,
                                viewModel.uiContent.staticContent.dataSet
                            ),
                            locale
                        )
                        navController.navigate("editPrice")
                    })
            }
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { errorMessage = null },
            title = { Text(stringResource(R.string.title_error)) },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = { errorMessage = null }) {
                    Text(stringResource(R.string.button_ok))
                }
            }
        )
    }

    if (showRestartDialog) {
        LaunchedEffect(Unit) {
            Log.d("MyAppFFS", "activity $activity")
            delay(1500)
            safeRestartApp(activity!!)
        }

        AlertDialog(
            onDismissRequest = { /* prevent dismissal */ },
            title = { Text(stringResource(R.string.title_app_will_restart)) },
            text = { Text(stringResource(R.string.message_applying_restored_data)) },
            confirmButton = {}
        )
    }
}

// ENHANCE: This function was mostly written by ChatGPT. I'm loosely aware of what it does but I
// don't pretend to understand the details at this point.
@Composable
private inline fun <reified VM : ViewModel> screenWithViewModel(
    backStackEntry: NavBackStackEntry,
    noinline clearUIContent: () -> Unit,
    noinline buildViewModel: @DisallowComposableCalls (MyApplication, SavedStateHandle) -> VM,
    crossinline content: @Composable (VM) -> Unit
) {
    val factory = remember(backStackEntry) {
        viewModelFactoryWithHandle { app, handle -> buildViewModel(app, handle) }
    }

    LaunchedEffect(Unit) {
        clearUIContent()
    }

    val viewModel: VM = viewModel(
        viewModelStoreOwner = backStackEntry,
        factory = factory
    )

    content(viewModel)
}

// TODO: ChatGPT magic
private inline fun <reified VM : ViewModel> viewModelFactoryWithHandle(
    crossinline builder: (MyApplication, SavedStateHandle) -> VM
): ViewModelProvider.Factory {
    return viewModelFactory {
        initializer {
            val handle = createSavedStateHandle()
            // As written by ChatGPT, this passed "this", a CreationExtras, as the first argument of
            // builder. Given how we actually use this, it saves code duplication to just extract a
            // MyApplication here and pass that instead.
            builder(
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication,
                handle
            )
        }
    }
}

// AppViewModelProvider.Factory allows us to control the arguments passed to our ViewModel
// constructors when viewModel() is called.
// TODO: Can/should I get rid of this and use viewModelFactoryWithHandle for these cases?
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer<HomeViewModel> {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
            HomeViewModel(app.repository, app)
        }
        initializer<SettingsViewModel> {
            val app =
                this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
            SettingsViewModel(app)
        }
    }
}
