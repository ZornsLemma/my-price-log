package com.example.composetutorial.ui.common

import java.text.Normalizer
import java.util.Locale

// TODO: Not here, but since I've used z spelling here and am generally following US-ish Android/Compose standards, should I rename "sanitise*" functions?

// From discussion with LLMs and doing my own web searches, we need something like the ICU string
// search service (https://unicode-org.github.io/icu/userguide/collation/string-search) to do really
// good substring searches in different languages. This is apparently quite large and Android
// doesn't include it by default, even though it has some ICU stuff. Further LLM discussion suggests
// that using this form of normalization is the usual compromise.
//
// Note that we deliberately use the root locale here, not the current locale. Apparently using (for
// example) the Turkish locale would break well-established expectations for Turkish users (the
// "Turkish I problem"). I think the key point here is that for *searching* we want to use very
// loose rules, not the linguistically correct ones for the locale, because users don't want to
// fiddle around press-and-holding keys to get the correct characters for ephemeral input. In the
// Turkish case users want to search for "istanbul" and have it match "İstanbul" even though they
// are different according to the locale, simply because it's a lot more convenient to type the
// former.
// TODO: It might be good to fake up some data with these strings (copy and paste into emulator!)
// and see if we are apparently getting this right.
fun String.normalizedForSearch() = Normalizer.normalize(this, Normalizer.Form.NFD)
    // Remove diacritics
    .replace("\\p{M}".toRegex(), "")
    // Remove all forms of apostrophes/quotes
    .replace("['’ʻʼʽʾˮˈˌʹʺ˝]".toRegex(), "")
    // Replace all remaining punctuation and symbols with spaces
    .replace("[\\p{Punct}\\p{Symbol}]".toRegex(), " ")
    // Collapse adjacent whitespace into single spaces
    .replace("\\s+".toRegex(), " ")
    // Trim leading/trailing whitespace
    .trim()
    // Lowercase using invariant rules to ensure case insensitivity
    .lowercase(Locale.ROOT)
