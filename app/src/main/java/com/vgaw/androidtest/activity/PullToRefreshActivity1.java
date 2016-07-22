package com.vgaw.androidtest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vgaw.androidtest.EasyAdapter;
import com.vgaw.androidtest.R;
import com.vgaw.androidtest.ui.RefreshLayout;
import com.vgaw.androidtest.ui.RefreshLayout1;

import java.util.ArrayList;

/**
 * from : Volodymyr
 * to : caojinmail@163.com
 * me : github.com/VolodymyrCj/
 */
public class PullToRefreshActivity1 extends Activity {
    private RefreshLayout1 rl;
    private ListView lv;
    private EasyAdapter adapter;
    private ArrayList<String> dataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulltorefresh1);
        lv = (ListView) findViewById(R.id.lv);
        getData();
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.test_list_item, android.R.id.text1, dataList));
        rl = (RefreshLayout1)findViewById(R.id.rl);
        rl.setOnRefreshListener(new RefreshLayout1.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rl.notifyFinished();
            }
        });
    }

    private void getData() {
        dataList.clear();
        for (int i = 0; i < 43; i++) {
            dataList.add(String.valueOf(i));
        }
    }
}
