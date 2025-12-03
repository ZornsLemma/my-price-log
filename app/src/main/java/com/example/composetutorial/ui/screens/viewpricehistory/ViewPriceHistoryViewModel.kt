package com.example.composetutorial.ui.screens.viewpricehistory

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.composetutorial.domain.Quantity
import com.example.composetutorial.domain.Repository
import com.example.composetutorial.domain.baseUnit
import com.example.composetutorial.data.PriceHistory
import com.example.composetutorial.domain.sanitisePriceHistoryUnits
import com.example.composetutorial.data.DataSet
import com.example.composetutorial.data.Item
import com.example.composetutorial.data.Price
import com.example.composetutorial.data.Source
import com.example.composetutorial.ui.common.EmptyParcelable
import com.example.composetutorial.ui.common.PersistentUiContent
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale

// TODO: MOVE?
@Parcelize
data class ViewPriceHistoryScreenStaticContent(
    val dataSet: DataSet,
    val item: Item,
    val source: Source,
    val price: Price?,
) : Parcelable

class ViewPriceHistoryViewModel(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialStaticContent: ViewPriceHistoryScreenStaticContent?
) : ViewModel() {
    val uiContent = PersistentUiContent(
        this,
        savedStateHandle,
        "PriceHistory",
        EmptyParcelable(),
        initialStaticContent
    )

    val priceHistoryListFlow = repository.getPriceHistory(
        uiContent.staticContent.dataSet.id,
        uiContent.staticContent.item.id,
        uiContent.staticContent.source.id
    ).map { priceHistoryList -> sanitisePriceHistoryUnits(uiContent.staticContent.dataSet, priceHistoryList) }

    fun generatePriceHistoryDeltaList(
        priceHistoryList: List<PriceHistory>,
        locale: Locale,
        confirmedAtFormatter: DateTimeFormatter
    ) =
    // If there is no current price (it's been deleted), start the list with a null to represent
        // that deletion.
        (if (uiContent.staticContent.price == null) listOf(null) else emptyList()) +
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

data class PriceHistoryDelta(
    val priceHistory: PriceHistory,
    val price: Double?,
    val count: Long?,
    val quantity: Quantity?,
    // confirmedAt is a string so we can do "user-resolution" de-duplication
    val confirmedAt: String?,
    val notes: String?,
    val modifiedAt: Instant
)

fun PriceHistory.toPriceHistoryDelta(confirmedAtFormatter: DateTimeFormatter): PriceHistoryDelta =
    PriceHistoryDelta(
        priceHistory = this,
        price = price,
        count = count,
        quantity = Quantity(quantityInBaseUnit, userUnit.quantityType.baseUnit()).to(
            userUnit
        ),
        confirmedAt = confirmedAtFormatter.format(confirmedAt),
        notes = notes,
        modifiedAt = modifiedAt
    )

// TODO: Where does this belong and what naming and calling convention should it have?!?!?!
private fun diff(
    lhs: PriceHistory,
    rhs: PriceHistory,
    confirmedAtFormatter: DateTimeFormatter
): PriceHistoryDelta? {
    val rhsQuantity = Quantity(
        rhs.quantityInBaseUnit,
        rhs.userUnit.quantityType.baseUnit()
    ).to(rhs.userUnit)
    // Note that by using confirmedAtFormatter here and PriceHistory.confirmedAt being the resulting
    // string, if two PriceHistory records have visually indistinguishable confirmedAt values we
    // won't show them, and if there are no other differences we will hide the extra record
    // entirely.
    val lhsConfirmedAt = confirmedAtFormatter.format(lhs.confirmedAt)
    val rhsConfirmedAt = confirmedAtFormatter.format(rhs.confirmedAt)
    Log.d("MyApp", "lhsConfirmedAt $lhsConfirmedAt rhsConfirmedAt $rhsConfirmedAt")
    val confirmedAt = if (lhsConfirmedAt == rhsConfirmedAt) null else rhsConfirmedAt
    val notes = if (lhs.notes.trim() == rhs.notes.trim()) null else rhs.notes
    val priceOrQuantityChanged = (lhs.price != rhs.price) || (lhs.count != rhs.count) || (lhs.quantityInBaseUnit != rhs.quantityInBaseUnit)
    if (priceOrQuantityChanged || confirmedAt != null || notes != null) {
        // The notes field is a legitimate source of diffs, but in practice it sometimes looks a bit
        // odd showing it when it's empty. For the moment we elide diffs where the only change is
        // that a note used to be empty and now it isn't. ENHANCE: There is probably scope for
        // tweaking this.
        if (!priceOrQuantityChanged && confirmedAt == null && notes != null && notes.trim().isEmpty()) {
            return null
        }
        return PriceHistoryDelta(
            priceHistory = rhs,
            price = if (!priceOrQuantityChanged) null else rhs.price,
            count = if (!priceOrQuantityChanged) null else rhs.count,
            quantity = if (!priceOrQuantityChanged) null else rhsQuantity,
            confirmedAt = confirmedAt,
            notes = notes,
            modifiedAt = rhs.modifiedAt
        )
    } else {
        return null
    }
}
