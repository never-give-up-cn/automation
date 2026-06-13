#!/usr/bin/env python3
"""Extract DataCart inner class from DataCartFactoryGame.java into standalone DataCart.java"""

import re

# Read the source file
with open(r'e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCartFactoryGame.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Extract imports (line 0 = package, lines 1-95 = imports)
import_lines = lines[0:96]

# Extract DataCart inner class (lines 3749-7136, 0-indexed: 3748-7135)
datacart_lines = lines[3748:7136]

# Start building the new file
output_lines = []

# 1. Package
output_lines.append("package com.never_give_up.automation.demo;\n\n")

# 2. All imports
for i, line in enumerate(import_lines):
    if i == 0:
        continue  # skip package line
    output_lines.append(line)

# 3. Additional imports
output_lines.append("import com.never_give_up.automation.demo.model.*;\n")
output_lines.append("import java.util.Random;\n")

# Now process DataCart inner class content
content = ''.join(datacart_lines)

# ============ TRANSFORMATIONS ============

# A. Class declaration: keep @Data, change private -> public
content = content.replace('private class DataCart {', 'public class DataCart {')

# B. Add fields after opening brace
old_open = 'public class DataCart {\n'
new_open = 'public class DataCart {\n\n    private final GameContext context;\n    private transient FactoryManager factoryManager;\n'
content = content.replace(old_open, new_open, 1)

# C. Change constructor signature
content = content.replace(
    'public DataCart(double sx, double sy, String type, int seq) {',
    'public DataCart(double sx, double sy, String type, int seq, GameContext context) {'
)

# D. Add constructor body init
old_ctor = 'public DataCart(double sx, double sy, String type, int seq, GameContext context) {\n\n            this.srcPort = 1234;'
new_ctor = 'public DataCart(double sx, double sy, String type, int seq, GameContext context) {\n            this.context = context;\n            this.factoryManager = context.getFactoryManager();\n\n            this.srcPort = 1234;'
content = content.replace(old_ctor, new_ctor)

# E. Simple function replacements (no field names)
content = content.replace('appendToConsole(', 'context.getAppendToConsole().accept(')
content = content.replace('findBuildingCoords(', 'context.getFindBuildingCoords().apply(')
content = content.replace('updateArpDisplay()', 'context.getUpdateArpDisplay().run()')
content = content.replace('updateNatDisplay()', 'context.getUpdateNatDisplay().run()')
content = content.replace('updateDnsDisplay()', 'context.getUpdateDnsDisplay().run()')

# F. Handle WRITE operations for mutable fields BEFORE general read replacement
# These fields can be assigned: field = value;  field++;  ++field;  field += value;
mutable_fields = {
    'ipIdentifierCounter': ('context.setIpIdentifierCounter', 'context.getIpIdentifierCounter'),
    'httpResponseContent': ('context.setHttpResponseContent', 'context.getHttpResponseContent'),
    'serverReceivedCount': ('context.setServerReceivedCount', 'context.getServerReceivedCount'),
    'cwnd': ('context.setCwnd', 'context.getCwnd'),
    'tlsState': ('context.setTlsState', 'context.getTlsState'),
}

for fld, (setter, getter) in mutable_fields.items():
    # Handle field++
    content = content.replace(fld + '++', f'__W_{fld}_INC__')
    # Handle ++field
    content = content.replace('++' + fld, f'__W_{fld}_PREINC__')
    # Handle field = value;  (but NOT field == value or field != value)
    content = re.sub(r'(?<![.\w!<>])' + fld + r'\s*=\s*([^;]+);', setter + r'(\1);', content)
    # Handle field += value;
    content = re.sub(r'(?<![.\w])' + fld + r'\s*\+=\s*([^;]+);', f'__W_{fld}_ADD__\\1__END__', content)
    # Handle field -= value;
    content = re.sub(r'(?<![.\w])' + fld + r'\s*-=\s*([^;]+);', f'__W_{fld}_SUB__\\1__END__', content)

# Now do general READ replacement for ALL field names
all_field_replacements = [
    (r'(?<![.\w])pcIpAddress(?!\w)', 'context.getPcIpAddress()'),
    (r'(?<![.\w])resolvedServerIp(?!\w)', 'context.getResolvedServerIp()'),
    (r'(?<![.\w])pcFactory(?!\w)', 'context.getPcFactory()'),
    (r'(?<![.\w])pendingDataCarts(?!\w)', 'context.getPendingDataCarts()'),
    (r'(?<![.\w])natTable(?!\w)', 'context.getNatTable()'),
    (r'(?<![.\w])natPortCounter(?!\w)', 'context.getNatPortCounter()'),
    (r'(?<![.\w])currentTcpState(?!\w)', 'context.getCurrentTcpState()'),
    (r'(?<![.\w])pcIpAssigned(?!\w)', 'context.isPcIpAssigned()'),
    (r'(?<![.\w])tracerouteActive(?!\w)', 'context.isTracerouteActive()'),
    (r'(?<![.\w])useUdp(?!\w)', 'context.isUseUdp()'),
    (r'(?<![.\w])tlsEnabled(?!\w)', 'context.isTlsEnabled()'),
    (r'(?<![.\w])portFactory(?!\w)', 'factoryManager.getPortFactory()'),
    (r'(?<![.\w])tcpFactory(?!\w)', 'factoryManager.getTcpPacketFactory()'),
    (r'(?<![.\w])ipFactory(?!\w)', 'factoryManager.getIpPacketFactory()'),
    (r'(?<![.\w])ethernetFactory(?!\w)', 'factoryManager.getEthernetFactory()'),
    (r'(?<![.\w])cwnd(?!\w)', 'context.getCwnd()'),
    (r'(?<![.\w])httpResponseContent(?!\w)', 'context.getHttpResponseContent()'),
    (r'(?<![.\w])tlsState(?!\w)', 'context.getTlsState()'),
    (r'(?<![.\w])serverReceivedCount(?!\w)', 'context.getServerReceivedCount()'),
    (r'(?<![.\w])totalDataToTransmit(?!\w)', 'context.getTotalDataToTransmit()'),
    (r'(?<![.\w])ipIdentifierCounter(?!\w)', 'context.getIpIdentifierCounter()'),
]

for pattern, replacement in all_field_replacements:
    content = re.sub(pattern, replacement, content)

# Restore write patterns for all mutable fields
for fld, (setter, getter) in mutable_fields.items():
    cap_fld = fld[0].upper() + fld[1:]
    content = content.replace(f'__W_{fld}_INC__', f'{setter}({getter}() + 1)')
    content = content.replace(f'__W_{fld}_PREINC__', f'{setter}({getter}() + 1)')
    content = re.sub(
        f'__W_{fld}_ADD__([^;]*?)__END__',
        lambda m, g=getter, s=setter: f'{s}({g}() + {m.group(1).strip()})',
        content
    )
    content = re.sub(
        f'__W_{fld}_SUB__([^;]*?)__END__',
        lambda m, g=getter, s=setter: f'{s}({g}() - {m.group(1).strip()})',
        content
    )

# G. Fix DataCart constructor calls
result = []
i = 0
while i < len(content):
    idx = content.find('new DataCart(', i)
    if idx == -1:
        result.append(content[i:])
        break
    
    result.append(content[i:idx])
    paren_start = idx + len('new DataCart(')
    
    depth = 1
    j = paren_start
    while j < len(content) and depth > 0:
        if content[j] == '(':
            depth += 1
        elif content[j] == ')':
            depth -= 1
        j += 1
    
    args_str = content[paren_start:j-1]
    
    depth = 0
    commas = 0
    for ch in args_str:
        if ch == '(':
            depth += 1
        elif ch == ')':
            depth -= 1
        elif ch == ',' and depth == 0:
            commas += 1
    
    if commas == 3:
        result.append(f'new DataCart({args_str}, context)')
    else:
        result.append(f'new DataCart({args_str})')
    
    i = j

content = ''.join(result)

# Assemble final output
output_lines.append('\n')
output_lines.append(content)
final_text = ''.join(output_lines)

# Deduplicate imports
seen = set()
clean_lines = []
for line in final_text.split('\n'):
    stripped = line.strip()
    if stripped.startswith('import ') and stripped.endswith(';'):
        if stripped not in seen:
            seen.add(stripped)
            clean_lines.append(line)
    else:
        clean_lines.append(line)
final_text = '\n'.join(clean_lines)

with open(r'e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCart.java', 'w', encoding='utf-8') as f:
    f.write(final_text)

print(f"Written {len(final_text)} characters to DataCart.java")
print("Done!")
