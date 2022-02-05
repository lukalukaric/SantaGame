package com.game.santa.santaWithClasses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Santa extends GameObjectDynamic {

    public Santa(Texture image, float x, float y, int width, int height, int speed) {
        super(image);
        newRectangle();
        setRectangleX(x);
        setRectangleY(y);
        setRectangleWidth(width);
        setRectangleHeight(height);
        this.speed = speed;
    }
    public void commandMoveUp() {
        setRectangleY(getRectangleY() + (float)speed * Gdx.graphics.getDeltaTime());
        if (getRectangleY() > Gdx.graphics.getHeight() - Assets.santaImage.getHeight())
            setRectangleY(Gdx.graphics.getHeight() - Assets.santaImage.getHeight());
    }

    public void commandMoveDown() {
        setRectangleY(getRectangleY() - (float)speed * Gdx.graphics.getDeltaTime());
        if (getRectangleY() < 0)
            setRectangleY(0);
    }

    public void commandMoveLeft() {
        setRectangleX(getRectangleX() - (float)speed * Gdx.graphics.getDeltaTime());
        if (getRectangleX() < 0) setRectangleX(0);
    }

    public void commandMoveRight() {
        setRectangleX(getRectangleX() + (float)speed * Gdx.graphics.getDeltaTime());
        if (getRectangleX() > Gdx.graphics.getWidth() - Assets.snowmanImage.getWidth())
            setRectangleX(Gdx.graphics.getWidth() - Assets.snowmanImage.getWidth());
    }

    @Override
    public void render(Batch render) {
        render.draw(getObjectImage(), getRectangleX(), getRectangleY(), Assets.santaImage.getWidth(), Assets.santaImage.getHeight());
    }

    @Override
    public void updateScore(Score objectScore) {

    }

    @Override
    public void free() {
    }
}
