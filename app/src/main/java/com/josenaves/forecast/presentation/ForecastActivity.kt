package com.josenaves.forecast.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.josenaves.forecast.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForecastActivity : AppCompatActivity() {

    private val viewModel: ForecastViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.fetchForecastAsync(59.337239,18.062381)
    }
}
