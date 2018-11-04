package com.newtechsys.othello.Classes;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

public class Board {
    /***********
     * GLOBALS
     ***********/

    // initialize the enums used to determine what color is at each square on the board
    public enum State {
        EMPTY, BLACK, WHITE, VALID
    }

    // the foundation of the board Object is centered around a 2D-array of states
    public State[][] boardState = new State[8][8];

    /****************
     * CONSTRUCTORS
     ****************/

    /* Default Constructor */
    public Board() {
        // this method creates a brand new board Object with an "X" in the middle of the board; once
        // the board is created, we check for all available moves for the first player

        // iterate through all squares and make them empty except for the X in the middle of the board
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                if ((i == 3 && j == 3) || (i == 4 && j == 4))
                    boardState[i][j] = State.WHITE;
                else if ((i == 3 && j == 4) || (i == 4 && j == 3))
                    boardState[i][j] = State.BLACK;
                else
                    boardState[i][j] = State.EMPTY;
            }
        }

        // check for all valid moves for black pieces (since black starts first)
        checkForAllValidMoves(State.BLACK);
    }

    /***********
     * METHODS
     ***********/

    /* Check All Squares for Available Moves */
    public void checkForAllValidMoves(State currentTurnColor) {
        // this method iterate through the entire board and check each empty square's validity as a
        // move for the current player

        // we're checking for the number of pieces between the selected square an "end piece" of the
        // same color
        ArrayList<Pair<Integer, Integer>> inbetweenPieces;

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                // we only need to check if the move is valid in empty and previously valid squares
                if (boardState[i][j] == State.EMPTY || boardState[i][j] == State.VALID) {
                    // check in all 8 directions for flippable pieces
                    inbetweenPieces = inspectDirections(currentTurnColor, i, j);

                    // if we're not given back any pieces between the current piece and another of
                    // its same color, then make the square empty; otherwise, it's a valid move
                    if (inbetweenPieces.size() == 0) {
                        boardState[i][j] = State.EMPTY;
                    }
                    else {
                        boardState[i][j] = State.VALID;
                    }
                }
            }
        }
    }

    /* Check All Directions for In Between Pieces */
    public ArrayList<Pair<Integer, Integer>> inspectDirections(State endColor, int row, int column) {
        // this method checks all 8 cardinal directions from a square to see if there's any pieces
        // in between itself and a piece of the given end color

        // in the end we're going to return a list of in between pieces; if the list is empty then
        // this square isn't valid for a move
        ArrayList<Pair<Integer, Integer>> inbetweenPieces = new ArrayList<>();

        // for each direction we're going to create a buffer lists of pieces which we'll only add
        // to the main list of in between pieces if we come across an end piece
        ArrayList<Pair<Integer, Integer>> buffer;

        // we need to iterate across both rows and columns simultaneously when we check for diagonals
        // so we need temporary variables to iterate over
        int tempRow;
        int tempCol;

        // set the opposite color of what we're trying to place
        State inbetweenColor;
        if (endColor == State.BLACK)
            inbetweenColor = State.WHITE;
        else
            inbetweenColor = State.BLACK;

        // for vertical directions, we only iterate across the rows of the board for in between pieces

        // check North (negative rows)
        buffer = new ArrayList<>();
        for (int i = row; i >= 0; i--) {
            if (i == row)
                continue;
            else if (boardState[i][column] == inbetweenColor)
                buffer.add(new Pair<>(i, column));
            else if (boardState[i][column] == endColor) {
                inbetweenPieces.addAll(buffer);
                break;
            }
            else
                break;
        }

        // check South (positive rows)
        buffer = new ArrayList<>();
        for (int i = row; i < 8; i++) {
            if (i == row)
                continue;
            else if (boardState[i][column] == inbetweenColor)
                buffer.add(new Pair<>(i, column));
            else if (boardState[i][column] == endColor) {
                inbetweenPieces.addAll(buffer);
                break;
            }
            else
                break;
        }

        // for horizontal directions, we only iterate across the columns of the board for in between pieces

        // check East (positive columns)
        buffer = new ArrayList<>();
        for (int j = column; j < 8; j++) {
            if (j == column)
                continue;
            else if (boardState[row][j] == inbetweenColor)
                buffer.add(new Pair<>(row, j));
            else if (boardState[row][j] == endColor) {
                inbetweenPieces.addAll(buffer);
                break;
            }
            else
                break;
        }

        // check West (negative columns)
        buffer = new ArrayList<>();
        for (int j = column; j >= 0; j--) {
            if (j == column)
                continue;
            else if (boardState[row][j] == inbetweenColor)
                buffer.add(new Pair<>(row, j));
            else if (boardState[row][j] == endColor) {
                inbetweenPieces.addAll(buffer);
                break;
            }
            else
                break;
        }

        // for diagonals, we iterate across both rows and columns for in between pieces

        // check NorthEast (negative row, positive column)
        buffer = new ArrayList<>();
        tempRow = row - 1;
        tempCol = column + 1;
        while (tempRow >= 0 && tempCol < 8) {
            if (boardState[tempRow][tempCol] == inbetweenColor)
                buffer.add(new Pair<>(tempRow, tempCol));
            else if (boardState[tempRow][tempCol] == endColor) {
                inbetweenPieces.addAll(buffer);
                break;
            }
            else
                break;

            tempRow--;
            tempCol++;
        }

        // check NorthWest (negative row, negative column)
        buffer = new ArrayList<>();
        tempRow = row - 1;
        tempCol = column - 1;
        while (tempRow >= 0 && tempCol >= 0) {
            if (boardState[tempRow][tempCol] == inbetweenColor)
                buffer.add(new Pair<>(tempRow, tempCol));
            else if (boardState[tempRow][tempCol] == endColor) {
                inbetweenPieces.addAll(buffer);
                break;
            }
            else
                break;

            tempRow--;
            tempCol--;
        }

        // check SouthEast (positive row, positive column)
        buffer = new ArrayList<>();
        tempRow = row + 1;
        tempCol = column + 1;
        while (tempRow < 8 && tempCol < 8) {
            if (boardState[tempRow][tempCol] == inbetweenColor)
                buffer.add(new Pair<>(tempRow, tempCol));
            else if (boardState[tempRow][tempCol] == endColor) {
                inbetweenPieces.addAll(buffer);
                break;
            }
            else
                break;

            tempRow++;
            tempCol++;
        }

        // check SouthWest (positive row, negative column)
        buffer = new ArrayList<>();
        tempRow = row + 1;
        tempCol = column - 1;
        while (tempRow < 8 && tempCol >= 0) {
            if (boardState[tempRow][tempCol] == inbetweenColor)
                buffer.add(new Pair<>(tempRow, tempCol));
            else if (boardState[tempRow][tempCol] == endColor) {
                inbetweenPieces.addAll(buffer);
                break;
            }
            else
                break;

            tempRow++;
            tempCol--;
        }

        // return the list of in between pieces even it's empty
        return inbetweenPieces;
    }

    /* Print the ASCII Representation of the Board to LogCat */
    public void printASCIIBoard() {
        // this method converts the current board Object into ASCII and then prints it to LogCat

        // the ASCII board needs to be built dynamically, so we'll use a string builder for that
        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        // iterate through the entire board and shorten each space to single-letter representation
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                State currState = boardState[i][j];

                if (currState == State.EMPTY)
                    sb.append("|E|");
                else if (currState == State.BLACK)
                    sb.append("|B|");
                else if (currState == State.WHITE)
                    sb.append("|W|");
                else if (currState == State.VALID)
                    sb.append("|V|");
            }

            // end each row with a new line
            sb.append("\n");
        }

        // print the board
        Log.d("Board:", sb.toString());
    }
}
