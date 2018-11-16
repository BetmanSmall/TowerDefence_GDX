package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.betmansmall.game.GameScreenInteface.GameInterface;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.game.gameLogic.UnderConstruction;
import com.betmansmall.game.gameLogic.playerTemplates.FactionsManager;

public class GameScreen /*extends GestureDetector*/ implements Screen, GestureListener, InputProcessor {
//    class CameraController implements GestureListener {
        // Need all fix this!!! PIZDEC?1??
        private float zoomMax = 50f; //max size
        private float zoomMin = 0.2f; // 2x zoom
        public Float borderLeftX, borderRightX;
        public Float borderUpY, borderDownY;
        public OrthographicCamera camera;
        // Need all fix this!!! PIZDEC??2?
        float velX, velY;
        boolean flinging = false; // Что бы не пересикалось одно действие с другим действием (с) Андрей А
        float initialScale = 2f;
        boolean lastCircleTouched = false;
        // Need all fix this!!! PIZDEC???3

//        public CameraController(float maxZoom, float minZoom, OrthographicCamera camera) {//, float borderLeftX, float borderRightX, float borderUpY, float borderDownY) {
//            Gdx.app.log("CameraController::CameraController()", "-- maxZoom:" + maxZoom + " minZoom:" + minZoom + " camera:" + camera);
//            this.zoomMax = maxZoom;
//            this.zoomMin = minZoom;
//            this.camera = camera;
////            this.borderLeftX = borderLeftX;
////            this.borderRightX = borderRightX;
////            this.borderUpY = borderUpY;
////            this.borderDownY = borderDownY;
//        }

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            Gdx.app.log("CameraController::touchDown()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
//            flinging = false;
//            initialScale = camera.zoom;
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            Gdx.app.log("CameraController::tap()", "-- x:" + x + " y:" + y + " count:" + count + " button:" + button);
////          CHECK IF THE PAUSE BUTTON IS TOUCHED //CHECK IF THE TOWER BUTTON IS TOUCHED
            if (gameInterface.tap(x, y, count, button)) {
                return false;
            }
//
//            Vector3 touch = new Vector3(x, y, 0.0f);
//            camera.unproject(touch);
//            GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch, gameField.isDrawableTowers); // need to units too!
//            if (cellCoordinate != null && gameField.getUnderConstruction() == null) {
//                if (button == 0) {
////                    gameField.towerActions(cellCoordinate.x, cellCoordinate.y);
//                } else if (button == 1) {
//                    gameField.removeTower(cellCoordinate.x, cellCoordinate.y);
////                  gameField.prepareBuildTower(cellCoordinate.x, cellCoordinate.y);
////              } else if(button == 2) {
////                  gameField.createUnit(cellCoordinate.x, cellCoordinate.y);
//                } else if (button == 3) {
//                    gameField.createUnit(cellCoordinate.x, cellCoordinate.y);
//                } else if (button == 4) {
//                    gameField.setExitCell(cellCoordinate.x, cellCoordinate.y);
//                }
//            } else if(gameField.getUnderConstruction() != null && button == 1) {
//                gameField.cancelUnderConstruction();
//            }
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            Gdx.app.log("CameraController::longPress()", "-- x:" + x + " y:" + y);
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
            Vector3 touch = new Vector3(x, y, 0.0f);
            camera.unproject(touch);
            Gdx.app.log("CameraController::pan()", "-- x:" + x + " y:" + y + " deltaX:" + deltaX + " deltaY:" + deltaY);
//            Gdx.app.log("CameraController::pan(1)", "-- x:" + camera.position.x + " y:" + camera.position.y);
//            Gdx.app.log("CameraController::pan(2)", "-- x:" + touch.x + " y:" + touch.y);
            if (gameInterface.pan(x, y, deltaX, deltaY)) {
                lastCircleTouched = true;
                return true;
            }
            lastCircleTouched = false;
            if (gameField.getUnderConstruction() == null || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                float newCameraX = camera.position.x + (-deltaX * camera.zoom);
                float newCameraY = camera.position.y + (deltaY * camera.zoom);
                if (borderLeftX != null && borderRightX != null && borderUpY != null && borderDownY != null) {
                    if (borderLeftX < newCameraX && newCameraX < borderRightX &&
                            borderUpY > newCameraY && newCameraY > borderDownY) {
                        camera.position.set(newCameraX, newCameraY, 0.0f);
                    }
                } else {
                    camera.position.set(newCameraX, newCameraY, 0.0f);
                }
            } else {
                float space = 50f;
                float shiftCamera = 5f;
                if (x < space) {
                    camera.position.add(-shiftCamera, 0.0f, 0.0f);
                }
                if (x > Gdx.graphics.getWidth() - space) {
                    camera.position.add(shiftCamera, 0.0f, 0.0f);
                }
                if (y < space) {
                    camera.position.add(0.0f, shiftCamera, 0.0f);
                }
                if (y > Gdx.graphics.getHeight() - space) {
                    camera.position.add(0.0f, -shiftCamera, 0.0f);
                }
            }
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            Gdx.app.log("CameraController::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
            if(gameInterface.panStop(x, y, pointer, button)) {
//                return true;
            }
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
                        float newCameraX = camera.position.x + (-velX * Gdx.graphics.getDeltaTime());
                        float newCameraY = camera.position.y + (velY * Gdx.graphics.getDeltaTime());
                        if (borderLeftX != null && borderRightX != null && borderUpY != null && borderDownY != null) {
                            if (borderLeftX < newCameraX && newCameraX < borderRightX &&
                                    borderUpY > newCameraY && newCameraY > borderDownY) {
                                camera.position.set(newCameraX, newCameraY, 0.0f);
                            }
                        } else {
                            camera.position.set(newCameraX, newCameraY, 0.0f);
                        }
                        if (Math.abs(velX) < 0.01f) velX = 0.0f;
                        if (Math.abs(velY) < 0.01f) velY = 0.0f;
                    }
                }
                camera.update();
            } catch (Exception exp) {
                Gdx.app.error("GameScreen::CameraController::update()", "-- Exception:" + exp);
            }
        }
