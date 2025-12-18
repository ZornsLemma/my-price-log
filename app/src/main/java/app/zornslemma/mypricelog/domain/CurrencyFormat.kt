package app.zornslemma.mypricelog.domain

import android.util.Log
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.ui.common.ValidationRule
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
//
// NB: Currency formatting via numberFormat.format() and the value of defaultFractionDigits for a
// currency can be inconsistent, as they are defined independently with the first being a
// locale-based property and the second a property of the currency. As a concrete example, MGA has 0
// defaultFractionDigits but in my test US/Europe locales it generally formats with two decimal
// places for display. This creates some small inconsistencies in the UI - in particular, if you
// start with USD so you can enter decimal places then switch the data set to MGA, you can see a
// value of 0.67 on the home screen which turns into 1 when you're editing. I think these corner
// case behaviours are the least worst possible and in general we have to trust the localisation
// framework to do the right thing.
fun DataSet.createCurrencyFormat(locale: Locale): CurrencyFormat {
    val currencyInstance = Currency.getInstance(currencyCode)
    // currencyInstance will give us the number of decimal places, but it won't give us a
    // prefix or suffix to use - which we need for currency TextFields. So we ask it to
    // format a sample price and take the prefix and suffix from that.
    val numberFormat =
        NumberFormat.getCurrencyInstance(locale).apply { currency = currencyInstance }
    val sampleFormattedCurrency = numberFormat.format(1.0)
    Log.d(
        TAG,
        "sampleFormattedCurrency for $currencyCode is '$sampleFormattedCurrency', defaultFractionDigits is ${currencyInstance.defaultFractionDigits}",
    )
    val (prefix, suffix) = splitAroundDigits(sampleFormattedCurrency)
    return CurrencyFormat(
        decimalPlaces = currencyInstance.defaultFractionDigits,
        prefix = prefix.trim().ifBlank { null },
        suffix = suffix.trim().ifBlank { null },
        validationRules =
            numericValidationRules(
                locale,
                allowDecimals = currencyInstance.defaultFractionDigits > 0,
                allowZero = false,
                maxDecimals = currencyInstance.defaultFractionDigits,
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
