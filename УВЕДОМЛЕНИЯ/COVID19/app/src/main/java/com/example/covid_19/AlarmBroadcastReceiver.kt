package com.example.covid_19

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val CHANNEL_ID = "COVID_NOTIFICATION_CHANNEL_ID"
        val name: CharSequence = context.resources.getString(R.string.app_name)
        val mBuilder: NotificationCompat.Builder
        val notificationIntent: Intent = Intent(context, MainActivity::class.java)
        val bundle: Bundle = Bundle()
        notificationIntent.putExtras(bundle)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        val contentIntent: PendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mNotificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(android.os.Build.VERSION.SDK_INT >= 26) {
            val mChannel: NotificationChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(mChannel)
            mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLights(Color.RED, 300, 300)
                .setChannelId(CHANNEL_ID)
                .setContentTitle("Title")
        } else {
            mBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("Title")
        }

        mBuilder.setContentIntent(contentIntent)
        mBuilder.setContentText("Content text")
        mBuilder.setAutoCancel(true)
        mNotificationManager.notify(1, mBuilder.build())

        Log.e("MESSAGE: ", "HELLO WORLD!")
    }
}










