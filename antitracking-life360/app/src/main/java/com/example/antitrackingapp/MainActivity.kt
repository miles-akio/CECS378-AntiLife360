package com.example.antitrackingapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var setLocationButton: Button

    // Mock location coordinates (San Francisco in this example)
    private val mockLatitude = 37.7749
    private val mockLongitude = -122.4194

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the button and set up a click listener
        setLocationButton = findViewById(R.id.setLocationButton)
        setLocationButton.setOnClickListener {
            // Check if mock location is enabled
            if (isMockLocationEnabled()) {
                // Set mock location if enabled
                setMockLocation(mockLatitude, mockLongitude)
            } else {
                // Prompt the user to enable mock location in Developer Options
                Toast.makeText(this, "Please enable mock locations in Developer Options", Toast.LENGTH_LONG).show()
            }
        }

        // Optional: Function to monitor network requests
        monitorNetworkRequests()
    }

    // Function to check if mock location is enabled on the device
    private fun isMockLocationEnabled(): Boolean {
        return Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ALLOW_MOCK_LOCATION, 0
        ) != 0
    }

    // Function to set mock location
    private fun setMockLocation(latitude: Double, longitude: Double) {
        // Request location permission if not granted
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

        // Create a mock location object and assign latitude and longitude
        val mockLocation = Location("gps").apply {
            this.latitude = latitude
            this.longitude = longitude
            this.accuracy = 1f
            this.elapsedRealtimeNanos = System.nanoTime()
        }

        // Notify the user that the mock location is set
        Toast.makeText(this, "Mock location set to: $latitude, $longitude", Toast.LENGTH_LONG).show()
    }

    // Function to monitor network requests (Optional, for network logging)
    private fun monitorNetworkRequests() {
        val client = OkHttpClient()

        // Create an HTTP GET request to a placeholder API
        val request = Request.Builder()
            .url("https://jsonplaceholder.typicode.com/posts") // Placeholder API for testing
            .build()

        // Send the network request asynchronously
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle request failure and notify the user
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network request failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                // Handle request success and notify the user
                val responseData = response.body?.string()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network request succeeded: $responseData", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
