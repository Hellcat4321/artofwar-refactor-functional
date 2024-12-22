package com.mygdx.game.view.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.ProjectVariables.*;
import com.mygdx.game.model.maps.MapToRendererTransformator;
import com.mygdx.game.controllers.actors.TiledMapActor;
import com.mygdx.game.controllers.listeners.game_cl.*;
import com.mygdx.game.model.GamingProcess;
import com.mygdx.game.model.gameobjects.GameObject;
import com.mygdx.game.model.gameobjects.buildings.Farm;
import com.mygdx.game.model.gameobjects.buildings.SuperTower;
import com.mygdx.game.model.gameobjects.buildings.Tower;
import com.mygdx.game.model.gameobjects.units.*;
import com.mygdx.game.model.maps.CellType;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.model.players.Player;
import com.mygdx.game.model.players.PlayerStats;
import com.mygdx.game.utils.TurnState;
import com.mygdx.game.view.ArtofWar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.BiFunction;

public class MainGameStage extends Stage implements Screen {
    private final ArtofWar artofWar;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final MapToRendererTransformator mapToRendererTransformator;
    private GameMap gameMap;
    private Group selectedArea;
    private final Group movableActors = new Group();
    private Group controls;

    private GamingProcess gamingProcess;


    public MainGameStage(GameMap gameMap, GamingProcess gamingProcess, ArtofWar artofWar) {
        this.artofWar = artofWar;
        this.gameMap = gameMap;
        this.mapToRendererTransformator = new MapToRendererTransformator(gameMap);
        this.gamingProcess = gamingProcess;
        artofWar.factory.setGameStage(this);
        addActor(movableActors);
        placeCapitalArea();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        addListener(
                new DragListener() {
                    @Override
                    public void drag(InputEvent event, float x, float y, int pointer) {
                        camera.position.set(camera.position.x - getDeltaX(), camera.position.y - getDeltaY(), 0);
                        movableActors.moveBy(getDeltaX(), getDeltaY());
                    }
                }
        );
    }

    public void nexTurn() {
        if(gamingProcess.getRound()>0)clearSelectedArea();
        TurnState turnState = gamingProcess.nextTurn();
        switch (turnState) {
            case FINISH -> showEndStats();
            case CAPITAL -> {
                if (gamingProcess.getRound() == 0) {
                    placeCapitalArea();
                } else {
                    selectArea(PlaceToCellCL::new, gameMap.getPlayerTerritory(gamingProcess.getCurrentPlayer().id));
                }
            }
        }
        if (gamingProcess.getRound() > 0) updateInfo();
    }

    public void moveUnit(Unit unit, int x, int y) {
        int xOld = unit.getPlacement().x;
        int yOld = unit.getPlacement().y;

        gamingProcess.moveUnit(unit, x, y);
        clearSelectedArea();

        mapToRendererTransformator.update(xOld, yOld, gamingProcess.getTurnOrder());
        mapToRendererTransformator.update(x, y, gamingProcess.getTurnOrder());
    }

    public void selectUnit(Unit unit, int x, int y) {
        clearSelectedArea();
        gamingProcess.setUnitSelection(unit);
        selectArea(MoveToCellCL::new, gameMap.selectCellsToMove(x, y));
    }

    public void placeCapitalFirstRound(int x, int y) {
        clearSelectedArea();
        gamingProcess.createCapitalArea(gamingProcess.getCurrentPlayer(), x, y);
        if (gamingProcess.isLast()) loadActors();
        mapToRendererTransformator.update(x, y, gamingProcess.getTurnOrder());
        nexTurn();
    }

    public void addNewGameObject(GameObject gameObject) {
        if (gamingProcess.getCurrentPlayer().getGold() < gameObject.getCost()) return;
        clearSelectedArea();
        gamingProcess.setGameObjectSelection(gameObject);
        selectArea(
                PlaceToCellCL::new,
                gameMap.getPlayerTerritory(gamingProcess.getCurrentPlayer().id)
        );
    }

    public void placeGameObject(GameObject gameObject, int x, int y) {
        clearSelectedArea();
        gamingProcess.placeNewGameObjectOnCell(gameObject, x, y);
        updateInfo();
        getRoot().findActor("next turn").setVisible(true);
        mapToRendererTransformator.update(x, y, gamingProcess.getTurnOrder());
    }

    public Group getMovableActors() {
        return movableActors;
    }

