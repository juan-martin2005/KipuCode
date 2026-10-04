package com.kipucode.ui.screens.code.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeOptionsBottomSheet(
    lessonTitle: String,
    exerciseType: String,
    totalCount: Int,
    dueCount: Int,
    onDismiss: () -> Unit,
    onSelectOption: (onlyDue: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White,
        scrimColor = Color.Black.copy(alpha = 0.35f),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        PracticeOptionsContent(
            lessonTitle = lessonTitle,
            exerciseType = exerciseType,
            totalCount = totalCount,
            dueCount = dueCount,
            onSelectOption = onSelectOption
        )
    }
}

@Composable
fun PracticeOptionsContent(
    lessonTitle: String,
    exerciseType: String,
    totalCount: Int,
    dueCount: Int,
    onSelectOption: (onlyDue: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isFlashcard = exerciseType.equals("FLASHCARD", ignoreCase = true)
    val typeTitle = if (isFlashcard) "Tarjetas de Memoria" else "Preguntas de Opción Múltiple"
    val typeIcon = if (isFlashcard) R.drawable.ic_flash_card else R.drawable.ic_quiz

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(KipuTeal.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = typeIcon),
                    contentDescription = null,
                    tint = KipuTeal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = typeTitle,
                    fontFamily = Nunito,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KipuDarkBlue
                )
                Text(
                    text = lessonTitle,
                    fontFamily = Nunito,
                    fontSize = 13.sp,
                    color = Gray,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Selecciona el modo de práctica",
            fontFamily = Nunito,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // OPCIÓN 1: Repasar todos los ejercicios (Reutiliza FilledButton)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Repasar todos los ejercicios",
                    fontFamily = Nunito,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = KipuDarkBlue
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Practica la totalidad de preguntas ($totalCount ejercicios disponibles)",
                    fontFamily = Nunito,
                    fontSize = 12.sp,
                    color = Gray
                )
                Spacer(modifier = Modifier.height(12.dp))
                FilledButton(
                    textButton = "Practicar todo ($totalCount)",
                    onClickFilledButton = { onSelectOption(false) },
                    containerColor = KipuTeal,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // OPCIÓN 2: Repaso Inteligente FSRS (Reutiliza FilledButton)
        if (dueCount > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F1)),
                border = BorderStroke(1.5.dp, Color(0xFFFFB74D))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_fire),
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Repaso Inteligente FSRS",
                            fontFamily = Nunito,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$dueCount ejercicios con retención menor al 90% listos para consolidar.",
                        fontFamily = Nunito,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    FilledButton(
                        textButton = "Repasar pendientes ($dueCount)",
                        onClickFilledButton = { onSelectOption(true) },
                        containerColor = Color(0xFFE65100),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = BorderStroke(1.dp, Color(0xFFC8E6C9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_correct),
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¡Estás al día con tus repasos!",
                            fontFamily = Nunito,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tienes la mente fresca con la información. No requieres repaso prioritario en este momento.",
                            fontFamily = Nunito,
                            fontSize = 12.sp,
                            color = Color(0xFF388E3C),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Modal con Ejercicios Pendientes")
@Composable
fun PracticeOptionsContentPendingPreview() {
    PracticeOptionsContent(
        lessonTitle = "Lección 1: Historia y Filosofía de Java",
        exerciseType = "FLASHCARD",
        totalCount = 10,
        dueCount = 3,
        onSelectOption = {}
    )
}

@Preview(showBackground = true, name = "Modal Al Día")
@Composable
fun PracticeOptionsContentUpToDatePreview() {
    PracticeOptionsContent(
        lessonTitle = "Lección 2: Variables y Tipos Primitivos",
        exerciseType = "UNIQUE_CHOICE",
        totalCount = 8,
        dueCount = 0,
        onSelectOption = {}
    )
}
