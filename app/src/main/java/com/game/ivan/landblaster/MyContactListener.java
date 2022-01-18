package com.game.ivan.landblaster;

import android.content.Intent;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Contact;
import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.Fixture;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mfaella on 01/03/16.
 */
public class MyContactListener extends ContactListener {

    public static final int RIGHT=1;
    public static final int LEFT=-1;
    public static final int DIFFERENCE_MIN_ROCK=1;
    public static final int DIFFERENCE_MAX_ROCK=3;
    private Collection<Collision> cache = new HashSet<>();
    private final int rand= new Random().nextInt(DIFFERENCE_MAX_ROCK -DIFFERENCE_MIN_ROCK) + DIFFERENCE_MIN_ROCK;
    private boolean flag=true;

    public Collection<Collision> getCollisions() {
        Collection<Collision> result = new HashSet<>(cache);
        cache.clear();
        return result;
    }

    /** Warning: this method runs inside world.step
     *  Hence, it cannot change the physical world.
     */
    @Override
    public void beginContact(Contact contact) {
        //Log.d("MyContactListener", "Begin contact");
        Fixture fa = contact.getFixtureA(),
                fb = contact.getFixtureB();
        Body ba = fa.getBody(), bb = fb.getBody();
        Object userdataA = ba.getUserData(), userdataB = bb.getUserData();
        GameObject a = (GameObject)userdataA,
                   b = (GameObject)userdataB;
        //Border change
        if((a.name.equals("wheel1") && b.name.equals("dirt1")) || (b.name.equals("wheel1") && a.name.equals("dirt1"))
                && a.gw.lastHit<Calendar.getInstance().getTimeInMillis()-4000) {
            a.gw.direction = LEFT;
            a.gw.lastHit=Calendar.getInstance().getTimeInMillis();
            a.gw.lifes=a.gw.lifes-1;
        }
        if((a.name.equals("wheel2") && b.name.equals("dirt2")) || (b.name.equals("wheel2") && a.name.equals("dirt2"))
                && a.gw.lastHit<Calendar.getInstance().getTimeInMillis()-4000) {
            a.gw.direction = RIGHT;
            a.gw.lastHit=Calendar.getInstance().getTimeInMillis();
            a.gw.lifes=a.gw.lifes-1;
        }
        //Finish game
        if(a.gw.lifes==0){
            finishGame(a.gw);
        }
        //Rock destruction
        if((a.name.equals("telaio") && b.name.contains("rock") )||
                a.name.equals("wheel1") && b.name.contains("rock") ||
                a.name.equals("wheel2") && b.name.contains("rock")) {
            Rock rock= (Rock) b;

            Timer timer= new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    rock.isDestroyed=true;
                    if(a.gw.lifes>0)a.gw.score=a.gw.score-1;
                }
            },1300);
        }
        if(b.name.equals("telaio") && a.name.contains("rock") ||
                b.name.equals("wheel1") && a.name.contains("rock") ||
                b.name.equals("wheel2") && a.name.contains("rock")){
            Rock rock= (Rock) a;
            Timer timer= new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    rock.isDestroyed=true;
                    if(a.gw.lifes>0)a.gw.score=a.gw.score-1;
                    int rockLeft = countLeftRock(a.gw);
                    int rockRight = countRightRock(a.gw);
                    if(rockLeft==rockRight+rand){
                        if(new Random().nextFloat()<0.5f)a.gw.direction=LEFT;
                        else a.gw.direction=RIGHT;
                    }
                }
            },1300);
        }

        //Rock counter
        if((a.name.contains("wheel") && b.name.contains("ramp")) ||(b.name.contains("wheel") && a.name.contains("ramp")) ) {
            int rockLeft = countLeftRock(a.gw);
            int rockRight = countRightRock(a.gw);
            if (rockLeft + rand >= rockRight){
                Timer timer= new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        a.gw.direction=LEFT;
                    }
                },1000);
            }
            else if (rockLeft < rockRight + rand){
                Timer timer= new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        a.gw.direction=RIGHT;
                    }
                },1000);
            }
        }
        // TO DO: use an object pool instead
        cache.add(new Collision(a, b));

        // Sound sound = CollisionSounds.getSound(a.getClass(), b.getClass());
        //if (sound!=null)
        //    sound.play(0.7f);
        // Log.d("MyContactListener", "contact bwt " + a.name + " and " + b.name);
    }

    private int countRightRock(GameWorld gw) {
        float x;
        float y;
        int counter=0;
        for (GameObject obj:gw.objects) {
            if(obj.name.contains("rock")){
              x=obj.body.getPositionX();
              y=obj.body.getPositionY();
              if(x<6 && y>0)counter=counter+1;
            }
        }
        return counter;
    }

    private int countLeftRock(GameWorld gw) {
        float x;
        float y;
        int counter=0;
        for (GameObject obj:gw.objects) {
            if(obj.name.contains("rock")){
                x=obj.body.getPositionX();
                y=obj.body.getPositionY();
                if(x<6 && y<0)counter=counter+1;
            }
        }
        return counter;
    }

    private void finishGame(GameWorld gw) {
        if (flag) {
            flag = false;
            Timer timer = new Timer();
            for (int i = 1; i < gw.objects.size(); i++) gw.deleteGameObject(gw.objects.get(i));
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Intent i = new Intent(gw.activity, StartActivity.class);
                    gw.activity.startActivity(i);
                }
            }, 5000);
        }
    }
}
