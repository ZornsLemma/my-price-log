package app.zornslemma.mypricelog.domain

import android.util.Log
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.ui.common.ValidationRule
import app.zornslemma.mypricelog.ui.common.formatPrice
import app.zornslemma.mypricelog.ui.components.numericValidationRules
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

private const val TAG = "CurrencyFormat"

data class CurrencyFormat(
    val decimalPlaces: Int,
    val prefix: String?,
    val suffix: String?,
    val validationRules: List<ValidationRule<String>>,
)

// This is an extension function on DataSet rather than a top-level function taking a currency code
// because at some point a DataSet may contain custom currency formatting which overrides whatever
// the current locale says to do.
fun DataSet.createCurrencyFormat(locale: Locale): CurrencyFormat {
    val currencyInstance = Currency.getInstance(currencyCode)
    // currencyInstance will give us the number of decimal places, but it won't give us a
    // prefix or suffix to use - which we need for currency TextFields. So we ask it to
    // format a sample price and take the prefix and suffix from that.
    val sampleFormattedCurrency = formatPrice(1.0, this, locale)
    Log.d(
        TAG,
        "sampleFormattedCurrency for $currencyCode is '$sampleFormattedCurrency', effectiveFractionDigits is ${currencyInstance.effectiveFractionDigits}",
    )
    val (prefix, suffix) = splitAroundDigits(sampleFormattedCurrency)
    return CurrencyFormat(
        decimalPlaces = currencyInstance.effectiveFractionDigits,
        prefix = prefix.trim().ifBlank { null },
        suffix = suffix.trim().ifBlank { null },
        validationRules =
            numericValidationRules(
                locale,
                allowDecimals = currencyInstance.effectiveFractionDigits > 0,
                allowZero = false,
                maxDecimals = currencyInstance.effectiveFractionDigits,
                required = true,
            ),
    )
}

// Return the non-digit prefix and suffix around a digit-containing string. Given "foo123bar4 baz56
// quux", this returns ("foo", " quux").
private fun splitAroundDigits(input: String): Pair<String, String> {
    var firstDigitIndex = input.indexOfFirst { it.isDigit() }
    if (firstDigitIndex == -1) {
        firstDigitIndex = 0
    }
    val prefix = input.take(firstDigitIndex)

    val lastDigitIndex = input.indexOfLast { it.isDigit() }
    val suffix =
        if (lastDigitIndex == -1) {
            ""
        } else {
            input.substring(lastDigitIndex + 1)
        }

    return Pair(prefix, suffix)
}

// For some currencies defaultFractionDigits doesn't match what users likely expect in the context
// of shopping.
val Currency.effectiveFractionDigits: Int
    get() = when (currencyCode) {
        "COP" -> 0
        "IDR" -> 0
        else -> defaultFractionDigits}
