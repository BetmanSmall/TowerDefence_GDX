package com.betmansmall.screens.server;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.ProtoGameObject;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.screens.client.ProtoGameScreen;
import com.betmansmall.server.AuthServerThread;
import com.betmansmall.server.ProtoServerSessionThread;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.utils.logging.Logger;

import protobuf.Proto;

public class ProtoServerGameScreen extends ProtoGameScreen {
    public AuthServerThread authServerThread;
    public ProtoServerSessionThread protoServerSessionThread;

    private Proto.SendObject lastSendObject = Proto.SendObject.getDefaultInstance();

    public ProtoServerGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);
        Logger.logFuncStart();

        this.authServerThread = new AuthServerThread(this);
        this.protoServerSessionThread = new ProtoServerSessionThread(this);

        this.authServerThread.start();
        this.protoServerSessionThread.start();
//        super.initGameField();

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        this.authServerThread.dispose();
        this.protoServerSessionThread.dispose();
    }

    @Override
    public void render(float delta) {
        if (gameField != null && !gameField.gamePaused) {
            if (physicsObjectManager.instances.values().size() > 5) {
                String uuid = physicsObjectManager.instances.keySet().toArray()[2].toString();
                ProtoGameObject protoGameObject = physicsObjectManager.instances.get(uuid);
                physicsObjectManager.dynamicsWorld.removeRigidBody(protoGameObject.physicsObject.body);
                Proto.SendObject sendObject = Proto.SendObject.newBuilder()
                        .setUuid(protoGameObject.uuid)
                        .setActionEnum(Proto.ActionEnum.REMOVE_OBJECT).build();
                protoServerSessionThread.sendObject(sendObject);
                protoGameObject.dispose();
                physicsObjectManager.instances.remove(uuid);
            }
            this.physicsObjectManager.update(delta);
        }
        super.render(delta);
        Player currPlayer = protoController.player;
        if (currPlayer != null && currPlayer.hmdGameObject != null) {
            currPlayer.hmdGameObject.update(protoController);
            Proto.SendObject sendObject = Proto.SendObject.newBuilder()
                    .setIndex(currPlayer.playerID).setUuid(currPlayer.accountID)
                    .setActionEnum(Proto.ActionEnum.MOVE)
                    .setTransform(Proto.Transform.newBuilder().setPosition(
                            Proto.Position.newBuilder().setX(currPlayer.hmdGameObject.position.x).setY(currPlayer.hmdGameObject.position.y).setZ(currPlayer.hmdGameObject.position.z).build()).setRotation(
                            Proto.Rotation.newBuilder().setX(currPlayer.hmdGameObject.rotation.x).setY(currPlayer.hmdGameObject.rotation.y).setZ(currPlayer.hmdGameObject.rotation.z).setW(currPlayer.hmdGameObject.rotation.w).build()).build()).build();
            if (!lastSendObject.equals(sendObject)) {
                protoServerSessionThread.sendObject(sendObject);
                lastSendObject = sendObject;
            }
        }
        for (Player player : playersManager.getPlayers()) {
            if (player != null && player.hmdGameObject != null) {
                Vector3 position = player.hmdGameObject.physicsObject.body.getWorldTransform().getTranslation(player.hmdGameObject.position);
                Quaternion rotation = player.hmdGameObject.physicsObject.body.getOrientation();
                Proto.SendObject sendObject = Proto.SendObject.newBuilder()
                        .setIndex(player.playerID).setUuid(player.accountID)
                        .setActionEnum(Proto.ActionEnum.MOVE)
                        .setTransform(Proto.Transform.newBuilder().setPosition(
                                Proto.Position.newBuilder().setX(position.x).setY(position.y).setZ(position.z).build()).setRotation(
                                Proto.Rotation.newBuilder().setX(rotation.x).setY(rotation.y).setZ(rotation.z).setW(rotation.w).build()).build()).build();
                if (!player.lastSendObject.equals(sendObject)) {
                    protoServerSessionThread.sendObject(sendObject);
                    player.lastSendObject = sendObject;
                }
            }
        }
        for (ProtoGameObject protoGameObject : physicsObjectManager.instances.values()) {
            if (protoGameObject.index != null && protoGameObject.uuid != null) {
                Vector3 position = protoGameObject.physicsObject.body.getWorldTransform().getTranslation(protoGameObject.position);
                Quaternion rotation = protoGameObject.physicsObject.body.getOrientation();
                Proto.SendObject sendObject = Proto.SendObject.newBuilder()
                        .setIndex(protoGameObject.index).setUuid(protoGameObject.uuid)
                        .setActionEnum(Proto.ActionEnum.MOVE)
                        .setPrefabName(protoGameObject.prefabName)
                        .setTransform(Proto.Transform.newBuilder().setPosition(
                                Proto.Position.newBuilder().setX(position.x).setY(position.y).setZ(position.z).build()).setRotation(
                                Proto.Rotation.newBuilder().setX(rotation.x).setY(rotation.y).setZ(rotation.z).setW(rotation.w).build()).build()).build();
                if (protoGameObject.lastSendObject == null) {
                    sendObject = sendObject.toBuilder().setActionEnum(Proto.ActionEnum.NEW_OBJECT).build();
                    protoServerSessionThread.sendObject(sendObject);
                    protoGameObject.lastSendObject = sendObject;
                } else if (!protoGameObject.lastSendObject.equals(sendObject)) {
                    protoServerSessionThread.sendObject(sendObject);
                    protoGameObject.lastSendObject = sendObject;
                }
            }
        }
    }

    @Override
    public boolean spawnUnitFromServerScreenByWaves() {
        return true;
    }

    @Override
    public void sendGameFieldVariables() {
        Logger.logFuncStart();
    }

    @Override
    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower);
        return null;
    }

    @Override
    public Tower createTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        return null;
    }

    @Override
    public boolean removeTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        return false;
    }

    public Unit createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, Cell exitCell, Player player) {
        Logger.logFuncStart("spawnCell:" + spawnCell, "destCell:" + destCell, "templateForUnit:" + templateForUnit, "exitCell:" + exitCell, "player:" + player);
        return null;
    }
}
