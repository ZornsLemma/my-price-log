@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.composetutorial

//import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import kotlinx.parcelize.Parcelize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.semantics.dialog
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay
import androidx.activity.compose.BackHandler
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.material3.Button
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.rememberNavController
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.collection.emptyLongSet
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.map
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// TODO: This is boilerplate *in memory* viewmodel stuff which I got from Grok. The idea is that I
// can try to start using viewmodels and passing data back and forth between eg my home screen and
// my notional full screen dialog and have it flow round and update rather than being hardcoded (to
// prove communication is working) without getting into the additional worries of having an actual
// database, which I will retrofit later. I have no idea if the code is actually correct, although
// it seems simple enough that I don't think it hides too many nasty surprises.

data class UnitX(
    val id: Long,
    val name: String
) // TODO: very hacky, not sure how will represent this

data class Category(val id: Long, val name: String)

data class Product(val id: Long, val name: String)

data class Store(val id: Long, val name: String)

// TODO: Should Price have a price_id on it? If it does, it will need to be nullable (I think) so we can use it in-memory when adding a brand new price, before the db layer assigns an id
@Parcelize
data class Price(
    val productId: Long,
    val storeId: Long,
    val price: Double,
    val details: String // Additional price details
) : Parcelable

// TODO: This is part way through being converted to use Flow
class PriceTrackerRepository {
    // TODO: listOf may be more correct than mutableListOf everywhere, not just here, but I really don't understand this.
    private val categories = MutableStateFlow<List<Category>>(
        mutableListOf(
            Category(1, "Demo"),
            Category(2, "Groceries (home)"),
            Category(3, "Groceries (Manchester)")
        )
    )
    private val products = MutableStateFlow<List<Product>>(
        mutableListOf(
            Product(1, "Milk"), Product(2, "Bread")
        )
    )
    private val stores = MutableStateFlow<List<Store>>(
        mutableListOf(
            Store(1, "Walmart"), Store(2, "Target")
        )
    )
    private val prices = MutableStateFlow<List<Price>>(listOf(
        Price(1, 1, 3.99, "Organic milk at Walmart"),
        Price(1, 2, 4.29, "Organic milk at Target"),
        Price(2, 1, 2.49, "Whole wheat bread at Walmart"),
        Price(2, 2, 2.79, "Whole wheat bread at Target"))
    )

    fun getAllCategories(): Flow<List<Category>> = categories

    fun getAllProducts(): Flow<List<Product>> = products

    fun addProduct(product: Product) {
        products.value = products.value + product
    }

    fun getAllStores(): Flow<List<Store>> = stores

    /* TODO
    fun getPricesForProduct(productId: Long): List<Price> =
        prices.filter { it.productId == productId }
        */


    // We expect the returned list to have 0 or 1 items
    fun getPriceForProductAndStore(productId: Long, storeId: Long): Flow<List<Price>> {
        // TODO: I assume we use find() here to avoid duplicating data, but in a db-backed version
        // this would be an actual SELECT. I suspect this code might *work* without writing a
        // SELECT and we'd end up doing an in memory join after pulling in the entire tables.
        /* TODO: Old broken code
        val result = _prices.find { it.productId == productId && it.storeId == storeId }
        if (result == null) {
            return flowOf(listOf())
        } else {
            return flowOf(listOf(result))
        }
        */
        Log.d("MyApp", "repository getPriceForProductAndStore")
        return prices.map { list ->
            list.filter { it.productId == productId && it.storeId == storeId }
        }
            .onEach { filteredList ->
                Log.d("MyApp", "Flow emitted for $productId/$storeId: $filteredList")
            }
    }

    fun updateOrInsertPrice(price: Price) {
        // TODO: perplexity.ai code
        prices.update { currentPrices ->
            val index = currentPrices.indexOfFirst {
                it.productId == price.productId && it.storeId == price.storeId
            }
            if (index >= 0) {
                // Update existing
                currentPrices.toMutableList().apply {
                    set(index, price)
                }
            } else {
                // Insert new
                currentPrices + price
            }
        }
        Log.d("MyApp", "after updateorinsert: ${prices.value}")

    }
}

class PriceTrackerViewModel : ViewModel() {
    private val repository = PriceTrackerRepository()

    val products: Flow<List<Product>> = repository.getAllProducts()

    // Optional: Map for efficient lookups, computed as a Flow
    val productMap: Flow<Map<Long, Product>> = products.map { list ->
        list.associateBy { it.id }
    }

    val stores: Flow<List<Store>> = repository.getAllStores()

    val storeMap: Flow<Map<Long, Store>> = stores.map { list ->
        list.associateBy { it.id }
    }

    val categories: Flow<List<Category>> = repository.getAllCategories()

