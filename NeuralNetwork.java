/*
//	Name:	Eric Ortiz
// CWID:	102-39-903
//	Date:	10/5/18
//	Assignment #2
//	Desc:	This program is a neural network of sigmoid neruons that are programmed to train themselves to learn individual
//				handwritten digits from the MNIST data set.
*/

import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import javafx.util.Pair; 
import java.util.ArrayList; 

/* Main Class */
public class NeuralNetwork {
	// set up the constant of "e" - the base of the natural logarithm
	final static double e = 2.71828;
	final static int epochs = 2;
	final static int miniBatchSize = 10;
	final static double eta = 3.0;
	
	final static int inputSize = 784;
	final static int trainingSize = 60000;
	final static int testingSize = 10000;
	final static String saveFileName = "test.csv";
	final static String trainFileName = "mnist_train.csv";
	final static String testFileName = "mnist_test.csv";
	
	static boolean firstRun = true;
	
	static int layers;
	static int[] layerSizes = {784, 30, 10};
	static Matrix[] biases;
	static Matrix[] weights;
	
	static List<Pair<Integer,float[]>> testingData = new ArrayList<>();
	static List<Pair<Integer,float[]>> trainingData = new ArrayList<>();
	
	static int[][] accuracyPairs = new int[11][2];
	
	static Scanner scanner;
	
	
	//static List<Pair<Integer,float[]>> tempData = new ArrayList<>();
	
	/* Main Function */
	public static void main(String[] args) {
		// print the welcome statement
		System.out.println("\fWelcome to Eric Ortiz's MNIST Digit Recognizer!\n");
		
		// create the network and load in the training set and the testing set
		createNetwork(true);
		try {
			loadDataSets();
		} catch (FileNotFoundException e) {
			System.out.println("Error loading data sets - " + e);
		}
		
		// start a scanner to read user input
		scanner = new Scanner(System.in);
		
		// main operation loop where the user can continually submit input until they exit (or the program crashes :D)
		while(true) {
			// show the user the available commands after every iteration
			printOptions();
			
			// grab the user's input
			String input = scanner.next();
			
			try {
				int response = Integer.parseInt(input);
				
				// if the response was zero, break from the for-loop to exit the program, otherwise, perform the function requested
				if (response == 0)
					break;
				else
					parseInput(response);
				
			} catch (Exception e) {
				System.out.println("Error parsing your input - please only enter digits\n");
			}
		}
		
		// exit the program with a goodbye message
		System.out.println("\nGoodbye!\n");
		System.exit(0);
	}
	
	/* Create a New Neural Network */
	private static void createNetwork(boolean prefilledNetwork) {
		// this method creates all of the randomized weights and biases for the neural network based on our amount of layers
		// and each of their sizes
		
		// first declare the arrays of matrices for the weights and biases to be the size of the neural network minus 1
		layers = layerSizes.length;
		biases = new Matrix[layers-1];
		weights = new Matrix[layers-1];
		
		// create each new bias matrix with randomized values
		for (int i = 1; i < layers; i++){
			biases[i-1] = new Matrix(layerSizes[i], 1, prefilledNetwork);
		}
		
		// create each new weight matrix with randomized values
		for (int i = 0; i < layers-1; i++) {
			weights[i] = new Matrix(layerSizes[i+1], layerSizes[i], prefilledNetwork);
		}
	}
	
