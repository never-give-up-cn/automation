# Phase 2: DataCart 传送带集成方案

## 背景

Phase 1 实现了传送带基础设施（BeltDirection/BeltItem/BeltNetwork、放置/拆除UI、渲染），但 DataCart 仍使用飞行模式。Phase 2 的目标是：**当传送带连接建筑时，DataCart 自动使用传送带代替飞行**。

核心设计模式：**"离港时切换"**——DataCart 在建筑完成加工后，检查去往下一建筑的方向是否有传送带。有则切换到传送带模式，无则继续飞行。

## 文件修改

### 1. DataCart.java — 新增 isOnBelt 标志 + getTargetBuilding() 包装方法

**添加字段**（在 `isArrived` / `cartType` 附近，约第 363 行）：

```java
public boolean isOnBelt = false;
```

**添加公开包装方法**（在 `isDnsOrDhcp()` 方法附近，约第 991 行）：

```java
/**
 * 获取当前 stage 的下一个目标建筑坐标（公开包装，供游戏主类调用）
 */
public Point getTargetBuilding() {
    return findTargetMachine(stage, cartType);
}
```

**修改 enterBuilding() 方法**（约第 3532-3561 行）：

当前代码在 stage 递增后，直接创建新的 BeltItem 但忽略了 `spawnBeltItemFromCart()` 的返回值。修改为：

```java
// 处理完后递增 stage（已有代码，不变）
// ...

// 尝试将 DataCart 放到通往下一建筑的传送带上
if (!isArrived) {
    Point nextTarget = findTargetMachine(stage, cartType);
    if (nextTarget != null) {
        int nextCol = nextTarget.x / com.never_give_up.automation.demo.conveyor.BeltNetwork.TILE_SIZE;
        int nextRow = nextTarget.y / com.never_give_up.automation.demo.conveyor.BeltNetwork.TILE_SIZE;
        int dRow = nextRow - (int)(buildingY / com.never_give_up.automation.demo.conveyor.BeltNetwork.TILE_SIZE);
        int dCol = nextCol - (int)(buildingX / com.never_give_up.automation.demo.conveyor.BeltNetwork.TILE_SIZE);
        
        // 仅当两个建筑相邻时使用传送带
        if (Math.abs(dRow) + Math.abs(dCol) == 1) {
            BeltDirection dir = BeltDirection.fromDelta(dCol, dRow);
            if (dir != BeltDirection.NONE) {
                com.never_give_up.automation.demo.conveyor.BeltNetwork network =
                        new com.never_give_up.automation.demo.conveyor.BeltNetwork();
                com.never_give_up.automation.demo.model.BeltItem spawned = network.spawnBeltItemFromCart(this,
                        (int)(buildingY / com.never_give_up.automation.demo.conveyor.BeltNetwork.TILE_SIZE),
                        (int)(buildingX / com.never_give_up.automation.demo.conveyor.BeltNetwork.TILE_SIZE),
                        dir, game);
                if (spawned != null) {
                    isOnBelt = true;  // 成功上带
                    return;
                }
            }
        }
    }
    // 无传送带路径 → 退回飞行模式
    isOnBelt = false;
}
```

**改动要点**：
- 新增 `isOnBelt` 字段，标记 DataCart 正在传送带上
- 新增 `getTargetBuilding()` 包装 `findTargetMachine()`（private → public）
- `enterBuilding()` 中，传送带生成失败时设置 `isOnBelt = false`，让 DataCart 飞行
- 仅相邻建筑（曼哈顿距离=1）才使用传送带

### 2. DataCartFactoryGame.java — 修改 gameTick() DataCart 处理循环

**在 gameTick() 的 DataCart 循环中**（约第 2504 行），在调用 `cart.update()` 前加入腰带检测：

```java
for (DataCart cart : dataCarts) {
    int col = (int) (cart.x / TILE_SIZE);
    // ... 公网队列限制检查（不变）...

    // ===== 新增：传送带模式 =====
    if (cart.isOnBelt) {
        // 传送带上的 DataCart 由 BeltNetwork 移动，跳过飞行 update
        if (cart.isArrived || cart.isDropped) {
            toRemoveCarts.add(cart);
            updateTopLabel();
        }
        continue;
    }
    // ===== 结束：传送带模式 =====

    cart.update(); // 原飞行移动（不变）

    // 到达/丢弃处理（不变）
    if (cart.isDropped) {
        toRemoveCarts.add(cart);
        updateTopLabel();
    } else if (cart.isArrived) {
        handleCartArrival(cart);
        toRemoveCarts.add(cart);
        updateTopLabel();
    }
}
```

