package com.mygdx.game.model.gameobjects;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.mygdx.game.model.maps.CellType;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;

public abstract class GameObject {
    private final GameMap gameMap;
    private MapCell placement;
    public final int ownerId;

    public GameObject(
            GameMap gameMap,
            MapCell placement,
            Player owner
    ) {
        this.gameMap = gameMap;
        this.placement = placement;
        this.ownerId = owner.id;
    }

    public GameMap getMap() {
        return gameMap;
    }

    public MapCell getPlacement() {
        return placement;
    }

    public void setPlacement(MapCell placement) {
        if(placement.getType() == CellType.WATER) return;
        placement.setGameObject(this);
        this.placement = placement;
    }
    public abstract int getMoneyPerTurn();
    public abstract int getDefence();
    public abstract int getCost();
    public abstract TiledMapTile getTile();
}
