package app.zornslemma.mypricelog.domain

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import app.zornslemma.mypricelog.R
import app.zornslemma.mypricelog.common.formatDouble
import app.zornslemma.mypricelog.common.intersectionIsEmpty
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.debug.myCheck
import app.zornslemma.mypricelog.debug.myRequire
import app.zornslemma.mypricelog.ui.nonBreakingSpace
import java.util.Locale
import kotlinx.parcelize.Parcelize

// Enum class to represent whether something is sold by "count of items" ($4 for 6 bananas),
// weight or volume. This is fundamental as we make no effort to convert between them using some
// sort of density estimate or whatever. Actual units (kg, oz, etc) of the same quantity type can
// be varied much more freely.
enum class QuantityType(val id: Int, @field:StringRes val nameResource: Int) {
    ITEM(1, R.string.label_sold_by_item),
    WEIGHT(
        2,
        R.string.label_sold_by_weight,
    ), // technically mass but everyone says "price per weight"
    VOLUME(3, R.string.label_sold_by_volume);

    companion object {
        // This is unused but it supports Converters.toQuantityType(), which we want to keep around.
        fun fromId(id: Int): QuantityType? {
            return entries.find { it.id == id }
        }
    }
}

fun QuantityType.baseUnit() =
    when (this) {
        QuantityType.WEIGHT -> MeasurementUnit.G
        QuantityType.VOLUME -> MeasurementUnit.ML
        QuantityType.ITEM -> MeasurementUnit.EACH
    }

enum class UnitFamily {
    ITEM,
    METRIC,
    IMPERIAL, // as used in UK
    US_CUSTOMARY, // as used in US
}

// Note that MeasurementUnit symbols and full names do not attempt to avoid ambiguity with units
// like pint and fluid ounce that differ between imperial and US customary. Using qualified names
// like "imperial pint" would look awkward in the UI, especially on smaller screens. We disambiguate
// by not allowing both imperial and US customary to be active for the same data set, making the
// shorter common names unambiguous in that context.

