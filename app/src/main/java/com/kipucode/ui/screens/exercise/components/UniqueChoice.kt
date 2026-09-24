package com.kipucode.ui.screens.exercise.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.screens.lesson.components.ContentMarkdown
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

@Composable
fun UniqueChoice(
    current: Int,
    total: Int,
    instruction: String,
    options: List<BlockOptionDomain>,
    selectedOptionId: String?,
    onOptionSelected: (BlockOptionDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalProgressFactor = current.toFloat() / total.toFloat()
    val hasAnswered = selectedOptionId != null

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- CONTADOR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                modifier = Modifier.alignByBaseline(),
                text = current.toString(),
                color = lerp(KipuTeal, Color.Black, 0.1f),
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
            Text(
                modifier = Modifier.alignByBaseline().padding(horizontal = 2.dp),
                text = "/",
                color = KipuDarkBlue.copy(alpha = 0.5f),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier.alignByBaseline(),
                text = "$total",
                color = KipuDarkBlue.copy(alpha = 0.5f),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                modifier = Modifier.alignByBaseline(),
                text = "25% COMPLETADO",
                color = lerp(KipuTeal, Color.Black, 0.1f),
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        // --- BARRA DE PROGRESO ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(
                    KipuDarkBlue.copy(alpha = 0.1f),
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

        Spacer(modifier = Modifier.height(8.dp))

        // --- PREGUNTA (INSTRUCTION) ---
        ContentMarkdown(Modifier, instruction.trimIndent())


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
                current = 3,
                total = 10,
                instruction = """
                    #### ¿En qué año nació Java?
                    ```
        """.trimIndent(),
                options = listOf(
                    BlockOptionDomain(id = "1", exerciseId = "ex1", content = "##### console.log()", isCorrect = false),
                    BlockOptionDomain(id = "2", exerciseId = "ex1", content = "##### print()", isCorrect = true),
                    BlockOptionDomain(id = "3", exerciseId = "ex1", content = "##### echo", isCorrect = false),
                    BlockOptionDomain(id = "4", exerciseId = "ex1", content = "##### System.out.println()", isCorrect = false)
                ),
                selectedOptionId = selectedId,
                onOptionSelected = { option -> selectedId = option.id },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}