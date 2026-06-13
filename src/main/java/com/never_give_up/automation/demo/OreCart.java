package com.never_give_up.automation.demo;

import java.awt.Point;

public class OreCart {
    public double x, y;
    public double speed = 6.0;
    public String oreType;
    public boolean isArrived = false;
    private final Point pcFactory;

    public OreCart(double x, double y, String type, Point pcFactory) {
        this.x = x;
        this.y = y;
        this.oreType = type;
        this.pcFactory = pcFactory;
    }

    public void update() {
        double dx = pcFactory.x - x;
        double dy = pcFactory.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist <= speed) isArrived = true;
        else {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
    }
}
