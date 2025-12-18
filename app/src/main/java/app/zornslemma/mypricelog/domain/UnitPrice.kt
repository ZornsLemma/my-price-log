package app.zornslemma.mypricelog.domain

import app.zornslemma.mypricelog.common.roundTo
import app.zornslemma.mypricelog.debug.myRequire
import java.util.Locale
import kotlin.math.abs

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

// This takes currencyDecimalPlaces not a CurrencyFormat because we only need the number of decimal
// places and our caller will not always have a locale to get a CurrencyFormat with.
// ENHANCE: While this function probably does a fairly good job in practice (albeit I haven't used
// it much in anger yet), it might be nice to record the user's last-chosen unit price unit for each
// (item, source) combination. This way, even if this function makes a poor choice, it will not
// matter much in the long run as the user will just change the selected unit once per (item,
// source) and that's that. We could do store this in a separate database table and write to it
// asynchronously on a best-effort basis (as opposed to the "save is initiated and we make the user
// wait, trapped, until it completes" saves for critical data). Based on discussions with ChatGPT
// this is the way to go, rather than trying to put it in any form of shared preferences, as even
// the more modern DataStore is not optimised for this. Although this would involve a database
// upgrade to add later, it is just adding a new table which would start off empty with no data
// migration, so it's probably not too scary.
fun UnitPrice.withFriendlyDenominator(
    preferredUnit: MeasurementUnit,
    currencyDecimalPlaces: Int,
    candidateDenominators: List<MeasurementUnit>,
): UnitPrice {
    myRequire(candidateDenominators.isNotEmpty()) { "Expected at least one candidate denominator" }
    var bestScore: Double = Double.MAX_VALUE
    var bestUnitPrice: UnitPrice? = null
    for (candidateDenominator in candidateDenominators) {
        val candidateUnitPrice = withDenominator(candidateDenominator)
        // We compute a score (lower is better) for candidateUnitPrice. This is based on an ad-hoc
        // weighted combination of factors:
        // - We like to minimise relative error caused by significant figures being rounded off at
        //   currencyDecimalPlaces.
        // - We like to use the same unit the price is expressed in if it's practical. This is more
        //   of an issue with non-metric, where e.g. milk might be sold in 4 pint containers and
        //   without this preference we might express the unit price per gallon, which is OK but
        //   not so clear in my opinion.
        // - We like to have an integer part which is a short as possible. (Remember the non-integer
        //   part isn't under our control; we will always have currencyDecimalPlaces of it.) But we
        //   don't like to have "0.xx" because then we're wasting the digit before the decimal
        //   separator which we always have to display.
        val relativeError =
            abs(
                candidateUnitPrice.numerator.roundTo(currencyDecimalPlaces) -
                    candidateUnitPrice.numerator
            ) / candidateUnitPrice.numerator
        val displayIntegerPart =
            String.format(Locale.US, "%.${currencyDecimalPlaces}f", candidateUnitPrice.numerator)
                .substringBefore('.')
        val displayIntegerLength = if (displayIntegerPart == "0") 0 else displayIntegerPart.length
        val candidateScore =
            abs(displayIntegerLength - 1) + (10.0 * relativeError) -
                (if (candidateUnitPrice.denominator == preferredUnit) 1.1 else 0.0)
        if (candidateScore < bestScore) {
            bestScore = candidateScore
            bestUnitPrice = candidateUnitPrice
        }
    }
    return bestUnitPrice!!
}
