#!/bin/bash
# Test script for global chat message duplication fix in v3.1.1

echo "======================================="
echo "SkyeNetV 3.1.1 Global Chat Duplication Fix Test"
echo "======================================="

# Check if plugin is running
if [ ! -f "./target/SkyeNetV-3.1.1.jar" ]; then
  echo "ERROR: Plugin JAR not found. Please build the plugin first."
  echo "Run: mvn clean package"
  exit 1
fi

echo "Test scenarios to verify manually:"
echo ""
echo "1. GLOBAL CHAT ON - SENDING NORMAL MESSAGE"
echo "   - Type a normal message"
echo "   - ✓ Message should appear once with globe emoji"
echo "   - ✓ Message should be visible to all players with global chat ON"
echo "   - ✓ Message should be visible to players on same server with global chat OFF"
echo ""
echo "2. GLOBAL CHAT OFF - SENDING NORMAL MESSAGE"
echo "   - Type /gc to turn global chat OFF"
echo "   - Type a normal message"
echo "   - ✓ Message should appear once WITHOUT globe emoji"
echo "   - ✓ Message should ONLY be visible to players on same server"
echo "   - ✓ Message should NOT be visible to players on other servers"
echo ""
echo "3. USING /GC COMMAND WITH MESSAGE"
echo "   - Type \"/gc test message\""
echo "   - ✓ Message should appear with globe emoji"
echo "   - ✓ Message should be visible to all players with global chat ON"
echo "   - ✓ Message should be visible to players on same server with global chat OFF"
echo "   - ✓ This should work regardless of your global chat toggle state"
echo ""
echo "4. QUICK TOGGLE TEST"
echo "   - Toggle global chat ON/OFF several times"
echo "   - Send messages after each toggle"
echo "   - ✓ No duplicate messages should appear"
echo ""
echo "If all checks pass, the duplication bug has been fixed!"
echo "======================================="
