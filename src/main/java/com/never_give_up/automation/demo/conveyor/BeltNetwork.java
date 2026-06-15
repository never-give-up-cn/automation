package com.never_give_up.automation.demo.conveyor;

import com.never_give_up.automation.demo.DataCartFactoryGame;
import com.never_give_up.automation.demo.model.BeltDirection;
import com.never_give_up.automation.demo.model.BeltItem;

import java.util.List;

public class BeltNetwork {

    public static final int TILE_SIZE = 40;

    /**
     * 更新所有传送带物品的位置
     */
    public void updateAll(List<BeltItem> items, BeltDirection[][] beltGrid,
                          String[][] buildingLayout, DataCartFactoryGame game) {
        long now = System.currentTimeMillis();

        for (BeltItem item : items) {
            if (item.consumed) continue;

            moveItem(item, beltGrid, buildingLayout, game);
        }
    }

    private void moveItem(BeltItem item, BeltDirection[][] beltGrid,
                          String[][] buildingLayout, DataCartFactoryGame game) {
        item.direction = beltGrid[item.tileRow][item.tileCol];
        if (item.direction == BeltDirection.NONE) {
            item.isAtEnd = true;
            return;
        }

        double newPixelX = item.pixelX + item.direction.dx * BeltItem.BELT_SPEED;
        double newPixelY = item.pixelY + item.direction.dy * BeltItem.BELT_SPEED;

        int tileRow = item.tileRow;
        int tileCol = item.tileCol;

        // 检查是否越界到相邻瓦片
        if (newPixelX < 0) {
            tryTransition(item, tileRow, tileCol - 1, BeltDirection.LEFT, newPixelX, newPixelY, beltGrid, buildingLayout, game);
        } else if (newPixelX >= TILE_SIZE) {
            tryTransition(item, tileRow, tileCol + 1, BeltDirection.RIGHT, newPixelX, newPixelY, beltGrid, buildingLayout, game);
        } else if (newPixelY < 0) {
            tryTransition(item, tileRow - 1, tileCol, BeltDirection.UP, newPixelX, newPixelY, beltGrid, buildingLayout, game);
        } else if (newPixelY >= TILE_SIZE) {
            tryTransition(item, tileRow + 1, tileCol, BeltDirection.DOWN, newPixelX, newPixelY, beltGrid, buildingLayout, game);
        } else {
            // 在瓦片内正常移动
            item.pixelX = newPixelX;
            item.pixelY = newPixelY;
            item.isAtEnd = false;
        }
    }

    private void tryTransition(BeltItem item, int nextRow, int nextCol,
                                BeltDirection enteringFrom, double newPixelX, double newPixelY,
                                BeltDirection[][] beltGrid, String[][] buildingLayout,
                                DataCartFactoryGame game) {
        // 边界检查
        if (nextRow < 0 || nextRow >= beltGrid.length ||
                nextCol < 0 || nextCol >= beltGrid[0].length) {
            clampToEdge(item, enteringFrom);
            item.isAtEnd = true;
            return;
        }

        BeltDirection targetDir = beltGrid[nextRow][nextCol];

        if (targetDir == BeltDirection.NONE) {
            // 先检查目标瓦片是否是建筑——如果是，触发建筑进入
            String buildingTag = buildingLayout[nextRow][nextCol];
            if (buildingTag != null && !"NONE".equals(buildingTag)
                    && item.sourceDataCart != null && !item.sourceDataCart.isArrived) {
                item.tileRow = nextRow;
                item.tileCol = nextCol;
                checkBuildingEntry(item, buildingTag, game);
                item.consumed = true;
            } else {
                // 到达传送带终点，停在边缘等待
                clampToEdge(item, enteringFrom);
                item.isAtEnd = true;
            }
            return;
        }

        if (BeltDirection.canAccept(targetDir, enteringFrom)) {
            // 成功进入下一格
            item.tileRow = nextRow;
            item.tileCol = nextCol;
            item.direction = targetDir;

            // 计算在新瓦片内的坐标（从对面边缘进入）
            switch (enteringFrom) {
                case LEFT:
                    item.pixelX = 0;
                    item.pixelY = newPixelY;
                    break;
                case RIGHT:
                    item.pixelX = TILE_SIZE - 1;
                    item.pixelY = newPixelY;
                    break;
                case UP:
                    item.pixelX = newPixelX;
                    item.pixelY = 0;
                    break;
                case DOWN:
                    item.pixelX = newPixelX;
                    item.pixelY = TILE_SIZE - 1;
                    break;
                default:
                    item.pixelX = 0;
                    item.pixelY = 0;
            }

            // 如果物品是DataCart，检查是否到达目标建筑
            if (item.sourceDataCart != null && !item.sourceDataCart.isArrived) {
                String buildingTag = buildingLayout[nextRow][nextCol];
                if (!"NONE".equals(buildingTag)) {
                    checkBuildingEntry(item, buildingTag, game);
                }
            }

            item.isAtEnd = false;
        } else {
            // 方向不匹配，停在边缘等待
            clampToEdge(item, enteringFrom);
            item.isAtEnd = true;
        }
    }

