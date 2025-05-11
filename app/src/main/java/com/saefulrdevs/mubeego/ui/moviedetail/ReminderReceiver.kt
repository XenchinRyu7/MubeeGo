package com.saefulrdevs.mubeego.ui.moviedetail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.saefulrdevs.mubeego.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_baseline_timelapse_24)
            .setContentTitle("Movie Reminder")
            .setContentText("It's time to watch your movie!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, builder.build())
        Toast.makeText(context, "Reminder!", Toast.LENGTH_SHORT).show()
    }
}
