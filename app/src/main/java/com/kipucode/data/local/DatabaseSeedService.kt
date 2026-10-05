package com.kipucode.data.local

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.room.withTransaction
import com.google.gson.Gson
import com.kipucode.data.local.converter.ModuleJsonDto
import com.kipucode.data.local.converter.toEntity
import com.kipucode.data.local.dao.BlockOptionDao
import com.kipucode.data.local.dao.CourseDao
import com.kipucode.data.local.dao.ExerciseDao
import com.kipucode.data.local.dao.LessonDao
import com.kipucode.data.local.database.AppDatabase
import com.kipucode.data.local.model.BlockOptionEntity
import com.kipucode.data.local.model.CourseEntity
import com.kipucode.data.local.model.ExerciseEntity
import com.kipucode.data.local.model.LessonEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeedService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val appDatabase: AppDatabase,
    private val courseDao: CourseDao,
    private val lessonDao: LessonDao,
    private val exerciseDao: ExerciseDao,
    private val blockOptionDao: BlockOptionDao
) {
    companion object {
        private const val TAG = "DatabaseSeedService"
        private const val DATA_ROOT = "data"
        private const val PREFS_NAME = "kipu_content_version_prefs"
        private const val KEY_VERSION_PREFIX = "installed_version_"
        private val DEFAULT_TRACKS = listOf("c_sharp", "java")
    }

    private val seedMutex = Mutex()

    suspend fun seedIfNeeded() = seedMutex.withLock {
        try {
            val gson = Gson()
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            // Detección dinámica de carpetas de tracks en assets/data
            val assetTracks = try {
                context.assets.list(DATA_ROOT)?.filter { !it.contains(".") } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            val tracks = (assetTracks + DEFAULT_TRACKS).distinct()

            for (track in tracks) {
                val folderPath = "$DATA_ROOT/$track"
                val files = try {
                    context.assets.list(folderPath) ?: emptyArray()
                } catch (e: Exception) {
                    emptyArray()
                }

                val jsonFiles = files.filter { it.endsWith(".json") }.sorted()
                if (jsonFiles.isEmpty()) continue

                val coursesInTrack = courseDao.getCoursesCountByTrack(track)
                val isTrackEmpty = coursesInTrack == 0

                val coursesToInsert = mutableListOf<CourseEntity>()
                val lessonsToInsert = mutableListOf<LessonEntity>()
                val exercisesToInsert = mutableListOf<ExerciseEntity>()
                val optionsToInsert = mutableListOf<BlockOptionEntity>()
                val versionsToPersist = mutableMapOf<String, Int>()

                for (fileName in jsonFiles) {
                    val filePath = "$folderPath/$fileName"
                    val jsonString = try {
                        context.assets.open(filePath).bufferedReader().use { it.readText() }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al leer archivo de módulo: $filePath", e)
                        continue
                    }

                    val moduleDto = gson.fromJson(jsonString, ModuleJsonDto::class.java) ?: continue
                    val courseId = moduleDto.course.id
                    val fileVersion = moduleDto.version
                    val installedVersion = prefs.getInt("$KEY_VERSION_PREFIX$courseId", 0)

                    // Si el track ya tiene datos y la versión instalada es igual que la versión del archivo, omitir
                    if (!isTrackEmpty && installedVersion == fileVersion) {
                        Log.d(TAG, "Módulo '$courseId' al día (v$installedVersion). Omitiendo.")
                        continue
                    }

                    Log.d(
                        TAG,
                        "Cargando/Actualizando módulo '$courseId' (v$installedVersion -> v$fileVersion) desde: $filePath"
                    )

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

                    versionsToPersist[courseId] = fileVersion
                }

                if (coursesToInsert.isNotEmpty()) {
                    appDatabase.withTransaction {
                        courseDao.insertCourses(coursesToInsert)
                        lessonDao.insertAll(lessonsToInsert)
                        exerciseDao.insertAll(exercisesToInsert)
                        blockOptionDao.insertAll(optionsToInsert)
                    }

                    // Persistir las nuevas versiones de los módulos actualizados
                    prefs.edit {
                        versionsToPersist.forEach { (courseId, version) ->
                            putInt("$KEY_VERSION_PREFIX$courseId", version)
                        }
                    }

                    Log.d(
                        TAG,
                        "Track '$track' sincronizado exitosamente: " +
                                "${coursesToInsert.size} cursos actualizados/insertados."
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al pre-poblar o actualizar la base de datos desde assets: ${e.message}", e)
        }
    }
}
