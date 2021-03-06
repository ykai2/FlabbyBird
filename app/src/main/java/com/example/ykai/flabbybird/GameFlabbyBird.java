package com.example.ykai.flabbybird;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ykai on 2015/7/23.
 */
public class GameFlabbyBird extends SurfaceView implements Callback,Runnable {
    private SurfaceHolder mHolder;
    //与surfaceview绑定的canvas
    private Canvas mCanvas;
    //用于绘制的线程
    private Thread thread;
    private boolean isRunning;
    //the size of this view
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect=new RectF();
    //the background
    private Bitmap mBg;

    //bird
    private Bird mBird;
    private Bitmap mBirdBitmap;

    //floor
    private Paint mPaint;
    private Floor mFloor;
    private Bitmap mFloorBg;
    private int mSpeed;

    //pipe
    private Bitmap mPipeTop;
    private Bitmap mPipeBottom;
    private RectF mPipeRect;
    private int mPipeWidth;
    private static final int PIPE_WIDTH=60;
    private List<Pipe> mPipes=new ArrayList<Pipe>();
    private final int PIPE_DIS_BETWEEN_TWO=Util.dp2px(getContext(),300);
    private int mTmpMoveDistance;
    private List<Pipe> mNeedRemovePipe=new ArrayList<Pipe>();// the pepes need to be remove
    private int mRemovePipe=0;


    //grade
    private final int []mNums=new int[]{R.drawable.n0,R.drawable.n1,R.drawable.n2,
            R.drawable.n3,R.drawable.n4,R.drawable.n5,
            R.drawable.n6,R.drawable.n7,R.drawable.n8,R.drawable.n9};
    private Bitmap[]mNumBitmap;
    private int mGrade=0;
    private static final float RADIO_SINGLE_NUM_HEIGHT=1/15F;  //1/15 of the height of a number
    private int mSingleGradeWidth; //width of a number
    private int mSingleGradeHeight; //height of a number
    private RectF mSingleNumRectF;

    //status of game
    private enum GameStatus{
        WAITTING,RUNNING,STOP;
    }
    private GameStatus mStatus=GameStatus.WAITTING;
    private static final int TOUCH_UP_SIZE=-16;
    private final int mBirdUpDis=Util.dp2px(getContext(),TOUCH_UP_SIZE);
    private int mTmpBirdDis;
    private final int mAutoDownSpeed=Util.dp2px(getContext(),2);

    private void logic(){
        switch (mStatus){
            case RUNNING:
                mGrade=0;
                mFloor.setX(mFloor.getX()-mSpeed);
/**
 * 增加了一个变量mNeedRemovePipe，在遍历Pipes的时候，如果x左边已经小于 -mPipeWidth时候，说明看不到了，那么就防到mNeedRemovePipe中；
 最后统一移除mNeedRemovePipe。
 */
                mRemovePipe=0;
                for(Pipe pipe:mPipes){
                    if(pipe.getX()<-mPipeWidth)
                    {
                        mNeedRemovePipe.add(pipe);
                        mRemovePipe++;
                        continue;
                    }
                    pipe.setX(pipe.getX()-mSpeed);
                }
                mPipes.remove(mNeedRemovePipe);
                mTmpMoveDistance+=mSpeed;
                if(mTmpMoveDistance>=PIPE_DIS_BETWEEN_TWO){
                    Pipe pipe=new Pipe(getContext(),getWidth(),getHeight(),mPipeTop,mPipeBottom);
                    mPipes.add(pipe);
                    mTmpMoveDistance=0;
                }


                mTmpBirdDis+=mAutoDownSpeed;
                mBird.setY(mBird.getY()+mTmpBirdDis);

                /**
                 * the method of calculate grade
                 * int the current Activity:  grade = the number of remove pipe + the number of pipe that in left of bird
                 */
               // mGrade+=mRemovePipe;
                for(Pipe pipe:mPipes){
                    if(pipe.getX()+mPipeWidth<mBird.getX())
                    {
                        mGrade++;
                    }
                }
                checkGameOver();
                break;
            case STOP: // bird drop down
                // if the bird in the air ,let it drop down first
                if(mBird.getY()+mBird.getmHeight()<mFloor.getY())
                {
                    mTmpBirdDis+=mAutoDownSpeed;
                    mBird.setY(mBird.getY()+mTmpBirdDis);
                }else{
                    mStatus=GameStatus.WAITTING;

                    
                }
                break;
            case WAITTING:
                initPOS();
                break;
            default:
                break;
        }
    }

    private void initPOS() {
        mPipes.clear();
        mNeedRemovePipe.clear();
        mBird.setY(mHeight * 3/7);
        mTmpBirdDis=0;
     //   mGrade=0;
    }

