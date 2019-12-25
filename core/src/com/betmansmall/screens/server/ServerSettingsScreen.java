package com.betmansmall.screens.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.enums.GameType;
import com.betmansmall.enums.SessionType;
import com.betmansmall.screens.AbstractScreen;
import com.betmansmall.util.logging.Logger;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

public class ServerSettingsScreen extends AbstractScreen {
    private Stage stage;
    private VisTextButton startServer;
    private VisCheckBox withControlCheckBox;
    private VisTable playerSettings;
    private VisTextField nameField;
    private VisSelectBox<String> factionSelectBox;

    public ServerSettingsScreen(GameMaster gameMaster) {
        super(gameMaster);
        Logger.logFuncStart();
        createUI();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        this.stage.dispose();
    }

    public void createUI() {
        this.stage = new Stage(new ScreenViewport());
//        this.stage.setDebugAll(true);

        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);

        VisLabel hostLabel = new VisLabel("host:");
        rootTable.add(hostLabel);

        Array<String> addresses = new Array<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)){
                for (InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (address instanceof Inet4Address){
                        if (!address.getHostAddress().equals("127.0.0.1")) {
                            addresses.add(address.getHostAddress());
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        VisSelectBox<String> hostField = new VisSelectBox<>();
        hostField.setItems(addresses);
        hostField.setSelected(addresses.peek());
        rootTable.add(hostField).row();

        VisLabel authServerPortLabel = new VisLabel("authServerPort:");
        rootTable.add(authServerPortLabel);

        VisTextField authServerPortField = new VisTextField(game.sessionSettings.authServerPort.toString());
        rootTable.add(authServerPortField).row();

        VisLabel gameServerPortLabel = new VisLabel("gameServerPort:");
        rootTable.add(gameServerPortLabel);

        VisTextField gameServerPortField = new VisTextField(game.sessionSettings.gameServerPort.toString());
        rootTable.add(gameServerPortField).row();

        VisLabel gameTypeLabel = new VisLabel("gameType:");
        rootTable.add(gameTypeLabel);

        VisSelectBox<GameType> gameTypeSelectBox = new VisSelectBox<>();
        gameTypeSelectBox.setItems(GameType.values());
        gameTypeSelectBox.setSelected(GameType.TowerDefence);
        rootTable.add(gameTypeSelectBox).colspan(2).row();

        VisLabel mapLabel = new VisLabel("map:");
        rootTable.add(mapLabel);

        VisSelectBox<String> mapSelectBox = new VisSelectBox<>();
        mapSelectBox.setItems(game.gameLevelMaps);
//        mapSelectBox.setSelected(game.gameLevelMaps.random());
        rootTable.add(mapSelectBox).colspan(2).row();

        startServer = new VisTextButton("START SERVER");
        startServer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logFuncStart("event:" + event + ", actor:" + actor);
                game.sessionSettings.host = hostField.getSelected();
                game.sessionSettings.authServerPort = Integer.parseInt(authServerPortField.getText());
                game.sessionSettings.gameServerPort = Integer.parseInt(gameServerPortField.getText());
                game.sessionSettings.gameSettings.gameType = gameTypeSelectBox.getSelected();
                game.sessionSettings.gameSettings.mapPath = mapSelectBox.getSelected();

                if (withControlCheckBox.isChecked()) {
                    game.userAccount.loginName = nameField.getText();
                    game.userAccount.factionName = factionSelectBox.getSelected();
                    game.sessionSettings.sessionType = SessionType.SERVER_AND_CLIENT;
                } else {
                    game.sessionSettings.sessionType = SessionType.SERVER_STANDALONE;
                }
                game.addScreen(new ServerGameScreen(game, game.userAccount));
            }
        });
        rootTable.add(startServer).colspan(2).row();

        withControlCheckBox = new VisCheckBox("WITH CONTROL", true);
        withControlCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playerSettings.setVisible(withControlCheckBox.isChecked());
            }
        });
        rootTable.add(withControlCheckBox).colspan(2).row();

        playerSettings = new VisTable();
        playerSettings.add(new VisLabel("name:"));
        nameField = new VisTextField("ClientByServer");
        playerSettings.add(nameField).row();

        playerSettings.add(new VisLabel("faction:"));
        factionSelectBox = new VisSelectBox();
        factionSelectBox.setItems(game.factionsManager.getFactionsNames());
        playerSettings.add(factionSelectBox).row();

        rootTable.add(playerSettings).colspan(2).row();

        VisLabel versionLabel = new VisLabel(game.version.getVersionAndHash());
        rootTable.add(versionLabel).colspan(2).row();

        stage.addActor(rootTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        inputHandler(delta);
        stage.act();
        stage.draw();
    }

    private void inputHandler(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Logger.logDebug("isKeyJustPressed(Input.Keys.BACK || Input.Keys.ESCAPE);");
            game.removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Logger.logDebug("isKeyJustPressed(Input.Keys.ENTER || Input.Keys.SPACE);");
            startServer.toggle();
        }
    }
}
