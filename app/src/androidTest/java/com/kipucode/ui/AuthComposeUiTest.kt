package com.kipucode.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kipucode.domain.usecase.auth.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.auth.ValidateEmailUseCase
import com.kipucode.domain.usecase.auth.ValidateFullNameUseCase
import com.kipucode.domain.usecase.auth.ValidatePasswordUseCase
import com.kipucode.ui.screens.login.LoginContent
import com.kipucode.ui.screens.register.RegisterContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthComposeUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var validateEmailUseCase: ValidateEmailUseCase
    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    private lateinit var validateFullNameUseCase: ValidateFullNameUseCase
    private lateinit var validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase

    @Before
    fun setUp() {
        validateEmailUseCase = ValidateEmailUseCase()
        validatePasswordUseCase = ValidatePasswordUseCase()
        validateFullNameUseCase = ValidateFullNameUseCase()
        validateConfirmPasswordUseCase = ValidateConfirmPasswordUseCase()
    }

    // ============================================================================================
    //  PF-01: Flujo de Interacción y Validación de Inicio de Sesión
    // ============================================================================================
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
                    val emailResult = validateEmailUseCase(email)
                    val passResult = validatePasswordUseCase(pass)
                    emailResult.successful && passResult.successful
                },
                onLoginClick = { email, password ->
                    isLoginCalled = true
                    submittedEmail = email
                    submittedPassword = password
                }
            )
        }

        // Act
        composeTestRule.onNodeWithTag("input_login_email").performTextInput("marcelomamani246@gmail.com")
        composeTestRule.onNodeWithTag("input_login_password").performTextInput("123456")
        composeTestRule.onNodeWithTag("btn_login").performClick()

        // Assert
        assertTrue(wasValidated)
        assertTrue(isLoginCalled)
        assertEquals("marcelomamani246@gmail.com", submittedEmail)
        assertEquals("123456", submittedPassword)
    }

    // ============================================================================================
    //  PF-02: Flujo de Interacción y Validación de Registro de Usuario
    // ============================================================================================
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
                    val nameResult = validateFullNameUseCase(name)
                    val emailResult = validateEmailUseCase(email)
                    val passResult = validatePasswordUseCase(pass)
                    val confirmpassResult = validateConfirmPasswordUseCase(pass, confirmPass)
                    nameResult.successful && emailResult.successful && passResult.successful && confirmpassResult.successful
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
        composeTestRule.onNodeWithTag("input_register_name").performTextInput("Marcelo Fernando")
        composeTestRule.onNodeWithTag("input_register_email").performTextInput("marcelomamani246@gmail.com")

        composeTestRule.onNodeWithTag("input_register_password").performTextInput("123456")
        composeTestRule.onNodeWithTag("input_register_confirm_password").performTextInput("123456")
        composeTestRule.onNodeWithTag("btn_register").performClick()

        // Assert
        assertTrue("El validador de registro debió ejecutarse", wasValidated)
        assertTrue("onRegisterClick debió ser invocado", isRegisterCalled)
        assertEquals("Marcelo Fernando", registeredName)
        assertEquals("marcelomamani246@gmail.com", registeredEmail)
        assertEquals("123456", registeredPassword)
    }
}