	/* Load In Training and Testing Sets */
	private static void loadDataSets() throws FileNotFoundException {
		
		// start a scanner to scan through the testing file for input data
		Scanner fileScanner = new Scanner(new File(testFileName)).useDelimiter("[,\n]");
		
		// for the size of the expected training data, we need to grab all of the data on a row
		for (int i = 0; i < testingSize; i++) {
			// grab the row as a single string, then split that string into an array of strings with a comma as a delimiter
			String inputRow = fileScanner.nextLine();
			String[] stringInputs = inputRow.split(",");
			
			// grab the first string integer and store that as the correct value for the input
			Integer firstInteger = new Integer(stringInputs[0]);
			// create a parallel array of floats that will take each item in the row, normalize it, and store it into itself
			float[] inputs = new float[inputSize];
			
			for (int j = 1; j < stringInputs.length-1; j++) {
				Float tempFloat = Float.parseFloat(stringInputs[j]);
				inputs[j] = tempFloat / 255;
			}
			
			// create and store the pair of correct integer output and the float array of normalized output
			testingData.add(new Pair<Integer,float[]>(firstInteger, inputs));
		}
		
		System.out.println("\nTesting Data Retrieved");
		
		//printVector(testingData.get(9999).getValue());
		
		// close the scanner for that file, open the training data file, and start the process again for that data
		fileScanner.close();
		fileScanner = new Scanner(new File(trainFileName)).useDelimiter("[,\n]");
		
		for (int i = 0; i < trainingSize; i++) {
			String inputRow = fileScanner.nextLine();
			String[] stringInputs = inputRow.split(",");
			
			Integer firstInteger = new Integer(stringInputs[0]);
			float[] inputs = new float[inputSize];
			
			for (int j = 1; j < stringInputs.length-1; j++) {
				Float tempFloat = Float.parseFloat(stringInputs[j]);
				inputs[j] = tempFloat / 255;
			}
			
			trainingData.add(new Pair<Integer,float[]>(firstInteger, inputs));
		}
		
		fileScanner.close();
		
		System.out.println("\nTraining Data Retrieved");
	}
	
	/* Print User Command Options */
	private static void printOptions() {
		System.out.println("\nPlease select an option:");
		System.out.println("\t[1] Train the Network");
		System.out.println("\t[2] Load a Pre-Trained Network");
		
		// if the user hasn't trained or loaded a network yet, then don't show them these following options
		if (!firstRun) {
			System.out.println("\t[3] Display Network Accuracy on TRAINING Set");
			System.out.println("\t[4] Display Network Accuracy on TESTING Set");
			System.out.println("\t[5] Save the Current Network State to File");
		}
		
		System.out.println("\t[0] Exit\n");
	}
	
	/* Parse User Command */
	private static void parseInput(int input) {
		if (input == 1 || input == 2) {
			// this is no longer the program's first run
			if (firstRun)
				firstRun = false;
			
			if (input == 1) {
				SGD();
			}
			else {
				try {
					loadNetworkNew();
				} catch (FileNotFoundException e) {
					System.out.println("Error saving file - " + e.toString());
				}
			}
		}
		else if (input <= 5) {
			// if this is the program's first run, treat the input as invalid
			if (firstRun)
				System.out.println("The integer you submitted is not a valid command.\n");
			else {
				if (input == 3) {
					testNetworkAccuracy(trainingData);
				}
				else if (input == 4) {
					testNetworkAccuracy(testingData);
				}
				else {
					try {
						saveNetworkNew();
					} catch (FileNotFoundException e) {
						System.out.println("Error saving file - " + e.toString());
					}
				}
			}
		}
		else {
			System.out.println("The integer you submitted is not a valid command.\n");
		}
	}
	
	private static void loadNetworkNew() throws FileNotFoundException {
		Scanner fileScanner = new Scanner(new File(saveFileName)).useDelimiter("[,\n]");
		
		for (int l = 0; l < layers-1; l++) {
			fileScanner.nextLine();
			
			for (int i = 0; i < weights[l].rows; i++) {
				String weightRow = fileScanner.nextLine();
				String[] weightStrings = weightRow.split(",");
				
				for (int j = 0; j < weightStrings.length; j++) {
					Float weight = Float.parseFloat(weightStrings[j]);
					weights[l].matrix[i][j] = weight;
				}
			}
			
			fileScanner.nextLine();
			
			for (int i = 0; i < biases[l].rows; i++) {
				String biasRow = fileScanner.nextLine();
				String[] biasStrings = biasRow.split(",");
				
				Float bias = Float.parseFloat(biasStrings[0]);
				biases[l].matrix[i][0] = bias;
			}
		}
		
		fileScanner.close();
	}
	
