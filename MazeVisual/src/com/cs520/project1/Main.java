package com.cs520.project1;
import java.awt.Point;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cs520.project1.UI.PathFind;
import com.cs520.project1.UI.TieBreak;


public class Main implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private Grid environment;
	private UI userInterface;
	private Stage stage;
	
	public static final int NUMBER_OF_MAZES = 50;

	@Override
	public void create() {
		Point windowSize = new Point(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera = new OrthographicCamera(windowSize.x,windowSize.y);
		camera.translate(windowSize.x/2f, windowSize.y/2f);
		camera.update();
		stage = new Stage();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		environment = new Grid(new Point(101,101), this);
		userInterface = new UI(environment, this);
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapeRenderer.dispose();
		stage.dispose();
	}
	
	@Override
	public void render() {		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);		
		environment.render(camera, shapeRenderer, batch, new Point(10,30), new Point(800,800));
		userInterface.render(camera, shapeRenderer, batch, new Point(730,0), new Point(Gdx.graphics.getWidth()-730,Gdx.graphics.getHeight())); 
		stage.draw();
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
	
	//TODO
	public void showBasicDialog(String message) {
		/*Skin skin = new Skin();
		Dialog dialog = new Dialog("...", skin) {
			protected void result (Object object) { }
		}.text(message).button("Okay",true).key(Keys.ENTER, true).show(stage);*/
		System.out.println(message);
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
