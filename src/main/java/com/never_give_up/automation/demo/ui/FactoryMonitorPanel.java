package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.demo.factory.address.IpAddressFactory;
import com.never_give_up.automation.demo.factory.address.MacAddressFactory;
import com.never_give_up.automation.demo.factory.address.PortFactory;
import com.never_give_up.automation.demo.factory.function.ArpCacheFactory;
import com.never_give_up.automation.demo.factory.function.DnsCacheFactory;
import com.never_give_up.automation.demo.factory.function.NatMappingFactory;
import com.never_give_up.automation.demo.factory.transport.TcpPacketFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class FactoryMonitorPanel extends JPanel {
    private final IpAddressFactory ipFactory;
    private final MacAddressFactory macFactory;
    private final PortFactory portFactory;
    private final ArpCacheFactory arpCache;
    private final DnsCacheFactory dnsCache;
    private final NatMappingFactory natFactory;
    private final TcpPacketFactory tcpFactory;

    private JProgressBar prgIpUsage;
    private JProgressBar prgMacUsage;
    private JProgressBar prgPortUsage;
    private JLabel lblIpAllocated;
    private JLabel lblMacAllocated;
    private JLabel lblPortAllocated;
    private JLabel lblArpEntries;
    private JLabel lblDnsEntries;
    private JLabel lblNatMappings;
    private JLabel lblTcpState;
    private JTable tableFactoryStatus;
    private DefaultTableModel statusTableModel;

    public FactoryMonitorPanel(IpAddressFactory ipFactory, MacAddressFactory macFactory,
                               PortFactory portFactory, ArpCacheFactory arpCache,
                               DnsCacheFactory dnsCache, NatMappingFactory natFactory,
                               TcpPacketFactory tcpFactory) {
        this.ipFactory = ipFactory;
        this.macFactory = macFactory;
        this.portFactory = portFactory;
        this.arpCache = arpCache;
        this.dnsCache = dnsCache;
        this.natFactory = natFactory;
        this.tcpFactory = tcpFactory;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("📊 工厂状态监控"));

        createUsageBars();
        createStatusTable();
        createDetailPanel();

        startAutoRefresh();
    }

    private void createUsageBars() {
        JPanel barPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        barPanel.setBorder(BorderFactory.createTitledBorder("资源使用率"));

        prgIpUsage = new JProgressBar(0, 254);
        prgIpUsage.setStringPainted(true);
        lblIpAllocated = new JLabel("已分配: 0 / 254");

        prgMacUsage = new JProgressBar(0, 1000);
        prgMacUsage.setStringPainted(true);
        lblMacAllocated = new JLabel("已分配: 0 / 1000");

        prgPortUsage = new JProgressBar(0, 65535);
        prgPortUsage.setStringPainted(true);
        lblPortAllocated = new JLabel("已使用: 0 / 65535");

        JPanel ipPanel = new JPanel(new BorderLayout());
        ipPanel.add(new JLabel("IP 地址池"), BorderLayout.NORTH);
        ipPanel.add(prgIpUsage, BorderLayout.CENTER);
        ipPanel.add(lblIpAllocated, BorderLayout.SOUTH);

        JPanel macPanel = new JPanel(new BorderLayout());
        macPanel.add(new JLabel("MAC 地址池"), BorderLayout.NORTH);
        macPanel.add(prgMacUsage, BorderLayout.CENTER);
        macPanel.add(lblMacAllocated, BorderLayout.SOUTH);

        JPanel portPanel = new JPanel(new BorderLayout());
        portPanel.add(new JLabel("端口池"), BorderLayout.NORTH);
        portPanel.add(prgPortUsage, BorderLayout.CENTER);
        portPanel.add(lblPortAllocated, BorderLayout.SOUTH);

        barPanel.add(ipPanel);
        barPanel.add(macPanel);
        barPanel.add(portPanel);

        add(barPanel, BorderLayout.NORTH);
    }

    private void createStatusTable() {
        statusTableModel = new DefaultTableModel(
                new String[]{"工厂名称", "生产数量", "当前状态", "详细信息"}, 0);
        tableFactoryStatus = new JTable(statusTableModel);
        tableFactoryStatus.getColumnModel().getColumn(3).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(tableFactoryStatus);
        scrollPane.setBorder(BorderFactory.createTitledBorder("工厂运行状态"));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void createDetailPanel() {
        JPanel detailPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        detailPanel.setBorder(BorderFactory.createTitledBorder("缓存 & 映射状态"));

        lblArpEntries = new JLabel("ARP 缓存条目: 0");
        lblDnsEntries = new JLabel("DNS 缓存条目: 0");
        lblNatMappings = new JLabel("NAT 映射条目: 0");
        lblTcpState = new JLabel("TCP 状态: cwnd=1, ssthresh=12");

        JButton btnClearArp = new JButton("清空 ARP 缓存");
        JButton btnClearDns = new JButton("清空 DNS 缓存");

        btnClearArp.addActionListener(e -> {
            arpCache.clear();
            updateDisplay();
        });

        btnClearDns.addActionListener(e -> {
            dnsCache.clear();
            updateDisplay();
        });

        detailPanel.add(lblArpEntries);
        detailPanel.add(btnClearArp);
        detailPanel.add(lblDnsEntries);
        detailPanel.add(btnClearDns);
        detailPanel.add(lblNatMappings);
        detailPanel.add(lblTcpState);

        add(detailPanel, BorderLayout.SOUTH);
    }

    private void startAutoRefresh() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(FactoryMonitorPanel.this::updateDisplay);
            }
        }, 0, 1000);
    }

    public void updateDisplay() {
        int ipCount = ipFactory.getUsedIps().size();
        prgIpUsage.setValue(ipCount);
        lblIpAllocated.setText("已分配: " + ipCount + " / 254");

        int macCount = macFactory.getAllocatedMacs().size();
        prgMacUsage.setValue(Math.min(macCount, 1000));
        lblMacAllocated.setText("已分配: " + macCount + " / 1000");

        int portCount = 0;
        prgPortUsage.setValue(portCount);
        lblPortAllocated.setText("已使用: " + portCount + " / 65535");

        lblArpEntries.setText("ARP 缓存条目: " + arpCache.size());
        lblDnsEntries.setText("DNS 缓存条目: " + dnsCache.size());
        lblNatMappings.setText("NAT 映射条目: " + natFactory.size());
        lblTcpState.setText(String.format("TCP 状态: cwnd=%d, ssthresh=%d",
                tcpFactory.getCwnd(), tcpFactory.getSsthresh()));

        statusTableModel.setRowCount(0);
        statusTableModel.addRow(new Object[]{"IP 地址工厂", ipCount, "运行中",
                "私有网络: " + ipFactory.getPrivateNetwork()});
        statusTableModel.addRow(new Object[]{"MAC 地址工厂", macCount, "运行中",
                "前缀: " + MacAddressFactory.class.getDeclaredFields()[0]});
        statusTableModel.addRow(new Object[]{"端口工厂", portCount, "运行中",
                "临时端口范围: 49152-65535"});
        statusTableModel.addRow(new Object[]{"TCP 工厂", 0, "运行中",
                tcpFactory.getCwnd() + ", ssthresh=" + tcpFactory.getSsthresh()});
        statusTableModel.addRow(new Object[]{"ARP 缓存", arpCache.size(), "活跃",
                "超时: 300s"});
        statusTableModel.addRow(new Object[]{"DNS 缓存", dnsCache.size(), "活跃",
                "自动刷新"});
        statusTableModel.addRow(new Object[]{"NAT 映射", natFactory.size(), "活跃",
                "端口范围: 50001-65535"});
    }

    public void reset() {
        prgIpUsage.setValue(0);
        prgMacUsage.setValue(0);
        prgPortUsage.setValue(0);
        statusTableModel.setRowCount(0);
    }
}
