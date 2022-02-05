package com.game.santa.santaWithClasses;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

public class Snowman extends GameObjectDynamic implements Pool.Poolable {
    public static long isTimeToCreateNew;
    public float rotate;
    public String name = "snowman";
    public int speed;
    public long lastTime;
    public static long CREATE_SNOWMAN_TIME = 2000000000;
    public long cookieEffectStartTime;
    public static final Pool<Snowman> SNOWMAN_POOL = Pools.get(Snowman.class, 5);

    public static long getIsTimeToCreateNew() {
        return isTimeToCreateNew;
    }

    public static void setIsTimeToCreateNew(long isTimeToCreateNew) {
        Snowman.isTimeToCreateNew = isTimeToCreateNew;
    }

    public boolean isTimeToCreateNew(){
        return (TimeUtils.nanoTime() - this.lastTime)  > CREATE_SNOWMAN_TIME;
    }

    public boolean cookieEffectEnabled(){
        return (TimeUtils.nanoTime() - this.cookieEffectStartTime) > 2000000000;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Snowman(){}

    public Snowman(Texture image, int x, int y, int width, int height, int speed) {
        super(image);
        newRectangle();
        setRectangleX(x);
        setRectangleY(y);
        setRectangleWidth(width);
        setRectangleHeight(height);
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    @Override
    public void render(Batch render) {
        render.draw(getObjectImage(), getRectangleX(), getRectangleY(), getRectangle().width,getRectangle().height);
    }

    @Override
    public void updateScore(Score objectScore) {
        objectScore.setSantaHealth(objectScore.getSantaHealth() - 1);
    }

    @Override
    public void free() {
    }

    @Override
    public void reset() {
    }

}
