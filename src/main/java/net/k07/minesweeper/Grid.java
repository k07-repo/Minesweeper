package net.k07.minesweeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Class to handle the grid of cells within the game
 */
public class Grid {
    private int numberOfMines;
    private int rows;
    private int cols;
    private Random random;

    public Cell[][] grid;

    public Grid(int mines, int rows, int cols)
    {
        this.numberOfMines = mines;
        this.rows = rows;
        this.cols = cols;
        this.random = new Random();

        grid = new Cell[rows][cols];
    }

    public Cell getFirstSafeCell() {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                Cell cell = this.getCellAt(row, col);
                System.out.println("pass");
                if(!(cell.isMine())) {
                    return cell;
                }
            }
        }

        //should never be reached
        return null;
    }

    public void createGrid() {

        ArrayList<Cell> mineArray = new ArrayList<Cell>();

        for(int k = 0; k < rows * cols; k++) {
            Cell newCell = new Cell();
            if(k < numberOfMines) {
                newCell.setMine();
            }
            mineArray.add(k, newCell);
        }

        Collections.shuffle(mineArray);

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                Cell cell = mineArray.get(col % cols + row * cols);
                cell.row = row;
                cell.column = col;
                grid[row][col] = cell;
            }
        }
    }

    public void initializeGrid() {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                initializeCell(row, col);
            }
        }
    }

    public void initializeCell(int row, int col) {
        Cell current = getCellAt(row, col);
        if(!current.isMine()) {
            current.setNumber(getNumberOfAdjacentMines(row, col));
        }
    }

    public void setCellNumber(Cell cell) {
        if(!cell.isMine()) {
            cell.setNumber(getNumberOfAdjacentMines(cell.row, cell.column));
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

    public Cell getRandomSafeCell() {

         while (true) {
            Cell result = this.getCellAt(random.nextInt(rows), random.nextInt(cols));
            if (!result.isMine()) {
                return result;
            }
        }
    }

    public Cell getCellAt(int row, int col) {
        return grid[row][col];
    }

    public int getNumberOfAdjacentMines(int row, int col) {
        int result = 0;
        ArrayList<Cell> adjacent = getAdjacentCells(row, col);
        for(Cell cell: adjacent) {
            if(cell.isMine()) {
                result++;
            }
        }

        return result;
    }

    public ArrayList<Cell> getAdjacentCells(int r, int c) {
        ArrayList<Cell> result = new ArrayList<>();

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

    public ArrayList<Cell> getAllCells() {
        ArrayList<Cell> result = new ArrayList<>();
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
