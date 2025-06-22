#!/bin/bash

# Test Script for SkyeNetV 3.2.1 Reload Command
# This script helps verify that the reload command functionality works correctly

echo "===== Starting SkyeNetV 3.2.1 Reload Command Test ====="
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

echo "===== Manual Test Instructions ====="
echo ""
echo "To verify the reload command functionality, please perform these tests:"
echo ""
echo "Test 1: Main Plugin Information Command"
echo "1. Start your Velocity server with the plugin installed"
echo "2. Run command: /skyenetv"
echo "3. Verify you see the plugin information including version 3.2.1"
echo "4. Run command: /snv"
echo "5. Verify you see the same information (alias works)"
echo ""
echo "Test 2: Reload Command with Permission"
echo "1. Make sure you have the 'skyenetv.admin' permission"
echo "2. Edit the config.yml and make a small change (e.g., change a message)"
echo "3. Run command: /skyenetv reload"
echo "4. Verify you get the success message"
echo "5. Check that your change took effect"
echo ""
echo "Test 3: Reload Command with Alias"
echo "1. Make another small change to config.yml"
echo "2. Run command: /snv reload"
echo "3. Verify it works the same as the main command"
echo ""
echo "Test 4: Permission Check"
echo "1. Login with a user without 'skyenetv.admin' permission"
echo "2. Try to run: /skyenetv reload"
echo "3. Verify you get a permission denied message"
echo ""
echo "Test 5: Discord Reload Check"
echo "1. Try to run: /discord reload"
echo "2. Verify that this command is no longer available"
echo "3. Edit Discord settings in config.yml"
echo "4. Run: /skyenetv reload"
echo "5. Verify the Discord connection is reinitialied with new settings"
echo ""
echo "If all tests pass: Congratulations! The reload command is working correctly."
echo "If any test fails: Please report which test failed and what you observed."
echo ""
echo "===== End of Test Script ====="
