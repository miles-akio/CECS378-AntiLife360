package com.example.anti_life360

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.anti_life360.ui.theme.AntiLife360Theme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    @RequiresApi(Build.VERSION_CODES.S)
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
    @RequiresApi(Build.VERSION_CODES.S)
    private fun startLocationUpdates() {
        val locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        if(locationManager.getProviderProperties(LocationManager.GPS_PROVIDER) != null)
            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    // Function to stop location updates
    @RequiresApi(Build.VERSION_CODES.S)
    private fun stopLocationUpdates() {
        setMockLocation(33.7839,-118.1141,0.0F)
        //pauseAtCurrentLocation()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationStatus", "Location updates stopped.")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setMockLocation(lat: Double, long: Double, acc: Float) {
        val locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER,
            true,
            false,
            false,
            false,
            true,
            true,
            true,
            ProviderProperties.POWER_USAGE_LOW,
            ProviderProperties.ACCURACY_FINE)

        val mockLocation = Location(LocationManager.GPS_PROVIDER)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        mockLocation.latitude = lat
        mockLocation.longitude = long
        mockLocation.accuracy = acc
        mockLocation.altitude = 0.0
        mockLocation.accuracy = 500.0F
        mockLocation.time = System.currentTimeMillis()
        mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER, LocationProvider.AVAILABLE, null, System.currentTimeMillis())
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun pauseAtCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    setMockLocation(location.latitude, location.longitude, 0.0F)
                }
            }
        }
    }
}

@Composable
fun PauseButton(
    modifier: Modifier = Modifier,
    startLocationUpdates: () -> Unit,
    stopLocationUpdates: () -> Unit,
) {
    var isClicked by remember { mutableStateOf(false) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
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
            fontWeight = FontWeight.Normal,
            modifier = Modifier.widthIn(max = screenWidth * 0.7f)
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
