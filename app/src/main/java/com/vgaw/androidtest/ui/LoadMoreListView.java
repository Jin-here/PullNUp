package com.vgaw.androidtest.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.vgaw.androidtest.R;

/**
 * from : Volodymyr
 * to : caojinmail@163.com
 * me : github.com/VolodymyrCj/
 */
public class LoadMoreListView extends ListView {
    private static final int STATUS_FINISHED = 1;
    private static final int STATUS_LOADING = 2;
    private static final int STATUS_NOMORE = 3;
    private static final int STATUS_ERROR = 4;

    private View footerView;
    private TextView tv_hint;

    private int status;

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFooterView(context);
        setFooterDividersEnabled(false);
        addFooterView(footerView, null, false);
        setOnScrollListener(scrollListener);

    }

    private OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // 前提：必有footer
            // #1 firstVisibleItem == 0 && 最后一个非footer的item.getBottom + dividerheight > getheight，此时不刷新
            // #2 否则如果firstVisibleItem + visibleItemCount > totalItemCount - 1，此时刷新
            if (totalItemCount == visibleItemCount) {
                return;
            }

            if (!(firstVisibleItem == 0 && getChildAt(visibleItemCount - 2).getBottom() + getDividerHeight() < getHeight())
                    && firstVisibleItem + visibleItemCount > totalItemCount - 1) {
                if (status == STATUS_FINISHED){
                    if (listener != null){
                        changeFooterHint(STATUS_LOADING);
                        listener.onLoadMore();
                    }
                }
            }
        }
    };

    private void initFooterView(Context context) {
        footerView = LayoutInflater.from(context).inflate(R.layout.layout_loadmore, null);
        tv_hint = (TextView) footerView.findViewById(R.id.tv_hint);
        changeFooterHint(STATUS_FINISHED);
    }

    /**
     * 更改状态提示
     */
    private void changeFooterHint(int status) {
        this.status = status;
        switch (status) {
            case STATUS_FINISHED:
                tv_hint.setText("");
                break;
            case STATUS_LOADING:
                tv_hint.setText("加载中。。。");
                break;
            case STATUS_NOMORE:
                tv_hint.setText("没有更多啦");
                break;
            case STATUS_ERROR:
                tv_hint.setText("加载出错");
                break;
        }
    }

    public void notifyFinished(){
        changeFooterHint(STATUS_FINISHED);
    }

    public void notifyNoMore(){
        changeFooterHint(STATUS_NOMORE);
    }

    public void notifyError(){
        changeFooterHint(STATUS_ERROR);
    }

    public OnLoadMoreListener listener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
