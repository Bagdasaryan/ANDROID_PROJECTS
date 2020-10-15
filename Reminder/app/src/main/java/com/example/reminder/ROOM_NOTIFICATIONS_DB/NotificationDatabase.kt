package com.example.reminder.ROOM_NOTIFICATIONS_DB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Notifications::class), version = 1)
abstract class NotificationDatabase: RoomDatabase() {
    abstract fun notificationsDao(): NotificationsDao
}
