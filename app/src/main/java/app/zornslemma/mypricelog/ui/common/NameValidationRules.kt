package app.zornslemma.mypricelog.ui.common

import app.zornslemma.mypricelog.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

private fun createNameValidationRules(
    existingNameList: List<String>
): List<ValidationRule<String>> {
    // We could use Collator.PRIMARY to do this comparison (probably combined with squashing spaces
    // and trim()-ing) but it's probably better to use normalizedForSearch() here.
    // ENHANCE: We use one validation rule per possibly-clashing name here partly so we have the
    // option to modify the message to include the actual clashing name, which may or may not be
    // useful. (Remember the name clash is post-normalisation, so the clash may not be completely
    // obvious.)
    return listOf<ValidationRule<String>>(
        ValidationRule({ it.isNotEmpty() }, UiText.Res(R.string.supporting_text_required))
    ) +
        existingNameList.map { existingName ->
            val normalizedExistingName = existingName.normalizedForSearch()
            ValidationRule(
                { candidateName -> candidateName.normalizedForSearch() != normalizedExistingName },
                UiText.Res(R.string.supporting_text_name_must_be_unique),
            )
        }
}

// Create a name validation rules flow which will be null initially while we wait for the database
// results to become available. By making composables which apply the rules treat null as "no rules"
// and the view model's validateForSave() silently return false without emitting a validation event,
// we get practically correct behaviour and fix a theoretical corner case where the user manages to
// click Save before the rules have loaded and thereby skips validation. Instead Save will just be a
// no-op in this case, which isn't ideal but it won't happen in practice and it protects the
// integrity of the database. (It might be nice to disable the Save button until the rules load, but
// this would cause a small unnecessary visual glitch and is also more intrusive to arrange than
// it's worth.)
fun nameValidationRulesFlow(
    otherNameListFlow: Flow<List<String>>,
    viewModelScope: CoroutineScope,
): StateFlow<Versioned<out List<ValidationRule<String>>?>> {
    return otherNameListFlow
        .map { createNameValidationRules(it) }
        .withVersion()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            Versioned.initial(null as List<ValidationRule<String>>?),
        )
}
