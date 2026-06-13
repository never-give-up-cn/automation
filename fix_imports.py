#!/usr/bin/env python3
"""
Fix imports in DataCart.java by using correct imports from DataCartFactoryGame.java,
while keeping the existing class implementation (fields, constructor, methods).
"""
import re

with open(r'e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCartFactoryGame.java', 'r', encoding='utf-8') as f:
    game_lines = f.readlines()

# Extract correct imports (lines 1-96, 0-indexed: 1-95)
correct_imports = game_lines[1:96]  # line 2 to 96

# The current DataCart.java
with open(r'e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCart.java', 'r', encoding='utf-8') as f:
    current_content = f.read()

# Find the position where class declaration starts
class_match = re.search(r'\npublic class DataCart', current_content)
if class_match:
    # Replace everything from after package line to before class declaration
    package_end = current_content.index('\n') + 1  # after first newline
    class_start = class_match.start() + 1  # newline before 'public class'
    
    # Build new import section
    new_imports = []
    for line in correct_imports:
        stripped = line.strip()
        if stripped.startswith('import ') or stripped.startswith('//') or stripped == '' or stripped.startswith('@'):
            new_imports.append(line)
        elif stripped.startswith('package '):
            continue
        elif stripped == '':
            new_imports.append(line)
    
    # Add model import
    new_imports.append('import com.never_give_up.automation.demo.model.*;\n')
    new_imports.append('import java.util.Random;\n')
    
    # Combine
    new_header = ''.join(new_imports)
    
    new_content = current_content[:package_end] + '\n' + new_header + '\n' + current_content[class_start:]
    
    # Deduplicate imports
    seen = set()
    clean_lines = []
    for line in new_content.split('\n'):
        stripped = line.strip()
        if stripped.startswith('import ') and stripped.endswith(';'):
            if stripped not in seen:
                seen.add(stripped)
                clean_lines.append(line)
        else:
            clean_lines.append(line)
    
    new_content = '\n'.join(clean_lines)
    
    with open(r'e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCart.java', 'w', encoding='utf-8') as f:
        f.write(new_content)
    
    print("Done! Fixed imports in DataCart.java")
else:
    print("Could not find class declaration")