	private static void saveNetworkNew() throws FileNotFoundException {
		// this method saves all of the weights and biases we currently have for our network to an CSV file that we can later
		// load from
		
		// create a print writer and string builder so we can write a new string to the file
		PrintWriter pw = new PrintWriter(new File(saveFileName));
		StringBuilder sb = new StringBuilder();
		
		for (int l = 0; l < layers-1; l++) {
			sb.append("w" + l + "\n");
			
			for (int i = 0; i < weights[l].rows; i++) {
				for (int j = 0; j < weights[l].columns; j++) {
					sb.append(weights[l].matrix[i][j] + ",");
				}
				sb.append("\n");
			}
			
			sb.append("b" + l + "\n");
			
			for (int i = 0; i < biases[l].rows; i++) {
				sb.append(biases[l].matrix[i][0] + ",\n");
			}
		}
		
		// finally write that mega matrix to a file and close the file
		pw.write(sb.toString());
		pw.close();
	}
	
	/* Stochastic Gradient Descent */
	private static void SGD() {
		//TODO: DELETE THIS SECTION
		// create temp data for now
		/*
		float[] firstInputs = new float[] {0, 1, 0, 1};
		float[] secondInputs = new float[] {1, 0, 1, 0};
		float[] thirdInputs = new float[] {0, 0, 1, 1};
		float[] fourthInputs = new float[] {1, 1, 0, 0};
		
		Pair<Integer, float[]> firstPair = new Pair<Integer,float[]>(1, firstInputs);
		Pair<Integer, float[]> secondPair = new Pair<Integer,float[]>(0, secondInputs);
		Pair<Integer, float[]> thirdPair = new Pair<Integer,float[]>(1, thirdInputs);
		Pair<Integer, float[]> fourthPair = new Pair<Integer,float[]>(0, fourthInputs);
		
		tempData.add(firstPair);
		tempData.add(secondPair);
		tempData.add(thirdPair);
		tempData.add(fourthPair);
		
		int tempEpoch = 6;
		int tempMiniBatchSize = 2;
		*/
		long totalStartTime = System.currentTimeMillis();
		long totalEndTime;
		long startTime;
		long endTime;
		
		for (int i = 0; i < epochs; i++) {
			startTime = System.currentTimeMillis();
			
			// shuffle the training data and create mini-batches based off of them
			Collections.shuffle(trainingData);
			
			// reset the accuracy array since this is a new epoch
			accuracyPairs = new int[11][2];
			
			// update the weights and biases for each mini batch
			for (int miniBatch = 0; miniBatch < trainingData.size(); miniBatch += miniBatchSize) {
				/*
				System.out.println("\nMiniBatch - " + miniBatch);
				System.out.println("\nBiases for Epoch " + (i+1));
				for (int z = 0; z < biases.length; z++) {
					biases[z].printMatrix();
				}
				*/
				
				updateWeightsAndBiases(miniBatch);
			}
			
			// print out the accuracy array with a tag designating which epoch it came from
			System.out.println("\nEpoch " + (i+1) + " Accuracy");
			printAccuracy();
			
			endTime = System.currentTimeMillis();
			
			System.out.println("Time for Epoch Completion - " + ((endTime - startTime) / 1000) + " seconds");
		}
		
		totalEndTime = System.currentTimeMillis();
		System.out.println("Time for Entire Completion - " + ((totalEndTime - totalStartTime) / 1000) + " seconds");
		
		accuracyPairs = new int[11][2];
	}
	
