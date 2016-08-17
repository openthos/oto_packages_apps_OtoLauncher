package com.openthos.launcher.openthoslauncher.activity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.openthos.launcher.openthoslauncher.R;
import com.openthos.launcher.openthoslauncher.adapter.HomeAdapter;
import com.openthos.launcher.openthoslauncher.adapter.ItemCallBack;
import com.openthos.launcher.openthoslauncher.adapter.RecycleCallBack;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.entity.Consts;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BasicActivity implements RecycleCallBack {
    private RecyclerView mRecyclerView;
    private List<HashMap<String, Object>> mDatas;
    public HomeAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        init();
    }

    private void initData() {
        String[] defaultName = getResources().getStringArray(R.array.default_icon_name);
        TypedArray defaultIcon = getResources().obtainTypedArray(R.array.default_icon);
        Type[] defaultType = {Type.computer, Type.recycle};
        mDatas = new ArrayList<>();
        for (int i = 0; i < defaultName.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", defaultName[i]);
            map.put("isChecked", false);
            map.put("null", false);
            map.put("icon", defaultIcon.getResourceId(i, R.mipmap.ic_launcher));
            map.put("type", defaultType[i]);
            mDatas.add(map);
        }
        initDesktop();
    }

    private void initDesktop() {
        File dir = Environment.getExternalStorageDirectory();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            if (files[i].isDirectory()) {
                map.put("name", files[i].getName());
                map.put("path", files[i].getAbsolutePath());
                map.put("isChecked", false);
                map.put("null", false);
                map.put("icon", R.drawable.ic_app_file);
                map.put("type", Type.directory);

            } else {
                map.put("name", files[i].getName());
                map.put("path", files[i].getAbsolutePath());
                map.put("isChecked", false);
                map.put("null", false);
                map.put("icon", R.drawable.ic_app_text);
                map.put("type", Type.file);
            }
            if (mDatas.size() < Consts.MAX_ICON) {
                mDatas.add(map);
            }
        }
        while (mDatas.size() < Consts.MAX_ICON) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", "");
            map.put("isChecked", false);
            map.put("icon", -1);
            map.put("null", true);
            map.put("path", "");
            map.put("type", Type.blank);
            mDatas.add(map);
        }
    }

    private void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(Consts.MAX_LINE,
                StaggeredGridLayoutManager.HORIZONTAL));
        mAdapter = new HomeAdapter(mDatas, this);

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mItemTouchHelper = new ItemTouchHelper(new ItemCallBack(this));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void itemOnClick(int position, View view) {
    }

    @Override
    public void onMove(int from, int to) {
        if ((Boolean) mDatas.get(from).get("null") != true) {
            if (to > 0 && from>0) {
                synchronized (this) {
                    if (from > to) {
                        int count = from - to;
                        for (int i = 0; i < count; i++) {
                            Collections.swap(mDatas, from - i, from - i - 1);
                        }
                    }
                    if (from < to) {
                        int count = to - from;
                        for (int i = 0; i < count; i++) {
                            Collections.swap(mDatas, from + i, from + i + 1);
                        }
                    }
                    mAdapter.setData(mDatas);
                    mAdapter.notifyItemMoved(from, to);
                    mAdapter.pos = to;
                }
            }
        }
    }
}
