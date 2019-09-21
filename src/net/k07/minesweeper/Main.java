package net.k07.minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Main {
    public static MinesweeperGrid grid;
    public static void main(String[] args) {
        grid = new MinesweeperGrid(40, 20, 30);

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

                        if(SwingUtilities.isLeftMouseButton(e) && SwingUtilities.isRightMouseButton(e)) {
                            if(!button.isEnabled() && button.cell.isRevealed) {
                                revealAllAdjacent(button);
                            }
                        }
                        if(SwingUtilities.isLeftMouseButton(e)) {
                            if(!button.cell.isFlagged) {
                                reveal(button);
                            }
                        }
                        else if(SwingUtilities.isRightMouseButton(e)) {
                            if(!button.cell.isRevealed) {
                                flag(button);
                            }
                        }

                    }
                    public void mousePressed(MouseEvent e) {
                    }
                    public void mouseReleased(MouseEvent e) {
                    }
                    public void mouseEntered(MouseEvent e) {
                    }
                    public void mouseExited(MouseEvent e) {
                    }


                });
                rootPanel.add(button);
            }
        }

        rootWindow.setVisible(true);
    }

    public static void reveal(MinesweeperButton button) {
        MinesweeperCell cell = button.cell;
        if(!cell.isRevealed && !cell.isFlagged) {
            button.setText(cell.toString());
            button.setEnabled(false);
            cell.isRevealed = true;
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

    public static void flag(MinesweeperButton button) {
        MinesweeperCell cell = button.cell;
        if(cell.isFlagged) {
            cell.isFlagged = false;
            button.setText(" ");
        }
        else if(cell.isRevealed == false){
            cell.isFlagged = true;
            button.setText("!");
        }
    }
}
