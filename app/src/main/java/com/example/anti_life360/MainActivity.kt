/*
This code is for testing demonstrations only. Your mock location settings must be turned off,
otherwise the location will always display at Google headquarters and the simulation route in
extended controls will not work."
*/

package com.example.anti_life360

import android.Manifest
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
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2500).build()

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
                                Log.d("Marker", "Marker updated.")
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