package com.josenaves.forecast.di

import com.josenaves.forecast.data.ForecastRepository
import com.josenaves.forecast.data.remote.api.DarkSkyApiClient
import com.josenaves.forecast.presentation.ForecastViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module(override = true) {

    // view model
    viewModel { ForecastViewModel(get()) }

    // repository
    single { ForecastRepository(get()) }

    // remote API
    single { DarkSkyApiClient.create() }
}