    init {
        Log.d("MyApp", "PriceTrackerViewModel created: $this")
    }
    /* TODO!?
    init {
        _products.postValue(repository.getAllProducts())
    }

    // Example of updating products
    fun updateProducts(newProducts: List<Product>) {
        _products.value = newProducts
    }

    val stores: List<Store> get() = repository.getAllStores()
        */


    /* TODO
    // Prices for selected product (external to ViewModel)
    fun getPricesForProduct(productId: Long): List<Price> {
        return repository.getPricesForProduct(productId)
    }
    */

    // Price details for selected product and store (external to ViewModel)
    //@Composable // TODO!?
    fun getPriceDetailsForProductAndStore(productId: Long, storeId: Long): Flow<List<Price>> {
        return repository.getPriceForProductAndStore(productId, storeId)
    }

    // TODO: I don't think this will insert correctly yet, as Price has no price_id primary key to
    // allow us to indicate to this function when it is an insert rather than an update, but let's
    // worry about that later.
    fun updateOrInsertPrice(price: Price) {
        repository.updateOrInsertPrice(price)
    }
}

/* TODO
class StoreEditorViewModel : ViewModel() {
    private val repository = PriceTrackerRepository()

    val stores: List<Store> get() = repository.getAllStores()

    fun addStore(name: String) {
        // For prototyping, add to repository directly
        // Later, call repository.insertStore()
    }

    fun deleteStore(storeId: Long) {
        // For prototyping, remove from repository directly
        // Later, call repository.deleteStore()
    }
}
*/

enum class ThemePreference {
    LIGHT, DARK, SYSTEM
}

val screenBorder = 8.dp

