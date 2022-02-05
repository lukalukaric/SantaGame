package com.game.santa.santaWithClasses;

import com.badlogic.gdx.graphics.Texture;

public abstract class GameObjectDynamic extends GameObject {
    public String name;
    public int speed;
    public Texture objectImage;
    public  GameObjectDynamic(){
    }

    public GameObjectDynamic(Texture objectImage) {
        this.objectImage = objectImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public Texture getObjectImage() {
        return objectImage;
    }

    public abstract void updateScore(Score objectScore);

    public abstract void free();
}
