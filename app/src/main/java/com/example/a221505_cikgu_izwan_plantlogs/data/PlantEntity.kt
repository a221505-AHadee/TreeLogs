package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// ── PLANT ENTITY ─────────────────────────────────────────
// @Entity tells Room this is a database table
// tableName = "plants" is the table name in SQLite
// @PrimaryKey(autoGenerate = true) — Room auto assigns
// a unique id for each row (1, 2, 3...)
@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id       : Int    = 0,
    val name     : String = "",
    val species  : String = "",
    val location : String = "",
    val notes    : String = ""
)
