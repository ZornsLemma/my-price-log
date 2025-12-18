package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.domain.Quantity
import app.zornslemma.mypricelog.domain.UnitPrice
import app.zornslemma.mypricelog.domain.areDifferentUnitFamilies
import app.zornslemma.mypricelog.domain.getMeasurementUnitsOfSameQuantityTypeAndUnitFamily
import app.zornslemma.mypricelog.domain.getRelevantMeasurementUnits
import app.zornslemma.mypricelog.domain.withFriendlyDenominator
import app.zornslemma.mypricelog.ui.common.AsyncOperationStatus
import app.zornslemma.mypricelog.ui.common.format
import app.zornslemma.mypricelog.ui.common.formatPrice
import app.zornslemma.mypricelog.ui.common.isNotBusy
import app.zornslemma.mypricelog.ui.storePriceGridGutterWidth
import app.zornslemma.mypricelog.ui.storePriceGridLeftColumnWeight
import app.zornslemma.mypricelog.ui.storePriceGridRightColumnWeight
import java.util.Currency.getInstance

@Composable
fun PackPriceAndSizeRow(
    price: Double,
    count: Long,
    quantity: Quantity,
    dataSet: DataSet,
    asyncOperationStatus: AsyncOperationStatus,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(storePriceGridGutterWidth),
    ) {
        LabeledItem(
            label = stringResource(R.string.label_shelf_price),
            modifier = Modifier.weight(storePriceGridLeftColumnWeight),
        ) {
            val formattedPrice = formatPrice(price, dataSet, LocalConfiguration.current.locales[0])
            val formattedMeasure =
                quantity.toDisplayString(context, LocalConfiguration.current.locales[0])
            Text(
                if (count == 1L) {
                    stringResource(
                        R.string.message_price_for_quantity,
                        formattedPrice,
                        formattedMeasure,
                    )
                } else {
                    stringResource(
                        R.string.message_price_for_count_quantity,
                        formattedPrice,
                        count,
                        formattedMeasure,
                    )
                }
            )
        }

        val relevantUnitList =
            remember(dataSet, quantity.unit.quantityType) {
                dataSet.getRelevantMeasurementUnits(
                    quantity.unit.quantityType,
                    includeDisplayOnly = true,
                )
            }
        // NB: We are using remember() here to avoid redoing an expensive computation on every
        // recomposition. We *must not* use rememberSaveable(), because it does *not* recompute when
        // navigating back after a new item is selected in another screen, due to saved state
        // restoration behaviour. We could force recomputation by adding a composite key like
        // "$dataSet-$price-$measure", but that's a hack and not an ideal solution. ENHANCE: That's
        // not even the whole story. selectedUnitPriceUnit is *initialised* by an expensive
        // computation, but the user can change it, and we really ought to be remembering what they
        // select fairly persistently, at least across config changes (e.g. dark mode toggle). This
        // would seem to argue for some use of rememberSaveable() but there is some hellishly subtle
        // behaviour here whether with keys or "inputs=" and if we're not careful we end up crashing
        // as well because we preserve a denominator of the wrong quantity type as the item changes.
        // On top of these technical complexities, I am not even sure when we should preserve the
        // user's value - if for example the price changes enough that our recommended denominator
        // changes, should we override the user's selection?
        var selectedUnitPriceUnit by
            remember(dataSet, price, count, quantity) {
                val candidateDenominators =
                    dataSet.getMeasurementUnitsOfSameQuantityTypeAndUnitFamily(
                        quantity.unit,
                        includeDisplayOnly = true,
                    )
                val friendlyUnitPrice =
                    UnitPrice.calculate(price, count, quantity)
                        .withFriendlyDenominator(
                            quantity.unit,
                            getInstance(dataSet.currencyCode).defaultFractionDigits,
                            candidateDenominators,
                        )
                mutableStateOf(friendlyUnitPrice.denominator)
            }
        // If the user chooses a "bad" unit price denominator, we might end up with the unit price
        // being formatted to 0 with the available decimal places. I don't think this is really a
        // big deal and the alternatives (e.g. adding extra decimal places beyond the currency's
        // standard dps or rounding up instead of to nearest so the value isn't zero) are probably
        // worse.
        val unitPriceString =
            UnitPrice.calculate(price, count, quantity, selectedUnitPriceUnit)
                .format(context, dataSet, LocalConfiguration.current.locales[0])
        val context = LocalContext.current
        LabeledItemWithDropdown(
            modifier = Modifier.weight(storePriceGridRightColumnWeight),
            label = stringResource(R.string.label_unit_price),
            dropdownContentDescription = stringResource(R.string.content_description_select_unit),
            text = unitPriceString,
            enabled = asyncOperationStatus.isNotBusy(),
            items = relevantUnitList,
            getId = { it },
            getItemText = { "${it.perSymbol}${context.getString(it.symbol)}".trim() },
            getDividerBetween = { previousItem, item ->
                areDifferentUnitFamilies(previousItem, item)
            },
            selectedId = selectedUnitPriceUnit,
            onItemSelected = { selectedUnitPriceUnit = it },
        )
    }
}