	/* Update All Weights and Biases */
	private static void updateWeightsAndBiases(int startPos) {
		/*
		double tempEta = 10;
		int tempMiniBatchSize = 2;
		*/
		
		// initilize the empty bias and weight gradient Matrix arrays
		Matrix[] biasGradients = new Matrix[layers-1];
		Matrix[] weightGradients = new Matrix[layers-1];
		
		// create a new set of empty bias and weight gradients
		for (int i = 0; i < layers-1; i++) {
			Matrix bias = biases[i];
			biasGradients[i] = new Matrix(bias.rows, bias.columns, false);
			
			Matrix weight = weights[i];
			weightGradients[i] = new Matrix(weight.rows, weight.columns, false);
		}
		
		//System.out.println("Computing Weight and Bias Gradients");
		
		// compute all of the bias and weight gradients based on the backpropagation method
		for (int miniBatch = startPos; miniBatch < startPos + miniBatchSize; miniBatch++) {
			ArrayList<Matrix[]> gradients = backpropagation(miniBatch);
			Matrix[] newBiasGradients = gradients.get(0);
			Matrix[] newWeightGradients = gradients.get(1);
			
			/*
			for (int i = 0; i < newBiasGradients.length; i++) {
				System.out.println("\nBiasGradients in Layer " + i);
				newBiasGradients[i].printMatrix();
			}
			
			for (int i = 0; i < newWeightGradients.length; i++) {
				System.out.println("\nWeightGradients in Layer " + i);
				newWeightGradients[i].printMatrix();
			}
			*/
			
			for (int i = 0; i < biasGradients.length; i++) {
				biasGradients[i] = Matrix.add(biasGradients[i], newBiasGradients[i]);
				weightGradients[i] = Matrix.add(weightGradients[i], newWeightGradients[i]);
			}
		}
		
		//System.out.println("\nAll Weights and Bias Gradients Calculated");
		
		// update all of the biases and weights based on their respective gradients
		for (int i = 0; i < layers-1; i++) {
			biasGradients[i] = Matrix.multiply(biasGradients[i], (float) (eta / miniBatchSize));
			biases[i] = Matrix.add(biases[i], Matrix.multiply(biasGradients[i], -1));
			
			weightGradients[i] = Matrix.multiply(weightGradients[i], (float) (eta / miniBatchSize));
			weights[i] = Matrix.add(weights[i], Matrix.multiply(weightGradients[i], -1));
		}
		
		//System.out.println("Biases and Weights Updated");
	}
	
	/* Compute Bias and Weight Gradients Via Backpropagation */
	private static ArrayList<Matrix[]> backpropagation(int inputPos) {
		ArrayList<Matrix[]> gradients = new ArrayList<>();
		
		// set up L as the last layer position in the biases, biasGradients, weights, weightGradients, and outputs Matrix arrays
		// Array Lists
		int L = layers-2;
		
		// initilize the empty bias and weight gradient Matrix arrays
		Matrix[] biasGradients = new Matrix[biases.length];
		Matrix[] weightGradients = new Matrix[weights.length];
		
		// create a new set of empty bias and weight gradients
		for (int i = 0; i < layers-1; i++) {
			Matrix bias = biases[i];
			biasGradients[i] = new Matrix(bias.rows, bias.columns, false);
			
			Matrix weight = weights[i];
			weightGradients[i] = new Matrix(weight.rows, weight.columns, false);
		}
		
		// FOWARD PASS
		// perform the feed forward pass and get back its output
		ArrayList<float[]> output = feedForward(trainingData.get(inputPos).getValue());
		
		// compute how well the neural network did for the given input and store that accuracy
		computeAccuracy(output.get(L), trainingData.get(inputPos).getKey());
		
		// store the expected output as an array that's the same size as the actual output
		float[] expectedOutput = new float[output.get(L).length];
		expectedOutput[trainingData.get(inputPos).getKey()] = 1;
		
		// BACKWARDS PASS
		// compute the final (L) layer of bias and weight gradients
		for (int j = 0; j < biases[L].rows; j++) {
			biasGradients[L].matrix[j][0] = (output.get(L)[j] - expectedOutput[j]) * output.get(L)[j] * (1 - output.get(L)[j]);
		}
		
		// compute the l-1 layer of bias gradients
		for (int l = L-1; l >= 0; l--) {
			for (int k = 0; k < biases[l].rows; k++) {
				float bGradient = 0;
				
				for (int j = 0; j < biases[l+1].rows; j++) {
					bGradient += weights[l+1].matrix[j][k] * biasGradients[l+1].matrix[j][0];
				}
				
				biasGradients[l].matrix[k][0] = bGradient * output.get(l)[k] * (1 - output.get(l)[k]);
			}
		}
		
		// compute all of the weight gradients
		for (int l = L; l >= 0; l--) {
			for (int j = 0; j < weightGradients[l].rows; j++) {
				for (int k = 0; k < weightGradients[l].columns; k++) {
					if (l != 0)
						weightGradients[l].matrix[j][k] = output.get(l-1)[k] * biasGradients[l].matrix[j][0];
					else
						weightGradients[l].matrix[j][k] = trainingData.get(inputPos).getValue()[k] * biasGradients[l].matrix[j][0];
				}
			}
		}
		
		
		// add the bias and weight gradients to the master gradients list and return it
		gradients.add(biasGradients);
		gradients.add(weightGradients);
		return gradients;
	}
	
