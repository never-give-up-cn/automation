# 传送带物流系统 — Factorio 风格

## 背景

用户希望将现有的网络协议模拟器改造为类似《异星工厂》(Factorio) 的传送带物流游戏。当前 DataCart 在建筑之间"直线飞行"，用户希望改为 DataCart 在传送带上逐格移动，玩家需要铺设传送带来连接建筑，形成自动化生产线。

**核心变更**：传送带将**替代**现有的 DataCart 直线飞行模式，成为物品移动的主要方式。

---

## 架构概览

```
┌─────────────────────────────────────────────────────────┐
│  gameTick() @ 30ms (~33 FPS)                            │
├─────────────────────────────────────────────────────────┤
│  1. 时间/超时检查 (现有逻辑)                              │
│  2. 协议状态机 (DHCP/DNS/TCP 握手等, 现有逻辑)             │
│  3. 矿机产出 — 产出 BeltItem 到传送带 (替代 OreCart)      │
│  4. BeltNetwork.updateAll() — 所有传送带物品移动           │
│  5. Grabber.tickAll() — 抓取臂检测/抓取/投放              │
│  6. FactoryProcessor.tickAll() — 工厂加工                 │
│  7. DataCart.update() — DataCart 沿传送带移动到下一建筑    │
│  8. canvas.repaint()                                     │
└─────────────────────────────────────────────────────────┘
```

---

## 新增文件

### 1. model/BeltDirection.java
方向枚举，定义传送带方向及向量运算：
```java
public enum BeltDirection {
    NONE(0, 0, 0),    // 无传送带
    RIGHT(1, 1, 0),   // →
    DOWN(2, 0, 1),    // ↓
    LEFT(3, -1, 0),   // ←
    UP(4, 0, -1);     // ↑
    
    public final int value;
    public final int dx;  // 每 tick 的 x 方向增量
    public final int dy;  // 每 tick 的 y 方向增量
    
    public static BeltDirection fromDelta(int dx, int dy) { ... }
    public boolean isHorizontal() { return dx != 0; }
    public boolean isVertical() { return dy != 0; }
    public BeltDirection opposite() { ... }
}
```

### 2. model/BeltItem.java
传送带上的物品，使用**瓦片内相对坐标**：
```java
public class BeltItem {
    public int tileRow, tileCol;           // 当前所在瓦片
    public double pixelX, pixelY;          // 瓦片内偏移 (0~TILE_SIZE=40)
    public String type;                    // "HELLO", "SAY", "DATA", "SYN" 等
    public DataCart sourceDataCart;        // 关联的 DataCart (如果物品就是 DataCart)
    public boolean consumed = false;       // 是否已被消费
    public boolean isAtEnd = false;        // 是否到达传送带末端
    public long maxLifetime = 30000;       // 30 秒后自动消失
    public long createdAt;
}
```

### 3. conveyor/BeltNetwork.java
传送带网络管理器——核心物流引擎：
```java
public class BeltNetwork {
    public static final double BELT_SPEED = 40.0 / 20; // = 2.0 px/tick (20 ticks=600ms 过一格)
    
    public void updateAll(List<BeltItem> items, BeltDirection[][] beltGrid, 
                          String[][] buildingLayout, 
                          DataCartFactoryGame game);
    
    private void moveItem(BeltItem item, BeltDirection[][] beltGrid);
    private boolean tryTransition(BeltItem item, int nextRow, int nextCol, 
                                   BeltDirection enteringFrom, BeltDirection[][] beltGrid);
    private boolean canAccept(BeltDirection targetDir, BeltDirection enteringFrom);
    
    public void spawnItem(int row, int col, String type, DataCartFactoryGame game);
    public void removeItem(BeltItem item);
}
```

