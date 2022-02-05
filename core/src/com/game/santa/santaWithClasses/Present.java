package com.game.santa.santaWithClasses;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class Present extends GameObjectDynamic implements Pool.Poolable {
    public boolean presentCollected;
    public String name = "present";
    public long lastTime;
    private static long CREATE_PRESENT_TIME = 1000000000;
    public static final Pool<Present> PRESENT_POOL = Pools.get(Present.class, 3);

    public Present(){}

    public Present(Texture image, int x, int y, int width, int height, int speed) {
        super(image);
        newRectangle();
        setRectangleX(x);
        setRectangleY(y);
        setRectangleWidth(width);
        setRectangleHeight(height);
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isTimeToCreateNew(){
        return (TimeUtils.nanoTime() - this.lastTime)  > CREATE_PRESENT_TIME;
    }

    @Override
    public void render(Batch render) {
        render.draw(getObjectImage(), getRectangleX(), getRectangleY(), getRectangle().width, getRectangle().height);
    }

    @Override
    public void updateScore(Score objectScore) {
        if(!presentCollected)
            objectScore.setPresentsCollectedScore(objectScore.getPresentsCollectedScore() + 1);
        presentCollected = true;
    }

    @Override
    public void reset() {
        PRESENT_POOL.clear();
    }
    public void free(){
    }
}
