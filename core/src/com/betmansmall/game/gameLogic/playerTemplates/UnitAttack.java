package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by betma on 12.03.2019.
 */

public class UnitAttack {
    public enum AttackType {
        Melee("Melee"),
        RangeStand("RangeStand"), // not use for now
        RangeWalk("RangeWalk");

        private final String text;
        AttackType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static AttackType getType(String type) {
            for (AttackType t : AttackType.values()) {
                if (t.name().equals(type)) {
                    return t;
                }
            }
            Gdx.app.error("AttackType::getType()", "-- BadType:" + type);
            return null;
        }
    }

    public AttackType attackType;
    public float damage;
    public float range;

    public float speedProjectile;
    public float reload;

    public float elapsedTime;

    public UnitAttack(AttackType attackType) {
        this.attackType = attackType;
        this.elapsedTime = 0;
    }

    public UnitAttack(UnitAttack unitAttack) {
        this.attackType = unitAttack.attackType;
        this.damage = unitAttack.damage;
        this.range = unitAttack.range;
        this.speedProjectile = unitAttack.speedProjectile;
        this.reload = unitAttack.reload;
        this.elapsedTime = unitAttack.elapsedTime;
    }

//    @Override
//    public boolean equals(Object object) {
//        if(object instanceof UnitAttack) {
//            UnitAttack unitAttack = (UnitAttack) object;
//            if (this.attackType.equals(unitAttack.attackType)) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//        return false;
//    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UnitAttack[");
        sb.append("attackType:" + attackType);
        sb.append(",damage:" + damage);
        sb.append(",range:" + range);
        sb.append(",speedProjectile:" + speedProjectile);
        sb.append(",reload:" + reload);
        sb.append(",elapsedTime:" + elapsedTime);
        sb.append("]");
        return sb.toString();
    }
}
