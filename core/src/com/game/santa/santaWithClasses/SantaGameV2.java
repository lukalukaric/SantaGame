package com.game.santa.santaWithClasses;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.santa.util.ViewportUtils;
import com.game.santa.util.debug.DebugCameraController;
import com.game.santa.util.debug.MemoryInfo;

/**
 * Artwork from https://goodstuffnononsense.com/about/ and
 * https://goodstuffnononsense.com/hand-drawn-icons/space-icons/
 */
public class SantaGameV2 extends ApplicationAdapter {
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Present present;
	private Santa santa;
	private Snowman snowman;
	private Score score;
	private Array<GameObjectDynamic> dynamicArray;
	private boolean gamePaused;
	private boolean gameOver;

	// debug
	private DebugCameraController debugCameraController;
	private MemoryInfo memoryInfo;
	private boolean debug = false;

	private ShapeRenderer shapeRenderer;
	public Viewport viewport;

	public ParticleEffect snowTrace;
	public ParticleEffect snowTrace2;
	public ParticleEffect scateTrace;
	public ParticleEffect scateTrace2;


	private void initParticleEffects()
	{
		snowTrace = new ParticleEffect();
		snowTrace.load(Gdx.files.internal("snow"), Gdx.files.internal(""));
		snowTrace.setPosition( (float)(Gdx.graphics.getWidth() * 0.35), (float)(Gdx.graphics.getHeight() * 1.2));

		snowTrace2 = new ParticleEffect();
		snowTrace2.load(Gdx.files.internal("snow"), Gdx.files.internal(""));
		snowTrace2.setPosition( (float)(Gdx.graphics.getWidth() * 0.85), (float)(Gdx.graphics.getHeight() * 1.2));

		scateTrace = new ParticleEffect();
		scateTrace.load(Gdx.files.internal("scate"), Gdx.files.internal(""));
		scateTrace.setPosition(5,5);

		scateTrace2 = new ParticleEffect();
		scateTrace2.load(Gdx.files.internal("scate"), Gdx.files.internal(""));
		scateTrace2.setPosition(5,5);

	}
	private void update(float delta)
	{
		snowTrace.update(delta);
		if(snowTrace.isComplete())
			snowTrace.reset();

		snowTrace2.update(delta);
		if(snowTrace2.isComplete())
			snowTrace2.reset();

		scateTrace.update(delta);
		if(scateTrace.isComplete())
			scateTrace.reset();

		scateTrace2.update(delta);
		if(scateTrace2.isComplete())
			scateTrace2.reset();
	}

	@Override
	public void create() {
		Assets.Load();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();

		// debug
		debugCameraController = new DebugCameraController();
		debugCameraController.setStartPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
		memoryInfo = new MemoryInfo(500);

		shapeRenderer = new ShapeRenderer();
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);

		santa = new Santa(Assets.santaImage, 5,Gdx.graphics.getHeight() / 2f - Assets.santaImage.getHeight() /2f,Assets.santaImage.getWidth(),Assets.santaImage.getHeight(), 300);

		dynamicArray = new Array<GameObjectDynamic>();

		score = new Score();

		spawnPresents();
		spawnSnowmans();

		gamePaused = false;
		gameOver = false;

