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
import com.google.fpl.liquidfun.Vec2;


public class Wheel extends GameObject{
    private static final float width = 1f, height = 1f, density = 0.5f;
    private static final float THICKNESS = 1;
    private final float screen_semi_width;
    private final float screen_semi_height;
    private Bitmap bitmap;
    private Paint paint = new Paint();
    private  int number=0;

    public Wheel(GameWorld gw,float x, float y,int id) {
        super(gw);
        number=id;
        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.dynamicBody);
        this.screen_semi_width = gw.toPixelsXLength(width);
        this.screen_semi_height = gw.toPixelsYLength(height);
        // a body
        bdef.setPosition(x,y);
        this.body = gw.world.createBody(bdef);
        this.name = "wheel"+number;
        body.setUserData(this);
        body.setLinearVelocity(new Vec2(0,0f));
        body.applyForceToCenter(new Vec2(300,100),false);

        CircleShape circle = new CircleShape();
        circle.setPosition(0,0);
        circle.setRadius(0.4f);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(circle);
        body.createFixture(fixturedef);

        // clean up native objects
        fixturedef.delete();
        bdef.delete();
        circle.delete();

        // Prevents scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.wheel, o);
        // Note: top <= bottom
        src.set(0,0,600, 600);
    }
    private final Rect src = new Rect();
    private final RectF dest = new RectF();

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        dest.left = x - screen_semi_width;
        dest.bottom = y + screen_semi_height;
        dest.right = x + screen_semi_width;
        dest.top = y - screen_semi_height;
        //Canvas canvas2 = new Canvas(buf);
        //if(number==1)paint.setARGB(255,23,123,12);
        //else paint.setARGB(255,122,12,235);
        //canvas2.drawCircle(x,y,10,paint);
    }
}
