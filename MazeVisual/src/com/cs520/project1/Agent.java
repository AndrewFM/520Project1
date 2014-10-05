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
 * 
 * D* Lite implementation based on psuedocode of (unoptimized) D* Lite from Koenig
 * and Likhachev, Fast Replanning for Navigation in Unknown Terrain. [Fig 4]
 */
public class Agent extends GridObject {

	private Grid grid;
	private Point[] actionList;
	private int visitedCount = 0; //Keep track of how many nodes visited, for debugging.
	private BinaryHeap<CellNode> openList;
	private ArrayList<CellNode> closedList;
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
	public void startPathFind() {
		visitedCount = 0;
		counter = 0;
		openList = new BinaryHeap<CellNode>();
		state = grid.getCellProperties(grid.agentPoint);
		start = state;
		goal = grid.getCellProperties(grid.goalPoint);
		grid.fullTraversedPath.add(start);
		
		if (grid.getMain().getPathFindingAlgorithm() == PathFind.AdaptiveAStar)
			closedList = new ArrayList<CellNode>();
		
		if (grid.getMain().getPathFindingAlgorithm() == PathFind.DStarLite) {
			goal.rhs = 0;
			goal.calculateKey(state);
			openList.insert(goal);
			DStarShortestPath();
		}
		
		started = true;
	}
	
