package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.betmansmall.enums.SessionState;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.server.ServerGameScreen;
import com.betmansmall.server.data.BuildTowerData;
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
            serverGameScreen.game.removeTopScreen(); // TODO mb not good!
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

        for (Player player : serverGameScreen.playersManager.getPlayers()) {
            if (player.playerID != 0) {
                tcpConnection.sendObject(new SendObject(new PlayerInfoData(player)));
            }
        }
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        for (NetworkPackage networkPackage : sendObject.networkPackages) {
            if (networkPackage instanceof PlayerInfoData) {
                PlayerInfoData playerInfoData = (PlayerInfoData) networkPackage;

                Player player = new Player(tcpConnection, playerInfoData.type, null);
                player.playerID = serverGameScreen.playersManager.getPlayers().size;
                player.name = playerInfoData.name;
                player.faction = serverGameScreen.game.factionsManager.getFactionByName(playerInfoData.factionName);
                serverGameScreen.playersManager.addPlayer(player);
                sessionState = SessionState.PLAYER_CONNECTED;

                for (TcpConnection connection : connections) {
                    connection.sendObject(new SendObject(new PlayerInfoData(player)));
                }
            } else if (networkPackage instanceof BuildTowerData) {
                BuildTowerData buildTowerData = (BuildTowerData) networkPackage;

                TemplateForTower templateForTower = tcpConnection.player.faction.getTemplateForTower(buildTowerData.templateName);
                Tower tower = serverGameScreen.tryCreateTower(buildTowerData.buildX, buildTowerData.buildY, templateForTower, tcpConnection.player);

                for (TcpConnection connection : connections) {
                    if (tcpConnection.equals(connection)) {
                        connection.sendObject(new SendObject(new BuildTowerData(tower)));
                    }
                }
            }
        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        connections.removeValue(tcpConnection, true);
        Player player = serverGameScreen.playersManager.getPlayerByConnection(tcpConnection);
        for (TcpConnection connection : connections) {
            connection.sendObject(new SendObject(SendObject.SendObjectEnum.PLAYER_DISCONNECTED, new PlayerInfoData(player)));
        }
        serverGameScreen.playersManager.removePlayer(player);
//        gameServer.playerDisconnect(tcpConnection);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }

    public synchronized void sendObjectToAll(final SendObject sendObject) {
        for (TcpConnection connection : connections) {
            connection.sendObject(sendObject);
        }
    }
}
