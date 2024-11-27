## TraceFree

TraceFree is an Android application designed to provide users with control over their location data by simulating mock locations.
This project demonstrates how to manipulate location services using Android’s FusedLocationProviderClient and other key APIs, offering insights
into digital privacy and app development.

## Features

- Pause and resume location tracking.
- Simulate mock locations to override real device coordinates.
- Test compatibility with location-dependent apps like Google Maps and - Life360.
- Dynamic location updates using FusedLocationProviderClient.

## Setup and Installation

### Prerequisites

- Android Studio (latest version recommended)
- Android SDK tools and emulator
- A Google Maps API key

### Steps

1.  Clone this repository.

```bash
git clone https://github.com/miles-akio/CECS378-TraceFree.git
```

2. Open the project in Android Studio.

3. Sync the Gradle files to install dependencies.

4. Create a secrets.properties file in the root of the project (e.g., GitHub\CECS378-SemesterProject).

**Paste:**

```
MAPS_API_KEY=YOUR_API_KEY
```

5. Create a local.defaults.properties file in the same folder.

**Paste:**

```
MAPS_API_KEY=DEFAULT_API_KEY
```

6. Verify that local.properties exists but does not contain a MAPS_API_KEY variable.

7. Set up an emulator or connect a physical Android device for testing.

- Ensure Mock Location is enabled in developer settings.

8. Run the app:

## How It Works

TraceFree uses the following components:

- FusedLocationProviderClient: Provides location updates while considering GPS and Wi-Fi data for accurate positioning.
- LocationManager: Handles mock location injection and updates.
- setMockLocation(): Overrides the device’s location with user-defined coordinates when location tracking is paused.

The app allows users to:

1. Pause location tracking, freezing the marker at the last known coordinates.
2. Resume location tracking, resetting the location updates to the real-time position.
3. Test the mock location in third-party apps, ensuring proper functionality.

## License

This project is licensed under the MIT License.
