package com.betmansmall.game;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

/**
 * Created by betmansmall on 18.09.2015.
 */
public class Field {
    class Cell {
        int step;
        boolean empty;
        boolean busy;

//        bool spawn; // NEED Check!!!!! ?????  Check
//        bool exit; // NEED Check!!!!! ????? // NEED Check!!!!! ????? // NEED Check!!!!! ?????

        //    Tower* tower;
//        bool tower;
//        vector<Creep*> creeps;
        Creep creep;

//        QPixmap backgroundPixmap;
//        QPixmap busyPixmap;

        Texture backgrounTexture;
        Texture busyTexture;

        Cell() {
            this.step = 0;
            this.empty = true;
            this.busy = false;
        }
    }

    Cell field[];
    //    Towers towers;
    Creeps creeps;
//    Faction* faction1;

//    boolean creepSet;

    int gameOverLimitCreeps;
    int currentFinishedCreeps;

//    int sizeWidgetWidth;
//    int sizeWidgetHeight;

    int sizeX, sizeY;

    int mainCoorMapX, mainCoorMapY;
    int spaceWidget;
    int sizeCell;

    //    int mouseX, mouseY;
    int spawnPointX, spawnPointY;
    int exitPointX, exitPointY;
    boolean CIRCLET8 = true;

    Field() {
        field = null;
    }

    void createField(int newSizeX, int newSizeY) {
        if(field == null) {
            int size = newSizeX * newSizeY;

            this.field = new Cell[size];
            for(int k = 0; k < size; k++) { this.field[k] = new Cell(); }

            this.creeps = new Creeps(30);

//            this.creepSet = true;

            this.sizeX = newSizeX;
            this.sizeY = newSizeY;

            this.mainCoorMapX = 0;
            this.mainCoorMapY = 0;
            this.spaceWidget = 0;
            this.sizeCell = 32;

//            mouseX = -1;
//            mouseY = -1;
            spawnPointX = -1;
            spawnPointY = -1;
            exitPointX = -1;
            exitPointY = -1;
        } else {
//            deleteField();
//            createField(newSizeX, newSizeY);
        }
    }

    void deleteField() {
        if(field != null) {
            int size = getSizeX() * getSizeY();
            for(int k = 0; k < size; k++) {
                this.field[k] = null;
            }
            field = null;
//            towers.deleteField();
            creeps.deleteMass();
        }
    }

//    void Field::setFaction(Faction* faction);

    boolean createSpawnPoint(int num, int x, int y) {
        for(int k = 0; k < creeps.getAmount(); k++) {
            Creep creep = creeps.getCreep(k);
            int creepX = creep.coorByCellX;
            int creepY = creep.coorByCellY;
            clearCreep(creepX, creepY);
        }
        if(x == -1 && y == -1) {
            if(!isSetSpawnPoint())
                return false;
        } else {
            spawnPointX = x;
            spawnPointY = y;
//        field[sizeX*y+x].spawn = true; // BAGS!!!!!
//        field[sizeX*y+x].empty = false; // BAGS!!!!!
            clearBusy(x, y); // BAGS!!!!!
        }
        creeps.deleteMass();
        creeps.createMass(num);
        currentFinishedCreeps = 0;
        return true;
    }

    void createExitPoint(int x, int y) {
        exitPointX = x;
        exitPointY = y;
//    field[sizeX*y+x].exit = true; // BAGS!!!!!
//    field[sizeX*y+x].empty = false; // BAGS!!!!!
        clearBusy(x, y); // BAGS!!!!!
        waveAlgorithm(x, y);
    }

    int getSizeX() {
        return sizeX;
    }

    int getSizeY() {
        return sizeY;
    }

    void setMainCoorMap(int mainCoorMapX, int mainCoorMapY) {
        this.mainCoorMapX = mainCoorMapX;
        this.mainCoorMapY = mainCoorMapY;
    }

    void setSizeCell(int sizeCell) {
        this.sizeCell = sizeCell;
    }

    int getMainCoorMapX() {
        return mainCoorMapX;
    }

    int getMainCoorMapY() {
        return mainCoorMapY;
    }

    int getSpaceWidget() {
        return spaceWidget;
    }

