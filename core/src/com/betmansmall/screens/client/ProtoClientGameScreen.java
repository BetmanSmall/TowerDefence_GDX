package com.betmansmall.screens.client;

import com.betmansmall.GameMaster;
import com.betmansmall.game.ProtoClientSessionThread;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.utils.logging.Logger;

public class ProtoClientGameScreen extends ProtoGameScreen {
    public ProtoClientSessionThread clientSessionThread;

    public ProtoClientGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);
        Logger.logFuncStart();

        this.clientSessionThread = new ProtoClientSessionThread(this);
        this.clientSessionThread.start();

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        this.clientSessionThread.dispose();
    }
}
