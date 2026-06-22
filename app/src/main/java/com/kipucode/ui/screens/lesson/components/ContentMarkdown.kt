package com.kipucode.ui.screens.lesson.components

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
fun ContentMarkdown(content: String){
    val markdown = content.trimIndent()

    val highlightsBuilder = remember {
        Highlights.Builder().theme(SyntaxThemes.atom())
    }

    Markdown(
        content = markdown,
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
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
        content = """
            # 01 - Historia, Visión y Ecosistema de Python
            
            ---
            
            > **Objetivo de Aprendizaje:** > Comprender el origen de Python, asimilar la filosofía de su diseño y dimensionar su rol y capacidades en la industria de la ingeniería de software moderna.

            ## El Origen y el Propósito del Lenguaje     
            
            ---
            
            Python fue creado a principios de la década de 1990 por **Guido van Rossum**. A diferencia de otros lenguajes de la época que priorizaban la optimización extrema para la máquina, Python fue diseñado con una visión disruptiva: **optimizar el tiempo del desarrollador**.
            
            El lenguaje se construyó bajo la premisa de que el código se lee muchas más veces de las que se escribe. Por lo tanto, su sintaxis elimina elementos superfluos (como llaves o puntos y comas) y utiliza la **indentación obligatoria** para definir bloques de código, forzando un estilo visualmente limpio.
            
            ### El "Zen de Python"
            
            La filosofía central del lenguaje está documentada en un manifiesto conocido como el "Zen de Python" (accesible ejecutando `import this` en el intérprete). Algunos de sus principios arquitectónicos más importantes son:
            
            - **Explícito es mejor que implícito:** El código no debe esconder "magia" o comportamientos oscuros.
                
            - **Simple es mejor que complejo:** Si un problema puede resolverse con una estructura básica, no se debe sobreingenierizar.
                
            - **La legibilidad cuenta:** Un código mantenible es aquel que otro ingeniero puede entender sin esfuerzo.
                
            
            ## Lenguajes Formales vs. Lenguajes Naturales
            
            Para pensar como un programador, es vital entender la diferencia entre los lenguajes que hablamos y los que la computadora procesa.
            
            - **Lenguajes Naturales:** Como el español o el inglés. Están llenos de ambigüedades, modismos y dependen fuertemente del contexto.
                
            - **Lenguajes Formales:** Como las matemáticas o Python. Son diseñados para aplicaciones específicas, poseen reglas de sintaxis estrictas y carecen por completo de ambigüedad. Una instrucción en Python significa exactamente una cosa, sin interpretaciones alternativas.
                
            
            ## El Ecosistema Actual y Casos de Uso
            
            Actualmente, Python no es solo un lenguaje para aprender a programar; es una herramienta de grado industrial. Sus aplicaciones abarcan:
            
            - **Automatización y Scripting:** Tareas repetitivas, administración de sistemas y extracción de datos web (Web Scraping).
                
            - **Desarrollo Backend:** Sistemas de alta concurrencia y APIs utilizando frameworks como Django o FastAPI (utilizado por plataformas masivas como Instagram).
                
            - **Data Science y Machine Learning:** Es el estándar de facto para el análisis de datos, entrenamiento de modelos de inteligencia artificial y computación científica. 
        """.trimIndent()
    )
}