	/* Feed Forward Pass */
	private static ArrayList<float[]> feedForward(float[] inputs) {
		ArrayList<float[]> outputs = new ArrayList<>();
		
		for (int i = 0; i < weights.length; i++) {
			/*
			System.out.println("\nLayer " + i);
			System.out.println("Inputs");
			printVector(inputs);
			*/
			
			inputs = Matrix.multiply(weights[i], inputs);
			inputs = Matrix.add(biases[i], inputs);
			
			/*
			System.out.println("Z");
			printVector(inputs);
			*/
			
			inputs = sigmoid(inputs);
			
			/*
			System.out.println("\na Layer - " + i);
			printVector(inputs);
			*/
			
			outputs.add(inputs);
		}
		
		return outputs;
	}
	
	/* Compute Accuracy */
	private static Pair<Boolean, Integer> computeAccuracy(float[] output, int expectedOutput) {
		float bestGuessPercent = 0;
		int bestGuessPosition = 0;
		
		for (int i = 0; i < output.length; i++) {
			if (output[i] > bestGuessPercent) {
				bestGuessPercent = output[i];
				bestGuessPosition = i;
			}
		}
		
		accuracyPairs[expectedOutput][1] += 1;
		accuracyPairs[10][1] += 1;
		
		if (bestGuessPosition == expectedOutput) {
			accuracyPairs[expectedOutput][0] += 1;
			accuracyPairs[10][0] += 1;
			return new Pair<Boolean, Integer> (true, 0);
		}
		else {
			return new Pair<Boolean, Integer> (false, bestGuessPosition);
		}
	}
	
	/* Print the Accuracy Array with Proper Formatting */
	private static void printAccuracy() {
		for (int i = 0; i < accuracyPairs.length-1; i++) {
			System.out.print(i + " = "+ accuracyPairs[i][0] + "/" + accuracyPairs[i][1] + " ");
			
			if (i == 5) {
				System.out.print("\n");
			}
		}
		
		double accuracyPercent = 00.000;
		try {
			accuracyPercent = ((float) accuracyPairs[10][0] / accuracyPairs[10][1] * 100);
		} catch (Exception e) {
			System.out.println("Error calculating accuracy percent - " + e);
		}
		
		System.out.print("Accuracy = " + accuracyPairs[10][0] + "/" + accuracyPairs[10][1] + " = " + accuracyPercent + "%\n");
	}
	
