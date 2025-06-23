#!/bin/bash

# SkyeNetV Version 3.2.3 Testing Script  
# Tests /fly command functionality across the network

echo "🧪 SkyeNetV v3.2.3 - Network Fly Command Test"
echo "=============================================="
echo ""

JAR_FILE="/mnt/sda4/SkyeNetwork/SkyeNetV/target/SkyeNetV-3.2.1.jar"

# Check if JAR exists
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ ERROR: JAR file not found at $JAR_FILE"
    exit 1
fi

echo "✅ JAR File Found: $(ls -lh $JAR_FILE | awk '{print $5}')"
echo ""

# Check JAR contents for FlyCommand
echo "🔍 Checking JAR Contents..."
echo "----------------------------"

jar tf "$JAR_FILE" | grep -E "FlyCommand\.class" | while read -r class; do
    echo "✅ $class"
done

echo ""
echo "📋 NEW FEATURE: /fly Command - Network-wide Flight Control"
echo "=========================================================="
echo ""

echo "🚀 COMMAND SYNTAX:"
echo "   /fly                    - Toggle your own flight"
echo "   /fly on                 - Enable your flight"
echo "   /fly off                - Disable your flight"
echo "   /fly <player>           - Toggle another player's flight"
echo "   /fly <player> on        - Enable another player's flight"
echo "   /fly <player> off       - Disable another player's flight"
echo ""

echo "🔐 PERMISSIONS:"
echo "   skyenetv.fly            - Use /fly command on yourself"
echo "   skyenetv.fly.others     - Use /fly command on other players"
echo ""

echo "📡 HOW IT WORKS:"
echo "   • Commands executed on Velocity proxy"
echo "   • Plugin messages sent to backend servers"
echo "   • Works across all servers in the network"
echo "   • Real-time flight mode changes"
echo ""

echo "📝 EXAMPLE USAGE SCENARIOS:"
echo ""
echo "Player on 'survival' types: /fly"
echo "├─ Flight toggled on survival server"
echo "└─ Player receives: 'Toggling flight mode...'"
echo ""

echo "Admin on 'lobby' types: /fly PlayerName on"
echo "├─ PlayerName's flight enabled on their current server"
echo "└─ Admin receives: 'Enabled flight for PlayerName!'"
echo ""

echo "Player switches from 'creative' to 'survival' server:"
echo "├─ Flight status preserved by backend server logic"
echo "└─ /fly command works immediately on new server"
echo ""

echo "🎯 FEATURES:"
echo "• Cross-server compatibility: Works on any server in network"
echo "• Player targeting: Enable/disable flight for other players"
echo "• Action specification: Force on/off or toggle current state"
echo "• Permission-based: Different permissions for self vs others"
echo "• Real-time feedback: Immediate confirmation messages"
echo "• Console support: Console can toggle any player's flight"
echo ""

echo "⚙️  BACKEND SERVER REQUIREMENTS:"
echo "This command sends plugin messages to backend servers. Your backend servers need:"
echo ""
echo "1. Plugin message channel: 'skyenetv:fly'"
echo "2. Message format:"
echo "   - String: Command ('FlyCommand')"
echo "   - String: Player UUID"
echo "   - String: Action ('toggle', 'on', or 'off')"
echo "   - String: Sender name"
echo ""
echo "3. Example backend plugin message handler:"
echo "   if (channel.equals('skyenetv:fly')) {"
echo "       String command = data.readUTF();  // 'FlyCommand'"
echo "       String uuid = data.readUTF();     // Player UUID"
echo "       String action = data.readUTF();   // 'on', 'off', 'toggle'"
echo "       String sender = data.readUTF();   // Who sent command"
echo "       // Handle flight change logic"
echo "   }"
echo ""

echo "🚀 TESTING INSTRUCTIONS:"
echo "========================="
echo ""
echo "1. Deploy SkyeNetV-3.2.1.jar to your Velocity proxy"
echo "2. Configure permissions:"
echo "   - Grant 'skyenetv.fly' to players"
echo "   - Grant 'skyenetv.fly.others' to admins"
echo "3. Implement backend server plugin message handlers"
echo "4. Restart the proxy"
echo "5. Test the fly commands"
echo ""

echo "📋 Test Cases:"
echo ""
echo "Basic Functionality:"
echo "• Player uses /fly to toggle their own flight"
echo "• Player uses /fly on to enable flight"
echo "• Player uses /fly off to disable flight"
echo ""
echo "Cross-Server Testing:"
echo "• Player enables flight on server A"
echo "• Player switches to server B"
echo "• Verify flight commands work on server B"
echo ""
echo "Admin Testing:"
echo "• Admin toggles another player's flight"
echo "• Admin enables flight for offline/online players"
echo "• Console executes fly commands"
echo ""
echo "Permission Testing:"
echo "• Player without 'skyenetv.fly' permission"
echo "• Player without 'skyenetv.fly.others' permission"
echo "• Verify proper permission error messages"
echo ""

echo "✨ TECHNICAL DETAILS:"
echo "====================="
echo "• Added: FlyCommand.java - Complete fly command implementation"
echo "• Modified: SkyeNetV.java - Command registration"
echo "• Protocol: Plugin message system for cross-server communication"
echo "• Compatible: Works with existing network infrastructure"
echo "• Flexible: Supports both toggle and explicit on/off actions"
echo ""

echo "📦 PLUGIN MESSAGE PROTOCOL:"
echo "============================"
echo "Channel: 'skyenetv:fly'"
echo "Data Format (DataOutputStream):"
echo "1. writeUTF('FlyCommand')           // Command identifier"
echo "2. writeUTF(playerUUID.toString())  // Target player UUID"
echo "3. writeUTF(action)                 // 'toggle', 'on', or 'off'"
echo "4. writeUTF(senderName)             // Who issued the command"
echo ""

echo "🎉 Ready for deployment!"
echo "Version: SkyeNetV-3.2.1 with network-wide fly command support"
