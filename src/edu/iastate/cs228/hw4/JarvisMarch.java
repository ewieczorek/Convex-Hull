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

public class JarvisMarch extends ConvexHull
{
	// last element in pointsNoDuplicate(), i.e., highest of all points (and the rightmost one in case of a tie)
	private Point highestPoint; 
	
	// left chain of the convex hull counterclockwise from lowestPoint to highestPoint
	private PureStack<Point> leftChain; 
	
	// right chain of the convex hull counterclockwise from highestPoint to lowestPoint
	private PureStack<Point> rightChain; 
		

	/**
	 * Call corresponding constructor of the super class.  Initialize the variable algorithm 
	 * (from the class ConvexHull). Set highestPoint. Initialize the two stacks leftChain 
	 * and rightChain. 
	 * 
	 * @param n  number of points 
	 * @throws IllegalArgumentException  when pts.length == 0
	 */
	public JarvisMarch(Point[] pts) throws IllegalArgumentException 
	{
		super(pts); 
		algorithm = "Jarvis March";
		leftChain = new ArrayBasedStack<Point>();
		rightChain = new ArrayBasedStack<Point>();
		highestPoint = pointsNoDuplicate[pointsNoDuplicate.length - 1];
	}

	
	/**
	 * Call corresponding constructor of the superclass.  Initialize the variable algorithm.
	 * Set highestPoint.  Initialize leftChain and rightChain.  
	 * 
	 * @param  inputFileName
	 * @throws FileNotFoundException
	 * @throws InputMismatchException   when the input file contains an odd number of integers
	 */
	public JarvisMarch(String inputFileName) throws FileNotFoundException, InputMismatchException
	{
		super(inputFileName); 
		try{
			algorithm = "Jarvis March";
			leftChain = new ArrayBasedStack<Point>();
			rightChain = new ArrayBasedStack<Point>();
			highestPoint = pointsNoDuplicate[pointsNoDuplicate.length - 1];
		}catch (NullPointerException e1){
			//if file is not found this will give null pointer exception and i'm just trying to avoid that
		}
	}


	// ------------
	// Javis' march
	// ------------

	/**
	 * Calls createRightChain() and createLeftChain().  Merge the two chains stored on the stacks  
	 * rightChain and leftChain into the array hullVertices[].
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
			createRightChain();
			createLeftChain();
			hullVertices = new Point[leftChain.size() + rightChain.size() - 2];
			int i = hullVertices.length - 1;
			hullVertices[0] = leftChain.pop();
			while(!leftChain.isEmpty()){
				hullVertices[i]= leftChain.pop();
				i--;
			}
			rightChain.pop();
			while(rightChain.size() > 1){
				hullVertices[i]= rightChain.pop();
				i--;
			}
			
		}
		stoptime = System.nanoTime();
		this.time = stoptime- starttime;
	}
	
	
	/**
	 * Construct the right chain of the convex hull.  Starts at lowestPoint and wrap around the 
	 * points counterclockwise.  For every new vertex v of the convex hull, call nextVertex()
	 * to determine the next vertex, which has the smallest polar angle with respect to v.  Stop 
	 * when the highest point is reached.  
	 * 
	 * Use the stack rightChain to carry out the operation.  
	 * 
	 * Ought to be private, but is made public for testing convenience. 
	 */
	public void createRightChain()
	{
		rightChain.push(pointsNoDuplicate[0]); //starts at lowest point
		while(rightChain.peek().compareTo(highestPoint) != 0){ //goes until it hits highest point
			rightChain.push(nextVertex(rightChain.peek()));
		}
	}
	
	
	/**
	 * Construct the left chain of the convex hull.  Starts at highestPoint and continues the 
	 * counterclockwise wrapping.  Stop when lowestPoint is reached.  
	 * 
	 * Use the stack leftChain to carry out the operation. 
	 * 
	 * Ought to be private, but is made public for testing convenience. 
	 */
	public void createLeftChain()
	{
		leftChain.push(highestPoint); //starts at highest point
		while(leftChain.peek().compareTo(lowestPoint) != 0 ){ //goes until it hits lowest point
			leftChain.push(nextVertex(leftChain.peek()));
		}
	}
	
	
	/**
	 * Return the next vertex, which is less than all other points by polar angle with respect
	 * to the current vertex v. When there is a tie, pick the point furthest from v. Comparison 
	 * is done using a PolarAngleComparator object created by the constructor call 
	 * PolarAngleCompartor(v, false).
	 * 
	 * Ought to be private. Made public for testing. 
	 * 
	 * @param v  current vertex 
	 * @return
	 */
	public Point nextVertex(Point v)
	{
		Comparator<Point> comp =  new PolarAngleComparator(v, false);
		Point temp = new Point(pointsNoDuplicate[0]);
		for(int i = 1; i < pointsNoDuplicate.length; i++){
			if(comp.compare(pointsNoDuplicate[i], temp) == -1) temp = new Point(pointsNoDuplicate[i]);
		}
		return temp;
	}
}
