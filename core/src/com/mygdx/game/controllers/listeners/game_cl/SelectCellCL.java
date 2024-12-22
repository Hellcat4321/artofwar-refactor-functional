package com.mygdx.game.controllers.listeners.game_cl;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.view.stages.MainGameStage;
import com.mygdx.game.model.gameobjects.units.Unit;
import com.mygdx.game.model.maps.MapCell;

public class SelectCellCL extends CellActionCL {

    public SelectCellCL(MainGameStage stage, MapCell cell) {
        super(stage, cell);
    }


    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (cell.getOwnerId() == stage.getGamingProcess().getCurrentPlayer().id && cell.getGameObject() instanceof Unit unit && !unit.isMoved()) {
            stage.selectUnit(unit, cell.x, cell.y);
            return;
        }
        stage.clearSelectedArea();
    }
}
