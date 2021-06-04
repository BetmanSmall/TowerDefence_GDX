package com.betmansmall.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.betmansmall.physics.BtContactListener;
import com.betmansmall.utils.logging.Logger;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

import protobuf.Proto;

public class PhysicsObjectManager implements Disposable {
    private final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
    private final static short GROUND_FLAG = 1 << 8;
    private final static short OBJECT_FLAG = 1 << 9;
    private final static short ALL_FLAG = -1;
    private float angle, speed = 90f;
    public float spawnTimer;

    public Model simpleModel;
    public ArrayMap<String, ProtoGameObject.Constructor> constructors;
    public btCollisionConfiguration collisionConfig;
    public btDispatcher dispatcher;
    public BtContactListener contactListener;
    public btBroadphaseInterface broadphase;
    public btDynamicsWorld dynamicsWorld;
    public btConstraintSolver constraintSolver;
    public Model modelGround;
    public Model modelPlayer;
    public Array<ProtoGameObject> instances;
    public HashMap<String, ProtoGameObject> instances2 = new LinkedHashMap<>();

    public PhysicsObjectManager() {
        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "player";
        mb.part("player", GL20.GL_TRIANGLES, attributes, new Material(ColorAttribute.createDiffuse(Color.WHITE))).box(1f, 1f, 1f);
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, attributes, new Material(ColorAttribute.createDiffuse(Color.RED))).box(5f, 1f, 5f);

        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, attributes, new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, attributes, new Material(ColorAttribute.createDiffuse(Color.BLUE))).box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, attributes, new Material(ColorAttribute.createDiffuse(Color.YELLOW))).cone(1f, 2f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, attributes, new Material(ColorAttribute.createDiffuse(Color.CYAN))).capsule(0.5f, 2f, 10);
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, attributes, new Material(ColorAttribute.createDiffuse(Color.MAGENTA))).cylinder(1f, 2f, 1f, 10);
        simpleModel = mb.end();

        constructors = new ArrayMap<>(String.class, ProtoGameObject.Constructor.class);
        constructors.put("player",      new ProtoGameObject.Constructor(simpleModel, "player", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("ground",      new ProtoGameObject.Constructor(simpleModel, "ground", new btBoxShape(new Vector3(5f, 0.5f, 5f)), 0f));
        constructors.put("sphere",      new ProtoGameObject.Constructor(simpleModel, "sphere", new btSphereShape(0.5f), 1f));
        constructors.put("box",         new ProtoGameObject.Constructor(simpleModel, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f));
        constructors.put("cone",        new ProtoGameObject.Constructor(simpleModel, "cone", new btConeShape(0.5f, 2f), 1f));
        constructors.put("capsule",     new ProtoGameObject.Constructor(simpleModel, "capsule", new btCapsuleShape(.5f, 1f), 1f));
        constructors.put("cylinder",    new ProtoGameObject.Constructor(simpleModel, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f)), 1f));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        constraintSolver = new btSequentialImpulseConstraintSolver();
        dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
        dynamicsWorld.setGravity(new Vector3(0, -10f, 0));
        contactListener = new BtContactListener();

        ModelBuilder modelBuilder = new ModelBuilder();
        modelGround = modelBuilder.createBox(25f, 0.5f, 25f, new Material(ColorAttribute.createDiffuse(Color.BROWN)), attributes);
        modelPlayer = modelBuilder.createBox(1f, 1f, 1f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), attributes);

        instances = new Array<>();
        ProtoGameObject object = constructors.get("ground").construct();
        object.physicsObject.body.setCollisionFlags(object.physicsObject.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        object.physicsObject.body.setContactCallbackFlag(GROUND_FLAG);
        object.physicsObject.body.setContactCallbackFilter(0);
        object.physicsObject.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        object.index = 1;
        object.uuid = UUID.randomUUID().toString();
        instances.add(object);
        dynamicsWorld.addRigidBody(object.physicsObject.body);

        ProtoGameObject object2 = new ProtoGameObject.Constructor(modelGround, new btBoxShape(new Vector3(12.5f, 0.25f, 12.5f)), 0f).construct();
        object2.physicsObject.body.setCollisionFlags(object.physicsObject.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
        object2.physicsObject.body.setContactCallbackFlag(GROUND_FLAG);
        object2.physicsObject.body.setContactCallbackFilter(0);
        object2.physicsObject.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        object2.transform.set(new Vector3(0f, -0.5f, 0f), new Quaternion(0f, 0f, 0f, 0f));
        instances.add(object2);
        dynamicsWorld.addRigidBody(object2.physicsObject.body);
    }

    @Override
    public void dispose() {
        Logger.logFuncStart();
        this.simpleModel.dispose();
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

        this.modelGround.dispose();
        this.modelPlayer.dispose();

        for (ProtoGameObject obj : this.instances) {
            obj.dispose();
        }
        this.instances.clear();
    }

    public ProtoGameObject spawnPlayer(Player player) {
        ProtoGameObject protoGameObject = new ProtoGameObject.Constructor(modelPlayer, new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f)), 1f).construct();
//        protoGameObject.position = player.
        addToPhysic(protoGameObject);
        return protoGameObject;
    }

    public void addByServer() {
        int index = 2 + MathUtils.random(constructors.size - 3);
        ProtoGameObject obj = constructors.values[index].construct();
        obj.position = new Vector3(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.rotation = new Quaternion().setEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.uuid = UUID.randomUUID().toString();
        obj.index = index;
        addToPhysic(obj);
//        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
//        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
    }

    public ProtoGameObject addByClient(Proto.SendObject sendObject) {
        ProtoGameObject protoGameObject = constructors.values[sendObject.getIndex()].construct();
        protoGameObject.uuid = sendObject.getUuid();
        protoGameObject.updateData(sendObject);
        addToPhysic(protoGameObject);
        return protoGameObject;
    }

    private void addToPhysic(ProtoGameObject obj) {
//        obj.transform.setFromEulerAngles(obj.rotation.x, obj.rotation.y, obj.rotation.z);
//        obj.transform.trn(obj.position);
        obj.transform.set(obj.position, obj.rotation);
        obj.physicsObject.body.proceedToTransform(obj.transform);
        obj.physicsObject.body.setUserValue(instances.size);
        obj.physicsObject.body.setCollisionFlags(obj.physicsObject.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        obj.physicsObject.body.setContactCallbackFlag(OBJECT_FLAG);
        obj.physicsObject.body.setContactCallbackFilter(GROUND_FLAG);
        instances.add(obj);
        instances2.put(obj.uuid, obj);
        dynamicsWorld.addRigidBody(obj.physicsObject.body);
    }

    public void update(float delta) {
        angle = (angle + delta * speed) % 360f;
        instances.get(0).transform.setTranslation(0, MathUtils.sinDeg(angle) * 2.5f, 0f);

        dynamicsWorld.stepSimulation(delta, 5, 1f / 60f);

        if ((spawnTimer -= delta) < 0) {
            addByServer();
            spawnTimer = 1.5f;
        }
    }
}
