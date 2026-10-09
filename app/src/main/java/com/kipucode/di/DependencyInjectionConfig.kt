package com.kipucode.di

import android.app.Application
import com.kipucode.util.notification.StudyReminderScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DependencyInjectionConfig : Application() {
    override fun onCreate() {
        super.onCreate()
        val (hour, minute) = StudyReminderScheduler.getSavedReminderTime(this)
        StudyReminderScheduler.scheduleDailyReminder(this, targetHour = hour, targetMinute = minute)
    }
}