package com.betmansmall.screens.client;

import com.betmansmall.enums.SessionState;
import com.betmansmall.game.ClientSessionThread;
import com.betmansmall.GameMaster;
import com.betmansmall.util.logging.Logger;

public class ClientGameScreen extends GameScreen {
    public ClientSessionThread clientSessionThread;

    public ClientGameScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();

        this.clientSessionThread = new ClientSessionThread(this);
        clientSessionThread.start();

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        super.dispose();
        clientSessionThread.dispose();
    }

    @Override
    public void render(float delta) {
        if (clientSessionThread.sessionState == SessionState.RECEIVED_SERVER_INFO_DATA) {
            this.initGameField();
            clientSessionThread.sessionState = SessionState.INITIALIZED;
        }
        if (clientSessionThread.sessionState == SessionState.INITIALIZED) {
            super.render(delta);
        }
    }
}
