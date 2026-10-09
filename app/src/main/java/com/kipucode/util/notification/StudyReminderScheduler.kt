package com.kipucode.util.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Calendar
import java.util.concurrent.TimeUnit

object StudyReminderScheduler {

    private const val PREFS_NAME = "kipu_reminder_prefs"
    private const val KEY_HOUR = "reminder_hour"
    private const val KEY_MINUTE = "reminder_minute"
    private const val WORK_NAME = "kipu_daily_study_reminder"

    /**
     * Obtiene la hora y minuto configurados (por defecto 12:00 PM).
     */
    fun getSavedReminderTime(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hour = prefs.getInt(KEY_HOUR, 12)
        val minute = prefs.getInt(KEY_MINUTE, 0)
        return Pair(hour, minute)
    }

    /**
     * Guarda la nueva hora configurada por el usuario y reprograma el recordatorio.
     */
    fun saveAndScheduleReminder(context: Context, hour: Int, minute: Int = 0) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()

        scheduleDailyReminder(context, hour, minute)
    }

    /**
     * Programa el chequeo diario en WorkManager con política UPDATE.
     */
    fun scheduleDailyReminder(context: Context, targetHour: Int = 12, targetMinute: Int = 0) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Si la hora ya pasó hoy, se programa para mañana
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val periodicWorkRequest = PeriodicWorkRequestBuilder<StudyReminderWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }

    /**
     * Dispara una prueba inmediata (se ejecuta en 1 segundo sin esperar a la hora programada).
     */
    fun triggerImmediateTest(context: Context) {
        val testRequest = OneTimeWorkRequestBuilder<StudyReminderWorker>()
            .setInputData(workDataOf(StudyReminderWorker.KEY_IS_TEST to true))
            .build()
        WorkManager.getInstance(context).enqueue(testRequest)
    }
}
