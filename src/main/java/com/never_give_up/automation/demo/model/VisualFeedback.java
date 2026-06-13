package com.never_give_up.automation.demo.model;

import java.awt.Color;

public class VisualFeedback {
    public String label;
    public Color color;
    public long timestamp;

    public VisualFeedback(String label, Color color) {
        this.label = label;
        this.color = color;
        this.timestamp = System.currentTimeMillis();
    }
}
