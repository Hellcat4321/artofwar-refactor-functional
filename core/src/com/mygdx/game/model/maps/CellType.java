package com.mygdx.game.model.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public enum CellType {
    UNDEFINED(null),
    LAND("sheepHex.png"),
    BEACH("desertHex.png"),
    WATER("waterHex.png"),
    MOUNTAIN("oreHex.png"),
    JUNGLE("woodHex.png"),
    CLAY("clayHex.png"),
    WHEAT("wheatHex.png");
    private final TiledMapTile tile;
    private static final String resourceCatalog = "pastel_resources_hex/rotat/";

    CellType(String tileTexturePath) {
        if (tileTexturePath == null) {
            this.tile = null;
            return;
        }
        this.tile = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(resourceCatalog + tileTexturePath))));
    }
    public static CellType defineBiom(double e, double m) {
        if (e < 0.2) return CellType.BEACH;

        if (e > 0.8) {
            if (m < 0.5) return CellType.MOUNTAIN;
            return CellType.MOUNTAIN;
        }

        if (e > 0.6) {
            if (m < 0.3) return CellType.JUNGLE;
            if (m < 0.5) return CellType.LAND;
            if (m < 0.8) return CellType.MOUNTAIN;
            return CellType.JUNGLE;
        }

        if (e > 0.3) {
            if (m < 0.15) return CellType.CLAY;
            if (m < 0.30) return CellType.WHEAT;
            if (m < 0.45) return CellType.LAND;
            if (m < 0.60) return CellType.JUNGLE;
            return CellType.LAND;
        }

        if (m < 0.15) return CellType.CLAY;
        if (m < 0.30) return CellType.LAND;
        if (m < 0.60) return CellType.JUNGLE;
        return CellType.LAND;
    }

    public TiledMapTile tile() {
        return this.tile;
    }
}

