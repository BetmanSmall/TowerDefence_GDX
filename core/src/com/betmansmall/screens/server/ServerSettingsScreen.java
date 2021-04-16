package com.betmansmall.screens.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.enums.GameType;
import com.betmansmall.enums.SessionType;
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
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
        this.stage.setDebugUnderMouse(true);

        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);

        VisTable leftTable = new VisTable();
        rootTable.add(leftTable).left();

        VisLabel hostLabel = new VisLabel("host:");
        leftTable.add(hostLabel).align(Align.right);

        Array<String> addresses = getAddresses();

        VisSelectBox<String> hostField = new VisSelectBox<>();
        hostField.setItems(addresses);
        hostField.setSelected(addresses.peek());
        leftTable.add(hostField).align(Align.left).row();

        VisLabel authServerPortLabel = new VisLabel("authServerPort:");
        leftTable.add(authServerPortLabel).align(Align.right);

        VisTextField authServerPortField = new VisTextField(gameMaster.sessionSettings.authServerPort.toString());
        leftTable.add(authServerPortField).align(Align.left).row();

        VisLabel gameServerPortLabel = new VisLabel("gameServerPort:");
        leftTable.add(gameServerPortLabel).align(Align.right);

        VisTextField gameServerPortField = new VisTextField(gameMaster.sessionSettings.gameServerPort.toString());
        leftTable.add(gameServerPortField).align(Align.left).row();

        VisLabel gameTypeLabel = new VisLabel("gameType:");
        leftTable.add(gameTypeLabel).align(Align.right);

        VisSelectBox<GameType> gameTypeSelectBox = new VisSelectBox<>();
        gameTypeSelectBox.setItems(GameType.values());
        gameTypeSelectBox.setSelected(GameType.ProtoServer);
        leftTable.add(gameTypeSelectBox).colspan(2).align(Align.left).row();

        VisLabel mapLabel = new VisLabel("map:");
        leftTable.add(mapLabel).align(Align.right);

        VisSelectBox<String> mapSelectBox = new VisSelectBox<>();
        mapSelectBox.setItems(gameMaster.gameLevelMaps);
//        mapSelectBox.setSelected(game.gameLevelMaps.random());
        leftTable.add(mapSelectBox).colspan(2).align(Align.left).row();

        VisTable rightTable = new VisTable();
        rootTable.add(rightTable);

        VisLabel versionLabel = new VisLabel(gameMaster.version.getVersionAndHash());
        rightTable.add(versionLabel).colspan(2).row();

        VisTextButton backButton = new VisTextButton("BACK");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameMaster.removeTopScreen();
            }
        });
        rightTable.add(backButton);

        VisTable midTable = new VisTable();
        rootTable.add(midTable);

        startServer = new VisTextButton("START SERVER");
        startServer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.logFuncStart("event:" + event + ", actor:" + actor);
                gameMaster.sessionSettings.host = hostField.getSelected();
                gameMaster.sessionSettings.authServerPort = Integer.parseInt(authServerPortField.getText());
                gameMaster.sessionSettings.gameServerPort = Integer.parseInt(gameServerPortField.getText());
                gameMaster.sessionSettings.gameSettings.gameType = gameTypeSelectBox.getSelected();
                gameMaster.sessionSettings.gameSettings.mapPath = mapSelectBox.getSelected();

                if (withControlCheckBox.isChecked()) {
                    gameMaster.userAccount.loginName = nameField.getText();
                    gameMaster.userAccount.factionName = factionSelectBox.getSelected();
                    gameMaster.sessionSettings.sessionType = SessionType.SERVER_AND_CLIENT;
                } else {
                    gameMaster.sessionSettings.sessionType = SessionType.SERVER_STANDALONE;
                }
                if (gameMaster.sessionSettings.gameSettings.gameType == GameType.ProtoServer) {
                    gameMaster.sessionSettings.sessionType = SessionType.SERVER_STANDALONE;
                    gameMaster.addScreen(new ProtoServerGameScreen(gameMaster, gameMaster.userAccount));
                } else {
                    gameMaster.addScreen(new ServerGameScreen(gameMaster, gameMaster.userAccount));
                }
            }
        });
        midTable.add(startServer).colspan(2).align(Align.center).row();

        withControlCheckBox = new VisCheckBox("WITH CONTROL", true);
        withControlCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playerSettings.setVisible(withControlCheckBox.isChecked());
            }
        });
        midTable.add(withControlCheckBox).colspan(2).align(Align.center).row();

        playerSettings = new VisTable();
        playerSettings.add(new VisLabel("name:"));
        nameField = new VisTextField("ClientByServer");
        playerSettings.add(nameField).align(Align.center).row();

        playerSettings.add(new VisLabel("faction:"));
        factionSelectBox = new VisSelectBox();
        factionSelectBox.setItems(gameMaster.factionsManager.getFactionsNames());
        playerSettings.add(factionSelectBox).row();
        midTable.add(playerSettings).colspan(2).align(Align.center).row();

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
            gameMaster.removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Logger.logDebug("isKeyJustPressed(Input.Keys.ENTER || Input.Keys.SPACE);");
            startServer.toggle();
        }
    }

    public static Array<String> getAddresses() {
        Array<String> addresses = new Array<>();
        try {
//            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
//            for (NetworkInterface ni : Collections.list(interfaces)){
//                for (InetAddress address : Collections.list(ni.getInetAddresses())) {
//                    if (address instanceof Inet4Address){
//                        if (!address.getHostAddress().equals("127.0.0.1")) {
//                            addresses.add(address.getHostAddress());
//                        }
//                    }
//                }
//            }
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)) {
                if (ni.isLoopback() || !ni.isUp())
                    continue;
                for (InetAddress address : Collections.list(ni.getInetAddresses())) {
                    if (address instanceof Inet4Address) {
                        Logger.logDebug("ni:" + ni, "ia:" + address);
                        if (!address.getHostAddress().equals("127.0.0.1")) {
                            addresses.add(address.getHostAddress());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.logDebug("addresses:" + addresses);
        return addresses;
    }
}
