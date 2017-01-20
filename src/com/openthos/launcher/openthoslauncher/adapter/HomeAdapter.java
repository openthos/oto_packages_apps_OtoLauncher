package com.openthos.launcher.openthoslauncher.adapter;

import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.EditText;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.Window;

import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.utils.OperateUtils;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;
import com.openthos.launcher.openthoslauncher.view.OpenWithDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by xu on 2016/8/8.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private List<HashMap<String, Object>> data;
    private List<Integer> selectData;
    private RecycleCallBack mRecycleClick;

    public int pos = -1;
    private int mLastClickId = -1;
    private long mLastClickTime = 0;
    private boolean isClicked = false;
    public boolean isRename = false;

    private static final int LESS = 0;
    private static final int MORE = 1;

    public HomeAdapter(List<HashMap<String, Object>> data, RecycleCallBack click) {
        this.data = data;
        this.mRecycleClick = click;
        selectData = new ArrayList<>();
    }

    public void setData(List<HashMap<String, Object>> data) {
        this.data = data;
    }


    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomeViewHolder holder = new HomeViewHolder(LayoutInflater.from(parent.getContext())
                                       .inflate(R.layout.item_icon, parent, false), mRecycleClick);
        return holder;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.tv.setText(data.get(position).get("name").toString());
        if ((Boolean) data.get(position).get("isChecked")) {
            holder.item.setBackgroundResource(R.drawable.icon_background);
        } else if (!(Boolean) data.get(position).get("isChecked")) {
            holder.item.setBackgroundResource(R.drawable.icon_background_trans);
        }
        if ((Boolean) data.get(position).get("null")) {
            holder.nullnull.setChecked(true);
        } else if (!(Boolean) data.get(position).get("null")) {
            holder.nullnull.setChecked(false);
        }
        if ((Drawable) data.get(position).get("icon") != null) {
            holder.iv.setImageDrawable((Drawable) data.get(position).get("icon"));
        } else {
            holder.iv.setImageDrawable(new ColorDrawable(0));
        }
        if (isRename == true && position == pos) {
            holder.tv.setFocusable(true);
            holder.tv.setFocusableInTouchMode(true);
            holder.tv.requestFocus();
        } else {
            holder.tv.setFocusable(false);
            holder.tv.clearFocus();
        }
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        RelativeLayout item;
        ImageView iv;
        EditText tv;
        CheckBox checkBox;
        CheckBox nullnull;
        private RecycleCallBack mClick;

        public HomeViewHolder(View view, RecycleCallBack click) {
            super(view);
            item = (RelativeLayout) view.findViewById(R.id.item);
            iv = (ImageView) view.findViewById(R.id.icon);
            tv = (EditText) view.findViewById(R.id.texts);
            tv.setClickable(true);
            tv.setFocusable(true);
            tv.setOnKeyListener(keyListener);
            tv.setOnFocusChangeListener(focusChangeListener);
            tv.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isRename == false) {
                        ctrlProcess(v,event);
                    }
                    return false;
                }
            });
            checkBox = (CheckBox) view.findViewById(R.id.check);
            nullnull = (CheckBox) view.findViewById(R.id.nullnull);
            item.setOnTouchListener(this);
            this.mClick = click;
            itemView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (MenuDialog.isExistMenu() == false) {
                            MenuDialog dialog = new MenuDialog(item.getContext(), Type.BLANK, "");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                            MenuDialog.setExistMenu(true);
                        } else {
                            MenuDialog.setExistMenu(false);
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (isClicked != true && pos != -1) {
                            selectCurrent(-1);
                            selectData.clear();
                            pos = -1;
                            notifyDataSetChanged();
                        }
                        isClicked = false;
                        if (isRename == true) {
                            isRename = false;
                            notifyDataSetChanged();
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ctrlProcess(v, event);
            return true;
        }

        private void ctrlProcess(View v, MotionEvent event) {
            if (getAdapterPosition() != -1) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (selectData != null) {
                            switch (selectData.size()) {
                                case 0:
                                case 1:
                                    selectLess(event);
                                    break;
                                default:
                                    selectMore(event);
                                    break;
                            }
                        }
                    } else if (!(Boolean) data.get(getAdapterPosition()).get("null")) {
                        if (MainActivity.mIsCtrlPress) {
                            Type type = (Type) data.get(getAdapterPosition()).get("type");
                            if (type == Type.COMPUTER || type == Type.RECYCLE) {
                                boolean isChecked = (boolean) data.get(getAdapterPosition())
                                                                                 .get("isChecked");
                                data.get(getAdapterPosition()).put("isChecked", !isChecked);
                            } else {
                                boolean isSelect = false;
                                for (int i = 0; i < selectData.size(); i++) {
                                    if (selectData.get(i) == getAdapterPosition()) {
                                        isSelect = true;
                                        data.get(selectData.get(i)).put("isChecked", false);
                                        pos = -1;
                                        selectData.remove(i);
                                        break;
                                    }
                                }
                                if (!isSelect) {
                                    pos = getAdapterPosition();
                                    addSelectData(getAdapterPosition());
                                    data.get(getAdapterPosition()).put("isChecked", true);
                                }
                            }
                        } else {
                            if (event.getButtonState() != MotionEvent.BUTTON_SECONDARY
                                    && Math.abs(System.currentTimeMillis()
                                    - mLastClickTime) < OtoConsts.DOUBLE_CLICK_TIME
                                    && pos == getAdapterPosition()) {
                                OperateUtils.enter(item.getContext(),
                                                (String) data.get(getAdapterPosition()).get("path"),
                                                (Type) data.get(getAdapterPosition()).get("type"));
                            } else {
                                selectCurrent(getAdapterPosition());
                                selectData.clear();
                                addSelectData(getAdapterPosition());
                                pos = getAdapterPosition();
                            }
                        }
                        mLastClickTime = System.currentTimeMillis();
                    } else {
                        pos = -1;
                        selectData.clear();
                        selectCurrent(-1);
                    }
                    notifyDataSetChanged();
                }
            }
        }

        private void selectLess(MotionEvent event) {
            if ((boolean) data.get(getAdapterPosition()).get("null")) {
                pos = -1;
                selectData.clear();
                selectCurrent(-1);
                showDialog(event, LESS);
            } else {
                pos = getAdapterPosition();
                selectData.clear();
                addSelectData(getAdapterPosition());
                selectCurrent(getAdapterPosition());
                showDialog(event, LESS);
            }
            notifyDataSetChanged();
        }

        private void selectMore(MotionEvent event) {
            if ((boolean) data.get(getAdapterPosition()).get("null")) {
                pos = -1;
                selectData.clear();
                selectCurrent(-1);
                showDialog(event, LESS);
            } else {
                boolean isSelect = false;
                for (int i = 0; i < selectData.size(); i++) {
                    if (selectData.get(i) == getAdapterPosition()) {
                        isSelect = true;
                        break;
                    }
                }
                if (isSelect) {
                    Type type = (Type) data.get(getAdapterPosition()).get("type");
                    if (type == Type.COMPUTER || type == Type.RECYCLE) {
                        pos = getAdapterPosition();
                        selectCurrent(getAdapterPosition());
                        selectData.clear();
                        showDialog(event, LESS);
                    } else {
                        for (int i = 0; i < selectData.size(); i++) {
                            Type types = (Type) data.get(i).get("type");
                            if (types == Type.COMPUTER || types == Type.RECYCLE) {
                                data.get(i).put("isChecked", false);
                            }
                        }
                        showDialog(event, MORE);
                    }
                } else {
                    pos = getAdapterPosition();
                    selectData.clear();
                    addSelectData(getAdapterPosition());
                    selectCurrent(getAdapterPosition());
                    showDialog(event, LESS);
                }
            }
            notifyDataSetChanged();
        }

        private void showDialog(MotionEvent event, int num) {
            Type type = null;
            String path = null;
            switch (num) {
                case LESS:
                    type = (Type) data.get(getAdapterPosition()).get("type");
                    path = (String) data.get(getAdapterPosition()).get("path");
                    break;
                case MORE:
                    type = Type.MORE;
                    path = "";
                    for (int i = 0; i < selectData.size(); i++) {
                        path = path + Intent.EXTRA_DELETE_FILE_HEADER +
                                            (String) data.get(selectData.get(i)).get("path");
                    }
                    break;
            }
            MenuDialog dialog = new MenuDialog((MainActivity) mRecycleClick, type, path);
            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
            MenuDialog.setExistMenu(true);
        }

        private void selectCurrent(int current) {
            if (data != null && data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    if (current == i) {
                        data.get(i).put("isChecked", true);
                        continue;
                    }
                    data.get(i).put("isChecked", false);
                }
            }
        }
    }

    public void addSelectData(int pos){
        Type type = (Type)data.get(pos).get("type");
        if (type != Type.COMPUTER && type !=Type.RECYCLE) {
            selectData.add(pos);
        }
    }


    public static void openAppBroadcast(Context context) {
        Intent openAppIntent = new Intent();
        openAppIntent.setAction(Intent.ACTION_OPEN_APPLICATION);
        context.sendBroadcast(openAppIntent);
    }

    public List<Integer> getSelectData() {
        return selectData;
    }

    View.OnKeyListener keyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)
                        && v.hasFocus()) {
                HashMap current = data.get(pos);
                String path = (String)current.get("path");
                String newName = String.valueOf(((EditText)v).getText());
                File oldFile = new File(path);
                File newFile = new File(oldFile.getParent(), newName);
                oldFile.renameTo(newFile);
                current.put("name", newName);
                current.put("path", newFile.getAbsolutePath());
                data.set(pos,current);
                notifyDataSetChanged();
                HashMap mCurrent = ((MainActivity)mRecycleClick).mDatas.get(pos);
                mCurrent.put("name", newName);
                mCurrent.put("path", newFile.getAbsolutePath());
                ((MainActivity)mRecycleClick).mDatas.set(pos, mCurrent);
                v.setFocusable(false);
                v.clearFocus();
                isRename = false;
                MainActivity.mHandler.sendEmptyMessage(OtoConsts.SAVEDATA);
                return true;
            }
            return false;
        }
    };

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) { // TODO: will add code, next.
            } else {
                v.setFocusable(false);
                v.clearFocus();
                isRename = false;
            }
        }
    };
}
