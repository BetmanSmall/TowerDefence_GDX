package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.betmansmall.game.gameLogic.GameField;
import com.betmansmall.utils.logging.Logger;

public class InterfaceSelector extends Table {
    protected GameField gameField;
    protected BitmapFont bitmapFont;
    protected GameInterface gameInterface;

    protected boolean vertical;
    protected boolean topBottomLeftRight;
    protected boolean smoothFling;

    protected float parentWidth, parentHeight;
    protected float selectorPrefWidth, selectorPrefHeight;
    protected float selectorBorderVertical;
    protected float selectorBorderHorizontal;

    protected boolean flinging;
    protected float flingVelocityX, flingVelocityY;

    private boolean open = false;
    protected float coordinateX = 0;
    protected float coordinateY = 0;

    private boolean isPanning;

    public void initButtons() {
        Logger.logFuncStart();
    }

    public boolean buttonPressed(Integer index) {
        Logger.logFuncStart("index:" + index);
        return false;
    }

    public void selectorClosed() {
        Logger.logFuncStart();
    }

    public void resize(int width, int height) {
        updateBorders(vertical, topBottomLeftRight, smoothFling);
    }

    public void updateBorders(boolean vertical, boolean topBottomLeftRight, boolean smoothFling) {
        Logger.logFuncStart("vertical:" + vertical, "topBottomLeftRight:" + topBottomLeftRight, "smoothFling:" + smoothFling);
        Group groupParent = getParent(); // mb it is not good!
        if (groupParent != null) {
            parentWidth = groupParent.getWidth(); // mb need set simple // parentWidth = width;
            parentHeight = groupParent.getHeight(); // mb need set simple // parentHeight = height;
            Gdx.app.log("InterfaceSelector::resize()", "-- parentWidth:" + parentWidth + " parentHeight:" + parentHeight);
        }
        selectorPrefWidth = getPrefWidth();
        selectorPrefHeight = getPrefHeight();
        Gdx.app.log("InterfaceSelector::resize()", "-- selectorPrefWidth:" + selectorPrefWidth + " selectorPrefHeight:" + selectorPrefHeight);

        if (vertical) {
            selectorBorderHorizontal = 0;
            coordinateY = parentHeight;
            if (topBottomLeftRight) {
                selectorBorderVertical = parentWidth - selectorPrefWidth;
                if (open) {
                    coordinateX = selectorBorderVertical;
                } else {
                    coordinateX = parentWidth;
                }
            } else {
                selectorBorderVertical = selectorPrefWidth;
                if (open) {
                    coordinateX = 0;
                } else {
                    coordinateX = 0 - selectorPrefWidth;
                }
            }
        } else {
            selectorBorderVertical = 0;
            coordinateX = 0;
            if (topBottomLeftRight) {
                selectorBorderHorizontal = parentHeight - selectorPrefHeight;
                if (open) {
                    coordinateY = parentHeight;
                } else {
                    coordinateY = parentHeight + selectorPrefHeight;
                }
            } else {
                selectorBorderHorizontal = selectorPrefHeight;
                if (open) {
                    coordinateY = selectorBorderHorizontal;
                } else {
                    coordinateY = 0;
                }
            }
        }
        Gdx.app.log("InterfaceSelector::resize()", "-- selectorBorderVertical:" + selectorBorderVertical);
        Gdx.app.log("InterfaceSelector::resize()", "-- selectorBorderHorizontal:" + selectorBorderHorizontal);
        Gdx.app.log("InterfaceSelector::resize()", "-- coordinateX:" + coordinateX);
        Gdx.app.log("InterfaceSelector::resize()", "-- coordinateY:" + coordinateY);
        if (vertical != this.vertical || topBottomLeftRight != this.topBottomLeftRight || smoothFling != this.smoothFling) {
            this.vertical = vertical;
            this.topBottomLeftRight = topBottomLeftRight;
            this.smoothFling = smoothFling;
            this.initButtons();
            this.updateBorders(vertical, topBottomLeftRight, smoothFling);
        }
    }

