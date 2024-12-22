package com.mygdx.game.controllers.listeners.game_cl;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.view.stages.MainGameStage;
import com.mygdx.game.model.gameobjects.units.Unit;
import com.mygdx.game.model.maps.MapCell;

public class MoveToCellCL extends CellActionCL {
    public MoveToCellCL(MainGameStage stage, MapCell cell) {
        super(stage,cell);
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        Unit movingUnit = stage.getGamingProcess().getUnitSelection();
        stage.moveUnit(movingUnit, cell.x, cell.y);
    }
}
