package com.vgaw.androidtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

/**
 * from : Volodymyr
 * to : caojinmail@163.com
 * me : github.com/VolodymyrCj/
 */
public class LoadMoreActivity extends Activity {
    private LoadMoreListView lv;
    private ArrayAdapter<String> adapter;

    private ArrayList<String> dataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadmore);

        lv = (LoadMoreListView) findViewById(R.id.lv);
        lv.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addData();
                        lv.notifyFinished();
                        adapter.notifyDataSetChanged();
                    }
                }, 1000);
            }
        });
        getData();
        adapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item, android.R.id.text1, dataList);
        lv.setAdapter(adapter);
        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getData() {
        dataList.clear();
        int count = new Random().nextInt(43);
        for (int i = 0; i < count; i++) {
            dataList.add(String.valueOf(i));
        }
    }

    public void addData(){
        for (int i = 0;i < 3;i++){
            dataList.add(String.valueOf(i));
        }
    }
}
