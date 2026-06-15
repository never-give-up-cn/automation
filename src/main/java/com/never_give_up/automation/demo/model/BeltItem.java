package com.never_give_up.automation.demo.model;

import com.never_give_up.automation.demo.DataCart;

public class BeltItem {
    public static final double BELT_SPEED = 2.0; // 像素/帧 (40px / 20帧 = 600ms过一格 @33FPS)
    public static final long MAX_LIFETIME = 30000; // 30秒后自动消失

    public int tileRow;
    public int tileCol;
    public double pixelX;          // 瓦片内偏移 (0~39)
    public double pixelY;
    public BeltDirection direction;
    public String type;
    public DataCart sourceDataCart;
    public boolean consumed = false;
    public boolean isAtEnd = false;
    public long createdAt;

    public BeltItem(int tileRow, int tileCol, double pixelX, double pixelY,
                    BeltDirection direction, String type) {
        this.tileRow = tileRow;
        this.tileCol = tileCol;
        this.pixelX = pixelX;
        this.pixelY = pixelY;
        this.direction = direction;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }

    public BeltItem(int tileRow, int tileCol, String type) {
        this(tileRow, tileCol, 0, 0, BeltDirection.RIGHT, type);
    }
}
