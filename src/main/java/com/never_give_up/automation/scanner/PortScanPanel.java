package com.never_give_up.automation.scanner;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * 端口扫描 GUI 面板
 * <p>
 * 提供图形界面配置子网、端口、并发数，实时显示扫描进度与结果
 * </p>
 */
public class PortScanPanel extends JPanel {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ========== 输入控件 ==========
    private JTextField txtSubnet;
    private JTextField txtPorts;
    private JTextField txtThreads;
    private JTextField txtTimeout;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnClear;

    // ========== 结果展示 ==========
    private JTable resultTable;
    private DefaultTableModel resultTableModel;
    private JLabel lblStatus;
    private JProgressBar progressBar;
    private JTextArea txtDetail;

    // ========== 数据 ==========
    private final PortScannerService scannerService;
    private ScanWorker currentWorker;

    public PortScanPanel() {
        this.scannerService = new PortScannerService();
        initUI();
    }

    public PortScanPanel(PortScannerService scannerService) {
        this.scannerService = scannerService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createInputPanel(), BorderLayout.NORTH);
        add(createResultPanel(), BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "🔍 扫描配置", TitledBorder.LEFT, TitledBorder.TOP));

        // 参数行
        JPanel paramsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        // 子网
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        paramsPanel.add(new JLabel("目标子网:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        txtSubnet = new JTextField("192.168.1.0/24");
        paramsPanel.add(txtSubnet, gbc);

        // 端口
        gbc.gridx = 2;
        gbc.weightx = 0;
        paramsPanel.add(new JLabel("端口(逗号分隔):"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.3;
        txtPorts = new JTextField();
        txtPorts.setToolTipText("留空则扫描前 1000 个常见端口");
        paramsPanel.add(txtPorts, gbc);

        // 并发数
        gbc.gridx = 4;
        gbc.weightx = 0;
        paramsPanel.add(new JLabel("并发线程:"), gbc);
        gbc.gridx = 5;
        gbc.weightx = 0.1;
        txtThreads = new JTextField("100");
        paramsPanel.add(txtThreads, gbc);

        // 超时 (第二行)
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        paramsPanel.add(new JLabel("超时(ms):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.1;
        txtTimeout = new JTextField("2000");
        paramsPanel.add(txtTimeout, gbc);

        // 快捷端口按钮
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.weightx = 0.5;
        JPanel quickBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        JButton btnCommon = new JButton("常见端口");
        btnCommon.setToolTipText("22,80,443,3306,6379,5432,8080");
        btnCommon.addActionListener(e -> txtPorts.setText("22,80,443,3306,6379,5432,8080,27017,3389,8443"));
        JButton btnWeb = new JButton("Web 端口");
        btnWeb.setToolTipText("80,443,8080,8443,9090,3000,5000");
        btnWeb.addActionListener(e -> txtPorts.setText("80,443,8080,8443,9090,3000,5000,8000,8888"));
        JButton btnDatabase = new JButton("数据库");
        btnDatabase.setToolTipText("3306,5432,1521,1433,27017,6379,9042");
        btnDatabase.addActionListener(e -> txtPorts.setText("3306,5432,1521,1433,27017,6379,9042,6380,9200"));
        JButton btnFull = new JButton("全端口扫描");
        btnFull.setToolTipText("扫描 1-65535 全端口（较慢）");
        btnFull.addActionListener(e -> txtPorts.setText("1-65535"));

        quickBtnPanel.add(new JLabel("快捷: "));
        quickBtnPanel.add(btnCommon);
        quickBtnPanel.add(btnWeb);
        quickBtnPanel.add(btnDatabase);
        quickBtnPanel.add(btnFull);
        paramsPanel.add(quickBtnPanel, gbc);

        inputPanel.add(paramsPanel, BorderLayout.CENTER);

        // 操作按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnStart = new JButton("▶ 开始扫描");
        btnStart.setFont(new Font("微软雅黑", Font.BOLD, 14));
        btnStart.setBackground(new Color(76, 175, 80));
        btnStart.setForeground(Color.WHITE);
        btnStart.addActionListener(e -> startScan());

        btnStop = new JButton("■ 停止");
        btnStop.setFont(new Font("微软雅黑", Font.BOLD, 14));
        btnStop.setBackground(new Color(244, 67, 54));
        btnStop.setForeground(Color.WHITE);
        btnStop.setEnabled(false);
        btnStop.addActionListener(e -> stopScan());

        btnClear = new JButton("🗑 清空结果");
        btnClear.addActionListener(e -> clearResults());

        btnPanel.add(btnClear);
        btnPanel.add(btnStart);
        btnPanel.add(btnStop);

        inputPanel.add(btnPanel, BorderLayout.SOUTH);

        return inputPanel;
    }

    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout(5, 5));

        // 结果表格
        String[] columns = {"IP 地址", "端口", "服务", "产品/版本", "状态", "响应时间"};
        resultTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        resultTable = new JTable(resultTableModel);
        resultTable.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultTable.setRowHeight(24);
        resultTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        resultTable.getTableHeader().setBackground(new Color(33, 150, 243));
        resultTable.getTableHeader().setForeground(Color.WHITE);
        resultTable.setAutoCreateRowSorter(true);

        // 设置列宽
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(5).setPreferredWidth(80);

        // 行高亮：不同端口状态不同颜色
        resultTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String state = table.getValueAt(row, 4) != null ? table.getValueAt(row, 4).toString() : "";
                    switch (state) {
                        case "open" -> setBackground(new Color(200, 255, 200));
                        case "filtered" -> setBackground(new Color(255, 255, 200));
                        case "closed" -> setBackground(new Color(255, 220, 220));
                        default -> setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        // 双击查看详情
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showDetailDialog();
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("扫描结果"));

        // 右侧详情面板
        txtDetail = new JTextArea();
        txtDetail.setEditable(false);
        txtDetail.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtDetail.setBackground(new Color(30, 30, 30));
        txtDetail.setForeground(new Color(50, 255, 120));
        txtDetail.setText("双击结果行查看详情...");

        JScrollPane detailScroll = new JScrollPane(txtDetail);
        detailScroll.setPreferredSize(new Dimension(350, 0));
        detailScroll.setBorder(BorderFactory.createTitledBorder("详细信息"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailScroll);
        split.setResizeWeight(0.7);

        resultPanel.add(split, BorderLayout.CENTER);
        return resultPanel;
    }

    private JPanel createStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout(10, 5));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());

        lblStatus = new JLabel("就绪 - 输入子网后点击开始扫描");
        lblStatus.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 22));

        statusPanel.add(lblStatus, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.EAST);

        return statusPanel;
    }

    // ========== 扫描控制 ==========

    private void startScan() {
        // 解析输入
        String subnet = txtSubnet.getText().trim();
        if (subnet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入目标子网", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> ports = null;
        String portText = txtPorts.getText().trim();
        if (!portText.isEmpty()) {
            ports = new ArrayList<>();
            // 支持 1-65535 范围语法
            for (String part : portText.split(",")) {
                part = part.trim();
                if (part.contains("-")) {
                    String[] range = part.split("-");
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    for (int i = start; i <= end; i++) {
                        ports.add(i);
                    }
                } else if (!part.isEmpty()) {
                    ports.add(Integer.parseInt(part));
                }
            }
        }

        int threads;
        try {
            threads = Integer.parseInt(txtThreads.getText().trim());
            if (threads < 1) threads = 50;
        } catch (NumberFormatException e) {
            threads = 50;
        }

        int timeout;
        try {
            timeout = Integer.parseInt(txtTimeout.getText().trim());
            if (timeout < 100) timeout = 2000;
        } catch (NumberFormatException e) {
            timeout = 2000;
        }

        // 清空旧结果
        resultTableModel.setRowCount(0);
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        lblStatus.setText("扫描中... 子网: " + subnet + (ports != null ? ", 端口数: " + ports.size() : ", 1000 个常见端口"));
        progressBar.setIndeterminate(true);

        currentWorker = new ScanWorker(subnet, ports, timeout, threads);
        currentWorker.execute();
    }

    private void stopScan() {
        if (currentWorker != null && !currentWorker.isDone()) {
            currentWorker.cancel(true);
            currentWorker = null;
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            progressBar.setIndeterminate(false);
            progressBar.setValue(0);
            lblStatus.setText("扫描已停止");
        }
    }

    private void clearResults() {
        resultTableModel.setRowCount(0);
        txtDetail.setText("双击结果行查看详情...");
        lblStatus.setText("就绪");
    }

    private void showDetailDialog() {
        int row = resultTable.getSelectedRow();
        if (row < 0) return;

        String ip = resultTable.getValueAt(row, 0).toString();
        String port = resultTable.getValueAt(row, 1).toString();
        String service = resultTable.getValueAt(row, 2).toString();
        String product = resultTable.getValueAt(row, 3) != null ? resultTable.getValueAt(row, 3).toString() : "-";
        String state = resultTable.getValueAt(row, 4).toString();
        String time = resultTable.getValueAt(row, 5).toString();

        txtDetail.setText(String.format("""
                ╔══════════════════════════════╗
                ║       端口详细信息            ║
                ╚══════════════════════════════╝

                目标 IP:   %s
                端口号:    %s
                协议:      TCP
                服务:      %s
                产品:      %s
                状态:      %s
                响应时间:  %s

                —— 该主机所有开放端口 ——
                """, ip, port, service, product, state, time));

        // 显示同 IP 下所有端口
        for (int i = 0; i < resultTableModel.getRowCount(); i++) {
            if (resultTableModel.getValueAt(i, 0).toString().equals(ip)) {
                txtDetail.append(String.format("  %s → %s (%s)%n",
                        resultTableModel.getValueAt(i, 1),
                        resultTableModel.getValueAt(i, 2),
                        resultTableModel.getValueAt(i, 4)));
            }
        }
    }

    // ========== 后台扫描任务 ==========

    private class ScanWorker extends SwingWorker<Void, PortScanResult> {

        private final String subnet;
        private final List<Integer> ports;
        private final int timeout;
        private final int threads;
        private long startTime;

        ScanWorker(String subnet, List<Integer> ports, int timeout, int threads) {
            this.subnet = subnet;
            this.ports = ports;
            this.timeout = timeout;
            this.threads = threads;
        }

        @Override
        protected Void doInBackground() {
            startTime = System.currentTimeMillis();
            List<PortScanResult> results = scannerService.scanSubnet(subnet, ports, timeout, threads);
            for (PortScanResult host : results) {
                if (isCancelled()) break;
                publish(host);
            }
            return null;
        }

        @Override
        protected void process(List<PortScanResult> chunks) {
            for (PortScanResult host : chunks) {
                if (host.isAlive()) {
                    for (PortScanResult.PortInfo port : host.getOpenPorts()) {
                        resultTableModel.addRow(new Object[]{
                                host.getIp(),
                                port.getPort(),
                                port.getServiceName(),
                                port.getServiceProduct() != null ? port.getServiceProduct() : port.getBanner(),
                                port.getState(),
                                port.getConnectTimeMs() + "ms"
                        });
                    }
                }

                String status = String.format("已发现 %d 台存活主机, %d 个开放端口...",
                        countAliveHosts(), resultTableModel.getRowCount());
                lblStatus.setText(status);
            }
        }

        @Override
        protected void done() {
            progressBar.setIndeterminate(false);
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);

            try {
                get(); // 检查异常
                long elapsed = System.currentTimeMillis() - startTime;
                lblStatus.setText(String.format("✅ 扫描完成! 存活: %d 台, 开放端口: %d 个, 耗时: %d 秒",
                        countAliveHosts(), resultTableModel.getRowCount(), elapsed / 1000));
            } catch (CancellationException e) {
                lblStatus.setText("扫描已取消");
            } catch (InterruptedException | ExecutionException e) {
                lblStatus.setText("扫描出错: " + e.getMessage());
                e.printStackTrace();
            }
            currentWorker = null;
        }

        private long countAliveHosts() {
            return resultTableModel.getDataVector().stream()
                    .map(v -> v.get(0))
                    .distinct()
                    .count();
        }
    }

    // ========== 独立运行测试 ==========

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("端口扫描器");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.add(new PortScanPanel());
        frame.setVisible(true);
    }
}