    public void setMap(GameMap GameMap) {
        this.gameMap = GameMap;
    }

    public GameMap getMap() {
        return this.gameMap;
    }

    public GamingProcess getGamingProcess() {
        return gamingProcess;
    }

    //-------------------------------------------
    // UI
    //-------------------------------------------
    public void updateInfo() {
        Label name = controls.findActor("name");
        Label farm = controls.findActor("farm");
        Label gold = controls.findActor("gold");
        farm.setText((BuildingSpec.defaultFarmCost + gamingProcess.getCurrentPlayer().getFarmsNumber() * BuildingSpec.additionalFarmCost) + " G");
        name.setText(gamingProcess.getCurrentPlayer().name);
        gold.setText(gamingProcess.getCurrentPlayer().getGold() + "");
    }

    public void placeCapitalArea() {
        selectArea(PlaceCapitalFirstRoundCL::new, gameMap.getPlayerTerritory(Player.NOBODY.id));
    }

    public void loadActors() {
        createActorsLayer();
        createControls();
        movableActors.toBack();
    }

    private void createActorsLayer() {
        Group cellActors = new Group();
        cellActors.setName("cellActors");
        for (int i = 0; i < gameMap.getWidth(); ++i) {
            for (int j = 0; j < gameMap.getHeight(); ++j) {
                if (gameMap.getMapCreator().getCells()[i][j].getType() == CellType.WATER) continue;
                TiledMapActor actor = artofWar.factory.createTiledMapActor(gameMap.getCell(i, j), new SelectCellCL(this, gameMap.getCell(i, j)), 2);
                cellActors.addActor(actor);
            }
        }
        movableActors.addActor(cellActors);
    }

    public void selectArea(BiFunction<MainGameStage, MapCell, ClickListener> listenerCreator, int[][] area) {
        selectedArea = new Group();
        for (int i = 0; i < gameMap.getWidth(); ++i) {
            for (int j = 0; j < gameMap.getHeight(); ++j) {
                if (area[i][j] != -1) {
                    TiledMapActor actor = artofWar.factory.createTiledMapActor(gameMap.getCell(i, j), listenerCreator.apply(this, gameMap.getCell(i, j)), 3);
                    selectedArea.addActor(actor);
                }
            }
        }
        mapToRendererTransformator.createSelectedArea(area);
        movableActors.addActor(selectedArea);
    }

    public void clearSelectedArea() {
        movableActors.getChild(0).setVisible(true);
        movableActors.removeActor(selectedArea);
        mapToRendererTransformator.clearSelectedArea();
        gamingProcess.setGameObjectSelection(null);
        gamingProcess.setUnitSelection(null);
        selectedArea = null;
    }

    public void endGame() {
        showEndStats();
        artofWar.setScreen(artofWar.menuStage);
        artofWar.menuStage.toMain();
        dispose();
    }

    public void showEndStats() {
        Group endStat = new Group();
        Table table = new Table();

        Label label = artofWar.factory.createLabel(0, 0, "Game over!");
        table.add(label).colspan(4).center().expand();
        table.row();

        try {
            ArrayList<PlayerStats> stats = artofWar.gameDatabase.getGameOverPlayerStats(gamingProcess.getGameId());

            table.add(artofWar.factory.createLabel(0, 0, "Player")).center().pad(10);
            table.add(artofWar.factory.createLabel(0, 0, "Max territory")).center().pad(10);
            table.add(artofWar.factory.createLabel(0, 0, "Max gold")).center().pad(10);
            table.add(artofWar.factory.createLabel(0, 0, "Last round")).center().pad(10);
            table.row();
            stats.forEach(playerStats -> {
                table.add(artofWar.factory.createLabel(0, 0, playerStats.name())).center();
                table.add(artofWar.factory.createLabel(0, 0, playerStats.maxTerrs() + "")).center();
                table.add(artofWar.factory.createLabel(0, 0, playerStats.maxGold() + "")).center();
                table.add(artofWar.factory.createLabel(0, 0, playerStats.lastRound() + "")).center();
                table.row();
            });
        } catch (SQLException ignored) {
        }

        Button button = artofWar.factory.createTextButton(100, 30, "Back to menu", new EndGameCL(this));
        table.add(button).colspan(4).center();

        TextureRegionDrawable back = new TextureRegionDrawable(new Texture(Gdx.files.internal("endStatBackground.png")));
        table.setBackground(new TextureRegionDrawable(back));
        table.pack();
        table.setBounds(0.25f * Gdx.graphics.getWidth(), 0.25f * Gdx.graphics.getHeight(), 0.5f * Gdx.graphics.getWidth(), 0.5f * Gdx.graphics.getHeight());
        endStat.addActor(table);
        setRoot(endStat);
    }

