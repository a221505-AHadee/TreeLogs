package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// HEALTH ENTITY

@Entity(tableName = "health_checks")
data class HealthEntity(
    @PrimaryKey(autoGenerate = true)
    val id        : Int    = 0,
    val plantName : String = "",
    val status    : String = "",
    val symptom   : String = "",
    val imageUris : String = ""   // multiple photos
) {
    fun getImageList(): List<String> =
        if (imageUris.isBlank()) emptyList()
        else imageUris.split("|").filter { it.isNotBlank() } // separator
}