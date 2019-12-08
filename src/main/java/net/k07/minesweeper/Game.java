package net.k07.minesweeper;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
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


    private static MSWindow window;

    public static Timer timer = new Timer(1000, e -> {
            timePassed++;
            window.timeLabel.setText("Time passed: " + timePassed);

            if(MSWindow.soundEnabled) {
                window.tickPlayer.start();
            }
    });
    
    public Game(MSWindow window) {
        this.window = window;
    }

    public void newGame() {
        timer.stop();
        window.pressedButton = null;
        grid.createGrid();
        grid.initializeGrid();
        window.addGridToWindow();
        setMinesLeft(mineCount);
        timePassed = 0;
        firstClick = true;
        window.lifelines = window.options.lifelines;
        window.updateToolbar();
        setState(GameState.ONGOING);
    }

    public void setState(GameState state) {
        gameState = state;

        if(gameState == GameState.LOST) {
            timer.stop();
            revealAllMines(false);

            if(MSWindow.soundEnabled) {
                window.explosionPlayer.start();
            }
        }
        else if(gameState == GameState.WON) {
            timer.stop();
            revealAllMines(true);
            JOptionPane.showMessageDialog(window.rootWindow, "You win!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void reveal(MSButton button) {
        Cell cell = button.cell;
        Cell.State state = cell.getState();
        if(state == Cell.State.NONE || state == Cell.State.QUESTIONED) {
            cell.setState(Cell.State.NONE);
            button.reveal();
            if(MSWindow.colorEnabled) {
                button.setUI(new MetalButtonUI() {
                    protected Color getDisabledTextColor() {
                        return MSButton.getColorForNumber(cell.getNumber());
                    }
                });
            }
            button.setEnabled(false);
            button.setForeground(MSButton.getColorForNumber(cell.getNumber()));
            cell.setState(Cell.State.REVEALED);

            if(cell.isMine()) {
                if(firstClick) {
                    Cell safe = grid.getFirstSafeCell();
                    safe.setMine();
                    cell.setNumber(grid.getNumberOfAdjacentMines(cell.row, cell.column));
                    cell.button.reveal();
                    cell.button.unflag();

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
                    if(window.lifelines > 0) {
                        button.setEnabled(true);

                        window.lifelines--;
                        window.updateToolbar();
                        decrementMinesLeft();
                        button.setFlagMineIcon();
                    }
                    else {
                        button.setBackground(Color.RED);
                        setState(GameState.LOST);
                    }
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
            if(c.getState() == Cell.State.FLAGGED || c.isLifelinedMine()) {
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
            button.unflag();
            button.setText("?");
            incrementMinesLeft();
        }
        else if(cell.getState() == Cell.State.QUESTIONED){
            cell.setState(Cell.State.NONE);
            button.setText("");
        }
        else {
            cell.setState(Cell.State.FLAGGED);
            button.setFlagIcon();
            decrementMinesLeft();
        }
    }

    public void revealAllMines(boolean flagMines) {
        for(Cell c: grid.getAllCells()) {
            Cell.State state = c.getState();
            if(c.isMine() && state != Cell.State.FLAGGED && state != Cell.State.REVEALED) {
                if(flagMines) {
                    c.setState(Cell.State.FLAGGED);
                    c.button.setFlagIcon();
                }
                else {
                    c.button.reveal();
                }
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
        window.updateToolbar();
    }
}
