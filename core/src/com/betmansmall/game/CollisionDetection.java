package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * Created by Андрей on 31.01.2016.
 */
public class CollisionDetection {
    public CollisionDetection() {
    }
    public boolean estimation(ArrayList<Vector2> mapPoints, Vector3 touch) {
        int res = 0;
        for(int i=0;i<mapPoints.size()-1;i++)
            res += getDelta(mapPoints.get(i).x, mapPoints.get(i).y, mapPoints.get(i + 1).x, mapPoints.get(i + 1).y, touch);
        res += getDelta(mapPoints.get(3).x,mapPoints.get(3).y, mapPoints.get(0).x, mapPoints.get(0).y, touch);
        if (res == 0) {
            return false;
        }
        else
            return true;
    }
    private float getDelta(float q,float w, float e, float r, Vector3 touch) {
        int j = getOktant(q - touch.x, touch.y - w);
        int h = getOktant(e - touch.x, touch.y - r);
        if ((h - j) > 4)
            return  h-j - 8;
        else if ((h-j) < -4)
            return  h-j + 8;
        else if ((h-j) == 4 || (h-j) == -4){
            int f = correlation(q, w, e, r, touch);
            if(f == 0)
                Gdx.app.log("Точка находится на границе полигона","");
            return f;
        }
        return h-j;
    }

    private int correlation(float q,float w, float e, float r, Vector3 touch) {
        float result = opredelitel(r,w,1,1)*touch.x + opredelitel(1, 1, e, q)*touch.y + opredelitel(w,q,r,e);
        if(result == 0){
            return 0;
        } else if(result < 0){
            return -4;
        } else if(result > 0)
            return 4;
        return 0;
    }

    private int getOktant(float X, float Y) {
        if(0 <= Y && Y < X) {
            return 1;
        }
        else if(0 < X && X <= Y) {
            return 2;
        }
        else if(-Y < X && X <= 0) {
            return 3;
        }
        else if(0 < Y && Y <= -X) {
            return 4;
        }
        else if(X < Y && Y <= 0) {
            return 5;
        }
        else if(Y <= X && X < 0) {
            return 6;
        }
        else if(0 <= X && X < -Y) {
            return 7;
        }
        else if(-X <= Y && Y < 0) {
            return 8;
        }
        return 0;
    }

    private float opredelitel(float x, float s, float t, float f) {
        return x*f - s*t;
    }

}