// It's tempting to call this just "Unit", but that's a keyword in Kotlin.
enum class MeasurementUnit(
    val id: Long,
    val unitFamilies: Set<UnitFamily>,
    val quantityType: QuantityType,
    @field:StringRes val symbol: Int,
    @field:StringRes val fullName: Int,
    val maxDecimals: Int,
    val toBase: Double,
    val displayOnly: Boolean,
    val perSymbol: String = "/",
) {
    // Countable items
    EACH(
        101,
        setOf(UnitFamily.ITEM),
        QuantityType.ITEM,
        R.string.unit_each_symbol,
        R.string.unit_each,
        0,
        1.0,
        false,
        perSymbol = " ",
    ),
    EACH10(
        102,
        setOf(UnitFamily.ITEM),
        QuantityType.ITEM,
        R.string.unit_10,
        R.string.unit_10,
        1,
        10.0,
        true,
    ),
    EACH100(
        103,
        setOf(UnitFamily.ITEM),
        QuantityType.ITEM,
        R.string.unit_100,
        R.string.unit_100,
        2,
        100.0,
        true,
    ),

    // Weight (metric)
    G(
        201,
        setOf(UnitFamily.METRIC),
        QuantityType.WEIGHT,
        R.string.unit_gram_symbol,
        R.string.unit_gram,
        0,
        1.0,
        false,
    ),
    G100(
        202,
        setOf(UnitFamily.METRIC),
        QuantityType.WEIGHT,
        R.string.unit_100_gram_symbol,
        R.string.unit_100_grams,
        2,
        100.0,
        true,
    ),
    KG(
        203,
        setOf(UnitFamily.METRIC),
        QuantityType.WEIGHT,
        R.string.unit_kilogram_symbol,
        R.string.unit_kilogram,
        3,
        1000.0,
        false,
    ),

    // Weight (imperial/US customary)
    OZ(
        211,
        setOf(UnitFamily.IMPERIAL, UnitFamily.US_CUSTOMARY),
        QuantityType.WEIGHT,
        R.string.unit_ounce_symbol,
        R.string.unit_ounce,
        3, // allow for eighths
        28.349523125,
        false,
    ),
    LB(
        212,
        setOf(UnitFamily.IMPERIAL, UnitFamily.US_CUSTOMARY),
        QuantityType.WEIGHT,
        R.string.unit_pound_symbol,
        R.string.unit_pound,
        3, // allow for eighths
        453.59237,
        false,
    ),

    // Volume (metric)
    ML(
        301,
        setOf(UnitFamily.METRIC),
        QuantityType.VOLUME,
        R.string.unit_millilitre_symbol,
        R.string.unit_millilitre,
        0,
        1.0,
        false,
    ),
    ML100(
        302,
        setOf(UnitFamily.METRIC),
        QuantityType.VOLUME,
        R.string.unit_100_millilitre_symbol,
        R.string.unit_100_millilitres,
        2,
        100.0,
        true,
    ),
    L(
        303,
        setOf(UnitFamily.METRIC),
        QuantityType.VOLUME,
        R.string.unit_litre_symbol,
        R.string.unit_litre,
        3,
        1000.0,
        false,
    ),

    // Volume (imperial)
    IMPERIAL_FLOZ(
        311,
        setOf(UnitFamily.IMPERIAL),
        QuantityType.VOLUME,
        R.string.unit_fluid_ounce_symbol,
        R.string.unit_fluid_ounce,
        3, // allow for eighths
        28.4130625,
        false,
    ),
    IMPERIAL_PINT(
        312,
        setOf(UnitFamily.IMPERIAL),
        QuantityType.VOLUME,
        R.string.unit_pint_symbol,
        R.string.unit_pint,
        3, // allow for eighths
        568.26125,
        false,
    ),
    IMPERIAL_GAL(
        313,
        setOf(UnitFamily.IMPERIAL),
        QuantityType.VOLUME,
        R.string.unit_gallon_symbol,
        R.string.unit_gallon,
        3, // allow for eighths
        4546.09,
        false,
    ),

    // Volume (US customary)
    US_CUSTOMARY_FLOZ(
        321,
        setOf(UnitFamily.US_CUSTOMARY),
        QuantityType.VOLUME,
        R.string.unit_fluid_ounce_symbol,
        R.string.unit_fluid_ounce,
        3, // allow for eighths
        29.5735295625,
        false,
    ),
    US_CUSTOMARY_PINT(
        322,
        setOf(UnitFamily.US_CUSTOMARY),
        QuantityType.VOLUME,
        R.string.unit_pint_symbol,
        R.string.unit_pint,
        3, // allow for eighths
        473.176473,
        false,
    ),
    US_CUSTOMARY_GAL(
        323,
        setOf(UnitFamily.US_CUSTOMARY),
        QuantityType.VOLUME,
        R.string.unit_gallon_symbol,
        R.string.unit_gallon,
        3, // allow for eighths
        3785.411784,
        false,
    );

    companion object {
        private val measurementUnitById = entries.associateBy { it.id }

        fun fromId(measurementUnitId: Long): MeasurementUnit? =
            measurementUnitById[measurementUnitId]
    }
}

fun areDifferentUnitFamilies(lhs: MeasurementUnit, rhs: MeasurementUnit) =
    intersectionIsEmpty(lhs.unitFamilies, rhs.unitFamilies)

@Parcelize
data class Quantity(val value: Double, val unit: MeasurementUnit) : Parcelable {
    private val quantityType: QuantityType
        get() = unit.quantityType

    fun to(unit: MeasurementUnit): Quantity {
        myRequire(this.quantityType == unit.quantityType) {
            "Cannot convert between different quantity types: trying to convert $this to $unit"
        }
        val baseValue = this.value * this.unit.toBase
        return Quantity(baseValue / unit.toBase, unit)
    }

    operator fun plus(other: Quantity): Quantity {
        myRequire(this.quantityType == other.quantityType) {
            "Cannot add values of different quantity types (this: $this, other: $other)"
        }
        val otherInThis = other.to(this.unit)
        return Quantity(this.value + otherInThis.value, this.unit)
    }

    fun asValue(unit: MeasurementUnit): Double = this.to(unit).value

    // Based on my own experience and a possibly-trustworthy discussion with ChatGPT for an
    // international angle, I suspect that in practice we don't want grouping separators in our
    // measures even when they're for display only - "2272 ml" feels better than "2,272 ml", at
    // least to me.
    // ENHANCE: This may need tweaking for localisation (especially with ITEM) but we'll see how
    // things work out in practice.
    fun toDisplayString(context: Context, locale: Locale): String =
        formatDouble(
            value,
            minDecimals = 0,
            maxDecimals = unit.maxDecimals,
            useLocaleGrouping = false,
            locale,
        ) +
            if (quantityType == QuantityType.ITEM) ""
            else "$nonBreakingSpace${context.getString(unit.symbol)}"
}

