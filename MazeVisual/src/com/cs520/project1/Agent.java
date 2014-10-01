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
				forwardAStar(grid.getCellProperties(grid.agentPoint), grid.getCellProperties(grid.goalPoint));
				break;
				
			case BackwardAStar:
				//TODO
				break;
				
			case AdaptiveAStar:
				//TODO
				break;
				
		}
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
						
						if(grid.getObjectTypeAtCell(new Point(i,j))!=ObjectType.WALL){
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
