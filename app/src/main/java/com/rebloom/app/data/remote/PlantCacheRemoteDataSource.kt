package com.rebloom.app.data.remote

import com.rebloom.app.BuildConfig
import com.rebloom.app.RebloomApp
import com.rebloom.app.data.dto.EdgeFunctionResponseDto
import com.rebloom.app.data.dto.PlantCacheRowDto
import io.github.jan.supabase.postgrest.from
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class PlantCacheRemoteDataSource {

    private val client = HttpClient(Android)
    private val json   = Json { ignoreUnknownKeys = true }

    suspend fun searchPlant(query: String): EdgeFunctionResponseDto {
        val responseText = client.post("${BuildConfig.SUPABASE_URL}/functions/v1/plant-service") {
            header(HttpHeaders.Authorization, "Bearer ${BuildConfig.SUPABASE_ANON_KEY}")
            contentType(ContentType.Application.Json)
            setBody("""{"query":"${query.replace("\"", "\\\"")}"}""")
        }.bodyAsText()
        return json.decodeFromString(responseText)
    }

    suspend fun getCachedId(query: String): String? =
        RebloomApp.supabase.from("plants_cache")
            .select {
                filter { eq("query_name", query.lowercase().trim()) }
                limit(1)
            }
            .decodeSingleOrNull<PlantCacheRowDto>()?.id
}
