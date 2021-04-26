package com.betmansmall.screens.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.betmansmall.GameMaster;
import com.betmansmall.game.Player;
import com.betmansmall.game.ProtoGameObject;
import com.betmansmall.game.gameInterface.ProtoController;
import com.betmansmall.game.gameLogic.Cell;
import com.betmansmall.game.gameLogic.Tower;
import com.betmansmall.game.gameLogic.Unit;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForTower;
import com.betmansmall.game.gameLogic.playerTemplates.TemplateForUnit;
import com.betmansmall.physics.BtContactListener;
import com.betmansmall.server.accouting.UserAccount;
import com.betmansmall.utils.logging.Logger;

public class ProtoGameScreen extends GameScreen {
    public PerspectiveCamera perspectiveCamera;
    public CameraInputController cameraInputController;

    public ModelBatch modelBatch;
    public Environment environment;
    public Model model;
    public ModelInstance modelInstance;

    public BitmapFont font = new BitmapFont();
    public BitmapFontCache fontCache = new BitmapFontCache(font, false);
    public SpriteBatch spriteBatch = new SpriteBatch();
    public final ScreenViewport uiViewport = new ScreenViewport();

    public ProtoController protoController;
    public Array<ProtoGameObject> instances;
    public ArrayMap<String, ProtoGameObject.Constructor> constructors;
    public float spawnTimer;

    private final static short GROUND_FLAG = 1 << 8;
    private final static short OBJECT_FLAG = 1 << 9;
    private final static short ALL_FLAG = -1;
    private float angle, speed = 90f;

    public btCollisionConfiguration collisionConfig;
    public btDispatcher dispatcher;
    public BtContactListener contactListener;
    public btBroadphaseInterface broadphase;
    public btDynamicsWorld dynamicsWorld;
    public btConstraintSolver constraintSolver;

    public ProtoGameScreen(GameMaster gameMaster, UserAccount userAccount) {
        super(gameMaster, userAccount);

        perspectiveCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        perspectiveCamera.position.set(10f, 10f, 10f);
        perspectiveCamera.lookAt(0,0,0);
//        perspectiveCamera.position.set(3f, 7f, 10f);
//        perspectiveCamera.lookAt(0,4f,0);
        perspectiveCamera.near = 1f;
        perspectiveCamera.far = 300f;
        perspectiveCamera.update();

        cameraInputController = new CameraInputController(perspectiveCamera);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(25f, 1f, 20f,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        modelInstance = new ModelInstance(model);
        modelInstance.transform.set(new Vector3(0f, -0.5f, 0f), new Quaternion(0f, 0f, 0f, 0f));

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "player";
        mb.part("player", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.WHITE))).box(1f, 1f, 1f);
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED))).box(10f, 1f, 10f);
        {
            mb.node().id = "sphere";
            mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(1f, 1f, 1f, 10, 10);
            mb.node().id = "box";
            mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE))).box(1f, 1f, 1f);
            mb.node().id = "cone";
            mb.part("cone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW))).cone(1f, 2f, 1f, 10);
            mb.node().id = "capsule";
            mb.part("capsule", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN))).capsule(0.5f, 2f, 10);
            mb.node().id = "cylinder";
            mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA))).cylinder(1f, 2f, 1f, 10);
        }
        model = mb.end();

        constructors = new ArrayMap<>(String.class, ProtoGameObject.Constructor.class);
        constructors.put("ground",      new ProtoGameObject.Constructor(model, "ground", new btBoxShape(new Vector3(5f, 0.5f, 5f)), 0f));
        constructors.put("sphere",      new ProtoGameObject.Constructor(model, "sphere", new btSphereShape(0.5f), 1f));
        constructors.put("box",         new ProtoGameObject.Constructor(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("cone",        new ProtoGameObject.Constructor(model, "cone", new btConeShape(0.5f, 2f), 1f));
        constructors.put("capsule",     new ProtoGameObject.Constructor(model, "capsule", new btCapsuleShape(.5f, 1f), 1f));
        constructors.put("cylinder",    new ProtoGameObject.Constructor(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f)), 1f));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
        contactListener = new BtContactListener();

        instances = new Array<ProtoGameObject>();
        ProtoGameObject object = constructors.get("ground").construct();
        object.body.setCollisionFlags(object.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        instances.add(object);
        dynamicsWorld.addRigidBody(object.body);
        object.body.setContactCallbackFlag(GROUND_FLAG);
        object.body.setContactCallbackFilter(0);
        object.body.setActivationState(Collision.DISABLE_DEACTIVATION);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();

//        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            protoController = new ProtoController(playersManager);
            inputMultiplexer.addProcessor(protoController);
//        }

        inputMultiplexer.addProcessor(cameraInputController);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void spawn() {
        ProtoGameObject obj = constructors.values[1 + MathUtils.random(constructors.size - 2)].construct();
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.proceedToTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(obj);
        dynamicsWorld.addRigidBody(obj.body);
        obj.body.setContactCallbackFlag(OBJECT_FLAG);
        obj.body.setContactCallbackFilter(GROUND_FLAG);
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        super.dispose();
        for (ProtoGameObject obj : this.instances) {
            obj.dispose();
        }
        this.instances.clear();
        for (ProtoGameObject.Constructor ctor : this.constructors.values()) {
            ctor.dispose();
        }
        this.constructors.clear();
        this.dynamicsWorld.dispose();
        this.constraintSolver.dispose();
        this.broadphase.dispose();
        this.dispatcher.dispose();
        this.collisionConfig.dispose();
        this.contactListener.dispose();
        this.modelBatch.dispose();
        this.model.dispose();
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
        modelBatch.render(modelInstance, environment);
        for (Player player : playersManager.getPlayers()) {
            if (player != null && player.gameObject != null) {
                modelBatch.render(player.gameObject, environment);
            }
        }
        modelBatch.end();
        renderText(delta);

        angle = (angle + delta * speed) % 360f;
        instances.get(0).transform.setTranslation(0, MathUtils.sinDeg(angle) * 2.5f, 0f);

        dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);

        if ((spawnTimer -= delta) < 0) {
            spawn();
            spawnTimer = 1.5f;
        }

        modelBatch.begin(perspectiveCamera);
        modelBatch.render(instances, environment);
        modelBatch.end();

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
