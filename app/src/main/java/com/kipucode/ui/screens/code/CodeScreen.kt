package com.kipucode.ui.screens.code

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.navigation.ExerciseRoute
import com.kipucode.ui.screens.code.components.PracticeCodeSelectorContent
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
            CodeContent(
                selectedMethod = uiState.selectedMethod,
                completeCodeCount = uiState.availability.completeCodeCount,
                whatsOutputCount = uiState.availability.whatsOutputCount,
                onMethodSelected = { codeViewModel.selectMethod(it) },
                onStartPractice = { method ->
                    navController.navigate(
                        ExerciseRoute(
                            lessonId = "csharp_lesson_01",
                            type = method.name
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun CodeContent(
    selectedMethod: PracticeMethod,
    completeCodeCount: Int,
    whatsOutputCount: Int,
    onMethodSelected: (PracticeMethod) -> Unit,
    onStartPractice: (PracticeMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelectedAvailable = when (selectedMethod) {
        PracticeMethod.COMPLETE_CODE -> completeCodeCount > 0
        PracticeMethod.WHATS_OUTPUT -> whatsOutputCount > 0
        else -> false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
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
            text = "Elige una modalidad y pon a prueba tu lógica.",
            fontSize = 15.sp,
            fontFamily = Nunito,
            color = Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Selector con tarjetas interactivas y estado de bloqueo
        PracticeCodeSelectorContent(
            selectedMethod = selectedMethod,
            completeCodeCount = completeCodeCount,
            whatsOutputCount = whatsOutputCount,
            onMethodSelected = onMethodSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilledButton(
            textButton = if (isSelectedAvailable) "Comenzar práctica" else "Modo bloqueado",
            onClickFilledButton = {
                if (isSelectedAvailable) onStartPractice(selectedMethod)
            },
            enabled = isSelectedAvailable,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
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
            CodeContent(
                selectedMethod = PracticeMethod.COMPLETE_CODE,
                completeCodeCount = 1,
                whatsOutputCount = 0,
                onMethodSelected = {},
                onStartPractice = {}
            )
        }
    }
}