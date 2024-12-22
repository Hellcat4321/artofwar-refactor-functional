package com.mygdx.game.controllers.listeners.game_cl;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.game.view.stages.MainGameStage;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

import java.lang.reflect.Constructor;

public class GameObjectCreationCL extends MainGameStageCL {
    private Class<? extends GameObject> aClass;

    public GameObjectCreationCL(MainGameStage stage, Class<? extends GameObject> aClass) {
        super(stage);
        this.aClass = aClass;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        GameObject gameObject;
        try {
            Constructor<? extends GameObject> cons = aClass.getDeclaredConstructor(GameMap.class, MapCell.class, Player.class);
            gameObject = cons.newInstance(stage.getMap(), null, stage.getGamingProcess().getCurrentPlayer());
        } catch (Exception e) {
            return;
        }
        stage.addNewGameObject(gameObject);
    }
}
