package com.kipucode.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.kipucode.R
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.components.card.KipuDialog
import com.kipucode.ui.components.card.MultipleChoicesCard
import com.kipucode.ui.components.card.UserProfileCard
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    navController: NavController,
    onLogoutClick: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val userProfile by userViewModel.userProfileState.collectAsStateWithLifecycle()

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
                name = userProfile?.name ?: "",
                email = userProfile?.email ?: "",
                onLogoutClick = {
                    showLogoutDialog = true
                }
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
                onLogoutClick()
            },
            iconTint = KipuTeal
        )
    }
}

@Composable
fun ProfileContent(
    name: String,
    email: String,
    onLogoutClick: () -> Unit
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
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            MultipleChoicesCard(
                text = stringResource(id = R.string.logout_title),
                iconRes = R.drawable.ic_exit,
                disableArrow = true,
                isRed = true,
                onClick = onLogoutClick
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileContent(
        name = "Pepeito Gonzles",
        email = "pedro@upn.pe",
        onLogoutClick = {}
    )
}