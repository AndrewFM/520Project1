package com.cs520.project1;
import java.awt.Point;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class Main implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private Grid environment;
	private UI userInterface;

	@Override
	public void create() {
		Point windowSize = new Point(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera = new OrthographicCamera(windowSize.x,windowSize.y);
		camera.translate(windowSize.x/2f, windowSize.y/2f);
		camera.update();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		userInterface = new UI();
		environment = new Grid(new Point(101,101), userInterface);
		environment.generateEnviroFromFile("mazes/Maze1.txt");
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
