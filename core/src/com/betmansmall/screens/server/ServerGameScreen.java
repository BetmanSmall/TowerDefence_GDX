package com.betmansmall.screens.server;

import com.betmansmall.GameMaster;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.server.ServerSessionThread;
import com.betmansmall.util.logging.Logger;

public class ServerGameScreen extends GameScreen {
    public ServerSessionThread serverSessionThread;

    public ServerGameScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();

        this.serverSessionThread = new ServerSessionThread(this);
        this.serverSessionThread.start();
        super.initGameField();

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        serverSessionThread.dispose();
    }
}
