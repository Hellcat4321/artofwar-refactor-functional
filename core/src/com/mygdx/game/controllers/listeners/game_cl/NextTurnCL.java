package com.mygdx.game.controllers.listeners.game_cl;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.view.stages.MainGameStage;

public class NextTurnCL extends MainGameStageCL {

    public NextTurnCL(MainGameStage stage) {
        super(stage);
    }


    @Override
    public void clicked(InputEvent event, float x, float y) {

        stage.nexTurn();
    }
}
