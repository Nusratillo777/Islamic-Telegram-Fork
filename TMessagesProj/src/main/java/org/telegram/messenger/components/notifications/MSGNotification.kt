package org.telegram.messenger.components.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import smd.telegram.islamic.R
import org.telegram.ui.LaunchActivity

/**
 * Created by Siddikov Mukhriddin on 08/11/22
 */

object MSGNotification {
    private const val CHANNEL_ID ="MSGNotification"
    var intentMain : Intent?=null
    fun showNotification(context: Context,title: String = "Title", text: String = "") {
        intentMain = Intent(context, LaunchActivity::class.java)
        intentMain?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        createNotificationChannel(context)
        val pIntent = PendingIntent.getActivity(context, 0, intentMain,    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT)

        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/prayer_sound")

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(text)
            )
//            .setContentText(text)
            .setSmallIcon(R.drawable.notification)
//            .setColor(ContextCompat.getColor(context.applicationContext, R.color.widget_badge))
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .build()


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(title.hashCode(), notification)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/raw/prayer_sound")
            val name = "Pray Times Alarm"
            val descriptionText = "Pray Times Alarm"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT)

            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}