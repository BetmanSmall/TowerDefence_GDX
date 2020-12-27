package com.betmansmall;

/* Java Program to print matrix in Zig-zag pattern*/
class GFG {
    static final int C = 3;

    // Utility function to print matrix
    // in zig-zag form
    static void zigZagMatrix(int arr[][], int n, int m) {
        int row = 0, col = 0;

        // Boolean variable that will true if we
        // need to increment 'row' value otherwise
        // false- if increment 'col' value
        boolean row_inc = false;

        // Print matrix of lower half zig-zag pattern
        int mn = Math.min(m, n);
        for (int len = 1; len <= mn; ++len) {
            for (int i = 0; i < len; ++i) {
                System.out.print(arr[row][col] + " ");

                if (i + 1 == len)
                    break;
                // If row_increment value is true
                // increment row and decrement col
                // else decrement row and increment
                // col
                if (row_inc) {
                    ++row;
                    --col;
                } else {
                    --row;
                    ++col;
                }
            }

            if (len == mn)
                break;

            // Update row or col valaue according
            // to the last increment
            if (row_inc) {
                ++row;
                row_inc = false;
            } else {
                ++col;
                row_inc = true;
            }
        }

        // Update the indexes of row and col variable
        if (row == 0) {
            if (col == m - 1)
                ++row;
            else
                ++col;
            row_inc = true;
        } else {
            if (row == n - 1)
                ++col;
            else
                ++row;
            row_inc = false;
        }

        // Print the next half zig-zag pattern
        int MAX = Math.max(m, n) - 1;
        for (int len, diag = MAX; diag > 0; --diag) {

            if (diag > mn)
                len = mn;
            else
                len = diag;

            for (int i = 0; i < len; ++i) {
                System.out.print(arr[row][col] + " ");

                if (i + 1 == len)
                    break;

                // Update row or col value according
                // to the last increment
                if (row_inc) {
                    ++row;
                    --col;
                } else {
                    ++col;
                    --row;
                }
            }

            // Update the indexes of row and col variable
            if (row == 0 || col == m - 1) {
                if (col == m - 1)
                    ++row;
                else
                    ++col;

                row_inc = true;
            } else if (col == 0 || row == n - 1) {
                if (row == n - 1)
                    ++col;
                else
                    ++row;

                row_inc = false;
            }
        }
    }

    // Driver code
    public static void main(String[] args) {
        int matrix[][] = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        zigZagMatrix(matrix, 3, 3);
    }
}
// This code is contributed by Anant Agarwal.