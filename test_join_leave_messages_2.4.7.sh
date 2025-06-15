#!/bin/bash

echo "==============================================="
echo "   SkyeNetV 2.4.7 - Join/Leave Message Test   "
echo "==============================================="
echo ""

# Check JAR file
JAR_FILE="/mnt/sda4/SkyeNetwork/SkyeNetV/target/SkyeNetV-2.4.7.jar"
if [ -f "$JAR_FILE" ]; then
    JAR_SIZE=$(ls -lh "$JAR_FILE" | awk '{print $5}')
    echo "✅ Plugin JAR: SkyeNetV-2.4.7.jar ($JAR_SIZE)"
else
    echo "❌ Plugin JAR not found!"
    exit 1
fi

echo ""
echo "🎯 NEW JOIN/LEAVE MESSAGE FORMATS:"
echo ""
echo "📥 JOIN MESSAGE:"
echo "   Format: <gray>[<green>+<gray>] {luckperms_prefix}<bold>{player}</bold> <green>joined global chat</green>"
echo "   Example: [+] [ADMIN] PlayerName joined global chat"
echo ""
echo "📤 LEAVE MESSAGE:"
echo "   Format: <gray>[<red>-<gray>] {luckperms_prefix}<bold>{player}</bold> <red>left global chat</red>"
echo "   Example: [-] [ADMIN] PlayerName left global chat"
echo ""

echo "🧪 TESTING STEPS:"
echo "1. Deploy SkyeNetV-2.4.7.jar to your Velocity server"
echo "2. Restart the server"
echo "3. Have a player join and use '/gc' to enable global chat"
echo "4. Verify join message shows: [+] [Prefix] PlayerName joined global chat"
echo "5. Have player use '/gc' again to disable global chat"
echo "6. Verify leave message shows: [-] [Prefix] PlayerName left global chat"
echo ""

echo "✨ VISUAL CHANGES:"
echo "• Gray brackets [] replace the globe icon 🌐"
echo "• Green + symbol for joins"
echo "• Red - symbol for leaves"
echo "• Maintains LuckPerms prefix formatting"
echo "• Clean, professional appearance"
echo ""

echo "📋 CONFIGURATION UPDATED:"
echo "• config.yml - Updated default message formats"
echo "• DiscordConfig.java - Updated default values"
echo "• Fully configurable via config.yml"
echo ""

echo "🚀 Ready for deployment!"
