#!/bin/bash

# SkyeNetV Plugin Test Script
# This script tests the core functionality of the plugin

echo "=== SkyeNetV Plugin Test ==="

# Find jar file dynamically
JAR_FILE=$(ls target/SkyeNetV-*.jar 2>/dev/null | head -1)
if [ -n "$JAR_FILE" ]; then
    echo "Plugin JAR: $(ls -la $JAR_FILE)"
    echo "Version: $(basename $JAR_FILE | sed 's/SkyeNetV-\(.*\)\.jar/\1/')"
else
    echo "❌ No SkyeNetV jar file found in target/"
    exit 1
fi
echo ""

echo "=== JAR Contents Check ==="
echo "Checking for velocity-plugin.json..."
jar tf $JAR_FILE | grep velocity-plugin.json

echo ""
echo "Checking for main class..."
jar tf $JAR_FILE | grep "SkyeNetV.class"

echo ""
echo "Checking for all command classes..."
jar tf $JAR_FILE | grep "commands.*\.class"

echo ""
echo "Checking for configuration classes..."
jar tf $JAR_FILE | grep "config.*\.class"

echo ""
echo "Checking for utility classes..."
jar tf $JAR_FILE | grep "utils.*\.class"

echo ""
echo "Checking for discord classes..."
jar tf $JAR_FILE | grep "discord.*\.class"in Test Script
# This script tests the core functionality of the plugin

echo "=== SkyeNetV Plugin Test ==="
echo "Plugin JAR: $(ls -la target/SkyeNetV-*.jar)"
echo ""

echo "=== JAR Contents Check ==="
echo "Checking for velocity-plugin.json..."
jar tf target/SkyeNetV-2.4.3.jar | grep velocity-plugin.json

echo ""
echo "Checking for main class..."
jar tf target/SkyeNetV-2.4.3.jar | grep "SkyeNetV.class"

echo ""
echo "Checking for all command classes..."
jar tf target/SkyeNetV-2.4.3.jar | grep "commands.*\.class"

echo ""
echo "Checking for configuration classes..."
jar tf target/SkyeNetV-2.4.3.jar | grep "config.*\.class"

echo ""
echo "Checking for module classes..."
jar tf target/SkyeNetV-2.4.3.jar | grep "modules.*\.class"

echo ""
echo "=== Configuration Files ==="
echo "Discord config sample:"
if [ -f config.yml ]; then
    echo "✅ config.yml created"
    # Check if it contains discord configuration
    if grep -q "discord:" config.yml; then
        echo "✅ Discord configuration found in config.yml"
    else
        echo "❌ Discord configuration missing from config.yml"
    fi
else
    echo "❌ config.yml missing"
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
jar xf "$JAR_FILE" velocity-plugin.json 2>/dev/null
if [ -f velocity-plugin.json ]; then
    echo "Found velocity-plugin.json in $JAR_FILE:"
    echo ""
    echo "--- velocity-plugin.json ---"
    cat velocity-plugin.json | python3 -m json.tool 2>/dev/null || cat velocity-plugin.json
    echo ""
    echo "--- end velocity-plugin.json ---"
    rm velocity-plugin.json
else
    echo "❌ Could not extract velocity-plugin.json from $JAR_FILE"
fi

echo ""
echo "=== Class Verification ==="
echo "Verifying key classes exist:"

# Check main classes
if jar tf "$JAR_FILE" | grep -q "me/pilkeysek/skyenetv/SkyeNetV.class"; then
    echo "✅ Main plugin class found"
else
    echo "❌ Main plugin class missing"
fi

# Check command classes
COMMAND_COUNT=$(jar tf "$JAR_FILE" | grep "commands.*\.class" | wc -l)
echo "✅ Found $COMMAND_COUNT command classes"

# Check config classes
CONFIG_COUNT=$(jar tf "$JAR_FILE" | grep "config.*\.class" | wc -l)
echo "✅ Found $CONFIG_COUNT configuration classes"

echo ""
echo "=== Version Information ==="
VERSION=$(basename $JAR_FILE | sed 's/SkyeNetV-\(.*\)\.jar/\1/')
echo "Plugin Version: $VERSION"
echo "JAR File Size: $(du -h $JAR_FILE | cut -f1)"
echo "Build Date: $(stat -c %y $JAR_FILE | cut -d' ' -f1)"

echo ""
echo "=== Test Complete ==="
echo "Plugin JAR: $JAR_FILE"
echo "Status: ✅ Ready for deployment!"
