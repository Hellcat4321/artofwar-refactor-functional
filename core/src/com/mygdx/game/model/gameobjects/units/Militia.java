package com.mygdx.game.model.gameobjects.units;

import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

import static com.mygdx.game.ProjectVariables.*;
import static com.mygdx.game.ProjectVariables.UnitSpec.*;

public class Militia extends Unit {
    public Militia(
            GameMap GameMap,
            MapCell placement,
            Player owner
    ) {
        super(
                GameMap,
                placement,
                owner
        );
    }

    @Override
    public int getPower() {
        return militiaPower;
    }

    @Override
    public int getDistance() {
        return militiaDistance;
    }

    @Override
    public int getMoneyPerTurn() {
        return militiaMoneyPerTurn;
    }

    @Override
    public int getDefence() {
        return militiaDefence;
    }

    @Override
    public StaticTiledMapTile getTile() {
        return militiaPic;
    }

    @Override
    public int getCost() {
        return militiaCost;
    }
}
