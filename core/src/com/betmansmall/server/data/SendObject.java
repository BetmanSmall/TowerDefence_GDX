package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;

import java.io.Serializable;
import java.util.ArrayList;

public class SendObject implements Serializable {
    public enum SendObjectEnum implements Serializable {
        SERVER_VERSION_AND_BASE_INFO_DATA,
        GAME_SETTINGS_AND_SERVER_PLAYER_DATA,

        GAME_FIELD_VARIABLES_AND_MANAGERS_DATA,
//        SOFT_UPDATE_UNITS_MANAGER,
//        HARD_UPDATE_UNITS_MANAGER,

        PLAYER_CONNECTED_DATA,
        PLAYER_UPDATE_DATA,
        PLAYER_DISCONNECTED_DATA,

        BUILD_TOWER_DATA,
        REMOVE_TOWER_DATA,

//        PAUSED_GAME_FIELD,
        GAME_FIELD_INITIALIZED,

        CREATE_UNIT_DATA,
    }

    public SendObjectEnum sendObjectEnum;
    public ArrayList<NetworkPackage> networkPackages;

    public SendObject(SendObjectEnum sendObjectEnum, NetworkPackage ... networkPackages) {
        this.sendObjectEnum = sendObjectEnum;
//        if (networkPackages.length != 0) {
            this.networkPackages = new ArrayList<>();
            for (NetworkPackage networkPackage : networkPackages) {
                this.networkPackages.add(networkPackage);
            }
//        }
    }

    public SendObject(NetworkPackage networkPackage) {
        this.networkPackages = new ArrayList<>();
        this.networkPackages.add(networkPackage);
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("SendObject[");
        sb.append("sendObjectEnum:" + sendObjectEnum);
        if (networkPackages != null) {
            sb.append(",networkPackages.size():" + networkPackages.size());
            if (full) {
                for (int n = 0; n < networkPackages.size(); n++) {
                    sb.append(",networkPackages[" + n + "]:" + networkPackages.get(n));
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
