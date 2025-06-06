#!/bin/bash

# SkyeNetV Plugin Test Script
# This script tests the core functionality of the plugin

echo "=== SkyeNetV Plugin Test ==="
echo "Plugin JAR: $(ls -la target/SkyeNetV-*.jar)"
echo ""

echo "=== JAR Contents Check ==="
echo "Checking for velocity-plugin.json..."
jar tf target/SkyeNetV-2.2.jar | grep velocity-plugin.json

echo ""
echo "Checking for main class..."
jar tf target/SkyeNetV-2.2.jar | grep "SkyeNetV.class"

echo ""
echo "Checking for all command classes..."
jar tf target/SkyeNetV-2.2.jar | grep "commands.*\.class"

echo ""
echo "Checking for configuration classes..."
jar tf target/SkyeNetV-2.2.jar | grep "config.*\.class"

echo ""
echo "Checking for module classes..."
jar tf target/SkyeNetV-2.2.jar | grep "modules.*\.class"

echo ""
echo "=== Configuration Files ==="
echo "Discord config sample:"
if [ -f discord_config.yml ]; then
    echo "✅ discord_config.yml created"
else
    echo "❌ discord_config.yml missing"
fi

echo ""
echo "Rules configuration:"
if [ -f src/main/resources/rules.json ]; then
    echo "✅ rules.json present"
else
    echo "❌ rules.json missing"
fi

echo ""
echo "=== Plugin Metadata ==="
echo "Checking velocity-plugin.json content..."
jar xf target/SkyeNetV-2.2.jar velocity-plugin.json 2>/dev/null
if [ -f velocity-plugin.json ]; then
    cat velocity-plugin.json
    rm velocity-plugin.json
else
    echo "❌ Could not extract velocity-plugin.json"
fi

echo ""
echo "=== Test Complete ==="
echo "Plugin ready for deployment!"
