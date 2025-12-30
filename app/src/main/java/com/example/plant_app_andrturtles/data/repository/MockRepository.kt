package com.example.plant_app_andrturtles.data.repository

import android.content.Context
import com.example.plant_app_andrturtles.data.dto.PlantDto
import com.example.plant_app_andrturtles.data.dto.TaskDto
import com.example.plant_app_andrturtles.data.dto.UserDto
import com.example.plant_app_andrturtles.data.dto.ArticleDto
import com.example.plant_app_andrturtles.data.dto.PostDto
import com.example.plant_app_andrturtles.data.mapper.toDomain
import com.example.plant_app_andrturtles.domain.model.Plant
import com.example.plant_app_andrturtles.domain.model.TaskDefinition
import com.example.plant_app_andrturtles.domain.model.User
import com.example.plant_app_andrturtles.domain.model.Article
import com.example.plant_app_andrturtles.domain.model.Post
import kotlinx.coroutines.delay
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class MockRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadUser(): User {
        delay(2000)
        val raw = context.assets.open("user.json").bufferedReader().use { it.readText() }
        val dto = json.decodeFromString(UserDto.serializer(), raw)
        return dto.toDomain()
    }

    suspend fun loadPlants(): List<Plant> {
        delay(2000)
        val raw = context.assets.open("plants.json").bufferedReader().use { it.readText() }
        val dtoList = json.decodeFromString(ListSerializer(PlantDto.serializer()), raw)
        return dtoList.map { it.toDomain() }
    }

    suspend fun loadTaskDefinitions(): List<TaskDefinition> {
        delay(2000)
        val raw = context.assets.open("tasks.json").bufferedReader().use { it.readText() }
        val dtoList = json.decodeFromString(ListSerializer(TaskDto.serializer()), raw)
        return dtoList.map { it.toDomain() }
    }

    suspend fun loadArticles(): List<Article> {
        delay(2000)
        val raw = context.assets.open("articles.json").bufferedReader().use { it.readText() }
        val dtoList = json.decodeFromString(ListSerializer(ArticleDto.serializer()), raw)
        return dtoList.map { it.toDomain() }
    }

    suspend fun loadPosts(): List<Post> {
        delay(2000)
        val raw = context.assets.open("posts.json").bufferedReader().use { it.readText() }
        val dtoList = json.decodeFromString(ListSerializer(PostDto.serializer()), raw)
        return dtoList.map { it.toDomain() }
    }
}
