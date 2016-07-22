package com.vgaw.androidtest.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * from : Volodymyr
 * to : caojinmail@163.com
 * me : github.com/VolodymyrCj/
 */
public class NavigationActivity extends Activity {
    private String[] nameArray = new String[]{"下拉刷新", "下拉刷新1", "上拉加载更多"};
    private Class[] classArray = new Class[]{PullToRefreshActivity.class, PullToRefreshActivity1.class, LoadMoreActivity.class};
    @Override
    protected void onResume() {
        super.onResume();
        new AlertDialog.Builder(this)
                .setItems(nameArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(NavigationActivity.this, classArray[which]));
                    }
                })
                .create().show();
    }
}
