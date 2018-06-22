package com.fos.myView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Apersonalive on 2018/6/11.
 */

public class WaveView extends View {
    private float mInitialRadius=88;   // 初始波纹半径
    private float mMaxRadiusRate = 0.85f;   // 如果没有设置mMaxRadius，可mMaxRadius = 最小长度 * mMaxRadiusRate;
    private float mMaxRadius;   // 最大波纹半径
    private long mDuration = 1000; // 一个波纹从创建到消失的持续时间
    private int mSpeed = 400;   // 波纹的创建速度，每500ms创建一个
    private Interpolator mInterpolator = new FastOutLinearInInterpolator();

    private List<Circle> mCircleList = new ArrayList<Circle>();
    private boolean mIsRunning;

    private boolean mMaxRadiusSet;

    private Paint mPaint;
    private long mLastCreateTime;

    private Runnable mCreateCircle = new Runnable() {
        @Override
        public void run() {
            if (mIsRunning) {
                newCircle();
                postDelayed(mCreateCircle, mSpeed);
            }
        }
    };

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(3);
        setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#2c99e9"));
    }

    public void setStyle(Paint.Style style) {
        mPaint.setStyle(style);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (!mMaxRadiusSet) {
            mMaxRadius = Math.min(w, h) * mMaxRadiusRate / 2.0f;
        }
    }

    public void setMaxRadiusRate(float maxRadiusRate) {
        this.mMaxRadiusRate = maxRadiusRate;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }
    public void setStrokeWidth(float f){
        mPaint.setStrokeWidth(f);
    }

    /**
     * 开始
     */
    public void start() {
        if (!mIsRunning) {
            mIsRunning = true;
            mCreateCircle.run();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        mIsRunning = false;
    }

    protected void onDraw(Canvas canvas) {
        Iterator<Circle> iterator = mCircleList.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
                mPaint.setAlpha(circle.getAlpha());
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, circle.getCurrentRadius(), mPaint);
            } else {
                iterator.remove();
            }
        }
        if (mCircleList.size() > 0) {
            postInvalidateDelayed(10);
        }
    }

    public void setInitialRadius(float radius) {
        mInitialRadius = radius;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public void setMaxRadius(float maxRadius) {
        this.mMaxRadius = maxRadius;
        mMaxRadiusSet = true;
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    private void newCircle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastCreateTime < mSpeed) {
            return;
        }
        Circle circle = new Circle();
        mCircleList.add(circle);
        invalidate();
        mLastCreateTime = currentTime;
    }

    private class Circle {
        private long mCreateTime;

        public Circle() {
            this.mCreateTime = System.currentTimeMillis();
        }

        public int getAlpha() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) ((1.0f - mInterpolator.getInterpolation(percent)) * 255);
        }

        public float getCurrentRadius() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius);
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }
}
