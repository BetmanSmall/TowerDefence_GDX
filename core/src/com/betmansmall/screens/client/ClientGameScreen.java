package com.betmansmall.screens.client;

import com.betmansmall.enums.SessionState;
import com.betmansmall.game.ClientSessionThread;
import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.server.data.GameFieldData;
import com.betmansmall.server.data.RemoveTowerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.util.logging.Logger;

public class ClientGameScreen extends GameScreen {
    public ClientSessionThread clientSessionThread;

    public ClientGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);
        Logger.logFuncStart();

        this.clientSessionThread = new ClientSessionThread(this);
        clientSessionThread.start();

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        super.dispose();
        clientSessionThread.dispose();
    }

    @Override
    public void render(float delta) {
        if (clientSessionThread.sessionState == SessionState.RECEIVED_SERVER_INFO_DATA) {
            this.initGameField();
            clientSessionThread.sessionState = SessionState.INITIALIZED;
            clientSessionThread.sendObject(new SendObject(SendObject.SendObjectEnum.GAME_FIELD_INITIALIZED));
        }
        if (clientSessionThread.sessionState == SessionState.INITIALIZED) {
            super.render(delta);
        }
    }

    @Override
    public void sendGameFieldVariables() {
        clientSessionThread.sendObject(new SendObject(new GameFieldData(gameField)));
    }

    @Override
    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower);
        Tower tower = gameField.createTowerWithGoldCheck(buildX, buildY, templateForTower);
        if (tower != null) {
            clientSessionThread.sendObject(new SendObject(new BuildTowerData(tower)));
            return tower;
        }
        return null;
    }

    @Override
    public Tower towerToggle(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        Cell cell = gameField.getCell(buildX, buildY);
        if (cell != null) {
            if (cell.isEmpty()) {
                Tower tower = gameField.createTowerWithGoldCheck(buildX, buildY, gameField.factionsManager.getRandomTemplateForTowerFromAllFaction());
                if (tower != null) {
                    gameField.rerouteAllUnits();
                    clientSessionThread.sendObject(new SendObject(new BuildTowerData(tower)));
                }
                return tower;
            } else if (cell.getTower() != null) {
                Tower tower = cell.getTower();
                Player localPlayer = playersManager.getLocalPlayer();
                if (localPlayer.equals(tower.player)) {
                    gameField.removeTowerWithGold(buildX, buildY, playersManager.getLocalPlayer());
                    clientSessionThread.sendObject(new SendObject(new RemoveTowerData(buildX, buildY, playersManager.getLocalPlayer())));
                }
            }
        }
        return null;
    }
}
