package com.kipucode.domain.model

enum class ValidationErrorType{
    EMPTY_FIELD,
    INVALID_EMAIL_DOMAIN,
    PASSWORD_TOO_SHORT,
    PASSWORDS_DONT_MATCH
}

data class ValidationResult (
    val successful: Boolean,
    val errorType: ValidationErrorType? = null
)