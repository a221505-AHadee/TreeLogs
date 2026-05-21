package com.example.a221505_cikgu_izwan_plantlogs

import android.app.Application
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantLogsDatabase
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantLogsRepository

// ── APPLICATION CLASS ─────────────────────────────────────
// Application class runs FIRST before any Activity or Screen
// Used to create the database and repository ONE TIME
// and make them available to the whole app
//
// Must register this in AndroidManifest.xml:
//   <application android:name=".PlantLogsApplication" ...>
class PlantLogsApplication : Application() {

    // lazy = only created when first accessed, not at startup
    val database   by lazy { PlantLogsDatabase.getDatabase(this) }
    val repository by lazy {
        PlantLogsRepository(
            plantDao  = database.plantDao(),
            healthDao = database.healthDao()
        )
    }
}
