package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.demo.factory.address.IpAddressFactory;
import com.never_give_up.automation.demo.factory.address.MacAddressFactory;
import com.never_give_up.automation.demo.factory.address.PortFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AddressConfigPanel extends JPanel {
    private final IpAddressFactory ipFactory;
    private final MacAddressFactory macFactory;
    private final PortFactory portFactory;

    private JTextField txtDeviceName;
    private JTextField txtCustomIp;
    private JTextField txtCustomMac;
    private JTextField txtSubnetMask;
    private JTextField txtPrivateNetwork;
    private JTextField txtPublicNetwork;
    private JTable tableAllocatedIps;
    private JTable tableUsedPorts;
    private DefaultTableModel ipTableModel;
    private DefaultTableModel portTableModel;

    public AddressConfigPanel(IpAddressFactory ipFactory, MacAddressFactory macFactory, PortFactory portFactory) {
        this.ipFactory = ipFactory;
        this.macFactory = macFactory;
        this.portFactory = portFactory;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("📍 地址配置面板"));

        createIpConfigPanel();
        createMacConfigPanel();
        createPortConfigPanel();
        createMonitoringTables();
    }

    private void createIpConfigPanel() {
        JPanel ipPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        ipPanel.setBorder(BorderFactory.createTitledBorder("IP 地址配置"));

        txtDeviceName = new JTextField();
        txtCustomIp = new JTextField();
        txtSubnetMask = new JTextField("24");
        txtPrivateNetwork = new JTextField("192.168.1");
        txtPublicNetwork = new JTextField("10.0.0");

        ipPanel.add(new JLabel("设备名称:"));
        ipPanel.add(txtDeviceName);
        ipPanel.add(new JLabel("自定义 IP:"));
        ipPanel.add(txtCustomIp);
        ipPanel.add(new JLabel("子网掩码 (/0-32):"));
        ipPanel.add(txtSubnetMask);
        ipPanel.add(new JLabel("私有网络:"));
        ipPanel.add(txtPrivateNetwork);
        ipPanel.add(new JLabel("公网网络:"));
        ipPanel.add(txtPublicNetwork);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAllocatePrivate = new JButton("分配私有 IP");
        JButton btnAllocatePublic = new JButton("分配公网 IP");
        JButton btnSetCustom = new JButton("设置自定义 IP");
        JButton btnRelease = new JButton("释放 IP");

        btnAllocatePrivate.addActionListener(e -> allocatePrivateIp());
        btnAllocatePublic.addActionListener(e -> allocatePublicIp());
        btnSetCustom.addActionListener(e -> setCustomIp());
        btnRelease.addActionListener(e -> releaseIp());

        btnPanel.add(btnAllocatePrivate);
        btnPanel.add(btnAllocatePublic);
        btnPanel.add(btnSetCustom);
        btnPanel.add(btnRelease);

        add(ipPanel, BorderLayout.NORTH);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void createMacConfigPanel() {
        JPanel macPanel = new JPanel(new FlowLayout());
        macPanel.setBorder(BorderFactory.createTitledBorder("MAC 地址配置"));

        JTextField txtMacPrefix = new JTextField("00:1A:2B", 10);
        JButton btnGenerateMac = new JButton("生成 MAC 地址");
        JLabel lblGeneratedMac = new JLabel("已生成: --", JLabel.CENTER);

        btnGenerateMac.addActionListener(e -> {
            String mac;
            if (txtMacPrefix.getText().trim().isEmpty()) {
                mac = macFactory.generateMac();
            } else {
                mac = macFactory.generateMac(txtMacPrefix.getText().trim());
            }
            lblGeneratedMac.setText("已生成: " + mac);
        });

        macPanel.add(new JLabel("MAC 前缀:"));
        macPanel.add(txtMacPrefix);
        macPanel.add(btnGenerateMac);
        macPanel.add(lblGeneratedMac);

        add(macPanel, BorderLayout.CENTER);
    }

    private void createPortConfigPanel() {
        JPanel portPanel = new JPanel(new FlowLayout());
        portPanel.setBorder(BorderFactory.createTitledBorder("端口配置"));

        JTextField txtPortNumber = new JTextField("", 8);
        JButton btnReservePort = new JButton("保留端口");
        JButton btnReleasePort = new JButton("释放端口");
        JLabel lblPortStatus = new JLabel("状态: --", JLabel.CENTER);

        btnReservePort.addActionListener(e -> {
            try {
                int port = Integer.parseInt(txtPortNumber.getText().trim());
                portFactory.reservePort(port);
                lblPortStatus.setText("已保留端口: " + port);
                updatePortTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的端口号", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReleasePort.addActionListener(e -> {
            try {
                int port = Integer.parseInt(txtPortNumber.getText().trim());
                portFactory.releasePort(port);
                lblPortStatus.setText("已释放端口: " + port);
                updatePortTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "请输入有效的端口号", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        portPanel.add(new JLabel("端口号:"));
        portPanel.add(txtPortNumber);
        portPanel.add(btnReservePort);
        portPanel.add(btnReleasePort);
        portPanel.add(lblPortStatus);

        add(portPanel, BorderLayout.SOUTH);
    }

    private void createMonitoringTables() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        ipTableModel = new DefaultTableModel(new String[]{"设备名称", "IP 地址"}, 0);
        tableAllocatedIps = new JTable(ipTableModel);
        JScrollPane ipScroll = new JScrollPane(tableAllocatedIps);
        ipScroll.setBorder(BorderFactory.createTitledBorder("已分配 IP"));

        portTableModel = new DefaultTableModel(new String[]{"端口号", "类型", "状态"}, 0);
        tableUsedPorts = new JTable(portTableModel);
        JScrollPane portScroll = new JScrollPane(tableUsedPorts);
        portScroll.setBorder(BorderFactory.createTitledBorder("已使用端口"));

        splitPane.setTopComponent(ipScroll);
        splitPane.setBottomComponent(portScroll);

        add(splitPane, BorderLayout.EAST);
    }

    private void allocatePrivateIp() {
        String deviceName = txtDeviceName.getText().trim();
        if (deviceName.isEmpty()) {
            deviceName = "Device_" + System.currentTimeMillis();
        }

        try {
            String ip = ipFactory.allocatePrivateIp(deviceName);
            JOptionPane.showMessageDialog(this, "已分配私有 IP: " + ip, "成功", JOptionPane.INFORMATION_MESSAGE);
            updateIpTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void allocatePublicIp() {
        String deviceName = txtDeviceName.getText().trim();
        if (deviceName.isEmpty()) {
            deviceName = "Device_" + System.currentTimeMillis();
        }

        try {
            String ip = ipFactory.allocatePublicIp(deviceName);
            JOptionPane.showMessageDialog(this, "已分配公网 IP: " + ip, "成功", JOptionPane.INFORMATION_MESSAGE);
            updateIpTable();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setCustomIp() {
        String deviceName = txtDeviceName.getText().trim();
        String customIp = txtCustomIp.getText().trim();

        if (deviceName.isEmpty() || customIp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入设备名称和 IP 地址", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ipFactory.setSubnetMask(Integer.parseInt(txtSubnetMask.getText().trim()));
            ipFactory.setPrivateNetwork(txtPrivateNetwork.getText().trim());
            ipFactory.setPublicNetwork(txtPublicNetwork.getText().trim());
            ipFactory.setCustomIp(deviceName, customIp);
            JOptionPane.showMessageDialog(this, "已设置自定义 IP: " + customIp, "成功", JOptionPane.INFORMATION_MESSAGE);
            updateIpTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "子网掩码格式错误", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void releaseIp() {
        String ip = txtCustomIp.getText().trim();
        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要释放的 IP 地址", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ipFactory.releaseIp(ip);
        JOptionPane.showMessageDialog(this, "已释放 IP: " + ip, "成功", JOptionPane.INFORMATION_MESSAGE);
        updateIpTable();
    }

    private void updateIpTable() {
        ipTableModel.setRowCount(0);
        ipFactory.getDeviceIpMap().forEach((device, ip) -> {
            ipTableModel.addRow(new Object[]{device, ip});
        });
    }

    private void updatePortTable() {
        portTableModel.setRowCount(0);
    }

    public void reset() {
        txtDeviceName.setText("");
        txtCustomIp.setText("");
        ipTableModel.setRowCount(0);
        portTableModel.setRowCount(0);
    }
}
