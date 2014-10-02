package com.cs520.project1;

import com.cs520.project1.Grid.ObjectType;

/**
 * The Goal that the Agent must reach at the end of its path.
 */
public class Goal extends GridObject {

	public Goal(Grid grid) {
		super(Grid.ObjectType.GOAL, grid.getMain().objectImages[ObjectType.GOAL.ordinal()]);
	}
	
}
