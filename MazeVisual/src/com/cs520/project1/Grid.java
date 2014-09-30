package com.cs520.project1;
import java.awt.Point;
import java.io.*;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Represents a grid-based environment where all of the magic happens.
 */
public class Grid {

	public static enum ObjectType {
		AGENT, WALL, GOAL
	};
	
	public Point cellDim;		   	 	 // Dimensions of the grid.
	private GridObject[][] objects;  	 // All objects active on the grid.
	public CellNode[][] cellNodes;		// Array of cell nodes
	private ArrayList<CellNode> pathVisual; // Visualization of a path on the grid.
	private Main program;			 	 // Reference to the main class.
	public Point agentPoint;			 // Cell containing agent.
	public Point goalPoint;				 // Cell containing goal.
	
	/**
	 * @param cellDim The size of the grid in columns (x) and rows (y).
	 */
	public Grid(Point cellDim, Main program) {
		this.cellDim = cellDim;
		this.program = program;
		objects = new GridObject[cellDim.x][cellDim.y];
		cellNodes = new CellNode[cellDim.x][cellDim.y];
		pathVisual = new ArrayList<CellNode>();
		agentPoint = new Point(0,0);
		goalPoint = new Point(0,0);
		clearGrid();
	}
	
	/**
	 * @param type Type of GridObject to search for.
	 * @return true if at least one instance of the specified object exists in the
	 * 		   environment, false otherwise.
	 */
	public boolean doesObjectExist(ObjectType type) {
		for(int i=0;i<cellDim.x;i++) {
			for(int j=0;j<cellDim.y;j++) {
				if (objects[i][j] != null && objects[i][j].getType() == type)
					return true;
			}		
		}
		return false;
	}
	
	/**
	 * Gets the type of object that is occupying the designated cell.
	 * 
	 * @param cell The (col,row) coordinate of the cell to check.
	 * @return The ObjectType of the object in the designated cell, or
	 *         null if the cell is not occupied.
	 */
	public ObjectType getObjectAtCell(Point cell) {
		if (!isValidCell(cell,false))
			return ObjectType.WALL;
		if (objects[cell.x][cell.y] == null)
			return null;
		else
			return objects[cell.x][cell.y].getType();
	}
	
	/**
	 * Add an object to the specified cell. This will fail if the
	 * requested cell is already occupied by another object.
	 * 
	 * @param cell The (col,row) coordinate of the cell to add into.
	 * @param type The type of object to add to the cell.
	 * @return A positive integer if the action was successful, or
	 *         a negative integer if the object could not be added.
	 */
	public int addObjectToCell(Point cell, ObjectType type) {
		if (!isValidCell(cell,false) || objects[cell.x][cell.y] != null)
			return -1;
		else {
			switch (type) {
			case WALL:
				objects[cell.x][cell.y] = new Wall(this); break;
			case AGENT:
				clearGridObject(ObjectType.AGENT);
				objects[cell.x][cell.y] = new Agent(this);
				agentPoint.x = cell.x;
				agentPoint.y = cell.y;
				break;
			case GOAL:
				clearGridObject(ObjectType.GOAL);
				objects[cell.x][cell.y] = new Goal(this);
				goalPoint.x = cell.x;
				goalPoint.y = cell.y;
				break;
			}
			return 1;
		}
	}
	
	/**
	 * Takes the object located at one cell, and moves it to a different
	 * cell. If the destination cell is occupied, this will fail. This
	 * will also fail if the source cell has no object in it.
	 * 
	 * @param from 
	 * @param to
	 * @return A positive integer if the move succeeded, negative if the
	 * 		   move failed.
	 */
	public int moveObjectToCell(Point from, Point to) {
		if (!isValidCell(from,true) || !isValidCell(to,true))
			return -1;
		if (objects[from.x][from.y] == null || objects[to.x][to.y] != null)
			return -1;
		else {
			objects[to.x][to.y] = objects[from.x][from.y];
			objects[from.x][from.y] = null;
			
			if (objects[to.x][to.y].getType() == ObjectType.AGENT) {
				agentPoint.x = to.x;
				agentPoint.y = to.y;
			} else if (objects[to.x][to.y].getType() == ObjectType.GOAL) {
				goalPoint.x = to.x;
				goalPoint.y = to.y;
			}
			return 1;
		}
	}
	
