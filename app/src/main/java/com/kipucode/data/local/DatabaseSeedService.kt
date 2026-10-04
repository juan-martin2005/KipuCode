package com.kipucode.data.local

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.kipucode.data.local.converter.ModuleJsonDto
import com.kipucode.data.local.converter.toEntity
import com.kipucode.data.local.dao.BlockOptionDao
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.local.model.BlockOptionEntity
import com.kipucode.data.local.model.CourseEntity
import com.kipucode.data.local.model.ExerciseEntity
import com.kipucode.data.local.model.LessonEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeedService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val courseDao: CourseDao,
    private val lessonDao: LessonDao,
    private val exerciseDao: ExerciseDao,
    private val blockOptionDao: BlockOptionDao
) {
    companion object {
        private const val TAG = "DatabaseSeedService"
        private const val DATA_ROOT = "data"
        private val TRACKS = listOf("c_sharp")
    }

    suspend fun seedIfNeeded() {
        try {
            val gson = Gson()

            // Carga modular jerárquica por lenguajes/tracks
            for (track in TRACKS) {
                val coursesInTrack = courseDao.getCoursesCountByTrack(track)
                if (coursesInTrack > 0) {
                    Log.d(TAG, "Track '$track' ya poblado ($coursesInTrack cursos en SQLite). Omitiendo.")
                    continue
                }

                val folderPath = "$DATA_ROOT/$track"
                val files = try {
                    context.assets.list(folderPath) ?: emptyArray()
                } catch (e: Exception) {
                    emptyArray()
                }

                val jsonFiles = files.filter { it.endsWith(".json") }.sorted()
                if (jsonFiles.isEmpty()) continue

                val coursesToInsert = mutableListOf<CourseEntity>()
                val lessonsToInsert = mutableListOf<LessonEntity>()
                val exercisesToInsert = mutableListOf<ExerciseEntity>()
                val optionsToInsert = mutableListOf<BlockOptionEntity>()

                for (fileName in jsonFiles) {
                    val filePath = "$folderPath/$fileName"
                    Log.d(TAG, "Cargando módulo jerárquico desde: $filePath")
                    val jsonString = context.assets.open(filePath).bufferedReader().use { it.readText() }
                    val moduleDto = gson.fromJson(jsonString, ModuleJsonDto::class.java) ?: continue

                    coursesToInsert.add(moduleDto.course.toEntity())

                    moduleDto.lessons.forEach { lessonDto ->
                        lessonsToInsert.add(lessonDto.toEntity(moduleDto.course.id))

                        lessonDto.exercises.forEach { exerciseDto ->
                            exercisesToInsert.add(exerciseDto.toEntity(lessonDto.id))

                            exerciseDto.options.forEachIndexed { optIndex, optDto ->
                                val optId = "${exerciseDto.id}_opt_${optIndex + 1}"
                                optionsToInsert.add(optDto.toEntity(optId, exerciseDto.id, optIndex + 1))
                            }
                        }
                    }
                }

                if (coursesToInsert.isNotEmpty()) {
                    courseDao.insertCourses(coursesToInsert)
                    lessonDao.insertAll(lessonsToInsert)
                    exerciseDao.insertAll(exercisesToInsert)
                    blockOptionDao.insertAll(optionsToInsert)

                    Log.d(TAG, "Track '$track' pre-poblado exitosamente: " +
                            "${coursesToInsert.size} cursos, " +
                            "${lessonsToInsert.size} lecciones, " +
                            "${exercisesToInsert.size} ejercicios, " +
                            "${optionsToInsert.size} opciones.")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al pre-poblar la base de datos desde assets: ${e.message}", e)
        }
    }
}