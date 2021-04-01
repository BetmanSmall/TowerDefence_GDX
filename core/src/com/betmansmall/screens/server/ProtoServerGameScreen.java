package com.betmansmall.screens.server;

import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.server.AuthServerThread;
import com.betmansmall.server.ProtoServerSessionThread;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.server.data.CreateUnitData;
import com.betmansmall.server.data.GameFieldVariablesData;
import com.betmansmall.server.data.RemoveTowerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.UnitsManagerData;
import com.betmansmall.utils.logging.Logger;

public class ProtoServerGameScreen extends GameScreen {
//    public AuthServerThread authServerThread;
    public ProtoServerSessionThread protoServerSessionThread;

    public ProtoServerGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);
        Logger.logFuncStart();

//        this.authServerThread = new AuthServerThread(this);
        this.protoServerSessionThread = new ProtoServerSessionThread(this);

//        this.authServerThread.start();
        this.protoServerSessionThread.start();
        super.initGameField();

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
//        this.authServerThread.dispose();
        this.sessionThread.dispose();
    }

//    @Override
//    public void render(float delta) {
//        super.render(delta);
//        Logger.logFuncStart();
//    }

    @Override
    public boolean spawnUnitFromServerScreenByWaves() {
        return true;
    }

    @Override
    public void sendGameFieldVariables() {
        sessionThread.sendObject(new SendObject(
                SendObject.SendObjectEnum.GAME_FIELD_VARIABLES_AND_MANAGERS_DATA,
                new GameFieldVariablesData(gameField),
                new UnitsManagerData(gameField.unitsManager)));
    }

    @Override
    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower);
        Tower tower = gameField.createTowerWithGoldCheck(buildX, buildY, templateForTower);
        if (tower != null) {
            sessionThread.sendObject(new SendObject(new BuildTowerData(tower)));
            return tower;
        }
        return null;
    }

    @Override
    public Tower createTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        Tower tower = super.createTower(buildX, buildY);
        if (tower != null) {
            sessionThread.sendObject(new SendObject(new BuildTowerData(tower)));
        }
        return null;
    }

    @Override
    public boolean removeTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        if (super.removeTower(buildX, buildY)) {
            sessionThread.sendObject(new SendObject(new RemoveTowerData(buildX, buildY, playersManager.getLocalPlayer())));
        }
        return false;
    }

    public Unit createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, Cell exitCell, Player player) {
        Unit unit = super.createUnit(spawnCell, destCell, templateForUnit, exitCell, player);
        if (unit != null) {
            sessionThread.sendObject(new SendObject(new CreateUnitData(spawnCell, destCell, templateForUnit, exitCell, player)));
        }
        return unit;
    }
}
