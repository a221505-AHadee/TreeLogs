package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id       : Int    = 0,
    val name     : String = "",
    val species  : String = "",
    val location : String = "",
    val notes    : String = ""
)
