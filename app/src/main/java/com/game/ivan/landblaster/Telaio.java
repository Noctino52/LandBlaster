package com.game.ivan.landblaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;

public class Telaio extends GameObject {
    private static final float width = 3f, height = 4f, density = 0.5f;
    private static final float THICKNESS = 1;
    private final float screen_semi_width;
    private final float screen_semi_height;

    public Telaio(GameWorld gw, float x, float y) {
        super(gw);
        this.screen_semi_width = gw.toPixelsXLength(width)/2;
        this.screen_semi_height = gw.toPixelsYLength(height)/2;
        this.name="telaio";

        BodyDef telaiodef= new BodyDef();
        telaiodef.setPosition(x,y);

        telaiodef.setType(BodyType.dynamicBody);
        Body telaio= gw.world.createBody(telaiodef);
        telaio.setLinearVelocity(new Vec2(0,0.4f));

        this.body=telaio;
        body.setUserData(this);

        PolygonShape box = new PolygonShape();
        box.setAsBox(1.5f, 1.5f);
        FixtureDef fixturedefTel = new FixtureDef();
        fixturedefTel.setShape(box);
        telaio.createFixture(fixturedefTel);


        fixturedefTel.delete();

        box.delete();

        Fixture f = body.getFixtureList();
        //Log.i("Dragme", "size: " + bitmap.getWidth() + ", " + bitmap.getHeight());
        // Note: top <= bottom
        src.set(1000,0,10, 0);
    }
    private  Bitmap bitmap;
    private final Rect src = new Rect();
    private final RectF dest = new RectF();


    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        Paint paint= new Paint();
        if(gw.direction==1)bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.bulldozer1, o);
        else bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.bulldozer2, o);
        paint.setARGB(255,0,0,255);
        //dest.left = x - screen_semi_width;
        dest.left = x - screen_semi_width-30;
        dest.bottom = y + screen_semi_height;
        dest.right = x + screen_semi_width;
        dest.top = y - screen_semi_height;
        Canvas canvas2 = new Canvas(buffer);
        canvas2.drawBitmap(bitmap,null,dest,null);
        //canvas2.drawRect(dest,paint);
    }

}
