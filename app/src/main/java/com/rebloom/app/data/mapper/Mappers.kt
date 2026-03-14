package com.rebloom.app.data.mapper

import com.rebloom.app.data.dto.PlantDto
import com.rebloom.app.data.dto.TaskDto
import com.rebloom.app.data.dto.UserDto
import com.rebloom.app.data.dto.ArticleDto
import com.rebloom.app.data.dto.PostDto
import com.rebloom.app.domain.model.*
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

fun ArticleDto.toDomain(): Article = Article(
    id = id,
    title = title,
    content = content,
    imageName = imageName,
    rating = rating,
    author = author
)

fun PostDto.toDomain(): Post = Post(
    id = id,
    userName = userName,
    avatar = avatar,
    text = text,
    imageName = imageName,
    replies = replies?.map { it.toDomain() } ?: emptyList()
)
