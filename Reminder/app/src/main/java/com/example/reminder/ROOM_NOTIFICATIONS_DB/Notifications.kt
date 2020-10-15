package com.example.reminder.ROOM_NOTIFICATIONS_DB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notifications(
    @PrimaryKey val id: Int,
    @ColumnInfo(name="title") val title: String?,
    @ColumnInfo(name="description") val description: String?,
    @ColumnInfo(name="cbWithoutSound") val cbWithoutSound: Boolean,
    @ColumnInfo(name="day") val day: Int,
    @ColumnInfo(name="month") val month: Int,
    @ColumnInfo(name="year") val year: Int,
    @ColumnInfo(name="hour") val hour: Int,
    @ColumnInfo(name="minute") val minute: Int,
    @ColumnInfo(name="isShowed") val isShowed: Boolean
)