//    }

//    class MyGestureDetector extends GestureDetector {
//        public MyGestureDetector(GestureListener listener) {
//            super(listener);
//        }

        @Override
        public boolean keyDown(int keycode) {
            Gdx.app.log("MyGestureDetector::keyDown()", "-- keycode:" + keycode);
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
            flinging = false;
            initialScale = camera.zoom;
//            Gdx.app.log("CameraController::tap()", "-- x:" + x + " y:" + y + " count:" + pointer + " button:" + button);
//          CHECK IF THE PAUSE BUTTON IS TOUCHED //CHECK IF THE TOWER BUTTON IS TOUCHED
//            if (gameInterface.tap(screenX, screenY, pointer, button)) {
//                return false;
//            }

            if (!gameInterface.interfaceTouched) {
                Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                camera.unproject(touch);
                if (gameField.getUnderConstruction() != null) {
                    if (button == 1) {
                        gameField.cancelUnderConstruction();
                        return false;
                    }
                    GridPoint2 cellCoordinate2 = gameField.getWhichCell().whichCell(touch, gameField.isDrawableTowers);
                    if (cellCoordinate2 != null) {
                        UnderConstruction underConstruction = gameField.getUnderConstruction();
                        if (button == 0) {
                            underConstruction.setStartCoors(cellCoordinate2.x, cellCoordinate2.y);
                        } else if (button == 1) {
                            gameField.removeTower(cellCoordinate2.x, cellCoordinate2.y);
                        }
                    }
                } else {
                    GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch, gameField.isDrawableTowers); // need to units too!
                    if (cellCoordinate != null) {
                        if (button == 0) {
                            gameField.removeTower(cellCoordinate.x, cellCoordinate.y);
                        } else if (button == 1) {
//                    gameField.towerActions(cellCoordinate.x, cellCoordinate.y);
//                        } else if(button == 2) {
//                            gameField.createUnit(cellCoordinate.x, cellCoordinate.y);
                        } else if (button == 3) {
                            gameField.createUnit(cellCoordinate.x, cellCoordinate.y);
                        } else if (button == 4) {
                            gameField.setExitPoint(cellCoordinate.x, cellCoordinate.y);
                        }

                    }
                }
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            Gdx.app.log("MyGestureDetector::touchUp()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer + " button:" + button);
            if (!gameInterface.interfaceTouched) {
                if (gameField != null && gameField.getUnderConstruction() != null && button == 0) {
                    Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                    camera.unproject(touch);
                    GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch, gameField.isDrawableTowers);
                    if (cellCoordinate != null) {
                        gameField.buildTowersWithUnderConstruction(cellCoordinate.x, cellCoordinate.y);
                    }
                }
            }
            gameInterface.interfaceTouched = false;
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            Gdx.app.log("MyGestureDetector::touchDragged()", "-- screenX:" + screenX + " screenY:" + screenY + " pointer:" + pointer);
            if (gameField != null && gameField.getUnderConstruction() != null) {
                Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                camera.unproject(touch);
                GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch, gameField.isDrawableTowers);
                if (cellCoordinate != null) {
                    gameField.getUnderConstruction().setEndCoors(cellCoordinate.x, cellCoordinate.y);
                }
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
//            Gdx.app.log("MyGestureDetector::mouseMoved()", "-- screenX:" + screenX + " screenY:" + screenY + " deviceSettings.getDevice():" + deviceSettings.getDevice());
            if (gameField != null && gameField.getUnderConstruction() != null/* && deviceSettings.getDevice().equals("desktop")*/) { // !LOL! deviceSettings is SHIT
                Vector3 touch = new Vector3(screenX, screenY, 0.0f);
                Gdx.app.log("GameScreen::mouseMoved()", "-window- screenX:" + screenX + " screenY:" + screenY);
                camera.unproject(touch);
//                Gdx.app.log("GameScreen::mouseMoved()", "-graphics- touch.x:" + touch.x + " touch.y:" + touch.y);
                GridPoint2 cellCoordinate = gameField.getWhichCell().whichCell(touch, gameField.isDrawableTowers);
                if (cellCoordinate != null) {
                    gameField.getUnderConstruction().setEndCoors(cellCoordinate.x, cellCoordinate.y);
                    Gdx.app.log("GameScreen::mouseMoved()", "-cell- cellCoordinate.x:" + cellCoordinate.x + " cellCoordinate.y:" + cellCoordinate.y);
                }
            }
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            Gdx.app.log("MyGestureDetector::scrolled()", "-- amount:" + amount);
            if (gameInterface.scrolled(amount)) {
                return false;
            }
            if (amount == 1) {
                if (camera.zoom <= zoomMax)
                    camera.zoom += 0.1f;
            } else if (amount == -1) {
                if (camera.zoom >= zoomMin)
                    camera.zoom -= 0.1f;
            }
            camera.update();
            return false;
        }
