#!/usr/bin/env python3
"""
Extract methods from DataCartFactoryGame.java inner class (lines 4636-7135),
apply field reference replacements, and append to DataCart.java.
"""

import re

# Read current DataCart.java (first 1011 lines - the existing code without closing brace)
with open(r"e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCart.java", "r", encoding="utf-8") as f:
    current_lines = f.readlines()

# Strip trailing whitespace/newlines to find the last content line
# The file has 1012 lines, last line is empty
# Content ends at line 1011 (close of findBuildingCoords)

# Read the original file methods
with open(r"e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCartFactoryGame.java", "r", encoding="utf-8") as f:
    all_lines = f.readlines()

# Extract lines 4636-7135 (0-indexed: 4635 to 7135)
method_lines = all_lines[4635:7135]  # line 4636 to 7135 inclusive

# Build the methods text
methods_text = "".join(method_lines)

# Apply replacements
# 1. Remove 'private' from method signatures since the class is no longer inner
methods_text = methods_text.replace("private Point findTargetMachine(", "Point findTargetMachine(")
methods_text = methods_text.replace("private void processStageCraft(", "void processStageCraft(")
methods_text = methods_text.replace("private void processUdpStage(", "void processUdpStage(")
methods_text = methods_text.replace("private void processTcpControlStage(", "void processTcpControlStage(")

# 2. Replace field references with context calls
# Use word boundary for field names to avoid partial matches
# Note: we allow . after the field name (e.g., resolvedServerIp.contains(...))
methods_text = re.sub(r'(?<!\w)pcIpAddress(?!\w)', 'context.getPcIpAddress()', methods_text)
methods_text = re.sub(r'(?<!\w)resolvedServerIp(?!\w)', 'context.getResolvedServerIp()', methods_text)
methods_text = re.sub(r'(?<!\w)currentTcpState(?!\w)', 'context.getCurrentTcpState()', methods_text)
methods_text = re.sub(r'(?<!\w)pcIpAssigned(?!\w)', 'context.isPcIpAssigned()', methods_text)
methods_text = re.sub(r'(?<!\w)tracerouteActive(?!\w)', 'context.isTracerouteActive()', methods_text)
methods_text = re.sub(r'(?<!\w)useUdp(?!\w)', 'context.isUseUdp()', methods_text)
methods_text = re.sub(r'(?<!\w)cwnd(?!\w)', 'context.getCwnd()', methods_text)
methods_text = re.sub(r'(?<!\w)tlsEnabled(?!\w)', 'context.isTlsEnabled()', methods_text)
methods_text = re.sub(r'(?<!\w)tlsState(?!\w)', 'context.getTlsState()', methods_text)
methods_text = re.sub(r'(?<!\w)serverReceivedCount(?!\w)', 'context.getServerReceivedCount()', methods_text)
methods_text = re.sub(r'(?<!\w)totalDataToTransmit(?!\w)', 'context.getTotalDataToTransmit()', methods_text)

# Write the combined output
output_path = r"e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCart.java.new"

# Combine: current content + methods + closing brace
with open(output_path, "w", encoding="utf-8") as f:
    # Write current content (lines 1-1011)
    for i, line in enumerate(current_lines):
        if i < 1011:  # only first 1011 lines
            f.write(line)
    # Write methods
    f.write(methods_text)
    # Write class closing brace
    f.write("}\n")

print("Done! Output written to DataCart.java.new")
print(f"Current file has {len(current_lines)} lines")
print(f"Extracted methods have {len(method_lines)} lines")
