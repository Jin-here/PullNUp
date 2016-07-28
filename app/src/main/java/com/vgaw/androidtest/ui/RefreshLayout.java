package com.vgaw.androidtest.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vgaw.androidtest.R;

/**
 * from : Volodymyr
 * to : caojinmail@163.com
 * me : github.com/VolodymyrCj/
 */
public class RefreshLayout extends LinearLayout {
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
    private int refreshHeight;

    private ListView lv;

    private int status;

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        addRefreshHeader(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        lv = (ListView) getChildAt(1);
        lv.setOnTouchListener(touchListener);
    }

    private void addRefreshHeader(Context context) {
        refreshView = LayoutInflater.from(context).inflate(R.layout.refresh_header, null);
        tv_hint = (TextView) refreshView.findViewById(R.id.tv_hint);
        changeRefreshHeaderHint(STATUS_REFRESH_FINISHED);
        addView(refreshView);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshHeight = refreshView.getMeasuredHeight();
        ((MarginLayoutParams) refreshView.getLayoutParams()).topMargin = -refreshHeight;
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
                        if (dy < ViewConfiguration.get(getContext()).getScaledTouchSlop()){
                            return false;
                        }
                        lastY = nowY;
                        // 添加阻力
                        dy /= RESISTANCE;
                        if (dy > 0) {
                            moveDown((int) dy);
                        }else {
                            if (-getTopMargin(refreshView) < refreshHeight){
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
                        } else if (status == STATUS_RELEASE_TO_REFRESH) {
                            int backHeight = canRefresh();
                            if (backHeight > 0) {
                                moveUp(backHeight);
                            }
                            changeRefreshHeaderHint(STATUS_REFRESHING);
                            if (listener != null){
                                listener.onRefresh();
                            }
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
        changeRefreshHeaderHint(STATUS_REFRESH_FINISHED);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                moveUp(refreshHeight + getTopMargin(refreshView));
                changeRefreshHeaderHint(STATUS_PULL_TO_REFRESH);
            }
        }, 500);
    }

    /**
     * 下滑
     * @param dy
     */
    private void moveDown(int dy) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) refreshView.getLayoutParams();
        int result = marginLayoutParams.topMargin + dy;
        // 防止溢出最顶端
        if (result > 0) {
            result = 0;
        }
        marginLayoutParams.topMargin = result;
        refreshView.setLayoutParams(marginLayoutParams);
    }

    /**
     * 上滑
     * @param dy
     */
    private void moveUp(int dy){
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) refreshView.getLayoutParams();
        int result = marginLayoutParams.topMargin - dy;
        // 防止溢出最底端
        if (-result > refreshHeight) {
            result = -refreshHeight;
        }
        marginLayoutParams.topMargin = result;
        refreshView.setLayoutParams(marginLayoutParams);
    }

    /**
     * 是否可以进行刷新
     * @return 偏移量
     */
    private int canRefresh() {
        return getTopMargin(refreshView) + tv_hint.getTop();
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
                tv_hint.setText("下拉可以刷新");
                break;
            case STATUS_RELEASE_TO_REFRESH:
                tv_hint.setText("松开刷新");
                break;
            case STATUS_REFRESHING:
                tv_hint.setText("刷新中。。。");
                break;
            case STATUS_REFRESH_FINISHED:
                tv_hint.setText("刷新完成");
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
