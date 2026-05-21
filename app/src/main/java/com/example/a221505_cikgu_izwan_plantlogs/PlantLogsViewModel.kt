package com.example.a221505_cikgu_izwan_plantlogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.a221505_cikgu_izwan_plantlogs.data.HealthEntity
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantEntity
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantLogsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ── VIEWMODEL ─────────────────────────────────────────────
// Upgraded from Lab 4:
// Lab 4 → mutableStateListOf (in-memory only, lost on close)
// Lab 5 → StateFlow from Room (persistent, survives close)
//
// viewModelScope.launch = runs suspend functions safely
// on background thread without blocking the UI
class PlantLogsViewModel(
    private val repository: PlantLogsRepository
) : ViewModel() {

    // ── Plant list — from Room via StateFlow ──────────────
    // stateIn converts Flow<List> to StateFlow<List>
    // StateFlow = like mutableStateOf but for coroutines
    // UI reads plantList and updates automatically when
    // Room database changes
    val plantList: StateFlow<List<PlantEntity>> =
        repository.allPlants.stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )

    // ── Health list — from Room via StateFlow ─────────────
    val healthList: StateFlow<List<HealthEntity>> =
        repository.allHealthChecks.stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )

    // ── Selected plant (Screen 3 → Screen 4) ─────────────
    var selectedPlant by mutableStateOf<PlantEntity?>(null)
        private set

    fun selectPlant(plant: PlantEntity) { selectedPlant = plant }
    fun clearSelectedPlant()            { selectedPlant = null  }

    // ── Edit mode ─────────────────────────────────────────
    var editingPlant  by mutableStateOf<PlantEntity?>(null)
        private set
    var editingHealth by mutableStateOf<HealthEntity?>(null)
        private set

    fun startEditPlant(plant: PlantEntity)   { editingPlant  = plant }
    fun startEditHealth(health: HealthEntity){ editingHealth = health }
    fun clearEditPlant()                     { editingPlant  = null  }
    fun clearEditHealth()                    { editingHealth = null  }

    // ── Plant operations ──────────────────────────────────
    // viewModelScope.launch = runs on background thread
    // Room CANNOT run on main thread — must use coroutines

    fun addPlant(name: String, species: String, location: String, notes: String) {
        viewModelScope.launch {
            repository.insertPlant(
                PlantEntity(name = name, species = species, location = location, notes = notes)
            )
        }
    }

    fun updatePlant(plant: PlantEntity, name: String, species: String, location: String, notes: String) {
        viewModelScope.launch {
            repository.updatePlant(
                plant.copy(name = name, species = species, location = location, notes = notes)
            )
        }
    }

    fun deletePlant(plant: PlantEntity) {
        viewModelScope.launch {
            repository.deletePlant(plant)
        }
    }

    // ── Health check operations ───────────────────────────

    fun addHealthCheck(plantName: String, status: String, symptom: String) {
        viewModelScope.launch {
            repository.insertHealthCheck(
                HealthEntity(plantName = plantName, status = status, symptom = symptom)
            )
        }
    }

    fun updateHealthCheck(health: HealthEntity, plantName: String, status: String, symptom: String) {
        viewModelScope.launch {
            repository.updateHealthCheck(
                health.copy(plantName = plantName, status = status, symptom = symptom)
            )
        }
    }

    fun deleteHealthCheck(health: HealthEntity) {
        viewModelScope.launch {
            repository.deleteHealthCheck(health)
        }
    }
}

// ── VIEWMODEL FACTORY ─────────────────────────────────────
// Factory needed because our ViewModel has a constructor
// parameter (repository). Compose's viewModel() function
// cannot pass parameters automatically — Factory does it.
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
