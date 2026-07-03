package com.rebloom.app.domain.usecase

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object WateringScheduleConverter {

    private val levelToMonthlyCount = mapOf(
        1 to 1,
        2 to 4,
        3 to 8,
        4 to 16,
        5 to 30,
        6 to 60
    )
    fun monthlyWateringCount(wateringLevel: Int): Int {
        return levelToMonthlyCount[wateringLevel] ?: 0
    }

    fun generateWateringDates(
        wateringLevel: Int,
        startDate: LocalDate,
        monthsAhead: Int = 3
    ): List<LocalDate> {
        val countPerMonth = monthlyWateringCount(wateringLevel)
        if (countPerMonth <= 0) return emptyList()

        val allDates = mutableListOf<LocalDate>()
        val daysInMonth = 30.0

        for (monthOffset in 0 until monthsAhead) {
            val monthStart = startDate.plusMonths(monthOffset.toLong())
            val interval = daysInMonth / countPerMonth

            for (i in 0 until countPerMonth) {
                val dayOffset = (i * interval).toLong()
                val wateringDate = monthStart.plusDays(dayOffset)
                allDates.add(wateringDate)
            }
        }

        return allDates
    }
}