package com.josenaves.forecast.data

import com.josenaves.forecast.data.remote.api.DarkSkyApi
import com.josenaves.forecast.data.remote.api.dto.ForecastResponse

class ForecastRepository(private val api: DarkSkyApi) : ForecastDataSource {

    override suspend fun getCurrentDayForecast(
        latitude: Double,
        longitude: Double
    ): ForecastResponse? {
        val response = api.getForecastAsync(latitude, longitude).await()
        return response.body()
    }
}