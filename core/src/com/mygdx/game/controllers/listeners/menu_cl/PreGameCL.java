package com.mygdx.game.controllers.listeners.menu_cl;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.view.stages.MenuStage;

public class PreGameCL extends MenuCL{
    public PreGameCL(MenuStage stage){
        super(stage);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        stage.toPreGame();
    }
}
