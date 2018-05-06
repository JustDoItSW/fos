package com.fos.fragment.guide_fragment;

import android.support.v4.app.Fragment;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: 公共父接口，提供开始动画和结束动画方法
 * @date 2018/5/2 21:22
 */

public abstract class LauncherBaseFragment extends Fragment {
    public abstract void  startAnimation();
    public abstract void  stopAnimation();
}
