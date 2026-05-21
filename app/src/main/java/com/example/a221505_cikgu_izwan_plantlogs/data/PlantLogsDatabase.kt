package com.example.a221505_cikgu_izwan_plantlogs.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ── ROOM DATABASE ─────────────────────────────────────────
// @Database tells Room this is the database class
// entities = list of ALL tables in this database
// version = 1 — increase this number when you change
//           the schema (add/remove columns or tables)
// exportSchema = false — no need to export schema file
@Database(
    entities = [PlantEntity::class, HealthEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class PlantLogsDatabase : RoomDatabase() {

    // Room generates these DAO implementations automatically
    abstract fun plantDao()  : PlantDao
    abstract fun healthDao() : HealthDao

    // SINGLETON PATTERN
    // companion object = shared instance for the whole app
    // Only ONE database instance ever created
    // synchronized = thread-safe — prevents two threads
    //               creating two instances at the same time
    companion object {
        @Volatile
        private var INSTANCE: PlantLogsDatabase? = null

        fun getDatabase(context: Context): PlantLogsDatabase {
            // Return existing instance if already created
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantLogsDatabase::class.java,
                    "plantlogs_database"   // database file name
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
