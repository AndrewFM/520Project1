package com.cs520.project1;

import java.awt.Point;

import com.cs520.project1.UI.TieBreak;

public class CellNode implements Comparable<CellNode> {

	private Main program;
	public Point position;
	public int gValue;
	public int fValue;
	public int hValue;
	public int search;
	public int actionCost;
	public CellNode parentOnPath;
	public int rhs;   //Used by D* Lite only
	public Point key; //Used by D* Lite only
	
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
		fValue = Main.addNoOverflow(gValue,hValue);
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
		actionCost = c.actionCost;
	}
	
	public void setGValue(int g) {
		gValue = g;
		fValue = Main.addNoOverflow(gValue,hValue);
	}

	private int calculateHValue(CellNode goalNode) {
		return (Math.abs(position.x-goalNode.position.x)+Math.abs(position.y-goalNode.position.y));
	}
	
	public void calculateAdaptiveFValue(CellNode goalNode) {
		hValue = goalNode.gValue-gValue;
		fValue = Main.addNoOverflow(gValue, hValue);
	}
	
	public void calculateFValue(CellNode goalNode){
		//Only calculate h-value if it hasn't already been calculated.
		if (hValue == -1)
			hValue=calculateHValue(goalNode);
		fValue = Main.addNoOverflow(gValue,hValue);
	}
	
	public void reset() {
		gValue = Integer.MAX_VALUE;
		hValue = -1;
		fValue = Integer.MAX_VALUE;
		search = 0;
		actionCost = 1;
		parentOnPath = null;
		rhs = Integer.MAX_VALUE;
		key = null;
	}
	
	public Point calculateKey(CellNode goal) {
		hValue = calculateHValue(goal);
		if (key == null)
			key = new Point();
		key.y = Math.min(gValue, rhs);
		key.x = Main.addNoOverflow(key.y,hValue);
		return key;
	}
	
	public boolean equals(Object cellNode){
		if(cellNode instanceof CellNode && ((CellNode)cellNode).position.equals(position))
			return true;
		return false;
	}
	
	public int compareTo(CellNode cn) {
		//Regular CellNode: Resolve ties in nondecreasing order on f-values, secondarily by g-values.
		if (key == null || cn.key == null) {
			//F-Values are Equal
			if (this.fValue == cn.fValue) {
				//Larger G-Value Tie Breaking
				if (program.getTieBreakingMode() == TieBreak.Larger) {
					if (this.gValue == cn.gValue)
						return 0;
					else if (this.gValue > cn.gValue)
						return 1;
					else
						return -1;
				} 
				
				//Smaller G-Value Tie Breaking
				else if (program.getTieBreakingMode() == TieBreak.Smaller) {
					if (this.gValue == cn.gValue)
						return 0;
					else if (this.gValue > cn.gValue)
						return -1;
					else
						return 1;				
				} 
				return 0;
			}
			else if (this.fValue > cn.fValue)
				return 1;
			else
				return -1;
		}
		
		//D* Lite CellNode: Resolve ties in nondecreasing order on primary key, secondarily by secondary key.
		else
			return compareKeys(this.key, cn.key);
	}
	
	public static int compareKeys(Point keyA, Point keyB) {
		if (keyA.x == keyB.x) {
			if (keyA.y == keyB.y)
				return 0;
			else if (keyA.y > keyB.y)
				return 1;
			else
				return -1;
		}
		else if (keyA.x > keyB.x)
			return 1;
		else
			return -1;		
	}
	
}
