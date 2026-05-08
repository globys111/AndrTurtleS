package com.rebloom.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserPlantEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPlantDao(): UserPlantDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "rebloom.db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
