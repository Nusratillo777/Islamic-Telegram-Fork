package org.telegram.messenger.components.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast

class RestartBackgroundService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("Broadcast Listened", "Service tried to stop")
//        if (!Prefs.token.isNullOrBlank())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context!!.startForegroundService(Intent(context, LocationService::class.java))
            } else {
                context!!.startService(Intent(context, LocationService::class.java))
            }
            Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show()
        } catch (e:Exception){
            Log.e("TTT", "RestartBackgroundService$e")
            Toast.makeText(context, "Service restart failed", Toast.LENGTH_SHORT).show()
        }
    }
}