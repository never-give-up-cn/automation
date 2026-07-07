package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.scanner.HomeAssistantPanel;
import com.never_give_up.automation.scanner.PortScanPanel;
import com.never_give_up.automation.scanner.PortScannerService;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.io.Serial;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 自动化工具集成面板 - 主窗口
 * <p>
 * 集成所有现有工具：端口扫描、网络协议模拟、Home Assistant、工厂监控、地址配置等
 * </p>
 */
public class AutomationDashboard extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ========== 桌面面板 ==========
    private JTabbedPane tabbedPane;
    private JLabel lblStatusBar;
    private Timer statusTimer;

    // ========== 工具面板 ==========
    private PortScanPanel portScanPanel;
    private HomeAssistantPanel homeAssistantPanel;
    private FactoryMonitorPanel factoryMonitorPanel;
    private AddressConfigPanel addressConfigPanel;
    private ProtocolConfigPanel protocolConfigPanel;
    private PacketAnalyzerPanel packetAnalyzerPanel;

    // ========== 共享依赖 ==========
    private final PortScannerService scannerService = new PortScannerService();

    // ========== 网络模拟 ==========
    private MainFrame networkSimFrame;

    public AutomationDashboard() {
        initUI();
        initActions();
        startStatusTimer();
    }

    private void initUI() {
        // ---------- 窗口设置 ----------
        setTitle("🔧 自动化工具集成面板 v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));

        // ---------- 顶部标题栏 ----------
        add(createHeaderPanel(), BorderLayout.NORTH);

        // ---------- 中央标签页 ----------
        tabbedPane = createTabbedPane();
        add(tabbedPane, BorderLayout.CENTER);

        // ---------- 底部状态栏 ----------
        add(createStatusBar(), BorderLayout.SOUTH);

        // ---------- 设置图标 ----------
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/icon.png")));
        } catch (Exception ignored) {
            // 无图标时使用默认
        }

        // 显示窗口
        setVisible(true);
    }

    // ========== 顶部面板 ==========

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 33, 33));
        header.setPreferredSize(new Dimension(0, 70));

        // 标题
        JLabel titleLabel = new JLabel("🔧 自动化工具集成面板");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 10));
        header.add(titleLabel, BorderLayout.WEST);

        // 右侧快速操作
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 18));
        quickPanel.setOpaque(false);

        JButton btnNetSim = createToolbarButton("🌐", "网络模拟器", e -> launchNetworkSimulator());
        JButton btnAbout = createToolbarButton("ℹ️", "关于", e -> showAbout());
        JButton btnExit = createToolbarButton("❌", "退出", e -> exitApp());

        quickPanel.add(btnNetSim);
        quickPanel.add(btnAbout);
        quickPanel.add(btnExit);
        header.add(quickPanel, BorderLayout.EAST);

        return header;
    }

    private JButton createToolbarButton(String icon, String tooltip, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(icon);
        btn.setToolTipText(tooltip);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(66, 66, 66));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(97, 97, 97));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(66, 66, 66));
            }
        });
        return btn;
    }

    // ========== 标签页 ==========

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("微软雅黑", Font.BOLD, 13));
        tabs.setTabPlacement(JTabbedPane.TOP);
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // ----- Tab 1: 端口扫描 -----
        portScanPanel = new PortScanPanel(scannerService);
        tabs.addTab("🔍 端口扫描", createTabWrapper(portScanPanel, "端口扫描器 - 支持子网扫描、端口探测、服务指纹识别"));

        // ----- Tab 2: 网络模拟 -----
        JPanel netSimPlaceholder = createNetworkSimPanel();
        tabs.addTab("🌐 网络模拟", createTabWrapper(netSimPlaceholder, "全协议栈网络可视化模拟器"));

        // ----- Tab 3: Home Assistant -----
        homeAssistantPanel = new HomeAssistantPanel();
        homeAssistantPanel.loadDemoData();
        tabs.addTab("🏠 Home Assistant", createTabWrapper(homeAssistantPanel, "Home Assistant 智能家居集成 - 设备状态监控与控制"));

        // ----- Tab 4: 工厂监控 -----
        try {
            factoryMonitorPanel = createFactoryMonitorPanel();
            tabs.addTab("📊 工厂监控", createTabWrapper(factoryMonitorPanel, "工厂状态监控 - IP/MAC/端口使用率、缓存状态"));
        } catch (Exception e) {
            tabs.addTab("📊 工厂监控", createErrorPanel("工厂监控加载失败", e));
        }

        // ----- Tab 5: 地址配置 -----
        try {
            addressConfigPanel = createAddressConfigPanel();
            tabs.addTab("📍 地址配置", createTabWrapper(addressConfigPanel, "IP/MAC/端口地址配置"));
        } catch (Exception e) {
            tabs.addTab("📍 地址配置", createErrorPanel("地址配置加载失败", e));
        }

        // ----- Tab 6: 协议参数 -----
        protocolConfigPanel = new ProtocolConfigPanel();
        tabs.addTab("⚙️ 协议参数", createTabWrapper(protocolConfigPanel, "TCP/IP 协议参数配置 - MTU、TTL、RTO、拥塞控制等"));

        // ----- Tab 7: 报文分析 -----
        packetAnalyzerPanel = new PacketAnalyzerPanel();
        tabs.addTab("🔬 报文分析", createTabWrapper(packetAnalyzerPanel, "Wireshark 风格报文解析器 - 分层结构/十六进制/详细信息"));

        // 添加监听：切换 tab 时更新状态栏
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            String title = tabs.getTitleAt(idx);
            updateStatus("当前工具: " + title);
        });

        return tabs;
    }

    /** 为标签页创建带标题提示的包装面板 */
    private JPanel createTabWrapper(JComponent component, String hint) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(component, BorderLayout.CENTER);

        // 底部提示
        JLabel hintLabel = new JLabel("  💡 " + hint);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        wrapper.add(hintLabel, BorderLayout.SOUTH);

        return wrapper;
    }

    /** 错误回退面板 */
    private JPanel createErrorPanel(String message, Exception e) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel errorLabel = new JLabel("⚠️ " + message);
        errorLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(errorLabel, BorderLayout.CENTER);

        if (e != null) {
            JTextArea errorDetail = new JTextArea(e.getMessage());
            errorDetail.setEditable(false);
            errorDetail.setBackground(new Color(255, 235, 235));
            errorDetail.setFont(new Font("Consolas", Font.PLAIN, 11));
            JScrollPane scroll = new JScrollPane(errorDetail);
            scroll.setPreferredSize(new Dimension(0, 80));
            panel.add(scroll, BorderLayout.SOUTH);
        }
        return panel;
    }

    // ========== 网络模拟面板 ==========

    private JPanel createNetworkSimPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // 内容区
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 图标
        JLabel iconLabel = new JLabel("🌐");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        centerPanel.add(iconLabel, gbc);

        gbc.gridy = 1;
        JLabel titleLabel = new JLabel("全协议栈网络可视化模拟器");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        centerPanel.add(titleLabel, gbc);

        gbc.gridy = 2;
        JLabel descLabel = new JLabel("<html><center>" +
                "基于 Swing 的交互式网络协议栈可视化模拟器<br>" +
                "支持 TCP/UDP/IP/ARP/DNS/DHCP 等协议的<br>" +
                "数据包封装、传输、解封全过程动画演示" +
                "</center></html>");
        descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        descLabel.setForeground(Color.DARK_GRAY);
        centerPanel.add(descLabel, gbc);

        gbc.gridy = 3;
        JButton launchBtn = new JButton("🚀 启动网络模拟器");
        launchBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        launchBtn.setBackground(new Color(33, 150, 243));
        launchBtn.setForeground(Color.WHITE);
        launchBtn.setPreferredSize(new Dimension(250, 50));
        launchBtn.addActionListener(e -> launchNetworkSimulator());
        centerPanel.add(launchBtn, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        // 快捷键提示
        JLabel shortcutHint = new JLabel("  提示: 点击顶部工具栏的 🌐 按钮也可启动");
        shortcutHint.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        shortcutHint.setForeground(Color.GRAY);
        panel.add(shortcutHint, BorderLayout.SOUTH);

        return panel;
    }

    private void launchNetworkSimulator() {
        if (networkSimFrame != null && networkSimFrame.isVisible()) {
            networkSimFrame.toFront();
            updateStatus("网络模拟器已在运行");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                networkSimFrame = new MainFrame();
                networkSimFrame.setTitle("🌐 全协议栈网络可视化模拟器 (独立窗口)");
                updateStatus("✅ 网络模拟器已启动");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "启动网络模拟器失败:\n" + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
                updateStatus("❌ 网络模拟器启动失败");
            }
        });
    }

    // ========== 工厂依赖注入 ==========

    private FactoryMonitorPanel createFactoryMonitorPanel() {
        var ipFactory = new com.never_give_up.automation.demo.factory.address.IpAddressFactory();
        var macFactory = new com.never_give_up.automation.demo.factory.address.MacAddressFactory();
        var portFactory = new com.never_give_up.automation.demo.factory.address.PortFactory();
        var arpCache = new com.never_give_up.automation.demo.factory.function.ArpCacheFactory();
        var dnsCache = new com.never_give_up.automation.demo.factory.function.DnsCacheFactory();
        var natFactory = new com.never_give_up.automation.demo.factory.function.NatMappingFactory("10.0.0.1");
        var tcpFactory = new com.never_give_up.automation.demo.factory.transport.TcpPacketFactory();
        return new FactoryMonitorPanel(ipFactory, macFactory, portFactory,
                arpCache, dnsCache, natFactory, tcpFactory);
    }

    private AddressConfigPanel createAddressConfigPanel() {
        var ipFactory = new com.never_give_up.automation.demo.factory.address.IpAddressFactory();
        var macFactory = new com.never_give_up.automation.demo.factory.address.MacAddressFactory();
        var portFactory = new com.never_give_up.automation.demo.factory.address.PortFactory();
        return new AddressConfigPanel(ipFactory, macFactory, portFactory);
    }

    // ========== 状态栏 ==========

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        statusBar.setPreferredSize(new Dimension(0, 28));

        lblStatusBar = new JLabel(" 就绪");
        lblStatusBar.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        statusBar.add(lblStatusBar, BorderLayout.WEST);

        JLabel lblTime = new JLabel();
        lblTime.setFont(new Font("Consolas", Font.PLAIN, 12));
        lblTime.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        statusBar.add(lblTime, BorderLayout.EAST);

        // 更新时间
        statusTimer = new Timer(1000, e ->
                lblTime.setText(LocalDateTime.now().format(DTF)));
        statusTimer.start();

        return statusBar;
    }

    // ========== 操作 ==========

    private void initActions() {
        // 全局快捷键
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        // Ctrl+1-7 切换标签
        for (int i = 0; i < 7; i++) {
            int index = i;
            String key = "tab_" + i;
            inputMap.put(KeyStroke.getKeyStroke("ctrl " + (i + 1)), key);
            actionMap.put(key, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (index < tabbedPane.getTabCount()) {
                        tabbedPane.setSelectedIndex(index);
                    }
                }
            });
        }

        // Esc 关闭网络模拟
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "closeNetSim");
        actionMap.put("closeNetSim", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (networkSimFrame != null && networkSimFrame.isVisible()) {
                    networkSimFrame.dispose();
                    networkSimFrame = null;
                    updateStatus("网络模拟器已关闭");
                }
            }
        });
    }

    private void startStatusTimer() {
        Timer timer = new Timer(5000, e -> {
            // 定时检查网络模拟器状态
            if (networkSimFrame != null && !networkSimFrame.isVisible()) {
                networkSimFrame = null;
                updateStatus("网络模拟器已关闭");
            }
        });
        timer.start();
    }

    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() ->
                lblStatusBar.setText(" " + message));
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                """
                        🔧 自动化工具集成面板 v1.0
                        ════════════════════════════
                        集成工具:
                        🔍 端口扫描器       - 子网扫描、端口探测、服务识别
                        🌐 网络模拟器       - 全协议栈网络可视化模拟
                        🏠 Home Assistant   - 智能家居设备管理与控制
                        📊 工厂监控面板     - IP/MAC/端口使用率监控
                        📍 地址配置面板     - 网络地址资源配置
                        ⚙️ 协议参数面板     - TCP/IP 协议栈参数配置
                        🔬 报文分析面板     - Wireshark 风格报文解析

                        快捷键:
                        Ctrl+1~7  切换工具标签
                        Esc       关闭网络模拟器
                        ════════════════════════════
                        """,
                "关于", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitApp() {
        int result = JOptionPane.showConfirmDialog(this,
                "确定要退出自动化工具集成面板吗？",
                "确认退出", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (networkSimFrame != null) {
                networkSimFrame.dispose();
            }
            dispose();
            System.exit(0);
        }
    }

    // ========== 入口 ==========

    public static void main(String[] args) {
        try {
            // 设置系统外观
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // 设置全局字体
        Font font = new Font("微软雅黑", Font.PLAIN, 12);
        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 12));
        UIManager.put("TabbedPane.font", new Font("微软雅黑", Font.BOLD, 13));

        SwingUtilities.invokeLater(() -> new AutomationDashboard());
    }

    // ---------- static main entry for non-Spring launch ----------
    public static void startUI() {
        main(new String[0]);
    }
}
