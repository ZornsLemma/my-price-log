package com.example.composetutorial.domain

import android.util.Log
import com.example.composetutorial.models.DataSet
import com.example.composetutorial.splitAroundDigits
import com.example.composetutorial.ui.common.ValidationRule
import com.example.composetutorial.ui.components.numericValidationRules
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

data class CurrencyFormat(
    val decimalPlaces: Int,
    val prefix: String?,
    val suffix: String?,
    val validationRules: List<ValidationRule<String>>
)

// This is an extension function on DataSet rather than a top-level function taking a currency code
// because at some point a DataSet may contain custom currency formatting which overrides whatever
// the current locale says to do.
fun DataSet.createCurrencyFormat(locale: Locale): CurrencyFormat {
    val currencyInstance = Currency.getInstance(currencyCode)
    // currencyInstance will give us the number of decimal places, but it won't give us a
    // prefix or suffix to use - which we need for currency TextFields. So we ask it to
    // format a sample price and take the prefix and suffix from that.
    val numberFormat = NumberFormat.getCurrencyInstance(locale).apply {
        currency = currencyInstance
    }
    val sampleFormattedCurrency = numberFormat.format(1.0)
    Log.d(
        "MyApp",
        "sampleFormattedCurrency for $currencyCode is '$sampleFormattedCurrency'"
    )
    val (prefix, suffix) = splitAroundDigits(sampleFormattedCurrency)
    return CurrencyFormat(
        decimalPlaces = currencyInstance.defaultFractionDigits,
        prefix = prefix.trim().ifBlank { null },
        suffix = suffix.trim().ifBlank { null },
        validationRules = numericValidationRules(
            locale,
            allowDecimals = currencyInstance.defaultFractionDigits > 0,
            allowZero = false,
            maxDecimals = currencyInstance.defaultFractionDigits
        )
    )
}