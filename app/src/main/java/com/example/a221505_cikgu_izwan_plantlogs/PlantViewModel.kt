package com.example.a221505_cikgu_izwan_plantlogs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class PlantData(
    val name    : String = "",
    val species : String = ""
)

class PlantViewModel : ViewModel() {

    var plantData by mutableStateOf(PlantData())
        private set

    fun updatePlant(name: String, species: String) {
        plantData = PlantData(
            name    = name,
            species = species
        )
    }

    fun clearPlant() {
        plantData = PlantData()
    }
}