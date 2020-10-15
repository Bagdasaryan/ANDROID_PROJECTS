package com.example.reminder.ROOM_NOTIFICATIONS_DB

import androidx.room.*

@Dao
interface NotificationsDao {
    @Query("SELECT * FROM notifications")
    fun getAll(): List<Notifications>

    @Insert
    fun insert(notifications: Notifications)

    @Delete
    fun delete(notifications: Notifications)

    @Delete
    fun deleteAll(notifications: List<Notifications>)

    @Update
    fun update(notifications: Notifications)
}
