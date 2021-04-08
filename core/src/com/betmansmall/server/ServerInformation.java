package com.betmansmall.server;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.GameType;
import com.betmansmall.server.data.GameServerNetworkData;
import com.betmansmall.server.data.GameSettingsData;
import com.betmansmall.server.data.NetworkPackage;
import com.betmansmall.server.data.PlayersManagerData;
import com.betmansmall.server.data.VersionData;

import java.net.InetSocketAddress;
import java.util.List;

public class ServerInformation {
    public InetSocketAddress inetSocketAddress;

    public String versionAndGitHash;
    public String mapPath;
    public GameType gameType;
    public String playersSize;

    public ServerInformation(String host, List<NetworkPackage> networkPackages) {
        for (NetworkPackage networkPackage : networkPackages) {
            if (networkPackage instanceof VersionData) {
                VersionData versionData = (VersionData)networkPackage;
                this.versionAndGitHash = versionData.version + "_" + versionData.gitHash;
            } else if (networkPackage instanceof GameSettingsData) {
                GameSettingsData gameSettingsData = (GameSettingsData)networkPackage;
                this.mapPath = gameSettingsData.mapPath;
                this.gameType = gameSettingsData.gameType;
            } else if (networkPackage instanceof PlayersManagerData) {
                PlayersManagerData playersManagerData = (PlayersManagerData)networkPackage;
                this.playersSize = String.valueOf(playersManagerData.players.size());
            } else if (networkPackage instanceof GameServerNetworkData) {
                GameServerNetworkData gameServerNetworkData = (GameServerNetworkData)networkPackage;
                this.inetSocketAddress = new InetSocketAddress(host, gameServerNetworkData.port);
            }
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("ServerInformation[");
        sb.append("inetSocketAddress:" + inetSocketAddress);
        sb.append(",versionAndGitHash:" + versionAndGitHash);
        sb.append(",mapPath:" + mapPath);
        sb.append(",gameType:" + gameType);
        sb.append(",playersSize:" + playersSize);
        sb.append("]");
        return sb.toString();
    }
}