	public void update() {
		while (started) {
			//TODO: Implement D* Lite.
			if (grid.getMain().getPathFindingAlgorithm() == PathFind.DStarLite)
				DStar();
			else
				RepeatedAStar(); //TODO: Backwards A* is sometimes really slow. Check for bugs.
			
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
			grid.getMain().showBasicDialog("Located the Goal!\n \nCells Traversed: "+grid.fullTraversedPath.size()+"\nCells Expanded: "+visitedCount+"\nA* Searches: "+counter);
	}
	
	/**
	 * Implementation for iterative A* methods.
	 */
	private void RepeatedAStar() {			
			if (grid.getMain().getPathFindingAlgorithm() == PathFind.BackwardAStar) {
				//Backward A*: Swap goal & state before running path find.
				CellNode rem = goal;
				goal = state;
				state = rem;
			}
			
			counter += 1;
			goal.search = counter;
			goal.setGValue(Integer.MAX_VALUE);	
			goal.calculateFValue(goal);
			state.search = counter;
			state.setGValue(0);
			state.calculateFValue(goal);
				
			openList.clear();
			openList.insert(state);				
			
			if (grid.getMain().getPathFindingAlgorithm() == PathFind.AdaptiveAStar)
				closedList.clear();
			
			AStar();
			
			if (openList.isEmpty()) {
				endAStar(false);
				return;
			}
			
			if (grid.getMain().getPathFindingAlgorithm() == PathFind.AdaptiveAStar) {
				//Adaptive A*: Update h-values for all expanded nodes.
				for(CellNode exp : closedList)
					exp.calculateAdaptiveFValue(goal);
			}
			
			tracePathAndMove();
	}
	
	/**
	 * The A* subroutine that finds the shortest path from the agent's current position to the goal.
	 */
	private void AStar() {
		boolean goalFound = false;
		while (!openList.isEmpty() && !goalFound) {
			CellNode minInOpen = openList.poll();
			
			if (grid.getMain().getPathFindingAlgorithm() == PathFind.AdaptiveAStar)
				closedList.add(minInOpen); //Keep track of all expanded nodes in Adaptive A* so we can update h-values later.
			
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
			//Backward A*: Path traced from start to goal. Swap goal & state back to normal before moving along path.
			CellNode rem = goal;
			goal = state;
			state = rem;
			startPath = 0;
			endPath = remPath.size()-1;
			pathIncrement = 1;
		} else {
			//Forward A*: Path traced from goal to start.
			startPath = remPath.size()-2;
			endPath = 0;
			pathIncrement = -1;
		}
		
		for(int i=startPath;i*pathIncrement<=endPath;i+=pathIncrement) {
			//Look at cells adjacent to us, and update action costs if walls are found.
			for(Point a : actionList) {
				CellNode adj = grid.getCellProperties(new Point(state.position.x+a.x,state.position.y+a.y));
				if (adj != null && grid.getObjectTypeAtCell(adj.position) == ObjectType.WALL) {
					adj.actionCost = Integer.MAX_VALUE;
				}
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
	
	private void DStar() {				
		if (openList.isEmpty() || state.gValue == Integer.MAX_VALUE) {
			endAStar(false);
			return;
		}
		
		moveDStar();
		
		if (state.equals(goal))
			return;
		
		DStarShortestPath();
	}
	
	/**
	 * The shortest-path finding subroutine used in D* Lite.
	 */
	private void DStarShortestPath() {
		counter += 1;
		while (!openList.isEmpty() && (CellNode.compareKeys(openList.peek().key,state.calculateKey(state)) < 0 || state.rhs != state.gValue)) {
			CellNode minInOpen = openList.poll();
			if (minInOpen.gValue > minInOpen.rhs) {
				minInOpen.setGValue(minInOpen.rhs);
				for(Point a : actionList) {
					CellNode pred = grid.getCellProperties(new Point(minInOpen.position.x+a.x, minInOpen.position.y+a.y));
					if (pred != null && pred.actionCost != Integer.MAX_VALUE) {
						updateVertex(pred);
						visitedCount++;
					}
				}
			} else {
				minInOpen.setGValue(Integer.MAX_VALUE);
				for(Point a : actionList) {
					CellNode pred = grid.getCellProperties(new Point(minInOpen.position.x+a.x, minInOpen.position.y+a.y));
					if (pred != null && pred.actionCost != Integer.MAX_VALUE) {
						updateVertex(pred);
						visitedCount++;
					}
				}
				updateVertex(minInOpen);
			}
		}
	}
	
	private void moveDStar() {		
		ArrayList<CellNode> costChanges = new ArrayList<CellNode>();
		while (costChanges.size() == 0 && !state.equals(goal)) {
			//Find the next cell to move to.
			CellNode moveTo = null;
			int minArg = Integer.MAX_VALUE;
			for(Point a : actionList) {
				CellNode succ = grid.getCellProperties(new Point(state.position.x+a.x, state.position.y+a.y));
				if (succ != null && grid.getObjectTypeAtCell(succ.position) == ObjectType.WALL) {
					costChanges.add(succ);
				}
				if (succ != null && Main.addNoOverflow(succ.actionCost, succ.gValue) <= minArg) {
					minArg = Main.addNoOverflow(succ.actionCost, succ.gValue);
					moveTo = succ;
				}
			}
			if (grid.getObjectTypeAtCell(moveTo.position) != ObjectType.WALL) {
				state = moveTo;
				grid.moveObjectToCell(grid.agentPoint, state.position);
				grid.fullTraversedPath.add(moveTo);
			}
		}
		
		//If any path costs changed, update cells accordingly.
		if (costChanges.size() > 0) {			
			for(CellNode cn : costChanges) {
				cn.actionCost = Integer.MAX_VALUE;
				for(Point a : actionList) {
					CellNode succ = grid.getCellProperties(new Point(cn.position.x+a.x, cn.position.y+a.y));
					if (succ != null)
						updateVertex(succ);
				}
			}
			
			//Refresh all content still in the open list.
			ArrayList<CellNode> temp = new ArrayList<CellNode>();
			while (!openList.isEmpty()) {
				CellNode minInOpen = openList.poll();
				minInOpen.calculateKey(state);
				temp.add(minInOpen);
			}
			for(CellNode cn : temp)
				openList.insert(cn);
		}
	}
	
	private void updateVertex(CellNode cn) {
		if (!cn.equals(goal)) {
			int minSucc = Integer.MAX_VALUE;
			for (Point a : actionList) {
				CellNode succ = grid.getCellProperties(new Point(cn.position.x+a.x, cn.position.y+a.y));
				if (succ != null && Main.addNoOverflow(succ.actionCost, succ.gValue) < minSucc)
					minSucc = Main.addNoOverflow(succ.actionCost, succ.gValue);
			}
			cn.rhs = minSucc;
		}
		if (openList.contains(cn))
			openList.remove(cn);
		if (cn.rhs != cn.gValue) {
			cn.calculateKey(state);
			openList.insert(cn);
		}
	}
}
