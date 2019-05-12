package com.josenaves.forecast.data.remote.api

import com.josenaves.forecast.data.remote.api.dto.ForecastResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface DarkSkyApi {

    @Headers("Content-Type: application/json")
    @GET("/forecast/2bb07c3bece89caf533ac9a5d23d8417/{latitude},{longitude}")
    fun getForecastAsync(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Deferred<Response<ForecastResponse>>

}