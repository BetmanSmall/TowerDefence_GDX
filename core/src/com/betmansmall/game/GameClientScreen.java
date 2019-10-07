package com.betmansmall.game;

import com.betmansmall.game.GameScreen;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.server.SessionSettings;
import com.betmansmall.util.logging.Logger;

public class GameClientScreen extends GameScreen {
    public ClientSessionThread clientSessionThread;

    public GameClientScreen(FactionsManager factionsManager, SessionSettings sessionSettings) {
        super(factionsManager, sessionSettings.gameSettings);
        Logger.logFuncStart();

        this.clientSessionThread = new ClientSessionThread(sessionSettings.host, sessionSettings.port);
        clientSessionThread.start();

        Logger.logFuncEnd();
    }
}
