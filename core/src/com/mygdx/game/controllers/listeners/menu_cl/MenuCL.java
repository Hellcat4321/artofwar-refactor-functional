package com.mygdx.game.controllers.listeners.menu_cl;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.view.stages.MenuStage;

public abstract class MenuCL extends ClickListener {
    protected final MenuStage stage;
    public MenuCL(MenuStage stage){
        this.stage = stage;
    }
}
