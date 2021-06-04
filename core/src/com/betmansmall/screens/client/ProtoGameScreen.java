package com.betmansmall.screens.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.game.PhysicsObjectManager;
import com.betmansmall.game.Player;
import com.betmansmall.game.gameInterface.ProtoController;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.utils.logging.Logger;

public class ProtoGameScreen extends GameScreen {
    public PerspectiveCamera perspectiveCamera;
    public CameraInputController cameraInputController;

    public ModelBatch modelBatch;
    public Environment environment;

    public BitmapFont font = new BitmapFont();
    public BitmapFontCache fontCache = new BitmapFontCache(font, false);
    public SpriteBatch spriteBatch = new SpriteBatch();
    public final ScreenViewport uiViewport = new ScreenViewport();

    public ProtoController protoController;

    public PhysicsObjectManager physicsObjectManager;

    public ProtoGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);

        perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(10f, 10f, 10f);
        perspectiveCamera.lookAt(0,0,0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();

        cameraInputController = new CameraInputController(perspectiveCamera);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
//        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            protoController = new ProtoController(playersManager);
            inputMultiplexer.addProcessor(protoController);
//        }
        inputMultiplexer.addProcessor(cameraInputController);
        Gdx.input.setInputProcessor(inputMultiplexer);

        this.physicsObjectManager = new PhysicsObjectManager();
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        this.modelBatch.dispose();
    }

    @Override
    public void render(float delta) {
//        final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        cameraInputController.update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        super.render(delta);

        modelBatch.begin(perspectiveCamera);
//        modelBatch.render(modelInstance, environment);
//        for (Player player : playersManager.getPlayers()) {
//            if (player != null && player.gameObject != null) {
//                modelBatch.render(player.gameObject, environment);
//            }
//        }

        modelBatch.render(physicsObjectManager.instances, environment);
        modelBatch.end();
        renderText(delta);

        if (protoController != null) {
            protoController.act();
            protoController.draw();
        }
    }

    public void renderText(float delta) {
        for (Player player : playersManager.getPlayers()) {
            if (player != null && player.gameObject != null) {
                final Vector3 clipSpacePos = new Vector3(player.gameObject.position);
                final float w = multiplyProjective(perspectiveCamera.combined, clipSpacePos, 1f);
                final float textPosX = clipSpacePos.x * Gdx.graphics.getWidth() * 0.5f;
                final float textPosY = clipSpacePos.y * Gdx.graphics.getHeight() * 0.5f;
                fontCache.setText(player.playerID.toString() + "\n" + player.gameObject.position, 0f, 20f, 0f, Align.center, false);
                final float fontSize = 30f;
                final float fontScale = fontSize / w;
                final int regionCount = font.getRegions().size;
                for (int page = 0; page < regionCount; page++) {
                    final int vertexCount = fontCache.getVertexCount(page);
                    final float[] vertices = fontCache.getVertices(page);
                    for (int v = 0; v < vertexCount; v += 5) {
                        vertices[v] = vertices[v] * fontScale + textPosX;
                        vertices[v + 1] = vertices[v + 1] * fontScale + textPosY;
                    }
                }
                uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                spriteBatch.setProjectionMatrix(uiViewport.getCamera().projection);
                spriteBatch.begin();
                fontCache.draw(spriteBatch);
                spriteBatch.end();
            }
        }
    }

    private static float multiplyProjective(Matrix4 m, Vector3 v, float vW) {
        final float[] mat = m.val;
        final float x = v.x * mat[Matrix4.M00] + v.y * mat[Matrix4.M01] + v.z * mat[Matrix4.M02] + vW * mat[Matrix4.M03];
        final float y = v.x * mat[Matrix4.M10] + v.y * mat[Matrix4.M11] + v.z * mat[Matrix4.M12] + vW * mat[Matrix4.M13];
        final float z = v.x * mat[Matrix4.M20] + v.y * mat[Matrix4.M21] + v.z * mat[Matrix4.M22] + vW * mat[Matrix4.M23];
        final float w = v.x * mat[Matrix4.M30] + v.y * mat[Matrix4.M31] + v.z * mat[Matrix4.M32] + vW * mat[Matrix4.M33];
        final float iw = 1f / w;
        v.set(x * iw, y * iw, z * iw);
        return w;
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
