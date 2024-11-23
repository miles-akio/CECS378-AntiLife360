package com.example.anti_life360

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.anti_life360.ui.theme.AntiLife360Theme
import androidx.compose.ui.platform.LocalConfiguration
import androix.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // This line references the activity_main.xml layout
    }
}

// MainActivity class, the entry point for the Android app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Enabling edge-to-edge display (no system UI)

        // Check if the app has the necessary location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request location permission if not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Set the content of the app to the Composable UI elements
        setContent {
            AntiLife360Theme {  // Use the defined theme for styling
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding: PaddingValues ->
                    // Display the PauseButton composable with inner padding
                    PauseButton(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Composable function to display a pause/resume button and a message
@Composable
fun PauseButton(name: String, modifier: Modifier = Modifier) {
    // State to track whether the button is clicked (paused or resumed)
    var isClicked by remember { mutableStateOf(false) }

    // Get the screen width to make the message responsive
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // Column layout to center the button and message on the screen
    Column(
        modifier = Modifier
            .fillMaxSize()  // Fill the entire screen
            .padding(16.dp),  // Padding around the content
        verticalArrangement = Arrangement.Center,  // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally  // Center horizontally
    ) {
        // Button that toggles between pause and resume states
        Button(
            onClick = { isClicked = !isClicked },  // Toggle state on click
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isClicked) Color(0xFF008421) else Color(0xFFcf142b)  // Green if paused, red if active
            ),
            shape = CircleShape,  // Rounded button shape
            modifier = Modifier.size(130.dp)  // Fixed button size
        ) {
            // Text inside the button, changes based on isClicked state
            Text(text = if (isClicked) "RESUME" else "PAUSE",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)  // Styled text for the button
        }

        Spacer(modifier = Modifier.height(16.dp))  // Space between button and message

        // Text below the button displaying the current status of location tracking
        Text(
            text = if (isClicked) "Your location is paused." else "Your location is currently being tracked.",
            color = Color.Black,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,  // Center the text
            fontWeight = FontWeight.Normal,
            modifier = Modifier.widthIn(max = screenWidth * 0.7f)  // Ensure the text fits on screen
        )
    }
}

// Preview function to display the PauseButton composable in the Android Studio preview
@Preview(showBackground = true)
@Composable
fun PauseButtonPreview() {
    AntiLife360Theme {  // Use the defined theme for preview
        PauseButton("Android")  // Display the PauseButton composable
    }
}