    public boolean touchDown(float x, float y, int pointer, int button) {
//        Gdx.app.log("InterfaceSelector::touchDown()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        for (Actor actor : getChildren()) {
            if (actor instanceof Button) {
                Button buttonActor = (Button)actor;
                if(buttonActor.isPressed()) {
                    Integer towerIndex = (Integer) buttonActor.getUserObject();
                    if (towerIndex != null) {
                        buttonPressed(towerIndex);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean pan(float x, float y, float deltaX, float deltaY) {
//        Logger.logFuncStart("x:" + x, "y:" + y, "deltaX:" + deltaX, "deltaY:" + deltaY);
        this.isPanning = false;
        float deltaXabs = Math.abs(deltaX);
        float deltaYabs = Math.abs(deltaY);
        if (deltaXabs > deltaYabs) {// && !isPanning) { // select direction
//            coordinateX += deltaX;
            if (vertical) {
                if (deltaX > 0) {
                    if (topBottomLeftRight && x >= coordinateX) {
                        coordinateX += deltaX;
                        if (coordinateX > parentWidth) {
                            coordinateX = parentWidth;
                            this.selectorClosed();
                        }
                        isPanning = true;
                    } else if (!topBottomLeftRight && x <= selectorBorderVertical) {
                        coordinateX += deltaX;
                        if (coordinateX > 0) {
                            coordinateX = 0;
                        }
                        isPanning = true;
                    }
                } else {
                    if (topBottomLeftRight && x >= selectorBorderVertical) {
                        coordinateX += deltaX;
                        if (coordinateX < selectorBorderVertical) {
                            coordinateX = selectorBorderVertical;
                        }
                        isPanning = true;
                    } else if (!topBottomLeftRight && x <= coordinateX+ selectorPrefWidth) {
                        coordinateX += deltaX;
                        if (coordinateX < 0 - selectorPrefWidth) {
                            coordinateX = 0 - selectorPrefWidth;
                            this.selectorClosed();
                        }
                        isPanning = true;
                    }
                }
            } else {
                if (deltaX > 0) {
                    if (topBottomLeftRight && y >= coordinateY- selectorPrefHeight) {
                        coordinateX += deltaX;
                        if (coordinateX > 0) {
                            coordinateX = 0;
                        }
                        isPanning = true;
                    } else if (!topBottomLeftRight && y <= coordinateY){
                        coordinateX += deltaX;
                        if (coordinateX > 0) {
                            coordinateX = 0;
                        }
                        isPanning = true;
                    }
                } else {
                    if (topBottomLeftRight && y >= coordinateY- selectorPrefHeight) {
                        coordinateX += deltaX;
                        if (coordinateX+ selectorPrefWidth < parentWidth) {
                            coordinateX = parentWidth- selectorPrefWidth;
                        }
                        isPanning = true;
                    } else if (!topBottomLeftRight && y <= coordinateY) {
                        coordinateX += deltaX;
                        if (coordinateX+ selectorPrefWidth < parentWidth) {
                            coordinateX = parentWidth- selectorPrefWidth;
                        }
                        isPanning = true;
                    }
                }
            }
        } else {
//            coordinateY += deltaY;
            if (vertical) {
                if (deltaY > 0) {
                    if (topBottomLeftRight && x >= coordinateX) {
                        coordinateY += deltaY;
                        if (coordinateY- selectorPrefHeight > 0) {
                            coordinateY = selectorPrefHeight;
                        }
                        isPanning = true;
                    } else if (!topBottomLeftRight && x <= coordinateX+ selectorPrefWidth) {
                        coordinateY += deltaY;
                        if (coordinateY- selectorPrefHeight > 0) {
                            coordinateY = selectorPrefHeight;
                        }
                        isPanning = true;
                    }
                } else {
                    if (topBottomLeftRight && x >= coordinateX) {
                        coordinateY += deltaY;
                        if (coordinateY < parentHeight) {
                            coordinateY = parentHeight;
                        }
                        isPanning = true;
                    } else if (!topBottomLeftRight && x <= coordinateX+ selectorPrefWidth) {
                        coordinateY += deltaY;
                        if (coordinateY < parentHeight) {
                            coordinateY = parentHeight;
                        }
                        isPanning = true;
                    }
                }
            } else {
                if (deltaY > 0) {
                    if (topBottomLeftRight && y >= coordinateY- selectorPrefHeight) {
                        coordinateY += deltaY;
                        if (coordinateY- selectorPrefHeight > parentHeight) {
                            coordinateY = parentHeight+ selectorPrefHeight;
                            this.selectorClosed();
                        }
                        isPanning = true;
                    } else if (!topBottomLeftRight && y <= selectorBorderHorizontal){
                        coordinateY += deltaY;
                        if (coordinateY > selectorBorderHorizontal) {
                            coordinateY = selectorBorderHorizontal;
                        }
                        isPanning = true;
                    }
                } else {
                    if (topBottomLeftRight && y >= selectorBorderHorizontal) {
                        coordinateY += deltaY;
                        if (coordinateY- selectorPrefHeight < selectorBorderHorizontal) {
                            coordinateY = parentHeight;
                        }
                        isPanning = true;
                    } else if (!topBottomLeftRight && y <= coordinateY) {
                        coordinateY += deltaY;
                        if (coordinateY < 0) {
                            coordinateY = 0;
                            this.selectorClosed();
                        }
                        isPanning = true;
                    }
                }
            }
        }
        return isPanning;
    }

    public boolean panStop(float x, float y, int pointer, int button) {
//        Gdx.app.log("InterfaceSelector::panStop()", "-- x:" + x + " y:" + y + " pointer:" + pointer + " button:" + button);
        boolean panStop = false;
        if (vertical) {
            if (topBottomLeftRight) {
                if (coordinateX >= selectorBorderVertical + (selectorPrefWidth / 2) ) {
                    if (coordinateX < parentWidth) {
                        coordinateX = parentWidth;
                        this.selectorClosed();
                        panStop = true;
                    }
                } else {
                    coordinateX = selectorBorderVertical;
                    panStop = true;
                }
            } else {
                if (coordinateX+ selectorPrefWidth >= selectorBorderVertical - (selectorPrefWidth / 2) ) {
                    coordinateX = 0;
                    panStop = true;
                } else {
                    if (coordinateX > -selectorPrefWidth) {
                        coordinateX = -selectorPrefWidth;
                        this.selectorClosed();
                        panStop = true;
                    }
                }
            }
        } else {
            if (topBottomLeftRight) {
                if (coordinateY-selectorPrefHeight >= selectorBorderHorizontal + (selectorPrefHeight / 2) ) {
                    if (coordinateY-selectorPrefHeight < parentHeight) {
                        coordinateY = parentHeight + selectorPrefHeight;
                        this.selectorClosed();
                        panStop = true;
                    }
                } else {
                    coordinateY = parentHeight;
                    panStop = true;
                }
            } else {
                if (coordinateY >= selectorBorderHorizontal - (selectorPrefHeight / 2) ) {
                    coordinateY = selectorBorderHorizontal;
                    panStop = true;
                } else {
                    if (coordinateY > 0) {
                        coordinateY = 0;
                        this.selectorClosed();
                        panStop = true;
                    }
                }
            }
        }
        this.isPanning = false;
        return panStop;
    }

    public boolean fling(float velocityX, float velocityY, int button) {
//        Gdx.app.log("InterfaceSelector::fling()", "-- velocityX:" + velocityX + " velocityY:" + velocityY);
        this.flinging = false;
        if (vertical) {
            if (topBottomLeftRight) {
                if (gameInterface.prevMouseX > selectorBorderVertical) {
                    flinging = true;
                }
            } else {
                if (gameInterface.prevMouseX < selectorBorderVertical) {
                    flinging = true;
                }
            }
        } else {
            if (topBottomLeftRight) {
                if (gameInterface.prevMouseY > selectorBorderHorizontal) {
                    flinging = true;
                }
            } else {
                if (gameInterface.prevMouseY < selectorBorderHorizontal) {
                    flinging = true;
                }
            }
        }
        if (smoothFling && flinging) { // smoothFlingSelector - плавное движение селектора
            flingVelocityX = velocityX * 0.5f;
            flingVelocityY = velocityY * 0.5f;
        } else { // если smoothFlingSelector=false значит нужно рывками сдвигать по секциям. как в help SlidingTable
            flinging = false; // TODO work need
        }
        return flinging;
    }

    public boolean scrolled(int amount) {
//        Gdx.app.log("InterfaceSelector::scrolled()", "-- amount:" + amount);
        int x = gameInterface.prevMouseX;
        int y = gameInterface.prevMouseY;
        if (vertical) {
            if (topBottomLeftRight) {
                if (x >= selectorBorderVertical) {
                    coordinateY += amount*20f;
                    return true;
                }
            } else {
                if (x < selectorBorderVertical) {
                    coordinateY += amount*20f;
                    return true;
                }
            }
        } else {
            if (topBottomLeftRight) {
                if (y >= selectorBorderHorizontal) {
                    coordinateX += amount*20f;
                    return true;
                }
            } else {
                if (y < selectorBorderHorizontal) {
                    coordinateX += amount*20f;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void act(float delta) {
        if (flinging) {
            flingVelocityX *= 0.98f;
            flingVelocityY *= 0.98f;
            float newX = coordinateX + (flingVelocityX * delta);
            float newY = coordinateY + (flingVelocityY * delta);
            if (vertical) {
                if (newY > parentHeight && newY - selectorPrefHeight < 0) {
                    coordinateY = newY;
                }
            } else {
                if (newX < 0 && newX + selectorPrefWidth > parentWidth) {
                    coordinateX = newX;
                }
            }
            if (Math.abs(flingVelocityX) < 0.01) flingVelocityX = 0.0f;
            if (Math.abs(flingVelocityY) < 0.01) flingVelocityY = 0.0f;
            if (flingVelocityX == 0.0 && flingVelocityY == 0.0) {
                flinging = false;
            }
        }
        setX(coordinateX);
        setY( -(coordinateY-parentHeight) ); // pizdec libgdx draw ui from leftDown but mouse Coord from leftUp
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
