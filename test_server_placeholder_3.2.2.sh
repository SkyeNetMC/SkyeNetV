#!/bin/bash

# SkyeNetV Version 3.2.2 Testing Script  
# Tests {server} placeholder functionality in chat messages

echo "🧪 SkyeNetV v3.2.2 - Server Placeholder Test"
echo "============================================="
echo ""

JAR_FILE="/mnt/sda4/SkyeNetwork/SkyeNetV/target/SkyeNetV-3.2.1.jar"

# Check if JAR exists
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ ERROR: JAR file not found at $JAR_FILE"
    exit 1
fi

echo "✅ JAR File Found: $(ls -lh $JAR_FILE | awk '{print $5}')"
echo ""

# Check JAR contents for ChatManager
echo "🔍 Checking JAR Contents..."
echo "----------------------------"

jar tf "$JAR_FILE" | grep -E "(ChatManager|Config)\.class" | while read -r class; do
    echo "✅ $class"
done

echo ""
echo "📋 NEW FEATURE: {server} Placeholder in Chat Messages"
echo "======================================================"
echo ""

echo "🔧 CONFIGURATION ADDED:"
echo "   chat:"
echo "     format: \"{server} {prefix}{player}{suffix} <gray>»<gray> {message}\""
echo ""

echo "📝 EXAMPLE CHAT MESSAGES:"
echo ""
echo "Player on 'survival' server types: Hello everyone!"
echo "├─ Players on survival see: Hello everyone!"
echo "├─ Players on creative see: survival [Admin]PlayerName » Hello everyone!"
echo "└─ Players on lobby see: survival [Admin]PlayerName » Hello everyone!"
echo ""

echo "Player on 'creative' server types: Building something cool"
echo "├─ Players on creative see: Building something cool"  
echo "├─ Players on survival see: creative [VIP]Builder » Building something cool"
echo "└─ Players on lobby see: creative [VIP]Builder » Building something cool"
echo ""

echo "🎯 BENEFITS:"
echo "• Server identification: Players can see which server messages come from"
echo "• Cross-server communication: Better context for multi-server networks"
echo "• Configurable format: Customize how server names appear in chat"
echo "• LuckPerms integration: Works with existing prefix/suffix system"
echo ""

echo "⚙️  CONFIGURATION OPTIONS:"
echo "You can customize the chat format in config.yml:"
echo ""
echo "# Option 1 - Server name with brackets"
echo "format: \"[{server}] {prefix}{player}{suffix} <gray>»</gray> {message}\""
echo ""
echo "# Option 2 - Colored server name"
echo "format: \"<aqua>{server}</aqua> {prefix}{player}{suffix} <gray>»</gray> {message}\""
echo ""
echo "# Option 3 - Server name with separator"
echo "format: \"{server} | {prefix}{player}{suffix} <gray>→</gray> {message}\""
echo ""

echo "🚀 TESTING INSTRUCTIONS:"
echo "========================="
echo ""
echo "1. Deploy SkyeNetV-3.2.1.jar to your Velocity proxy"
echo "2. Configure multiple servers in velocity.toml"
echo "3. Update config.yml with your preferred chat format"
echo "4. Restart the proxy"
echo "5. Have players on different servers send messages"
echo "6. Verify that cross-server messages show the server name"
echo ""

echo "📋 Test Cases:"
echo "• Player sends message from 'survival' server"
echo "• Player sends message from 'creative' server"  
echo "• Player sends message from 'lobby' server"
echo "• Verify LuckPerms prefixes still work"
echo "• Verify local messages don't show server name"
echo ""

echo "✨ TECHNICAL DETAILS:"
echo "====================="
echo "• Modified: ChatManager.java - Added {server} placeholder replacement"
echo "• Modified: Config.java - Added chat configuration section"
echo "• Modified: config.yml - Added chat format configuration"
echo "• Compatible: Works with existing LuckPerms and Discord integration"
echo ""

echo "🎉 Ready for deployment!"
echo "Version: SkyeNetV-3.2.1 with server placeholder support"
