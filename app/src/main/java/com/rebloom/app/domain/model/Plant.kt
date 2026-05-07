package com.rebloom.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val id: String,
    val name: String,
    val type: String,
    val description: String,
    val imageUrl: String?,
    val plantingDate: String = ""
) : Parcelable
