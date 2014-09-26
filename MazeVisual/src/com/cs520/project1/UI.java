package com.cs520.project1;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * User Interface Control
 */
public class UI {

	public static enum PathFind {
		ForwardAStar, BackwardAStar, AdaptiveAStar 
	};
	
	public static enum TieBreak {
		Smaller, Larger
	};
	
	/**
	 * An interface button that can be clicked on.
	 */
	private class Button {
		public String label;	// Text label.
		public Point pixelPos;	// Position to be drawn at.
		public Point pixelSize; // Size to be drawn at.
		public boolean active;  // Whether button is active or not.
		
		public Button(String label, boolean active) {
			this.label = label;
			this.active = active;
			pixelPos = new Point(0,0);
			pixelSize = new Point(0,0);
		}
	}
	
	private BitmapFont font;
	private PathFind pfMode; 	// Pathfinding Algorithm to use.
	private TieBreak tbMode; 	// Tie-breaking method to use.
	private Button[] pfButtons; // Group of buttons to pick pathfinding algo.
	private Button[] tbButtons; // Group of buttons to pick tie-breaking mode.
	
	public UI() {
		font = new BitmapFont();
		
		pfMode = PathFind.ForwardAStar;
		tbMode = TieBreak.Smaller;
		
		pfButtons = new Button[PathFind.values().length];
		pfButtons[PathFind.ForwardAStar.ordinal()] = new Button("Repeated Forward A*", true);
		pfButtons[PathFind.BackwardAStar.ordinal()] = new Button("Repeated Backward A*", false);
		pfButtons[PathFind.AdaptiveAStar.ordinal()] = new Button("Adaptive A*", false);
		
		tbButtons = new Button[TieBreak.values().length];
		tbButtons[TieBreak.Smaller.ordinal()] = new Button("Smaller g-Values", true);
		tbButtons[TieBreak.Larger.ordinal()] = new Button("Larger g-Values", false);
	}
	
	/**
	 * Logic such as input that needs to be monitored/updated each frame as the
	 * program runs.
	 * 
	 * @param pixelPos  The position in the window the UI is located at. This point
	 * 				    corresponds to the position of the bottom-left corner of the
	 * 				    UI, and is measured in pixels.
	 * @param pixelSize The size in pixels of the drawn UI.
	 */
	public void update(Point pixelPos, Point pixelSize) {
		//Handle Mouse Actions		
		if (Gdx.input.justTouched()){
			//PathFind Buttons
			for(int i=0;i<pfButtons.length; i++) {
				if (isButtonClicked(pfButtons[i])) {
					pfMode = PathFind.values()[i];
					pfButtons[i].active = true;
					for(int j=0;j<pfButtons.length; j++) {
						if (i != j)
							pfButtons[j].active = false;
					}
					break;
				}
			}
			//TieBreak Buttons
			for(int i=0;i<tbButtons.length; i++) {
				if (isButtonClicked(tbButtons[i])) {
					tbMode = TieBreak.values()[i];
					tbButtons[i].active = true;
					for(int j=0;j<tbButtons.length; j++) {
						if (i != j)
							tbButtons[j].active = false;
					}
					break;
				}
			}
		}
	}
	