//    }

//    private ShapeRenderer shapeRenderer; // Нужно все эти штуки вынести из интерфейса и геймФиелда, сюда на уровень геймСкрина.
//    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;

    private GameField gameField;
    private GameInterface gameInterface;
//    private CameraController cameraController;

    public GameScreen(String mapName, FactionsManager factionsManager, float levelOfDifficulty) {
        Gdx.app.log("GameScreen::GameScreen(" + mapName + ", " + levelOfDifficulty + ")", "--");
//        shapeRenderer = new ShapeRenderer();
//        spriteBatch = new SpriteBatch();
        bitmapFont = new BitmapFont();

        gameField = new GameField(mapName, factionsManager, levelOfDifficulty);
        gameInterface = new GameInterface(gameField, bitmapFont);
        gameInterface.mapNameLabel.setText("MapName:" + mapName);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        cameraController = new CameraController(50.0f, 0.2f, new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
//        borderLeftX  = new Float(0 - (gameField.getSizeCellX()/2 * gameField.getSizeFieldY()));
//        borderRightX = new Float(0 + (gameField.getSizeCellX()/2 * gameField.getSizeFieldX()));
//        borderUpY    = new Float(0);
//        borderDownY  = new Float(0 - (gameField.getSizeCellY() * (gameField.getSizeFieldX()>gameField.getSizeFieldY() ? gameField.getSizeFieldX() : gameField.getSizeFieldY())));

        InputMultiplexer inputMultiplexer = new InputMultiplexer();//new MyGestureDetector(cameraController));// я хз че делать=(
//        inputMultiplexer.addProcessor(new GestureDetector(cameraController)); // Бля тут бага тоже есть | очень страная бага | поменяй местам, запусти, выбери башню она построется в (0,0)
        inputMultiplexer.addProcessor(new GestureDetector(this));
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(gameInterface.stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen::show()", "--");
        camera.position.set(0.0f, 0.0f, 0.0f);
    }

    private void inputHandler(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.MINUS)");
            if (camera.zoom <= zoomMax) {
                camera.zoom += 0.1f;
            }
            camera.update();
            gameInterface.addActionToHistory("-- camera.zoom:" + camera.zoom);
            Gdx.app.log("GameScreen::inputHandler()", "-- camera.zoom:" + camera.zoom);
            if (gameField.gameSpeed > 0.1f) {
                gameField.gameSpeed -= 0.1f;
            }
            gameInterface.addActionToHistory("-- gameField.gameSpeed:" + gameField.gameSpeed);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.gameSpeed:" + gameField.gameSpeed);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.PLUS)");
            if (camera.zoom >= zoomMin) {
                camera.zoom -= 0.1f;
            }
            camera.update();
            gameInterface.addActionToHistory("-- camera.zoom:" + camera.zoom);
            Gdx.app.log("GameScreen::inputHandler()", "-- camera.zoom:" + camera.zoom);
            gameField.gameSpeed += 0.1f;
            gameInterface.addActionToHistory("-- gameField.gameSpeed:" + gameField.gameSpeed);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.gameSpeed:" + gameField.gameSpeed);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_0 || Input.Keys.NUMPAD_0)");
