package com.game.ivan.landblaster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

public class Ramp extends GameObject{
    private static final float width = 3.5f, height = 5f, density = 0.5f;
    private static final float THICKNESS = 1;
    private final float screen_semi_width;
    private final float screen_semi_height;
    private Bitmap bitmap;
    private Paint paint = new Paint();
    private  int number=0;
    private final PointF pointCordinatesA =new PointF(-8.5f,-5), pointCordinatesC =new PointF(-8.5f,0), pointCordinatesB =new PointF(-5f,0);


    public Ramp(GameWorld gw, float x, float y,int id) {
        super(gw);
        number=id;
        BodyDef bdef = new BodyDef();
        bdef.setPosition(x,y);
        bdef.setType(BodyType.staticBody);
        this.screen_semi_width = gw.toPixelsXLength(width);
        this.screen_semi_height = gw.toPixelsYLength(height);
        // a body

        this.body = gw.world.createBody(bdef);
        this.name = "ramp"+number;
        body.setUserData(this);

        PolygonShape triangle = new PolygonShape();
        if(number==1)triangle.setAsTriangle(pointCordinatesA.x, pointCordinatesA.y, pointCordinatesC.x, pointCordinatesC.y, pointCordinatesB.x, pointCordinatesB.y);
        else triangle.setAsTriangle(pointCordinatesA.x,-pointCordinatesA.y, pointCordinatesC.x, pointCordinatesC.y, pointCordinatesB.x, pointCordinatesB.y);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(triangle);
        body.createFixture(fixturedef);


        // clean up native objects
        fixturedef.delete();
        bdef.delete();
        triangle.delete();
        // Note: top <= bottom
        src.set(0,0,300, 300);
    }
    private final Rect src = new Rect();
    private final RectF dest = new RectF();


    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        dest.left = 30;
        dest.bottom = y+screen_semi_height;
        dest.right = x-screen_semi_height;
        dest.top = y - screen_semi_height;
        //System.out.println("semi-width:"+screen_semi_width +"X: "+x);
        //System.out.println("semi-height"+screen_semi_height+"Y: "+y);
        //System.out.println("dest:"+dest);
        Canvas canvas = new Canvas(buffer);
        BitmapFactory.Options o = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.ramp, o);
        canvas.drawBitmap(bitmap,null,dest,paint);
    }

    public void drawTriangle(Point p1, Point p2, Point p3, int clr, Canvas c)
    {
        Paint paint = new Paint();
        Bitmap newBitmap;
        BitmapFactory.Options o = new BitmapFactory.Options();
        newBitmap = BitmapFactory.decodeResource(gw.activity.getResources(), R.drawable.ramp, o);
        Bitmap mutableBitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas= new Canvas(mutableBitmap);
        paint.setStrokeWidth(20);
        paint.setARGB(255,255,0,0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        if(number==1)path.moveTo(p1.x,p1.y);
        else path.moveTo(p1.x,p1.y*2);
        path.lineTo(p2.x,p2.y);
        path.lineTo(p3.x,p3.y);
        path.close();

        canvas.drawPath(path,paint);
        canvas.clipPath(path);
        canvas.setBitmap(mutableBitmap);
        c.drawBitmap(mutableBitmap,src,dest,paint);
    }

}