	/**
	 * Draws the UI to the application window.
	 * 
	 * @param camera	Camera that defines the viewport of the world.
	 * @param render	ShapeRenderer for drawing geometric shapes to the window.
	 * @param batch		SpriteBatch for drawing images to the application window.
	 * @param pixelPos  The position in the window the UI should be drawn at. This
	 * 					point corresponds to the position of the bottom-left corner of
	 * 					the UI, and is measured in pixels.
	 * @param pixelSize The size in pixels of the drawn UI.
	 */
	public void render(OrthographicCamera camera, ShapeRenderer render, SpriteBatch batch, Point pixelPos, Point pixelSize) {
		//Draw Background
		render.begin(ShapeType.Filled);
		render.setColor(0.8f, 0.9f, 1, 1);
		render.rect(pixelPos.x, pixelPos.y, pixelSize.x, pixelSize.y);
		render.end();

		Gdx.gl10.glLineWidth(3);
		render.begin(ShapeType.Line);
		render.setColor(Color.BLACK);
		render.line(pixelPos.x, pixelPos.y, pixelPos.x, pixelPos.y + pixelSize.y);
		render.end();
		
		//Draw Section Text and Buttons
		batch.setProjectionMatrix(camera.projection);
		batch.begin();
		
		float buttonSpacing = 0.025f;
		float buttonHeight = 0.05f;
		float buttonWidth = 0.8f;
		float pfHeight = 0.95f;
		float tbHeight = 0.65f;
		font.setColor(0, 0, 0, 1);
		
		//Pathfinding section & buttons
		drawTextCentered(font, batch, "Pathfinding Mode:", pixelPos.x + pixelSize.x/2f, pixelPos.y + pixelSize.y*pfHeight);
		for(int i=0; i<pfButtons.length; i++) {
			Button b = pfButtons[i];
			b.pixelPos.x = (int)(pixelPos.x+pixelSize.x*((1-buttonWidth)/2f));
			b.pixelPos.y = (int)(pixelPos.y + pixelSize.y*pfHeight - pixelSize.y*(buttonHeight+buttonSpacing)*(i+1));
			b.pixelSize.x = (int)(pixelSize.x*buttonWidth);
			b.pixelSize.y = (int)(pixelSize.y*buttonHeight);
			drawButton(b, render, batch, font);
		}
		
		//Tie-breaking section & buttons
		drawTextCentered(font, batch, "Tie Breaking Mode:", pixelPos.x + pixelSize.x/2f, pixelPos.y + pixelSize.y*tbHeight);
		for(int i=0; i<tbButtons.length; i++) {
			Button b = tbButtons[i];
			b.pixelPos.x = (int)(pixelPos.x+pixelSize.x*((1-buttonWidth)/2f));
			b.pixelPos.y = (int)(pixelPos.y + pixelSize.y*tbHeight - pixelSize.y*(buttonHeight+buttonSpacing)*(i+1));
			b.pixelSize.x = (int)(pixelSize.x*buttonWidth);
			b.pixelSize.y = (int)(pixelSize.y*buttonHeight);
			drawButton(b, render, batch, font);
		}
		
		batch.end();
		
		update(pixelPos, pixelSize);
	}
	
	/**
	 * @return The tie-breaking method to use.
	 */
	public TieBreak getTieBreakingMode() {
		return tbMode;
	}
	
	/**
	 * @return The pathfinding algorithm to use.
	 */
	public PathFind getPathFindingAlgorithm() {
		return pfMode;
	}
	
	/**
	 * Draws text horizontally & vertically centered at the specified point.
	 * @param font Font to use.
	 * @param batch SpriteBatch for drawing to the screen.
	 * @param str Text to display.
	 * @param x X-position on the screen.
	 * @param y Y-position on the screen.
	 */
	private void drawTextCentered(BitmapFont font, SpriteBatch batch, CharSequence str, float x, float y) {
		TextBounds bounds = font.getBounds(str);
		font.draw(batch, str, x-bounds.width/2f, y+bounds.height/2f);
	}
	
	/**
	 * Draws a button to the application window.
	 * @param b Button object to draw
	 * @param render ShapeRenderer for drawing to the screen.
	 * @param batch SpriteBatch for drawing to the screen.
	 * @param font Font to use for the button's text label.
	 */
	private void drawButton(Button b, ShapeRenderer render, SpriteBatch batch, BitmapFont font) {
		render.begin(ShapeType.Filled);
		if (b.active)
			render.setColor(0.6f, 0.65f, 0.9f, 1);
		else
			render.setColor(0.7f, 0.85f, 1, 1);
		render.rect(b.pixelPos.x, b.pixelPos.y, b.pixelSize.x, b.pixelSize.y);
		render.end();
		
		Gdx.gl10.glLineWidth(1);
		render.begin(ShapeType.Line);
		render.setColor(Color.BLACK);
		render.rect(b.pixelPos.x, b.pixelPos.y, b.pixelSize.x, b.pixelSize.y);
		render.end();
		
		font.setColor(0, 0, 0, 1);
		drawTextCentered(font, batch, b.label, b.pixelPos.x+b.pixelSize.x/2f, b.pixelPos.y+b.pixelSize.y/2f);
	}
	
	/**
	 * Checks whether the user is currently left-clicking a button.
	 * @param b The button object to check.
	 * @return true/false: Is it currently being clicked?
	 */
	private boolean isButtonClicked(Button b) {
		if (Gdx.input.getX() >= b.pixelPos.x 
		&& Gdx.input.getX() <= b.pixelPos.x+b.pixelSize.x
		&& (Gdx.graphics.getHeight()-Gdx.input.getY()) >= b.pixelPos.y
		&& (Gdx.graphics.getHeight()-Gdx.input.getY()) <= b.pixelPos.y+b.pixelSize.y)
			return true;
		else
			return false;
	}
	
}
