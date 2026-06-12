package com.never_give_up.automation.demo.winModel;

import lombok.Data;
import java.awt.Point;

@Data
public class OreCart {
    private double x, y;
    private double speed = 6.0;
    private String oreType;
    private boolean arrived = false;
    private Point target;

    public OreCart(double x, double y, String type, Point target) {
        this.x = x;
        this.y = y;
        this.oreType = type;
        this.target = target;
    }

    public void update() {
        if (target == null) return;

        double dx = target.x - x;
        double dy = target.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= speed) {
            arrived = true;
        } else {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
    }
}