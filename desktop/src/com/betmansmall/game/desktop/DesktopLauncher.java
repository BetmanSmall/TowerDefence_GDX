package com.betmansmall.game.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.betmansmall.GameMaster;
import com.betmansmall.utils.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.awt.Dimension;
import java.awt.Toolkit;

public class DesktopLauncher {
    public static void main(String[] args) {
        Logger.logFuncStart(args);
        Options options = new Options()
                .addOption("help", false, "prints this message")
                .addOption("me", "mapeditor", false, "Start MapEditorScreen")
                .addOption("c", "client", true, "Start ClientGameScreen")
                .addOption("s", "server", false, "Start ServerGameScreen");

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

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Tower Defence";
        config.x = -3;
        config.y = 0;
        config.width = 1000;
        config.height = 500;
//        config.foregroundFPS = 90;
        config.addIcon("icons8-batman-emoji-32.png", Files.FileType.Internal);
        if (cmd.hasOption("server")) {
            config.width = (int)width/2;
            config.height = (int)height/2;
        } else if (cmd.hasOption("client")) {
            String value = cmd.getOptionValue("client");
            if (value.equals("1")) {
                config.x += (int) width / 2;
                config.width = (int) width / 2;
                config.height = (int) height / 2;
            } else if (value.equals("2")) {
                config.x += (int) width / 2;
                config.y = (int) height / 2;
                config.width = (int) width / 2;
                config.height = (int) height / 2;
            } else if (value.equals("3")) {
                config.y = (int) height / 2;
                config.width = (int) width / 2;
                config.height = (int) height / 2;
            }
//        } else if (cmd.hasOption("mapeditor")) {
        }
//        config.useGL30 = true;
//        config.fullscreen = true;
//        config.vSyncEnabled = true;
        new LwjglApplication(new GameMaster(cmd), config);
//        new LwjglApplication(new OrthographicCameraController(), config);
    }
}
