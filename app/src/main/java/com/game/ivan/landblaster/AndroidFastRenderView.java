package com.game.ivan.landblaster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.fpl.liquidfun.Vec2;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
    private Bitmap framebuffer;
    private Thread renderThread = null;
    private SurfaceHolder holder;
    private GameWorld gameworld;
    private volatile boolean running = false;
    GameObject telaio;
    private static final float LINEAR_VELOCITY_BULLDOZER=3.5f;
    
    public AndroidFastRenderView(Context context, GameWorld gw) {
        super(context);
        this.gameworld = gw;
        this.framebuffer = gw.buffer;
        this.holder = getHolder();
    }

    /** Starts the game loop in a separate thread.
     */
    public void resume() {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    /** Stops the game loop and waits for it to finish
     */
    public void pause() {
        running = false;
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // just retry
            }
        }
    }

    public void run() {
        Rect dstRect = new Rect();
        long startTime = System.nanoTime(), fpsTime = startTime, frameCounter = 0;

        /*** The Game Main Loop ***/
        while (running) {
            if(!holder.getSurface().isValid()) {
                // too soon (busy waiting), this only happens on startup and resume
                continue;
            }

            long currentTime = System.nanoTime();
            // deltaTime is in seconds
            float deltaTime = (currentTime-startTime) / 1000000000f,
                  fpsDeltaTime = (currentTime-fpsTime) / 1000000000f;
            startTime = currentTime;
            System.currentTimeMillis();

            gameworld.update(deltaTime);
            gameworld.render();
            //Retrieve bulldozer
            for (GameObject obj:gameworld.objects) {
                if(obj.name.contains("wheel")) {
                    if(gameworld.direction==1 && gameworld.lastRockDestroyed+10000>=System.currentTimeMillis())obj.body.setLinearVelocity(new Vec2(0,LINEAR_VELOCITY_BULLDOZER));
                    else if(gameworld.direction==-1 && gameworld.lastRockDestroyed+10000>=System.currentTimeMillis()) obj.body.setLinearVelocity(new Vec2(0,-LINEAR_VELOCITY_BULLDOZER));
                    else if ( gameworld.direction==1 && gameworld.lastRockDestroyed+10000<System.currentTimeMillis()&& gameworld.lastRockDestroyed!=0)obj.body.setLinearVelocity(new Vec2(0,LINEAR_VELOCITY_BULLDOZER*2));
                    else if(gameworld.direction==-1 && gameworld.lastRockDestroyed+10000<System.currentTimeMillis() && gameworld.lastRockDestroyed!=0) obj.body.setLinearVelocity(new Vec2(0,-LINEAR_VELOCITY_BULLDOZER*2));
                    else if(gameworld.direction==-1) obj.body.setLinearVelocity(new Vec2(0,-LINEAR_VELOCITY_BULLDOZER));
                    else obj.body.setLinearVelocity(new Vec2(0,LINEAR_VELOCITY_BULLDOZER));
                }
            }

            // Draw framebuffer on screen
            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            // Scales to actual screen resolution
            canvas.drawBitmap(framebuffer, null, dstRect, null);
            holder.unlockCanvasAndPost(canvas);

            // Measure FPS
            frameCounter++;
            if (fpsDeltaTime > 1) { // Print every second
                Log.d("FastRenderView", "Current FPS = " + frameCounter);
                frameCounter = 0;
                fpsTime = currentTime;
            }
        }
    }
}