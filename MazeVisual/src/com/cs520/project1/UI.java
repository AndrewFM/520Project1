package com.cs520.project1;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.cs520.project1.Grid.ObjectType;

/**
 * User Interface Control
 */
public class UI {

	public static enum PathFind {
		ForwardAStar, BackwardAStar, AdaptiveAStar, DStarLite 
	};
	
	public static enum TieBreak {
		Smaller, Larger
	};
	
	public static enum AnimationSetting {
		Enabled, Disabled
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
	
	private Grid environment;
	private BitmapFont font;
	public PathFind pfMode; 	     // Pathfinding Algorithm to use.
	public TieBreak tbMode; 	     // Tie-breaking method to use.
	public AnimationSetting aniMode; // Whether to animate the pathfinding sequence or not.
	private Button[] pfButtons;      // Group of buttons to pick pathfinding algo.
	private Button[] tbButtons;      // Group of buttons to pick tie-breaking mode.
	private Button startButton;      // Button to initiate the path finding
	private Button[] mazeButtons;    // Buttons to change the displayed maze.
	private Button[] aniButtons;	 // Buttons to set the animation preferences.
	private int selectedMaze;
	private String debugString = "";
	
	public UI(Grid environment) {
		font = new BitmapFont();
		this.environment = environment;
		
		pfMode = PathFind.ForwardAStar;
		tbMode = TieBreak.Smaller;
		aniMode = AnimationSetting.Enabled;
		
		pfButtons = new Button[PathFind.values().length];
		pfButtons[PathFind.ForwardAStar.ordinal()] = new Button("Repeated Forward A*", true);
		pfButtons[PathFind.BackwardAStar.ordinal()] = new Button("Repeated Backward A*", false);
		pfButtons[PathFind.AdaptiveAStar.ordinal()] = new Button("Adaptive A*", false);
		pfButtons[PathFind.DStarLite.ordinal()] = new Button("D* Lite", false);
		
		tbButtons = new Button[TieBreak.values().length];
		tbButtons[TieBreak.Smaller.ordinal()] = new Button("Smaller g-Values", true);
		tbButtons[TieBreak.Larger.ordinal()] = new Button("Larger g-Values", false);
		
		aniButtons = new Button[AnimationSetting.values().length];
		aniButtons[AnimationSetting.Enabled.ordinal()] = new Button("Animated", true);
		aniButtons[AnimationSetting.Disabled.ordinal()] = new Button("Direct", false);
		
		startButton = new Button("Start Pathfinding", false);
		
		mazeButtons = new Button[3];
		mazeButtons[0] = new Button("<", false);
		mazeButtons[1] = new Button("Maze 1", false);
		mazeButtons[2] = new Button(">", false);
		selectedMaze = 0;
		loadMaze(selectedMaze);
	}
	
