package com.example.lijinming.hdtest.WaveShow.DataPlayBack;

/**
 * Created by lijinming on 2016/5/20.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.example.lijinming.hdtest.DataManage.MyInternalStorage;

public class WaveViewPulse extends SurfaceView implements Callback,Runnable{
    private Context       mContext;
    private SurfaceHolder surfaceHolder;
    MyInternalStorage mMyInternalStorage;
    private int mSurfaceWidth,mSurfaceHeight;//屏幕宽高
    private Thread thread;
    public static Handler chartHandler ;
    Paint[] paints = new Paint[3];
    public WaveViewPulse(Context context, AttributeSet attrs) {

        super(context, attrs);
        //setBackgroundColor(Color.GREEN);
        paints[0] = new Paint();
        paints[1] = new Paint();
        paints[2] = new Paint();
        paints[0].setColor(Color.RED);
        paints[1].setColor(Color.BLACK);
        paints[2].setColor(Color.BLUE);
        paints[0].setStrokeWidth(40);
        paints[1].setStrokeWidth(2);
        paints[2].setStrokeWidth(2);
        mMyInternalStorage = new  MyInternalStorage(getContext());

        this.mContext = context;
        /*setFocusable(true);
        setFocusableInTouchMode(true);*/
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

    }
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        mSurfaceWidth = getWidth();//得到画布的宽度
        mSurfaceHeight = getHeight();//得到画布的高度
        Log.e("Width", String.valueOf(mSurfaceWidth));
        Log.e("Height", String.valueOf(mSurfaceHeight));
        thread = new Thread(this);
        thread.start();//开启绘制线程
    }
    int xpos = 1;
    int oldX = 0,x,y;
    int oldY1,y1,y2;
    /**
     * 将从SDCard读取的数据绘制成波形
     * */
    @Override
    public void run() {
        Looper.prepare();
        chartHandler = new Handler() {
            public void handleMessage(Message msg) {
                    synchronized (surfaceHolder) {
                        y = msg.what;
                        Log.e("MSG", String.valueOf(y));
                        //如果x坐标超出屏幕宽度则让x坐标置0
                        if (xpos > mSurfaceWidth) xpos = 1;
//                        y1 =(float)((y+2300000)/10-5700);
//                        y1 =(float)((y+100000)/50);
                        y2 = y*-5;
                        y1 = mSurfaceHeight +y2;

                        //y坐标的转换。把Y坐标通过下面的转换显示
                        /*float average;
                        average = (mSurfaceHeight/9);
                        y2 = (float)(y)/10000+9;
                        y1 = y2*average;*/
                        Canvas canvas = surfaceHolder.lockCanvas(new Rect(xpos, 0,
                               xpos + 10/*(int)(mSurfaceWidth)*/, (int) (mSurfaceHeight)));//锁定画布的一小块
                        canvas.drawColor(Color.BLACK);x = xpos;
                        canvas.drawLine(oldX, oldY1, x, y1, paints[0]);//绘图
                        oldX = x;oldY1 = y1;
                        surfaceHolder.unlockCanvasAndPost(canvas);//解锁画布
                    }
                    xpos += 8;
            }
        };
        Looper.loop();//循环等待
    }
    /**
     * 清理画布
     * */
    public void ClearDraw(){

        Canvas canvas = null;
        try{
            canvas = surfaceHolder.lockCanvas(null);
            canvas.drawColor(Color.WHITE);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
            xpos = 0;//清理画布之后x轴起点重新置0

        }catch(Exception e){


        }finally{

            if(canvas != null){
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {

    }
}
