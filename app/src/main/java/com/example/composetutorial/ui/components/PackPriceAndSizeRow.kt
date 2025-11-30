package com.example.composetutorial.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.composetutorial.R
import com.example.composetutorial.domain.areDifferentUnitFamilies
import com.example.composetutorial.domain.Quantity
import com.example.composetutorial.domain.UnitPrice
import com.example.composetutorial.ui.common.format
import com.example.composetutorial.domain.getMeasurementUnitsOfSameQuantityTypeAndUnitFamily
import com.example.composetutorial.domain.getRelevantMeasurementUnits
import com.example.composetutorial.domain.getRelevantUnitFamilies
import com.example.composetutorial.domain.withFriendlyDenominator
import com.example.composetutorial.ui.common.formatPrice
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.ui.common.AsyncOperationStatus
import com.example.composetutorial.ui.common.isNotBusy
import java.util.Currency

@Composable
fun PackPriceAndSizeRow(
    price: Double,
    count: Long,
    quantity: Quantity,
    dataSet: DataSet,
    asyncOperationStatus: AsyncOperationStatus
) {
    val context = LocalContext.current

    // The two elements of this row share the space 60%/40%. The shelf price can get quite long for
    // multipack items and 50%/50% starts to get tight on small phones. We don't really need that
    // much space for the unit price either. This might ruin a proper 2x2 grid, but in practice at
    // least for now the row below is "Confirmed" and it theoretically has the full width of the
    // display. TODO HACKED TEMPORARILY TO 55/45 TO TRY TO MOVE THE PRICE INDICATOR INTO THE
    // BOTTOM RIGHT CELL. THIS MAY OR MAY NOT WORK OUT, IT MIGHT "FIT" FOR SPANISH AND ENGLISH
    // BUT NOT SURE ABOUT OTHER LANGUAGES, AND ALSO NEED TO DECIDE IF I LIKE THE LOOK. IT DOES
    // JUST ABOUT SEEM OK WITH SMALL PHONE LAYOUT AND MY DEMO PRICES THOUGH.
    // TODO: Do we need some kind of spacer or padding or border to stop the two running together
    // too much if the text happens to be just the wrong size? Maybe that's almost better. Be
    // careful not to break the alignment of the second row of the grid - I suspect adding rhs
    // padding or border to the shelf price might be the safest way to do this without it affecting other parts of the layout.

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        // This Box(Row(LabeledItem, Spacer)) structure is to stop the LabeledItem touching the left
        // edge of the unit price box if the text just happens to be precisely the right size, but
        // using an "inner padding" rather than normal padding so the other row of the "2x2 grid"
        // doesn't need to do the same thing. TODO: To be honest it might be as easy just to apply a
        // right padding to the LabeledItem and the leftmost element of the second row.
        Box(modifier = Modifier.weight(weight = 0.55f)) {
            Row {
                LabeledItem(
                    label = stringResource(R.string.label_shelf_price),
                    modifier = Modifier.weight(1f)

                ) {
                    val formattedPrice = formatPrice(
                        price,
                        dataSet,
                        LocalConfiguration.current.locales[0]
                    )
                    val formattedMeasure =
                        quantity.toDisplayString(context, LocalConfiguration.current.locales[0])
                    Text(
                        if (count == 1L) {
                            stringResource(
                                R.string.message_price_for_quantity,
                                formattedPrice, formattedMeasure
                            )
                        } else {
                            stringResource(
                                R.string.message_price_for_count_quantity,
                                formattedPrice, count, formattedMeasure
                            )
                        }
                    )
                }
                Box(modifier = Modifier.width(4.dp))
            }
        }

        val relevantUnitFamilies =
            remember(dataSet) { getRelevantUnitFamilies(dataSet) }

        val relevantUnitList =
            remember(dataSet, quantity.unit.quantityType) {
                getRelevantMeasurementUnits(
                    dataSet,
                    quantity.unit.quantityType,
                    includeDisplayOnly = true
                )
            }
        Log.d("MyAppQA", "measure identityHashCode=${System.identityHashCode(quantity)}")
        // NB: We are using remember() here to avoid redoing an expensive computation on every
        // recomposition. We *must not* use rememberSaveable(), because it does *not* recompute when
        // navigating back after a new item is selected in another screen, due to saved state
        // restoration behaviour. We could force recomputation by adding a composite key like
        // "$dataSet-$price-$measure", but that's a hack and not an ideal solution.
        // ENHANCE: That's not even the whole story. selectedUnitPriceUnit is
        // *initialised* by an expensive computation, but the user can change it, and we really
        // ought to be remembering what they select fairly persistently, at least across config
        // changes (e.g. dark mode toggle). This would seem to argue for some use of rememberSaveable()
        // but there is some hellishly subtle behaviour here whether with keys or "inputs=" and if
        // we're not careful we end up crashing as well because we preserve a denominator of the
        // wrong quantity type as the item changes. On top of these technical complexities, I am
        // not even sure when we should preserve the user's value - if for example the price changes
        // enough that our recommended denominator changes, should we override the user's selection?
        var selectedUnitPriceUnit by remember(dataSet, price, count, quantity) {
            Log.d("MyAppQA", "rememberSaveable $price $quantity")
            val candidateDenominators = getMeasurementUnitsOfSameQuantityTypeAndUnitFamily(
                dataSet,
                quantity.unit,
                includeDisplayOnly = true
            )
            val friendlyUnitPrice = UnitPrice.calculate(price, count, quantity).withFriendlyDenominator(
                quantity.unit,
                getCurrencyDecimalPlaces(dataSet),
                candidateDenominators
            )
            Log.d("MyAppQA", "rememberSaveable returning $friendlyUnitPrice")
            mutableStateOf(friendlyUnitPrice.denominator)
        }
        // If the user chooses a "bad" unit price denominator, we might end up with the unit price
        // being formatted to 0 with the available decimal places. I don't think this is really a
        // big deal and the alternatives (e.g. adding extra decimal places beyond the currency's
        // standard dps or rounding up instead of to nearest so the value isn't zero) are probably
        // worse.
        Log.d("MyAppQA", "calling formatUnitPrice $price $quantity $selectedUnitPriceUnit")
        val unitPriceString =
            UnitPrice.calculate(
                price,
                count,
                quantity,
                selectedUnitPriceUnit,
            ).format(context, dataSet,LocalConfiguration.current.locales[0])
        val context = LocalContext.current
        LabeledItemWithDropdown(
            modifier = Modifier.weight(0.45f), label = stringResource(R.string.label_unit_price),
            dropdownContentDescription = stringResource(R.string.content_description_select_unit),
            text = unitPriceString,
            enabled = asyncOperationStatus.isNotBusy(),
            items = relevantUnitList,
            getId = { it },
            getItemText = { "${it.perSymbol}${context.getString(it.symbol)}".trim() },
            getDividerBetween = { previousItem, item -> areDifferentUnitFamilies(previousItem, item) },
            selectedId = selectedUnitPriceUnit,
            onItemSelected = { selectedUnitPriceUnit = it })
    }
}

// TODO: If this really is just used in one place, inline it?
fun getCurrencyDecimalPlaces(dataSet: DataSet) =
    Currency.getInstance(dataSet.currencyCode).defaultFractionDigits
