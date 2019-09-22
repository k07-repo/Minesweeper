package net.k07.minesweeper;

import java.util.ArrayList;
import java.util.Collections;

public class MinesweeperGrid {
    private int numberOfMines;
    private int rows;
    private int cols;

    public MinesweeperCell[][] grid;

    public MinesweeperGrid(int mines, int rows, int cols)
    {
        this.numberOfMines = mines;
        this.rows = rows;
        this.cols = cols;

        grid = new MinesweeperCell[rows][cols];

        createGrid();
        initializeGrid();
        printGrid();
    }


    public void createGrid() {

        ArrayList<MinesweeperCell> mineArray = new ArrayList<MinesweeperCell>();

        for(int k = 0; k < rows * cols; k++) {
            MinesweeperCell newCell = new MinesweeperCell();
            if(k < numberOfMines) {
                newCell.setMine();
            }
            mineArray.add(k, newCell);
        }

        Collections.shuffle(mineArray);

        for(int k = 0; k < mineArray.size(); k++) {
            grid[k / cols][k % cols] = mineArray.get(k);
        }
    }

    public void initializeGrid() {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                MinesweeperCell current = getCellAt(row, col);
                if(!current.isMine()) {
                    current.setNumber(getNumberOfAdjacentMines(row, col));
                }
            }
        }
    }

    public void printGrid() {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                System.out.print(getCellAt(row, col).toString() + " ");
            }
            System.out.println();
        }
    }

    public MinesweeperCell getCellAt(int row, int col) {
        return grid[row][col];
    }

    public int getNumberOfAdjacentMines(int row, int col) {
        int result = 0;
        ArrayList<MinesweeperCell> adjacent = getAdjacentCells(row, col);
        for(MinesweeperCell cell: adjacent) {
            if(cell.isMine()) {
                result++;
            }
        }

        return result;
    }

    public ArrayList<MinesweeperCell> getAdjacentCells(int r, int c) {
        ArrayList<MinesweeperCell> result = new ArrayList<>();

        for(int row = -1; row < 2; row++) {
            for(int col = -1; col < 2; col++) {
                if(!(row == 0 && col == 0)) {
                    if(isValidCellLocation(r + row, c + col)) {
                        result.add(getCellAt(r + row, c + col));
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<MinesweeperCell> getAllCells() {
        ArrayList<MinesweeperCell> result = new ArrayList<>();
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                result.add(grid[row][col]);
            }
        }

        return result;
    }

    public boolean isValidCellLocation(int row, int col) {
        if((row >= 0 && row < rows) && (col >= 0 && col < cols)) {
            return true;
        }
        return false;
    }

}
