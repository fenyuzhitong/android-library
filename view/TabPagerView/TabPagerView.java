package com.test.test;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * author：rongxianzhuo on 2016/7/27 14:39
 * email：rongxianzhuo@gmail.com
 * 自定义组合View，快速实现TabLayout + ViewPager 效果
 * 使用方法：
 * 1.在xml中加入该View
 * 2.代码中调用其setup方法
 */
public class TabPagerView extends LinearLayout {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private TabPagerView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        tabLayout = new TabLayout(context);
        viewPager = new ViewPager(context);
        addView(tabLayout);
        addView(viewPager);
    }

    public TabPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        tabLayout = new TabLayout(context, attrs);
        viewPager = new ViewPager(context, attrs);
        viewPager.setId(1);
        addView(tabLayout);
        addView(viewPager);
    }

    /**
     * TabPagerView id can not be 'tab_layout' or 'tab_pager'
     * @param activity 所在的activity
     * @param pagerTitles 标题数组
     * @param fragments 碎片数组
     */
    public void setup(AppCompatActivity activity, String[] pagerTitles, Fragment[] fragments){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(activity.getSupportFragmentManager(), pagerTitles, fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments;
        private String[] titles;
        public SectionsPagerAdapter(FragmentManager fm, String[] titles, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
            this.titles = titles;
        }
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }
        @Override
        public int getCount() {
            return fragments.length;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public TabLayout getTabLayout() {
        return tabLayout;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}