### 4. conveyor/Grabber.java
抓取臂——从传送带上抓取物品，放入相邻建筑：
```java
public class Grabber {
    public int row, col;                    // 抓取臂所在格
    public BeltDirection pickDirection;      // 从哪个方向抓取
    public BeltDirection dropDirection;      // 往哪个方向投放 (指向建筑)
    public String targetBuildingTag;         // 投放目标建筑标签
    
    public enum Phase { IDLE, PICKING, HOLDING, DROPPING, RETURNING }
    public Phase phase = Phase.IDLE;
    public long phaseStartTime = 0;
    public static final long PICK_DURATION = 200;   // 200ms 伸出抓取
    public static final long DROP_DURATION = 200;   // 200ms 投放
    public static final long COOLDOWN = 300;         // 300ms 冷却
    
    public BeltItem heldItem = null;
    
    public void tick(long now, List<BeltItem> beltItems, 
                     BeltNetwork network, DataCartFactoryGame game);
}
```

### 5. model/FactoryRecipe.java
工厂配方：
```java
public class FactoryRecipe {
    public String name;                     // 配方名
    public Map<String, Integer> inputs;     // 输入: {HELLO→2, SAY→1}
    public String outputType;               // 输出: "DATA", "REFINED_ORE"
    public long craftTimeMs;                // 加工耗时 (ms)
}
```

### 6. conveyor/FactoryProcessor.java
工厂加工处理器：
```java
public class FactoryProcessor {
    public int row, col;
    public String processTag;               // 建筑标签: "SMELTER", "ASSEMBLER"
    public Map<String, Integer> inputBuffer = new HashMap<>();
    public String outputType = null;
    
    public enum State { IDLE, CRAFTING, OUTPUTTING }
    public State state = State.IDLE;
    public long craftStartTime;
    public FactoryRecipe currentRecipe;
    
    public void tick(long now, BeltNetwork network, DataCartFactoryGame game);
    public void addInput(String type);
}
```

---

## 需要修改的现有文件

### 1. DataCartFactoryGame.java — 主要修改点

#### 新增字段 (~line 340 附近)
```java
// ========== 传送带物流系统 ==========
public BeltDirection[][] beltGrid = new BeltDirection[MAP_ROWS][MAP_COLS];
public List<BeltItem> beltItems = new CopyOnWriteArrayList<>();
public List<Grabber> grabbers = new CopyOnWriteArrayList<>();
public List<FactoryProcessor> factoryProcessors = new CopyOnWriteArrayList<>();
public boolean beltMode = false;               // 传送带放置模式
public BeltDirection selectedBeltDir = BeltDirection.RIGHT;
private BeltNetwork beltNetwork;
```

#### initMap() 末尾初始化 beltGrid
```java
for (int r = 0; r < MAP_ROWS; r++)
    for (int c = 0; c < MAP_COLS; c++)
        beltGrid[r][c] = BeltDirection.NONE;
```

#### gameTick() 中插入传送带更新逻辑
在现有 OreCart 更新之后、DataCart 更新之前加入：
```java
// === 传送带物流更新 ===
if (beltNetwork != null) {
    beltNetwork.updateAll(beltItems, beltGrid, buildingLayout, this);
    
    for (Grabber g : grabbers) {
        g.tick(now, beltItems, beltNetwork, this);
    }
    
    for (FactoryProcessor fp : factoryProcessors) {
        fp.tick(now, beltNetwork, this);
    }
    
    // 清理过期的 belt items
    beltItems.removeIf(item -> 
        item.consumed || (now - item.createdAt > item.maxLifetime));
}
```

#### 修改矿机产出逻辑
将产出的 OreCart 改为产出 BeltItem 到传送带（或保持 OreCart 作为后备）：
```java
// 矿机产出：优先产出到传送带，否则用 OreCart
if (hasAdjacentBelt(row, col)) {
    beltNetwork.spawnItem(adjRow, adjCol, oreType, this);
} else {
    oreCarts.add(new OreCart(x, y, type, pcFactory));
}
```

#### 修改建筑放置互斥规则
放置建筑时检查 `beltGrid[row][col] == NONE`，放置传送带时检查 `buildingLayout[row][col].equals("NONE")`。

