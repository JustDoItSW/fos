package com.fos.activity;

import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.fos.R;
import com.fos.fragment.guide_fragment.BaseFragmentAdapter;
import com.fos.fragment.guide_fragment.DataAnalysiscLauncherFragment;
import com.fos.fragment.guide_fragment.GuideViewPager;
import com.fos.fragment.guide_fragment.LauncherBaseFragment;
import com.fos.fragment.guide_fragment.PlantsDataLauncherFragment;
import com.fos.fragment.guide_fragment.VideoLauncherFragment;
import com.fos.fragment.guide_fragment.WelcomeLauncherFragment;
import com.fos.util.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: cwxiong
 * @e-mail: 1451780593@qq.com
 * @Company: CSUFT
 * @Description: 第一次进入时的引导页面，第二次进入将不再显示这个Acivity
 * @date 2018/5/2 21:20t
 */

public class GuideLauncherActivity extends FragmentActivity {
    private GuideViewPager vPager;
    private List<LauncherBaseFragment> list = new ArrayList<LauncherBaseFragment>();
    private BaseFragmentAdapter adapter;

    private ImageView[] tips;
    private int currentSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        ActivityCollector.addActivity(this);

        //初始化点点点控件
        ViewGroup group = (ViewGroup)findViewById(R.id.viewGroup);
        tips = new ImageView[4];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                imageView.setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            tips[i]=imageView;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 20;//设置点点点view的左边距
            layoutParams.rightMargin = 20;//设置点点点view的右边距
            group.addView(imageView,layoutParams);
        }

        //获取自定义viewpager 然后设置背景图片
        vPager = (GuideViewPager) findViewById(R.id.viewpager_launcher);
        vPager.setBackGroud(BitmapFactory.decodeResource(getResources(),R.drawable.bg));

        /**
         * 初始化三个fragment  并且添加到list中
         */
        VideoLauncherFragment rewardFragment = new VideoLauncherFragment();
        DataAnalysiscLauncherFragment privateFragment = new DataAnalysiscLauncherFragment();
        WelcomeLauncherFragment stereoscopicFragment = new WelcomeLauncherFragment();
        PlantsDataLauncherFragment dataFragment=new PlantsDataLauncherFragment();

        list.add(rewardFragment);
        list.add(privateFragment);
        list.add(dataFragment);
        list.add(stereoscopicFragment);

        adapter = new BaseFragmentAdapter(getSupportFragmentManager(),list);
        vPager.setAdapter(adapter);
        vPager.setOffscreenPageLimit(2);
        vPager.setCurrentItem(0);
        vPager.setOnPageChangeListener(changeListener);
    }

    /**
     * 监听viewpager的移动
     */
    ViewPager.OnPageChangeListener changeListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int index) {
            setImageBackground(index);//改变点点点的切换效果
            LauncherBaseFragment fragment=list.get(index);

            list.get(currentSelect).stopAnimation();//停止前一个页面的动画
            fragment.startAnimation();//开启当前页面的动画

            currentSelect=index;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}
        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    /**
     * 改变点点点的切换效果
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}