**在 gameTick() 的 belt 更新后加入**（约第 2498 行，beltNetwork.updateAll() 之后）：

```java
// ===== 新增：检测新建 DataCart 是否可以进入传送带 =====
for (DataCart cart : dataCarts) {
    if (cart.isOnBelt) continue;
    if (cart.isArrived || cart.isDropped) continue;
    tryActivateBeltForCart(cart);
}
```

**新增方法**（在 DataCartFactoryGame 类中）：

```java
/**
 * 尝试将 DataCart 从飞行模式切换到传送带模式
 * 在 cart.update() 到达建筑后（即 cart 位于建筑中心时）调用
 */
private void tryActivateBeltForCart(DataCart cart) {
    // 仅当 cart 位于建筑瓦片上
    int col = (int)(cart.x / TILE_SIZE);
    int row = (int)(cart.y / TILE_SIZE);
    if (row < 0 || row >= MAP_ROWS || col < 0 || col >= MAP_COLS) return;
    if (buildingLayout[row][col].equals("NONE")) return; // 不在建筑上

    // 获取下一个目标
    Point nextTarget = cart.getTargetBuilding();
    if (nextTarget == null) return;

    int nextCol = nextTarget.x / TILE_SIZE;
    int nextRow = nextTarget.y / TILE_SIZE;

    // 仅相邻建筑使用传送带
    int dRow = nextRow - row;
    int dCol = nextCol - col;
    if (Math.abs(dRow) + Math.abs(dCol) != 1) return;

    BeltDirection dir = BeltDirection.fromDelta(dCol, dRow);
    if (dir == BeltDirection.NONE) return;

    // 检查目标方向是否有传送带
    int beltRow = row + dir.dy;
    int beltCol = col + dir.dx;
    if (beltRow < 0 || beltRow >= MAP_ROWS || beltCol < 0 || beltCol >= MAP_COLS) return;
    if (beltGrid[beltRow][beltCol] == BeltDirection.NONE) return;

    // 上带！
    com.never_give_up.automation.demo.conveyor.BeltItem spawned =
            beltNetwork.spawnBeltItemFromCart(cart, row, col, dir, this);
    if (spawned != null) {
        cart.isOnBelt = true;
        appendToConsole("【🔧 传送带】: " + cart.cartType + " 进入传送带 " + dir);
    }
}
```

### 3. GameCanvas.java — 新增：跳过传送带上的 DataCart 绘制

在 DataCart 绘制循环起始处（约第 295 行）加入：

```java
// 绘制数据包
for (DataCart cart : game.dataCarts) {
    if (cart.isOnBelt) continue;  // 传送带上的 cart 由 BeltItem 绘制
    // ... 原有绘制代码 ...
}
```

## 边界情况处理

| 场景 | 处理方式 |
|------|---------|
| DataCart 创建时不在建筑上 | `tryActivateBeltForCart()` 检查 `buildingLayout[row][col]` 为 "NONE" 时跳过，cart 继续飞行 |
| 传送带路径不存在 | `tryActivateBeltForCart()` 检查 `beltGrid[beltRow][beltCol]` 为 NONE 时跳过；`enterBuilding()` 中 `spawnBeltItemFromCart()` 返回 null 时设置 `isOnBelt=false` |
| 建筑不相邻（距离 > 1） | 曼哈顿距离检查 `abs(dRow)+abs(dCol) != 1` 时跳过，使用飞行模式 |
| 最终 stage 到达 | `enterBuilding()` 设置 `isArrived=true`，gameTick() 处理移除 |
| 传送带上进入建筑后下一段 | `enterBuilding()` 处理 stage 递增后，再次尝试 spawn BeltItem，形成链式接力 |

## 验证方法

1. `mvn compile` 编译验证
2. 启动游戏，按 `B` 进入传送带模式，在相邻建筑间放置传送带
3. 触发网络协议演示（TCP 握手等），观察 DataCart 是否沿传送带移动而不是飞行
4. 拆除部分传送带，观察 DataCart 是否自动退回飞行模式
5. 验证 BeltItem 消失后 DataCart 仍然能在下一帧恢复正常飞行