	/**
	 * Logic such as input that needs to be monitored/updated each frame as the
	 * program runs.
	 * 
	 * @param pixelPos  The position in the window the grid is located at. This point
	 * 				    corresponds to the position of the bottom-left corner of the
	 * 				    grid, and is measured in pixels.
	 * @param pixelSize The size in pixels of the drawn grid.
	 */
	public void update(Point pixelPos, Point pixelSize) {
		
		//Handle Mouse Actions
		// (The math here is a little weird because drawing coordinates are
		//  measured from the bottom-left corner of the screen, while mouse
		//  coordinates are measured from the top-left corner of the screen.)
		
		if (Gdx.input.justTouched()){
			//Find which cell was clicked, if any.
			Point cellSize = new Point(pixelSize.x/cellDim.x,pixelSize.y/cellDim.y);
			Point cellClicked = new Point((int)Math.floor((Gdx.input.getX()-pixelPos.x)/cellSize.x)
										 ,(int)Math.floor(((Gdx.graphics.getHeight()-Gdx.input.getY())-pixelPos.y)/cellSize.y));
			
			if (isValidCell(cellClicked,false))
			{
				//Left Click places the agent.
				if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
					if (objects[cellClicked.x][cellClicked.y] == null)
						addObjectToCell(cellClicked, ObjectType.AGENT);
				}
				
				//Right Click places the goal.
				if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
					if (objects[cellClicked.x][cellClicked.y] == null)
						addObjectToCell(cellClicked, ObjectType.GOAL);
				}
			}
		}
		
	}
	
	/**
	 * Draws the Grid to the application window.
	 * 
	 * @param camera	Camera that defines the viewport of the environment.
	 * @param render	ShapeRenderer for drawing geometric shapes to the window.
	 * @param batch		SpriteBatch for drawing images to the application window.
	 * @param pixelPos  The position in the window the grid should be drawn at. This
	 * 					point corresponds to the position of the bottom-left corner of
	 * 					the grid, and is measured in pixels..
	 * @param pixelSize The size in pixels of the drawn grid.
	 */
	public void render(OrthographicCamera camera, ShapeRenderer render, SpriteBatch batch, Point pixelPos, Point pixelSize) {
		update(pixelPos, pixelSize);
		
		//Pass 1: Render the Objects
		Point cellSize = new Point(pixelSize.x/cellDim.x,pixelSize.y/cellDim.y);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for(int i=0;i<cellDim.x;i++) {
			for(int j=0;j<cellDim.y;j++) {				
				if (objects[i][j] != null)
					objects[i][j].render(batch, new Point(pixelPos.x+cellSize.x*i,pixelPos.y+cellSize.y*j), cellSize);
			}
		}
		batch.end();
		
		//Pass 2: Render the Grid
		Gdx.gl10.glLineWidth(1);
		render.setProjectionMatrix(camera.combined);
		render.begin(ShapeType.Line);
		
		render.setColor(0.92f, 0.92f, 0.92f, 1f);
		for(int i=0; i<cellDim.x; i++) {
				//Vertical Grid Lines
				render.line(pixelPos.x+cellSize.x*i, 
							pixelPos.y, 
							pixelPos.x+cellSize.x*i, 
							pixelPos.y+cellSize.y*cellDim.y);
		}
		
		for(int i=0; i<cellDim.y; i++) {
				//Horizontal Grid Lines
				render.line(pixelPos.x, 
							pixelPos.y+cellSize.y*i, 
							pixelPos.x+cellSize.x*cellDim.x, 
							pixelPos.y+cellSize.y*i);
		}
		
		//Lastly: Render the Path
		Gdx.gl10.glLineWidth(2);
		render.setColor(1f, 0f, 0f, 1f);
		renderPath(render, pixelPos, pixelSize);
		render.end();		
	}
	
	/**
	 * Places walls into the environment based on the input from a file.
	 * The file structure is a series of 0s and 1s, each character being
	 * a cell in the grid, and each line being a new row of the grid. The
	 * character 1 means the cell should be obstructed by a wall.
	 * 
	 * @param filePath A path to a text file containing the Maze data.
	 */
	public void generateEnviroFromFile(String filePath) {		
		try {
			BufferedReader read = new BufferedReader(new FileReader(filePath));
			String line = "";
			int row = 0;
			
			clearGrid();
			while ((line = read.readLine()) != null) {
				char[] lineChars = line.toCharArray();
				for(int i=0;i<Math.min(lineChars.length,cellDim.x); i++) {
					if (lineChars[i] == '1')
						addObjectToCell(new Point(i,row), ObjectType.WALL);
					
					cellNodes[i][row].position.setLocation(i, row);
					cellNodes[i][row].setGValue(0);
				}
				row += 1;
				if (row == cellDim.y)
					break;
			}
			read.close();
		} catch (FileNotFoundException e) {
			System.err.println("Tried to load grid from a non-existant file.");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}	
	}
	
	/**
	 * Removes all GridObjects from the environment.
	 */
	private void clearGrid() {
		for(int i=0;i<cellDim.x;i++)
			for(int j=0;j<cellDim.y;j++)
				objects[i][j] = null;
	}	
	
	/**
	 * Removes all GridObjects of a certain type from the environment.
	 * @param type The type of object to search for.
	 */
	private void clearGridObject(ObjectType type) {
		for(int i=0;i<cellDim.x;i++)
			for(int j=0;j<cellDim.y;j++) {
				if (objects[i][j] != null && objects[i][j].getType() == type)
					objects[i][j] = null;
			}
	}
	
	/**
	 * Adds a cell to the path, which will be visualized on the grid. The
	 * lines will be connected in the order that the cells are added to
	 * the path.
	 * 
	 * @param cell The (col,row) coordinates of the cell to add.
	 */
	public void addCellToPath(CellNode cell) {
		if (!isValidCell(cell.position,true))
			return;
		pathVisual.add(cell);
	}
	
	/**
	 * Removes the most recently added cell from the path.
	 * @return The cell that was removed.
	 */
	public CellNode popCellFromPath() {
		CellNode p = pathVisual.get(pathVisual.size()-1);
		CellNode q = new CellNode(p);
		pathVisual.remove(pathVisual.size()-1);
		
		return q;
	}
	
	/**
	 * Removes the path from the grid, and clears it to a blank slate.
	 */
	public void clearPath() {
		pathVisual.clear();
	}
	
	/**
	 * Gets a reference to the main program class.
	 */
	public Main getMain() {
		return program;
	}
	
	/**
	 * Draws the path on the grid, if one has been defined.
	 * 
	 * @param render    ShapeRenderer for drawing geometric shapes to the window.
	 * @param pixelPos  The position in the window the grid is located at. This point
	 * 				    corresponds to the position of the bottom-left corner of the
	 * 				    grid, and is measured in pixels.
	 * @param pixelSize The size in pixels of the drawn grid.
	 */
	private void renderPath(ShapeRenderer render, Point pixelPos, Point pixelSize) {
		if (pathVisual.size() <= 1)
			return;
		
		Point cellSize = new Point(pixelSize.x/cellDim.x,pixelSize.y/cellDim.y);
		Point oldPoint = pathVisual.get(0).position;
		Point newPoint = null;
		
		for(int i=1;i<pathVisual.size();i++) {
			newPoint = pathVisual.get(i).position;
			render.line(pixelPos.x+cellSize.x*oldPoint.x, 
						pixelPos.y+cellSize.y*oldPoint.y, 
						pixelPos.x+cellSize.x*newPoint.x, 
						pixelPos.y+cellSize.y*newPoint.y);
			oldPoint = newPoint;
		}
	}
	
	/**
	 * @param cell		The (col,row) coordinates of the cell to check.
	 * @param showError If an error should be shown in the case of an invalid cell.
	 * @return			Whether the cell is valid or not.
	 */
	private boolean isValidCell(Point cell, boolean showError) {
		if (cell.x < 0 || cell.y < 0 || cell.x >= cellDim.x || cell.y >= cellDim.y) {
			if (showError)
				System.err.println("An invalid cell was referenced.");
			return false;
		} else
			return true;
	}
}
