package net.k07.minesweeper;

import javax.swing.*;

public class MSButton extends JButton {
    public final int row;
    public final int col;
    public Cell cell;

    public MSButton(String text, int row, int col) {
        super(text);
        this.row = row;
        this.col = col;
        this.cell = null;
    }

    public void setCell(Cell cell) {
        if(this.cell == null) {
            this.cell = cell;
        }
    }
}
