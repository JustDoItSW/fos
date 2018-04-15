package com.fos.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fos.fragment.HumFragment;
import com.fos.fragment.LuxFragment;
import com.fos.fragment.SoilHumFragment;
import com.fos.fragment.TempFragment;

/**
 * Created by Apersonalive on 2018/4/14.
 */

public class MyDataFragmentPagerAdapter extends FragmentPagerAdapter {

    private  final  int PAGER_COUNT = 4;
    private Fragment fragment1;
    private Fragment fragment2;
    private Fragment fragment3;

    public Fragment getFragment4() {
        return fragment4;
    }

    public void setFragment4(Fragment fragment4) {
        this.fragment4 = fragment4;
    }

    private Fragment fragment4;

    public Fragment getFragment1() {
        return fragment1;
    }

    public void setFragment1(Fragment fragment1) {
        this.fragment1 = fragment1;
    }

    public Fragment getFragment2() {
        return fragment2;
    }

    public void setFragment2(Fragment fragment2) {
        this.fragment2 = fragment2;
    }

    public Fragment getFragment3() {
        return fragment3;
    }

    public void setFragment3(Fragment fragment3) {
        this.fragment3 = fragment3;
    }

    public MyDataFragmentPagerAdapter(FragmentManager fm){
        super(fm);
        fragment1 = TempFragment.newInstance();
        fragment2 = HumFragment.newInstance();
        fragment3 = SoilHumFragment.newInstance();
        fragment4 = LuxFragment.newInstance();

    }
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = fragment1;
                break;
            case 1:
                fragment = fragment2;
                break;
            case 2:
                fragment = fragment3;
                break;
            case 3:
                fragment = fragment4;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
