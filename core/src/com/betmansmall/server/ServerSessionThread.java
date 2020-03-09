package com.betmansmall.server;

import com.badlogic.gdx.utils.Array;
import com.betmansmall.enums.SessionState;
import com.betmansmall.game.Player;
import com.betmansmall.game.SessionThread;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.screens.server.ServerGameScreen;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.server.data.CreateUnitData;
import com.betmansmall.server.data.GameFieldVariablesData;
import com.betmansmall.server.data.GameSettingsData;
import com.betmansmall.server.data.NetworkPackage;
import com.betmansmall.server.data.PlayerInfoData;
import com.betmansmall.server.data.PlayersManagerData;
import com.betmansmall.server.data.RemoveTowerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.TowersManagerData;
import com.betmansmall.server.data.UnitsManagerData;
import com.betmansmall.server.networking.TcpConnection;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSessionThread extends SessionThread {
    private ServerGameScreen serverGameScreen;
    private SessionSettings sessionSettings;
    private ServerSocket serverSocket;
    private Array<TcpConnection> connections;

    public ServerSessionThread(ServerGameScreen serverGameScreen) {
        Logger.logFuncStart();
        this.serverGameScreen = serverGameScreen;
        this.sessionSettings = serverGameScreen.game.sessionSettings;
        this.serverSocket = null;
        this.connections = new Array<>();
        this.sessionState = SessionState.INITIALIZATION;
        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        this.interrupt();
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (TcpConnection socket : connections) {
            socket.disconnect();
        }
    }

    @Override
    public void run() {
        Logger.logFuncStart();
//        try ( ServerSocket serverSocket = new ServerSocket(sessionSettings.gameServerPort) ) {
//            this.serverSocket = serverSocket;
        try {
            this.serverSocket = new ServerSocket(sessionSettings.gameServerPort);
            this.sessionState = SessionState.WAIT_CONNECTIONS;
            while (!this.isInterrupted()) {
                try {
                    new TcpConnection(this, serverSocket.accept());
                } catch (IOException exception) {
                    Logger.logError("exception:" + exception);
                }
            }
        } catch (IOException exception) {
            Logger.logError("exception:" + exception);
            serverGameScreen.game.removeTopScreen(); // TODO mb not good!
            throw new RuntimeException(exception);
//        } finally {
//            if (serverSocket != null) {
//                serverSocket.close();
//            }
        }
        Logger.logFuncEnd();
    }

    @Override
    public void onConnectionReady(TcpConnection tcpConnection) {
        Logger.logWithTime("tcpConnection:" + tcpConnection);
        connections.add(tcpConnection);
        NetworkPackage serverInfoData = new GameSettingsData(sessionSettings.gameSettings);
        NetworkPackage serverPlayerInfoData = new PlayerInfoData(serverGameScreen.playersManager.getLocalServer());

        tcpConnection.sendObject(new SendObject(SendObject.SendObjectEnum.GAME_SETTINGS_AND_SERVER_PLAYER_DATA, serverInfoData, serverPlayerInfoData));
    }

    @Override
    public void onReceiveObject(TcpConnection tcpConnection, SendObject sendObject) { // need create stackArray receive SendObjects and in other thread work with it;
        Logger.logInfo("tcpConnection:" + tcpConnection + ", sendObject:" + sendObject);
        if (sendObject.sendObjectEnum != null) {
//            if (sendObject.networkPackages != null && sendObject.networkPackages.size() != 0) {
//                for (NetworkPackage networkPackage : sendObject.networkPackages) {
//                    switch (sendObject.sendObjectEnum) {
//            FOR FUTURE
//                    }
//                }
//            }
            switch (sendObject.sendObjectEnum) {
                case GAME_FIELD_INITIALIZED: {
                    tcpConnection.sendObject(new SendObject(
                            SendObject.SendObjectEnum.GAME_FIELD_VARIABLES_AND_MANAGERS_DATA,
                            new GameFieldVariablesData(serverGameScreen.gameField),
                            new PlayersManagerData(serverGameScreen.playersManager),
                            new TowersManagerData(serverGameScreen.gameField.towersManager),
                            new UnitsManagerData(serverGameScreen.gameField.unitsManager)
                        )
                    );
                }
                case GAME_FIELD_VARIABLES_AND_MANAGERS_DATA: {
                    boolean hardOrSoftUpdate = false;
                    if (sendObject.networkPackages != null && sendObject.networkPackages.size() != 0) {
                        for (int n = sendObject.networkPackages.size()-1; n >= 0; n--) {
                            NetworkPackage networkPackage = sendObject.networkPackages.get(n);
                            if (networkPackage instanceof GameFieldVariablesData) {
                                GameFieldVariablesData gameFieldVariablesData = (GameFieldVariablesData) networkPackage;
                                Logger.logDebug("gameFieldVariablesData:" + gameFieldVariablesData);
                                serverGameScreen.gameField.updateGameFieldVariables(gameFieldVariablesData);

                                this.sendObject(new SendObject(
                                        SendObject.SendObjectEnum.GAME_FIELD_VARIABLES_AND_MANAGERS_DATA,
                                        new GameFieldVariablesData(serverGameScreen.gameField),
                                        new UnitsManagerData(serverGameScreen.gameField.unitsManager, hardOrSoftUpdate)
                                ));
                            } else if (networkPackage instanceof UnitsManagerData) {
                                UnitsManagerData unitsManagerData = (UnitsManagerData) networkPackage;
                                if (unitsManagerData.units.size() != serverGameScreen.gameField.unitsManager.units.size) {
                                    hardOrSoftUpdate = true;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        } else { // i think all time networkPackages.length == 1
            for (NetworkPackage networkPackage : sendObject.networkPackages) {
                if (networkPackage instanceof PlayerInfoData) {
                    PlayerInfoData playerInfoData = (PlayerInfoData) networkPackage;

                    Player player = serverGameScreen.playersManager.addPlayerByServer(tcpConnection, playerInfoData);
                    sessionState = SessionState.PLAYER_CONNECTED;

                    tcpConnection.sendObject(new SendObject(SendObject.SendObjectEnum.PLAYER_UPDATE_DATA, new PlayerInfoData(player)));
                    this.sendObject(new SendObject(SendObject.SendObjectEnum.PLAYER_CONNECTED_DATA, new PlayerInfoData(player)), tcpConnection);
                } else if (networkPackage instanceof BuildTowerData) {
                    BuildTowerData buildTowerData = (BuildTowerData) networkPackage;

                    Player player = serverGameScreen.playersManager.getPlayer(buildTowerData.playerID);
                    TemplateForTower templateForTower = player.faction.getTemplateForTower(buildTowerData.templateName);
//                TemplateForTower templateForTower = tcpConnection.player.faction.getTemplateForTower(buildTowerData.templateName);
                    Tower tower = serverGameScreen.gameField.createTowerWithGoldCheck(buildTowerData.buildX, buildTowerData.buildY, templateForTower, player);
                    serverGameScreen.gameField.rerouteAllUnits();

                    if (tower != null) {
                        this.sendObject(new SendObject(new BuildTowerData(tower)), tcpConnection);
                    }
                } else if (networkPackage instanceof RemoveTowerData) {
                    RemoveTowerData removeTowerData = (RemoveTowerData) networkPackage;

                    Player player = serverGameScreen.playersManager.getPlayer(removeTowerData.playerID);
                    serverGameScreen.gameField.removeTowerWithGold(removeTowerData.removeX, removeTowerData.removeY, player);
//                    serverGameScreen.gameField.rerouteAllUnits(); // need or not?

                    this.sendObject(new SendObject(removeTowerData), tcpConnection);
//                     or
//                    this.sendObject(networkPackage, tcpConnection);
//                    this.sendObject(new SendObject(new RemoveTowerData(removeTowerData.removeX, removeTowerData.removeY, player)), tcpConnection);
//                } else if (networkPackage instanceof GameFieldVariablesData) {
//                    GameFieldVariablesData gameFieldVariablesData = (GameFieldVariablesData) networkPackage;
//                    Logger.logDebug("gameFieldVariablesData:" + gameFieldVariablesData);
//                    serverGameScreen.gameField.updateGameFieldVariables(gameFieldVariablesData);
//
//                    this.sendObject(new SendObject(
//                            SendObject.SendObjectEnum.GAME_FIELD_VARIABLES_AND_MANAGERS_DATA,
//                            new GameFieldVariablesData(serverGameScreen.gameField),
//                            new UnitsManagerData(serverGameScreen.gameField.unitsManager)
//                    ));
                } else if (networkPackage instanceof CreateUnitData) {
                    CreateUnitData createUnitData = (CreateUnitData) networkPackage;
                    serverGameScreen.gameField.createUnit(createUnitData);
                    this.sendObject(new SendObject(createUnitData), tcpConnection);
                }
            }
        }
    }

    @Override
    public void onDisconnect(TcpConnection tcpConnection) {
        Logger.logInfo("tcpConnection:" + tcpConnection);
        connections.removeValue(tcpConnection, true);
        Player player = serverGameScreen.playersManager.getPlayerByConnection(tcpConnection);
        for (TcpConnection connection : connections) {
            connection.sendObject(new SendObject(SendObject.SendObjectEnum.PLAYER_DISCONNECTED_DATA, new PlayerInfoData(player)));
        }
        serverGameScreen.playersManager.removePlayer(player);
//        gameServer.playerDisconnect(tcpConnection);
    }

    @Override
    public void onException(TcpConnection tcpConnection, Exception exception) {
        Logger.logError("tcpConnection:" + tcpConnection + ", exception:" + exception);
//        exception.printStackTrace();
    }

    public synchronized void sendObject(final SendObject sendObject, TcpConnection tcpConnection) {
        for (TcpConnection connection : connections) {
            if (!connection.equals(tcpConnection) || connection != tcpConnection) {
                connection.sendObject(sendObject);
            }
        }
    }

    public synchronized void sendObject(final SendObject sendObject) {
        for (TcpConnection connection : connections) {
            connection.sendObject(sendObject);
        }
    }
}
