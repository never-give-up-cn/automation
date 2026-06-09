package com.never_give_up.automation.demo.test;

import com.never_give_up.automation.demo.factory.address.*;
import com.never_give_up.automation.demo.factory.function.*;
import com.never_give_up.automation.demo.factory.transport.TcpPacketFactory;
import com.never_give_up.automation.demo.ui.AddressConfigPanel;
import com.never_give_up.automation.demo.ui.FactoryMonitorPanel;
import com.never_give_up.automation.demo.ui.PacketAnalyzerPanel;
import com.never_give_up.automation.demo.ui.ProtocolConfigPanel;

import javax.swing.*;
import java.awt.*;

public class UiDemo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IpAddressFactory ipFactory = new IpAddressFactory();
            MacAddressFactory macFactory = new MacAddressFactory();
            PortFactory portFactory = new PortFactory();
            ArpCacheFactory arpCache = new ArpCacheFactory();
            DnsCacheFactory dnsCache = new DnsCacheFactory();
            NatMappingFactory natFactory = new NatMappingFactory("10.0.0.1");
            TcpPacketFactory tcpFactory = new TcpPacketFactory();

            JFrame frame = new JFrame("网络配置面板");
            frame.setSize(1200, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(2, 2, 10, 10));

            AddressConfigPanel addressPanel = new AddressConfigPanel(
                    ipFactory, macFactory, portFactory);
            ProtocolConfigPanel protocolPanel = new ProtocolConfigPanel();
            FactoryMonitorPanel monitorPanel = new FactoryMonitorPanel(
                    ipFactory, macFactory, portFactory, arpCache, dnsCache, natFactory, tcpFactory);
            PacketAnalyzerPanel analyzerPanel = new PacketAnalyzerPanel();

            frame.add(addressPanel);
            frame.add(protocolPanel);
            frame.add(monitorPanel);
            frame.add(analyzerPanel);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
