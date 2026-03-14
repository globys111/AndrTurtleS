package com.rebloom.app.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    @SerialName("id") val id: Int,
    @SerialName("userName") val userName: String,
    @SerialName("avatar") val avatar: String?,
    @SerialName("text") val text: String,
    @SerialName("image_name") val imageName: String?,
    @SerialName("replies") val replies: List<PostDto>?
)
