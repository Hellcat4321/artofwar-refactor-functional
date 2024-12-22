package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.mygdx.game.model.players.Player;

import java.util.ArrayList;
import java.util.List;

public class ProjectVariables {



    public static class UnitSpec{
        public static final int
                peasantPower = 1,
                peasantDefence = 1,
                peasantDistance = 5,
                peasantCost = 10,
                peasantMoneyPerTurn = -1;

        public static final int
                militiaPower = 2,
                militiaDefence = 2,
                militiaDistance = 5,
                militiaCost = 20,
                militiaMoneyPerTurn = -3;

        public static final int
                knightPower = 3,
                knightDefence = 3,
                knightDistance = 4,
                knightCost = 30,
                knightMoneyPerTurn = -10;

        public static final int
                paladinPower = 5,
                paladinDefence = 3,
                paladinDistance = 4,
                paladinCost = 30,
                paladinMoneyPerTurn = -10;
    }
    public static class BuildingSpec{
        public static final int
                towerCost = 15,
                towerMoneyPerTurn = -3,
                towerDefence = 2;

        public static final int
                superTowerCost = 35,
                superTowerMoneyPerTurn = -6,
                superTowerDefence = 3;

        public static final int
                defaultFarmCost = 15,
                additionalFarmCost = 3,
                farmMoneyPerTurn = 5,
                farmDefence = 0;

        public static final int
                capitalCost = 0,
                capitalMoneyPerTurn = 2,
                capitalDefence = 1;
    }


    public static String
            unitAssetsDirectory = "units/",
            buildingAssetsDirectory="buildings/";


    public static StaticTiledMapTile
            peasantPic = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(unitAssetsDirectory + "peasant.png")))),
            militiaPic = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(unitAssetsDirectory +"militia.png")))),
            knightPic= new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(unitAssetsDirectory + "knight.png")))),
            paladinPic = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(unitAssetsDirectory + "paladin.png"))));



    public static StaticTiledMapTile
            farmPic = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(buildingAssetsDirectory + "farm.png")))),
            towerPic=new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(buildingAssetsDirectory + "tower.png")))),
            superTowerPic=new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(buildingAssetsDirectory + "superTower.png")))),
            capitalPic=new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal(buildingAssetsDirectory + "capital.png"))));

    public static int
            tileWidth = 64,
            tileHeight = 55;
    public static int
            screenWidth = 1920,
            screenHeight = 980;
    public static TiledMapTile blackTile = new StaticTiledMapTile(new TextureRegion(new Texture(Gdx.files.internal("pastel_resources_hex/rotat/black.png"))));



}
