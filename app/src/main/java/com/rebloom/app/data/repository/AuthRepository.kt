package com.rebloom.app.data.repository

import android.content.Context
import com.rebloom.app.data.dto.UserDto
import com.rebloom.app.data.mapper.toDomain
import com.rebloom.app.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

class AuthRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadUser(): User {
        delay(2000)
        val raw = context.assets.open("user.json").bufferedReader().use { it.readText() }
        val dto = json.decodeFromString(UserDto.serializer(), raw)
        return dto.toDomain()
    }
}
