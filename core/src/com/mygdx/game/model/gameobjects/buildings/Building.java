package com.mygdx.game.model.gameobjects.buildings;

import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

public abstract class Building extends GameObject {
    public Building(
            GameMap gameMap,
            MapCell placement,
            Player owner
    ){
        super(
                gameMap,
                placement,
                owner
        );
    }
}
