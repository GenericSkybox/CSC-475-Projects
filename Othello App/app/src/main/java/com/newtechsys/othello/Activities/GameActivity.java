package com.newtechsys.othello.Activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtechsys.othello.Classes.Board;
import com.newtechsys.othello.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    /***********
     * GLOBALS
     ***********/

    // tag the activity for debug statements
    final static String TAG = "!GameActivity";

    // initialize the maximum depth of the mini max tree as a constant
    final static int maxDepth = 3;

    // we also need to keep track of the current depth of the tree
    int currDepth = 0;

    // create a board that will be used to keep track of piece placements
    Board board;

    // initialize some string constants to display depending on the state of the game
    final String turnPlayer1 = "Player 1's Turn";
    final String turnPlayer2 = "Player 2's Turn";
    final String turnSkipPlayer1 = "Skipped Player 1's Turn\nPlayer 2's Turn";
    final String turnSkipPlayer2 = "Skipped Player 2's Turn\nPlayer 1's Turn";
    final String turnWinner1 = "Player 1 Wins!";
    final String turnWinner2 = "Player 2 Wins!";
    final String turnWinnerTie = "It's a Tie!";

    // declare all of the views used in the layout that will be altered throughout the course of play
    public ImageButton[][] squares = new ImageButton[8][8];
    public ImageView player1ColorImage;
    public ImageView player2ColorImage;
    public TextView player1Score;
    public TextView player2Score;
    public TextView turnIndicator;

    // keep track of the both player's color and which player is making the next move
    public Board.State currentPlayerColor;
    public Board.State player1Color;
    public Board.State player2Color;

    // keep track of whether or not the player is against the computer or another human
    public boolean againstComputer;

    // keep track of how many black and white pieces are on the board
    public int whitePieces = 2;
    public int blackPieces = 2;

    // used to block the player from making more moves after the game is over
    public boolean isGameOver = false;

    // declare extras from the previous activity
    Bundle extras;

    // used to keep track of whether or not the AI is making a move so we can lock the human
    // player out of a move until the AI is finished
    boolean inTheMiddleOfAITurn = false;

    // used to display an AI's potential board states as it travers the MiniMax algorithm
    boolean DEBUG = false;

    /**********************
     * ACTIVITY LIFECYCLE
     **********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this method is called whenever the user starts a new game - it initializes a new board
        // and sets up all of the variables needed to play a game of Othello

        // create the activity and set up its layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // set up the board and other objects needed to play the game
        startNewGame();

        // set up the reset button
        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGame();
            }
        });

        // set up the debug checkbox
        CheckBox debugCheck = findViewById(R.id.debug_check);
        debugCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DEBUG = ((CheckBox)view).isChecked();
                Log.i(TAG, "Debug is now " + DEBUG);
            }
        });
    }

    /***********
     * METHODS
     ***********/

    /* Start New Game */
    public void startNewGame() {
        // this method sets up the player's color, the board and its view, as well as the initial score

        // determine what color player 1 and player 2 is
        getPlayerColor();

        // determine if the user is playing against an AI
        if (extras != null)
            againstComputer = extras.getBoolean("AgainstComputer");

        // initialize the views for the squares on the board and the other score-keeping views around
        // the board
        initializeBoardViews();
        initializeScoreKeeping();

        // create a new board object that will keep track of the color of each square on the visual
        // board
        board = new Board();

        // print our starting board for reference
        Log.d(TAG, "Starting Board");
        board.printASCIIBoard();

        // Othello always starts with black pieces first
        currentPlayerColor = Board.State.BLACK;

        // update the board visually to match the board object and also update date the scores
        updateBoard();
        updateScores();

        // reset the game over state
        isGameOver = false;

        // if the AI is turned on and it's going first, then compute its turn
        if (againstComputer && (player2Color == currentPlayerColor))
            computeAITurn();
    }

    /* Determine the Color of Each Player */
    public void getPlayerColor() {
        // this method grabs the extras sent from the previous activity and uses those to determine
        // which color player 1 is supposed to be

        // make sure we have extras in the first place and default to player 1 being black otherwise
        extras = getIntent().getExtras();
        if (extras != null) {
            // store the color of player 1 and 2 depending on the extra
            if (extras.getBoolean("StartingBlack")) {
                player1Color = Board.State.BLACK;
                player2Color = Board.State.WHITE;
            }
            else {
                player1Color = Board.State.WHITE;
                player2Color = Board.State.BLACK;
            }
        }
        else {
            Log.e(TAG, "Error grabbing extras");
            player1Color = Board.State.BLACK;
            player2Color = Board.State.WHITE;
        }
    }

    /* Initialize All Squares for the Board */
    public void initializeBoardViews() {
        // this method stores all of the image buttons that represent squares on the board as a 2D
        // array of button

        // row 0
        squares[0][0] = findViewById(R.id.square00);
        squares[0][1] = findViewById(R.id.square01);
        squares[0][2] = findViewById(R.id.square02);
        squares[0][3] = findViewById(R.id.square03);
        squares[0][4] = findViewById(R.id.square04);
        squares[0][5] = findViewById(R.id.square05);
        squares[0][6] = findViewById(R.id.square06);
        squares[0][7] = findViewById(R.id.square07);

        // row 1
        squares[1][0] = findViewById(R.id.square10);
        squares[1][1] = findViewById(R.id.square11);
        squares[1][2] = findViewById(R.id.square12);
        squares[1][3] = findViewById(R.id.square13);
        squares[1][4] = findViewById(R.id.square14);
        squares[1][5] = findViewById(R.id.square15);
        squares[1][6] = findViewById(R.id.square16);
        squares[1][7] = findViewById(R.id.square17);

        // row 2
        squares[2][0] = findViewById(R.id.square20);
        squares[2][1] = findViewById(R.id.square21);
        squares[2][2] = findViewById(R.id.square22);
        squares[2][3] = findViewById(R.id.square23);
        squares[2][4] = findViewById(R.id.square24);
        squares[2][5] = findViewById(R.id.square25);
        squares[2][6] = findViewById(R.id.square26);
        squares[2][7] = findViewById(R.id.square27);

        // row 3
        squares[3][0] = findViewById(R.id.square30);
        squares[3][1] = findViewById(R.id.square31);
        squares[3][2] = findViewById(R.id.square32);
        squares[3][3] = findViewById(R.id.square33);
        squares[3][4] = findViewById(R.id.square34);
        squares[3][5] = findViewById(R.id.square35);
        squares[3][6] = findViewById(R.id.square36);
        squares[3][7] = findViewById(R.id.square37);

        // row 4
        squares[4][0] = findViewById(R.id.square40);
        squares[4][1] = findViewById(R.id.square41);
        squares[4][2] = findViewById(R.id.square42);
        squares[4][3] = findViewById(R.id.square43);
        squares[4][4] = findViewById(R.id.square44);
        squares[4][5] = findViewById(R.id.square45);
        squares[4][6] = findViewById(R.id.square46);
        squares[4][7] = findViewById(R.id.square47);

        // row 5
        squares[5][0] = findViewById(R.id.square50);
        squares[5][1] = findViewById(R.id.square51);
        squares[5][2] = findViewById(R.id.square52);
        squares[5][3] = findViewById(R.id.square53);
        squares[5][4] = findViewById(R.id.square54);
        squares[5][5] = findViewById(R.id.square55);
        squares[5][6] = findViewById(R.id.square56);
        squares[5][7] = findViewById(R.id.square57);

        // row 6
        squares[6][0] = findViewById(R.id.square60);
        squares[6][1] = findViewById(R.id.square61);
        squares[6][2] = findViewById(R.id.square62);
        squares[6][3] = findViewById(R.id.square63);
        squares[6][4] = findViewById(R.id.square64);
        squares[6][5] = findViewById(R.id.square65);
        squares[6][6] = findViewById(R.id.square66);
        squares[6][7] = findViewById(R.id.square67);

        // row 7
        squares[7][0] = findViewById(R.id.square70);
        squares[7][1] = findViewById(R.id.square71);
        squares[7][2] = findViewById(R.id.square72);
        squares[7][3] = findViewById(R.id.square73);
        squares[7][4] = findViewById(R.id.square74);
        squares[7][5] = findViewById(R.id.square75);
        squares[7][6] = findViewById(R.id.square76);
        squares[7][7] = findViewById(R.id.square77);
    }

    /* Initialize All Views for Score and Turn Keeping */
    public void initializeScoreKeeping() {
        // this method sets up all of the views required for score and turn keeping, as well as
        // displaying the proper color icon for player 1 and 2

        // initialize the views
        player1ColorImage = findViewById(R.id.player1_color);
        player1Score = findViewById(R.id.player1_score);
        player2ColorImage = findViewById(R.id.player2_color);
        player2Score = findViewById(R.id.player2_score);
        turnIndicator = findViewById(R.id.turn_indicator);

        // depending on player 1's color, change the icons of the player's color at the top of the
        // screen as well as change what the turn indicator says at the bottom of the board
        if (player1Color == Board.State.BLACK) {
            player1ColorImage.setImageDrawable(getDrawable(R.drawable.black_circle));
            player2ColorImage.setImageDrawable(getDrawable(R.drawable.white_circle));
            turnIndicator.setText(turnPlayer1);
        }
        else {
            player1ColorImage.setImageDrawable(getDrawable(R.drawable.white_circle));
            player2ColorImage.setImageDrawable(getDrawable(R.drawable.black_circle));
            turnIndicator.setText(turnPlayer2);
        }
    }

    /* Determine Which Square Was Selected */
    public void onSquareClick(View view) {
        // this method is called by every single square on the board - it takes the ID of that
        // square and, based on that ID, sends the square's position to placing a piece if the user
        // is able to

        // declare the square's position as a pair (row, column)
        Pair<Integer, Integer> squarePosition;

        // depending on which square was selected, store that square's position
        switch(view.getId()) {
            // row 0
            case R.id.square00:
                squarePosition = new Pair<>(0, 0);
                break;
            case R.id.square01:
                squarePosition = new Pair<>(0, 1);
                break;
            case R.id.square02:
                squarePosition = new Pair<>(0, 2);
                break;
            case R.id.square03:
                squarePosition = new Pair<>(0, 3);
                break;
            case R.id.square04:
                squarePosition = new Pair<>(0, 4);
                break;
            case R.id.square05:
                squarePosition = new Pair<>(0, 5);
                break;
            case R.id.square06:
                squarePosition = new Pair<>(0, 6);
                break;
            case R.id.square07:
                squarePosition = new Pair<>(0, 7);
                break;

            // row 1
            case R.id.square10:
                squarePosition = new Pair<>(1, 0);
                break;
            case R.id.square11:
                squarePosition = new Pair<>(1, 1);
                break;
            case R.id.square12:
                squarePosition = new Pair<>(1, 2);
                break;
            case R.id.square13:
                squarePosition = new Pair<>(1, 3);
                break;
            case R.id.square14:
                squarePosition = new Pair<>(1, 4);
                break;
            case R.id.square15:
                squarePosition = new Pair<>(1, 5);
                break;
            case R.id.square16:
                squarePosition = new Pair<>(1, 6);
                break;
            case R.id.square17:
                squarePosition = new Pair<>(1, 7);
                break;

            // row 2
            case R.id.square20:
                squarePosition = new Pair<>(2, 0);
                break;
            case R.id.square21:
                squarePosition = new Pair<>(2, 1);
                break;
            case R.id.square22:
                squarePosition = new Pair<>(2, 2);
                break;
            case R.id.square23:
                squarePosition = new Pair<>(2, 3);
                break;
            case R.id.square24:
                squarePosition = new Pair<>(2, 4);
                break;
            case R.id.square25:
                squarePosition = new Pair<>(2, 5);
                break;
            case R.id.square26:
                squarePosition = new Pair<>(2, 6);
                break;
            case R.id.square27:
                squarePosition = new Pair<>(2, 7);
                break;

            // row 3
            case R.id.square30:
                squarePosition = new Pair<>(3, 0);
                break;
            case R.id.square31:
                squarePosition = new Pair<>(3, 1);
                break;
            case R.id.square32:
                squarePosition = new Pair<>(3, 2);
                break;
            case R.id.square33:
                squarePosition = new Pair<>(3, 3);
                break;
            case R.id.square34:
                squarePosition = new Pair<>(3, 4);
                break;
            case R.id.square35:
                squarePosition = new Pair<>(3, 5);
                break;
            case R.id.square36:
                squarePosition = new Pair<>(3, 6);
                break;
            case R.id.square37:
                squarePosition = new Pair<>(3, 7);
                break;

            // row 4
            case R.id.square40:
                squarePosition = new Pair<>(4, 0);
                break;
            case R.id.square41:
                squarePosition = new Pair<>(4, 1);
                break;
            case R.id.square42:
                squarePosition = new Pair<>(4, 2);
                break;
            case R.id.square43:
                squarePosition = new Pair<>(4, 3);
                break;
            case R.id.square44:
                squarePosition = new Pair<>(4, 4);
                break;
            case R.id.square45:
                squarePosition = new Pair<>(4, 5);
                break;
            case R.id.square46:
                squarePosition = new Pair<>(4, 6);
                break;
            case R.id.square47:
                squarePosition = new Pair<>(4, 7);
                break;

            // row 5
            case R.id.square50:
                squarePosition = new Pair<>(5, 0);
                break;
            case R.id.square51:
                squarePosition = new Pair<>(5, 1);
                break;
            case R.id.square52:
                squarePosition = new Pair<>(5, 2);
                break;
            case R.id.square53:
                squarePosition = new Pair<>(5, 3);
                break;
            case R.id.square54:
                squarePosition = new Pair<>(5, 4);
                break;
            case R.id.square55:
                squarePosition = new Pair<>(5, 5);
                break;
            case R.id.square56:
                squarePosition = new Pair<>(5, 6);
                break;
            case R.id.square57:
                squarePosition = new Pair<>(5, 7);
                break;

            // row 6
            case R.id.square60:
                squarePosition = new Pair<>(6, 0);
                break;
            case R.id.square61:
                squarePosition = new Pair<>(6, 1);
                break;
            case R.id.square62:
                squarePosition = new Pair<>(6, 2);
                break;
            case R.id.square63:
                squarePosition = new Pair<>(6, 3);
                break;
            case R.id.square64:
                squarePosition = new Pair<>(6, 4);
                break;
            case R.id.square65:
                squarePosition = new Pair<>(6, 5);
                break;
            case R.id.square66:
                squarePosition = new Pair<>(6, 6);
                break;
            case R.id.square67:
                squarePosition = new Pair<>(6, 7);
                break;

            // row 7
            case R.id.square70:
                squarePosition = new Pair<>(7, 0);
                break;
            case R.id.square71:
                squarePosition = new Pair<>(7, 1);
                break;
            case R.id.square72:
                squarePosition = new Pair<>(7, 2);
                break;
            case R.id.square73:
                squarePosition = new Pair<>(7, 3);
                break;
            case R.id.square74:
                squarePosition = new Pair<>(7, 4);
                break;
            case R.id.square75:
                squarePosition = new Pair<>(7, 5);
                break;
            case R.id.square76:
                squarePosition = new Pair<>(7, 6);
                break;
            case R.id.square77:
                squarePosition = new Pair<>(7, 7);
                break;

            default:
                squarePosition = new Pair<>(8, 8);
                break;
        }

        // if the board state is valid at that square and the game isn't over yet, let the player
        // place their piece at that position
        if (board.boardState[squarePosition.first][squarePosition.second] == Board.State.VALID && !isGameOver) {
            // we also need to lock the player out of using moves during an AI's turn
            if (!((player2Color == currentPlayerColor) && againstComputer))
                placePiece(squarePosition.first, squarePosition.second, board);
        }
    }

    /* Place the Piece in the Selected Square */
    public void placePiece(int row, int column, Board givenBoard) {
        // this method changes the color of the selected square to be that of the current player's
        // color; furthermore, this method handles all of the repercussions of that action (i.e.
        // flipping in between pieces and updating the board state)

        // change the selected square to the current player's color
        givenBoard.boardState[row][column] = currentPlayerColor;

        // discover which squares need to be flipped and then change those square's color
        ArrayList<Pair<Integer, Integer>> inbetweenPieces = givenBoard.inspectDirections(currentPlayerColor, row, column);
        flipPieces(inbetweenPieces, givenBoard);

        // update the board and scores visually and then move on to the next turn if we're not in
        // the middle of the computer's turn
        if (!inTheMiddleOfAITurn) {
            updateBoard();
            updateScores();
            updateTurn();
        }
    }

    /* Flip All Pieces that Need to Be Flipped */
    public void flipPieces(ArrayList<Pair<Integer, Integer>> piecesToFlip, Board givenBoard) {
        // iterate through our list of pieces to flip and change their color to the current player's
        // color
        for (int i = 0; i < piecesToFlip.size(); i++) {
            Pair<Integer, Integer> position = piecesToFlip.get(i);
            givenBoard.boardState[position.first][position.second] = currentPlayerColor;
        }
    }

    /* Update the Board Visually */
    public void updateBoard() {
        // this method changes the image of all of the image buttons that comprise the visual board
        // to match that of the board Object

        // print the board in ASCII if we're debugging
        if (DEBUG) {
            Log.d(TAG, "Current board state");
            board.printASCIIBoard();
        }

        // iterate through the entire board and update each image button's background as we go
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares.length; j++) {
                if (board.boardState[i][j] == Board.State.EMPTY) {
                    squares[i][j].setBackground(getDrawable(R.drawable.empty_square));
                }
                else if (board.boardState[i][j] == Board.State.BLACK) {
                    squares[i][j].setBackground(getDrawable(R.drawable.black_square));
                }
                else if (board.boardState[i][j] == Board.State.WHITE) {
                    squares[i][j].setBackground(getDrawable(R.drawable.white_square));
                }
                else if (board.boardState[i][j] == Board.State.VALID) {
                    squares[i][j].setBackground(getDrawable(R.drawable.available_square));
                }
            }
        }
    }

    /* Update the Turn Order */
    public void updateTurn() {
        // this method handles all of the actions that need to complete at the end of a turn and
        // switching board control to the next player (if they can make a valid move)

        // determine the next color to place a piece
        Board.State nextPlayerColor;
        if (currentPlayerColor == Board.State.BLACK)
            nextPlayerColor = Board.State.WHITE;
        else
            nextPlayerColor = Board.State.BLACK;

        // check for all valid moves for the player next turn and then see if they have any valid moves
        ArrayList<Pair<Integer, Integer>> availableMoves = board.checkForAllValidMoves(nextPlayerColor);
        boolean nextCanTurnProceed = availableMoves.size() > 0;

        // if the next player can make a valid move, then update the current player's color and the
        // board state
        if (nextCanTurnProceed) {
            currentPlayerColor = nextPlayerColor;
            updateBoard();

            // display who's going next
            if (currentPlayerColor == player1Color)
                turnIndicator.setText(turnPlayer1);
            else {
                turnIndicator.setText(turnPlayer2);

                // if player 2 is the computer, then compute its turn
                if (againstComputer)
                    computeAITurn();
            }
        }
        // otherwise, check to see if the current player has any valid moves (since we're skipping
        // the next player)
        else {
            availableMoves = board.checkForAllValidMoves(currentPlayerColor);
            nextCanTurnProceed = availableMoves.size() > 0;

            // if the current player can proceed, then we update the board and allow them to
            // continue play
            if (nextCanTurnProceed) {
                updateBoard();

                // display who's going next
                if (currentPlayerColor == player1Color)
                    turnIndicator.setText(turnSkipPlayer2);
                else {
                    turnIndicator.setText(turnSkipPlayer1);

                    // if player 2 is the computer, then compute its turn
                    if (againstComputer)
                        computeAITurn();
                }
            }
            // else if the current player also has no more valid moves, then the game is over and we
            // count the number of pieces left on the board and end the game
            else
                gameOver();
        }
    }

    /* Update the Score Views and Values */
    public void updateScores() {
        // this method computes the number of black and white pieces on the board and displays their
        // totals to the players

        // reset the pieces to be 0
        blackPieces = 0;
        whitePieces = 0;

        // determine how many white and black pieces are on the board
        for (int i = 0; i < board.boardState.length; i++) {
            for (int j = 0; j < board.boardState[i].length; j++) {
                if (board.boardState[i][j] == Board.State.BLACK) {
                    blackPieces++;
                }
                else if (board.boardState[i][j] == Board.State.WHITE) {
                    whitePieces++;
                }
            }
        }

        // display these amounts to the players, depending on their color
        String scoreText = ": ";
        String player1ScoreText;
        String player2ScoreText;

        if (player1Color == Board.State.BLACK) {
            player1ScoreText = scoreText + blackPieces;
            player2ScoreText = scoreText + whitePieces;
        }
        else {
            player1ScoreText = scoreText + whitePieces;
            player2ScoreText = scoreText + blackPieces;
        }

        player1Score.setText(player1ScoreText);
        player2Score.setText(player2ScoreText);
    }

    /* Determine Where the AI Will Place Its Piece */
    public void computeAITurn() {
        // this method utilizes the MiniMax algorithm on a separate thread to compute the most
        // optimal move for the AI

        Log.d(TAG, "Starting MiniMax On Current Board");

        // signify to the place piece method that we're in the middle of an AI's turn
        inTheMiddleOfAITurn = true;

        // set up a handler (a thread essentially) that will run the AI's calculations after a set
        // delay
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // create a copy of the current board state and pass it to the MiniMax algorithm
                Board tempBoard = new Board(board);
                miniMax(tempBoard, true);
            }
        }, 1000);
    }

    /* Recursive MiniMax Algorithm */
    public Integer miniMax(Board currBoard, boolean maximizing) {
        // this method implements the MiniMax tree search algorithm recursively to find the most
        // optimal piece placement for the AI

        // first we need to determine the number of available moves, depending on whether or not
        // we're maximizing or minimizing
        ArrayList<Pair<Integer, Integer>> availableMoves;
        if (maximizing)
            availableMoves = currBoard.checkForAllValidMoves(player2Color);
        else
            availableMoves = currBoard.checkForAllValidMoves(player1Color);

        // ultimately we're going to return the best heuristic for this node, so we need to keep
        // track of all heuristics we gather
        ArrayList<Integer> heuristics = new ArrayList<>(availableMoves.size());

        // if we have any available moves, then we need to determine the heuristic for each leaf
        if (availableMoves.size() > 0) {
            for (int i = 0; i < availableMoves.size(); i++) {
                // grab the move for this leaf node
                Pair<Integer, Integer> move = availableMoves.get(i);

                // set up the "current" player's color depending on whether or not we're maximizing
                if (maximizing)
                    currentPlayerColor = player2Color;
                else
                    currentPlayerColor = player1Color;

                // create a new board (which is cloned off of the current board) and place a piece on it
                Board newBoard = new Board(currBoard);
                placePiece(move.first, move.second, newBoard);

                // if debug is turned on, print where we are in the algorithm and the resulting
                // board
                if (DEBUG) {
                    Log.d(TAG, "Depth - " + currDepth + " Move - " + i);
                    newBoard.printASCIIBoard();
                }

                // if we're not at maximum depth, increment the current depth and compute the
                // heuristic for the next layer
                if (currDepth < maxDepth) {
                    currDepth++;
                    heuristics.add(miniMax(newBoard, !maximizing));
                }
                // if we're at maximum depth, determine the heuristic of the leaf node
                else {
                    if (maximizing)
                        heuristics.add(determineHeuristic(newBoard, player2Color));
                    else
                        heuristics.add(determineHeuristic(newBoard, player1Color));
                }
            }
        }
        // if we have no moves on a maximizing leaf, then we've essentially "lost"; meanwhile if the
        // opponent has no moves on a minimizing leaf, then we've essentially "won"
        else {
            if (maximizing)
                heuristics.add(-10000);
            else
                heuristics.add(1000);
        }

        // after we're done computing all of the leaf node heuristics for the current node, we need
        // to go up a level in depth
        currDepth--;

        // if we're "above the tree" at this point, that means we have computed all of the nodes
        // below the base node -- in other we're we're done and we need to compute the best
        // heuristic and place a piece based off of that's heuristic's place in the move list
        if (currDepth == -1) {
            int currentMax = heuristics.get(0);
            int heuristicPos = 0;

            for (int i = 0; i < heuristics.size(); i++) {
                if (heuristics.get(i) > currentMax) {
                    currentMax = heuristics.get(i);
                    heuristicPos = i;
                }
            }

//            Log.w(TAG, "Heuristic Chosen " + currentMax + " On Move " + heuristicPos);

            // reset all of the variables changed so that we're back to the original board state
            currentPlayerColor = player2Color;
            currDepth = 0;
            inTheMiddleOfAITurn = false;

            // place the most optimal piece on the board
            Pair<Integer, Integer> move = availableMoves.get(heuristicPos);
            placePiece(move.first, move.second, board);
            return null;
        }
        // if we're on a maximizing layer, but not done with the tree, we take the highest heuristic
        else if (maximizing)
            return Collections.max(heuristics);
        // else, we're on a minimizing layer and not done with the tree, so we take the lowest heuristic
        else
            return Collections.min(heuristics);
    }

    /* Determine the Final Heuristic for a Leaf */
    public int determineHeuristic(Board finalBoard, Board.State playerColor) {
        // this method determines the heuristic for each node based on the amount of pieces the AI
        // has compared to those that the opponent has

        int blackAmount = 0;
        int whiteAmount = 0;

        // determine how many white and black pieces are on the board
        for (int i = 0; i < finalBoard.boardState.length; i++) {
            for (int j = 0; j < finalBoard.boardState[i].length; j++) {
                if (finalBoard.boardState[i][j] == Board.State.BLACK) {
                    blackAmount++;
                }
                else if (finalBoard.boardState[i][j] == Board.State.WHITE) {
                    whiteAmount++;
                }
            }
        }

//        Log.i(TAG, "Black " + blackAmount + " White " + whiteAmount + " Max Heuristic " + (whiteAmount - blackAmount) + " Min Heuristic " + (blackAmount - whiteAmount));

        // determine the heuristic depending on the given player color
        if (playerColor == Board.State.BLACK)
            return blackAmount - whiteAmount;
        else
            return whiteAmount - blackAmount;
    }

    /* Compute Final Actions for Match */
    public void gameOver() {
        // this method declares the winning player and locks the board from further usage

        // determine first which color won (or if it's a tie)
        Board.State winningColor;
        if (blackPieces > whitePieces) {
            winningColor = Board.State.BLACK;
        }
        else if (whitePieces > blackPieces) {
            winningColor = Board.State.WHITE;
        }
        else {
            winningColor = Board.State.EMPTY;
        }

        // then display which player won based on the winning color
        if (player1Color == winningColor)
            turnIndicator.setText(turnWinner1);
        else if (player2Color == winningColor)
            turnIndicator.setText(turnWinner2);
        else
            turnIndicator.setText(turnWinnerTie);

        isGameOver = true;
    }
}
