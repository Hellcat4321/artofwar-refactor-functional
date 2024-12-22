package com.mygdx.game.controllers.listeners.menu_cl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ExitCL extends ClickListener {
    @Override
    public void clicked(InputEvent event, float x, float y) {
        Gdx.app.exit();
    }
}
