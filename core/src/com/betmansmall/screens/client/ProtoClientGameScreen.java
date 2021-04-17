package com.betmansmall.screens.client;

import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.ProtoClientSessionThread;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.utils.logging.Logger;

import protobuf.Proto;

public class ProtoClientGameScreen extends ProtoGameScreen {
    public ProtoClientSessionThread clientSessionThread;

    private Proto.SendObject lastSendObject = Proto.SendObject.getDefaultInstance();

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
        if (player != null && player.gameObject != null) {
            player.gameObject.update(protoController);
            Proto.SendObject sendObject = Proto.SendObject.newBuilder()
                    .setIndex(player.playerID).setUuid(player.accountID)
                    .setActionEnum(Proto.ActionEnum.MOVE)
                    .setTransform(Proto.Transform.newBuilder().setPosition(
                            Proto.Position.newBuilder().setX(player.gameObject.position.x).setY(player.gameObject.position.y).setZ(player.gameObject.position.z).build()).setRotation(
                            Proto.Rotation.newBuilder().setX(player.gameObject.rotation.x).setY(player.gameObject.rotation.y).setZ(player.gameObject.rotation.z).setW(player.gameObject.rotation.w).build()).build()).build();
            if (!lastSendObject.equals(sendObject)) {
                clientSessionThread.sendObject(sendObject);
                lastSendObject = sendObject;
            }
        }
    }
}
