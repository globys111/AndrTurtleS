package com.rebloom.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPlantDto(
    val id: String,
    @SerialName("user_id")          val userId: String,
    @SerialName("plant_id")         val plantId: String? = null,
    val nickname: String,
    val notes: String? = null,
    @SerialName("acquired_date")    val acquiredDate: String? = null,
    @SerialName("custom_image_url") val customImageUrl: String? = null,
    val room: String? = null,
    @SerialName("last_watered_at")  val lastWateredAt: String? = null,
    @SerialName("created_at")       val createdAt: String,
)

@Serializable
data class UserPlantWithCacheDto(
    val id: String,
    @SerialName("user_id")          val userId: String,
    @SerialName("plant_id")         val plantId: String? = null,
    val nickname: String,
    val notes: String? = null,
    @SerialName("acquired_date")    val acquiredDate: String? = null,
    @SerialName("custom_image_url") val customImageUrl: String? = null,
    val room: String? = null,
    @SerialName("last_watered_at")  val lastWateredAt: String? = null,
    @SerialName("created_at")       val createdAt: String,
    @SerialName("plants_cache")     val plantsCache: PlantCacheRefDto? = null,
)

@Serializable
data class PlantCacheRefDto(
    @SerialName("common_name") val commonName: String? = null,
    @SerialName("image_url")   val imageUrl: String? = null,
)

@Serializable
data class PlantCacheRowDto(
    val id: String,
    @SerialName("query_name")  val queryName: String,
    @SerialName("common_name") val commonName: String? = null,
    @SerialName("image_url")   val imageUrl: String? = null,
)

@Serializable
data class EdgeFunctionResponseDto(
    @SerialName("common_name")     val commonName: String? = null,
    @SerialName("scientific_name") val scientificName: String? = null,
    @SerialName("image_url")       val imageUrl: String? = null,
    val family: String? = null,
)
