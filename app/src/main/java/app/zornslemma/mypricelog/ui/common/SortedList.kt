package app.zornslemma.mypricelog.ui.common

import android.icu.text.Collator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

fun <T> List<T>.sortedByLocale(selector: (T) -> String, locale: Locale): List<T> {
    val collator = Collator.getInstance(locale).apply { strength = Collator.PRIMARY }

    return sortedWith { lhs, rhs -> collator.compare(selector(lhs), selector(rhs)) }
}

@Composable
fun <T> List<T>.rememberSortedByLocale(selector: (T) -> String): List<T> {
    val locale = LocalConfiguration.current.locales[0]
    return remember(this, locale) { sortedByLocale(selector, locale) }
}
