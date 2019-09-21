package net.k07.minesweeper;

import javax.swing.*;

public class MinesweeperButton extends JButton {
    public final int row;
    public final int col;
    public MinesweeperCell cell;

    public MinesweeperButton(String text, int row, int col) {
        super(text);
        this.row = row;
        this.col = col;
        this.cell = null;
    }

    public void setCell(MinesweeperCell cell) {
        if(this.cell == null) {
            this.cell = cell;
        }
    }
}
