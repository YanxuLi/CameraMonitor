package com.media.dingping.cameramonitor.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.media.dingping.cameramonitor.R;

public class MySurfaceView extends SurfaceView implements Callback, Runnable {

    private final String TAG = getClass().getSimpleName();
    private Thread th;
    private SurfaceHolder sfh;
    private Canvas canvas;
    private Paint paint;
    private boolean flag;
    //固定摇杆背景圆形的X,Y坐标以及半径
    private int RockerCircleX = 150;
    private int RockerCircleY = 150;
    private int RockerCircleR = 100;
    //摇杆的X,Y坐标以及摇杆的半径
    private float SmallRockerCircleX = 150;
    private float SmallRockerCircleY = 150;
    private float SmallRockerCircleR = 50;

    private OnCtrlListener mOnCtrlListener;
    private int mCurrDirection;
    private boolean hasStart;

    private int bigCircleColor;
    private int smallCircleColor;
    private int backGroundDrawable;


    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getAttrs(context, attrs);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        getAttrs(context, attrs);
    }

    /**
     * 得到属性值
     *
     * @param context
     * @param attrs
     */
    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MySurfaceView);
        bigCircleColor = ta.getColor(R.styleable.MySurfaceView_bigcirclebackgroundcolor, Color.TRANSPARENT);
        smallCircleColor = ta.getColor(R.styleable.MySurfaceView_smallcirclecolor, Color.TRANSPARENT);
        boolean isFullSrceen = ta.getBoolean(R.styleable.MySurfaceView_backgroundsrc, false);
        if (isFullSrceen) {
            backGroundDrawable = R.drawable.direction2;
        } else {
            backGroundDrawable = R.drawable.direction;
        }
        ta.recycle();
    }

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Log.v("Himi", "MySurfaceView");
        this.setKeepScreenOn(true);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }


    public void surfaceCreated(SurfaceHolder holder) {
        th = new Thread(this);
        flag = true;
        th.start();
    }

    /***
     * 得到两点之间的弧度
     */
    public double getRad(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float x = px2 - px1;
        //得到两点Y的距离
        float y = py1 - py2;
        //算出斜边长
        float xie = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = x / xie;

        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            // 当触屏区域不在活动范围内
            if (Math.sqrt(Math.pow((RockerCircleX - (int) event.getX()), 2) + Math.pow((RockerCircleY - (int) event.getY()), 2)) >= RockerCircleR) {
                //得到摇杆与触屏点所形成的角度
                double tempRad = getRad(RockerCircleX, RockerCircleY, event.getX(), event.getY());
                //保证内部小圆运动的长度限制
                getXY(RockerCircleX, RockerCircleY, RockerCircleR, tempRad);
                int i = -(int) (tempRad * 60);
                int realRad = i >= 0 ? i : (360 + i);
                int currDirection = -1;
                if ((realRad >= 0 && realRad <= 45) || (realRad > 315 && realRad <= 360)) {
                    currDirection = 3;
                } else if (realRad > 45 && realRad <= 135) {
                    currDirection = 0;
                } else if (realRad > 135 && realRad <= 225) {
                    currDirection = 2;
                } else if (realRad > 225 && realRad <= 315) {
                    currDirection = 1;
                }

                if (mOnCtrlListener != null) {
                    if (hasStart) {
                        if (mCurrDirection == currDirection) {
                            return true;
                        } else {
                            mOnCtrlListener.onCtrl(mCurrDirection, 1);
                            mOnCtrlListener.onCtrl(currDirection, 0);
                            mCurrDirection = currDirection;
                            hasStart = true;
                        }
                    } else {
                        mOnCtrlListener.onCtrl(currDirection, 0);
                        hasStart = true;
                    }
                }
            } else {//如果小球中心点小于活动区域则随着用户触屏点移动即可
                SmallRockerCircleX = (int) event.getX();
                SmallRockerCircleY = (int) event.getY();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //up的时候调用停止
            if (hasStart) {
                mOnCtrlListener.onCtrl(mCurrDirection, 1);
                hasStart = false;
            }
            //当释放按键时摇杆要恢复摇杆的位置为初始位置
            SmallRockerCircleX = getWidth() / 2;
            SmallRockerCircleY = getHeight() / 2;
        }
        return true;
    }

    /**
     * @param R       圆周运动的旋转点
     * @param centerX 旋转点X
     * @param centerY 旋转点Y
     * @param rad     旋转的弧度
     */
    public void getXY(float centerX, float centerY, float R, double rad) {
        //获取圆周运动的X坐标
        SmallRockerCircleX = (float) (R * Math.cos(rad)) + centerX;
        //获取圆周运动的Y坐标
        SmallRockerCircleY = (float) (R * Math.sin(rad)) + centerY;
    }

    public void draw() {
        try {
            canvas = sfh.lockCanvas();
//            canvas.drawColor(Color.parseColor("#E5E5E5"));
            canvas.drawColor(bigCircleColor);
            //设置透明度
//            paint.setColor(0x70000000);
            //绘制摇杆背景
//            canvas.drawCircle(RockerCircleX, RockerCircleY, RockerCircleR, paint);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), backGroundDrawable);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int left = getWidth() / 2 - width / 2;
            int top = getHeight() / 2 - height / 2;
            Rect rect1 = new Rect(left, top, left + width, top + height);
            Rect rect = new Rect(0, 0, width, height);
            canvas.drawBitmap(bitmap, rect, rect1, null);
//            paint.setColor(0x70ffffff);
            paint.setColor(smallCircleColor);
            //绘制摇杆
            canvas.drawCircle(SmallRockerCircleX, SmallRockerCircleY, SmallRockerCircleR, paint);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            try {
                if (canvas != null)
                    sfh.unlockCanvasAndPost(canvas);
            } catch (Exception e2) {

            }
        }
    }

    public void run() {
        // TODO Auto-generated method stub
        while (flag) {
            draw();
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v("Himi", "surfaceChanged");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
        Log.v("Himi", "surfaceDestroyed");
    }

    public void addCtrlListener(OnCtrlListener onCtrlListener) {
        this.mOnCtrlListener = onCtrlListener;

    }

    public interface OnCtrlListener {
        void onCtrl(int direction, int state);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                widthSize = 300;
                heightSize = 300;
                break;
            case MeasureSpec.EXACTLY:
                widthSize = heightSize = Math.min(widthSize, heightSize);
                break;
        }
        setMeasuredDimension(widthSize, heightSize);
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        Log.i(TAG, "中心点横坐标：" + x);
        Log.i(TAG, "中心点纵坐标：" + y);
        RockerCircleX = x;
        RockerCircleY = y;
        SmallRockerCircleX = x;
        SmallRockerCircleY = y;
    }
}