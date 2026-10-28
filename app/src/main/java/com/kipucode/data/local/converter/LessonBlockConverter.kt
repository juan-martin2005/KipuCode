package com.kipucode.data.local.converter

import com.google.gson.JsonParser
import com.kipucode.domain.model.LessonBlock

object LessonBlockConverter {
    fun sanitizeHeading(title: String): String = title.replace(Regex("""^#{1,6}\s+"""), "").trim()

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
                                title = sanitizeHeading(obj.get("title")?.asString.orEmpty()),
                                objective = obj.get("objective")?.asString.orEmpty()
                            )
                        )
                    }
                    "concept" -> {
                        blocks.add(
                            LessonBlock.Concept(
                                title = sanitizeHeading(obj.get("title")?.asString.orEmpty()),
                                text = obj.get("text")?.asString.orEmpty()
                            )
                        )
                    }
                    "subconcept" -> {
                        blocks.add(
                            LessonBlock.Subconcept(
                                title = sanitizeHeading(obj.get("title")?.asString.orEmpty()),
                                text = obj.get("text")?.asString.orEmpty()
                            )
                        )
                    }
                    "code" -> {
                        blocks.add(
                            LessonBlock.Code(
                                language = obj.get("language")?.asString ?: "text",
                                code = obj.get("code")?.asString.orEmpty(),
                                output = obj.get("output")?.asString?.takeIf { it.isNotBlank() },
                                isDiagram = false
                            )
                        )
                    }
                    "diagram" -> {
                        blocks.add(
                            LessonBlock.Code(
                                language = obj.get("language")?.asString ?: "text",
                                code = obj.get("code")?.asString.orEmpty(),
                                output = obj.get("output")?.asString?.takeIf { it.isNotBlank() },
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
                    "fact" -> {
                        blocks.add(
                            LessonBlock.Fact(
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
