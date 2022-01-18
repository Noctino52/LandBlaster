//
// Decompiled by Procyon v0.5.36
// 
/*
package com.example.mfaella.physicsapp;

import android.graphics.Bitmap;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.World;

public class LoadRUBE_car2 extends GameObject
{
    Body[] wheelBodies;
    int driveState;
    
    public LoadRUBE_car2() {
        this.wheelBodies = null;
        this.driveState = 0;
    }
    
    @Override
    public boolean isSaveLoadEnabled() {
        return true;
    }
    
    @Override
    public void initTest(final boolean argDeserialized) {
        if (argDeserialized) {
            return;
        }
        final Jb2dJson json = new Jb2dJson();
        final StringBuilder errorMsg = new StringBuilder();
        final World world = json.readFromFile("car2.json", errorMsg);
        if (null != world) {
            this.m_world = world;
            this.groundBody = this.m_world.createBody(new BodyDef());
            this.m_world.setDestructionListener(this.destructionListener);
            this.m_world.setContactListener(this);
            this.m_world.setDebugDraw(this.model.getDebugDraw());
            this.wheelBodies = json.getBodiesByName("carwheel");
        }
        else {
            System.out.println(errorMsg);
        }
    }
    
    void updateDriveState() {
        if (this.wheelBodies.length == 0) {
            return;
        }
        final float maxSpeed = 16.0f;
        float desiredSpeed = 0.0f;
        if (this.driveState == 1) {
            desiredSpeed = maxSpeed;
        }
        else if (this.driveState == 2) {
            desiredSpeed = -maxSpeed;
        }
        for (int i = 0; i < this.wheelBodies.length; ++i) {
            this.wheelBodies[i].setAngularVelocity(desiredSpeed);
        }
    }
    
    @Override
    public void keyPressed(final char key, final int argKeyCode) {
        switch (key) {
            case 'j': {
                this.driveState |= 0x1;
                break;
            }
            case 'k': {
                this.driveState |= 0x2;
                break;
            }
        }
        this.updateDriveState();
    }
    
    @Override
    public void keyReleased(final char key, final int argKeyCode) {
        switch (key) {
            case 'j': {
                this.driveState &= 0xFFFFFFFE;
                break;
            }
            case 'k': {
                this.driveState &= 0xFFFFFFFD;
                break;
            }
        }
        this.updateDriveState();
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {

    }
}
 */
