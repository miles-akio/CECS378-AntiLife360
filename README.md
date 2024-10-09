# CECS 378 Sec 02 - Anti-Life 360 Android Application

## **Project Overview**

This project aims to block Life360, a popular family-tracking app, from tracking and sending the user’s location data to its servers. The app achieves this by spoofing GPS coordinates on the user’s Android device, effectively feeding false location data to Life360 and similar location-tracking apps. Additionally, optional features such as network traffic monitoring and VPN-based blocking can be implemented for enhanced privacy control.

This README provides an overview of the project, its structure, installation, and key technical concepts.

---

## **Table of Contents**

1. [Key Features](#key-features)
2. [Technologies Used](#technologies-used)
3. [Installation](#installation)
4. [Usage](#usage)
5. [Project Structure](#project-structure)
6. [Explanation of Key Components](#explanation-of-key-components)
7. [Limitations and Future Improvements](#limitations-and-future-improvements)

---

## **Key Features**

- **GPS Spoofing:** The app spoofs GPS location on Android, sending fake location data to prevent Life360 from tracking real-time location.
- **Location Blocking:** Users can toggle location tracking on or off directly from the app’s interface.
- **Optional Network Traffic Logging:** A Python script logs the network traffic between the Life360 app and its servers to better understand the data being transmitted.
- **VPN-Based Blocking (Optional):** For advanced users, a bash script provides VPN configuration to block Life360’s API requests at the network level.

---

## **Technologies Used**

- **Languages:**
  - **Java (Android):** Used for the main Android app that handles GPS spoofing and location control.
  - **Python (Optional):** Used for network traffic logging, capturing requests made to Life360.
  - **Bash (Optional):** Used for a VPN-based blocking mechanism, which redirects or blocks specific API calls from Life360.
  
- **Tools:**
  - **Android SDK:** For building the Android app.
  - **Wireshark/Fiddler:** For network traffic analysis (optional).
  - **Frida/APKTool (Optional):** For reverse engineering and analyzing Life360’s internal logic (not implemented directly in this project but useful for future improvements).
  - **OpenVPN/Shadowsocks (Optional):** For implementing a VPN to block network communication with Life360 servers.

---

## **Installation**

### **1. Prerequisites**

- **Android Studio:** Install Android Studio to build and run the Android app.
- **Python 3.x (Optional):** Required for network traffic logging.
- **OpenVPN or Shadowsocks (Optional):** For the VPN-based traffic blocking option.

### **2. Setting Up the Android App**

1. **Clone the repository** to your local machine:
   ```bash
   git clone https://github.com/your-repo/antitracking-life360.git
   cd antitracking-life360
   ```

2. **Open the project in Android Studio**:
   - Open Android Studio and select "Open an Existing Project."
   - Navigate to the cloned folder and select it.

3. **Run the app on an Android device**:
   - Ensure Developer Options are enabled on your Android device and USB Debugging is turned on.
   - Build and run the app on the connected device.

### **3. Running the Network Logging Script (Optional)**

1. **Install the required Python packages**:
   ```bash
   pip install requests
   ```

2. **Run the Python script**:
   ```bash
   python3 NetworkLogger.py
   ```

### **4. Configuring VPN-Based Blocking (Optional)**

1. **Install OpenVPN** (or any VPN of your choice):
   ```bash
   sudo apt-get install openvpn
   ```

2. **Run the Bash script** to block traffic to Life360 servers:
   ```bash
   chmod +x vpn_block.sh
   ./vpn_block.sh
   ```

---

## **Usage**

1. **Launch the Android App** on your phone and click the "Block Tracking" button to start spoofing your GPS location.
   - When GPS spoofing is active, Life360 will receive fake coordinates, effectively preventing accurate tracking.

2. **Monitor Life360 Network Traffic** by running the Python script, which will log the network requests made by Life360 for analysis.

3. **Set up VPN-based blocking** using the provided Bash script to block API calls to Life360’s servers.

---

## **Project Structure**

```
antitracking-life360/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/antitrackingapp/
│           │       └── MainActivity.java      # Main Android Activity for GPS Spoofing
│           └── res/
│               └── layout/
│                   └── activity_main.xml      # UI Layout for the app
│
├── NetworkLogger.py                           # Python script for network traffic logging
└── vpn_block.sh                               # Bash script to block Life360 traffic using a VPN
```

---

## **Explanation of Key Components**

### **1. `MainActivity.java`**
The core logic for the app is implemented here. The app uses Android’s `LocationManager` to set up a mock location provider. When the "Block Tracking" button is clicked, the app injects fake GPS coordinates (latitude/longitude) into the system, and these coordinates are picked up by apps like Life360, effectively preventing them from knowing your real location.

### **2. `activity_main.xml`**
The simple UI for the app, contains a button that users click to toggle between "Block Tracking" and "Unblock Tracking."

### **3. `NetworkLogger.py`**
A Python script that uses the `requests` library to capture HTTP requests and responses. It logs network traffic that could provide insight into what data Life360 sends or receives, offering another layer of analysis.

### **4. `vpn_block.sh`**
A Bash script that configures OpenVPN or other VPN services to block specific API calls made to Life360 servers. This is an optional feature for users who want to ensure no traffic is sent to Life360 at the network level.

---

## **Limitations and Future Improvements**

### **1. Accuracy of Spoofed Locations**
While the app fakes the GPS location, some apps may cross-check the coordinates with network or WiFi-based location services. Additional steps may be required to handle these scenarios.

### **2. SSL Pinning Bypass**
If Life360 uses SSL pinning to encrypt its traffic, you might need to use a tool like **Frida** to bypass it and inspect network traffic properly.

### **3. Multi-Platform Support**
Currently, the project focuses on Android devices. Future updates could extend the functionality to iOS with similar location spoofing techniques.

### **4. Reverse Engineering Life360**
Further reverse engineering can reveal more about how Life360 collects data. Tools like **Frida** or **APKTool** can decompile the app and provide insights into additional tracking mechanisms beyond GPS.

---

## **Conclusion**

This project offers a privacy-focused solution for users who wish to block or fake their location when using Life360. By using GPS spoofing and optional network blocking, users can protect their real location data from being tracked by third-party apps.
