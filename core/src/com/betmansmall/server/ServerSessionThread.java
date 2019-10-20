package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.betmansmall.enums.SessionState;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSessionThread extends Thread implements TcpSocketListener {
    public SessionState sessionState;
    public SessionSettings sessionSettings;
    public ServerSocket serverSocket;
    public Array<TcpConnection> connections;

    public ServerSessionThread(SessionSettings sessionSettings) {
        Logger.logFuncStart();
        this.sessionState = SessionState.INITIALIZATION;
        this.sessionSettings = sessionSettings;
        this.serverSocket = null;
        this.connections = new Array<TcpConnection>();
        Logger.logFuncEnd();
    }

    @Override
    public void run() {
        Logger.logFuncStart();
//        try ( ServerSocket serverSocket = new ServerSocket(port) ) {
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

//    public void dispose() {
//        Logger.logInfo("");
//        for(TcpConnection socket : connections) {
//            socket.disconnect();
//        }
//        try {
//            serverSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        gameServer = null;
//        this.interrupt();
//    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        connections.add(tcpConnection);
//        tcpConnection.sendObject(new PackageServerGreating());
//        gameServer.sendServerGreating(tcpConnection);
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
//        gameServer.serverPackageProcessing(tcpConnection, sendObject);
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
    }
}
