package com.rebloom.app.domain.model

data class Post(
    val id: Int,
    val userName: String,
    val avatar: String?,
    val text: String,
    val imageName: String?,
    val replies: List<Post>
)
