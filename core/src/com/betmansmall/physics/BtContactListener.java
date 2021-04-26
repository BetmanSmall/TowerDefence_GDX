package com.betmansmall.physics;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;

public class BtContactListener extends ContactListener {
    @Override
    public boolean onContactAdded(int userValue0, int partId0, int index0, boolean match0, int userValue1, int partId1, int index1, boolean match1) {
//        Logger.logFuncStart();
//            if (match0)
//                ((ColorAttribute) instances.get(userValue0).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
//            if (match1)
//                ((ColorAttribute) instances.get(userValue1).materials.get(0).get(ColorAttribute.Diffuse)).color.set(Color.WHITE);
        return true;
    }
}
