package com.example.composetutorial.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.composetutorial.EditItemScreenUIContent
import com.example.composetutorial.EditPriceScreenUIContent
import com.example.composetutorial.EditSourceScreenUIContent
import com.example.composetutorial.HomeScreenUIContent
import com.example.composetutorial.SelectItemScreenUIContent
import com.example.composetutorial.SelectSourceScreenUIContent
import com.example.composetutorial.ViewPriceHistoryScreenUIContent
import com.example.composetutorial.domain.createCurrencyFormat
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.models.EditableDataSet
import com.example.composetutorial.models.EditablePrice
import com.example.composetutorial.models.Item
import com.example.composetutorial.models.Source
import com.example.composetutorial.models.toEditable
import java.util.Locale

// TODO: Maybe this file should be moved to be a sibling of AppNavigation.kt?

// Shared ViewModel to pass data between screens
// TODO: Some inconsistency between "UIContent" and "Content" here - think about renaming.
class SharedViewModel : ViewModel() {
    // This is only nullable to provide us with an easy initial value to use. In use
    // setEditPriceScreenState() should always have been called before it is used.
    var editPriceScreenUIContent: EditPriceScreenUIContent? = null

    var viewPriceHistoryScreenUIContent: ViewPriceHistoryScreenUIContent? = null

    // frozenLocale becomes part of the edit screen state - it was used to convert the doubles to
    // strings, and we will use it to convert the strings back to doubles if the user saves. If the
    // user changes the locale while on the edit screen, we do *not* want to reflect that change
    // immediately because it makes parsing the strings ambiguous. (TODO: This is not heavily tested
    // and is not all that an important case, but I am at least trying to do things right.)
    fun setEditPriceScreenContent(
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
        editPriceScreenUIContent = EditPriceScreenUIContent(
            editablePrice = mutableStateOf(editablePrice),
            originalPrice = editablePrice,
            dataSet = dataSet,
            item = item,
            source = source,
            nonLinearEdit = false,
            frozenLocale = frozenLocale,
        )
    }

    // TODO: It might be possible to share some code with the non-2 version or refactor but let's just
    // bash this out for now.
    fun setEditPriceScreenContent2(
        dataSet: DataSet,
        item: Item,
        source: Source,
        editablePrice: EditablePrice,
        frozenLocale: Locale
    ) {
        editPriceScreenUIContent = EditPriceScreenUIContent(
            editablePrice = mutableStateOf(editablePrice),
            originalPrice = editablePrice,
            dataSet = dataSet,
            item = item,
            source = source,
            nonLinearEdit = true,
            frozenLocale = frozenLocale,
        )
    }

    // TODO: Some overlap with setEditPriceScreenContent()?
    fun setViewPriceHistoryScreenContent(
        uiContent: HomeScreenUIContent,
        frozenLocale: Locale
    ) {
        // !! is justified because uiContent was shown on the home screen and the view history option
        // was enabled, which can only happen if we have all three available.
        val dataSet = uiContent.dataSet!!
        val item = uiContent.item!!
        val source = uiContent.source!!
        val price =
            uiContent.priceAnalysis.augmentedPriceList.map { it.basePrice }.find { it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id }

        viewPriceHistoryScreenUIContent = ViewPriceHistoryScreenUIContent(
            dataSet = dataSet,
            item = item,
            source = source,
            price = price,
        )
    }

    // TODO: Rename the following now they are just List<T>? not a UIContent structure? Or is the "UIContent" convention more valuable?
    var selectDataSetScreenUIContent: List<DataSet>? = null
    var selectItemScreenUIContent: SelectItemScreenUIContent? = null
    var selectSourceScreenUIContent: SelectSourceScreenUIContent? = null

    // TODO: The "doubling" in the next three functions is a temporary hack to show that we use the
    // initial list and then it gets replaced by the query results from the database. The map step
    // is because we use the IDs as keys on LazyColumn and if there are duplicate IDs it gets upset;
    // of course with real data there won't be duplicate IDs at all.

    fun setSelectDataSetScreenContent(uiContent: HomeScreenUIContent) {
        selectDataSetScreenUIContent =
            uiContent.dataSetList + uiContent.dataSetList.map { it -> it.copy(id = it.id * 1000) }
    }

    fun setSelectItemScreenContent(uiContent: HomeScreenUIContent) {
        selectItemScreenUIContent = SelectItemScreenUIContent(
            uiContent.itemList + uiContent.itemList.map { it -> it.copy(id = it.id * 1000) },
            uiContent.dataSet!!
        )
    }

    fun setSelectSourceScreenContent(uiContent: HomeScreenUIContent) {
        selectSourceScreenUIContent = SelectSourceScreenUIContent(
            uiContent.sourceList + uiContent.sourceList.map { it -> it.copy(id = it.id * 1000) },
            uiContent.dataSet!!
        )
    }

    /* TODO DELETE
    var editDataSetScreenUIContent: EditDataSetScreenUIContent? = null
    */
    var editDataSetScreenInitialUIContent: EditableDataSet? = null // TODO RENAME?

    fun setEditDataSetScreenInitialUIContent(dataSet: DataSet?, locale: Locale) {
        /* TODO DELETE
        val editableDataSet = dataSet.toEditable(locale)
        editDataSetScreenUIContent = EditDataSetScreenUIContent(
            editableDataSet = mutableStateOf(editableDataSet),
            originalDataSet = editableDataSet,
        )
        */
        editDataSetScreenInitialUIContent = dataSet.toEditable(locale)
    }

    var editItemScreenUIContent: EditItemScreenUIContent? = null

    fun setEditItemScreenContent(item: Item?, dataSet: DataSet) {
        val editableItem = item.toEditable(dataSet)
        editItemScreenUIContent = EditItemScreenUIContent(
            editableItem = mutableStateOf(editableItem),
            originalItem = editableItem,
            dataSet = dataSet,
        )
    }

    var editSourceScreenUIContent: EditSourceScreenUIContent? = null

    fun setEditSourceScreenContent(
        // TODO: name should include "FromBlah"? or maybe that's a silly convention?
        source: Source?,
        dataSet: DataSet,
        frozenLocale: Locale
    ) {
        val editableSource = source.toEditable(dataSet.id, frozenLocale)
        editSourceScreenUIContent = EditSourceScreenUIContent(
            editableSource = mutableStateOf(editableSource),
            originalSource = editableSource,
            dataSet = dataSet,
            frozenLocale = frozenLocale,
        )
    }
}
