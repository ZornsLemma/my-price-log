package app.zornslemma.mypricelog.ui.screens.viewpricehistory

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.data.Item
import app.zornslemma.mypricelog.data.Price
import app.zornslemma.mypricelog.data.PriceHistory
import app.zornslemma.mypricelog.data.Source
import app.zornslemma.mypricelog.domain.Quantity
import app.zornslemma.mypricelog.domain.Repository
import app.zornslemma.mypricelog.domain.baseUnit
import app.zornslemma.mypricelog.domain.sanitisePriceHistoryUnits
import app.zornslemma.mypricelog.ui.common.EmptyParcelable
import app.zornslemma.mypricelog.ui.common.PersistentUiContent
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.map
import kotlinx.parcelize.Parcelize

@Parcelize
data class ViewPriceHistoryScreenStaticContent(
    val dataSet: DataSet,
    val item: Item,
    val source: Source,
    val price: Price?,
) : Parcelable

class ViewPriceHistoryViewModel(
    repository: Repository,
    savedStateHandle: SavedStateHandle,
    initialStaticContent: ViewPriceHistoryScreenStaticContent?,
) : ViewModel() {
    val uiContent =
        PersistentUiContent(
            this,
            savedStateHandle,
            "PriceHistory",
            EmptyParcelable(),
            initialStaticContent,
        )

    val priceHistoryListFlow =
        repository
            .getPriceHistory(
                uiContent.staticContent.dataSet.id,
                uiContent.staticContent.item.id,
                uiContent.staticContent.source.id,
            )
            .map { priceHistoryList ->
                uiContent.staticContent.dataSet.sanitisePriceHistoryUnits(priceHistoryList)
            }

    fun generatePriceHistoryDeltaList(
        priceHistoryList: List<PriceHistory>,
        @Suppress("unused") locale: Locale,
        confirmedAtFormatter: DateTimeFormatter,
    ) =
        // If there is no current price (it's been deleted), start the list with a null to represent
        // that deletion.
        (if (uiContent.staticContent.price == null) listOf(null) else emptyList()) +
            // Now add on the main list of deltas.
            //
            // Remember that we are doing a "backwards delta" here - we show the very latest
            // element in full, and for older elements we show differences between them and the
            // next newest element. This zip has every member of priceHistoryList appear exactly
            // once as oldPriceHistory.
            //
            // The reason we have collapseNulls() is because if a price is deleted then
            // reinstated identically then deleted again then reinstated identically again,
            // diff() will eliminate the reinstated version between the two deletions and we'd
            // show two deletions with nothing in between them.
            //
            // ENHANCE: It's possible the algorithm here could be better, particularly in
            // considering how deletions interact with diffs, but it's not completely clear
            // what's actually best. We could potentially consider a deletion as giving a "null
            // record" and the following version therefore appears in its entirely regardless of
            // how it compares to the previous version, but that might be over-verbose. In
            // practice all of this is relatively unimportant corner cases.
            (listOf(null) + priceHistoryList)
                .zip(priceHistoryList)
                .flatMap { (newPriceHistory, oldPriceHistory) ->
                    if (newPriceHistory == null)
                        listOf(oldPriceHistory.toPriceHistoryDelta(confirmedAtFormatter))
                    else {
                        val subList = mutableListOf<PriceHistoryDelta?>()
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
                .collapseNulls()
}

private fun <T> List<T?>.collapseNulls(): List<T?> =
    fold(mutableListOf()) { acc, item ->
        if (item != null || acc.lastOrNull() != null) {
            acc.add(item)
        }
        acc
    }

data class PriceHistoryDelta(
    val priceHistory: PriceHistory,
    val price: Double?,
    val count: Long?,
    val quantity: Quantity?,
    // confirmedAt is a string so we can do "user-resolution" de-duplication
    val confirmedAt: String?,
    val notes: String?,
    val modifiedAt: Instant,
)

fun PriceHistory.toPriceHistoryDelta(confirmedAtFormatter: DateTimeFormatter): PriceHistoryDelta =
    PriceHistoryDelta(
        priceHistory = this,
        price = price,
        count = count,
        quantity = Quantity(quantityInBaseUnit, userUnit.quantityType.baseUnit()).to(userUnit),
        confirmedAt = confirmedAtFormatter.format(confirmedAt),
        notes = notes,
        modifiedAt = modifiedAt,
    )

private fun diff(
    lhs: PriceHistory,
    rhs: PriceHistory,
    confirmedAtFormatter: DateTimeFormatter,
): PriceHistoryDelta? {
    val rhsQuantity =
        Quantity(rhs.quantityInBaseUnit, rhs.userUnit.quantityType.baseUnit()).to(rhs.userUnit)
    // Note that by using confirmedAtFormatter here and PriceHistory.confirmedAt being the resulting
    // string, if two PriceHistory records have visually indistinguishable confirmedAt values that
    // counts as the value not having changed, and if there are no other differences we will hide
    // the extra record entirely.
    val lhsConfirmedAt = confirmedAtFormatter.format(lhs.confirmedAt)
    val rhsConfirmedAt = confirmedAtFormatter.format(rhs.confirmedAt)
    val confirmedAt = if (lhsConfirmedAt == rhsConfirmedAt) null else rhsConfirmedAt
    val notes = if (lhs.notes.trim() == rhs.notes.trim()) null else rhs.notes
    val priceOrQuantityChanged =
        (lhs.price != rhs.price) ||
            (lhs.count != rhs.count) ||
            (lhs.quantityInBaseUnit != rhs.quantityInBaseUnit)
    if (priceOrQuantityChanged || confirmedAt != null || notes != null) {
        // The notes field is a legitimate source of diffs, but in practice it sometimes looks a bit
        // odd showing it when it's empty. For the moment we elide diffs where the only change is
        // that a note used to be empty and now it isn't. ENHANCE: There is probably scope for
        // tweaking this.
        if (
            !priceOrQuantityChanged &&
                confirmedAt == null &&
                notes != null &&
                notes.trim().isEmpty()
        ) {
            return null
        }
        return PriceHistoryDelta(
            priceHistory = rhs,
            price = if (!priceOrQuantityChanged) null else rhs.price,
            count = if (!priceOrQuantityChanged) null else rhs.count,
            quantity = if (!priceOrQuantityChanged) null else rhsQuantity,
            confirmedAt = confirmedAt,
            notes = notes,
            modifiedAt = rhs.modifiedAt,
        )
    } else {
        return null
    }
}
