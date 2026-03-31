package app.zornslemma.mypricelog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.domain.MeasurementUnit
import app.zornslemma.mypricelog.domain.Quantity
import app.zornslemma.mypricelog.domain.UnitPrice
import app.zornslemma.mypricelog.ui.common.AsyncOperationStatus
import app.zornslemma.mypricelog.ui.common.formatPrice
import app.zornslemma.mypricelog.ui.common.isNotBusy
import app.zornslemma.mypricelog.ui.storePriceGridGutterWidth
import app.zornslemma.mypricelog.ui.storePriceGridLeftColumnWeight
import app.zornslemma.mypricelog.ui.storePriceGridRightColumnWeight

@Composable
fun PackPriceAndSizeRow(
    price: Double,
    count: Long,
    quantity: Quantity,
    unitPriceDenominator: MeasurementUnit,
    onUnitPriceDenominatorChange: (MeasurementUnit) -> Unit,
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

        UnitPriceField(
            unitPrice =
                UnitPrice.calculate(price, count, quantity).withDenominator(unitPriceDenominator),
            dataSet = dataSet,
            enabled = asyncOperationStatus.isNotBusy(),
            modifier = Modifier.weight(storePriceGridRightColumnWeight),
            onItemSelected = onUnitPriceDenominatorChange,
        )
    }
}
