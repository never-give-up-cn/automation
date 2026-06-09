package com.never_give_up.automation.demo.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class PacketAnalyzerPanel extends JPanel {
    private JTree treePacketStructure;
    private JTextArea txtHexView;
    private JTextArea txtDetailInfo;

    public PacketAnalyzerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("🔍 报文解析器 (Wireshark)"));

        createPacketTree();
        createHexView();
        createDetailPanel();
    }

    private void createPacketTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("报文结构");
        treePacketStructure = new JTree(new DefaultTreeModel(root));

        JScrollPane treeScroll = new JScrollPane(treePacketStructure);
        treeScroll.setBorder(BorderFactory.createTitledBorder("分层结构"));

        add(treeScroll, BorderLayout.WEST);
    }

    private void createHexView() {
        txtHexView = new JTextArea();
        txtHexView.setEditable(false);
        txtHexView.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtHexView.setBackground(new Color(30, 30, 30));
        txtHexView.setForeground(new Color(50, 255, 120));

        JScrollPane hexScroll = new JScrollPane(txtHexView);
        hexScroll.setBorder(BorderFactory.createTitledBorder("十六进制视图"));

        add(hexScroll, BorderLayout.CENTER);
    }

    private void createDetailPanel() {
        txtDetailInfo = new JTextArea();
        txtDetailInfo.setEditable(false);
        txtDetailInfo.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        JScrollPane detailScroll = new JScrollPane(txtDetailInfo);
        detailScroll.setBorder(BorderFactory.createTitledBorder("详细信息"));

        add(detailScroll, BorderLayout.EAST);
    }

    public void displayPacket(byte[] data, String[] layers) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("报文结构");

        for (String layer : layers) {
            DefaultMutableTreeNode layerNode = new DefaultMutableTreeNode(layer);
            root.add(layerNode);
        }

        treePacketStructure.setModel(new DefaultTreeModel(root));

        StringBuilder hexSb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            hexSb.append(String.format("%02X ", data[i]));
            if ((i + 1) % 16 == 0) {
                hexSb.append("\n");
            }
        }
        txtHexView.setText(hexSb.toString());

        txtDetailInfo.setText("报文长度: " + data.length + " 字节\n" +
                "层级数: " + layers.length);
    }

    public void clear() {
        treePacketStructure.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("报文结构")));
        txtHexView.setText("");
        txtDetailInfo.setText("");
    }
}
