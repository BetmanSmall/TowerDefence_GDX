package com.betmansmall.screens.server;

import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.server.ServerSessionThread;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.util.logging.Logger;

public class ServerGameScreen extends GameScreen {
    public ServerSessionThread serverSessionThread;

    public ServerGameScreen(GameMaster gameMaster, Player player) {
        super(gameMaster, player);
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
    public Tower buildTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        Player player = playersManager.localPlayer;
        return tryCreateTower(buildX, buildY, player.faction.getTemplateForTowers().random(), player);
    }

    @Override
    public Tower tryCreateTower(int buildX, int buildY, TemplateForTower templateForTower, Player player) {
        Tower tower = gameField.createTowerWithGoldCheck(buildX, buildY, templateForTower, player);
        if (tower != null) {
            serverSessionThread.sendObjectToAll(new SendObject(new BuildTowerData(tower)));
        }
        return tower;
    }
}
