package com.kipucode.domain.model

enum class ValidationErrorType {
    EMPTY_FIELD,
    INVALID_EMAIL_DOMAIN,
    PASSWORD_TOO_SHORT,
    PASSWORD_MISSING_UPPERCASE,
    PASSWORD_MISSING_LOWERCASE,
    PASSWORD_MISSING_DIGIT,
    PASSWORD_MISSING_SPECIAL_CHAR,
    PASSWORDS_DONT_MATCH,
    NAME_TOO_SHORT,
    NAME_CONTAINS_NUMBERS
}

data class ValidationResult(
    val successful: Boolean,
    val errorType: ValidationErrorType? = null
)
