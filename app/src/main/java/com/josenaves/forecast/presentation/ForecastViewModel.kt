package com.josenaves.forecast.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.josenaves.forecast.R
import com.josenaves.forecast.common.architecture.Event
import com.josenaves.forecast.data.ForecastRepository
import com.josenaves.forecast.data.remote.api.dto.ForecastResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ForecastViewModel(private val repository: ForecastRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    private val snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = snackbarText

    val isLoading = MutableLiveData<Event<Boolean>>()
    val onDataReady = MutableLiveData<Event<ForecastResponse>>()

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun fetchForecastAsync(section: String) = ioScope.launch {
        isLoading.postValue(Event(true))
        try {
            val forecast = repository.getCurrentDayForecast()
            onDataReady.postValue(Event(forecast))
        } catch (e: Exception) {
            showSnackbarMessage(R.string.message_error_getting_forecast)
        } finally {
            isLoading.postValue(Event(false))
        }
    }

    private fun showSnackbarMessage(@StringRes message: Int) {
        snackbarText.postValue(Event(message))
    }

}