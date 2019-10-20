package com.betmansmall.screens.server;

import com.betmansmall.GameMaster;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.server.ServerSessionThread;
import com.betmansmall.util.logging.Logger;

public class GameServerScreen extends GameScreen {
    public ServerSessionThread serverSessionThread;

    public GameServerScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();

        this.serverSessionThread = new ServerSessionThread(game.sessionSettings);
        serverSessionThread.start();
        super.initGameField();

        Logger.logFuncEnd();
    }
}
