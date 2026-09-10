package com.kipucode.domain.usecase.user

import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.domain.model.ValidationResult
import javax.inject.Inject


class ValidateFullNameUseCase @Inject constructor(){
    operator fun invoke(fullName: String): ValidationResult{
        if (fullName.isBlank()){
            return ValidationResult(false, ValidationErrorType.EMPTY_FIELD)
        }

        return ValidationResult(true)
    }
}
class ValidateEmailUseCase @Inject constructor(){
    operator fun invoke(email: String): ValidationResult{
        val emailTrimmed = email.trim().lowercase()

        if (emailTrimmed.isBlank()){
            return ValidationResult(false, ValidationErrorType.EMPTY_FIELD)
        }

        if (!emailTrimmed.endsWith("@upn.pe")){
            return ValidationResult(false, ValidationErrorType.INVALID_EMAIL_DOMAIN)
        }

        return ValidationResult(true)
    }
}

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String): ValidationResult{
        if (password.isBlank()){
            return ValidationResult(false, ValidationErrorType.EMPTY_FIELD)
        }

        if (password.length<6){
            return ValidationResult(false, ValidationErrorType.PASSWORD_TOO_SHORT)
        }

        return ValidationResult(true)
    }
}

class ValidateConfirmPasswordUseCase @Inject constructor(){
    operator fun invoke(password:String, confirmPassword: String): ValidationResult{
        if (confirmPassword.isBlank()){
            return ValidationResult(false, ValidationErrorType.EMPTY_FIELD)
        }

        if (confirmPassword != password){
            return ValidationResult(false, ValidationErrorType.PASSWORDS_DONT_MATCH)
        }

        return ValidationResult(true)
    }
}