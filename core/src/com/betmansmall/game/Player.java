package com.betmansmall.game;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.PlayerStatus;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.playerTemplates.Faction;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.server.data.PlayerInfoData;
import com.betmansmall.server.networking.ProtoTcpConnection;
import com.betmansmall.server.networking.TcpConnection;

import protobuf.Proto;

public class Player {
    public ProtoTcpConnection protoTcpConnection;
    public ProtoGameObject hmdGameObject;
    public ProtoGameObject vrLeftHand;
    public ProtoGameObject vrRightHand;
    public Array<ProtoGameObject> gameObjects = new Array<>();
//    public Proto.SendObject sendObject;
    public Proto.SendObject lastSendObject = Proto.SendObject.getDefaultInstance();

    public TcpConnection connection; // only for ServerSessionThread class
    public PlayerType type;
    public PlayerStatus playerStatus;
    public String accountID;
    public Integer playerID;
    public String name;
    public Faction faction;
    public int gold = 10000;

    public Tower selectedTower;
    public Cell cellSpawnHero;
    public Cell cellExitHero;

    public int maxOfMissedUnits = 100;
    public int missedUnits = 0;

    public Player(String serverName, Faction faction) {
        this.connection = null;
        this.type = PlayerType.SERVER;
        this.playerStatus = null;
        this.accountID = "accID_SERVER";
        this.playerID = 0;
        this.name = serverName;
        this.faction = faction;

        this.selectedTower = null;
        this.cellSpawnHero = null;
        this.cellExitHero = null;
    }

    public Player(UserAccount userAccount, Faction faction) {
        this.connection = null;
        this.type = PlayerType.CLIENT;
        this.playerStatus = null;
        this.accountID = userAccount.accountID;
        this.playerID = type.ordinal();
//        if (type == PlayerType.SERVER) {
//            this.playerID = 0;
//        } else if (type == PlayerType.CLIENT) {
//            this.playerID = 1;
//        }
        this.name = userAccount.loginName;
        this.faction = faction;

        this.selectedTower = null;
        this.cellSpawnHero = null;
        this.cellExitHero = null;
    }

    public Player(PlayerInfoData playerInfoData, Faction faction) {
        init(null, playerInfoData, faction);
    }

    public Player(ProtoTcpConnection tcpConnection) { // only for ProtoServerSessionThread class
        init(tcpConnection);
    }

//    public Player(ProtoTcpConnection protoTcpConnection, Proto.SendObject sendObject) { // only for ProtoClientSessionThread class
//        init(protoTcpConnection);
//        updateData(sendObject);
//    }

    public Player(TcpConnection tcpConnection, PlayerInfoData playerInfoData, Faction faction) { // only for ServerSessionThread class
        init(tcpConnection, playerInfoData, faction);
    }

    private void init(ProtoTcpConnection protoTcpConnection) {
        this.protoTcpConnection = protoTcpConnection;
        this.type = PlayerType.CLIENT;
        this.playerStatus = PlayerStatus.LOADING;
    }

    private void init(TcpConnection tcpConnection, PlayerInfoData playerInfoData, Faction faction) {
        this.connection = tcpConnection;
        this.updateData(playerInfoData);
        this.faction = faction;
    }

    public void updateData(PlayerInfoData playerInfoData) {
        this.type = playerInfoData.type;
        this.playerStatus = playerInfoData.playerStatus;
        this.accountID = playerInfoData.accountID;
        this.playerID = playerInfoData.playerID;
        this.name = playerInfoData.name;
//        this.faction = playerInfoData.factionName;
        this.gold = playerInfoData.gold;
    }

    public void updateData(Proto.SendObject sendObject) {
        hmdGameObject.updateData(sendObject);
        if (sendObject.getOtherObjectsCount() == 2) {
            vrLeftHand.updateData(sendObject.getOtherObjects(0));
            vrRightHand.updateData(sendObject.getOtherObjects(1));
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Player[");
        sb.append("type:" + type);
        sb.append(",playerStatus:" + playerStatus);
        sb.append(",accountID:" + accountID);
        sb.append(",playerID:" + playerID);
        sb.append(",name:" + name);
        sb.append(",faction:" + faction);
        sb.append(",gold:" + gold);
        if (full) {
            sb.append(",selectedTower:" + selectedTower);
            sb.append(",cellSpawnHero:" + cellSpawnHero);
            sb.append(",cellExitHero:" + cellExitHero);

            sb.append(",maxOfMissedUnits:" + maxOfMissedUnits);
            sb.append(",missedUnits:" + missedUnits);
//            sb.append(",protoTcpConnection:" + protoTcpConnection);
//            sb.append(",tcpConnection:" + tcpConnection);
        }
        sb.append("]");
        return sb.toString();
    }
}
