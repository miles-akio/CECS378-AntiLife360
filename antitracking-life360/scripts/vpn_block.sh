#!/bin/bash
# Script to block traffic to Life360 servers using OpenVPN

sudo openvpn --config /path/to/vpn/config.ovpn &

# Block Life360 traffic with iptables (Linux specific, macOS uses pfctl)
if [[ "$(uname)" == "Linux" ]]; then
    sudo iptables -A INPUT -s life360.com -j DROP
    sudo iptables -A OUTPUT -d life360.com -j DROP
else
    echo "This script is designed for Linux systems. Update for macOS if necessary."
fi

echo "Life360 tracking has been blocked!"
