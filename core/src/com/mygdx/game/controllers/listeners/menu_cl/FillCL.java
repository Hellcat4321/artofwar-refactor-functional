package com.mygdx.game.controllers.listeners.menu_cl;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.view.stages.MenuStage;

public class FillCL extends MenuCL{
    public FillCL(MenuStage stage){
        super(stage);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        stage.createPlayerTable();
    }
}
