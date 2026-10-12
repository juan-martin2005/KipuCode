package com.kipucode

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kipucode.ui.screens.auth.LoginContent
import com.kipucode.ui.screens.auth.RegisterContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun login_whenCredentialsSubmitted_triggersLoginCallback() {
        var isLoginCalled = false
        var submittedEmail = ""
        var submittedPassword = ""

        // Arrange
        composeTestRule.setContent {
            LoginContent(
                modifier = Modifier,
                onNavigateToRegister = {},
                onBack = {},
                onNavigateToForgotPassword = {},
                onValidate = { _, _ -> true },
                onLoginClick = { email, password ->
                    isLoginCalled = true
                    submittedEmail = email
                    submittedPassword = password
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("n00123456@upn.pe")
            .performTextInput("student@gmail.com")

        composeTestRule.onNodeWithText("••••••••")
            .performTextInput("securePassword123")

        composeTestRule.onNodeWithText("Iniciar Sesión")
            .performClick()

        // Assert
        assertTrue("onLoginClick should be called", isLoginCalled)
        assertEquals("student@gmail.com", submittedEmail)
        assertEquals("securePassword123", submittedPassword)
    }

    @Test
    fun register_whenFormSubmitted_triggersRegisterCallback() {
        var isRegisterCalled = false
        var submittedName = ""
        var submittedEmail = ""
        var submittedPassword = ""

        // Arrange
        composeTestRule.setContent {
            RegisterContent(
                modifier = Modifier,
                onNavigateToLogin = {},
                onBack = {},
                onValidate = { _, _, _, _ -> true },
                onRegisterClick = { name, email, password ->
                    isRegisterCalled = true
                    submittedName = name
                    submittedEmail = email
                    submittedPassword = password
                }
            )
        }

        // Act
        composeTestRule.onNodeWithText("Tu nombre")
            .performTextInput("Marcelino")

        composeTestRule.onNodeWithText("n00123456@upn.pe")
            .performTextInput("marcelino@upn.pe")

        val passwordFields = composeTestRule.onAllNodesWithText("••••••••")
        passwordFields[0].performTextInput("password123")
        passwordFields[1].performTextInput("password123")

        composeTestRule.onNodeWithText("Siguiente")
            .performClick()

        // Assert
        assertTrue("onRegisterClick should be called", isRegisterCalled)
        assertEquals("Marcelino", submittedName)
        assertEquals("marcelino@upn.pe", submittedEmail)
        assertEquals("password123", submittedPassword)
    }
}
