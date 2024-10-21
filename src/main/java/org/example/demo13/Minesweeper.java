package org.example.demo13;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Random;

public class Minesweeper extends Application {

    private static final int SIZE = 12; // Dimensione del tabellone
    private static final int MINES = 14; // Numero di mine
    private Cell[][] board = new Cell[SIZE][SIZE]; // Tabellone di celle
    private boolean gameOver = false; // Stato del gioco

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane(); // Creazione della griglia

        // Inizializzazione del tabellone
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Cell cell = new Cell(row, col);
                board[row][col] = cell;
                grid.add(cell.button, col, row);
            }
        }

        // Posizionamento casuale delle mine
        placeMines();

        // Configurazione della finestra
        Scene scene = new Scene(grid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Prato Fiorito");
        primaryStage.show();
    }

    // Metodo per posizionare le mine casualmente sul tabellone
    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;
        while (minesPlaced < MINES) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);
            if (!board[row][col].hasMine) {
                board[row][col].hasMine = true;
                minesPlaced++;
            }
        }

        // Calcolo dei numeri delle celle vicine alle mine
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col].setSurroundingMines();
            }
        }
    }

    // Classe per rappresentare ogni cella del gioco
    class Cell {
        int row, col;
        boolean hasMine = false;
        boolean revealed = false;
        Button button;
        int surroundingMines = 0;



        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            button = new Button();
            button.setMinSize(40, 40);

            // Gestione del click sinistro e destro
            button.setOnMouseClicked(event -> {
                if (gameOver) return; // Se il gioco è finito, ignora l'input
                if (event.getButton() == MouseButton.PRIMARY) {
                    reveal();
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    markAsFlag();
                }
            });
        }

        // Metodo per rivelare la cella
        public void reveal() {
            if (revealed || button.getText().equals("F")) return; // Se è già rivelata o marcata, non fare nulla
            revealed = true;

            if (hasMine) {
                button.setText("X"); // Se c'è una mina, mostra X
                button.setStyle("-fx-background-color: red");
                gameOver();
            } else {
                button.setText(surroundingMines == 0 ? "" : String.valueOf(surroundingMines));
                button.setDisable(true);

                // Se non ci sono mine nelle vicinanze, riveliamo le celle vicine
                if (surroundingMines == 0) {
                    revealAdjacentCells();
                }
            }
        }

        // Metodo per marcare una cella come bandiera
        private void markAsFlag() {
            if (button.getText().equals("F")) {
                button.setText("");
            } else {
                button.setText("F");
            }
        }

        // Metodo per rivelare le celle adiacenti
        private void revealAdjacentCells() {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newRow = row + i;
                    int newCol = col + j;
                    if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE) {
                        board[newRow][newCol].reveal();
                    }
                }
            }
        }

        // Calcola il numero di mine nelle celle adiacenti
        public void setSurroundingMines() {
            if (hasMine) return;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newRow = row + i;
                    int newCol = col + j;
                    if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE && board[newRow][newCol].hasMine) {
                        surroundingMines++;
                    }
                }
            }
        }
    }

    // Metodo per terminare il gioco quando si trova una mina
    private void gameOver() {
        gameOver = true;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Hai perso! Game Over!");
        alert.showAndWait();

        // Rivela tutte le mine
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col].hasMine) {
                    board[row][col].button.setText("X");
                    board[row][col].button.setStyle("-fx-background-color: red");
                }
            }
        }
    }
}
