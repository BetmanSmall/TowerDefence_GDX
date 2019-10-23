package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.betmansmall.enums.SessionState;
import com.betmansmall.game.Player;
import com.betmansmall.screens.server.ServerGameScreen;
import com.betmansmall.server.data.NetworkPackage;
import com.betmansmall.server.data.PlayerInfoData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.ServerInfoData;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSessionThread extends Thread implements TcpSocketListener {
    private ServerGameScreen serverGameScreen;
    private SessionSettings sessionSettings;
    private ServerSocket serverSocket;
    private Array<TcpConnection> connections;
    public SessionState sessionState;

    public ServerSessionThread(ServerGameScreen serverGameScreen) {
        Logger.logFuncStart();
        this.serverGameScreen = serverGameScreen;
        this.sessionSettings = serverGameScreen.game.sessionSettings;
        this.serverSocket = null;
        this.connections = new Array<TcpConnection>();
        this.sessionState = SessionState.INITIALIZATION;
        Logger.logFuncEnd();
    }

    public void dispose() {
        Logger.logFuncStart();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(TcpConnection socket : connections) {
            socket.disconnect();
        }
        this.interrupt();
    }

    @Override
    public void run() {
        Logger.logFuncStart();
//        try ( ServerSocket serverSocket = new ServerSocket(sessionSettings.port) ) {
//            this.serverSocket = serverSocket;
        try {
            this.serverSocket = new ServerSocket(sessionSettings.port);
            this.sessionState = SessionState.WAIT_CONNECTIONS;
            while (!this.isInterrupted()) {
                try {
                    new TcpConnection(this, serverSocket.accept());
                } catch (IOException exception) {
                    Logger.logError("exception:" + exception);
                }
            }
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            throw new RuntimeException(exception);
//        } finally {
//            if (serverSocket != null) {
//                serverSocket.close();
//            }
        }
        Logger.logFuncEnd();
    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        connections.add(tcpConnection);
        tcpConnection.sendObject(new SendObject(new ServerInfoData(sessionSettings.gameSettings)));
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        for (NetworkPackage networkPackage : sendObject.networkPackages) {
            if (networkPackage instanceof PlayerInfoData) {
                PlayerInfoData playerInfoData = (PlayerInfoData) networkPackage;
                Player player = new Player();
                player.connection = tcpConnection;
                player.playerID = sessionSettings.gameSettings.playersManager.players.size;
                player.name = playerInfoData.name;
                player.faction = serverGameScreen.game.factionsManager.getFactionByName(playerInfoData.factionName);
                sessionSettings.gameSettings.playersManager.players.add(player);
                sessionState = SessionState.PLAYER_CONNECTED;
            }
        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
//        connections.removeValue(tcpConnection, true);
//        gameServer.playerDisconnect(tcpConnection);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }
}
