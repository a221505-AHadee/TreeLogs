package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface HealthDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(health: HealthEntity)


    @Query("SELECT * FROM health_checks ORDER BY id DESC")
    fun getAll(): Flow<List<HealthEntity>>


    @Delete
    suspend fun delete(health: HealthEntity)


    @Update
    suspend fun update(health: HealthEntity)


    @Query("DELETE FROM health_checks WHERE id = :id")
    suspend fun deleteById(id: Int)
}