// Start Grok chunk
@Composable
fun MainScreen(vm: PriceTrackerViewModel, selectedProductId: Long?, onSelectedProductIdChange: (Long) -> Unit) {
    // TODO: Note that because category and product use a TextField, they have the (I think) nice
    // property that the label expands into a sort of big hint when they are empty. We should
    // probably take advantage of this where having them empty makes sense - and it probably does
    // everywhere, even if it's rare, because the user *could* go and delete every single item in
    // the database in theory. TODO: We should make sure we have the same behaviour for Source,
    // because that actually *should* allow the user to easily set it to empty/none.
    var selectedCategoryId: Long? by remember { mutableStateOf(null) } // TODO: do we actually allow nulls for category?
    var showProductSheet by remember { mutableStateOf(false) }
    //val categories = listOf("Demo", "Groceries (home)", "Groceries (Manchester)")
    //val products = listOf("Beans", "Milk", "Bread", "Chicken" /* ... */)
    var searchQuery by remember { mutableStateOf("") }

    // TODO: I suspect in general (not just here) I should be passing viewmodel *into* these functions rather than getting it from "global", to allow for dependency injection. but in practice it wouldn't be hard to rework this after and i am not sure this ui stuff is testable - I really don't know how it works.
    //var vm: PriceTrackerViewModel = viewModel()
    val categories by vm.categories.collectAsStateWithLifecycle(initialValue = emptyList())
    val products by vm.products.collectAsStateWithLifecycle(initialValue = emptyList())
    val productMap by vm.productMap.collectAsStateWithLifecycle(initialValue = emptyMap())

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Category Selector
        // TODO: I am starting to think this is the best drop down menu implementation (needs renaming to avoid confusion). We probably don't *want* the primary colour underline highlight here, given that e.g. "buttons" get highlighted by an overall colour change as this does rather than an "underline" - TextFields obviously *do* get this underline for whatever reason known only to MD3 specs, but our TextField is not a "real" TextField so this "darken whole thing" approach is probably consistent
        // TODO: We *may* want to disable the on click ripple whatsit for this, based on how the "official" experimental ExposedDropdownMenuBox behaves - although having thoughts about it and chatted with Grok and ChatGPT, maybe this is *good* and it is a weird quirk of (my impl) of the experimental "official" one that is weird
        MyExposedDropdownMenuBox(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            selectedId = selectedCategoryId,
            onValueChange = { selectedCategoryId = it },
            label = { Text("Category") },
            items = categories,
            getId = { it.id },
            getLabel = { it.name },
        )

        // Product Selector
        TextField(
            value = productMap[selectedProductId]?.name ?: "Invalid product ID $selectedProductId",
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
                //Log.d("MyApp", "FFS:" + items(products).joinToString(","))
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
                            it.name.contains(searchQuery, ignoreCase = true)
                        }) { product ->
                            ListItem(
                                headlineContent = { Text(product.name) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelectedProductIdChange(product.id)
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

// TODO: THis needs support for selecting "None" - maybe we just make the user pass it in the input with a null ID, actually?
@Composable
fun <T, ID : Comparable<ID>> MyExposedDropdownMenuBox(
    selectedId: ID?,
    onValueChange: (ID) -> Unit, // TODO: rename onItemSelected? is there a "standard" for e.g. the crappy MD3 experimental dropdown?
    label: @Composable () -> Unit, // TODO: rename to distinguish from getLabel type use?
    supportingText: @Composable (() -> Unit)? = null,
    items: List<T>,
    getId: (T) -> ID,
    getLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }/*
    val focusedColor   = MaterialTheme.colorScheme.error
    val unfocusedColor = MaterialTheme.colorScheme.tertiary //     onSurfaceVariant
    var indicatorColor by remember { mutableStateOf(unfocusedColor) } // TODO: ALL THIS STUFF ISN'T WORKING, I SUSPECT THE *FOCUS* ISN'T HITTING THE CONTROL AS IT'S DISABLED, BUT *SOMETHING* IS HITTING IT AND TOGGLING ITS COLOUR BUT IT ISN'T THIS, NOT SURE
    */
    var textFieldWidth by remember { mutableStateOf(0) }
    val itemMap =
        items.associateBy { getId(it) } // TODO: inefficient? should we make caller supply use with this so viewmodel can be caching it?
    val PULLEDOUT: String = if (selectedId == null) "" else {
        val item = itemMap[selectedId]
        if (item != null) getLabel(item) else "Invalid ID $selectedId"
    }
    Box(modifier = modifier) {
        TextField(
            value = PULLEDOUT,
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
                        getLabel(item),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }, contentPadding = PaddingValues(start = 16.dp), onClick = {
                    onValueChange(getId(item))
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
fun ItemSourceInfo(vm: PriceTrackerViewModel, navController: NavHostController, selectedProductId: Long?) {
    // TODO: Do we want any kind of "heading" or not? We may want some simple dividers, but those would be provided by the surrounding composables. Gut feeling is we don't want a heading, but think about it.
    var expanded by remember { mutableStateOf(false) }
    var currentUnit by remember { mutableStateOf("100g") }

    //var vm: PriceTrackerViewModel = viewModel()
    val sources by vm.stores.collectAsStateWithLifecycle(initialValue = emptyList())

    // fontSize/iconSize are used here so that the drop down icon scales correctly when the user
    // changes the system font size. (Even if we didn't do this, we'd still want to use a fixed
    // size() Modifier (16.dp works quite nicely at the default settings on my current emulator) to
    // improve the appearance, but it's nicer to take font size into account.)
    val fontSize = MaterialTheme.typography.bodyLarge.fontSize
    val iconSize = with(LocalDensity.current) { fontSize.toDp() }
    var selectedSourceId: Long? by rememberSaveable { mutableStateOf(null) }
    //val sources = listOf("None", "Tesco", "Asda", "Sainsbury's Local", "Iceland")
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
            MyExposedDropdownMenuBox(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth(),
                selectedId = selectedSourceId,
                onValueChange = { selectedSourceId = it },
                label = { Text("Source") },
                supportingText = if (selectedSourceId != null) null else {
                    { Text("Select a source to view or change the price there") }
                },
                items = sources,
                getId = { it.id },
                getLabel = { it.name },
            )
            if (selectedSourceId != null) {
                // TODO: DEFAULTING TO PRODUCT ID 1 IS A MASSIVE HACK BUT I DON'T WANT TO GET SIDETRACKED THINKING ABOUT NULL CASE RIGHT NOW
                val priceList by vm.getPriceDetailsForProductAndStore(
                    productId = if (selectedProductId == null) 1 else selectedProductId,
                    storeId = selectedSourceId!!
                ).collectAsStateWithLifecycle(initialValue = emptyList())
                Log.d("MyApp", "Recomposed with priceList: $priceList")
                check(priceList.size <= 1) { "Expected 0 or 1 prices for a product and store, but got ${priceList.size}" }

                if (priceList.isEmpty()) {
                    // TODO: Very quick hack
                    Text("TODO: No price, do something useful here")
                } else {


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
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }) {
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
                            Text(priceList[0].details)
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
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            // TODO: Confirm button sets last updated to "today" and turns itself into "Undo confirm" (or something) on being clicked, we should ideally make this as obvious as possible to the user, maybe some kind of animation
                            FilledTonalButton(onClick = {}, shape = MaterialTheme.shapes.small) {
                                Text("Confirm")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            FilledTonalButton(
                                onClick = { navController.navigate("fullScreenDialog/$selectedProductId/$selectedSourceId") },
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text("Edit") // TODO: "Update"? (we do have a history-ish element, maybe)
                            }
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


// Utility to get Activity window
@Composable
private fun Context.getActivityWindow(): Window? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context.window
        context = context.baseContext
    }
    return null
}

// TODO: ChatGPT magic plus my hackery to fix bugs
// TODO: Even if this seems OK and I decide to keep/use it, giving it a thorough manual code review alongside https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/ would likely be a good idea.
// TODO: I got Grok to do a code review on this. Frankly it seemed to miss the point somewhat but it made some comments that *might* be relevant, so probably ought to go over its feedback again once I have studied this code and tried to simplify (if possible) the state handling, which may well be over-complex as a result of ChatGPT and/or my incompetence and/or an interaction between the two. (grok-full-screen-dialog-code-review-iffy...)
// TODO: It might of course be worth asking ChatGPT to do a code review, especially if it approaches the code "fresh" (tell it Grok wrote it) it might find problems
// TODO: Perplexity.ai says "9. Focus Management
//
//    Focus Trap:
//    Platform dialogs trap focus within themselves; your implementation does not.
//    Landmine: If your dialog contains text fields or focusable elements, users could tab out of the dialog into the underlying screen. Consider using FocusRequester to ensure initial focus, and possibly a custom focus trap if accessibility is a concern."
// I am not sure if this is a problem, using the cursor keys to move round doesn't seem to obviously be moving off the dialog (but it is very primitive), but pressing the tab key does seem to "lose" focus for a while, which suggests it might be floating behind. May be worth looking into this and/or asking ChatGPT for advice/tweaks on this.
// TODO: o4-mini says: Definitive Bugs / Behavioral Surprises
//– pointerInput( Unit ) {} does not actually consume all touches.
//• You’ve seen that it blocks clicks “often,” but on a lot of devices / Compose versions touches will actually fall through.
//• If you want to fully block taps behind your “dialog,” you need something like:
//
//kotlin
//
//.pointerInput(Unit) {
//  awaitPointerEventScope {
//    while (true) {
//      // we just loop forever, consuming every event
//      awaitPointerEvent()
//    }
//  }
//}
// • Alternatively, a zero‐visual Modifier.clickable(...) with indication = null + interactionSource = remember { MutableInteractionSource() } will also reliably eat taps.
//
//– zIndex might not be high enough in more complex layouts.
//
// • As soon as your dialog appears, you should move focus into it.
//
//• You can call bringIntoViewRequester on a known focusable, or use a FocusRequester on your first input or button inside.
// • You have .semantics { dialog() } which is great. You should also move accessibility focus into your dialog container when it opens, which you can do with LaunchedEffect(visible) { focusManager.moveFocus(FocusDirection.In) }.
//
//– WindowInsets stacking
//
//• Chaining .windowInsetsPadding(WindowInsets.systemBars) and .windowInsetsPadding(WindowInsets.ime) will add navBar/inset space twice in overlapping areas (e.g. bottom).
//• A better pattern is:
//
//kotlin     val combined = WindowInsets.systemBars.union(WindowInsets.ime)     Modifier.windowInsetsPadding(combined)
//
//    Nice-to-Haves / Hardening
//    – BackHandler placement
//    • You register the BackHandler only when isComposed. That’s correct, but if you inverted your AnimatedVisibility logic you could move it inside your AnimatedVisibility block so you don’t have to manage isComposed at all.

// This is a mash-up of some ChatGPT-written code with
// https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/ and some of my own
// tweaking and experience from earlier implementation efforts, plus trying to investigate and
// address points raised by code reviews from all the AIs I could get my hands on. It attempts to be
// an MD3-compliant implementation of a full-screen dialog box, including animation (sliding in
// vertically to distinguish it from a full-fledged screen which slides in horizontally). Note that
// it does *not* use the standard Dialog class - I was ultimately advised not to use this and to try
// to take care of things myself. If I remember correctly, using Dialog caused weird colour changes
// on the status bar and although I could have hacked around that further, I decided to take
// ChatGPT's advice and sample code and get rid of Dialog. It feels like MD3-compliant full-screen
// dialogs on Android are a black art. I would have preferred not to take so much AI advice but I
// really struggled to find any concrete, recent human-written advice - the amount of blogs turning
// up in web searches which are probably just AI slop doesn't help, of course. I am concerned there
// will be a lingering landmine here ("this crashes if you have an Android 10 device with a hardware
// keyboard attached and use D-pad navigation during the exit animation") but I can only hope most
// cases are covered. The AI code reviews certainly raised some things (like focus trapping in a
// dialog) that I might have missed otherwise.
@Composable
fun AnimatedFullScreenDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enterDurationMillis: Int = 300,
    exitDurationMillis: Int = 250, // was 300,
    content: @Composable () -> Unit
) {
    // The parent holds the source of truth for whether the dialog is visible or not across things
    // like rotations. We just need to be aware that we may be visible on our first composition in
    // that case, so we must initialise visibleState using visible and not hard-code it to false.
    // (We don't want the dialog to animate in if it is visible on first composition.)
    var visibleState = remember { MutableTransitionState(visible) }
    visibleState.targetState = visible

    // The actual content needs to be present in the compose tree during enter and exit animations
    // as well as (obviously) when it is supposed to be visible. The only time we don't need it is
    // if we are idling in the non-visible state.
    if (visibleState.currentState || visibleState.targetState) {
        Dialog(
            onDismissRequest = onDismiss, properties = DialogProperties(
                usePlatformDefaultWidth = false, // Makes dialog full-width
                decorFitsSystemWindows = false, // TODO: to make imepadding work!?
                /* TODO!? Probably not needed now
                dismissOnBackPress = true, // Handles back button
                dismissOnClickOutside = true // Allows dismissal by clicking outside
                */
            )
        ) {
// Get the view to manage insets
            val view = LocalView.current
            LaunchedEffect(view) {
                // Get the Activity and its Window
                val activity = view.context as? android.app.Activity
                activity?.window?.let { window ->
                    // Ensure content is drawn behind system bars
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                        // Consume system insets to prevent automatic adjustments
                        WindowInsetsCompat.Builder(insets).setInsets(
                                WindowInsetsCompat.Type.systemBars(),
                                Insets.NONE
                            ) // Use androidx.core.graphics.Insets
                            .build()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(WindowInsets.ime)
                    .windowInsetsPadding(WindowInsets.systemBars)
            ) {
                // Get the window to control system bars
                val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
                val activityWindow = LocalView.current.context.getActivityWindow()

                // Without the code in this SideEffect block, the Dialog appears to effectively invert
                // the status bar colours. This is readable but looks ugly for our full screen dialog.
                SideEffect {
                    dialogWindow?.let { window ->
                        // Disable the dialog scrim; we don't want this for our full screen dialog,
                        // especially since we are sliding it in from the bottom and having the screen
                        // go dim first looks ugly.
                        window.setDimAmount(0f)

                        // Absolute Grok (I think?) voodoo which avoids the status bar (at the top of
                        // the screen) going white-on-white as a result of the previous line disabling
                        // the scrim.
                        activityWindow?.let { actWindow ->
                            // Copy activity window flags for consistent behavior
                            window.setFlags(
                                actWindow.attributes.flags, actWindow.attributes.flags
                            )
                            // Use WindowCompat for system bar transparency and cutout support
                            WindowCompat.setDecorFitsSystemWindows(window, false)
                            // Match system bar appearance (light/dark icons) to activity
                            val controller =
                                WindowCompat.getInsetsController(window, window.decorView)
                            val activityController =
                                WindowCompat.getInsetsController(actWindow, actWindow.decorView)
                            controller.isAppearanceLightStatusBars =
                                activityController.isAppearanceLightStatusBars
                            controller.isAppearanceLightNavigationBars =
                                activityController.isAppearanceLightNavigationBars
                        }
                    }
                }

                // Animate visibility for dialog content sliding vertically
                AnimatedVisibility(
                    visibleState, enter = slideInVertically(
                    animationSpec = tween(
                    durationMillis = enterDurationMillis, easing = LinearOutSlowInEasing
                ), initialOffsetY = { fullHeight -> fullHeight } // Slide from bottom
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = enterDurationMillis, easing = LinearOutSlowInEasing
                    )
                ), exit = slideOutVertically(
                        animationSpec = tween(
                    durationMillis = exitDurationMillis, easing = FastOutLinearInEasing
                ), targetOffsetY = { fullHeight -> fullHeight } // Exit to bottom
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = exitDurationMillis, easing = FastOutLinearInEasing
                    )
                ),

                    modifier = modifier.fillMaxSize()/* TODO!? These were probably added when we were trying to emulate Dialog and we may well not need them now we *are* using Dialog, but I'll keep them around just in case for a bit
                        // Handle system bars & keyboard insets
                        // .consumeWindowInsets(WindowInsets.systemBars) - probably don't need this, but it will not prevent insets from propagating, whether that is a bad thing or not is utterly beyond me of course - but hey, full screen dialogs are advanced ninja-level magic
                        .windowInsetsPadding(WindowInsets.systemBars)
                        .windowInsetsPadding(WindowInsets.ime)
                        */) {
                    // The actual dialog content container (can be Scaffold, etc)
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(vm: PriceTrackerViewModel, navController: NavHostController) {

    var menuExpanded by remember { mutableStateOf(false) }
    var showEditDialog by rememberSaveable { mutableStateOf(false) }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    var selectedProductId: Long by rememberSaveable { mutableStateOf(1) } // TODO: massive hack defaulting to hardcoded, we need a genuine ID from somewhere and/or support for null


    // TODO: I added this Surface by analogy with the one in SettingsScreen, but it appears to have
    // no real effect - even if I set its color to Red or primary, nothing shows.
    // TODO: Actually it may or may not be this, but on the O6 at least there does seem to be a weird
    // extra background shade with a bit of the white background down the edges where the border is.
    // No - it is there, but even if I remove this surface it is still there. I will have to experiment further. Part of the issue may be that it's the top-level Nav thing which is responsible.
    // Surface(modifier = Modifier.fillMaxSize()/*, color=MaterialTheme.colorScheme.surface */) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red /* TODO DEBUG HACK */),
        topBar = {
            TopAppBar(title = { Text("My App Name Here") }, actions = {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    // TODO: FONTS AND PROB COLORS ON THIS LIST ARE PROB WRONG
                    DropdownMenuItem(text = { Text("Edit product list") }, onClick = {
                        menuExpanded = false
                        // Handle navigation or action
                    })
                    DropdownMenuItem(text = { Text("Edit categories") }, onClick = {
                        menuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Settings") }, onClick = {
                        menuExpanded = false
                        navController.navigate("settings")
                    })
                }
            })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = screenBorder)
                .background(MaterialTheme.colorScheme.secondary) // TODO debug hack

        ) {
            MainScreen(
                vm = vm,
                selectedProductId = selectedProductId,
                onSelectedProductIdChange = { selectedProductId = it }) // TODO: rename this

            /*
            // TODO TEMP HACK FOR KEYBOARD/SCROLLING EXPERIMENTS
            Spacer(modifier = Modifier.height(300.dp)) // TODO TEMP HACK
            var packSize by remember { mutableStateOf("123") }
            TextField(
                label = { Text("Pack size") },
                value = packSize,
                onValueChange = { packSize = it },
                // TODO: keyboardOptions here hints to on-screen keyboard, we probably also ought to prohibit non-numbers or (regional) decimal separator and *maybe* prohibit multiple decimal separators (but maybe this should just be an error report not prohibited, what's normal?)
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    })
                    */

            androidx.compose.foundation.layout.Spacer(
                modifier = androidx.compose.ui.Modifier.height(
                    8.dp
                )
            )

            ItemSourceInfo(
                vm = vm,
                navController = navController,
                selectedProductId = selectedProductId)

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
                    "Tesco", "£2.13", "Tesco Finest is actually cheapest"
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

    }

    /* TODO
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
    AnimatedFullScreenDialog(visible = showEditDialog, onDismiss = { showEditDialog = false }) {
                        OuterFullScreenDialog()
                    } */
}

// https://www.sinasamaki.com/custom-dialog-animation-in-jetpack-compose/
@ReadOnlyComposable
@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window


@Composable
fun OuterFullScreenDialog(vm: PriceTrackerViewModel, navController: NavHostController, productId: Long, storeId: Long) {
    //var vm: PriceTrackerViewModel = viewModel()
    // TODO: Should we just have the caller pass the product name through so we don't have to do this lookup? the viewmodel should have the data cached, but we still have to through the collectstatewithlifecycle overhead?
    val productMap by vm.productMap.collectAsStateWithLifecycle(initialValue = emptyMap())
    val productName = productMap[productId]?.name ?: "Invalid product ID $productId"
    val storeMap by vm.storeMap.collectAsStateWithLifecycle(initialValue = emptyMap())
    val storeName = storeMap[storeId]?.name ?: "Invalid store ID $storeId"
    val nullablePriceList: List<Price>? by vm.getPriceDetailsForProductAndStore(
        productId = productId,
        storeId = storeId
    ).collectAsStateWithLifecycle(initialValue = null)

    if (nullablePriceList == null) {
        // This will almost certainly never be seen - we will likely get the query results back and
        // be recomposed before the first frame.
        Text("Loading...")
    } else {
        val priceList = nullablePriceList!!
    check(  priceList.size <= 1) { "Expected 0 or 1 prices for a product and store, but got ${priceList.size}" }
    // TODO: Create empty price like this feels crap, and it's also not right that the price defaults to 0.0 - it needs to be nullable, and possibly the price should be a string not a double at least in this context, not sure about db
    // TODO: price probably needs rememberSaveable
    //var price by rememberSaveable { mutableStateOf ( if (priceList.isEmpty()) Price(productId = productId, storeId = storeId, price = 0.0, details = "") else priceList[0])}

    // Initialize price with a default value
    var price by rememberSaveable {
        mutableStateOf(
            if (priceList.isEmpty()) {
                Price(productId = productId, storeId = storeId, price = 0.0, details = "")
            } else {
                priceList[0]
            }
        )
    }
    var originalPrice by rememberSaveable { mutableStateOf(price) }

    var showConfirmDialog by rememberSaveable { mutableStateOf( false ) }

    fun onCloseRequest() {
        if (price != originalPrice) {
            showConfirmDialog = true
        } else {
            navController.popBackStack()
        }
    }


    BackHandler {
        onCloseRequest()
    }

        // TODO: Grok suggests wrapping a Box with:
        //Modifier.semantics {
        //    role = Role.Dialog // Marks this as a dialog for TalkBack
        //    contentDescription = "Full-screen dialog for [task, e.g., entering details]" // Optional: describe purpose
        //    liveRegion = LiveRegionMode.Polite // Announce when dialog opens
        //} *around* the Scaffold. I am not entirely sure about flagging this as a dialog anyway - I sort of get the MD3 "full screen dialog" concept, but it feels very technical and not something a user (accessibility-using or not) is likely to be actively aware of. I suppose there is some argument that it clues the user in to expect (as there is) a close icon and a "confirm" type icon in the top bar.
        // I suspect I shouldn't provide a contentDescription unless/until I do this for other screens, and at the moment I am trying not to be actively accessibility-hostile but not go out of my way to add stuff that may not be helpful. If the app is released it will be open source and I'm happy to take advice/patches if someone actually is using this.
        // I would rather attach the modifier to the Scaffold if I can, but I don't know if that will work correctly. Maybe it
        // doesn't work with a Box either, I haven't tested that. (Perplexity.ai says this semantics modifier won't truly flag it
        // as a dialog, but the link it gives doesn't actually say that. It doesn't have a better option, short of actually
        // using Dialog, which I know to my cost is utterly impractical or I'd already be using it. Perplexity does say I can
        // attach the modifier to the Scaffold no problem.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    // TODO: We need to check for unsaved changes here before blindly going back, I think
                    // TODO: We probably also need to do something to intercept back button clicks/back gestures and do the same validation?
                    IconButton(onClick = { onCloseRequest() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                title = { Text("TODO: Dialog Title") }, // TODO: Do not use "Edit price", you can also eg edit pack size and probably a free text notes field etc
                actions = {
                    // TODO: Can/should there be an icon with this textbutton?
                    // TODO: WHen/where should "data is not valid, we cannot save" check happen? We should probably be putting little warnings on the dialog components as the user edits, but we also need to check this before actually saving if they click save without resolving all the issues.
                    TextButton(onClick = { vm.updateOrInsertPrice(price); navController.popBackStack() }) {
                        Text("Save") // TODO: arbitrary, not thought about wording
                    }
                },
            )
        },
    ) { innerPadding ->

        // TODO: We could probably just pass innerPadding through to FullScreenDialog, that may or may not be clearer
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = screenBorder)
                .background(MaterialTheme.colorScheme.primary /* TODO DEBUG HACK SHOULD BE SOMETHING ELSE MAYBE NOT NEEDED TO BE SPEC EXPLICITLY */)
                .verticalScroll(rememberScrollState())
        ) {
            var packSize by remember { mutableStateOf("123") }
            var selectedUnitId: Long by remember { mutableStateOf(1) }
            var packPrice by remember { mutableStateOf("2.98") }
            //var notes by remember { mutableStateOf("My cool notes") }
            // TODO: Product and Store should maybe be in a row. Just hacking up a rough
            // dialog here for testing of my dialog box code (esp focus stuff) for now.
            LabeledItem(label = "Product") {
                Text(productName)
            }
            // Spacer(modifier = Modifier.height(300.dp)) // TODO TEMP HACK
            LabeledItem(label = "Store") {
                Text(storeName)
            }
            Spacer(modifier = Modifier.height(8.dp))
            val units = mutableListOf(
                UnitX(1, "g"),
                UnitX(2, "kg"),
                UnitX(3, "oz"),
                UnitX(4, "ml"),
                UnitX(5, "l")
            )
            Row {
                // TODO: Don't really like this way of showing pack size and unit etc, but
                // this is just a quick hack to get some "realistic-ish" content on the
                // dialog for testing
                // TODO: Using weight to size the components is also sucky, since we really
                // just want "a reasonable fixed size" for the unit with
                // the product taking whatever's left, but this will do for now.
                // TODO: This TextField will *not* show a cursor or let the value be changed
                // - I don't know if this is because my dialog code is breaking it, or I've
                // done something wrong here. OK, if I copy this code to HomeScreen() it
                // works, so it is probably dialog related. Yay!
                // TODO: Should I use OutlinedTextFields here? If so, for the drop down too.
                // TODO: Note that "Pack size" is blue only when the field is selected, but
                // "Unit" is always blue. This may be a localised colour tweak or it may be
                // a systemic glitch e.g. with my dropdown menu. FWIW the background of the
                // two fields appears to change differenly as I navigate around with cursor
                // keys too, although this *might* be normal - but worth trying to
                // investigate.
                // TODO: When the onscreen keyboard is up for "pack size", clicking on unit
                // opens the dropdown and hides the keyboard but the dropdown gets
                // positioned "to avoid" the keyboard - this might be normal/OK, but
                // check/read/think
                TextField(
                    label = { Text("Pack size") },
                    value = packSize,
                    onValueChange = { packSize = it },
                    // TODO: keyboardOptions here hints to on-screen keyboard, we probably also ought to prohibit non-numbers or (regional) decimal separator and *maybe* prohibit multiple decimal separators (but maybe this should just be an error report not prohibited, what's normal?)
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // TODO: We *may* want to disable the on click ripple whatsit for this, based on how the "official" experimental ExposedDropdownMenuBox behaves - although having thoughts about it and chatted with Grok and ChatGPT, maybe this is *good* and it is a weird quirk of (my impl) of the experimental "official" one that is weird
                MyExposedDropdownMenuBox(
                    selectedId = selectedUnitId,
                    onValueChange = { selectedUnitId = it },
                    label = { Text("Unit") },
                    items = units,
                    modifier = Modifier.weight(0.5f),
                    getId = { it.id },
                    getLabel = { it.name },
                )

            }
            Spacer(modifier = Modifier.height(8.dp))
            // TODO: Should the pack price be MaxWidth or something more "restrained" given it's short (5-ish digits absolute max)
            TextField(
                label = { Text("Pack price") },
                prefix = { Text("£") },
                value = packPrice,
                onValueChange = { packPrice = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(8.dp))
            // TODO: Can/should I do something to scroll the screen when focus enters this and the caret is half-hidden?
            TextField(
                label = { Text("Notes") },
                value = price.details,
                onValueChange = { price = price.copy(details = it) },
            )
            //}
        }

        if (showConfirmDialog) {
            AlertDialog(
                title = { Text("Discard unsaved changes?") },
                text = { Text("You have changes that won't be saved if you close.") },
                onDismissRequest = { showConfirmDialog = false },
                dismissButton = { TextButton(onClick = { showConfirmDialog = false }) { Text("Keep editing") } },
                confirmButton = { TextButton(onClick = { navController.popBackStack() }) { Text("Discard") } },
            )
        }
    }
}
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Settings") }, navigationIcon = {

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
            val darkTheme = isSystemInDarkTheme()

            ComposeTutorialTheme(darkTheme = darkTheme) {
                val window = (this as ComponentActivity).window

                /*
                // This allows us to control status bar icon color
                SideEffect {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
                    insetsController.isAppearanceLightStatusBars = !darkTheme // false in dark mode = light icons
                    insetsController.isAppearanceLightNavigationBars = !darkTheme
                }
                */

                // TODO: Grok told me I could/should shove a DisposableEffect() in here to futz around with isAppearanceLightStatusBars. I don't particularly trust it, but let's make a note in csae this is part of fixing any problems we might see on older Android versions later.
                // TODO: OK, I have added this Surface here because I wondered if I "should" as well as/instead of the Surfaces wrapping
                // the individual screens. Honestly don't know any more. There might be some slightly odd colours on the O6 but maybe
                // they are just its theme. I will have to play around with this and maybe it will become clearer as I write more code
                // etc. fillMaxHeight() is perhaps a bit unusual here but I was experimenting and thought I'd leave it in for now.
                Surface(
                    modifier = Modifier
                        .fillMaxSize()/* .safeDrawingPadding() */.imePadding(),
                    color = Color.Green /* MaterialTheme.colorScheme.background */
                ) {
                    Box(modifier = Modifier.safeDrawingPadding()) {
                        AppNavigation()
                    }
                }
            }/* TODO
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
    var vm: PriceTrackerViewModel = viewModel()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        // TODO!? modifier = Modifier.padding(innerPadding)
    ) {
        // TODO: The animation here is complete voodoo. This is a tweaked version of https://stackoverflow.com/questions/65643015/animating-between-composables-in-navigation-with-compose
        // and does actually seem to more-or-less behave (and consistently too). I didn't want to force 700ms, this feels a smidge fast at the (I think) default 300 but I think it is OK.
        // No, no, it isn't consistent. Sometimes the back animation is much faster than others. Not a clue. Not a f* clue.

        composable(
            "home",
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
            HomeScreen(vm, navController)
        }
        val tweenDurationMillisEnter = 700; // TODO: should probably be 300 in final version
        val tweenDurationMillisExit = 700; // TODO: should probably be 250 in final version
        composable(
            "settings", enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,

                    animationSpec = tween(
                        durationMillis = tweenDurationMillisEnter,
                        easing = LinearOutSlowInEasing
                    ),
                )

            },/* TODO This is probably not used as this is a "leaf" screen
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
                    animationSpec = tween(
                        durationMillis = tweenDurationMillisExit,
                        easing = FastOutLinearInEasing
                    )
                )
            }) {
            SettingsScreen(navController)
        }
        // TODO: This needs the correct vertical transitions, but let's not fuss with that for now
        composable("fullScreenDialog/{productId}/{storeId}") {
            backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLong() ?: 0
            val storeId = backStackEntry.arguments?.getString("storeId")?.toLong() ?: 0
            OuterFullScreenDialog(vm, navController, productId, storeId)
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

// TODO: It is just possible that we should be using somethnig like animateContentSize on the column containing the "cards" on the home screen, so that if (most likely because the Notes field appears/disappears/occupies more or less lines because text is longer or shorter) the card showing price-at-store changes size, the card show price-across-stores below it "animates" discreetly into its new position instead of jumping. With the current test data stuff, the upper card tends to be fixed size so this isn't too noticeable yet.