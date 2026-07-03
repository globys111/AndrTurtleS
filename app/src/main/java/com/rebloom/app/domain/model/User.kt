package com.rebloom.app.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val about: String,
    val avatarImageName: String
)
