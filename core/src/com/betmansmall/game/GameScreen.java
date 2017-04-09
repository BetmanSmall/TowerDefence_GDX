package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.betmansmall.game.GameScreenInteface.DeviceSettings;
import com.betmansmall.game.GameScreenInteface.GameInterface;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.UnderConstruction;

public class GameScreen implements Screen {
    private BitmapFont bitmapFont = new BitmapFont();
    private DeviceSettings deviceSettings = new DeviceSettings();

    private float currentDuration; // LOL GOVNE code
    private float MAX_DURATION_FOR_DEFEAT_SCREEN = 1f;

    private Texture defeatScreen;

    private GameInterface gameInterface;
    private GameField gameField;

    private CameraController cameraController;

    class CameraController implements GestureListener {
        public OrthographicCamera camera;
        private float zoomMax = 50f; //max size
        private float zoomMin = 0.2f; // 2x zoom
        public float borderLeftX, borderRightX;
        public float borderUpY, borderDownY;

        float velX, velY;
        boolean flinging = false; // Что бы не пересикалось одно действие с другим действием (с) Андрей А
        float initialScale = 2f;
        boolean lastCircleTouched = false;

        public CameraController(OrthographicCamera camera, float maxZoom, float minZoom) {//, float borderLeftX, float borderRightX, float borderUpY, float borderDownY) {
            Gdx.app.log("CameraController::CameraController()", "-- camera:" + camera + " maxZoom:" + maxZoom + " minZoom:" + minZoom);
            this.camera = camera;
            this.zoomMax = maxZoom;
            this.zoomMin = minZoom;
//            this.borderLeftX = borderLeftX;
//            this.borderRightX = borderRightX;
//            this.borderUpY = borderUpY;
//            this.borderDownY = borderDownY;
        }

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            Gdx.app.log("CameraController::touchDown()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
            flinging = false;
            initialScale = camera.zoom;
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            Gdx.app.log("CameraController::tap()", "-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
//          CHECK IF THE PAUSE BUTTON IS TOUCHED //CHECK IF THE TOWER BUTTON IS TOUCHED
            if (gameInterface.getCreepsRoulette().isButtonTouched(x, y) || gameInterface.getTowersRoulette().isButtonTouched(x, y)) {
                return false;
            }

            Vector3 touch = new Vector3(x, y, 0);
            camera.unproject(touch);
            GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch);
            if (cellCoordinate != null && gameField.getUnderConstruction() == null) {
                if (button == 0) {
                    gameField.towerActions(cellCoordinate.x, cellCoordinate.y);
                } else if (button == 1) {
                    gameField.removeTower(cellCoordinate.x, cellCoordinate.y);
//                  gameField.prepareBuildTower(cellCoordinate.x, cellCoordinate.y);
//              } else if(button == 2) {
//                  gameField.createCreep(cellCoordinate.x, cellCoordinate.y);
                } else if (button == 3) {
                    gameField.setSpawnPoint(cellCoordinate.x, cellCoordinate.y);
                } else if (button == 4) {
                    gameField.setExitPoint(cellCoordinate.x, cellCoordinate.y);
                }
            }
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            Gdx.app.log("CameraController::longPress()", "-- x:" + x + " y:" + y);
//          gameField.createSpawnTimerForCreeps();
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            Gdx.app.log("CameraController::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY + " button:" + button);
            if (!lastCircleTouched) {
                flinging = true;
                velX = camera.zoom * velocityX * 0.5f;
                velY = camera.zoom * velocityY * 0.5f;
            }
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            Vector3 touch = new Vector3(x, y, 0);
            camera.unproject(touch);
//            Gdx.app.log("CameraController::pan()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
//            Gdx.app.log("CameraController::pan(1)", "-- x:" + camera.position.x + " y:" + camera.position.y);
//            Gdx.app.log("CameraController::pan(2)", "-- x:" + touch.x + " y:" + touch.y);
            if (gameInterface.getTowersRoulette().makeRotation(x, y, deltaX, deltaY) && deviceSettings.getDevice() == "android") {
                lastCircleTouched = true;
                return true;
            }
            lastCircleTouched = false;
            if (gameField.getUnderConstruction() == null || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                float newCameraX = camera.position.x + (-deltaX*camera.zoom);
                float newCameraY = camera.position.y + ( deltaY*camera.zoom);
                if (cameraController.borderLeftX < newCameraX && newCameraX < cameraController.borderRightX &&
                        cameraController.borderUpY > newCameraY && newCameraY > cameraController.borderDownY) {
                    camera.position.set(newCameraX, newCameraY, 0.0f);
                }
            } else {
                int space = 50;
                if(x < space) {
                    camera.position.add(-5, 0, 0);
                }
                if(x > Gdx.graphics.getWidth()-space) {
                    camera.position.add(5, 0, 0);
                }
                if(y < space) {
                    camera.position.add(0, 5, 0);
                }
                if(y > Gdx.graphics.getHeight()-space) {
                    camera.position.add(0, -5, 0);
                }
            }
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            Gdx.app.log("CameraController::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            Gdx.app.log("CameraController::zoom()", "-- initialDistance:" + initialDistance + " distance:" + distance + " initialScale:" + initialScale);
            float ratio = initialDistance / distance;
            float newZoom = initialScale * ratio;
            if (newZoom < zoomMax && newZoom > zoomMin) {
                camera.zoom = newZoom;
            }
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            Gdx.app.log("CameraController::pinch()", "-- initialPointer1:" + initialPointer1 + " initialPointer2:" + initialPointer2 + " pointer1:" + pointer1 + " pointer2:" + pointer2);
            return false;
        }

        public void update() {
//            Gdx.app.log("CameraController::update()", "-- ");
            try {
                if (gameField.getUnderConstruction() == null) {
                    if (flinging) {
                        velX *= 0.98f;
                        velY *= 0.98f;
                        float newCameraX = camera.position.x + (-velX*Gdx.graphics.getDeltaTime());
                        float newCameraY = camera.position.y + ( velY*Gdx.graphics.getDeltaTime());
                        if (cameraController.borderLeftX < newCameraX && newCameraX < cameraController.borderRightX &&
                                cameraController.borderUpY > newCameraY && newCameraY > cameraController.borderDownY) {
                            camera.position.set(newCameraX, newCameraY, 0.0f);
                        }
                        if (Math.abs(velX) < 0.01f) velX = 0;
                        if (Math.abs(velY) < 0.01f) velY = 0;
                    }
                }
            } catch (Exception exp) {
                Gdx.app.error("GameScreen::CameraController::update()", "-- Exception:" + exp);
            }
        }
    }

    class MyGestureDetector extends GestureDetector {
        public MyGestureDetector(GestureListener listener) {
            super(listener);
        }

        @Override
        public boolean keyDown(int keycode) {
//            Gdx.app.log("MyGestureDetector::keyDown()", "-- keycode:" + keycode);
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
//            Gdx.app.log("MyGestureDetector::keyUp()", "-- keycode:" + keycode);
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
//            Gdx.app.log("MyGestureDetector::keyTyped()", "-- character:" + character);
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Gdx.app.log("MyGestureDetector::touchDown()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
            if(gameField != null && gameField.getUnderConstruction() != null) {
                Vector3 touch = new Vector3(screenX, screenY, 0);
                cameraController.camera.unproject(touch);
                GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch);
                if (cellCoordinate != null) {
                    UnderConstruction underConstruction = gameField.getUnderConstruction();
                    if (button == 0) {
                        underConstruction.setStartCoors(cellCoordinate.x, cellCoordinate.y);
                    } else if (button == 1) {
                        gameField.removeTower(cellCoordinate.x, cellCoordinate.y);
                    }
                }
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            Gdx.app.log("MyGestureDetector::touchUp()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
            if(gameField != null && gameField.getUnderConstruction() != null && button == 0) {
                Vector3 touch = new Vector3(screenX, screenY, 0);
                cameraController.camera.unproject(touch);
                GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch);
                if (cellCoordinate != null) {
                    gameField.buildTowersWithUnderConstruction(cellCoordinate.x, cellCoordinate.y);
                }
            }
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
//            Gdx.app.log("MyGestureDetector::touchDragged()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " deviceSettings.getDevice():" + deviceSettings.getDevice());
            if(gameField != null && gameField.getUnderConstruction() != null) {
                Vector3 touch = new Vector3(screenX, screenY, 0);
                cameraController.camera.unproject(touch);
                GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch);
                if (cellCoordinate != null) {
                    gameField.getUnderConstruction().setEndCoors(cellCoordinate.x, cellCoordinate.y);
                }
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
//            Gdx.app.log("MyGestureDetector::mouseMoved()", "-- screenX:" + screenX + " screenY:" + screenY + " deviceSettings.getDevice():" + deviceSettings.getDevice());
            if(gameField != null && gameField.getUnderConstruction() != null/* && deviceSettings.getDevice().equals("desktop")*/) {
                Vector3 touch = new Vector3(screenX, screenY, 0);
                cameraController.camera.unproject(touch);
                GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch);
                if (cellCoordinate != null) {
                    gameField.getUnderConstruction().setEndCoors(cellCoordinate.x, cellCoordinate.y);
                }
            }
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            Gdx.app.log("MyGestureDetector::scrolled()", "-- amount:" + amount);
            if (amount == 1) {
                if (cameraController.camera.zoom <= cameraController.zoomMax)
                    cameraController.camera.zoom += 0.1f;
            } else if (amount == -1) {
                if (cameraController.camera.zoom >= cameraController.zoomMin)
                    cameraController.camera.zoom -= 0.1f;
            }
            cameraController.camera.update();
            return false;
        }
    }


    public GameScreen(String mapName) {
        gameField = new GameField(mapName);
        gameInterface = new GameInterface(gameField);

        cameraController = new CameraController(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), 50.0f, 0.2f);
        cameraController.borderLeftX  = 0 - (gameField.getSizeCellX()/2 * gameField.getSizeFieldY());
        cameraController.borderRightX = 0 + (gameField.getSizeCellX()/2 * gameField.getSizeFieldX());
        cameraController.borderUpY    = 0;
        cameraController.borderDownY  = 0 - (gameField.getSizeCellY() * (gameField.getSizeFieldX()>gameField.getSizeFieldY() ? gameField.getSizeFieldX() : gameField.getSizeFieldY()));
        MyGestureDetector myGestureDetector = new MyGestureDetector(cameraController);

        InputMultiplexer inputMultiplexer = gameInterface.setCommonInputHandler(new GestureDetector(cameraController));
        inputMultiplexer.addProcessor(myGestureDetector);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen::show()", "-- Start!");
        cameraController.camera.position.set(0f, 0f, 0f);
    }

