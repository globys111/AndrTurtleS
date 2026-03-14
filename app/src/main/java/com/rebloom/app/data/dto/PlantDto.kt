package com.rebloom.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlantDto(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val imageName: String
)
