package com.mygdx.game.model.maps;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.gameobjects.buildings.Building;
import com.mygdx.game.model.gameobjects.buildings.Capital;
import com.mygdx.game.model.gameobjects.units.Unit;
import com.mygdx.game.model.players.Player;
import com.mygdx.game.utils.Triple;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.mygdx.game.model.maps.CellType.WATER;

public class GameMap {
    private final MapCreator mapCreator;

    public GameMap(int width, int height) throws IllegalArgumentException {
        this.mapCreator = new MapCreator(width, height, 0, -1);
    }

    public GameMap(int width, int height, int mode, long seed) {
        this.mapCreator = new MapCreator(width, height, mode, seed);
    }

    public MapCell getCell(int x, int y) {
        return mapCreator.safeAccess(x, y);
    }

    public void removeGameObject(GameObject gameObject) {
        MapCell cell = gameObject.getPlacement();
        cell.setGameObject(null);
    }

    public int[] setGameObject(GameObject gameObject, int x, int y) {
        MapCell cell = mapCreator.safeAccess(x, y);
        int[] res = new int[2];
        res[0] = cell.getOwnerId();
        res[1] = gameObject.ownerId;
        cell.setGameObject(gameObject);
        gameObject.setPlacement(cell);
        cell.setOwnerId(gameObject.ownerId);
        return res;
    }

    public int[][] selectCellsToMove(int xValue, int yValue) {
        int[][] mirror = new int[mapCreator.getWidth()][mapCreator.getHeight()];
        for (int[] row : mirror) {
            Arrays.fill(row, -1);
        }
        MapCell startCell;
        Unit unit;
        try {
            startCell = getCell(xValue, yValue);
            unit = (Unit) startCell.getGameObject();
        } catch (NullPointerException | ClassCastException e) {
            return null;
        }
        Queue<Triple<Integer, Integer, Integer>> q = new Queue<>();
        q.addFirst(Triple.triple(xValue, yValue, unit.getDistance()));
        while (q.notEmpty()) {
            Triple<Integer, Integer, Integer> t = q.removeLast();
            int x = t.first;
            int y = t.second;
            int n = t.third;
            MapCell cell = getCell(x, y);

            boolean stop = false;
            if (cell == null) continue;
            if (cell.getType() == WATER) continue;
            if (mirror[x][y] > 0) stop = true;
            if (startCell.getOwnerId() != cell.getOwnerId()) {
                if (cell.getDefence() >= unit.getPower()) continue;
                stop = true;
            }
            mirror[x][y] = Math.max(mirror[x][y], n);

            if (n <= 0 || stop) continue;
            int[][] nb = MapCreator.getNeighbours(x);
            for (int[] ints : nb) {
                int dx = ints[0];
                int dy = ints[1];
                q.addFirst(Triple.triple(x + dx, y + dy, n - 1));
            }
        }
        mirror[xValue][yValue] = -1;
        return mirror;
    }

    public int[][] getPlayerTerritory(int playerId) {
        int[][] territory = new int[getWidth()][getHeight()];
        for (int[] row : territory) {
            Arrays.fill(row, -1);
        }
        for (int i = 0; i < getWidth(); ++i) {
            for (int j = 0; j < getHeight(); ++j) {
                if (mapCreator.getCells()[i][j].getOwnerId() == playerId) {
                    territory[i][j] = 0;
                }
            }
        }
        return territory;
    }


    public void recountDefenceCoverage(Map<Integer, Player> playerList) {
        for (int i = 0; i < getWidth(); ++i) {
            for (int j = 0; j < getHeight(); ++j) {
                getCell(i, j).setDefence(0);
            }
        }
        for (Player player : playerList.values()) {
            for (Building building : player.getBuildings()) {
                countGameObjectCoverage(building);
            }
            for (Unit unit : player.getUnits()) {
                countGameObjectCoverage(unit);
            }
            if (player.getCapital() != null)
                countGameObjectCoverage(player.getCapital());
        }
    }

    private void countGameObjectCoverage(GameObject gameObject) {
        processNeighbours(cell -> {
            if (cell == null || cell.getOwnerId() != gameObject.ownerId) return;
            cell.setDefence(Math.max(cell.getDefence(), gameObject.getDefence()));
        }, gameObject.getPlacement());
        MapCell cell = gameObject.getPlacement();
        cell.setDefence(Math.max(cell.getDefence(), gameObject.getDefence()));
    }

    public int createCapitalArea(Capital capital) {
        var ref = new Object() {
            int cnt = 0;
        };
        processNeighbours(cell -> {
            if (cell == null) return;
            if (cell.getType() != CellType.WATER && cell.getOwnerId() == Player.NOBODY.id) {
                cell.setOwnerId(capital.ownerId);
                ++ref.cnt;
            }
        }, capital.getPlacement());
        return ref.cnt;
    }

    private void processNeighbours(Consumer<MapCell> consumer, MapCell start) {
        int[][] nb = MapCreator.getNeighbours(start.x);
        for (int i = 0; i < 6; ++i) {
            int dx = nb[i][0];
            int dy = nb[i][1];

            MapCell cell = getCell(start.x + dx, start.y + dy);
            consumer.accept(cell);
        }
    }

    public int getWidth() {
        return mapCreator.getWidth();
    }

    public int getHeight() {
        return mapCreator.getHeight();
    }

    public MapCreator getMapCreator() {
        return mapCreator;
    }
}
