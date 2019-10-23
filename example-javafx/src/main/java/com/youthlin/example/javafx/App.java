package com.youthlin.example.javafx;


import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;

import java.util.Map;

import static com.youthlin.utils.i18n.Translation.__;

/**
 * @author youthlin.chen
 * @date 2019-10-22 17:26
 */
public class App extends GameApplication {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle(__("Tetris"));
        settings.setWidth(640);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new GameEntityFactory());
        FXGL.spawn("ground");
        FXGL.spawn("tile", 510, 100);
    }

    @Override
    protected void initUI() {
        FXGL.addVarText(510, 10, "score");
    }
}
