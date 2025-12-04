package com.example.composetutorial.ui.common

import com.example.composetutorial.R

fun createNameValidationRules(existingNameList: List<String>): List<ValidationRule<String>> {
    // We could use Collator.PRIMARY to do this comparison (probably combined with squashing spaces
    // and trim()-ing) but it's probably better to use normalizedForSearch() here.
    // ENHANCE: We use one validation rule per possibly-clashing name here partly so we have the
    // option to modify the message to include the actual clashing name, which may or may not be
    // useful. (Remember the name clash is post-normalisation, so the clash may not be completely
    // obvious.)
    return listOf<ValidationRule<String>>(
        ValidationRule(
            { it.isNotEmpty() },
            UiText.Res(R.string.supporting_text_required)
        )
    ) + existingNameList.map { existingName ->
        val normalizedExistingName = existingName.normalizedForSearch()
        ValidationRule(
            { candidateName -> candidateName.normalizedForSearch() != normalizedExistingName },
            UiText.Res(R.string.supporting_text_name_must_be_unique)
        )
    }
}