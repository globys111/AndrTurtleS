package com.rebloom.app.data.remote

import com.rebloom.app.RebloomApp
import com.rebloom.app.data.dto.UserPlantDto
import com.rebloom.app.data.dto.UserPlantWithCacheDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class UserPlantRemoteDataSource {

    suspend fun fetchAll(userId: String): List<UserPlantWithCacheDto> =
        RebloomApp.supabase.from("user_plants")
            .select(Columns.raw("*, plants_cache(common_name, image_url)")) {
                filter { eq("user_id", userId) }
            }
            .decodeList()

    suspend fun upsert(dto: UserPlantDto) {
        RebloomApp.supabase.from("user_plants").upsert(dto)
    }

    suspend fun delete(id: String) {
        RebloomApp.supabase.from("user_plants")
            .delete { filter { eq("id", id) } }
    }
}
