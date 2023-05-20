package com.maximus.moodtrack

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.maximus.moodtrack.NotificationHelper.Companion.ACTION_MARK_MOOD

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "Name"
        private const val NOTIFICATION_ID = 1
        const val ACTION_MARK_MOOD = "action_mark_mood"
    }
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//    fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            channel.enableLights(true)
//            channel.lightColor = Color.BLUE
//            notificationManager.createNotificationChannel(channel)
//        }
//    }

    fun showNotification() {
        val openAppIntent = Intent(context, MainActivity::class.java)
        openAppIntent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val markMoodPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("Напоминание")
            .setContentText("Не забудьте отметить настроение!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(markMoodPendingIntent)
            .addAction(
                R.drawable.ic_launcher,
                "Отметить настроение",
                markMoodPendingIntent
            )

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    fun scheduleNotification() {
        val markMoodIntent = Intent(context, NotificationHelper::class.java)
        markMoodIntent.action = ACTION_MARK_MOOD
        val markMoodPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            markMoodIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intervalMillis = 10000 // 10 seconds
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            intervalMillis.toLong(),
            markMoodPendingIntent
        )
        showNotification()

    }

    private fun isAppRunning(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        return appProcesses.any { it.processName == context.packageName }
    }

}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_MARK_MOOD) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showNotification()
        }
    }
}