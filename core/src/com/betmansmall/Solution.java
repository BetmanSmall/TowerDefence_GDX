package com.betmansmall;

import java.lang.*;

class Solution {
    private static String zigZagConcat(String s, int n) {
        if (n <= 1) {
            return s;
        }
        StringBuilder result = new StringBuilder();
        for (int rowNum = 0; rowNum < n; rowNum++) {
            int i = rowNum;
            boolean up = true;
            while (i < s.length()) {
                result = result.append(s.charAt(i));
                if (rowNum == 0 || rowNum == n - 1) {
                    i += (2 * n - 2);
                } else {
                    if (up) {
                        i += (2 * (n - rowNum) - 2);
                    } else {
                        i += rowNum * 2;
                    }
                    up ^= true;
                }
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String str = "GEEKSFORGEEKS";
        int n = 3;
        System.out.println(zigZagConcat(str, n));
    }
}
