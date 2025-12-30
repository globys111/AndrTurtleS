package com.example.plant_app_andrturtles.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: Int,
    val title: String,
    val content: String,
    val imageName: String,
    val rating: Float,
    val author: String
) : Parcelable