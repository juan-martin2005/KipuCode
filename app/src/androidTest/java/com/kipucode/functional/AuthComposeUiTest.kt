package com.kipucode.functional

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kipucode.ui.screens.auth.LoginContent
import com.kipucode.ui.screens.auth.RegisterContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas funcionales automatizadas de interfaz Compose (PF-01, PF-02).
 * Verifica los flujos de interacción de formularios de Login y Registro de Usuario,
 * asegurando la captura de credenciales y el bloqueo reactivo ante validaciones fallidas.
 */
@RunWith(AndroidJUnit4::class)
class AuthComposeUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ============================================================================================
    //  PF-01: Flujo de Interacción y Validación de Inicio de Sesión
    // ============================================================================================

    /**
     * PF-01: Verifies Login UI interaction, credential capture, and callback execution on success.
     */
    @Test
    fun loginFormCapturesCredentialsAndTriggersCallback() {
        // Arrange
        var submittedEmail = ""
        var submittedPassword = ""
        var wasValidated = false
        var isLoginCalled = false

        composeTestRule.setContent {
            LoginContent(
                modifier = Modifier,
                onNavigateToRegister = {},
                onBack = {},
                onNavigateToForgotPassword = {},
                onValidate = { email, pass ->
                    wasValidated = true
                    email.isNotEmpty() && pass.isNotEmpty()
                },
                onLoginClick = { email, password ->
                    isLoginCalled = true
                    submittedEmail = email
                    submittedPassword = password
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("¡Bienvenido de nuevo!").assertIsDisplayed()
        composeTestRule.onNodeWithText("n00123456@upn.pe").performTextInput("n00123456@upn.pe")
        composeTestRule.onNodeWithText("••••••••").performTextInput("kipuPassword2026")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        // Assert
        assertTrue("La función de validación debió ser ejecutada", wasValidated)
        assertTrue("onLoginClick debió ser invocado", isLoginCalled)
        assertEquals("n00123456@upn.pe", submittedEmail)
        assertEquals("kipuPassword2026", submittedPassword)

    }

    /**
     * PF-01: Verifies Login UI blocks submission when validator rejects credentials.
     */
    @Test
    fun loginFormDoesNotTriggerCallbackWhenValidationFails() {
        // Arrange
        var wasValidated = false
        var isLoginCalled = false

        composeTestRule.setContent {
            LoginContent(
                modifier = Modifier,
                onNavigateToRegister = {},
                onBack = {},
                onNavigateToForgotPassword = {},
                onValidate = { _, _ ->
                    wasValidated = true
                    false // Simula fallo en validación (e.g. dominio no institucional)
                },
                onLoginClick = { _, _ ->
                    isLoginCalled = true
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("n00123456@upn.pe").performTextInput("invalido@externo.xyz")
        composeTestRule.onNodeWithText("••••••••").performTextInput("123")
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        // Assert
        assertTrue("La función de validación debió ser ejecutada", wasValidated)
        assertFalse("onLoginClick NO debe ser invocado si la validación falla", isLoginCalled)
    }

    // ============================================================================================
    //  PF-02: Flujo de Interacción y Validación de Registro de Usuario
    // ============================================================================================

    /**
     * PF-02: Verifies Registration UI interaction, field inputs, and callback execution on success.
     */
    @Test
    fun registrationFormCapturesAllFieldsAndTriggersCallback() {
        // Arrange
        var registeredName = ""
        var registeredEmail = ""
        var registeredPassword = ""
        var wasValidated = false
        var isRegisterCalled = false

        composeTestRule.setContent {
            RegisterContent(
                modifier = Modifier,
                onNavigateToLogin = {},
                onBack = {},
                onValidate = { name, email, pass, confirmPass ->
                    wasValidated = true
                    name.isNotBlank() && email.isNotBlank() && pass == confirmPass
                },
                onRegisterClick = { name, email, password ->
                    isRegisterCalled = true
                    registeredName = name
                    registeredEmail = email
                    registeredPassword = password
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Crear cuenta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tu nombre").performTextInput("Estudiante Kipu")
        composeTestRule.onNodeWithText("n00123456@upn.pe").performTextInput("estudiante@upn.pe")

        // Consumo secuencial del placeholder para password y confirmPassword
        composeTestRule.onAllNodesWithText("••••••••")[0].performTextInput("KipuSecure2026")
        composeTestRule.onAllNodesWithText("••••••••")[0].performTextInput("KipuSecure2026")
        composeTestRule.onNodeWithText("Siguiente").performClick()

        // Assert
        assertTrue("El validador de registro debió ejecutarse", wasValidated)
        assertTrue("onRegisterClick debió ser invocado", isRegisterCalled)
        assertEquals("Estudiante Kipu", registeredName)
        assertEquals("estudiante@upn.pe", registeredEmail)
        assertEquals("KipuSecure2026", registeredPassword)
    }

    /**
     * PF-02: Verifies Registration UI blocks submission when validator rejects form data.
     */
    @Test
    fun registrationFormDoesNotTriggerCallbackWhenValidationFails() {
        // Arrange
        var wasValidated = false
        var isRegisterCalled = false

        composeTestRule.setContent {
            RegisterContent(
                modifier = Modifier,
                onNavigateToLogin = {},
                onBack = {},
                onValidate = { _, _, _, _ ->
                    wasValidated = true
                    false // Simula fallo en validación (e.g. contraseñas no coinciden)
                },
                onRegisterClick = { _, _, _ ->
                    isRegisterCalled = true
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Tu nombre").performTextInput("Marcelino")
        composeTestRule.onNodeWithText("n00123456@upn.pe").performTextInput("marcelino@upn.pe")
        composeTestRule.onAllNodesWithText("••••••••")[0].performTextInput("password123")
        composeTestRule.onAllNodesWithText("••••••••")[0].performTextInput("discrepancia99")
        composeTestRule.onNodeWithText("Siguiente").performClick()

        // Assert
        assertTrue("El validador de registro debió ejecutarse", wasValidated)
        assertFalse("onRegisterClick NO debe ser invocado si la validación falla", isRegisterCalled)
    }
}
