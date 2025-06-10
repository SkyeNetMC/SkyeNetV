#!/bin/bash

# Discord Message Content Test Script
# This script helps verify that Discord messages are working correctly

echo "=== Discord Message Content Test ==="
echo ""

# Check if debug JAR exists
if [ ! -f "target/SkyeNetV-2.4.3.jar" ]; then
    echo "❌ Debug JAR not found. Please compile first with: mvn clean package"
    exit 1
fi

echo "✅ Debug JAR found: target/SkyeNetV-2.4.3.jar"
echo ""

# Check configuration
if [ ! -f "config.yml" ]; then
    echo "❌ Configuration file not found: config.yml"
    echo "   Make sure to create and configure your Discord bot settings."
    exit 1
fi

echo "✅ Configuration file found: config.yml"
echo ""

# Show current message format from config
echo "📋 Current Discord message format:"
grep -A 5 "message_format:" config.yml || echo "   Could not find message_format in config"
echo ""

# Show file sizes for verification
echo "📊 File Information:"
echo "   Plugin JAR size: $(ls -lh target/SkyeNetV-2.4.3.jar | awk '{print $5}')"
echo "   Config file size: $(ls -lh config.yml | awk '{print $5}')"
echo ""

echo "🚀 Next Steps:"
echo "1. Copy the JAR to your server: cp target/SkyeNetV-2.4.3.jar /path/to/server/plugins/"
echo "2. Start your server and check for Discord connection"
echo "3. Send a test message from Discord"
echo "4. Check server logs for detailed debug output"
echo ""

echo "🔧 Quick Commands to Test:"
echo "   /discord status   - Check connection"
echo "   /discord test     - Send test message to Discord"
echo "   /discord reload   - Reload configuration"
echo ""

echo "📋 What to Look For in Logs:"
echo "   'Received Discord message from...' - Message received"
echo "   'message length: X' - Should be > 0"
echo "   'Formatted message after replacement...' - Should include content"
echo "   'Sent Discord message to X online players' - Broadcast successful"
echo ""

echo "⚠️  If message length is 0, enable 'Message Content Intent' in Discord Developer Portal"
echo ""

echo "=== Test script complete ==="
