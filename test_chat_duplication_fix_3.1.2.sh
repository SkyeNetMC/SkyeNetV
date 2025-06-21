#!/bin/bash

# Test Script for SkyeNetV 3.1.2 Chat Duplication Fix
# This script helps verify that the chat duplication fix is working properly

echo "===== Starting SkyeNetV 3.1.2 Chat Duplication Fix Test ====="
echo ""

# Build the plugin
echo "Step 1: Building the plugin with Maven..."
mvn clean package
if [ $? -ne 0 ]; then
    echo "Error: Maven build failed."
    exit 1
fi
echo "✅ Plugin built successfully."
echo ""

# Copy the built plugin to our test server
echo "Step 2: Preparing test environment..."
PLUGIN_PATH="target/SkyeNetV-3.1.2.jar"
TEST_SERVER_PLUGINS="/path/to/test/server/plugins/"

# Check if the built plugin exists
if [ ! -f "$PLUGIN_PATH" ]; then
    echo "Error: Built plugin not found at $PLUGIN_PATH"
    exit 1
fi

echo "The plugin is ready for testing."
echo ""

echo "===== Manual Test Instructions ====="
echo "To verify the chat duplication fix, please perform these tests:"
echo ""
echo "Test 1: Global Chat ON - Sender Message"
echo "1. Enable global chat with '/gc'"
echo "2. Send any message in chat"
echo "3. Verify you see your message ONCE with the globe emoji (🌐)"
echo ""
echo "Test 2: Global Chat OFF - Sender Message"
echo "1. Disable global chat with '/gc'"
echo "2. Send any message in chat"
echo "3. Verify you see your message ONCE without the globe emoji"
echo ""
echo "Test 3: Global Chat Command - Explicit Message"
echo "1. Run '/gc test message'"
echo "2. Verify you see your message ONCE with the globe emoji (🌐)"
echo "3. This should work regardless of your current global chat toggle state"
echo ""
echo "Test 4: Cross-Server Communication"
echo "1. Have a second player on another server"
echo "2. Enable global chat for both players"
echo "3. Send messages and verify neither player sees duplicates"
echo ""
echo "If all tests pass: Congratulations! The duplicate chat issue has been fixed."
echo "If any test fails: Please report which test failed and what you observed."
echo ""
echo "===== End of Test Script ====="
