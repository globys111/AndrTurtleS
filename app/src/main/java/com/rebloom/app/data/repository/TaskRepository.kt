package com.rebloom.app.data.repository

import android.content.Context
import com.rebloom.app.data.dto.TaskDto
import com.rebloom.app.data.mapper.toDomain
import com.rebloom.app.domain.model.TaskDefinition
import kotlinx.coroutines.delay
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class TaskRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadTaskDefinitions(): List<TaskDefinition> {
        delay(2000)
        val raw = context.assets.open("tasks.json").bufferedReader().use { it.readText() }
        val dtoList = json.decodeFromString(ListSerializer(TaskDto.serializer()), raw)
        return dtoList.map { it.toDomain() }
    }
}
