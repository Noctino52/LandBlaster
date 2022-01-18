package com.game.ivan.landblaster;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.DistanceJointDef;
import com.google.fpl.liquidfun.Joint;

/**
 *
 * Created by mfaella on 27/02/16.
 */
public class MyDistanceJoint
{
    Joint joint;


    public MyDistanceJoint(GameWorld gw, Body a, Body b,int id)
    {
        DistanceJointDef jointDef = new DistanceJointDef();
        jointDef.setBodyA(a);
        jointDef.setBodyB(b);
        jointDef.setLocalAnchorA(0f, 0f);
        if(id==1)jointDef.setLocalAnchorB(0f, 0f);
        jointDef.setLength(2f);
        jointDef.setFrequencyHz(20);
        joint = gw.world.createJoint(jointDef);

        jointDef.delete();
    }
}
