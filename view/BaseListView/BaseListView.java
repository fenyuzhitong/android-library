package com.test.test;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;




/**
 * author：rongxianzhuo on 2016/7/28 09:59
 * email：rongxianzhuo@gmail.com
 * 加强版RecyclerView，项目中常用功能封装其中，主要是耗时加载功能
 * 使用方法
 * 1.将java文件和xml文件拷贝到工程，修改包引用的错误
 * 2.xml中加入该View
 * 3.java中调用其setup方法
 * 4.在回调中加载数据完成后要调用loadingFinish() 方法
 *
 * 使用规则：所有的数据加载都应该写在refresh(), 和 loadMore() 的接口里，setup后的第一次加载可以调用
 */
public class BaseListView extends RelativeLayout {

    public interface Listener {
        int listSize();//列表中Item个数

        void refresh();//用户手动下来刷新的回调
        void loadMore();//滑动到底部，需要加载更多时执行的回调
    }

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView errorMessageView;
    private Listener listener;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private int finalDy = 0;//最近滑动的y轴速度
    private int loadingState = 0;//奇数代表正在加载， 反之为偶数
    private boolean canLoadMore = true;

    private BaseListView(Context context) {
        super(context);
    }

    public BaseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_base_list, this, true);
        recyclerView = (RecyclerView) findViewById(R.id.base_list_view_list_127);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.base_list_view_swipe_layout_127);
        errorMessageView = (TextView) findViewById(R.id.base_list_view_msg_127);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (loadingState % 2 == 1) return;
                loadingState++;
                if (listener == null) swipeRefreshLayout.setRefreshing(false);
                else listener.refresh();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light
                , android.R.color.holo_green_light
                , android.R.color.holo_blue_light
                , android.R.color.holo_orange_light);
        scrollToBottom();
    }

    public void setup(Listener listener, LinearLayoutManager manager, RecyclerView.Adapter adapter) {
        this.listener = listener;
        this.linearLayoutManager = manager;
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    public void setup(Listener listener, StaggeredGridLayoutManager manager, RecyclerView.Adapter adapter) {
        this.listener = listener;
        this.staggeredGridLayoutManager = manager;
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 判断是否滑动到底部的方法
     * 一个是当前滑动的y轴速度要够大
     * 另一个是最新的滑动状态为停止
     * 二者结合代表滑动到底部忽然停止，即意味着滑动到底部
     */
    private void scrollToBottom() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                finalDy = dy;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (loadingState % 2 == 1) return;
                if (scrollToBottom(newState) && finalDy > 10) {
                    loadingState++;
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                            listener.loadMore();
                        }
                    });
                }
            }
        });
    }

    /**
     * 判断当前状态是否停止
     * @param newState 新状态
     * @return 是否停止
     */
    private boolean scrollToBottom(int newState) {
        if (newState != RecyclerView.SCROLL_STATE_IDLE) return false;
        if (staggeredGridLayoutManager != null) {
            int[] lastPosition = {0, 1};
            staggeredGridLayoutManager.findLastVisibleItemPositions(lastPosition);
            return lastPosition[0] == listener.listSize() - 1 || lastPosition[1] == listener.listSize() - 1;
        }
        return linearLayoutManager != null && listener.listSize() == linearLayoutManager.findLastVisibleItemPosition() + 1;
    }

    public void loadingFinish() {
        if (!swipeRefreshLayout.isRefreshing()) return;
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (loadingState % 2 == 0) loadingState+= 2;
                else loadingState++;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 设置错误提示，例如无法联网或者没有数据之类的
     * @param error 错误信息
     */
    public void setErrorMessage(String error) {
        if (error == null || error.isEmpty()) errorMessageView.setVisibility(GONE);
        else {
            errorMessageView.setText(error);
            errorMessageView.setVisibility(VISIBLE);
        }
    }

    /**
     * 通知进行第一次加载
     * BaseListView 会调用loadMore接口，不要在额外的地方加载数据
     */
    public void notifyFirstLoading() {
        if (listener == null) return;
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (loadingState % 2 == 0) loadingState++;
                else loadingState += 2;
                swipeRefreshLayout.setRefreshing(true);
                listener.refresh();
            }
        });
    }

    public void canRefresh(boolean b) {
        swipeRefreshLayout.setEnabled(b);
    }

    public void canLoadMore(boolean b) {
        canLoadMore = b;
    }


}
