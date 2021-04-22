package com.betmansmall.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class DesktopWindow {
    public static LwjglApplicationConfiguration getConfiguration(String[] args) {
        Options options = new Options()
                .addOption("help", false, "prints this message")
                .addOption("me", "mapeditor", false, "Start MapEditorScreen")
                .addOption("c", "client", true, "Start ClientGameScreen")
                .addOption("s", "server", false, "Start ServerGameScreen")
                .addOption("p", "proto", true, "Start ProtoClientGameScreen");

        CommandLine cmd = null;
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            System.out.println("For usage, use -help option");
        } finally {
            if (cmd != null) {
                if (cmd.hasOption("help")) {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp("", options);
                    System.exit(0);
                }
            }
        }

        LwjglApplicationConfiguration.disableAudio = true;
        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        if (cmd.hasOption("proto")) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double width = screenSize.getWidth();
            double height = screenSize.getHeight();

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] graphicsDevices = ge.getScreenDevices();
            GraphicsDevice graphicsDevice = graphicsDevices[graphicsDevices.length - 1];
            Dimension dimension = new Dimension((int) width / 4, (int) height / 4);
            config.width = dimension.width;
            config.height = dimension.height;
            int c = Integer.parseInt(cmd.getOptionValue("proto").replaceAll(" ", ""));
            Rectangle rectangle = getRectangle(c, dimension, graphicsDevice);
            config.x = rectangle.x;
            config.y = rectangle.y;
        }
        return config;
    }

    private static Rectangle getRectangle(int c, Dimension dimension, GraphicsDevice graphicsDevice) {
        DisplayMode displayMode = graphicsDevice.getDisplayMode();
        Rectangle rectangle = new Rectangle();
        int startX = 0;
        int displayModeWidth = displayMode.getWidth();
//        int displayModeHeight = displayMode.getHeight();
        String iDstring = graphicsDevice.getIDstring();
        if (iDstring.contains("0")) {
            displayModeWidth /= 2f;
            startX += displayModeWidth;
        } else if (iDstring.contains("1")) {
            startX += displayModeWidth;
        }
        int y = (c * dimension.width) / displayModeWidth;
        int x = ((c * dimension.width) - (y * displayModeWidth)) / dimension.width;
        rectangle.x = startX + (x * dimension.width);
        rectangle.y = y * dimension.height;
        rectangle.width = dimension.width;
        rectangle.height = dimension.height;
        return rectangle;
    }

//    private static String getMonitorSizes() {
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice[]    gs = ge.getScreenDevices();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < gs.length; i++) {
//            DisplayMode dm = gs[i].getDisplayMode();
//            sb.append(i + ", width: " + dm.getWidth() + ", height: " + dm.getHeight() + "\n");
//        }
//        return sb.toString();
//    }
}
