package com.example.plant_app_andrturtles.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String,
    @SerialName("image_name") val imageName: String,
    @SerialName("rating") val rating: Float,
    @SerialName("author") val author: String
)