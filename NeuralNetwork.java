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

/* Main Class */
public class NeuralNetwork {
	// set up the constant of "e" - the base of the natural logarithm
	final static double e = 2.71828;
	
	static boolean firstRun = true;
	
	static int layers;
	static int[] layerSizes = {4, 3, 2};
	static Matrix[] biases;
	static Matrix[] weights;
	
	/* Main Function */
	public static void main(String[] args) {
		// print welcome statement and then create the neural network
		System.out.println("Welcome to Eric Ortiz's MNIST Digit Recognizer!\n");
		System.out.println("There's unfortunately not much done yet, so a lot of features are missing currently.\n");
		createNetwork();
		
		// start a scanner to read user input
		Scanner scanner = new Scanner(System.in);
		
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
	private static void createNetwork() {
		// this method creates all of the randomized weights and biases for the neural network based on our amount of layers
		// and each of their sizes
		
		// first declare the arrays of matrices for the weights and biases to be the size of the neural network minus 1
		layers = layerSizes.length;
		biases = new Matrix[layers-1];
		weights = new Matrix[layers-1];
		
		// create each new weight matrix with randomized values
		System.out.println("Biases:");
		for (int i = 1; i < layers; i++){
			System.out.println("Layer " + i);
			
			biases[i-1] = new Matrix(layerSizes[i], 1, true);
			biases[i-1].printMatrix();
		}
		
		// create each new bias matrix with randomized values
		System.out.println("\nWeights");
		for (int i = 0; i < layers-1; i++) {
			System.out.println("Layer " + i);
			
			weights[i] = new Matrix(layerSizes[i+1], layerSizes[i], true);
			weights[i].printMatrixSize();
		}
	}
	
	/* Print User Command Options */
	private static void printOptions() {
		System.out.println("\nPlease select an option:");
		System.out.println("   [1] Train the Network");
		System.out.println("   [2] Load a Pre-Trained Network");
		
		// if the user hasn't trained or loaded a network yet, then don't show them these following options
		if (!firstRun) {
			System.out.println("   [3] Display Network Accuracy on TRAINING Set");
			System.out.println("   [4] Display Network Accuracy on TRAINING Set");
			System.out.println("   [5] Save the Current Network State to File");
		}
		
		System.out.println("   [0] Exit\n");
	}
	
	/* Parse User Command */
	private static void parseInput(int input) {
		if (input == 1 || input == 2) {
			// this is no longer the program's first run
			if (firstRun)
				firstRun = false;
			
			if (input == 1) {
				//matrixTest();
				
				Matrix inputs = new Matrix(new double[][] {{0}, {1}, {0}, {1}});
				Matrix output = feedForward(inputs);
				
				if (output != null)
					output.printMatrix();
				else
					System.out.println("The output is null");
			}
			else {
				try {
					loadNetwork();
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
				
				}
				else if (input == 4) {
					
				}
				else {
					try {
						saveNetwork();
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
	
	/* Load a Saved Neural Network */
	private static void loadNetwork() throws FileNotFoundException{
		
	}
	
	/* Save Current Weights and Biases */
	private static void saveNetwork() throws FileNotFoundException {
		// this method saves all of the weights and biases we currently have for our network to an CSV file that we can later
		// load from
		
		PrintWriter pw = new PrintWriter(new File("test.csv"));
		StringBuilder sb = new StringBuilder();
		
		// iterate through the length of weights and add a "w" to the top of each weight matrix
		for (int i = 0; i < weights.length; i++) {
			sb.append("w" + i);
			
			// we need to add a comma for each column that's in the weight matrix
			for (int j = 0; j < weights[i].columns; j++) {
				sb.append(",");
			}
		}
		
		// iterate through the length of biases and add a "w" to the top of each bias matrix
		for (int i = 0; i < biases.length; i++) {
			sb.append("b" + i);
			
			// we need to add a comma for each column that's in the bias matrix
			for (int j = 0; j < biases[i].columns; j++) {
				sb.append(",");
			}
		}
		// the actual matrices will be written onto the next line
		sb.append("\n");
		
		// figure out the maximum number of rows between all of the matrices - weights and biases
		int rowsMax = 0;
		for (int i = 0; i < weights.length; i++) {
			if (weights[i].rows > rowsMax)
				rowsMax = weights[i].rows;
			
			if (biases[i].rows > rowsMax)
				rowsMax = biases[i].rows;
		}
		
		// iterate through all of the rows of this "mega matrix" that we'll be putting into the CSV file
		for (int j = 0; j < rowsMax; j++) {
			// iterate through all of the weights matrices
			for (int i = 0; i < weights.length; i++) {
				// iterate through all of the columns in a weight matrix and add its value to the mega matrix
				for (int k = 0; k < weights[i].columns; k++) {
					try {
						sb.append(weights[i].matrix[j][k] + "");
					} catch (Exception e) {
						sb.append(",");
						continue;
					}
					
					sb.append(",");
				}
			}
			
			// iterate through all of the bias matrices
			for (int i = 0; i < biases.length; i++) {
				// iterate through all of the columns in a bias matrix and add its value to the mega matrix
				for (int k = 0; k < biases[i].columns; k++) {
					try {
						sb.append(biases[i].matrix[j][k] + "");
					} catch (Exception e) {
						continue;
					}
					
					// if we reach the end of the array of bias matrices and this is the last column in the bias matrix, then start
					// the next row
					if (i == biases.length-1 && k == biases[i].columns-1) {
						sb.append("\n");
					}
					else{
						sb.append(",");
					}
				}
			}
		}
		
		// finally write that mega matrix to a file and close the file
		pw.write(sb.toString());
		pw.close();
		
		System.out.println(sb.toString());
	}
	
	private static Matrix feedForward(Matrix inputs) {
		for (int i = 0; i < weights.length; i++) {
			weights[i].printMatrixSize();
			inputs.printMatrixSize();
			
			inputs = Matrix.dot(weights[i], inputs);
			System.out.println(i + " - dot done");
			
			inputs = Matrix.add(inputs, biases[i]);
			System.out.println(i + " - add done");
			
			inputs = sigmoid(inputs);
			System.out.println(i + " - sigmoid done");
		}
		
		return inputs;
	}
	
	private static void SGD() {
		
	}
	
	private static void backPropagate() {
		
	}
	
	/* Sigmoid Function */
	private static Matrix sigmoid(Matrix z) {
		// basically return (1/(1+e^z)) = y for every index in z
		
		Matrix sigma = z;
		
		for (int i = 0; i < z.rows; i++) {
			for (int j = 0; j < z.columns; j++) {
				sigma.matrix[i][j] = (1.0 / (1.0 + Math.pow(e, z.matrix[i][j])));
			}
		}
		
		return sigma;
	}
	
	/* Derivative of Sigmoid Function */
	/*
	private static Matrix sigmoidPrime(Matrix z) {
		// basically return y(1 - y) for every index in z
		
		Matrix sigmaPrime = new Matrix(z);
		
		for (int i = 0; i < z.rows; i++) {
			for (int j = 0; j < z.columns; j++) {
				sigma.matrix[i][j] = sigmoid(z) * (1 - sigmoid(z));
			}
		}
		
		return sigmaPrime;
	}
	*/
	
	/* Test the Full Functionality of Matrices */
	private static void matrixTest() {
		double[][] first = {{4, -1}, {0, 5}};
		double[][] second = {{1, 8, 0}, {6, -2, 3}};
		
		Matrix firstM = new Matrix(first);
		System.out.println("\nFirst Matrix");
		firstM.printMatrix();
		
		Matrix secondM = new Matrix(second);
		System.out.println("\nSecondMatrix");
		secondM.printMatrix();
		
		Matrix fourthM = Matrix.dot(firstM, secondM);
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
	public final double[][] matrix;
	
	private Random r;
	
	/* Constructor of a New Matrix */
	public Matrix(int rows, int columns, boolean populate) {
		this.rows = rows;
		this.columns = columns;
		this.matrix = new double[rows][columns];
		
		// if we want to populate the matrix, then we'll fill it with randomized real numbers
		if (populate)
			randomizeItems();
	}
	
	/* Constructor of an Existing Array */
	public Matrix(double[][] oldMatrix) {
		this.rows = oldMatrix.length;
		this.columns = oldMatrix[0].length;
		this.matrix = oldMatrix;
	}
	
	/* Dot Product Between Two Matrices */
	public static Matrix dot(Matrix A, Matrix B) {
		if (A.columns == B.rows) {
			Matrix output = new Matrix(A.rows, B.columns, false);
			
			for (int i = 0; i < A.rows; i++) {
				for (int l = 0; l < B.columns; l++) {
					double c = 0;
					
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
	
	/* Create a Random Double For Each Position */
	private void randomizeItems() {
		// iterate through the entire matrix and fill it with random real numbers
		
		r = new Random();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.matrix[i][j] = r.nextDouble();
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

/*
References:
https://alvinalexander.com/java/edu/pj/pj010005 - Scanner
https://introcs.cs.princeton.edu/java/95linear/Matrix.java.html - Matrix class
https://stackoverflow.com/questions/30073980/java-writing-strings-to-a-csv-file - csv file
*/