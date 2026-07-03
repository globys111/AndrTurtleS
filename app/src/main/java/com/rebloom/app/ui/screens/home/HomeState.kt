package com.rebloom.app.ui.screens.home

import com.rebloom.app.domain.model.Plant
import com.rebloom.app.domain.model.TaskOccurrence
import java.time.LocalDate

data class HomeData(
    val plants: List<Plant>,
    val allOccurrences: List<TaskOccurrence>,
    val selectedDate: LocalDate,
    val today: LocalDate
)
