package com.kipucode.ui.screens.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kipucode.R
import com.kipucode.domain.model.ValidationErrorType
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.components.card.KipuDialog
import com.kipucode.ui.components.text_field.KipuForm
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.PasswordViewModel

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    viewModel: PasswordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            KipuTopBar(
                title = stringResource(R.string.change_password),
                onBackClick = onBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 8.dp)
            )
        },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "ChangePasswordStepTransition"
            ) { step ->
                when (step) {
                    1 -> {
                        val currentPasswordErrorText = when (uiState.currentPasswordError) {
                            ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_password_required)
                            ValidationErrorType.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
                            else -> uiState.serverError
                        }

                        VerifyCurrentPasswordStep(
                            currentPassword = currentPassword,
                            onCurrentPasswordChange = {
                                currentPassword = it
                                viewModel.clearCurrentPasswordError()
                            },
                            currentPasswordVisible = currentPasswordVisible,
                            onToggleVisibility = { currentPasswordVisible = !currentPasswordVisible },
                            errorMessage = currentPasswordErrorText,
                            isLoading = uiState.isLoading,
                            onContinueClick = {
                                viewModel.verifyCurrentPassword(currentPassword)
                            }
                        )
                    }

                    2 -> {
                        val newPasswordErrorText = when (uiState.newPasswordError) {
                            ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_password_required)
                            ValidationErrorType.PASSWORD_TOO_SHORT -> stringResource(R.string.error_password_too_short)
                            else -> null
                        }

                        val confirmPasswordErrorText = when (uiState.confirmPasswordError) {
                            ValidationErrorType.EMPTY_FIELD -> stringResource(R.string.error_confirm_password_required)
                            ValidationErrorType.PASSWORDS_DONT_MATCH -> stringResource(R.string.error_password_mismatch)
                            else -> uiState.serverError
                        }

                        NewPasswordStep(
                            newPassword = newPassword,
                            onNewPasswordChange = {
                                newPassword = it
                                viewModel.clearNewPasswordError()
                            },
                            confirmPassword = confirmPassword,
                            onConfirmPasswordChange = {
                                confirmPassword = it
                                viewModel.clearConfirmPasswordError()
                            },
                            newPasswordVisible = newPasswordVisible,
                            onToggleNewPasswordVisibility = { newPasswordVisible = !newPasswordVisible },
                            confirmPasswordVisible = confirmPasswordVisible,
                            onToggleConfirmPasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
                            newPasswordError = newPasswordErrorText,
                            confirmPasswordError = confirmPasswordErrorText,
                            isLoading = uiState.isLoading,
                            onSaveClick = {
                                viewModel.submitNewPassword(newPassword, confirmPassword)
                            }
                        )
                    }
                }
            }

            if (uiState.isSuccess) {
                KipuDialog(
                    title = stringResource(R.string.password_updated_title),
                    description = stringResource(R.string.password_updated_desc),
                    confirmButtonText = stringResource(R.string.confirm),
                    dismissButtonText = stringResource(R.string.cancel),
                    onConfirmClick = {
                        viewModel.resetState()
                        onBack()
                    },
                    onDismissClick = {
                        viewModel.resetState()
                        onBack()
                    },
                    onDismissRequest = {
                        viewModel.resetState()
                        onBack()
                    },
                    iconRes = R.drawable.ic_correct,
                    iconTint = KipuTeal
                )
            }
        }
    }
}

@Composable
private fun VerifyCurrentPasswordStep(
    currentPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    currentPasswordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.verify_identity_title),
            fontSize = 28.sp,
            fontFamily = Nunito,
            lineHeight = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KipuDarkBlue,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.verify_identity_desc),
            fontSize = 15.sp,
            fontFamily = Nunito,
            lineHeight = 20.sp,
            color = Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        KipuForm(
            label = stringResource(R.string.current_password),
            value = currentPassword,
            placeholder = if (currentPasswordVisible) stringResource(R.string.current_password).lowercase() else "••••••••",
            iconRes = if (currentPasswordVisible) R.drawable.ic_unlock else R.drawable.ic_lock,
            keyboardType = KeyboardType.Password,
            isPasswordField = true,
            isPasswordVisible = currentPasswordVisible,
            onVisibilityChange = onToggleVisibility,
            onValueChange = onCurrentPasswordChange,
            isError = errorMessage != null,
            errorMessage = errorMessage
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilledButton(
            textButton = stringResource(R.string.continue_text),
            onClickFilledButton = onContinueClick,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun NewPasswordStep(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    newPasswordVisible: Boolean,
    onToggleNewPasswordVisibility: () -> Unit,
    confirmPasswordVisible: Boolean,
    onToggleConfirmPasswordVisibility: () -> Unit,
    newPasswordError: String?,
    confirmPasswordError: String?,
    isLoading: Boolean,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.new_password_title),
            fontSize = 28.sp,
            fontFamily = Nunito,
            lineHeight = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = KipuDarkBlue,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.subheadline_change_password),
            fontSize = 15.sp,
            fontFamily = Nunito,
            lineHeight = 20.sp,
            color = Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        KipuForm(
            label = stringResource(R.string.new_password),
            value = newPassword,
            placeholder = if (newPasswordVisible) stringResource(R.string.new_password).lowercase() else "••••••••",
            iconRes = if (newPasswordVisible) R.drawable.ic_unlock else R.drawable.ic_lock,
            keyboardType = KeyboardType.Password,
            isPasswordField = true,
            isPasswordVisible = newPasswordVisible,
            onVisibilityChange = onToggleNewPasswordVisibility,
            onValueChange = onNewPasswordChange,
            isError = newPasswordError != null,
            errorMessage = newPasswordError
        )

        Spacer(modifier = Modifier.height(8.dp))

        KipuForm(
            label = stringResource(R.string.confirm_password),
            value = confirmPassword,
            placeholder = if (confirmPasswordVisible) stringResource(R.string.confirm_password).lowercase() else "••••••••",
            iconRes = if (confirmPasswordVisible) R.drawable.ic_unlock else R.drawable.ic_lock,
            keyboardType = KeyboardType.Password,
            isPasswordField = true,
            isPasswordVisible = confirmPasswordVisible,
            onVisibilityChange = onToggleConfirmPasswordVisibility,
            onValueChange = onConfirmPasswordChange,
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilledButton(
            textButton = stringResource(R.string.save_password),
            onClickFilledButton = onSaveClick,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordPreview() {
    VerifyCurrentPasswordStep(
        currentPassword = "",
        onCurrentPasswordChange = {},
        currentPasswordVisible = false,
        onToggleVisibility = {},
        isLoading = false,
        errorMessage = null,
        onContinueClick = {}
    )
}