package com.betmansmall.game.gameLogic;

import com.badlogic.gdx.Screen;

public class MapEditorCameraController extends CameraController {
    public MapEditorCameraController(Screen screen) {
        super(screen);
    }

//    @Override
//    public boolean tap(float x, float y, int count, int button) {
//        Logger.logFuncStart("x:" + x + " y:" + y + " count:" + count + " button:" + button);
//        return false;
//    }
//
//    @Override
//    public boolean longPress(float x, float y) {
//        Logger.logFuncStart("x:" + x + " y:" + y);
//        return false;
//    }
//
//    @Override
//    public boolean fling(float velocityX, float velocityY, int button) {
//        Logger.logFuncStart("velocityX:" + velocityX + " velocityY:" + velocityY + " button:" + button);
//        return false;
//    }
//
//    @Override
//    public boolean pan(float x, float y, float deltaX, float deltaY) {
//        Logger.logFuncStart("x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
////        if (camera.position.x + -deltaX * camera.zoom < MAX_DESTINATION_X && camera.position.x + -deltaX * camera.zoom > 0)
//        camera.position.add(-deltaX * camera.zoom, 0, 0);
////        if (Math.abs(camera.position.y + deltaY * camera.zoom) < MAX_DESTINATION_Y)
//        camera.position.add(0, deltaY * camera.zoom, 0);
//        camera.update();
//        return false;
//    }
//
//    @Override
//    public boolean panStop(float x, float y, int pointer, int button) {
//        Logger.logFuncStart("x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
//        return false;
//    }
//
//    @Override
//    public boolean zoom(float initialDistance, float distance) {
//        Logger.logFuncStart("initialDistance:" + initialDistance + " distance:" + distance);
//        float ratio = initialDistance / distance;
//        float newZoom = initialScale * ratio;
//        if (newZoom < zoomMax && newZoom > zoomMin) {
//            camera.zoom = newZoom;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
//        Logger.logFuncStart("initialPointer1:" + initialPointer1 + " initialPointer2:" + initialPointer2 + " pointer1:" + pointer1 + " pointer2:" + pointer2);
//        return false;
//    }
//
//    @Override
//    public void pinchStop() {
//        Logger.logFuncStart();
//    }
//
//    @Override
//    public boolean keyDown(int keycode) {
//        return false;
//    }
//
//    @Override
//    public boolean keyUp(int keycode) {
//        return false;
//    }
//
//    @Override
//    public boolean keyTyped(char character) {
//        return false;
//    }
//
//    @Override
//    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        return false;
//    }
//
//    @Override
//    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
//        return false;
//    }
//
//    @Override
//    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        return false;
//    }
//
//    @Override
//    public boolean mouseMoved(int screenX, int screenY) {
//        return false;
//    }
//
//    @Override
//    public boolean scrolled(int amount) {
//        Logger.logFuncStart("camera.zoom:" + camera.zoom);
//        super.scrolled(amount);
//        if (camera != null) {
//            if (amount > 0) {
//                if (camera.zoom <= zoomMax)
//                    camera.zoom += 0.1f;
//            } else if (amount < 0) {
//                if (camera.zoom >= zoomMin)
//                    camera.zoom -= 0.1f;
//            }
//            camera.update();
//        }
//        Logger.logFuncEnd("camera.zoom:" + camera.zoom);
//        return false;
//    }
}
