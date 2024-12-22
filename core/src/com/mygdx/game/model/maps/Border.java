package com.mygdx.game.model.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.Arrays;
import java.util.Optional;

public enum Border {
    DEEP_PURPLE(
            0,
            "deepPurple_top.png",
            "deepPurple_side.png"
    ),
    FUCHSIA(
            1,
            "fuchsia_top.png",
            "fuchsia_side.png"
    ),
    LIME(
            2,
            "lime_top.png",
            "lime_side.png"
    ),
    MAROON(
            3,
            "maroon_top.png",
            "maroon_side.png"
    ),
    NAVY(
            4,
            "navy_top.png",
            "navy_side.png"
    ),
    OLIVE(
            5,
            "olive_top.png",
            "olive_side.png"
    ),
    ORANGE(
            6,
            "orange_top.png",
            "orange_side.png"
    ),
    PURPLE(
            7,
            "purple_top.png",
            "purple_side.png"
    ),
    RED(
            8,
            "red_top.png",
            "red_side.png"
    ),
    YELLOW(
            9,
            "yellow_top.png",
            "yellow_side.png"
    );
    private static final String directory = "borders/";
    private final int num;
    private final TiledMapTile top;
    private final TiledMapTile side;

    Border(int n, String pathTop, String pathSide) {
        this.num = n;
        this.top = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(directory + pathTop))));
        this.side = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(directory + pathSide))));
    }

    public static Border get(int n) {
        if (n < 0 || n > 9) return null;
        Optional<Border> b= Arrays.stream(Border.values()).filter(border -> border.num == n).findFirst();
        if(b.isEmpty()) return null;
        return b.get();
    }

    public static void flipCell(TiledMapTileLayer.Cell cell, int neighbourNumber) {
        switch (neighbourNumber) {
            case 0 -> cell.setFlipHorizontally(true);
            case 3, 4 -> cell.setFlipVertically(true);
            case 5 -> {
                cell.setFlipVertically(true);
                cell.setFlipHorizontally(true);
            }
            default -> {
            }
        }
    }

    public TiledMapTile getTile(int neighbourNumber) {
        if(neighbourNumber<0||neighbourNumber>5) return null;
        if (neighbourNumber == 1 || neighbourNumber == 4) return top;
        else return side;
    }
}
