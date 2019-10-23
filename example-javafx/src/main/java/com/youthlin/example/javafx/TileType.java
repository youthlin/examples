package com.youthlin.example.javafx;

import com.almasb.fxgl.dsl.FXGL;

/**
 * @author youthlin.chen
 * @date 2019-10-23 21:20
 */
public enum TileType {
    // 方块形状
    L(0, 0, 40, 0, 40, 40, 80, 40, 80, 80, 0, 80),
    Z(0, 0, 0, 80, 80, 40, 120, 40, 120, 80, 40, 80, 40, 40, 0, 40),
    I(0, 0, 40, 0, 40, 160, 0, 160),
    T(0, 0, 120, 0, 120, 40, 80, 40, 80, 80, 40, 80, 40, 40, 40, 0),
    O(0, 0, 80, 0, 80, 80, 0, 80);
    final double[] points;

    TileType(double... points) {
        this.points = points;
    }

    public static TileType randomType() {
        TileType[] values = TileType.values();
        return values[FXGL.random(0, values.length - 1)];
    }

}
