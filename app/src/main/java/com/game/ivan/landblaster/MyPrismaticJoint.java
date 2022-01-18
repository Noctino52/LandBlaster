package com.game.ivan.landblaster;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.PrismaticJointDef;

/**
 *
 * Created by mfaella on 27/02/16.
 */
public class MyPrismaticJoint
{
    Joint joint;

    public MyPrismaticJoint(GameWorld gw, Body a, Body b,int id)
    {
        PrismaticJointDef jointDef = new PrismaticJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(1, 0);
        if(id==1)jointDef.setLocalAnchorB(2f, 1.5f);
        else jointDef.setLocalAnchorB(-2f, -1.5f);
        jointDef.setLocalAxisA(1f,0f);
        // add friction
        jointDef.setEnableMotor(true);
        jointDef.setMotorSpeed(0f);
        jointDef.setMaxMotorForce(10f);
        joint = gw.world.createJoint(jointDef);

        jointDef.delete();
    }
}