    private void createControls() {
        controls = new Group();

        HorizontalGroup currentTurnInfo = new HorizontalGroup();
        currentTurnInfo.setPosition(Gdx.graphics.getWidth() / 2f - 300, Gdx.graphics.getHeight() - 50);
        HorizontalGroup unitButtons = new HorizontalGroup();
        unitButtons.setPosition(Gdx.graphics.getWidth() / 2f - 350, 90);


        Button peasant = artofWar.factory.createImageTextButton(
                0, 0,
                new Texture(Gdx.files.internal("button/peasant_button.png")),
                new GameObjectCreationCL(this, Peasant.class),
                UnitSpec.peasantCost + " G"
        );

        Button militia = artofWar.factory.createImageTextButton(
                0, 0,
                new Texture(Gdx.files.internal("button/militia_button.png")),
                new GameObjectCreationCL(this, Militia.class),
                UnitSpec.militiaCost + " G"
        );

        Button knight = artofWar.factory.createImageTextButton(
                0, 0,
                new Texture(Gdx.files.internal("button/knight_button.png")),
                new GameObjectCreationCL(this, Knight.class),
                UnitSpec.knightCost + " G"
        );

        Button paladin = artofWar.factory.createImageTextButton(
                0, 0,
                new Texture(Gdx.files.internal("button/paladin_button.png")),
                new GameObjectCreationCL(this, Paladin.class),
                UnitSpec.paladinCost + " G"
        );

        Button farm = artofWar.factory.createImageTextButton(
                0, 0,
                new Texture(Gdx.files.internal("button/farm_button.png")),
                new GameObjectCreationCL(this, Farm.class),
                (BuildingSpec.defaultFarmCost + gamingProcess.getCurrentPlayer().getFarmsNumber() * BuildingSpec.additionalFarmCost) + " G"
        );
        farm.getChild(1).setName("farm");

        Button tower = artofWar.factory.createImageTextButton(
                0, 0,
                new Texture(Gdx.files.internal("button/tower_button.png")),
                new GameObjectCreationCL(this, Tower.class),
                BuildingSpec.towerCost + " G"
        );

        Button supertower = artofWar.factory.createImageTextButton(
                0, 0,
                new Texture(Gdx.files.internal("button/superTower_button.png")),
                new GameObjectCreationCL(this, SuperTower.class),
                BuildingSpec.superTowerCost + " G"
        );

        Button nextTurn = artofWar.factory.createImageButton(
                Gdx.graphics.getWidth() - 100, 30,
                new Texture(Gdx.files.internal("button/arrow_next.png")),
                new NextTurnCL(this)
        );
        nextTurn.setName("next turn");

        Button backToMenu = artofWar.factory.createImageButton(
                10, 900,
                new Texture(Gdx.files.internal("button/close_button.png")),
                new EndGameCL(this)
        );
        backToMenu.setName("back to menu");

        unitButtons.addActor(peasant);
        unitButtons.addActor(militia);
        unitButtons.addActor(knight);
        unitButtons.addActor(paladin);
        unitButtons.addActor(farm);
        unitButtons.addActor(tower);
        unitButtons.addActor(supertower);


        Label playerName = artofWar.factory.createLabel(0, 0, gamingProcess.getCurrentPlayer().name);
        playerName.setName("name");
        Image goldPic = new Image(new Texture(Gdx.files.internal("button/coin_2.png")));
        Label gold = artofWar.factory.createLabel(0, 0, gamingProcess.getCurrentPlayer().getGold() + "");
        gold.setName("gold");


        currentTurnInfo.addActor(playerName);
        currentTurnInfo.addActor(goldPic);
        currentTurnInfo.addActor(gold);


        controls.addActor(nextTurn);
        controls.addActor(backToMenu);
        controls.addActor(unitButtons);
        controls.addActor(currentTurnInfo);
        addActor(controls);
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        mapToRendererTransformator.getRenderer().setView(camera);
        mapToRendererTransformator.getRenderer().render();
        act();
        draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
        mapToRendererTransformator.getRenderer().dispose();
    }
}
