package edu.iastate.cs228.hw4;

/**
 *  
 * @author Ethan Wieczorek
 *
 */

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.ArrayList; 

public class GrahamScan extends ConvexHull
{
	/**
	 * Stack used by Grahma's scan to store the vertices of the convex hull of the points 
	 * scanned so far.  At the end of the scan, it stores the hull vertices in the 
	 * counterclockwise order. 
	 */
	private PureStack<Point> vertexStack;  


	/**
	 * Call corresponding constructor of the super class.  Initialize the variables algorithm 
	 * (from the class ConvexHull) and vertexStack. 
	 * 
	 * @param n  number of points 
	 * @throws IllegalArgumentException  if pts.length == 0
	 */
	public GrahamScan(Point[] pts) throws IllegalArgumentException 
	{
		super(pts); 
		algorithm = "Graham's Scan";
		vertexStack = new ArrayBasedStack<Point>();
	}
	

	/**
	 * Call corresponding constructor of the super class.  Initialize algorithm and vertexStack.  
	 * 
	 * @param  inputFileName
	 * @throws FileNotFoundException
	 * @throws InputMismatchException   when the input file contains an odd number of integers
	 */
	public GrahamScan(String inputFileName) throws FileNotFoundException, InputMismatchException
	{
		super(inputFileName); 
		super.algorithm = "Graham's Scan";
		vertexStack = new ArrayBasedStack<Point>();
	}

	
	// -------------
	// Graham's scan
	// -------------
	
	/**
	 * This method carries out Graham's scan in several steps below: 
	 * 
	 *     1) Call the private method setUpScan() to sort all the points in the array 
	 *        pointsNoDuplicate[] by polar angle with respect to lowestPoint.    
	 *        
	 *     2) Perform Graham's scan. To initialize the scan, push pointsNoDuplicate[0] and 
	 *        pointsNoDuplicate[1] onto vertexStack.  
	 * 
     *     3) As the scan terminates, vertexStack holds the vertices of the convex hull.  Pop the 
     *        vertices out of the stack and add them to the array hullVertices[], starting at index
     *        vertexStack.size() - 1, and decreasing the index toward 0.    
     *        
     * Two degenerate cases below must be handled: 
     * 
     *     1) The array pointsNoDuplicates[] contains just one point, in which case the convex
     *        hull is the point itself. 
     *     
     *     2) The array contains only two points, in which case the hull is the line segment 
     *        connecting them.   
	 */
	public void constructHull()
	{
		Comparator<Point> comp =  new PolarAngleComparator(lowestPoint, true);
		setUpScan();
		long starttime = 0;
		long stoptime = 0;
		starttime = System.nanoTime();
		if(pointsNoDuplicate.length == 1){
			hullVertices = new Point[1];
			hullVertices[0] = new Point(pointsNoDuplicate[0]);
		}else if(pointsNoDuplicate.length == 2){
			hullVertices = new Point[2];
			hullVertices[0] = new Point(pointsNoDuplicate[0]);
			hullVertices[1] = new Point(pointsNoDuplicate[1]);
		}else{
			Point tempfirstpoint = new Point(pointsNoDuplicate[1]);
			vertexStack.push(pointsNoDuplicate[0]); // the first point is the lowest point and also the first point of the convex hull 
			vertexStack.push(pointsNoDuplicate[1]); //the second point is the point with the smallest polar angle so it is the second point in the convex hull
			vertexStack.push(pointsNoDuplicate[2]); 
			for (int i = 3; i < pointsNoDuplicate.length; i++){ 
				comp =  new PolarAngleComparator(tempfirstpoint, true);
				
				while(comp.compare(pointsNoDuplicate[i], vertexStack.peek()) == -1) {
					vertexStack.pop();
										
					Point temp = new Point(vertexStack.pop());
					tempfirstpoint = new Point(vertexStack.peek());
					vertexStack.push(temp);	
					comp =  new PolarAngleComparator(tempfirstpoint, true);
				}
				
				tempfirstpoint = new Point(vertexStack.peek());
				
				vertexStack.push(pointsNoDuplicate[i]);
				
			}
			hullVertices = new Point[vertexStack.size()];
			int i = vertexStack.size() -1;
			while(!vertexStack.isEmpty()){
				hullVertices[i]= vertexStack.pop();
				i--;
			}
		}
		stoptime = System.nanoTime();
		this.time = stoptime- starttime;
	}
	
	
	/**
	 * Set the variable quicksorter from the class ConvexHull to sort by polar angle with respect 
	 * to lowestPoint, and call quickSort() from the QuickSortPoints class on pointsNoDupliate[]. 
	 * The argument supplied to quickSort() is an object created by the constructor call 
	 * PolarAngleComparator(lowestPoint, true).       
	 * 
	 * Ought to be private, but is made public for testing convenience. 
	 *
	 */
	public void setUpScan()
	{
		Comparator<Point> comp =  new PolarAngleComparator(lowestPoint, true);
		quicksorter = new QuickSortPoints(pointsNoDuplicate);
		quicksorter.quickSort(comp);
		quicksorter.getSortedPoints(pointsNoDuplicate);
	}	
}
