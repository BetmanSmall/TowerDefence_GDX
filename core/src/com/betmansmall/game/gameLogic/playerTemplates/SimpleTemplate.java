package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by betma on 06.11.2018.
 */

public class SimpleTemplate extends Template {
    public SimpleTemplate(FileHandle templateFile) throws Exception {
        loadBasicTemplate(templateFile);
        basicValidate();
    }

    public String toString() {
        return toString(true);
    }

    public String toString(boolean full) {
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleTemplate:[");
        if(full) {
            sb.append(toStringProperties());
            sb.append(toStringBasicParam());
        }
        sb.append("]");
        return sb.toString();
    }
}
