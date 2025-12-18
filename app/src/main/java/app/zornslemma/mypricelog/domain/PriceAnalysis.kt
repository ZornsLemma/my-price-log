package app.zornslemma.mypricelog.domain

import android.icu.text.Collator
import app.zornslemma.mypricelog.data.Price
import app.zornslemma.mypricelog.data.Source
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.debug.myRequire
import java.time.Duration
import java.time.Instant
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.pow

data class PriceAnalysis(
    val augmentedPriceList: List<AugmentedPrice>,
    val priceClassificationThresholds: PriceClassificationThresholds?,
)

data class AugmentedPrice(
    val basePrice: Price,
    val sourceName: String, // technically redundant but saves much id->name lookup faff
    val loyaltyPrice: Double,
    val ageDays: Long,
    val ageClass: AgeClass,
    val inflatedLoyaltyPrice: Double,
    val unitPrice: UnitPrice,
    val priceJudgement: PriceJudgement,
) {
    companion object {
        fun fromPrice(
            price: Price,
            source: Source,
            priceAgeSettings: PriceAgeSettings,
        ): AugmentedPrice {
            val loyaltyPrice = price.price * source.loyaltyMultiplier
            // We use an integer ageDays as there's little value in working to sub-day resolution
            // and it will make the calculation a bit more repeatable/easy to follow for humans. If
            // we add a screen showing how the calculation was done, ageDays will not be constantly
            // increasing slightly every time it's shown.
            val ageDays = Duration.between(price.confirmedAt, Instant.now()).toDays()
            val inflatedLoyaltyPrice =
                inflationAdjustedPrice(loyaltyPrice, ageDays, priceAgeSettings)
            return AugmentedPrice(
                basePrice = price,
                sourceName = source.name,
                loyaltyPrice = loyaltyPrice,
                ageDays = ageDays,
                // We use the word "after" in the Settings descriptions of these thresholds, so we
                // use <= here.
                ageClass =
                    if (ageDays <= priceAgeSettings.stalePriceThresholdDays) {
                        AgeClass.FRESH
                    } else if (ageDays <= priceAgeSettings.ancientPriceThresholdDays) {
                        AgeClass.STALE
                    } else {
                        AgeClass.ANCIENT
                    },
                inflatedLoyaltyPrice = inflatedLoyaltyPrice,
                unitPrice = UnitPrice.calculate(inflatedLoyaltyPrice, price.count, price.quantity),
                priceJudgement = PriceJudgement.NONE,
            )
        }
    }
}

private fun inflationAdjustedPrice(
    price: Double,
    ageDays: Long,
    priceAgeSettings: PriceAgeSettings,
): Double {
    // We use the word "after" in the Settings descriptions of these thresholds, so we use <= here.
    return if (ageDays <= priceAgeSettings.stalePriceThresholdDays) {
        price
    } else {
        // Note that inflation starts to apply only from stalePriceThresholdDays; the exponent here
        // is ageDays - stalePriceThresholdDays. We don't want to suddenly apply the previous
        // stalePriceThresholdDays' worth of inflation the instant a price becomes stale.
        price *
            (1.0 + priceAgeSettings.annualInflationPercent / 100.0).pow(
                (ageDays - priceAgeSettings.stalePriceThresholdDays) / 365.25
            )
    }
}

enum class AgeClass {
    FRESH,
    STALE,
    ANCIENT,
}

enum class PriceJudgement {
    NONE,
    GOOD,
    OK,
    BAD,
}

private fun AugmentedPrice.judge(
    priceClassificationThresholds: PriceClassificationThresholds?
): PriceJudgement {
    return if (priceClassificationThresholds == null) {
        PriceJudgement.NONE
    } else if (unitPrice < priceClassificationThresholds.good) {
        PriceJudgement.GOOD
    } else if (unitPrice <= priceClassificationThresholds.bad) {
        PriceJudgement.OK
    } else {
        PriceJudgement.BAD
    }
}

data class PriceClassificationThresholds(val good: UnitPrice, val bad: UnitPrice)

