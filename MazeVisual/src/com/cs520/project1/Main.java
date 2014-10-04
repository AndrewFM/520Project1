package com.cs520.project1;
import java.awt.Point;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cs520.project1.Grid.ObjectType;
import com.cs520.project1.UI.*;


public class Main implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private Grid environment;
	private UI userInterface;
	public Texture[] objectImages;
	
	public static final int NUMBER_OF_MAZES = 50;

	@Override
	public void create() {
		Point windowSize = new Point(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera = new OrthographicCamera(windowSize.x,windowSize.y);
		camera.translate(windowSize.x/2f, windowSize.y/2f);
		camera.update();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		objectImages = new Texture[ObjectType.values().length];
		objectImages[ObjectType.AGENT.ordinal()] = new Texture(Gdx.files.internal("data/agent.png"));
		objectImages[ObjectType.GOAL.ordinal()] = new Texture(Gdx.files.internal("data/goal.png"));
		objectImages[ObjectType.WALL.ordinal()] = new Texture(Gdx.files.internal("data/wall.png"));
		for(int i=0; i<ObjectType.values().length; i++) {
			if (i != ObjectType.VACANT.ordinal())
				objectImages[i].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		environment = new Grid(new Point(101,101), this);
		userInterface = new UI(environment);
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
	}
	
	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);		
		environment.render(camera, shapeRenderer, batch, new Point(10,30), new Point(800,800));
		userInterface.render(camera, shapeRenderer, batch, new Point(730,0), new Point(Gdx.graphics.getWidth()-730,Gdx.graphics.getHeight())); 
	}
	
	/**
	 * @return The tie-breaking method to use.
	 */
	public TieBreak getTieBreakingMode() {
		return userInterface.tbMode;
	}
	
	/**
	 * @return The pathfinding algorithm to use.
	 */
	public PathFind getPathFindingAlgorithm() {
		return userInterface.pfMode;
	}
	
	/**
	 * @return Whether to animate the pathfinding sequence or not.
	 */
	public AnimationSetting getAnimationSettings() {
		return userInterface.aniMode;
	}
	
	public void showBasicDialog(String message) {
		/*Skin skin = new Skin();
		Dialog dialog = new Dialog("...", skin) {
			protected void result (Object object) { }
		}.text(message).button("Okay",true).key(Keys.ENTER, true).show(stage);*/
		userInterface.setUIDebugString(message);
	}
	
	/**
	 * Add two integers together without them overflowing if one or both
	 * of them is equal to Integer.MAX_VALUE.
	 * @param val1 First value to add.
	 * @param val2 Second value to add.
	 * @return The sum of the two numbers.
	 */
	public static int addNoOverflow(int val1, int val2) {
		if (val1 == Integer.MAX_VALUE || val2 == Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return val1 + val2;
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
