#!/bin/bash

# Local Chat Test Script for Version 2.4.4
# This script helps verify that local chat is working without the LOCAL prefix

echo "=== SkyeNetV 2.4.4 Local Chat Test ==="
echo ""

# Check if JAR exists
if [ ! -f "target/SkyeNetV-2.4.4.jar" ]; then
    echo "❌ Version 2.4.4 JAR not found. Please compile first with: mvn clean package"
    exit 1
fi

echo "✅ Version 2.4.4 JAR found: target/SkyeNetV-2.4.4.jar"
echo "   Size: $(ls -lh target/SkyeNetV-2.4.4.jar | awk '{print $5}')"
echo ""

# Show local chat command info
echo "📋 Local Chat Command Information:"
echo "   Command: /lc <message>"
echo "   Alias: /localchat <message>"
echo "   Purpose: Send messages to players on the same server only"
echo ""

echo "🎯 Expected Behavior (Version 2.4.4):"
echo "   Before: [LOCAL] PlayerName: Hello server"
echo "   After:  PlayerName: Hello server"
echo ""

echo "✨ Key Features:"
echo "   • No [LOCAL] prefix shown"
echo "   • Messages stay on current server only" 
echo "   • LuckPerms prefix/colors still applied"
echo "   • No Discord integration (stays local)"
echo "   • No global chat broadcast"
echo ""

echo "🧪 Testing Steps:"
echo "1. Install: cp target/SkyeNetV-2.4.4.jar /path/to/server/plugins/"
echo "2. Restart server"
echo "3. Join server with test player"
echo "4. Run: /lc Hello local chat test"
echo "5. Verify message appears as: 'PlayerName: Hello local chat test'"
echo "6. Verify message only visible to players on same server"
echo ""

echo "🔧 Additional Commands to Test:"
echo "   /gc           - Toggle global chat"
echo "   /discord test - Test Discord integration"
echo "   /rules        - View server rules"
echo ""

echo "📊 Version Comparison:"
echo "   v2.4.3: Local chat had [LOCAL] prefix"
echo "   v2.4.4: Local chat has NO prefix (clean format)"
echo ""

echo "⚠️  Note: This change only affects /lc command display"
echo "   All other features remain unchanged from v2.4.3"
echo ""

echo "=== Local Chat Test Guide Complete ==="
