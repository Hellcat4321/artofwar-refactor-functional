package com.mygdx.game.controllers.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.spinner.IntSpinnerModel;
import com.kotcrab.vis.ui.widget.spinner.Spinner;
import com.mygdx.game.view.stages.MainGameStage;
import com.mygdx.game.view.stages.MenuStage;
import com.mygdx.game.model.maps.MapCell;

public class ActorsFactory {
    private MainGameStage gameStage;
    private MenuStage menuStage;
    public TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
    public TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
    public Label.LabelStyle labelStyle = new Label.LabelStyle();
    public ImageTextButton.ImageTextButtonStyle imageTextButtonStyle = new ImageTextButton.ImageTextButtonStyle();

    public ActorsFactory() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("bitmapfont/Amble-Regular-26.fnt"));
        textButtonStyle.font = font;
        textFieldStyle.font = font;
        labelStyle.font = font;
        imageTextButtonStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
    }

    public void setGameStage(MainGameStage gameStage) {
        this.gameStage = gameStage;
    }

    public void setMenuStage(MenuStage menuStage) {
        this.menuStage = menuStage;
    }

    public TiledMapActor createTiledMapActor(MapCell cell, ClickListener listener, int zIndex) {
        TiledMapActor actor = new TiledMapActor(cell, zIndex);
        actor.addListener(listener);
        return actor;
    }

    public Spinner createIntSpinner(int lowerBound, int upperBound, String name) {
        IntSpinnerModel model = new IntSpinnerModel(lowerBound, lowerBound, upperBound);
        return new Spinner(name, model);
    }

    public Button createTextButton(int x, int y, String text, ClickListener listener) {
        Button button = new TextButton(text, textButtonStyle);
        button.moveBy(x, y);
        button.addListener(listener);
        button.setBackground(new TextureRegionDrawable(new Texture(Gdx.files.internal("button/peasant_button.png"))));
        return button;
    }

    public TextField createTextField(String text) {
        TextField textField = new TextField(text, textFieldStyle);
        return textField;
    }

    public Label createLabel(int x, int y, String text) {
        Label label = new Label(text, labelStyle);
        label.moveBy(x, y);
        return label;
    }

    public Button createImageButton(int x, int y, Texture texture, ClickListener listener) {
        TextureRegionDrawable tr = new TextureRegionDrawable(texture);
        Button button = new ImageButton(tr);
        button.addListener(listener);
        button.setPosition(x, y);
        return button;
    }

    public Button createImageTextButton(int x, int y, Texture texture, ClickListener listener, String text) {
        Button button = createImageButton(x,y,texture, listener);
        button.row();
        button.add(new Label(text,labelStyle));
        return button;
    }
}
