"""
Refactor DataCartFactoryGame.java:
1. Add imports for model classes
2. Remove BuildingZone, TcpState, TlsState, DnsEntry, ArpEntry, NatEntry, RetransmissionTask inner classes
3. Remove IpFragment and IpFragmentKey inner classes
4. Remove OreCart, DataCart, GameCanvas inner classes
5. Add GameContext field
6. Create GameContext in constructor
7. Update canvas instantiation to pass 'this'
8. Update all DataCart constructions with gameContext
9. Update OreCart constructions with pcFactory
"""

filepath = "src/main/java/com/never_give_up/automation/demo/DataCartFactoryGame.java"

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

print("Original file: {} chars".format(len(content)))

# STEP 1: Add imports
p1 = "import java.util.concurrent.atomic.AtomicInteger;\n\npublic class DataCartFactoryGame extends JFrame {"
r1 = """import java.util.concurrent.atomic.AtomicInteger;
import com.never_give_up.automation.demo.model.ArpEntry;
import com.never_give_up.automation.demo.model.BuildingZone;
import com.never_give_up.automation.demo.model.DnsEntry;
import com.never_give_up.automation.demo.model.GameContext;
import com.never_give_up.automation.demo.model.IpFragment;
import com.never_give_up.automation.demo.model.IpFragmentKey;
import com.never_give_up.automation.demo.model.NatEntry;
import com.never_give_up.automation.demo.model.PacketClass;
import com.never_give_up.automation.demo.model.RetransmissionTask;
import com.never_give_up.automation.demo.model.TcpState;
import com.never_give_up.automation.demo.model.TlsState;
import com.never_give_up.automation.demo.model.VisualFeedback;

public class DataCartFactoryGame extends JFrame {"""
assert p1 in content, "S1 fail"
content = content.replace(p1, r1)
print("S1: imports added")

# STEP 2: Remove BuildingZone through RetransmissionTask
p2_start = "    // 在 DataCartFactoryGame 类中添加区域定义枚举和配置\n    private enum BuildingZone {"
p2_end = "    }\n\n    private int udpPacketsSent = 0;"
s2 = content.find(p2_start)
e2 = content.find(p2_end)
assert s2 >= 0 and e2 > s2, "S2 fail"
replacement2 = "    // 添加建筑到区域的映射\n    private Map<String, BuildingZone> buildingZoneMap = new HashMap<>();\n\n    private int udpPacketsSent = 0;"
content = content[:s2] + replacement2 + content[e2+len(p2_end):]
print("S2: inner classes removed")

# STEP 3: Remove IpFragment and IpFragmentKey
p3_start = "    // ========== 新增字段（类成员） ==========\n    private Map<IpFragmentKey, List<IpFragment>> fragmentBuffer = new HashMap<>();\n    private int ipIdentifierCounter = 2000;\n    private boolean udpCompleted = false;\n\n    private static class IpFragment {"
p3_end = "    }\n\n    private int funds = 3000;"
s3 = content.find(p3_start)
e3 = content.find(p3_end)
assert s3 >= 0 and e3 > s3, "S3 fail"
replacement3 = "    // ========== 新增字段（类成员） ==========\n    private Map<IpFragmentKey, List<IpFragment>> fragmentBuffer = new HashMap<>();\n    private int ipIdentifierCounter = 2000;\n    private boolean udpCompleted = false;\n\n    private int funds = 3000;"
content = content[:s3] + replacement3 + content[e3+len(p3_end):]
print("S3: IpFragment classes removed")

# STEP 4: Remove OreCart, DataCart, GameCanvas
lines = content.split('\n')
dcart_start = -1
main_start = -1
for i, line in enumerate(lines):
    if line.strip() == '@Data' and i+1 < len(lines) and 'private class DataCart' in lines[i+1]:
        dcart_start = i
    if 'public static void main(String[] args)' in line:
        main_start = i
assert dcart_start >= 0 and main_start > dcart_start, "S4 fail"
new_lines = lines[:dcart_start] + lines[main_start:]
content = '\n'.join(new_lines)
print("S4: inner classes removed ({} -> {} lines)".format(len(lines), len(new_lines)))

# STEP 5: Add GameContext field
p5 = "    private GameCanvas canvas;\n    private JLabel lblDashboard;"
r5 = "    private GameCanvas canvas;\n    private GameContext gameContext;\n    private JLabel lblDashboard;"
assert p5 in content, "S5 fail"
content = content.replace(p5, r5)
print("S5: GameContext field added")

# STEP 6: Create GameContext in constructor
p6 = "        initDnsCache();\n\n        JPanel topPanel = new JPanel(new BorderLayout());"
r6 = """        initDnsCache();

        // 创建 GameContext 用于 DataCart 依赖注入
        gameContext = new GameContext(
            pcFactory,
            pendingDataCarts,
            natTable,
            natPortCounter,
            ipIdentifierCounter,
            factoryManager,
            this::appendToConsole,
            this::findBuildingCoords,
            this::updateArpDisplay,
            this::updateNatDisplay,
            this::updateDnsDisplay
        );

        JPanel topPanel = new JPanel(new BorderLayout());"""
assert p6 in content, "S6 fail"
content = content.replace(p6, r6)
print("S6: GameContext creation added")

# STEP 7: Update canvas instantiation
p7 = "        // 找到创建 canvas 的位置\n        canvas = new GameCanvas();"
r7 = "        // 找到创建 canvas 的位置\n        canvas = new GameCanvas(this);"
assert p7 in content, "S7 fail"
content = content.replace(p7, r7)
print("S7: canvas updated")

# STEP 8: Update DataCart constructions
lines = content.split('\n')
dc_count = 0
for i, line in enumerate(lines):
    idx = line.find('new DataCart(')
    if idx >= 0:
        paren_pos = idx + len('new DataCart(') - 1
        depth = 0
        for j in range(paren_pos, len(line)):
            if line[j] == '(':
                depth += 1
            elif line[j] == ')':
                depth -= 1
                if depth == 0:
                    lines[i] = line[:j] + ', gameContext' + line[j:]
                    dc_count += 1
                    break
content = '\n'.join(lines)
gc_count = content.count(', gameContext)')
print("S8: {} DataCart calls updated ({} with gameContext)".format(dc_count, gc_count))

# STEP 9: Update OreCart constructions
p9a = 'oreCarts.add(new OreCart(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2, "HELLO"));'
r9a = 'oreCarts.add(new OreCart(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2, "HELLO", pcFactory));'
p9b = 'oreCarts.add(new OreCart(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2, "SAY"));'
r9b = 'oreCarts.add(new OreCart(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2, "SAY", pcFactory));'
assert p9a in content, "S9a fail"
assert p9b in content, "S9b fail"
content = content.replace(p9a, r9a)
content = content.replace(p9b, r9b)
print("S9: OreCart calls updated")

# Write
with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)

final_lines = content.split('\n')
print("\nFinal: {} chars, {} lines".format(len(content), len(final_lines)))
print("All steps completed!")
