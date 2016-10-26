package com.openthos.launcher.openthoslauncher.adapter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.EditText;
import android.content.Context;

import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.utils.DiskUtils;
import com.openthos.launcher.openthoslauncher.utils.FileUtils;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xu on 2016/8/8.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private List<HashMap<String, Object>> data;

    private RecycleCallBack mRecycleClick;

    public int pos = -1;
    private int mLastClickId = -1;
    private long mLastClickTime = 0;
    private boolean isClicked = false;
    public boolean isRename = false;

    public HomeAdapter(List<HashMap<String, Object>> data, RecycleCallBack click) {
        this.data = data;
        this.mRecycleClick = click;
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
        if ((int) data.get(position).get("icon") != -1) {
            holder.iv.setImageDrawable(holder.item.getResources().getDrawable(
                                                         (int) data.get(position).get("icon")));
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
                        listenProcess(v,event);
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
                            //MenuDialog dialog = new MenuDialog(itemView.getContext(),
                            MenuDialog dialog = MenuDialog.getInstance(itemView.getContext(),
                                                                       Type.blank, "/");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                        } else {
                            MenuDialog.setExistMenu(false);
                        }
                    }
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (isClicked != true && pos != -1) {
                            data.get(pos).put("isChecked", false);
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
            listenProcess(v, event);
            return true;
        }

        private void listenProcess(View v, MotionEvent event) {
            if (getAdapterPosition() != -1) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                    MenuDialog dialog = null;
                    if (Type.blank == (Type) data.get(getAdapterPosition()).get("type")) {
                        dialog = MenuDialog.getInstance(item.getContext(),
                                 (Type) data.get(getAdapterPosition()).get("type"),
                                 (String) data.get(getAdapterPosition()).get("path"));
                    } else {
                        dialog = new MenuDialog(item.getContext(),
                                 (Type) data.get(getAdapterPosition()).get("type"),
                                 (String) data.get(getAdapterPosition()).get("path"));
                    }
                    dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                    MenuDialog.setExistMenu(true);
                    }
                    if (!(Boolean) data.get(getAdapterPosition()).get("null")) {
                        isClicked = true;
                        if (event.getButtonState() != MotionEvent.BUTTON_SECONDARY
                                                   && Math.abs(System.currentTimeMillis()
                                                   - mLastClickTime) < OtoConsts.DOUBLE_CLICK_TIME
                                                   && mLastClickId == getAdapterPosition()) {
                            switch ((Type) data.get(getAdapterPosition()).get("type")) {
                                case computer:
                                case recycle:
                                case directory:
                                    PackageManager packageManager = item.getContext()
                                                                        .getPackageManager();
                                    try {
                                        Intent intent = packageManager.getLaunchIntentForPackage(
                                                                 OtoConsts.OTO_FILEMANAGER_PACKAGE);
                                        item.getContext().startActivity(intent);
                                    } catch (NullPointerException e) {
                                        Intent intent = packageManager.getLaunchIntentForPackage(
                                                                     OtoConsts.FILEMANAGER_PACKAGE);
                                        item.getContext().startActivity(intent);
                                    }
                                    break;
                                case file:
                                    String filePath = (String)
                                                         data.get(getAdapterPosition()).get("path");
                                    Intent openFile = new Intent();
                                    openFile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    openFile.setAction(Intent.ACTION_VIEW);
                                    String fileType = FileUtils.getMIMEType(new File(filePath));
                                    openFile.setDataAndType(
                                                        Uri.fromFile(new File(filePath)), fileType);
                                    item.getContext().startActivity(openFile);
                                    break;
                            }
                            openAppBroadcast(item.getContext());
                        } else {
                            if (null != mClick) {
                                mClick.itemOnClick(getAdapterPosition(), v);
                            }
                            if (!(Boolean) data.get(getAdapterPosition()).get("isChecked")) {
                                if (pos != -1 && pos != getAdapterPosition()
                                    && (Boolean) data.get(pos).get("isChecked")) {
                                    data.get(pos).put("isChecked", false);
                                }
                                data.get(getAdapterPosition()).put("isChecked", true);
                                if (pos != getAdapterPosition()) {
                                    pos = getAdapterPosition();
                                } else {
                                    pos = -1;
                                }
                                notifyDataSetChanged();
                            }
                            mLastClickTime = System.currentTimeMillis();
                            mLastClickId = pos;
                        }
                    }
                } else if ((Boolean) data.get(getAdapterPosition()).get("null")
                                     && pos != -1 && pos != getAdapterPosition()
                                     && (Boolean) data.get(pos).get("isChecked")) {
                    data.get(pos).put("isChecked", false);
                    pos = -1;
                    notifyDataSetChanged();
                }
            }
        }
    }

    public static void openAppBroadcast(Context context) {
        Intent openAppIntent = new Intent();
        openAppIntent.setAction(Intent.ACTION_OPEN_APPLICATION);
        context.sendBroadcast(openAppIntent);
    }

    View.OnKeyListener keyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
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
