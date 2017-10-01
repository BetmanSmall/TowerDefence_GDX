package com.betmansmall.game.server;

import com.badlogic.gdx.utils.StringBuilder;

import java.io.Serializable;

/**
 * Created by betma on 10.09.2017.
 */

public class GameServerInfo implements Serializable {
    public String name = "ServerTTW";
    public int version = 1;

    public GameServerInfo(String name) {
        this.name = name;
//        this.version = version;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GameServerInfo[");
        sb.append("name:" + name);
        sb.append("," + "version:" + version);
        sb.append("]");
        return sb.toString();
    }
}
