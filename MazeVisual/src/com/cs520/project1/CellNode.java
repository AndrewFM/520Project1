package com.cs520.project1;

import java.awt.Point;

import com.cs520.project1.UI.TieBreak;

public class CellNode implements Comparable<CellNode> {

	public Point position;
	public int gValue;
	public int fValue;
	public int hValue;
	public int search;
	public CellNode parentOnPath;
	private Main program;
	
	public CellNode(Main program, Point cell) {
		this.position = new Point(cell.x, cell.y);
		this.program = program;
		reset();
	}
	
	public CellNode(Main program, Point cell, int g, int h) {
		this.position = new Point(cell.x, cell.y);
		reset();
		gValue = g;
		hValue = h;
		fValue = g+h;
		this.program = program;
	}
	
	public CellNode(CellNode c) {
		program = c.program;
		position = c.position;
		gValue = c.gValue;
		hValue = c.hValue;
		fValue = gValue+fValue;
		search = c.search;
		parentOnPath = c.parentOnPath;
	}
	
	public void setGValue(int g) {
		gValue = g;
		fValue = gValue+hValue;
	}

	private int calculateHValue(CellNode goalNode) {
		return (Math.abs(position.x-goalNode.position.x)+Math.abs(position.y-goalNode.position.y));
	}
	
	public void calculateFValue(CellNode goalNode){
		hValue=calculateHValue(goalNode);
		fValue=gValue+hValue;
	}
	
	public void reset() {
		gValue = 0;
		hValue = 0;
		fValue = 0;
		search = 0;
		parentOnPath = null;
	}
	
	public boolean equals(Object cellNode){
		if(cellNode instanceof CellNode && ((CellNode)cellNode).position.equals(position))
			return true;
		return false;
	}
	
	public int compareTo(CellNode cn) {		
		//F-Values are Equal
		if (this.fValue == ((CellNode)cn).fValue) {
			//Larger G-Value Tie Breaking
			if (program.getTieBreakingMode() == TieBreak.Larger) {
				if (this.gValue == ((CellNode)cn).gValue)
					return 0;
				else if (this.gValue > ((CellNode)cn).gValue)
					return 1;
				else
					return -1;
			} 
			
			//Smaller G-Value Tie Breaking
			else if (program.getTieBreakingMode() == TieBreak.Smaller) {
				if (this.gValue == ((CellNode)cn).gValue)
					return 0;
				else if (this.gValue > ((CellNode)cn).gValue)
					return -1;
				else
					return 1;				
			} 
			return 0;
		}
		else if (this.fValue > ((CellNode)cn).fValue)
			return 1;
		else
			return -1;
	}
	
}
