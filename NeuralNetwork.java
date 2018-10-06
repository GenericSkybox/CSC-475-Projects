/*
//	Name:	Eric Ortiz
// CWID:	102-39-903
//	Date:	10/5/18
//	Assignment #2
//	Desc:	This program is a neural network of sigmoid neruons that are programmed to train themselves to learn individual
//				handwritten digits from the MNIST data set.
*/

import java.util.Scanner;

public class NeuralNetwork {
	final static double e = 2.71828;
	
	public static void main(String[] args) {
		System.out.println("Welcome to Eric Ortiz's MNIST Digit Recognizer! \nThere's unfortunately not much done yet, so a lot of features are missing currently.");
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			printOptions();
			String input = scanner.next();
			
			System.out.println("Your response was - " + input);
			
			try {
				int response = Integer.parseInt(input);
				
				if (response == 0)
					break;
				else
					parseInput(response);
				
				parseInput(Integer.parseInt(input));
			} catch (Exception e) {
				System.out.println("Error parsing your input - please only enter digits");
			}
		}
		
		System.out.println("\nGoodbye!");
		System.exit(0);
	}
	
	private static void printOptions() {
		System.out.println("Please select an option:");
		System.out.println("   [1] Train the Network");
		System.out.println("   [2] Load a Pre-Trained Network");
		System.out.println("   [3] Display Network Accuracy on TRAINING Set");
		System.out.println("   [4] Display Network Accuracy on TRAINING Set");
		System.out.println("   [5] Save the Current Network State to File");
		System.out.println("   [0] Exit");
	}
	
	private static void parseInput(int input) {
		
	}
	
	private class Neuron {
		
	}
}

/*
References:
https://alvinalexander.com/java/edu/pj/pj010005 - Scanner

*/