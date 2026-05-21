package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ── HEALTH DAO ────────────────────────────────────────────
// Same pattern as PlantDao but for health_checks table
@Dao
interface HealthDao {

    // INSERT — adds one health check to database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(health: HealthEntity)

    // GET ALL — returns all health checks as Flow
    @Query("SELECT * FROM health_checks ORDER BY id DESC")
    fun getAll(): Flow<List<HealthEntity>>

    // DELETE — removes one health check
    @Delete
    suspend fun delete(health: HealthEntity)

    // UPDATE — replaces existing health check
    @Update
    suspend fun update(health: HealthEntity)

    // DELETE BY ID
    @Query("DELETE FROM health_checks WHERE id = :id")
    suspend fun deleteById(id: Int)
}
