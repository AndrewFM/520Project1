package com.cs520.project1;
import java.awt.Point;
import java.io.*;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.cs520.project1.UI.PathFind;

/**
 * Represents a grid-based environment where all of the magic happens.
 */
public class Grid {

	public static enum ObjectType {
		AGENT, WALL, GOAL, VACANT
	};
	
	public Point cellDim;		   	 	   // Dimensions of the grid.
	private GridObject[][] objects;  	   // All objects active on the grid.
	private CellNode[][] cellNodes;		   // Array of cell nodes
	private Main program;			 	   // Reference to the main class.
	public Point agentPoint;			   // Cell containing agent.
	public Point goalPoint;				   // Cell containing goal.
	private Texture visitedTexture;
	private Sprite visitedSprite;
	public ArrayList<CellNode> shortestPresumedPath; // Visualization of shortest path to goal at current time step.
	public ArrayList<CellNode> fullTraversedPath;	 // Visualization of agent's full environment traversal so far.
	
	/**
	 * @param cellDim The size of the grid in columns (x) and rows (y).
	 */
	public Grid(Point cellDim, Main program) {
		this.cellDim = cellDim;
		this.program = program;
		objects = new GridObject[cellDim.x][cellDim.y];
		cellNodes = new CellNode[cellDim.x][cellDim.y];
		Point p = new Point();
		for(int i=0;i<cellDim.x;i++) {
			for(int j=0;j<cellDim.y;j++) {
				p.setLocation(i,j);
				cellNodes[i][j] = new CellNode(getMain(),p);
			}
		}
		shortestPresumedPath = new ArrayList<CellNode>();
		fullTraversedPath = new ArrayList<CellNode>();
		agentPoint = new Point(-1,-1);
		goalPoint = new Point(-1,-1);
		
		visitedTexture = new Texture(Gdx.files.internal("data/visited.png"));
		visitedTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		visitedSprite = new Sprite(visitedTexture);
		visitedSprite.setOrigin(0, 0);
		
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
	 *         ObjectType.VACANT if the cell is not occupied.
	 */
	public ObjectType getObjectTypeAtCell(Point cell) {		
		if (!isValidCell(cell,false))
			return ObjectType.WALL;
		if (objects[cell.x][cell.y] == null)
			return ObjectType.VACANT;
		else
			return objects[cell.x][cell.y].getType();
	}
	
	/**
	 * Gets the GridObject reference to the object at the designated
	 * cell.
	 * 
	 * @param cell The (col,row) coordinate of the cell to check.
	 * @return The GridObject at that position, or null if the cell
	 * 		   is vacant, or an invalid cell was specified.
	 */
	public GridObject getObjectAtCell(Point cell) {
		if (!isValidCell(cell,false))
			return null;
		else
			return objects[cell.x][cell.y];		
	}
	
	/**
	 * Gets the CellNode reference to the cell's properties at the
	 * designated position.
	 * 
	 * @param cell The (col,row) coordinate of the cell to check.
	 * @return The CellNode reference at that position. If an invalid
	 * 		   cell is specified, this will return null.
	 */
	public CellNode getCellProperties(Point cell) {
		if (!isValidCell(cell,false))
			return null;
		else
			return cellNodes[cell.x][cell.y];
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
				if (getObjectTypeAtCell(agentPoint) == ObjectType.AGENT)
					objects[agentPoint.x][agentPoint.y] = null;
				objects[cell.x][cell.y] = new Agent(this);
				agentPoint.x = cell.x;
				agentPoint.y = cell.y;
				break;
			case GOAL:
				if (getObjectTypeAtCell(goalPoint) == ObjectType.GOAL)
					objects[goalPoint.x][goalPoint.y] = null;
				objects[cell.x][cell.y] = new Goal(this);
				goalPoint.x = cell.x;
				goalPoint.y = cell.y;
				break;
			default:
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
		
		Agent a = getAgent();
		if (a != null)
			a.update();		
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
		
		//Pass 1: Render the Objects & Visited Cells
		Agent a = getAgent();
		Point cellSize = new Point(pixelSize.x/cellDim.x,pixelSize.y/cellDim.y);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for(int i=0;i<cellDim.x;i++) {
			for(int j=0;j<cellDim.y;j++) {				
				//Render Visited Cells
				if (cellNodes[i][j].fValue != Integer.MAX_VALUE
				     || (getMain().getPathFindingAlgorithm() == PathFind.DStarLite && (cellNodes[i][j].rhs != Integer.MAX_VALUE || cellNodes[i][j].key != null))) {
					visitedSprite.setSize(cellSize.x, cellSize.y);
					visitedSprite.setPosition(pixelPos.x+cellSize.x*i, pixelPos.y+cellSize.y*j);
					visitedSprite.draw(batch);
				}
				
				//Render Object Images
				if (objects[i][j] != null) {
					boolean wallDisplay = true;
					if (a != null && a.started && getObjectTypeAtCell(new Point(i,j)) == ObjectType.WALL && getCellProperties(new Point(i,j)).actionCost != Integer.MAX_VALUE)
						wallDisplay = false;
					if (wallDisplay)
						objects[i][j].render(batch, new Point(pixelPos.x+cellSize.x*i,pixelPos.y+cellSize.y*j), cellSize);
				}
			}
		}
		batch.end();
		
		//Pass 2: Render the Grid
		Gdx.gl10.glLineWidth(1f);
		render.setProjectionMatrix(camera.combined);
		render.begin(ShapeType.Line);
		
		render.setColor(0.95f, 0.95f, 0.95f, 1f);
		for(int i=0; i<=cellDim.x; i++) {
				//Vertical Grid Lines
				render.line(pixelPos.x+cellSize.x*i, 
							pixelPos.y, 
							pixelPos.x+cellSize.x*i, 
							pixelPos.y+cellSize.y*cellDim.y);
		}
		
		for(int i=0; i<=cellDim.y; i++) {
				//Horizontal Grid Lines
				render.line(pixelPos.x, 
							pixelPos.y+cellSize.y*i, 
							pixelPos.x+cellSize.x*cellDim.x, 
							pixelPos.y+cellSize.y*i);
		}
		
		//Lastly: Render the Paths
		Gdx.gl10.glLineWidth(2f);
		render.setColor(1f, 0f, 0f, 1f);
		renderPath(shortestPresumedPath,render, pixelPos, pixelSize);
		
		if (a != null && !a.started) {
			render.setColor(0f, 0f, 1f, 1f);
			renderPath(fullTraversedPath,render, pixelPos, pixelSize);
			
			render.setColor(1f, 0f, 0f, 1f);
			if (agentPoint.x >= 0)
				render.circle(pixelPos.x+cellSize.x*agentPoint.x+cellSize.x/2f, pixelPos.y+cellSize.y*agentPoint.y+cellSize.y/2f, 15f);
			if (goalPoint.x >= 0)
				render.circle(pixelPos.x+cellSize.x*goalPoint.x+cellSize.x/2f, pixelPos.y+cellSize.y*goalPoint.y+cellSize.y/2f, 15f);
		}
		
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
			Point p = new Point();
			
			clearGrid();
			while ((line = read.readLine()) != null) {
				char[] lineChars = line.toCharArray();
				for(int i=0;i<Math.min(lineChars.length,cellDim.x); i++) {
					if (lineChars[i] == '1') {
						p.setLocation(i,row);
						addObjectToCell(p, ObjectType.WALL);
					}
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
			for(int j=0;j<cellDim.y;j++) {
				objects[i][j] = null;
				cellNodes[i][j].reset();
			}
		agentPoint.setLocation(-1,-1);
		goalPoint.setLocation(-1,-1);
		resetPathFind();
	}	
	
	/**
	 * Removes the path from the grid, and clears data for a fresh pathfinding execution.
	 */
	public void resetPathFind() {
		shortestPresumedPath.clear();
		fullTraversedPath.clear();
		for(int i=0;i<cellDim.x;i++) {
			for(int j=0;j<cellDim.y;j++) {
				cellNodes[i][j].reset();
			}
		}
	}
	
	/**
	 * Gets a reference to the main program class.
	 */
	public Main getMain() {
		return program;
	}
	
	/**
	 * @return The agent's object if it exists on the grid, null otherwise.
	 */
	public Agent getAgent() {
		Object o = getObjectAtCell(agentPoint);
		if (o instanceof Agent) {
			return (Agent)o;
		}
		return null;
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
	private void renderPath(ArrayList<CellNode> pathVisual, ShapeRenderer render, Point pixelPos, Point pixelSize) {
		if (pathVisual.size() <= 1)
			return;
		
		Point cellSize = new Point(pixelSize.x/cellDim.x,pixelSize.y/cellDim.y);
		Point oldPoint = pathVisual.get(0).position;
		Point newPoint = null;
		
		for(int i=1;i<pathVisual.size();i++) {
			newPoint = pathVisual.get(i).position;
			render.line(pixelPos.x+cellSize.x*oldPoint.x+(cellSize.x/2), 
						pixelPos.y+cellSize.y*oldPoint.y+(cellSize.y/2), 
						pixelPos.x+cellSize.x*newPoint.x+(cellSize.x/2), 
						pixelPos.y+cellSize.y*newPoint.y+(cellSize.y/2));
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
