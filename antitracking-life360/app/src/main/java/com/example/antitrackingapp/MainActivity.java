// MainActivity.java
package com.example.antitrackingapp;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isTrackingBlocked = false;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Button toggleButton = findViewById(R.id.toggleButton);

        toggleButton.setOnClickListener(view -> {
            isTrackingBlocked = !isTrackingBlocked;
            if (isTrackingBlocked) {
                blockTracking();
                toggleButton.setText("Unblock Tracking");
            } else {
                unblockTracking();
                toggleButton.setText("Block Tracking");
            }
        });
    }

    private void blockTracking() {
        // Spoof GPS location
        locationManager.addTestProvider(LocationManager.GPS_PROVIDER, false, false, false, false, true, true, 0, 5);
        Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(37.7749);  // Fake latitude
        mockLocation.setLongitude(-122.4194);  // Fake longitude
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
    }

    private void unblockTracking() {
        // Reset GPS to normal behavior (in case needed)
        locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
    }
}
