package com.betmansmall.game;

import com.betmansmall.enums.SessionState;
import com.betmansmall.screens.client.ProtoGameScreen;
import com.betmansmall.server.SessionSettings;
import com.betmansmall.server.networking.ProtoTcpConnection;
import com.betmansmall.utils.logging.Logger;

import java.io.IOException;

import protobuf.Proto;

public class ProtoClientSessionThread extends ProtoSessionThread {
    private ProtoGameScreen protoGameScreen;
    private SessionSettings sessionSettings;
    private ProtoTcpConnection connection;

    public ProtoClientSessionThread(ProtoGameScreen protoGameScreen) {
        Logger.logFuncStart();
        this.protoGameScreen = protoGameScreen;
        this.sessionSettings = protoGameScreen.gameMaster.sessionSettings;
        this.connection = null;
        this.sessionState = SessionState.INITIALIZATION;
    }

    public void dispose() {
        Logger.logFuncStart();
        this.interrupt();
        this.connection.disconnect();
    }

    @Override
    public void run() {
        Logger.logFuncStart("Try connect to:" + sessionSettings.host + ":" + sessionSettings.gameServerPort);
        try {
            new ProtoTcpConnection(this, sessionSettings.host, sessionSettings.gameServerPort);
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            protoGameScreen.gameMaster.removeTopScreen(); // TODO mb not good!
            throw new RuntimeException(exception);
        }
        Logger.logFuncEnd();
    }

    @Override
    public void onConnectionReady(ProtoTcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        this.connection = tcpConnection;
        sessionState = SessionState.CONNECTED;
    }

    @Override
    public void onReceiveObject(ProtoTcpConnection tcpConnection, Proto.SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
    }


    @Override
    public void onDisconnect(ProtoTcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        protoGameScreen.gameMaster.removeTopScreen(); // TODO mb not good!
    }

    @Override
    public void onException(ProtoTcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }

    public void sendObject(final Proto.SendObject sendObject, ProtoTcpConnection tcpConnection) {
        Logger.logError("sendObject:" + sendObject + ", tcpConnection:" + tcpConnection);
    }
}
