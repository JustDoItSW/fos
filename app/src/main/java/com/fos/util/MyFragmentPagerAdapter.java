package com.fos.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fos.fragment.ControlFragment;
import com.fos.fragment.DataFragment;
import com.fos.fragment.FlowerFragment;

/**
 * Created by Apersonalive（丁起柠） on 2018/1/2 14 32.
 * Project_name MyWord
 * Package_name myword.demo.com.myword.Util
 * Email 745267209@QQ.com
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private  final  int PAGER_COUNT = 3;
    private Fragment controlFragment = null;
    private Fragment dataFragment = null;
    private Fragment flowerFragment = null;
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        //添加三个Fragment
        controlFragment = ControlFragment.newInstance();
        dataFragment = DataFragment.newInstance();
        flowerFragment = FlowerFragment.newInstance();
    }

    public Fragment getControlFragment() {
        return controlFragment;
    }

    public void setControlFragment(Fragment controlFragment) {
        this.controlFragment = controlFragment;
    }

    public Fragment getDataFragment() {
        return dataFragment;
    }

    public void setDataFragment(Fragment dataFragment) {
        this.dataFragment = dataFragment;
    }

    public Fragment getFlowerFragment() {
        return flowerFragment;
    }

    public void setFlowerFragment(Fragment flowerFragment) {
        this.flowerFragment = flowerFragment;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = controlFragment;
                break;
            case 1:
                fragment = dataFragment;
                break;
            case 2:
                fragment = flowerFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
