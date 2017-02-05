package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by betma on 19.01.2017.
 */

public class ShellEffectType {
    public enum ShellEffectEnum {
        FreezeEffect("FreezeEffect"),
        FireEffect("FireEffect");

        private final String text;

        private ShellEffectEnum(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static ShellEffectEnum getType(String type) {
            for (ShellEffectEnum t : ShellEffectEnum.values()) {
                if (t.name().equals(type)) {
                    return t;
                }
            }
            Gdx.app.error("ShellEffectEnum", "getType(" + type + "); -- Bad type!");
            return null;
        }
    }

    public ShellEffectEnum shellEffectEnum;
    public float time;
    public float elapsedTime;
    public float damage;
    public float speed;
    public boolean used = false;

    public ShellEffectType(ShellEffectEnum shellEffectEnum) {
        this.shellEffectEnum = shellEffectEnum;
        this.elapsedTime = 0;
    }

    public ShellEffectType(ShellEffectType shellEffectType) {
        this.shellEffectEnum = shellEffectType.shellEffectEnum;
        this.time = shellEffectType.time;
        this.elapsedTime = shellEffectType.elapsedTime;
        this.damage = shellEffectType.damage;
        this.speed = shellEffectType.speed;
    }

//    @Override
//    public boolean equals(Object object) {
//        if(object instanceof ShellEffectType) {
//            ShellEffectType shellEffectType = (ShellEffectType) object;
//            if (this.shellEffectEnum.equals(shellEffectType.shellEffectEnum)) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//        return false;
//    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ShellEffectType[");
        sb.append("shellEffectEnum:" + shellEffectEnum + ",");
        sb.append("time:" + time + ",");
        sb.append("elapsedTime:" + elapsedTime + ",");
        sb.append("damage:" + damage + ",");
        sb.append("speed:" + speed + ",");
        sb.append("damage:" + damage + ",");
        sb.append("]");
        return sb.toString();
    }
}