    int getSizeCell() {
        return sizeCell;
    }

//    bool Field::towersAttack();

    void waveAlgorithm() {
        waveAlgorithm(-1, -1);
    }

    void waveAlgorithm(int x, int y) {
//        Log.d("TTW", "Field::waveAlgorithm(" + x + ", " + y + ");");
        if(x == -1 && y == -1) {
            if (isSetExitPoint()) {
                waveAlgorithm(exitPointX, exitPointY);
                return;
            }
        }

        if(!containBusy(x, y) && !containTower(x, y))
        {
            for(int tmpX = 0; tmpX < getSizeX(); tmpX++)
                for(int tmpY = 0; tmpY < getSizeY(); tmpY++)
                    clearStepCell(tmpX, tmpY);

            setStepCell(x, y, 1);

            waveStep(x, y, 1);
        }
    }

    void waveStep(int x, int y, int step) {
        if(CIRCLET8) {
            //------------3*3----------------
            boolean mass[][] = new boolean[3][3];
            int nextStep = step + 1;

            for (int tmpY = -1; tmpY < 2; tmpY++)
                for (int tmpX = -1; tmpX < 2; tmpX++)
                    mass[tmpX + 1][tmpY + 1] = setNumOfCell(x + tmpX, y + tmpY, nextStep);

            for (int tmpY = -1; tmpY < 2; tmpY++)
                for (int tmpX = -1; tmpX < 2; tmpX++)
                    if (mass[tmpX + 1][tmpY + 1])
                        waveStep(x + tmpX, y + tmpY, nextStep);
        } else {
            //------------2*2----------------
            boolean mass[] = new boolean[4];
            int nextStep = step + 1;
            int x1 = x - 1, x2 = x, x3 = x + 1;
            int y1 = y - 1, y2 = y, y3 = y + 1;

            mass[0] = setNumOfCell(x1, y2, nextStep);
            mass[1] = setNumOfCell(x2, y1, nextStep);
            mass[2] = setNumOfCell(x2, y3, nextStep);
            mass[3] = setNumOfCell(x3, y2, nextStep);

            if (mass[0])
                waveStep(x1, y2, nextStep);
            if (mass[1])
                waveStep(x2, y1, nextStep);
            if (mass[2])
                waveStep(x2, y3, nextStep);
            if (mass[3])
                waveStep(x3, y2, nextStep);
        }
    }

//    void Field::setMousePress(int x, int y);
//    bool Field::getMousePress(int x, int y);

    boolean isSetSpawnPoint() {
        return isSetSpawnPoint(-1, -1);
    }

    boolean isSetSpawnPoint(int x, int y) {
        if(spawnPointX != -1 && spawnPointY != -1) {
            if((x == spawnPointX && y == spawnPointY) || (x == -1 && y == -1)) {
                return true;
            }
        }
        return false;
    }

    boolean isSetExitPoint() {
        return isSetExitPoint(-1, -1);
    }

    boolean isSetExitPoint(int x, int y) {
        if(exitPointX != -1 && exitPointY != -1) {
            if((x == exitPointX && y == exitPointY) || (x == -1 && y == -1)) {
                return true;
            }
        }
        return false;
    }

    int stepAllCreeps() {
        boolean allDead = true;
        for(int k = 0; k < creeps.getAmount(); k++) {
            int result = stepOneCreep(k);
            if(result != -2)
                allDead = false;

            if(result == 1) {
                currentFinishedCreeps++;
                if(currentFinishedCreeps >= gameOverLimitCreeps)
                    return 1;
            }
            else if(result == -1)
                return -1;
        }

        if(allDead)
            return 2;
        else
            return 0;
    }

