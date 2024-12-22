package com.mygdx.game.model.maps;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.mygdx.game.ProjectVariables;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.players.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mygdx.game.ProjectVariables.tileHeight;
import static com.mygdx.game.ProjectVariables.tileWidth;

public class MapToRendererTransformator {
    private final HexagonalTiledMapRenderer renderer;
    private TiledMap tiledMap;
    private GameMap gameMap;

    public MapToRendererTransformator(GameMap gameMap) {
        this.gameMap = gameMap;
        loadNewTiledMap();
        renderer = new HexagonalTiledMapRenderer(tiledMap);
    }

    private void loadNewTiledMap() {
        tiledMap = new TiledMap();
        createMapLayer();
        createBorders();
        createGameObjectsLayer();
    }

    private void createMapLayer() {
        TiledMapTileLayer generalMapLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), tileWidth, tileHeight);
        for (int i = 0; i < gameMap.getWidth(); ++i) {
            for (int j = 0; j < gameMap.getHeight(); ++j) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(gameMap.getMapCreator().getCells()[i][j].getType().tile());
                generalMapLayer.setCell(i, j, cell);
            }
        }
        tiledMap.getLayers().add(generalMapLayer);
    }

    private void createGameObjectsLayer() {
        TiledMapTileLayer gameObjectsLayer = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), tileWidth, tileHeight);
        for (int i = 0; i < gameMap.getWidth(); ++i) {
            for (int j = 0; j < gameMap.getHeight(); ++j) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                GameObject obj = gameMap.getMapCreator().getCells()[i][j].getGameObject();
                gameObjectsLayer.setCell(i, j, cell);
                if (obj == null) continue;
                cell.setTile(obj.getTile());
            }
        }
        tiledMap.getLayers().add(gameObjectsLayer);
    }

    private void createBorders() {
        TiledMapTileLayer[] borders = new TiledMapTileLayer[6];
        for (int i = 0; i < 6; ++i) {
            borders[i] = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), tileWidth, tileHeight);
            tiledMap.getLayers().add(borders[i]);
        }
        for (int i = 0; i < gameMap.getWidth(); ++i) {
            for (int j = 0; j < gameMap.getHeight(); ++j) {
                for (int k = 0; k < 6; ++k) {
                    TiledMapTileLayer.Cell tiledMapCell = new TiledMapTileLayer.Cell();
                    borders[k].setCell(i, j, tiledMapCell);
                    Border.flipCell(tiledMapCell, k);
                }
            }
        }
    }

    private void updateBorders(int x, int y, List<Integer> turnOrder) {
        setBorders(x, y, turnOrder);
        int[][] nb = MapCreator.getNeighbours(x);
        for (int i = 0; i < 6; ++i) {
            MapCell cell = gameMap.getCell(x + nb[i][0], y + nb[i][1]);
            if (cell == null) continue;
            setBorders(cell.x, cell.y, turnOrder);
        }
    }

    private void setBorders(int x, int y, List<Integer> turnOrder) {
        MapCell curCell = gameMap.getCell(x, y);
        int[][] nb = MapCreator.getNeighbours(x);
        if (curCell.getType() == CellType.WATER) return;
        if (curCell.getOwnerId() == Player.NOBODY.id) return;
        for (int k = 0; k < 6; ++k) {
            MapCell cell = gameMap.getCell(x + nb[k][0], y + nb[k][1]);
            TiledMapTileLayer.Cell cell1 = ((TiledMapTileLayer) tiledMap.getLayers().get(k + 1)).getCell(x, y);
            if (cell == null || cell.getOwnerId() != curCell.getOwnerId()) {
                int index = turnOrder.indexOf(curCell.getOwnerId());
                if (index == -1)
                    throw new NullPointerException("Player with id " + curCell.getOwnerId() + " not found");
                cell1.setTile(Border.get(index).getTile(k));
            } else cell1.setTile(null);
        }
    }

    public void createSelectedArea(int[][] area) {
        TiledMapTileLayer black = new TiledMapTileLayer(gameMap.getWidth(), gameMap.getHeight(), tileWidth, tileHeight);
        black.setName("selected");
        for (int i = 0; i < gameMap.getWidth(); ++i) {
            for (int j = 0; j < gameMap.getHeight(); ++j) {
                if (area[i][j] != -1) continue;
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(ProjectVariables.blackTile);
                black.setCell(i, j, cell);
            }
        }
        black.setOpacity(0.7f);
        tiledMap.getLayers().add(black);
    }

    public void clearSelectedArea() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("selected");
        if (layer != null) tiledMap.getLayers().remove(layer);

    }

    public void update(int x, int y, List<Integer> turnOrder) {
        if (x < 0 || x >= gameMap.getWidth() || y < 0 || y >= gameMap.getHeight()) return;
        TiledMapTileLayer gameObjectsLayer = (TiledMapTileLayer) tiledMap.getLayers().get(7);
        MapCell mapCell = gameMap.getCell(x, y);
        TiledMapTileLayer.Cell cell = gameObjectsLayer.getCell(x, y);
        if (mapCell.getGameObject() == null) {
            cell.setTile(null);
            return;
        }
        gameObjectsLayer.getCell(x, y).setTile(gameMap.getCell(x, y).getGameObject().getTile());
        updateBorders(x, y, turnOrder);
    }

    public HexagonalTiledMapRenderer getRenderer() {
        return renderer;
    }
}
