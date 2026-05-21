package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ── PLANT DAO ────────────────────────────────────────────
// DAO = Data Access Object
// Interface that defines all database operations for plants
// Room generates the actual SQL code automatically
// @Dao tells Room this is a DAO interface
@Dao
interface PlantDao {

    // INSERT — adds one plant to the database
    // onConflict = IGNORE means if same id exists, skip it
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plant: PlantEntity)

    // GET ALL — returns all plants as a Flow
    // Flow = like a live stream — UI updates automatically
    // when database changes (new plant added or deleted)
    @Query("SELECT * FROM plants ORDER BY id DESC")
    fun getAll(): Flow<List<PlantEntity>>

    // DELETE — removes one plant from database
    @Delete
    suspend fun delete(plant: PlantEntity)

    // UPDATE — replaces existing plant with new data
    @Update
    suspend fun update(plant: PlantEntity)

    // DELETE BY ID — remove plant using just its id number
    @Query("DELETE FROM plants WHERE id = :id")
    suspend fun deleteById(id: Int)
}
