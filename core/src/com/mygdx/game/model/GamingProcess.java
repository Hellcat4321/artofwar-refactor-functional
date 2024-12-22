package com.mygdx.game.model;

import com.mygdx.game.db.GameDatabase;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.gameobjects.buildings.Capital;
import com.mygdx.game.model.gameobjects.units.Unit;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.utils.TurnState;
import com.mygdx.game.model.players.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.util.stream.Collectors;


public class GamingProcess {
    private int gameId;
    private int round;
    private int currentPlayer;

    private final GameMap gameMap;
    private final GameDatabase gameDatabase;
    private Map<Integer, Player> players = new HashMap<>();
    private final List<Integer> turnOrder = new ArrayList<>();

    private GameObject gameObjectToPlace = null;
    private Unit unitToMove = null;

    public GamingProcess(GameMap gameMap, GameDatabase gameDatabase, List<Player> players, int gameId) {
        setPlayers(players);
        this.gameId = gameId;
        this.gameMap = gameMap;
        this.currentPlayer = 0;
        this.round = 0;
        this.gameDatabase = gameDatabase;
    }


    public void setGameObjectSelection(GameObject gameObjectToPlace) {
        this.gameObjectToPlace = gameObjectToPlace;
    }

    public void setUnitSelection(Unit unit) {
        unitToMove = unit;
    }


    public Unit getUnitSelection() {
        return unitToMove;
    }

    public GameObject getGameObjectSelection() {
        return gameObjectToPlace;
    }


    public Player getCurrentPlayer() {
        return players.get(turnOrder.get(currentPlayer));
    }

    private void nextRound() {
        this.players = players.entrySet()
                .stream()
                .filter(player -> !player.getValue().isDone())
                .collect(Collectors.toMap(Entry<Integer, Player>::getKey, Entry<Integer, Player>::getValue));
        ++round;
    }

    private void killGameObject(GameObject gameObject) {
        if (players .containsKey(gameObject.ownerId)) {
            Player player = players.get(gameObject.ownerId);
            player = player.removeGameObject(gameObject);
            players = updatePlayer(player);
        }
        gameMap.removeGameObject(gameObject);
    }

    public void wipePlayerArmy(Player player) {
        player.getUnits().forEach(gameMap::removeGameObject);
        player.getUnits().clear();
        gameMap.recountDefenceCoverage(players);
    }

    public void createCapitalArea(Player player, int x, int y) {
        Capital capital = new Capital(gameMap, null, player);
        Player newPlayer = player.addGameObject(capital);

        players = updatePlayer(newPlayer);
        recountPlayerTerritory(gameMap.setGameObject(capital, x, y));
        int cnt = gameMap.createCapitalArea(capital);
        players = updatePlayer(players.get(newPlayer.id).addTerritory(cnt));
    }


    public void placeNewGameObjectOnCell(GameObject gameObject, int x, int y) {
        MapCell placeTo = gameMap.getCell(x, y);
        if (placeTo.getGameObject() != null) {
            killGameObject(placeTo.getGameObject());
        }

        Player newPlayer = players.get(gameObject.ownerId).addGameObject(gameObject);
        players = updatePlayer(newPlayer);
        recountPlayerTerritory(gameMap.setGameObject(gameObject, x, y));

        gameMap.recountDefenceCoverage(players);
    }

    public void moveUnit(Unit unit, int x, int y) {
        MapCell moveTo = gameMap.getCell(x, y);

        if (moveTo == null) return;
        if (!unit.canMove(moveTo)) return;

        if (moveTo.getGameObject() != null) {
            killGameObject(moveTo.getGameObject());
        }

        if (unit.getPlacement() != null) {
            gameMap.removeGameObject(unit);
        }

        recountPlayerTerritory(gameMap.setGameObject(unit, x, y));
        gameMap.recountDefenceCoverage(players);

        unit.setMoved(true);
    }

    private void recountPlayerTerritory(int[] ids) {
        if (ids[0] == ids[1]) return;
        if(ids[0]!=-1)players = updatePlayer(players.get(ids[0]).removeTerritory(1));
        players = updatePlayer(players.get(ids[1]).addTerritory(1));
    }

    private void nextPlayer() {
        if (isLast()) {
            nextRound();
            currentPlayer = 0;
            return;
        }
        ++currentPlayer;
        while (!players.containsKey(turnOrder.get(currentPlayer)) || getCurrentPlayer().isDone()) {
            nextPlayer();
        }
    }

    public TurnState nextTurn() {
        nextPlayer();
        if (players.size() < 2) {
            insertGameIntoDB();
            return TurnState.FINISH;
        }
        if (round == 0) return TurnState.CAPITAL;
        Player player = getCurrentPlayer();
        if (player.isDone()) {
            insertTurnInfoIntoDB(player, round);
            return nextTurn();
        }

        Map.Entry<Player, Boolean> res = player.countIncome();
        player = res.getKey();
        if (!res.getValue()) wipePlayerArmy(player);

        player.refreshUnits();

        players = updatePlayer(player);
        if (!res.getValue()) gameMap.recountDefenceCoverage(players);

        insertTurnInfoIntoDB(player, round);

        if (player.getCapital() == null) {
            setGameObjectSelection(new Capital(gameMap, null, player));
            return TurnState.CAPITAL;
        }

        return TurnState.OK;
    }

    private void insertTurnInfoIntoDB(Player player, int round) {
        try {
            gameDatabase.insertTurn(player.id, gameId, round, player.getGold(), player.getTerritories());
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void insertGameIntoDB() {
        try {
            gameDatabase.finishGame(gameId);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isFirst() {
        return currentPlayer == 0;
    }

    public boolean isLast() {
        return currentPlayer == turnOrder.size() - 1;
    }

    public void setId(int id) {
        this.gameId = id;
    }

    public int getGameId() {
        return gameId;
    }

    public int getRound() {
        return round;
    }

    private void setPlayers(List<Player> players) {
        for (int i = 0; i < players.size(); ++i) {
            this.players.put(players.get(i).id, players.get(i));
            this.turnOrder.add(players.get(i).id);
        }
    }

    public GameMap getMap() {
        return gameMap;
    }

    public Map<Integer, Player> getPlayers() {
        return new HashMap<>(players);
    }

    public List<Integer> getTurnOrder() {
        return new ArrayList<>(turnOrder);
    }

    private Map<Integer, Player> updatePlayer(Player player) {
        Map<Integer, Player> newPlayers = new HashMap<>(players);
        newPlayers.put(player.id, player);
        return newPlayers;
    }
}
