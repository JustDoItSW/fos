package com.fos.fragment.guide_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fos.R;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: 对应MainActivity中的第三个Fragment
 * @date 2018/5/3 21:25
 */

public class PlantsDataLauncherFragment extends LauncherBaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_plants_launcher,null);
        return view;
    }

    @Override
    public void startAnimation() {

    }

    @Override
    public void stopAnimation() {

    }
}
