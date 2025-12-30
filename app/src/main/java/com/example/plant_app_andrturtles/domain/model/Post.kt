package com.example.plant_app_andrturtles.domain.model

data class Post(
    val id: Int,
    val userName: String,
    val avatar: String?,
    val text: String,
    val imageName: String?,
    val replies: List<Post>
)