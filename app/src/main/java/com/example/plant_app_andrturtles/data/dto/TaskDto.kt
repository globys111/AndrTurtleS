package com.example.plant_app_andrturtles.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String,
    val plantId: String,
    val type: String,                // "WATER" | "REPOT" | "LIGHT"
    val isRepeating: Boolean,
    val startDate: String? = null,   // yyyy-MM-dd
    val intervalDays: Int? = null,
    val date: String? = null,        // yyyy-MM-dd
    val time: String,                // HH:mm
    val completedDates: List<String> = emptyList() // yyyy-MM-dd
)
