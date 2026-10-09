package com.kipucode.ui.screens.code

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CodeViewModel

@Composable
fun CodeScreen(
    navController: NavController,
    codeViewModel: CodeViewModel = hiltViewModel()
) {
    val uiState by codeViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = Color.White
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { codeViewModel.swipeToRefresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CodeContent()
        }
    }
}

@Composable
fun CodeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Tu espacio de práctica",
            fontSize = 28.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            color = KipuDarkBlue
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Elige una lección y empieza.",
            fontSize = 15.sp,
            fontFamily = Nunito,
            color = Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CodeScreenPreview() {
    val mockNavController = rememberNavController()
    Scaffold(
        bottomBar = { KipuBottomBar(navController = mockNavController) },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CodeContent()
        }
    }
}