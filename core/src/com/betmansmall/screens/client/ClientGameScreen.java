package com.betmansmall.screens.client;

import com.betmansmall.enums.SessionState;
import com.betmansmall.game.ClientSessionThread;
import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.server.data.CreateUnitData;
import com.betmansmall.server.data.GameFieldVariablesData;
import com.betmansmall.server.data.RemoveTowerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.server.data.UnitsManagerData;
import com.betmansmall.utils.logging.Logger;

public class ClientGameScreen extends GameScreen {
    public ClientGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);
        Logger.logFuncStart();

        this.sessionThread = new ClientSessionThread(this);
        this.sessionThread.start();

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        this.sessionThread.dispose();
    }

    @Override
    public boolean spawnUnitFromServerScreenByWaves() {
        return false;
    }

    @Override
    public void render(float delta) {
        if (sessionThread.sessionState == SessionState.RECEIVED_SERVER_INFO_DATA) {
            this.initGameField();
            sessionThread.sendObject(new SendObject(SendObject.SendObjectEnum.GAME_FIELD_INITIALIZED));
            sessionThread.sessionState = SessionState.INITIALIZED;
        }
        if (sessionThread.sessionState == SessionState.INITIALIZED) {
            super.render(delta);
        }
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
