# 全量传送带集成方案

## 问题

当前所有 DataCart 飞行在建筑之间，传送带只对 `BELT_DEMO` 类型有效。主要障碍：主路径建筑全部**相邻排列**（行7：18个建筑连续相邻，行8：9个建筑连续相邻），没有空间放置传送带瓦片。

## 核心设计：建筑瓦片 = 传送带瓦片

**关键创新**：在建筑瓦片上直接设置 `beltGrid` 方向，让 BeltItem 在建筑瓦片上滑动到下一个建筑。不需要在建筑之间插入额外的传送带瓦片。

```
当前: [TX_APP][T_SP ][T_DP ]  ← 相邻，无传送带空间
改造: [TX_APP]→[T_SP ]→[T_DP ]→  ← beltGrid 设在建筑瓦片上
```

### 工作原理

1. `beltGrid[7][8] = RIGHT`（TX_APP 的建筑瓦片）
2. `spawnBeltItemFromCart()` 在**当前建筑瓦片**创建 BeltItem（不是相邻瓦片）
3. BeltItem 每帧向右移动 2px
4. 到达 [7][9] 边界 → `tryTransition()` 检测到 [7][9] 是建筑（T_SP）
5. Step 1 的修复已支持建筑进入 → 触发 `checkBuildingEntry()`
6. DataCart 进入 T_SP，`processStageCraft()`，stage++
7. 继续向右传送至下一个建筑...

## 实施步骤

### Step 1: 修改 `spawnBeltItemFromCart()` — 在源建筑瓦片上创建

**文件**: `e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\conveyor\BeltNetwork.java`

当前在 `(buildingRow + beltDir.dy, buildingCol + beltDir.dx)` 创建。
修改为在 `(buildingRow, buildingCol)` 创建（源建筑瓦片）。

```java
public BeltItem spawnBeltItemFromCart(DataCart cart,
                                       int buildingRow, int buildingCol,
                                       BeltDirection beltDir,
                                       DataCartFactoryGame game) {
    // 不再检查相邻瓦片是否有传送带
    // 而是在源建筑瓦片上创建 BeltItem

    double startPixelX, startPixelY;
    switch (beltDir) {
        case RIGHT:
            startPixelX = 0;
            startPixelY = TILE_SIZE / 2;
            break;
        case DOWN:
            startPixelX = TILE_SIZE / 2;
            startPixelY = 0;
            break;
        case LEFT:
            startPixelX = TILE_SIZE - 1;
            startPixelY = TILE_SIZE / 2;
            break;
        case UP:
            startPixelX = TILE_SIZE / 2;
            startPixelY = TILE_SIZE - 1;
            break;
        default:
            startPixelX = TILE_SIZE / 2;
            startPixelY = TILE_SIZE / 2;
    }

    BeltItem item = new BeltItem(buildingRow, buildingCol,
            startPixelX, startPixelY, beltDir, cart.cartType);
    item.sourceDataCart = cart;
    game.beltItems.add(item);
    return item;
}
```

同时删除 `tryActivateBeltForCart()` 中的相邻传送带检查——因为建筑瓦片上的 beltGrid 就是传送带。

### Step 2: 修改 `tryActivateBeltForCart()` — 移除相邻传送带检查

**文件**: `e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCartFactoryGame.java`

```java
// 不再检查相邻瓦片是否有传送带，因为 beltGrid 设在建筑瓦片上
// 直接通过方向判断
if (dir == BeltDirection.NONE) return;

// 检查当前建筑瓦片是否有指向目标方向的传送带
if (beltGrid[row][col] == dir) {
    // 上带！
    ...
}
```

### Step 3: 设置所有主路径建筑的 beltGrid

**文件**: `e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\DataCartFactoryGame.java`，`initMap()` 末尾

