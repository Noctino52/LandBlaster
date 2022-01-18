package com.game.ivan.landblaster;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Sound;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The game objects and the viewport.
 *
 * Created by mfaella on 27/02/16.
 */
public class GameWorld {
    // Rendering
    final static int bufferWidth = 400, bufferHeight = 600;    // actual pixels
    Bitmap buffer;
    public Canvas canvas;
    private Paint particlePaint;

    // Simulation
    List<GameObject> objects;
    World world;
    final Box physicalSize, screenSize, currentView;
    private MyContactListener contactListener;
    private TouchConsumer touchConsumer;
    private TouchHandler touchHandler;

    // Particles
    ParticleSystem particleSystem;
    private static final int MAXPARTICLECOUNT = 1000;
    private static final float PARTICLE_RADIUS = 0.3f;

    // Parameters for world simulation
    private static final float TIME_STEP = 1 / 50f; // 50 fps
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;
    private static final int BONUS_SCORE=10;


    final Activity activity; // just for loading bitmaps in game objects

    //Parametri gioco
    public long lastRockSend =0;
    public long lastRockDestroyed =0;
    public int direction=-1;
    public int score=0;
    public int lifes=5;
    public long lastHit=0;
    public int maxHighScore=0;
    private int lastScoreBonus=0;

    // Arguments are in physical simulation units.
    public GameWorld(Box physicalSize, Box screenSize, Activity theActivity) {
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.activity = theActivity;
        this.buffer = Bitmap.createBitmap(bufferWidth, bufferHeight, Bitmap.Config.ARGB_8888);
        this.world = new World(0, 0);  // gravity vector

        this.currentView = physicalSize;
        // Start with half the world
        // new Box(physicalSize.xmin, physicalSize.ymin, physicalSize.xmax, physicalSize.ymin + physicalSize.height/2);

        // The particle system
        ParticleSystemDef psysdef = new ParticleSystemDef();
        this.particleSystem = world.createParticleSystem(psysdef);
        particleSystem.setRadius(PARTICLE_RADIUS);
        particleSystem.setMaxParticleCount(MAXPARTICLECOUNT);
        psysdef.delete();

        // stored to prevent GC
        contactListener = new MyContactListener();
        world.setContactListener(contactListener);

        touchConsumer = new TouchConsumer(this);

        this.objects = new ArrayList<>();
        this.canvas = new Canvas(buffer);

        createScore();
        lastHit= Calendar.getInstance().getTimeInMillis();
    }


    public synchronized GameObject addGameObject(GameObject obj)
    {
        objects.add(obj);
        return obj;
    }
    public synchronized  void deleteGameObject(GameObject obj){
        this.world.destroyBody(obj.body);
    }

    public synchronized void addParticleGroup(GameObject obj)
    {
        objects.add(obj);
    }

    // To distance sounds from each other
    private long timeOfLastSound = 0;

    public synchronized void update(float elapsedTime)
    {
        // advance the physics simulation
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);
        checkGameRules();
        destroyRock();

