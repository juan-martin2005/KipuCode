package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.ui.theme.JetBrains
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownPadding
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes

@Composable
fun ContentMarkdown(
    modifier: Modifier = Modifier,
    content: String
){
    val markdown = content.trimIndent()

    val highlightsBuilder = remember {
        Highlights.Builder().theme(SyntaxThemes.atom())
    }

    Markdown(
        content = markdown,
        modifier = modifier,
        padding = markdownPadding(
            block = 4.dp,
            list = 3.dp
        ),
        colors = markdownColor(
            text = KipuDarkBlue,
            codeBackground = MoonFrost,
            inlineCodeBackground = MoonFrost
        ),
        typography = markdownTypography(
            h1 = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                textAlign = TextAlign.Start,
                color = KipuTeal
            ),
            h2 = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                textAlign = TextAlign.Start,
                color = KipuTeal
            ),
            h3 = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = KipuDarkBlue
            ),
            h4 = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                textAlign = TextAlign.Start,
                color = KipuDarkBlue
            ),
            paragraph = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                color = KipuDarkBlue
            ),
            inlineCode = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = KipuDarkBlue
            ),
            code = TextStyle(
                fontFamily = JetBrains,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = KipuDarkBlue
            )
        ),
        components = markdownComponents(
            codeFence = { model ->
                MarkdownHighlightedCodeFence(
                    content = model.content,
                    node = model.node,
                    highlightsBuilder = highlightsBuilder,
                    showHeader = true
                )
            },
            codeBlock = { model ->
                MarkdownHighlightedCodeBlock(
                    content = model.content,
                    node = model.node,
                    highlightsBuilder = highlightsBuilder,
                    showHeader = true
                )
            }
        )
    )
}


@Preview(
    name = "Vista Previa de Lección (Celular)",
    showBackground = true
)
@Composable
fun PreviewMarkdown() {
    ContentMarkdown(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 24.dp),
        content = """
            # 01 - Historia, Visión y Ecosistema de Python
            
            ---
            
            > **Objetivo de Aprendizaje:** > Comprender el origen de Python, asimilar la filosofía de su diseño y dimensionar su rol y capacidades en la industria de la ingeniería de software moderna.

            ## El Origen y el Propósito del Lenguaje       
        """.trimIndent()
    )
}