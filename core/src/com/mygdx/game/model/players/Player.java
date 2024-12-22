package com.mygdx.game.model.players;

import com.mygdx.game.ProjectVariables;
import com.mygdx.game.model.GamingProcess;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.gameobjects.buildings.Building;
import com.mygdx.game.model.gameobjects.buildings.Capital;
import com.mygdx.game.model.gameobjects.buildings.Farm;
import com.mygdx.game.model.gameobjects.units.Unit;
import com.mygdx.game.model.maps.*;
import com.mygdx.game.utils.ListUtils;

import java.util.*;
import java.util.Map;

public class Player {
    public final int id;
    public static final Player NOBODY = new Player(-1, "", null);
    public final Border border;
    public final String name;
    private List<Building> buildings;
    private List<Unit> units;
    private List<Farm> farms;

    private boolean done = false;
    private Capital capital;
    private int gold = 0;
    private int territory = 0;

    public Player(int id, String name, Border border) {
        this.id = id;
        this.border = border;
        this.name = name;
        buildings = new ArrayList<>();
        units = new ArrayList<>();
        farms = new ArrayList<>();
    }

    public Player(Player player) {
        this.id = player.id;
        this.border = player.border;
        this.name = player.name;
        this.buildings = player.buildings;
        this.units = player.units;
        this.farms = player.farms;

        this.done = player.done;
        this.capital = player.capital;
        this.gold = player.gold;
        this.territory = player.territory;
    }

    public int getFarmsNumber() {
        return farms.size();
    }

    public Player removeGameObject(GameObject gameObject) {
        Player newPlayer = new Player(this);
        if (gameObject instanceof Unit) {
            newPlayer.units = ListUtils.removeObject(units, List.of((Unit) gameObject));
        }
        if (gameObject instanceof Building) {
            if (gameObject instanceof Capital) {
                newPlayer.capital = null;
            }
            if (gameObject instanceof Farm) {
                newPlayer.farms = ListUtils.removeObject(farms, List.of((Farm) gameObject));

            } else {
                newPlayer.buildings = ListUtils.removeObject(buildings, List.of((Building) gameObject));
            }
        }
        return newPlayer;
    }

    public Player addGameObject(GameObject gameObject) {
        Player newPlayer = new Player(this);

        newPlayer.gold -= getGameObjectFullCost(gameObject);
        if (gameObject instanceof Unit) {
            newPlayer.units = ListUtils.addObject(units, (Unit) gameObject);
        }
        if (gameObject instanceof Building) {
            if (gameObject instanceof Capital) {
                newPlayer.capital = (Capital) gameObject;
            }
            if (gameObject instanceof Farm) {
                newPlayer.farms = ListUtils.addObject(farms, (Farm) gameObject);
            } else {
                newPlayer.buildings = ListUtils.addObject(buildings, (Building) gameObject);
            }
        }
        return newPlayer;
    }

    private int getGameObjectFullCost(GameObject gameObject){
        int cost = gameObject.getCost();
        if(gameObject instanceof Farm){
            cost += farms.size() + ProjectVariables.BuildingSpec.additionalFarmCost;
        }
        return cost;
    }

    public void refreshUnits() {
        units.forEach(unit -> unit.setMoved(false));
    }

    public Map.Entry<Player, Boolean> countIncome() {
        Player newPlayer = new Player(this);

        if (newPlayer.capital != null) {
            newPlayer.gold += newPlayer.getFarmsNumber() * ProjectVariables.BuildingSpec.farmMoneyPerTurn;
            newPlayer.gold += newPlayer.capital.getMoneyPerTurn();
            newPlayer.gold += newPlayer.territory;
        }
        for (Building building : newPlayer.buildings) {
            newPlayer.gold += building.getMoneyPerTurn();
        }
        for (Unit unit : newPlayer.units) {
            newPlayer.gold += unit.getMoneyPerTurn();
        }
        if (newPlayer.gold < 0) {
            newPlayer.gold = 0;
            return new AbstractMap.SimpleEntry<>(newPlayer, false);
        }
        return new AbstractMap.SimpleEntry<>(newPlayer, true);
    }

    public boolean isDone() {
        return done;
    }

    public Player addTerritory(int n) {
        Player newPlayer = new Player(this);
        newPlayer.territory+=n;
        return newPlayer;
    }

    public Player removeTerritory(int n) {
        Player newPlayer = new Player(this);
        newPlayer.territory-=n;
        if (newPlayer.territory <= 0) newPlayer.done = true;
        return newPlayer;
    }

    public Capital getCapital() {
        return capital;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public int getGold() {
        return gold;
    }

    public int getTerritories() {
        return territory;
    }
}
