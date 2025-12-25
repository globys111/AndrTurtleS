package com.example.plant_app_andrturtles.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val imageName: String,
    val plantingDate: String = ""
) : Parcelable
