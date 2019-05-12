package com.josenaves.forecast.data

import com.josenaves.forecast.data.remote.api.dto.ForecastResponse

interface ForecastDataSource {

    suspend fun getCurrentDayForecast(latitude: Double, longitude: Double): ForecastResponse?
}