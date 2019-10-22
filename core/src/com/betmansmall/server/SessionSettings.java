package com.betmansmall.server;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.game.GameSettings;

public class SessionSettings {
    public boolean localServer;
    public String host;
    public Integer port;

    public GameSettings gameSettings;

    public SessionSettings() {
        this.localServer = false;
        this.host = "localhost";
        this.port = 48999;

        this.gameSettings = new GameSettings();
    }

    public void dispose() {
//        this.gameSettings.dispose();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("SessionSettings[");
        sb.append("localServer:" + localServer);
        if (full) {
            sb.append(",host:" + host);
            sb.append(",port:" + port);
            sb.append(",gameSettings:" + gameSettings);
        }
        sb.append("]");
        return sb.toString();
    }
}
