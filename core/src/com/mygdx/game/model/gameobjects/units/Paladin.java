package com.mygdx.game.model.gameobjects.units;

import static com.mygdx.game.ProjectVariables.UnitSpec.*;
import static com.mygdx.game.ProjectVariables.paladinPic;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

public class Paladin extends Unit {
    public Paladin(
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
        return paladinPower;
    }

    @Override
    public int getDistance() {
        return paladinDistance;
    }

    @Override
    public int getMoneyPerTurn() {
        return paladinMoneyPerTurn;
    }

    @Override
    public int getDefence() {
        return paladinDefence;
    }

    @Override
    public int getCost() {
        return paladinCost;
    }

    @Override
    public TiledMapTile getTile() {
        return paladinPic;
    }
}
