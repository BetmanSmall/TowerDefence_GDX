package com.betmansmall.screens.client;

import com.betmansmall.game.ClientSessionThread;
import com.betmansmall.GameMaster;
import com.betmansmall.util.logging.Logger;

public class GameClientScreen extends GameScreen {
    public ClientSessionThread clientSessionThread;

    public GameClientScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();

        this.clientSessionThread = new ClientSessionThread(game.sessionSettings);
        clientSessionThread.start();
        super.initGameField();

        Logger.logFuncEnd();
    }
}
