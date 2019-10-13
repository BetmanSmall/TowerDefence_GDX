package com.betmansmall.game;

import com.betmansmall.server.SessionSettings;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;

public class ClientSessionThread extends Thread implements TcpSocketListener {
    public TcpConnection connection;
    private String host;
    private int port;

    public ClientSessionThread(SessionSettings sessionSettings) {
        Logger.logInfo("");
        this.connection = null;
        this.host = sessionSettings.host;
        this.port = sessionSettings.port;
    }

    @Override
    public void run() {
        Logger.logInfo("Try connect to:" + host + ":" + port);
        try {
            connection = new TcpConnection(this, host, port);
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            throw new RuntimeException(exception);
        }
    }

    public void dispose() {
        Logger.logInfo("");
        connection.disconnect();
        this.interrupt();
    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        //connection.sendObject(new SendObject("User1", "hello thisIsServer!"));
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
    }

//    private synchronized void printMSG(final String msgWarn) { // synchronized for different threads;
//        System.out.println("ClientSessionThread::printMSG(); -- " + msgWarn);
//    }
}
