package net.k07.minesweeper;

import javax.imageio.ImageIO;
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
        //this.setBackground(new Color(200, 200, 200));

        Font font = this.getFont();
        this.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
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
                return Color.GREEN.darker();
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

    public void reveal() {
        if (this.cell.isMine()) {
            this.setMineIcon();
        } else {
            this.setText(this.cell.toString());
        }
    }

    private void setMineIcon() {
        this.setIconToFileAt("/mine.png");
    }

    public void setFlagIcon() {
        this.setIconToFileAt("/flag.png");
    }

    public void setFlagMineIcon() { this.setIconToFileAt("/flagmine.png"); }

    public void unflag() {
        this.setIcon(null);
    }

    private void setIconToFileAt(String path) {
        this.setIcon(new ImageIcon(getClass().getResource(path)));
    }
}
