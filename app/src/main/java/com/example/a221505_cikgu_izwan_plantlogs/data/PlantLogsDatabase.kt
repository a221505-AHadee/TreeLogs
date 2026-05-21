package com.example.a221505_cikgu_izwan_plantlogs.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlantEntity::class, HealthEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class PlantLogsDatabase : RoomDatabase() {

    abstract fun plantDao()  : PlantDao
    abstract fun healthDao() : HealthDao

    companion object {
        @Volatile
        private var INSTANCE: PlantLogsDatabase? = null

        fun getDatabase(context: Context): PlantLogsDatabase {
            // Return existing instance if already created
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantLogsDatabase::class.java,
                    "plantlogs_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
