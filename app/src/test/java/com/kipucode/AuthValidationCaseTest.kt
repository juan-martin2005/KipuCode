package com.kipucode

import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.domain.usecase.user.ValidateEmailUseCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ValidateEmailUseCaseTest{
    private lateinit var validateEmailUseCase: ValidateEmailUseCase

    @Before
    fun steUp(){
        validateEmailUseCase = ValidateEmailUseCase()
    }

    @Test
    fun `Cuando el Email esta vacio, retorna error EMPTY_FIELD`(){
        val result = validateEmailUseCase("")
        Assert.assertFalse(result.successful)
        Assert.assertEquals(ValidationErrorType.EMPTY_FIELD, result.errorType)
    }

    @Test
    fun `Cuando el dominio no es upn punto pe, retorna error INVALID_EMAIL_DOMAIN`() {
        val result = validateEmailUseCase("estudiante@gmail.com")
        Assert.assertFalse(result.successful)
        Assert.assertEquals(ValidationErrorType.INVALID_EMAIL_DOMAIN, result.errorType)
    }

    @Test
    fun `Cuando el correo institucional es valido, no retorna ningun error`() {
        val result = validateEmailUseCase("n00123456@upn.pe")
        Assert.assertTrue(result.successful)
        Assert.assertEquals(null, result.errorType)
    }
}