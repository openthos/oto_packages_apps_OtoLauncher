package com.openthos.launcher.openthoslauncher.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.adapter.HomeAdapter;
import com.openthos.launcher.openthoslauncher.adapter.ItemCallBack;
import com.openthos.launcher.openthoslauncher.adapter.RecycleCallBack;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.entity.IconEntity;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.OperateUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.utils.RenameUtils;
import com.openthos.launcher.openthoslauncher.view.FrameSelectView;
import com.openthos.launcher.openthoslauncher.view.CopyInfoDialog;
import com.openthos.launcher.openthoslauncher.view.PropertyDialog;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;

import android.view.KeyEvent;
import android.text.ClipboardManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Launcher implements RecycleCallBack {
    private RecyclerView mRecyclerView;
    public List<IconEntity> mDatas;
    public HomeAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private CustomFileObserver mCustomFileObserver;
    public static Handler mHandler;
    public static boolean mIsCtrlPress;
    public static boolean mIsShiftPress;
    private int mHeightNum;
    private SharedPreferences mSp;
    private int mSumNum;
    private CopyInfoDialog mCopyInfoDialog;
    private IconEntity mBlankIcon = new IconEntity();
    private SdReceiver mSdReceiver;
    private String mCommitText;
    private FrameSelectView mFrameSelectView;
    private float mDownX, mDownY, mMoveX, mMoveY;
    private boolean mIsSelected;
    private ArrayList<IconParams> mPosList;
    private ArrayList<IconEntity> mTempList;
    private static ContentResolver mContentResolver;
    private static Uri mUri;
    public static boolean mIsMove;

    private long mPressTime;
    private Type mPressType;
    private String mPressPath;
    private int mRenamePos;
    private long mLastModified;
    private int mPressX;
    private int mPressY;
    private boolean mIsLongPress;
    private LongPressRunnable mLongPressRunnable = new LongPressRunnable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOtoContentView(R.layout.activity_main);
        mSp = getSharedPreferences(OtoConsts.DESKTOP_DATA, Context.MODE_PRIVATE);
        mDatas = new ArrayList<>();
        mSumNum = getNum();
        mBlankIcon.setName("");
        mBlankIcon.setIsChecked(false);
        mBlankIcon.setIcon(null);
        mBlankIcon.setIsBlank(true);
        mBlankIcon.setPath("");
        mBlankIcon.setType(Type.BLANK);
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
            private long mPreTime = 0;
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case OtoConsts.SORT:
                        mDatas.clear();
                        initDesktop();
                        mAdapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        break;
                    case OtoConsts.DELETE_REFRESH:
                        for (int i = 0; i < mDatas.size(); i++) {
                            if ((mDatas.get(i).getPath()).equals(msg.obj)) {
                                mAdapter.getSelectedPosList().remove(mDatas.get(i));
                                mDatas.set(i, mBlankIcon);
                                break;
                            }
                        }
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        if (System.currentTimeMillis() - mPreTime >= 1000) {
                            mHandler.removeMessages(OtoConsts.ONLY_REFRESH);
                            mHandler.sendMessage(Message.obtain(mHandler, OtoConsts.ONLY_REFRESH));
                            mPreTime = System.currentTimeMillis();
                        } else {
                            mHandler.removeMessages(OtoConsts.ONLY_REFRESH);
                            mHandler.sendMessageDelayed(Message.obtain(mHandler,
                                    OtoConsts.ONLY_REFRESH), 1000);
                        }
                        break;
                    case OtoConsts.NEWFOLDER:
                        createNewFileOrFolder(Type.DIRECTORY, null);
                        break;
                    case OtoConsts.NEWFILE:
                        createNewFileOrFolder(Type.FILE, (String) msg.obj);
                        break;
                    case OtoConsts.SHOW_FILE:
                        File showFile = new File((String) msg.obj);
                        if (!showFile.exists()) {
                            return;
                        }
                        for (int i = 1; i < mDatas.size(); i++) {
                            if ((mDatas.get(i).getPath()).equals(showFile.getAbsolutePath())) {
                                return;
                            }
                        }
                        IconEntity icon = new IconEntity();
                        icon.setName(showFile.getName());
                        icon.setPath(showFile.getAbsolutePath());
                        icon.setIsChecked(false);
                        icon.setIsBlank(false);
                        icon.setLastModified(showFile.lastModified());
                        if (showFile.isDirectory()) {
                            icon.setIcon(MainActivity.this
                                    .getResources().getDrawable(R.drawable.ic_directory));
                            icon.setType(Type.DIRECTORY);
                        } else {
                            icon.setIcon(FileUtils.getFileIcon(
                                    showFile.getAbsolutePath(), MainActivity.this));
                            icon.setType(Type.FILE);
                        }

                        if (mRenamePos != -1 && mLastModified == showFile.lastModified()) {
                            mDatas.set(mRenamePos, icon);
                        } else {
                            for (int i = 1; i < mDatas.size(); i++) {
                                if (mDatas.get(i) == mBlankIcon) {
                                    mDatas.set(i, icon);
                                    break;
                                }
                            }
                        }
                        mRenamePos = -1;
                        mLastModified = 0;
                        mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                        if (System.currentTimeMillis() - mPreTime >= 1000) {
                            mHandler.removeMessages(OtoConsts.ONLY_REFRESH);
                            mHandler.sendMessage(Message.obtain(mHandler, OtoConsts.ONLY_REFRESH));
                            mPreTime = System.currentTimeMillis();
                        } else {
                            mHandler.removeMessages(OtoConsts.ONLY_REFRESH);
                            mHandler.sendMessageDelayed(Message.obtain(mHandler,
                                    OtoConsts.ONLY_REFRESH), 1000);
                        }
                        break;
                    case OtoConsts.RENAME:
                        mAdapter.isRename = true;
                        mAdapter.mIsRenameFirst = true;
                        mAdapter.mRenamePos = mAdapter.getLastClickPos();
                        mAdapter.notifyDataSetChanged();
                        break;
                    case OtoConsts.PROPERTY:
                        PropertyDialog propertyDialog = new PropertyDialog(MainActivity.this,
                                                                          (String) msg.obj);
                        propertyDialog.showDialog();
                        break;
                    case OtoConsts.DELETE:
                        showDialogForMoveToRecycle((String) msg.obj);
                        break;
                    case OtoConsts.SAVEDATA:
                        mSp.edit().putString(OtoConsts.DESKTOP_DATA, dataToString()).commit();
                        break;
                    case OtoConsts.DELETE_DIRECT:
                        showDialogForDirectDelete((String) msg.obj);
                        break;
                    case OtoConsts.COPY_PASTE:
                        new CopyThread((String) msg.obj, false).start();
                        break;
                    case OtoConsts.CROP_PASTE:
                        new CopyThread((String) msg.obj, true).start();
                        break;
                    case OtoConsts.COPY_INFO_SHOW:
                        mCopyInfoDialog.showDialog(R.raw.paste);
                        mCopyInfoDialog.changeTitle(MainActivity.this.getResources()
                                                                .getString(R.string.copy_info));
                        break;
                    case OtoConsts.DELETE_INFO_SHOW:
                        mCopyInfoDialog.showDialog(R.raw.delete);
                        mCopyInfoDialog.changeTitle(MainActivity.this.getResources()
                                                                .getString(R.string.copy_info));
                        break;
                    case OtoConsts.COPY_INFO:
                        mCopyInfoDialog.changeMsg((String) msg.obj);
                        break;
                    case OtoConsts.COPY_INFO_HIDE:
                        mCopyInfoDialog.cancel();
                        break;
                    case OtoConsts.CLEAN_CLIPBOARD:
                        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                                                                               .setText("");
                        break;
                    case OtoConsts.ONLY_REFRESH:
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
        mSdReceiver = new SdReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OtoConsts.ACTION_DESKTOP_SHOW_FILE);
        intentFilter.addAction(OtoConsts.ACTION_DESKTOP_DELETE_FILE);
        intentFilter.addAction(OtoConsts.ACTION_DESKTOP_FOCUSED_STATE);
        intentFilter.addAction(OtoConsts.ACTION_DESKTOP_UNFOCUSED_STATE);
        intentFilter.addAction(OtoConsts.ACTION_DESKTOP_INTERCEPT);
        intentFilter.addAction(OtoConsts.ACTION_DESKTOP_COMMIT_TEXT);
        registerReceiver(mSdReceiver, intentFilter);
        mContentResolver = getContentResolver();
        mUri = Uri.parse("content://com.openthos.filemanager/recycle");
        mCustomFileObserver = new CustomFileObserver(OtoConsts.DESKTOP_PATH);
        mCustomFileObserver.startWatching();
    }

    private class CopyThread extends Thread {
        private String mPath;
        private boolean mIsCut;

        public CopyThread(String path, boolean isCut) {
            super();
            mPath = path;
            mIsCut = isCut;
        }

        @Override
        public void run() {
            super.run();
            if (mIsCut) {
                String[] srcCropPaths = mPath.split(OtoConsts.EXTRA_CROP_FILE_HEADER);
                for (int i = 1; i < srcCropPaths.length; i++) {
                    DiskUtils.moveFile(srcCropPaths[i].replace(OtoConsts.EXTRA_CROP_FILE_HEADER, ""),
                            OtoConsts.DESKTOP_PATH);
                }
            } else {
                String[] srcCopyPaths = mPath.split(OtoConsts.EXTRA_FILE_HEADER);
                for (int i = 1; i < srcCopyPaths.length; i++) {
                    DiskUtils.copyFile(srcCopyPaths[i].replace(OtoConsts.EXTRA_FILE_HEADER, ""),
                            OtoConsts.DESKTOP_PATH);
                }
            }
        }
    }

    private void createNewFileOrFolder(Type type, String suffix) {
        File root = new File(OtoConsts.DESKTOP_PATH);
        for (int j = 1; ; j++) {
            File file = null;
            if (type == Type.FILE) {
                file = new File(root, getResources()
                                                .getString(R.string.new_file) + j + suffix);
            } else if (type == Type.DIRECTORY){
                file = new File(root, getResources().getString(R.string.new_folder) + j);
            }
            if (!file.exists()) {
                if (type == Type.FILE) {
                    copyBaseFile(file, suffix);
                } else if (type == Type.DIRECTORY) {
                    file.mkdir();
                }
                return;
            }
        }
    }

    private void copyBaseFile(File file, String end) {
        try {
            InputStream inputStream = null;
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int count = 0;
            switch(end) {
                case OtoConsts.SUFFIX_TXT:
                    inputStream = getAssets().open("t.txt");
                    break;
                case OtoConsts.SUFFIX_DOC:
                    inputStream = getAssets().open("d.doc");
                    break;
                case OtoConsts.SUFFIX_XLS:
                    inputStream = getAssets().open("x.xls");
                    break;
                case OtoConsts.SUFFIX_PPT:
                    inputStream = getAssets().open("p.ppt");
                    break;
            }
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        initDesktop();
    }

    private void initDesktop() {
        //default icon
        String[] defaultNames = getResources().getStringArray(R.array.default_icon_name);
        TypedArray defaultIcons = getResources().obtainTypedArray(R.array.default_icon);
        String[] defaultPaths = {"", OtoConsts.RECYCLE_PATH};
        File recycle = DiskUtils.getRecycle();
        if (!recycle.exists()) {
            recycle.mkdir();
        }
        Type[] defaultTypes = {Type.COMPUTER, Type.RECYCLE};
        for (int i = 0; i < defaultNames.length; i++) {
            IconEntity icon = new IconEntity();
            icon.setName(defaultNames[i]);
            icon.setPath(defaultPaths[i]);
            icon.setIsChecked(false);
            icon.setIsBlank(false);
            icon.setIcon(getResources().getDrawable(
                                           defaultIcons.getResourceId(i, R.mipmap.ic_launcher)));
            icon.setType(defaultTypes[i]);
            mDatas.add(icon);
        }
        //desktop icon
        List<IconEntity> userDatas = new ArrayList<>();
        File dir = new File(OtoConsts.DESKTOP_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                IconEntity icon = new IconEntity();
                if (files[i].isDirectory()) {
                    icon.setIcon(getResources().getDrawable(R.drawable.ic_directory));
                    icon.setType(Type.DIRECTORY);
                } else {
                    icon.setIcon(FileUtils.getFileIcon(files[i].getAbsolutePath(), this));
                    icon.setType(Type.FILE);
                }
                icon.setName(files[i].getName());
                icon.setPath(files[i].getAbsolutePath());
                icon.setIsChecked(false);
                icon.setIsBlank(false);
                icon.setLastModified(files[i].lastModified());
                if (userDatas.size() < (mSumNum - mDatas.size())) {
                    userDatas.add(icon);
                }
            }
        }
        Collections.sort(userDatas, new Comparator<IconEntity>() {

            @Override
            public int compare(IconEntity object, IconEntity anotherObject) {
                String anotherObjectName = anotherObject.getName();
                String objectName = object.getName();
                return objectName.compareTo(anotherObjectName);
            }
        });
        mDatas.addAll(userDatas);
        while (mDatas.size() < mSumNum) {
            mDatas.add(mBlankIcon);
        }
    }

    private void getHeightNum(DisplayMetrics dm) {
        int heightPixels = dm.heightPixels;
        mHeightNum = heightPixels / getResources().getDimensionPixelSize(R.dimen.icon_size);
    }

    private int getNum() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        getHeightNum(dm);
        int widthPixels = dm.widthPixels;
        int widthNum = widthPixels / getResources().getDimensionPixelSize(R.dimen.icon_size);
        return widthNum * mHeightNum;
    }

    private void init() {
        mPosList = getParams();
        mTempList = new ArrayList<>();
        mFrameSelectView = (FrameSelectView) findViewById(R.id.frame_select_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(mHeightNum,
                                       StaggeredGridLayoutManager.HORIZONTAL));
        mAdapter = new HomeAdapter(mDatas, this);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mIsCtrlPress || mIsShiftPress) {
                            break;
                        }
                        setPressInfo(event.getEventTime(), Type.BLANK, "",
                                     (int) event.getRawX(), (int) event.getRawY());
                        if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                            removeCallbacks();
                            MenuDialog dialog = new MenuDialog(MainActivity.this, Type.BLANK, "");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                        }
                        if (!mAdapter.isClicked && mAdapter.getLastClickPos() != -1) {
                            mDatas.get(mAdapter.getLastClickPos()).setIsChecked(false);
                            mAdapter.setSelectedCurrent(-1);
                            if (mAdapter.isRename) {
                                mAdapter.isRename = false;
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                        if (!mIsSelected && !mAdapter.isClicked
                                        && event.getButtonState() != MotionEvent.BUTTON_SECONDARY) {
                            mDownX = event.getRawX();
                            mDownY = event.getRawY();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (!mIsLongPress) {
                            removeCallbacks();
                        }
                        if ((mIsLongPress || mIsCtrlPress || mIsShiftPress) && !mIsMove) {
                            break;
                        }
                        if (!mIsSelected && !mAdapter.isClicked
                                        && event.getButtonState() != MotionEvent.BUTTON_SECONDARY) {
                            mIsMove = true;
                            mMoveX = event.getRawX();
                            mMoveY = event.getRawY();
                            mFrameSelectView.setPositionCoordinate(mDownX < mMoveX ? mDownX : mMoveX,
                                                                  mDownY < mMoveY ? mDownY : mMoveY,
                                                                  mDownX > mMoveX ? mDownX : mMoveX,
                                                                  mDownY > mMoveY ? mDownY : mMoveY);
                            mFrameSelectView.invalidate();
                            mTempList.clear();
                            for (int i = 0; i < mPosList.size(); i++) {
                                if (frameSelectionJudge(
                                                 mPosList.get(i), mDownX, mDownY, mMoveX, mMoveY)) {
                                    if (!mDatas.get(i).isBlank()){
                                        mTempList.add(mDatas.get(i));
                                        mDatas.get(i).setIsChecked(true);
                                        mDatas.get(i).getView().setSelected(true);
                                    }
                                } else {
                                    if (!mDatas.get(i).isBlank()){
                                        mDatas.get(i).setIsChecked(false);
                                        mDatas.get(i).getView().setSelected(false);
                                    }
                                }
                            }
                            if (!(mAdapter.getSelectedPosList().containsAll(mTempList)
                                     && mAdapter.getSelectedPosList().size() == mTempList.size())) {
                                mAdapter.getSelectedPosList().clear();
                                mAdapter.getSelectedPosList().addAll(mTempList);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!mIsSelected && !mAdapter.isClicked
                                        && event.getButtonState() != MotionEvent.BUTTON_SECONDARY) {
                            mFrameSelectView.setPositionCoordinate(-1, -1, -1, -1);
                            mFrameSelectView.invalidate();
                            mTempList.clear();
                        }
                        if (mPressTime == 0 || mIsMove
                            || event.getEventTime() - mPressTime < OtoConsts.DOUBLE_CLICK_TIME) {
                            removeCallbacks();
                        }
                        mPressTime = 0;
                        mIsMove = false;
                        mIsSelected = false;
                        break;
                }
                mAdapter.isClicked = false;
                return false;
            }
        });
        mItemTouchHelper = new ItemTouchHelper(new ItemCallBack(this));
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mCopyInfoDialog = CopyInfoDialog.getInstance(this);
    }

    @Override
    public void itemOnClick(int position, View view) {
    }

    @Override
    public void onMove(int from, int to) {
        if (!mDatas.get(from).isBlank()) {
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
                    mAdapter.notifyItemMoved(from, to);
                    mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mIsCtrlPress = event.isCtrlPressed();
        mIsShiftPress = event.isShiftPressed();
        if (!mAdapter.isRename) {
            return keyDealing(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean keyDealing(int keyCode, KeyEvent event) {
        if (event.isCtrlPressed() && !mAdapter.isRename) {
            if (keyCode == KeyEvent.KEYCODE_A) {
                mAdapter.setSelectedCurrent(-1);
                for (int i = 0; i < mDatas.size(); i++) {
                    if (mDatas.get(i).isBlank() == false) {
                        IconEntity icon = mDatas.get(i);
                        icon.setIsChecked(true);
                        mAdapter.getSelectedPosList().add(icon);
                    }
                }
                mAdapter.notifyDataSetChanged();
            } else if (keyCode == KeyEvent.KEYCODE_D && mAdapter.getSelectedPosList() != null) {
                String deletePath = getSelectedPath(OtoConsts.DELETE);
                if (deletePath != null) {
                    Message deleteFile = new Message();
                    deleteFile.obj = deletePath;
                    deleteFile.what = OtoConsts.DELETE;
                    mHandler.sendMessage(deleteFile);
                }
            } else if (keyCode == KeyEvent.KEYCODE_X && mAdapter.getSelectedPosList() != null) {
                String cropPath = getSelectedPath(OtoConsts.CROP_PASTE);
                if (cropPath != null) {
                    ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                            .setText(cropPath);
                }
            } else if (keyCode == KeyEvent.KEYCODE_C && mAdapter.getSelectedPosList() != null) {
                String copyPath = getSelectedPath(OtoConsts.COPY_PASTE);
                if (copyPath != null) {
                    ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                            .setText(copyPath);
                }
            } else if (keyCode == KeyEvent.KEYCODE_V) {
                String sourcePath = "";
                try {
                    sourcePath = (String)
                         ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getText();
                } catch (ClassCastException e) {
                    sourcePath = "";
                }
                if (sourcePath == null) {
                    sourcePath = "";
                }
                if (!(sourcePath.startsWith(OtoConsts.EXTRA_FILE_HEADER)
                                   || sourcePath.startsWith(OtoConsts.EXTRA_CROP_FILE_HEADER))) {
                   return true;
                }
                Message paste = new Message();
                if (sourcePath.startsWith(OtoConsts.EXTRA_FILE_HEADER)) {
                    paste.what = OtoConsts.COPY_PASTE;
                } else if (sourcePath.startsWith(OtoConsts.EXTRA_CROP_FILE_HEADER)) {
                    paste.what = OtoConsts.CROP_PASTE;
                }
                paste.obj = sourcePath;
                mHandler.sendMessage(paste);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_FORWARD_DEL
                       && mAdapter.getSelectedPosList() != null && !mAdapter.isRename) {
            String deletePath = getSelectedPath(OtoConsts.DELETE);
            if (deletePath != null) {
                Message deleteFile = new Message();
                deleteFile.obj = deletePath;
                if (event.isShiftPressed()) {
                    deleteFile.what = OtoConsts.DELETE_DIRECT;
                } else {
                    deleteFile.what = OtoConsts.DELETE;
                }
                mHandler.sendMessage(deleteFile);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_F5) {
            mHandler.sendEmptyMessage(OtoConsts.SORT);
        } else if (keyCode == KeyEvent.KEYCODE_F2 && mAdapter.getLastClickPos() != -1) {
            mAdapter.setSelectedCurrent(mAdapter.getLastClickPos());
            Type type = mDatas.get(mAdapter.getLastClickPos()).getType();
            if (type == Type.DIRECTORY || type == Type.FILE) {
                mHandler.sendEmptyMessage(OtoConsts.RENAME);
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP && !mAdapter.isRename) {
            if (mAdapter.getSelectedPosList().size() == 0) {
                selectFirstIcon();
            } else {
                for (int i = mAdapter.getLastClickPos() - 1; i >= 0; i--) {
                    if (!mDatas.get(i).isBlank()) {
                        mAdapter.setSelectedCurrent(i);
                        break;
                    }
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !mAdapter.isRename) {
            if (mAdapter.getSelectedPosList().size() == 0) {
                selectFirstIcon();
            } else {
                if (mAdapter.getLastClickPos() == mAdapter.getItemCount()) {
                    mAdapter.setSelectedCurrent(mAdapter.getItemCount());
                } else {
                    for (int i = mAdapter.getLastClickPos() + 1; i < mAdapter.getItemCount(); i++) {
                        if (!mDatas.get(i).isBlank()) {
                            mAdapter.setSelectedCurrent(i);
                            break;
                        }
                    }
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && !mAdapter.isRename) {
            if (mAdapter.getSelectedPosList().size() == 0) {
                selectFirstIcon();
            } else {
                int currentPostion = mAdapter.getLastClickPos();
                int currentRow = currentPostion / mHeightNum + 1;
                if (currentPostion - mHeightNum >= 0) {
                    EXIT:
                    for (int i = currentRow - 1; i >= 0; i--) {
                        currentPostion = currentPostion - mHeightNum;
                        for (int j = currentPostion; j >= mHeightNum * (i - 1); j--) {
                            if (!mDatas.get(j).isBlank()) {
                                mAdapter.setSelectedCurrent(j);
                                break EXIT;
                            }
                        }
                        for (int j = currentPostion; j <= mHeightNum * i - 1; j++) {
                            if (!mDatas.get(j).isBlank()) {
                                mAdapter.setSelectedCurrent(j);
                                break EXIT;
                            }
                        }
                    }
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && !mAdapter.isRename) {
            if (mAdapter.getSelectedPosList().size() == 0) {
                selectFirstIcon();
            } else {
                int currentPostion = mAdapter.getLastClickPos();
                int currentRow = currentPostion / mHeightNum + 1;
                if (currentPostion + mHeightNum <= mAdapter.getItemCount()) {
                    EXIT:
                    for (int i = currentRow + 1; i <= mSumNum / mHeightNum; i++) {
                        currentPostion = currentPostion + mHeightNum;
                        for (int j = currentPostion; j >= mHeightNum * (i - 1); j--) {
                            if (!mDatas.get(j).isBlank()) {
                                mAdapter.setSelectedCurrent(j);
                                break EXIT;
                            }
                        }
                        for (int j = currentPostion; j <= mHeightNum * i - 1; j++) {
                            if (!mDatas.get(j).isBlank()) {
                                mAdapter.setSelectedCurrent(j);
                                break EXIT;
                            }
                        }
                    }
                }
            }
        } else if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                        && mAdapter.getLastClickPos() != -1 && !mAdapter.isRename) {
            OperateUtils.enter(this, mDatas.get(mAdapter.getLastClickPos()).getPath(),
                                     mDatas.get(mAdapter.getLastClickPos()).getType());
        } else {
            String textEnglish = RenameUtils.switchKeyCodeToString(event, keyCode);
            if (mAdapter.isRename && textEnglish != null) {
                mAdapter.notifyText(textEnglish);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void selectFirstIcon() {
        for (int i = 0; i < mDatas.size(); i++) {
            if (!mDatas.get(i).isBlank()) {
                mAdapter.setSelectedCurrent(i);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private String getSelectedPath(int copyType) {
        StringBuffer buff = new StringBuffer();
        if (mAdapter.getSelectedPosList() != null && mAdapter.getSelectedPosList().size() > 0) {
            for (int i = 0; i < mAdapter.getSelectedPosList().size(); i++) {
                Type type = mAdapter.getSelectedPosList().get(i).getType();
                if (type == Type.DIRECTORY || type == Type.FILE) {
                    switch (copyType) {
                        case OtoConsts.COPY_PASTE:
                            buff.append(OtoConsts.EXTRA_FILE_HEADER
                                    + mAdapter.getSelectedPosList().get(i).getPath());
                            break;
                        case OtoConsts.CROP_PASTE:
                            buff.append(OtoConsts.EXTRA_CROP_FILE_HEADER
                                    + mAdapter.getSelectedPosList().get(i).getPath());
                            break;
                        case OtoConsts.DELETE:
                            buff.append(OtoConsts.EXTRA_DELETE_FILE_HEADER
                                    + mAdapter.getSelectedPosList().get(i).getPath());
                            break;
                    }
                }
            }
            return buff.toString();
        }
        return null;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mIsCtrlPress = event.isCtrlPressed();
        mIsShiftPress = event.isShiftPressed();
        return super.onKeyUp(keyCode, event);
    }

    private void showDialogForMoveToRecycle(String path) {
        MoveToRecycleClickListener listener = new MoveToRecycleClickListener(path);
        OperateUtils.showBaseAlertDialog(this, R.string.dialog_delete_text, listener);
    }

    private void showDialogForDirectDelete(String path) {
        DirectDeleteClickListener listener = new DirectDeleteClickListener(path);
        OperateUtils.showBaseAlertDialog(this, R.string.dialog_direct_delete_text, listener);
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
                String[] split = mPath.split(OtoConsts.EXTRA_DELETE_FILE_HEADER);
                for (int i = 1; i < split.length; i++) {
                    DiskUtils.moveFile(split[i], OtoConsts.RECYCLE_PATH);
                    Message deleteRefreshFile = new Message();
                    deleteRefreshFile.obj = split[i];
                    deleteRefreshFile.what = OtoConsts.DELETE_REFRESH;
                    MainActivity.mHandler.sendMessage(deleteRefreshFile);
                }
            }
        }
    }

    private class DirectDeleteClickListener implements OnClickListener {
        String mPath;

        public DirectDeleteClickListener(String path) {
            mPath = path;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            DirectDeleteThread thread = new DirectDeleteThread();
            thread.start();
            dialog.cancel();
        }

        private class DirectDeleteThread extends Thread {

            public DirectDeleteThread() {
                super();
            }

            @Override
            public void run() {
                super.run();
                String[] split = mPath.split(OtoConsts.EXTRA_DELETE_FILE_HEADER);
                for (int i = 1; i < split.length; i++) {
                    DiskUtils.delete(new File(split[i]));
                    Message deleteRefreshFile = new Message();
                    deleteRefreshFile.obj = split[i];
                    deleteRefreshFile.what = OtoConsts.DELETE_REFRESH;
                    MainActivity.mHandler.sendMessage(deleteRefreshFile);
                }
            }
        }
    }

    private String dataToString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        boolean first = true;
        for (IconEntity icon : mDatas) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append("{\"type\":\"");
            sb.append(icon.getType());
            sb.append("\",\"name\":\"");
            sb.append(icon.getName());
            sb.append("\",\"path\":\"");
            sb.append(icon.getPath());
            sb.append("\",\"lastModified\":\"");
            sb.append(icon.getLastModified());
            sb.append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    private ArrayList<IconEntity> stringToData(String s) throws JSONException {
        JSONArray array = new JSONArray(s);
        ArrayList<IconEntity> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            if (list.size() == mSumNum) {
                return list;
            }
            JSONObject obj = array.getJSONObject(i);
            Type type = Type.valueOf(obj.getString("type"));
            if (type == Type.BLANK) {
                list.add(mBlankIcon);
                continue;
            }
            IconEntity icon = new IconEntity();
            switch (type) {
                case COMPUTER:
                    icon.setIsBlank(false);
                    icon.setIcon(getResources().getDrawable(R.drawable.ic_app_computer));
                    icon.setName(getResources().getString(R.string.my_computer));
                    break;
                case RECYCLE:
                    icon.setIsBlank(false);
                    icon.setIcon(getResources().getDrawable(R.drawable.ic_app_recycle));
                    icon.setName(getResources().getString(R.string.recycle));
                    break;
                case FILE:
                    icon.setIsBlank(false);
                    icon.setIcon(FileUtils.getFileIcon(obj.getString("path"), this));
                    icon.setName(obj.getString("name"));
                    break;
                case DIRECTORY:
                    icon.setIsBlank(false);
                    icon.setIcon(getResources().getDrawable(R.drawable.ic_directory));
                    icon.setName(obj.getString("name"));
                    break;
            }
            icon.setIsChecked(false);
            icon.setPath(obj.getString("path"));
            icon.setType(type);
            icon.setLastModified(obj.getLong("lastModified"));
            list.add(icon);
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
        unregisterReceiver(mSdReceiver);
        if (mCustomFileObserver != null) {
            mCustomFileObserver.stopWatching();
            mCustomFileObserver = null;
        }
        super.onDestroy();
    }

    private class SdReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra(OtoConsts.EXTRA_DESKTOP_PATH_TAG);
            switch (intent.getAction()) {
                //case Intent.ACTION_DESKTOP_FOCUSED_STATE:
                //    Settings.Secure.putString(MainActivity.this.getContentResolver(),
                //                              Settings.Secure.DEFAULT_INPUT_METHOD,
                //                              OtoConsts.DESKTOP_INPUT);
                //    break;
                case OtoConsts.ACTION_DESKTOP_INTERCEPT:
                    mIsCtrlPress = intent.getBooleanExtra(OtoConsts.CTRL_ISPRESSED_INFO, false);
                    mIsShiftPress = intent.getBooleanExtra(OtoConsts.SHIFT_ISPRESSED_INFO, false);
                    Bundle bundle = intent.getParcelableExtra(OtoConsts.BUNDLE_KEYEVENT);
                    if (bundle != null && bundle.getBoolean(OtoConsts.KEY_ACTION_DOWN)) {
                        keyDealing((bundle.getInt(OtoConsts.KEY_CODE)),
                                              (KeyEvent) bundle.getParcelable(OtoConsts.KEY_EVENT));
                    }
                    break;
                case OtoConsts.ACTION_DESKTOP_COMMIT_TEXT:
                    mCommitText = intent.getStringExtra(OtoConsts.EXTRA_DESKTOP_RESULTTEXT);
                    if (mAdapter.isRename) {
                        mAdapter.notifyText(mCommitText);
                    } else if (RenameUtils.ENTER.equals(mCommitText)
                            && mAdapter.getLastClickPos() != -1) {
                         OperateUtils.enter(MainActivity.this,
                                            mDatas.get(mAdapter.getLastClickPos()).getPath(),
                                            mDatas.get(mAdapter.getLastClickPos()).getType());
                    }
                case OtoConsts.ACTION_DESKTOP_UNFOCUSED_STATE:
                    mIsCtrlPress = false;
                    mIsShiftPress = false;
                    break;
            }
        }
    }

    public void setIsSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    class IconParams {
        public int mLeft;
        public int mRight;
        public int mTop;
        public int mBottom;

        public IconParams(int left, int top, int right, int bottom) {
            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;
        }
    }

    class CustomFileObserver extends FileObserver {
        public CustomFileObserver(String path) {
            super(path);
        }

        @Override
        public void onEvent(int event, String path) {
            final int action = event & FileObserver.ALL_EVENTS;
            if (path != null) {
                final String filePath = OtoConsts.DESKTOP_PATH + "/" + path;
                switch (action) {
                    case FileObserver.CREATE:
                    case FileObserver.MOVED_TO:
                        if (FileUtils.isApk(filePath)) {
                            new Timer().schedule(new TimerTask(){
                                long fileLength = -1;
                                File file = new File(filePath);
                                @Override
                                public void run(){
                                    if (file.length() > fileLength) {
                                        fileLength = file.length();
                                     } else {
                                         MainActivity.mHandler.sendMessage(
                                                      Message.obtain(MainActivity.mHandler,
                                                                OtoConsts.SHOW_FILE,filePath));
                                         cancel();
                                     }
                                }
                            }, 0, OtoConsts.DELAY_REFRESH_TIME);
                        } else {
                            MainActivity.mHandler.sendMessage(Message.obtain(MainActivity.mHandler,
                                    OtoConsts.SHOW_FILE, filePath));
                        }
                        break;
                    case FileObserver.MOVED_FROM:
                        for (int i = 1; i < mDatas.size(); i++) {
                            if ((mDatas.get(i).getPath()).equals(filePath)) {
                                mRenamePos = i;
                                mLastModified = mDatas.get(i).getLastModified();
                                break;
                            }
                        }
                    case FileObserver.DELETE:
                        MainActivity.mHandler.sendMessage(Message.obtain(MainActivity.mHandler,
                                OtoConsts.DELETE_REFRESH, filePath));
                        break;
                    case FileObserver.MODIFY:
                        break;
                    case FileObserver.ATTRIB:
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private ArrayList<IconParams> getParams() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int iconHeight = dm.heightPixels / mHeightNum;
        int spaceH = (int) ((iconHeight
                - getResources().getDimensionPixelSize(R.dimen.icon_size)) / 2 + 0.5);
        int spaceW = (int) ((getResources().getDimensionPixelSize(R.dimen.icon_size)
                - getResources().getDimensionPixelSize(R.dimen.icon_shadow_size)) / 2 + 0.5);
        int height = getResources().getDimensionPixelSize(R.dimen.icon_size);
        int width = getResources().getDimensionPixelSize(R.dimen.icon_shadow_size);
        int left, right, top, bottom;
        ArrayList<IconParams> list = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            left = spaceW + (i / mHeightNum) * (2 * spaceW + width);
            right = spaceW + width + (i / mHeightNum) * (2 * spaceW + width);
            top = spaceH + (i % mHeightNum) * (2 * spaceH + height);
            bottom = spaceH + height + (i % mHeightNum) * (2 * spaceH + height);
            list.add(new IconParams(left, top, right, bottom));
        }
        return list;
    }

    private boolean frameSelectionJudge(IconParams icon, float downX, float downY,
                                                                          float toX, float toY) {
        return (((icon.mLeft >= Math.min(downX, toX) && icon.mLeft <= Math.max(downX, toX))
              || (icon.mRight >= Math.min(downX, toX) && icon.mRight <= Math.max(downX, toX)))
              && ((icon.mTop >= Math.min(downY, toY) && icon.mTop <= Math.max(downY, toY))
              || (icon.mBottom >= Math.min(downY, toY) && icon.mBottom <= Math.max(downY, toY))))
              || (((icon.mLeft <= Math.min(downX, toX) && icon.mRight >= Math.max(downX, toX))
              && ((icon.mTop >= Math.min(downY, toY) && icon.mTop <= Math.max(downY, toY))
              || (icon.mBottom >= Math.min(downY, toY) && icon.mBottom <= Math.max(downY, toY))))
              || ((icon.mTop <= Math.min(downY, toY) && icon.mBottom >= Math.max(downY, toY))
              && ((icon.mLeft >= Math.min(downX, toX) && icon.mLeft <= Math.max(downX, toX))
              || (icon.mRight >= Math.min(downX, toX) && icon.mRight <= Math.max(downX, toX)))));
    }

    public void setLocation(float x, float y) {
        mDownX = x;
        mDownY = y;
    }

    public static ContentResolver getResolver() {
        return mContentResolver;
    }

    public static Uri getUri() {
        return mUri;
    }

    public static void setState(boolean isCtrlPress, boolean isShiftPress) {
        mIsCtrlPress = isCtrlPress;
        mIsShiftPress = isShiftPress;
    }

    private class LongPressRunnable implements Runnable {

        @Override
        public void run() {
            MenuDialog dialog = new MenuDialog(MainActivity.this, mPressType, mPressPath);
            dialog.showDialog(mPressX, mPressY);
            mIsLongPress = true;
        }
    }

    public void setPressInfo(long time, Type type, String path, int x, int y) {
        mPressTime = time;
        mPressType = type;
        mPressPath = path;
        mPressX = x;
        mPressY = y;
        mIsLongPress = false;
        mHandler.postDelayed(mLongPressRunnable, OtoConsts.DOUBLE_CLICK_TIME);
    }

    public void removeCallbacks() {
        mHandler.removeCallbacks(mLongPressRunnable);
    }
}