		initParticleEffects();
	}

	/**
	 * Runs every frame.
	 */
	@Override
	public void render() {
		// clear screen
		Gdx.gl.glClearColor(0, 0, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) debug = !debug;

		if (debug) {
			debugCameraController.handleDebugInput(Gdx.graphics.getDeltaTime());
			memoryInfo.update();
		}


		if (Gdx.input.isKeyPressed(Keys.P)){
			gamePaused = !gamePaused;
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if(gamePaused)
		{
			batch.begin();
			batch.draw(Assets.backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Assets.font.setColor(Color.RED);
			Assets.font.draw(batch, "GAME IS PAUSED ", Gdx.graphics.getWidth() /2f - 130, Gdx.graphics.getHeight() / 2f + 50);
			batch.end();
		}
		else if(gameOver){
			batch.begin();
			batch.draw(Assets.backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			Assets.font.setColor(Color.RED);
			Assets.font.draw(batch, "GAME OVER", Gdx.graphics.getWidth() /2f - 130, Gdx.graphics.getHeight() / 2f + 50);
			Assets.font.setColor(Color.LIME);
			Assets.font.draw(batch, "Your score was: " + score.getPresentsCollectedScore(), Gdx.graphics.getWidth() /2f - 150, Gdx.graphics.getHeight() / 2f);
			Assets.font.setColor(Color.FOREST);
			Assets.font.draw(batch, "Press R to restart the game", Gdx.graphics.getWidth() /2f - 200, Gdx.graphics.getHeight() / 2f - 50);
			batch.end();
			if (Gdx.input.isKeyPressed(Keys.R)) {
				resetGame();
				gameOver = false;
			}
		}
		else {
			update(Gdx.graphics.getDeltaTime());
			// process user input
			if (Gdx.input.isKeyPressed(Keys.UP)) santa.commandMoveUp();
			if (Gdx.input.isKeyPressed(Keys.DOWN)) santa.commandMoveDown();
			if (Gdx.input.isKeyPressed(Keys.LEFT)) santa.commandMoveLeft();
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) santa.commandMoveRight();
			if (Gdx.input.isKeyPressed(Keys.ESCAPE)) commandExitGame();

			if (Gdx.input.isKeyPressed(Keys.R)) {
				resetGame();
			}

			if(snowman.isTimeToCreateNew())
				spawnSnowmans();

			if(present.isTimeToCreateNew()){
				if(score.getPresentsCollectedScore()%15 != 0 || score.getPresentsCollectedScore() == 0)
					spawnPresents();
				else{
					spawnCookie();
					snowman.CREATE_SNOWMAN_TIME /= 2;
				}
			}

			if(!score.isEnd()){
				for (Iterator<GameObjectDynamic> it = dynamicArray.iterator(); it.hasNext(); ){
					GameObjectDynamic ob = it.next();
					ob.setRectangleX(ob.getRectangleX() - ob.getSpeed() * Gdx.graphics.getDeltaTime());
					if (ob.getRectangleX() + ob.getObjectImage().getWidth() < 0) it.remove();
					if(ob.getRectangle().overlaps(santa.getRectangle())){
						ob.updateScore(score);
						if(ob.getName().equals("present")){
							it.remove();
							Assets.presentSound.play();
						}
						else if(ob.getName().equals("cookie")){
							it.remove();
							Assets.cookieSound.play();
							score.setSantaHealth(score.getSantaHealth() + 50);
							ob.free();
						}
					}
				}
			}else{
				gameOver = true;
				snowman.CREATE_SNOWMAN_TIME = 2000000000;
			}
			camera.update();
			batch.setProjectionMatrix(camera.combined);

			scateTrace.setPosition(santa.getRectangleX() + Assets.santaImage.getWidth() - 10, santa.getRectangleY() + 5);
			scateTrace2.setPosition(santa.getRectangleX() +10, santa.getRectangleY()+20);

			batch.begin();
			batch.draw(Assets.backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			santa.render(batch);
			score.render(batch);


			snowTrace.draw(batch);
			snowTrace2.draw(batch);
			scateTrace.draw(batch);
			scateTrace2.draw(batch);

			for (GameObjectDynamic ob : dynamicArray){
				ob.render(batch);
			}

			batch.end();
		}

		if (debug) {
			debugCameraController.applyTo(camera);
			batch.begin();
			{
				// the average number of frames per second
				GlyphLayout layout = new GlyphLayout(Assets.font, "FPS:" + Gdx.graphics.getFramesPerSecond());
				Assets.font.setColor(Color.BLUE);
				Assets.font.draw(batch, layout, Gdx.graphics.getWidth() - layout.width, Gdx.graphics.getHeight() - 50);

				// number of rendering calls, ever; will not be reset unless set manually
				Assets.font.setColor(Color.BLUE);
				Assets.font.draw(batch, "RC:" + batch.totalRenderCalls, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() - 20);

				memoryInfo.render(batch, Assets.font);
			}
			batch.end();

			batch.totalRenderCalls = 0;
			ViewportUtils.drawGrid(viewport, shapeRenderer, 18);

			// print rectangles
			shapeRenderer.setProjectionMatrix(camera.combined);
			// https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
			{
				shapeRenderer.setColor(Color.RED);
				for (GameObjectDynamic a : dynamicArray)
					shapeRenderer.rect(a.getRectangleX(), a.getRectangleY(), a.getRectangle().getWidth(), a.getRectangle().getHeight());
				shapeRenderer.setColor(Color.BLUE);
				shapeRenderer.rect(santa.getRectangleX(), santa.getRectangleY(), santa.getRectangle().getWidth(), santa.getRectangle().getHeight());
			}
			shapeRenderer.end();
		}

	}

	private void spawnPresents() {
		present = new Present(Assets.presentImage,Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - Assets.presentImage.getHeight()), Assets.presentImage.getWidth(), Assets.presentImage.getHeight(), 250);
		present.setName("present");
		present.lastTime = TimeUtils.nanoTime();
		dynamicArray.add(present);
	}

	private void spawnSnowmans() {
		snowman = new Snowman(Assets.snowmanImage, Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - Assets.snowmanImage.getHeight()), Assets.snowmanImage.getWidth(), Assets.snowmanImage.getHeight(), 300);
		snowman.setName("snowman");
		snowman.lastTime = TimeUtils.nanoTime();
		dynamicArray.add(snowman);
	}

	private void commandExitGame() {
		Gdx.app.exit();
	}

	private void spawnCookie() {
		present = new Present(Assets.cookieImage,Gdx.graphics.getWidth(), MathUtils.random(0, Gdx.graphics.getHeight() - Assets.cookieImage.getHeight()), Assets.cookieImage.getWidth(), Assets.cookieImage.getHeight(), 250);
		present.setName("cookie");
		present.lastTime = TimeUtils.nanoTime();
		Present.PRESENT_POOL.obtain();
		dynamicArray.add(present);
	}

	private void resetGame(){
		for(Iterator<GameObjectDynamic> it = dynamicArray.iterator(); it.hasNext();) {
			GameObjectDynamic obj = it.next();
			it.remove();
		}
		score.setSantaHealth(100);
		score.setPresentsCollectedScore(0);
		santa.setRectangleX(5);
		santa.setRectangleY(Gdx.graphics.getHeight() / 2f - Assets.santaImage.getHeight() /2f);
		spawnSnowmans();
		spawnPresents();
	}

	@Override
	public void dispose() {
		super.dispose();
		snowTrace.dispose();
		snowTrace2.dispose();
		scateTrace.dispose();
		scateTrace2.dispose();
	}
}
