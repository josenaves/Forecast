package com.josenaves.forecast.data.remote.api

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class DarkSkyApiUnitTest {

    private lateinit var api: DarkSkyApi

    @Before
    fun setup() {
        api = DarkSkyApiClient.create()
    }

    @Test
    fun `API is ok`() {
        assertNotNull(api)
    }

    @Test
    fun `User should forecast from API`() {
        runBlocking {

            val response = api.getForecastAsync(59.337239,18.062381).await()
            assert(response.isSuccessful)

            val apiResponse = response.body()
            assertNotNull(apiResponse)

            assertNotNull(apiResponse?.latitude)
            assert(apiResponse?.latitude == 59.337239)

            assertNotNull(apiResponse?.longitude)
            assert(apiResponse?.longitude == 18.062381)

            assertNotNull(apiResponse?.currently)

            assertNotNull(apiResponse?.currently?.temperature)
            assertNotNull(apiResponse?.currently?.humidity)

            println("API Response: $apiResponse")
        }
    }


}