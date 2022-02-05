package com.game.santa.santaV2;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.santa.util.ViewportUtils;
import com.game.santa.util.debug.DebugCameraController;


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

	private Viewport viewport;
	private Viewport hudViewport;
	private ShapeRenderer renderer;

	private DebugCameraController debugCameraController;
	private boolean debug = false;

	// world units
	private static final float WORLD_WIDTH = 640f;
	private static final float WORLD_HEIGHT = 480f;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
		hudViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		renderer = new ShapeRenderer();

		debugCameraController = new DebugCameraController();
		debugCameraController.setStartPosition(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f);
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
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// create a Rectangle to logically represents the rocket
		santa = new Rectangle();
		santa.x = 5;
		//santa.y = Gdx.graphics.getHeight() / 2f - santaImage.getHeight() / 2f;
		santa.y = viewport.getWorldHeight() / 2f - santaImage.getHeight() / 2f;
		santa.width = santaImage.getWidth();
		santa.height = santaImage.getHeight();

		presents = new Array<Rectangle>();
		snowmans = new Array<Rectangle>();
		// add first astronaut and asteroid
		spawnPresents();
		spawnSnowmans();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		hudViewport.update(width, height, true);
		ViewportUtils.debugPixelsPerUnit(viewport);
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
		if(santaHealth > 0 ){
			// check if we need to create a new objects
			if (TimeUtils.nanoTime() - lastPresentTime > CREATE_PRESENT_TIME) spawnPresents();
			if (TimeUtils.nanoTime() - lastSnowmanTime > CREATE_SNOWMAN_TIME) spawnSnowmans();
		}


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
			//font.draw(batch, "" + presentsCollectedScore, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
			font.draw(batch, "" + presentsCollectedScore, viewport.getWorldWidth() - 50, viewport.getWorldHeight() - 20);
			font.setColor(Color.RED);
			//font.draw(batch, "" + santaHealth, 20, Gdx.graphics.getHeight() - 20);
			font.draw(batch, "" + santaHealth, 20, viewport.getWorldHeight() - 20);
		}
		batch.end();
		hudViewport.apply();
		batch.setProjectionMatrix(hudViewport.getCamera().combined);
		batch.begin();
		draw();
		batch.end();
		if(santaHealth <= 0)
		{    // health of santa is 0 or less
			batch.begin();
			{
				font.setColor(Color.RED);
				font.draw(batch, "The END", viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f);
			}
			batch.end();
		}
	}

	private void draw() {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		float worldWidth = viewport.getWorldWidth();
		float worldHeight = viewport.getWorldHeight();

		String screenSize = "Screen/Window size: " + screenWidth + " x " + screenHeight + " px";
		String worldSize = "World size: " + (int) worldWidth + " x " + (int) worldHeight + " world units";
		String oneWorldUnit = "One world unit: " + (screenWidth / worldWidth) + " x " + (screenHeight / worldHeight) + " px";


		font.draw(batch,
				screenSize,
				120f,
				hudViewport.getWorldHeight() - 20f);

		font.draw(batch,
				worldSize,
				120f,
				hudViewport.getWorldHeight() - 50f);

		font.draw(batch,
				oneWorldUnit,
				120f,
				hudViewport.getWorldHeight() - 80f);
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
		present.x = viewport.getWorldWidth();
		present.y =MathUtils.random(0, viewport.getWorldHeight() - presentImage.getHeight());
		present.width = presentImage.getWidth();
		present.height = presentImage.getHeight();
		presents.add(present);
		lastPresentTime = TimeUtils.nanoTime();
	}

	private void spawnSnowmans() {
		Rectangle snowman = new Rectangle();
		snowman.x = viewport.getWorldWidth();
		snowman.y = MathUtils.random(0, viewport.getWorldHeight() - presentImage.getHeight());
		snowman.width = snowmanImage.getWidth();
		snowman.height = snowmanImage.getHeight();
		snowmans.add(snowman);
		lastSnowmanTime = TimeUtils.nanoTime();
	}

	private void commandMoveUp() {
		santa.y += SPEED * Gdx.graphics.getDeltaTime();
		if (santa.y > viewport.getWorldHeight() - santaImage.getHeight())
			santa.y = viewport.getWorldHeight() - santaImage.getHeight();
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
		if (santa.x > viewport.getWorldWidth() - santa.getWidth())
			santa.x = viewport.getWorldWidth() - santa.getWidth();
	}

	private void commandMoveUpCorner() {
		santa.y = viewport.getWorldHeight() - santaImage.getHeight();
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
