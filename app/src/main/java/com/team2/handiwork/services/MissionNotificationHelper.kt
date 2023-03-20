package com.team2.handiwork.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.team2.handiwork.AppConst
import com.team2.handiwork.MainActivity
import com.team2.handiwork.R

@RequiresApi(Build.VERSION_CODES.S)
class MissionNotificationHelper(private val context: Context) {
    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            AppConst.SUGGESTION_CHANNEL_ID,
            "Heads Up Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendMissionNotification(count: Int) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val resultIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val notification: Notification.Builder = Notification.Builder(context, AppConst.SUGGESTION_CHANNEL_ID)
            .setContentTitle("Enrol before you've missed it!")
            .setContentText("There are $count new mission suggestions in the past hour!")
            .setContentIntent(resultIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1, notification.build())
        }
    }

}