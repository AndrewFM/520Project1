package com.cs520.project1;

import java.awt.Point;
import java.util.ArrayList;
import java.util.PriorityQueue;

import com.cs520.project1.Grid.ObjectType;
import com.cs520.project1.CellNode;

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
	
	public boolean forwardAStar(CellNode start, CellNode goal){
		
		int calc_gValue;
		CellNode current;
		ArrayList<CellNode> closedList = new ArrayList<CellNode>();
		PriorityQueue<CellNode> openList = new PriorityQueue<CellNode>();
		
		start.setGValue(0); //gValue of Start is 0
		start.setFValue(start, goal);
		
		openList.add(start);
		
		while(!openList.isEmpty()){
			
			current = openList.peek();
			
			//SUCCESS condition
			if (current.equals(goal)){
				return true;			
			}
			
			openList.remove();
			closedList.add(current);
			
			int xPos = current.position.x;
			int yPos = current.position.y;
			
			//checking neighbours
			for(int i = xPos-1;i <= xPos+1;i++){
				for(int j = yPos-1; j <= yPos+1; j++){					
					if (i == xPos && j == yPos)
						continue;
					
					if(!(xPos<0 || yPos<0 || xPos>=grid.cellDim.x || yPos>=grid.cellDim.y) && (i==xPos || j==yPos)){
						
						if(grid.getObjectAtCell(new Point(i,j))!=ObjectType.WALL){
							if(closedList.contains(grid.cellNodes[i][j])){
								continue;
							}
							calc_gValue = current.gValue + 1;   //gValue is 1 from current node to new node
							
							if(!(openList.contains(grid.cellNodes[i][j])) ||  calc_gValue < grid.cellNodes[i][j].gValue){
								grid.addCellToPath(current);
								grid.cellNodes[i][j].setGValue(calc_gValue);
								grid.cellNodes[i][j].setFValue(grid.cellNodes[i][j], goal);
								
								if(!(openList.contains(grid.cellNodes[i][j]))){
									openList.add(grid.cellNodes[i][j]);
								}
							}
						}						
					}
				}
			}
		}
		return false;
		
		
	}
	
}
