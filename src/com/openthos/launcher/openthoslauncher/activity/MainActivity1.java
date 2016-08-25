package com.openthos.launcher.openthoslauncher.activity;


import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.adapter.MenuAdapter;
import com.openthos.launcher.openthoslauncher.view.WindowsLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity1 extends BasicActivity  {

    private List<HashMap<String, Object>> mDatas;
    WindowsLayout root;

    public int pos = -1;
    private int mLastClickId = -1;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        root= (WindowsLayout) findViewById(R.id.windows);
        initData();
        initDesktop();
    }

    private void initData() {
        String[] defaultName = getResources().getStringArray(R.array.default_icon_name);

        TypedArray defaultIcon=getResources().obtainTypedArray(R.array.default_icon);

        CharSequence[] defaultIsFile = getResources().getTextArray(R.array.default_is_file);
        CharSequence[] defaultIsApp = getResources().getTextArray(R.array.default_is_app);
        mDatas = new ArrayList<>();
        for (int i = 0; i < defaultName.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", defaultName[i]);
            map.put("isChecked", false);
            map.put("icon", defaultIcon.getResourceId(i,R.mipmap.ic_launcher));
            mDatas.add(map);
        }


        File dir = Environment.getExternalStorageDirectory();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            if (files[i].isDirectory()){
                map.put("name", files[i].getName());
                map.put("path",files[i].getAbsolutePath());
                map.put("isChecked", false);
                map.put("icon", R.drawable.ic_app_file);
            }else {
                map.put("name", files[i].getName());
                map.put("path",files[i].getAbsolutePath());
                map.put("isChecked", false);
                map.put("icon", R.drawable.ic_app_text);
            }
            mDatas.add(map);
        }
    }
    RelativeLayout item;
    ImageView iv;
    TextView tv;
    CheckBox checkBox;

    private void initDesktop() {
        for (int i=0;i<mDatas.size();i++){
            View view=View.inflate(this,R.layout.item_icon,null);
            item = (RelativeLayout) view.findViewById(R.id.item);
            iv = (ImageView) view.findViewById(R.id.icon);
            tv = (TextView) view.findViewById(R.id.text);
            checkBox = (CheckBox) view.findViewById(R.id.check);

            tv.setText(mDatas.get(i).get("name").toString());
            if ((Boolean) mDatas.get(i).get("isChecked")) {
                item.setBackgroundResource(R.drawable.icon_background);
            } else if (!(Boolean) mDatas.get(i).get("isChecked")) {
                item.setBackgroundResource(R.drawable.icon_background_trans);
            }
            iv.setImageDrawable(item.getResources().getDrawable((int)mDatas.get(i).get("icon")));
            item.setTag(i);
            root.addView(view);
        }
    }
}
