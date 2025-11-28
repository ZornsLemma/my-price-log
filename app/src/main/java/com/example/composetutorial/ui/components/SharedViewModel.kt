package com.example.composetutorial.ui.components

import androidx.lifecycle.ViewModel
import com.example.composetutorial.domain.createCurrencyFormat
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.EditableDataSet
import com.example.composetutorial.models.EditableItem
import com.example.composetutorial.models.EditablePrice
import com.example.composetutorial.models.EditableSource
import com.example.composetutorial.models.Item
import com.example.composetutorial.models.Price
import com.example.composetutorial.models.Source
import com.example.composetutorial.models.toEditable
import com.example.composetutorial.ui.screens.home.HomeScreenUIContent
import java.util.Locale

// TODO: Maybe this file should be moved to be a sibling of AppNavigation.kt?

// Shared ViewModel to pass data between screens
// TODO: Some inconsistency between "UIContent" and "Content" here - think about renaming.
class SharedViewModel : ViewModel() {
    data class EditPriceScreenInitialUiContent(
        val editablePrice: EditablePrice,
        val dataSet: DataSet,
        val item: Item,
        val source: Source,
        val nonLinearEdit: Boolean,
        val frozenLocale: Locale,
        )

    // This is only nullable to provide us with an easy initial value to use. In use
    // setEditPriceScreenState() should always have been called before it is used. TODO: This comment is probably still true but perhaps a bit "feeling my way" and not really useful now?
    var editPriceScreenInitialUiContent: EditPriceScreenInitialUiContent? = null

    fun setEditPriceScreenInitialUiContent(
        uiContent: HomeScreenUIContent,
        frozenLocale: Locale
    ) {
        // !! is justified because uiContent was shown on the home screen and the edit price button
        // was visible, which can only happen if we have all three available.
        val dataSet = uiContent.dataSet!!
        val item = uiContent.item!!
        val source = uiContent.source!!

        val price =
            uiContent.priceAnalysis.augmentedPriceList.map { it.basePrice }.find { it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id }

        val editablePrice = if (price != null)
            price.toEditable(frozenLocale, dataSet.createCurrencyFormat(frozenLocale))
        else EditablePrice.forNew(
            dataSetId = dataSet.id,
            itemId = item.id,
            sourceId = source.id,
            itemDefaultUnit = item.defaultUnit
        )

        editPriceScreenInitialUiContent = EditPriceScreenInitialUiContent(
            editablePrice = editablePrice,
            dataSet = dataSet,
            item = item,
            source = source,
            nonLinearEdit = false,
            frozenLocale = frozenLocale,
        )
    }


    // frozenLocale becomes part of the edit screen state - it was used to convert the doubles to
    // strings, and we will use it to convert the strings back to doubles if the user saves. If the
    // user changes the locale while on the edit screen, we do *not* want to reflect that change
    // immediately because it makes parsing the strings ambiguous. (TODO: This is not heavily tested
    // and is not all that an important case, but I am at least trying to do things right.)

    // TODO: It might be possible to share some code with the non-2 version or refactor but let's just
    // bash this out for now.
    fun setEditPriceScreenContent2(
        dataSet: DataSet,
        item: Item,
        source: Source,
        editablePrice: EditablePrice,
        frozenLocale: Locale
    ) {
        editPriceScreenInitialUiContent = EditPriceScreenInitialUiContent(
            editablePrice = editablePrice,
            dataSet = dataSet,
            item = item,
            source = source,
            nonLinearEdit = true,
            frozenLocale = frozenLocale,
        )
    }

    data class ViewPriceHistoryScreenInitialUiContent(
        val dataSet: DataSet,
        val item: Item,
        val source: Source,
        val price: Price?
    )
    var viewPriceHistoryScreenInitialUiContent: ViewPriceHistoryScreenInitialUiContent? = null


