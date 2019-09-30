package com.betmansmall.game.gameLogic.playerTemplates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Created by betma on 12.03.2019.
 */

public class UnitAttack {
    public enum AttackType {
        Melee("Melee"), // attack to die
//        RangeStand("RangeStand"), // attack to die
        Range("Range"); // walk and attack

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
    public float attackSpeed;

    public float speedProjectile;
    public float reload;
    public boolean stackInOneCell;
    public boolean stayToDie;

    public float ammoSize; // need transfer to Weapon Or Unit Attack template and other
    public float ammoSpeed;
    public float animationTime;
    public UnitAttack defUnitAttack;

    public float elapsedTimeRecharge;
    public float elapsedTimeAttacked;
    public boolean attacked;
    public Circle circle;
//    public Circle circle1;
//    public Circle circle2;
//    public Circle circle3;
//    public Circle circle4;

    public UnitAttack(AttackType attackType) {
        this.attackType = attackType;
        this.elapsedTimeRecharge = 0;
        this.elapsedTimeAttacked = 0;
        this.attacked = false;
    }

    public UnitAttack(UnitAttack unitAttack) { // mb need create TemplateUnitAttack
        if (unitAttack != null) {
            this.attackType = unitAttack.attackType;
            this.damage = unitAttack.damage;
            this.range = unitAttack.range;
            this.attackSpeed = unitAttack.attackSpeed;

            this.speedProjectile = unitAttack.speedProjectile;
            this.reload = unitAttack.reload;
            this.stackInOneCell = unitAttack.stackInOneCell;
            this.stayToDie = unitAttack.stayToDie;

            this.ammoSize = unitAttack.ammoSize;
            this.ammoSpeed = unitAttack.ammoSpeed;
            this.animationTime = unitAttack.animationTime;
            this.defUnitAttack = unitAttack;

            this.elapsedTimeRecharge = unitAttack.elapsedTimeRecharge;
            this.elapsedTimeAttacked = unitAttack.elapsedTimeAttacked;
            this.attacked = unitAttack.attacked;
            if (attackType != null && attackType == AttackType.Range) {
                this.circle = new Circle(0, 0, range);
                Gdx.app.log("UnitAttack::UnitAttack()", "-- circle:" + circle);
//                this.circle1 = new Circle(0, 0, range);
//                this.circle2 = new Circle(0, 0, range);
//                this.circle3 = new Circle(0, 0, range);
//                this.circle4 = new Circle(0, 0, range);
            } else {
                Gdx.app.log("UnitAttack::UnitAttack()", "-- attackType:" + attackType);
            }
//        } else {
//            throw new Exception("UnitAttack::UnitAttack(); -- unitAttack:" + unitAttack);
        }
    }

//    public void setRange(float range) {
//        this.range = range;
//        if (attackType != null && attackType == AttackType.Range) {
//            this.circle1 = new Circle(0, 0, range);
//            this.circle2 = new Circle(0, 0, range);
//            this.circle3 = new Circle(0, 0, range);
//            this.circle4 = new Circle(0, 0, range);
//        } else {
//            Gdx.app.log("UnitAttack::setRange()", "-- attackType:" + attackType);
//        }
//    }

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
        sb.append(",elapsedTimeRecharge:" + elapsedTimeRecharge);
        sb.append(",elapsedTimeAttacked:" + elapsedTimeAttacked);
        sb.append(",attacked:" + attacked);
        sb.append(",circle:" + circle);
        sb.append(",ammoSize:" + ammoSize);
        sb.append(",ammoSpeed:" + ammoSpeed);
        sb.append(",animationTime:" + animationTime);
        sb.append("]");
        return sb.toString();
    }
}
