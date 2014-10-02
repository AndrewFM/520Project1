package com.cs520.project1;

import java.awt.Point;
import java.util.PriorityQueue;

import com.cs520.project1.Grid.ObjectType;
import com.cs520.project1.CellNode;
import com.cs520.project1.UI.PathFind;

/**
 * The Agent in the environment that needs to find a shortest path to the goal.
 */
public class Agent extends GridObject {

	private Grid grid;
	private Point[] actionList;
	private int visitedCount = 0; //Keep track of how many nodes visited, for debugging.
	
	public Agent(Grid grid) {
		super(Grid.ObjectType.AGENT, grid.getMain().objectImages[ObjectType.AGENT.ordinal()]);
		this.grid = grid;
		actionList = new Point[4];
		actionList[0] = new Point(0,1);
		actionList[1] = new Point(0,-1);
		actionList[2] = new Point(1,0);
		actionList[3] = new Point(-1,0);
	}
	
	/**
	 * Initiate a path finding routine for the Agent.
	 */
	public void pathFind() {
		visitedCount = 0;
		switch (grid.getMain().getPathFindingAlgorithm()) {
		
			case ForwardAStar:
				AStar();
				break;
				
			case BackwardAStar:
				AStar();
				break;
				
			case AdaptiveAStar:
				//TODO: Implement Adaptive A*
				break;
				
			case DStarLite:
				//TODO: Implement D* Lite. We're allowed to use pseudocode from
				//      the web for this, but we should cite source.
				break;
				
		}
	}
	
	//TODO: Make this more efficient. This shouldn't be a direct copy
	//      of the psuedo-code from the project description.
	/**
	 * Implementation for Forward & Backward Repeating A*
	 * @return true if a path was found, false otherwise.
	 */
	public boolean AStar() {
		int counter = 0;
		BinaryHeap<CellNode> openList = new BinaryHeap<CellNode>();
		CellNode state;
		CellNode goal;
		
		//Forward A* searches from the Agent to the Goal.
		if (grid.getMain().getPathFindingAlgorithm() == PathFind.ForwardAStar) {
			state = grid.getCellProperties(grid.agentPoint);
			goal = grid.getCellProperties(grid.goalPoint);
			
		//Backwards A* searches from the Goal to the Agent.
		} else {
			state = grid.getCellProperties(grid.goalPoint);
			goal = grid.getCellProperties(grid.agentPoint);			
		}
		
		while (!state.equals(goal)) {
			counter += 1;
			goal.search = counter;
			goal.setGValue(Integer.MAX_VALUE);	
			
			state.search = counter;
			state.calculateFValue(goal);
			openList.insert(state);
			
			ComputePath(openList, goal, counter);
			
			if (openList.isEmpty()) {
				grid.getMain().showBasicDialog("There is no path from the\nagent to the goal.\n \nNodes Visited: "+visitedCount);
				return false;
			}
			
			CellNode tracePath = goal;
			while (tracePath != state) {
				grid.addCellToPath(tracePath);
				tracePath = tracePath.parentOnPath;
			}
			
			state = goal;
		}
		
		grid.getMain().showBasicDialog("Path Found!\n \nPath Length: "+grid.getPathLength()+"\nNodes Visited: "+visitedCount);
		return true;
	}
	
	/**
	 * A subroutine of A* to find the path from the agent's current position to the goal.
	 */
	public void ComputePath(BinaryHeap<CellNode> openList, CellNode goal, int counter) {
		while (goal.gValue > openList.peek().fValue) {
			CellNode minInOpen = openList.peek();
			openList.remove(minInOpen);
			
			for(Point a : actionList) {
				CellNode succ = grid.getCellProperties(new Point(minInOpen.position.x+a.x, minInOpen.position.y+a.y));
				if (grid.getObjectTypeAtCell(succ.position) == ObjectType.WALL)
					continue;
				
				if (succ.search < counter) {
					succ.gValue = Integer.MAX_VALUE;
					succ.search = counter;
				}
				if (succ.gValue > minInOpen.gValue + 1) {
					succ.gValue = minInOpen.gValue + 1;
					succ.parentOnPath = minInOpen;
					if (openList.contains(succ))
						openList.remove(succ);
					succ.calculateFValue(goal);
					openList.insert(succ);
					visitedCount++;
				}
			}
			
			if (openList.isEmpty())
				break;
		}
	}
	
}
