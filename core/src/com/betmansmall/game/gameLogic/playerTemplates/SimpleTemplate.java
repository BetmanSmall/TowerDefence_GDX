package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.StringBuilder;

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
        sb.append("SimpleTemplate[");
        sb.append(toStringBasicParam());
        if (full) {
            sb.append("," + toStringProperties());
        }
        sb.append("]");
        return sb.toString();
    }
}