private fun quantile(sortedValues: List<Double>, q: Double): Double {
    myRequire(q in 0.0..1.0) { "Expected q in [0, 1] but got $q" }

    // We could return null for empty, but in reality we don't expect this to happen and it feels
    // better to avoid making the result nullable.
    myRequire(sortedValues.isNotEmpty()) { "Expected non-empty list" }

    // It's slightly inefficient to be checking sortedValues is sorted every time, but for our tiny
    // lists it is very cheap and it might catch a bug causing invalid results to be generated.
    myRequire(sortedValues.zipWithNext().all { (a, b) -> a <= b }) {
        "Expected sortedValues to be sorted but got $sortedValues"
    }

    val doubleIndex = q * (sortedValues.size - 1)
    val lowerIndex = doubleIndex.toInt()
    // min() here is just paranoia in case of floating point imprecision.
    val upperIndex = kotlin.math.min(ceil(doubleIndex).toInt(), sortedValues.size - 1)
    val fractionalIndex = doubleIndex - lowerIndex
    return sortedValues[lowerIndex] * (1 - fractionalIndex) +
        sortedValues[upperIndex] * fractionalIndex
}

fun analysePrices(
    priceList: List<Price>,
    sourceList: List<Source>,
    priceAgeSettings: PriceAgeSettings,
    locale: Locale,
): PriceAnalysis {
    if (priceList.isEmpty()) {
        return PriceAnalysis(emptyList(), null)
    }

    // It's important for our calls to quantile() below that augmentedPriceList is sorted on unit
    // price. We use sourceName as a tie breaker just to improve visual consistency of the results
    // when shown to the user.
    val sourceById = sourceList.associateBy { it.id }
    val collator = Collator.getInstance(locale).apply { strength = Collator.PRIMARY }
    var augmentedPriceList =
        priceList
            .mapNotNull { price ->
                // I don't think we can have a Price but not the corresponding Source, but we play
                // it safe just in case.
                sourceById[price.sourceId]?.let { source ->
                    AugmentedPrice.fromPrice(price, source, priceAgeSettings)
                }
            }
            .sortedWith(
                compareBy<AugmentedPrice> { it.unitPrice }
                    .thenComparing({ it.sourceName }, collator)
            )

    // fromPrice() should have generated all unit prices using the base unit, but let's check
    // as otherwise recentEnoughPriceList (which discards the denominators) will be meaningless.
    val unitPriceDenominator = augmentedPriceList.first().unitPrice.denominator
    myCheck(augmentedPriceList.all { it.unitPrice.denominator == unitPriceDenominator }) {
        "Not all augmentedPriceList values have identical unitPrice denominators"
    }

    val recentEnoughPriceList =
        augmentedPriceList.filter { it.ageClass != AgeClass.ANCIENT }.map { it.unitPrice.numerator }

    val priceClassificationThresholds =
        if (recentEnoughPriceList.size <= 2) {
            null
        } else {
            // This isn't necessarily the ideal way to classify things but it's what I settled on
            // after much discussion with ChatGPT and thinking about it. We have so little data that
            // we can't go full on stats nerd. We calculate a buffered IQR [Q1*(1-k), Q3+(1+k)] and
            // use that to classify prices as good, OK or bad. The idea is not to obsess over small
            // price variations when making our recommendation. Note that we only do this if we have
            // at least three recent enough prices to work with. There are numerous flaws with this,
            // but we're just trying to give an at-a-glance recommendation which is reasonably
            // trustworthy. Users can obviously see the actual list of unit prices by store and
            // judge from that if they prefer.
            val lowerQuartile = quantile(recentEnoughPriceList, 0.25)
            val upperQuartile = quantile(recentEnoughPriceList, 0.75)
            val k = 0.1 // ENHANCE: Make this configurable in settings? May be too "advanced"...
            PriceClassificationThresholds(
                good = UnitPrice(lowerQuartile * (1 - k), unitPriceDenominator),
                bad = UnitPrice(upperQuartile * (1 + k), unitPriceDenominator),
            )
        }

    augmentedPriceList =
        augmentedPriceList.map { augmentedPrice ->
            // We classify prices even if they aren't fresh. This seems best, they are marked as
            // stale so the user can tell, but it's not unreasonable to offer a judgement.
            // ENHANCE: Possibly we should not offer a judgement on ancient prices?
            augmentedPrice.copy(
                priceJudgement = augmentedPrice.judge(priceClassificationThresholds)
            )
        }
    return PriceAnalysis(augmentedPriceList, priceClassificationThresholds)
}
