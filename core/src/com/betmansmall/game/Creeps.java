package com.betmansmall.game;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class Creeps {
    Creep creeps[];
    int size;
    int amount;

    Creeps(int newSize) {
        creeps = null;
        createMass(newSize);
    }

    void createMass(int newSize) {
        if(creeps == null) {
            creeps = new Creep[newSize];
            this.size = newSize;
            this.amount = 0;
        } else {
            deleteMass();
            createMass(newSize);
        }
    }

    void  deleteMass() {
        if(creeps != null) {
            for(int k = 0; k < size; k++) {
                creeps[k] = null;
            }
            creeps = null;
        }
    }

    int getSize() {
        return size;
    }

    int getAmount() {
        return amount;
    }

    int getHP(int x, int y) {
        for(int k = 0; k < amount; k++) {
            int localX = creeps[k].coorByCellX;
            int localY = creeps[k].coorByCellY;

            if(localX == x && localY == y) {
                return creeps[k].hp;
            }
        }
        return 0;
    }

//    bool Creeps::attackCreep(int x, int y, int damage, Creep *creep);

    Creep getCreep(int number) {
        return creeps[number];
    }

    Creep getCreep(int x, int y) {
        for(int k = 0; k < amount; k++) {
            int localX = creeps[k].coorByCellX;
            int localY = creeps[k].coorByCellY;

            if(localX == x && localY == y) {
                return creeps[k];
            }
        }
        return null;
    }

    Creep createCreep(int coorByCellX, int coorByCellY) { //, int coorByMapX, int coorByMapY) { //, DefaultUnit* unit) {
        if(amount < size) {
            creeps[amount] = new Creep(amount+1, coorByCellX, coorByCellY);
            creeps[amount].hp = 100;
            creeps[amount].alive = true;
//            creeps[amount].preDeath = false;
//            creeps[amount].number = amount+1;
//            creeps[amount].coorByCellX = coorByCellX;
//            creeps[amount].coorByCellY = coorByCellY;
//            creeps[amount].coorByMapX = coorByMapX;
//            creeps[amount].coorByMapY = coorByMapY;
//            creeps[amount].lastX = coorByCellX;
//            creeps[amount].lastY = coorByCellY;

//        creeps[amount].speed = speed;
//        creeps[amount].type = type;

//            creeps[amount].animationCurrIter = 0;
//            creeps[amount].animationMaxIter = 0;

//            creeps[amount].pixmap = defaultPixmapForCreep;
//            creeps[amount].defUnit = unit;

            return creeps[amount++];
        }
        return null;
    }

//    void Creeps::setDefaulPixmapForCreep(QPixmap pixmap);

//    QPixmap Creeps::getCreepPixmap(int x, int y);
}
