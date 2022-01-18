package com.game.ivan.landblaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;

import java.util.Random;

public class Rock extends GameObject{

    private final float BONUS_ROCK=1.2f;
    private final float DENSITY_ROCK=5;
    private float screen_semi_width;
    private float screen_semi_height;
    private Bitmap bitmap;
    private Paint paint = new Paint();
    private  int counter=0;
    private  int number=0;
    private final Rect src = new Rect();
    private final RectF dest = new RectF();
    private float rand=0;
    private float width, height;
    public boolean isDestroyed=false;

    public Rock(GameWorld gw,float x, float y) {
        super(gw);
        counter=counter+1;
        number=counter;
        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.dynamicBody);
        Random rn= new Random();
        while(rand < 0.6F || rand > 0.99F) rand=rn.nextFloat();
        width=rand;
        height=rand;
        screen_semi_width = gw.toPixelsXLength(rand * width);
        screen_semi_height = gw.toPixelsYLength(rand*height);
        // a body
        bdef.setPosition(x,y);
        this.body = gw.world.createBody(bdef);
        this.name = "rock"+number;
        body.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setPosition(0,0);
        circle.setRadius(rand-0.2f);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(circle);
        fixturedef.setDensity(DENSITY_ROCK);
        body.createFixture(fixturedef);


        // clean up native objects
        fixturedef.delete();
        bdef.delete();
        circle.delete();

        // Prevents scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.rock, o);
        // Note: top <= bottom
        src.set(20, 20,100, 10);
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        dest.left = x - screen_semi_width;
        dest.bottom = y + screen_semi_height;
        dest.right = x + screen_semi_width;
        dest.top = y - screen_semi_height;
        Canvas canvas2 = new Canvas(buf);
        canvas2.drawBitmap(bitmap,null,dest,null);
    }

    public void bonusRock(){
        this.rand*=BONUS_ROCK;

        body.setUserData(this);

        CircleShape circle = new CircleShape();
        circle.setPosition(0,0);
        circle.setRadius(rand-0.2f);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setDensity(DENSITY_ROCK*(DENSITY_ROCK/2));
        fixturedef.setShape(circle);
        body.createFixture(fixturedef);

        fixturedef.delete();
        circle.delete();
        width=rand;
        height=rand;
        screen_semi_width = gw.toPixelsXLength(rand);
        screen_semi_height = gw.toPixelsYLength(rand);
    }

}