    private void clampToEdge(BeltItem item, BeltDirection enteringFrom) {
        switch (enteringFrom) {
            case LEFT:
                item.pixelX = TILE_SIZE - 1;
                break;
            case RIGHT:
                item.pixelX = 0;
                break;
            case UP:
                item.pixelY = TILE_SIZE - 1;
                break;
            case DOWN:
                item.pixelY = 0;
                break;
        }
    }

    private void checkBuildingEntry(BeltItem item, String buildingTag, DataCartFactoryGame game) {
        // 让 DataCart 进入建筑处理
        if (item.sourceDataCart != null) {
            item.sourceDataCart.enterBuilding(buildingTag, item.tileCol * TILE_SIZE + TILE_SIZE / 2,
                    item.tileRow * TILE_SIZE + TILE_SIZE / 2, game);
            item.consumed = true;
        }
    }

    /**
     * 在指定瓦片中心生成一个传送带物品
     */
    public BeltItem spawnItem(int row, int col, String type, DataCartFactoryGame game) {
        if (row < 0 || row >= game.beltGrid.length ||
                col < 0 || col >= game.beltGrid[0].length) {
            return null;
        }
        if (game.beltGrid[row][col] == BeltDirection.NONE) {
            return null;
        }

        BeltItem item = new BeltItem(row, col, TILE_SIZE / 2, TILE_SIZE / 2,
                game.beltGrid[row][col], type);
        game.beltItems.add(item);
        return item;
    }

    /**
     * 从建筑位置将 DataCart 放到传送带上
     * 在源建筑瓦片上创建 BeltItem，沿 beltDir 方向移动向下一建筑
     */
    public BeltItem spawnBeltItemFromCart(com.never_give_up.automation.demo.DataCart cart,
                                           int buildingRow, int buildingCol,
                                           BeltDirection beltDir,
                                           DataCartFactoryGame game) {
        // 在源建筑瓦片上创建 BeltItem（不再检查相邻瓦片）
        double startPixelX, startPixelY;
        switch (beltDir) {
            case RIGHT:
                startPixelX = 0;
                startPixelY = TILE_SIZE / 2;
                break;
            case LEFT:
                startPixelX = TILE_SIZE - 1;
                startPixelY = TILE_SIZE / 2;
                break;
            case DOWN:
                startPixelX = TILE_SIZE / 2;
                startPixelY = 0;
                break;
            case UP:
                startPixelX = TILE_SIZE / 2;
                startPixelY = TILE_SIZE - 1;
                break;
            default:
                startPixelX = TILE_SIZE / 2;
                startPixelY = TILE_SIZE / 2;
        }

        BeltItem item = new BeltItem(buildingRow, buildingCol, startPixelX, startPixelY,
                beltDir, cart.cartType);
        item.sourceDataCart = cart;
        game.beltItems.add(item);
        return item;
    }

    /**
     * 检查某个建筑相邻位置是否有传送带
     */
    public static boolean hasAdjacentBelt(int row, int col, BeltDirection[][] beltGrid) {
        int[][] dirs = {{0,1}, {0,-1}, {1,0}, {-1,0}};
        for (int[] d : dirs) {
            int r = row + d[0], c = col + d[1];
            if (r >= 0 && r < beltGrid.length && c >= 0 && c < beltGrid[0].length) {
                if (beltGrid[r][c] != BeltDirection.NONE) return true;
            }
        }
        return false;
    }
}
