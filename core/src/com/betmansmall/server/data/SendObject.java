package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;

import java.io.Serializable;
import java.util.ArrayList;

public class SendObject implements Serializable {
    public enum SendObjectEnum implements Serializable {
        SERVER_INFO_DATA,
        CONNECT_BY_CLIENT,
        CONNECT_BY_SERVER
    }
    public SendObjectEnum sendObjectEnum;
    public ArrayList<NetworkPackage> networkPackages;

    public SendObject(NetworkPackage ... networkPackages) {
        this.networkPackages = new ArrayList<>();
        for (NetworkPackage networkPackage : networkPackages) {
            if (networkPackage instanceof ServerInfoData) {
                sendObjectEnum = SendObjectEnum.SERVER_INFO_DATA;
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
        sb.append(",networkPackages.get(0):" + networkPackages.get(0));
        if (full) {
            for (int n = 1; n < networkPackages.size(); n++) {
                sb.append(",networkPackages.get(" + n + "):" + networkPackages.get(n));
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
