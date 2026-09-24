package com.kipucode.ui.screens.lesson.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
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
import org.intellij.markdown.ast.getTextInNode

@Composable
fun ContentMarkdown(
    modifier: Modifier = Modifier,
    content: String,
    color: Color? = null
){
    val markdown = content.trimIndent()

    val highlightsBuilder = remember {
        Highlights.Builder().theme(SyntaxThemes.atom())
    }

    val mdColors = markdownColor(
        text = KipuDarkBlue,
        codeBackground = MoonFrost,
        inlineCodeBackground = color ?: MoonFrost
    )

    val mdTypography = markdownTypography(
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
        h5 = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
            color = color ?: KipuDarkBlue
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
        ),
        quote = TextStyle(
            fontFamily = Nunito,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Italic,
            fontSize = 15.sp,
            color = KipuDarkBlue.copy(alpha = 0.85f)
        )
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
            },

            blockQuote = { model ->
                val raw = model.node.getTextInNode(model.content).toString()
                val callout = parseCallout(raw)
                val style = calloutStyles[callout.type] ?: calloutStyles.getValue("note")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            color = style.color.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = style.color.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (callout.hasHeader) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = style.icon),
                                contentDescription = null,
                                tint = style.color,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = callout.title ?: style.defaultTitle,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = style.color
                            )
                        }
                    }

                    if (callout.body.isNotBlank()) {
                        Markdown(
                            content = callout.body,
                            modifier = Modifier.fillMaxWidth(),
                            padding = mdPadding,
                            colors = mdColors,
                            typography = mdTypography
                        )
                    }
                }
            }
        )
    )
}


// Callouts
private data class CalloutStyle(
    val color: Color,
    @param:DrawableRes val icon: Int,
    val defaultTitle: String
)


private val calloutStyles: Map<String, CalloutStyle> = buildMap {
    fun add(style: CalloutStyle, vararg aliases: String) =
        aliases.forEach { put(it, style) }

    add(CalloutStyle(Color(0xFF086DDD), R.drawable.ic_note, "Nota"), "note")
    add(CalloutStyle(Color(0xFF00BFBC), R.drawable.ic_resume, "Resumen"), "abstract", "summary", "tldr")
    add(CalloutStyle(Color(0xFF086DDD), R.drawable.ic_info, "Info"), "info")
    add(CalloutStyle(Color(0xFFE0AC00), R.drawable.ic_question, "Pregunta"), "question", "help", "faq")
    add(CalloutStyle(Color(0xFFEC7500), R.drawable.ic_warning, "Advertencia"), "warning", "caution", "attention")
}

private val calloutHeader = Regex("""^\[!(\w+)]([+-])?[ \t]*(.*)$""")

private data class ParsedCallout(
    val type: String,
    val title: String?,
    val body: String,
    val hasHeader: Boolean
)

private fun parseCallout(raw: String): ParsedCallout {
    // Quita el ">" inicial de cada línea
    val lines = raw.lines().map { it.trimStart().removePrefix(">").removePrefix(" ") }
    val match = calloutHeader.find(lines.first().trim())

    return if (match != null) {
        val (type, _, title) = match.destructured
        ParsedCallout(
            type = type.lowercase(),
            title = title.ifBlank { null },
            body = lines.drop(1).joinToString("\n").trim(),
            hasHeader = true
        )
    } else {
        ParsedCallout(
            type = "quote",
            title = null,
            body = lines.joinToString("\n").trim(),
            hasHeader = false
        )
    }
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

            > [!abstract] Objetivo de Aprendizaje
            > Comprender el origen de Python, asimilar la filosofía de su diseño y dimensionar su rol y capacidades en la industria de la ingeniería de software moderna.

            ## El Origen y el Propósito del Lenguaje

            > [!note] Nota
            > Ejemplo de una **nota** con `código inline`.

            > [!warning]
            > Sin título: usa el título por defecto.

            > Un blockquote normal, sin tipo.
        """.trimIndent()
    )
}