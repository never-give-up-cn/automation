package com.never_give_up.automation.demo.model;

public enum BeltDirection {
    NONE(0, 0, 0),
    RIGHT(1, 1, 0),
    DOWN(2, 0, 1),
    LEFT(3, -1, 0),
    UP(4, 0, -1);

    public final int value;
    public final int dx;
    public final int dy;

    BeltDirection(int value, int dx, int dy) {
        this.value = value;
        this.dx = dx;
        this.dy = dy;
    }

    public boolean isHorizontal() {
        return dx != 0;
    }

    public boolean isVertical() {
        return dy != 0;
    }

    public BeltDirection opposite() {
        switch (this) {
            case RIGHT: return LEFT;
            case LEFT: return RIGHT;
            case UP: return DOWN;
            case DOWN: return UP;
            default: return NONE;
        }
    }

    public static BeltDirection fromDelta(int dx, int dy) {
        if (dx > 0) return RIGHT;
        if (dx < 0) return LEFT;
        if (dy > 0) return DOWN;
        if (dy < 0) return UP;
        return NONE;
    }

    /**
     * 判断下一格的目标方向能否从进入方向接收物品。
     * 例如: 从 LEFT 方向进入时，目标方向可以是 LEFT(直行)、UP(向上弯)、DOWN(向下弯)
     */
    public static boolean canAccept(BeltDirection targetDir, BeltDirection enteringFrom) {
        if (targetDir == NONE) return false;
        if (targetDir == enteringFrom) return true; // 直行
        // 直角弯兼容
        if (enteringFrom == LEFT || enteringFrom == RIGHT) {
            return targetDir == UP || targetDir == DOWN;
        }
        if (enteringFrom == UP || enteringFrom == DOWN) {
            return targetDir == LEFT || targetDir == RIGHT;
        }
        return false;
    }
}
