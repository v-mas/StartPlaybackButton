package com.github.vmas.startplaybackbutton

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

const val FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID =
    "com.github.v-mas.StartPlaybackButton.ForegroundService"

fun NotificationManager.ensureNotificationChannelExists() {
    if (Build.VERSION.SDK_INT >= 26) {
        createNotificationChannel(
            NotificationChannel(
                FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID,
                "Channel to display required notification for app to run in background. Disabling this channel may render app unusable.",
                NotificationManager.IMPORTANCE_MIN
            )
        )
    }
}
