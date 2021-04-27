package com.betmansmall.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.betmansmall.game.gameInterface.ProtoController;
import com.betmansmall.physics.BtMotionState;

import protobuf.Proto;

public class ProtoGameObject extends ModelInstance implements Disposable {
    public static class Constructor implements Disposable {
        public Model model;
        public String node;
        public btCollisionShape shape;
        public btRigidBody.btRigidBodyConstructionInfo constructionInfo;
        private static final Vector3 localInertia = new Vector3();

        public Constructor(Model model, btCollisionShape shape, float mass) {
            init(model, null, shape, mass);
        }

        public Constructor(Model model, String node, btCollisionShape shape, float mass) {
            init(model, node, shape, mass);
        }

        private void init(Model model, String node, btCollisionShape shape, float mass) {
            this.model = model;
            this.node = node;
            this.shape = shape;
            if (mass > 0f) {
                shape.calculateLocalInertia(mass, localInertia);
            } else {
                localInertia.set(0, 0, 0);
            }
            this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
        }

        public ProtoGameObject construct() {
            if (node != null) {
                return new ProtoGameObject(model, node, constructionInfo);
            } else {
                return new ProtoGameObject(model, constructionInfo);
            }
        }

        @Override
        public void dispose() {
            shape.dispose();
            constructionInfo.dispose();
        }
    }

    private float velocity = 5f;
    private final Vector3 forwardDirection = new Vector3(0, 0, -1);
    private final Vector3 upDirection = new Vector3(0, 1, 0);
    private final Vector3 tmp = new Vector3();

    public Proto.Transform protoTransform;
    public Vector3 position;
    public Quaternion rotation;

    public BtMotionState motionState;
    public btRigidBody body;

    public ProtoGameObject(Model model, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        super(model);
        init(constructionInfo);
    }

    public ProtoGameObject(Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        super(model, node);
        init(constructionInfo);
    }
    private void init(btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        this.protoTransform = Proto.Transform.newBuilder()
                .setPosition(Proto.Position.newBuilder().setY(0.5f))
                .setRotation(Proto.Rotation.newBuilder())
                .build();
        this.position = new Vector3();
        this.rotation = new Quaternion();

        motionState = new BtMotionState();
        motionState.transform = transform;
        body = new btRigidBody(constructionInfo);
        body.setMotionState(motionState);
    }

    @Override
    public void dispose() {
        this.body.dispose();
        this.motionState.dispose();
    }

    public void update(ProtoController protoController) {
        update(Gdx.graphics.getDeltaTime(), protoController);
    }

    public void update(float deltaTime, ProtoController protoController) {
//        float speed = movementSpeed;
        boolean forward, back, strafeLeft, strafeRight, rightControl, up, down;
        if (protoController != null) {
            forward = protoController.isUpPressed();
            back = protoController.isDownPressed();
            strafeLeft = protoController.isLeftPressed();
            strafeRight = protoController.isRightPressed();
        } else {
            forward = Gdx.input.isKeyPressed(Input.Keys.UP);
            back = Gdx.input.isKeyPressed(Input.Keys.DOWN);
            strafeLeft = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            strafeRight = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        }
        rightControl = Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
        up = (rightControl && forward);
        down = (rightControl && back);
//        boolean rotateLeft = (rightControl && strafeLeft);
//        boolean rotateRight = (rightControl && strafeRight);
//        if ((forward | back) & (strafeRight | strafeLeft)) {
//            speed /= Math.sqrt(2);
//        }
//        Logger.logDebug("forward:" + forward, "back:" + back);
//        Logger.logDebug("strafeLeft:" + strafeLeft, "strafeRight:" + strafeRight);
//        Logger.logDebug("1transform:" + transform, "tmp:" + tmp);
        tmp.setZero();

//        float deltaX = 0f, deltaY = 0f;

//        if (rotateLeft) {
//            deltaY = -1f;
////            forwardDirection.rotate(upDirection, deltaX * velocity);
////            tmp.set(forwardDirection).crs(upDirection).nor();
////            forwardDirection.rotate(tmp, deltaY * velocity);
//            rotation.add(0, deltaY, 0, 0);
//        } else if (rotateRight) {
//            deltaY = 1f;
////            forwardDirection.rotate(upDirection, deltaX * velocity);
////            tmp.set(forwardDirection).crs(upDirection).nor();
////            forwardDirection.rotate(tmp, deltaY * velocity);
//            rotation.add(0, deltaY, 0, 0);
//        }

        if (forward && !up) {
            tmp.add(forwardDirection).nor().scl(deltaTime * velocity);
        }
        if (back && !down) {
            tmp.add(forwardDirection).nor().scl(-deltaTime * velocity);
        }
        if (strafeLeft) {
            tmp.add(forwardDirection).crs(upDirection).nor().scl(-deltaTime * velocity);
        }
        if (strafeRight) {
            tmp.add(forwardDirection).crs(upDirection).nor().scl(deltaTime * velocity);
        }
        if (up) {
            tmp.add(upDirection).nor().scl(deltaTime * velocity);
        }
        if (down) {
            tmp.add(upDirection).nor().scl(-deltaTime * velocity);
        }
        position.add(tmp);
        transform.set(position, rotation);
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_0)) {
            position.setZero();
            rotation.set(0f, 0f, 0f, 0f);
            transform.set(position, rotation);
//            Logger.logDebug("transform:" + transform);
        }

//        rotation.add()
//        Logger.logDebug("2transform:" + transform, "tmp:" + tmp);
//        translation.set(baseDirection).rot(player.modelInstance.transform).nor().scl(speed*delta);
//        Logger.logDebug("translation:" + translation);
//        player.modelInstance.transform.translate(translation);
//        Logger.logDebug("player.modelInstance.transform:" + player.modelInstance.transform);
    }

    public void updateData(Proto.SendObject sendObject) {
        this.protoTransform = sendObject.getTransform();
        Proto.Position position = protoTransform.getPosition();
        Proto.Rotation rotation = protoTransform.getRotation();
        this.position.set(position.getX(), position.getY(), position.getZ());
        this.rotation.set(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW());
        this.transform.set(this.position, this.rotation);
    }
}
