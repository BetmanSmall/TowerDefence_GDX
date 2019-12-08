package com.betmansmall.screens.server;

import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.server.ServerSessionThread;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.server.data.CreateUnitData;
import com.betmansmall.server.data.GameFieldVariablesData;
import com.betmansmall.server.data.RemoveTowerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.UnitsManagerData;
import com.betmansmall.util.logging.Logger;

public class ServerGameScreen extends GameScreen {
    public ServerSessionThread serverSessionThread;

    public ServerGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);
        Logger.logFuncStart();

        this.serverSessionThread = new ServerSessionThread(this);
        this.serverSessionThread.start();
        super.initGameField();

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        serverSessionThread.dispose();
    }

    @Override
    public boolean spawnUnitFromServerScreenByWaves() {
        return true;
    }

    @Override
    public void sendGameFieldVariables() {
        serverSessionThread.sendObject(new SendObject(
                SendObject.SendObjectEnum.GAME_FIELD_VARIABLES_AND_MANAGERS_DATA,
                new GameFieldVariablesData(gameField),
                new UnitsManagerData(gameField.unitsManager)));
    }

    @Override
    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower);
        Tower tower = gameField.createTowerWithGoldCheck(buildX, buildY, templateForTower);
        if (tower != null) {
            serverSessionThread.sendObject(new SendObject(new BuildTowerData(tower)));
            return tower;
        }
        return null;
    }

    public Tower towerToggle(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        Cell cell = gameField.getCell(buildX, buildY);
        if (cell != null) {
            if (cell.isEmpty()) {
                Tower tower = gameField.createTowerWithGoldCheck(buildX, buildY, gameField.factionsManager.getRandomTemplateForTowerFromAllFaction());
                if (tower != null) {
                    gameField.rerouteAllUnits();
                    serverSessionThread.sendObject(new SendObject(new BuildTowerData(tower)));
                }
                return tower;
            } else if (cell.getTower() != null) {
                Tower tower = cell.getTower();
                Player localPlayer = playersManager.getLocalPlayer();
                if (localPlayer.equals(tower.player)) {
                    gameField.removeTowerWithGold(buildX, buildY, playersManager.getLocalPlayer());
                    serverSessionThread.sendObject(new SendObject(new RemoveTowerData(buildX, buildY, playersManager.getLocalPlayer())));
                }
            }
        }
        return null;
    }

    public Unit createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, Cell exitCell, Player player) {
        Unit unit = super.createUnit(spawnCell, destCell, templateForUnit, exitCell, player);
        if (unit != null) {
            serverSessionThread.sendObject(new SendObject(new CreateUnitData(spawnCell, destCell, templateForUnit, exitCell, player)));
        }
        return unit;
    }
}
