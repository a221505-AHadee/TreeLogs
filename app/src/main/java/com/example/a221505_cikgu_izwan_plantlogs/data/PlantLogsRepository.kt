package com.example.a221505_cikgu_izwan_plantlogs.data

import kotlinx.coroutines.flow.Flow

// ── REPOSITORY ────────────────────────────────────────────
// Repository sits between ViewModel and DAO
// ViewModel never talks to DAO directly — always via Repository
// This keeps ViewModel clean and easy to test
//
// Pattern:
//   UI → ViewModel → Repository → DAO → Room Database
//   UI ← ViewModel ← Repository ← DAO ← Room Database
class PlantLogsRepository(
    private val plantDao  : PlantDao,
    private val healthDao : HealthDao
) {

    // ── Plant operations ──────────────────────────────────

    // allPlants is a Flow — ViewModel collects it as StateFlow
    // UI automatically updates when database changes
    val allPlants: Flow<List<PlantEntity>> = plantDao.getAll()

    // suspend = runs on background thread (not main/UI thread)
    // Room does NOT allow database operations on main thread
    suspend fun insertPlant(plant: PlantEntity) {
        plantDao.insert(plant)
    }

    suspend fun deletePlant(plant: PlantEntity) {
        plantDao.delete(plant)
    }

    suspend fun updatePlant(plant: PlantEntity) {
        plantDao.update(plant)
    }

    suspend fun deletePlantById(id: Int) {
        plantDao.deleteById(id)
    }

    // ── Health check operations ───────────────────────────

    val allHealthChecks: Flow<List<HealthEntity>> = healthDao.getAll()

    suspend fun insertHealthCheck(health: HealthEntity) {
        healthDao.insert(health)
    }

    suspend fun deleteHealthCheck(health: HealthEntity) {
        healthDao.delete(health)
    }

    suspend fun updateHealthCheck(health: HealthEntity) {
        healthDao.update(health)
    }

    suspend fun deleteHealthCheckById(id: Int) {
        healthDao.deleteById(id)
    }
}
