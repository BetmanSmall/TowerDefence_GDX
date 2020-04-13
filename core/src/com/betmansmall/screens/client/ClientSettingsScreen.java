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
import com.betmansmall.utils.AbstractScreen;
import com.betmansmall.server.ServerInformation;
import com.betmansmall.server.ServersSearchThread;
import com.betmansmall.server.data.NetworkPackage;
import com.betmansmall.utils.logging.Logger;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

import java.util.List;

public class ClientSettingsScreen extends AbstractScreen {
    private Stage stage;
    private VisTextField nameField;
    private VisSelectBox<String> factionSelectBox;
    public VisLabel currentSearchLabel;
    private VisTable serverBrowserTable;
    private VisTextButton connectToServer;

    private Array<ServersSearchThread> serversSearchThreads;

    public ClientSettingsScreen(GameMaster gameMaster) {
        super(gameMaster);
        createUI();
        this.serversSearchThreads = new Array<>();
        for (int k = 0; k <= 10; k++) {
            this.serversSearchThreads.add(new ServersSearchThread((25*k)+1, (25*k)+25, this));
            this.serversSearchThreads.peek().start();
        }
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        for (ServersSearchThread serversSearchThread : serversSearchThreads) {
            serversSearchThread.dispose();
        }
        serversSearchThreads.clear();
        this.stage.dispose();
    }

    public void createUI() {
        this.stage = new Stage(new ScreenViewport());
        this.stage.setDebugTableUnderMouse(true);

        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);

        VisTable leftTable = new VisTable();
        rootTable.add(leftTable);

        currentSearchLabel = new VisLabel("currentSearch:");
        leftTable.add(currentSearchLabel).bottom().row();

        serverBrowserTable = new VisTable();
        leftTable.add(serverBrowserTable).top().row();

        VisTable rightTable = new VisTable();
        rootTable.add(rightTable);

        VisLabel versionLabel = new VisLabel(game.version.getVersionAndHash());
        rightTable.add(versionLabel).colspan(2).row();

        VisTextButton backButton = new VisTextButton("BACK");
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.removeTopScreen();
            }
        });
        rightTable.add(backButton);

        VisTable midTable = new VisTable();
        rootTable.add(midTable);

        midTable.add(new VisLabel("name:"));
        if (game != null && game.cmd != null) {
            nameField = new VisTextField("Player_Desktop" + (game.cmd.hasOption("client") ? game.cmd.getOptionValue("client") : "_"));
        } else {
            nameField = new VisTextField("Player_Android");
        }
        midTable.add(nameField).row();

        midTable.add(new VisLabel("faction:"));
        factionSelectBox = new VisSelectBox();
        factionSelectBox.setItems(game.factionsManager.getFactionsNames());
        midTable.add(factionSelectBox).row();

        VisLabel hostLabel = new VisLabel("host:");
        midTable.add(hostLabel);

        String host = "localhost";
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            host = "192.168.0.";
        } else if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            host = "127.0.0.1";
        }
        VisTextField hostField = new VisTextField(host);
        midTable.add(hostField).row();

        VisLabel portLabel = new VisLabel("gameServerPort:");
        midTable.add(portLabel);

        VisTextField portField = new VisTextField(game.sessionSettings.gameServerPort.toString());
        midTable.add(portField).row();

        connectToServer = new VisTextButton("CONNECT TO SERVER");
        connectToServer.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                game.sessionSettings.host = hostField.getSelected();
                game.sessionSettings.host = hostField.getText();
                game.sessionSettings.gameServerPort = Integer.parseInt(portField.getText());
                game.sessionSettings.sessionType = SessionType.CLIENT_ONLY;

                game.userAccount.loginName = nameField.getText();
                game.userAccount.factionName = factionSelectBox.getSelected();
                game.addScreen(new ClientGameScreen(game, game.userAccount));
            }
        });
        midTable.add(connectToServer).colspan(2).row();

        stage.addActor(rootTable);
    }

    public synchronized void addSimpleHost(String host) {
        serverBrowserTable.add(new VisLabel(host)).row();
    }

    public synchronized void addServerBaseInfo(String host, List<NetworkPackage> networkPackages) {
        VisTable serverInfoRowTable = new VisTable();

        ServerInformation serverInformation = new ServerInformation(host, networkPackages);

        VisLabel hostLabel = new VisLabel(serverInformation.inetSocketAddress.toString());
        serverInfoRowTable.add(hostLabel);

        VisLabel mapPathLabel = new VisLabel("mapPath:" + serverInformation.mapPath);
        serverInfoRowTable.add(mapPathLabel).row();

        VisLabel versionLabel = new VisLabel(serverInformation.versionAndGitHash);
        serverInfoRowTable.add(versionLabel);

        VisLabel gameTypeLabel = new VisLabel("gameType:" + serverInformation.gameType);
        serverInfoRowTable.add(gameTypeLabel).row();

        VisLabel playersCountLabel = new VisLabel("playersCount:" + serverInformation.playersSize);
        serverInfoRowTable.add(playersCountLabel);

        VisTextButton joinBtn = new VisTextButton("JOIN");
        joinBtn.setUserObject(serverInformation);
        joinBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ServerInformation serverInformation = (ServerInformation)actor.getUserObject();
                Logger.logFuncStart("serverInformation:" + serverInformation);
                game.sessionSettings.host = serverInformation.inetSocketAddress.getHostString();
                game.sessionSettings.gameServerPort = serverInformation.inetSocketAddress.getPort();
                game.sessionSettings.sessionType = SessionType.CLIENT_ONLY;

                game.userAccount.loginName = nameField.getText();
                game.userAccount.factionName = factionSelectBox.getSelected();
                game.addScreen(new ClientGameScreen(game, game.userAccount));
            }
        });
        serverInfoRowTable.add(joinBtn).row();

        serverBrowserTable.add(serverInfoRowTable).row();
    }

//    public synchronized void setProgressSearch(String text) {
//        currentSearchLabel.setText(text);
//    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Logger.logFuncStart();
        for (ServersSearchThread serversSearchThread : serversSearchThreads) {
            serversSearchThread.dispose();
        }
        serversSearchThreads.clear();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
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
