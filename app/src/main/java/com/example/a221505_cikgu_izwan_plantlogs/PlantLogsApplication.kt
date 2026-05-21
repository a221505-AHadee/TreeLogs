package com.example.a221505_cikgu_izwan_plantlogs

import android.app.Application
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantLogsDatabase
import com.example.a221505_cikgu_izwan_plantlogs.data.PlantLogsRepository


class PlantLogsApplication : Application() {

    val database   by lazy { PlantLogsDatabase.getDatabase(this) }
    val repository by lazy {
        PlantLogsRepository(
            plantDao  = database.plantDao(),
            healthDao = database.healthDao()
        )
    }
}
