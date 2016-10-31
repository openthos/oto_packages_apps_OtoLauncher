package com.openthos.launcher.openthoslauncher.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.adapter.HomeAdapter;
import com.openthos.launcher.openthoslauncher.adapter.ItemCallBack;
import com.openthos.launcher.openthoslauncher.adapter.RecycleCallBack;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.view.PropertyDialog;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;
import android.view.KeyEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Launcher implements RecycleCallBack {
    private RecyclerView mRecyclerView;
    public List<HashMap<String, Object>> mDatas;
    public HomeAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    public static Handler mHandler;
    private int mHeightNum;
    private boolean mIsClicked = false;
    private boolean mIsRename = false;
    private SharedPreferences mSp;
    private int mSumNum;
    private HashMap<String, Object> mBlankMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOtoContentView(R.layout.activity_main);
        mSp = getSharedPreferences(OtoConsts.DESKTOP_DATA, Context.MODE_PRIVATE);
        mDatas = new ArrayList<>();
        mSumNum = getNum();
        mBlankMap.put("name", "");
        mBlankMap.put("isChecked", false);
        mBlankMap.put("icon", -1);
        mBlankMap.put("null", true);
        mBlankMap.put("path", "");
        mBlankMap.put("type", Type.blank);
        if (savedInstanceState != null) {
            try {
                mDatas = stringToData(savedInstanceState.getString(OtoConsts.DESKTOP_DATA));
            } catch (JSONException e) {
                initData();
            }
        } else {
            initData();
        }
        init();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case OtoConsts.SORT:
                        mDatas.clear();
                        initData();
                        mAdapter.setData(mDatas);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.DELETE_REFRESH:
                        inner:
                        for (int i = 0; i < mDatas.size(); i++) {
                            if ((mDatas.get(i).get("path")).equals(msg.obj)) {
                                mDatas.set(i, mBlankMap);
                                break inner;
                            }
                        }
                        mAdapter.setData(mDatas);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.NEWFOLDER:
                        inner:
                        for (int i = 0; i < mDatas.size(); i++) {
                            if ((mDatas.get(i).get("path")).equals("")) {
                                File root = new File(OtoConsts.DESKTOP_PATH);
                                for (int j = 1; ; j++) {
                                    File file = new File(root,
                                                        MainActivity.this.getResources()
                                                        .getString(R.string.new_folder) + j);
                                    if (!file.exists()) {
                                        file.mkdir();
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("name", file.getName());
                                        map.put("path", file.getAbsolutePath());
                                        map.put("isChecked", false);
                                        map.put("null", false);
                                        map.put("icon", R.drawable.ic_app_file);
                                        map.put("type", Type.directory);
                                        mDatas.set(i, map);
                                        break inner;
                                    }
                                }
                            }
                        }
                        mAdapter.setData(mDatas);
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.RENAME:
                        mAdapter.isRename = true;
                        mAdapter.notifyDataSetChanged();
                        break;
                    case OtoConsts.PROPERTY:
                        PropertyDialog dialog = new PropertyDialog(MainActivity.this,
                                                                   (String) msg.obj);
                        dialog.showDialog();
                        break;
                    case OtoConsts.DELETE:
                        showDialogForMoveToRecycle((String) msg.obj);
                        break;
                    case OtoConsts.SAVEDATA:
                        mSp.edit().putString(OtoConsts.DESKTOP_DATA, dataToString()).commit();
                        break;
                }
            }
        };
    }

    private void initData() {
        String tempData = mSp.getString(OtoConsts.DESKTOP_DATA, "");
        if (!TextUtils.isEmpty(tempData)) {
            try {
                mDatas = stringToData(tempData);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String[] defaultName = getResources().getStringArray(R.array.default_icon_name);
        TypedArray defaultIcon = getResources().obtainTypedArray(R.array.default_icon);
        String[] paths = {"/", OtoConsts.RECYCLE_PATH};
        new Thread() {
            @Override
            public void run() {
                super.run();
                File recycle = DiskUtils.getRecycle();
                if (!recycle.exists()) {
                    recycle.mkdir();
                }
            }
        }.start();
        Type[] defaultType = {Type.computer, Type.recycle};
        for (int i = 0; i < defaultName.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", defaultName[i]);
            map.put("path", paths[i]);
            map.put("isChecked", false);
            map.put("null", false);
            map.put("icon", defaultIcon.getResourceId(i, R.mipmap.ic_launcher));
            map.put("type", defaultType[i]);
            mDatas.add(map);
        }
        initDesktop();
    }

    private void initDesktop() {
        List<HashMap<String, Object>> userDatas = new ArrayList<>();
        File dir = new File(OtoConsts.DESKTOP_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] files = dir.listFiles();
        if(files != null){
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

            if (userDatas.size() < (mSumNum - mDatas.size())) {
                userDatas.add(map);
            }
        }}

        Collections.sort(userDatas, new Comparator<HashMap<String, Object>>() {

            @Override
            public int compare(HashMap<String, Object> object,
                               HashMap<String, Object> anotherObject) {
                String anotherObjectName = (String) anotherObject.get("name");
                String objectName = (String) object.get("name");
                return objectName.compareTo(anotherObjectName);
            }
        });
        mDatas.addAll(userDatas);

        while (mDatas.size() < mSumNum) {
            mDatas.add(mBlankMap);
        }
    }

    private void getHeightNum(DisplayMetrics dm) {
        int heightPixels = dm.heightPixels;
        mHeightNum = heightPixels / getResources().getDimensionPixelSize(R.dimen.icon_size);
    }

    private int getNum() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getHeightNum (dm);
        int widthPixels = dm.widthPixels;
        int widthNum = widthPixels / getResources().getDimensionPixelSize(R.dimen.icon_size);
        return widthNum * mHeightNum;
    }

    private void init() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (!MenuDialog.isExistMenu()) {
                            MenuDialog dialog = MenuDialog.getInstance(MainActivity.this,
                                                                       Type.blank, "/");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                        } else {
                            MenuDialog.setExistMenu(false);
                        }
                    }
                    if (!mIsClicked && mAdapter.pos != -1) {
                        mDatas.get(mAdapter.pos).put("isChecked", false);
                        mAdapter.setData(mDatas);
                        mAdapter.pos = -1;
                        mAdapter.notifyDataSetChanged();
                    }
                    mIsClicked = false;
                }
                return false;
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(mHeightNum,
                                       StaggeredGridLayoutManager.HORIZONTAL));
        mAdapter = new HomeAdapter(mDatas, this);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (!MenuDialog.isExistMenu()) {
                            MenuDialog dialog = MenuDialog.getInstance(MainActivity.this,
                                                                       Type.blank, "/");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                        } else {
                            MenuDialog.setExistMenu(false);
                        }
                    }
                    if (!mIsClicked && mAdapter.pos != -1) {
                        mDatas.get(mAdapter.pos).put("isChecked", false);
                        mAdapter.pos = -1;
                        mAdapter.notifyDataSetChanged();
                    }
                    mIsClicked = false;
                    if (mIsRename) {
                        mIsRename = false;
                        mAdapter.notifyDataSetChanged();
                    }
                }
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
        if (!(Boolean) mDatas.get(from).get("null")) {
            if (to > 0 && from > 0) {
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
                    mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.isCtrlPressed()) {
            return true;
        } else if (!event.isShiftPressed() && keyCode == KeyEvent.KEYCODE_FORWARD_DEL) {
            Type type = (Type) (mDatas.get(mAdapter.pos).get("type"));
            if (type == Type.directory || type == Type.file) {
                    Message deleteFile = new Message();
                    deleteFile.obj = mDatas.get(mAdapter.pos).get("path");
                    deleteFile.what = OtoConsts.DELETE;
                    mHandler.sendMessage(deleteFile);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialogForMoveToRecycle(String path) {
        MoveToRecycleClickListener listener = new MoveToRecycleClickListener(path);
        new AlertDialog.Builder(MainActivity.this)
             .setMessage(getResources().getString(R.string.dialog_delete_text))
             .setPositiveButton(getResources().getString(R.string.dialog_delete_yes), listener)
             .setNegativeButton(getResources().getString(
                                                  R.string.dialog_delete_no),
                 new android.content.DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.cancel();
                     }
                 }).show();
    }

    private class MoveToRecycleClickListener implements OnClickListener {
        String mPath;

        public MoveToRecycleClickListener(String path) {
            mPath = path;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            MoveToRecycleThread thread = new MoveToRecycleThread();
            thread.start();
            dialog.cancel();
        }

        private class MoveToRecycleThread extends Thread {

            public MoveToRecycleThread() {
               super();
            }

            @Override
            public void run() {
                super.run();
                DiskUtils.moveFile(mPath, OtoConsts.RECYCLE_PATH);
                Message deleteFile = new Message();
                deleteFile.obj = mPath;
                deleteFile.what = OtoConsts.DELETE_REFRESH;
                MainActivity.mHandler.sendMessage(deleteFile);
            }
        }
    }

    private String dataToString () {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        boolean first = true;
        for (HashMap map : mDatas) {
            if (!first){
                sb.append(",");
            } else {
                first = false;
            }
            sb.append("{\"type\":\"");
            sb.append(map.get("type"));
            sb.append("\",\"name\":\"");
            sb.append(map.get("name"));
            sb.append("\",\"path\":\"");
            sb.append(map.get("path"));
            sb.append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    private ArrayList<HashMap<String, Object>> stringToData(String s) throws JSONException {
        JSONArray array = new JSONArray(s);
        ArrayList<HashMap<String, Object>> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Type type = Type.valueOf(obj.getString("type"));
            if (type == Type.blank) {
                list.add(mBlankMap);
                continue;
            }
            HashMap<String, Object> map = new HashMap<>();
            switch (type){
                case computer:
                    map.put("null", false);
                    map.put("icon", R.drawable.ic_app_computer);
                    break;
                case recycle:
                    map.put("null", false);
                    map.put("icon", R.drawable.ic_app_recycle);
                    break;
                case file:
                    map.put("null", false);
                    map.put("icon", R.drawable.ic_app_text);
                    break;
                case directory:
                    map.put("null", false);
                    map.put("icon", R.drawable.ic_app_file);
                    break;
                case blank:
                    map.put("null", true);
                    map.put("icon", -1);
                    break;
            }
            map.put("isChecked", false);
            map.put("name", obj.getString("name"));
            map.put("path", obj.getString("path"));
            map.put("type", type);
            list.add(map);
        }
        return list;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(OtoConsts.DESKTOP_DATA, dataToString());
    }

    @Override
    public void onDestroy() {
        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
        super.onDestroy();
    }
}
