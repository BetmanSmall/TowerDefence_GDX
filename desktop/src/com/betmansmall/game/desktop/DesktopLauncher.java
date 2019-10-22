package com.betmansmall.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.betmansmall.GameMaster;
import com.betmansmall.util.logging.Logger;

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
        Options options = new Options()
                .addOption("help", false,
                        "prints this message")
                .addOption("c", "client", false, "start ClientGameScreen")
                .addOption("s", "server", false, "start ServerGameScreen");

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
        Logger.logDebug(cmd.toString());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Tower Defence";
        config.x = -3;
        config.y = 0;
        config.width = 1280;
        config.height = 720;
        if (cmd.hasOption("server")) {
            config.width = (int)width/2;
            config.height = (int)height/2;
        } else if (cmd.hasOption("client")) {
            config.x += (int)width/2;
            config.width = (int)width/2;
            config.height = (int)height/2;
        }
//        config.useGL30 = true;
//        config.fullscreen = true;
//        config.vSyncEnabled = true;
        new LwjglApplication(new GameMaster(cmd), config);
//        new LwjglApplication(new OrthographicCameraController(), config);
    }
}
