package com.never_give_up.automation.demo.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProtocolConfigPanel extends JPanel {
    private JTextField txtMtu;
    private JTextField txtTtl;
    private JTextField txtRto;
    private JTextField txtBufferSize;
    private JTextField txtPacketLossRate;
    private JTextField txtBandwidth;
    private JTextField txtLatency;
    private JTextField txtCwnd;
    private JTextField txtSsthresh;
    private JCheckBox chkEnableNat;
    private JCheckBox chkEnableFragmentation;
    private JTable tableProtocols;
    private DefaultTableModel protocolTableModel;

    public ProtocolConfigPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("⚙️ 协议参数配置"));

        createNetworkParameters();
        createProtocolTable();
        createControlPanel();
    }

    private void createNetworkParameters() {
        JPanel paramPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        paramPanel.setBorder(BorderFactory.createTitledBorder("网络参数"));

        txtMtu = new JTextField("1500");
        txtTtl = new JTextField("64");
        txtRto = new JTextField("5000");
        txtBufferSize = new JTextField("5");
        txtPacketLossRate = new JTextField("0.0");
        txtBandwidth = new JTextField("1000");
        txtLatency = new JTextField("10");
        txtCwnd = new JTextField("1");
        txtSsthresh = new JTextField("12");
        chkEnableNat = new JCheckBox("启用 NAT");
        chkEnableNat.setSelected(true);
        chkEnableFragmentation = new JCheckBox("启用 IP 分片");
        chkEnableFragmentation.setSelected(true);

        paramPanel.add(new JLabel("MTU (字节):"));
        paramPanel.add(txtMtu);
        paramPanel.add(new JLabel("默认 TTL:"));
        paramPanel.add(txtTtl);
        paramPanel.add(new JLabel("RTO 超时 (ms):"));
        paramPanel.add(txtRto);
        paramPanel.add(new JLabel("缓冲区大小:"));
        paramPanel.add(txtBufferSize);
        paramPanel.add(new JLabel("丢包率 (0-1):"));
        paramPanel.add(txtPacketLossRate);
        paramPanel.add(new JLabel("带宽 (Kbps):"));
        paramPanel.add(txtBandwidth);
        paramPanel.add(new JLabel("延迟 (ms):"));
        paramPanel.add(txtLatency);
        paramPanel.add(new JLabel("初始 cwnd:"));
        paramPanel.add(txtCwnd);
        paramPanel.add(new JLabel("ssthresh:"));
        paramPanel.add(txtSsthresh);
        paramPanel.add(chkEnableNat);
        paramPanel.add(chkEnableFragmentation);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnSave = new JButton("💾 保存参数");
        JButton btnReset = new JButton("🔄 恢复默认");
        JButton btnApply = new JButton("✅ 应用");

        btnSave.addActionListener(e -> saveParameters());
        btnReset.addActionListener(e -> resetToDefaults());
        btnApply.addActionListener(e -> applyParameters());

        btnPanel.add(btnSave);
        btnPanel.add(btnApply);
        btnPanel.add(btnReset);

        add(paramPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void createProtocolTable() {
        protocolTableModel = new DefaultTableModel(
                new String[]{"协议", "端口", "状态", "描述"}, 0);
        tableProtocols = new JTable(protocolTableModel);
        tableProtocols.getColumnModel().getColumn(3).setPreferredWidth(200);

        loadProtocolDefaults();

        JScrollPane scrollPane = new JScrollPane(tableProtocols);
        scrollPane.setBorder(BorderFactory.createTitledBorder("协议配置"));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadProtocolDefaults() {
        protocolTableModel.addRow(new Object[]{"HTTP", "80", "启用", "超文本传输协议"});
        protocolTableModel.addRow(new Object[]{"HTTPS", "443", "启用", "安全超文本传输协议"});
        protocolTableModel.addRow(new Object[]{"DNS", "53", "启用", "域名解析系统"});
        protocolTableModel.addRow(new Object[]{"DHCP", "67/68", "启用", "动态主机配置协议"});
        protocolTableModel.addRow(new Object[]{"TCP", "动态", "启用", "传输控制协议"});
        protocolTableModel.addRow(new Object[]{"UDP", "动态", "启用", "用户数据报协议"});
        protocolTableModel.addRow(new Object[]{"ICMP", "N/A", "启用", "Internet 控制消息协议"});
        protocolTableModel.addRow(new Object[]{"ARP", "N/A", "启用", "地址解析协议"});
    }

    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("高级控制"));

        JButton btnClearCache = new JButton("🗑️ 清空缓存");
        JButton btnResetFactory = new JButton("🔄 重置工厂");
        JButton btnExportConfig = new JButton("📤 导出配置");
        JButton btnImportConfig = new JButton("📥 导入配置");

        btnClearCache.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "缓存已清空", "成功", JOptionPane.INFORMATION_MESSAGE);
        });

        btnResetFactory.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "确定要重置所有工厂吗？这将清除所有状态。",
                    "确认重置",
                    JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "工厂已重置", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnExportConfig.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, "配置已导出到: " + chooser.getSelectedFile().getAbsolutePath(),
                        "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnImportConfig.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, "配置已导入: " + chooser.getSelectedFile().getAbsolutePath(),
                        "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        controlPanel.add(btnClearCache);
        controlPanel.add(btnResetFactory);
        controlPanel.add(btnExportConfig);
        controlPanel.add(btnImportConfig);

        add(controlPanel, BorderLayout.EAST);
    }

    private void saveParameters() {
        try {
            int mtu = Integer.parseInt(txtMtu.getText().trim());
            int ttl = Integer.parseInt(txtTtl.getText().trim());
            long rto = Long.parseLong(txtRto.getText().trim());
            int bufferSize = Integer.parseInt(txtBufferSize.getText().trim());
            double lossRate = Double.parseDouble(txtPacketLossRate.getText().trim());
            int bandwidth = Integer.parseInt(txtBandwidth.getText().trim());
            int latency = Integer.parseInt(txtLatency.getText().trim());

            if (mtu < 576 || mtu > 9000) {
                throw new IllegalArgumentException("MTU 必须在 576-9000 范围内");
            }
            if (ttl < 1 || ttl > 255) {
                throw new IllegalArgumentException("TTL 必须在 1-255 范围内");
            }
            if (lossRate < 0 || lossRate > 1) {
                throw new IllegalArgumentException("丢包率必须在 0-1 范围内");
            }

            JOptionPane.showMessageDialog(this, "参数已保存", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "参数格式错误", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetToDefaults() {
        txtMtu.setText("1500");
        txtTtl.setText("64");
        txtRto.setText("5000");
        txtBufferSize.setText("5");
        txtPacketLossRate.setText("0.0");
        txtBandwidth.setText("1000");
        txtLatency.setText("10");
        txtCwnd.setText("1");
        txtSsthresh.setText("12");

        JOptionPane.showMessageDialog(this, "已恢复默认参数", "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void applyParameters() {
        saveParameters();
    }

    public int getMtu() {
        return Integer.parseInt(txtMtu.getText().trim());
    }

    public int getTtl() {
        return Integer.parseInt(txtTtl.getText().trim());
    }

    public long getRto() {
        return Long.parseLong(txtRto.getText().trim());
    }

    public int getBufferSize() {
        return Integer.parseInt(txtBufferSize.getText().trim());
    }

    public double getPacketLossRate() {
        return Double.parseDouble(txtPacketLossRate.getText().trim());
    }
}
