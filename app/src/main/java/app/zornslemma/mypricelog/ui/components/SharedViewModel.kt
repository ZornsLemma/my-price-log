package app.zornslemma.mypricelog.ui.components

import androidx.lifecycle.ViewModel
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.EditableDataSet
import app.zornslemma.mypricelog.data.EditableItem
import app.zornslemma.mypricelog.data.EditablePrice
import app.zornslemma.mypricelog.data.EditableSource
import app.zornslemma.mypricelog.data.Item
import app.zornslemma.mypricelog.data.Price
import app.zornslemma.mypricelog.data.Source
import app.zornslemma.mypricelog.data.toEditable
import app.zornslemma.mypricelog.domain.createCurrencyFormat
import app.zornslemma.mypricelog.domain.sanitiseItems
import app.zornslemma.mypricelog.ui.screens.editprice.EditPriceScreenStaticContent
import app.zornslemma.mypricelog.ui.screens.editsource.EditSourceScreenStaticContent
import app.zornslemma.mypricelog.ui.screens.home.HomeScreenUiContent
import java.util.Locale

class SharedViewModel : ViewModel() {
    data class EditPriceScreenInitialUiContent(
        val editablePrice: EditablePrice,
        val staticContent: EditPriceScreenStaticContent,
    )

    var editPriceScreenInitialUiContent: EditPriceScreenInitialUiContent? = null

    fun setEditPriceScreenInitialUiContent(uiContent: HomeScreenUiContent, frozenLocale: Locale) {
        // !! is justified because uiContent was shown on the home screen and the edit price button
        // was visible, which can only happen if we have all three available.
        val dataSet = uiContent.dataSet!!
        val item = uiContent.item!!
        val source = uiContent.source!!

        val price =
            uiContent.priceAnalysis.augmentedPriceList
                .map { it.basePrice }
                .find {
                    it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id
                }

        val editablePrice =
            price?.toEditable(frozenLocale, dataSet.createCurrencyFormat(frozenLocale))
                ?: EditablePrice.forNew(
                    dataSetId = dataSet.id,
                    itemId = item.id,
                    sourceId = source.id,
                    itemDefaultUnit = item.defaultUnit,
                )

        editPriceScreenInitialUiContent =
            EditPriceScreenInitialUiContent(
                editablePrice = editablePrice,
                staticContent =
                    EditPriceScreenStaticContent(
                        dataSet = dataSet,
                        item = item,
                        source = source,
                        nonLinearEdit = false,
                        frozenLocale = frozenLocale,
                    ),
            )
    }

    fun setEditPriceScreenInitialUiContent(
        dataSet: DataSet,
        item: Item,
        source: Source,
        editablePrice: EditablePrice,
        frozenLocale: Locale,
    ) {
        editPriceScreenInitialUiContent =
            EditPriceScreenInitialUiContent(
                editablePrice = editablePrice,
                staticContent =
                    EditPriceScreenStaticContent(
                        dataSet = dataSet,
                        item = item,
                        source = source,
                        nonLinearEdit = true,
                        frozenLocale = frozenLocale,
                    ),
            )
    }

    data class ViewPriceHistoryScreenInitialUiContent(
        val dataSet: DataSet,
        val item: Item,
        val source: Source,
        val price: Price?,
    )

    var viewPriceHistoryScreenInitialUiContent: ViewPriceHistoryScreenInitialUiContent? = null

    fun setViewPriceHistoryScreenInitialUiContent(uiContent: HomeScreenUiContent) {
        // !! is justified because uiContent was shown on the home screen and the view history
        // option was enabled, which can only happen if we have all three available.
        val dataSet = uiContent.dataSet!!
        val item = uiContent.item!!
        val source = uiContent.source!!
        val price =
            uiContent.priceAnalysis.augmentedPriceList
                .map { it.basePrice }
                .find {
                    it.dataSetId == dataSet.id && it.itemId == item.id && it.sourceId == source.id
                }

        viewPriceHistoryScreenInitialUiContent =
            ViewPriceHistoryScreenInitialUiContent(
                dataSet = dataSet,
                item = item,
                source = source,
                price = price,
            )
    }

    data class SelectItemScreenInitialUiContent(val itemList: List<Item>, val dataSet: DataSet)

    data class SelectSourceScreenInitialUiContent(
        val sourceList: List<Source>,
        val dataSet: DataSet,
    )

    var selectDataSetScreenInitialUiContent: List<DataSet>? = null
    var selectItemScreenInitialUiContent: SelectItemScreenInitialUiContent? = null
    var selectSourceScreenInitialUiContent: SelectSourceScreenInitialUiContent? = null

    // If it's helpful for debugging, one way to make it possible to see that the initial list is
    // used for the follow select screen is to add a tweaked copy of the list to itself. This gives
    // a doubled-up list initially which is then replaced when the database query returns. For
    // example:
    //     selectDataSetScreenInitialUiContent =
    //         uiContent.dataSetList +
    //         uiContent.dataSetList.map { it -> it.copy(id = it.id * 1000) }
    // (The ID change is needed because we use the ID as a LazyColumn key.)

    fun setSelectDataSetScreenContent(uiContent: HomeScreenUiContent) {
        selectDataSetScreenInitialUiContent = uiContent.dataSetList
    }

    fun setSelectItemScreenInitialUiContent(uiContent: HomeScreenUiContent) {
        selectItemScreenInitialUiContent =
            SelectItemScreenInitialUiContent(uiContent.itemList, uiContent.dataSet!!)
    }

    fun setSelectSourceScreenInitialUiContent(uiContent: HomeScreenUiContent) {
        selectSourceScreenInitialUiContent =
            SelectSourceScreenInitialUiContent(uiContent.sourceList, uiContent.dataSet!!)
    }

    var editDataSetScreenInitialUiContent: EditableDataSet? = null

    fun setEditDataSetScreenInitialUiContent(dataSet: DataSet?, locale: Locale) {
        editDataSetScreenInitialUiContent = dataSet.toEditable(locale)
    }

    data class EditItemScreenInitialUiContent(val editableItem: EditableItem, val dataSet: DataSet)

    var editItemScreenInitialUiContent: EditItemScreenInitialUiContent? = null

    fun setEditItemScreenInitialUiContent(item: Item?, dataSet: DataSet) {
        val sanitisedItem: Item? = item?.let { dataSet.sanitiseItems(listOf(it)).first() }
        val editableItem = sanitisedItem.toEditable(dataSet)
        editItemScreenInitialUiContent = EditItemScreenInitialUiContent(editableItem, dataSet)
    }

    data class EditSourceScreenInitialUiContent(
        val editableSource: EditableSource,
        val staticContent: EditSourceScreenStaticContent,
    )

    var editSourceScreenInitialUiContent: EditSourceScreenInitialUiContent? = null

    fun setEditSourceScreenInitialUiContent(
        source: Source?,
        dataSet: DataSet,
        frozenLocale: Locale,
    ) {
        val editableSource = source.toEditable(dataSet.id, frozenLocale)
        editSourceScreenInitialUiContent =
            EditSourceScreenInitialUiContent(
                editableSource = editableSource,
                EditSourceScreenStaticContent(dataSet = dataSet, frozenLocale = frozenLocale),
            )
    }
}
