package com.rebloom.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val about: String,
    val avatarImageName: String
)
