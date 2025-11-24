package com.example.composetutorial.ui.screens.viewpricehistory

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.composetutorial.PriceHistoryDelta
import com.example.composetutorial.ViewPriceHistoryScreenUIContent
import com.example.composetutorial.diff
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.models.PriceHistory
import com.example.composetutorial.sanitisePriceHistoryUnits
import com.example.composetutorial.toPriceHistoryDelta
import kotlinx.coroutines.flow.map
import java.time.format.DateTimeFormatter
import java.util.Locale

class ViewPriceHistoryViewModel(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle,
    val uiContent: ViewPriceHistoryScreenUIContent,
) : ViewModel() {
    init {
        Log.d("MyApp", "ViewPriceHistoryViewModel.init($uiContent)")
        uiContent.saveState(savedStateHandle)
    }

    val priceHistoryListFlow = repository.getPriceHistory(
        uiContent.dataSet.id,
        uiContent.item.id,
        uiContent.source.id
    ).map { priceHistoryList -> sanitisePriceHistoryUnits(uiContent.dataSet, priceHistoryList) }

    fun generatePriceHistoryDeltaList(
        priceHistoryList: List<PriceHistory>,
        locale: Locale,
        confirmedAtFormatter: DateTimeFormatter
    ) =
    // If there is no current price (it's been deleted), start the list with a null to represent
        // that deletion.
        (if (uiContent.price == null) listOf(null) else emptyList()) +
                // Now add on the main list of deltas.
                // Remember that we are doing a "backwards delta" here - we show the very latest element in full,
                // and for older elements we show differences between them and the next newest element. This zip
                // has every member of priceHistoryList appear exactly once as oldPriceHistory.
                // TODO: Once the dust settles, test this with a price being deleted then reinstated
                // with no changes and check how it appears
                // TODO: I think it's technically correct (but need to test properly) but we can end
                // up with
                // multiple adjacent deletes if the intermediate deltas are "empty". We should probably
                // collapse multiple adjacent nulls down to one - it's probably not worth over-faffing to try
                // to show the precise history or to force a diff card in there. Maybe it's OK as it is - it
                // does kind of reflect reality (multiple deletes and we know - but it maybe looks odd -
                // there were no real changes in between them because the diff cards are missing) - but I
                // suspect it's more confusng than helpful and no one really cares about the history at that
                // level of detail.
                (listOf(null) + priceHistoryList).zip(priceHistoryList)
                    .flatMap { (newPriceHistory, oldPriceHistory) ->
                        if (newPriceHistory == null) listOf(oldPriceHistory.toPriceHistoryDelta(
                            confirmedAtFormatter
                        )) else {
                            var subList = mutableListOf<PriceHistoryDelta?>()
                            if (newPriceHistory.priceId != oldPriceHistory.priceId) {
                                subList.add(null)
                            }
                            val delta = diff(newPriceHistory, oldPriceHistory, confirmedAtFormatter)
                            if (delta != null) {
                                subList.add(delta)
                            }
                            subList
                        }
                    }
}
