package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface PlantDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plant: PlantEntity)


    @Query("SELECT * FROM plants ORDER BY id DESC")
    fun getAll(): Flow<List<PlantEntity>>

    @Delete
    suspend fun delete(plant: PlantEntity)

    @Update
    suspend fun update(plant: PlantEntity)

    @Query("DELETE FROM plants WHERE id = :id")
    suspend fun deleteById(id: Int)
}
