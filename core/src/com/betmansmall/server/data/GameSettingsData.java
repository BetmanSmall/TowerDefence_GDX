package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.enums.GameType;
import com.betmansmall.game.GameSettings;

public class GameSettingsData implements NetworkPackage {
    public String mapPath;
    public GameType gameType;

    public GameSettingsData(GameSettings gameSettings) {
        this.mapPath = gameSettings.mapPath;
        this.gameType = gameSettings.gameType;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("GameSettingsData[");
        sb.append("mapPath:" + mapPath);
        sb.append(",gameType:" + gameType);
//        sb.append(",networkPackages.get(0):" + networkPackages.get(0));
//        if (full) {
//            for (int n = 1; n < networkPackages.size(); n++) {
//                sb.append(",networkPackages.get(" + n + "):" + networkPackages.get(n));
//            }
//        }
        sb.append("]");
        return sb.toString();
    }
}
