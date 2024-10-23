Here is a fully updated README, including the latest version of the code structure and detailed instructions:

---

# TraceFree App

**TraceFree** is an Android app designed to block Life360 from tracking user location and monitor the data collected by the Life360 app. This app allows users to set mock locations to avoid real-time tracking and can block Life360 traffic via VPN. 

Additionally, the project provides a Python script for logging network requests to analyze data traffic and a bash script to block Life360 servers using VPN.

## Features:
- **Mock Location**: Automatically detects the user's current location and pauses it using mock location services when the user presses the "Pause Location" button.
- **VPN-Based Blocking**: A bash script to block traffic to Life360 servers using OpenVPN.
- **Network Logger**: A Python script to log network requests and responses to see what data Life360 sends and receives.
  
---

## Code Structure

```
TraceFree/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml            # Android permissions for location and mock location
│   │       ├── java/com/example/antitrackingapp/
│   │       │   ├── MainActivity.kt            # Main Android Activity (auto-detects location & sets mock location)
│   │       └── res/
│   │           └── layout/
│   │               └── activity_main.xml      # UI layout with the "Pause Location" button
│   └── build.gradle                           # Build configuration for Android dependencies
│
├── scripts/
│   ├── NetworkLogger.py                       # Python script to log network requests and responses
│   └── vpn_block.sh                           # Bash script to block Life360 servers using VPN
│
├── .gitignore                                 # Git ignore file to exclude unnecessary files from version control
└── README.md                                  # Project README with setup, description, and instructions
```

---

## Requirements:
- **Android Device**: Developer Mode enabled, with mock location support.
- **Mock Location App**: The app uses a mock location to pause the user’s location.
- **Android Studio**: To build and run the Android app.
- **Python 3**: For the network logger script.
- **OpenVPN**: For the VPN-based blocking feature (optional).

---

## Setup Instructions:

### 1. Cloning the Project
Clone this repository to your desired directory:

```bash
git clone https://github.com/miles-akio/CECS378-TraceFree.git /path/to/your/directory
```

### 2. Building and Running the Android App
- Open **Android Studio**.
- Load the project from `/TraceFree/app`.
- Ensure your Android device has **Developer Mode** enabled and allows **Mock Location** apps.
- Run the app on your Android device. It will detect your current location automatically and allow you to pause it by pressing the **Pause Location** button.

### 3. Running the Network Logger (Optional)
The `NetworkLogger.py` script logs all network traffic to observe the requests Life360 makes. To run it:
   
1. Install the required Python package:
   ```bash
   pip install requests
   ```

2. Run the Python script:
   ```bash
   python3 scripts/NetworkLogger.py
   ```

### 4. Configuring VPN-Based Blocking (Optional)
The `vpn_block.sh` script blocks traffic to Life360 servers using OpenVPN.

1. Install OpenVPN:
   ```bash
   brew install openvpn
   ```

2. Run the VPN blocking script:
   ```bash
   chmod +x scripts/vpn_block.sh
   ./scripts/vpn_block.sh
   ```

   This script will block traffic to Life360 servers. Ensure that your VPN is correctly configured.

---

## Mock Location Feature Details

The app automatically detects the user's current location when it is opened. By pressing the **Pause Location** button, the app sets the current location as a mock location, pausing the Life360 tracking. This function works while the Android **Developer Mode** is enabled and mock location is allowed.

---

## VPN Blocking Feature Details (Optional)

The VPN-based blocking uses OpenVPN to block traffic to Life360 servers. It will prevent the app from sending or receiving any network traffic, effectively blocking tracking capabilities.

To adapt this for **macOS**, use `pfctl` instead of `iptables`. The VPN script can be modified for macOS or left as-is for Linux.

---

## Network Logger Feature (Optional)

The Python script `NetworkLogger.py` captures and logs requests to Life360 servers, allowing you to observe the type of data being sent and received. This will help you better understand how Life360 gathers and transmits location and other user data.

---

## Future Improvements
- **Advanced Mock Location**: Add features for more advanced control over mock location settings, such as setting custom coordinates.
- **Stealth Mode**: Implementing a more stealthy approach for mock location detection, improving the bypassing capabilities.
- **Cross-platform Support**: Adding support for iOS devices for similar functionality.

---

This project provides a robust solution for users looking to prevent location tracking while also monitoring Life360's data activities.
