package com.example.anti_life360

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.anti_life360.ui.theme.AntiLife360Theme
import android.location.Location
import android.util.Log
import com.google.android.gms.location.*
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Create a location request with intervals
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        // Create a location callback to listen for updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.d("LocationStatus", "Updated location: ${location.latitude}, ${location.longitude}")
                }
            }
        }

        // Register for permission request result handling
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationPermissionGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (locationPermissionGranted || coarseLocationPermissionGranted) {
                // If permissions granted, start location updates
                startLocationUpdates()
            } else {
                Log.d("LocationStatus", "Location permission denied.")
            }
        }

        // Check and request permissions
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                startLocationUpdates()
            }
            else -> {
                // Request location permissions
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        }

        setContent {
            AntiLife360Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->
                    PauseButton(
                        modifier = Modifier.padding(innerPadding),
                        startLocationUpdates = ::startLocationUpdates,
                        stopLocationUpdates = ::stopLocationUpdates
                    )
                }
            }
        }
    }

    // Function to start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    // Function to stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationStatus", "Location updates stopped.")
    }
}

@Composable
fun PauseButton(
    modifier: Modifier = Modifier,
    startLocationUpdates: () -> Unit,
    stopLocationUpdates: () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                isClicked = !isClicked
                if (isClicked) {
                    stopLocationUpdates()
                    Log.d("LocationStatus", "Location paused.")
                } else {
                    startLocationUpdates()
                    Log.d("LocationStatus", "Location tracking resumed.")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(if (isClicked) 0xFF008421 else 0xFFcf142b)
            ),
            shape = CircleShape,
            modifier = Modifier.size(130.dp)
        ) {
            Text(
                text = if (isClicked) "RESUME" else "PAUSE",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the appropriate message below the button
        Text(
            text = if (isClicked) "Your location is paused." else "Your location is currently being tracked.",
            color = Color.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PauseButtonPreview() {
    AntiLife360Theme {
        PauseButton(
            startLocationUpdates = {},
            stopLocationUpdates = {}
        )
    }
}
