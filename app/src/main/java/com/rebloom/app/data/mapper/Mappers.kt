package com.rebloom.app.data.mapper

import com.rebloom.app.data.dto.TaskDto
import com.rebloom.app.data.dto.UserDto
import com.rebloom.app.data.dto.UserPlantDto
import com.rebloom.app.data.dto.UserPlantWithCacheDto
import com.rebloom.app.data.local.UserPlantEntity
import com.rebloom.app.domain.model.*
import java.time.LocalDate
import java.time.LocalTime

fun UserPlantEntity.toDomain(): Plant = Plant(
    id           = id,
    name         = nickname,
    type         = plantTypeName,
    description  = notes ?: "",
    imageUrl     = customImageUrl,
    plantingDate = acquiredDate ?: "",
    wateringLevel = wateringLevel
)

fun UserPlantEntity.toDto(): UserPlantDto = UserPlantDto(
    id             = id,
    userId         = userId,
    plantId        = plantId,
    nickname       = nickname,
    plantTypeName  = plantTypeName,
    notes          = notes,
    acquiredDate   = acquiredDate,
    customImageUrl = customImageUrl,
    room           = room,
    lastWateredAt  = lastWateredAt,
    createdAt      = createdAt,
    wateringLevel = wateringLevel
)

fun UserPlantWithCacheDto.toEntity(): UserPlantEntity = UserPlantEntity(
    id             = id,
    userId         = userId,
    plantId        = plantId,
    nickname       = nickname,
    plantTypeName  = plantTypeName ?: plantsCache?.commonName ?: "",
    notes          = notes,
    acquiredDate   = acquiredDate,
    customImageUrl = customImageUrl ?: plantsCache?.imageUrl,
    room           = room,
    lastWateredAt  = lastWateredAt,
    createdAt      = createdAt,
    isSynced       = true,
    wateringLevel = wateringLevel ?: 3
)

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