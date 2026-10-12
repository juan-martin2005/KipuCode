package com.kipucode

import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.domain.usecase.user.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.user.ValidateEmailUseCase
import com.kipucode.domain.usecase.user.ValidateFullNameUseCase
import com.kipucode.domain.usecase.user.ValidatePasswordUseCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ValidateFormTest {
    private data class AuthForm(
        val field: String,
        val value: String,
        val secondaryValue: String = "", // Solo necesario para confirmPassword
        val expected: Boolean,
        val error: ValidationErrorType?
    )

    private lateinit var validateFullNameUseCase: ValidateFullNameUseCase
    private lateinit var validateEmailUseCase: ValidateEmailUseCase
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    private lateinit var validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase

    @Before
    fun setUp() {
        validateFullNameUseCase = ValidateFullNameUseCase()
        validateEmailUseCase = ValidateEmailUseCase()
        validatePasswordUseCase = ValidatePasswordUseCase()
        validateConfirmPasswordUseCase = ValidateConfirmPasswordUseCase()
    }

    @Test
    fun `Verificacion de multiples casos de prueba para el formulario de registro de usuarios`() {
        val listOfTests = listOf(
            // --- NOMBRE COMPLETO ---
            AuthForm(field = "name", value = "", expected = false, error = ValidationErrorType.EMPTY_FIELD),
            AuthForm(field = "name", value = "   ", expected = false, error = ValidationErrorType.EMPTY_FIELD),

            AuthForm(field = "name", value = "Marcelino Pérez", expected = true, error = null),

            // --- CORREO ELECTRÓNICO ---
            AuthForm(field = "email", value = "", expected = false, error = ValidationErrorType.EMPTY_FIELD),
            AuthForm(field = "email", value = "  ", expected = false, error = ValidationErrorType.EMPTY_FIELD),
            AuthForm(field = "email", value = "test@empresa.xyz", expected = false, error = ValidationErrorType.INVALID_EMAIL_DOMAIN),
            AuthForm(field = "email", value = "correo_sin_arroba.pe", expected = false, error = ValidationErrorType.INVALID_EMAIL_DOMAIN),

            AuthForm(field = "email", value = "alumno@upn.pe", expected = true, error = null),
            AuthForm(field = "email", value = "usuario@gmail.com", expected = true, error = null),
            AuthForm(field = "email", value = "cuenta@outlook.com", expected = true, error = null),

            // --- CONTRASEÑA ---
            AuthForm(field = "password", value = "", expected = false, error = ValidationErrorType.EMPTY_FIELD),
            AuthForm(field = "password", value = "12345", expected = false, error = ValidationErrorType.PASSWORD_TOO_SHORT),

            AuthForm(field = "password", value = "123456", expected = true, error = null),
            AuthForm(field = "password", value = "miClaveSegura2026", expected = true, error = null),

            // --- CONFIRMAR CONTRASEÑA ---
            AuthForm(field = "confirmPassword", value = "", secondaryValue = "123456", expected = false, error = ValidationErrorType.EMPTY_FIELD),
            AuthForm(field = "confirmPassword", value = "otraClave99", secondaryValue = "123456", expected = false, error = ValidationErrorType.PASSWORDS_DONT_MATCH),

            AuthForm(field = "confirmPassword", value = "123456", secondaryValue = "123456", expected = true, error = null)
        )

        listOfTests.forEach { dataForm ->
            val result = when (dataForm.field) {
                "name" -> validateFullNameUseCase(dataForm.value)
                "email" -> validateEmailUseCase(dataForm.value)
                "password" -> validatePasswordUseCase(dataForm.value)
                "confirmPassword" -> validateConfirmPasswordUseCase(
                    dataForm.secondaryValue,
                    dataForm.value)
                else -> null
            }

            Assert.assertEquals(
                "Fallo en `successful` para [${dataForm.field}: ${dataForm.value}]",
                dataForm.expected,
                result?.successful
            )

            Assert.assertEquals(
                "Fallo en `errorType` para [${dataForm.field}: ${dataForm.value}]",
                dataForm.error,
                result?.errorType
            )
        }
    }
}