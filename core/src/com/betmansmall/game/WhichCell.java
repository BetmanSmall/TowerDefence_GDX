package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Андрей on 31.01.2016.
 */
public class WhichCell {
    int sizeFieldX, sizeFieldY;
    int sizeCellX, sizeCellY;
    float halfSizeCellX, halfSizeCellY;

    public WhichCell(int sizeFieldX, int sizeFieldY, int sizeCellX, int sizeCellY) {
        this.sizeFieldX = sizeFieldX;
        this.sizeFieldY = sizeFieldY;
        this.sizeCellX = sizeCellX;
        this.sizeCellY = sizeCellY;
        this.halfSizeCellX = sizeCellX/2;
        this.halfSizeCellY = sizeCellY/2;
    }

    public GridPoint2 whichCell(Vector3 touch) {
        Gdx.app.log("WhichCell::whichCell(" + touch + ")", "--");
        return whichCell(touch, 5);
    }

    public GridPoint2 whichCell(Vector3 touch, int map) {
        Gdx.app.log("WhichCell::whichCell(" + touch + "," + map + ")", "--");
        touch.x /= sizeCellX;
        touch.y = (touch.y - sizeCellY / 2) / sizeCellY + touch.x;
        touch.x -= touch.y - touch.x;
        Gdx.app.log("WhichCell::whichCell()", "-- new touch:" + touch);
        GridPoint2 cell = new GridPoint2(Math.abs((int) touch.x), Math.abs((int) touch.y));
        Gdx.app.log("WhichCell::whichCell()", "-- cell:" + cell);
        if (cell.x < sizeFieldX && cell.y < sizeFieldY) {
            if ( (map == 5) ||
                    (map == 1 && touch.x > 0 && touch.y < 0) ||
                    (map == 2 && touch.x > 0 && touch.y > 0) ||
                    (map == 3 && touch.x < 0 && touch.y > 0) ||
                    (map == 4 && touch.x < 0 && touch.y < 0) ) {
                return cell;
            }
        }
        return null;
    }

