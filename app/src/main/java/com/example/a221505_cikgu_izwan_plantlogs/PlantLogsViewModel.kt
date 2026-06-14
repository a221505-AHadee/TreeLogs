package com.example.a221505_cikgu_izwan_plantlogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a221505_cikgu_izwan_plantlogs.api.WeatherApi
import com.example.a221505_cikgu_izwan_plantlogs.api.WeatherResponse
import com.example.a221505_cikgu_izwan_plantlogs.data.HealthEntity
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantEntity
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantLogsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ── VIEWMODEL ─────────────────────────────────────────────
class PlantLogsViewModel(
    private val repository: PlantLogsRepository
) : ViewModel() {

    // ── User display name (Image task: Ahmad -> Abdul) ────
    var displayName by mutableStateOf("Abdul")
        private set
    fun updateDisplayName(name: String) { displayName = name }

    // ── Plant list from Room ──────────────────────────────
    val plantList: StateFlow<List<PlantEntity>> =
        repository.allPlants.stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val healthList: StateFlow<List<HealthEntity>> =
        repository.allHealthChecks.stateIn(
            scope        = viewModelScope,
            started      = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ── Selected plant ────────────────────────────────────
    var selectedPlant by mutableStateOf<PlantEntity?>(null)
        private set

    fun selectPlant(plant: PlantEntity) { selectedPlant = plant }
    fun clearSelectedPlant()            { selectedPlant = null  }

    // ── Edit mode ─────────────────────────────────────────
    var editingPlant  by mutableStateOf<PlantEntity?>(null)
        private set
    var editingHealth by mutableStateOf<HealthEntity?>(null)
        private set

    fun startEditPlant(plant: PlantEntity)    { editingPlant  = plant }
    fun startEditHealth(health: HealthEntity) { editingHealth = health }
    // Clears edit state — called when leaving AddPlant/HealthCheck
    fun clearEditPlant()                      { editingPlant  = null  }
    fun clearEditHealth()                     { editingHealth = null  }

    // ── QR scan result ────────────────────────────────────
    var scannedPlant by mutableStateOf<PlantEntity?>(null)
        private set

    fun clearScannedPlant() { scannedPlant = null }

    fun findPlantByCode(code: String) {
        viewModelScope.launch {
            scannedPlant = repository.getPlantByCode(code)
        }
    }

    // ── Plant counter for generating PLT-001 codes ────────
    fun generatePlantCode(): String {
        val count = plantList.value.size + 1
        return "PLT-${count.toString().padStart(3, '0')}"
    }

    // Plant CRUD
    fun addPlant(
        name: String, species: String, location: String,
        latitude: Double, longitude: Double,
        healthStatus: String, fertiliserType: String,
        fertiliserRatio: String, comment: String, notes: String,
        imageUris: String = ""
    ) {
        viewModelScope.launch {
            val code = generatePlantCode()
            repository.insertPlant(
                PlantEntity(
                    plantCode        = code,
                    name             = name,
                    species          = species,
                    location         = location,
                    latitude         = latitude,
                    longitude        = longitude,
                    healthStatus     = healthStatus,
                    fertiliserType   = fertiliserType,
                    fertiliserRatio  = fertiliserRatio,
                    comment          = comment,
                    notes            = notes,
                    imageUris        = imageUris
                )
            )
        }
    }

    fun updatePlant(
        plant: PlantEntity, name: String, species: String,
        location: String, latitude: Double, longitude: Double,
        healthStatus: String, fertiliserType: String,
        fertiliserRatio: String, comment: String, notes: String,
        imageUris: String = plant.imageUris
    ) {
        viewModelScope.launch {
            val updated = plant.copy(
                name            = name,
                species         = species,
                location        = location,
                latitude        = latitude,
                longitude       = longitude,
                healthStatus    = healthStatus,
                fertiliserType  = fertiliserType,
                fertiliserRatio = fertiliserRatio,
                comment         = comment,
                notes           = notes,
                imageUris       = imageUris
            )
            repository.updatePlant(updated)
            // If currently viewing this plant in detail, update selection too
            if (selectedPlant?.id == plant.id) {
                selectedPlant = updated
            }
        }
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch { repository.deletePlant(plant) }
    }

    // Health CRUD
    fun addHealthCheck(plantName: String, status: String, symptom: String, imageUris: String = "") {
        viewModelScope.launch {
            repository.insertHealthCheck(
                HealthEntity(plantName = plantName, status = status, symptom = symptom, imageUris = imageUris)
            )
        }
    }

    fun updateHealthCheck(health: HealthEntity, plantName: String, status: String, symptom: String, imageUris: String = health.imageUris) {
        viewModelScope.launch {
            repository.updateHealthCheck(health.copy(plantName = plantName, status = status, symptom = symptom, imageUris = imageUris))
        }
    }

    fun deleteHealthCheck(health: HealthEntity) {
        viewModelScope.launch { repository.deleteHealthCheck(health) }
    }

    // ── Clear all local data — useful for testing/demo ────
    fun clearAllData() {
        viewModelScope.launch {
            plantList.value.forEach { repository.deletePlant(it) }
            healthList.value.forEach { repository.deleteHealthCheck(it) }
        }
    }

    // ── Firebase community share ──────────────────────────
    var shareStatus by mutableStateOf("")
        private set

    fun clearShareStatus() { shareStatus = "" }

    fun shareToCommunity(plant: PlantEntity, deviceId: String) {
        viewModelScope.launch {
            try {
                repository.sharePlantToFirestore(plant, deviceId, displayName)
                shareStatus = "Shared to community! ✅"
            } catch (e: Exception) {
                shareStatus = "Share failed: ${e.message}"
            }
        }
    }

    // ── Community plants from Firestore ───────────────────
    var communityPlants by mutableStateOf<List<Map<String, Any>>>(emptyList())
        private set
    var communityLoading by mutableStateOf(false)
        private set
    var communityError by mutableStateOf("")
        private set

    fun loadCommunityPlants() {
        viewModelScope.launch {
            communityLoading = true
            communityError = ""
            try {
                communityPlants = repository.getCommunityPlants()
            } catch (e: Exception) {
                communityPlants = emptyList()
                communityError = "Could not load: ${e.message}"
            }
            communityLoading = false
        }
    }

    // ── Weather API ───────────────────────────────────────
    var weatherData    by mutableStateOf<WeatherResponse?>(null)
        private set
    var weatherLoading by mutableStateOf(false)
        private set
    var weatherError   by mutableStateOf("")
        private set

    fun fetchWeather(lat: Double, lon: Double) {
        if (lat == 0.0 && lon == 0.0) return
        viewModelScope.launch {
            weatherLoading = true
            weatherError   = ""
            try {
                weatherData = WeatherApi.service.getWeatherByCoords(lat, lon)
            } catch (e: Exception) {
                weatherError = "Could not load weather"
            }
            weatherLoading = false
        }
    }
}

// VIEWMODEL FACTORY
class PlantLogsViewModelFactory(
    private val repository: PlantLogsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantLogsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantLogsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}