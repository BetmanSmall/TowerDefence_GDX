package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.betmansmall.screens.client.ClientSettingsScreen;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;

public class ServersSearchThread extends Thread implements TcpSocketListener {
    private ClientSettingsScreen clientSettingsScreen;
    private Array<TcpConnection> connections;

    private int authPort = 48999;
    private int timeout = 1000;

    public ServersSearchThread(ClientSettingsScreen clientSettingsScreen) {
        Logger.logFuncStart();
        this.clientSettingsScreen = clientSettingsScreen;
        this.connections = new Array<TcpConnection>();
        Logger.logFuncEnd();
    }

    private void tryConnectToHost(String host) {
        try {
            Logger.logInfo("host:" + host);
            clientSettingsScreen.currentSearchLabel.setText("currentSearchLabel:" + host);
            if (InetAddress.getByName(host).isReachable(timeout)) {
                Logger.logFuncStart("getCanonicalHostName:" + InetAddress.getByName(host).getCanonicalHostName());
//                Logger.logFuncStart("getHostAddress:" + InetAddress.getByName(host).getHostAddress());
//                Logger.logFuncStart("getHostName:" + InetAddress.getByName(host).getHostName());
//                Logger.logFuncStart("toString:" + InetAddress.getByName(host).toString());

                Logger.logInfo("Try connect to:" + host + ":" + authPort);
                try {
                    new TcpConnection(this, host, authPort);
                } catch (ConnectException exp) {
                    Logger.logError("exp:" + exp);
//                        exp.printStackTrace();
                    clientSettingsScreen.addSimpleHost(host);
                }
            }
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void run() {
        Logger.logFuncStart();
        tryConnectToHost("127.0.0.1");
        String subnet = "192.168.0";
        for (int i = 1; i <= 255; i++) {
            String host = subnet + "." + i;
            tryConnectToHost(host);
        }
        Logger.logFuncEnd();
    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        connections.add(tcpConnection);

//        NetworkPackage versionData = new VersionData(clientSettingsScreen.game.version);
//        tcpConnection.sendObject(new SendObject(versionData));
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) {
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        if (sendObject.sendObjectEnum != null) {
            switch (sendObject.sendObjectEnum) {
                case SERVER_VERSION_AND_BASE_INFO_DATA: {
                    clientSettingsScreen.addServerBaseInfo(sendObject.networkPackages);
                }
            }
        } else {

        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {

    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception e) {

    }
}
