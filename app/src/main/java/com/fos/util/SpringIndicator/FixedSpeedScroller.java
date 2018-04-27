package com.fos.util.SpringIndicator;

import android.content.Context;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: TODO
 * @date 2018/4/26 20:49
 */

public class FixedSpeedScroller extends Scroller {
    private int mDuration=1000;
    boolean useFixedSpeed=false;

//    public FixedSpeedScroller(Context context) {
//        super(context);
//    }

    public FixedSpeedScroller(Context context, DecelerateInterpolator decelerateInterpolator) {
        super(context,decelerateInterpolator);
    }
//    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel){
//        super(context,interpolator,flywheel);
//    }
//    public void setScrollAtFixedSpeed(int paramInt){
//        this.useFixedSpeed=true;
//        this.mDuration=paramInt;
//    }
    public void startScroll(int startX,int startY,int dx,int dy,int duration){
        super.startScroll(startX,startY,dx,dy,mDuration);
    }

    public void startScroll(int startX,int startY,int dx,int dy){
     super.startScroll(startX,startY,dx,dy,mDuration);
    }

    public void setDuration(int duration) {
        this.mDuration=duration;
    }
}