	/* Test Saved Weights and Biases with Data Set */
	private static void testNetworkAccuracy(List<Pair<Integer,float[]>> data) {
		boolean continueTest = true;
		
		System.out.println("\nWould you like to see which inputs were incorrect?");
		System.out.println("\t[1] Yes");
		System.out.println("\t[2] No");
		System.out.println("\tAll other inputs return to main menu");
		
		String responseString = scanner.next();
		int response;
		
		try {
			response = Integer.parseInt(responseString);
			
			if (response != 1 && response != 2)
				continueTest = false;
			
		} catch (Exception e) {
			continueTest = false;
			response = 0;
		}
		
		for (int i = 0; i < data.size(); i++) {
			// perform the feed forward pass and get back its output
			ArrayList<float[]> output = feedForward(data.get(i).getValue());
			
			// compute how well the neural network did for the given input and store that accuracy
			
			Pair<Boolean, Integer> accuracyPair = computeAccuracy(output.get(output.size()-1), data.get(i).getKey());
			
			if (!accuracyPair.getKey() && response == 1) {
				continueTest = printIncorrectNumber(i, data.get(i).getKey(), accuracyPair.getValue(), data.get(i).getValue());
				
				if (!continueTest)
					break;
			}
		}
		
		if (continueTest)
			printAccuracy();
		
		accuracyPairs = new int[11][2];
	}
	
	/* Print the Incorrect Number in an ASCII Representation */
	private static boolean printIncorrectNumber(int caseNumber, int correct, int output, float[] input) {
		System.out.println("\nTesting Case #" + caseNumber + "\tCorrect Classification = " + correct + "\tNetwork Output = " + output + "\tIncorrect.");
		
		for (int i = 0; i < input.length; i++) {
			// if we've reached the end of a "row", then move to the next line
			if (i % 28 == 0)
				System.out.print("\n");
			
			float value = input[i];
			
			// depending on the greyscale value, display an ASCII character to represent it
			if (value <= 0.1)
				System.out.print(" ");
			else if (value <= 0.2)
				System.out.print(".");
			else if (value <= 0.3)
				System.out.print(",");
			else if (value <= 0.4)
				System.out.print("k");
			else if (value <= 0.5)
				System.out.print("o");
			else if (value <= 0.6)
				System.out.print("i");
			else if (value <= 0.7)
				System.out.print("H");
			else if (value <= 0.8)
				System.out.print("X");
			else if (value <= 0.9)
				System.out.print("&");
			else
				System.out.print("8");
		}
		
		// if the user responds with a 1 after an image, continue computing the accuracy; otherwise bring them to the main menu
		System.out.println("\nEnter 1 to continue. All other values return to main menu.");
		String responseString = scanner.next();
		
		try {
			return Integer.parseInt(responseString) == 1;
		} catch (Exception e) {}
		
		return false;
	}
	
	/* Sigmoid Function */
	private static float[] sigmoid(float[] z) {
		// basically return (1/(1+e^z)) = y for every index in z
		
		float[] sigma = z;
		
		for (int i = 0; i < sigma.length; i++) {
			sigma[i] = (float) (1.0 / (1.0 + Math.pow(e, -z[i])));
		}
		
		return sigma;
	}
	
	/* Print the Vector as a Single Column */
	private static void printVector(float[] vector) {
		System.out.print("[");
		
		for (int i = 0; i < vector.length; i++) {
			System.out.print(vector[i] + "");
			
			if (i != vector.length - 1)
				System.out.print(",\n ");
			else
				System.out.print("]\n");
		}
	}
	
	/* Test the Full Functionality of Matrices */
	private static void matrixTest() {
		float[][] first = {{4, -1}, {0, 5}};
		float[][] second = {{1, 8, 0}, {6, -2, 3}};
		
		Matrix firstM = new Matrix(first);
		System.out.println("\nFirst Matrix");
		firstM.printMatrix();
		
		Matrix secondM = new Matrix(second);
		System.out.println("\nSecondMatrix");
		secondM.printMatrix();
		
		Matrix fourthM = Matrix.multiply(firstM, secondM);
		System.out.println("\nDot Matrix");
		if (fourthM != null)
			fourthM.printMatrix();
		else
			System.out.println("The Dot Product is null");
		
		
		Matrix thirdM = Matrix.add(firstM, secondM);
		System.out.println("\nSum Matrix");
		if (thirdM != null)
			thirdM.printMatrix();
		else
			System.out.println("The Sum is null");
	}
}

/* Custom Matrix Class */
class Matrix {
	public final int rows;
	public final int columns;
	public final float[][] matrix;
	
	private Random r;
	
