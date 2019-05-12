package com.josenaves.forecast.presentation

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.josenaves.forecast.R
import com.josenaves.forecast.common.extension.setupSnackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForecastActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ForecastActivity"
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

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        locationClient?.let {
            it.lastLocation.addOnCompleteListener(this@ForecastActivity) { task ->
                if (task.isSuccessful) {
                    task.result?.let { location ->
                        viewModel.fetchForecastAsync(location.latitude, location.longitude)
                    }
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                    viewModel.showSnackbarMessage(R.string.message_error_getting_location)
                }
            }
        }
    }

    private fun requestPermission() = ActivityCompat.requestPermissions(this, permissions, 0)

    private fun permissionsGranted(): Boolean =
        Build.VERSION.SDK_INT >= 23 &&
                PermissionChecker.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    private fun setupObservers() {
        root_view.setupSnackbar(this, viewModel.snackbarMessage, Snackbar.LENGTH_LONG)
    }
}
