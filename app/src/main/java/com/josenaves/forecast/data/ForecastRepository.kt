package com.josenaves.forecast.data

import com.josenaves.forecast.data.remote.api.dto.ForecastResponse

class ForecastRepository(private val remote: ForecastDataSource) : ForecastDataSource {

    override suspend fun getCurrentDayForecast(): ForecastResponse = remote.getCurrentDayForecast()
}