        // Handle collisions
        handleCollisions(contactListener.getCollisions());

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents())
            touchConsumer.consumeTouchEvent(event);
    }

    private void destroyRock() {
        Rock rock;
        GameObject obj;
        ListIterator<GameObject> iter = objects.listIterator();
        while(iter.hasNext()){
            obj=iter.next();
            if(obj.name.contains("rock")){
                rock= (Rock) obj;
                if(rock.isDestroyed) {
                    lastRockDestroyed=System.currentTimeMillis();
                    deleteGameObject(rock);
                    iter.remove();
                }
            }
        }
    }

    public synchronized void render()
    {
        // clear the screen (with black)
        canvas.drawARGB(255, 0, 0, 0);
        backgroundCreator();
        showScore();
        showRockLine();
        showLifes();
        for (GameObject obj: objects)
            obj.draw(buffer);
        // drawParticles();
        if(lifes<=0)showFinish();
    }

    private void handleCollisions(Collection<Collision> collisions) {
        for (Collision event: collisions) {
            Sound sound = CollisionSounds.getSound(event.a.getClass(), event.b.getClass());
            if (sound!=null) {
                long currentTime = System.nanoTime();
                if (currentTime - timeOfLastSound > 500_000_000) {
                    timeOfLastSound = currentTime;
                    sound.play(0.7f);
                }
            }
        }

    }

    // Conversions between screen coordinates and physical coordinates

    public float toMetersX(float x) { return currentView.xmin + x * (currentView.width/screenSize.width); }
    public float toMetersY(float y) { return currentView.ymin + y * (currentView.height/screenSize.height); }

    public float toPixelsX(float x) { return (x-currentView.xmin)/currentView.width*bufferWidth; }
    public float toPixelsY(float y) { return (y-currentView.ymin)/currentView.height*bufferHeight; }

    public float toPixelsXLength(float x)
    {
        return x/currentView.width*bufferWidth;
    }
    public float toPixelsYLength(float y)
    {
        return y/currentView.height*bufferHeight;
    }

    public synchronized void setGravity(float x, float y)
    {
        world.setGravity(x, y);
    }
    private void backgroundCreator() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        final Rect src = new Rect();
        final RectF dest = new RectF();
        Paint paint = new Paint();
        Bitmap bitmap = BitmapFactory.decodeResource(this.activity.getResources(), R.drawable.bg, o);
        src.set(0,0,850,1250);
        dest.bottom=src.centerY();
        dest.top=0;
        dest.right=src.centerX();
        dest.left=0;
        canvas.drawBitmap(bitmap,src, dest,paint);
    }
    private void checkGameRules() {
        //SM reload
        if(maxHighScore%BONUS_SCORE ==0 && maxHighScore !=0 && lastScoreBonus!= maxHighScore){
            lastScoreBonus=maxHighScore;
            for ( GameObject obj: objects) {
                if(obj.name.contains("button")){
                    Button button= (Button) obj;
                    button.isOn=true;
                }

            }
        }
        //...
    }
    private void createScore() {

        Timer timer = new Timer();
        int begin = 1000; //timer starts after 1 second.
        int timeinterval = 1000; //timer executes every 10 seconds.
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //This code is executed at every interval defined by timeinterval (eg 10 seconds)
                //And starts after x milliseconds defined by begin.
                if(lifes>0)score=score+1;
                if(maxHighScore<score)maxHighScore=score;
            }
        },begin, timeinterval);

    }
    private void showScore() {
        Paint paint=new Paint();
        String text = "Score: "+ score+"";
        Bitmap bitmap=textAsBitmap(text,24,0xFFFFFFFF);
        Bitmap newBitmap=RotateBitmap(bitmap,90);
        canvas.drawBitmap(newBitmap,350,490,paint);
    }

    private void showRockLine() {
        Paint paint=new Paint();
        paint.setARGB(155,255,0,0);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        canvas.drawRect(350,65,1000,540,paint);
    }
    private void showLifes() {
        Paint paint=new Paint();
        String text = "Lifes: "+ lifes+"";
        Bitmap bitmap=textAsBitmap(text,24,0xFFFFFFFF);
        Bitmap newBitmap=RotateBitmap(bitmap,90);
        canvas.drawBitmap(newBitmap,350,10,paint);
    }
    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void showFinish() {
        Paint paint=new Paint();
        String text = "Game Over " + "Score:"+score;
        Bitmap bitmap=textAsBitmap(text,30,0xFFFFFFFF);
        Bitmap newBitmap= GameWorld.RotateBitmap(bitmap,90);
        canvas.drawBitmap(newBitmap,210,170,paint);
    }
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    @Override
    public void finalize()
    {
        world.delete();
    }

    public void setTouchHandler(TouchHandler touchHandler) {
        this.touchHandler = touchHandler;
    }
}
