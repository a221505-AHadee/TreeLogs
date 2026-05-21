package com.example.a221505_cikgu_izwan_plantlogs.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// ── HEALTH ENTITY ─────────────────────────────────────────
// Second table in the same Room database
// Each health check record stored as one row
@Entity(tableName = "health_checks")
data class HealthEntity(
    @PrimaryKey(autoGenerate = true)
    val id        : Int    = 0,
    val plantName : String = "",
    val status    : String = "",
    val symptom   : String = ""
)
