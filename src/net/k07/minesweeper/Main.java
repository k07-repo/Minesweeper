package net.k07.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class Main {

    enum GameState {
        ONGOING, LOST, WON
    }

    public static MinesweeperGrid grid;
    public static MinesweeperButton pressedButton;

    public static JFrame rootWindow;
    public static JPanel rootPanel;
    public static JFrame optionSetWindow;

    public static JTextField rowField = new JTextField();
    public static JTextField colField = new JTextField();
    public static JTextField mineField = new JTextField();

    public static GameState gameState = GameState.ONGOING;

    public static void setState(GameState state) {
        gameState = state;

        if(gameState == GameState.LOST) {
            revealAllMines(false);
            JOptionPane.showMessageDialog(optionSetWindow, "Ba-boom!", "Better luck next time...", JOptionPane.ERROR_MESSAGE);
        }
        else if(gameState == GameState.WON) {
            revealAllMines(true);
            JOptionPane.showMessageDialog(optionSetWindow, "You win!", "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
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

    public static void main(String[] args) {
        grid = new MinesweeperGrid(50, 20, 30);
        pressedButton = null;

        boolean playHit = false;

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
        optionSetWindow.setVisible(true);


    }

    public static void setupWindow() {
        rootWindow = new JFrame("Minesweeper");
        rootWindow.setSize(500, 600);
        rootPanel = new JPanel();
        rootWindow.add(rootPanel);


        int rows = Integer.parseInt(rowField.getText());
        int cols =  Integer.parseInt(colField.getText());
        int mines =  Integer.parseInt(mineField.getText());
        grid = new MinesweeperGrid(mines, rows, cols);
        rootPanel.setLayout(new GridLayout(rows, cols));

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                MinesweeperButton button = new MinesweeperButton("", row, col);
                MinesweeperCell cell = grid.getCellAt(row, col);
                cell.setButton(button);
                button.setCell(cell);

                button.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {

                    }
                    public void mousePressed(MouseEvent e) {
                        pressedButton = button;
                    }
                    public void mouseReleased(MouseEvent e) {
                        if(button == pressedButton) {
                            if (SwingUtilities.isLeftMouseButton(e) && SwingUtilities.isRightMouseButton(e)) {
                                if (!button.isEnabled() && button.cell.getState() == MinesweeperCell.State.REVEALED) {
                                    revealAllAdjacentWithFlagCheck(button);
                                }
                            }
                            else if(button.isEnabled()) {
                                if (SwingUtilities.isLeftMouseButton(e)) {
                                    if (!(button.cell.getState() == MinesweeperCell.State.FLAGGED)) {
                                        reveal(button);
                                    }
                                } else if (SwingUtilities.isRightMouseButton(e)) {
                                    if (!(button.cell.getState() == MinesweeperCell.State.REVEALED)) {
                                        rotateFlagState(button);
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
        rootWindow.setVisible(true);
    }
    public static void reveal(MinesweeperButton button) {
        MinesweeperCell cell = button.cell;
        MinesweeperCell.State state = cell.getState();
        if(state == MinesweeperCell.State.NONE || state == MinesweeperCell.State.QUESTIONED) {
            button.setText(cell.toString());
            button.setEnabled(false);
            cell.setState(MinesweeperCell.State.REVEALED);
            if (cell.getNumber() == 0) {
                button.setBackground(new Color(40, 40, 40));
                revealAllAdjacent(button);
            }
            else if(cell.isMine()) {
                if(gameState == GameState.ONGOING) {
                    setState(GameState.LOST);
                }
            }

            checkForWin();
        }
    }

    public static void revealAllAdjacent(MinesweeperButton button) {
        ArrayList<MinesweeperCell> adjacentCells = grid.getAdjacentCells(button.row, button.col);
        System.out.println(adjacentCells);
        for (MinesweeperCell c : adjacentCells) {
            reveal(c.button);
        }
    }

    public static void revealAllAdjacentWithFlagCheck(MinesweeperButton button) {
        ArrayList<MinesweeperCell> adjacentCells = grid.getAdjacentCells(button.row, button.col);
        System.out.println(adjacentCells);
        int flagCount = 0;
        for (MinesweeperCell c : adjacentCells) {
            if(c.getState() == MinesweeperCell.State.FLAGGED) {
                flagCount++;
            }
        }

        if(flagCount == button.cell.getNumber()) {
            for (MinesweeperCell c : adjacentCells) {
                reveal(c.button);
            }
        }
    }

    public static void rotateFlagState(MinesweeperButton button) {
        MinesweeperCell cell = button.cell;
        if(cell.getState() == MinesweeperCell.State.REVEALED) {
            return;
        }
        else if(cell.getState() == MinesweeperCell.State.FLAGGED) {
            cell.setState(MinesweeperCell.State.QUESTIONED);
            button.setText("?");
        }
        else if(cell.getState() == MinesweeperCell.State.QUESTIONED){
            cell.setState(MinesweeperCell.State.NONE);
            button.setText("");
        }
        else {
            cell.setState(MinesweeperCell.State.FLAGGED);
            button.setText("!");
        }
    }

    public static void revealAllMines(boolean flagMines) {
        for(MinesweeperCell c: grid.getAllCells()) {
            MinesweeperCell.State state = c.getState();
            if(c.isMine() && state != MinesweeperCell.State.FLAGGED && state != MinesweeperCell.State.REVEALED) {
                if(flagMines) {
                    c.setState(MinesweeperCell.State.FLAGGED);
                }
                c.button.setText(c.toString());
            }
        }
    }

    public static void checkForWin() {
        if (!(gameState == GameState.ONGOING)) {
            return;
        }

        for (MinesweeperCell c : grid.getAllCells()) {
            MinesweeperCell.State state = c.getState();
            if (state != MinesweeperCell.State.REVEALED && !c.isMine())
                return;
        }

        setState(GameState.WON);

    }

}
