package com.game.ivan.landblaster;

import android.util.Log;

import com.badlogic.androidgames.framework.Input;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.MouseJoint;
import com.google.fpl.liquidfun.MouseJointDef;
import com.google.fpl.liquidfun.QueryCallback;

import java.util.concurrent.TimeUnit;

/**
 * Takes care of user interaction: pulls objects using a Mouse Joint.
 */
public class TouchConsumer {

    // keep track of what we are dragging
    private MouseJoint mouseJoint;
    private int activePointerID;
    private Fixture touchedFixture;

    private GameWorld gw;
    private QueryCallback touchQueryCallback = new TouchQueryCallback();
    private Button button;

    // physical units, semi-side of a square around the touch point
    private final static float POINTER_SIZE = 0.5f;

    private class TouchQueryCallback extends QueryCallback
    {
        public boolean reportFixture(Fixture fixture) {
            touchedFixture = fixture;
            return true;
        }
    }

    /**
        scale{X,Y} are the scale factors from pixels to physics simulation coordinates
    */
    public TouchConsumer(GameWorld gw) {
        this.gw = gw;
    }

    public void consumeTouchEvent(Input.TouchEvent event)
    {
        switch (event.type) {
            case Input.TouchEvent.TOUCH_DOWN:
                consumeTouchDown(event);
                break;
            case Input.TouchEvent.TOUCH_UP:
                consumeTouchUp(event);
                break;
            case Input.TouchEvent.TOUCH_DRAGGED:
                consumeTouchMove(event);
                break;
        }
    }

    private void consumeTouchDown(Input.TouchEvent event) {
        int pointerId = event.pointer;

        // if we are already dragging with another finger, discard this event
        if (mouseJoint != null) return;

        //Caduta massi
        float x = gw.toMetersX(event.x), y = gw.toMetersY(event.y);
        if(x>7.7 && (y<11.3 && y>-11.3)){
            long current=TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            if((current)-(gw.lastRockSend)>2) {
                gw.addGameObject(new Rock(gw, x, y));
                gw.lastRockSend = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            }
        }
        //Click button
        for (GameObject obj: gw.objects) {
            if(obj.name.contains("button")) button= (Button) obj;
        }
        if(x<button.body.getPositionX()+2 && x>=button.body.getPositionX()-2 &&
                y<button.body.getPositionY()+2 && y>=button.body.getPositionY()-2)pressButton(button);
        Log.d("MultiTouchHandler", "touch down at " + x + ", " + y+"In pixel: "+event.x+" , "+event.y);

    }

    private void pressButton(Button button) {
        if(button.isOn) {
            button.isOn = false;
            Rock roccia;
            //Per qualsiasi roccia
            for (GameObject obj : gw.objects) {
                if (obj.name.contains("rock")) {
                    roccia = (Rock) obj;
                    roccia.bonusRock();
                }
            }
        }
    }

    // If a DynamicBox is touched, it splits into two
    private void splitBox(GameObject touchedGO, Body touchedBody) {
        if (touchedGO instanceof DynamicBoxGO) {
            gw.world.destroyBody(touchedBody);
            gw.objects.remove(touchedGO);
            gw.addGameObject(new DynamicBoxGO(gw, touchedBody.getPositionX(), touchedBody.getPositionY()));
            gw.addGameObject(new DynamicBoxGO(gw, touchedBody.getPositionX(), touchedBody.getPositionY()));
        }
    }

    // Set up a mouse joint between the touched GameObject and the touch coordinates (x,y)
    private void setupMouseJoint(float x, float y, Body touchedBody) {
        MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.setBodyA(touchedBody); // irrelevant but necessary
        mouseJointDef.setBodyB(touchedBody);
        mouseJointDef.setMaxForce(500 * touchedBody.getMass());
        mouseJointDef.setTarget(x, y);
        mouseJoint = gw.world.createMouseJoint(mouseJointDef);
    }

    private void consumeTouchUp(Input.TouchEvent event) {
        if (mouseJoint != null && event.pointer == activePointerID) {
            Log.d("MultiTouchHandler", "Releasing joint");
            gw.world.destroyJoint(mouseJoint);
            mouseJoint = null;
            activePointerID = 0;
        }
    }

    private void consumeTouchMove(Input.TouchEvent event) {
        float x = gw.toMetersX(event.x), y = gw.toMetersY(event.y);
        if (mouseJoint!=null && event.pointer == activePointerID) {
            Log.d("MultiTouchHandler", "active pointer moved to " + x + ", " + y);
            mouseJoint.setTarget(x, y);
        }
    }
}
