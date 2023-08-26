package com.example.musicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass : Application() {

    companion object{
        const val Channel_Id="channel1"
        const val play="play"
        const val next="next"
        const val previous="previous"
        const val exit="exit"
    }
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                Channel_Id,
                "Music Player",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "Music playback notifications"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}