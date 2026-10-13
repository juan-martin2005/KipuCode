package com.kipucode.unit

import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.domain.usecase.user.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.user.ValidateEmailUseCase
import com.kipucode.domain.usecase.user.ValidateFullNameUseCase
import com.kipucode.domain.usecase.user.ValidatePasswordUseCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Modelo de datos estructurado para pruebas basadas en datos (Data-Driven Testing).
 * Permite documentar cada caso de prueba con su campo, valor, valor secundario, resultado esperado y escenario.
 */
data class AuthFormTestCase(
    val field: String,
    val value: String,
    val secondaryValue: String = "", // Requerido para confirmPassword
    val expected: Boolean,
    val error: ValidationErrorType?,
    val scenario: String
) {
    override fun toString(): String = "[$field: '$value'] -> exp: $expected ($scenario)"
}

/**
 * Suite unificada y parametrizada de pruebas unitarias para el formulario de autenticación (PU-01 a PU-04).
 * Ejecuta de forma atómica y aislada cada caso de prueba bajo el motor Parameterized de JUnit 4.
 */
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
        fun data(): Collection<Array<Any>> {
            return listOf(
                // ====================================================================================
                // --- NOMBRE COMPLETO (PU-01) ---
                // ====================================================================================
                arrayOf(AuthFormTestCase("name", "", expected = false, error = ValidationErrorType.EMPTY_FIELD, scenario = "Nombre vacío")),
                arrayOf(AuthFormTestCase("name", "   ", expected = false, error = ValidationErrorType.EMPTY_FIELD, scenario = "Nombre con solo espacios")),
                arrayOf(AuthFormTestCase("name", "Marcelino Pérez", expected = true, error = null, scenario = "Nombre alfabético con tilde")),
                arrayOf(AuthFormTestCase("name", "Juan Pérez", expected = true, error = null, scenario = "Nombre de estudiante estándar")),
                arrayOf(AuthFormTestCase("name", "Estudiante Capstone UPN", expected = true, error = null, scenario = "Nombre institucional completo")),

                // ====================================================================================
                // --- CORREO ELECTRÓNICO (PU-02: Soporte .com y .pe según regla de producción) ---
                // ====================================================================================
                arrayOf(AuthFormTestCase("email", "", expected = false, error = ValidationErrorType.EMPTY_FIELD, scenario = "Email vacío")),
                arrayOf(AuthFormTestCase("email", "  ", expected = false, error = ValidationErrorType.EMPTY_FIELD, scenario = "Email solo espacios")),
                arrayOf(AuthFormTestCase("email", "test@empresa.xyz", expected = false, error = ValidationErrorType.INVALID_EMAIL_DOMAIN, scenario = "Dominio .xyz no autorizado")),
                arrayOf(AuthFormTestCase("email", "correo_sin_arroba.pe", expected = false, error = ValidationErrorType.INVALID_EMAIL_DOMAIN, scenario = "Correo sin arroba")),
                arrayOf(AuthFormTestCase("email", "usuario@dominio.net", expected = false, error = ValidationErrorType.INVALID_EMAIL_DOMAIN, scenario = "Dominio .net no autorizado")),
                arrayOf(AuthFormTestCase("email", "profesor@portal.org", expected = false, error = ValidationErrorType.INVALID_EMAIL_DOMAIN, scenario = "Dominio .org no autorizado")),
                arrayOf(AuthFormTestCase("email", "invalido@upnpe", expected = false, error = ValidationErrorType.INVALID_EMAIL_DOMAIN, scenario = "Dominio sin punto")),
                arrayOf(AuthFormTestCase("email", "alumno@upn.pe", expected = true, error = null, scenario = "Correo institucional UPN")),
                arrayOf(AuthFormTestCase("email", "n00123456@upn.pe", expected = true, error = null, scenario = "Código institucional de estudiante")),
                arrayOf(AuthFormTestCase("email", "N00123456@UPN.PE", expected = true, error = null, scenario = "Correo institucional en mayúsculas")),
                arrayOf(AuthFormTestCase("email", "usuario@gmail.com", expected = true, error = null, scenario = "Correo comercial Gmail (.com)")),
                arrayOf(AuthFormTestCase("email", "cuenta@outlook.com", expected = true, error = null, scenario = "Correo comercial Outlook (.com)")),
                arrayOf(AuthFormTestCase("email", "  estudiante.kipu@upn.pe  ", expected = true, error = null, scenario = "Correo con espacios periféricos")),

                // ====================================================================================
                // --- CONTRASEÑA (PU-03: Longitud >= 6 caracteres) ---
                // ====================================================================================
                arrayOf(AuthFormTestCase("password", "", expected = false, error = ValidationErrorType.EMPTY_FIELD, scenario = "Contraseña vacía")),
                arrayOf(AuthFormTestCase("password", "     ", expected = false, error = ValidationErrorType.EMPTY_FIELD, scenario = "Contraseña solo espacios")),
                arrayOf(AuthFormTestCase("password", "1", expected = false, error = ValidationErrorType.PASSWORD_TOO_SHORT, scenario = "Longitud 1 (<6)")),
                arrayOf(AuthFormTestCase("password", "123", expected = false, error = ValidationErrorType.PASSWORD_TOO_SHORT, scenario = "Longitud 3 (<6)")),
                arrayOf(AuthFormTestCase("password", "12345", expected = false, error = ValidationErrorType.PASSWORD_TOO_SHORT, scenario = "Longitud 5 (caso límite inferior)")),
                arrayOf(AuthFormTestCase("password", "abcde", expected = false, error = ValidationErrorType.PASSWORD_TOO_SHORT, scenario = "Longitud 5 alfabética")),
                arrayOf(AuthFormTestCase("password", "123456", expected = true, error = null, scenario = "Longitud 6 (caso límite exacto)")),
                arrayOf(AuthFormTestCase("password", "abcdef", expected = true, error = null, scenario = "Longitud 6 alfabética")),
                arrayOf(AuthFormTestCase("password", "miClaveSegura2026", expected = true, error = null, scenario = "Contraseña alfanumérica segura")),
                arrayOf(AuthFormTestCase("password", "KipuPass2026!", expected = true, error = null, scenario = "Contraseña con caracteres especiales")),

                // ====================================================================================
                // --- CONFIRMAR CONTRASEÑA (PU-04: Coincidencia y Case-Sensitivity) ---
                // ====================================================================================
                arrayOf(AuthFormTestCase("confirmPassword", "", secondaryValue = "123456", expected = false, error = ValidationErrorType.EMPTY_FIELD, scenario = "Confirmación vacía")),
                arrayOf(AuthFormTestCase("confirmPassword", "   ", secondaryValue = "123456", expected = false, error = ValidationErrorType.EMPTY_FIELD, scenario = "Confirmación solo espacios")),
                arrayOf(AuthFormTestCase("confirmPassword", "12345", secondaryValue = "123456", expected = false, error = ValidationErrorType.PASSWORDS_DONT_MATCH, scenario = "Longitud discrepante")),
                arrayOf(AuthFormTestCase("confirmPassword", "otraClave99", secondaryValue = "123456", expected = false, error = ValidationErrorType.PASSWORDS_DONT_MATCH, scenario = "Claves totalmente distintas")),
                arrayOf(AuthFormTestCase("confirmPassword", "kipupassword", secondaryValue = "KipuPassword", expected = false, error = ValidationErrorType.PASSWORDS_DONT_MATCH, scenario = "Sensibilidad a mayúsculas/minúsculas")),
                arrayOf(AuthFormTestCase("confirmPassword", "123456", secondaryValue = "123456", expected = true, error = null, scenario = "Coincidencia numérica exacta")),
                arrayOf(AuthFormTestCase("confirmPassword", "KipuSeguro2026", secondaryValue = "KipuSeguro2026", expected = true, error = null, scenario = "Coincidencia alfanumérica exacta"))
            )
        }
    }

    /**
     * PU-01 to PU-04: Verifies form validation rules for user registration fields
     * (Full Name PU-01, Email PU-02, Password PU-03, and Confirm Password PU-04).
     */
    @Test
    fun verifyMultipleTestCaseScenariosForRegistrationForm() {
        // Arrange (Injected by setUp and JUnit Parameterized runner)

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
