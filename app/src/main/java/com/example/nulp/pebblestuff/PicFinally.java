package com.example.nulp.pebblestuff;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import com.example.nulp.pebblestuff.R;

/**
 * Created by gaurav on 4/11/15.
 */

public class PicFinally extends View {
    private Bitmap pic;
    //private int x;
    //private int y;
    //private Paint block;
    //private Rect r;
    private Canvas c;

    public PicFinally(Context context) {
        super(context);
        pic = BitmapFactory.decodeResource(getResources(),R.drawable.tiger_icon);
        c = new Canvas();
        onDraw(c);
        //block = new Paint(Color.CYAN);
        //r = new Rect();

        //x = 400;
        //y = 400;
        //int width = 50;
        //int height = 50;
        //r.set(x,y,x + width, y + height);
        //c.drawColor(Color.WHITE);

    }

    protected void onDraw (Canvas canvas){
        canvas.drawColor(Color.WHITE);
        Paint p = new Paint(Color.DKGRAY);
        canvas.drawBitmap(pic,100,100,p);
    }

    /*
    public void updatePos(int x, int z){
        this.x = this.x + x;
        this.y = this.y + z;
        Log.d("THIS.X:",Integer.toString(this.x));
        Log.d("THIS.Y:", Integer.toString(this.y));

        //this.draw(this.c, block);
    }
*/

}
