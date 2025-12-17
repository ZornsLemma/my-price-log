package app.zornslemma.mypricelog.ui.common

import android.content.Context
import app.zornslemma.mypricelog.data.DataSet
import app.zornslemma.mypricelog.domain.UnitPrice
import app.zornslemma.mypricelog.ui.nonBreakingSpace
import app.zornslemma.mypricelog.ui.zeroWidthSpace
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatPrice(price: Double, dataSet: DataSet, locale: Locale): String {
    // At least on Android this doesn't throw for invalid three-letter currency codes but it will
    // throw if given currency code "AAAA", so it seems safest to catch exceptions and have a
    // fallback, even if it's not great.
    try {
        val numberFormat =
            NumberFormat.getCurrencyInstance(locale).apply {
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
        return "${dataSet.currencyCode}${nonBreakingSpace}${numberFormat.format(price)}"
    }
}

// In practice it probably won't be necessary and may not help, but we put a zero-width space in
// here after perSymbol to hint at a good place to wrap if necessary. The price and symbol may have
// spaces in but they are likely (and with good reason) non-breaking spaces.
fun UnitPrice.format(context: Context, dataSet: DataSet, locale: Locale) =
    "${formatPrice(
        numerator,
        dataSet,
        locale,
    )
    }${denominator.perSymbol}${zeroWidthSpace}${context.getString(denominator.symbol)}"
