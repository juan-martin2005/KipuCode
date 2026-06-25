package com.kipucode.ui.screens.code

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kipucode.R
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.Nunito

@Composable
fun CodeScreen(
    navController: NavController,
) {
    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            ComingSoonContent()
        }
    }
}

@Composable
private fun ComingSoonContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // --- ÍCONO PRINCIPAL ---
        Icon(
            painter = painterResource(id = R.drawable.ic_maintenance),
            contentDescription = null,
            tint = KipuTealDark,
            modifier = Modifier.size(86.dp)
        )


        // --- TÍTULO PRINCIPAL ---
        Text(
            text = "Próximamente!",
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
            lineHeight = 52.sp
        )

        // --- SUBTÍTULO DESTACADO ---
        Text(
            text = "Se vienen cositas",
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = KipuTealDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // --- DESCRIPCIÓN ---
        Text(
            text = "Trabajamos para DARTE\nlo mejor. ¡No te lo pierdas!",
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = Color(0xFF8A8A8A),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CodeScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray),
        contentAlignment = Alignment.Center
    ) {
        ComingSoonContent()
    }
}