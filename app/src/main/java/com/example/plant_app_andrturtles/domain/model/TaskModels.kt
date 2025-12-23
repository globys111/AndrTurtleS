package com.example.plant_app_andrturtles.domain.model

import java.time.LocalDate
import java.time.LocalTime

enum class TaskType { WATER, REPOT, LIGHT }

data class TaskDefinition(
    val id: String,
    val plantId: String,
    val type: TaskType,
    val isRepeating: Boolean,
    val startDate: LocalDate?,
    val intervalDays: Int?,
    val oneTimeDate: LocalDate?,
    val time: LocalTime,
    val completedDates: Set<LocalDate>
)

enum class TaskStatus { OVERDUE, TODAY, UPCOMING }

data class TaskOccurrence(
    val definitionId: String,
    val plantId: String,
    val type: TaskType,
    val date: LocalDate,
    val time: LocalTime,
    val isCompleted: Boolean,
    val status: TaskStatus
)
