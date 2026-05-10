package com.example.a221505_cikgu_izwan_plantlogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


//PlantData — holds info for 1 plant
data class PlantData(
    val name     : String = "",   // plant name
    val species  : String = "",   // species
    val location : String = "",   // location found
    val notes    : String = ""    // extra notes
)

//HealthData — holds 1 health check
data class HealthData(
    val plantName : String = "",   // plant name checked
    val status    : String = "",   // Healthy / Unhealthy
    val symptom   : String = ""    // current symptoms
)

//TreeLogsViewModel — ONE shared ViewModel
//Stores ALL data shared across all 6 screens:
//plantList     → all logged plants (Screen 2, 3, 4)
//healthList    → all health checks (Screen 5)
//selectedPlant → plant tapped in list (Screen 3 → 4)
class TreeLogsViewModel : ViewModel() {

    //Plant list
    val plantList = mutableStateListOf<PlantData>()

    fun addPlant(name: String, species: String, location: String, notes: String) {
        plantList.add(PlantData(name = name, species = species, location = location, notes = notes))
    }

    fun removePlant(index: Int) {
        if (index in plantList.indices) plantList.removeAt(index)
    }

    //Selected plant — Screen 3 taps → Screen 4 reads
    var selectedPlant by mutableStateOf<PlantData?>(null)
        private set

    fun selectPlant(plant: PlantData) { selectedPlant = plant }
    fun clearSelectedPlant()          { selectedPlant = null  }

    //Health check list
    val healthList = mutableStateListOf<HealthData>()

    fun addHealthCheck(plantName: String, status: String, symptom: String) {
        healthList.add(HealthData(plantName = plantName, status = status, symptom = symptom))
    }

    fun removeHealthCheck(index: Int) {
        if (index in healthList.indices) healthList.removeAt(index)
    }

    //Clear all data
    fun clearAll() {
        plantList.clear()
        healthList.clear()
        selectedPlant = null
    }
}