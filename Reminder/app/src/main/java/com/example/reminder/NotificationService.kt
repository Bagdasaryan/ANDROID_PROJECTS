package com.example.reminder

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.example.reminder.ROOM_NOTIFICATIONS_DB.NotificationDatabase
import com.example.reminder.ROOM_NOTIFICATIONS_DB.Notifications
import java.util.*


class NotificationService: Service() {
    private val TAG = "TAG: "
    private val CHANNEL_ID = "ForegroundService Kotlin"

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)

        return START_STICKY
    }

    override fun onCreate() {
        Log.e(TAG, "onCreate")
        outputNotifications()
    }

    private fun outputNotifications() {
        val timer = Timer()
        var x = 0
        val task = object: TimerTask() {
            var timesRan = 0
            override fun run() {
                // ПРОВЕРЯЕМ, ЕСЛИ ЕСТЬ УВЕДОМЛЕНИЯ, У КОТОРЫХ НАСТУПИЛО ВРЕМЯ ДЛЯ ОТОБРАЖЕНИЯ, ТО ОТПРАВЛЯЕМ ПОЛЬЗОВАТЕЛЮ УВЕДОМЛЕНИЕ
                isTimeUp()
                Log.e("LOG: ", "Hello " + (++x))
            }
        }
        timer.schedule(task, 0, 20000)
    }

    private fun isTimeUp() {
        val db: NotificationDatabase = Room.databaseBuilder(
            applicationContext,
            NotificationDatabase::class.java,
            "notification"
        ).build()
        val SIZE = db.notificationsDao().getAll().size

        for(i in 0..SIZE - 1) {
            // PARAMETERS: day, month, year, hour, minute
            val n = db.notificationsDao().getAll()[i]
            if(isDateExpired(n.day, n.month, n.year, n.hour, n.minute) && !n.isShowed) {

                notificationFun(
                    n.title.toString(),
                    n.description.toString(),
                    "CI1",
                    1,
                    n.cbWithoutSound
                )

                db.notificationsDao().update(
                    Notifications(
                        n.id, n.title, n.description, n.cbWithoutSound,
                        n.day, n.month, n.year, n.hour, n.minute, true
                    )
                )
            }
        }
    }

    private fun notificationFun(
        title: String,
        description: String,
        channelId: String,
        identifierChannelId: Int,
        isWithoutSound: Boolean
    ) {
        val notificationChannel: NotificationChannel
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: Notification.Builder
        val alarmSound: Uri
        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(
            this, 0, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //NOTIFICATION
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                channelId,
                description,
                NotificationManager.IMPORTANCE_HIGH
            )

            if(isWithoutSound) {
                notificationChannel.setSound(null, null)
            }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(resultPendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(resultPendingIntent)
        }
        builder.setSound(null)
        notificationManager.notify(identifierChannelId, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
}



















