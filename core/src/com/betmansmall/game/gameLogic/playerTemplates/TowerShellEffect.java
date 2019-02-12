package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by betma on 19.01.2017.
 */

public class TowerShellEffect {
    public enum ShellEffectEnum {
        FreezeEffect("FreezeEffect"),
        FireEffect("FireEffect");

        private final String text;
        ShellEffectEnum(final String text) {
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
            Gdx.app.error("ShellEffectEnum::getType()", "-- BadType:" + type);
            return null;
        }
    }

    public ShellEffectEnum shellEffectEnum;
    public float time;
    public float elapsedTime;
    public float damage;
    public float speed;
    public boolean used = false;

    public TowerShellEffect(ShellEffectEnum shellEffectEnum) {
        this.shellEffectEnum = shellEffectEnum;
        this.elapsedTime = 0;
    }

    public TowerShellEffect(TowerShellEffect towerShellEffect) {
        this.shellEffectEnum = towerShellEffect.shellEffectEnum;
        this.time = towerShellEffect.time;
        this.elapsedTime = towerShellEffect.elapsedTime;
        this.damage = towerShellEffect.damage;
        this.speed = towerShellEffect.speed;
    }

//    @Override
//    public boolean equals(Object object) {
//        if(object instanceof TowerShellEffect) {
//            TowerShellEffect towerShellEffect = (TowerShellEffect) object;
//            if (this.shellEffectEnum.equals(towerShellEffect.shellEffectEnum)) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//        return false;
//    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TowerShellEffect[");
        sb.append("shellEffectEnum:" + shellEffectEnum);
        sb.append(",time:" + time);
        sb.append(",elapsedTime:" + elapsedTime);
        sb.append(",damage:" + damage);
        sb.append(",speed:" + speed);
        sb.append(",used:" + used);
        sb.append("]");
        return sb.toString();
    }
}