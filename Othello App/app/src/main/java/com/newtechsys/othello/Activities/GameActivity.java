package com.newtechsys.othello.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtechsys.othello.Classes.Board;
import com.newtechsys.othello.R;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    final static String TAG = "!GameActivity";

    Board board;

    final String turnPlayer1 = "Player 1's Turn";
    final String turnPlayer2 = "Player 2's Turn";
    final String turnSkipPlayer1 = "Skipped Player 1's Turn\nPlayer 2's Turn";
    final String turnSkipPlayer2 = "Skipped Player 2's Turn\nPlayer 1's Turn";
    final String turnWinner1 = "Player 1 Wins!";
    final String turnWinner2 = "Player 2 Wins!";
    final String turnWinnerTie = "It's a Tie!";

    public ImageButton[][] squares = new ImageButton[8][8];
    public ImageView player1ColorImage;
    public ImageView player2ColorImage;
    public TextView player1Score;
    public TextView player2Score;
    public TextView turnIndicator;

    public Board.State currentPlayerColor;
    public Board.State player1Color;
    public Board.State player2Color;

    public int whitePieces = 2;
    public int blackPieces = 2;

    public boolean isGameOver = false;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getPlayerColor();
        initializeBoardViews();
        initializeScoreKeeping();

        board = new Board();
        board.printBoard();

        currentPlayerColor = Board.State.BLACK;

        updateBoard();
        updateScores();
    }

    private void getPlayerColor() {
        extras = getIntent().getExtras();
        if (extras != null) {
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

    /* Initialize All Views for the Board */
    private void initializeBoardViews() {
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
    private void initializeScoreKeeping() {
        player1ColorImage = findViewById(R.id.player1_color);
        player1Score = findViewById(R.id.player1_score);
        player2ColorImage = findViewById(R.id.player2_color);
        player2Score = findViewById(R.id.player2_score);
        turnIndicator = findViewById(R.id.turn_indicator);

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

        if (board.boardState[squarePosition.first][squarePosition.second] == Board.State.VALID && !isGameOver) {
            placePiece(squarePosition.first, squarePosition.second);
        }
    }

    /* Place the Piece in the Selected Square */
    public void placePiece(int row, int column) {
        // change the square to the current player's color
        board.boardState[row][column] = currentPlayerColor;

        // flip all pieces between this piece and an end piece in all directions
        ArrayList<Pair<Integer, Integer>> inbetweenPieces = board.inspectDirections(currentPlayerColor, row, column);
        flipPieces(inbetweenPieces);

        // update the board and scores visually and then move on to the next turn
        updateBoard();
        updateScores();
        updateTurn();
    }

    /* Flip All Pieces that Need to Be Flipped */
    public void flipPieces(ArrayList<Pair<Integer, Integer>> piecesToFlip) {
        // iterate through our list of pieces to flip and change their color to the current player's
        // color
        for (int i = 0; i < piecesToFlip.size(); i++) {
            Pair<Integer, Integer> position = piecesToFlip.get(i);
            board.boardState[position.first][position.second] = currentPlayerColor;
        }
    }

    public void updateBoard() {
        board.printBoard();

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

    public void updateTurn() {
        Board.State nextPlayerColor;

        // determine the next color to place a piece
        if (currentPlayerColor == Board.State.BLACK)
            nextPlayerColor = Board.State.WHITE;
        else
            nextPlayerColor = Board.State.BLACK;

        board.checkForAllValidMoves(nextPlayerColor);
        boolean nextCanTurnProceed = areThereValidMoves();

        if (nextCanTurnProceed) {
            currentPlayerColor = nextPlayerColor;
            updateBoard();

            // display who's going next
            if (currentPlayerColor == player1Color)
                turnIndicator.setText(turnPlayer1);
            else
                turnIndicator.setText(turnPlayer2);
        }
        else {
            board.checkForAllValidMoves(currentPlayerColor);
            nextCanTurnProceed = areThereValidMoves();

            if (nextCanTurnProceed) {
                updateBoard();

                // display who's going next
                if (currentPlayerColor == player1Color)
                    turnIndicator.setText(turnSkipPlayer2);
                else
                    turnIndicator.setText(turnSkipPlayer1);
            }
            else
                gameOver();
        }
    }

    public boolean areThereValidMoves() {
        boolean thereAreValidMoves = false;
        for (int i = 0; i < board.boardState.length; i++) {
            for (int j = 0; j < board.boardState[i].length; j++) {
                if (board.boardState[i][j] == Board.State.VALID) {
                    thereAreValidMoves = true;
                    break;
                }
            }
        }

        return thereAreValidMoves;
    }

    public void updateScores() {
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

        // display these amounts to the user
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

    public void gameOver() {
        Board.State winningColor;

        // determine which color won (or if it's a tie)
        if (blackPieces > whitePieces) {
            winningColor = Board.State.BLACK;
        }
        else if (whitePieces > blackPieces) {
            winningColor = Board.State.WHITE;
        }
        else {
            winningColor = Board.State.EMPTY;
        }

        // display to the user which player won (or if it's a tie)
        if (player1Color == winningColor)
            turnIndicator.setText(turnWinner1);
        else if (player2Color == winningColor)
            turnIndicator.setText(turnWinner2);
        else
            turnIndicator.setText(turnWinnerTie);

        isGameOver = true;
    }
}
