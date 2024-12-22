package com.mygdx.game.controllers.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.mygdx.game.model.maps.MapCell;
import com.mygdx.game.utils.HexagonUtils;

import static com.mygdx.game.ProjectVariables.tileHeight;
import static com.mygdx.game.ProjectVariables.tileWidth;

public class TiledMapActor extends Actor {
    public TiledMapActor(MapCell cell, int zIndex){
        int x = cell.x;
        int y= cell.y;
        setWidth(tileWidth);
        setHeight(tileHeight);
        setPosition(
                HexagonUtils.countXLayout(x, y) * tileWidth,
                HexagonUtils.countYLayout(x, y) * tileHeight
        );
        setZIndex(zIndex);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable()!= Touchable.enabled) return null;
        if (!isVisible()) return null;
        float centerX=getOriginX()+getWidth()/2;
        float centerY=getOriginY()+getHeight()/2;
        float a =(float) Math.sqrt(3)*getWidth()/4;
        float b = getHeight()/2;
        if(Math.sqrt(Math.pow((double) x-centerX,2)/(a*a) + Math.pow((double) y-centerY,2)/(b*b))>1) return null;
        return this;
    }
}
