package com.kipucode.util.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.kipucode.data.local.dao.ExerciseAttemptDao
import com.kipucode.data.local.dao.LearningProgressDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StudyReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_IS_TEST = "is_test"
        private const val PREFS_NAME = "kipu_reminder_prefs"
        private const val KEY_LAST_NOTIFIED_DATE = "last_notified_date"
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface StudyReminderEntryPoint {
        fun learningProgressDao(): LearningProgressDao
        fun exerciseAttemptDao(): ExerciseAttemptDao
        fun auth(): FirebaseAuth
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val isTest = inputData.getBoolean(KEY_IS_TEST, false)
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Verificador anti-spam: Si no es test y ya se notificó hoy, no repetir
            if (!isTest) {
                val lastDate = prefs.getString(KEY_LAST_NOTIFIED_DATE, null)
                if (lastDate == todayStr) {
                    return@withContext Result.success()
                }
            }

            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                StudyReminderEntryPoint::class.java
            )

            val currentUserId = entryPoint.auth().currentUser?.uid
            if (currentUserId.isNullOrEmpty()) {
                return@withContext Result.success()
            }

            val helper = ReminderNotificationHelper(context)
            if (!helper.hasNotificationPermission()) {
                return@withContext Result.success()
            }

            val now = System.currentTimeMillis()
            val dueExercises = entryPoint.learningProgressDao().getDueExercises(currentUserId, now).first()

            var notificationSent = false

            if (dueExercises.isNotEmpty()) {
                // Caso 1: Tarjetas pendientes según FSRS
                val count = dueExercises.size
                helper.sendHighPriorityNotification(
                    title = "¡Repaso pendiente!",
                    message = "Tienes $count ejercicio${if (count > 1) "s" else ""} listo${if (count > 1) "s" else ""} para afianzar en tu memoria hoy."
                )
                notificationSent = true
            } else {
                // Caso 2: Verificar si ha hecho algún intento hoy
                val startOfDay = getStartOfDayMillis()
                val attempts = entryPoint.exerciseAttemptDao().getAllAttemptsForUser(currentUserId)
                val hasPracticedToday = attempts.any { it.lastAttemptAt >= startOfDay }

                if (!hasPracticedToday) {
                    // No ha practicado hoy: Notificación motivacional
                    helper.sendDefaultNotification(
                        title = "¿Unos minutos para programar?",
                        message = "Mantén tu racha activa. Completa un reto rápido o avanza en tu lección."
                    )
                    notificationSent = true
                }
            }

            // Registrar fecha si se envió la notificación
            if (notificationSent && !isTest) {
                prefs.edit().putString(KEY_LAST_NOTIFIED_DATE, todayStr).apply()
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun getStartOfDayMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
}
