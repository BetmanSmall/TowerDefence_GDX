package com.betmansmall.screens.client;

import com.betmansmall.enums.SessionState;
import com.betmansmall.game.ClientSessionThread;
import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.util.logging.Logger;

public class ClientGameScreen extends GameScreen {
    public ClientSessionThread clientSessionThread;

    public ClientGameScreen(GameMaster gameMaster, Player player) {
        super(gameMaster);
        Logger.logFuncStart();

        playersManager.localServer = new Player(null, Player.Type.SERVER, 0);
        playersManager.localPlayer = player;

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
        }
        if (clientSessionThread.sessionState == SessionState.INITIALIZED) {
            super.render(delta);
        }
    }

    public void buildTowerFromServer(BuildTowerData buildTowerData, Player player) {
        Logger.logFuncStart("buildTowerData:" + buildTowerData, "player:" + player);
        TemplateForTower templateForTower = player.faction.getTemplateForTower(buildTowerData.templateName);
        gameField.createTower(buildTowerData.buildX, buildTowerData.buildY, templateForTower, player);
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
            clientSessionThread.sendObject(new SendObject(new BuildTowerData(tower)));
        }
        return tower;
    }
}
