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


class PlantLogsViewModel(
    private val repository: PlantLogsRepository
) : ViewModel() {


    val plantList: StateFlow<List<PlantEntity>> =
        repository.allPlants.stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )


    val healthList: StateFlow<List<HealthEntity>> =
        repository.allHealthChecks.stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )


    var selectedPlant by mutableStateOf<PlantEntity?>(null)
        private set

    fun selectPlant(plant: PlantEntity) { selectedPlant = plant }
    fun clearSelectedPlant()            { selectedPlant = null  }


    var editingPlant  by mutableStateOf<PlantEntity?>(null)
        private set
    var editingHealth by mutableStateOf<HealthEntity?>(null)
        private set

    fun startEditPlant(plant: PlantEntity)   { editingPlant  = plant }
    fun startEditHealth(health: HealthEntity){ editingHealth = health }
    fun clearEditPlant()                     { editingPlant  = null  }
    fun clearEditHealth()                    { editingHealth = null  }


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
