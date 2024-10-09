package com.example.antitrackingapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var pauseLocationButton: Button
    private var mockLatitude = 0.0  // Variables to hold current coordinates
    private var mockLongitude = 0.0
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pauseLocationButton = findViewById(R.id.pauseLocationButton)

        // Initialize LocationManager to get current location
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Request permission to access location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0, 0f, this
            )
        }

        // Set the mock location to the current location when the button is clicked
        pauseLocationButton.setOnClickListener {
            if (isMockLocationEnabled()) {
                setMockLocation(mockLatitude, mockLongitude)
            } else {
                Toast.makeText(this, "Enable mock locations in Developer Options", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Automatically detects current location
    override fun onLocationChanged(location: Location) {
        mockLatitude = location.latitude
        mockLongitude = location.longitude
        Toast.makeText(this, "Current location detected: $mockLatitude, $mockLongitude", Toast.LENGTH_LONG).show()
    }

    // Checks if mock location is enabled
    private fun isMockLocationEnabled(): Boolean {
        return Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ALLOW_MOCK_LOCATION, 0
        ) != 0
    }

    // Sets the mock location to the user's current location
    private fun setMockLocation(latitude: Double, longitude: Double) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        }

        val mockLocation = Location("gps").apply {
            this.latitude = latitude
            this.longitude = longitude
            this.accuracy = 1f
            this.elapsedRealtimeNanos = System.nanoTime()
        }

        Toast.makeText(this, "Mock location set to: $latitude, $longitude", Toast.LENGTH_LONG).show()
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(this, "Location provider disabled", Toast.LENGTH_LONG).show()
    }

    override fun onProviderEnabled(provider: String) {
        Toast.makeText(this, "Location provider enabled", Toast.LENGTH_LONG).show()
    }
}
