package com.fos.util;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by Apersonalive on 2017/12/27.
 */
public class MyViewPager extends ViewPager {
    public MyViewPager(Context context) {
        super(context);
    }
    public MyViewPager(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    public PagerAdapter getAdapter() {
        return super.getAdapter();
    }
}
