package com.betmansmall.game;

import com.betmansmall.TTW;
import com.betmansmall.util.logging.Logger;

public class GameClientScreen extends GameScreen {
    public ClientSessionThread clientSessionThread;

    public GameClientScreen() {
        Logger.logFuncStart();

        this.clientSessionThread = new ClientSessionThread(TTW.game.sessionSettings);
        clientSessionThread.start();
        super.initGameField();

        Logger.logFuncEnd();
    }
}
