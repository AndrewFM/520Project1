package com.cs520.project1;

import com.cs520.project1.Grid.ObjectType;

/**
 * An obstruction in the environment that the Agent cannot pass through.
 */
public class Wall extends GridObject {
	
	public Wall(Grid grid) {
		super(Grid.ObjectType.WALL, grid.getMain().objectImages[ObjectType.WALL.ordinal()]);
	}
	
}