    int stepOneCreep(int creepId) {
//        Log.d("TTW", "Field::stepOneCreep(" + creepId + ")");
        Creep tmpCreep = creeps.getCreep(creepId);
        if(tmpCreep.alive) {
//            if(tmpCreep->animationCurrIter < tmpCreep->animationMaxIter) {
////            qDebug() << "tmpCreep->animationCurrIter: " << tmpCreep << "->" << tmpCreep->animationCurrIter;
//                tmpCreep->pixmap = tmpCreep->activePixmaps[tmpCreep->animationCurrIter++];
////            tmpCreep->animationCurrIter = tmpCreep->animationCurrIter+1;
//            }
//            else
//            {
            int currX = tmpCreep.coorByCellX;
            int currY = tmpCreep.coorByCellY;

            int exitX = currX, exitY = currY;

            int min = getNumStep(currX,currY);
            if(min == 1)
                return 1;
            if(min == 0)
                return -1;

            int defaultStep = min;
            //--------------Looking specific cell-----------------------
            for(int tmpY = -1; tmpY < 2; tmpY++)
                for(int tmpX = -1; tmpX < 2; tmpX++)
                    if(!(tmpX == 0 && tmpY == 0)) {
                        int num = getNumStep(currX + tmpX, currY + tmpY);
//                            Log.d("TTW", "stepOneCreep() -- num: " + num);
                        if(num <= min && num != 0) {
                            if(num == min) {
                                if( ((int) (Math.random()*2)) == 1) {
                                    exitX = currX + tmpX;
                                    exitY = currY + tmpY;
                                }
                            } else if(num == defaultStep-1) {
                                exitX = currX + tmpX;
                                exitY = currY + tmpY;
                                min = num;
                            }
                        }
                    }
            //-----------------------------------------------------------

            if(exitX != currX || exitY != currY)
            {
//                Log.d("TTW", "stepOneCreep() -- exitX: " + exitX + " exitY: " + exitY);
//                Log.d("TTW", "stepOneCreep() -- currX: " + currX + " currY: " + currY);

                clearCreep(currX, currY, tmpCreep);
//                    tmpCreep->lastX = currX;
//                    tmpCreep->lastY = currY;
                tmpCreep.coorByCellX = exitX;
                tmpCreep.coorByCellY = exitY;
                tmpCreep.number = min;
//                    tmpCreep->animationCurrIter = 0;
//
//                    if(exitX < currX && exitY < currY)
//                    {
//                        tmpCreep->animationMaxIter = tmpCreep->defUnit->walk_up_left.size();
//                        tmpCreep->activePixmaps = tmpCreep->defUnit->walk_up_left;
//                        tmpCreep->direction = DirectionUpLeft;
//                    }
//                    else if(exitX == currX && exitY < currY)
//                    {
//                        tmpCreep->animationMaxIter = tmpCreep->defUnit->walk_up.size();
//                        tmpCreep->activePixmaps = tmpCreep->defUnit->walk_up;
//                        tmpCreep->direction = DirectionUp;
//                    }
//                    else if(exitX > currX && exitY < currY)
//                    {
//                        tmpCreep->animationMaxIter = tmpCreep->defUnit->walk_up_right.size();
//                        tmpCreep->activePixmaps = tmpCreep->defUnit->walk_up_right;
//                        tmpCreep->direction = DirectionUpRight;
//                    }
//                    else if(exitX < currX && exitY == currY)
//                    {
//                        tmpCreep->animationMaxIter = tmpCreep->defUnit->walk_left.size();
//                        tmpCreep->activePixmaps = tmpCreep->defUnit->walk_left;
//                        tmpCreep->direction = DirectionLeft;
//                    }
//                    else if(exitX > currX && exitY == currY)
//                    {
//                        tmpCreep->animationMaxIter = tmpCreep->defUnit->walk_right.size();
//                        tmpCreep->activePixmaps = tmpCreep->defUnit->walk_right;
//                        tmpCreep->direction = DirectionRight;
//                    }
//                    else if(exitX < currX && exitY > currY)
//                    {
//                        tmpCreep->animationMaxIter = tmpCreep->defUnit->walk_down_left.size();
//                        tmpCreep->activePixmaps = tmpCreep->defUnit->walk_down_left;
//                        tmpCreep->direction = DirectionDownLeft;
//                    }
//                    else if(exitX == currX && exitY > currY)
//                    {
//                        tmpCreep->animationMaxIter = tmpCreep->defUnit->walk_down.size();
//                        tmpCreep->activePixmaps = tmpCreep->defUnit->walk_down;
//                        tmpCreep->direction = DirectionDown;
//                    }
//                    else if(exitX > currX && exitY > currY)
//                    {
//                        tmpCreep->animationMaxIter = tmpCreep->defUnit->walk_down_right.size();
//                        tmpCreep->activePixmaps = tmpCreep->defUnit->walk_down_right;
//                        tmpCreep->direction = DirectionDownRight;
//                    }
////                qDebug() << "tmpCreep->animationMaxIter: " << tmpCreep << "->" << tmpCreep->animationMaxIter;
//                    tmpCreep->pixmap = tmpCreep->activePixmaps[0];
//
                setCreep(exitX, exitY, tmpCreep);
            } else {
//                Log.d("TTW", "stepOneCreep() -- BAD");
                return 0;
            }
//            }
        }
//        else if(tmpCreep->preDeath)
//        {
//            if(tmpCreep->animationCurrIter < tmpCreep->animationMaxIter)
//            {
////            qDebug() << "tmpCreep->animationCurrIter: " << tmpCreep << "->" << tmpCreep->animationCurrIter;
//                tmpCreep->pixmap = tmpCreep->activePixmaps[tmpCreep->animationCurrIter++];
////            tmpCreep->animationCurrIter = tmpCreep->animationCurrIter+1;
//            }
//            else
//                tmpCreep->preDeath = false;
//        }
//        else
//            return -2;

        return 0;
    }

