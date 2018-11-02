package com.newtechsys.othello.Classes;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;

/*********************************
 Created by eortiz on 10/29/2018.
 **********************************/
public class Board {
    public enum State {
        EMPTY, BLACK, WHITE, VALID
    }

    public State[][] boardState = new State[8][8];

    public Board() {
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

    public void checkForAllValidMoves(State currentTurnColor) {
        // iterate through the entire board and check each empty square's validity as a move for the
        // current player
        ArrayList<Pair<Integer, Integer>> inbetweenPieces;

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                // we only need to check if the move is valid in empty and previously valid squares
                if (boardState[i][j] == State.EMPTY || boardState[i][j] == State.VALID) {
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

    public ArrayList<Pair<Integer, Integer>> inspectDirections(State endColor, int row, int column) {
        ArrayList<Pair<Integer, Integer>> inbetweenPieces = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> buffer;
        int tempRow;
        int tempCol;

        // set the opposite color of what we're trying to place
        State inbetweenColor;
        if (endColor == State.BLACK)
            inbetweenColor = State.WHITE;
        else
            inbetweenColor = State.BLACK;

        // for each direction we check, we add each inbetweenColor position to our buffer until we
        // hit our endColor; once we hit our end color, we dump all of the inbetweenColor positions
        // into the array list that we return; if we don't hit our end color, then we essentially
        // ignore the buffer and move on

        // check North
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

        // check South
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

        // check East
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

        // check West
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

        // check NorthEast
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

        // check NorthWest
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

        // check SouthEast
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

        // check NorthEast
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

        return inbetweenPieces;
    }

    public void printBoard() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");

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

            sb.append("\n");
        }

        Log.d("Board:", sb.toString());
    }
}
