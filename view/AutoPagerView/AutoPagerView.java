package com.test.test;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * author：rongxianzhuo on 2016/8/1 15:44
 * email：rongxianzhuo@gmail.com
 * 自动轮播PagerView，在xml声明后，在代码中调用其setup方法
 */
public class AutoPagerView extends ViewPager {

    private class Adapter extends PagerAdapter {

        private View[] views;

        public Adapter(View[] views) {
            this.views = views;
        }

        @Override
        public int getCount() {

            if (views != null) return views.length;
            else return 0;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views[position]);
            return views[position];
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    private Adapter adapter;

    public AutoPagerView(Context context) {
        super(context);
    }

    public AutoPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 初始化
     * @param views 要轮播的视图
     * @param duration 轮播间隔（毫秒）
     */
    public void setup(final View[] views, final int duration) {
        adapter = new Adapter(views);
        setAdapter(adapter);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            public void run() {
                setCurrentItem((getCurrentItem() + 1) % views.length , true);
                handler.postDelayed(this, duration);
            }

        }, duration);
    }
}
