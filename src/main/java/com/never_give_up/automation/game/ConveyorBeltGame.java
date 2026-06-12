package com.never_give_up.automation.game;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConveyorBeltGame extends JFrame {
    // ==================== 地图配置 ====================
    private final int TILE_SIZE = 48;
    private final int MAP_COLS = 15;
    private final int MAP_ROWS = 10;

    // 传送带布局：数字代表传送带上的物品类型
    private int[][] conveyorItems = new int[MAP_ROWS][MAP_COLS];
    private String[][] buildingLayout = new String[MAP_ROWS][MAP_COLS];

    // ==================== 游戏状态 ====================
    private int funds = 5000;
    private int score = 0;
    private boolean isRunning = true;
    private String selectedBuilding = "CLAW";

    // ==================== 爪子系统 ====================
    private int clawRow = 3;
    private int clawCol = 4;
    private int clawAngle = 0;  // 0=右, 90=下, 180=左, 270=上
    private int clawLength = 1;  // 爪子长度
    private int clawInventory = 0;  // 爪子抓取的物品（0表示空）
    private int clawInventoryType = 0;

    // ==================== 机器A系统 ====================
    private int machineRow = 2;
    private int machineCol = 4;
    private int machineNeedType = 3;
    private int machineNeedCount = 5;
    private int machineStock = 0;
    private boolean machineRunning = false;
    private boolean machineLightOn = false;

    // ==================== 动态实体 ====================
    private List<MovingItem> movingItems = new CopyOnWriteArrayList<>();
    private List<SmokeParticle> smokeParticles = new CopyOnWriteArrayList<>();

    // ==================== UI组件 ====================
    private GameCanvas canvas;
    private JLabel lblFunds;
    private JLabel lblScore;
    private JLabel lblMachineStatus;
    private JLabel lblClawStatus;
    private JTextArea txtLog;
    private JPanel shopPanel;

    // ==================== 定时器 ====================
    private Timer gameTimer;
    private Timer machineBlinkTimer;
    private Timer conveyorTimer;

    public ConveyorBeltGame() {
        setTitle("🏭 自动化传送带关卡 - 爪子系统");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initMap();
        initBuildings();
        startTimers();
        createUI();

        setVisible(true);
    }

    private void initMap() {
        // 初始化传送带上的物品
        // 行3-6是传送带区域
        for (int r = 3; r < 7; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                conveyorItems[r][c] = 0;
            }
        }

        // 放置初始物品
        conveyorItems[3][2] = 1;
        conveyorItems[3][3] = 2;
        conveyorItems[4][5] = 3;
        conveyorItems[4][6] = 4;
        conveyorItems[5][8] = 5;
        conveyorItems[5][9] = 6;
        conveyorItems[6][10] = 7;
        conveyorItems[6][11] = 8;
    }

    private void initBuildings() {
        // 清空建筑
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                buildingLayout[r][c] = "NONE";
            }
        }

        // 放置爪子（初始位置）
        buildingLayout[clawRow][clawCol] = "CLAW";

        // 放置机器A
        buildingLayout[machineRow][machineCol] = "MACHINE_A";

        // 放置传送带起点和终点
        buildingLayout[3][0] = "BELT_START";
        buildingLayout[6][MAP_COLS-1] = "BELT_END";

        // 放置一些装饰建筑
        buildingLayout[1][2] = "DECO_1";
        buildingLayout[8][12] = "DECO_2";
    }

    private void startTimers() {
        // 游戏主循环 (60fps)
        gameTimer = new Timer(16, e -> {
            if (isRunning) {
                updateMovingItems();
                updateSmokeParticles();
                canvas.repaint();
                updateUI();
            }
        });
        gameTimer.start();

        // 传送带滚动 (每1秒)
        conveyorTimer = new Timer(1000, e -> scrollConveyor());
        conveyorTimer.start();

        // 机器闪烁灯 (每0.3秒)
        machineBlinkTimer = new Timer(300, e -> {
            if (machineRunning) {
                machineLightOn = !machineLightOn;
                if (machineLightOn) {
                    // 产生烟雾粒子
                    for (int i = 0; i < 3; i++) {
                        smokeParticles.add(new SmokeParticle(
                                machineCol * TILE_SIZE + TILE_SIZE/2,
                                machineRow * TILE_SIZE + TILE_SIZE/2 - 10
                        ));
                    }
                }
            } else {
                machineLightOn = false;
            }
            canvas.repaint();
        });
        machineBlinkTimer.start();
    }

    private void scrollConveyor() {
        // 传送带向右滚动
        for (int r = 3; r < 7; r++) {
            int lastItem = conveyorItems[r][MAP_COLS - 1];
            for (int c = MAP_COLS - 1; c > 0; c--) {
                conveyorItems[r][c] = conveyorItems[r][c - 1];
            }
            conveyorItems[r][0] = lastItem;
        }

        // 物品到达终点时加分
        for (int r = 3; r < 7; r++) {
            if (conveyorItems[r][MAP_COLS - 1] != 0) {
                int itemValue = conveyorItems[r][MAP_COLS - 1];
                score += itemValue * 10;
                conveyorItems[r][MAP_COLS - 1] = 0;
                addLog("【🏆 得分】: 物品 " + itemValue + " 号到达终点 +" + (itemValue * 10) + "分");
            }
        }

        addLog("【🔄 传送带】: 向右滚动一格");
    }

    // 手动抓取物品
    private void grabItem() {
        if (!isRunning) return;

        // 如果爪子已经有物品，不能抓取
        if (clawInventory != 0) {
            addLog("【🦞 爪子】: 爪子已有物品，请先放入机器");
            return;
        }

        // 获取爪子能抓取的格子
        List<Point> reachable = getClawReachableCells();

        // 尝试从传送带抓取物品
        boolean grabbed = false;
        for (Point p : reachable) {
            if (p.x >= 3 && p.x < 7 && conveyorItems[p.x][p.y] != 0) {
                int itemType = conveyorItems[p.x][p.y];
                conveyorItems[p.x][p.y] = 0;
                clawInventory = 1;
                clawInventoryType = itemType;
                grabbed = true;
                addLog(String.format("【🦞 爪子】: 抓取 %d 号物品", itemType));
                break;
            }
        }

        if (!grabbed) {
            addLog("【🦞 爪子】: 范围内没有可抓取的物品");
        }

        canvas.repaint();
    }

    // 手动放置物品到机器
    private void placeToMachine() {
        if (!isRunning) return;

        // 如果爪子没有物品，不能放置
        if (clawInventory == 0) {
            addLog("【🤖 机器A】: 爪子没有物品可放置");
            return;
        }

        // 检查机器是否需要这个物品
        if (clawInventoryType == machineNeedType && machineStock < machineNeedCount) {
            // 放置到机器
            machineStock++;
            machineRunning = true;
            addLog(String.format("【🤖 机器A】: 爪子放入 %d 号物品，库存: %d/%d",
                    clawInventoryType, machineStock, machineNeedCount));
            clawInventory = 0;
            clawInventoryType = 0;

            // 创建移动动画
            movingItems.add(new MovingItem(
                    clawCol * TILE_SIZE + TILE_SIZE/2,
                    clawRow * TILE_SIZE + TILE_SIZE/2,
                    machineCol * TILE_SIZE + TILE_SIZE/2,
                    machineRow * TILE_SIZE + TILE_SIZE/2,
                    clawInventoryType
            ));

            // 检查机器任务完成
            if (machineStock >= machineNeedCount) {
                machineRunning = false;
                int reward = machineNeedCount * 100;
                funds += reward;
                score += reward;
                addLog(String.format("【🎉 任务完成】: 机器A收集完成! 获得 %d 资金和积分", reward));
                resetMachine();
            }
        } else {
            if (clawInventoryType != machineNeedType) {
                addLog(String.format("【🤖 机器A】: 机器需要 %d 号物品，但爪子持有 %d 号物品",
                        machineNeedType, clawInventoryType));
            } else {
                addLog(String.format("【🤖 机器A】: 机器已完成任务 (%d/%d)，等待新任务",
                        machineStock, machineNeedCount));
            }
        }

        canvas.repaint();
    }

    private List<Point> getClawReachableCells() {
        List<Point> cells = new ArrayList<>();

        switch (clawAngle) {
            case 0: // 向右
                for (int c = clawCol; c < clawCol + clawLength && c < MAP_COLS; c++) {
                    cells.add(new Point(clawRow, c));
                }
                break;
            case 90: // 向下
                for (int r = clawRow; r < clawRow + clawLength && r < MAP_ROWS; r++) {
                    cells.add(new Point(r, clawCol));
                }
                break;
            case 180: // 向左
                for (int c = clawCol; c >= clawCol - clawLength + 1 && c >= 0; c--) {
                    cells.add(new Point(clawRow, c));
                }
                break;
            case 270: // 向上
                for (int r = clawRow; r >= clawRow - clawLength + 1 && r >= 0; r--) {
                    cells.add(new Point(r, clawCol));
                }
                break;
        }

        return cells;
    }

    private void updateMovingItems() {
        // 更新运动中的物品（从爪子到机器的动画）
        for (MovingItem item : movingItems) {
            item.update();
        }
        movingItems.removeIf(item -> item.isArrived);
    }

    private void updateSmokeParticles() {
        for (SmokeParticle p : smokeParticles) {
            p.update();
        }
        smokeParticles.removeIf(p -> p.isDead);
    }

    private void resetMachine() {
        machineStock = 0;
        machineRunning = false;
        machineNeedCount = 5 + (int)(Math.random() * 3);
        machineNeedType = 1 + (int)(Math.random() * 8);
        addLog(String.format("【🤖 机器A】: 新任务 - 需要 %d 个 %d 号物品", machineNeedCount, machineNeedType));
    }

    private void createUI() {
        // 顶部状态栏
        JPanel topPanel = new JPanel(new GridLayout(1, 4, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("📊 状态面板"));
        topPanel.setPreferredSize(new Dimension(0, 60));

        lblFunds = new JLabel("💰 资金: " + funds, SwingConstants.CENTER);
        lblFunds.setFont(new Font("微软雅黑", Font.BOLD, 14));
        lblScore = new JLabel("⭐ 积分: " + score, SwingConstants.CENTER);
        lblScore.setFont(new Font("微软雅黑", Font.BOLD, 14));
        lblMachineStatus = new JLabel("🤖 机器: 等待物品", SwingConstants.CENTER);
        lblMachineStatus.setFont(new Font("微软雅黑", Font.BOLD, 12));
        lblClawStatus = new JLabel("🦞 爪子: 空 → 右", SwingConstants.CENTER);
        lblClawStatus.setFont(new Font("微软雅黑", Font.BOLD, 12));

        topPanel.add(lblFunds);
        topPanel.add(lblScore);
        topPanel.add(lblMachineStatus);
        topPanel.add(lblClawStatus);
        add(topPanel, BorderLayout.NORTH);

        // 中间画布
        canvas = new GameCanvas();
        canvas.setPreferredSize(new Dimension(MAP_COLS * TILE_SIZE, MAP_ROWS * TILE_SIZE));
        canvas.setBackground(new Color(30, 35, 45));

        JScrollPane scrollPane = new JScrollPane(canvas);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // 右侧面板
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(280, 0));

        // 建筑商店
        shopPanel = new JPanel();
        shopPanel.setLayout(new BoxLayout(shopPanel, BoxLayout.Y_AXIS));
        shopPanel.setBorder(BorderFactory.createTitledBorder("🏭 建筑商店"));

        buildShopUI();

        JScrollPane shopScroll = new JScrollPane(shopPanel);
        shopScroll.setPreferredSize(new Dimension(280, 200));
        rightPanel.add(shopScroll, BorderLayout.NORTH);

        // 日志面板
        txtLog = new JTextArea(15, 25);
        txtLog.setEditable(false);
        txtLog.setBackground(new Color(20, 25, 35));
        txtLog.setForeground(new Color(100, 255, 150));
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(txtLog);
        logScroll.setBorder(BorderFactory.createTitledBorder("📋 事件日志"));
        rightPanel.add(logScroll, BorderLayout.CENTER);

        // 控制按钮
        JPanel controlPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("🎮 控制"));

        JButton btnReset = new JButton("🔄 重置游戏");
        btnReset.addActionListener(e -> resetGame());

        JButton btnClawLeft = new JButton("← 爪子左移");
        btnClawLeft.addActionListener(e -> moveClaw(0, -1));

        JButton btnClawRight = new JButton("爪子右移 →");
        btnClawRight.addActionListener(e -> moveClaw(0, 1));

        JButton btnClawUp = new JButton("↑ 爪子上移");
        btnClawUp.addActionListener(e -> moveClaw(-1, 0));

        JButton btnClawDown = new JButton("爪子下移 ↓");
        btnClawDown.addActionListener(e -> moveClaw(1, 0));

        JButton btnRotate = new JButton("🔄 旋转爪子");
        btnRotate.addActionListener(e -> {
            clawAngle = (clawAngle + 90) % 360;
            updateClawDisplay();
            canvas.repaint();
        });

        JButton btnGrab = new JButton("🦞 抓取物品");
        btnGrab.addActionListener(e -> grabItem());
        btnGrab.setBackground(new Color(100, 200, 100));

        JButton btnPlace = new JButton("🤖 放入机器");
        btnPlace.addActionListener(e -> placeToMachine());
        btnPlace.setBackground(new Color(255, 200, 100));

        controlPanel.add(btnReset);
        controlPanel.add(btnRotate);
        controlPanel.add(btnClawLeft);
        controlPanel.add(btnClawRight);
        controlPanel.add(btnClawUp);
        controlPanel.add(btnClawDown);
        controlPanel.add(btnGrab);
        controlPanel.add(btnPlace);

        rightPanel.add(controlPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // 底部提示
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("💡 提示: 移动爪子位置 → 旋转爪子方向 → 点击【抓取物品】→ 移动到机器旁边 → 点击【放入机器】"));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void buildShopUI() {
        String[][] buildings = {
                {"CLAW", "🦞 爪子 ($0)"},
                {"MACHINE_A", "🤖 机器A ($500)"},
                {"DECO_1", "🏢 装饰建筑1 ($100)"},
                {"DECO_2", "🏭 装饰建筑2 ($100)"}
        };

        ButtonGroup group = new ButtonGroup();

        for (String[] b : buildings) {
            JRadioButton btn = new JRadioButton(b[1]);
            btn.setActionCommand(b[0]);
            btn.addActionListener(e -> selectedBuilding = e.getActionCommand());
            group.add(btn);
            shopPanel.add(btn);
        }

        // 默认选中爪子
        ((JRadioButton)shopPanel.getComponent(0)).setSelected(true);
    }

    private void moveClaw(int dr, int dc) {
        int newRow = clawRow + dr;
        int newCol = clawCol + dc;

        if (newRow >= 0 && newRow < MAP_ROWS && newCol >= 0 && newCol < MAP_COLS) {
            // 更新建筑布局
            buildingLayout[clawRow][clawCol] = "NONE";
            clawRow = newRow;
            clawCol = newCol;
            buildingLayout[clawRow][clawCol] = "CLAW";

            addLog(String.format("【🦞 爪子】: 移动到 (%d, %d)", clawRow, clawCol));
            canvas.repaint();
        }
    }

    private void updateUI() {
        lblFunds.setText("💰 资金: " + funds);
        lblScore.setText("⭐ 积分: " + score);

        String machineText = String.format("🤖 机器: %d/%d (需要%d号)",
                machineStock, machineNeedCount, machineNeedType);
        lblMachineStatus.setText(machineText);

        String clawInv = clawInventory != 0 ? "持有" + clawInventoryType : "空";
        String[] dirs = {"右", "下", "左", "上"};
        lblClawStatus.setText(String.format("🦞 爪子: %s → %s", clawInv, dirs[clawAngle / 90]));
    }

    private void updateClawDisplay() {
        String[] dirs = {"右", "下", "左", "上"};
        addLog(String.format("【🦞 爪子】: 方向改为 %s", dirs[clawAngle / 90]));
    }

    private void addLog(String msg) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append(msg + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());

            // 限制日志行数
            if (txtLog.getLineCount() > 200) {
                try {
                    int end = txtLog.getLineStartOffset(100);
                    txtLog.replaceRange("", 0, end);
                } catch (Exception e) {}
            }
        });
    }

    private void resetGame() {
        funds = 5000;
        score = 0;
        machineStock = 0;
        machineRunning = false;
        clawInventory = 0;
        clawInventoryType = 0;

        initMap();
        initBuildings();

        addLog("【🔄 游戏重置】: 所有状态已恢复");
        canvas.repaint();
    }

    // ==================== 内部类 ====================

    private class GameCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 绘制网格和背景
            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    int x = c * TILE_SIZE;
                    int y = r * TILE_SIZE;

                    // 传送带区域背景
                    if (r >= 3 && r < 7) {
                        g2.setColor(new Color(60, 70, 80));
                        g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        g2.setColor(new Color(100, 110, 120));
                        // 绘制传送带纹理
                        for (int i = 0; i < 3; i++) {
                            g2.fillRect(x + 5, y + TILE_SIZE/2 - 2 + i*3, TILE_SIZE - 10, 2);
                        }
                    } else {
                        g2.setColor(new Color(40, 45, 55));
                        g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    }

                    // 网格线
                    g2.setColor(new Color(80, 85, 95));
                    g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);

                    // 绘制建筑
                    String building = buildingLayout[r][c];
                    if (!building.equals("NONE")) {
                        drawBuilding(g2, x, y, building);
                    }

                    // 绘制传送带上的物品
                    if (r >= 3 && r < 7 && conveyorItems[r][c] != 0) {
                        drawItem(g2, x, y, conveyorItems[r][c]);
                    }
                }
            }

            // 绘制爪子抓取范围指示器
            drawClawRange(g2);

            // 绘制机器烟雾
            for (SmokeParticle p : smokeParticles) {
                p.draw(g2);
            }

            // 绘制运动中的物品
            for (MovingItem item : movingItems) {
                item.draw(g2);
            }

            // 绘制爪子图标（覆盖在建筑上）
            drawClaw(g2);
        }

        private void drawBuilding(Graphics2D g2, int x, int y, String building) {
            switch (building) {
                case "CLAW":
                    // 爪子建筑用半透明背景
                    g2.setColor(new Color(100, 200, 255, 80));
                    g2.fillRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                    g2.setColor(new Color(100, 200, 255));
                    g2.drawRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                    break;

                case "MACHINE_A":
                    // 机器A - 带闪烁灯
                    if (machineRunning && machineLightOn) {
                        g2.setColor(new Color(255, 100, 0));
                        g2.fillRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                        g2.setColor(Color.YELLOW);
                        g2.fillOval(x + TILE_SIZE/2 - 8, y + 5, 6, 6);
                    } else if (machineRunning) {
                        g2.setColor(new Color(200, 80, 0));
                        g2.fillRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                        g2.setColor(Color.ORANGE);
                        g2.fillOval(x + TILE_SIZE/2 - 8, y + 5, 6, 6);
                    } else {
                        g2.setColor(new Color(100, 100, 120));
                        g2.fillRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8);
                    }
                    g2.setColor(Color.WHITE);
                    g2.drawString("A", x + TILE_SIZE/2 - 4, y + TILE_SIZE/2 + 4);

                    // 显示需求
                    g2.setFont(new Font("微软雅黑", Font.PLAIN, 9));
                    g2.drawString(machineNeedType + "x" + machineStock + "/" + machineNeedCount,
                            x + 5, y + TILE_SIZE - 8);
                    break;

                case "BELT_START":
                    g2.setColor(new Color(50, 200, 100));
                    g2.fillOval(x + 10, y + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                    g2.setColor(Color.WHITE);
                    g2.drawString("▶", x + TILE_SIZE/2 - 5, y + TILE_SIZE/2 + 5);
                    break;

                case "BELT_END":
                    g2.setColor(new Color(200, 100, 50));
                    g2.fillOval(x + 10, y + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                    g2.setColor(Color.WHITE);
                    g2.drawString("●", x + TILE_SIZE/2 - 4, y + TILE_SIZE/2 + 4);
                    break;

                default:
                    g2.setColor(new Color(80, 100, 150));
                    g2.fillRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12);
                    break;
            }
        }

        private void drawItem(Graphics2D g2, int x, int y, int type) {
            Color[] colors = {
                    new Color(255, 100, 100), // 1 - 红
                    new Color(100, 255, 100), // 2 - 绿
                    new Color(100, 100, 255), // 3 - 蓝
                    new Color(255, 255, 100), // 4 - 黄
                    new Color(255, 100, 255), // 5 - 紫
                    new Color(100, 255, 255), // 6 - 青
                    new Color(255, 165, 0),   // 7 - 橙
                    new Color(200, 100, 100)  // 8 - 棕
            };

            g2.setColor(colors[(type - 1) % colors.length]);
            g2.fillOval(x + 8, y + 8, TILE_SIZE - 16, TILE_SIZE - 16);
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(type), x + TILE_SIZE/2 - 4, y + TILE_SIZE/2 + 4);

            // 添加高光
            g2.setColor(new Color(255, 255, 255, 100));
            g2.fillOval(x + 10, y + 10, 8, 8);
        }

        private void drawClaw(Graphics2D g2) {
            int x = clawCol * TILE_SIZE;
            int y = clawRow * TILE_SIZE;

            // 根据方向绘制爪子
            g2.setColor(new Color(200, 150, 50));
            g2.setStroke(new BasicStroke(3));

            int cx = x + TILE_SIZE/2;
            int cy = y + TILE_SIZE/2;

            switch (clawAngle) {
                case 0: // 向右
                    g2.drawLine(cx, cy, cx + TILE_SIZE/3, cy);
                    g2.drawLine(cx + TILE_SIZE/3, cy - 5, cx + TILE_SIZE/3 + 5, cy);
                    g2.drawLine(cx + TILE_SIZE/3, cy + 5, cx + TILE_SIZE/3 + 5, cy);
                    break;
                case 90: // 向下
                    g2.drawLine(cx, cy, cx, cy + TILE_SIZE/3);
                    g2.drawLine(cx - 5, cy + TILE_SIZE/3, cx, cy + TILE_SIZE/3 + 5);
                    g2.drawLine(cx + 5, cy + TILE_SIZE/3, cx, cy + TILE_SIZE/3 + 5);
                    break;
                case 180: // 向左
                    g2.drawLine(cx, cy, cx - TILE_SIZE/3, cy);
                    g2.drawLine(cx - TILE_SIZE/3, cy - 5, cx - TILE_SIZE/3 - 5, cy);
                    g2.drawLine(cx - TILE_SIZE/3, cy + 5, cx - TILE_SIZE/3 - 5, cy);
                    break;
                case 270: // 向上
                    g2.drawLine(cx, cy, cx, cy - TILE_SIZE/3);
                    g2.drawLine(cx - 5, cy - TILE_SIZE/3, cx, cy - TILE_SIZE/3 - 5);
                    g2.drawLine(cx + 5, cy - TILE_SIZE/3, cx, cy - TILE_SIZE/3 - 5);
                    break;
            }

            // 爪子持有物品
            if (clawInventory != 0) {
                drawItem(g2, x, y, clawInventoryType);
            }
        }

        private void drawClawRange(Graphics2D g2) {
            List<Point> reachable = getClawReachableCells();
            g2.setColor(new Color(255, 255, 0, 80));
            g2.setStroke(new BasicStroke(2));

            for (Point p : reachable) {
                int x = p.y * TILE_SIZE;
                int y = p.x * TILE_SIZE;
                g2.drawRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
            }
        }
    }

    private class MovingItem {
        double x, y;
        int itemType;
        int targetX, targetY;
        boolean isArrived = false;
        double progress = 0;

        public MovingItem(int fromX, int fromY, int toX, int toY, int type) {
            this.x = fromX;
            this.y = fromY;
            this.targetX = toX;
            this.targetY = toY;
            this.itemType = type;
        }

        public void update() {
            progress += 0.05;
            if (progress >= 1.0) {
                isArrived = true;
            }
            x = x + (targetX - x) * 0.1;
            y = y + (targetY - y) * 0.1;
        }

        public void draw(Graphics2D g2) {
            int size = TILE_SIZE / 2;
            g2.setColor(new Color(255, 200, 50));
            g2.fillOval((int)x - size/2, (int)y - size/2, size, size);
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf(itemType), (int)x - 4, (int)y + 4);
        }
    }

    private class SmokeParticle {
        double x, y;
        double vx, vy;
        int life = 30;
        boolean isDead = false;

        public SmokeParticle(double x, double y) {
            this.x = x;
            this.y = y;
            this.vx = (Math.random() - 0.5) * 2;
            this.vy = -Math.random() * 3;
        }

        public void update() {
            x += vx;
            y += vy;
            life--;
            if (life <= 0) isDead = true;
        }

        public void draw(Graphics2D g2) {
            int alpha = Math.min(100, life * 3);
            g2.setColor(new Color(150, 150, 150, alpha));
            int size = 8 - life / 5;
            g2.fillOval((int)x - size/2, (int)y - size/2, size, size);
        }
    }

    private static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConveyorBeltGame());
    }
}