package structures;

import java.util.ArrayList;

/**
 * This class is a repository of sorting methods used by the interval tree.
 * It's a utility class - all methods are static, and the class cannot be instantiated
 * i.e. no objects can be created for this class.
 * 
 * @author runb-cs112
 */
public class Sorter {

	private Sorter() { }
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		for(int a=0;a<intervals.size();a++){			//Make an iteration for every element in the array
			int i = 0;
			for(int b=1;b<(intervals.size()-a);b++){ 	//Make an iteration of the elements that haven't been moved
				if(lr=='l' && intervals.get(b).leftEndPoint < intervals.get(i).leftEndPoint )i=b;
				if(lr=='r' && intervals.get(b).rightEndPoint < intervals.get(i).rightEndPoint )i=b;
			}
			intervals.add(intervals.remove(i));			//Find the smallest end point and add it to the back of the array
		}
	}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		int size = rightSortedIntervals.size();
		boolean changes;
		do{
			changes = false;
			int endPoint = rightSortedIntervals.get(size-1).rightEndPoint;
			for(int c = 0;c<leftSortedIntervals.size();c++){
				int[] endpoints = {leftSortedIntervals.get(c).leftEndPoint,
						leftSortedIntervals.get(c).rightEndPoint,
						rightSortedIntervals.get(c).leftEndPoint,
						rightSortedIntervals.get(c).rightEndPoint};
				for(int temp:endpoints)if((ret.isEmpty() || temp > ret.get(ret.size()-1)) && temp<endPoint) endPoint=temp;		
			}	//Find the next smallest end point in the interval and add it to the return statement
			if(ret.isEmpty() || endPoint > ret.get(ret.size()-1)){ //If the next smallest is greater than the most recent endPoint add it
				ret.add(endPoint);
				changes=true;
			}
		}while(changes); //keep adding the next smallest endPoint until it iterates and can't find one
		return ret;
	}
}












