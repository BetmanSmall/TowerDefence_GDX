package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.betmansmall.screens.client.ClientSettingsScreen;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.server.networking.TcpSocketListener;
import com.betmansmall.utils.logging.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
public class ServersSearchThread extends Thread implements TcpSocketListener, Disposable {
    private ClientSettingsScreen clientSettingsScreen;
    private Array<TcpConnection> connections;

    private int authPort = 48999;
    private int timeout = 1000;

    private int from1, to1, from2, to2;

    public ServersSearchThread(int from1, int to1, int from2, int to2, ClientSettingsScreen clientSettingsScreen) {
        Logger.logFuncStart();
        this.clientSettingsScreen = clientSettingsScreen;
        this.connections = new Array<>();

        this.from1 = from1;
        this.to1 = to1;
        this.from2 = from2;
        this.to2 = to2;
        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        super.interrupt();
        this.clientSettingsScreen = null; // if set null || in run()->tryConnectToHost() throw NullPointer after check interrupted(). потому что не успевает. он проскакиевает проверку.
        for (TcpConnection connection : connections) {
            connection.disconnect();
        }
        this.connections.clear();
    }

    @Override
    public void run() {
        Logger.logFuncStart();
        if (from1 == 0) {
            tryConnectToHost("127.0.0.1");
        } else if (to1 <= 255 && to2 <= 255) {
            String subnet = "192.168";
            for (int i1 = from1; i1 <= to1; i1++) {
                for (int i2 = from2; i2 <= to2; i2++) {
                    boolean interrupted = interrupted();
                    if (!interrupted) {
                        String host = subnet + "." + i1 + "." + i2;
                        tryConnectToHost(host);
                    } else {
                        Logger.logDebug("this:" + this);
                        dispose();
                        return;
                    }
                }
            }
        }
        Logger.logFuncEnd();
    }

    private void tryConnectToHost(String host) {
        try {
            Logger.logInfo("host:" + host);
//            clientSettingsScreen.setProgressSearch("currentSearchLabel:" + host);
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
                    if (clientSettingsScreen != null) {
                        clientSettingsScreen.addSimpleHost(host);
                    }
                }
            }
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }
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
                    clientSettingsScreen.addServerBaseInfo(tcpConnection.getRemoteHost(), sendObject.networkPackages);
                }
            }
        } else {

        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
    }
}
