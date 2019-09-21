package net.k07.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public class Main {
    public static MinesweeperGrid grid;
    public static MinesweeperButton pressedButton;
    public static void main(String[] args) {
        grid = new MinesweeperGrid(80, 20, 30);
        pressedButton = null;

        JFrame rootWindow = new JFrame();
        JPanel rootPanel = new JPanel();
        rootWindow.add(rootPanel);
        rootPanel.setLayout(new GridLayout(20 , 30));

        for(int row = 0; row < 20; row++) {
            for(int col = 0; col < 30; col++) {
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
}
