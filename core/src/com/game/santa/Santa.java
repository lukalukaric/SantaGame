package com.game.santa;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

/**
 * Artwork from https://goodstuffnononsense.com/about/ and
 * https://goodstuffnononsense.com/hand-drawn-icons/space-icons/
 */
public class Santa extends ApplicationAdapter {
	private Texture presentImage;
	private Texture santaImage;
	private Texture snowmanImage;
	private Texture backgroundImage;
	private Sound presentSound;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle santa;
	private Array<Rectangle> presents;    // special LibGDX Array
	private Array<Rectangle> snowmans;
	private long lastPresentTime;
	private long lastSnowmanTime;
	private int presentsCollectedScore;
	private int santaHealth;    // starts with 100

	public BitmapFont font;

	// all values are set experimental
	private static final int SPEED = 400;    // pixels per second
	private static final int SPEED_PRESENT = 200; // pixels per second
	private static int SPEED_SNOWMAN = 100;    // pixels per second
	private static final long CREATE_PRESENT_TIME = 1000000000;    // ns
	private static long CREATE_SNOWMAN_TIME = 2000000000;    // ns

	@Override
	public void create() {
		font = new BitmapFont();
		font.getData().setScale(2);
		presentsCollectedScore = 0;
		santaHealth = 100;

		// default way to load a texture
		santaImage = new Texture(Gdx.files.internal("santa.png"));
		presentImage = new Texture(Gdx.files.internal("present.png"));
		snowmanImage = new Texture(Gdx.files.internal("snowman.png"));
		backgroundImage = new Texture(Gdx.files.internal("background.jpg"));

		presentSound = Gdx.audio.newSound(Gdx.files.internal("sound.wav"));

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();

		// create a Rectangle to logically represents the rocket
		santa = new Rectangle();
		santa.x = 5;
		santa.y = Gdx.graphics.getHeight() / 2f - santaImage.getHeight() / 2f;
		santa.width = santaImage.getWidth();
		santa.height = santaImage.getHeight();

		presents = new Array<Rectangle>();
		snowmans = new Array<Rectangle>();
		// add first astronaut and asteroid
		spawnPresents();
		spawnSnowmans();
	}

	/**
	 * Runs every frame.
	 */
	@Override
	public void render() {
		// clear screen
		Gdx.gl.glClearColor(0, 0, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// process user input
		if (Gdx.input.isTouched()) commandTouched();    // mouse or touch screen
		if (Gdx.input.isKeyPressed(Keys.UP)) commandMoveUp();
		if (Gdx.input.isKeyPressed(Keys.DOWN)) commandMoveDown();
		if (Gdx.input.isKeyPressed(Keys.LEFT)) commandMoveLeft();
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) commandMoveRight();
		if (Gdx.input.isKeyPressed(Keys.A)) commandMoveUpCorner();
		if (Gdx.input.isKeyPressed(Keys.S)) commandMoveDownCorner();
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) commandExitGame();

		// check if we need to create a new objects
		if (TimeUtils.nanoTime() - lastPresentTime > CREATE_PRESENT_TIME) spawnPresents();
		if (TimeUtils.nanoTime() - lastSnowmanTime > CREATE_SNOWMAN_TIME) spawnSnowmans();