    // TODO: Some overlap with setEditPriceScreenContent()?
    fun setViewPriceHistoryScreenInitialUiContent(
        uiContent: HomeScreenUIContent,
        frozenLocale: Locale // TODO: Needed!? Do we even use this? I suspect locale should nto be frozen for a view-only screen
    ) {
        // !! is justified because uiContent was shown on the home screen and the view history option
        // was enabled, which can only happen if we have all three available.
        val dataSet = uiContent.dataSet!!
        val item = uiContent.item!!
        val source = uiContent.source!!
        val price =
            uiContent.priceAnalysis.augmentedPriceList.map { it.basePrice }.find { it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id }

        viewPriceHistoryScreenInitialUiContent = ViewPriceHistoryScreenInitialUiContent(
            dataSet = dataSet,
            item = item,
            source = source,
            price = price,
        )
    }

    data class SelectItemScreenInitialUiContent(
        val itemList: List<Item>,
        val dataSet: DataSet
    )

    data class SelectSourceScreenInitialUiContent(
        val sourceList: List<Source>,
        val dataSet: DataSet
    )

    // TODO: Rename the following now they are just List<T>? not a UIContent structure? Or is the "UIContent" convention more valuable?
    var selectDataSetScreenUIContent: List<DataSet>? = null
    var selectItemScreenInitialUiContent: SelectItemScreenInitialUiContent? = null
    var selectSourceScreenInitialUiContent: SelectSourceScreenInitialUiContent? = null

    // TODO: The "doubling" in the next three functions is a temporary hack to show that we use the
    // initial list and then it gets replaced by the query results from the database. The map step
    // is because we use the IDs as keys on LazyColumn and if there are duplicate IDs it gets upset;
    // of course with real data there won't be duplicate IDs at all.

    fun setSelectDataSetScreenContent(uiContent: HomeScreenUIContent) {
        selectDataSetScreenUIContent =
            uiContent.dataSetList + uiContent.dataSetList.map { it -> it.copy(id = it.id * 1000) }
    }

    fun setSelectItemScreenInitialUiContent(uiContent: HomeScreenUIContent) {
        selectItemScreenInitialUiContent = SelectItemScreenInitialUiContent(
            uiContent.itemList + uiContent.itemList.map { it -> it.copy(id = it.id * 1000) },
            uiContent.dataSet!!
        )
    }

    fun setSelectSourceScreenInitialUiContent(uiContent: HomeScreenUIContent) {
        selectSourceScreenInitialUiContent = SelectSourceScreenInitialUiContent(
            uiContent.sourceList + uiContent.sourceList.map { it -> it.copy(id = it.id * 1000) },
            uiContent.dataSet!!
        )
    }

    var editDataSetScreenInitialUiContent: EditableDataSet? = null

    fun setEditDataSetScreenInitialUiContent(dataSet: DataSet?, locale: Locale) {
        editDataSetScreenInitialUiContent = dataSet.toEditable(locale)
    }

    // TODO: Probably follow the naming model for EditItemScreen and EditDataSetScreen in all the other Edit* things?
    data class EditItemScreenInitialUiContent(
        val editableItem: EditableItem,
        val dataSet: DataSet,
    )

    var editItemScreenInitialUiContent: EditItemScreenInitialUiContent? = null

    fun setEditItemScreenInitialUiContent(item: Item?, dataSet: DataSet) {
        val editableItem = item.toEditable(dataSet)
        editItemScreenInitialUiContent = EditItemScreenInitialUiContent(editableItem, dataSet)
    }

    data class EditSourceScreenInitialUiContent(
        val editableSource: EditableSource,
        val dataSet: DataSet,
        val frozenLocale: Locale
    )

    var editSourceScreenInitialUiContent: EditSourceScreenInitialUiContent? = null

    fun setEditSourceScreenInitialUiContent(
        source: Source?,
        dataSet: DataSet,
        frozenLocale: Locale
    ) {
        val editableSource = source.toEditable(dataSet.id, frozenLocale)
        editSourceScreenInitialUiContent = EditSourceScreenInitialUiContent(
            editableSource = editableSource,
            dataSet = dataSet,
            frozenLocale = frozenLocale,
        )
    }
}
