#!/bin/bash

# SkyeNetV 2.4.5 Testing Script
# Tests all new features and fixes

echo "=== SkyeNetV 2.4.5 Testing Guide ==="
echo ""

# Check if JAR exists
if [ ! -f "target/SkyeNetV-2.4.5.jar" ]; then
    echo "❌ Version 2.4.5 JAR not found."
    exit 1
fi

echo "✅ Version 2.4.5 JAR found: target/SkyeNetV-2.4.5.jar"
echo "   Size: $(ls -lh target/SkyeNetV-2.4.5.jar | awk '{print $5}')"
echo ""

echo "🔧 Issues Fixed in 2.4.5:"
echo "   1. ✅ Message duplication when global chat enabled"
echo "   2. ✅ Discord → Minecraft messages not working"
echo "   3. ✅ Added global chat join/leave notifications"
echo ""

echo "🧪 Testing Checklist:"
echo ""

echo "📋 Global Chat Testing:"
echo "   1. Join server → Should see: 'You are not connected to global chat. Type /gc to toggle.'"
echo "   2. Run /gc → Should see: '🌐 PlayerName joined global chat'"
echo "   3. Send chat message → Should appear ONCE with globe icon (no duplication)"
echo "   4. Run /gc again → Should see: '🌐 PlayerName left global chat'"
echo "   5. Send message → Should appear as normal server chat only"
echo ""

echo "📋 Local Chat Testing:"
echo "   1. Run /lc Hello local → Should appear as: 'PlayerName: Hello local'"
echo "   2. No [LOCAL] prefix should be shown"
echo "   3. Only players on same server should see it"
echo ""

echo "📋 Discord Integration Testing:"
echo "   1. Send message from Minecraft → Should appear in Discord"
echo "   2. Send message from Discord → Should appear in Minecraft"
echo "   3. If Discord → Minecraft fails:"
echo "      - Enable 'Message Content Intent' in Discord Developer Portal"
echo "      - Check /discord status for connection info"
echo "      - Use debug version if needed"
echo ""

echo "📋 Additional Commands to Test:"
echo "   /gc settings              # Interactive settings menu"
echo "   /gc toggle icon           # Toggle globe emoji"
echo "   /discord status           # Check Discord connection"
echo "   /discord test             # Send test to Discord"
echo "   /rules                    # View server rules"
echo ""

echo "⚠️  Expected Behavior Changes:"
echo "   - NO message duplication when global chat is on"
echo "   - Global chat join/leave messages broadcast to all users"
echo "   - New player notification for disabled global chat"
echo "   - Clean local chat format without prefix"
echo ""

echo "🚀 Deployment:"
echo "   1. cp target/SkyeNetV-2.4.5.jar /path/to/server/plugins/"
echo "   2. Remove old version: rm SkyeNetV-2.4.4.jar"
echo "   3. Restart server"
echo "   4. Test all features above"
echo ""

echo "📁 Documentation:"
echo "   - VERSION-2.4.5-COMPLETE.md → Complete feature summary"
echo "   - CHANGELOG.md → Detailed version history"
echo "   - DISCORD-MESSAGE-SOLUTION.md → Discord troubleshooting"
echo ""

echo "=== Testing Guide Complete ==="