		if (santaHealth > 0) {    // is game end?
			// move and remove any that are beneath the bottom edge of
			// the screen or that hit the rocket
			for (Iterator<Rectangle> it = snowmans.iterator(); it.hasNext(); ) {
				Rectangle snowman = it.next();
				snowman.x -= SPEED_SNOWMAN * Gdx.graphics.getDeltaTime();
				if (snowman.x + snowmanImage.getWidth() < 0) it.remove();
				if (snowman.overlaps(santa)) {
					presentSound.play();
					santaHealth--;
				}
			}

			for (Iterator<Rectangle> it = presents.iterator(); it.hasNext(); ) {
				Rectangle present = it.next();
				present.x -= SPEED_PRESENT * Gdx.graphics.getDeltaTime();
				if (present.x + presentImage.getWidth() < 0) it.remove();    // from screen
				if (present.overlaps(santa)) {
					presentSound.play();
					presentsCollectedScore++;
					if (presentsCollectedScore % 10 == 0) SPEED_SNOWMAN += 66; // speeds up
					if (presentsCollectedScore % 20 == 0) CREATE_SNOWMAN_TIME /= 2;  // spawn time speeds up too
					it.remove();    // smart Array enables remove from Array
				}
			}
		} else {    // health of santa is 0 or less
			batch.begin();
			{
				font.setColor(Color.RED);
				font.draw(batch, "The END", Gdx.graphics.getHeight() / 2f, Gdx.graphics.getHeight() / 2f);
			}
			batch.end();
		}

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera
		batch.setProjectionMatrix(camera.combined);
		// begin a new batch and draw the rocket, astronauts, asteroids
		batch.begin();
		{    // brackets added just for indent
			batch.draw(backgroundImage, 0, 0);
			for (Rectangle snowman : snowmans) {
				batch.draw(snowmanImage, snowman.x, snowman.y);
			}
			for (Rectangle present : presents) {
				batch.draw(presentImage, present.x, present.y);
			}
			batch.draw(santaImage, santa.x, santa.y);
			font.setColor(Color.GREEN);
			font.draw(batch, "" + presentsCollectedScore, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
			font.setColor(Color.RED);
			font.draw(batch, "" + santaHealth, 20, Gdx.graphics.getHeight() - 20);
		}
		batch.end();
	}

	/**
	 * Release all the native resources.
	 */
	@Override
	public void dispose() {
		presentImage.dispose();
		snowmanImage.dispose();
		santaImage.dispose();
		presentSound.dispose();
		batch.dispose();
		font.dispose();
	}

	private void spawnPresents() {
		Rectangle present = new Rectangle();
		present.x = Gdx.graphics.getWidth();
		present.y =MathUtils.random(0, Gdx.graphics.getHeight() - presentImage.getHeight());
		present.width = presentImage.getWidth();
		present.height = presentImage.getHeight();
		presents.add(present);
		lastPresentTime = TimeUtils.nanoTime();
	}

	private void spawnSnowmans() {
		Rectangle snowman = new Rectangle();
		snowman.x = Gdx.graphics.getWidth();
		snowman.y = MathUtils.random(0, Gdx.graphics.getHeight() - presentImage.getHeight());
		snowman.width = snowmanImage.getWidth();
		snowman.height = snowmanImage.getHeight();
		snowmans.add(snowman);
		lastSnowmanTime = TimeUtils.nanoTime();
	}

	private void commandMoveUp() {
		santa.y += SPEED * Gdx.graphics.getDeltaTime();
		if (santa.y > Gdx.graphics.getHeight() - santaImage.getHeight())
			santa.y = Gdx.graphics.getHeight() - santaImage.getHeight();
	}

	private void commandMoveDown() {
		santa.y -= SPEED * Gdx.graphics.getDeltaTime();
		if (santa.y < 0) santa.y = 0;
	}

	private void commandMoveLeft() {
		santa.x -= SPEED * Gdx.graphics.getDeltaTime();
		if (santa.x < 0) santa.x = 0;
	}

	private void commandMoveRight() {
		santa.x += SPEED * Gdx.graphics.getDeltaTime();
		if (santa.x > Gdx.graphics.getWidth() - santa.getWidth())
			santa.x = Gdx.graphics.getWidth() - santa.getWidth();
	}

	private void commandMoveUpCorner() {
		santa.y = Gdx.graphics.getHeight() - santaImage.getHeight();
	}

	private void commandMoveDownCorner() {
		santa.y = 0;
	}

	private void commandTouched() {
		Vector3 touchPos = new Vector3();
		touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(touchPos);
		santa.y = touchPos.y - santaImage.getHeight() / 2f;
		santa.x = touchPos.x - santaImage.getWidth() / 2f;
	}

	private void commandExitGame() {
		Gdx.app.exit();
	}
}
