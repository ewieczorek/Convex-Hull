package edu.iastate.cs228.hw4;

/**
 *  
 * @author Ethan Wieczorek
 *
 */

import java.util.Arrays;
import java.util.Comparator;


/**
 * This class sorts an array of Point objects using a provided Comparator.  You may 
 * modify your implementation of quicksort from Project 2.  
 */

public class QuickSortPoints
{
	private Point[] points;  	// Array of points to be sorted.
	

	/**
	 * Constructor takes an array of Point objects. 
	 * 
	 * @param pts
	 */
	QuickSortPoints(Point[] pts)
	{
		points = new Point[pts.length];
		for (int i = pts.length - 1; i >= 0; --i) {
		    Point p = pts[i];
		    if (p != null) { 
		        points[i] = new Point(p);
		    }
		}
	}
	
	
	/**
	 * Copy the sorted array to pts[]. 
	 * 
	 * @param pts  array to copy onto
	 */
	void getSortedPoints(Point[] pts)
	{
		for (int i = points.length - 1; i >= 0; --i) {
		    Point p = points[i];
		    if (p != null) { 
		        pts[i] = new Point(p);
		    }
		}
	}

	
	/**
	 * Perform quicksort on the array points[] with a supplied comparator. 
	 * 
	 * @param arr
	 * @param comp
	 */
	public void quickSort(Comparator<Point> comp)
	{
		this.quickSortRec(0, (points.length - 1), comp);
	}
	
	
	/**
	 * Operates on the subarray of points[] with indices between first and last. 
	 * 
	 * @param first  starting index of the subarray
	 * @param last   ending index of the subarray
	 */
	private void quickSortRec(int first, int last, Comparator<Point> comp)
	{
		if (last - first >= 1){                   
			if (first < last) {
				int i = this.partition(first, last, comp);
				if(i > 0){
					this.quickSortRec(first, i - 1, comp);
				}
				if(i < (last - 1)){
					this.quickSortRec(i + 1, last, comp);
				}
			}
		} else {
	            return;                     
	    }
	}
	

	/**
	 * Operates on the subarray of points[] with indices between first and last.
	 * 
	 * @param first
	 * @param last
	 * @return
	 */
	private int partition(int first, int last, Comparator<Point> comp)
	{
		int i = first;
        int j = last;

        Point pivot = points[first];

        while (j > i)
        {
                while (comp.compare(points[i], pivot) < 1 && i <= last && j > i)  
                	i++; 
                while (comp.compare(points[j], pivot) == 1 && j >= first && j >= i) 
                    j--;
                if (j > i) {
                	Point temp = points[i];
        			points[i] = points[j];
        			points[j] = temp;
                }
        }
        Point temp = points[first];
		points[first] = points[j];
		points[j] = temp;
        return j;
	}
}

