package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.betmansmall.enums.PlayerStatus;
import com.betmansmall.enums.SessionState;
import com.betmansmall.game.Player;
import com.betmansmall.game.ProtoSessionThread;
import com.betmansmall.screens.server.ProtoServerGameScreen;
import com.betmansmall.server.networking.ProtoTcpConnection;
import com.betmansmall.utils.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;

import protobuf.Proto;

public class ProtoServerSessionThread extends ProtoSessionThread {
    private ProtoServerGameScreen serverGameScreen;
    private SessionSettings sessionSettings;
    private ServerSocket serverSocket;
    private Array<ProtoTcpConnection> connections;

    public ProtoServerSessionThread(ProtoServerGameScreen serverGameScreen) {
        Logger.logFuncStart();
        this.serverGameScreen = serverGameScreen;
        this.sessionSettings = serverGameScreen.gameMaster.sessionSettings;
        this.serverSocket = null;
        this.connections = new Array<>();
        this.sessionState = SessionState.INITIALIZATION;
        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        this.interrupt();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (ProtoTcpConnection socket : connections) {
            socket.disconnect();
        }
    }

    @Override
    public void run() {
        Logger.logFuncStart();
//        try ( ServerSocket serverSocket = new ServerSocket(sessionSettings.gameServerPort) ) {
//            this.serverSocket = serverSocket;
        try {
            this.serverSocket = new ServerSocket(sessionSettings.gameServerPort);
            this.sessionState = SessionState.WAIT_CONNECTIONS;
            while (!this.isInterrupted()) {
                try {
                    new ProtoTcpConnection(this, serverSocket.accept());
                } catch (IOException exception) {
                    Logger.logError("exception:" + exception);
                }
            }
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            serverGameScreen.gameMaster.removeTopScreen(); // TODO mb not good!
            throw new RuntimeException(exception);
//        } finally {
//            if (serverSocket != null) {
//                serverSocket.close();
//            }
        }
        Logger.logFuncEnd();
    }

    @Override
    public void onConnectionReady(ProtoTcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        connections.add(tcpConnection);

        String uuid = UUID.randomUUID().toString();
        Integer index = connections.size;

        Proto.SendObject sendObject = Proto.SendObject.newBuilder()
                .setActionEnum(Proto.ActionEnum.START)
                .setUuid(uuid).setIndex(index)
                .setTransform(Proto.Transform.newBuilder()
                        .setPosition(Proto.Position.newBuilder())
                        .setRotation(Proto.Rotation.newBuilder())).build();
        Logger.logDebug("sendObject:" + sendObject);
        serverGameScreen.playersManager.addPlayerByServer(tcpConnection, sendObject);
        tcpConnection.sendObject(sendObject);

        sendObject = sendObject.toBuilder().setActionEnum(Proto.ActionEnum.NEW_PLAYER).build();
        sendObject(sendObject, tcpConnection);

        for (Player player : serverGameScreen.playersManager.getPlayers()) {
            if (!player.playerStatus.equals(PlayerStatus.LOCAL_SERVER)) {
                sendObject = sendObject.toBuilder().setUuid(player.accountID).setIndex(player.playerID).setTransform(player.transform).build();
                tcpConnection.sendObject(sendObject);
            }
        }
    }

    @Override
    public void onReceiveObject(ProtoTcpConnection tcpConnection, Proto.SendObject sendObject) { // need create stackArray receive SendObjects and in other thread work with it;
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        Player player = serverGameScreen.playersManager.getPlayerByConnection(tcpConnection);
        player.transform = sendObject.getTransform();
        sendObject(sendObject, tcpConnection);
    }

    @Override
    public void onDisconnect(ProtoTcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        connections.removeValue(tcpConnection, true);
        Player player = serverGameScreen.playersManager.getPlayerByConnection(tcpConnection);
//        for (TcpConnection connection : connections) {
//            connection.sendObject(new SendObject(SendObject.SendObjectEnum.PLAYER_DISCONNECTED_DATA, new PlayerInfoData(player)));
//        }
        serverGameScreen.playersManager.playerDisconnect(player);
    }

    @Override
    public void onException(ProtoTcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }

    public synchronized void sendObject(final Proto.SendObject sendObject, ProtoTcpConnection tcpConnection) {
        for (ProtoTcpConnection connection : connections) {
            if (!connection.equals(tcpConnection) || connection != tcpConnection) {
                connection.sendObject(sendObject);
            }
        }
    }

    public synchronized void sendObject(final Proto.SendObject sendObject) {
        for (ProtoTcpConnection connection : connections) {
            connection.sendObject(sendObject);
        }
    }
}
