package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.demo.engine.Material;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 机械臂筛选器设置框。
 * <p>
 * 给某个工位的机械臂设置：<b>设备需求</b>（要消耗哪种原料）、<b>白名单</b>、
 * <b>黑名单</b>。白名单非空时只有名单里的物料才取；黑名单里的物料一律不取，
 * 被挡掉的物料就留在传送带上（或让工件直接过站）。
 * </p>
 */
public class ArmFilterDialog extends JDialog {

    /** 需求下拉框 */
    private final JComboBox<String> demandBox = new JComboBox<>();
    /** 白名单勾选框 */
    private final Map<String, JCheckBox> whiteBoxes = new LinkedHashMap<>();
    /** 黑名单勾选框 */
    private final Map<String, JCheckBox> blackBoxes = new LinkedHashMap<>();

    private final GameCanvas canvas;
    private final String tag;
    private boolean confirmed = false;

    public ArmFilterDialog(Frame owner, GameCanvas canvas, String tag) {
        super(owner, "机械臂筛选器 · " + tag, true);
        this.canvas = canvas;
        this.tag = tag;
        buildUi();
        loadFromCanvas();
        setLocationRelativeTo(owner);
    }

    private void buildUi() {
        JPanel root = new JPanel();
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));

        // 顶部：需求
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.add(new JLabel("设备需求原料："));
        demandBox.setPreferredSize(new Dimension(150, 24));
        top.add(demandBox);
        root.add(top, BorderLayout.NORTH);

        // 中间：白名单 / 黑名单
        JPanel mid = new JPanel(new GridLayout(1, 2, 16, 0));
        mid.add(buildListPanel("白名单（只取名单里的物料）", whiteBoxes));
        mid.add(buildListPanel("黑名单（名单里的物料一律不取）", blackBoxes));
        root.add(mid, BorderLayout.CENTER);

        // 底部：按钮
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton reset = new JButton("恢复默认");
        reset.addActionListener(e -> {
            demandBox.setSelectedItem(currentDemand());
            whiteBoxes.values().forEach(b -> b.setSelected(false));
            blackBoxes.values().forEach(b -> b.setSelected(false));
        });
        JButton cancel = new JButton("取消");
        cancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        JButton ok = new JButton("确定");
        ok.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        bottom.add(reset);
        bottom.add(cancel);
        bottom.add(ok);
        root.add(bottom, BorderLayout.SOUTH);

        // 供给带上正在送的料块说明
        JLabel tip = new JLabel("<html>说明：一条传送带只运一种物料；机械臂按需求去供给带上取料，<br>"
                + "被筛选器挡掉的物料会留在带上排队。</html>");
        tip.setForeground(new Color(0x55, 0x5F, 0x66));
        root.add(tip, BorderLayout.WEST);

        setContentPane(root);
        getRootPane().setDefaultButton(ok);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmed = false;
            }
        });
        pack();
        setResizable(false);
    }

    private JPanel buildListPanel(String title, Map<String, JCheckBox> boxes) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(title));
        for (String material : Material.ALL) {
            JCheckBox box = new JCheckBox(Material.displayName(material));
            boxes.put(material, box);
            p.add(box);
        }
        return p;
    }

    /** 从画布读当前配置 */
    private void loadFromCanvas() {
        String demand = canvas.getStationDemand(tag);
        demandBox.removeAllItems();
        List<String> options = new ArrayList<>();
        options.add(Material.PAYLOAD);
        for (String m : Material.ALL) {
            if (Material.isHeader(m)) {
                options.add(m);
            }
        }
        for (String m : options) {
            demandBox.addItem(m);
        }
        if (demand != null) {
            demandBox.setSelectedItem(demand);
        }
        Set<String> white = canvas.getStationWhitelist(tag);
        Set<String> black = canvas.getStationBlacklist(tag);
        for (String m : Material.ALL) {
            whiteBoxes.get(m).setSelected(white.contains(m));
            blackBoxes.get(m).setSelected(black.contains(m));
        }
    }

    private String currentDemand() {
        Object o = demandBox.getSelectedItem();
        return o == null ? Material.PAYLOAD : o.toString();
    }

    /** 点「确定」时把配置写回画布 */
    public void applyToCanvas() {
        if (canvas == null || tag == null) {
            return;
        }
        canvas.setStationDemand(tag, currentDemand());
        Set<String> white = new LinkedHashSet<>();
        Set<String> black = new LinkedHashSet<>();
        for (String m : Material.ALL) {
            if (whiteBoxes.get(m).isSelected()) {
                white.add(m);
            }
            if (blackBoxes.get(m).isSelected()) {
                black.add(m);
            }
        }
        canvas.setStationFilter(tag, white, black);
    }

    /** 是否点了确定 */
    public boolean isConfirmed() {
        return confirmed;
    }
}
