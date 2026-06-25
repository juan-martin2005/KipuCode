package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.KipuTealDark
import com.kipucode.ui.theme.MoonFrost

@Composable
fun UniqueChoice(
    currentEx: Int,
    totalEx: Int,
    instruction: String,
    options: List<BlockOptionDomain>,
    selectedOptionId: String?,
    onOptionSelected: (BlockOptionDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalProgressFactor = currentEx.toFloat() / totalEx.toFloat()
    val hasAnswered = selectedOptionId != null

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- BARRA DE PROGRESO ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    MoonFrost.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(horizontalProgressFactor)
                    .height(10.dp)
                    .background(
                        KipuTeal,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }


        // --- CONTADOR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentEx.toString(),
                    color = KipuTealDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = " / $totalEx",
                    color = KipuDarkBlue.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- PREGUNTA (INSTRUCTION) ---
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = instruction,
                fontSize = 24.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                color = KipuDarkBlue,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- LISTA DE OPCIONES ---
        options.forEach { option ->
            OptionCard(
                text = option.content,
                isSelected = selectedOptionId == option.id,
                isCorrect = option.isCorrect,
                showResult = hasAnswered,
                onClick = {
                    if (!hasAnswered) {
                        onOptionSelected(option)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Multiple Choice Exercise")
@Composable
fun MultipleChoiceExercisePreview() {
    var selectedId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        KipuTopBar(
            title = "",
            onBackClick = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            UniqueChoice(
                currentEx = 3,
                totalEx = 10,
                instruction = "¿Qué comando utilizas en Python para mostrar texto en la consola?",
                options = listOf(
                    BlockOptionDomain(id = "1", exerciseId = "ex1", content = "console.log()", isCorrect = false),
                    BlockOptionDomain(id = "2", exerciseId = "ex1", content = "print()", isCorrect = true),
                    BlockOptionDomain(id = "3", exerciseId = "ex1", content = "echo", isCorrect = false),
                    BlockOptionDomain(id = "4", exerciseId = "ex1", content = "System.out.println()", isCorrect = false)
                ),
                selectedOptionId = selectedId,
                onOptionSelected = { option -> selectedId = option.id },
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}