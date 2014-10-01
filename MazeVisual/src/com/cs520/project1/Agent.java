package com.cs520.project1;

import java.awt.Point;
import java.util.ArrayList;
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
	
	public Agent(Grid grid) {
		super(Grid.ObjectType.AGENT ,"data/agent.png");
		this.grid = grid;
		actionList = new Point[4];
		actionList[0] = new Point(0,1);
		actionList[1] = new Point(0,-1);
		actionList[2] = new Point(1,0);
		actionList[3] = new Point(-1,0);
	}
	
	/*
	 * TODO:
	 * A* logic goes in here. Agent should use Grid's path functions when
	 * tracing its way to the goal:
	 * 
	 * grid.addCellToPath()
	 * grid.popCellFromPath()
	 */
	public void pathFind() {
		switch (grid.getMain().getPathFindingAlgorithm()) {
		
			case ForwardAStar:
				AStar();
				break;
				
			case BackwardAStar:
				AStar();
				break;
				
			case AdaptiveAStar:
				//TODO
				break;
				
		}
	}
	
	public void ComputePath(PriorityQueue<CellNode> openList, ArrayList<CellNode> closedList, CellNode goal, int counter) {
		while (goal.gValue > openList.peek().fValue) {
			CellNode minInOpen = openList.peek();
			openList.remove(minInOpen);
			closedList.add(minInOpen);
			
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
					openList.add(succ);
				}
			}
			
			if (openList.isEmpty())
				break;
		}
	}
	
	public boolean AStar() {
		int counter = 0;
		ArrayList<CellNode> closedList = new ArrayList<CellNode>();
		PriorityQueue<CellNode> openList = new PriorityQueue<CellNode>();
		
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
			openList.add(state);
			
			ComputePath(openList, closedList, goal, counter);
			
			if (openList.isEmpty()) {
				grid.getMain().showBasicDialog("There does not exist a path from the agent to the goal.");
				return false;
			}
			
			CellNode tracePath = goal;
			while (tracePath != state) {
				grid.addCellToPath(tracePath);
				tracePath = tracePath.parentOnPath;
			}
			
			state = goal;
		}
		return true;
	}
	
}
