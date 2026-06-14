package com.example.a221505_cikgu_izwan_plantlogs.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ── WEATHER API ───────────────────────────────────────────
// OpenWeatherMap API — free tier
// API Key: d806dc36edbf93d8e56eb4a642b752e3
// Docs: https://openweathermap.org/current

// Data class for API response
data class WeatherResponse(
    val name    : String = "",
    val main    : MainWeather = MainWeather(),
    val weather : List<WeatherDesc> = emptyList(),
    val wind    : Wind = Wind()
)

data class MainWeather(
    val temp     : Double = 0.0,
    val humidity : Int    = 0,
    val feels_like: Double = 0.0
)

data class WeatherDesc(
    val main        : String = "",
    val description : String = ""
)

data class Wind(
    val speed : Double = 0.0
)

// Retrofit interface — defines API endpoints
interface WeatherApiService {

    // Get weather by GPS coordinates
    @GET("weather")
    suspend fun getWeatherByCoords(
        @Query("lat")   lat    : Double,
        @Query("lon")   lon    : Double,
        @Query("appid") apiKey : String = "d806dc36edbf93d8e56eb4a642b752e3",
        @Query("units") units  : String = "metric"  // Celsius
    ): WeatherResponse
}

// Singleton Retrofit instance
object WeatherApi {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val service: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
