package com.rebloom.app.data.repository

import android.content.Context
import com.rebloom.app.data.dto.PlantDto
import com.rebloom.app.data.mapper.toDomain
import com.rebloom.app.domain.model.Plant
import kotlinx.coroutines.delay
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class PlantRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadPlants(): List<Plant> {
        delay(2000)
        val raw = context.assets.open("plants.json").bufferedReader().use { it.readText() }
        val dtoList = json.decodeFromString(ListSerializer(PlantDto.serializer()), raw)
        return dtoList.map { it.toDomain() }
    }
}
