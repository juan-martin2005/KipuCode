package com.kipucode.util.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kipucode.MainActivity
import com.kipucode.R

class ReminderNotificationHelper(private val context: Context) {

    companion object {
        // Canal 1: Alta Prioridad (Flotante)
        const val CHANNEL_HIGH_ID = "kipu_study_high"
        const val NOTIFICATION_HIGH_ID = 1002

        // Canal 2: Prioridad Estándar (Barra de estado)
        const val CHANNEL_DEFAULT_ID = "kipu_study_default"
        const val NOTIFICATION_DEFAULT_ID = 1001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Alta prioridad (Flotante)
        val highChannel = NotificationChannel(
            CHANNEL_HIGH_ID,
            "Recordatorios de estudio",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Recordatorios diarios para continuar con el aprendizaje"
            enableVibration(true)
        }

        // 2. Prioridad estándar (Barra)
        val defaultChannel = NotificationChannel(
            CHANNEL_DEFAULT_ID,
            "Avisos discretos",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Avisos discretos cuando tienes ejercicios listos para repasar en tu tiempo libre"
        }

        manager.createNotificationChannel(highChannel)
        manager.createNotificationChannel(defaultChannel)
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @SuppressLint("MissingPermission")
    fun sendHighPriorityNotification(
        title: String = "Píldora del día",
        message: String = "Del fracaso se aprende, del éxito no mucho."
    ) {
        if (!hasNotificationPermission()) return

        val appLogoBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

        val notification = NotificationCompat.Builder(context, CHANNEL_HIGH_ID)
            .setSmallIcon(R.drawable.ic_code)
            .setLargeIcon(appLogoBitmap)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_HIGH_ID, notification)
    }

    @SuppressLint("MissingPermission")
    fun sendDefaultNotification(
        title: String = "Aviso de práctica",
        message: String = "Tienes ejercicios listos para repasar cuando tengas 5 minutos libres."
    ) {
        if (!hasNotificationPermission()) return

        val appLogoBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

        val notification = NotificationCompat.Builder(context, CHANNEL_DEFAULT_ID)
            .setSmallIcon(R.drawable.ic_code)
            .setLargeIcon(appLogoBitmap)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent())
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_DEFAULT_ID, notification)
    }
}