    // GOVNO CODE | Delete this piece of shit | Goodbye Andrey code! | СУКА НАХУЙ ПИЗДЕЦ БЛЯТЬ!!!!! бомбит очко
//    public GridPoint2 whichCell(GridPoint2 grafCoordinate) {
//        int halfSizeCellX = sizeCellX/2;
//        int halfSizeCellY = sizeCellY/2;
//        for(int tileX = 0; tileX < sizeFieldX; tileX++) {
//            for(int tileY = 0; tileY < sizeFieldY; tileY++) {
//                float posX, posY;
//                posX =  (tileX*halfSizeCellX) - (tileY*halfSizeCellX) - halfSizeCellX;
//                posY = -(tileX*halfSizeCellY) - (tileY*halfSizeCellY);
//                ArrayList<Vector2> tilePoints1 = new ArrayList<Vector2>();
//                tilePoints1.add(new Vector2(posX, posY));
//                tilePoints1.add(new Vector2(posX + halfSizeCellX, posY + halfSizeCellY));
//                tilePoints1.add(new Vector2(posX + sizeCellX, posY));
//                tilePoints1.add(new Vector2(posX + halfSizeCellX, posY - halfSizeCellY));
//                if(estimation(tilePoints1, grafCoordinate)) {
//                    GridPoint2 gameCoord = new GridPoint2(tileX, tileY);
////                    Gdx.app.log("WhichCell::whichCell(" + grafCoordinate + ")", "-- return:" + gameCoord + ":1");
//                    return gameCoord;
//                }
//                posX =  (tileX*halfSizeCellX) + (tileY*halfSizeCellX);
//                posY = -(tileX*halfSizeCellY) + (tileY*halfSizeCellY) + halfSizeCellY;
//                ArrayList<Vector2> tilePoints2 = new ArrayList<Vector2>();
//                tilePoints2.add(new Vector2(posX, posY));
//                tilePoints2.add(new Vector2(posX + halfSizeCellX, posY + halfSizeCellY));
//                tilePoints2.add(new Vector2(posX + sizeCellX, posY));
//                tilePoints2.add(new Vector2(posX + halfSizeCellX, posY - halfSizeCellY));
//                if(estimation(tilePoints2, grafCoordinate)) {
//                    GridPoint2 gameCoord = new GridPoint2(tileX, tileY);
////                    Gdx.app.log("WhichCell::whichCell(" + grafCoordinate + ")", "-- return:" + gameCoord + ":2");
//                    return gameCoord;
//                }
//                posX = (tileX*halfSizeCellX) - (tileY*halfSizeCellX) + halfSizeCellX;
//                posY = (tileX*halfSizeCellY) + (tileY*halfSizeCellY) + halfSizeCellY*2;
//                ArrayList<Vector2> tilePoints3 = new ArrayList<Vector2>();
//                tilePoints3.add(new Vector2(posX, posY));
//                tilePoints3.add(new Vector2(posX - halfSizeCellX, posY - halfSizeCellY));
//                tilePoints3.add(new Vector2(posX - sizeCellX, posY));
//                tilePoints3.add(new Vector2(posX - halfSizeCellX, posY + halfSizeCellY));
//                if(estimation(tilePoints3, grafCoordinate)) {
//                    GridPoint2 gameCoord = new GridPoint2(tileX, tileY);
////                    Gdx.app.log("WhichCell::whichCell(" + grafCoordinate + ")", "-- return:" + gameCoord + ":3");
//                    return gameCoord;
//                }
//                posX = -(tileX*halfSizeCellX) - (tileY*halfSizeCellX);
//                posY = -(tileX*halfSizeCellY) + (tileY*halfSizeCellY) + halfSizeCellY;
//                ArrayList<Vector2> tilePoints4 = new ArrayList<Vector2>();
//                tilePoints4.add(new Vector2(posX, posY));
//                tilePoints4.add(new Vector2(posX - halfSizeCellX, posY - halfSizeCellY));
//                tilePoints4.add(new Vector2(posX - sizeCellX, posY));
//                tilePoints4.add(new Vector2(posX - halfSizeCellX, posY + halfSizeCellY));
//                if(estimation(tilePoints4, grafCoordinate)) {
//                    GridPoint2 gameCoord = new GridPoint2(tileX, tileY);
////                    Gdx.app.log("WhichCell::whichCell(" + grafCoordinate + ")", "-- return:" + gameCoord + ":4");
//                    return gameCoord;
//                }
//            }
//        }
//        return null;
//    }
//
//    private boolean estimation(ArrayList<Vector2> mapPoints, GridPoint2 touch) {
//        int res = 0;
//        for(int i = 0; i < mapPoints.size()-1; i++)
//            res += getDelta(mapPoints.get(i).x, mapPoints.get(i).y, mapPoints.get(i + 1).x, mapPoints.get(i + 1).y, touch);
//        res += getDelta(mapPoints.get(3).x,mapPoints.get(3).y, mapPoints.get(0).x, mapPoints.get(0).y, touch);
//        if(res == 0) {
//            return false;
//        } else return true;
//    }
//
//    private float getDelta(float q,float w, float e, float r, GridPoint2 touch) {
//        int j = getOktant(q - touch.x, touch.y - w);
//        int h = getOktant(e - touch.x, touch.y - r);
//        if((h - j) > 4)
//            return h-j - 8;
//        else if((h-j) < -4)
//            return h-j + 8;
//        else if((h-j) == 4 || (h-j) == -4) {
//            int f = correlation(q, w, e, r, touch);
//            if(f == 0)
//                Gdx.app.log("WhichCell::getDelta()", "-- Точка находится на границе полигона.");
//            return f;
//        }
//        return h-j;
//    }
//
//    private int correlation(float q, float w, float e, float r, GridPoint2 touch) {
//        float result = opredelitel(r, w, 1, 1)*touch.x + opredelitel(1, 1, e, q)*touch.y + opredelitel(w,q,r,e);
//        if(result == 0) {
//            return 0;
//        } else if(result < 0) {
//            return -4;
//        } else if(result > 0)
//            return 4;
//        return 0;
//    }
//
//    private int getOktant(float X, float Y) {
//        if(0 <= Y && Y < X)
//            return 1;
//        else if(0 < X && X <= Y)
//            return 2;
//        else if(-Y < X && X <= 0)
//            return 3;
//        else if(0 < Y && Y <= -X)
//            return 4;
//        else if(X < Y && Y <= 0)
//            return 5;
//        else if(Y <= X && X < 0)
//            return 6;
//        else if(0 <= X && X < -Y)
//            return 7;
//        else if(-X <= Y && Y < 0)
//            return 8;
//        return 0;
//    }
//
//    private float opredelitel(float x, float s, float t, float f) {
//        return x*f - s*t;
//    }
}