fun DataSet.getRelevantUnitFamilies(): Set<UnitFamily> {
    val relevantUnitFamilies =
        setOfNotNull(
            if (allowMetric) UnitFamily.METRIC else null,
            if (allowImperial) UnitFamily.IMPERIAL else null,
            if (allowUSCustomary) UnitFamily.US_CUSTOMARY else null,
            UnitFamily.ITEM,
        )
    myCheck(relevantUnitFamilies.isNotEmpty()) { "Data set ID $id has no unit families enabled" }
    myCheck(!(allowImperial && allowUSCustomary)) {
        "Data set ID $id has both imperial and US customary unit families enabled"
    }
    return relevantUnitFamilies
}

// Returns a sensibly-ordered list (which will probably be shown to the user in this order) of all
// the measurement units for dataSet and quantityType.
// ENHANCE: Where multiple unit families are enabled in the data set, this will currently always
// follow the order in MeasurementUnit. So metric will always come before imperial/US customary if
// they are enabled. It might be desirable to have some global or per-data set configuration which
// says something like "I prefer family X to come first where it's included" or (in practice mostly
// equivalent) "I prefer {metric/non-metric} to come first when both are available". The precise
// wording and level of control would have to be decided, though in practice we are unlikely to add
// new unit families so there isn't too much need for amazing amounts of flexibility - the real
// choice is "metric first or non-metric first"?.
fun DataSet.getRelevantMeasurementUnits(
    quantityType: QuantityType,
    includeDisplayOnly: Boolean,
): List<MeasurementUnit> {
    val relevantUnitFamilies = getRelevantUnitFamilies()
    val relevantMeasurementUnits =
        MeasurementUnit.entries.filter { measurementUnit ->
            measurementUnit.quantityType == quantityType &&
                measurementUnit.unitFamilies.any { it in relevantUnitFamilies } &&
                (!measurementUnit.displayOnly || includeDisplayOnly)
        }
    myCheck(relevantMeasurementUnits.isNotEmpty()) {
        "Expected at least one relevant measure unit for QuantityType ${quantityType.name} in " +
            "the context of data set ID $id but found none"
    }
    return relevantMeasurementUnits
}

// Return a list of the MeasurementUnits of the same QuantityType and UnitFamily as measurementUnit.
// Note that measurementUnit itself will be included in the results. The results are in the same
// order as in MeasurementUnit.entries, but in practice I don't believe this matters.
fun DataSet.getMeasurementUnitsOfSameQuantityTypeAndUnitFamily(
    measurementUnit: MeasurementUnit,
    includeDisplayOnly: Boolean,
): List<MeasurementUnit> {
    // dataSet is used here to decide which of the possible multiple families measurementUnit
    // belongs to is the one we're interested in. Suppose we have OZ - it could be imperial or US
    // customary. As it happens, in all cases where a MeasurementUnit belongs to two families, the
    // families as we currently define them are identical anyway so the distinction doesn't matter.
    // But we do the right thing anyway just to be cautious. (It's not likely but to see why this
    // matters, suppose we add support for the cubic inch as a volume measurement. It's the same in
    // imperial and US customary. But if measurementUnit were CUBIC_INCH, the family would matter in
    // deciding whether the returned MeasurementUnits are US or imperial floz/pint/gallon.)
    val unitFamilies = measurementUnit.unitFamilies.intersect(getRelevantUnitFamilies())
    myCheck(unitFamilies.size == 1) {
        "measurementUnit ${measurementUnit.id} should belong to one unit family for data set " +
            "$id, not $unitFamilies"
    }
    val result =
        MeasurementUnit.entries.filter {
            it.quantityType == measurementUnit.quantityType &&
                unitFamilies.single() in it.unitFamilies &&
                (!it.displayOnly || includeDisplayOnly)
        }
    myCheck(result.isNotEmpty()) {
        "measurementUnit ${measurementUnit.id} belongs to no unit families for data set $id"
    }
    // This is a linear search but it's a tiny list and we don't call this a lot.
    myCheck(measurementUnit in result) {
        "Original measurementUnit ${measurementUnit.id} not present in result: ${result.map { it.id }}"
    }
    return result
}
