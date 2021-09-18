package com.betmansmall.game;

import com.betmansmall.enums.SessionState;
import com.betmansmall.screens.client.ProtoGameScreen;
import com.betmansmall.server.SessionSettings;
import com.betmansmall.server.networking.ProtoTcpConnection;
import com.betmansmall.utils.logging.Logger;

import java.io.IOException;

import protobuf.Action;
import protobuf.ProtoObject;

public class ProtoClientSessionThread extends ProtoSessionThread {
    private ProtoGameScreen protoGameScreen;
    private SessionSettings sessionSettings;
    private ProtoTcpConnection connection;

    public ProtoClientSessionThread(ProtoGameScreen protoGameScreen) {
        Logger.logFuncStart();
        this.protoGameScreen = protoGameScreen;
        this.sessionSettings = protoGameScreen.gameMaster.sessionSettings;
        this.connection = null;
        this.sessionState = SessionState.INITIALIZATION;
    }

    public void dispose() {
        Logger.logFuncStart();
        this.interrupt();
        this.connection.disconnect();
    }

    @Override
    public void run() {
        Logger.logFuncStart("Try connect to:" + sessionSettings.host + ":" + sessionSettings.gameServerPort);
        try {
            new ProtoTcpConnection(this, sessionSettings.host, sessionSettings.gameServerPort);
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            protoGameScreen.gameMaster.removeTopScreen(); // TODO mb not good!
            throw new RuntimeException(exception);
        }
        Logger.logFuncEnd();
    }

    @Override
    public void onConnectionReady(ProtoTcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        this.connection = tcpConnection;
        sessionState = SessionState.CONNECTED;
    }

    @Override
    public void onReceiveObject(ProtoTcpConnection tcpConnection, ProtoObject sendObject) {
//        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
//        Logger.logDebug("sendObject.getActionEnum():" + sendObject.getActionEnum());
        Action actionEnum = sendObject.getAction();
        if (actionEnum.equals(Action.PLAYER_CREATE)) {
            Player player = protoGameScreen.playersManager.addPlayerByClient(tcpConnection, sendObject, protoGameScreen.physicsObjectManager);
            protoGameScreen.protoController.setPlayer(player);
            if (protoGameScreen.playersManager.getLocalPlayer() == null) {
                protoGameScreen.playersManager.getPlayers().remove(player);
                protoGameScreen.playersManager.setLocalPlayer(player);
            } else {
                Logger.logDebug("get START again!");
                Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
                Logger.logDebug("sendObject.getActionEnum():" + sendObject.getAction());
            }
        } else if (actionEnum.equals(Action.PLAYER_MOVE) || actionEnum.equals(Action.OBJECT_MOOVE))  {
            ProtoGameObject protoGameObject = protoGameScreen.physicsObjectManager.instances.get(sendObject.getUuid());
            if (protoGameObject != null) {
                protoGameObject.updateData(sendObject);
            } else {
                Player player = protoGameScreen.playersManager.getPlayer(sendObject.getUuid());
                if (player != null) {
                    player.updateData(sendObject);
                }
            }
        } else if (actionEnum.equals(Action.OBJECT_CREATE)) {
            protoGameScreen.physicsObjectManager.addByClient(sendObject);
        } else if (actionEnum.equals(Action.OBJECT_REMOVE)) {
            protoGameScreen.physicsObjectManager.removeObject(sendObject.getUuid());
        }
//        System.out.println("sendObject.toString():" + sendObject.toString().replaceAll("\n", " "));
//        System.out.println("sendObject.toByteArray():" + Arrays.toString(sendObject.toByteArray()));
//        System.out.println("sendObject.toByteString():" + sendObject.toByteString() + " size:" + sendObject.getSerializedSize());
    }

    @Override
    public void onDisconnect(ProtoTcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
//        protoGameScreen.gameMaster.removeTopScreen(); // TODO mb not good!
    }

    @Override
    public void onException(ProtoTcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
        exception.printStackTrace();
    }

    public void sendObject(final ProtoObject sendObject, ProtoTcpConnection tcpConnection) {
        Logger.logError("sendObject:" + sendObject + ", tcpConnection:" + tcpConnection);
    }

    public synchronized void sendObject(final ProtoObject sendObject) {
        this.connection.sendObject(sendObject);
    }
}
