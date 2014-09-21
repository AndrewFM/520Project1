package com.cs520.project1;

/**
 * The Goal that the Agent must reach at the end of its path.
 */
public class Goal extends GridObject {

	private Grid grid;
	
	public Goal(Grid grid) {
		super(Grid.ObjectType.GOAL ,"data/goal.png");
		this.grid = grid;
	}
	
}
