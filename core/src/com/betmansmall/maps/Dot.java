package com.betmansmall.maps;

import com.badlogic.gdx.math.GridPoint2;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

class Dot {
    public int minRow, minCol, maxRow, maxCol;
    public int row, col;
    public List<GridPoint2> visitations;

    public Dot(int minRow, int minCol, int maxRow, int maxCol, int row, int col) {
        this.minRow = minRow;
        this.minCol = minCol;
        this.maxRow = maxRow;
        this.maxCol = maxCol;
        this.row = row;
        this.col = col;
        this.visitations = new ArrayList<>();
    }

    public boolean TryMoveRight() {
        boolean test = ValidTest(row, col + 1);
        if (test) {
            Bookmark();
            col++;
        }
        return test;
    }

    public boolean TryMoveDown() {
        boolean test = ValidTest(row + 1, col);
        if (test) {
            Bookmark();
            row++;
        }
        return test;
    }

    public boolean TryMoveLeft() {
        boolean test = ValidTest(row, col - 1);
        if (test) {
            Bookmark();
            col--;
        }
        return test;
    }

    public boolean TryMoveUp() {
        boolean test = ValidTest(row - 1, col);
        if (test) {
            Bookmark();
            row--;
        }
        return test;
    }

    void Bookmark() {
        visitations.add(new GridPoint2(row, col));
    }

    boolean ValidTest(int row, int col) {
        return BorderTest(row, col) && VisitationTest(row, col);
    }

    boolean BorderTest(int row, int col) {
        if (row < minRow) return false;
        if (row > maxRow) return false;
        if (col < minCol) return false;
        if (col > maxCol) return false;
        return true;
    }

    boolean VisitationTest(int row, int col) {
        return !visitations.contains(new GridPoint2(row, col));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("minRow", minRow)
                .add("minCol", minCol)
                .add("maxRow", maxRow)
                .add("maxCol", maxCol)
                .add("row", row)
                .add("col", col)
//                .add("visitations", visitations)
                .toString();
    }
}
