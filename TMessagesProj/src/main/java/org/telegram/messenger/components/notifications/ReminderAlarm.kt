package org.telegram.messenger.components.notifications

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings


private fun checkAlarmsAccess1(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return true
    }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager?
    return alarmManager?.canScheduleExactAlarms() == true
}
fun checkAlarmAccess(context:Context){
    return//TODO
    if (!checkAlarmsAccess1(context)) {
        val builder = AlertDialog.Builder(context).create()
        builder.setTitle("Need Permission")
        builder.setMessage("Please Give access to ALARM_SERVICE in settings! You Need to find the Islamic Telegram app and allow to schedule exact alarms")
        builder.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { p0, p1 ->
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
            builder.cancel()
        }
        builder.show()
    } else {
        if (!isIgnoringBatteryOptimizations(context)) {
            val builder = AlertDialog.Builder(context).create()
            builder.setTitle("Exempt Battery optimization for Islamic Telegram app")
            builder.setMessage("Please Exempt Battery optimization for Islamic Telegram app in settings! You Need to find the Islamic Telegram app and disable Battery optimization to schedule exact alarms")
            builder.setButton(AlertDialog.BUTTON_POSITIVE, "Ok") { p0, p1 ->
                requestBatteryOptimizationExemption(context)
                builder.cancel()
            }
            builder.show()

        } else {
            // Proceed with your logic if already exempted
//            Toast.makeText(context, "Battery optimization already exempted", Toast.LENGTH_SHORT).show()
        }
    }
}
private fun requestBatteryOptimizationExemption(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        context.startActivity(intent)
    }
}
private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as android.os.PowerManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
    } else {
        true // Battery optimizations not present on pre-Marshmallow devices
    }
}