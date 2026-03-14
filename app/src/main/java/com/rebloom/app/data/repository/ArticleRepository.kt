package com.rebloom.app.data.repository

import android.content.Context
import com.rebloom.app.data.dto.ArticleDto
import com.rebloom.app.data.mapper.toDomain
import com.rebloom.app.domain.model.Article
import kotlinx.coroutines.delay
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class ArticleRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadArticles(): List<Article> {
        delay(2000)
        val raw = context.assets.open("articles.json").bufferedReader().use { it.readText() }
        val dtoList = json.decodeFromString(ListSerializer(ArticleDto.serializer()), raw)
        return dtoList.map { it.toDomain() }
    }
}
