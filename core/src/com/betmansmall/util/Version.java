package com.betmansmall.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.betmansmall.util.logging.Logger;

import java.io.IOException;

public class Version {
    public String version;
    public String date;
    public String gitHash;
    private String versionAndHash;

    public Version() {
        Logger.logFuncStart();
        try {
            FileHandle file = Gdx.files.internal("version.properties");

            ObjectMap<String, String> map = new ObjectMap<>();
            PropertiesUtils.load(map, file.reader());

            version = map.get("version");
            date = map.get("date");
            gitHash = map.get("gitHash");
            versionAndHash = version + "_" + gitHash;
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getVersionAndHash() {
        return versionAndHash;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("Version[");
        sb.append("version:" + version);
        if (full) {
            sb.append(",date:" + date);
            sb.append(",gitHash:" + gitHash);
        }
        sb.append("]");
        return sb.toString();
    }
}

