package com.example.a221505_cikgu_izwan_plantlogs.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

// ── REPOSITORY ────────────────────────────────────────────
// ViewModel <> DAO + Firebase Firestore
class PlantLogsRepository(
    private val plantDao  : PlantDao,
    private val healthDao : HealthDao
) {
    // ── Room operations ───────────────────────────────────
    val allPlants: Flow<List<PlantEntity>> = plantDao.getAll()
    val allHealthChecks: Flow<List<HealthEntity>> = healthDao.getAll()

    suspend fun insertPlant(plant: PlantEntity)  { plantDao.insert(plant) }
    suspend fun deletePlant(plant: PlantEntity)  { plantDao.delete(plant) }
    suspend fun updatePlant(plant: PlantEntity)  { plantDao.update(plant) }

    // find plant by QR code
    suspend fun getPlantByCode(code: String): PlantEntity? {
        return plantDao.getByPlantCode(code)
    }

    suspend fun insertHealthCheck(health: HealthEntity) { healthDao.insert(health) }
    suspend fun deleteHealthCheck(health: HealthEntity) { healthDao.delete(health) }
    suspend fun updateHealthCheck(health: HealthEntity) { healthDao.update(health) }

    // ── Firebase Firestore operations ─────────────────────
    // Shares a plant to the community collection in Firestore.
    // deviceId makes the document ID unique per device, so two
    // phones that both generate "PLT-001" won't overwrite each other.
    suspend fun sharePlantToFirestore(plant: PlantEntity, deviceId: String, sharedByName: String) {
        val db = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            "plantCode"      to plant.plantCode,
            "name"           to plant.name,
            "species"        to plant.species,
            "location"       to plant.location,
            "latitude"       to plant.latitude,
            "longitude"      to plant.longitude,
            "healthStatus"   to plant.healthStatus,
            "fertiliserType" to plant.fertiliserType,
            "fertiliserRatio" to plant.fertiliserRatio,
            "comment"        to plant.comment,
            "imageUris"      to plant.imageUris,
            "sharedBy"       to sharedByName,
            "deviceId"       to deviceId
        )
        // Composite document ID — deviceId + plantCode — avoids
        // collisions when multiple devices generate the same code
        val docId = "${deviceId}_${plant.plantCode}"
        db.collection("community_plants")
            .document(docId)
            .set(data)
            .await()

        // Update local Room record to mark as shared
        plantDao.update(plant.copy(sharedToCommunity = true))
    }

    // Fetches all community plants from Firestore
    suspend fun getCommunityPlants(): List<Map<String, Any>> {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("community_plants").get().await()
        return snapshot.documents.mapNotNull { it.data }
    }
}