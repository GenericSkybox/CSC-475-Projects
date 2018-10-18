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
	
	// set up the number of epochs, each mini batch size, and the learning rate
	final static int epochs = 30;
	final static int miniBatchSize = 10;
	final static double eta = 3.0;
	
	// set up the size of the input we're expected, along with the size of the training and test sets
	final static int inputSize = 784;
	final static int trainingSize = 60000;
	final static int testingSize = 10000;
	
	// set up the names of the files we'll be accessing
	final static String saveFileName = "test.csv";
	final static String trainFileName = "mnist_train.csv";
	final static String testFileName = "mnist_test.csv";
	
	// use a boolean to keep track of whether or not the user can see all of the options yet
	static boolean firstRun = true;
	
	// initialize the size of each layer, and declare variables for the number of layers
	static int[] layerSizes = {784, 30, 10};
	static int layers;
	
	// declare the Matrix arrays for each weight between layers and each bias on each layer after the first
	static Matrix[] biases;
	static Matrix[] weights;
	
	// initialize the lists of testing data and training data, where each list will contain a pair
	// each pair will put the correct output first and then the array of inputs
	static List<Pair<Integer,float[]>> testingData = new ArrayList<>();
	static List<Pair<Integer,float[]>> trainingData = new ArrayList<>();
	
	// initialize a 2D array of accuracy pairs, where the first row will determine the amount correct and the second row
	// will determine the total amount; each column corresponds to an expected output (i.e. 0 to 9 plus total accuracy)
	static int[][] accuracyPairs = new int[11][2];
	
	// create a global scanner for reading user input (stdin)
	static Scanner scanner;
	
	/* Main Function */
	public static void main(String[] args) {
		/*
		this is the main function where process control starts at; it also contains the main process while-loop through
		which users can enter in commands
		*/
		
		// print the welcome statement
		System.out.println("\fWelcome to Eric Ortiz's MNIST Digit Recognizer!\n");
		
		// create the network and load in the training and testing data from a file
		createNetwork();
		try {
			loadDataSets();
		} catch (FileNotFoundException e) {
			System.out.println("Error loading data sets - " + e);
		}
		
		// start the scanner to read user input
		scanner = new Scanner(System.in);
		
		// main operation loop where the user can continually submit input until they exit (or the program crashes :D)
		while(true) {
			// show the user the available commands after every iteration
			printOptions();
			
			// grab the user's input
			String input = scanner.next();
			
			// try to capture and parse the user input
			try {
				int response = Integer.parseInt(input);
				
				// if the response was zero, break from the for-loop to exit the program
				if (response == 0)
					break;
				// otherwise, perform the function requested
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
	private static void createNetwork() {
		/* 
		this method creates all of the randomized weights and biases for the neural network based on our amount of layers
		and each layer's size
		*/
		
		// first initialize the size of the network and use that size to determine the number of weight matrices and bias vectors
		layers = layerSizes.length;
		biases = new Matrix[layers-1];
		weights = new Matrix[layers-1];
		
		// create each new bias matrix with randomized values
		for (int i = 1; i < layers; i++){
			biases[i-1] = new Matrix(layerSizes[i], 1, true);
		}
		
		// create each new weight matrix with randomized values
		for (int i = 0; i < layers-1; i++) {
			weights[i] = new Matrix(layerSizes[i+1], layerSizes[i], true);
		}
	}
	
	/* Load In Training and Testing Sets */
	private static void loadDataSets() throws FileNotFoundException {
		/*
		this method reads in all of the training and testing data of the MNIST data sets and stores them upfront;
		the data sets should be in two separate CSV files, each labelled appropriately at the top of the program
		*/
		
		// start a scanner to read through the testing file for input data
		Scanner fileScanner = new Scanner(new File(testFileName)).useDelimiter("[,\n]");
		
		// for the size of the expected training data, we need to grab all of the data on a row
		for (int i = 0; i < testingSize; i++) {
			// grab the row as a single string, then split that string into an array of strings with a comma as a delimiter
			String inputRow = fileScanner.nextLine();
			String[] stringInputs = inputRow.split(",");
			
			// grab the first string as an integer and store that as the correct value for the input
			Integer firstInteger = new Integer(stringInputs[0]);
			
			// create an array of floats that will match the expected inputsize; this array will parallel the string array of inputs
			float[] inputs = new float[inputSize];
			
			// fill up the float array with the float conversions of the string input; be sure to normalize each input!
			for (int j = 1; j < stringInputs.length-1; j++) {
				Float tempFloat = Float.parseFloat(stringInputs[j]);
				inputs[j] = tempFloat / 255;
			}
			
			// create and store the pair of correct integer output and the float array of normalized output
			testingData.add(new Pair<Integer,float[]>(firstInteger, inputs));
		}
		
		// print completion
		System.out.println("\nTesting Data Retrieved");
		
		// close the scanner for the testing data file, open the training data file, and start the process again for that data
		fileScanner.close();
		fileScanner = new Scanner(new File(trainFileName)).useDelimiter("[,\n]");
		
		// this for-loop is exactly the same as the one above, except the variables relate to the training data
		// rather than the testing data
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
		
		// close the scanner and print completion
		fileScanner.close();
		System.out.println("\nTraining Data Retrieved\n");
	}
	
	/* Print User Command Options */
	private static void printOptions() {
		/*
		this method prints out all of the options of the user, unless they just opened the application
		*/
		
		// print the preliminary options
		System.out.println("\nPlease select an option:");
		System.out.println("\t[1] Train the Network");
		System.out.println("\t[2] Load a Pre-Trained Network");
		
		// if the user hasn't trained or loaded a network yet, then don't show them these following options
		if (!firstRun) {
			// print additional options
			System.out.println("\t[3] Display Network Accuracy on TRAINING Set");
			System.out.println("\t[4] Display Network Accuracy on TESTING Set");
			System.out.println("\t[5] Save the Current Network State to File");
		}
		
		// print the exit option
		System.out.println("\t[0] Exit\n");
	}
	
	/* Parse User Command */
	private static void parseInput(int input) {
		/*
		this method parses through all of the input sent to the command line from the main menu; depending on the command
		the program will execute different functions
		*/
		
		// if the user wants to train the network or load a network
		if (input == 1 || input == 2) {
			// if the user wish to train the network, run the Stochastic Gradient Descent algorithm on the current weights and biases
			if (input == 1) {
				SGD();
			}
			// if the user wishes to load a pre-trained network, then we'll load it in
			else {
				try {
					loadNetwork();
				} catch (FileNotFoundException e) {
					System.out.println("Error saving file - " + e.toString());
				}
			}
			
			// once the network has been trained or loaded, we can display all of the options
			if (firstRun)
				firstRun = false;
		}
		// if the user selects the option to display network accuracy or save a network
		else if (input <= 5) {
			// if this is the program's first run, treat the input as invalid
			if (firstRun)
				System.out.println("The integer you submitted is not a valid command.\n");
			// else, either
			else {
				// display network accuracy on training set
				if (input == 3) {
					testNetworkAccuracy(trainingData);
				}
				// display network accuracy on testing set
				else if (input == 4) {
					testNetworkAccuracy(testingData);
				}
				// save the network (barring file errors)
				else {
					try {
						saveNetworkNew();
					} catch (FileNotFoundException e) {
						System.out.println("Error saving file - " + e.toString());
					}
				}
			}
		}
		// if the user submits any other option, consider it invalid
		else {
			System.out.println("The integer you submitted is not a valid command.\n");
		}
	}
	
	/* Load Existing Network */
	private static void loadNetwork() throws FileNotFoundException {
		/*
		this method will load in an existing, pre-trained neural network by storing all of the weights and biases that it
		reads from the designated filename
		*/
		
		// create a new scanner to iterate through all of the items in the network's CSV file
		Scanner fileScanner = new Scanner(new File(saveFileName)).useDelimiter("[,\n]");
		
		// iterate through every layer of the network and scan through each row of the CSV
		for (int l = 0; l < layers-1; l++) {
			// skip the 'w'
			fileScanner.nextLine();
			
			// iterate through all of the rows of the file until we reach the end of that weight matrix
			for (int i = 0; i < weights[l].rows; i++) {
				// just like loading in the data set, load in each row a giant string and convert it to an array of strings
				String weightRow = fileScanner.nextLine();
				String[] weightStrings = weightRow.split(",");
				
				// then store the array of strings as an array of floats for that row in the weight matrix
				for (int j = 0; j < weightStrings.length; j++) {
					Float weight = Float.parseFloat(weightStrings[j]);
					weights[l].matrix[i][j] = weight;
				}
			}
			
			// skip the 'b'
			fileScanner.nextLine();
			
			// iterate through all of the rows of the file until we reach the end of that bias "matrix"
			for (int i = 0; i < biases[l].rows; i++) {
				// since the bias "matrix" is actually a vector of rows, we just take the first and only item on each row
				// of the file and store that as the bias
				String biasRow = fileScanner.nextLine();
				String[] biasStrings = biasRow.split(",");
				
				Float bias = Float.parseFloat(biasStrings[0]);
				biases[l].matrix[i][0] = bias;
			}
		}
		
		// close the file scanner to plug up them leaks
		fileScanner.close();
	}
	
	/* Save the Network's Weights and Biases */
	private static void saveNetworkNew() throws FileNotFoundException {
		// this method saves all of the weights and biases we currently have for our network to an CSV file that we can later
		// load from
		
		// create a print writer and string builder so we can write all of the weights and biases to a file dynamically
		PrintWriter pw = new PrintWriter(new File(saveFileName));
		StringBuilder sb = new StringBuilder();
		
		// iterate through all of the layers (i.e. length of the weights and biases arrays) to print all of the weights and biases
		for (int l = 0; l < layers-1; l++) {
			// mark which layer this set of weights is from
			sb.append("w" + l + "\n");
			
			// iterate through each row and column of the weight matrices, printing each weight with a comma after it and
			// printing a new line character after each row is complete
			for (int i = 0; i < weights[l].rows; i++) {
				for (int j = 0; j < weights[l].columns; j++) {
					sb.append(weights[l].matrix[i][j] + ",");
				}
				sb.append("\n");
			}
			
			// mark which layer this set of biases is from
			sb.append("b" + l + "\n");
			
			// iterate through the bias vector and print each bias with a comma and newline character after each
			for (int i = 0; i < biases[l].rows; i++) {
				sb.append(biases[l].matrix[i][0] + ",\n");
			}
		}
		
		// finally write that supermassive string to the file and close the file writer
		pw.write(sb.toString());
		pw.close();
	}
	
	/* Stochastic Gradient Descent */
	private static void SGD() {
		/*
		this method peforms the entire Stochastic Gradient Descent algorithm on the currently loaded weights and biases
		with the enormous training data that's preloaded in
		*/
		
		// create some variables that will aid us in keeping track of how long each epoch and the entire algorithm performs
		long totalStartTime = System.currentTimeMillis();
		long totalEndTime;
		long startTime;
		long endTime;
		
		// this is the main epoch loop - after each loop, we go back through all of the data again
		for (int i = 0; i < epochs; i++) {
			// set the start time for this epoch
			startTime = System.currentTimeMillis();
			
			// shuffle the training data and create mini-batches based off of them
			Collections.shuffle(trainingData);
			
			// reset the accuracy array since this is a new epoch
			accuracyPairs = new int[11][2];
			
			// update the weights and biases for each mini batch
			for (int miniBatch = 0; miniBatch < trainingData.size(); miniBatch += miniBatchSize) {
				updateWeightsAndBiases(miniBatch);
			}
			
			// print out the accuracy array with a tag designating which epoch it came from
			System.out.println("\nEpoch " + (i+1) + " Accuracy");
			printAccuracy();
			
			endTime = System.currentTimeMillis();
			
			System.out.println("Time for Epoch Completion - " + ((endTime - startTime) / 1000) + " seconds");
		}
		
		// print out the total time for completing the entire algorithm
		totalEndTime = System.currentTimeMillis();
		System.out.println("Time for Entire Completion - " + ((totalEndTime - totalStartTime) / 1000) + " seconds");
		
		// reset the accuracy pairing (so that we don't add on to it again when printing accuracy later)
		accuracyPairs = new int[11][2];
	}
	
	/* Update All Weights and Biases */
	private static void updateWeightsAndBiases(int startPos) {
		
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