#### 修改鼠标交互
- 按 `B` 切换传送带模式
- 滚轮切换传送带方向
- 左键放置传送带（消耗 5 资金）
- 右键删除传送带（返还 2 资金）

#### 修改 DataCart 的创建方式
当 DataCart 完成在建筑 X 的处理后，不再直线飞向建筑 X+1，而是：创建一个 BeltItem，将其放置在通往下一建筑的传送带上。BeltItem 的 `sourceDataCart` 持有 DataCart 引用。

### 2. DataCart.java — 核心修改

#### 修改 update() 方法
DataCart 的移动方式从直线飞行改为传送带跟随：

**简化方案**：当 DataCart 在建筑中处理完成后 (`processStageCraft()`)，它并不立即移动，而是将自身"注册"到传送带系统。具体来说：

1. `processStageCraft()` 执行完 stage 处理后，不再执行 `stage++` 后的坐标更新
2. 而是在执行完 `processStageCraft()` 后，创建一个 `BeltItem` 放在建筑输出方向的传送带上
3. BeltItem 的 `sourceDataCart` 指向这个 DataCart
4. DataCart 的 `isOnBelt = true`，`update()` 方法不再做飞行移动
5. 当 BeltItem 沿传送带移动到目标建筑时，Grabber 或直接传送带→建筑入口抓取 DataCart
6. DataCart 进入建筑，设置新坐标，`isOnBelt = false`，执行下次 `processStageCraft()`

**具体实现**：

新增字段：
```java
public boolean isOnBelt = false;     // 是否在传送带上
public boolean waitingForBelt = false; // 是否等待上传送带
```

修改 update() 中的抵达逻辑：
```java
// 原本: 到达目标建筑后直接处理并飞向下一个
// 改成: 到达建筑 → 处理 → 产生 BeltItem → 等待传送带带走

if (dist <= speed) {
    x = target.x;
    y = target.y;
    processStageCraft();
    
    // 处理完之后，如果还有下一阶段，不上传送带而是放到传送带上
    if (stage < maxStage && hasBeltConnection(targetTag)) {
        // 创建 BeltItem 代表此 DataCart 上路
        beltNetwork.spawnBeltItemFromCart(this, nextBuildingCoords);
        isOnBelt = true;
        return;  // update() 中不再继续飞行
    }
}
```

**简化版本**实际上：不必修改 DataCart.update() 的核心逻辑。只需要在 DataCart 被放入 `pendingDataCarts` 或首次创建时，判断目标建筑之间是否有传送带连接。如果有，将 DataCart 放到传送带起点（创建 BeltItem 承载它），BeltItem 沿传送带移动到终点后，DataCart "下车"进入目标建筑。

### 3. GameCanvas.java — 新增渲染

在 `paintComponent()` 中，在建筑绘制之后、OreCart 绘制之前插入：

```java
// === 绘制传送带瓦片 ===
for (int r = 0; r < MAP_ROWS; r++) {
    for (int c = 0; c < MAP_COLS; c++) {
        BeltDirection dir = game.beltGrid[r][c];
        if (dir != BeltDirection.NONE) {
            int x = c * TILE_SIZE, y = r * TILE_SIZE;
            drawBeltTile(g2, x, y, dir);
        }
    }
}

// === 绘制传送带物品 ===
for (BeltItem item : game.beltItems) {
    int drawX = item.tileCol * TILE_SIZE + (int)item.pixelX;
    int drawY = item.tileRow * TILE_SIZE + (int)item.pixelY;
    drawBeltItem(g2, drawX, drawY, item);
}

// === 绘制抓取臂 ===
for (Grabber g : game.grabbers) {
    drawGrabber(g2, g);
}

// === 绘制工厂加工状态 ===
for (FactoryProcessor fp : game.factoryProcessors) {
    drawFactoryStatus(g2, fp);
}
```

绘制方法：
- `drawBeltTile`: 灰底色 + 方向箭头 + 滚轴纹理线条
- `drawBeltItem`: 6×6 小方块，按类型着色 (复用 DataCart 颜色映射)
- `drawGrabber`: 基座 + 旋转臂，动画插值
- `drawFactoryStatus`: 建筑边框颜色 + 进度条

