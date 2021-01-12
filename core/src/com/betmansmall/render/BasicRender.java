package com.betmansmall.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.betmansmall.game.gameLogic.CameraController;
import com.betmansmall.maps.TmxMap;

public class BasicRender implements Disposable {
    protected ShapeRenderer shapeRenderer;

    protected final CameraController cameraController;
    protected TmxMap tmxMap;

    public BasicRender(final CameraController cameraController) {
        this.shapeRenderer = new ShapeRenderer();

        this.cameraController = cameraController;
        this.tmxMap = cameraController.tmxMap;
    }

    @Override
    public void dispose() {
//        this.cameraController = null;
        this.shapeRenderer.dispose();
    }

    public void render() {
        drawGrid();
        drawRedCenterDot();
    }

    protected void drawRedCenterDot() {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(0f, 0f, 5);
        shapeRenderer.end();
    }

    protected void drawGrid() {
        shapeRenderer.setProjectionMatrix(cameraController.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BROWN);
        if (!tmxMap.isometric) {
            float sizeCellX = cameraController.tmxMap.tileWidth;
//            float sizeCellY = cameraController.sizeCellY;
            if (cameraController.isDrawableGrid == 1 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < tmxMap.width+1; x++)
                    shapeRenderer.line(-(x*sizeCellX), 0, -(x*sizeCellX), -(sizeCellX*tmxMap.height));
                for (int y = 0; y < tmxMap.height+1; y++)
                    shapeRenderer.line(0, -(y*sizeCellX), -(sizeCellX*tmxMap.width), -(y*sizeCellX));
            }
            if (cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < tmxMap.width+1; x++)
                    shapeRenderer.line(x*sizeCellX, 0, x*sizeCellX, -(sizeCellX*tmxMap.height));
                for (int y = 0; y < tmxMap.height+1; y++)
                    shapeRenderer.line(0, -(y*sizeCellX), sizeCellX*tmxMap.width, -(y*sizeCellX));
            }
            if (cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < tmxMap.width+1; x++)
                    shapeRenderer.line(x*sizeCellX, 0, x*sizeCellX, sizeCellX*tmxMap.height);
                for (int y = 0; y < tmxMap.height+1; y++)
                    shapeRenderer.line(0, y*sizeCellX, sizeCellX*tmxMap.width, y*sizeCellX);
            }
            if (cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x < tmxMap.width+1; x++)
                    shapeRenderer.line(-(x*sizeCellX), 0, -(x*sizeCellX), sizeCellX*tmxMap.height);
                for (int y = 0; y < tmxMap.height+1; y++)
                    shapeRenderer.line(0, y*sizeCellX, -(sizeCellX*tmxMap.width), y*sizeCellX);
            }
        } else {
            float halfSizeCellX = cameraController.tmxMap.halfTileWidth;
            float halfSizeCellY = cameraController.tmxMap.halfTileHeight;
            float widthForTop = tmxMap.height * halfSizeCellX; // A - B
            float heightForTop = tmxMap.height * halfSizeCellY; // B - Top
            float widthForBottom = tmxMap.width * halfSizeCellX; // A - C
            float heightForBottom = tmxMap.width * halfSizeCellY; // C - Bottom
            if (cameraController.isDrawableGrid == 1 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= tmxMap.width; x++)
                    shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),-(widthForTop)+(halfSizeCellX*x),-(heightForTop)-(x*halfSizeCellY));
                for (int y = 0; y <= tmxMap.height; y++)
                    shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),(widthForBottom)-(halfSizeCellX*y),-(heightForBottom)-(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 2 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= tmxMap.width; x++)
                    shapeRenderer.line((halfSizeCellX*x),-(halfSizeCellY*x),(widthForTop)+(halfSizeCellX*x),(heightForTop)-(x*halfSizeCellY));
                for (int y = 0; y <= tmxMap.height; y++)
                    shapeRenderer.line((halfSizeCellX*y),(halfSizeCellY*y),(widthForBottom)+(halfSizeCellX*y),-(heightForBottom)+(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 3 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= tmxMap.height; x++) // WHT??? tmxMap.height check groundDraw
                    shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x),(widthForBottom)-(halfSizeCellX*x),(heightForBottom)+(x*halfSizeCellY));
                for (int y = 0; y <= tmxMap.width; y++) // WHT??? tmxMap.width check groundDraw
                    shapeRenderer.line((halfSizeCellX*y),(halfSizeCellY*y),-(widthForTop)+(halfSizeCellX*y),(heightForTop)+(halfSizeCellY*y));
            }
            if (cameraController.isDrawableGrid == 4 || cameraController.isDrawableGrid == 5) {
                for (int x = 0; x <= tmxMap.height; x++) // WHT??? tmxMap.height check groundDraw
                    shapeRenderer.line(-(halfSizeCellX*x),(halfSizeCellY*x),-(widthForBottom)-(halfSizeCellX*x),-(heightForBottom)+(x*halfSizeCellY));
                for (int y = 0; y <= tmxMap.width; y++) // WHT??? tmxMap.width check groundDraw
                    shapeRenderer.line(-(halfSizeCellX*y),-(halfSizeCellY*y),-(widthForTop)-(halfSizeCellX*y),(heightForTop)-(halfSizeCellY*y));
            }
        }
        shapeRenderer.end();
    }
}
