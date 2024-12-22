package com.mygdx.game.model.gameobjects.units;

import static com.mygdx.game.ProjectVariables.*;
import static com.mygdx.game.ProjectVariables.UnitSpec.*;

import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

public class Knight extends Unit {
    public Knight(
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
        return knightPower;
    }

    @Override
    public int getDistance() {
        return knightDistance;
    }

    @Override
    public int getMoneyPerTurn() {
        return knightMoneyPerTurn;
    }

    @Override
    public int getDefence() {
        return knightDefence;
    }

    @Override
    public StaticTiledMapTile getTile() {
        return knightPic;
    }

    @Override
    public int getCost() {
        return knightCost;
    }
}