    private void inputHandler(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            if (cameraController.camera.zoom <= cameraController.zoomMax)
                cameraController.camera.zoom += 0.1f;
            cameraController.camera.update();
            Gdx.app.log("GameScreen::inputHandler()", "-- Pressed MINUS");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
            if (cameraController.camera.zoom >= cameraController.zoomMin)
                cameraController.camera.zoom -= 0.1f;
            cameraController.camera.update();
            Gdx.app.log("GameScreen::inputHandler()", "-- Pressed PLUS");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_0 || Input.Keys.NUMPAD_0);");
            gameInterface.getCreepsRoulette().buttonClick();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_1 || Input.Keys.NUMPAD_1);");
            gameField.isDrawableGrid++;
            if(gameField.isDrawableGrid > 5) {
                gameField.isDrawableGrid = 0;
            }
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableGrid:" + gameField.isDrawableGrid);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_2 || Input.Keys.NUMPAD_2);");
            GameField.isDrawableCreeps++;
            if(GameField.isDrawableCreeps > 5) {
                GameField.isDrawableCreeps = 0;
            }
            Gdx.app.log("GameScreen::inputHandler()", "-- GameField.isDrawableCreeps:" + GameField.isDrawableCreeps);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_3 || Input.Keys.NUMPAD_3);");
            gameField.isDrawableTowers++;
            if(gameField.isDrawableTowers > 5) {
                gameField.isDrawableTowers = 0;
            }
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableTowers:" + gameField.isDrawableTowers);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_4 || Input.Keys.NUMPAD_4);");
            gameField.isDrawableGridNav++;
            if(gameField.isDrawableGridNav > 5) {
                gameField.isDrawableGridNav = 0;
            }
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableGridNav:" + gameField.isDrawableGridNav);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_5 || Input.Keys.NUMPAD_5);");
            gameField.isDrawableBackground++;
            if(gameField.isDrawableBackground > 5) {
                gameField.isDrawableBackground = 0;
            }
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableBackground:" + gameField.isDrawableBackground);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_6)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_6 || Input.Keys.NUMPAD_6);");
            gameField.isDrawableForeground++;
            if(gameField.isDrawableForeground > 5) {
                gameField.isDrawableForeground = 0;
            }
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableForeground:" + gameField.isDrawableForeground);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_7)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_7 || Input.Keys.NUMPAD_7);");
            gameField.drawOrder++;
            if(gameField.drawOrder > 8) {
                gameField.drawOrder = 0;
            }
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.drawOrder:" + gameField.drawOrder);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE);");
            TowerDefence.getInstance().removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER);");
            TowerDefence.getInstance().nextGameLevel();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_8)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_8 || Input.Keys.NUMPAD_8); -- gameField.gameSpeed:" + gameField.gameSpeed);
            gameField.gameSpeed -= 0.1f;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_9)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_9 || Input.Keys.NUMPAD_9); -- gameField.gameSpeed:" + gameField.gameSpeed);
            gameField.gameSpeed += 0.1f;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.turnLeft()");
            gameField.turnLeft();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.turnRight()");
            gameField.turnRight();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.flipX()");
            gameField.flipX();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.flipY()");
            gameField.flipY();
        }
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("GameScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String gameState = gameField.getGameState();
        if (gameState.equals("In progress")) {
            inputHandler(delta);
            cameraController.update();
            cameraController.camera.update();
            gameField.render(delta, cameraController.camera);
            gameInterface.act(delta);
            gameInterface.draw();
            gameInterface.getInterfaceStage().getBatch().begin();
            bitmapFont.getData().setScale(4);
            bitmapFont.setColor(Color.YELLOW);
            bitmapFont.draw(gameInterface.getInterfaceStage().getBatch(), String.valueOf("Gold amount: "
                                + gameField.getGamerGold()),
                                Gdx.graphics.getWidth() / 2 - 150,
                                Gdx.graphics.getHeight() - 10);
            gameInterface.getInterfaceStage().getBatch().end();
        } else if (gameState.equals("Lose")) {
            currentDuration += delta;
            if (currentDuration > MAX_DURATION_FOR_DEFEAT_SCREEN) {
                //this.dispose();
                TowerDefence.getInstance().nextGameLevel();
                return;
            }
            if (defeatScreen == null)
                defeatScreen = new Texture(Gdx.files.internal("img/defeat.jpg"));
            gameInterface.getInterfaceStage().getBatch().begin();
            gameInterface.getInterfaceStage().getBatch().draw(defeatScreen, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            gameInterface.getInterfaceStage().getBatch().end();
        } else if (gameState.equals("Win")) {
            currentDuration += delta;
            if (currentDuration > MAX_DURATION_FOR_DEFEAT_SCREEN) {
                //this.dispose();
                TowerDefence.getInstance().nextGameLevel();
                return;
            }
            if (defeatScreen == null)
                defeatScreen = new Texture(Gdx.files.internal("img/victory.jpg"));
            gameInterface.getInterfaceStage().getBatch().begin();
            gameInterface.getInterfaceStage().getBatch().draw(defeatScreen, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            gameInterface.getInterfaceStage().getBatch().end();
        } else {
            Gdx.app.log("Something goes wrong", "123");
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen::resize()", "-- New width:" + width + " height:" + height);
        cameraController.camera.viewportHeight = height;
        cameraController.camera.viewportWidth = width;
        cameraController.camera.update();
//        gameInterface.updateStage(); // Андрей. Твой ресайз не пашет! Если это разкомменить. То не будет работать селектор вообще. Этот инит твой будет по несколько раз вызываться. Один раз при создании и два раза во время ресайза. (эти два ресайза делаются почему то во время инициализации)
    }

    @Override
    public void pause() {
        Gdx.app.log("GameScreen::pause()", " Called!");
    }

    @Override
    public void resume() {
        Gdx.app.log("GameScreen::resume()", " Called!");
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen::hide()", "-- Called!");
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameScreen::dispose()", "-- Called!");
        gameField.dispose();
        bitmapFont.dispose();
        if(defeatScreen != null) {
            defeatScreen.dispose();
        }
    }
}
