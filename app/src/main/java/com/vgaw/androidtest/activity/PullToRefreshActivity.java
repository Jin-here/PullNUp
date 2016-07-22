package com.vgaw.androidtest.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.vgaw.androidtest.EasyAdapter;
import com.vgaw.androidtest.R;
import com.vgaw.androidtest.ui.RefreshLayout;

import java.util.ArrayList;

public class PullToRefreshActivity extends AppCompatActivity {
    private RefreshLayout rl;
    private ListView lv;
    private EasyAdapter adapter;
    private ArrayList<String> dataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulltorefresh);
        lv = (ListView) findViewById(R.id.lv);
        getData();
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.test_list_item, android.R.id.text1, dataList));
        rl = (RefreshLayout)findViewById(R.id.rl);
        rl.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rl.notifyFinished();
                    }
                }, 1000);
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
