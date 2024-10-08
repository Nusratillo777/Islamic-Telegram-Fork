package org.telegram.messenger.components.notifications
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import org.telegram.messenger.components.local.Prefs
import smd.telegram.islamic.R
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class LocationService : Service() {

    override fun onCreate() {
        super.onCreate()
        Prefs.init(applicationContext)
        showNotification()
    }

    fun showNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChanel() else startForeground(
            12,
            Notification()
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val NOTIFICATION_CHANNEL_ID = "PrayerAlarmService"
        val channelName = "Prayer Alarm Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Prayer Alarm Service")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Service is running")
            )
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(12, notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            startForeground(12, notification,FOREGROUND_SERVICE_TYPE_LOCATION)
            startForeground(12, notification)
        }else{
            startForeground(12, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Prefs.init(applicationContext)
        showNotification()
        Prefs.init(applicationContext)
        startTimer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendCoords()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TTT_SERVICE","Destroy service")
        stoptimertask()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartBackgroundService::class.java)
        this.sendBroadcast(broadcastIntent) //TODO fix later
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendCoords(){
        GlobalScope.launch {
            val currentSeconds = LocalTime.now().second
            val initialDelay = (60 - currentSeconds) * 1000L // Calculate time to next minute boundary in milliseconds
            delay(initialDelay)
            while (true){
                launch { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendCoord()
                }
                }
                delay(1 * 60 * 1000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendCoord(){
        blockTry{
            Prefs.getPrayTimes()?.prays?.forEach { pray->
                if (!Prefs.prayNotificationsBlockList.contains(pray.name)){
                    Log.e("TTT_Notification",Prefs.prayNotificationsBlockList)
                    Log.e("TTT_Notification1",pray.name)
                    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                    val currentTime = LocalTime.now()
                    if (pray.day==today){
                        val hour = String.format("%02d", currentTime.hour)
                        val minute = String.format("%02d", currentTime.minute)
                        if (pray.time=="${hour}:${minute}"){
                            if (applicationContext != null) MSGNotification.showNotification(
                                applicationContext,
                                pray.name,
                                pray.time
                            )
                        }
                    }
                }
            }
        }
    }
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    fun startTimer() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.e("TTTLocationValue",LocalTime.now().hour.toString()+LocalTime.now().minute.toString())
                }


            }
        }
        timer!!.schedule(
            timerTask,
            0,
            1 * 60 * 1000
        ) //1 * 60 * 1000 1 minute
    }

    fun stoptimertask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private suspend fun blockTry(block: suspend () -> Unit) {
        try {
            block.invoke()
        }catch (e: Exception) {
            Log.e("TTT",e.toString())
        }
    }
}