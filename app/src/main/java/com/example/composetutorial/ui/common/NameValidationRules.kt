package com.example.composetutorial.ui.common

import com.example.composetutorial.R
import com.example.composetutorial.normalizedForSearch

// ENHANCE: Not here specifically, I almost wonder if the lambdas should have the *option* (not
// obligation) to modify the value for later lambdas in the chain, and the validation process
// returns the final one. This *might* provide a natural way to implement things like "strip
// spaces" or "strip insignificant fluff in a double-as-string" as an initial step, avoid
// redoing that work in subsequent lambdas which want the same sanitising and help to avoid the
// situation where for example the validation is all based on a trim()ed string but I forget to
// manually apply the trim() when writing the string to the database. On the other hand, applying
// the validation rule changes to a data class via copy() might be finicky and error prone, and
// this would perhaps add subtle behavioural quirks around the ordering of the list which might
// be brittle. (Then again, with respect to brittleness, some rules' error messages might implicitly
// assume earlier rules already filtered out some unacceptable cases anyway.)
fun createNameValidationRules(existingNameList: List<String>): List<ValidationRule<String>> {
    // We could use Collator.PRIMARY to do this comparison (probably combined with squashing spaces and trim()-ing) but it's probably better to use normalizedForSearch() here.
    // TODO: Can/should we just return a single ValidationRule here which does an internal check
    // against a set pregenerated from existingNameList?
    val TODO0 =         ValidationRule<String>({ it.isNotEmpty() }, UiText.Res(R.string.supporting_text_required))
    val TODO1 = listOf(TODO0)

    val TODO2 = existingNameList.map { existingName ->
        val normalizedExistingName = existingName.normalizedForSearch()
        ValidationRule<String>(
            { candidateName -> candidateName.normalizedForSearch() != normalizedExistingName },
            UiText.Res(R.string.supporting_text_name_must_be_unique)
        )
    }
    return TODO1 + TODO2
}