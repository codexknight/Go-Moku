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

import java.util.EmptyStackException;

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



        GoMokuBoard() {
            super(314, 314);
            doNewGame();
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

        public void doNewGame() {
            if (gameInProgress) {
                message.setText("Finnish the current game first!");
                return;
            }
            boardData = new int[13][13];
            //boardData[1][1] = BLACK;//testing
            //boardData[1][2] = WHITE;
            currentPlayer = BLACK;
            message.setText("Black: Make your move.");
            gameInProgress = true;
            newGameButton.setDisable(true);
            resignButton.setDisable(false);
            drawBoard();
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
        }

    }


}