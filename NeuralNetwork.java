/*
//	Name:	Eric Ortiz
// CWID:	102-39-903
//	Date:	10/5/18
//	Assignment #2
//	Desc:	This program is a neural network of sigmoid neruons that are programmed to train themselves to learn individual
//				handwritten digits from the MNIST data set.
*/

import java.util.*;

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
	
	/* Create the Neural Network */
	private static void createNetwork() {
		layers = layerSizes.length;
		biases = new Matrix[layers-1];
		weights = new Matrix[layers-1];
		
		System.out.println("Biases:");
		for (int i = 1; i < layers; i++){
			System.out.println("Layer " + i);
			
			biases[i-1] = new Matrix(layerSizes[i], 1, true);
			biases[i-1].printMatrix();
		}
		
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
				
			}
			else {
				
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
					
				}
			}
		}
		else {
			System.out.println("The integer you submitted is not a valid command.\n");
		}
	}
	
	private static Matrix feedForward(Matrix inputs) {
		for (int i = 0; i < weights.length; i++) {
			inputs = Matrix.dot(weights[i], inputs);
			inputs = Matrix.add(inputs, biases[i]);
			inputs = sigmoid(inputs);
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
		
		Matrix sigma = new Matrix(z);
		
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
	
	/* Constructor of an Existing Matrix */
	public Matrix(Matrix oldMatrix) {
		this.rows = oldMatrix.rows;
		this.columns = oldMatrix.columns;
		this.matrix = oldMatrix.matrix;
	}
	
	/* Dot Product Between Two Matrices */
	public static Matrix dot(Matrix A, Matrix B) {
		return null;
	}
	
	/* Addition Between Two Matrices */
	public static Matrix add(Matrix A, Matrix B) {
		if (A.rows == B.rows && A.columns == B.columns) {
			return null;
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
				// if this is the last item in the column, don't add a comma
				if (j == columns-1) {
					System.out.print(matrix[i][j] + "");
				}
				else {
					System.out.print(matrix[i][j] + ", ");
				}
			}
			
			// if this is the last item in the row, don't add a comma
			if (i == rows-1) {
				System.out.print("]");
			}
			else {
				System.out.print("], ");
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
*/