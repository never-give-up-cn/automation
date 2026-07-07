package com.never_give_up.automation.scanner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * Home Assistant 集成面板
 * <p>
 * 显示 HA 连接状态、设备列表，支持设备控制（开关、调温等）
 * 可作为 Spring Bean 注入使用，也可独立运行
 * </p>
 */
public class HomeAssistantPanel extends JPanel {

    // ========== 状态控件 ==========
    private JLabel lblConnectionStatus;
    private JLabel lblDeviceCount;
    private JLabel lblLastSync;
    private JButton btnConnect;
    private JButton btnRefresh;
    private JButton btnReconnect;

    // ========== 设备列表 ==========
    private JTable deviceTable;
    private DefaultTableModel deviceTableModel;

    // ========== 控制面板 ==========
    private JTextField txtEntityId;
    private JButton btnTurnOn;
    private JButton btnTurnOff;
    private JButton btnGetState;

    // ========== 日志 ==========
    private JTextArea txtLog;

    public HomeAssistantPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createStatusPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createLogPanel(), BorderLayout.SOUTH);
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout(10, 10));
        statusPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "🏠 Home Assistant 连接状态",
                TitledBorder.LEFT, TitledBorder.TOP));

        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 10, 5));

        lblConnectionStatus = new JLabel("⚪ 未连接");
        lblConnectionStatus.setFont(new Font("微软雅黑", Font.BOLD, 14));

        lblDeviceCount = new JLabel("📊 设备数: -");
        lblLastSync = new JLabel("⏱ 上次同步: -");

        btnConnect = new JButton("🔗 连接");
        btnConnect.setBackground(new Color(76, 175, 80));
        btnConnect.setForeground(Color.WHITE);
        btnConnect.addActionListener(e -> log("连接功能需在 Spring 环境中使用"));

        btnReconnect = new JButton("🔄 重连");
        btnReconnect.setBackground(new Color(255, 152, 0));
        btnReconnect.setForeground(Color.WHITE);
        btnReconnect.addActionListener(e -> log("重连功能需在 Spring 环境中使用"));

        btnRefresh = new JButton("📥 刷新设备");
        btnRefresh.setBackground(new Color(33, 150, 243));
        btnRefresh.setForeground(Color.WHITE);

        infoPanel.add(lblConnectionStatus);
        infoPanel.add(lblDeviceCount);
        infoPanel.add(lblLastSync);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnConnect);
        btnPanel.add(btnReconnect);
        btnPanel.add(btnRefresh);

        statusPanel.add(infoPanel, BorderLayout.CENTER);
        statusPanel.add(btnPanel, BorderLayout.EAST);

        return statusPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // 设备列表
        deviceTableModel = new DefaultTableModel(
                new String[]{"设备ID", "状态", "类型", "最近更新"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        deviceTable = new JTable(deviceTableModel);
        deviceTable.setFont(new Font("Consolas", Font.PLAIN, 12));
        deviceTable.setRowHeight(24);
        deviceTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        deviceTable.getTableHeader().setBackground(new Color(63, 81, 181));
        deviceTable.getTableHeader().setForeground(Color.WHITE);
        deviceTable.setAutoCreateRowSorter(true);
        deviceTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        deviceTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        deviceTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        deviceTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        // 状态行高亮
        deviceTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String state = column == 1 && value != null ? value.toString() : "";
                    switch (state) {
                        case "on" -> setBackground(new Color(200, 255, 200));
                        case "off" -> setBackground(new Color(230, 230, 230));
                        case "unavailable" -> setBackground(new Color(255, 200, 200));
                        default -> setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(deviceTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("设备列表"));

        // 控制面板
        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("设备控制"));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Entity ID:"));
        txtEntityId = new JTextField(30);
        txtEntityId.setToolTipText("例如: switch.living_room_light, climate.air_conditioner");
        inputPanel.add(txtEntityId);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTurnOn = new JButton("🔛 开启");
        btnTurnOn.setBackground(new Color(76, 175, 80));
        btnTurnOn.setForeground(Color.WHITE);
        btnTurnOn.addActionListener(e -> log("开关控制需在 Spring 环境中使用: turn_on " + txtEntityId.getText()));

        btnTurnOff = new JButton("🔴 关闭");
        btnTurnOff.setBackground(new Color(244, 67, 54));
        btnTurnOff.setForeground(Color.WHITE);
        btnTurnOff.addActionListener(e -> log("开关控制需在 Spring 环境中使用: turn_off " + txtEntityId.getText()));

        btnGetState = new JButton("📋 查询状态");
        btnGetState.addActionListener(e -> log("状态查询需在 Spring 环境中使用: " + txtEntityId.getText()));

        actionPanel.add(btnTurnOn);
        actionPanel.add(btnTurnOff);
        actionPanel.add(btnGetState);

        controlPanel.add(inputPanel, BorderLayout.NORTH);
        controlPanel.add(actionPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, controlPanel);
        split.setResizeWeight(0.7);
        centerPanel.add(split, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("操作日志"));
        logPanel.setPreferredSize(new Dimension(0, 150));

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLog.setBackground(new Color(10, 12, 16));
        txtLog.setForeground(new Color(50, 255, 120));

        JScrollPane scroll = new JScrollPane(txtLog);
        logPanel.add(scroll, BorderLayout.CENTER);

        return logPanel;
    }

    // ========== 公开方法 ==========

    /** 更新连接状态 */
    public void setConnected(boolean connected) {
        SwingUtilities.invokeLater(() -> {
            if (connected) {
                lblConnectionStatus.setText("🟢 已连接");
                lblConnectionStatus.setForeground(new Color(76, 175, 80));
            } else {
                lblConnectionStatus.setText("🔴 未连接");
                lblConnectionStatus.setForeground(new Color(244, 67, 54));
            }
        });
    }

    /** 更新设备数量 */
    public void updateDeviceCount(int count) {
        SwingUtilities.invokeLater(() ->
                lblDeviceCount.setText("📊 设备数: " + count));
    }

    /** 更新同步时间 */
    public void updateSyncTime() {
        SwingUtilities.invokeLater(() ->
                lblLastSync.setText("⏱ 上次同步: " +
                        java.time.LocalTime.now().format(
                                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))));
    }

    /** 添加设备到列表 */
    public void addDevice(String entityId, String state, String type, String lastUpdated) {
        SwingUtilities.invokeLater(() ->
                deviceTableModel.addRow(new Object[]{entityId, state, type, lastUpdated}));
    }

    /** 清空设备列表 */
    public void clearDevices() {
        SwingUtilities.invokeLater(() -> deviceTableModel.setRowCount(0));
    }

    /** 添加日志 */
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            String time = java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            txtLog.append("[" + time + "] " + message + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    // ========== 加载模拟数据 ==========

    public void loadDemoData() {
        setConnected(true);
        updateDeviceCount(12);
        updateSyncTime();

        addDevice("sensor.temperature", "28.5", "sensor", "2024-01-15 14:30");
        addDevice("sensor.humidity", "65", "sensor", "2024-01-15 14:30");
        addDevice("switch.living_room_light", "on", "switch", "2024-01-15 14:28");
        addDevice("switch.kitchen_light", "off", "switch", "2024-01-15 14:25");
        addDevice("climate.air_conditioner", "cool", "climate", "2024-01-15 14:29");
        addDevice("cover.garage_door", "closed", "cover", "2024-01-15 14:20");
        addDevice("binary_sensor.motion", "clear", "binary_sensor", "2024-01-15 14:30");
        addDevice("light.bedroom", "off", "light", "2024-01-15 14:15");

        log("Home Assistant 面板已加载演示数据");
    }

    // ========== 独立运行 ==========

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("Home Assistant 设备管理");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 800);
        frame.setLocationRelativeTo(null);
        HomeAssistantPanel panel = new HomeAssistantPanel();
        panel.loadDemoData();
        frame.add(panel);
        frame.setVisible(true);
    }
}
