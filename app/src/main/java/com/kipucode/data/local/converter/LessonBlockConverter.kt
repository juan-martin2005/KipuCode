package com.kipucode.data.local.converter

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.kipucode.domain.model.LessonBlock

object LessonBlockConverter {
    private val gson = Gson()

    /**
     * Parsea un String de contenido. Si contiene un array JSON de bloques,
     * retorna la lista estructurada de [LessonBlock].
     * Si es texto Markdown clásico o inválido, retorna una lista vacía de forma segura.
     */
    fun parse(rawContent: String?): List<LessonBlock> {
        if (rawContent.isNullOrBlank()) return emptyList()

        val trimmed = rawContent.trim()
        if (!trimmed.startsWith("[")) return emptyList()

        return try {
            val jsonElement = JsonParser.parseString(trimmed)
            if (!jsonElement.isJsonArray) return emptyList()

            val array = jsonElement.asJsonArray
            val blocks = mutableListOf<LessonBlock>()

            for (element in array) {
                if (!element.isJsonObject) continue
                val obj = element.asJsonObject
                val type = obj.get("type")?.asString?.lowercase() ?: continue

                when (type) {
                    "header" -> {
                        blocks.add(
                            LessonBlock.Header(
                                title = obj.get("title")?.asString.orEmpty(),
                                objective = obj.get("objective")?.asString.orEmpty()
                            )
                        )
                    }
                    "concept" -> {
                        blocks.add(
                            LessonBlock.Concept(
                                title = obj.get("title")?.asString.orEmpty(),
                                text = obj.get("text")?.asString.orEmpty()
                            )
                        )
                    }
                    "code" -> {
                        blocks.add(
                            LessonBlock.Code(
                                language = obj.get("language")?.asString ?: "text",
                                code = obj.get("code")?.asString.orEmpty(),
                                isDiagram = false
                            )
                        )
                    }
                    "diagram" -> {
                        blocks.add(
                            LessonBlock.Code(
                                language = obj.get("language")?.asString ?: "text",
                                code = obj.get("code")?.asString.orEmpty(),
                                isDiagram = true
                            )
                        )
                    }
                    "cue", "qa" -> {
                        blocks.add(
                            LessonBlock.Cue(
                                question = obj.get("question")?.asString.orEmpty(),
                                answer = obj.get("answer")?.asString.orEmpty()
                            )
                        )
                    }
                    "summary" -> {
                        val items = obj.getAsJsonArray("items")
                            ?.mapNotNull { it.asString }
                            ?: emptyList()
                        blocks.add(LessonBlock.Summary(items = items))
                    }
                }
            }

            blocks
        } catch (_: Exception) {
            emptyList()
        }
    }
}
