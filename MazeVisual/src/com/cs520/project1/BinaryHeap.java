package com.cs520.project1;

import java.util.ArrayList;

/**
 * A Min-Binary Heap.
 */
public class BinaryHeap<T extends Comparable<T>> {
		
	private ArrayList<T> heapArray;
	
	public BinaryHeap() {
		heapArray = new ArrayList<T>();
	}
	
	/**
	 * @return The value at the root of the heap.
	 */
	public T peek() {
		if (heapArray.size() == 0)
			return null;
		else
			return heapArray.get(0);
	}
	
	/**
	 * Inserts a new node into the heap.
	 * @param value The value to insert.
	 */
	public void insert(T value) {
		//Insert node as leaf of tree.
		heapArray.add(value);
		
		//Sift up until the node is in the correct sorted position.
		int newInd = heapArray.size()-1;
		int parentInd = getParentIndex(newInd);
		while (newInd != 0 && heapArray.get(newInd).compareTo(heapArray.get(parentInd)) < 0) {
			//Swap values
			T remValue = heapArray.get(parentInd);
			heapArray.set(parentInd, heapArray.get(newInd));
			heapArray.set(newInd, remValue);
			
			//Climb up
			if (parentInd == 0) //If our value reached the root, time to stop.
				break;
			parentInd = getParentIndex(parentInd);
			newInd = getParentIndex(newInd);
		}
	}
	
	/**
	 * Removes a node from the heap containing the designated value.
	 * @param value The value to search for.
	 * @return true if the node was successfully removed, false if no node with
	 * 		   that value exists in the heap.
	 */
	public boolean remove(T value) {
		int hn = findNodeWithValue(value);
		if (hn == -1)
			return false;
		
		//Replace removal point with last leaf in heap.
		heapArray.set(hn, heapArray.get(heapArray.size()-1));
		heapArray.remove(heapArray.size()-1);
		
		if (isEmpty() || hn == heapArray.size())
			return true;
		
		//Sift value to its correct location in the heap.
		while (true) {
			int parent = getParentIndex(hn);
			int minChild = getMinChildIndex(hn);
			
			//Is less than parent?
			if (hn != 0 && heapArray.get(hn).compareTo(heapArray.get(parent)) < 0) {
				//Swap values & Sift Up
				T remValue = heapArray.get(parent);
				heapArray.set(parent, heapArray.get(hn));
				heapArray.set(hn, remValue);
				hn = parent;
				continue;
			}
			
			//Is greater than children?
			if (minChild != -1 && heapArray.get(hn).compareTo(heapArray.get(minChild)) > 0) {
				//Swap values & Sift Down
				T remValue = heapArray.get(minChild);
				heapArray.set(minChild, heapArray.get(hn));
				heapArray.set(hn, remValue);
				hn = minChild;				
				continue;
			}
		
			break;
		}		
		return true;
	}
	
	/**
	 * @param value Value to search for.
	 * @return true if this heap contains a node that has this value. false otherwise.
	 */
	public boolean contains(T value) {
		int hn = findNodeWithValue(value);
		if (hn == -1)
			return false;
		else
			return true;
	}
	
	/**
	 * @return true if the heap has no nodes, false otherwise.
	 */
	public boolean isEmpty() {
		return heapArray.isEmpty();
	}
	
	/**
	 * Determine if a node exists in the heap that has some value.
	 * @param root The node to start search from.
	 * @param value The value to search for.
	 * @return The node that contains the value, or -1 if no node exists.
	 */
	private int findNodeWithValue(T value) {
		for(int i=0;i<heapArray.size();i++) {
			if (heapArray.get(i).equals(value))
				return i;
		}		
		return -1;
	}
	
	/**
	 * @param index The index of the node to evaluate.
	 * @return The index of the parent of the designated node in the heap.
	 */
	private int getParentIndex(int index) {
		return (int)Math.ceil(index/2f)-1;
	}
	
	/**
	 * @param index The index of the node to evaluate.
	 * @return The index of the left child of the designated node in the heap.
	 */
	private int getLeftChildIndex(int index) {
		return (index+1)*2-1;
	}
	
	/**
	 * @param index The index of the node to evaluate.
	 * @return The index of the right child of the designated node in the heap.
	 */
	private int getRightChildIndex(int index) {
		return (index+1)*2;
	}
	
	/**
	 * @param index The index of the node to evaluate.
	 * @return The index of the child of the designated node that has the minimum
	 * 		   value of the two children, or -1 if no such node exists.
	 */
	private int getMinChildIndex(int index) {
		int indL = getLeftChildIndex(index);
		int indR = getRightChildIndex(index);
		int indMin = -1;
		T minVal = null;
		
		if (indL < heapArray.size()) {
			indMin = indL;
			minVal = heapArray.get(indL);
		}
		if (indR < heapArray.size() && (minVal == null || heapArray.get(indR).compareTo(minVal) < 0))
			indMin = indR;
		
		return indMin;
	}
}
