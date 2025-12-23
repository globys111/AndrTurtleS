package com.example.plant_app_andrturtles.data.mapper

import com.example.plant_app_andrturtles.data.dto.PlantDto
import com.example.plant_app_andrturtles.data.dto.TaskDto
import com.example.plant_app_andrturtles.data.dto.UserDto
import com.example.plant_app_andrturtles.domain.model.*
import java.time.LocalDate
import java.time.LocalTime

fun PlantDto.toDomain(): Plant = Plant(id, name, type, description, imageName)

fun UserDto.toDomain(): User = User(id, name, email, about, avatarImageName)

fun TaskDto.toDomain(): TaskDefinition {
    val taskType = TaskType.valueOf(type)
    return TaskDefinition(
        id = id,
        plantId = plantId,
        type = taskType,
        isRepeating = isRepeating,
        startDate = startDate?.let(LocalDate::parse),
        intervalDays = intervalDays,
        oneTimeDate = date?.let(LocalDate::parse),
        time = LocalTime.parse(time),
        completedDates = completedDates.map(LocalDate::parse).toSet()
    )
}
