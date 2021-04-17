package com.betmansmall.screens.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
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
        super.render(delta);
        Player player = protoController.player;
        if (player != null && player.gameObject != null) {
            player.gameObject.update(protoController);
            Proto.SendObject sendObject = Proto.SendObject.newBuilder()
                    .setIndex(player.playerID).setUuid(player.accountID)
                    .setActionEnum(Proto.ActionEnum.MOVE)
                    .setTransform(Proto.Transform.newBuilder().setPosition(
                            Proto.Position.newBuilder().setX(player.gameObject.position.x).setY(player.gameObject.position.y).setZ(player.gameObject.position.z).build()).setRotation(
                            Proto.Rotation.newBuilder().setX(player.gameObject.rotation.x).setY(player.gameObject.rotation.y).setZ(player.gameObject.rotation.z).setW(player.gameObject.rotation.w).build()).build()).build();
            if (!lastSendObject.equals(sendObject)) {
                protoServerSessionThread.sendObject(sendObject);
                lastSendObject = sendObject;
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
