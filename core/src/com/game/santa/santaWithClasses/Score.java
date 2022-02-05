package com.game.santa.santaWithClasses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Score extends GameObject {
    public int presentsCollectedScore;
    public int santaHealth; //Starts with 100

    public Score(){
        presentsCollectedScore = 0;
        santaHealth = 100;
    }

    public int getPresentsCollectedScore() {
        return presentsCollectedScore;
    }

    public void setPresentsCollectedScore(int presentsCollectedScore) {
        this.presentsCollectedScore = presentsCollectedScore;
    }

    public int getSantaHealth() {
        return santaHealth;
    }

    public void setSantaHealth(int santaHealth) {
        this.santaHealth = santaHealth;
    }

    @Override
    public void render(Batch render) {
        Assets.font.setColor(Color.YELLOW);
        Assets.font.draw(render, "Score: " + getPresentsCollectedScore(), Gdx.graphics.getWidth() - 130, Gdx.graphics.getHeight() - 10);
        Assets.font.setColor(Color.GREEN);
        Assets.font.draw(render, "Health: " + getSantaHealth(), 10, Gdx.graphics.getHeight() - 10);
    }
    public boolean isEnd() {
        return (santaHealth <=0);
    }

}
