#!/bin/bash

# Tally Tracker APK Build Script
# This script builds the Android APK for the tally-tracker app

# Exit on error
set -e  

echo "--- Building Tally Tracker APK ---"
echo "--- Cleaning previous builds ---"
./gradlew clean

echo "--- Building APK ---"
./gradlew assembleDebug
echo "--- Build complete, APK build location below ---"
ls -lh app/build/outputs/apk/debug/app-debug.apk

# Miscellaneous
#
# To install on connected device:
# adb install app/build/outputs/apk/debug/app-debug.apk
# 
# To install on emulator:
# adb -e install app/build/outputs/apk/debug/app-debug.apk