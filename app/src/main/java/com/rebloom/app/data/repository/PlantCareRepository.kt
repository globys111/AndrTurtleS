package com.rebloom.app.data.repository

import android.util.Log
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
    val care_parameters: CareParams? = null
)

@Serializable
data class CareParams(
    val base_watering_level: Int? = null
)

class PlantCareRepository {
    private val supabase get() = RebloomApp.supabase

    suspend fun getWateringLevel(plantType: String): Int {
        val cleanType = plantType.trim()
        return try {
            val rows: List<PlantCacheRow> = supabase.postgrest["plants_cache"]
                .select { filter { eq("common_name", cleanType) } }
                .decodeList()

            Log.d("PlantCare", "Rows found: ${rows.size}")
            val level = rows.firstOrNull()
                ?.growth_data
                ?.care_parameters
                ?.base_watering_level
            Log.d("PlantCare", "Extracted level: $level")
            level ?: 3   // fallback
        } catch (e: Exception) {
            Log.e("PlantCare", "Error for '$cleanType'", e)
            3
        }
    }
}