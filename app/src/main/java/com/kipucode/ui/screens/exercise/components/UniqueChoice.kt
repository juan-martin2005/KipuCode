package com.kipucode.ui.screens.exercise.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.domain.model.BlockOptionDomain
import com.kipucode.ui.components.KipuTopBar
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
    val hasAnswered = selectedOptionId != null

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- BARRA DE PROGRESO REUTILIZABLE ---
        ExerciseProgressBar(
            current = current,
            total = total,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- PREGUNTA (INSTRUCTION) ---
        ExerciseInstruction(
            instruction = instruction.trimIndent(),
            modifier = Modifier.fillMaxWidth()
        )


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

@Preview(showBackground = true, name = "Multiple Choice Exercise Preview")
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
                instruction = "Tras ejecutar `dotnet new console`, ¿cuál es el archivo de entrada generado por defecto?",
                options = listOf(
                    BlockOptionDomain(id = "1", exerciseId = "ex1", content = "App.config", isCorrect = false),
                    BlockOptionDomain(id = "2", exerciseId = "ex1", content = "Program.cs", isCorrect = true),
                    BlockOptionDomain(id = "3", exerciseId = "ex1", content = "Main.java", isCorrect = false),
                    BlockOptionDomain(id = "4", exerciseId = "ex1", content = "Startup.cs", isCorrect = false)
                ),
                selectedOptionId = selectedId,
                onOptionSelected = { option -> selectedId = option.id },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}