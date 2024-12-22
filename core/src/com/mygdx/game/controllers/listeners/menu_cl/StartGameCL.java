package com.mygdx.game.controllers.listeners.menu_cl;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.view.stages.MenuStage;

import java.sql.SQLException;

public class StartGameCL extends MenuCL{
    public StartGameCL(MenuStage stage){
        super(stage);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        try {
            stage.startGame();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
