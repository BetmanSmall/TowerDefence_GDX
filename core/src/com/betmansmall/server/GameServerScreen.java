package com.betmansmall.server;

import com.betmansmall.TTW;
import com.betmansmall.game.GameScreen;
import com.betmansmall.server.networking.ServerSessionThread;
import com.betmansmall.util.logging.Logger;

public class GameServerScreen extends GameScreen {
    public ServerSessionThread serverSessionThread;

    public GameServerScreen() {
        Logger.logFuncStart();

        this.serverSessionThread = new ServerSessionThread(TTW.game.sessionSettings);
        serverSessionThread.start();
        super.initGameField();

        Logger.logFuncEnd();
    }
}
