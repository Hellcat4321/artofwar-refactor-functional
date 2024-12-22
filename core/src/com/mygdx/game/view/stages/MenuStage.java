package com.mygdx.game.view.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.mygdx.game.ProjectVariables;
import com.mygdx.game.controllers.listeners.menu_cl.*;
import com.mygdx.game.view.ArtofWar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuStage extends Stage implements Screen {
    private final ArtofWar artofWar;
    private Group main;
    private Group preGame;
    private Group settings;
    private Texture backgroundImage = new Texture(Gdx.files.internal("menuBackground.jpg"));
    private Spinner
            playersNumber,
            mapWidth,
            mapHeight;

    public MenuStage(ArtofWar artofWar) {
        this.artofWar = artofWar;
        VisUI.load();
        ((OrthographicCamera) getCamera()).setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        createMainGroup();
        createPreGame();
        toMain();
    }

    public void createMainGroup() {
        main = new Group();

        Button play = artofWar.factory.createImageButton(100, 300, new Texture(Gdx.files.internal("button/play_button.png")), new PreGameCL(this));


        Button exit = artofWar.factory.createImageButton(100, 230, new Texture(Gdx.files.internal("button/quit_button.png")), new ExitCL());

        main.addActor(play);
        main.addActor(exit);
        main.debug();
    }

    public void toMain() {
        setRoot(main);
    }

    public void createPlayerTable() {
        int n;
        try {
            n = Integer.parseInt(playersNumber.getTextField().getText());
        } catch (NumberFormatException e) {
            return;
        }
        if (n < 2 || n > 10) return;

        Table table = new Table();
        table.setName("names table");
        for (int i = 0; i < n; ++i) {
            table.add(artofWar.factory.createTextField("player" + (i + 1)));
            table.row();
        }
        TextureRegion tr = new TextureRegion(new Texture("playersTableBackground.jpg"));
        tr.setRegionHeight((int) table.getHeight());
        tr.setRegionWidth((int) table.getWidth());
        table.setBackground(new TextureRegionDrawable(tr));
        table.pack();
        table.setPosition(500, 100);
        preGame.removeActor(preGame.findActor("names table"));
        preGame.addActor(table);
    }

    private void createPreGame() {
        preGame = new Group();

        Button startGame = artofWar.factory.createImageButton(250, 50, new Texture(Gdx.files.internal("button/start_button.png")), new StartGameCL(this));

        playersNumber = artofWar.factory.createIntSpinner(2, 10, "Players");
        playersNumber.addListener(new FillCL(this));
        playersNumber.moveBy(150, 150);

        mapWidth = artofWar.factory.createIntSpinner(10, 125, "Width of map");
        mapWidth.moveBy(150, 250);

        mapHeight = artofWar.factory.createIntSpinner(10, 125, "Height of map");
        mapHeight.moveBy(150, 350);

        preGame.addActor(playersNumber);
        preGame.addActor(mapWidth);
        preGame.addActor(mapHeight);
        preGame.addActor(startGame);

        createPlayerTable();

        Button back = artofWar.factory.createImageButton(50, 50, new Texture(Gdx.files.internal("button/back_button.png")), new BackCL(this));
        preGame.addActor(back);
    }

    public void toPreGame() {
        setRoot(preGame);
    }

    public void startGame() throws SQLException {
        int width, height;
        try {
            width = Integer.parseInt(mapWidth.getTextField().getText());
            height = Integer.parseInt(mapHeight.getTextField().getText());
        } catch (NumberFormatException e) {
            return;
        }
        if (width < 10 || width > 125 || height < 10 || height > 125) return;

        List<String> players = new ArrayList<>();
        Table table = preGame.findActor("names table");
        for (int i = 0; i < table.getChildren().size; ++i) {
            String player = ((TextField) table.getChildren().get(i)).getText();
            if (player.isEmpty()) player = "player" + (i + 1);
            players.add(player);
        }

        artofWar.newGame(width, height, players);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        getCamera().update();
        getBatch().begin();
        getBatch().draw(backgroundImage, 0, 0);
        getBatch().end();
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
}
