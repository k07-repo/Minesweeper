package net.k07.minesweeper;

public class MinesweeperCell {

    public int row;
    public int column;

    private static final int MINE = -1;
    private static final int UNINITIALIZED = -2;
    public int number;

    public MinesweeperCell() {
        this.number = UNINITIALIZED;
    }

    public void setMine() {
        this.number = MINE;
    }

    public boolean isMine() {
        return this.number == MINE;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        switch(this.number) {
            case MINE:
                return "X";
            case 0:
                return " ";
            default:
                return this.number + "";
        }
    }
}
