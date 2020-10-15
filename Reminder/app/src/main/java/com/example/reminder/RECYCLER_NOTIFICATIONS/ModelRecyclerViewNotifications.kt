package com.example.reminder.RECYCLER_NOTIFICATIONS

data class ModelRecyclerViewNotifications(
    val id: Int,
    val title: String,
    val description: String,
    val cbWithoutSound: Boolean,
    val day: Int,
    val month: Int,
    val year: Int,
    val hour: Int,
    val minute: Int
)