package com.example.ykai.flabbybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by ykai on 2015/7/23.
 */
public class Bird {
    //the 2/3 of screen
    private static final float PADIO_POS_HEIGHT=2/3F;
    private static final int BIRD_SIZE=30;
    // position of bird
    private int x;
    private int y;
    //size of bird
    private int mWidth;
    private int mHeight;
    // bitmap of bird
    private Bitmap bitmap;
    private RectF rect=new RectF();
    public Bird(Context context,int gameWidth,int gameHeight,Bitmap bitmap){
        this.bitmap=bitmap;
        x=gameWidth/2-bitmap.getWidth()/2;
        y=(int)(gameHeight*PADIO_POS_HEIGHT);
        mWidth=Util.dp2px(context,BIRD_SIZE);
        mHeight=(int)(mWidth*1.0f/bitmap.getWidth()*bitmap.getHeight());
    }

    public void draw(Canvas canvas){
        rect.set(x,y,x+mWidth,y+mHeight);
        canvas.drawBitmap(bitmap,null,rect,null);
    }
    public int getY()
    {
        return y;
    }
    public void setY(int y){
        this.y=y;
    }

    public int getmHeight() {
        return mHeight;
    }

    public int getmWidth() {
        return mWidth;
    }
}
