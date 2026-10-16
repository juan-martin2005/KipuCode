package com.kipucode.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kipucode.R
import com.kipucode.ui.components.navbar.KipuBottomBar
import com.kipucode.ui.screens.profile.components.avatar.AvatarProvider
import com.kipucode.ui.screens.profile.components.avatar.AvatarSelectionDialog
import com.kipucode.ui.components.cards.KipuDialog
import com.kipucode.ui.screens.home.components.MultipleChoicesCard
import com.kipucode.ui.screens.profile.components.UserProfileCard
import com.kipucode.ui.navigation.ChangePasswordRoute
import com.kipucode.ui.screens.profile.components.ActivityHeatmapCard
import com.kipucode.ui.screens.profile.components.DayStatus
import com.kipucode.ui.screens.profile.components.DayStreakUI
import com.kipucode.ui.screens.profile.components.WeeklyStreak
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

@Composable
fun ProfileScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAvatarList by remember { mutableStateOf(false) }
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.updateAvatarSuccess) {
        if (uiState.updateAvatarSuccess) {
            showAvatarList = false
            profileViewModel.resetAvatarSuccess()
        }
    }

    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
        ) {
            ProfileContent(
                name = uiState.name,
                email = uiState.email,
                avatarId = AvatarProvider.getDrawableById(uiState.avatarId),
                currentStreakDays = uiState.currentStreakDays,
                weekDays = uiState.weekDays,
                nextStreakText = uiState.nextStreakText,
                heatmapData = uiState.heatmapData,
                onLogoutClick = { showLogoutDialog = true },
                onEditAvatar = { showAvatarList = true },
                onChangePassword = { navController.navigate(ChangePasswordRoute) }
            )
        }
    }

    if (showLogoutDialog) {
        KipuDialog(
            title = stringResource(id = R.string.logout_title),
            description = stringResource(id = R.string.logout_desc),
            dismissButtonText = stringResource(id = R.string.cancel),
            confirmButtonText = stringResource(id = R.string.confirm),
            iconRes = R.drawable.ic_exit,
            onDismissRequest = {
                showLogoutDialog = false
            },
            onDismissClick = {
                showLogoutDialog = false
            },
            onConfirmClick = {
                showLogoutDialog = false
                profileViewModel.logout {
                    onLogoutClick()
                }
            },
            iconTint = KipuTeal
        )
    }

    if (showAvatarList) {
        AvatarSelectionDialog(
            currentAvatarId = uiState.avatarId,
            onDismiss = {
                if (!uiState.isUpdatingAvatar) {
                    showAvatarList = false
                }
            },
            isLoading = uiState.isUpdatingAvatar,
            onSave = { selectedAvatarId ->
                profileViewModel.updateUserAvatar(selectedAvatarId)
            }
        )
    }
}

@Composable
fun ProfileContent(
    name: String,
    email: String,
    avatarId: Int?,
    currentStreakDays: Int,
    weekDays: List<DayStreakUI>,
    nextStreakText: String,
    heatmapData: HeatmapUiData,
    onLogoutClick: () -> Unit,
    onEditAvatar: () -> Unit,
    onChangePassword: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))

            // --- SECCIÓN 1: TÍTULO ---
            Text(
                text = stringResource(id = R.string.my_profile),
                fontSize = 28.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            UserProfileCard(
                name = name,
                email = email,
                avatarId = avatarId,
                onClick = onEditAvatar
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        // --- SECCIÓN 2: RACHA SEMANAL ---
        item {
            WeeklyStreak(
                currentStreakDays = currentStreakDays,
                weekDays = weekDays,
                nextStreakText = nextStreakText,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        // --- SECCIÓN 3: MAPA DE CALOR DE ACTIVIDAD ANUAL ---
        item {
            ActivityHeatmapCard(
                heatmapData = heatmapData,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        // --- SECCIÓN 4: OPCIONES ---
        item {
            MultipleChoicesCard(
                text = stringResource(id = R.string.change_password),
                iconRes = R.drawable.ic_lock,
                isFirst = true,
                onClick = onChangePassword,
            )
        }

        item {
            MultipleChoicesCard(
                text = stringResource(id = R.string.logout_title),
                iconRes = R.drawable.ic_exit,
                disableArrow = true,
                isRed = true,
                isEnd = true,
                onClick = onLogoutClick,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "ProfileScreen - Default")
@Composable
private fun ProfileScreenPreview() {
    KipuCodeTheme {
        ProfileContent(
            name = "Pepito Gonzales",
            email = "pedro@upn.pe",
            avatarId = R.drawable.avatar_001,
            currentStreakDays = 6,
            weekDays = listOf(
                DayStreakUI("L", DayStatus.COMPLETED),
                DayStreakUI("M", DayStatus.COMPLETED),
                DayStreakUI("X", DayStatus.COMPLETED),
                DayStreakUI("J", DayStatus.COMPLETED),
                DayStreakUI("V", DayStatus.COMPLETED),
                DayStreakUI("S", DayStatus.COMPLETED),
                DayStreakUI("D", DayStatus.PENDING)
            ),
            nextStreakText = "A solo 1 día de superar la racha",
            heatmapData = HeatmapUiData(),
            onLogoutClick = {},
            onEditAvatar = {},
            onChangePassword = {}
        )
    }
}