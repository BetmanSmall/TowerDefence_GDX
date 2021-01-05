package com.betmansmall;

class GFG {
    static final int C = 3;
    static void zigZagMatrix(int arr[][], int n, int m) {
        int row = 0, col = 0;
        boolean row_inc = false;
        int mn = Math.min(m, n);
        for (int len = 1; len <= mn; ++len) {
            for (int i = 0; i < len; ++i) {
                System.out.print(arr[row][col] + " ");
                if (i + 1 == len) {
                    break;
                }
                if (row_inc) {
                    ++row;
                    --col;
                } else {
                    --row;
                    ++col;
                }
            }
            if (len == mn) {
                break;
            }
            if (row_inc) {
                ++row;
                row_inc = false;
            } else {
                ++col;
                row_inc = true;
            }
        }
        if (row == 0) {
            if (col == m - 1) {
                ++row;
            } else {
                ++col;
            }
            row_inc = true;
        } else {
            if (row == n - 1) {
                ++col;
            } else {
                ++row;
            }
            row_inc = false;
        }
        int MAX = Math.max(m, n) - 1;
        for (int len, diag = MAX; diag > 0; --diag) {
            if (diag > mn) {
                len = mn;
            } else {
                len = diag;
            }
            for (int i = 0; i < len; ++i) {
                System.out.print(arr[row][col] + " ");
                if (i + 1 == len) {
                    break;
                }
                if (row_inc) {
                    ++row;
                    --col;
                } else {
                    ++col;
                    --row;
                }
            }
            if (row == 0 || col == m - 1) {
                if (col == m - 1) {
                    ++row;
                } else {
                    ++col;
                }
                row_inc = true;
            } else if (col == 0 || row == n - 1) {
                if (row == n - 1) {
                    ++col;
                } else {
                    ++row;
                }
                row_inc = false;
            }
        }
    }
    public static void main(String[] args) {
        int matrix[][] = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        zigZagMatrix(matrix, 3, 3);
    }
}
