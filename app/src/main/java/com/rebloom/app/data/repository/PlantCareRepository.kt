package com.rebloom.app.data.repository

import com.rebloom.app.RebloomApp
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class PlantCacheRow(
    val common_name: String,
    val growth_data: GrowthData? = null
)

@Serializable
data class GrowthData(
    val base_watering_level: Int? = null
)

class PlantCareRepository {
    private val supabase get() = RebloomApp.supabase

    suspend fun getWateringLevel(plantType: String): Int {
        return try {
            val rows = supabase.postgrest["plants_cache"]
                .select {
                    filter { eq("common_name", plantType) }
                }
                .decodeList<PlantCacheRow>()

            rows.firstOrNull()?.growth_data?.base_watering_level ?: 3
        } catch (e: Exception) {
            3
        }
    }
}