package net.k07.minesweeper;

import javax.swing.*;
import java.awt.*;

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

    public static Color getColorForNumber(int number) {
        switch(number) {
            case 1:
                return Color.BLUE;
            case 2:
                return Color.GREEN;
            case 3:
                return Color.RED;
            case 4:
                return Color.BLUE.darker();
            case 5:
                return Color.RED.darker();
            case 6:
                return Color.CYAN;
            case 7:
                return Color.MAGENTA;
            case 8:
                return Color.BLACK;
            default:
                return Color.BLACK;
        }
    }
}
