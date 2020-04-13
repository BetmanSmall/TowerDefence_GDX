package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.utils.logging.Logger;

public class GameServerNetworkData implements NetworkPackage {
    public String host;
    public Integer port;

    public GameServerNetworkData(String host, Integer port) {
        Logger.logFuncStart("host:" + host, "port:" + port);
        this.host = host;
        this.port = port;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameServerNetworkData[");
        sb.append("host:" + host);
        sb.append(",port:" + port);
        sb.append("]");
        return sb.toString();
    }
}
