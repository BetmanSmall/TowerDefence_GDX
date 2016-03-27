package com.betmansmall.game.gameLogic;

/**
 * Created by Андрей on 10.03.2016.
 */
public class CellManager {

    private int width;
    private int height;

    private Cell[][] cells;

    CellManager(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
    }


    public int getWidth () {
        return width;
    }

    public int getHeight () {
        return height;
    }

    public Cell getCell (int x, int y) {
        if (x < 0 || x >= width) return null;
        if (y < 0 || y >= height) return null;
        return cells[x][y];
    }

    public void setCell (int x, int y, Cell cell) {
        if (x < 0 || x >= width) return;
        if (y < 0 || y >= height) return;
        cells[x][y] = cell;
    }
}
