package com.betmansmall.screens.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.screens.client.GameScreen;
import com.betmansmall.server.AuthServerThread;
import com.betmansmall.server.ProtoServerSessionThread;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.server.data.BuildTowerData;
import com.betmansmall.server.data.CreateUnitData;
import com.betmansmall.server.data.GameFieldVariablesData;
import com.betmansmall.server.data.RemoveTowerData;
import com.betmansmall.server.data.SendObject;
import com.betmansmall.server.data.UnitsManagerData;
import com.betmansmall.utils.logging.Logger;

public class ProtoServerGameScreen extends GameScreen {
//    public AuthServerThread authServerThread;
    public ProtoServerSessionThread protoServerSessionThread;

    public PerspectiveCamera perspectiveCamera;
    public CameraInputController cameraInputController;

    public ModelBatch modelBatch;
    public Environment environment;
    public ModelInstance modelInstance;

    public ProtoServerGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);
        Logger.logFuncStart();

//        this.authServerThread = new AuthServerThread(this);
        this.protoServerSessionThread = new ProtoServerSessionThread(this);

//        this.authServerThread.start();
        this.protoServerSessionThread.start();
        super.initGameField();

        perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(10f, 10f, 10f);
        perspectiveCamera.lookAt(0,0,0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();

        cameraInputController = new CameraInputController(perspectiveCamera);
        Gdx.input.setInputProcessor(cameraInputController);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(50f, 1f, 50f,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelInstance = new ModelInstance(model);
        modelInstance.transform.set(new Vector3(0f, -0.5f, 0f), new Quaternion(0f, 0f, 0f, 0f));

//        instance.transform.set(new Vector3(0f, 0f, 0f), new Quaternion(0f, 0, 0f, 0f));
//        instance.transform.set(new Vector3(0f, 0f, 0f), new Quaternion(1f, 1f, 1f, 1f));
//        instance.transform.set(new Quaternion((float)Math.random(), (float)Math.random(), (float)Math.random(), (float)Math.random()));
//        instance.transform.setToLookAt(Vector3.Y, new Vector3((float)Math.random(), (float)Math.random(), (float)Math.random()));

//        cameraController.camera.position.set(0f, 0f, 100f);
//        cameraController.camera.position.z = 100f;

        Logger.logFuncEnd();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
//        this.authServerThread.dispose();
        this.protoServerSessionThread.dispose();

        this.modelBatch.dispose();
    }

    @Override
    public void render(float delta) {
        cameraInputController.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        super.render(delta);
//        Logger.logFuncStart();

//        instance.transform.set(new Quaternion((float)Math.random(), (float)Math.random(), (float)Math.random(), (float)Math.random()));
//        instance.transform.setToLookAt(new Vector3((float)Math.random(), (float)Math.random(), (float)Math.random()), new Vector3((float)Math.random(), (float)Math.random(), (float)Math.random()));
        modelBatch.begin(perspectiveCamera);
        modelBatch.render(modelInstance, environment);
        for (Player player : playersManager.getPlayers()) {
            if (player.modelInstance != null) {
                modelBatch.render(player.modelInstance, environment);
            }
        }
        modelBatch.end();
    }

    @Override
    public boolean spawnUnitFromServerScreenByWaves() {
        return true;
    }

    @Override
    public void sendGameFieldVariables() {
        Logger.logFuncStart();
    }

    @Override
    public Tower createTowerWithGoldCheck(int buildX, int buildY, TemplateForTower templateForTower) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY, "templateForTower:" + templateForTower);
        return null;
    }

    @Override
    public Tower createTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        return null;
    }

    @Override
    public boolean removeTower(int buildX, int buildY) {
        Logger.logFuncStart("buildX:" + buildX, "buildY:" + buildY);
        return false;
    }

    public Unit createUnit(Cell spawnCell, Cell destCell, TemplateForUnit templateForUnit, Cell exitCell, Player player) {
        Logger.logFuncStart("spawnCell:" + spawnCell, "destCell:" + destCell, "templateForUnit:" + templateForUnit, "exitCell:" + exitCell, "player:" + player);
        return null;
    }
}
