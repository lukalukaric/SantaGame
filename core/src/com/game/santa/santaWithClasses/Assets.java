package com.game.santa.santaWithClasses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {
    public static Texture presentImage;
    public static Texture santaImage;
    public static Texture snowmanImage;
    public static Texture backgroundImage;
    public static Sound presentSound;
    public static BitmapFont font;
    public static Texture cookieImage;
    public static Sound cookieSound;

    public static void Load() {
        santaImage = new Texture(Gdx.files.internal("santa.png"));
        presentImage = new Texture(Gdx.files.internal("present.png"));
        snowmanImage = new Texture(Gdx.files.internal("snowman.png"));
        backgroundImage = new Texture(Gdx.files.internal("background.jpg"));
        presentSound = Gdx.audio.newSound(Gdx.files.internal("sound.wav"));
        font = new BitmapFont();
        font.getData().setScale(2);
        cookieImage = new Texture(Gdx.files.internal("cookieSanta.png"));
        cookieSound = Gdx.audio.newSound(Gdx.files.internal("cookieSound.mp3"));

    }

    public static void dispose() {
        presentImage.dispose();
        snowmanImage.dispose();
        santaImage.dispose();
        presentSound.dispose();
        font.dispose();
        cookieImage.dispose();
        cookieSound.dispose();
    }

}
