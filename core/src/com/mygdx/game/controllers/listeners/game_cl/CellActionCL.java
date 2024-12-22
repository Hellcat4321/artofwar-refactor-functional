package com.mygdx.game.controllers.listeners.game_cl;

import com.mygdx.game.view.stages.MainGameStage;
import com.mygdx.game.model.maps.MapCell;

abstract class CellActionCL extends MainGameStageCL {
    protected final MapCell cell;
    public CellActionCL(MainGameStage stage, MapCell cell){
        super(stage);
        this.cell = cell;
    }
}
