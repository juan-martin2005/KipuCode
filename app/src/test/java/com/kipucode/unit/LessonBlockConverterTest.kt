package com.kipucode.unit

import com.kipucode.data.local.converter.LessonBlockConverter
import com.kipucode.domain.model.LessonBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LessonBlockConverterTest {

    @Test
    fun sanitizeHeading_removesMarkdownHashesCorrectly() {
        // Casos con almohadillas Markdown (H1, H2, H3, H4)
        assertEquals("1.X Título con `código`", LessonBlockConverter.sanitizeHeading("## 1.X Título con `código`"))
        assertEquals("2.1 Enteros con signo: `int` y `long`", LessonBlockConverter.sanitizeHeading("### 2.1 Enteros con signo: `int` y `long`"))
        assertEquals("01 - Plataforma .NET", LessonBlockConverter.sanitizeHeading("# 01 - Plataforma .NET"))
        assertEquals("Sección profunda", LessonBlockConverter.sanitizeHeading("#### Sección profunda"))

        // Casos que deben preservarse intactos (como C#, F# o sin hashes)
        assertEquals("1.1 C# (estilo basado en llaves)", LessonBlockConverter.sanitizeHeading("1.1 C# (estilo basado en llaves)"))
        assertEquals("1.2 F# (estilo funcional)", LessonBlockConverter.sanitizeHeading("1.2 F# (estilo funcional)"))
        assertEquals("Sintaxis en C# y .NET", LessonBlockConverter.sanitizeHeading("Sintaxis en C# y .NET"))
        assertEquals("Título simple sin almohadillas", LessonBlockConverter.sanitizeHeading("Título simple sin almohadillas"))
    }

    @Test
    fun parse_deserializesBlocksAndSanitizesHeadings() {
        val json = """
            [
                {
                    "type": "header",
                    "title": "# 01 - Plataforma .NET e instalar SDK",
                    "objective": "Comprender la plataforma .NET"
                },
                {
                    "type": "concept",
                    "title": "## 1. Tipos de datos primitivos en `C#`",
                    "text": "C# incluye tipos como `int`, `long` y `bool`."
                },
                {
                    "type": "subconcept",
                    "title": "### 1.1 Enteros con signo: `int` y `long`",
                    "text": "Los enteros de 32 bits usan `int`."
                }
            ]
        """.trimIndent()

        val blocks = LessonBlockConverter.parse(json)

        assertEquals(3, blocks.size)

        val header = blocks[0] as LessonBlock.Header
        assertEquals("01 - Plataforma .NET e instalar SDK", header.title)
        assertEquals("Comprender la plataforma .NET", header.objective)

        val concept = blocks[1] as LessonBlock.Concept
        assertEquals("1. Tipos de datos primitivos en `C#`", concept.title)
        assertTrue(concept.title.contains("`C#`"))

        val subconcept = blocks[2] as LessonBlock.Subconcept
        assertEquals("1.1 Enteros con signo: `int` y `long`", subconcept.title)
        assertTrue(subconcept.title.contains("`int`"))
        assertTrue(subconcept.title.contains("`long`"))
    }

    @Test
    fun parse_returnsEmptyListForInvalidOrEmptyContent() {
        assertTrue(LessonBlockConverter.parse(null).isEmpty())
        assertTrue(LessonBlockConverter.parse("").isEmpty())
        assertTrue(LessonBlockConverter.parse("   ").isEmpty())
        assertTrue(LessonBlockConverter.parse("texto no json").isEmpty())
        assertTrue(LessonBlockConverter.parse("{}").isEmpty())
    }
}
