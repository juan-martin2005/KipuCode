package com.kipucode.ui.screens.code

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kipucode.R
import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.ui.components.KipuBottomBar
import com.kipucode.ui.navigation.ExerciseRoute
import com.kipucode.ui.screens.code.components.CardExerciseReview
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.viewmodel.CodeUiState
import com.kipucode.viewmodel.CodeViewModel

@Composable
fun CodeScreen(
    navController: NavController,
    codeViewModel: CodeViewModel = hiltViewModel()
) {
    val uiState by codeViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { KipuBottomBar(navController = navController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { codeViewModel.swipeToRefresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CodeContent(
                uiState = uiState,
                onExerciseClick = { dueExercise ->
                    navController.navigate(
                        ExerciseRoute(
                            lessonId = dueExercise.lessonId,
                            type = dueExercise.exerciseType
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun CodeContent(
    uiState: CodeUiState,
    onExerciseClick: (DueExerciseDomain) -> Unit
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = KipuTeal)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Repaso Inteligente",
                fontSize = 28.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                color = KipuDarkBlue
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Ejercicios programados por el algoritmo FSRS para consolidar tu retención de memoria a largo plazo.",
                fontSize = 14.sp,
                fontFamily = Nunito,
                color = Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (uiState.dueExercises.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_correct),
                            contentDescription = null,
                            tint = KipuTeal,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "¡Estás al día!",
                            fontSize = 20.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            color = KipuDarkBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No tienes ejercicios pendientes de repaso por ahora. Completa más lecciones para activar nuevos ciclos de memoria.",
                            fontSize = 14.sp,
                            fontFamily = Nunito,
                            color = Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        } else {
            items(uiState.dueExercises, key = { it.exerciseId }) { item ->
                CardExerciseReview(
                    masteryPercentage = item.retentionPercentage,
                    stability = item.stability,
                    dueTime = item.dueDate,
                    lessonTitle = item.lessonTitle,
                    exerciseType = item.exerciseType,
                    onClick = { onExerciseClick(item) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CodeScreenEmptyPreview() {
    val mockNavController = rememberNavController()
    Scaffold(
        bottomBar = { KipuBottomBar(navController = mockNavController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CodeContent(
                uiState = CodeUiState(
                    isLoading = false,
                    dueExercises = emptyList()
                ),
                onExerciseClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CodeScreenWithExercisesPreview() {
    val mockNavController = rememberNavController()
    Scaffold(
        bottomBar = { KipuBottomBar(navController = mockNavController) },
        containerColor = BackgroundGray
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CodeContent(
                uiState = CodeUiState(
                    isLoading = false,
                    dueExercises = listOf(
                        DueExerciseDomain(
                            exerciseId = "ex_1",
                            lessonId = "lesson_1",
                            lessonTitle = "Historia y Filosofía de Java",
                            exerciseType = "FLASHCARD",
                            dueDate = System.currentTimeMillis() - 3600000L * 4,
                            retentionPercentage = 45,
                            stability = 1.8
                        ),
                        DueExerciseDomain(
                            exerciseId = "ex_2",
                            lessonId = "lesson_2",
                            lessonTitle = "Variables y Tipos de Datos",
                            exerciseType = "UNIQUE_CHOICE",
                            dueDate = System.currentTimeMillis() - 3600000L * 24,
                            retentionPercentage = 30,
                            stability = 0.5
                        )
                    )
                ),
                onExerciseClick = {}
            )
        }
    }
}