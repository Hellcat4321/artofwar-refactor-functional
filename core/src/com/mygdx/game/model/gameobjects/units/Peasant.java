package com.mygdx.game.model.gameobjects.units;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

import static com.mygdx.game.ProjectVariables.*;
import static com.mygdx.game.ProjectVariables.UnitSpec.*;

public class Peasant extends Unit {
    public Peasant(
            GameMap GameMap,
            MapCell placement,
            Player owner) {
        super(
                GameMap,
                placement,
                owner
        );
    }

    @Override
    public int getPower() {
        return peasantPower;
    }

    @Override
    public int getDistance() {
        return peasantDistance;
    }

    @Override
    public int getMoneyPerTurn() {
        return peasantMoneyPerTurn;
    }

    @Override
    public int getDefence() {
        return peasantDefence;
    }

    @Override
    public int getCost() {
        return peasantCost;
    }

    @Override
    public TiledMapTile getTile() {
        return peasantPic;
    }
}
