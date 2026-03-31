package app.zornslemma.mypricelog.ui.components

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.domain.MeasurementUnit
import app.zornslemma.mypricelog.domain.UnitPrice
import app.zornslemma.mypricelog.domain.areDifferentUnitFamilies
import app.zornslemma.mypricelog.domain.getRelevantMeasurementUnits
import app.zornslemma.mypricelog.ui.common.format

private const val TAG = "UnitPriceField"

fun selectUnitPriceDenominator(
    autoUnitPriceDenominator: MeasurementUnit,
    userUnitPriceDenominator: MeasurementUnit?,
): MeasurementUnit {
    if (userUnitPriceDenominator != null) {
        // We prefer userUnitPriceDenominator unless it's of the wrong quantity type. This shouldn't
        // happen but as this is UI code we play it safe.
        if (userUnitPriceDenominator.quantityType == autoUnitPriceDenominator.quantityType) {
            return userUnitPriceDenominator
        } else {
            Log.w(
                TAG,
                "Unexpected: userUnitPriceDenominator $userUnitPriceDenominator doesn't match " +
                    "autoUnitPriceDenominator ${autoUnitPriceDenominator}'s quantity type",
            )
            return autoUnitPriceDenominator
        }
    } else {
        return autoUnitPriceDenominator
    }
}

@Composable
fun UnitPriceField(
    unitPrice: UnitPrice?,
    dataSet: DataSet,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.label_unit_price),
    onItemSelected: (MeasurementUnit) -> Unit,
) {
    if (unitPrice == null) {
        LabeledItem(modifier = modifier, label = label) { Text("-") }
    } else {
        val context = LocalContext.current

        val relevantUnitList =
            remember(dataSet, unitPrice.denominator.quantityType) {
                dataSet.getRelevantMeasurementUnits(
                    unitPrice.denominator.quantityType,
                    includeDisplayOnly = true,
                )
            }

        LabeledItemWithDropdown(
            modifier = modifier,
            label = label,
            dropdownContentDescription = stringResource(R.string.content_description_select_unit),
            text = unitPrice.format(context, dataSet, LocalConfiguration.current.locales[0]),
            enabled = enabled,
            items = relevantUnitList,
            getId = { it },
            getItemText = { "${it.perSymbol}${context.getString(it.symbol)}".trim() },
            getDividerBetween = { previousItem, item ->
                areDifferentUnitFamilies(previousItem, item)
            },
            selectedId = unitPrice.denominator,
            onItemSelected = onItemSelected,
        )
    }
}
