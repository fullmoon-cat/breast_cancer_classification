import java.util.Scanner;

/**
 * Project One: Breast Cancer Classification
 * SOLUTION CODE
 * 
 * BreastCancerClassifySoln contains the core implementation of the 
 * kNearestNeighbors algorithm to classify cell clumps as malignant
 * or benign. When implemented correctly with K = 5, the accuracy 
 * should be 94.4%
 */

/** TODO: Make the grapher use getAllDistances to make that the corresponding test case **/

public class BreastCancerClassifySoln {
	
	static int K = 5;
	static int CLASSIFICATION_INDEX = 10;
	static int BENIGN = 2;
	static int MALIGNANT = 4;
	
	/**
	 * calculateDistance computes the distance between the two data
	 * parameters. The distance is found by taking the difference in each 
	 * "coordinate", squaring it, adding all of those, and then taking the 
	 * square root of the result. 
	 * 
	 * Remember to exclude the first index (the element ID), and the last
	 * index (the classification). 
	 * 
	 * For example: 
	 * [12345, 6, 4, 4, MALIGNANT]
	 * [22344, 2, 8, 3, BENIGN]
	 * 
	 * distance = sqrt((6-2)^2 + (4-8)^2 + (4-3)^2)
	 */
	public static double calculateDistance(int[] first, int[] second)
	{
		double distance = 0;
		for(int i = 1; i < first.length - 1; i++)
		{
			distance += (first[i]-second[i])*(first[i]-second[i]);
		}
		return Math.sqrt(distance);
	}
	
	/**
	 * getAllDistances creates an array of doubles with the distances
	 * to each training instance. The double[] returned should have the 
	 * same number of instances as trainData. 
	 */
	public static double[] getAllDistances(int[][] trainData, int[] testInstance)
	{
		double[] allDistances = new double[trainData.length];
		for(int i = 0; i < trainData.length; i++)
		{
			allDistances[i] = calculateDistance(trainData[i], testInstance);
		}
		return allDistances;
	}
	
	/**
	 * findKClosestEntries finds and returns the indexes of the 
	 * K closest distances in allDistances. Return an array of size K, 
	 * that is filled with the indexes of the closest distances (not
	 * the distances themselves). 
	 * 
	 * Be careful! This function can be tricky.
	 */
	public static int[] findKClosestEntries(double[] allDistances)
	{
		double[] kClosestDistances = new double[K];
		int[] kClosestIndexes = new int[K];
		
		for(int i = 0; i < K; i++)
		{
			kClosestDistances[i] = allDistances[i];
			kClosestIndexes[i] = i;
		}
		
		for(int i = K; i < allDistances.length; i++)
		{
			int maxDistanceIndex = 0;
			for(int k = 1; k < K; k++)
			{
				if(kClosestDistances[k] > kClosestDistances[maxDistanceIndex]) {
					maxDistanceIndex = k;
				}
			}
			if(allDistances[i] < kClosestDistances[maxDistanceIndex]) {
				kClosestDistances[maxDistanceIndex] = allDistances[i];
				kClosestIndexes[maxDistanceIndex] = i;
			}
		}
		
		return kClosestIndexes;
	}
	
	/**
	 * classify makes a decision as to whether an instance of testing 
	 * data is BENIGN or MALIGNANT. The function makes this decision based
	 * on the K closest train data instances (whose indexes are stored in 
	 * kClosestIndexes). If more than half of the closest instances are 
	 * malignant, classify the growth as malignant. 
	 * 
	 * Return one of the global integer constants defined in this function. 
	 */
	public static int classify(int[][] trainData, int[] kClosestIndexes)
	{
		int numMalignant = 0;
		for(int j = 0; j < K; j++)
		{
			if(trainData[kClosestIndexes[j]][CLASSIFICATION_INDEX] == MALIGNANT)
				numMalignant++;
		}
		if(numMalignant > K/2) return MALIGNANT;
		else return BENIGN;				
	}
	
	/**
	 * kNearestNeighbors classifies all the data instances in testData as 
	 * BENIGN or MALIGNANT using the helper functions you wrote and the kNN 
	 * algorithm.
	 * 
	 * For each instance of your test data, use your helpers to find the 
	 * K closest points, and classify your result based on that!
	 * @param trainData: all training instances
	 * @param testData: all testing instances
	 * @return: int array of classifications (BENIGN or MALIGNANT)
	 */
	public static int[] kNearestNeighbors(int[][] trainData, int[][] testData){
		int[] myResults = new int[testData.length];
		for(int i = 0; i < testData.length; i++)
		{
			double[] allDistances = getAllDistances(trainData, testData[i]);
			int[] kClosestIndexes = findKClosestEntries(allDistances);
			myResults[i] = classify(trainData, kClosestIndexes);
		}
		return myResults;
	}
	
		
	public static void main(String[] args) {
		int[][] trainData = InputHandler.populateData("train_data.csv");
		int[][] testData = InputHandler.populateData("test_data.csv");

		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("What function would you like to test?");
			System.out.println("Enter the number to test that function, or 'q' to quit");
			System.out.println("1) calculateDistance");
			System.out.println("2) getAllDistances");
			System.out.println("3) run Grapher to plot all pairwise distances");
			System.out.println("4) findKClosestEntries");
			System.out.println("5) classify");
			System.out.println("6) kNearestNeighbors");
			System.out.print(">> ");
			
			String line = sc.nextLine();
			System.out.println("");
			switch(line) {
				case "q": 
					return;
				case "1":
					Tester.calculateDistance();
					break;
				case "2":
					Tester.getAllDistances();
					break;
				case "3":
					//Display the distances between instances of the train data. 
					//Points in the upper left corner (both benign) or in the bottom
					//right (both malignant) should be darker. 
					Grapher.createGraph(trainData);
					break;
				case "4":
					Tester.findKClosestEntries();
					break;
				case "5":
					Tester.classify();
					break;
				case "6":
					int[] myResults = kNearestNeighbors(trainData, testData);
					double acc = InputHandler.printAccuracy(myResults, testData);
					if(Math.abs(acc - 94.37) > 0.1) {
						System.out.println("Expected accuracy: 94.37%");
					} else {
						System.out.println("Congratulations, you've implemented KNN correctly!");
					}
					System.out.println("------------------------------------------------------");
					System.out.println("");

					break;					
				default:
					System.out.println("Invalid input.");
					break;
			}
		}
	}

}
