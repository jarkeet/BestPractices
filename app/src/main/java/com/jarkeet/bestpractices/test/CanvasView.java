package com.jarkeet.bestpractices.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.jarkeet.bestpractices.R;

import androidx.annotation.Nullable;

public class CanvasView extends View implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {

    private String TAG = "CanvasView";

    private GestureDetector gestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;

    private int mScrollX;
    private float mScaleFactor;

    private int mWith;
    private int mHeight;

    private Paint mPaintRed;
    private Paint mPaintGreen;
    private Paint mPaintYellow;



    public CanvasView(Context context) {
        super(context);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initDraw();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDraw();
    }

    public CanvasView(Context context, @Nullable  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initDraw();
    }

    private void initDraw() {
        mPaintRed = new Paint();
        mPaintGreen = new Paint();
        mPaintYellow = new Paint();
        mPaintRed.setColor(getResources().getColor(R.color.red));
        mPaintGreen.setColor(getResources().getColor(R.color.green));
        mPaintYellow.setColor(getResources().getColor(R.color.yellow));
        mPaintYellow.setStrokeWidth(5);

        gestureDetector = new GestureDetector(getContext(), this);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWith = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(mWith, mHeight);
        Log.d(TAG, "mWith : " + mWith);
        Log.d(TAG, "mHeight : " + mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.parseColor("#0000ff"));

        canvas.save();
        canvas.translate(mWith / 2,  mHeight / 2);
        canvas.drawRect(new Rect(0 ,0, 400, 400), mPaintRed);
        canvas.drawLine(0, -mHeight / 2 ,0, mHeight /2, mPaintYellow);
        canvas.drawLine(-mWith / 2, 0 ,mWith / 2, 0, mPaintYellow);
        canvas.restore();


        canvas.save();
        canvas.scale(2f, 2f);
        canvas.drawRect(new Rect(0 ,0, 200, 200), mPaintGreen);
        canvas.restore();


        canvas.save();
        canvas.translate(mWith / 2,  mHeight / 2);
        canvas.drawRect(new Rect(0 ,0, 200, 200), mPaintYellow);
//        scrollTo(-200,0);
        canvas.restore();


        canvas.save();
//        canvas.translate(mWith / 2,  mHeight / 2);
        canvas.drawRect(new Rect(0 ,0, 200, 200), mPaintRed);
//        scrollTo(0,0);
        canvas.restore();

        canvas.scale(mScaleFactor, 0);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
//        return gestureDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown : " + e.getX() + "," + e.getY());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp : " + e.getX() + "," + e.getY());
        return true;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        mScrollX += distanceX;
        scrollTo((int)mScrollX,0);
        Log.d(TAG, "distanceX : " + distanceX);
        Log.d(TAG, "mScrollX : " + mScrollX);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        Log.d(TAG ,"getFocusX: " +  detector.getFocusX());
        Log.d(TAG , "getFocusY: " + detector.getFocusY());

        mScaleFactor *= detector.getScaleFactor();
        Log.d(TAG, "getScaleFactor: " +  detector.getScaleFactor());
        Log.d(TAG, "mScaleFactor: " +  mScaleFactor);

        invalidate();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }
}
