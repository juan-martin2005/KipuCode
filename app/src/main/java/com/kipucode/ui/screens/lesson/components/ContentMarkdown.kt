package com.kipucode.ui.screens.lesson.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
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

private val mdH1Style = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Black,
    fontSize = 28.sp,
    textAlign = TextAlign.Start,
    color = KipuTeal
)

private val mdH2Style = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 22.sp,
    textAlign = TextAlign.Start,
    color = KipuTeal
)

private val mdH3Style = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    textAlign = TextAlign.Justify,
    color = KipuDarkBlue
)

private val mdH4Style = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 22.sp,
    textAlign = TextAlign.Start,
    color = KipuDarkBlue
)

private val mdParagraphStyle = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    textAlign = TextAlign.Justify,
    color = KipuDarkBlue
)

private val mdInlineCodeStyle = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp,
    color = KipuDarkBlue
)

private val mdCodeStyle = TextStyle(
    fontFamily = JetBrains,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    color = KipuDarkBlue
)

private val mdQuoteStyle = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Normal,
    fontStyle = FontStyle.Italic,
    fontSize = 15.sp,
    color = KipuDarkBlue.copy(alpha = 0.85f)
)

@Composable
fun ContentMarkdown(
    modifier: Modifier = Modifier,
    content: String,
    color: Color? = null
){
    val markdown = remember(content) { content.trimIndent() }

    val highlightsBuilder = remember {
        Highlights.Builder().theme(SyntaxThemes.atom())
    }

    val mdColors = markdownColor(
        text = KipuDarkBlue,
        codeBackground = MoonFrost,
        inlineCodeBackground = color ?: MoonFrost
    )

    val mdH5Style = remember(color) {
        TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            color = color ?: KipuDarkBlue
        )
    }

    val mdTypography = markdownTypography(
        h1 = mdH1Style,
        h2 = mdH2Style,
        h3 = mdH3Style,
        h4 = mdH4Style,
        h5 = mdH5Style,
        paragraph = mdParagraphStyle,
        inlineCode = mdInlineCodeStyle,
        code = mdCodeStyle,
        quote = mdQuoteStyle
    )

    val mdPadding = markdownPadding(
        block = 4.dp,
        list = 3.dp
    )

    Markdown(
        content = markdown,
        modifier = modifier.fillMaxWidth(),
        padding = mdPadding,
        colors = mdColors,
        typography = mdTypography,
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

            > Objetivo de Aprendizaje
            > Comprender el origen de Python, asimilar la filosofía de su diseño y dimensionar su rol y capacidades en la industria de la ingeniería de software moderna.

            ## El Origen y el Propósito del Lenguaje

            > Un blockquote normal, estilizado con los colores y tipografía del tema.
        """.trimIndent()
    )
}