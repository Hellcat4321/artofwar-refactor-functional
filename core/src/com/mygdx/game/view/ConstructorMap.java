package com.mygdx.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.model.maps.GameMap;
import com.mygdx.game.model.maps.MapToRendererTransformator;
import com.mygdx.game.view.stages.MainGameStage;
import com.mygdx.game.model.maps.MapCreator;

import java.util.HashMap;
import java.util.Objects;

public class ConstructorMap implements Screen {
    final ArtofWar game;
    private final MainGameStage stage;
    private MapToRendererTransformator mapToRendererTransformator;
    private GameMap gameMap;
    private MapCreator mapCreator;
    private String labelWidth = "25", labelHeight = "25", labelSeed = "5", n4 = "1.5", n5 = "2", n6 = "0.1";
    private String textRandom = "Random: off";
    private java.util.Map<String, Integer> statInfo = new HashMap<>();
    private OrthographicCamera camera;
    private BitmapFont font;

    public ConstructorMap(final ArtofWar game) {
        this.game = game;
        gameMap = new GameMap(
                Integer.parseInt(labelWidth),
                Integer.parseInt(labelHeight),
                0,
                Integer.parseInt(labelSeed)
        );
        stage = null;
        mapCreator = stage.getMap().getMapCreator();
        Gdx.input.setInputProcessor(stage);

        CreateTypeCell();
        stage.addActor(CreateMenu());


        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1080, 720);

        font = new BitmapFont();
    }


    //buttons,settings
    public Table CreateMenu() {
        Table table = new Table();
        BitmapFont myFont = new BitmapFont(Gdx.files.internal("bitmapfont/Amble-Regular-26.fnt"));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = myFont;
        labelStyle.fontColor = Color.RED;

        TextField.TextFieldStyle labelStyle1 = new TextField.TextFieldStyle();
        labelStyle1.font = myFont;
        labelStyle1.fontColor = Color.RED;

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = myFont;
        textButtonStyle.fontColor = Color.WHITE;

        final Label labelX = new Label("Count X:", labelStyle);
        final Label labelY = new Label("Count Y:", labelStyle);
        final Label labelSeed = new Label("Seed:", labelStyle);
        final Label labelDegree = new Label("degree:", labelStyle);
        final Label labelOctaves = new Label("octaves:", labelStyle);
        final Label labelPersistence = new Label("persistence:", labelStyle);

        final TextField fieldX = new TextField(this.labelWidth, labelStyle1);
        final TextField fieldY = new TextField(this.labelHeight, labelStyle1);
        final TextField fieldSeed = new TextField(this.labelSeed, labelStyle1);
        final TextField fieldDegree = new TextField(this.n4, labelStyle1);
        final TextField fieldOctaves = new TextField(this.n5, labelStyle1);
        final TextField fieldPersistence = new TextField(this.n6, labelStyle1);

        final Button lightingCheckBox = new TextButton(textRandom, textButtonStyle);
        lightingCheckBox.setName(textRandom);
        lightingCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Objects.equals(lightingCheckBox.getName(), "Random: off")) {
                    textRandom = "Random: on";
                } else {
                    textRandom = "Random: off";
                }
                lightingCheckBox.setName(textRandom);
                UpdateWindow();
            }
        });

        Button button = new TextButton("Update map", textButtonStyle);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                labelWidth = fieldX.getText();
                labelHeight = fieldY.getText();
                if (Objects.equals(textRandom, "Random: on")) {
                    ConstructorMap.this.labelSeed = "-1";
                } else ConstructorMap.this.labelSeed = fieldSeed.getText();
                n4 = fieldDegree.getText();
                n5 = fieldOctaves.getText();
                n6 = fieldPersistence.getText();
                gameMap = new GameMap(
                        Integer.parseInt(labelWidth),//x
                        Integer.parseInt(labelHeight),//y
                        0,//mode
                        Long.parseLong(ConstructorMap.this.labelSeed)//seed
                );
                mapCreator = gameMap.getMapCreator();
                UpdateSettings();
                ConstructorMap.this.labelSeed = String.valueOf(mapCreator.getSeed());
                statInfo = mapCreator.getStatInfo();
                UpdateWindow();
            }
        });

        table.add(labelX);
        table.add(fieldX).width(50);
        table.row();
        table.add(labelY);
        table.add(fieldY).width(50);
        table.add(lightingCheckBox);
        table.row();
        table.add(labelSeed);
        table.add(fieldSeed).width(50);
        table.add(button).colspan(2);
        table.row();
        table.add(labelDegree);
        table.add(fieldDegree).width(50);
        table.add(labelOctaves);
        table.add(fieldOctaves).width(50);
        table.row();
        table.add(labelPersistence);
        table.add(fieldPersistence).width(50);

        return CreateTableStatInfo(table);
    }


    private void CreateTypeCell() {
        gameMap = new GameMap(Integer.parseInt(labelWidth), Integer.parseInt(labelHeight), 0, Integer.parseInt(labelSeed));
        mapCreator = gameMap.getMapCreator();
        statInfo = mapCreator.getStatInfo();
    }

    private Table CreateTableStatInfo(Table tableMenu) {
        BitmapFont myFont = new BitmapFont(Gdx.files.internal("bitmapfont/Amble-Regular-26.fnt"));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = myFont;
        labelStyle.fontColor = Color.RED;

        for (java.util.Map.Entry<String, Integer> entry : statInfo.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            tableMenu.row();
            tableMenu.add(new Label(key, new Label.LabelStyle(labelStyle)));
            tableMenu.add(new Label(value, new Label.LabelStyle(labelStyle)));
        }
        tableMenu.setBounds(0, 0, 350, stage.getHeight());
        return tableMenu;
    }

    private void UpdateWindow() {
        stage.getActors().clear();
        stage.addActor(CreateMenu());
    }

    private void UpdateSettings() {
        stage.setMap(gameMap);
        mapCreator.setDegree(Double.parseDouble(n4));
        mapCreator.setOctaves(Integer.parseInt(n5));
        mapCreator.setPersistence(Double.parseDouble(n6));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        camera.update();
        //game.batch.setProjectionMatrix(camera.combined);

        mapToRendererTransformator.getRenderer().setView(camera);
        mapToRendererTransformator.getRenderer().render();

        stage.act();
        stage.draw();
        if (Gdx.input.isTouched() && (Gdx.input.getDeltaX() != 0 || Gdx.input.getDeltaY() != 0)) {
            camera.position.set(camera.position.x - Gdx.input.getDeltaX(), camera.position.y + Gdx.input.getDeltaY(), 0);
//            System.out.println(touchPos.x+" "+touchPos.y);
            //mapToRendererTransformator.updateLayer();
            Group g = stage.getMovableActors();
            g.moveBy(Gdx.input.getDeltaX(), -Gdx.input.getDeltaY());
        }
        game.batch.begin();
        font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond() + " " + Gdx.input.getX() + " " + Gdx.input.getY(), 10, 20);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        mapToRendererTransformator.getRenderer().dispose();
        stage.dispose();
        game.dispose();
    }
}