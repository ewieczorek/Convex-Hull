package edu.iastate.cs228.hw4;

/**
 *  
 * @author Ethan Wieczorek
 *
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner; 
import java.util.Random; 

public class CompareHullAlgorithms 
{
	/**
	 * Repeatedly take points either randomly generated or read from files. Perform Graham's scan and 
	 * Jarvis' march over the input set of points, comparing their performances.  
	 * 
	 * @param args
	 * @throws InputMismatchException 
	 * @throws IOException 
	 **/
	public static void main(String[] args) throws InputMismatchException, IOException 
	{		

		Boolean exit = false;
		Random generator = new Random(); 
		
		while(!exit){
			ConvexHull[] algorithms = new ConvexHull[2]; 
			//keys: 1 (for random points) 2 (file input) 3 (exit) 
			System.out.println("Enter 1 to generate random points");
			System.out.println("Enter 2 to read points from an input file");
			System.out.println("Enter 3 to exit");
			
			BufferedReader r = new BufferedReader (new InputStreamReader (System.in));
			String userinput = null;	
			Integer userkey = null;
			Integer userorder = null;
			while (userkey == null){ //this is going to get the key from the user using a regular expression to make sure they input a number
				while (userinput == null){
					userinput = r.readLine();
					if(userinput.matches("\\d")) {
						if (Integer.parseInt(userinput) < 4 && Integer.parseInt(userinput) > 0){
							userkey = Integer.parseInt(userinput);
						}else{
					    	System.err.println("Please enter a valid number");
					    }
					    
					}else {
						 System.err.println("Please enter a valid number");
					}
				}
				userinput = null;
			}
			
			if(userkey == 3){ //if the user enters 3 the program closes
				System.exit(0);
			}
			userinput = null;
			
			if(userkey == 1){
				userkey = null;
				int numpoints = 0;
				System.out.println("Enter how many random points you would like");	
				while (userinput == null || numpoints == 0){
					userinput = r.readLine();
					if(userinput.matches("\\d+?")) {
						numpoints = Integer.parseInt(userinput);
					}else {
						 System.err.println("Please enter a valid number");
					}
				}
				userinput = null;
				
				//    1) If the input are random points, calls generateRandomPoints() to initialize an array 
				//       pts[] of random points. Use pts[] to create two objects of GrahamScan and JarvisMarch, 
				//       respectively.
				
				Point[] randompoints = generateRandomPoints(numpoints, generator);
				algorithms[0] = new GrahamScan(randompoints);
				algorithms[1] = new JarvisMarch(randompoints);
				

			}else if(userkey == 2){
				userkey = null;
				System.out.println("Enter the name of the file in the project folder\n\".txt\" is added automatically");	
				String inputFileName = "";
				Boolean continues = false;
				while (userinput == null || continues == false){
					userinput = r.readLine();
					if(userinput.matches("\\w+")) {
						inputFileName = userinput + ".txt";
						continues = true;
					}else{
						System.err.println("Please enter a valid file name");
					}
				}
				userinput = null;
				
				//    2) If the input is from a file, construct two objects of the classes GrahamScan and  
				//       JarvisMarch, respectively, using the file.     
				
				algorithms[0] = new GrahamScan(inputFileName);// Conducts multiple rounds of convex hull construction. Within each round, performs the following: 
				algorithms[1] = new JarvisMarch(inputFileName);
				
			}
			try{
				System.out.format("%-15s %10s %10s\n", "algorithm", "size", "time (ns)");
				System.out.println("--------------------------------------");
				for(int i = 0; i < 2; i++){
					algorithms[i].constructHull();		//    3) Have each object call constructHull() to build the convex hull of the input points.
					System.out.println(algorithms[i].stats());		//    4) Meanwhile, prints out the table of runtime statistics.
					algorithms[i].draw();
					algorithms[i].writeHullToFile();
					//System.out.println(algorithms[i].toString()); //for testing purposes
				}
				System.out.println("--------------------------------------\n");
			}catch(NullPointerException e1){
				
			}
		}
		// Within a hull construction round, have each algorithm call the constructHull() and draw()
		// methods in the ConvexHull class.  You can visually check the result. (Windows 
		// have to be closed manually before rerun.)  Also, print out the statistics table 
		// (see above). 
		
		}
	
	
	/**
	 * This method generates a given number of random points.  The coordinates of these points are 
	 * pseudo-random numbers within the range [-50,50] × [-50,50]. 
	 * 
	 * Reuse your implementation of this method in the CompareSorter class from Project 2.
	 * 
	 * @param numPts  	number of points
	 * @param rand      Random object to allow seeding of the random number generator
	 * @throws IllegalArgumentException if numPts < 1
	 */
	private static Point[] generateRandomPoints(int numPts, Random rand) throws IllegalArgumentException
	{ 
		if(numPts < 1){
			throw new IllegalArgumentException();
		}
		Point[] temp = new Point[numPts];
		for(int i = 0; i < numPts; i++){
			temp[i] = new Point((rand.nextInt(101) - 50), (rand.nextInt(101) - 50)); //pseudo-random numbers within the range [-50,50] × [-50,50].
		}
			return temp;  
	}
}
