package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;

import java.io.Serializable;
import java.util.ArrayList;

public class SendObject implements Serializable {
    public enum SendObjectEnum implements Serializable {
        SERVER_INFO_DATA,
        PLAYER_INFO_DATA,
        UPDATE_PLAYER_INFO_DATA,
        PLAYER_DISCONNECTED,
        PLAYER_BUILD_TOWER,
        PLAYER_REMOVE_TOWER,
    }

    public SendObjectEnum sendObjectEnum;
    public ArrayList<NetworkPackage> networkPackages;

    public SendObject(SendObjectEnum sendObjectEnum, NetworkPackage ... networkPackages) {
        this.sendObjectEnum = sendObjectEnum;
        this.networkPackages = new ArrayList<>();
        for (NetworkPackage networkPackage : networkPackages) {
            this.networkPackages.add(networkPackage);
        }
    }

    public SendObject(NetworkPackage ... networkPackages) {
        this.networkPackages = new ArrayList<>();
        for (NetworkPackage networkPackage : networkPackages) {
            if (networkPackage instanceof ServerInfoData) {
                this.sendObjectEnum = SendObjectEnum.SERVER_INFO_DATA;
                this.networkPackages.add(networkPackage);
            } else if (networkPackage instanceof PlayerInfoData) {
                this.sendObjectEnum = SendObjectEnum.PLAYER_INFO_DATA;
                this.networkPackages.add(networkPackage);
            } else if (networkPackage instanceof BuildTowerData) {
                this.sendObjectEnum = SendObjectEnum.PLAYER_BUILD_TOWER;
                this.networkPackages.add(networkPackage);
            } else if (networkPackage instanceof RemoveTowerData) {
                this.sendObjectEnum = SendObjectEnum.PLAYER_REMOVE_TOWER;
                this.networkPackages.add(networkPackage);
            } else {
                this.networkPackages.add(networkPackage);
            }
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("SendObject[");
        sb.append("sendObjectEnum:" + sendObjectEnum);
        sb.append(",networkPackages[0]:" + networkPackages.get(0));
        if (full) {
            for (int n = 1; n < networkPackages.size(); n++) {
                sb.append(",networkPackages[" + n + "]:" + networkPackages.get(n));
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
