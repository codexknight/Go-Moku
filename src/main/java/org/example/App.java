package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;



/**
 * JavaFX App
 */
public class App extends Application {

    private GoMokuBoard board;

    private Button newGameButton;

    private Button resignButton;

    private Label message;

    private static final int
            EMPTY=0,
            BLACK=1,
            WHITE = 2;

    @Override
    public void start(Stage stage) {

        newGameButton = new Button("New Game");
        resignButton = new Button("Resign");
        message = new Label("Click \"New Game\" to begin.");
        message.setFont(Font.font(null, FontWeight.BOLD,18));
        message.setTextFill(Color.rgb(100, 255, 100));
        board = new GoMokuBoard();//calls doNewGame() therefore message,newGameB,resignB must be initialized before board is created
        board.drawBoard();

        newGameButton.setOnAction(e -> board.doNewGame());
        resignButton.setOnAction(e -> board.doResign());
        board.setOnMousePressed(e -> board.mousePressed(e));

        board.relocate(20, 20);
        newGameButton.relocate(370, 120);
        resignButton.relocate(370,200);
        message.relocate(20, 370);

        resignButton.setManaged(false);
        newGameButton.setManaged(false);
        resignButton.resize(100,30);
        newGameButton.resize(100, 30);

        Pane root = new Pane();
        root.setPrefWidth(500);
        root.setPrefHeight(420);
        root.getChildren().addAll(board, newGameButton, resignButton, message);
        root.setStyle("-fx-background-color:darkgreen; -fx-border-color:darkred; -fx-border-width:3");

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Go Moku!");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private class GoMokuBoard extends Canvas {

        int[][] boardData;

        boolean gameInProgress;

        int currentPlayer;

        int win_r1, win_c1, win_r2, win_c2;


        GoMokuBoard() {
            super(314, 314);
            doNewGame();
        }

        public void doNewGame() {
            if (gameInProgress) {
                message.setText("Finnish the current game first!");
                return;
            }
            boardData = new int[13][13];
            currentPlayer = BLACK;
            message.setText("Black: Make your move.");
            gameInProgress = true;
            newGameButton.setDisable(true);
            resignButton.setDisable(false);
            drawBoard();
        }

        public void drawBoard() {
            GraphicsContext g = getGraphicsContext2D();
            g.setFill(Color.LIGHTGREY);
            g.fillRect(0,0,314, 314);

            g.setStroke(Color.BLACK);
            g.setLineWidth(2);

            for (int i=0;i<=13;i++) {
                g.strokeLine(0,1+24*i,314,1+24*i);
                g.strokeLine(1+24*i, 0, 1+24*i, 314);
            }

            for (int row = 0; row < 13; row++) {
                for (int col = 0; col < 13; col++) {
                    if (boardData[row][col] != EMPTY) {
                        drawPiece(g, boardData[row][col], row, col);
                    }
                }
            }

        }

        private void drawPiece(GraphicsContext g, int piece, int row, int col) {
            if (piece == WHITE) {
                g.setFill(Color.WHITE);
                g.fillOval(4 + 24 * col, 4 + 24 * row, 18, 18);
                g.setStroke(Color.BLACK);
                g.setLineWidth(1);
                g.strokeOval(4 + 24 * col, 4 + 24 * row, 18, 18);
            } else {
                g.setFill(Color.BLACK);
                g.fillOval(4 + 24 * col, 4 + 24 * row, 18, 18);
            }
        }



        public void doResign() {
            if (gameInProgress == false) {
                message.setText("There is no game in progress!");
                return;
            }
            if (currentPlayer == WHITE) {
                gameOver("WHITE resign. BLACK wins.");
            } else {
                gameOver("BLACK resign. WHITE wins.");
            }
        }

        private void gameOver(String s) {
            message.setText(s);
            newGameButton.setDisable(false);
            resignButton.setDisable(true);
            gameInProgress = false;
        }

        public void mousePressed(MouseEvent e) {
            if (gameInProgress == false) {
                message.setText("Click \"New Game\" to start a new game.");

            } else {
                int col = (int) ((e.getX() - 2) / 24);
                int row = (int) ((e.getY() - 2) / 24);
                if (col >= 0 && col < 13 && row >= 0 && row < 13) {
                    doClickSquare(row, col);
                }
            }
        }

        private void doClickSquare(int row, int col) {
            if (boardData[row][col] != EMPTY) {
                if (currentPlayer == WHITE) {
                    message.setText("WHITE: Please click an empty square.");

                } else {
                    message.setText("BLACK: Please click an empty square.");
                }
                return;
            }
            boardData[row][col] = currentPlayer;
            drawBoard();

            if (winner(row, col)) {
                if (currentPlayer == WHITE) {
                    gameOver("WHITE wins the game!");
                } else {
                    gameOver("BLACK wins the game!");
                }
                drawWinLine();
                return;
            }

            boolean emptySpace = false;
            for (int r = 0; r < 13; r++) {
                for (int c = 0; c < 13; c++) {
                    if (boardData[r][c] == EMPTY) {
                        emptySpace = true;
                        break;
                    }
                }
            }
            if (emptySpace == false) {
                gameOver("The game ends in a draw.");
                return;
            }

            if (currentPlayer == BLACK) {
                currentPlayer = WHITE;
                message.setText("WHITE: Make your move.");
            } else {
                currentPlayer = BLACK;
                message.setText("BLACK: Make your move.");
            }
        }

        private boolean winner(int row, int col) {
            if (count(boardData[row][col], row, col, 1, 0) >= 5) {
                return true;
            }
            if (count(boardData[row][col], row, col, 0, 1) >= 5) {
                return true;
            }
            if (count(boardData[row][col], row, col, 1, -1) >= 5) {
                return true;
            }
            if (count(boardData[row][col], row, col, 1, 1) >= 5) {
                return true;
            }
            return false;
        }

        private int count(int player, int row, int col, int dirX, int dirY) {
            int ct = 1;  // Number of pieces in a row belonging to the player.

            int r, c;    // A row and column to be examined

            r = row + dirX;  // Look at square in specified direction.
            c = col + dirY;
            while ( r >= 0 && r < 13 && c >= 0 && c < 13 && boardData[r][c] == player ) {
                // Square is on the board and contains one of the players's pieces.
                ct++;
                r += dirX;  // Go on to next square in this direction.
                c += dirY;
            }

            win_r1 = r - dirX;  // The next-to-last square looked at.
            win_c1 = c - dirY;  //    (The LAST one looked at was off the board or
            //    did not contain one of the player's pieces.

            r = row - dirX;  // Look in the opposite direction.
            c = col - dirY;
            while ( r >= 0 && r < 13 && c >= 0 && c < 13 && boardData[r][c] == player ) {
                // Square is on the board and contains one of the players's pieces.
                ct++;
                r -= dirX;   // Go on to next square in this direction.
                c -= dirY;
            }

            win_r2 = r + dirX;
            win_c2 = c + dirY;

            // At this point, (win_r1,win_c1) and (win_r2,win_c2) mark the endpoints
            // of the line of pieces belonging to the player.

            return ct;

        }

        private void drawWinLine() {
            GraphicsContext g = getGraphicsContext2D();
            g.setStroke(Color.RED);
            g.setLineWidth(4);
            g.strokeLine(13+24*win_c1,13+24*win_r1,13+24*win_c2,13+24*win_r2);
        }

    }


}