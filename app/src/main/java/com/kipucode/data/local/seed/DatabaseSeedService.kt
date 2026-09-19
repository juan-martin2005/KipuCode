package com.kipucode.data.local.seed

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
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
        private const val SEED_FILE = "data/initial_data.json"
        private const val TAG = "DatabaseSeedService"
    }

    private data class InitialData(
        @SerializedName("courses") val courses: List<CourseEntity> = emptyList(),
        @SerializedName("lessons") val lessons: List<LessonEntity> = emptyList(),
        @SerializedName("exercises") val exercises: List<ExerciseEntity> = emptyList(),
        @SerializedName("blocksOptions") val blocksOptions: List<BlockOptionEntity> = emptyList()
    )

    suspend fun seedIfNeeded() {
        try {
            val coursesCount = courseDao.getCoursesCount()
            val lessonsCount = lessonDao.getLessonsCount()
            if (coursesCount > 0 && lessonsCount > 0) {
                Log.d(TAG, "La base de datos local ya contiene cursos ($coursesCount) y lecciones ($lessonsCount).")
                return
            }

            Log.d(TAG, "Iniciando carga inicial desde $SEED_FILE...")
            val jsonString = context.assets.open(SEED_FILE).bufferedReader().use { it.readText() }
            val gson = Gson()
            val initialData = gson.fromJson(jsonString, InitialData::class.java)

            if (initialData != null) {
                courseDao.insertCourses(initialData.courses)
                lessonDao.insertAll(initialData.lessons)
                exerciseDao.insertAll(initialData.exercises)
                blockOptionDao.insertAll(initialData.blocksOptions)

                Log.d(TAG, "Pre-poblado exitoso: ${initialData.courses.size} cursos, " +
                        "${initialData.lessons.size} lecciones, " +
                        "${initialData.exercises.size} ejercicios, " +
                        "${initialData.blocksOptions.size} opciones.")}
        } catch (e: Exception) {
            Log.e(TAG, "Error al pre-poblar la base de datos desde assets: ${e.message}", e)
        }
    }
}
