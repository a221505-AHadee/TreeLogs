package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// PLANT ENTITY

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id                : Int     = 0,
    val plantCode         : String  = "",
    val name              : String  = "",
    val species           : String  = "",
    val location          : String  = "",
    val latitude          : Double  = 0.0,
    val longitude         : Double  = 0.0,
    val healthStatus      : String  = "",
    val fertiliserType    : String  = "",
    val fertiliserRatio   : String  = "",
    val comment           : String  = "",
    val notes             : String  = "",
    val imageUris         : String  = "",   // multiple photos
    val sharedToCommunity : Boolean = false
) {

    fun getImageList(): List<String> =
        if (imageUris.isBlank()) emptyList()
        else imageUris.split("|").filter { it.isNotBlank() } // separator
}