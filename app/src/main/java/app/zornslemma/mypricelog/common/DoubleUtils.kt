package app.zornslemma.mypricelog.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow

// The arguments are mandatory on formatDouble() and formatPercent() so we're forced to think about
// what's correct when we call this. For miscellaneous debug output we can just use string
// interpolation of course.

fun formatDouble(
    value: Double,
    minDecimals: Int,
    maxDecimals: Int,
    useLocaleGrouping: Boolean,
    locale: Locale,
    useLeadingPlus: Boolean = false,
): String {
    val numberFormat = NumberFormat.getNumberInstance(locale)
    return commonFormat(
        numberFormat,
        value,
        minDecimals,
        maxDecimals,
        useLocaleGrouping,
        locale,
        useLeadingPlus,
    )
}

fun formatPercent(
    value: Double,
    minDecimals: Int,
    maxDecimals: Int,
    useLocaleGrouping: Boolean,
    locale: Locale,
    useLeadingPlus: Boolean = false,
): String {
    val numberFormat = NumberFormat.getPercentInstance(locale)
    return commonFormat(
        numberFormat,
        value,
        minDecimals,
        maxDecimals,
        useLocaleGrouping,
        locale,
        useLeadingPlus,
    )
}

private fun commonFormat(
    numberFormat: NumberFormat,
    value: Double,
    minDecimals: Int,
    maxDecimals: Int,
    useLocaleGrouping: Boolean,
    locale: Locale,
    useLeadingPlus: Boolean = false,
): String {
    numberFormat.minimumFractionDigits = minDecimals
    numberFormat.maximumFractionDigits = maxDecimals
    if (!useLocaleGrouping) {
        numberFormat.isGroupingUsed = false
    }
    val decimalFormat = numberFormat as DecimalFormat
    if (useLeadingPlus) {
        decimalFormat.positivePrefix = "+"
    }
    return decimalFormat.format(value)
}

// Format a double to be edited by the user as a string in a TextField. Grouping is *not* used -
// since this is for editing via a text field and the grouping characters (if any) won't
// automagically stay in place as the user edits, we don't want any. As far as I can tell, general
// consensus is that "clever" edit fields which automatically insert or maintain grouping separators
// are frowned on these days, this isn't just laziness on my part. (It's not part of this function,
// but we do allow the user to add their own grouping separators if they want; we just ignore them
// when parsing the string later.)
fun formatDoubleForEditing(value: Double, minDecimals: Int, maxDecimals: Int, locale: Locale) =
    formatDouble(
        value,
        minDecimals = minDecimals,
        maxDecimals = maxDecimals,
        useLocaleGrouping = false,
        locale = locale,
    )

fun Double.roundTo(decimalPlaces: Int): Double {
    val factor = 10.0.pow(decimalPlaces)
    return kotlin.math.round(this * factor) / factor
}

fun parseStringAsDoubleOrNull(locale: Locale, string: String): Double? {
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    // If input filtering allowed "-" characters through they are significant, so we don't strip
    // them out here. This is harmless if they were never allowed through, of course.
    val insignificantCharsRegex = "[^-0-9${Regex.escape(decimalSeparator.toString())}]".toRegex()
    return string
        .replace(insignificantCharsRegex, "")
        .replace(decimalSeparator, '.')
        .toDoubleOrNull() // not locale aware, decimal separator is always "."
}
