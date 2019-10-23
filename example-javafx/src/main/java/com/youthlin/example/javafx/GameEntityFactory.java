package com.youthlin.example.javafx;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

/**
 * @author youthlin.chen
 * @date 2019-10-23 21:08
 */
public class GameEntityFactory implements EntityFactory {
    @Spawns("ground")
    public Entity newGround(SpawnData data) {
        return FXGL.entityBuilder()
                .from(data)
                .view(new Line(10, FXGL.getAppHeight(), 490, FXGL.getAppHeight()))
                .collidable()
                .build();
    }

    @Spawns("tile")
    public Entity newTile(SpawnData data) {
        return FXGL.entityBuilder()
                .from(data)
                .view(randomPolygon())
                .collidable()
                .build();
    }

    private static Polygon randomPolygon() {
        return new Polygon(TileType.randomType().points);
    }

}
