package com.example.a221505_cikgu_izwan_plantlogs.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ROOM DATABASE
@Database(
    entities     = [PlantEntity::class, HealthEntity::class],
    version      = 4,
    exportSchema = false
)
abstract class PlantLogsDatabase : RoomDatabase() {

    abstract fun plantDao()  : PlantDao
    abstract fun healthDao() : HealthDao

    companion object {
        @Volatile
        private var INSTANCE: PlantLogsDatabase? = null

        fun getDatabase(context: Context): PlantLogsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantLogsDatabase::class.java,
                    "plantlogs_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}