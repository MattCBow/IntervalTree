package structures;

import java.util.*;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		Sorter.sortIntervals(intervalsLeft, 'l');
		Sorter.sortIntervals(intervalsRight,'r');

		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = Sorter.getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
		
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {
		Queue<IntervalTreeNode> q = new Queue<IntervalTreeNode>();
		for(Integer i:endPoints) q.enqueue(new IntervalTreeNode(i,i,i));	//Create Tree for each end point
		while(q.size()!=1){													//Make Trees Consisting of 
			int subTrees=q.size();											
			while(subTrees>1){												//do one iteration of q
				IntervalTreeNode T1= q.dequeue(), T2= q.dequeue();	
				IntervalTreeNode N = new IntervalTreeNode((T1.maxSplitValue+T2.minSplitValue)/2,T1.minSplitValue, T2.maxSplitValue);
				N.leftChild=T1;
				N.rightChild=T2;
				q.enqueue(N);									//Add a new tree consisting of q nodes back into q
				subTrees-=2;
			}
			if(subTrees==1)q.enqueue(q.dequeue());
		}
		
		return q.dequeue();
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		mapToSide(root, leftSortedIntervals, 'l');		//Maps leftIntervals
		mapToSide(root, rightSortedIntervals, 'r');		//Maps rightIntervals
		
	}
	private void mapToSide(IntervalTreeNode node, ArrayList<Interval> intervals, char lr){
		if(lr=='l')node.leftIntervals 	= new ArrayList<Interval>();
		if(lr=='r')node.rightIntervals	= new ArrayList<Interval>();
		ArrayList<Interval> leftList 	= new ArrayList<Interval>();
		ArrayList<Interval> rightList	= new ArrayList<Interval>();
		
		for(Interval i : intervals){		//Separates intervals for the root, left child, and right childe
			if(i.contains(node.splitValue) ){
				if(lr=='l')node.leftIntervals.add(i);
				if(lr=='r')node.rightIntervals.add(i);
			}
			if(i.rightEndPoint < node.splitValue)	leftList.add(i);
			if(i.leftEndPoint > node.splitValue) 	rightList.add(i);
		}
		if(!leftList.isEmpty())mapToSide(node.leftChild, leftList, lr); 	//recursive method call for left child
		if(!rightList.isEmpty())mapToSide(node.rightChild, rightList, lr);	//recursive method call for right child
	}
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		return findIntersectionsInTree(root, q); //recursive helper method
	}
	private ArrayList<Interval> findIntersectionsInTree(IntervalTreeNode node, Interval q){
		
		ArrayList<Interval> intersections = new ArrayList<Interval>();
		if(node.minSplitValue==node.maxSplitValue) return intersections;  //base case
		
		if(q.contains(node.splitValue)){							// if the interval contains the split value add all intervals
			if(node.leftIntervals!=null)intersections.addAll(node.leftIntervals); // and search both sub trees
			intersections.addAll(findIntersectionsInTree(node.leftChild, q));
			intersections.addAll(findIntersectionsInTree(node.rightChild, q));
		}
		else if(node.splitValue<q.leftEndPoint){ 					// if the split value is less than the interval 
			if(node.rightIntervals!=null) {							
				for(int i = node.rightIntervals.size()-1;(i>=0 && node.rightIntervals.get(i).intersects(q));i--){
					intersections.add(node.rightIntervals.get(i));	// add the intervals that intersect 
				}
			}														// add  the intervals to the left
			intersections.addAll(findIntersectionsInTree(node.rightChild, q));
		}
		
		else if(node.splitValue>q.rightEndPoint){					// if the split value is more than the interval 
			if(node.leftIntervals!=null){							
				for(int i = 0;(i<node.leftIntervals.size() && node.leftIntervals.get(i).intersects(q));i++){
					intersections.add(node.rightIntervals.get(i));	// add the intervals that intersect
				}
			}														// add the intervals to the right
			intersections.addAll(findIntersectionsInTree(node.leftChild, q));
			
		}
			
		return intersections;
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
}

