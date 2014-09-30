package com.cs520.project1;

import java.awt.Point;

public class CellNode {

	public Point position;
	public int gValue;
	public int fValue;
	public int hValue;
	public boolean visited;
	
	public CellNode(Point cell) {
		this.position = new Point(cell.x, cell.y);
		gValue = 0;
		fValue = 0;
		hValue = 0;
		visited = false;
	}
	
	public CellNode(Point cell, int g, int h) {
		this.position = new Point(cell.x, cell.y);
		gValue = g;
		hValue = h;
		fValue = g+h;
		visited = false;
	}
	
	public CellNode(CellNode c) {
		position = c.position;
		gValue = c.gValue;
		hValue = c.hValue;
		fValue = gValue+fValue;
		visited = c.visited;
	}
	
	public void setGValue(int g) {
		gValue = g;
		fValue = gValue+hValue;
	}

	public int calculateHValue(CellNode currentNode, CellNode goalNode) {
		return (Math.abs(currentNode.position.x-goalNode.position.x)+Math.abs(currentNode.position.y-goalNode.position.y));
	}
	
	public void setFValue(CellNode currentNode, CellNode goalNode){
		hValue=calculateHValue(currentNode, goalNode);
		fValue=gValue+fValue;
	}
	
	public boolean equals(Object cellNode){
		if(cellNode instanceof CellNode && ((CellNode)cellNode).position.equals(position))
			return true;
		return false;
	}
	
}
