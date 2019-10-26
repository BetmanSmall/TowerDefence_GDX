package com.betmansmall.server;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.SessionType;
import com.betmansmall.game.GameSettings;

public class SessionSettings {
    public SessionType sessionType;
    public String host;
    public Integer port;

    public GameSettings gameSettings;

    public SessionSettings() {
        this.sessionType = SessionType.SERVER_STANDALONE;
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
        sb.append("sessionType:" + sessionType);
        if (full) {
            sb.append(",host:" + host);
            sb.append(",port:" + port);
            sb.append(",gameSettings:" + gameSettings);
        }
        sb.append("]");
        return sb.toString();
    }
}
