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
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

public class Dirt extends GameObject{
    private static final float width = 0.4f, height = 2f, density = 0.5f;
    private static final float THICKNESS = 1;
    private final float screen_semi_width;
    private final float screen_semi_height;
    private Bitmap bitmap;
    private Paint paint = new Paint();
    private  int number=0;


    public Dirt(GameWorld gw, float x, float y,int id) {
        super(gw);
        number=id;
        BodyDef bdef = new BodyDef();
        bdef.setPosition(x,y);
        bdef.setType(BodyType.staticBody);
        this.screen_semi_width = gw.toPixelsXLength(width);
        this.screen_semi_height = gw.toPixelsYLength(height);
        // a body
        this.body = gw.world.createBody(bdef);
        this.name = "dirt"+number;
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(width, height);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(box);
        body.createFixture(fixturedef);

        // clean up native objects
        fixturedef.delete();
        bdef.delete();
        box.delete();

        Fixture f = body.getFixtureList();

        // Prevents scaling
        BitmapFactory.Options o = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.dirt, o);
        // Note: top <= bottom
        src.set(0,0,600, 600);
    }
    private final Rect src = new Rect();
    private final RectF dest = new RectF();


    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        dest.left = x - screen_semi_width;
        dest.bottom = y + screen_semi_height;
        dest.right = x + screen_semi_width;
        dest.top = y - screen_semi_height;
        Canvas canvas = new Canvas(buffer);
        canvas.drawBitmap(bitmap,src,dest,paint);
    }
}
