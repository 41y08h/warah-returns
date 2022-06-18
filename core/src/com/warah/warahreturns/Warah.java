package com.warah.warahreturns;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Warah extends com.badlogic.gdx.Game {

	private SpriteBatch batch;
	private static GlyphLayout glyphLayout;
	private	Random random;
	private Rectangle powerUpRectangle;

	private	Texture backgroundImage;
	private	Texture pigImage;
	private	Texture bhalaImage;
	private Texture powerUpImage;

	private	Music mainMusic;
	private	Music pigLaughMusic;
	private	Music killMusic;
	private	Music bhalaThrowMusic;

	private	Boolean isPigLaughing;
	private Boolean showPowerUp;

	private	Integer blinkTextCount;
	private	Integer gameState;
	private	Integer pigCount;
	private	Integer kills;
	private	Integer missed;
	private	Integer pigSpeed;
	private	Integer speedIncrementCount;
	private Integer deviceWidth;
	private Integer deviceHeight;
	private Integer maxMissed;
	private Integer powerUpCount;
	private Integer powerUpShowDuraionCount;
	
	private float bhalaWidth;
	private float bhalaHeight;
	private float pigWidth;
	private float pigHeight;
	private float powerUpWidth;
	private float powerUpHeight;
	
	private float xPositionPowerUp;
	private float yPositionPowerUp;

	// Font Setup
	private FreeTypeFontGenerator generator;
	private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
	private BitmapFont logoText;
	private BitmapFont homeScreenBottomText;
	private BitmapFont killsNmissedText;

	// Pig Setup
	private ArrayList<Integer> pigXs = new ArrayList<>();
	private ArrayList<Integer> pigYs = new ArrayList<>();
	private ArrayList<Rectangle> pigRectangles = new ArrayList<>();

	// Bhala Setup
	private ArrayList<Integer> bhalaXs = new ArrayList<>();
	private ArrayList<Integer> bhalaYs = new ArrayList<>();
	private ArrayList<Rectangle> bhalaRectangles = new ArrayList<>();

	private void addPig() {
		// Adds pig position to the array lists of positions of pig.

		float yPosition = random.nextFloat() * (deviceHeight-50);
		pigYs.add((int) yPosition);
		pigXs.add(-(pigImage.getWidth()));
	}

	private void throwBhala(Integer xPosition) {
		// Adds bhala position to the array lists of positions of pig.

		bhalaXs.clear();
		bhalaYs.clear();
		bhalaRectangles.clear();

		bhalaThrowMusic.play();
		bhalaXs.add(xPosition);
		bhalaYs.add(-(deviceHeight));

	}

	private void showPowerUp() {
		xPositionPowerUp = random.nextFloat() * (deviceWidth-50);
		yPositionPowerUp = random.nextFloat() * (deviceHeight-50);
		showPowerUp = true;
		powerUpShowDuraionCount = 0;
	}

	private void deletePowerUp() {
		showPowerUp = false;
	}

	@Override
	public void create () {

		batch = new SpriteBatch();
		glyphLayout = new GlyphLayout();
		random = new Random();
		powerUpRectangle = new Rectangle();

		backgroundImage = new Texture("img/background.png");
		pigImage = new Texture("img/lilpig.png");
		bhalaImage = new Texture("img/bhala.png");
		powerUpImage = new Texture("img/powerup.png");

		mainMusic = Gdx.audio.newMusic(Gdx.files.internal("snd/mainMusic.mp3"));
		killMusic = Gdx.audio.newMusic(Gdx.files.internal("snd/kill.mp3"));
		pigLaughMusic = Gdx.audio.newMusic(Gdx.files.internal("snd/pigLaugh.mp3"));
		bhalaThrowMusic = Gdx.audio.newMusic(Gdx.files.internal("snd/bhalaThrow.mp3"));

		pigLaughMusic.setLooping(true);
		pigLaughMusic.setVolume(1.0f);
		bhalaThrowMusic.setVolume(0.35f);
		killMusic.setVolume(0.7f);

		mainMusic.setLooping(true);
		mainMusic.setVolume(0.6f);
		mainMusic.play();

		generator = new FreeTypeFontGenerator(Gdx.files.internal("azo.ttf"));
		parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color.add(Color.WHITE);
		parameter.size = (int) (35 * Gdx.graphics.getDensity());
		logoText = generator.generateFont(parameter);
        parameter.size = (int) (14 * Gdx.graphics.getDensity());
		homeScreenBottomText = generator.generateFont(parameter);
        parameter.size = (int) (25 * Gdx.graphics.getDensity());
		killsNmissedText = generator.generateFont(parameter);

		isPigLaughing = false;
		showPowerUp = false;

		blinkTextCount = 0;
		gameState = 0;
		pigCount = 0;
		kills = 0;
		missed = 0;
		pigSpeed = 8;
		speedIncrementCount = 0;
		maxMissed = 20;
		powerUpCount = 0;
		powerUpShowDuraionCount = 0;

		deviceWidth = Gdx.graphics.getWidth();
		deviceHeight = Gdx.graphics.getHeight();
		
		bhalaWidth = bhalaImage.getWidth() * 12/100 * Gdx.graphics.getDensity();
        bhalaHeight = bhalaImage.getHeight() * 12/100 * Gdx.graphics.getDensity();
        pigWidth = pigImage.getWidth() * 25/100 * Gdx.graphics.getDensity();
        pigHeight = pigImage.getHeight() * 25/100 * Gdx.graphics.getDensity();
        powerUpWidth = powerUpImage.getWidth() * 12/100 * Gdx.graphics.getDensity();
        powerUpHeight = powerUpImage.getHeight() * 12/100 * Gdx.graphics.getDensity();

	}

	@Override
	public void render () {
		batch.begin();

		// Game State
		/*
		0 = waiting to start
		1 = is Live
		2 = game over
		 */

		// Background image
		batch.draw(backgroundImage, 0, 0, deviceWidth, deviceHeight);

		if (gameState==0) {
			// *****Waiting to start*****

			if(Gdx.input.justTouched()) {
				gameState = 1;
			}

			// Logo Text
			glyphLayout.setText(logoText, "Warah Returns");
			logoText.draw(batch, glyphLayout, deviceWidth/2 - (glyphLayout.width / 2), deviceHeight/2);

			// Blinking bottom Guideline
			if (blinkTextCount > 100 && blinkTextCount < 130) {
				blinkTextCount++;
			} else if (blinkTextCount > 130) {
				blinkTextCount = 0;
			} else {
				glyphLayout.setText(homeScreenBottomText, "TOUCH ANYWHERE TO START");
				homeScreenBottomText.draw(batch, glyphLayout, deviceWidth/2 - (glyphLayout.width / 2), 60);
				blinkTextCount++;
			}

		} else if (gameState==1){
			// *****Game Live*****

			// Speed Increment
			if (speedIncrementCount%5==0 && speedIncrementCount>=5) {
				pigSpeed += 2;
				speedIncrementCount = 0;
			}

			// Adding Pig at certain interval
			if (pigCount<100) {
				pigCount++;
			} else {
				pigCount = 0;
				addPig();
			}

			// Giving PowerUp
			if (powerUpCount<1800) {
				powerUpCount++;
			} else {
				showPowerUp();
				powerUpCount = 0;
			}


			// Showing Bhala
			bhalaRectangles.clear();
			for (int i = 0; i < bhalaXs.size(); i++) {
				batch.draw(bhalaImage, bhalaXs.get(i), bhalaYs.get(i), bhalaWidth, bhalaHeight);
				bhalaYs.set(i, bhalaYs.get(i) + 80);
				bhalaRectangles.add(new Rectangle(bhalaXs.get(i), bhalaYs.get(i), bhalaWidth, bhalaHeight));
			}

			// Showing Pig
			pigRectangles.clear();
			for (int i = 0; i < pigXs.size(); i++) {
				batch.draw(pigImage, pigXs.get(i), pigYs.get(i), pigWidth, pigHeight);
				pigXs.set(i, pigXs.get(i) + pigSpeed);
				pigRectangles.add(new Rectangle(pigXs.get(i), pigYs.get(i), pigWidth, pigHeight));
			}

			// Showing PowerUp
			if (powerUpShowDuraionCount<500) {
				if (showPowerUp) {
					batch.draw(powerUpImage, xPositionPowerUp, yPositionPowerUp, powerUpWidth, powerUpHeight);
					powerUpRectangle.set(new Rectangle(xPositionPowerUp, yPositionPowerUp, powerUpWidth, powerUpHeight));
					powerUpCount = 0;
				}
				powerUpShowDuraionCount++;
			} else {
				deletePowerUp();
				powerUpShowDuraionCount = 0;
			}


			// Counting Missed Pigs
			for (int i=0; i<pigXs.size(); i++) {
				if (pigXs.get(i) > deviceWidth/2 * 2) {
					missed++;
					pigXs.remove(i);
					pigYs.remove(i);
					pigRectangles.remove(i);
				}
			}

			// Throwing Bhala
			if(Gdx.input.justTouched()) {
				throwBhala(Gdx.input.getX());
			}

			// Killing Pig/ Checking overlapping
			for (int i=0;i<pigRectangles.size(); i++) {

				for (int b=0; b<bhalaRectangles.size(); b++) {
					if (Intersector.overlaps(pigRectangles.get(i), bhalaRectangles.get(b))) {
						kills++;
						speedIncrementCount++;

						killMusic.play();
						pigXs.remove(i);
						pigYs.remove(i);
						pigRectangles.remove(i);

						break;
					}
				}
			}

			// Getting PowerUp/ Checking overlapping
			if (showPowerUp) {
				for (int i = 0; i < bhalaRectangles.size(); i++) {
					if (Intersector.overlaps(bhalaRectangles.get(i), powerUpRectangle)) {
						if (random.nextBoolean()) {
						    kills++;
                        } else {
						    pigSpeed -= 2;
                        }

						killMusic.play();
						deletePowerUp();

						break;
					}
				}
			}


			// Showing scores
			glyphLayout.setText(killsNmissedText, "KILLS:" + kills.toString() + "    MISSED:" + missed.toString());
			killsNmissedText.draw(batch, glyphLayout, 60, 100);

			// Game Over Condition
			if (missed>=maxMissed) {
				gameState=2;
			}

		} else if (gameState==2) {
			// *****Game Over*****



			// Game Over Logo
			glyphLayout.setText(logoText, "Game Over");
			logoText.draw(batch, glyphLayout, deviceWidth/2 - (glyphLayout.width / 2), deviceHeight/2);

			// Showing End Scores
			glyphLayout.setText(homeScreenBottomText, "KILLS:"+kills.toString()+"    MISSED:"+missed.toString());
			homeScreenBottomText.draw(batch, glyphLayout, deviceWidth/2 - (glyphLayout.width / 2), 60);

			// Making Pig Laugh
			if (!isPigLaughing) {
				// Stoping Main Music
				mainMusic.stop();

				pigLaughMusic.play();
				isPigLaughing = true;
			}

			// Play again on touching screen
			if(Gdx.input.justTouched()) {
				gameState = 1;
				missed = 0;
				kills = 0;
				pigSpeed = 8;
				powerUpCount = 0;
				powerUpShowDuraionCount = 0;

				if (pigLaughMusic.isPlaying()) {
					pigLaughMusic.stop();
					mainMusic.play();
				}
				isPigLaughing = false;

			}
		}

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();

		generator.dispose();
		mainMusic.dispose();
		pigLaughMusic.dispose();
		bhalaThrowMusic.dispose();
	}
}
