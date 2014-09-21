package com.cs520.project1;

/**
 * An obstruction in the environment that the Agent cannot pass through.
 */
public class Wall extends GridObject {

	private Grid grid;
	
	public Wall(Grid grid) {
		super(Grid.ObjectType.WALL ,"data/wall.png");
		this.grid = grid;
	}
	
}
