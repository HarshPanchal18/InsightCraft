package com.harsh.askgemini.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.harsh.askgemini.util.Cupboard

// To configure the necessary settings for sending a notification.
class RunnerNotifier(
    notificationManager: NotificationManager,
    private val context: Context
) : Notifier(notificationManager) {

    override val notificationChannelId: String = "runner_channel_id"
    override val notificationChannelName: String = "Running notification"
    override val notificationId: Int = 200

    override fun buildNotification(): Notification {
        return NotificationCompat.Builder(context,notificationChannelId)
            .setContentTitle(getNotificationTitle())
            .setContentText(getNotificationMessage())
            .setSmallIcon(android.R.drawable.btn_star)
            .build()
    }

    override fun getNotificationTitle(): String = "Do you know? : "

    override fun getNotificationMessage(): String {
        return Cupboard.randomNotificationMessage()
    }
}
