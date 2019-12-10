package com.betmansmall.screens.client;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.enums.SessionType;
import com.betmansmall.screens.AbstractScreen;
import com.betmansmall.util.logging.Logger;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.net.InetAddress;

public class ClientSettingsScreen extends AbstractScreen {
    private Stage stage;
    private VisTextButton connectToServer;

    public ClientSettingsScreen(GameMaster gameMaster) {
        super(gameMaster);
        createUI();
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }

    public Array<String> getHosts(String subnet) {
        Array<String> hosts = new Array<>();
        try {
            int timeout = 10;
            for (int i = 1; i < 255; i++) {
                String host = subnet + "." + i;
                if (InetAddress.getByName(host).isReachable(timeout)) {
                    hosts.add(host);
                }
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return hosts;
    }

    public void createUI() {
        this.stage = new Stage(new ScreenViewport());
//        this.stage.setDebugAll(true);

        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);

        VisLabel hostLabel = new VisLabel("host:");
        rootTable.add(hostLabel);

//        Array<String> hosts = getHosts("192.168.0");
//        VisSelectBox<String> hostField = new VisSelectBox<>();
//        hostField.setItems(hosts);
//        hostField.setSelected(hosts.peek());
//        rootTable.add(hostField).row();

        String host = "localhost";
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            host = "192.168.0.";
        } else if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            host = "127.0.0.1";
        }
        VisTextField hostField = new VisTextField(host);
        rootTable.add(hostField).row();

        VisLabel portLabel = new VisLabel("port:");
        rootTable.add(portLabel);

        VisTextField portField = new VisTextField("48999");
        rootTable.add(portField).row();

        rootTable.add(new VisLabel("name:"));
        VisTextField nameField = new VisTextField("Player");
        rootTable.add(nameField).row();

        rootTable.add(new VisLabel("faction:"));

        VisSelectBox<String> factionSelectBox = new VisSelectBox();
        factionSelectBox.setItems(game.factionsManager.getFactionsNames());
        rootTable.add(factionSelectBox).row();

        connectToServer = new VisTextButton("CONNECT TO SERVER");
        connectToServer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                game.sessionSettings.host = hostField.getSelected();
                game.sessionSettings.host = hostField.getText();
                game.sessionSettings.port = Integer.parseInt(portField.getText());
                game.sessionSettings.sessionType = SessionType.CLIENT_ONLY;

                game.userAccount.loginName = nameField.getText();
                game.userAccount.factionName = factionSelectBox.getSelected();
                game.addScreen(new ClientGameScreen(game, game.userAccount));
            }
        });
        rootTable.add(connectToServer).colspan(2);

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { //
            Logger.logDebug("isKeyJustPressed(Input.Keys.BACK || Input.Keys.ESCAPE);");
            game.removeTopScreen();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Logger.logDebug("isKeyJustPressed(Input.Keys.ENTER || Input.Keys.SPACE);");
            connectToServer.toggle();
        }
    }
}
