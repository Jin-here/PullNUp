package com.vgaw.androidtest.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.vgaw.androidtest.R;

/**
 * from : Volodymyr
 * to : caojinmail@163.com
 * me : github.com/VolodymyrCj/
 */
public class RefreshLayout1 extends FrameLayout {
    // 下拉状态
    private static final int STATUS_PULL_TO_REFRESH = 0;
    // 释放立即刷新状态
    private static final int STATUS_RELEASE_TO_REFRESH = 1;
    // 正在刷新状态
    private static final int STATUS_REFRESHING = 2;
    // 刷新完成或未刷新状态
    private static final int STATUS_REFRESH_FINISHED = 3;

    // 滑动阻力系数
    private static final int RESISTANCE = 2;

    private View refreshView;
    private TextView tv_hint;
    private ImageView iv_loading;
    private int refreshHeight;

    private ListView lv;

    private int status;

    public RefreshLayout1(Context context, AttributeSet attrs) {
        super(context, attrs);
        addRefreshView(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        lv = (ListView) getChildAt(1);
        lv.setOnTouchListener(touchListener);
    }

    private void addRefreshView(Context context) {
        refreshView = LayoutInflater.from(context).inflate(R.layout.refresh_header1, null);
        tv_hint = (TextView) refreshView.findViewById(R.id.tv_hint);
        iv_loading = (ImageView) refreshView.findViewById(R.id.iv_loading);
        iv_loading.setImageResource(R.drawable.loadinganimation);
        changeRefreshHeaderHint(STATUS_REFRESH_FINISHED);
        addView(refreshView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshHeight = refreshView.getMeasuredHeight();
    }

    private float lastY;
    private float nowY;
    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (canPull()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        nowY = event.getRawY();
                        float dy = nowY - lastY;
                        lastY = nowY;
                        // 添加阻力
                        dy /= RESISTANCE;
                        if (dy > 0) {
                            moveDown((int) dy);
                        }else {
                            if (getTopMargin(lv) > 0){
                                moveUp(-(int) dy);
                            }else {
                                return false;
                            }
                        }
                        if (canRefresh() > 0) {
                            changeRefreshHeaderHint(STATUS_RELEASE_TO_REFRESH);
                        }else {
                            changeRefreshHeaderHint(STATUS_PULL_TO_REFRESH);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (status == STATUS_PULL_TO_REFRESH) {
                            moveUp(refreshHeight + getTopMargin(refreshView));
                            return true;
                        } else if (status == STATUS_RELEASE_TO_REFRESH) {
                            int backHeight = canRefresh();
                            if (backHeight > 0) {
                                moveUp(backHeight);
                            }
                            if (status != STATUS_REFRESHING){
                                changeRefreshHeaderHint(STATUS_REFRESHING);
                                if (listener != null){
                                    listener.onRefresh();
                                }
                            }
                            return true;
                        }
                        break;
                }
            }
            return false;
        }
    };

    /**
     * 是否可以下拉
     * @return
     */
    private boolean canPull(){
        if ((lv.getChildCount() == 0) || (lv.getFirstVisiblePosition() == 0 &&
                lv.getChildAt(0).getTop() == 0)) {
            return true;
        }
        return false;
    }

    /**
     * 通知刷新结束
     */
    public void notifyFinished() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                changeRefreshHeaderHint(STATUS_REFRESH_FINISHED);
                moveUp(getTopMargin(lv));
            }
        }, 1000);

    }

    /**
     * 通知刷新状态
     */
    public void notifyRefreshing(){
        changeRefreshHeaderHint(STATUS_REFRESHING);
        moveDown(tv_hint.getTop());
    }

    /**
     * 下滑
     * @param dy
     */
    private void moveDown(int dy) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) lv.getLayoutParams();
        int result = marginLayoutParams.topMargin + dy;
        // 防止溢出最顶端
        if (result > refreshHeight) {
            result = refreshHeight;
        }
        marginLayoutParams.topMargin = result;
        lv.setLayoutParams(marginLayoutParams);
    }

    /**
     * 上滑
     * @param dy
     */
    private void moveUp(int dy){
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) lv.getLayoutParams();
        int result = marginLayoutParams.topMargin - dy;
        // 防止溢出最底端
        if (result < 0) {
            result = 0;
        }
        marginLayoutParams.topMargin = result;
        lv.setLayoutParams(marginLayoutParams);
    }

    /**
     * 是否可以进行刷新
     * @return 偏移量
     */
    private int canRefresh() {
        return getTopMargin(lv) - tv_hint.getTop();
    }

    private int getTopMargin(View view) {
        return ((MarginLayoutParams) view.getLayoutParams()).topMargin;
    }

    /**
     * 更改状态提示
     */
    private void changeRefreshHeaderHint(int status) {
        this.status = status;
        switch (status) {
            case STATUS_PULL_TO_REFRESH:
                //tv_hint.setText("下拉可以刷新");
                //break;
            case STATUS_RELEASE_TO_REFRESH:
                //tv_hint.setText("松开刷新");
                refreshView.setVisibility(VISIBLE);
                break;
            case STATUS_REFRESHING:
                ((AnimationDrawable) iv_loading.getDrawable()).start();
                refreshView.setVisibility(VISIBLE);
                //tv_hint.setText("刷新中。。。");
                break;
            case STATUS_REFRESH_FINISHED:
                //tv_hint.setText("刷新完成");
                ((AnimationDrawable) iv_loading.getDrawable()).stop();
                refreshView.setVisibility(INVISIBLE);
                break;
        }
    }

    private OnRefreshListener listener;

    public void setOnRefreshListener(OnRefreshListener listener){
        this.listener = listener;
    }

    public interface OnRefreshListener{
        void onRefresh();
    }

}