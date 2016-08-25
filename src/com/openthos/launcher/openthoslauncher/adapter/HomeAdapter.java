package com.openthos.launcher.openthoslauncher.adapter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
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

import com.android.launcher3.R;
import com.openthos.launcher.openthoslauncher.activity.MainActivity;
import com.openthos.launcher.openthoslauncher.utils.OtoConsts;
import com.openthos.launcher.openthoslauncher.entity.Type;
import com.openthos.launcher.openthoslauncher.view.MenuDialog;

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
    private boolean isExistMene = false;
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
            tv.setOnKeyListener(keyListener);
            tv.setOnFocusChangeListener(focusChangeListener);
            checkBox = (CheckBox) view.findViewById(R.id.check);
            nullnull = (CheckBox) view.findViewById(R.id.nullnull);
            item.setOnTouchListener(this);
            this.mClick = click;
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                        if (isExistMene == false) {
                            MenuDialog dialog = new MenuDialog(itemView.getContext(),
                                                               Type.blank, "/");
                            dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                        } else {
                            isExistMene = false;
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
            if (event.getButtonState() == MotionEvent.BUTTON_SECONDARY) {
                MenuDialog dialog = new MenuDialog(item.getContext(),
                                              (Type) data.get(getAdapterPosition()).get("type"),
                                              (String)data.get(getAdapterPosition()).get("path"));
                dialog.showDialog((int) event.getRawX(), (int) event.getRawY());
                isExistMene = true;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (getAdapterPosition() != -1){
                if ((Boolean) data.get(getAdapterPosition()).get("null") != true) {
                    isClicked = true;
                    if ((Math.abs(System.currentTimeMillis() - mLastClickTime)
                            < OtoConsts.DOUBLE_CLICK_TIME) && (mLastClickId == getAdapterPosition())
                        && (event.getButtonState() != MotionEvent.BUTTON_SECONDARY)) {
                       PackageManager packageManager = item.getContext().getPackageManager();
                       Intent intent = packageManager.getLaunchIntentForPackage(
                                                                    OtoConsts.FILEMANAGER_PACKAGE);
                       item.getContext().startActivity(intent);
                    } else {
                        if (null != mClick) {
                            mClick.itemOnClick(getAdapterPosition(), v);
                        }
                        if ((Boolean) data.get(getAdapterPosition()).get("isChecked") == false) {
                            if (pos != -1 && pos != getAdapterPosition()
                                && (Boolean) data.get(pos).get("isChecked") == true) {
                                data.get(pos).put("isChecked", false);
                            }
                            data.get(getAdapterPosition()).put("isChecked",true);
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
                }}
            } else {
                if (pos != -1 && pos != getAdapterPosition()
                                && (Boolean) data.get(pos).get("isChecked") == true) {
                    data.get(pos).put("isChecked", false);
                }
                pos = -1;
                notifyDataSetChanged();
            }
            return false;
        }
    }

    View.OnKeyListener keyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                HashMap current = data.get(pos);
                current.put("name",((EditText)v).getText());
                data.set(pos,current);
                v.setFocusable(false);
                v.clearFocus();
                notifyDataSetChanged();
                HashMap mCurrent = ((MainActivity)mRecycleClick).mDatas.get(pos);
                mCurrent.put("name",((EditText)v).getText());
                ((MainActivity)mRecycleClick).mDatas.set(pos,mCurrent);
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
