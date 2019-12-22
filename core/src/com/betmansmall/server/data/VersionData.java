package com.betmansmall.server.data;

import com.badlogic.gdx.utils.StringBuilder;
import com.betmansmall.util.Version;
import com.betmansmall.util.logging.Logger;

public class VersionData implements NetworkPackage {
    public String version;
    public String gitHash;

    public VersionData(Version version) {
        Logger.logFuncStart("version:" + version);
        this.version = version.version;
        this.gitHash = version.gitHash;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("VersionData[");
        sb.append("version:" + version);
        if (full) {
            sb.append(",gitHash:" + gitHash);
        }
        sb.append("]");
        return sb.toString();
    }
}
