package edu.iastate.cs228.hw4;

/**
 *  
 * @author Ethan Wieczorek
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException; 
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException; 
import java.io.PrintWriter;
import java.util.Random; 
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;







/**
 * 
 * This class implements construction of the convex hull of a finite set of points. 
 *
 */

public abstract class ConvexHull 
{
	// ---------------
	// Data Structures 
	// ---------------
	protected String algorithm;  // has value either "Graham's scan" or "Jarvis' march". Initialized by a subclass.
	
	protected long time;         // execution time in nanoseconds
	
	/**
	 * The array points[] holds an input set of Points, which may be randomly generated or 
	 * input from a file.  Duplicates are possible. 
	 */
	private Point[] points;    
	

	/**
	 * Lowest point from points[]; and in case of a tie, the leftmost one of all such points. 
	 * To be set by a constructor. 
	 */
	protected Point lowestPoint; 

	
	/**
	 * This array stores the same set of points from points[] with all duplicates removed. 
	 * These are the points on which Graham's scan and Jarvis' march will be performed. 
	 */
	protected Point[] pointsNoDuplicate; 
	
	
	/**
	 * Vertices of the convex hull in counterclockwise order are stored in the array 
	 * hullVertices[], with hullVertices[0] storing lowestPoint. 
	 */
	protected Point[] hullVertices;
	
	
	protected QuickSortPoints quicksorter;  // used (and reset) by this class and its subclass GrahamScan

	
	
	// ------------
	// Constructors
	// ------------
	
	
	/**
	 * Constructor over an array of points.  
	 * 
	 *    1) Store the points in the private array points[].
	 *    
	 *    2) Initialize quicksorter. 
	 *    
	 *    3) Call removeDuplicates() to store distinct points from the input in pointsNoDuplicate[].
	 *    
	 *    4) Set lowestPoint to pointsNoDuplicate[0]. 
	 * 
	 * @param pts
	 * @throws IllegalArgumentException  if pts.length == 0
	 */
	public ConvexHull(Point[] pts) throws IllegalArgumentException 
	{
		points = new Point[pts.length]; //1) Store the points in the private array points[].
		for (int i = pts.length - 1; i >= 0; --i) {
		    Point p = pts[i];
		    if (p != null) { 
		        points[i] = new Point(p);
		    }
		}
		quicksorter = new QuickSortPoints(points); //2) Initialize quicksorter. 
		this.removeDuplicates(); //3) Call removeDuplicates() to store distinct points from the input in pointsNoDuplicate[].
		lowestPoint = pointsNoDuplicate[0]; //4) Set lowestPoint to pointsNoDuplicate[0].
	}
	
	/**
	 * Read integers from an input file.  Every pair of integers represent the x- and y-coordinates 
	 * of a point.  Generate the points and store them in the private array points[]. The total 
	 * number of integers in the file must be even.
	 * 
	 * You may declare a Scanner object and call its methods such as hasNext(), hasNextInt() 
	 * and nextInt(). An ArrayList may be used to store the input integers as they are read in 
	 * from the file.  
	 * 
	 * Perform the operations 1)-4) for the first constructor. 
	 * 
	 * @param  inputFileName
	 * @throws FileNotFoundException
	 * @throws InputMismatchException   when the input file contains an odd number of integers
	 */
	public ConvexHull(String inputFileName) throws FileNotFoundException, InputMismatchException
	{
		try{ //this is 100% exactly copied from my project 2
			BufferedReader lineReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
			List<Integer> temp = new ArrayList<Integer>();
			Pattern p = Pattern.compile("(-?)(\\d+)\\s?");
			String widthLine = "";
			try{
			while (!(widthLine = lineReader.readLine()).isEmpty()){
				Matcher m = p.matcher(widthLine); 
				while (m.find()) {					
					if(!m.group(1).isEmpty()){
						if(!m.group(2).isEmpty()){
							temp.add(Integer.parseInt(m.group(2))*-1);
						}
					}else if(m.group(1).isEmpty()){
						if(!m.group(2).isEmpty()){
							temp.add((!m.group(2).isEmpty()) ? Integer.parseInt(m.group(2)) : null);
						}
					}
				}
			}
			}catch(NullPointerException n1){
				
			}
			lineReader.close();
			
			if(temp.size() %2 != 0) {InputMismatchException e1 = new InputMismatchException(); System.out.println(e1.getMessage());}
			
			this.points = new Point[temp.size()/2];
			int j = 0;
			for(int i = 0; i < temp.size() - 1; i++){
				this.points[j] = new Point(temp.get(i), temp.get(i+1));
				i++;
				j++;
				
			}
			this.lowestPoint = new Point(50,50);
			for( int i = 0; i < points.length; i++){
				if(points[i].getY() < this.lowestPoint.getY()) this.lowestPoint = points[i];
				if(points[i].getY() == this.lowestPoint.getY() && points[i].getX() < this.lowestPoint.getX()) this.lowestPoint = points[i];
			}
			quicksorter = new QuickSortPoints(points);
			removeDuplicates();
			lowestPoint = pointsNoDuplicate[0];
			
		} catch (FileNotFoundException ex) {
		    System.out.println("File Not Found");
		} catch (IOException ex2) {
			System.out.println("IO Exception");
		}
	}

	
	/**
	 * Construct the convex hull of the points in the array pointsNoDuplicate[]. 
	 */
	public abstract void constructHull(); 

	
		
