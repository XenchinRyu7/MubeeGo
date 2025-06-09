package com.saefulrdevs.mubeego.ui.common

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import android.util.Log

object NotificationHelper {
    const val CHANNEL_ID = "reminder_channel"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleNotification(
        context: Context,
        timeInMillis: Long,
        title: String = "MubeeGo Reminder",
        message: String = "It's time to watch!",
        requestCode: Int = 0,
        intent: Intent? = null
    ) {
        val notifyIntent = intent ?: Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            Log.d("NotificationHelper", "Scheduling alarm at $timeInMillis, now=${System.currentTimeMillis()}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(context, "Cannot schedule exact alarms. Please allow permission in settings.", Toast.LENGTH_LONG).show()
                    Log.w("NotificationHelper", "Cannot schedule exact alarms, permission not granted")
                    return
                }
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            Log.d("NotificationHelper", "Alarm scheduled for $timeInMillis")
            Toast.makeText(context, "Reminder set!", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "SecurityException: ${e.message}")
            Toast.makeText(context, "Failed to set reminder: permission denied.", Toast.LENGTH_LONG).show()
        }
    }
}
