package com.josenaves.forecast.data.remote.api.dto

data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currently: CurrentForecast
)