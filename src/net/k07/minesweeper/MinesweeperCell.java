package net.k07.minesweeper;

public class MinesweeperCell {

    enum State {
        NONE, REVEALED, QUESTIONED, FLAGGED
    }

    public MinesweeperButton button;
    public int row;
    public int column;

    private static final int MINE = -1;
    private static final int UNINITIALIZED = -2;

    private int number;
    private State state;
    public MinesweeperCell() {
        this.number = UNINITIALIZED;
        this.button = null;
        this.state = State.NONE;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setButton(MinesweeperButton b) {
        if(this.button == null) {
            this.button = b;
        }
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
        switch(this.state) {
            case FLAGGED:
                return "!";
            case QUESTIONED:
                return "?";
            default:
                switch(this.number) {
                    case MINE:
                        return "X";
                    case 0:
                        return " ";
                    case -3:
                        return "Flag";
                    default:
                        return this.number + "";
                }
        }
    }
}
