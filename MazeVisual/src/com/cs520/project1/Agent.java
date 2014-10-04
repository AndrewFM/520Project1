package com.cs520.project1;

import java.awt.Point;
import java.util.ArrayList;
import java.util.PriorityQueue;

import com.cs520.project1.Grid.ObjectType;
import com.cs520.project1.CellNode;
import com.cs520.project1.UI.AnimationSetting;
import com.cs520.project1.UI.PathFind;

/**
 * The Agent in the environment that needs to find a shortest path to the goal.
 * This is where all the pathfinding logic takes place.
 */
public class Agent extends GridObject {

	private Grid grid;
	private Point[] actionList;
	private int visitedCount = 0; //Keep track of how many nodes visited, for debugging.
	private BinaryHeap<CellNode> openList;
	private CellNode state;
	private CellNode start;
	private CellNode goal;
	private int counter = 0;
	public boolean started = false;
	
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
		counter = 0;
		openList = new BinaryHeap<CellNode>();
		state = grid.getCellProperties(grid.agentPoint);
		start = state;
		goal = grid.getCellProperties(grid.goalPoint);
		grid.fullTraversedPath.add(start);
		started = true;
	}
	
	public void update() {
		while (started) {
			switch (grid.getMain().getPathFindingAlgorithm()) {
				case ForwardAStar:
					RepeatedAStar();
					break;
						
				case BackwardAStar:
					RepeatedAStar();
					break;
						
				case AdaptiveAStar:
					//TODO: Implement Adaptive A*
					break;
					
				case DStarLite:
					//TODO: Implement D* Lite. We're allowed to use pseudocode from
					//      the web for this, but we should cite source.
					break;
			}
			if (state.equals(goal))
				endAStar(true);
				
			if (grid.getMain().getAnimationSettings() == AnimationSetting.Enabled)
				break;
		}
	}
	
	private void endAStar(boolean success) {
		started = false;
		grid.shortestPresumedPath.clear();
		grid.moveObjectToCell(grid.agentPoint, start.position);
		
		if (!success)
			grid.getMain().showBasicDialog("There is no path from the\nagent to the goal.\n \nCells Traversed: "+grid.fullTraversedPath.size()+"\nCells Inspected: "+visitedCount+"\nA* Searches: "+counter);
		else			
			grid.getMain().showBasicDialog("Located the Goal!\n \nCells Traversed: "+grid.fullTraversedPath.size()+"\nCells Inspected: "+visitedCount+"\nA* Searches: "+counter);
	}
	
	/**
	 * Implementation for Repeated Forward/Backward A*
	 */
	private void RepeatedAStar() {
			counter += 1;
			
			if (grid.getMain().getPathFindingAlgorithm() == PathFind.BackwardAStar) {
				//Swap goal & state before running Backwards A*
				CellNode rem = goal;
				goal = state;
				state = rem;
			}
			
			goal.search = counter;
			goal.setGValue(Integer.MAX_VALUE);	
			goal.calculateFValue(goal);
			state.search = counter;
			state.setGValue(0);
			state.calculateFValue(goal);
			
			openList.clear();			
			openList.insert(state);
			
			AStar();
			
			if (openList.isEmpty()) {
				endAStar(false);
				return;
			}
			
			tracePathAndMove();
	}
	
	/**
	 * The A* subroutine that finds the path from the agent's current position to the goal.
	 */
	private void AStar() {
		boolean goalFound = false;
		while (!goalFound) {
			CellNode minInOpen = openList.peek();
			openList.remove(minInOpen);
			
			for(Point a : actionList) {
				CellNode succ = grid.getCellProperties(new Point(minInOpen.position.x+a.x, minInOpen.position.y+a.y));
				
				if (succ == null)
					continue;
				if (succ.search < counter) {
					succ.setGValue(Integer.MAX_VALUE);
					succ.search = counter;
				}
				if (succ.gValue > Main.addNoOverflow(minInOpen.gValue,succ.actionCost)) {
					succ.setGValue(Main.addNoOverflow(minInOpen.gValue,succ.actionCost));
					succ.parentOnPath = minInOpen;
					if (openList.contains(succ))
						openList.remove(succ);
					succ.calculateFValue(goal);
					openList.insert(succ);
					visitedCount++;
				}
				if (succ.equals(goal)) {
					goalFound = true;
					break;
				}
			}
			
			if (openList.isEmpty())
				break;
		}
	}
	
	private void tracePathAndMove() {
		int startPath, endPath, pathIncrement;
		
		//Follow along path until action cost changes or goal is reached.
		ArrayList<CellNode> remPath = new ArrayList<CellNode>();
		CellNode tracePath = goal;
		remPath.add(tracePath);
		grid.shortestPresumedPath.clear();
		while (!tracePath.equals(state)) {
			tracePath = tracePath.parentOnPath;
			remPath.add(tracePath);
			grid.shortestPresumedPath.add(tracePath);
		}
		
		if (grid.getMain().getPathFindingAlgorithm() == PathFind.BackwardAStar) {
			//Swap goal & state back to normal before moving along path.
			CellNode rem = goal;
			goal = state;
			state = rem;
			startPath = 0;
			endPath = remPath.size()-1;
			pathIncrement = 1;
		} else {
			startPath = remPath.size()-2;
			endPath = 0;
			pathIncrement = -1;
		}
		
		for(int i=startPath;i*pathIncrement<=endPath;i+=pathIncrement) {
			//Look at cells adjacent to us, and update action costs if walls are found.
			for(Point a : actionList) {
				CellNode adj = grid.getCellProperties(new Point(state.position.x+a.x,state.position.y+a.y));
				if (grid.getObjectTypeAtCell(adj.position) == ObjectType.WALL)
					adj.actionCost = Integer.MAX_VALUE;
			}
			
			CellNode moveTo = remPath.get(i);
			if (grid.getObjectTypeAtCell(moveTo.position) == ObjectType.WALL) {
				//Wall detected in path. Update action costs and exit.
				moveTo.actionCost = Integer.MAX_VALUE;
				break;
			}
			
			state = moveTo; //Path is not obstructed, move forward.
			grid.fullTraversedPath.add(moveTo);
		}
		grid.moveObjectToCell(grid.agentPoint, state.position);
	}
}
