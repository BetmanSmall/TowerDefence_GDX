package com.betmansmall.game.desktop;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class TestMain {
    public static void main(String[] args) {
//        int width = 0;
//        int height = 0;
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice[] gs = ge.getScreenDevices();
//        for (GraphicsDevice curGs : gs) {
//            DisplayMode mode = curGs.getDisplayMode();
//            System.out.println("mode:" + mode);
//            width += mode.getWidth();
//            height = mode.getHeight();
//            System.out.println("width:" + width);
//            System.out.println("height:" + height);
//        }
        System.out.println(getMonitorSizes());
    }

    private static String getMonitorSizes() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < gs.length; i++) {
            DisplayMode dm = gs[i].getDisplayMode();
            sb.append(i + ", width: " + dm.getWidth() + ", height: " + dm.getHeight() + "\n");
        }
        return sb.toString();
    }
}
