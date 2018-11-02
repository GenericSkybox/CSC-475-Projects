/*
//	Name:	Eric Ortiz
// CWID:	102-39-903
//	Date:	10/19/18
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
	/*
	this is the main class of the entire program; it houses not only the network itself, but also the main processing loop
	along with any function relating to the network (like SGD, feed foward, printing the accuracy, etc.)
	*/
	
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
	static int[] layerSizes = {784, 100, 10};
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
			
			// compute the time it took for each epoch to complete and display that time
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
		/*
		this method updates all of the weights and biases based on their respective gradients; these gradients are gathered
		from the backpropagation algorithm
		*/
		
		// initilize the empty bias and weight gradient Matrix arrays
		Matrix[] biasGradients = new Matrix[layers-1];
		Matrix[] weightGradients = new Matrix[layers-1];
		
		// create a new set of empty bias and weight gradients
		for (int i = 0; i < layers-1; i++) {
			biasGradients[i] = new Matrix(biases[i].rows, biases[i].columns, false);
			weightGradients[i] = new Matrix(weights[i].rows, weights[i].columns, false);
		}
		
		// compute all of the bias and weight gradients based on the backpropagation method
		for (int miniBatch = startPos; miniBatch < startPos + miniBatchSize; miniBatch++) {
			// we send our biasGradients and our weightGradients to the backpropagation method to be used to compute
			// the next round of gradients; since we're passing the gradients by reference, changing them in the method
			// will change their values outside of backpropagation
			backpropagation(miniBatch, biasGradients, weightGradients);
		}
		
		// compute the learning rate over the size of the mini batch
		float etaOverBatch = (float) eta / miniBatchSize;
		
		// update all of the biases and weights based on their respective gradients and the learning rate over mini batch size
		for (int i = 0; i < layers-1; i++) {
			biasGradients[i] = Matrix.multiply(biasGradients[i], etaOverBatch);
			biases[i] = Matrix.add(biases[i], Matrix.multiply(biasGradients[i], -1));
			
			weightGradients[i] = Matrix.multiply(weightGradients[i], etaOverBatch);
			weights[i] = Matrix.add(weights[i], Matrix.multiply(weightGradients[i], -1));
		}
	}
	
	/* Compute Bias and Weight Gradients Via Backpropagation */
	private static void backpropagation(int inputPos, Matrix[] biasGradients, Matrix[] weightGradients) {
		/*
		this method adds the old matrices of gradients for biases and weights to the newest computed matrices of gradients
		*/
		
		// set up L as the last layer position in the biases, biasGradients, weights, weightGradients, and outputs Matrix arrays
		// Array Lists
		int L = layers-2;
		
		// create a new matrix array of bias gradients that will mirror the current bias gradients; the old matrices of bias
		// gradients will be used in computations while the new matrices will be used to replace the old matrices after
		// computations are complete
		Matrix[] newBiasGradients = new Matrix[biases.length];
		for (int i = 0; i < layers-1; i++) {
			Matrix bias = biases[i];
			newBiasGradients[i] = new Matrix(biasGradients[i].matrix);
		}
		
		// FOWARD PASS
		// perform the feed forward pass with our input and get back the network's output
		ArrayList<float[]> output = feedForward(trainingData.get(inputPos).getValue());
		
		// compute how well the neural network did for the given input and store that accuracy
		computeAccuracy(output.get(L), trainingData.get(inputPos).getKey());
		
		// store the expected output as an array that's the same size as the actual output
		float[] expectedOutput = new float[output.get(L).length];
		expectedOutput[trainingData.get(inputPos).getKey()] = 1;
		
		// BACKWARDS PASS
		// compute the final (L) layer of bias and weight gradients
		for (int j = 0; j < biases[L].rows; j++) {
			// compute the gradient first
			float gradient = (output.get(L)[j] - expectedOutput[j]) * output.get(L)[j] * (1 - output.get(L)[j]);
			
			// add that gradient to the new bias gradients, but replace the old bias gradients with it
			newBiasGradients[L].matrix[j][0] += gradient;
			biasGradients[L].matrix[j][0] = gradient;
		}
		
		// compute the l-1 layer of bias gradients
		for (int l = L-1; l >= 0; l--) {
			for (int k = 0; k < biases[l].rows; k++) {
				float bGradient = 0;
				
				// sum up all of the products of weights and bias gradients
				for (int j = 0; j < biases[l+1].rows; j++) {
					bGradient += weights[l+1].matrix[j][k] * biasGradients[l+1].matrix[j][0];
				}
				
				// compute the full gradient
				float fullGradient = bGradient * output.get(l)[k] * (1 - output.get(l)[k]);
				
				// add that gradient to the new bias gradients, but replace the old bias gradients with it
				newBiasGradients[l].matrix[k][0] += fullGradient;
				biasGradients[l].matrix[k][0] = fullGradient;
			}
		}
		
		// compute all of the weight gradients for every layer; since the weight gradients aren't used in any computations
		// it's safe to update them without creating a "new weight gradients" array
		for (int l = L; l >= 0; l--) {
			for (int j = 0; j < weightGradients[l].rows; j++) {
				for (int k = 0; k < weightGradients[l].columns; k++) {
					if (l != 0)
						weightGradients[l].matrix[j][k] += output.get(l-1)[k] * biasGradients[l].matrix[j][0];
					else
						weightGradients[l].matrix[j][k] += trainingData.get(inputPos).getValue()[k] * biasGradients[l].matrix[j][0];
				}
			}
		}
		
		// replace the old bias gradients with the new bias gradients to update them
		biasGradients = newBiasGradients;
	}
	
	/* Feed Forward Pass */
	private static ArrayList<float[]> feedForward(float[] inputs) {
		/*
		this method takes the input vector and runs it through the entire network, saving the output of each layer along the way
		*/
		
		// create an empty list of outputs to return later
		ArrayList<float[]> outputs = new ArrayList<>();
		
		// iterate through every layer and compute each layer's output
		for (int i = 0; i < weights.length; i++) {
			// multiply all of the weights in each layer by the incoming input
			inputs = Matrix.multiply(weights[i], inputs);
			
			// add the biases to the weighted inputs
			inputs = Matrix.add(biases[i], inputs);
			
			// run each biased, weighted input through the sigmoid function
			inputs = sigmoid(inputs);
			
			// add the reult of the sigmoid function as an output to the list of outputs
			outputs.add(inputs);
		}
		
		// finally return the list of outputs for each layer
		return outputs;
	}
	
	/* Sigmoid Function */
	private static float[] sigmoid(float[] z) {
		/*
		compute the sigmoid function on every item in the list 'z'; basically return (1/(1+e^-z)) = sigma for every index in z
		*/
		
		float[] sigma = z;
		
		for (int i = 0; i < sigma.length; i++) {
			sigma[i] = (float) (1.0 / (1.0 + Math.pow(e, -z[i])));
		}
		
		return sigma;
	}
	
	/* Compute Accuracy */
	private static Pair<Boolean, Integer> computeAccuracy(float[] output, int expectedOutput) {
		/*
		this method computes the network's accuracy for a given output and the expected output; in actuality this method
		only changes the accuracyPairs at the top of this class
		*/
		
		// we first need to determine what number the network thought the input was
		float bestGuessPercent = 0;
		int bestGuessPosition = 0;
		
		// we do the above by keeping track of the highest guess percentage and where that percentage was in the output
		for (int i = 0; i < output.length; i++) {
			if (output[i] > bestGuessPercent) {
				bestGuessPercent = output[i];
				bestGuessPosition = i;
			}
		}
		
		// depending on what the expected output was, we need to increment the total of that number in the accuracyPair
		accuracyPairs[expectedOutput][1] += 1;
		accuracyPairs[10][1] += 1;
		
		// if the network was correct in its guess, then we need to update the accuracyPair to reflect that as well
		if (bestGuessPosition == expectedOutput) {
			accuracyPairs[expectedOutput][0] += 1;
			accuracyPairs[10][0] += 1;
			return new Pair<Boolean, Integer> (true, 0);
		}
		// otherwise, we need to return that the network got it wrong and what its incorrect guess was
		else {
			return new Pair<Boolean, Integer> (false, bestGuessPosition);
		}
	}
	
	/* Print the Accuracy Array with Proper Formatting */
	private static void printAccuracy() {
		/*
		this method takes the accuracyPairs from the top of the class and prints them in a legible fashion
		*/
		
		// we iterate through each pair, printing out the correct guess over total guesses
		for (int i = 0; i < accuracyPairs.length-1; i++) {
			System.out.print(i + " = "+ accuracyPairs[i][0] + "/" + accuracyPairs[i][1] + " ");
			
			if (i == 5) {
				System.out.print("\n");
			}
		}
		
		// the last item in the accuracyPairs array is the total amount over all guesses; from these totals, we need to determine
		// the percentage correct
		double accuracyPercent = 00.000;
		try {
			accuracyPercent = ((float) accuracyPairs[10][0] / accuracyPairs[10][1] * 100);
		} catch (Exception e) {
			System.out.println("Error calculating accuracy percent - " + e);
		}
		
		// print out the final accuracy along with its percentage correct
		System.out.print("Accuracy = " + accuracyPairs[10][0] + "/" + accuracyPairs[10][1] + " = " + accuracyPercent + "%\n");
	}
	
	/* Test Saved Weights and Biases with Data Set */
	private static void testNetworkAccuracy(List<Pair<Integer,float[]>> data) {
		/*
		this method sends all of the given data through the network and prints out the accuracy of the network; optionally
		showing the user which numbers the network got wrong
		*/
		
		// we use this variable when printing out the incorrect numbers; it tells use whether or not we need to stop early
		boolean continueTest = true;
		
		// first we need to ask the user if they actually want us to print out the incorrect numbers
		System.out.println("\nWould you like to see which inputs were incorrect?");
		System.out.println("\t[1] Yes");
		System.out.println("\t[2] No");
		System.out.println("\tAll other inputs return to main menu");
		
		// grab the response from the user and parse it
		String responseString = scanner.next();
		int response;
		
		try {
			response = Integer.parseInt(responseString);
			
			// if the user doesn't respond with yes or no, then bring them back to the main menu
			if (response != 1 && response != 2)
				continueTest = false;
			
		} catch (Exception e) {
			// bring the user back to the main menu
			continueTest = false;
			response = 0;
		}
		
		// for each input in the data, we need to feed it through the network and print the nextwork's accuracy
		for (int i = 0; i < data.size(); i++) {
			// perform the feed forward pass and get back its output
			ArrayList<float[]> output = feedForward(data.get(i).getValue());
			
			// compute how well the neural network did for the given input and store that accuracy; we get back an
			// accuracyPair in case we need to print the incorrect number
			Pair<Boolean, Integer> accuracyPair = computeAccuracy(output.get(output.size()-1), data.get(i).getKey());
			
			// if the guess was incorrect and the user wants us to print out incorrect numbers, then we'll do just that
			if (!accuracyPair.getKey() && response == 1) {
				continueTest = printIncorrectNumber(i, data.get(i).getKey(), accuracyPair.getValue(), data.get(i).getValue());
				
				// if the user wants to quit after printing the incorrect number, then we break early
				if (!continueTest)
					break;
			}
		}
		
		// if the user either entered a response that would bring them back to the main menu at any point, then we skip printing
		// out the accuracy
		if (continueTest)
			printAccuracy();
		
		// we need to reset the accuracyPairs after every full data set
		accuracyPairs = new int[11][2];
	}
	
	/* Print the Incorrect Number in an ASCII Representation */
	private static boolean printIncorrectNumber(int caseNumber, int correct, int output, float[] input) {
		/*
		this method uses the item number of the input, the correct output, the incorrect output the network gave, and
		actual input the network received for that incorrect output to print out the number in ASCII format
		*/
		
		// print the header for the incorrect output
		System.out.println("\nTesting Case #" + caseNumber + "\tCorrect Classification = " + correct + "\tNetwork Output = " + output + "\tIncorrect.");
		
		// iterate through every single greyscale value in the array of the input
		for (int i = 0; i < input.length; i++) {
			// if we've reached the end of a "row", then move to the next line
			if (i % 28 == 0)
				System.out.print("\n");
			
			float value = input[i];
			
			// depending on the greyscale value, display an ASCII character to represent it, with smaller values given a smaller
			// ASCII character
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
	
	/* Print the Vector as a Single Column */
	private static void printVector(float[] vector) {
		/*
		just as the matrix has a method for being printed, so too does a float vector; this is mainly used for debugging
		*/
		
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
	/*
	this class is used to make the creation, usage, and computations of multiple 2D float arrays easier
	*/
	
	public final int rows;
	public final int columns;
	public final float[][] matrix;
	
	private Random r;
	
	/* Constructor of a New Matrix */
	public Matrix(int rows, int columns, boolean populate) {
		/*
		this method takes a number of rows and columns and creates a new matrix; we can also define whether or not we
		want to auto-populate the matrix with random floats from -1 to 1
		*/
		
		// here we create the matrix object
		this.rows = rows;
		this.columns = columns;
		this.matrix = new float[rows][columns];
		
		// if we want to populate the matrix, then we'll fill it with randomized real numbers here
		if (populate)
			randomizeItems();
	}
	
	/* Constructor of an Existing Array */
	public Matrix(float[][] oldMatrix) {
		/*
		this method takes an existing 2D float array and uses it as the basis for a new Matrix object
		*/
		
		this.rows = oldMatrix.length;
		this.columns = oldMatrix[0].length;
		this.matrix = oldMatrix;
	}
	
	/* Multiplication Between Two Matrices */
	public static Matrix multiply(Matrix A, Matrix B) {
		/*
		this method performs matrix multiplcation between two matrices
		*/
		
		// we need to make sure that the interior sizes of the matrices match
		if (A.columns == B.rows) {
			Matrix output = new Matrix(A.rows, B.columns, false);
			
			// if the sizes match, then we need to iterate across the rows of A and the columns of B and perform the dot product
			// on each pair
			for (int i = 0; i < A.rows; i++) {
				for (int l = 0; l < B.columns; l++) {
					float c = 0;
					
					// here's the dot product itself of row i in A and column l in B
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
		/*
		this method performs matrix multiplciation between a matrix and a vector
		*/
		
		// we need to make sure that the interior sizes of the matrix and vector match (i.e. the columns of A and the rows of B)
		if (A.columns == B.length) {
			float[] output = new float[A.rows];
			
			// if the sizes match, then we need to iterate across the rows of A and the vector of B and perform the dot product
			// on each pair
			for (int i = 0; i < A.rows; i++) {
				float c = 0;
				
				// here's the dot product itself of row i in A and vector B
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
		/*
		this method essentially performs a scalar multiplication between a matrix and a float
		*/
		
		Matrix output = new Matrix(A.rows, A.columns, false);
		
		// we're going to create a new matrix and make every item in this matrix a product of an item in the input matrix
		// and the passed in scalar
		for (int i = 0; i < A.rows; i++) {
			for (int j = 0; j < A.columns; j++) {
				output.matrix[i][j] = A.matrix[i][j] * B;
			}
		}
		return output;
	}
	
	/* Addition Between Two Matrices */
	public static Matrix add(Matrix A, Matrix B) {
		/*
		this method adds together two matrices that match in size
		*/
		
		// first make sure that the matrices match in size
		if (A.rows == B.rows && A.columns == B.columns) {
			Matrix output = new Matrix(A.rows, A.columns, false);
			
			// if they match, then we're going to create a new matrix that has the sum of all of the items between the other
			// two matrices
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
		/*
		this method adds together a matrix (which is technically a vector at this point) with a float vector
		*/
		
		// first make sure that the rows match between the two "vectors"
		if (A.rows == B.length) {
			float[] output = new float[A.rows];
			
			// if they match, then we're going to create a new vector that has the sum of all of the items between the other
			// two vectors
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
		/*
		iterate through the entire matrix and fill it with random, real numbers
		*/
		
		r = new Random();
		
		// fill every row and column with random floats
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				// we multiply each float by 2 and subtract 1 so that we get a scale between -1 and 1 rather than 0 to 1
				this.matrix[i][j] = ((2 * r.nextFloat()) - 1);
			}
		}
	}
	
	/* Print All Rows and Columns of A Matrix */
	public void printMatrix() {
		/*
		print out the entire matrix, including all of its values at every index
		*/
		
		System.out.print("[");
		
		// iterate through all of the rows of the matrix
		for (int i = 0; i < rows; i++) {
			System.out.print("[");
			
			// iterate through all of the columns of the matrix
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
		/*
		print out the "rows x columns" of this Matrix
		*/
		
		System.out.println(this.rows + " x " + this.columns);
	}
}