package com.saefulrdevs.mubeego.ui.common

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.saefulrdevs.mubeego.R

class ReminderReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderReceiver", "onReceive called")
        val title = intent.getStringExtra("title") ?: "Movie Reminder"
        val message = intent.getStringExtra("message") ?: "It's time to watch your movie!"
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = launchIntent?.let {
            androidx.core.app.TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(it)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
        }
        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_baseline_timelapse_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, builder.build())
        Log.d("ReminderReceiver", "notificationManager.notify dipanggil")
        Toast.makeText(context, "Reminder!", Toast.LENGTH_SHORT).show()
    }
}