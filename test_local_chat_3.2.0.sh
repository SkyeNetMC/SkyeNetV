#!/bin/bash

# Test Script for SkyeNetV 3.2.0 Local Chat Implementation
# This script helps verify that the local chat-only implementation is working correctly

echo "===== Starting SkyeNetV 3.2.0 Local Chat Test ====="
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
PLUGIN_PATH="target/SkyeNetV-3.2.0.jar"
TEST_SERVER_PLUGINS="/path/to/test/server/plugins/"

# Check if the built plugin exists
if [ ! -f "$PLUGIN_PATH" ]; then
    echo "Error: Built plugin not found at $PLUGIN_PATH"
    exit 1
fi

echo "The plugin is ready for testing."
echo ""

echo "===== Manual Test Instructions ====="
echo "To verify the local chat implementation, please perform these tests:"
echo ""
echo "Test 1: Local Messages"
echo "1. Connect to any server"
echo "2. Send a message in chat"
echo "3. Verify that only players on your server can see it"
echo "4. Verify that you see your message only once"
echo ""
echo "Test 2: Cross-Server Messages"
echo "1. Have a second player connect to a different server"
echo "2. Send messages on both servers"
echo "3. Verify that messages do not cross servers"
echo ""
echo "Test 3: LuckPerms Integration"
echo "1. Verify that prefixes and suffixes appear correctly in chat"
echo "2. Test with players having different LuckPerms roles"
echo ""
echo "Test 4: Discord Integration"
echo "1. Verify that messages are properly forwarded to Discord if configured"
echo ""
echo "Test 5: Command Removal"
echo "1. Try the removed commands: /gc and /lc"
echo "2. Verify they are not available"
echo ""
echo "If all tests pass: Congratulations! The local chat implementation is working correctly."
echo "If any test fails: Please report which test failed and what you observed."
echo ""
echo "===== End of Test Script ====="
