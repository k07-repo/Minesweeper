package net.k07.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class Main {

    enum GameState {
        ONGOING, LOST, WON
    }

    public static Grid grid;
    public static MSButton pressedButton;

    public static JFrame rootWindow;
    public static JPanel rootPanel;
    public static JFrame optionSetWindow;

    public static JTextField rowField = new JTextField();
    public static JTextField colField = new JTextField();
    public static JTextField mineField = new JTextField();

    public static GameState gameState = GameState.ONGOING;

    public static boolean firstClick = false;

    public static void main(String[] args) {
        pressedButton = null;
        firstClick = true;

        setupOptionsWindow();
    }

    public static void newGame() {
        grid.createGrid();
        grid.initializeGrid();
        addGridToWindow();

        setState(GameState.ONGOING);
    }

    public static void setState(GameState state) {
        gameState = state;

        if(gameState == GameState.LOST) {
            revealAllMines(false);
            JOptionPane.showMessageDialog(optionSetWindow, "Ba-boom!", "Better luck next time...", JOptionPane.ERROR_MESSAGE);
            newGame();
        }
        else if(gameState == GameState.WON) {
            revealAllMines(true);
            JOptionPane.showMessageDialog(optionSetWindow, "You win!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            newGame();
        }
    }

    public static boolean validateInput(JTextField field, String fieldName, int min, int max) {
        String text = field.getText();
        int value = -1;

        try {
            value = Integer.parseInt(text);
        }
        catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, fieldName + ": numeric values only!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(value < min) {
            JOptionPane.showMessageDialog(null, fieldName + ": value too low! Minimum: " + min, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if(value > max) {
            JOptionPane.showMessageDialog(null, fieldName + ": value too high! Maximum: " + max, "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void setupOptionsWindow() {

        optionSetWindow = new JFrame();
        optionSetWindow.setLayout(new GridLayout(5, 1));

        JPanel sub1 = new JPanel();
        JPanel sub2 = new JPanel();
        JPanel sub3 = new JPanel();

        JButton playButton = new JButton("Play");
        optionSetWindow.add(new JLabel("Set Options"));

        sub1.setLayout(new GridLayout(1, 2));
        sub1.add(new JLabel("Rows: "));
        sub1.add(rowField);
        optionSetWindow.add(sub1);

        sub2.setLayout(new GridLayout(1, 2));
        sub2.add(new JLabel("Columns: "));
        sub2.add(colField);
        optionSetWindow.add(sub2);

        sub3.setLayout(new GridLayout(1, 2));
        sub3.add(new JLabel("Mines: "));
        sub3.add(mineField);
        optionSetWindow.add(sub3);

        playButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!validateInput(rowField, "Rows", 5, 20)) {
                    return;
                } else if (!validateInput(colField, "Rows", 5, 30)) {
                    return;
                }

                int rows = Integer.parseInt(rowField.getText());
                int cols =  Integer.parseInt(colField.getText());

                if (!validateInput(mineField, "Mines", 1, (rows * cols) - 1)) {
                    return;
                }

                optionSetWindow.dispatchEvent(new WindowEvent(optionSetWindow, WindowEvent.WINDOW_CLOSING));
                setupWindow();
            }
        });

        optionSetWindow.add(playButton);
        optionSetWindow.setSize(250, 250);
        optionSetWindow.setResizable(false);
        optionSetWindow.setLocationRelativeTo(null);
        optionSetWindow.setVisible(true);
    }

    public static void addGridToWindow() {
        rootPanel.removeAll();

        int rows = Integer.parseInt(rowField.getText());
        int cols =  Integer.parseInt(colField.getText());
        int mines =  Integer.parseInt(mineField.getText());
        grid = new Grid(mines, rows, cols);
        grid.createGrid();
        grid.initializeGrid();
        rootPanel.setLayout(new GridLayout(rows, cols));

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                MSButton button = new MSButton("", row, col);
                Cell cell = grid.getCellAt(row, col);
                cell.setButton(button);
                button.setCell(cell);

                button.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {

                    }
                    public void mousePressed(MouseEvent e) {
                        pressedButton = button;
                    }
                    public void mouseReleased(MouseEvent e) {

                        if(gameState == GameState.ONGOING) {
                            if (button == pressedButton) {
                                if (SwingUtilities.isLeftMouseButton(e) && SwingUtilities.isRightMouseButton(e)) {
                                    if (!button.isEnabled() && button.cell.getState() == Cell.State.REVEALED) {
                                        revealAllAdjacentWithFlagCheck(button);
                                    }
                                } else if (button.isEnabled()) {
                                    if (SwingUtilities.isLeftMouseButton(e)) {
                                        if (!(button.cell.getState() == Cell.State.FLAGGED)) {
                                            reveal(button);
                                        }
                                    } else if (SwingUtilities.isRightMouseButton(e)) {
                                        if (!(button.cell.getState() == Cell.State.REVEALED)) {
                                            rotateFlagState(button);
                                        }
                                    }
                                }
                            }
                        }

                        pressedButton = null;
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                        pressedButton = null;
                    }


                });
                rootPanel.add(button);
            }
        }

        //Redraw and refresh the grid after the new one is created
        rootPanel.repaint();
        rootPanel.revalidate();
    }

    public static void setupWindow() {
        rootWindow = new JFrame("Minesweeper");
        rootWindow.setSize(500, 600);
        rootPanel = new JPanel();
        rootWindow.add(rootPanel);

        addGridToWindow();

        rootWindow.setVisible(true);
    }

    public static void reveal(MSButton button) {
        Cell cell = button.cell;
        Cell.State state = cell.getState();
        if(state == Cell.State.NONE || state == Cell.State.QUESTIONED) {
            button.setText(cell.toString());
            button.setEnabled(false);
            cell.setState(Cell.State.REVEALED);
            if (cell.getNumber() == 0) {
                button.setBackground(new Color(40, 40, 40));
                revealAllAdjacent(button);
            }
            else if(cell.isMine()) {
                if(gameState == GameState.ONGOING) {
                    setState(GameState.LOST);
                }
            }

            if(firstClick) {
                firstClick = false;
            }
            checkForWin();
        }
    }

    public static void revealAllAdjacent(MSButton button) {
        ArrayList<Cell> adjacentCells = grid.getAdjacentCells(button.row, button.col);
        System.out.println(adjacentCells);
        for (Cell c : adjacentCells) {
            reveal(c.button);
        }
    }

    public static void revealAllAdjacentWithFlagCheck(MSButton button) {
        ArrayList<Cell> adjacentCells = grid.getAdjacentCells(button.row, button.col);
        System.out.println(adjacentCells);
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

    public static void rotateFlagState(MSButton button) {
        Cell cell = button.cell;
        if(cell.getState() == Cell.State.REVEALED) {
            return;
        }
        else if(cell.getState() == Cell.State.FLAGGED) {
            cell.setState(Cell.State.QUESTIONED);
            button.setText("?");
        }
        else if(cell.getState() == Cell.State.QUESTIONED){
            cell.setState(Cell.State.NONE);
            button.setText("");
        }
        else {
            cell.setState(Cell.State.FLAGGED);
            button.setText("!");
        }
    }

    public static void revealAllMines(boolean flagMines) {
        for(Cell c: grid.getAllCells()) {
            Cell.State state = c.getState();
            if(c.isMine() && state != Cell.State.FLAGGED && state != Cell.State.REVEALED) {
                if(flagMines) {
                    c.setState(Cell.State.FLAGGED);
                }
                c.button.setText(c.toString());
            }
        }
    }

    public static void checkForWin() {
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

}