---

## 传送带物品移动算法

BeltNetwork.updateAll() 核心逻辑：

```
for each BeltItem in beltItems:
    if consumed: continue
    
    // 1. 沿传送带方向移动
    dx = beltDirection.dx * BELT_SPEED   // = 方向单位向量 × 2.0
    dy = beltDirection.dy * BELT_SPEED
    newPixelX = pixelX + dx
    newPixelY = pixelY + dy
    
    // 2. 是否越界到下一瓦片?
    if newPixelX < 0:  → 向左越界
        tryTransition(row, col-1, LEFT, beltGrid[row][col-1])
    elif newPixelX >= TILE_SIZE:  → 向右越界
        tryTransition(row, col+1, RIGHT, beltGrid[row][col+1])
    elif newPixelY < 0:  → 向上越界
        tryTransition(row-1, col, UP, beltGrid[row-1][col])
    elif newPixelY >= TILE_SIZE:  → 向下越界
        tryTransition(row+1, col, DOWN, beltGrid[row+1][col])
    else:
        // 瓦片内正常移动
        pixelX = newPixelX; pixelY = newPixelY
```

`tryTransition(item, nextRow, nextCol, enteringFrom)`:
```
targetDir = beltGrid[nextRow][nextCol]
if targetDir == NONE:
    // 到达传送带终点，标记等待抓取
    item.isAtEnd = true
    clampToEdge(item)
elif canAccept(targetDir, enteringFrom):
    // 成功进入下一格
    item.tileRow = nextRow
    item.tileCol = nextCol
    pixelX = adjusted新坐标
    pixelY = adjusted新坐标
    // 检查这一格是否有建筑 → 如果是目标建筑，进入
    checkBuildingEntry(item, nextRow, nextCol)
else:
    // 方向不匹配，停在边缘等待
    item.isAtEnd = true
    clampToEdge(item)
```

`canAccept(targetDir, enteringFrom)` 的弯道兼容规则：
- 从 LEFT 进入：targetDir 可以是 LEFT(直行), UP(向上弯), DOWN(向下弯)
- 从 RIGHT 进入：targetDir 可以是 RIGHT(直行), UP(向上弯), DOWN(向下弯)
- 从 UP 进入：targetDir 可以是 UP(直行), LEFT(向左弯), RIGHT(向右弯)
- 从 DOWN 进入：targetDir 可以是 DOWN(直行), LEFT(向左弯), RIGHT(向右弯)

**建筑进入检测**：当 BeltItem 移动到一个瓦片时，检查 `buildingLayout[row][col]` 是否非"NONE"。如果是 DataCart 到达了其目标建筑 (`sourceDataCart.stage` 对应的建筑)，则 DataCart "下车"并进入建筑处理。

---

## 抓取臂工作流程

```
IDLE (phase=0):
    // 检查前方(按 pickDirection方向)的传送带末端是否有物品
    pickupTile = 相邻瓦片(在 pickDirection 方向)
    BeltItem target = beltItems 中 pickupTile 上 isAtEnd 的物品
    
    if target != null && 冷却已过:
        heldItem = target
        target.consumed = true      // 从传送带移除
        phaseStartTime = now
        phase = PICKING             // 伸出抓取(200ms)
    
PICKING (phase=1):
    if (now - phaseStartTime >= PICK_DURATION):
        phase = DROPPING            // 伸出投放(200ms)
        phaseStartTime = now

DROPPING (phase=2):
    if (now - phaseStartTime >= DROP_DURATION):
        // 将 heldItem 的内容送入建筑缓冲区
        addToFactoryBuffer(heldItem)
        heldItem = null
        phase = RETURNING           // 收回(200ms)
        phaseStartTime = now

RETURNING (phase=3):
    if (now - phaseStartTime >= PICK_DURATION):
        phase = IDLE
        cooldownUntil = now + COOLDOWN
```

