package com.game.ivan.landblaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

public class Button extends GameObject{

    private static final float width = 0.9f, height = 0.9f, density = 0.5f;
    private static final float THICKNESS = 1;
    private final float screen_semi_width;
    private final float screen_semi_height;
    private Bitmap bitmap;
    private Paint paint = new Paint();
    boolean isOn=false;



    public Button(GameWorld gw, float x, float y) {
        super(gw);
        BodyDef bdef = new BodyDef();
        bdef.setPosition(x,y);
        bdef.setType(BodyType.staticBody);
        this.screen_semi_width = gw.toPixelsXLength(width);
        this.screen_semi_height = gw.toPixelsYLength(height);
        // a body
        this.body = gw.world.createBody(bdef);
        this.name = "button";
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
    }

    private final Rect src = new Rect();
    private final RectF dest = new RectF();

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        dest.left = x - screen_semi_width;
        dest.bottom = y + screen_semi_height;
        dest.right = x + screen_semi_width;
        dest.top = y - screen_semi_height;
        Canvas canvas = new Canvas(buf);
        BitmapFactory.Options o = new BitmapFactory.Options();
        if(isOn)bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.button_on, o);
        else bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.button_off, o);
        canvas.drawBitmap(bitmap,null,dest,paint);
    }
}
