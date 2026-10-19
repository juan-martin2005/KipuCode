package com.kipucode.domain.model

sealed interface LessonBlock {
    data class Header(
        val title: String,
        val objective: String
    ) : LessonBlock

    data class Concept(
        val title: String,
        val text: String
    ) : LessonBlock

    data class Subconcept(
        val title: String,
        val text: String = ""
    ) : LessonBlock

    data class Code(
        val language: String = "text",
        val code: String,
        val output: String? = null,
        val isDiagram: Boolean = false
    ) : LessonBlock

    data class Cue(
        val question: String,
        val answer: String
    ) : LessonBlock
    data class Fact(
        val question: String,
        val answer: String
    ) : LessonBlock

    data class Summary(
        val items: List<String>
    ) : LessonBlock
}