---

## 工厂加工流程

```
1. IDLE 状态:
   检查 inputBuffer 是否有满足配方的输入
   是 → 消耗输入, state = CRAFTING, craftStartTime = now

2. CRAFTING 状态:
   检查是否达到加工时间 (now - craftStartTime >= craftDuration)
   是 → outputReady = true, 创建产出物品
   state = OUTPUTTING

3. OUTPUTTING 状态:
   尝试将产出物投放到了邻近的 BeltItem 或直接生成 BeltItem
   BeltItem 放到目标输出格上
   state = IDLE
```

---

## 分阶段实施计划

### Phase 1: 传送带基础设施 (核心)
1. 创建 `BeltDirection.java` — 方向枚举
2. 创建 `BeltItem.java` — 物品数据结构
3. 创建 `BeltNetwork.java` — 移动引擎
4. 修改 `DataCartFactoryGame.java` — 添加 beltGrid、UI 模式切换、放置/删除
5. 修改 `GameCanvas.java` — 绘制传送带瓦片 + 物品
6. 传送带放置：按 B 切换模式，滚轮换方向，左键放置(5资金)，右键删除
7. 验证：能在地图上放置传送带，能看到箭头方向，物品沿传送带移动

### Phase 2: DataCart 接入传送带
1. 修改 `DataCart.update()` — 处理完建筑后不飞行，放到传送带上
2. 实现 DataCart 从建筑 → 传送带的转移
3. 实现 BeltItem 到达目标建筑时 → DataCart 进入建筑
4. 验证：DataCart 沿传送带从建筑 A 移动到建筑 B

### Phase 3: 抓取臂
1. 创建 `Grabber.java`
2. 商店添加 "GRABBER" 建筑（价格 15资金）
3. 实现从传送带抓取物品送到建筑的逻辑
4. 添加抓取臂动画渲染
5. 验证：物品在传送带末端被抓起，送入相邻建筑

### Phase 4: 工厂加工
1. 创建 `FactoryRecipe.java` 和 `FactoryProcessor.java`
2. 商店添加工厂建筑（"SMELTER" 30资金、"ASSEMBLER" 40资金）
3. 实现配方驱动的加工循环
4. 工厂产出物放回传送带
5. 添加工厂状态渲染（进度条、缓冲计数）
6. 验证：矿机产出矿石→传送带→抓取→工厂→产出物→传送带

### Phase 5: 完善
1. 建筑→传送带的自动连接（建筑放置时自动创建连接点）
2. 直角弯/丁字路口支持
3. 性能优化（大量物品时的帧率稳定）
4. UI 信息面板（传送带统计、工厂吞吐量）
5. 调整经济平衡（传送带价格、工厂配方产出效率）

---

## 关键设计决策

1. **传送带替代飞行**：DataCart 不再直线飞行，改由传送带携带逐格移动
2. **BeltItem 独立于 DataCart**：BeltItem 是纯位置/移动实体，DataCart 是协议数据实体。BeltItem.sourceDataCart 持有 DataCart 引用
3. **瓦片内相对坐标**：BeltItem 的 pixelX/pixelY 是 0~39 的瓦片内偏移，简化边界检测
4. **无 A* 寻路**：物品仅沿传送带方向移动，不自动寻路。玩家负责铺设正确的传送带路径
5. **向后兼容**：无传送带时保持原有飞行逻辑，放置传送带后自动切换到传送带模式

---

## 验证方法

1. `mvn compile` — 确保编译通过
2. 运行时测试：
   - 放置传送带 → 看到箭头
   - 按测试键生成 BeltItem → 物品沿传送带移动
   - 放置抓取臂在传送带末端 → 物品被抓起
   - 放置工厂、连接传送带 → 物品进入、加工、产出
   - DataCart 从 PC_FACTORY → 传送带 → 各协议建筑
3. 最终验证：完整的 TCP 数据传输从 PC 到服务器，DataCart 全程在传送带上移动
