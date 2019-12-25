package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.betmansmall.screens.server.ServerGameScreen;
import com.betmansmall.server.data.GameServerNetworkData;
import com.betmansmall.server.data.GameSettingsData;
import com.betmansmall.server.data.NetworkPackage;
import com.betmansmall.server.data.PlayersManagerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.VersionData;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class AuthServerThread extends Thread implements TcpSocketListener, Disposable {
    private ServerGameScreen serverGameScreen;
    private SessionSettings sessionSettings;
    private ServerSocket serverSocket;
    private Array<TcpConnection> connections;

    public AuthServerThread(ServerGameScreen serverGameScreen) {
        Logger.logFuncStart();
        this.serverGameScreen = serverGameScreen;
        this.sessionSettings = serverGameScreen.game.sessionSettings;
        this.serverSocket = null;
        this.connections = new Array<TcpConnection>();
        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (TcpConnection socket : connections) {
            socket.disconnect();
        }
        this.interrupt();
    }

    @Override
    public void run() {
        Logger.logFuncStart();
        try {
            this.serverSocket = new ServerSocket(sessionSettings.authServerPort);
            while (!this.isInterrupted()) {
                try {
                    new TcpConnection(this, serverSocket.accept());
                } catch (IOException exception) {
                    Logger.logError("exception:" + exception);
                }
            }
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
        }
        Logger.logFuncEnd();
    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        connections.add(tcpConnection);

        NetworkPackage versionData = new VersionData(serverGameScreen.game.version);
        NetworkPackage gameSettingsData = new GameSettingsData(sessionSettings.gameSettings);
        NetworkPackage playersManagerData = new PlayersManagerData(serverGameScreen.playersManager);
        NetworkPackage gameServerNetworkData = new GameServerNetworkData(serverSocket.getInetAddress().getHostAddress(), sessionSettings.gameServerPort);

        tcpConnection.sendObject(new SendObject(SendObject.SendObjectEnum.SERVER_VERSION_AND_BASE_INFO_DATA, versionData, gameSettingsData, playersManagerData, gameServerNetworkData));
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        if (sendObject.sendObjectEnum != null) {

        } else {

        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        connections.removeValue(tcpConnection, true);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }
}
