package com.example.plant_app_andrturtles.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlantDto(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val imageName: String
)