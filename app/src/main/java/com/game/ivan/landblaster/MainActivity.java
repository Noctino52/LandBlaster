package com.game.ivan.landblaster;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.MultiTouchHandler;

import java.nio.ByteOrder;

public class MainActivity extends Activity {

    private MyThread t; // just for fun, unrelated to the rest
    private AndroidFastRenderView renderView;
    private Audio audio;
    private Music backgroundMusic;
    private MultiTouchHandler touch;

    // boundaries of the physical simulation
    private static final float XMIN = -10, XMAX = 10, YMIN = -15, YMAX = 15;

    // the tag used for logging
    public static String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        TAG = getString(R.string.app_name);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Sound
        audio = new AndroidAudio(this);
        CollisionSounds.init(audio);
        backgroundMusic = audio.newMusic("soundtrack.mp3");
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Game world
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box physicalSize = new Box(XMIN, YMIN, XMAX, YMAX),
            screenSize   = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);
        GameWorld gw = new GameWorld(physicalSize, screenSize, this);
        gw.setGravity(-10f,0);

        //Inserimento oggetti
        gw.addGameObject(new Terrain(gw,-9,0));
        gw.addGameObject(new Ramp(gw,0,0,1));
        gw.addGameObject(new Ramp(gw,0,0,2));
        gw.addGameObject(new Dirt(gw,-9,14,1));
        gw.addGameObject(new Dirt(gw,-9,-14,2));
        gw.addGameObject(new Button(gw,6.5f,-13.5f));
        GameObject ruotaSx=gw.addGameObject(new Wheel(gw,-5,1f,1));
        GameObject ruotaDx=gw.addGameObject(new Wheel(gw,-5,-1f,2));
        GameObject telaio=gw.addGameObject(new Telaio(gw,-4.5f,-1f));
        GameObject suspensionSx=gw.addGameObject(new Suspension(gw,-4.6f,1f,1));
        GameObject suspensionDx=gw.addGameObject(new Suspension(gw,-4.6f,-1,2));
        gw.addGameObject(new EnclosureGO(gw, XMIN-1, XMAX+1, YMIN-1, YMAX+1));

        //Inserimento Joint
        new MyRevoluteJoint(gw, suspensionSx.body,ruotaSx.body,1);
        new MyRevoluteJoint(gw, suspensionDx.body, ruotaDx.body,2);
        new MyPrismaticJoint(gw,suspensionSx.body,telaio.body,1);
        new MyPrismaticJoint(gw,suspensionDx.body,telaio.body,2);
        new MyDistanceJoint(gw,suspensionSx.body,telaio.body,1);
        new MyDistanceJoint(gw,suspensionDx.body,telaio.body,2);

        // Just for info
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRate = display.getRefreshRate();
        Log.i(getString(R.string.app_name), "Refresh rate =" + refreshRate);

        // View
        renderView = new AndroidFastRenderView(this, gw);
        setContentView(renderView);

        // Touch
        touch = new MultiTouchHandler(renderView, 1, 1);
        // Setter needed due to cyclic dependency
        gw.setTouchHandler(touch);

        // Unrelated to the rest, just to show interaction with another thread
        t = new MyThread(gw);
        t.start();

        Log.i(getString(R.string.app_name), "onCreate complete, Endianness = " +
                (ByteOrder.nativeOrder()==ByteOrder.BIG_ENDIAN? "Big Endian" : "Little Endian"));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Main thread", "pause");
        renderView.pause(); // stops the main loop
        backgroundMusic.pause();

        // persistence example
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(getString(R.string.important_info), t.counter);
        editor.commit();
        Log.i("Main thread", "saved counter " + t.counter);
    }

    @Override
    public void onStop() {
        super.onStop();
        finish();
        System.exit(0);
        Log.i("Main thread", "stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Main thread", "resume");

        renderView.resume(); // starts game loop in a separate thread
        backgroundMusic.play();

        // persistence example
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        int counter = pref.getInt(getString(R.string.important_info), -1); // default value
        Log.i("Main thread", "read counter " + counter);
        t.counter = counter;
    }
}
