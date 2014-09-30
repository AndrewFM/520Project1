package com.cs520.project1;

import java.awt.Point;

/**
 * The Agent in the environment that needs to find a shortest path to the goal.
 */
public class Agent extends GridObject {

	private Grid grid;
	
	public Agent(Grid grid) {
		super(Grid.ObjectType.AGENT ,"data/agent.png");
		this.grid = grid;
	}
	
	/*
	 * TODO:
	 * A* logic goes in here. Agent should use Grid's path functions when
	 * tracing its way to the goal:
	 * 
	 * grid.addCellToPath()
	 * grid.popCellFromPath()
	 */
	private void pathFind() {
		switch (grid.getMain().getPathFindingAlgorithm()) {
		
			case ForwardAStar:
				//TODO
				break;
				
			case BackwardAStar:
				//TODO
				break;
				
			case AdaptiveAStar:
				//TODO
				break;
				
		}
	}
	
	/**
	 * Move the agent one cell in a direction.
	 * @return Positive integer if the move succeeded, negative integer if the agent
	 * 		   was unable to move.
	 */
	private int moveUp() {
		return grid.moveObjectToCell(grid.agentPoint, new Point(grid.agentPoint.x,grid.agentPoint.y+1));
	}
	private int moveDown() {
		return grid.moveObjectToCell(grid.agentPoint, new Point(grid.agentPoint.x,grid.agentPoint.y-1));
	}
	private int moveLeft() {
		return grid.moveObjectToCell(grid.agentPoint, new Point(grid.agentPoint.x-1,grid.agentPoint.y));
	}
	private int moveRight() {
		return grid.moveObjectToCell(grid.agentPoint, new Point(grid.agentPoint.x+1,grid.agentPoint.y));
	}
	
	/**
	 * Look at the adjacent cell in a direction.
	 * @return true if the cell is unoccupied, false if it is obstructed.
	 */
	private boolean lookUp() {
		return grid.getObjectAtCell(new Point(grid.agentPoint.x,grid.agentPoint.y+1)) != Grid.ObjectType.WALL;
	}
	private boolean lookDown() {
		return grid.getObjectAtCell(new Point(grid.agentPoint.x,grid.agentPoint.y-1)) != Grid.ObjectType.WALL;
	}
	private boolean lookLeft() {
		return grid.getObjectAtCell(new Point(grid.agentPoint.x-1,grid.agentPoint.y)) != Grid.ObjectType.WALL;
	}
	private boolean lookRight() {
		return grid.getObjectAtCell(new Point(grid.agentPoint.x+1,grid.agentPoint.y)) != Grid.ObjectType.WALL;
	}
	
}
