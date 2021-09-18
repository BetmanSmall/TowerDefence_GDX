package com.betmansmall.screens.client;

import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.ProtoClientSessionThread;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.utils.logging.Logger;

import protobuf.Action;
import protobuf.ProtoObject;

public class ProtoClientGameScreen extends ProtoGameScreen {
    public ProtoClientSessionThread clientSessionThread;

    private ProtoObject lastSendObject = ProtoObject.getDefaultInstance();

    public ProtoClientGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);
        Logger.logFuncStart();

        this.clientSessionThread = new ProtoClientSessionThread(this);
        this.clientSessionThread.start();

//        FirstPersonCameraController firstPersonCameraController = new FirstPersonCameraController(perspectiveCamera);

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        this.clientSessionThread.dispose();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Player player = playersManager.getLocalPlayer();
        if (player != null && player.hmdGameObject != null) {
            player.hmdGameObject.update(protoController);
            ProtoObject sendObject = ProtoObject.newBuilder()
                    .setIndex(player.playerID).setUuid(player.accountID)
                    .setAction(Action.PLAYER_MOVE)
                    .setTransform(ProtoObject.Transform.newBuilder().setPosition(
                            ProtoObject.Position.newBuilder().setX(player.hmdGameObject.position.x).setY(player.hmdGameObject.position.y).setZ(player.hmdGameObject.position.z).build()).setRotation(
                            ProtoObject.Rotation.newBuilder().setX(player.hmdGameObject.rotation.x).setY(player.hmdGameObject.rotation.y).setZ(player.hmdGameObject.rotation.z).setW(player.hmdGameObject.rotation.w).build()).build()).build();
            if (!lastSendObject.equals(sendObject)) {
                clientSessionThread.sendObject(sendObject);
                lastSendObject = sendObject;
            }
        }
    }
}
