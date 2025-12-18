package app.zornslemma.mypricelog.ui.common

// ENHANCE: I almost wonder if the lambdas should have the *option* (not obligation) to modify the
// value for later lambdas in the chain, and the validation process returns the final one. This
// *might* provide a natural way to implement things like "strip spaces" or "strip insignificant
// fluff in a double-as-string" as an initial step, avoid redoing that work in subsequent lambdas
// which want the same sanitising and help to avoid the situation where for example the validation
// is all based on a trim()ed string but I forget to manually apply the trim() when writing the
// string to the database. On the other hand, applying the validation rule changes to a data class
// via copy() might be finicky and error prone, and this would perhaps add subtle behavioural quirks
// around the ordering of the list which might be brittle. (Then again, with respect to brittleness,
// some rules' error messages might implicitly assume earlier rules already filtered out some
// unacceptable cases anyway.) For example, ValidateFieldState() re-orders the list locally, which
// would probably break this, although of course it could be changed not to do that.
data class ValidationRule<T>(val validate: (T) -> Boolean, val message: UiText)

fun <T> failedValidationRuleOrNull(
    validationRules: List<ValidationRule<T>>,
    value: T,
): ValidationRule<T>? {
    for (validationRule in validationRules) {
        if (!validationRule.validate(value)) {
            return validationRule
        }
    }
    return null
}

fun <T> validationRulesOk(validationRules: List<ValidationRule<T>>, value: T) =
    failedValidationRuleOrNull(validationRules, value) == null
