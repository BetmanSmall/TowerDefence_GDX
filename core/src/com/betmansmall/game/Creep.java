package com.betmansmall.game;

/**
 * Created by betmansmall on 22.09.2015.
 */
public class Creep {
    int hp;
    boolean alive;
    //    bool preDeath;
    int number;
//    int speed;
//    int type;

    int coorByCellX, coorByCellY;
//    int coorByMapX, coorByMapY;

//    int lastX, lastY;

//    Direction direction;

//    int animationCurrIter;
//    int animationMaxIter;

//    QPixmap pixmap;
//    std::vector<QPixmap> activePixmaps;

//    DefaultUnit* defUnit;

//    Creep() {
//
//    }

    Creep(int number, int coorByCellX, int coorByCellY) {
        this.number = number;
        this.coorByCellX = coorByCellX;
        this.coorByCellY = coorByCellY;
    }

//    QPixmap getAnimationInformation(int *lastX, int *lastY, int *animationCurrIter, int *animationMaxIter);
//    bool takeDamage(int damage);
}
