package com.example.anti_life360

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.anti_life360.ui.theme.AntiLife360Theme
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat


class MainActivity : ComponentActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationManager: LocationManager
    private var googleMap: GoogleMap? = null
    private var loggingJob: Job? = null
    private var isPaused = false
    private var lastKnownLocation: LatLng? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        locationManager  = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Create location request
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        // Register for permission request
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationPermissionGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (locationPermissionGranted || coarseLocationPermissionGranted) {
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
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }
        }

        setContent {
            AntiLife360Theme {
                PauseButtonWithMap(
                    startLocationUpdates = ::startLocationUpdates,
                    stopLocationUpdates = ::stopLocationUpdates
                )
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isPaused = false
            loggingJob?.cancel() // Stop any previous logging
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    if (!isPaused) {
                        for (location in locationResult.locations) {
                            lastKnownLocation = LatLng(location.latitude, location.longitude)
                            Log.d("LocationStatus", "Updated location: ${location.latitude}, ${location.longitude}")

                            googleMap?.let { map ->
                                map.clear() // Clear previous markers
                                map.addMarker(MarkerOptions().position(lastKnownLocation!!).title("You are here"))
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation!!, 15f))
                            }
                        }
                    }
                }
            }, null)
        }
    }

    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setMockLocation(lat: Double, long: Double) {
        val locationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager

        // Check if the device supports mock locations (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                // Create mock location
                val mockLocation = Location(LocationManager.GPS_PROVIDER).apply {
                    this.latitude = lat
                    this.longitude = long
                    this.accuracy = 10.0F
                    this.time = System.currentTimeMillis()
                    this.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                }

                // Remove all test providers
                if (locationManager.allProviders.contains(LocationManager.GPS_PROVIDER)) {
                    locationManager.removeTestProvider(LocationManager.GPS_PROVIDER)
                }

                // Enable test provider for mock locations
                locationManager.addTestProvider(
                    LocationManager.GPS_PROVIDER,
                    true, false, false, false,
                    true, true, true,
                    ProviderProperties.POWER_USAGE_LOW, ProviderProperties.ACCURACY_FINE
                )
                locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
                locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation)

                // Log mock location set
                Log.d("LocationStatus", "Mock location set to: $lat, $long")

                // Update map with mock location
                googleMap?.let { map ->
                    map.clear()
                    map.addMarker(MarkerOptions().position(LatLng(lat, long)).title("Mock Location"))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), 15f))
                }

            } catch (e: IllegalArgumentException) {
                Log.e("LocationStatus", "Failed to set mock location: ${e.message}")
            }
        } else {
            Log.e("LocationStatus", "Mock locations are not supported on this device (API < 31).")
        }
    }



    @SuppressLint("NewApi", "MissingPermission")
    private fun stopLocationUpdates() {
        isPaused = true
        Log.d("LocationStatus", "Location updates paused.")
        fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})

        // Set a mock location at the last known coordinates
        lastKnownLocation?.let {
            // setMockLocation(it.latitude, it.longitude)
            setMockLocation(33.789, -118.293) // DEBUG PURPOSES
        }

        // Introduce a delay to ensure mock location is set before resuming updates
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000) // 1 second delay
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {}, null)
            logLastKnownLocation()
        }

        logLastKnownLocation()
    }

    private fun logLastKnownLocation() {
        loggingJob?.cancel() // Stop any previous logging
        loggingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive && isPaused) {
                lastKnownLocation?.let {
                    Log.d("LocationStatus", "Updated location: ${it.latitude}, ${it.longitude}")
                }
                delay(2500) // Log every 2.5 seconds
            }
        }
    }
}



@Composable
fun PauseButtonWithMap(
    modifier: Modifier = Modifier,
    startLocationUpdates: () -> Unit,
    stopLocationUpdates: () -> Unit
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
        Text(
            text = "Trace Free",
            color = Color.Black,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 0.dp, bottom = 25.dp)

        )

        // Google Maps
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    onCreate(null)
                    getMapAsync { map ->
                        (context as? OnMapReadyCallback)?.onMapReady(map)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9 / 10f)
                .padding(bottom = 16.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Pause Button
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
            modifier = Modifier.size(115.dp)
                .aspectRatio(1 / 1f)
        ) {
            val text = if (isClicked) "RESUME" else "PAUSE"

            // Dynamically adjust font size based on text length
            val fontSize = animateFloatAsState(
                targetValue = if (text.length > 5) 15f else 17f,
                animationSpec = tween(durationMillis = 300), label = ""
            )

            Text(
                text = text,
                color = Color.White,
                fontSize = fontSize.value.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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