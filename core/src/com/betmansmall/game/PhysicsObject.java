package com.betmansmall.game;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.betmansmall.physics.BtMotionState;

public class PhysicsObject implements Disposable {
//    public static class Constructor implements Disposable {
//        public Model model;
//        public String node;
//        public btCollisionShape shape;
//        public btRigidBody.btRigidBodyConstructionInfo constructionInfo;
//        private static final Vector3 localInertia = new Vector3();
//        public Constructor(Model model, btCollisionShape shape, float mass) {
//            init(model, null, shape, mass);
//        }
//        public Constructor(Model model, String node, btCollisionShape shape, float mass) {
//            init(model, node, shape, mass);
//        }
//        private void init(Model model, String node, btCollisionShape shape, float mass) {
//            this.model = model;
//            this.node = node;
//            this.shape = shape;
//            if (mass > 0f) {
//                shape.calculateLocalInertia(mass, localInertia);
//            } else {
//                localInertia.set(0, 0, 0);
//            }
//            this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
//        }
//        public ProtoGameObject construct() {
//            if (node != null) {
//                return new ProtoGameObject(model, node, constructionInfo);
//            } else {
//                return new ProtoGameObject(model, constructionInfo);
//            }
//        }
//        @Override
//        public void dispose() {
//            shape.dispose();
//            constructionInfo.dispose();
//        }
//    }
    public BtMotionState motionState;
    public btRigidBody body;
    public ModelInstance modelInstance;

    public PhysicsObject(ModelInstance modelInstance, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
        this.modelInstance = modelInstance;
        this.motionState = new BtMotionState();
        this.motionState.transform = modelInstance.transform;
        this.body = new btRigidBody(constructionInfo);
        this.body.setMotionState(motionState);
    }

    @Override
    public void dispose() {
        this.body.dispose();
        this.motionState.dispose();
    }
}
