package com.example.composetutorial.ui.common

data class ValidationRule<T>(val validate: (T) -> Boolean, val message: UiText)

fun <T> failedValidationRuleOrNull(validationRules: List<ValidationRule<T>>, value: T): ValidationRule<T>? {
    for (validationRule in validationRules) {
        if (!validationRule.validate(value)) {
            return validationRule
        }
    }
    return null
}

fun <T> validationRulesOk(validationRules: List<ValidationRule<T>>, value: T) =
    failedValidationRuleOrNull(validationRules, value) == null