package com.betmansmall.game;

import com.betmansmall.enums.SessionState;
import com.betmansmall.screens.client.ClientGameScreen;
import com.betmansmall.server.SessionSettings;
import com.betmansmall.server.data.NetworkPackage;
import com.betmansmall.server.data.PlayerInfoData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.ServerInfoData;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;

public class ClientSessionThread extends Thread implements TcpSocketListener {
    private ClientGameScreen clientGameScreen;
    private SessionSettings sessionSettings;
    private TcpConnection connection;
    public SessionState sessionState;

    public ClientSessionThread(ClientGameScreen clientGameScreen) {
        Logger.logFuncStart();
        this.clientGameScreen = clientGameScreen;
        this.sessionSettings = clientGameScreen.game.sessionSettings;
        this.connection = null;
        this.sessionState = SessionState.INITIALIZATION;
    }

    public void dispose() {
        Logger.logFuncStart();
        connection.disconnect();
        this.interrupt();
    }

    @Override
    public void run() {
        Logger.logInfo("Try connect to:" + sessionSettings.host + ":" + sessionSettings.port);
        try {
            new TcpConnection(this, sessionSettings.host, sessionSettings.port);
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        this.connection = tcpConnection;
        sessionState = SessionState.CONNECTED;
        tcpConnection.sendObject(new SendObject(new PlayerInfoData(sessionSettings.gameSettings.playersManager.localPlayer)));
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        for (NetworkPackage networkPackage : sendObject.networkPackages) {
            if (networkPackage instanceof ServerInfoData) {
                ServerInfoData serverInfoData = (ServerInfoData) networkPackage;
                clientGameScreen.game.sessionSettings.gameSettings.mapPath = serverInfoData.mapPath;
                clientGameScreen.game.sessionSettings.gameSettings.gameType = serverInfoData.gameType;
                sessionState = SessionState.RECEIVED_SERVER_INFO_DATA;
            }
        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }

//    private synchronized void printMSG(final String msgWarn) { // synchronized for different threads;
//        System.out.println("ClientSessionThread::printMSG(); -- " + msgWarn);
//    }
}
