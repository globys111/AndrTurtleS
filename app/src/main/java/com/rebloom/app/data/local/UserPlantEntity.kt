package com.rebloom.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_plants")
data class UserPlantEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val plantId: String?,
    val nickname: String,
    val plantTypeName: String,
    val notes: String?,
    val acquiredDate: String?,
    val customImageUrl: String?,
    val room: String?,
    val lastWateredAt: String?,
    val createdAt: String,
    val isSynced: Boolean = false
)
