package com.team2.handiwork.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.team2.handiwork.AppConst.AGENT
import com.team2.handiwork.AppConst.BODY
import com.team2.handiwork.AppConst.CHANNEL_ID
import com.team2.handiwork.AppConst.MISSION
import com.team2.handiwork.AppConst.NOTIFICATION_MSG
import com.team2.handiwork.AppConst.PREF_DEVICE_TOKEN
import com.team2.handiwork.AppConst.TITLE
import com.team2.handiwork.MainActivity
import com.team2.handiwork.R


class PushNotificationService : FirebaseMessagingService() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data[TITLE]
        val text = remoteMessage.data[BODY]
        val agent = remoteMessage.data[AGENT]
        val mission = remoteMessage.data[MISSION]

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Heads Up Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        val extras = Bundle()
        extras.putString(NOTIFICATION_MSG, CHANNEL_ID)
        extras.putString(AGENT, agent)
        extras.putString(MISSION, mission)
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtras(extras)
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val resultIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val notificationManager : NotificationManager =
            (this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        notificationManager.createNotificationChannel(channel)

        val notification: Notification.Builder = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(resultIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
        notificationManager.notify(1, notification.build())

        super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        //update user profile when the new device token is generated
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor =  pref.edit()
        editor.putString(PREF_DEVICE_TOKEN, token)
        editor.apply()

        super.onNewToken(token)
    }
}