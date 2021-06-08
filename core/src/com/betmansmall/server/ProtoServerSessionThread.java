package com.betmansmall.server;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.betmansmall.enums.SessionState;
import com.betmansmall.game.Player;
import com.betmansmall.game.PlayerType;
import com.betmansmall.game.ProtoGameObject;
import com.betmansmall.game.ProtoSessionThread;
import com.betmansmall.screens.server.ProtoServerGameScreen;
import com.betmansmall.server.networking.ProtoTcpConnection;
import com.betmansmall.utils.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import protobuf.Proto;

public class ProtoServerSessionThread extends ProtoSessionThread {
    private ProtoServerGameScreen serverGameScreen;
    private SessionSettings sessionSettings;
    private ServerSocket serverSocket;
    private ArrayList<ProtoTcpConnection> connections;

    public ProtoServerSessionThread(ProtoServerGameScreen serverGameScreen) {
        Logger.logFuncStart();
        this.serverGameScreen = serverGameScreen;
        this.sessionSettings = serverGameScreen.gameMaster.sessionSettings;
        this.serverSocket = null;
        this.connections = new ArrayList<>();
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
    public synchronized void onConnectionReady(ProtoTcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        connections.add(tcpConnection);

        Player newPlayer = serverGameScreen.playersManager.addPlayerByServer(tcpConnection, serverGameScreen.physicsObjectManager);
        Proto.SendObject sendObject = Proto.SendObject.newBuilder()
                .setActionEnum(Proto.ActionEnum.START)
                .setUuid(newPlayer.accountID).setIndex(newPlayer.playerID)
                .setTransform(newPlayer.gameObject.protoTransform).build();
        Logger.logDebug("sendObject:" + sendObject);
        tcpConnection.sendObject(sendObject);

        sendObject = sendObject.toBuilder().setActionEnum(Proto.ActionEnum.NEW_PLAYER).build();
        sendObject(sendObject, tcpConnection);

        ArrayList<Player> players = serverGameScreen.playersManager.getPlayers();
//        Logger.logDebug("players.size:" + players.size());
        for (Player player : players) {
//            Logger.logDebug("player:" + player);
            if (player != null && !player.type.equals(PlayerType.SERVER) && player.protoTcpConnection != tcpConnection) {
                sendObject = sendObject.toBuilder().setUuid(player.accountID).setIndex(player.playerID).setTransform(player.gameObject.protoTransform).build();
                tcpConnection.sendObject(sendObject);
            }
        }

//        for (int p = 0; p < serverGameScreen.physicsObjectManager.instances.size(); p++) {
//            ProtoGameObject protoGameObject = serverGameScreen.physicsObjectManager.instances.get(p);
        for (ProtoGameObject protoGameObject : serverGameScreen.physicsObjectManager.instances.values()) {
            if (protoGameObject.index != null && protoGameObject.uuid != null) {
                Vector3 position = protoGameObject.physicsObject.body.getWorldTransform().getTranslation(protoGameObject.position);
                Quaternion rotation = protoGameObject.physicsObject.body.getOrientation();
                sendObject = Proto.SendObject.newBuilder()
                        .setIndex(protoGameObject.index).setUuid(protoGameObject.uuid)
                        .setActionEnum(Proto.ActionEnum.NEW_OBJECT)
                        .setPrefabName(protoGameObject.prefabName)
                        .setTransform(Proto.Transform.newBuilder().setPosition(
                                Proto.Position.newBuilder().setX(position.x).setY(position.y).setZ(position.z).build()).setRotation(
                                Proto.Rotation.newBuilder().setX(rotation.x).setY(rotation.y).setZ(rotation.z).setW(rotation.w).build()).build()).build();
                tcpConnection.sendObject(sendObject);
            }
        }
    }

    @Override
    public void onReceiveObject(ProtoTcpConnection tcpConnection, Proto.SendObject sendObject) { // need create stackArray receive SendObjects and in other thread work with it;
//        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        Player player = serverGameScreen.playersManager.getPlayerByConnection(tcpConnection);
        player.updateData(sendObject);
        sendObject(sendObject, tcpConnection);
//        System.out.println("sendObject.toString():" + sendObject.toString().replaceAll("\n", " "));
//        System.out.println("sendObject.toByteArray():" + Arrays.toString(sendObject.toByteArray()));
//        System.out.println("sendObject.toByteString():" + sendObject.toByteString() + " size:" + sendObject.getSerializedSize());
    }

    @Override
    public synchronized void onDisconnect(ProtoTcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        connections.remove(tcpConnection);
        Player player = serverGameScreen.playersManager.getPlayerByConnection(tcpConnection);
//        for (TcpConnection connection : connections) {
//            connection.sendObject(new SendObject(SendObject.SendObjectEnum.PLAYER_DISCONNECTED_DATA, new PlayerInfoData(player)));
//        }
        serverGameScreen.playersManager.playerDisconnect(player);
        serverGameScreen.protoController.setPlayer(player);
    }

    @Override
    public void onException(ProtoTcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }

    public synchronized void sendObject(final Proto.SendObject sendObject, ProtoTcpConnection tcpConnection) {
        for (ProtoTcpConnection connection : connections) {
            if (!connection.equals(tcpConnection) || connection != tcpConnection) {
//                Thread thread1 = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
                        connection.sendObject(sendObject);
//                    }
//                });
//                thread1.start();
            }
        }
    }

    public synchronized void sendObject(final Proto.SendObject sendObject) {
        for (ProtoTcpConnection connection : connections) {
            connection.sendObject(sendObject);
        }
    }
}
