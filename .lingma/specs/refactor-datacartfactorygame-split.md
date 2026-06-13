# DataCartFactoryGame.java 拆分计划

## Context
`DataCartFactoryGame.java` 当前共 **7491 行**，包含一个主类 + 3 个内部类 + 2 个内部枚举 + 7 个内部静态类 + 1 个内部枚举 + 1 个内部静态类（嵌套在 DataCart 中），职责严重耦合，难以维护。

用户要求全面拆分为多个独立类文件。

## 拆分目标

| 文件 | 当前行数 | 目标行数 |
|------|---------|---------|
| `DataCartFactoryGame.java` | 7491 | ~2800 |
| 新增 15+ 个独立文件 | 0 | ~4600 |

## 包结构

```
com.never_give_up.automation.demo
├── DataCartFactoryGame.java      ← 瘦身版 JFrame (~2800行)
├── DataCart.java                 ← 从内部类提取 (~3400行)  
├── OreCart.java                  ← 从内部类提取 (~30行)
├── GameCanvas.java               ← 从内部类提取 (~350行)
│
└── model/
    ├── GameContext.java          ← 新增：DataCart 依赖注入上下文
    ├── BuildingZone.java         ← 从外类提取 (enum)
    ├── TcpState.java             ← 从外类提取 (enum)  
    ├── TlsState.java             ← 从外类提取 (enum)
    ├── DnsEntry.java             ← 从外类提取 (static class)
    ├── ArpEntry.java             ← 从外类提取 (static class)
    ├── NatEntry.java             ← 从外类提取 (static class)
    ├── RetransmissionTask.java   ← 从外类提取 (static class)
    ├── IpFragment.java           ← 从外类提取 (static class)
    ├── IpFragmentKey.java        ← 从外类提取 (static class)
    ├── PacketClass.java          ← 从 DataCart 提取 (enum)
    └── VisualFeedback.java       ← 从 DataCart 提取 (static class)
```

## 关键设计决策

### 1. GameContext 注入模式（解决 DataCart 对外类的耦合）
DataCart 在当前内部类中直接访问外类的 8 个字段和 2 个方法。提取为独立类后，通过 GameContext 统一注入：

```java
public class GameContext {
    private Point pcFactory;
    private String pcIpAddress;           // 需 setter（运行时变化）
    private String resolvedServerIp;      // 需 setter（运行时变化）
    private final List<DataCart> pendingDataCarts;
    private final Map<String, NatEntry> natTable;
    private final AtomicInteger natPortCounter;
    private int ipIdentifierCounter;       // 需 setter
    private final Consumer<String> appendToConsole;    // 方法引用
    private final Function<String, Point> findBuildingCoords; // 方法引用
}
```

### 2. GameCanvas 保持对外类引用
GameCanvas 的 `paintComponent()` 引用了外类 20+ 个字段（buildingLayout, dataCarts, oreCarts, viewOffsetX 等）。最简单的方案是保留外类引用：

```java
public class GameCanvas extends JPanel {
    private final DataCartFactoryGame game;
    public GameCanvas(DataCartFactoryGame game) { this.game = game; }
}
```

### 3. 文件位置
- 不使用已有的 `winModel/` 和 `ui/` 包（它们是另一套独立实现，API 不兼容）
- 小数据类放入 `model/` 子包
- DataCart、OreCart、GameCanvas 与主类同包（demo），避免大量 import 变更

## 执行步骤

### Phase 1：创建零依赖基础类（可并行）

1. `model/BuildingZone.java` — 从 DataCartFactoryGame:100-136 提取
2. `model/TcpState.java` — 从 DataCartFactoryGame:141-143 提取  
3. `model/TlsState.java` — 从 DataCartFactoryGame:145-147 提取
4. `model/DnsEntry.java` — 从 DataCartFactoryGame:149-169 提取
5. `model/ArpEntry.java` — 从 DataCartFactoryGame:171-181 提取
6. `model/NatEntry.java` — 从 DataCartFactoryGame:183-195 提取
7. `model/RetransmissionTask.java` — 从 DataCartFactoryGame:197-207 提取
8. `model/IpFragment.java` — 从 DataCartFactoryGame:380-390 提取
9. `model/IpFragmentKey.java` — 从 DataCartFactoryGame:393-418 提取
10. `model/PacketClass.java` — 从 DataCart:4033-4038 提取
11. `model/VisualFeedback.java` — 从 DataCart:4042-4052 提取

### Phase 2：创建依赖注入层

12. `model/GameContext.java` — 新增类，聚合 DataCart 所需的外类依赖

### Phase 3：提取核心逻辑类（顺序执行）

13. `OreCart.java` — 从 DataCartFactoryGame:3725-3747 提取
    - 构造函数增加 `Point pcFactory` 参数
14. `DataCart.java` — 从 DataCartFactoryGame:3750-7136 提取
    - 构造函数增加 `GameContext context` 参数
    - 替换 `findBuildingCoords(X)` → `context.getFindBuildingCoords().apply(X)`
    - 替换 `appendToConsole(X)` → `context.getAppendToConsole().accept(X)`
    - 替换 `pcFactory.x` → `context.getPcFactory().x`
    - 替换 `pendingDataCarts.add(X)` → `context.getPendingDataCarts().add(X)`
    - 保留 `@Data` 注解
    - 保留所有 `public` 字段访问（被外类 ~50 处直接访问）

### Phase 4：提取 UI 层

15. `GameCanvas.java` — 从 DataCartFactoryGame:7138-7486 提取
    - 构造函数接受 `DataCartFactoryGame game` 参数
    - 所有 `buildingLayout` → `game.buildingLayout`
    - 所有 `dataCarts` → `game.dataCarts`

### Phase 5：瘦身 DataCartFactoryGame.java

- 删除所有已提取的内类定义
- 删除已提取的枚举和静态类定义
- 添加 import 语句
- 创建 GameContext 实例（在构造函数中初始化所有回调）
- 修改所有 `new DataCart(x, y, type, seq)` → `new DataCart(x, y, type, seq, gameContext)`（约 30 处）
- 修改所有 `new OreCart(x, y, type)` → `new OreCart(x, y, type, pcFactory)`（约 4 处）
- 将 `GameCanvas` 创建改为 `new GameCanvas(this)`

## 验证方式

每完成一个 Phase 运行一次：
```bash
cd e:\newgit\automation && .\mvnw.cmd compile -q
```

最终验证：启动应用程序并测试以下场景：
1. 界面正常渲染（GameCanvas 绘制无异常）
2. 游戏循环正常运行（Timer 触发 gameTick）
3. DataCart 正常移动（点击发送 PING）
4. HTTP 子工厂测试按钮正常工作
5. FTP 演示模式正常工作
