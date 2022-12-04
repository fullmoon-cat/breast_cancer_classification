/**
 * Project One: Breast Cancer Classification
 * SOLUTION CODE
 * 
 * BreastCancerClassifySoln contains the core implementation of the 
 * kNearestNeighbors algorithm to classify cell clumps as malignant
 * or benign. When implemented correctly with K = 5, the accuracy 
 * should be 94.4%
 */

public class ParkinsonsSoln {
	
	static int K = 5;
	static int CLASSIFICATION_INDEX = 23;
	static int BENIGN = 0;
	static int MALIGNANT = 1;
	static double[] normalizationFactors;
	
	public static void normalize(double[][] trainData) 
	{
		normalizationFactors = new double[trainData[0].length];
		for(int i = 1; i < trainData[0].length; i++)
		{
			double min = 0;
			for(int j = 0; j < trainData.length; j++) 
			{
				if(normalizationFactors[i] < trainData[j][i]) 
					normalizationFactors[i] = trainData[j][i];
				if(min > trainData[j][i]) min = trainData[j][i];
			}
			if(min < 0) {
				normalizationFactors[i] = min;
				min = 0;
			}
		}
	}
	
	
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
	public static double calculateDistance(double[] first, double[] second)
	{
		double distance = 0;
		for(int i = 1; i < first.length - 1; i++)
		{
			distance += Math.pow((first[i]/normalizationFactors[i]-
					second[i]/normalizationFactors[i]), 2);
		}
		return Math.sqrt(distance);
	}
	
	/**
	 * getAllDistances creates an array of doubles with the distances
	 * to each training instance. The double[] returned should have the 
	 * same number of instances as trainData. 
	 */
	public static double[] getAllDistances(double[][] trainData, double[] testInstance)
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
	public static int classify(double[][] trainData, int[] kClosestIndexes)
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
	public static int[] kNearestNeighbors(double[][] trainData, double[][] testData){
		normalize(trainData);
		int[] myResults = new int[testData.length];
		for(int i = 0; i < testData.length; i++)
		{
			double[] allDistances = getAllDistances(trainData, testData[i]);
			int[] kClosestIndexes = findKClosestEntries(allDistances);
			myResults[i] = classify(trainData, kClosestIndexes);
			System.out.println(myResults[i]);
		}
		return myResults;
	}
	
	
	public static void main(String[] args) {
		double[][] trainData = InputHandlerParkinsons.populateData("parkinsons_data.csv");
		double[][] testData = InputHandlerParkinsons.populateData("parkinsons_data_test.csv");
		
		//Display the distances between instances of the train data. 
		//Points in the upper left corner (both benign) or in the bottom
		//right (both malignant) should be darker. 
		//Grapher.createGraph(trainData);

		int[] myResults = kNearestNeighbors(trainData, testData);
		int[] fakeResults = new int[testData.length];
		
		InputHandlerParkinsons.printAccuracy(fakeResults, testData);
	}

}
