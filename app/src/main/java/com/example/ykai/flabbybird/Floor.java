package com.example.ykai.flabbybird;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;

/**
 * Created by ykai on 2015/7/23.
 */
public class Floor {
    private static final float FLOOR_Y_POS_PADIO=4/5F;

    private int x;
    private int y;
    //填充物
    private BitmapShader mFloorShader;
    private int mGameWidth;
    private int mGameHeight;

    public Floor(int mGameWidth,int mGameHeight,Bitmap floorBg){
        this.mGameWidth=mGameWidth;
        this.mGameHeight=mGameHeight;
        y=(int)(mGameHeight*FLOOR_Y_POS_PADIO);
        mFloorShader=new BitmapShader(floorBg, TileMode.REPEAT, TileMode.CLAMP);

    }
    // draw floor
    public void draw(Canvas canvas,Paint paint){
        /**
         * 如果x的正值大于宽度了，我们取余一下
         最终我们的绘制范围是：
         mCanvas.drawRect(x, 0, -x + mGameWidth, mGameHeight - y, mPaint);
         */
        if(-x>mGameWidth){
            x=x%mGameWidth;
        }
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.translate(x, y);
        paint.setShader(mFloorShader);
        canvas.drawRect(x, 0, -x + mGameWidth, mGameHeight - y, paint);
        canvas.restore();
        paint.setShader(null);

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
