package com.josenaves.forecast.presentation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.josenaves.forecast.R
import com.josenaves.forecast.common.extension.setupSnackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class ForecastActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ForecastActivity"
        const val PERMISSIONS_REQUEST = 73
    }

    private val viewModel: ForecastViewModel by viewModel()

    private var locationClient: FusedLocationProviderClient? = null

    private val permissions = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        setupObservers()
    }

    override fun onStart() {
        super.onStart()

        if (permissionsGranted()) {
            getLastLocation()
        } else {
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST) {
            if (grantResults.isNotEmpty() && permissionsGranted()) {
                getLastLocation()
            } else {
                text_loading.text = getString(R.string.message_no_permission)
            }
        }
    }

    private fun setupObservers() {
        root_view.setupSnackbar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)

        viewModel.isLoading.observe(this, Observer {
            val data = it.getContentIfNotHandled()
            data?.let { isLoading ->
                if (isLoading) {
                    text_loading.visibility = VISIBLE
                    forecast_view.visibility = INVISIBLE
                } else {
                    text_loading.visibility = INVISIBLE
                    forecast_view.visibility = VISIBLE
                }
            }
        })

        viewModel.onDataReady.observe(this, Observer {
            val dataReceived = it.getContentIfNotHandled()
            dataReceived?.let { forecast ->
                text_coordinates.text = getString(R.string.label_coordinates, forecast.latitude, forecast.longitude)

                text_timezone.text = getString(R.string.label_timezone, forecast.timezone)

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                val date = Date(forecast.currently.time * 1000)
                text_time.text = getString(R.string.label_datetime, sdf.format(date))

                text_summary.text = getString(R.string.label_summary, forecast.currently.summary)
                text_temperature.text = getString(R.string.label_temperature, forecast.currently.temperature)

                text_precipitation_probability.text =
                    getString(R.string.label_precipitation_probability, forecast.currently.precipProbability)

                text_precipitation_intensity.text = String.format(
                    getString(
                        R.string.label_precipitation_intensity,
                        forecast.currently.precipIntensity
                    )
                )
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        locationClient?.let {
            it.lastLocation.addOnCompleteListener(this@ForecastActivity) { task ->
                if (task.isSuccessful) {
                    task.result?.let { location ->
                        if (isConnected()) {
                            viewModel.fetchForecastAsync(location.latitude, location.longitude)
                        } else {
                            viewModel.showSnackbarMessage(R.string.message_error_not_connected)
                        }
                    }
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                    viewModel.showSnackbarMessage(R.string.message_error_getting_location)
                }
            }
        }
    }

    private fun requestPermission() = ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST)

    private fun permissionsGranted(): Boolean =
        Build.VERSION.SDK_INT >= 23 &&
                PermissionChecker.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    private fun isConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting ?: false
    }
}
