package com.rebloom.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPlantDao {
    @Query("SELECT * FROM user_plants ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<UserPlantEntity>>

    @Upsert
    suspend fun upsert(entity: UserPlantEntity)

    @Upsert
    suspend fun upsertAll(entities: List<UserPlantEntity>)

    @Query("SELECT * FROM user_plants WHERE id = :id")
    suspend fun getById(id: String): UserPlantEntity?

    @Query("SELECT * FROM user_plants WHERE id = :id")
    fun observeById(id: String): Flow<UserPlantEntity?>

    @Query("DELETE FROM user_plants WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM user_plants WHERE isSynced = 0")
    suspend fun getUnsynced(): List<UserPlantEntity>
}
