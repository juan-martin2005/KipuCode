package com.kipucode.data.local.converter

import com.google.gson.annotations.SerializedName
import com.kipucode.data.local.model.BlockOptionEntity
import com.kipucode.data.local.model.CourseEntity
import com.kipucode.data.local.model.ExerciseEntity
import com.kipucode.data.local.model.LessonEntity

// ============================================================================================
//  DTOs JERÁRQUICOS PARA DESERIALIZACIÓN DE MÓDULOS DESDE ASSETS (JSON)
// ============================================================================================

data class ModuleJsonDto(
    @SerializedName("version") val version: Int = 1,
    @SerializedName("course") val course: CourseJsonDto,
    @SerializedName("lessons") val lessons: List<LessonJsonDto> = emptyList()
)

data class CourseJsonDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("track") val track: String = "",
    @SerializedName("orderIndex") val orderIndex: Int = 1,
    @SerializedName("xp") val xp: Int = 0
)

data class LessonJsonDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("content") val content: com.google.gson.JsonElement? = null,
    @SerializedName("orderIndex") val orderIndex: Int = 1,
    @SerializedName("xp") val xp: Int = 200,
    @SerializedName("exercises") val exercises: List<ExerciseJsonDto> = emptyList()
)

data class ExerciseJsonDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("instruction") val instruction: String = "",
    @SerializedName("answer") val answer: String = "",
    @SerializedName("orderIndex") val orderIndex: Int = 1,
    @SerializedName("xp") val xp: Int = 20,
    @SerializedName("options") val options: List<OptionJsonDto> = emptyList()
)

data class OptionJsonDto(
    @SerializedName("content") val content: String = "",
    @SerializedName("explanation") val explanation: String = "",
    @SerializedName("isCorrect") val isCorrect: Boolean = false,
    @SerializedName("orderIndex") val orderIndex: Int? = null
)

// ============================================================================================
//  MAPPERS A ENTIDADES DE ROOM DATABASE
// ============================================================================================

fun CourseJsonDto.toEntity(): CourseEntity = CourseEntity(
    id = this.id,
    title = this.title,
    description = this.description,
    track = this.track,
    orderIndex = this.orderIndex,
    xp = this.xp
)

fun LessonJsonDto.toEntity(courseId: String): LessonEntity {
    val contentString = when {
        this.content == null -> ""
        this.content.isJsonPrimitive && this.content.asJsonPrimitive.isString -> this.content.asString
        else -> this.content.toString()
    }
    return LessonEntity(
        id = this.id,
        courseId = courseId,
        title = this.title,
        content = contentString,
        orderIndex = this.orderIndex,
        xp = this.xp
    )
}


fun ExerciseJsonDto.toEntity(lessonId: String): ExerciseEntity = ExerciseEntity(
    id = this.id,
    lessonId = lessonId,
    type = this.type,
    instruction = this.instruction,
    answer = this.answer,
    orderIndex = this.orderIndex,
    xp = this.xp
)

fun OptionJsonDto.toEntity(id: String, exerciseId: String, fallbackOrderIndex: Int): BlockOptionEntity = BlockOptionEntity(
    id = id,
    exerciseId = exerciseId,
    content = this.content,
    explanation = this.explanation,
    isCorrect = this.isCorrect,
    orderIndex = this.orderIndex ?: fallbackOrderIndex
)
