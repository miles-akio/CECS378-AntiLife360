# CECS378-AntiLife360

## Project Overview

The **CECS378-AntiLife360** project is an Android application designed to help users protect their privacy by blocking the Life360 app from tracking their location data. The application achieves this by utilizing mock location functionality available on Android devices. By setting a fake GPS location, users can prevent Life360 from obtaining their real location, thereby enhancing their privacy.

### Features

- **Mock Location Setting**: The application allows users to set a mock location on their Android device, effectively masking their real GPS coordinates.
- **Network Request Monitoring**: The app can log network requests made by Life360 or any other specified API endpoints, providing insights into the data being sent and received.
- **User-Friendly Interface**: The application includes a simple interface for users to easily set their desired mock location.

### Technologies Used

- **Programming Language**: Kotlin.
- **Android SDK**: Utilizes Android APIs for location services.
- **OkHttp Library**: For monitoring network requests and responses.

## Getting Started

### Prerequisites

Before running the application, ensure you have the following:

- Android Studio installed on your development machine.
- A physical Android device or emulator configured with Developer Options enabled.
- The ability to allow mock locations on your Android device.

### Installation

1. **Clone the Repository**: 
   Clone this repository to your local machine using the following command:
   ```bash
   git clone https://github.com/miles-akio/CECS378-AntiLife360.git
   ```

2. **Open the Project in Android Studio**: 
   Navigate to the cloned project directory and open it in Android Studio.

3. **Gradle Sync**: 
   Allow Android Studio to sync the project and download any necessary dependencies.

4. **Configure Permissions**: 
   Ensure that the necessary permissions for accessing location and mock locations are declared in the `AndroidManifest.xml` file.

### Running the Application

1. **Connect Your Device**: 
   Connect your Android device to your computer or use an emulator.

2. **Set Up Mock Location**: 
   Ensure that your device's Developer Options are enabled and allow mock locations.

3. **Build and Run**: 
   Click on the 'Run' button in Android Studio to build and run the application on your device.

4. **Set Mock Location**: 
   Tap the "Set Mock Location" button in the app to set the desired GPS coordinates.

### Using the Application

- **Set Mock Location**: After launching the app, click the "Set Mock Location" button to set the predefined coordinates. The app will display a message confirming that the mock location has been set.

- **Monitor Network Requests**: The application logs network requests and responses, which can be viewed in the console or logcat.

## Code Structure

The project has the following structure:

```
CECS378-AntiLife360/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/antitrackingapp/
│   │       │       └── MainActivity.kt
│   │       └── res/
│   │           └── layout/
│   │               └── activity_main.xml
│   └── build.gradle
├── .gitignore
└── build.gradle
```

### Main Components

- **MainActivity.kt**: The primary activity containing the logic to set the mock location and monitor network requests.
- **activity_main.xml**: The layout file for the main activity, defining the user interface.

## Contributing

Contributions are welcome! If you would like to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Make your changes and commit them (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Acknowledgments

- [OkHttp](https://square.github.io/okhttp/) for the networking library used in this project.
- Android documentation and community forums for support and guidance.

---

Feel free to modify any sections or add additional details based on your project requirements!
