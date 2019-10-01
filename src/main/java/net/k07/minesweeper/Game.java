package net.k07.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Game {

    enum GameState {
        ONGOING, LOST, WON
    }

    public static GameState gameState = GameState.ONGOING;
    public static Grid grid;
    public static int mineCount = -1;
    public static int timePassed = 0;
    public static boolean firstClick = true;

    public static Timer timer = new Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            timePassed++;
            Main.timeLabel.setText("Time passed: " + timePassed);
        }
    });

    public void newGame() {
        timer.stop();
        Main.pressedButton = null;
        grid.createGrid();
        grid.initializeGrid();
        Main.addGridToWindow();
        setMinesLeft(mineCount);
        timePassed = 0;
        firstClick = true;
        Main.updateToolbar();
        setState(GameState.ONGOING);
    }

    public void setState(GameState state) {
        gameState = state;

        if(gameState == GameState.LOST) {
            timer.stop();
            revealAllMines(false);
            JOptionPane.showMessageDialog(Main.rootWindow, "Ba-boom!", "Better luck next time...", JOptionPane.ERROR_MESSAGE);
        }
        else if(gameState == GameState.WON) {
            timer.stop();
            revealAllMines(true);
            JOptionPane.showMessageDialog(Main.rootWindow, "You win!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void reveal(MSButton button) {
        Cell cell = button.cell;
        Cell.State state = cell.getState();
        if(state == Cell.State.NONE || state == Cell.State.QUESTIONED) {
            cell.setState(Cell.State.NONE);
            button.setText(cell.toString());
            button.setEnabled(false);
            cell.setState(Cell.State.REVEALED);

            if(cell.isMine()) {
                if(firstClick) {
                    Cell safe = grid.getFirstSafeCell();
                    safe.setMine();
                    cell.setNumber(grid.getNumberOfAdjacentMines(cell.row, cell.column));
                    cell.button.setText(cell.toString());

                    ArrayList<Cell> reevaluate = grid.getAdjacentCells(cell.row, cell.column);
                    for(Cell c: reevaluate) {
                        grid.initializeCell(c.row, c.column);
                    }

                    reevaluate = grid.getAdjacentCells(safe.row, safe.column);
                    for(Cell c: reevaluate) {
                        grid.initializeCell(c.row, c.column);
                    }
                }
                else if(gameState == GameState.ONGOING) {
                    setState(GameState.LOST);
                }
            }

            if (cell.getNumber() == 0) {
                button.setBackground(new Color(40, 40, 40));
                revealAllAdjacent(button);
            }

            if(firstClick) {
                timer.start();
                firstClick = false;
            }


            checkForWin();
        }
    }

    public void revealAllAdjacent(MSButton button) {
        ArrayList<Cell> adjacentCells = grid.getAdjacentCells(button.row, button.col);
        for (Cell c : adjacentCells) {
            reveal(c.button);
        }
    }

    public void revealAllAdjacentWithFlagCheck(MSButton button) {
        ArrayList<Cell> adjacentCells = grid.getAdjacentCells(button.row, button.col);
        int flagCount = 0;
        for (Cell c : adjacentCells) {
            if(c.getState() == Cell.State.FLAGGED) {
                flagCount++;
            }
        }

        if(flagCount == button.cell.getNumber()) {
            for (Cell c : adjacentCells) {
                reveal(c.button);
            }
        }
    }

    public void rotateFlagState(MSButton button) {
        Cell cell = button.cell;
        if(cell.getState() == Cell.State.REVEALED) {
            return;
        }
        else if(cell.getState() == Cell.State.FLAGGED) {
            cell.setState(Cell.State.QUESTIONED);
            button.setText("?");
            incrementMinesLeft();
        }
        else if(cell.getState() == Cell.State.QUESTIONED){
            cell.setState(Cell.State.NONE);
            button.setText("");
        }
        else {
            cell.setState(Cell.State.FLAGGED);
            button.setText("!");
            decrementMinesLeft();
        }
    }

    public void revealAllMines(boolean flagMines) {
        for(Cell c: grid.getAllCells()) {
            Cell.State state = c.getState();
            if(c.isMine() && state != Cell.State.FLAGGED && state != Cell.State.REVEALED) {
                if(flagMines) {
                    c.setState(Cell.State.FLAGGED);
                }
                c.button.setText(c.toString());
            }
        }
        if(flagMines) {
            setMinesLeft(0);
        }
    }

    public void checkForWin() {
        if (!(gameState == GameState.ONGOING)) {
            return;
        }

        for (Cell c : grid.getAllCells()) {
            Cell.State state = c.getState();
            if (state != Cell.State.REVEALED && !c.isMine())
                return;
        }

        setState(GameState.WON);
    }

    public static void decrementMinesLeft() {
        mineCount--;
        setMinesLeft(mineCount);
    }

    public static void incrementMinesLeft() {
        mineCount++;
        setMinesLeft(mineCount);
    }

    public static void setMinesLeft(int mines) {
        mineCount = mines;
        Main.updateToolbar();
    }
}
