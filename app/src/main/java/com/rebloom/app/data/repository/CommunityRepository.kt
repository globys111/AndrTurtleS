package com.rebloom.app.data.repository

import android.content.Context
import com.rebloom.app.data.dto.PostDto
import com.rebloom.app.data.mapper.toDomain
import com.rebloom.app.domain.model.Post
import kotlinx.coroutines.delay
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class CommunityRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadPosts(): List<Post> {
        delay(2000)
        val raw = context.assets.open("posts.json").bufferedReader().use { it.readText() }
        val dtoList = json.decodeFromString(ListSerializer(PostDto.serializer()), raw)
        return dtoList.map { it.toDomain() }
    }
}
