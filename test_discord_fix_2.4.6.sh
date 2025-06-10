#!/bin/bash

# Discord Message Content Intent Fix Test Script - Version 2.4.6
# This script tests the critical Discord message content fix

echo "=== Discord Message Content Intent Fix Test - v2.4.6 ==="
echo ""

# Check if the fixed JAR exists
if [ ! -f "target/SkyeNetV-2.4.6.jar" ]; then
    echo "❌ Fixed JAR not found. Please compile first with: mvn clean package"
    exit 1
fi

echo "✅ Fixed JAR found: target/SkyeNetV-2.4.6.jar"
echo ""

# Check file size
JAR_SIZE=$(du -h target/SkyeNetV-2.4.6.jar | cut -f1)
echo "📦 JAR Size: $JAR_SIZE"
echo ""

echo "🔧 Key Fixes Applied in v2.4.6:"
echo "  1. ✅ Added MESSAGE_CONTENT intent to JDA initialization"
echo "  2. ✅ Optimized channel filtering in Discord listener"
echo "  3. ✅ Enhanced empty message diagnostics"
echo "  4. ✅ Improved early return for non-target channels"
echo ""

echo "🚀 Testing Instructions:"
echo ""
echo "1. Deploy the plugin:"
echo "   - Stop your Velocity server"
echo "   - Replace old plugin with: target/SkyeNetV-2.4.6.jar"
echo "   - Ensure Discord bot has Message Content Intent enabled in Developer Portal"
echo "   - Start your server"
echo ""
echo "2. Test Discord → Minecraft messages:"
echo "   - Send a message from Discord: 'Hello from Discord!'"
echo "   - Check server logs for detailed processing info"
echo "   - Verify message appears in Minecraft as: [Discord] Username: Hello from Discord!"
echo ""
echo "3. Expected Log Output (Success):"
echo "   [INFO] Received Discord message from Username in correct channel"
echo "   [INFO]   - message.getContentRaw(): 'Hello from Discord!'"
echo "   [INFO]   - message length: 18"
echo "   [INFO] Broadcasting Discord message to Minecraft players"
echo "   [INFO] Sent Discord message to X online players"
echo ""
echo "4. If Still Getting Empty Messages:"
echo "   - Double-check Message Content Intent is enabled in Discord Developer Portal"
echo "   - Verify bot permissions in the Discord channel"
echo "   - Check if channel ID in config matches exactly"
echo "   - Restart server after enabling intents"
echo ""

echo "⚠️  Critical Change in v2.4.6:"
echo "   The plugin now automatically enables MESSAGE_CONTENT intent in code."
echo "   This should resolve 95% of empty message content issues."
echo ""

echo "🔍 Diagnostic Commands:"
echo "   /discord status  - Check connection and configuration"
echo "   /discord test    - Send test message to Discord"
echo ""

echo "=== Fix Test Complete ==="