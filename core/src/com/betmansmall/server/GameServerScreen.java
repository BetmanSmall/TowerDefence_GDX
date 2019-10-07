package com.betmansmall.server;

import com.betmansmall.game.GameScreen;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;
import com.betmansmall.server.networking.ServerSessionThread;
import com.betmansmall.util.logging.Logger;

public class GameServerScreen extends GameScreen {
    public ServerSessionThread serverSessionThread;

    public GameServerScreen(FactionsManager factionsManager, SessionSettings sessionSettings) {
        super(factionsManager, sessionSettings.gameSettings);
        Logger.logFuncStart();

        this.serverSessionThread = new ServerSessionThread(sessionSettings);
        serverSessionThread.start();

        Logger.logFuncEnd();
    }
}
