package com.game.ivan.landblaster;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

public class Suspension extends GameObject{
    private static final float width = 0.35f, height = 0.1f;
    private final float screen_semi_width;
    private final float screen_semi_height;
    private Bitmap bitmap;
    private Paint paint = new Paint();
    private  int number=0;

    public Suspension(GameWorld gw,float x, float y,int id) {
        super(gw);
        number=id;
        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.dynamicBody);
        this.screen_semi_width = gw.toPixelsXLength(width);
        this.screen_semi_height = gw.toPixelsYLength(height);
        // a body
        bdef.setPosition(x,y);
        this.body = gw.world.createBody(bdef);
        this.name = "suspension"+number;
        body.setUserData(this);

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width,height);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(poly);
        body.createFixture(fixturedef);

        // clean up native objects
        fixturedef.delete();
        bdef.delete();
        poly.delete();
    }
    private final RectF dest = new RectF();
    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        dest.left = x - screen_semi_width;
        dest.bottom = y + screen_semi_height;
        dest.right = x + screen_semi_width;
        dest.top = y - screen_semi_height;
        Canvas canvas2 = new Canvas(buf);
        //if(number==1)paint.setARGB(255,255,255,255);
        //else paint.setARGB(255,0,0,0);
        //canvas2.drawRect(dest,paint);
    }

}
