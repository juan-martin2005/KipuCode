package com.kipucode.unit

import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.domain.usecase.auth.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.auth.ValidateEmailUseCase
import com.kipucode.domain.usecase.auth.ValidateFullNameUseCase
import com.kipucode.domain.usecase.auth.ValidatePasswordUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

data class AuthFormTestCase(
    val field: String,
    val value: String,
    val secondaryValue: String = "", // Requerido para confirmPassword
    val expected: Boolean,
    val error: ValidationErrorType?,
    val scenario: String
) {
    override fun toString(): String = "$field: '$value' -> expected: $expected ($scenario)"
}

@RunWith(Parameterized::class)
class AuthFormValidationTest(
    private val dataForm: AuthFormTestCase
) {

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

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<AuthFormTestCase> {
            return listOf(
                // ====================================================================================
                // --- (PU-01) - NOMBRE COMPLETO ---
                // ====================================================================================
                AuthFormTestCase(
                    field = "name",
                    value = "",
                    expected = false,
                    error = ValidationErrorType.EMPTY_FIELD,
                    scenario = "Nombre vacío"
                ),
                AuthFormTestCase(
                    field = "name",
                    value = "   ",
                    expected = false,
                    error = ValidationErrorType.EMPTY_FIELD,
                    scenario = "Nombre con solo espacios"
                ),
                AuthFormTestCase(
                    field = "name",
                    value = "Marcelino Pérez",
                    expected = true,
                    error = null,
                    scenario = "Nombre alfabético con tilde"
                ),
                AuthFormTestCase(
                    field = "name",
                    value = "Juan Pérez",
                    expected = true,
                    error = null,
                    scenario = "Nombre de estudiante estándar"
                ),

                // ====================================================================================
                // --- (PU-02) - CORREO ELECTRÓNICO ---
                // ====================================================================================
                AuthFormTestCase(
                    field = "email",
                    value = "",
                    expected = false,
                    error = ValidationErrorType.EMPTY_FIELD,
                    scenario = "Email vacío"
                ),
                AuthFormTestCase(
                    field = "email",
                    value = "  ",
                    expected = false,
                    error = ValidationErrorType.EMPTY_FIELD,
                    scenario = "Email solo espacios"
                ),
                AuthFormTestCase(
                    field = "email",
                    value = "test@empresa.xyz",
                    expected = false,
                    error = ValidationErrorType.INVALID_EMAIL_DOMAIN,
                    scenario = "Dominio .xyz no autorizado"
                ),
                AuthFormTestCase(
                    field = "email",
                    value = "correo_sin_arroba.pe",
                    expected = false,
                    error = ValidationErrorType.INVALID_EMAIL_DOMAIN,
                    scenario = "Correo sin arroba"
                ),
                AuthFormTestCase(
                    field = "email",
                    value = "alumno@upn.pe",
                    expected = true,
                    error = null,
                    scenario = "Correo institucional UPN"
                ),
                AuthFormTestCase(
                    field = "email",
                    value = "usuario@gmail.com",
                    expected = true,
                    error = null,
                    scenario = "Correo comercial Gmail (.com)"
                ),

                // ====================================================================================
                // --- (PU-03) - CONTRASEÑA ---
                // ====================================================================================
                AuthFormTestCase(
                    field = "password",
                    value = "",
                    expected = false,
                    error = ValidationErrorType.EMPTY_FIELD,
                    scenario = "Contraseña vacía"
                ),
                AuthFormTestCase(
                    field = "password",
                    value = "     ",
                    expected = false,
                    error = ValidationErrorType.EMPTY_FIELD,
                    scenario = "Contraseña solo espacios"
                ),
                AuthFormTestCase(
                    field = "password",
                    value = "12345",
                    expected = false,
                    error = ValidationErrorType.PASSWORD_TOO_SHORT,
                    scenario = "Longitud 5 caso límite inferior"
                ),
                AuthFormTestCase(
                    field = "password",
                    value = "abcde",
                    expected = false,
                    error = ValidationErrorType.PASSWORD_TOO_SHORT,
                    scenario = "Longitud 5 alfabética"
                ),
                AuthFormTestCase(
                    field = "password",
                    value = "123456",
                    expected = true,
                    error = null,
                    scenario = "Longitud 6 caso límite exacto"
                ),
                AuthFormTestCase(
                    field = "password",
                    value = "abcdef",
                    expected = true,
                    error = null,
                    scenario = "Longitud 6 alfabética"
                ),

                // ====================================================================================
                // --- (PU-04) - CONFIRMAR CONTRASEÑA ---
                // ====================================================================================
                AuthFormTestCase(
                    field = "confirmPassword",
                    value = "",
                    secondaryValue = "123456",
                    expected = false,
                    error = ValidationErrorType.EMPTY_FIELD,
                    scenario = "Confirmación vacía"
                ),
                AuthFormTestCase(
                    field = "confirmPassword",
                    value = "   ",
                    secondaryValue = "123456",
                    expected = false,
                    error = ValidationErrorType.EMPTY_FIELD,
                    scenario = "Confirmación solo espacios"
                ),
                AuthFormTestCase(
                    field = "confirmPassword",
                    value = "12345",
                    secondaryValue = "123456",
                    expected = false,
                    error = ValidationErrorType.PASSWORDS_DONT_MATCH,
                    scenario = "Longitud discrepante"
                ),
                AuthFormTestCase(
                    field = "confirmPassword",
                    value = "otraClave99",
                    secondaryValue = "123456",
                    expected = false,
                    error = ValidationErrorType.PASSWORDS_DONT_MATCH,
                    scenario = "Claves totalmente distintas"
                ),
                AuthFormTestCase(
                    field = "confirmPassword",
                    value = "kipupassword",
                    secondaryValue = "KipuPassword",
                    expected = false,
                    error = ValidationErrorType.PASSWORDS_DONT_MATCH,
                    scenario = "Sensibilidad a mayúsculas/minúsculas"
                ),
                AuthFormTestCase(
                    field = "confirmPassword",
                    value = "123456",
                    secondaryValue = "123456",
                    expected = true,
                    error = null,
                    scenario = "Coincidencia numérica exacta"
                )
            )
        }
    }

    @Test
    fun verifyMultipleTestCaseScenariosForAuthenticationForms() {
        // Act
        val result = when (dataForm.field) {
            "name" -> validateFullNameUseCase(dataForm.value)
            "email" -> validateEmailUseCase(dataForm.value)
            "password" -> validatePasswordUseCase(dataForm.value)
            "confirmPassword" -> validateConfirmPasswordUseCase(dataForm.secondaryValue, dataForm.value)
            else -> throw IllegalArgumentException("Campo de formulario desconocido: '${dataForm.field}'")
        }

        // Assert
        assertEquals(
            "Fallo en `successful` para [${dataForm.field}: '${dataForm.value}'] (${dataForm.scenario})",
            dataForm.expected,
            result.successful
        )
        assertEquals(
            "Fallo en `errorType` para [${dataForm.field}: '${dataForm.value}'] (${dataForm.scenario})",
            dataForm.error,
            result.errorType
        )
    }
}