    private void checkGameOver() {
        // drop floor
        if(mBird.getY()+mBird.getmHeight()>mFloor.getY()){
            mStatus=GameStatus.STOP;
        }
        //strike pipe
        for(Pipe wall:mPipes){
            //bird have pass the pipe
            if(wall.getX()+mPipeWidth<mBird.getX()){
                continue;
            }
            if(wall.touchBird(mBird))
            {
                mStatus=GameStatus.STOP;
                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action=event.getAction();
        if(action==MotionEvent.ACTION_DOWN){
            switch (mStatus){
                case WAITTING:
                    mStatus=GameStatus.RUNNING;
                    break;
                case RUNNING:
                    mTmpBirdDis=mBirdUpDis;
                    break;
            }
        }
        return true;
    }










    public GameFlabbyBird(Context context){
        this(context, null);
    }
    public GameFlabbyBird(Context context,AttributeSet attributeSet){
        super(context,attributeSet);

        mHolder=getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);//设置画布，背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);


        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);

        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        initBitmaps();
        mSpeed=Util.dp2px(getContext(), 2);
        mPipeWidth=Util.dp2px(getContext(), PIPE_WIDTH);

    }

    private void initBitmaps() {
        mBg=loadImageByResId(R.drawable.bg1);
        mBirdBitmap=loadImageByResId(R.drawable.b1);
        mFloorBg=loadImageByResId(R.drawable.floor_bg2);
        mPipeTop=loadImageByResId(R.drawable.g2);
        mPipeBottom=loadImageByResId(R.drawable.g1);
        mNumBitmap=new Bitmap[mNums.length];
        for(int i=0;i<mNumBitmap.length;i++){
            mNumBitmap[i]=loadImageByResId(mNums[i]);
        }
    }

    private Bitmap loadImageByResId(int bg1) {
        return BitmapFactory.decodeResource(getResources(),bg1);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //  打开线程
        isRunning=true;
        thread=new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // close thread
        isRunning=false;

    }

    @Override
    public void run()
    {
        while (isRunning){
            long start=System.currentTimeMillis();
            logic();
            draw();
            long end=System.currentTimeMillis();

            try {
                if(end-start<50){
                    Thread.sleep(50-(end-start));
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private void draw() {
        try {
            mCanvas=mHolder.lockCanvas();
            if(mCanvas!=null){
                drawBg();
                drawBird();
                drawPipes();
                drawFloor();
                drawGrades();
//                mFloor.setX(mFloor.getX()-mSpeed);

            }
        }
        catch (Exception e){

        }finally {
            if(mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }

        }

    }

    /**
     * 定义了单个数字的范围，然后假设现在为100分，注意在绘制的时候，直接提取数字，把数字作为下标，找到对于的图片进行绘制；
     绘制前，根据数字的位数，对画布进行偏移到中心位置，然后绘制；绘制过程中，每绘制完成一个数字则偏移一个数字的宽度；
     */
    private void drawGrades() {
        String grade=mGrade+"";
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mWidth/2-grade.length()*mSingleGradeWidth/2,1f/8*mHeight);
        for(int i=0;i<grade.length();i++)
        {
            String numStr=grade.substring(i,i+1);
            int num=Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num],null,mSingleNumRectF,null);
            mCanvas.translate(mSingleGradeWidth,0);

        }
        mCanvas.restore();
    }

    private void drawPipes() {
        for(Pipe pipe:mPipes){
            pipe.setX(pipe.getX()-mSpeed);
            pipe.draw(mCanvas,mPipeRect);
        }
    }

    private void drawFloor() {
        mFloor.draw(mCanvas,mPaint);

    }

    private void drawBird() {
        mBird.draw(mCanvas);
    }

    //draw background
    private void drawBg() {
        mCanvas.drawBitmap(mBg,null,mGamePanelRect,null);
    }

    /**
     * 我们在onSizeChanged中初始化了一个Pipe，添加到了mPipes中，然后在draw里面，动态改变Pipe的x为pipe.setX(pipe.getX() - mSpeed);
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth=w;
        mHeight=h;
        mGamePanelRect.set(0,0,w,h);
        mBird=new Bird(getContext(),mWidth,mHeight,mBirdBitmap);
        mFloor=new Floor(mWidth,mHeight,mFloorBg);
        mPipeRect =new RectF(0,0,mPipeWidth,mHeight);
        Pipe pipe=new Pipe(getContext(),w,h,mPipeTop,mPipeBottom);
        mPipes.add(pipe);

        //init grade
        mSingleGradeHeight=(int)(h*RADIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth=(int)(mSingleGradeHeight*1.0f/mNumBitmap[0].getHeight()*mNumBitmap[0].getWidth());
        mSingleNumRectF=new RectF(0,0,mSingleGradeWidth,mSingleGradeHeight);


    }
}
