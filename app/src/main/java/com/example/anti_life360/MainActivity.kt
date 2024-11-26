package com.example.anti_life360

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

class MainActivity : ComponentActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var lastKnownLocation: LatLng? = null
    private var loggingJob: Job? = null
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

    private fun stopLocationUpdates() {
        isPaused = true
        fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
        Log.d("LocationStatus", "Location updates paused.")
        logLastKnownLocation()
    }

    private fun logLastKnownLocation() {
        loggingJob?.cancel() // Stop any previous logging
        loggingJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive && isPaused) {
                lastKnownLocation?.let {
                    Log.d("LocationStatus", "Last known location: ${it.latitude}, ${it.longitude}")
                }
                delay(5000) // Log every 5 seconds
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Trace Free",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 25.dp),
            textAlign = TextAlign.Center
        )

        // Google Map
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
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Pause Button
        Button(
            onClick = {
                isClicked = !isClicked
                if (isClicked) {
                    stopLocationUpdates()
                    Log.d("LocationStatus", "Location updates paused.")
                } else {
                    startLocationUpdates()
                    Log.d("LocationStatus", "Location tracking resumed.")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isClicked) Color.Green else Color.Red
            ),
            modifier = Modifier.size(115.dp)
        ) {
            Text(
                text = if (isClicked) "RESUME" else "PAUSE",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isClicked) "Your location is paused." else "Your location is currently \n being tracked.",
            color = Color.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
        )
    }
}
