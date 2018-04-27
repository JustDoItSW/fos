package com.fos.util;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import com.fos.util.SpringIndicator.FixedSpeedScroller;

import java.lang.reflect.Field;

/**
 * Created by Apersonalive on 2017/12/27.
 */
public class MyViewPager extends ViewPager {

    private static final String TAG=MyViewPager.class.getSimpleName();
    private int duration=1000;

    public MyViewPager(Context context) {
        super(context);
    }
    public MyViewPager(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public void fixScrollSpeed(){
        fixScrollSpeed(duration);
    }
    public void fixScrollSpeed(int duration){
        this.duration=duration;
        setScrollSpeedUsingRefection(duration);
    }

    private void setScrollSpeedUsingRefection(int duration) {
        try{
            Field localField=ViewPager.class.getDeclaredField("mScroller");
            localField.setAccessible(true);
            FixedSpeedScroller scroller=new FixedSpeedScroller(getContext(),new DecelerateInterpolator(1.5f));
            scroller.setDuration(duration);
            localField.set(this,scroller);
            return;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        }catch (IllegalArgumentException e){
            return false;
        }
    }

    @Override
    public PagerAdapter getAdapter() {
        return super.getAdapter();
    }
}
