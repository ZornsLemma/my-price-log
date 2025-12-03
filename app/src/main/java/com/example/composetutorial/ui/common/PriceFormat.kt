package com.example.composetutorial.ui.common

import android.content.Context
import com.example.composetutorial.domain.UnitPrice
import com.example.composetutorial.data.DataSet
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatPrice(price: Double, dataSet: DataSet, locale: Locale): String {
    // At least on Android this doesn't throw for invalid three-letter currency codes but it will
    // throw if given currency code "AAAA", so it seems safest to catch exceptions and have a
    // fallback, even if it's not great.
    try {
        val numberFormat = NumberFormat.getCurrencyInstance(locale).apply {
            currency = Currency.getInstance(dataSet.currencyCode)
        }
        // Note that the returned string appears to use a non-breaking space as a separator.
        return numberFormat.format(price)
    } catch (e: Exception) {
        // Generate a generic-ish "USD 1234" value as a fallback, without trying to use any
        // localisation settings.
        // ENHANCE: Eventually we might want to see if there's any useful data in a currency
        // prefix/suffix/decimal places set of fields in dataSet, but we don't have those yet. But
        // even if we did, we'd probably already be using those in preference to
        // getCurrencyInstance(), so they wouldn't help us at this point.
        val numberFormat = NumberFormat.getNumberInstance()
        // TODO: The "x" instead of a space in the next line is temporary, just to make it more
        // obvious if this code is coming into play while I am developing/testing. It probably
        // ought to be a non-breaking space, albeit this code path should never really be used.
        return "${dataSet.currencyCode}x${numberFormat.format(price)}"
    }
}

// TODO: Possibly we should add a zero-width space (\u200b ?) after the "perSymbol" just in case.
// The price and symbol may reasonably use non-breaking spaces to keep the value and the unit
// together, and that may mean there are no actual spaces, so offering this as a breaking point in
// the event we have to wrap might improve the display.
fun UnitPrice.format(context: Context, dataSet: DataSet, locale: Locale) =
    "${formatPrice(
        numerator,
        dataSet,
        locale
    )
    }${denominator.perSymbol}${context.getString(denominator.symbol)}"
