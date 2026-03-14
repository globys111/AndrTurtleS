package com.rebloom.app.domain.usecase

import com.rebloom.app.domain.model.*
import java.time.LocalDate

object TaskScheduler {

    /**
     * Генерим occurrences на период (например, -14..+30 дней),
     * чтобы можно было:
     * - показывать задачи на выбранную дату
     * - искать просроченные
     */
    fun generateOccurrences(
        defs: List<TaskDefinition>,
        today: LocalDate,
        daysBack: Long = 14,
        daysForward: Long = 30
    ): List<TaskOccurrence> {
        val start = today.minusDays(daysBack)
        val end = today.plusDays(daysForward)

        val result = mutableListOf<TaskOccurrence>()

        defs.forEach { def ->
            if (!def.isRepeating) {
                val d = def.oneTimeDate ?: return@forEach
                if (d.isBefore(start) || d.isAfter(end)) return@forEach
                val completed = def.completedDates.contains(d)
                result += TaskOccurrence(
                    definitionId = def.id,
                    plantId = def.plantId,
                    type = def.type,
                    date = d,
                    time = def.time,
                    isCompleted = completed,
                    status = statusFor(date = d, today = today, completed = completed)
                )
            } else {
                val s = def.startDate ?: return@forEach
                val step = def.intervalDays ?: return@forEach

                var d = s
                while (d.isBefore(start)) d = d.plusDays(step.toLong())
                while (!d.isAfter(end)) {
                    val completed = def.completedDates.contains(d)
                    result += TaskOccurrence(
                        definitionId = def.id,
                        plantId = def.plantId,
                        type = def.type,
                        date = d,
                        time = def.time,
                        isCompleted = completed,
                        status = statusFor(date = d, today = today, completed = completed)
                    )
                    d = d.plusDays(step.toLong())
                }
            }
        }

        return result.sortedWith(
            compareBy<TaskOccurrence> { it.date }.thenBy { it.time }
        )
    }

    private fun statusFor(date: LocalDate, today: LocalDate, completed: Boolean): TaskStatus {
        if (completed) return TaskStatus.UPCOMING // выполненные можно считать "не требующие"
        return when {
            date.isBefore(today) -> TaskStatus.OVERDUE
            date.isEqual(today) -> TaskStatus.TODAY
            else -> TaskStatus.UPCOMING
        }
    }

    fun tasksForDate(all: List<TaskOccurrence>, date: LocalDate): List<TaskOccurrence> =
        all.filter { it.date == date }.sortedBy { it.time }

    fun top3ForPlant(all: List<TaskOccurrence>, plantId: String): List<TaskOccurrence> {
        val plantTasks = all.filter { it.plantId == plantId && !it.isCompleted }

        return plantTasks
            .sortedWith(
                compareBy<TaskOccurrence> {
                    when (it.status) {
                        TaskStatus.OVERDUE -> 0
                        TaskStatus.TODAY -> 1
                        TaskStatus.UPCOMING -> 2
                    }
                }.thenBy { it.date }.thenBy { it.time }
            )
            .take(3)
    }

}
