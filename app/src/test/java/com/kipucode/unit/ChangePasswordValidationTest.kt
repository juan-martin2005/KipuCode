package com.kipucode.unit

import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.domain.usecase.auth.ValidateConfirmPasswordUseCase
import com.kipucode.domain.usecase.auth.ValidatePasswordUseCase
import com.kipucode.ui.screens.changepassword.ChangePasswordUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias para el flujo de validación de Cambio de Contraseña (ChangePasswordScreen / ViewModel).
 * Valida que el Paso 1 (Verificación de contraseña actual) NO exija complejidad estricta,
 * y que el Paso 2 (Nueva contraseña y confirmación) sí exija las políticas completas.
 */
class ChangePasswordValidationTest {

    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    private lateinit var validateConfirmPasswordUseCase: ValidateConfirmPasswordUseCase

    @Before
    fun setUp() {
        validatePasswordUseCase = ValidatePasswordUseCase()
        validateConfirmPasswordUseCase = ValidateConfirmPasswordUseCase()
    }

    // ====================================================================================
    // --- PASO 1: VALIDACIÓN DE CONTRASEÑA ACTUAL (requireComplexity = false) ---
    // ====================================================================================

    @Test
    fun `step 1 - current password empty returns EMPTY_FIELD error`() {
        val result = validatePasswordUseCase("", requireComplexity = false)
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.EMPTY_FIELD, result.errorType)
    }

    @Test
    fun `step 1 - current password whitespace only returns EMPTY_FIELD error`() {
        val result = validatePasswordUseCase("     ", requireComplexity = false)
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.EMPTY_FIELD, result.errorType)
    }

    @Test
    fun `step 1 - current password less than 6 chars returns PASSWORD_TOO_SHORT error`() {
        val result = validatePasswordUseCase("12345", requireComplexity = false)
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.PASSWORD_TOO_SHORT, result.errorType)
    }

    @Test
    fun `step 1 - current password without uppercase nor special char is accepted when complexity is false`() {
        // Contraseñas simples existentes que antes fallaban silenciosamente
        val result = validatePasswordUseCase("123456", requireComplexity = false)
        assertTrue(result.successful)
        assertNull(result.errorType)
    }

    @Test
    fun `step 1 - current password simple text is accepted when complexity is false`() {
        val result = validatePasswordUseCase("miclaveactual", requireComplexity = false)
        assertTrue(result.successful)
        assertNull(result.errorType)
    }

    @Test
    fun `step 1 - current password complex is also accepted`() {
        val result = validatePasswordUseCase("ClaveSegura2026!", requireComplexity = false)
        assertTrue(result.successful)
        assertNull(result.errorType)
    }

    // ====================================================================================
    // --- PASO 2: VALIDACIÓN DE NUEVA CONTRASEÑA (requireComplexity = true) ---
    // ====================================================================================

    @Test
    fun `step 2 - new password empty returns EMPTY_FIELD error`() {
        val result = validatePasswordUseCase("", requireComplexity = true)
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.EMPTY_FIELD, result.errorType)
    }

    @Test
    fun `step 2 - new password without uppercase returns PASSWORD_MISSING_UPPERCASE`() {
        val result = validatePasswordUseCase("password123!", requireComplexity = true)
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.PASSWORD_MISSING_UPPERCASE, result.errorType)
    }

    @Test
    fun `step 2 - new password without lowercase returns PASSWORD_MISSING_LOWERCASE`() {
        val result = validatePasswordUseCase("PASSWORD123!", requireComplexity = true)
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.PASSWORD_MISSING_LOWERCASE, result.errorType)
    }

    @Test
    fun `step 2 - new password without digit returns PASSWORD_MISSING_DIGIT`() {
        val result = validatePasswordUseCase("Password!", requireComplexity = true)
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.PASSWORD_MISSING_DIGIT, result.errorType)
    }

    @Test
    fun `step 2 - new password without special char returns PASSWORD_MISSING_SPECIAL_CHAR`() {
        val result = validatePasswordUseCase("Password123", requireComplexity = true)
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.PASSWORD_MISSING_SPECIAL_CHAR, result.errorType)
    }

    @Test
    fun `step 2 - new password complying with all policies is valid`() {
        val result = validatePasswordUseCase("P@ssw0rd2026!", requireComplexity = true)
        assertTrue(result.successful)
        assertNull(result.errorType)
    }

    // ====================================================================================
    // --- PASO 2: CONFIRMACIÓN DE CONTRASEÑA ---
    // ====================================================================================

    @Test
    fun `step 2 - confirm password empty returns EMPTY_FIELD error`() {
        val result = validateConfirmPasswordUseCase("P@ssw0rd2026!", "")
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.EMPTY_FIELD, result.errorType)
    }

    @Test
    fun `step 2 - confirm password mismatch returns PASSWORDS_DONT_MATCH error`() {
        val result = validateConfirmPasswordUseCase("P@ssw0rd2026!", "P@ssw0rd2026?")
        assertFalse(result.successful)
        assertEquals(ValidationErrorType.PASSWORDS_DONT_MATCH, result.errorType)
    }

    @Test
    fun `step 2 - confirm password matching returns successful`() {
        val result = validateConfirmPasswordUseCase("P@ssw0rd2026!", "P@ssw0rd2026!")
        assertTrue(result.successful)
        assertNull(result.errorType)
    }

    // ====================================================================================
    // --- UI STATE DEFAULTS ---
    // ====================================================================================

    @Test
    fun `change password initial ui state starts at step 1 with no errors`() {
        val initialState = ChangePasswordUiState()
        assertEquals(1, initialState.currentStep)
        assertNull(initialState.currentPasswordError)
        assertNull(initialState.newPasswordError)
        assertNull(initialState.confirmPasswordError)
        assertNull(initialState.serverError)
        assertFalse(initialState.isLoading)
        assertFalse(initialState.isSuccess)
    }
}
