package com.game.santa.santaWithClasses;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

import java.security.PublicKey;

public abstract class GameObject {
    public Rectangle rectangle;
    public GameObject(){}

    public void newRectangle(){
        rectangle = new Rectangle();
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangleX(float x){ this.rectangle.x = x; }

    public void setRectangleY(float y){ this.rectangle.y = y; }

    public float getRectangleX(){ return this.rectangle.x; }

    public float getRectangleY(){ return this.rectangle.y; }

    public void setRectangleWidth(float x){ this.rectangle.width = x; }

    public void setRectangleHeight(float y){ this.rectangle.height = y; }


    public abstract void render(Batch render);

}
