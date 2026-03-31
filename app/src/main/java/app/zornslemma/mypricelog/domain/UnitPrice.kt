package app.zornslemma.mypricelog.domain

import android.util.Log
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.debug.myRequire
import java.util.Currency
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max

private const val TAG = "UnitPrice"

data class UnitPrice(val numerator: Double, val denominator: MeasurementUnit) :
    Comparable<UnitPrice> {
    override fun compareTo(other: UnitPrice): Int {
        myRequire(denominator.quantityType == other.denominator.quantityType) {
            "UnitPrices with denominators $denominator and ${other.denominator} are incommensurable"
        }
        // We could convert this to other's denominator in order to compare the two. Although it may
        // be a bit superstitious of me, it feels safer ("rounding"/"consistency") to compare in the
        // base unit. The extra work is negligible in practice. Similarly, we avoid the probably
        // premature optimisation of checking to see if the two denominators are the same and just
        // comparing the numerators directly if they are.
        val baseUnit = denominator.quantityType.baseUnit()
        val thisWithBaseUnit = withDenominator(baseUnit)
        val otherWithBaseUnit = other.withDenominator(baseUnit)
        return thisWithBaseUnit.numerator.compareTo(otherWithBaseUnit.numerator)
    }

    fun withDenominator(newDenominator: MeasurementUnit): UnitPrice {
        myRequire(denominator.quantityType == newDenominator.quantityType) {
            "UnitPrice with denominator $denominator can't be converted to denominator $newDenominator"
        }
        return UnitPrice(numerator * newDenominator.toBase / denominator.toBase, newDenominator)
    }

    // operator fun times(other: Double): UnitPrice = UnitPrice(numerator * other, denominator)

    companion object {
        fun calculate(
            price: Double,
            count: Long,
            quantity: Quantity,
            denominator: MeasurementUnit = quantity.unit.quantityType.baseUnit(),
        ): UnitPrice {
            myRequire(count > 0) { "Expected positive count" }
            return UnitPrice(price / (count * quantity.asValue(denominator)), denominator)
        }
    }
}

fun calculateFriendlyUnitPriceDenominator(
    dataSet: DataSet,
    preferredUnit: MeasurementUnit?,
    unitPriceList: List<UnitPrice>,
): MeasurementUnit? {
    if (unitPriceList.isEmpty()) {
        return null
    }

    if (preferredUnit == null) {
        // ENHANCE: We really don't expect this to happen, but we do cope with it below in case of
        // unforeseen corner cases.
        Log.w(
            TAG,
            "Unexpected: preferredUnit should not be null in calculateFriendlyUnitPriceDenominator() with a non-empty price list",
        )
    }
    val candidateDenominators =
        dataSet.getMeasurementUnitsOfSameQuantityTypeAndUnitFamily(
            preferredUnit ?: unitPriceList.first().denominator,
            includeDisplayOnly = true,
        )
    val currencyDecimalPlaces = Currency.getInstance(dataSet.currencyCode).defaultFractionDigits

    // ENHANCE: I wonder if we should derive min/max values from unitPriceList (and perhaps add a
    // 10% buffer around those to give some stability if prices change a small amount?) and use
    // those to decide our denominator instead of assessing each individual price. Not sure if this
    // would be better, or how to do it (although we could perhaps use the same algorithm but with
    // the min and max as the two values considered).

    var bestScore: Double = Double.MAX_VALUE
    var bestDenominator: MeasurementUnit? = null
    for (candidateDenominator in candidateDenominators) {
        val candidateScore =
            unitPriceList.maxOf {
                scoreUnitPrice(it.withDenominator(candidateDenominator), currencyDecimalPlaces)
            }
        if (candidateScore < bestScore) {
            bestScore = candidateScore
            bestDenominator = candidateDenominator
        }
    }
    return bestDenominator!!
}

// We compute a score (lower is better) for candidateUnitPrice. This is somewhat ad-hoc but the
// basic idea is that we want to show at least targetSignificantFigures, allowing for the fact that
// the resulting currency amount will be shown with currencyDecimalPlaces, but we don't want to
// include excessive precision which is likely to be more confusing than helpful and (for currencies
// like USD, GBP or EUR at least) is likely to create unnecessary extra digits in front of the
// decimal point. This is definitely not perfect for currencies with very low per-unit values and
// particularly those which still have near-redundant decimal places, but ENHANCE: it feels better
// to react to user feedback than try to pre-emptively make this work perfectly for every currency.
// ENHANCE: We could allow targetSignificantFigures to be user-defined in each data set, and/or we
// could hard-code overrides of it for some currencies. I haven't checked it, but ChatGPT suggests
// COP and IDR are the two currencies that might benefit from hard-coded overrides to be 0 d.p.
private fun scoreUnitPrice(candidateUnitPrice: UnitPrice, currencyDecimalPlaces: Int): Double {
    // We could refuse to operate on zero numerator unit prices but it feels a bit petty and
    // there are corner cases (calculating on-the-fly unit prices when the user is editing a
    // price) where this can occur unless we take steps to avoid it.
    val numerator = max(candidateUnitPrice.numerator, 0.0001)

    // This table shows roughly how this code works. The number in square brackets is
    // currencyDecimalPlaces; remember numerator would always be shown with this number of decimal
    // places. It isn't perfect - 0.0099 would round to 0.01 with 2 dp, so maybe there's an argument
    // this is 1 sf - but I think it captures the basic idea fairly well. We could round numerator
    // before computing digitsBeforeDecimalSeparator to try to capture this, but I want to avoid
    // halfway points triggering changes in scoring.
    //
    // numerator  digitsBeforeDecimalSeparator  effectiveSF[2]  effectiveSF[3]
    //   10                                  2               4              5
    //    9.99                               1               3              4
    //    1                                  1               3              4
    //    0.1                                0               2              3
    //    0.099                             -1               1              2
    //    0.01                              -1               1              2
    //    0.0099                            -2               0              1
    val digitsBeforeDecimalSeparator = floor(log10(numerator)) + 1 // can be negative
    val effectiveSignificantFigures = digitsBeforeDecimalSeparator + currencyDecimalPlaces
    val targetSignificantFigures = 3
    val delta = effectiveSignificantFigures - targetSignificantFigures
    return if (delta > 0) delta * 0.5 else -delta
}