```java
// ===== 全量传送带路线 =====
// 发送端封装路径（行7）
for (int c = 8; c <= 25; c++) {  // TX_APP(8) → SESSION(26)
    if (!buildingLayout[7][c].equals("NONE")) {
        beltGrid[7][c] = BeltDirection.RIGHT;
    }
}
// 接收端解封装路径（行8）
for (int c = 34; c <= 41; c++) {  // RX_ETH(34) → RX_APP(42)
    if (!buildingLayout[8][c].equals("NONE")) {
        beltGrid[8][c] = BeltDirection.RIGHT;
    }
}
// 边界网关（行10）— 有间隙，在间隙处放传送带瓦片
beltGrid[10][21] = BeltDirection.RIGHT;  // R_LAN(20)→R_TAB(22) 之间
beltGrid[10][23] = BeltDirection.RIGHT;  // R_TAB(22)→R_NAT(24) 之间
beltGrid[10][27] = BeltDirection.RIGHT;  // R_WAN(26)→ROUTER1(28) 之间
beltGrid[10][29] = BeltDirection.RIGHT;  // ROUTER1(28)→ROUTER2(30) 之间
beltGrid[10][31] = BeltDirection.RIGHT;  // ROUTER2(30)→ROUTER3(32) 之间
// 网关建筑本身指向下一格
beltGrid[10][20] = BeltDirection.RIGHT;  // R_LAN
beltGrid[10][22] = BeltDirection.RIGHT;  // R_TAB
beltGrid[10][24] = BeltDirection.RIGHT;  // R_NAT
beltGrid[10][25] = BeltDirection.RIGHT;  // BW_CTRL
beltGrid[10][26] = BeltDirection.RIGHT;  // R_WAN
beltGrid[10][28] = BeltDirection.RIGHT;  // ROUTER1
beltGrid[10][30] = BeltDirection.RIGHT;  // ROUTER2
beltGrid[10][32] = BeltDirection.RIGHT;  // ROUTER3

// 其他主路径
// 防火墙（行5）
beltGrid[5][20] = BeltDirection.RIGHT;  // FW_IN
beltGrid[5][21] = BeltDirection.RIGHT;  // FW_OUT

// 队列（行9）
beltGrid[9][22] = BeltDirection.RIGHT;  // Q_IN
beltGrid[9][23] = BeltDirection.RIGHT;  // Q_OUT
```

### Step 4: 修改传送带渲染 — 在建筑瓦片上显示传送带标记

**文件**: `e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\GameCanvas.java`

当前传送带绘制画一个深色背景 + 箭头。在有建筑的瓦片上，跳过深色背景（保留建筑背景），只画一个小箭头指示方向：

```java
if (dir != BeltDirection.NONE) {
    String buildingTag = game.buildingLayout[r][c];
    boolean hasBuilding = buildingTag != null && !"NONE".equals(buildingTag);
    
    if (!hasBuilding) {
        // 纯传送带瓦片 → 深色背景
        g2.setColor(new Color(50, 55, 65, 200));
        g2.fillRect(x + 1, y + 1, game.TILE_SIZE - 2, game.TILE_SIZE - 2);
    }
    // 在瓦片底部画一个细条指示方向（建筑瓦片上也画）
    g2.setColor(hasBuilding ? new Color(80, 100, 130, 150) : new Color(80, 100, 130));
    // 画小箭头...
}
```

### Step 5: 调整机械臂绘制位置

**文件**: `e:\newgit\automation\src\main\java\com\never_give_up\automation\demo\GameCanvas.java`

机械臂从固定位置改为在所有相邻建筑对之间绘制：

```java
// 发送端路径机械臂（行7）
for (int c = 8; c < 26; c++) {
    if (!game.buildingLayout[7][c].equals("NONE") && !game.buildingLayout[7][c+1].equals("NONE")) {
        drawGrabber(g2, 7, c, game.TILE_SIZE); // 在建筑右侧画爪子
    }
}
// 接收端路径机械臂（行8）
for (int c = 34; c < 42; c++) {
    if (!game.buildingLayout[8][c].equals("NONE") && !game.buildingLayout[8][c+1].equals("NONE")) {
        drawGrabber(g2, 8, c, game.TILE_SIZE);
    }
}
```

## 验证方法

1. `mvn compile` 编译验证
2. 启动游戏，触发 TCP 握手（点击演示按钮）
3. 观察 SYN 数据包沿行7的传送带从 TX_APP 移动至 SESSION，不应飞行
4. 观察数据包经过网关时沿传送带走
5. 观察接收端行8的传送带动画
6. 按 `T` 键验证传送带演示区仍正常工作
