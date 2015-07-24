package com.example.ykai.flabbybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by ykai on 2015/7/23.
 */
public class Pipe {
    //上下管道间的距离
    private static final float RADIO_BETWEEN_UP_DOWN=1/5F;
    // 上管道的最大高度
    private static final float RADIO_MAX_HEIGHT=2/5F;
   // 上管道的最小高度
    private static final float RADIO_MIN_HEIGHT =1/5F;
    //管道横坐标
    private int x;
    //上管道的高度
    private int height;

    //上下管道间的距离
    private int margin;

    //top pipe pic
    private Bitmap mTop;
    // bottom pipe pic
    private Bitmap mBottom;

    private static Random random=new Random();
    public Pipe(Context context,int gameWidth,int gameHeight,Bitmap top,Bitmap bottom){
        margin=(int)(gameHeight*RADIO_BETWEEN_UP_DOWN);
        x=gameWidth;
        mTop=top;
        mBottom=bottom;
        randomHeight(gameHeight);
    }

    /**
     * 随机生成一个高度
     * @param gameHeight
     */
    private void randomHeight(int gameHeight) {
        height=random.nextInt((int)(gameHeight*(RADIO_MAX_HEIGHT-RADIO_MIN_HEIGHT)));
        height=(int)(height+gameHeight*RADIO_MIN_HEIGHT);
    }
    public void draw(Canvas mCanvas,RectF rect){
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);

        mCanvas.translate(x, -(rect.bottom - height));
        mCanvas.drawBitmap(mTop, null, rect, null);

        mCanvas.translate(0, (rect.bottom - height) + height + margin);
        mCanvas.drawBitmap(mBottom, null, rect, null);
        mCanvas.restore();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean touchBird(Bird mBird) {
        if(
                mBird.getX()+mBird.getmWidth()>x //there not operate the case that the bird pass the pipe
                &&
                ( mBird.getY()<height ||mBird.getY()+mBird.getmHeight()>height+margin)

                )
        {return true;}
        return false;
    }
}