    int getNumStep(int x, int y) {
        if(x >= 0 && x < getSizeX()) {
            if(y >= 0 && y < getSizeY()) {
                if(!containBusy(x, y)) {
                    if(!containTower(x, y)) {
                        return getStepCell(x, y);
                    }
                }
            }
        }
        return 0;
    }

    int getStepCell(int x, int y) {
        return field[sizeX*y + x].step;
    }

    boolean setNumOfCell(int x, int y, int step) {
        if(x >= 0 && x < getSizeX()) {
            if(y >= 0 && y < getSizeY()) {
                if(!containBusy(x, y) && !containTower(x, y)) {
                    if(getStepCell(x, y) > step || getStepCell(x, y) == 0) {
                        setStepCell(x, y, step);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void setStepCell(int x, int y, int step) {
        field[sizeX*y + x].step = step;
    }

    void clearStepCell(int x, int y) {
        field[sizeX*y + x].step = 0;
    }

//    Creep* Field::getCreep(int x, int y);
//    std::vector<Creep*> Field::getCreeps(int x, int y);
//    int Field::getCreepHpInCell(int x, int y);
//    Creep* Field::getCreepWithLowHP(int x, int y);
//    std::vector<Tower*> Field::getAllTowers();

    boolean containEmpty(int x, int y) {
        return field[sizeX*y + x].empty;
    }

    boolean containBusy(int x, int y) {
        return field[sizeX*y + x].busy;
    }

    boolean containTower(int x, int y) {
        return false;
    }

    boolean containCreep(int x, int y) {
//        return containCreep(x, y, null);
        return field[sizeX * y + x].creep != null;
    }

    boolean containCreep(int x, int y, Creep creep) {
//        if(!field[sizeX*y + x].creeps.empty())
        if(field[sizeX*y + x].creep != null) {
//            int size = field[sizeX*y + x].creeps.size();
            if(creep == null) {
//                return size;
                return true;
            } else {
                return true;
//                for (int k = 0; k < size; k++) {
////                    if (field[sizeX * y + x].creeps[k] == creep) {
//                    if (field[sizeX * y + x].creep == creep) {
//                        return k + 1;
//                    }
//                }
            }
        }
//    if(field[sizeX*y + x].creep != NULL)
//        if(field[sizeX*y + x].creep->alive)
//            return true;
//        return 0;
        return false;
    }

    boolean setBusy(int x, int y) {//, QPixmap pixmap) {
//        Log.d("TTW", "setBusy(1) -- x: " + x + " y: " + y);
        if(containEmpty(x, y)) {
//            Log.d("TTW", "setBusy(2) -- x: " + x + " y: " + y);
            field[sizeX*y + x].busy = true;
            field[sizeX*y + x].empty = false;
//            if(!pixmap.isNull())
//                field[sizeX*y + x].busyPixmap = pixmap;
//            Log.d("TTW", "setBusy(3) -- x: " + x + " y: " + y);
            return true;
        } else {
//            Log.d("TTW", "setBusy(4) -- x: " + x + " y: " + y);
            return false;
        }
    }

//    bool Field::setTower(int x, int y);
//    bool Field::setTower(int x, int y, DefaultTower* defTower);
//    bool Field::setCreepInSpawnPoint();

    boolean setCreep(int x, int y) {
//        Log.d("TTW", "setCreep(1) -- x: " + x + " y: " + y);
        return setCreep(x, y, null);
    }
    boolean setCreep(int x, int y, Creep creep) {
//    if(x == -1 && y == -1)
//        return setCreep(spawnPointX, spawnPointY, creep);//, type);

        if(field[sizeX*y + x].empty || field[sizeX*y + x].creep != null) {
            if(creep == null) {
//                int coorByMapX = mainCoorMapX + spaceWidget + x*sizeCell;
//                int coorByMapY = mainCoorMapY + spaceWidget + y*sizeCell;
//
//                Creep creep;
//                if(creepSet)
//                    creep = creeps.createCreep(x, y, coorByMapX, coorByMapY, faction1->getDefaultUnitById(0));
//                else
//                    creep = creeps.createCreep(x, y, coorByMapX, coorByMapY, faction1->getDefaultUnitById(1));
//                creepSet = !creepSet;
//
//                if(creep == NULL)
//                    return false;
//                field[sizeX*y + x].creeps.push_back(creep);

//                Log.d("TTW", "setCreep(2) -- field[" + sizeX*y + x + "].creep: " + field[sizeX*y + x].creep);
                field[sizeX*y + x].creep = creeps.createCreep(x, y);
//                Log.d("TTW", "setCreep(3) -- field[" + sizeX*y + x + "].creep: " + field[sizeX*y + x].creep);
                if(field[sizeX*y + x].creep == null) {
                    return false;
                }
            } else {
//                field[sizeX*y + x].creeps.push_back(creep);
                field[sizeX * y + x].creep = creep;
            }
            field[sizeX*y + x].empty = false;
            return true;
        }
        return false;
    }

    boolean clearBusy(int x, int y) {
//        Log.d("TTW", "clearBusy(1) -- x: " + x + " y: " + y);
        if(!containEmpty(x, y)) {
//            Log.d("TTW", "clearBusy(2) -- x: " + x + " y: " + y);
            if(containBusy(x, y)) {
//                Log.d("TTW", "clearBusy(3) -- x: " + x + " y: " + y);
                field[sizeX*y + x].busy = false;
                field[sizeX*y + x].empty = true;
//                Log.d("TTW", "clearBusy(4) -- x: " + x + " y: " + y);
                return true;
            }
        }
//        Log.d("TTW", "clearBusy(5) -- x: " + x + " y: " + y);
        return false;
    }

    //    bool Field::clearTower(int x, int y);
    boolean clearCreep(int x, int y) {
        return clearCreep(x, y, null);
    }

    boolean clearCreep(int x, int y, Creep creep) {
        if(!field[sizeX*y + x].empty) {
            if(creep == null) {
//                field[sizeX*y + x].creeps.clear();
                field[sizeX*y + x].creep = null;
//            } else if(int num = containCreep(x, y, creep) {
//                field[sizeX*y + x].creeps.erase(field[sizeX*y + x].creeps.begin()+(num-1));
//            field[sizeX*y + x].creep = NULL;
            } else if(containCreep(x, y, creep)) {
                field[sizeX*y + x].creep = null;
            }

//            if(field[sizeX*y + x].creeps.empty())
            if(field[sizeX*y + x].creep == null) {
                field[sizeX * y + x].empty = true;
            }
            return true;
        }
        return false;
    }
//    bool Field::deleteTower(int x, int y);
//    void Field::setPixmapInCell(int x, int y, QPixmap pixmap);
//    void Field::setPixmapForCreep(QPixmap pixmap);
//    void Field::setPixmapForTower(QPixmap pixmap);
//    QPixmap Field::getBusyPixmapOfCell(int x, int y);
//    QPixmap Field::getPixmapOfCell(int x, int y);
//    QPixmap Field::getCreepPixmap(int x, int y);
//    QPixmap Field::getTowerPixmap(int x, int y);
}
