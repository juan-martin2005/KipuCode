package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kipucode.domain.model.LessonBlock
import com.kipucode.ui.screens.exercise.components.formats.FormattedCode
import com.kipucode.ui.theme.KipuCodeTheme

@Composable
fun LessonCodeBlock(
    block: LessonBlock.Code,
    modifier: Modifier = Modifier
) {
    FormattedCode(
        code = block.code,
        language = block.language,
        output = block.output,
        modifier = modifier,
        isDiagram = block.isDiagram
    )
}

@Preview(showBackground = true, name = "LessonCodeBlock - Variants")
@Composable
private fun LessonCodeBlockPreview() {
    KipuCodeTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LessonCodeBlock(
                block = LessonBlock.Code(
                    code = "dotnet new console -n HolaMundo\ncd HolaMundo\ndotnet run",
                    language = "terminal",
                    isDiagram = false
                )
            )
            LessonCodeBlock(
                block = LessonBlock.Code(
                    code = "// Program.cs\nConsole.WriteLine(\"¡Hola, C#!\");",
                    language = "csharp",
                    isDiagram = false
                )
            )
        }
    }
}
