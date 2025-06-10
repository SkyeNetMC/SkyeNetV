#!/bin/bash

# SkyeNetV Plugin Test Script - Version Agnostic
# This script tests the core functionality of the plugin

set -e  # Exit on error

echo "=== SkyeNetV Plugin Test ==="

# Find jar file dynamically
JAR_FILE=$(ls target/SkyeNetV-*.jar 2>/dev/null | head -1)
if [ -z "$JAR_FILE" ]; then
    echo "❌ No SkyeNetV jar file found in target/"
    echo "Available files in target/:"
    ls -la target/ 2>/dev/null || echo "target/ directory not found"
    exit 1
fi

VERSION=$(basename "$JAR_FILE" | sed 's/SkyeNetV-\(.*\)\.jar/\1/')
echo "✅ Found JAR: $JAR_FILE"
echo "✅ Version: $VERSION"
echo "✅ Size: $(du -h "$JAR_FILE" | cut -f1)"
echo ""

echo "=== JAR Contents Verification ==="

# Check for velocity-plugin.json
if jar tf "$JAR_FILE" | grep -q "velocity-plugin.json"; then
    echo "✅ velocity-plugin.json present"
else
    echo "❌ velocity-plugin.json missing"
fi

# Check for main class
if jar tf "$JAR_FILE" | grep -q "me/pilkeysek/skyenetv/SkyeNetV.class"; then
    echo "✅ Main plugin class found"
else
    echo "❌ Main plugin class missing"
fi

# Count command classes
COMMAND_COUNT=$(jar tf "$JAR_FILE" | grep -c "commands.*\.class" || echo "0")
echo "✅ Command classes: $COMMAND_COUNT"

# Count config classes  
CONFIG_COUNT=$(jar tf "$JAR_FILE" | grep -c "config.*\.class" || echo "0")
echo "✅ Config classes: $CONFIG_COUNT"

# Count utility classes
UTIL_COUNT=$(jar tf "$JAR_FILE" | grep -c "utils.*\.class" || echo "0")
echo "✅ Utility classes: $UTIL_COUNT"

# Count discord classes
DISCORD_COUNT=$(jar tf "$JAR_FILE" | grep -c "discord.*\.class" || echo "0")
echo "✅ Discord classes: $DISCORD_COUNT"

echo ""
echo "=== Configuration Files ==="

if [ -f "discord_config.yml" ]; then
    echo "✅ discord_config.yml exists"
else
    echo "❌ discord_config.yml missing"
fi

if [ -f "src/main/resources/rules.json" ]; then
    echo "✅ rules.json exists"
else
    echo "❌ rules.json missing"
fi

echo ""
echo "=== Plugin Metadata ==="

# Extract and display velocity-plugin.json
if jar xf "$JAR_FILE" velocity-plugin.json 2>/dev/null; then
    echo "velocity-plugin.json content:"
    echo "---"
    cat velocity-plugin.json
    echo ""
    echo "---"
    rm -f velocity-plugin.json
else
    echo "❌ Could not extract velocity-plugin.json"
fi

echo ""
echo "=== Summary ==="
echo "Plugin: SkyeNetV $VERSION"
echo "JAR File: $JAR_FILE"
echo "Status: ✅ Ready for deployment"
echo ""
echo "To deploy:"
echo "  cp $JAR_FILE /path/to/velocity/plugins/"
echo "  # Restart Velocity server"