	/**
	 * Outputs performance statistics in the format: 
	 * 
	 * <convex hull algorithm> <size>  <time>
	 * 
	 * For instance, 
	 * 
	 * Graham's scan   1000	  9200867
	 * 
	 * Use the spacing in the sample run in Section 4 of the project description. 
	 */
	public String stats()
	{
		return String.format("%-15s %10d %10d", algorithm, points.length, time); //<convex hull algorithm> <size>  <time> formatted correctly
	}
	
	
	/**
	 * The string displays the convex hull with vertices in counterclockwise order starting at  
	 * lowestPoint.  When printed out, it will list five points per line with three blanks in 
	 * between. Every point appears in the format "(x, y)".  
	 * 
	 * For illustration, the convex hull example in the project description will have its 
	 * toString() generate the output below: 
	 * 
	 * (-7, -10)   (0, -10)   (10, 5)   (0, 8)   (-10, 0)   
	 * 
	 * lowestPoint is listed only ONCE.  
	 */
	public String toString()
	{
		String temp = "";
		int width = 0;
		for(int i = 0; i < hullVertices.length; i++){//The string displays the convex hull with vertices in counterclockwise order starting at lowestPoint
			temp += hullVertices[i].toString() + "   "; //with three blanks in between
			width++;
			if(width == 5){ //When printed out, it will list five points per line 
				temp += "\n";
				width = 0;
			}
		}
		return temp;
	}
	
	
	/** 
	 * 
	 * Writes to the file "hull.txt" the vertices of the constructed convex hull in counterclockwise 
	 * order.  These vertices are in the array hullVertices[], starting with lowestPoint.  Every line
	 * in the file displays the x and y coordinates of only one point.  
	 * 
	 * For instance, the file "hull.txt" generated for the convex hull example in the project 
	 * description will have the following content: 
	 * 
     *  -7 -10 
     *  0 -10
     *  10 5
     *  0  8
     *  -10 0
	 * 
	 * The generated file is useful for debugging as well as grading. 
	 * 
	 * Called only after constructHull().  
	 * 
	 * 
	 * @throws IllegalStateException  if hullVertices[] has not been populated (i.e., the convex 
	 *                                   hull has not been constructed)
	 */
	public void writeHullToFile() throws IllegalStateException 
	{
		 try{
			 BufferedWriter output = null;
			 output = new BufferedWriter(new FileWriter("hull.txt"));
			 for(int i = 0; i < hullVertices.length; i++){
				 output.write(hullVertices[i].toString() + "\n");
			 }
			 output.close();
		 }catch(FileNotFoundException e1){
			 System.out.println("Output File Not Found");
		 }catch(IOException e2){
			 
		 }
	}
	

	/**
	 * Draw the points and their convex hull.  This method is called after construction of the 
	 * convex hull.  You just need to make use of hullVertices[] to generate a list of segments 
	 * as the edges. Then create a Plot object to call the method myFrame().  
	 */
	public void draw()
	{		
		int numSegs = hullVertices.length;  // number of segments to draw 

		// Based on Section 4.1, generate the line segments to draw for display of the convex hull.
		// Assign their number to numSegs, and store them in segments[] in the order. 
		Segment[] segments = new Segment[numSegs];  
		
		for(int j = 1; j < hullVertices.length; j++){
			segments[j-1] = new Segment(hullVertices[j-1], hullVertices[j]);
		}
		//the following adds a segment between the last point and the first point, which was not included in the loop. It stores this in the final spot of hullVertices[]
		segments[hullVertices.length-1] = new Segment(hullVertices[hullVertices.length-1], hullVertices[0]);

		// The following statement creates a window to display the convex hull.
		Plot.myFrame(pointsNoDuplicate, segments, getClass().getName());
		
	}

		
	/**
	 * Sort the array points[] by y-coordinate in the non-decreasing order.  Have quicksorter 
	 * invoke quicksort() with a comparator object which uses the compareTo() method of the Point 
	 * class. Copy the sorted sequence onto the array pointsNoDuplicate[] with duplicates removed.
	 *     
	 * Ought to be private, but is made public for testing convenience. 
	 */
	public void removeDuplicates()
	{
		Comparator<Point> comp = new Comparator<Point>() {
			public int compare(Point p1, Point p2) {
				return p1.compareTo(p2);
			}
		};
		
		quicksorter.quickSort(comp);
		Point[] pointsWithDuplicates = new Point[points.length]; 
		quicksorter.getSortedPoints(pointsWithDuplicates);
		int newLength = 1;
		
		for(int i = 1; i < pointsWithDuplicates.length; i++){
			if(pointsWithDuplicates[i].compareTo(pointsWithDuplicates[i-1]) != 0){
				newLength++;
			}
		}
		pointsNoDuplicate = new Point[newLength];
		int iter = 1;
		pointsNoDuplicate[0] = new Point(pointsWithDuplicates[0]);
		for(int i = 1; i < pointsWithDuplicates.length; i++){
			if(pointsWithDuplicates[i].compareTo(pointsWithDuplicates[i-1]) != 0){
				pointsNoDuplicate[iter] = new Point(pointsWithDuplicates[i]);
				iter++;
			}
		}
		
		/*
		// I really just wanted to see if this was possible but i couldn't quite get it to work.
		// It sometimes didn't retain the order for some reason.
		ArrayList<Point> templist = new ArrayList<Point>();
		for(int i = 1; i < pointsWithDuplicates.length; i++){
			templist.add(pointsWithDuplicates[i]);
		}
		Set<Point> pointset = new LinkedHashSet<Point>(templist);
		templist.clear();
		templist.addAll(pointset);	
		
		pointsNoDuplicate = new Point[templist.size()];
		Iterator<Point> iter = templist.iterator();
		for(int i =0; i < templist.size(); i++){
			pointsNoDuplicate[i] = new Point(iter.next());
		}
	*/
	}
}
