package com.mygdx.game.controllers.listeners.game_cl;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.view.stages.MainGameStage;

public abstract class MainGameStageCL extends ClickListener {
    protected final MainGameStage stage;
    public MainGameStageCL(MainGameStage stage){
        this.stage = stage;
    }
}
