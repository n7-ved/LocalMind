#!/bin/bash

APP_NAME="LocalMind"

# Step 1: Find package name by app name
PACKAGE_NAME=$(adb shell pm list packages | grep -i "$APP_NAME" | awk -F':' '{print $2}')

if [ -z "$PACKAGE_NAME" ]; then
  echo "App '$APP_NAME' not found on device."
  exit 1
fi

echo "Found Package: $PACKAGE_NAME"

# Step 2: Show current app storage info
echo "App Storage Info:"
adb shell dumpsys package $PACKAGE_NAME | grep -i 'dataDir\|codePath'

# Step 3: Clear Cache (best effort)
echo "Clearing Cache (if accessible)..."
adb shell "rm -rf /data/data/$PACKAGE_NAME/cache/*" 2>/dev/null || echo "Cache clear command may be restricted on non-rooted devices."

# Step 4: Clear App Data (Factory Reset app)
echo "Clearing App Data..."
adb shell pm clear $PACKAGE_NAME

# Step 5: Confirm data cleared
echo "Done. App data & cache cleared for $PACKAGE_NAME"