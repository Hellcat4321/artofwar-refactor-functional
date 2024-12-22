package com.mygdx.game.model.gameobjects.buildings;

import static com.mygdx.game.ProjectVariables.*;
import static com.mygdx.game.ProjectVariables.BuildingSpec.*;

import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

public class Tower extends Building{
    public Tower(
            GameMap GameMap,
            MapCell placement,
            Player owner
    ){
        super(
                GameMap,
                placement,
                owner
        );
    }

    @Override
    public int getMoneyPerTurn() {
        return towerMoneyPerTurn;
    }

    @Override
    public int getDefence() {
        return towerDefence;
    }

    @Override
    public StaticTiledMapTile getTile() {
        return towerPic;
    }

    @Override
    public int getCost() {
        return towerCost;
    }
}
