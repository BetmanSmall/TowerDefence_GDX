package com.betmansmall.game.gameInterface;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.game.Player;
import com.betmansmall.game.PlayersManager;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class ProtoController extends Stage {
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    private PlayersManager playersManager;
    private VisLabel playerIndexLabel;
    public Player player;

    public ProtoController(PlayersManager playersManager) {
        super(new ScreenViewport());
        this.playersManager = playersManager;

        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.UP:
                        upPressed = true;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = true;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = true;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = true;
                        break;
                }
                return true;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.UP:
                        upPressed = false;
                        break;
                    case Input.Keys.DOWN:
                        downPressed = false;
                        break;
                    case Input.Keys.LEFT:
                        leftPressed = false;
                        break;
                    case Input.Keys.RIGHT:
                        rightPressed = false;
                        break;
                }
                return true;
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.right().bottom();

        int btnSize = Gdx.graphics.getWidth()/10;
        VisTextButton buttonUp = new VisTextButton("UP");
        buttonUp.setSize(btnSize, btnSize);
        buttonUp.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Button buttonDown = new VisTextButton("DOWN");
        buttonDown.setSize(btnSize, btnSize);
        buttonDown.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });

        Button buttonRight = new VisTextButton("RIGHT");
        buttonRight.setSize(btnSize, btnSize);
        buttonRight.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        Button buttonLeft = new VisTextButton("LEFT");
        buttonLeft.setSize(btnSize, btnSize);
        buttonLeft.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        playerIndexLabel = new VisLabel("-");
        playerIndexLabel.setFontScale(btnSize/15f);
        playerIndexLabel.setSize(btnSize, btnSize);

        VisTextButton prevPlayerBtn = new VisTextButton("<");
        prevPlayerBtn.setSize(btnSize, btnSize);
        prevPlayerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (player != null) {
                    if (player.playerID <= 1) {
                        setPlayer(playersManager.getPlayer(playersManager.getPlayers().size() - 1));
                    } else {
                        setPlayer(playersManager.getPlayer(player.playerID - 1));
                    }
                }
            }
        });
        VisTextButton nextPlayerBtn = new VisTextButton(">");
        nextPlayerBtn.setSize(btnSize, btnSize);
        nextPlayerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (player != null) {
                    if (player.playerID >= playersManager.getPlayers().size()-1) {
                        setPlayer(playersManager.getPlayer(1));
                    } else {
                        setPlayer(playersManager.getPlayer(player.playerID + 1));
                    }
                }
            }
        });

        table.add();
        table.add(buttonUp).size(buttonUp.getWidth(), buttonUp.getHeight());
        table.add();
        table.row().pad(5, 5, 5, 5);
        table.add(buttonLeft).size(buttonLeft.getWidth(), buttonLeft.getHeight());
        table.add(playerIndexLabel);
        table.add(buttonRight).size(buttonRight.getWidth(), buttonRight.getHeight());
        table.row().padBottom(5);
        table.add(prevPlayerBtn).size(btnSize/2f, btnSize/2f);
        table.add(buttonDown).size(buttonDown.getWidth(), buttonDown.getHeight());
        table.add(nextPlayerBtn).size(btnSize/2f, btnSize/2f);

        this.addActor(table);
//        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void resize(int width, int height) {
        getViewport().update(width, height);
    }

    public void setPlayer(Player player) {
        this.player = player;
        playerIndexLabel.setText(player.playerID);
    }
}