//            gameInterface.unitsSelector.changeGameState(); need func() here
            camera.position.set(0.0f, 0.0f, 0.0f);
            gameInterface.addActionToHistory("-- camera.position:" + camera.position);
            Gdx.app.log("GameScreen::inputHandler()", "-- camera.position:" + camera.position);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.SPACE)");
            gameField.gamePaused = !gameField.gamePaused;
            gameInterface.addActionToHistory("-- gameField.gamePaused:" + gameField.gamePaused);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.gamePaused:" + gameField.gamePaused);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- Gdx.input.isKeyJustPressed(Input.Keys.F1)");
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                gameField.isDrawableGrid--;
                if (gameField.isDrawableGrid < 0) {
                    gameField.isDrawableGrid = 5;
                }
            } else {
                gameField.isDrawableGrid++;
                if (gameField.isDrawableGrid > 5) {
                    gameField.isDrawableGrid = 0;
                }
            }
            gameField.isDrawableUnits = gameField.isDrawableGrid;
            gameField.isDrawableTowers = gameField.isDrawableGrid;
            gameField.isDrawableBackground = gameField.isDrawableGrid;
            gameField.isDrawableGround = gameField.isDrawableGrid;
            gameField.isDrawableForeground = gameField.isDrawableGrid;
            gameField.isDrawableGridNav = gameField.isDrawableGrid;
            gameField.isDrawableRoutes = gameField.isDrawableGrid;
            gameInterface.addActionToHistory("-and other- gameField.isDrawableGrid:" + gameField.isDrawableGrid);
            Gdx.app.log("GameScreen::inputHandler()", "-and other- gameField.isDrawableGrid:" + gameField.isDrawableGrid);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_1 || Input.Keys.NUMPAD_1)");
            gameField.isDrawableGrid++;
            if (gameField.isDrawableGrid > 5) {
                gameField.isDrawableGrid = 0;
            }
            gameInterface.addActionToHistory("-- gameField.isDrawableGrid:" + gameField.isDrawableGrid);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableGrid:" + gameField.isDrawableGrid);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_2 || Input.Keys.NUMPAD_2)");
            gameField.isDrawableUnits++;
            if (gameField.isDrawableUnits > 5) {
                gameField.isDrawableUnits = 0;
            }
            gameInterface.addActionToHistory("-- gameField.isDrawableUnits:" + GameField.isDrawableUnits);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableUnits:" + GameField.isDrawableUnits);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_3 || Input.Keys.NUMPAD_3)");
            gameField.isDrawableTowers++;
            if (gameField.isDrawableTowers > 5) {
                gameField.isDrawableTowers = 0;
            }
            gameInterface.addActionToHistory("-- gameField.isDrawableTowers:" + gameField.isDrawableTowers);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableTowers:" + gameField.isDrawableTowers);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_4)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_4 || Input.Keys.NUMPAD_4)");
            gameField.isDrawableBackground++;
            if (gameField.isDrawableBackground > 5) {
                gameField.isDrawableBackground = 0;
            }
            gameInterface.addActionToHistory("-- gameField.isDrawableBackground:" + gameField.isDrawableBackground);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableBackground:" + gameField.isDrawableBackground);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_5)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_5 || Input.Keys.NUMPAD_5)");
            gameField.isDrawableGround++;
            if (gameField.isDrawableGround > 5) {
                gameField.isDrawableGround = 0;
            }
            gameInterface.addActionToHistory("-- gameField.isDrawableGround:" + gameField.isDrawableGround);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableGround:" + gameField.isDrawableGround);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_6)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_6 || Input.Keys.NUMPAD_6)");
            gameField.isDrawableForeground++;
            if (gameField.isDrawableForeground > 5) {
                gameField.isDrawableForeground = 0;
            }
            gameInterface.addActionToHistory("-- gameField.isDrawableForeground:" + gameField.isDrawableForeground);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableForeground:" + gameField.isDrawableForeground);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_7)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_7 || Input.Keys.NUMPAD_7)");
            gameField.isDrawableGridNav++;
            if (gameField.isDrawableGridNav > 5) {
                gameField.isDrawableGridNav = 0;
            }
            gameInterface.addActionToHistory("-- gameField.isDrawableGridNav:" + gameField.isDrawableGridNav);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableGridNav:" + gameField.isDrawableGridNav);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_8)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_8 || Input.Keys.NUMPAD_8)");
            gameField.isDrawableRoutes++;
            if (gameField.isDrawableRoutes > 5) {
                gameField.isDrawableRoutes = 0;
            }
            gameInterface.addActionToHistory("-- gameField.isDrawableRoutes:" + gameField.isDrawableRoutes);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.isDrawableRoutes:" + gameField.isDrawableRoutes);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_9)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.NUM_9 || Input.Keys.NUMPAD_9)");
            gameField.drawOrder++;
            if (gameField.drawOrder > 8) {
                gameField.drawOrder = 0;
            }
            gameInterface.addActionToHistory("-- gameField.drawOrder:" + gameField.drawOrder);
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.drawOrder:" + gameField.drawOrder);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.BACK || Input.Keys.BACKSPACE)");
            TowerDefence.getInstance().removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ENTER)");
            TowerDefence.getInstance().nextGameLevel();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.A)");
            gameField.turnLeft();
            gameInterface.addActionToHistory("-- gameField.turnLeft()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.turnLeft()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.S)");
            gameField.turnRight();
            gameInterface.addActionToHistory("-- gameField.turnRight()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.turnRight()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.Q)");
            gameField.flipX();
            gameInterface.addActionToHistory("-- gameField.flipX()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.flipX()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.W)");
            gameField.flipY();
            gameInterface.addActionToHistory("-- gameField.flipY()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.flipY()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.PERIOD)");
            gameInterface.arrayActionsHistory.clear();
            gameInterface.addActionToHistory("-- gameInterface.arrayActionsHistory.clear()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameInterface.arrayActionsHistory.clear()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.ESCAPE || Input.Keys.N)");
            gameField.cancelUnderConstruction();
            gameInterface.addActionToHistory("-- gameField.cancelUnderConstruction()");
            Gdx.app.log("GameScreen::inputHandler()", "-- gameField.cancelUnderConstruction()");
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            Gdx.app.log("GameScreen::inputHandler()", "-- isKeyJustPressed(Input.Keys.B)");
            UnderConstruction underConstruction = gameField.createdRandomUnderConstruction();
            gameInterface.addActionToHistory("-- factionsManager.createdRandomUnderConstruction(" + underConstruction.templateForTower.name + ")");
            Gdx.app.log("GameScreen::inputHandler()", "-- factionsManager.createdRandomUnderConstruction(" + underConstruction.templateForTower.name + ")");
        }
    }

    @Override
    public void render(float delta) {
//      Gdx.app.log("GameScreen::render()", "-- delta:" + delta + " FPS:" + Gdx.graphics.getFramesPerSecond());
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String gameState = gameField.getGameState(); // Need change to enum GameState
        if (gameState.equals("In progress")) {
            inputHandler(delta);
            update();
            gameField.render(delta, camera);
            gameInterface.render(delta);
        } else if (gameState.equals("Lose") || gameState.equals("Win")) {
            gameInterface.renderEndGame(delta, gameState);
        } else {
            Gdx.app.log("GameScreen::render()", "-- Not get normal gameState!");
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen::resize(" + width + ", " + height + ")", "--");
//        if(Gdx.app.getType() == Application.ApplicationType.Android) {
//            gameInterface.stage.getViewport().update(width/2, height/2, true);
//        } else {
            gameInterface.stage.getViewport().update(width, height, true);
//        }
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
//        gameInterface.updateStage(); // Андрей. Твой ресайз не пашет! Если это разкомменить. То не будет работать селектор вообще. Этот инит твой будет по несколько раз вызываться. Один раз при создании и два раза во время ресайза. (эти два ресайза делаются почему то во время инициализации)
    }

    @Override
    public void pause() {
        Gdx.app.log("GameScreen::pause()", "--");
    }

    @Override
    public void resume() {
        Gdx.app.log("GameScreen::resume()", "--");
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen::hide()", "--");
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameScreen::dispose()", "--");
        gameField.dispose();
        gameInterface.dispose();
//        cameraController.dispose();
    }
}
