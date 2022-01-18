package com.game.ivan.landblaster;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.RevoluteJointDef;

/**
 *
 * Created by mfaella on 27/02/16.
 */
public class MyRevoluteJoint
{
    Joint joint;

    public MyRevoluteJoint(GameWorld gw, Body a, Body b,int id)
    {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(-0.4f, 0f);
        jointDef.setLocalAnchorB(0, 0);
        // add friction
        jointDef.setEnableMotor(true);
        jointDef.setMotorSpeed(0f);
        jointDef.setMaxMotorTorque(10f);
        joint = gw.world.createJoint(jointDef);

        jointDef.delete();
    }
}