	/**
	 * Load a maze from file.
	 * @param mazeID The ID of the maze from 0 to NUMBER_OF_MAZES-1.
	 */
	public void loadMaze(int mazeID) {
		environment.generateEnviroFromFile("mazes/Maze"+(Math.max(Math.min(mazeID+1,Main.NUMBER_OF_MAZES),0))+".txt");
		mazeButtons[1].label = "Maze "+(mazeID+1);
		setUIDebugString(getDefaultDebugString());
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
		if (Gdx.input.justTouched()) {
			Agent a = environment.getAgent();
			
			if (a == null || a.started == false) {
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
				
				//Maze Buttons
				if (isButtonClicked(mazeButtons[0])) { //Previous
					if (selectedMaze == 0)
						selectedMaze = Main.NUMBER_OF_MAZES-1;
					else
						selectedMaze -= 1;
					loadMaze(selectedMaze);
				}
				if (isButtonClicked(mazeButtons[2])) { //Next
					if (selectedMaze == Main.NUMBER_OF_MAZES-1)
						selectedMaze = 0;
					else
						selectedMaze += 1;
					loadMaze(selectedMaze);
				}
			}
			
			//Animation Buttons
			for(int i=0;i<aniButtons.length; i++) {
				if (isButtonClicked(aniButtons[i])) {
					aniMode = AnimationSetting.values()[i];
					aniButtons[i].active = true;
					for(int j=0;j<aniButtons.length; j++) {
						if (i != j)
							aniButtons[j].active = false;
					}
					break;
				}
			}
			
			//Start Pathfinding Button
			if (isButtonClicked(startButton)) {
				if (!environment.doesObjectExist(ObjectType.AGENT)
				 || !environment.doesObjectExist(ObjectType.GOAL)) {
					environment.getMain().showBasicDialog("ERROR: Agent and/or Goal is\nmissing! Please add them\ninto the grid.\n \n"+getDefaultDebugString());
				} else {
					if (a.started) {
						aniMode = AnimationSetting.Disabled;
						aniButtons[AnimationSetting.Enabled.ordinal()].active = false;
						aniButtons[AnimationSetting.Disabled.ordinal()].active = true;
					} else {
						environment.resetPathFind();
						a.startPathFind();
					}
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
		
		float buttonSpacing = 0.018f;
		float buttonHeight = 0.045f;
		float buttonWidth = 0.8f;
		float pfHeight = 0.95f;
		float tbHeight = 0.65f;
		float debugHeight = 0.45f;
		float aniHeight = 0.05f+(buttonHeight+buttonSpacing)*2f;
		float startHeight = 0.05f+buttonHeight+buttonSpacing;
		float mazeHeight = 0.05f;
		font.setColor(0, 0, 0, 1);
		
		//Pathfinding section & buttons
		drawTextCentered(font, batch, "Pathfinding Mode:", pixelPos.x + pixelSize.x/2f, pixelPos.y + pixelSize.y*pfHeight);
		for(int i=0; i<pfButtons.length; i++) {
			pfButtons[i].pixelPos.x = (int)(pixelPos.x+pixelSize.x*((1-buttonWidth)/2f));
			pfButtons[i].pixelPos.y = (int)(pixelPos.y + pixelSize.y*pfHeight - pixelSize.y*(buttonHeight+buttonSpacing)*(i+1));
			pfButtons[i].pixelSize.x = (int)(pixelSize.x*buttonWidth);
			pfButtons[i].pixelSize.y = (int)(pixelSize.y*buttonHeight);
			drawButton(pfButtons[i], render, batch, font);
		}
		
		//Tie-breaking section & buttons
		drawTextCentered(font, batch, "Tie Breaking Mode:", pixelPos.x + pixelSize.x/2f, pixelPos.y + pixelSize.y*tbHeight);
		for(int i=0; i<tbButtons.length; i++) {
			tbButtons[i].pixelPos.x = (int)(pixelPos.x+pixelSize.x*((1-buttonWidth)/2f));
			tbButtons[i].pixelPos.y = (int)(pixelPos.y + pixelSize.y*tbHeight - pixelSize.y*(buttonHeight+buttonSpacing)*(i+1));
			tbButtons[i].pixelSize.x = (int)(pixelSize.x*buttonWidth);
			tbButtons[i].pixelSize.y = (int)(pixelSize.y*buttonHeight);
			drawButton(tbButtons[i], render, batch, font);
		}
		
		//UI Console Text
		drawTextCentered(font, batch, "Information:\n \n"+debugString, pixelPos.x + pixelSize.x/2f, pixelPos.y + pixelSize.y*debugHeight);
		
		//Animation Buttons
		for(int i=0; i<aniButtons.length; i++) {
			aniButtons[i].pixelPos.x = (int)(pixelPos.x+pixelSize.x*((1-buttonWidth)/2f)+pixelSize.x*buttonWidth*0.525f*(float)i);
			aniButtons[i].pixelPos.y = (int)(pixelPos.y + pixelSize.y*aniHeight);
			aniButtons[i].pixelSize.x = (int)(pixelSize.x*buttonWidth*0.475f);
			aniButtons[i].pixelSize.y = (int)(pixelSize.y*buttonHeight);
			drawButton(aniButtons[i], render, batch, font);
		}
		
		//Start Button
		startButton.pixelPos.x = (int)(pixelPos.x+pixelSize.x*((1-buttonWidth)/2f));
		startButton.pixelPos.y = (int)(pixelPos.y + pixelSize.y*startHeight);
		startButton.pixelSize.x = (int)(pixelSize.x*buttonWidth);
		startButton.pixelSize.y = (int)(pixelSize.y*buttonHeight);
		drawButton(startButton, render, batch, font);
		
		//Maze Buttons
		mazeButtons[0].pixelPos.x = (int)(pixelPos.x+pixelSize.x*0.1f);
		mazeButtons[1].pixelPos.x = (int)(pixelPos.x+pixelSize.x*0.25f);
		mazeButtons[2].pixelPos.x = (int)(pixelPos.x+pixelSize.x*0.8f);
		mazeButtons[0].pixelSize.x = (int)(pixelSize.x*0.1f);
		mazeButtons[1].pixelSize.x = (int)(pixelSize.x*0.5f);
		mazeButtons[2].pixelSize.x = (int)(pixelSize.x*0.1f);
		for(int i=0; i<mazeButtons.length; i++) {
			mazeButtons[i].pixelPos.y = (int)(pixelPos.y + pixelSize.y*mazeHeight);
			mazeButtons[i].pixelSize.y = (int)(pixelSize.y*buttonHeight);
			drawButton(mazeButtons[i], render, batch, font);
		}
		
		batch.end();
		
		update(pixelPos, pixelSize);
	}
	
	/**
	 * Draws text horizontally & vertically centered at the specified point.
	 * @param font Font to use.
	 * @param batch SpriteBatch for drawing to the screen.
	 * @param str Text to display.
	 * @param x X-position on the screen.
	 * @param y Y-position on the screen.
	 */
	private void drawTextCentered(BitmapFont font, SpriteBatch batch, String str, float x, float y) {
		String[] lines = str.split("\n");
		TextBounds bounds = font.getBounds("A");
		float lineHeight = bounds.height*1.75f;
		
		for (int i=0; i<lines.length; i++) {
			bounds = font.getBounds(lines[i]);
			font.draw(batch, lines[i], x-bounds.width/2f, y+lineHeight/2f-lineHeight*i);
		}
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
	
	public void setUIDebugString(String message) {
		debugString = message;
	}
	
	public String getDefaultDebugString() {
		return "(Left Click) Place Agent\n(Right Click) Place Goal";
	}
	
}