	/* Constructor of a New Matrix */
	public Matrix(int rows, int columns, boolean populate) {
		this.rows = rows;
		this.columns = columns;
		this.matrix = new float[rows][columns];
		
		// if we want to populate the matrix, then we'll fill it with randomized real numbers
		if (populate)
			randomizeItems();
	}
	
	/* Constructor of an Existing Array */
	public Matrix(float[][] oldMatrix) {
		this.rows = oldMatrix.length;
		this.columns = oldMatrix[0].length;
		this.matrix = oldMatrix;
	}
	
	/* Multiplication Between Two Matrices */
	public static Matrix multiply(Matrix A, Matrix B) {
		if (A.columns == B.rows) {
			Matrix output = new Matrix(A.rows, B.columns, false);
			
			for (int i = 0; i < A.rows; i++) {
				for (int l = 0; l < B.columns; l++) {
					float c = 0;
					
					for (int j = 0; j < A.columns && j < B.rows; j++) {
						c += A.matrix[i][j] * B.matrix[j][l];
					}
					
					output.matrix[i][l] = c;
				}
			}
			return output;
		}
		else
			return null;
	}
	
	/* Multiplication Between a Matrix and a Vector */
	public static float[] multiply(Matrix A, float[] B) {
		if (A.columns == B.length) {
			float[] output = new float[A.rows];
			
			for (int i = 0; i < A.rows; i++) {
				float c = 0;
				
				for (int j = 0; j < A.columns; j++) {
					c += A.matrix[i][j] * B[j];
				}
				
				output[i] = c;
			} 
			
			return output;
		}
		else
			return null;
	}
	
	/* Multiplication Between a Matrix and a Scalar */
	public static Matrix multiply(Matrix A, float B) {
		Matrix output = new Matrix(A.rows, A.columns, false);
		
		for (int i = 0; i < A.rows; i++) {
			for (int j = 0; j < A.columns; j++) {
				output.matrix[i][j] = A.matrix[i][j] * B;
			}
		}
		return output;
	}
	
	/* Addition Between Two Matrices */
	public static Matrix add(Matrix A, Matrix B) {
		if (A.rows == B.rows && A.columns == B.columns) {
			Matrix output = new Matrix(A.rows, A.columns, false);
			
			for (int i = 0; i < A.rows; i++) {
				for (int j = 0; j < A.columns; j++) {
					output.matrix[i][j] = A.matrix[i][j] + B.matrix[i][j];
				}
			}
			
			return output;
		}
		else
			return null;
	}
	
	/* Addition Between Two Vectors */
	public static float[] add(Matrix A, float[] B) {
		if (A.rows == B.length) {
			float[] output = new float[A.rows];
			
			for (int i = 0; i < A.rows; i++) {
				output[i] = A.matrix[i][0] + B[i];
			}
			
			return output;
		}
		else
			return null;
	}
	
	/* Create a Random Float For Each Position */
	private void randomizeItems() {
		// iterate through the entire matrix and fill it with random real numbers
		
		r = new Random();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.matrix[i][j] = ((2 * r.nextFloat()) - 1);
			}
		}
	}
	
	/* Print All Rows and Columns of A Matrix */
	public void printMatrix() {
		// print out the entire matrix, including all of its values at every index
		
		System.out.print("[");
		
		for (int i = 0; i < rows; i++) {
			System.out.print("[");
			
			for (int j = 0; j < columns; j++) {
				// if this is the last item in the row, don't add a comma
				if (j == columns-1) {
					System.out.print(matrix[i][j] + "");
				}
				else {
					System.out.print(matrix[i][j] + ", ");
				}
			}
			
			// if this is the last item in the matrix, don't add a comma
			if (i == rows-1) {
				System.out.print("]");
			}
			else {
				System.out.print("],\n ");
			}
		}
		
		System.out.print("]\n");
	}
	
	/* Print Dimensions of Matrix */
	public void printMatrixSize() {
		// print out the "rows x columns" of this Matrix
		
		System.out.println(this.rows + " x " + this.columns);
	}
}